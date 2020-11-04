package com.run.locman.query.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.FaultOrderProcessCudService;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.Pages;
import com.run.locman.api.query.repository.DeviceQueryRepository;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.api.query.service.TimeoutReportConfigQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.MongoPageUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.FacilityDeviceContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderProcessContants;
import com.run.locman.constants.PublicConstants;

/**
 * 
 * @Description: 设备信息查询实现类
 * @author: 田明
 * @version: 1.0, 2018年03月05日
 */
@Service
public class DeviceQueryServiceImpl implements DeviceQueryService {

	/**
	 * 
	 */
	private static final String CODE_H = "-";

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceQueryRepository			deviceQueryRepository;

	@Autowired
	private MongoTemplate					mongoTemplate;

	@SuppressWarnings("unused")
	@Autowired
	private FaultOrderProcessCudService		faultOrderProcessCudService;

	@Autowired
	private OrderProcessQueryService		orderProcessQueryService;

	@SuppressWarnings("unused")
	@Autowired
	private TimeoutReportConfigQueryService	timeoutReportConfigQueryService;

	@Value("${api.host}")
	private String							ip;



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map<String, Object>>> queryDeviceInfoForCondition(int pageNum, int pageSize,
			String accessSecret, String deviceTypeId, String bingStatus, String deviceId, String facilityId,
			String factoryId, String whole, String startTime, String endTime, String onLineState) {
		logger.info(
				String.format("[queryDeviceInfoForCondition()方法执行开始...,参数：【%s】【%s】【%s】【%s】【%s】【%s】【%s】【%s】【%s】【%s】]",
						pageNum, pageSize, accessSecret, deviceTypeId, bingStatus, deviceId, facilityId, startTime,
						endTime, onLineState));
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("接入方秘钥不能为空!");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空");
		}
		try {
			int pageNumMySql = 0;
			if (pageNum > 0) {
				pageNumMySql = (pageNum - 1) * pageSize;
			}
			HashMap<String, Object> queryInfo = Maps.newHashMap();
			queryInfo.put("NumberPage", pageNumMySql);
			queryInfo.put("SizePage", pageSize);
			queryInfo.put(OrderProcessContants.USC_ACCESS_SECRET, accessSecret);
			queryInfo.put(FacilityDeviceContants.DEVICE_TYPE_ID, deviceTypeId);
			queryInfo.put(FacilityDeviceContants.BINGSTATUS, bingStatus);
			queryInfo.put(FacilityDeviceContants.DEVICE_ID, deviceId);
			queryInfo.put(FacilityDeviceContants.FACILITY_ID, facilityId);
			queryInfo.put(FacilityDeviceContants.FACTORY_ID, factoryId);
			queryInfo.put(FacilityDeviceContants.WHOLE, whole);
			queryInfo.put(FacilityDeviceContants.START_TIME, startTime);
			queryInfo.put(FacilityDeviceContants.END_TIME, endTime);
			queryInfo.put(FacilityDeviceContants.ON_LINE_STATE, onLineState);
			// 组合条件查询设备信息
			List<Map<String, Object>> deviceList = deviceQueryRepository.queryDeviceInfoForCondition(queryInfo);
			
			if (!deviceList.isEmpty()) {
				// 获取所有设备id
				List<String> deviceIdList = new ArrayList<>();
				for (Map<String, Object> map : deviceList) {
					deviceIdList.add(String.valueOf(map.get(FacilityDeviceContants.BIND_DEVICES_ID)));
				}
				
				// 查询设备首次上线时间
				logger.info("[queryDeviceInfoForCondition()->开始查询设备首次上线时间和状态]");
				//List<Map> statusAndFirstOnLineTime = mongoTemplate.find(query, Map.class, "deviceLineState");
				for (Map<String, Object> deviceInfo : deviceList) {
					
					deviceInfo.put("firstOnlineTime", "");
				}
			}
			
			/*if (!deviceList.isEmpty()) {
				// 获取所有设备id
				List<String> deviceIdList = new ArrayList<>();
				for (Map<String, Object> map : deviceList) {
					deviceIdList.add(String.valueOf(map.get(FacilityDeviceContants.BIND_DEVICES_ID)));
				}
				DBObject dbConditon = new BasicDBObject();
				dbConditon.put("deviceId", new BasicDBObject("$in", deviceIdList));
				Query query = new BasicQuery(dbConditon);

				// 查询设备首次上线时间
				logger.info("[queryDeviceInfoForCondition()->开始查询设备首次上线时间和状态]");
				List<Map> statusAndFirstOnLineTime = mongoTemplate.find(query, Map.class, "deviceLineState");
				for (Map<String, Object> deviceInfo : deviceList) {
					String id = String.valueOf(deviceInfo.get(FacilityDeviceContants.BIND_DEVICES_ID));
					String deviceFirstOnLineTime = "";
					for (Map mongoRes : statusAndFirstOnLineTime) {
						if (mongoRes.get("deviceId").equals(id)) {
							if (mongoRes.containsKey("firstOnlineTime")) {
								deviceFirstOnLineTime = String
										.valueOf(DateUtils.stampToDate(mongoRes.get("firstOnlineTime") + "000"));
							}
							break;
						}
					}
					deviceInfo.put("firstOnlineTime", deviceFirstOnLineTime);
				}
			}*/

			// 获取设备是否是维护状态

			logger.info(LogMessageContants.QUERY_SUCCESS);
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, deviceList);
		} catch (Exception e) {
			logger.error("queryDeviceInfoForCondition()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Map<String, Object>> queryDeviceBindingState(String deviceId) {
		logger.info(String.format("[queryDeviceBindingState()方法执行开始...,参数：【%s】]", deviceId));
		if (StringUtils.isBlank(deviceId)) {
			logger.error("设备id不能为空!");
			return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空");
		}
		try {
			Map<String, Object> deviceMap = deviceQueryRepository.queryDeviceBindingState(deviceId);
			DBObject dbCondition = new BasicDBObject();
			dbCondition.put(FacilityDeviceContants.DEVICE_ID, deviceId);
			Query query = new BasicQuery(dbCondition);
			// 查询设备最新上报时间
			Map<String, Object> newReportTimeMap = mongoTemplate.findOne(query, Map.class, "deviceState");
			// 判断查询出来是否存在设备的最新上报时间
			String newReportTime = "";
			if (null != newReportTimeMap && !newReportTimeMap.isEmpty()) {
				String time = newReportTimeMap.get("timestamp") + "";
				if (!StringUtils.isBlank(time)) {
					newReportTime = UtilTool.timeStampToDate(Long.parseLong(time));
				}
			}
			// String newReportTime = newReportTimeMap == null ? ""
			// :
			// DateUtils.stampToDate(String.valueOf(newReportTimeMap.get("timestamp")
			// + "000"));
			deviceMap.put("lastReportTime", newReportTime);
			// 查询设备状态
			Map<String, Object> deviceStatusMap = mongoTemplate.findOne(query, Map.class, "deviceLineState");
			String deviceStatus = deviceStatusMap == null ? "" : String.valueOf(deviceStatusMap.get("thingStatus"));
			deviceMap.put("deviceOnlineStatus", deviceStatus);
			
			//首次上线时间
			String deviceFirstOnLineTime="";
			Map<String, Object> statusAndFirstOnLineTime = mongoTemplate.findOne(query, Map.class, "deviceLineState");
			if (null != statusAndFirstOnLineTime && !statusAndFirstOnLineTime.isEmpty()) {
				String time1=statusAndFirstOnLineTime.get("firstOnlineTime")+"";
				if(!StringUtils.isBlank(time1)) {
					deviceFirstOnLineTime=UtilTool.timeStampToDate(Long.parseLong(time1));
				}
			}
			deviceMap.put("firstOnLineTime", deviceFirstOnLineTime);
			
			logger.info(String.format("[queryDeviceBindingState()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("设备状态信息查询成功！", deviceMap);
		} catch (Exception e) {
			logger.error("queryDeviceBindingState()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map<String, Object>>> queryBatchDeviceInfoForDeviceIds(List<String> list) {
		logger.info(String.format("[queryBatchDeviceInfoForDeviceIds()方法执行开始...,参数：【%s】]", list));
		try {
			if (list.isEmpty()) {
				return RpcResponseBuilder.buildErrorRpcResp("设备编号数组不能为空!");
			}
			// 根据设备编号数组查询批量设备信息
			List<Map<String, Object>> batchDeviceList = deviceQueryRepository.queryBatchDeviceInfoForDeviceIds(list);
			
			DBObject dbConditon = new BasicDBObject();
			dbConditon.put("deviceId", new BasicDBObject("$in", list));
			Query query = new BasicQuery(dbConditon);

			// 查询设备首次上线时间
			logger.info("[queryDeviceInfoForCondition()->开始查询设备首次上线时间和状态]");
			List<Map> statusAndFirstOnLineTime = mongoTemplate.find(query, Map.class, "deviceLineState");
			for (Map<String, Object> deviceInfo : batchDeviceList) {
				String id = String.valueOf(deviceInfo.get(FacilityDeviceContants.BIND_DEVICES_ID));
				String deviceFirstOnLineTime = "";
				for (Map mongoRes : statusAndFirstOnLineTime) {
					if (mongoRes.get("deviceId").equals(id)) {
						if (mongoRes.containsKey("firstOnlineTime")) {
							deviceFirstOnLineTime = String
									.valueOf(DateUtils.stampToDate(mongoRes.get("firstOnlineTime") + "000"));
						}
						break;
					}
				}
				deviceInfo.put("firstOnlineTime", deviceFirstOnLineTime);
			}
			
			logger.info(String.format("[queryBatchDeviceInfoForDeviceIds()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询设施下绑定设备信息成功", batchDeviceList);
		} catch (Exception e) {
			logger.error("queryBatchDeviceInfoForDeviceIds()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<JSONObject> queryDeviceLastState(String deviceId) {
		logger.info(String.format("[queryDeviceLastState()方法执行开始...,参数：【%s】]", deviceId));
		if (StringUtils.isBlank(deviceId)) {
			logger.error("设备id不能为空!");
			return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空");
		}
		try {
			JSONObject deviceStateInfo = null;
			// 如果网关id为NULL值，则执行原版IOT查询，否则执行新版IOT查询
			// if (StringUtils.isBlank(gatewayId)) {
			// 构建mongo查询
			DBObject dbCondition = new BasicDBObject();
			dbCondition.put(FacilityDeviceContants.DEVICE_ID, deviceId);
			Query query = new BasicQuery(dbCondition);
			// 查询设备deviceState状态信息
			deviceStateInfo = mongoTemplate.findOne(query, JSONObject.class, "deviceState");
			// } else {
			// // 构建mongo查询
			// DBObject dbCondition = new BasicDBObject();
			// dbCondition.put("things.subThingId", deviceId);
			// dbCondition.put("things.gatewayId", gatewayId);
			// Query query = new BasicQuery(dbCondition);
			// // 查询设备deviceState状态信息
			// deviceStateInfo = mongoTemplate.findOne(query, JSONObject.class,
			// "deviceState");
			// }

			if (deviceStateInfo == null) {
				logger.error("{queryDeviceLastState()}实时状态查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("实时状态查询失败");
			} else {
				logger.info("{queryDeviceLastState()}实时状态查询成功");
				// 判断查询出来是否存在上报时间
				String lastReportTime = "";
				String timestamp = deviceStateInfo.getString("timestamp");
				if (!StringUtils.isBlank(timestamp)) {
					lastReportTime = UtilTool.timeStampToDate(Long.parseLong(timestamp));
				}
				deviceStateInfo.put("timestamp", lastReportTime);
				logger.info(String.format("[queryDeviceLastState()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("实时状态查询成功", deviceStateInfo);
			}
		} catch (Exception e) {
			logger.error("queryDeviceLastState()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#queryDeviceByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<Device> queryDeviceByDeviceId(String deviceId) {
		logger.info(String.format("[queryDeviceByDeviceId()方法执行开始...,参数：【%s】]", deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("设备id不能为空!");
				return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空");
			}
			Device device = deviceQueryRepository.queryDeviceByDeviceId(deviceId);

			logger.info("{queryDeviceByDeviceId()--success:查询成功");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", device);

		} catch (Exception e) {
			logger.error("queryDeviceByDeviceId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<JSONObject>> queryDevicesLastState(List<String> deviceIds) {
		logger.info(String.format("[queryDevicesLastState()方法执行开始...,参数：【%s】]", deviceIds));
		if (deviceIds == null || deviceIds.isEmpty()) {
			logger.error("设备id不能为空!");
			return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空");
		}
		try {
			// 构建mongo查询
			Query queryDevice = new Query();
			queryDevice.addCriteria(Criteria.where(FacilityDeviceContants.DEVICE_ID).in(deviceIds));
			// 查询设备deviceState状态信息
			List<JSONObject> deviceStateInfo = mongoTemplate.find(queryDevice, JSONObject.class, "deviceState");
			if (deviceStateInfo == null) {
				logger.error("{queryDevicesLastState()}实时状态查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("实时状态查询失败");
			} else {
				logger.info("{queryDevicesLastState()}实时状态查询成功");
				// 判断查询出来是否存在上报时间
				return RpcResponseBuilder.buildSuccessRpcResp("实时状态查询成功", deviceStateInfo);
			}
		} catch (Exception e) {
			logger.error("queryDevicesLastState()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<Integer> getDeviceListNum(String accessSecret, String deviceTypeId, String bingStatus,
			String deviceId, String factoryId, String whole, String startTime, String endTime, String onLineState) {
		logger.info(String.format("[getDeviceListNum()方法执行开始...,参数：【%s】【%s】【%s】【%s】【%s】【%s】【%s】]", accessSecret,
				deviceTypeId, bingStatus, deviceId, startTime, endTime, onLineState));
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("接入方秘钥不能为空!");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空");
		}
		try {
			HashMap<String, Object> queryInfo = Maps.newHashMap();
			queryInfo.put(OrderProcessContants.USC_ACCESS_SECRET, accessSecret);
			queryInfo.put(FacilityDeviceContants.DEVICE_TYPE_ID, deviceTypeId);
			queryInfo.put(FacilityDeviceContants.BINGSTATUS, bingStatus);
			queryInfo.put(FacilityDeviceContants.DEVICE_ID, deviceId);
			queryInfo.put(FacilityDeviceContants.FACTORY_ID, factoryId);
			queryInfo.put(FacilityDeviceContants.WHOLE, whole);
			queryInfo.put(FacilityDeviceContants.START_TIME, startTime);
			queryInfo.put(FacilityDeviceContants.END_TIME, endTime);
			queryInfo.put(FacilityDeviceContants.ON_LINE_STATE, onLineState);

			int total = deviceQueryRepository.getDeviceListNum(queryInfo);

			logger.info("[getDeviceListNum()->success]");
			return RpcResponseBuilder.buildSuccessRpcResp("设备数量查询成功", total);

		} catch (Exception e) {
			logger.error("queryDevicesLastState()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}


	/**
	 * 查询各个接入方下，超时故障工单流程配置的人员id和组织id
	 */

	public JSONObject queryPersonIdAndOrganizeIdByAc() {
		logger.info(String.format("[queryPersonIdAndOrganizeIdByAc()方法执行开始...]"));
		// 查询所有接入方密钥
		List<String> allAccessSecret = deviceQueryRepository.getAllAccessSecret();
		JSONObject result = new JSONObject();
		if (allAccessSecret.isEmpty()) {
			logger.error("没有查询到任何接入方密钥");
		}

		for (String accessSecret : allAccessSecret) {
			// 查询各个接入方，超时故障工单流程配置的人员id和组织id
			RpcResponse<List<Map<String, Object>>> personInfo = orderProcessQueryService
					.findPersonByAccessSecret(accessSecret, "超时故障工单流程");
			List<Map<String, Object>> successValue = personInfo.getSuccessValue();
			if (!personInfo.isSuccess()) {
				logger.error("密钥为" + accessSecret + "的接入方查询超时故障工单流程失败");
				continue;
			}
			List<String> resultpersonId = Lists.newArrayList();

			if (successValue != null && !successValue.isEmpty()) {

				querySuccess(result, accessSecret, successValue, resultpersonId);
			} else {
				logger.debug("密钥为" + accessSecret + "的接入方没有查询到超时故障工单流程");
			}
		}
		logger.info(String.format("[queryPersonIdAndOrganizeIdByAc()方法执行结束!]"));
		return result;
	}



	/**
	 * @Description:
	 * @param result
	 * @param accessSecret
	 * @param successValue
	 * @param resultpersonId
	 */

	private void querySuccess(JSONObject result, String accessSecret, List<Map<String, Object>> successValue,
			List<String> resultpersonId) {
		for (Map<String, Object> person : successValue) {

			if (person.containsValue("发起人")) {
				// 人员id(多个)
				String personId = person.get("personId").toString();
				// 组织id(一级组织的id)
				String organizeId = person.get("organizeId").toString();
				// String[] personIdArray = string.split(",");
				List<String> personIdList = Lists.newArrayList(personId);
				JSONObject json = new JSONObject();
				json.put(InterGatewayConstants.USC_IDS, personIdList);
				// 查询人员对应的厂家
				String factoryIdsStr = InterGatewayUtil.getHttpValueByPost(InterGatewayConstants.U_FACTORY_IDS, json,
						ip, "");
				if (StringUtils.isBlank(factoryIdsStr)) {
					logger.error("interGateway查询厂家id返回值为null" + personIdList);
					continue;
				}
				// key:value 人员id:厂家id
				Map<String, Object> personAndFactoryIds = JSONObject.parseObject(factoryIdsStr);
				Object factoryIdObj = personAndFactoryIds.get(personId);
				String factoryId = "" + factoryIdObj;
				if (factoryIdObj == null || StringUtils.isBlank(factoryId)) {
					continue;
				}
				Map<String, Object> map = Maps.newHashMap();
				resultpersonId.add(personId);
				result.put(accessSecret, resultpersonId);
				map.put(personId, factoryId);
				map.put(factoryId, organizeId);
				result.put(personId, map);
			}
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#getDeviceGatewayId(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, String>> getDeviceInfoForRemoteControl(String deviceId) {
		logger.info(String.format("[getDeviceInfoForRemoteControl()->进入方法，参数 deviceId:%s]", deviceId));
		if (StringUtils.isBlank(deviceId)) {
			logger.error("[getDeviceInfoForRemoteControl()->设备id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("查询失败，设备id不能为空");
		}
		try {
			Map<String, String> res = deviceQueryRepository.getDeviceInfoForControl(deviceId);
			if (res == null) {
				logger.error("[getDeviceInfoForRemoteControl()->fail:无该设备信息,确认设备id正确]");
				return RpcResponseBuilder.buildErrorRpcResp("没有设备信息，请确认设备id正确");
			}
			logger.info(String.format("[getDeviceInfoForRemoteControl()->success:%s]", res));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);

		} catch (Exception e) {
			logger.error("[getDeviceInfoForRemoteControl()->exception]", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#getHistorySateByPage(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getHistorySateByPage(String deviceId, String pageNum,
			String pageSize, String startTime, String endTime) {
		logger.info(String.format(
				"[getHistorySateByPage()->进入方法，参数 deviceId:%s,pageNum:%s,PageSize:%s,startTime:%s,endTime:%s]",
				deviceId, pageNum, pageSize, startTime, endTime));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[getHistorySateByPage()->设备id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败，设备id不能为空");
			}
			if (StringUtils.isBlank(pageNum) || !StringUtils.isNumeric(pageNum)) {
				logger.error("[getHistorySateByPage()->分页信息pageNum不能为空或非数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("分页信息pageNum不能为空或非数字!!");
			}
			if (StringUtils.isBlank(pageSize) || !StringUtils.isNumeric(pageSize)) {
				logger.error("[getHistorySateByPage()->分页信息PageSize不能为空或非数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("分页信息PageSize不能为空或非数字!");
			}
			String startTimeStamp = null;
			String endTimeStamp = null;
			if (!StringUtils.isBlank(startTime)) {
				boolean validDate = DateUtils.isValidDate(startTime);
				if (!validDate) {
					logger.error("[getHistorySateByPage()->开始日期格式错误!]");
					return RpcResponseBuilder.buildErrorRpcResp("开始日期格式错误!");
				}
				startTimeStamp = DateUtils.timeToSecond(startTime);
			}
			if (!StringUtils.isBlank(endTime)) {
				boolean validDate = DateUtils.isValidDate(endTime);
				if (!validDate) {
					logger.error("[getHistorySateByPage()->结束日期格式错误!]");
					return RpcResponseBuilder.buildErrorRpcResp("结束日期格式错误!");
				}
				endTimeStamp = DateUtils.timeToSecond(endTime);
			}

			int pageNumInt = Integer.parseInt(pageNum);
			int pageSizeInt = Integer.parseInt(pageSize);

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, "timestamp")));
			query.addCriteria(Criteria.where("deviceId").is(deviceId));
			// 根据时间查询
			Criteria criteria = new Criteria("timestamp");
			if (!StringUtils.isBlank(startTimeStamp)) {
				criteria.gte(startTimeStamp);
			}
			if (!StringUtils.isBlank(endTimeStamp)) {
				criteria.lte(endTimeStamp);
			}
			if (!StringUtils.isBlank(startTimeStamp) || !StringUtils.isBlank(endTimeStamp)) {
				query.addCriteria(criteria);
			}

			// 分页查询设备历史数据上报状态
			Pagination<Map<String, Object>> pageResult = (Pagination<Map<String, Object>>) MongoPageUtil
					.getPage(mongoTemplate, pageNumInt, pageSizeInt, query, "deviceHistoryState");

			logger.debug(String.format("[getHistorySateByPage()->success:%s]", pageResult));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", pageResult);

		} catch (Exception e) {
			logger.error("[getHistorySateByPage()->exception]", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#queryDeviceInfoById(java.lang.String)
	 */
	@Override
	public RpcResponse<Device> queryDeviceInfoById(String deviceId) {
		logger.info(String.format("[queryDeviceInfoById()-->进入方法,参数：deviceId【%s】]", deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[queryDeviceInfoById()-->设备id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空");
			}
			Device device = deviceQueryRepository.queryDeviceInfoById(deviceId);

			logger.info("{queryDeviceInfoById()--success:查询成功");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", device);

		} catch (Exception e) {
			logger.error("queryDeviceInfoById()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#queryDeviceByhardwareIdAndAS(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Device> queryDeviceByhardwareIdAndAS(String hardwareId, String accessSecret) {
		logger.info(String.format("[queryDeviceByhardwareIdAndAS()-->进入方法,参数：hardwareId【%s】 accessSecret【%s】]",
				hardwareId, accessSecret));
		try {
			if (StringUtils.isBlank(hardwareId)) {
				logger.error("[queryDeviceByhardwareIdAndAS()-->设备硬件编码不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备硬件编码不能为空");
			}
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[queryDeviceByhardwareIdAndAS()-->接入方密钥不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
			}
			Device device = deviceQueryRepository.queryDeviceByhardwareIdAndAS(hardwareId, accessSecret);

			logger.info("[queryDeviceByhardwareIdAndAS()--success:查询成功]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", device);

		} catch (Exception e) {
			logger.error("queryDeviceByhardwareIdAndAS()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#getCountDeviceRealState(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getCountDeviceRealState(JSONObject jsonParam) {
		logger.info(String.format("[getCountDeviceRealState()-->进入方法,参数：jsonParam【%s】 ]", jsonParam));
		try {
			if (null == jsonParam) {
				logger.error("[getCountDeviceRealState()-->查询参数为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询参数为空!");
			}
			if (StringUtils.isBlank(jsonParam.getString(PublicConstants.ACCESSSECRET))) {
				logger.error("[getCountDeviceRealState()-->接入方密钥不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
			}
			if (StringUtils.isBlank(jsonParam.getString(PublicConstants.PUBLIC_PAGE_NUM))
					|| !StringUtils.isNumeric(jsonParam.getString(PublicConstants.PUBLIC_PAGE_NUM))) {
				logger.error("[getCountDeviceRealState()->分页信息pageNum不能为空或非数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("分页信息pageNum不能为空或非数字!!");
			}
			if (StringUtils.isBlank(jsonParam.getString(PublicConstants.PUBLIC_PAGE_SIZE))
					|| !StringUtils.isNumeric(jsonParam.getString(PublicConstants.PUBLIC_PAGE_SIZE))) {
				logger.error("[getCountDeviceRealState()->分页信息PageSize不能为空或非数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("分页信息PageSize不能为空或非数字!");
			}

			int pageNumInt = Integer.parseInt(jsonParam.getString("pageNum"));
			int pageSizeInt = Integer.parseInt(jsonParam.getString("pageSize"));

			RpcResponse<JSONObject> paramJson = getParamJson(jsonParam);
			if (!paramJson.isSuccess()) {
				return RpcResponseBuilder.buildErrorRpcResp(paramJson.getMessage());
			}

			PageHelper.startPage(pageNumInt, pageSizeInt);
			List<Map<String, Object>> countDeviceRealState = deviceQueryRepository
					.getCountDeviceRealState(paramJson.getSuccessValue());
			if (null == countDeviceRealState) {
				logger.error("[getCountDeviceRealState()->查询时操作mysql失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询时操作mysql失败!");
			}

			PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(countDeviceRealState);

			logger.debug(String.format("[getHistorySateByPage()->success:%s]", pageInfo));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", pageInfo);

		} catch (Exception e) {
			logger.error("queryDeviceByhardwareIdAndAS()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	private RpcResponse<JSONObject> getParamJson(JSONObject jsonParam) {
		List<String> params = Arrays.asList(DeviceContants.DEVICE_BV_MIN,DeviceContants.DEVICE_BV_MAX,DeviceContants.DEVICE_SIG_MIN,DeviceContants.DEVICE_SIG_MAX,DeviceContants.DEVICE_RSRP_MIN,DeviceContants.DEVICE_RSRP_MAX,DeviceContants.DEVICE_SINR_MIN,DeviceContants.DEVICE_SINR_MAX);
        
		for(String key : params){
			if (!StringUtils.isBlank(jsonParam.getString(key))) {
				String newStr = jsonParam.getString(key);
				if (newStr.contains(CODE_H) && UtilTool.isNumber(newStr.substring(1))) {
					jsonParam.put(key, -Double.parseDouble(newStr.substring(1)));
				} else if (newStr.contains(CODE_H) && !UtilTool.isNumber(newStr.substring(1))) {
					logger.error(String.format("[getCountDeviceRealState()->%s不能为非数字!]", key));
					return RpcResponseBuilder.buildErrorRpcResp(String.format("%s不能为非数字!", key));
				} else if (!newStr.contains(CODE_H) && UtilTool.isNumber(newStr)) {
					jsonParam.put(key, Double.parseDouble(newStr));
				} else if (!newStr.contains(CODE_H) && !UtilTool.isNumber(newStr)) {
					logger.error(String.format("[getCountDeviceRealState()->%s不能为非数字!]", key));
					return RpcResponseBuilder.buildErrorRpcResp(String.format("%s不能为非数字!", key));
				}
			}
		}
		return RpcResponseBuilder.buildSuccessRpcResp("封装成功!", jsonParam);
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#exportCountDeviceRealState(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> exportCountDeviceRealState(JSONObject jsonParam) {
		logger.info(String.format("[exportCountDeviceRealState()-->进入方法,参数：jsonParam【%s】 ]", jsonParam));
		try {
			if (null == jsonParam) {
				logger.error("[exportCountDeviceRealState()-->查询参数为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询参数为空!");
			}
			if (StringUtils.isBlank(jsonParam.getString(PublicConstants.ACCESSSECRET))) {
				logger.error("[exportCountDeviceRealState()-->接入方密钥不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
			}

			RpcResponse<JSONObject> paramJson = getParamJson(jsonParam);
			if (!paramJson.isSuccess()) {
				return RpcResponseBuilder.buildErrorRpcResp(paramJson.getMessage());
			}

			List<Map<String, Object>> countDeviceRealState = deviceQueryRepository
					.getCountDeviceRealState(paramJson.getSuccessValue());
			if (null == countDeviceRealState) {
				logger.error("[exportCountDeviceRealState()->查询时操作mysql失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询时操作mysql失败!");
			}
			if (countDeviceRealState.size() > 0) {
				for (Map<String, Object> map : countDeviceRealState) {
					String completeAddress = UtilTool.objectToString(map.get("completeAddress"));
					String address = UtilTool.objectToString(map.get("address"));
					if (completeAddress.length() > 0 && address.length() > 0) {
						map.put("completeAddress", completeAddress.substring(0, completeAddress.indexOf(address)));
					}
					if ("close".equals(UtilTool.objectToString(map.get("device_ls")))) {
						map.put("device_ls", "关闭");
					} else if ("open".equals(UtilTool.objectToString(map.get("device_ls")))) {
						map.put("device_ls", "开启");
					} else if ("illegally open".equals(UtilTool.objectToString(map.get("device_ls")))) {
						map.put("device_ls", "非法开启");
					} else if ("emergency open".equals(UtilTool.objectToString(map.get("device_ls")))) {
						map.put("device_ls", "应急开启");
					}
				}
			}
			logger.debug(String.format("[exportCountDeviceRealState()->success:%s]", countDeviceRealState));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", countDeviceRealState);

		} catch (Exception e) {
			logger.error("exportCountDeviceRealState()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#findFaultOrderByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<String> findFaultOrderByDeviceId(String deviceId) {
		logger.info(String.format("[findFaultOrderByDeviceId()-->进入方法,参数：【%s】 ]", deviceId));
		try {

			if (StringUtils.isBlank(deviceId)) {
				logger.error("[findFaultOrderByDeviceId()-->查询参数deviceId为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询参数deviceId为空!");
			}

			String faultOrderId = deviceQueryRepository.findFaultOrderByDeviceId(deviceId);

			logger.info(String.format("[findFaultOrderByDeviceId()->info:%s=%s]", PublicConstants.PARAM_SUCCESS,
					faultOrderId));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, faultOrderId);
		} catch (Exception e) {
			logger.error("findFaultOrderByDeviceId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#findDeviceId(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<List<String>> findDeviceId(String gatewayId, String subDeviceId) {
		logger.info(String.format("[findDeviceId()-->进入方法,参数：deviceId:%s gatewayId:%s ]", gatewayId, subDeviceId));
		try {
			if (StringUtils.isBlank(gatewayId)) {
				logger.error("[findDeviceId()-->gatewayId为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("gatewayId为空!");
			}
			if (StringUtils.isBlank(subDeviceId)) {
				logger.error("[findDeviceId()-->subDeviceId为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("subDeviceId为空!");
			}

			List<String> faultOrderId = deviceQueryRepository.findDeviceId(gatewayId, subDeviceId);

			logger.info(String.format("[findDeviceId()->查询出的设备id:%s]", faultOrderId));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, faultOrderId);
		} catch (Exception e) {
			logger.error("findDeviceId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#queryDeviceByRule(int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> queryDeviceByRule(int pageNum, int pageSize, String accessSecret,
			String alarmDesc) {
		logger.info(String.format("[queryDeviceByRule()-->进入方法,参数：accessSecret:%s alarmDesc:%s ]", accessSecret, alarmDesc));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[queryDeviceByRule()-->accessSecret为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("accessSecret为空!");
			}
			if (StringUtils.isBlank(alarmDesc)) {
				logger.error("[queryDeviceByRule()-->alarmDesc为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("alarmDesc为空!");
			}
			Map<String,Object> paramMap=new HashMap<String,Object>();
			int pageNumMySql = 0;
			if (pageNum > 0) {
				pageNumMySql = (pageNum - 1) * pageSize;
			}
			paramMap.put("NumberPage", pageNumMySql);
			paramMap.put("pageSize", pageSize);
			paramMap.put("accessSecret",accessSecret);
			paramMap.put("alarmDesc",alarmDesc);
			List<Map<String, Object>> listMap=deviceQueryRepository.getQueryDeviceByRule(paramMap);
			if(!listMap.isEmpty()) {
				logger.info(LogMessageContants.QUERY_SUCCESS);
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, listMap);
			}else {
				return RpcResponseBuilder.buildErrorRpcResp("告警规则查询设备失败！");
			}
		}catch(Exception e) {
			logger.error("queryDeviceByRule()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}


	/**
	 * @see com.run.locman.api.query.service.DeviceQueryService#countDeviceTimingTrigger(com.alibaba.fastjson.JSONObject)
	 */
	public RpcResponse<Pages<Map<String, Object>>> countDeviceTimingTrigger(JSONObject jsonParam) {
		logger.info(String.format("[countDeviceTimingTrigger()方法执行开始...,参数：【%s】]", jsonParam.toString()));
		try {
			
			if (!jsonParam.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[countDeviceTimingTrigger()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(jsonParam.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[countDeviceTimingTrigger()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!jsonParam.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[countDeviceTimingTrigger()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(jsonParam.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[countDeviceTimingTrigger()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (!jsonParam.containsKey(AlarmInfoConstants.USC_ACCESS_SECRET)
					|| StringUtils.isBlank(jsonParam.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[countDeviceTimingTrigger()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			//定义出开始时间dateNum
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			//5天前的00:00:00
			String startTime=Long.parseLong(timeStamp1)-(86400000 * 5)+"";
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd");
			String dateNum = tempDate.format(new Date(Long.parseLong(startTime)));
			
			Map<String,Object> map=Maps.newHashMap();
			String searchKey=jsonParam.getString("searchKey");
			Pages<Map<String, Object>> pages = new Pages<Map<String, Object>>();
			int size = jsonParam.getIntValue(AlarmInfoConstants.PAGE_SIZE);
			int start=pages.getStart(jsonParam.getIntValue(AlarmInfoConstants.PAGE_NO), size);
			map.put("searchKey", searchKey);
			map.put("size", size);
			map.put("start", start);
			map.put("dateNum", dateNum);
			map.put("accessSecret", jsonParam.getString(AlarmInfoConstants.USC_ACCESS_SECRET));
			List<Map<String ,Object>> listMap=deviceQueryRepository.getCountDeviceTimingTrigger(map);
			Set<String> deviceIdMap=new HashSet<String>();
			List<Map<String, Object>> linkedList=new LinkedList<Map<String,Object>>();
			for(Map<String,Object> deviceMap:listMap) {
				String deviceId=deviceMap.get("deviceId")+"";
				deviceIdMap.add(deviceId);
			}
			for(String deviceId1:deviceIdMap) {
				Map<String ,Object> allMap=new HashMap<String,Object>();
				allMap.put("deviceId", deviceId1);
				
				for(Map<String,Object> deviceMap:listMap){
					if(deviceId1.equals(deviceMap.get("deviceId")+"")) {
						if(!allMap.containsKey("device_fv")) {
							allMap.put("device_fv", deviceMap.get("device_fv"));
						}
						if(!allMap.containsKey("address")) {
							allMap.put("address", deviceMap.get("address"));
						}
						if(!allMap.containsKey("facilityId")) {
							allMap.put("facilityId", deviceMap.get("facilityId"));
						}
						if(!allMap.containsKey("facilitiesCode")) {
							allMap.put("facilitiesCode", deviceMap.get("facilitiesCode"));
						}
						if(!allMap.containsKey("deviceName")) {
							allMap.put("deviceName", deviceMap.get("deviceName"));
						}
						if(!allMap.containsKey("countSum")) {
							String a = deviceMap.get("countSum") + "";
							String substring = a.substring(0, a.indexOf("."));
							allMap.put("countSum", substring);
						}
						Map<String ,Object> countMap=new HashMap<String,Object>();
						String dateNum1=deviceMap.get("dateNum")+"";
						String countTiming1=deviceMap.get("countTiming")+"";
						String countTrigger1=deviceMap.get("countTrigger")+"";
						countMap.put("countTiming", countTiming1);
						countMap.put("countTrigger", countTrigger1);
						allMap.put(dateNum1, countMap);
					}
					
				}
				linkedList.add(allMap);
			}
			
			Collections.sort(linkedList, new Comparator<Map<String, Object>>() {
			      public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			        Integer name1 = Integer.valueOf(o1.get("countSum")+"") ;//name1是从你list里面拿出来的一个 
			        Integer name2 = Integer.valueOf(o2.get("countSum")+"") ; //name2是从你list里面拿出来的第二个name
			        return name2.compareTo(name1); 
			      }
			    });
			
			int total=deviceQueryRepository.getAllCountDeviceTimingTrigger(map);
			pages.setTotal(total);
			pages.setList(linkedList);
			return RpcResponseBuilder.buildSuccessRpcResp("成功！", pages);
		}catch(Exception e) {
			logger.error("countDeviceTimingTrigger()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
		
	}

	/*@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@Override
	public RpcResponse<List<Map<String, Object>>> countDeviceTimingTrigger1(JSONObject jsonParam) {
		logger.info(String.format("[countDeviceTimingTrigger()-->进入方法 ]"));
		try {
			if (null == jsonParam) {
				logger.error("[countDeviceTimingTrigger()-->查询参数为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询参数为空!");
			}
			if (StringUtils.isBlank(jsonParam.getString(PublicConstants.ACCESSSECRET))) {
				logger.error("[countDeviceTimingTrigger()-->接入方密钥不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
			}			
			if (StringUtils.isBlank(jsonParam.getString(PublicConstants.PUBLIC_PAGE_NUM))
					|| !StringUtils.isNumeric(jsonParam.getString(PublicConstants.PUBLIC_PAGE_NUM))) {
				logger.error("[countDeviceTimingTrigger()->分页信息pageNum不能为空或非数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("分页信息pageNum不能为空或非数字!!");
			}
			if (StringUtils.isBlank(jsonParam.getString(PublicConstants.PUBLIC_PAGE_SIZE))
					|| !StringUtils.isNumeric(jsonParam.getString(PublicConstants.PUBLIC_PAGE_SIZE))) {
				logger.error("[countDeviceTimingTrigger()->分页信息PageSize不能为空或非数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("分页信息PageSize不能为空或非数字!");
			}
			
			//获取近5天时间段的时间戳
			
			//当天的00：00：00
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			//5天前的00-昨天的23：59：59
			String startTime=Long.parseLong(timeStamp1)-(86400000 * 5)+"";
			String endTime=Long.parseLong(timeStamp1)-1000+"";
//			
//			int pageNumInt = Integer.parseInt(jsonParam.getString("pageNum"));
//			int pageSizeInt = Integer.parseInt(jsonParam.getString("pageSize"));
//			PageHelper.startPage(pageNumInt, pageSizeInt);
			List<Map<String, Object>> listMap=deviceQueryRepository.getCountDeviceTimingTrigger();
			//查出展示数据（提高查询速度）
			BasicDBObject fieldsObject = new BasicDBObject();
			fieldsObject.put("attributeInfo.attributeReportedValue", 1);
			fieldsObject.put("attributeInfo.attributeName",1);
			fieldsObject.put("timestamp",1);
			fieldsObject.put("deviceId",1);
			DBObject dbObject = new BasicDBObject("$gte", startTime).append("$lte", endTime);
			DBObject ageCompare = new BasicDBObject("timestamp", dbObject);
			Query query = new BasicQuery(ageCompare, fieldsObject);
			List<Map> deviceHistoryStateMap = mongoTemplate.find(query, Map.class, "deviceHistoryState");
			
			for(Map<String,Object> deviceMap:listMap) {
				int countTiming =0;
				int countTrigger=0;
				int countTiming1 =0;
				int countTrigger1=0;
				int countTiming2 =0;
				int countTrigger2=0;
				int countTiming3 =0;
				int countTrigger3=0;
				int countTiming4 =0;
				int countTrigger4=0;
				String deviceId=deviceMap.get("id")+"";
					//设备5天上报的历史数据
					for(Map<String,Object> parseObject:deviceHistoryStateMap) {
						
						if((parseObject.get("deviceId")+"").equals(deviceId)) {
							List<Map> attributeInfoMaps=(List<Map>) parseObject.get("attributeInfo");
							Long timestamp=Long.parseLong(parseObject.get("timestamp")+"");
							if(timestamp>=Long.parseLong(startTime)&&timestamp<Long.parseLong(startTime)+86400000) {
								for(Map<String,Object> map:attributeInfoMaps) {
									String rt=map.get("attributeReportedValue")+"";
									if(rt.equals("timing")) {
										countTiming++;
									}else if(rt.equals("trigger")) {
										countTrigger++;
									}
								}
								
							}else if(timestamp>=Long.parseLong(startTime)+86400000 && timestamp<Long.parseLong(startTime)+86400000*2) {
								for(Map<String,Object> map:attributeInfoMaps) {
									String rt=map.get("attributeReportedValue")+"";
									if(rt.equals("timing")) {
										countTiming1++;
									}else if(rt.equals("trigger")) {
										countTrigger1++;
									}
								}
							}else if(timestamp>=Long.parseLong(startTime)+86400000*2 && timestamp<Long.parseLong(startTime)+86400000*3) {
								for(Map<String,Object> map:attributeInfoMaps) {
									String rt=map.get("attributeReportedValue")+"";
									if(rt.equals("timing")) {
										countTiming2++;
									}else if(rt.equals("trigger")) {
										countTrigger2++;
									}
								}
							}else if(timestamp>=Long.parseLong(startTime)+86400000*3 && timestamp<Long.parseLong(startTime)+86400000*4) {
								for(Map<String,Object> map:attributeInfoMaps) {
									String rt=map.get("attributeReportedValue")+"";
									if(rt.equals("timing")) {
										countTiming3++;
									}else if(rt.equals("trigger")) {
										countTrigger3++;
									}
								}
							}else {
								for(Map<String,Object> map:attributeInfoMaps) {
									String rt=map.get("attributeReportedValue")+"";
									if(rt.equals("timing")) {
										countTiming4++;
									}else if(rt.equals("trigger")) {
										countTrigger4++;
									}
								}
							}
							
						}
					}
				Map dateMap=new HashMap<>();
				dateMap.put("countTiming", countTiming);
				dateMap.put("countTrigger", countTrigger);
				Map dateMap1=new HashMap<>();
				dateMap1.put("countTiming1", countTiming1);
				dateMap1.put("countTrigger1", countTrigger1);
				Map dateMap2=new HashMap<>();
				dateMap2.put("countTiming2", countTiming2);
				dateMap2.put("countTrigger2", countTrigger2);
				Map dateMap3=new HashMap<>();
				dateMap3.put("countTiming3", countTiming3);
				dateMap3.put("countTrigger3", countTrigger3);
				Map dateMap4=new HashMap<>();
				dateMap4.put("countTiming4", countTiming4);
				dateMap4.put("countTrigger4", countTrigger4);
				SimpleDateFormat tempDate1 = new SimpleDateFormat("yyyy-MM-dd");
				String dateNum1 = tempDate1.format(new Date(Long.parseLong(startTime))); 
				String dateNum2 = tempDate1.format(new Date(Long.parseLong(startTime)+86400000));
				String dateNum3 = tempDate1.format(new Date(Long.parseLong(startTime)+86400000*2));
				String dateNum4 = tempDate1.format(new Date(Long.parseLong(startTime)+86400000*3));
				String dateNum5 = tempDate1.format(new Date(Long.parseLong(startTime)+86400000*4));
				
				deviceMap.put(dateNum1, dateMap);
				deviceMap.put(dateNum2, dateMap1);
				deviceMap.put(dateNum3, dateMap2);
				deviceMap.put(dateNum4, dateMap3);
				deviceMap.put(dateNum5, dateMap4);
				deviceMap.put("countAll", countTiming+countTrigger+countTiming1+countTrigger1+countTiming2+countTrigger2+countTiming3+countTrigger3+countTiming4+countTrigger4);
			}
			Collections.sort(listMap, new Comparator<Map<String, Object>>() {
			      public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			        Integer name1 = Integer.valueOf(o1.get("countAll").toString()) ;//name1是从你list里面拿出来的一个 
			        Integer name2 = Integer.valueOf(o2.get("countAll").toString()) ; //name2是从你list里面拿出来的第二个name
			        return name1.compareTo(name2); 
			      }
			    });
			System.out.println(listMap);
			
			logger.debug(String.format("[countDeviceTimingTrigger()->success:%s]", listMap));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", listMap);
		}catch(Exception e) {
			logger.error("countDeviceTimingTrigger()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}*/

}