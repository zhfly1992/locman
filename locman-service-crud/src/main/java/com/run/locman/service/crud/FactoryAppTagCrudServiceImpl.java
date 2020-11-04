/*
 * File name: FactoryAppTagCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guolei 2017年10月20日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DeviceInfoCudRepository;
import com.run.locman.api.crud.repository.FactoryAppTagCrudRepository;
import com.run.locman.api.crud.service.DeviceReportedCrudService;
import com.run.locman.api.crud.service.FactoryAppTagCrudService;
import com.run.locman.api.dto.AppTagDto;
import com.run.locman.api.entity.Device;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.util.InfoPushUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilityDeviceContants;

/**
 *
 * @Description: 厂家与Apptag关系cud实现类
 * @author: guolei
 * @version: 1.0, 2017年9月14日
 */
@Transactional(rollbackFor = Exception.class)
public class FactoryAppTagCrudServiceImpl implements FactoryAppTagCrudService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FactoryAppTagCrudRepository	factoryAppTagCrudRepository;

	@Autowired
	private MongoTemplate				mongoTemplate;
	@Autowired
	private DeviceInfoCudRepository		deviceInfoCudRepository;
	@Autowired
	private FacilityDeviceQueryService	facilityDeviceQueryService;
	@Autowired
	private DeviceReportedCrudService	deviceReportedCrudService;
	@Autowired
	private DeviceQueryService			deviceQueryService;
	@Value("${api.host}")
	private String						ip;



	/**
	 * @see com.run.locman.api.crud.service.FactoryAppTagCrudService#addDeviceState(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<String> addDeviceState(JSONObject deviceStateJson, String gatewayId, String deviceId) {
		logger.info(String.format("[addDeviceState()---添加设备上报状态数据,参数:%s, %s ,%s]", deviceStateJson,gatewayId,deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.info("[addDeviceState()->fail: 添加设备上报状态数据失败，设备deviceId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("添加设备上报状态数据失败，设备deviceId不能为空!");
			}
			if (null == deviceStateJson || deviceStateJson.isEmpty()) {
				logger.info("[addDeviceState()->fail: 添加设备上报状态数据失败，设备上报数据为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("添加设备上报状态数据失败，设备上报数据为空!");
			}
			if (!StringUtils.isBlank(gatewayId)) {
				deviceStateJson.put("deviceId", deviceId);
			}
			// 保存设备历史状态
			logger.info(String.format("[addDeviceState()->进入方法,参数:%s]", deviceStateJson));
			// 处理数据修改为String类型
			deviceStateJson.put("timestamp", deviceStateJson.getString("timestamp"));
			mongoTemplate.insert(deviceStateJson, "deviceHistoryState");
			String lastReportTime = UtilTool
					.timeStampToDate(Long.parseLong(deviceStateJson.get("timestamp").toString()));
			// 获取设备硬件编码，新iot设备上报数据无硬件编码

			JSONObject reportedJson = UtilTool.getReported(deviceStateJson);
			// String hardwareid = reportedJson.getString("hardwareid");//
			// 旧iot设备有硬件编码
			String deviceBv = reportedJson.getString("bv");
			String deviceSig = reportedJson.getString("sig");
			String deviceRsrp = reportedJson.getString("rsrp");
			String deviceSinr = reportedJson.getString("sinr");
			String deviceLs = reportedJson.getString("ls");
			String deviceFv = reportedJson.getString("fv");
			
			String xavtv = reportedJson.getString("xavtv");
			String yavtv = reportedJson.getString("yavtv");
			String zavtv = reportedJson.getString("zavtv");
			

			// 保存实时状态数据 先查询数据，没有则新增，有则修改
			DBObject dbCondition = new BasicDBObject();
			dbCondition.put(FacilityDeviceContants.DEVICE_ID, deviceId);
			Query query1 = new BasicQuery(dbCondition);
			Map<String, Object> check = mongoTemplate.findOne(query1, Map.class, "deviceState");

			// 保存设备上报的电池电压、信号强度、信号接收功率等属性到中间表
			saveAttribute(deviceId, deviceBv, deviceSig, deviceRsrp, deviceSinr, deviceLs, deviceFv, xavtv, yavtv, zavtv);

			// Msql数据库更新设备上报状态
			int res = deviceInfoCudRepository.updateDeviceLastReportTime(deviceId, lastReportTime);
			if (res > 0) {
				logger.info("[addDeviceState()->info: 设备上报时间保存mysql成功!]");
			} else {
				logger.error("[addDeviceState()->fail: 设备上报时间保存mysql失败!]");
			}

			if (null == check || check.size() == 0) {
				mongoTemplate.insert(deviceStateJson, "deviceState");
			} else {

				// 修改设备上报状态数据
				Criteria criteria = Criteria.where(FacilityDeviceContants.DEVICE_ID).is(deviceId);
				Query query = new Query(criteria);

				mongoTemplate.remove(query, "deviceState");
				mongoTemplate.insert(deviceStateJson, "deviceState");

			}
			//infoPush(deviceId, deviceStateJson);
			logger.info("[addDeviceState(JSONObject deviceStateJson)---添加设备上报状态数据成功!]");
			return RpcResponseBuilder.buildSuccessRpcResp("添加设备上报状态数据成功!", null);
		} catch (Exception e) {
			logger.error("addDeviceState()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * 
	 * @param zavtv 
	 * @param yavtv 
	 * @param xavtv 
	 * @Description:
	 * @param
	 * @return
	 */

	private void saveAttribute(String deviceId, String deviceBv, String deviceSig, String deviceRsrp, String deviceSinr,
			String deviceLs, String deviceFv, String xavtv, String yavtv, String zavtv) {
		if (!StringUtils.isBlank(deviceBv) || !StringUtils.isBlank(deviceSig) || !StringUtils.isBlank(deviceRsrp)
				|| !StringUtils.isBlank(deviceSinr) || !StringUtils.isBlank(deviceLs) || !StringUtils.isBlank(deviceFv)) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("deviceId", deviceId);
			map.put("device_bv", deviceBv);
			map.put("device_sig", deviceSig);
			map.put("device_rsrp", deviceRsrp);
			map.put("device_sinr", deviceSinr);
			map.put("device_ls", deviceLs);
			map.put("device_fv", deviceFv);


			if (!StringUtils.isBlank(xavtv) && !StringUtils.isBlank(yavtv) && !StringUtils.isBlank(zavtv)
					&& UtilTool.isNumeric(xavtv) && UtilTool.isNumeric(yavtv) && UtilTool.isNumeric(zavtv) ) {
				int sumXYZ = Integer.parseInt(xavtv) + Integer.parseInt(yavtv) + Integer.parseInt(zavtv);
				map.put("sumXYZ", sumXYZ);
			}
			
			RpcResponse<Integer> update = deviceReportedCrudService.updateDeviceRealReporte(map);
			if (null == update) {
				logger.error("[updateDevice_RealReporte()->fail: 设备上报属性保存mysql失败!]");
				// 修改失败，则没有这条数据，则执行保存
			} else if (null != update && update.isSuccess() && update.getSuccessValue() == 0) {
				
				deviceReportedCrudService.saveDeviceRealReporte(map);
			}
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.FactoryAppTagCrudService#addDeviceLineState(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<String> addDeviceLineState(JSONObject deviceStateJson) {
		logger.info("[addDeviceLineState(JSONObject deviceStateJson)---添加设备在线离线状态数据!]");
		try {
			logger.info(String.format("[addDeviceLineState()->进入方法,参数:%s]", deviceStateJson));
			// 保存设备在线离线状态到mysql
			if (!StringUtils.isBlank(deviceStateJson.getString(CommonConstants.THING_STATUS))) {
				int res = deviceInfoCudRepository.updateDeviceOnLineState(
						deviceStateJson.getString(FacilityDeviceContants.DEVICE_ID),
						deviceStateJson.getString("thingStatus"));
				if (res > 0) {
					logger.info("[addDeviceLineState()->info:保存设备在线离线状态到mysql成功]");
				} else {
					logger.error("[addDeviceLineState()->fail:保存设备在线离线状态到mysql失败]");
				}
			}

			// 先查询数据，没有则新增，有则修改
			DBObject dbCondition = new BasicDBObject();
			dbCondition.put(FacilityDeviceContants.DEVICE_ID,
					deviceStateJson.getString(FacilityDeviceContants.DEVICE_ID));
			Query query1 = new BasicQuery(dbCondition);
			Map<String, Object> check = mongoTemplate.findOne(query1, Map.class, "deviceLineState");
			if (null == check || check.size() == 0) {
				// 首次上线时间
				deviceStateJson.put("firstOnlineTime", deviceStateJson.getString("updateTime"));
				mongoTemplate.insert(deviceStateJson, "deviceLineState");
			} else {
				// 修改设备在线离线状态信息
				Criteria criteria = Criteria.where(FacilityDeviceContants.DEVICE_ID)
						.is(deviceStateJson.getString(FacilityDeviceContants.DEVICE_ID));
				Query query = new Query(criteria);
				Update update = new Update();
				update.set("updateTime", deviceStateJson.getString("updateTime"));
				update.set("thingStatus", deviceStateJson.getString("thingStatus"));
				WriteResult updateMulti = mongoTemplate.updateMulti(query, update, "deviceLineState");
				if (updateMulti.getN() > 0) {
					logger.info("[addDeviceLineState()->success: 设备在线离线状态更新成功!]");
					return RpcResponseBuilder.buildSuccessRpcResp("设备在线离线状态更新成功!", null);
				}
			}
			logger.info("[addDeviceLineState(JSONObject deviceStateJson)---添加设备在线离线状态数据成功!]");
			return RpcResponseBuilder.buildSuccessRpcResp("设备在线离线状态添加成功!", null);
		} catch (Exception e) {
			logger.error("addDeviceLineState()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@Override
	public RpcResponse<String> addFactoryRsAppTag(String factoryId, List<AppTagDto> appTagDtoList) {
		try {
			if (StringUtils.isBlank(factoryId)) {
				logger.error("[addFactoryRsAppTag()->invalid: 厂家id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("厂家id不能为空");
			}
			if (null == appTagDtoList || appTagDtoList.isEmpty()) {
				logger.error("[addFactoryRsAppTag()->invalid: 厂家appTag不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("厂家appTag不能为空");
			}
			logger.info(
					String.format("[addFactoryRsAppTag()->进入方法,参数factoryId:%s,appTag:%s]", factoryId, appTagDtoList));
			Map<String, Object> addParams = Maps.newHashMap();
			addParams.put("factoryId", factoryId);
			addParams.put("appTagDtoList", appTagDtoList);
			int num = factoryAppTagCrudRepository.addFactoryRsAppTag(addParams);
			if (num == appTagDtoList.size()) {
				return RpcResponseBuilder.buildSuccessRpcResp("appTag保存成功", null);
			} else {
				logger.error("[addFactoryRsAppTag()->fail: appTag保存时发生异常:" + addParams + "]");
				return RpcResponseBuilder.buildErrorRpcResp("appTag保存时发生异常");
			}

		} catch (Exception e) {
			logger.error("addFactoryRsAppTag()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:将设备上报信息推送给其他平台
	 * @param Stirng
	 *            deviceId
	 * @param JSONObject
	 *            deviceStateJson
	 * @return
	 */
	private void infoPush(String deviceId, JSONObject deviceStateJson) {
		try {
			logger.info(String.format("[infoPush->设备上报数据推送，deviceId-%s]",deviceId));
			// 查询该设备绑定的设施id
			RpcResponse<String> queryDeviceBindingState = facilityDeviceQueryService.queryDeviceBindingState(deviceId);
			String facilityId = "";
			if (queryDeviceBindingState.isSuccess()) {
				facilityId = queryDeviceBindingState.getSuccessValue();
			}
			// 获取设备信息
			RpcResponse<Device> queryDeviceInfoById = deviceQueryService.queryDeviceInfoById(deviceId);
			if (!queryDeviceInfoById.isSuccess() || queryDeviceInfoById.getSuccessValue() == null) {
				logger.info("[infoPush->无该设备信息，不进行推送]");
				return;
			}

			Device device = queryDeviceInfoById.getSuccessValue();
			String accessSecret = device.getAccessSecret();
			if (accessSecret == null) {
				// 组装推送的数据
				logger.info("[infoPush->该设备无accessSecret，不进行推送]");
				return;
			}
			JSONObject pushInfo = new JSONObject();
			pushInfo.put("deviceId", deviceId);
			pushInfo.put("facilityId", facilityId);
			pushInfo.put("iotPush", deviceStateJson);
			String receiveUrl = InfoPushUtil.WhetherPush(accessSecret, ip);
			if (receiveUrl == null) {
				logger.info("[infoPush--->推送地址为空，不进行推送]");
				return;
			}
			String pushRes = InfoPushUtil.InfoPush(receiveUrl, pushInfo, InfoPushUtil.DEVICE);
			logger.info(String.format("[infoPush->%s]", pushRes));
			return;
		} catch (Exception e) {
			logger.error("[infoPush->Exception]", e);
			return;
		}
	}
}
