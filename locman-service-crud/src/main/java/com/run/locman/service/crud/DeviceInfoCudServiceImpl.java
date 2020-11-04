/*
 * File name: DeviceInfoCudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2018年2月2日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DeviceInfoCudRepository;
import com.run.locman.api.crud.repository.DeviceTypeCudRepository;
import com.run.locman.api.crud.repository.FactoryAppTagCrudRepository;
import com.run.locman.api.crud.repository.FactoryCrudRepository;
import com.run.locman.api.crud.service.DeviceInfoCudService;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceType;
import com.run.locman.api.entity.Factory;
import com.run.locman.api.entity.FactoryAppTag;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;

/**
 * @Description: 设备基础数据写入业务实现类
 * @author: qulong
 * @version: 1.0, 2018年2月2日
 */
@Transactional(rollbackFor = Exception.class)
public class DeviceInfoCudServiceImpl implements DeviceInfoCudService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceInfoCudRepository	deviceInfoCudRepository;

	@Autowired
	private DeviceTypeCudRepository	deviceTypeCudRepository;
	
	@Autowired
	private FactoryQueryService 			factoryQueryService;

	@Autowired
	private FactoryCrudRepository 			factoryCrudRepository;
	@Autowired
	private FactoryAppTagCrudRepository		factoryAppTagCrudRepository;

	



	/**
	 * @see com.run.locman.api.crud.service.DeviceInfoCudService#deleteDevices(java.util.List)
	 */
	@Override
	public RpcResponse<Integer> deleteDevices(List<String> ids) {
		if (ids == null || ids.isEmpty()) {
			logger.error("[deleteDevices --> error: 设备id集合不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设备id集合不能为空");
		}
		try {
			logger.info(String.format("[deleteDevices()->参数%s]", ids));
			Integer saveResult = deviceInfoCudRepository.deleteDeviceBatch(ids);
			if (saveResult > 0) {
				logger.info("[deleteDevices --> success: 删除成功(" + saveResult + ") ]");
				return RpcResponseBuilder.buildSuccessRpcResp("删除成功", saveResult);
			} else {
				logger.error("[deleteDevices --> error: 删除失败");
				return RpcResponseBuilder.buildErrorRpcResp("删除失败");
			}
		} catch (Exception e) {
			logger.error("[deleteDevices --> Exception: ]", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceInfoCudService#synchroDevices(java.util.List,
	 *      java.util.List, java.util.List, java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> synchroDevices(List<Device> devices, List<DeviceType> deviceTypes,
			List<String> appTagList, String accessSecret) {
		if (null == devices) {
			logger.error("[synchroDevices --> error: 设备集合不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设备集合不能为空");
		}
		if (null == deviceTypes) {
			logger.error("[synchroDevices --> error: 设备集合不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设备集合不能为空");
		}

		try {
			logger.info(String.format("[synchroDevices()->参数:devices:%s ; deviceTypeList:%s ; accessSecret:%s]",
					devices.toString(), deviceTypes.toString(), accessSecret));
			// 已有的设备类型不能重复添加
			List<DeviceType> deviceTypeList = deviceTypeCudRepository.queryDeviceTypeByAS(accessSecret);
			for (DeviceType oldDeviceType : deviceTypeList) {
				Iterator<DeviceType> iterator = deviceTypes.iterator();
				while (iterator.hasNext()) {
					DeviceType newDeviceType = iterator.next();
					if (oldDeviceType.getId().equals(newDeviceType.getId())) {
						iterator.remove();
					}
				}
			}
			// 删除原有设备
			Integer deleteDeviceBatch = deviceInfoCudRepository.deleteDeviceBatchByAppTags(appTagList);
			logger.info(String.format("[synchroDevices --> success: 删除原有设备数量: %s]", deleteDeviceBatch));

			// 保存新的设备
			if (!devices.isEmpty()) {
				Integer saveDeviceBatch = deviceInfoCudRepository.saveDeviceBatch(devices);
				if (saveDeviceBatch < 0) {
					logger.error("[synchroDevices --> error: 设备保存失败!!!]");
					return RpcResponseBuilder.buildErrorRpcResp("设备保存失败!!!");
				}
				logger.info(String.format("[synchroDevices --> success: 保存新的设备数量: %s]", saveDeviceBatch));
			}

			// 保存失败类型
			if (!deviceTypes.isEmpty()) {

				Integer addDeviceType = deviceTypeCudRepository.addDeviceType(deviceTypes);
				if (addDeviceType < 0) {
					logger.error("[synchroDevices --> error: 设备类型保存失败!!!]");
					return RpcResponseBuilder.buildErrorRpcResp("设备类型保存失败!!!");
				}

				logger.info(String.format("[synchroDevices --> success: 保存设备类型数量: %s]", addDeviceType));
			}
			// 级联删除设备类型接入方下的数据
			deleteCascadeDeviceTypeData(accessSecret);
			return RpcResponseBuilder.buildSuccessRpcResp("设备保存成功!!!", true);

		} catch (Exception e) {
			logger.error("[synchroDevices --> Exception: ]", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:删除与设备类型相关联的数据（告警规则，设备类型,设备属性模板）
	 * @param accessSecret
	 * @return
	 */
	private void deleteCascadeDeviceTypeData(String accessSecret) throws Exception {
		// 删除设备类型与告警规则关联数据
		deleteCascadeData(accessSecret, deviceTypeCudRepository::deleteCascadeAlarmRuleById);
		// 删除自定义告警规则下设备id都不存在的规则
		deleteCascadeData(accessSecret, deviceTypeCudRepository::deleteAlarmRuleByNotDeviceId);
		// 查询设备id不存在的关联告警规则(id) 并且删除设备id与告警规则关联表，
		deleteCascadeData(accessSecret, deviceTypeCudRepository::deleteAlarmRuleIdByAccess);
		// 删除设备类型数量
		deleteCascadeData(accessSecret, deviceTypeCudRepository::deleteUnusedDeviceTypeByAS);
		// 修改告警规则关联设备总条数
		updateAlarmRuleDeviceCount(accessSecret);
	}



	/**
	 * 
	 * @Description:lambda调用不同的删除方法
	 * @param accessSecret
	 * @param function
	 * @return
	 */
	private void deleteCascadeData(String accessSecret, Function<String, Integer> function) throws Exception {
		Integer sucCount = function.apply(accessSecret);
		logger.info(String.format("[deleteCascadeData()->success:删除成功数量:%s]", sucCount));
	}



	/**
	 * 
	 * @Description:修改告警规则中关联的告警设备id数量
	 * @param accessSecret
	 * @throws Exception
	 */
	private void updateAlarmRuleDeviceCount(String accessSecret) throws Exception {
		Integer sucCount = Optional.ofNullable(deviceTypeCudRepository.selectAlarmRuleDeviceCount(accessSecret))
				.filter(u -> !u.isEmpty()).map(deviceTypeCudRepository::updateAlarmRuleDeivceCount).orElse(0);
		logger.info(String.format("[updateAlarmRuleDeviceCount()->success:修改成功数量:%s]", sucCount));
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceInfoCudService#updateDeviceDefendState(java.lang.String,
	 *      java.lang.Boolean)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Boolean> updateDeviceDefendState(List<String> deviceIds, Boolean deviceState) {
		logger.info(String.format("[updateDeviceDefendState()->rpc params :%s,%s]", deviceIds, deviceState));
		try {
			if (null == deviceIds || deviceIds.isEmpty() || null == deviceState) {
				logger.error("[updateDeviceDefendState()->error:设备ids和devicestate表示不能为null]");
				return RpcResponseBuilder.buildErrorRpcResp("设备id和devicestate表示不能为null");
			}

			for (String deviceId : deviceIds) {
				Map map = Maps.newHashMap();
				map.put(DeviceContants.DEVICEID, deviceId);
				map.put(DeviceContants.DEVICE_STATE, String.valueOf(deviceState));
				Integer updateDeviceDefendState = deviceInfoCudRepository.updateDeviceDefendState(map);
				if (updateDeviceDefendState < 0) {
					logger.error("[updateDeviceDefendState --> error: 修改设备维护状态失败！]");
					return RpcResponseBuilder.buildErrorRpcResp(" 修改设备维护状态失败！");
				}
			}

			logger.info("[updateDeviceDefendState()-->suc:设备维护状态修改成功！]");
			return RpcResponseBuilder.buildSuccessRpcResp("设备维护状态修改成功！", true);
		} catch (Exception e) {
			logger.error("[updateDeviceDefendState --> Exception: ]", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceInfoCudService#synchroDevicesForDataConversion(com.alibaba.fastjson.JSONArray,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public RpcResponse<String> synchroDevicesForDataConversion(JSONArray jsonArray,String accessSecret,String locmanDeviceTypeId,String dcDeviceTypeId,String applicationId) {
		// TODO Auto-generated method stub
		try {
			logger.info(String.format(
					"[synchroDevicesForDataConversion->进入方法,accessSecret:%s,locmanDeviceTypeId:%s,deviceSize:%s,dcDeviceTypeId:%s,applicationId:%s]",
					accessSecret, locmanDeviceTypeId, jsonArray.size(),dcDeviceTypeId,applicationId));
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[synchroDevicesForDataConversion->传入accessSecret为空，不同步]");
				return RpcResponseBuilder.buildErrorRpcResp("accessSecret不能为空");
			}
			
		
			//同步厂家（一个接入方对应一个厂家）
			//查询该接入方是否存在
			RpcResponse<List<Map<String, String>>> downBoxFactory = factoryQueryService.downBoxFactory(accessSecret);
			//厂家id
			String factoryId="";
			if(downBoxFactory.isSuccess()) {
				List<Map<String, String>> successValue = downBoxFactory.getSuccessValue();
				if(null !=successValue && successValue.size()>0) {
					logger.info("[synchroDevicesForDataConversion->厂家已经存在!]");
					factoryId=successValue.get(0).get("id")+"";
				}else {
					logger.info("[synchroDevicesForDataConversion->厂家不存在，新增厂家!]");
					Factory factory = new Factory();
					factory.setAccessSecret(accessSecret); 
					factory.setAddress("成都市");
					factory.setContacts("sefon"); 
					factory.setContactsPhone("1234");
					factory.setFactoryName("四方");
					factory.setId(UtilTool.getUuId());
					factory.setManageState("enabled");
					factory.setRemark("1");
					int insertModel = factoryCrudRepository.insertNewFactory(factory);
					if(insertModel > 0) {
						logger.info("[synchroDevicesForDataConversion->厂家新增成功！]");
						factoryId=factory.getId();
					}
					else{
						logger.error("[synchroDevicesForDataConversion->厂家新增失败！]");
						return RpcResponseBuilder.buildErrorRpcResp("厂家新增失败,设备同步失败!!!");
					}
				}
			}
			
			RpcResponse<List<String>> queryFactoryByAppTag = factoryQueryService.queryFactoryByAppTag(applicationId);
			if (queryFactoryByAppTag.isSuccess()) {
				List<String> appTag = queryFactoryByAppTag.getSuccessValue();
				if (appTag == null || appTag.size() == 0) {
					logger.info("[synchroDevicesForDataConversion->根据appTag查询不到中间表信息，新增中间表记录！]");
					//存factory_apptag中间表
					FactoryAppTag factoryApptag = new FactoryAppTag();
					factoryApptag.setId(UtilTool.getUuId());
					factoryApptag.setAppTag(applicationId);
					factoryApptag.setFactoryId(factoryId);
					int insertModel = factoryAppTagCrudRepository.insertModel(factoryApptag);
					if (insertModel > 0) {
						logger.info("[synchroDevicesForDataConversion->factory_apptag新增成功！]");
					}
					else{
						logger.error("[synchroDevicesForDataConversion->factory_apptag新增成功！]");
						return RpcResponseBuilder.buildErrorRpcResp("factory_apptag新增失败");
					}
				}
			}
			else{
				logger.error("[synchroDevicesForDataConversion->factory_apptag查询错误！]");
				return RpcResponseBuilder.buildErrorRpcResp("factory_apptag查询错误");
			}
			
		
			

			//若是locmanDeviceTypeId为空，则需新建设备类型,该设备类型id为dcDeviceTypeId(数据转换中心的设备类型id)
			if (StringUtils.isBlank(locmanDeviceTypeId)) {
				// 存储设备类型数据
				List<DeviceType> deviceTypes = Lists.newArrayList();
				DeviceType deviceType = new DeviceType();
				deviceType.setAccessSecret(accessSecret);
				deviceType.setId(dcDeviceTypeId);
				deviceType.setUpdateTime(DateUtils.formatDate(new Date()));
				deviceType.setCreateTime(DateUtils.formatDate(new Date()));
				deviceType.setCreateBy("dataConversion");
				deviceType.setUpdateBy("dataConversion");
				deviceType.setParentId("");
				deviceType.setDeviceTypeName("默认，需修改");
				deviceTypes.add(deviceType);
				Integer addDeviceTypeNum = deviceTypeCudRepository.addDeviceType(deviceTypes);
				if (addDeviceTypeNum < 0) {
					logger.error("[synchroDevicesForDataConversion()->fail: 设备类型保存失败,设备同步失败!!!]");
					return RpcResponseBuilder.buildErrorRpcResp("设备类型保存失败,设备同步失败!!!");
				}
				logger.info("[synchroDevicesForDataConversion()->success: 设备类型保存成功]");
			}
			

			String gatewayId = "dataConversion";
			String protocolType = "MQTT";
			String openProtocols = "PUBLIC";

			Iterator<Object> iterator = jsonArray.iterator();
			Map<String, Object> param = new HashMap<String, Object>();
			while (iterator.hasNext()) {
				JSONObject deviceJson = (JSONObject) iterator.next();
				String hardwareId = deviceJson.getString("hardwareId");
				String deviceName = deviceJson.getString("deviceName");
				Integer queryResult = deviceInfoCudRepository.queryDeviceExistsBySubDeviceId(hardwareId);
				if (queryResult == 0) {
					// 插入设备
					param.put("id", UtilTool.getUuId());
					param.put("deviceName", deviceName);
					param.put("protocolType", protocolType);
					param.put("openProtocols", openProtocols);
					param.put("deviceType", dcDeviceTypeId);
					param.put("appTag", applicationId);
					param.put("manageState", "enabled");
					param.put("accessSecret", accessSecret);
					param.put("gatewayId", gatewayId);
					param.put("subDeviceId", hardwareId);
					param.put("deviceKey", "");
					Integer addDeviceFromDataConversion = deviceInfoCudRepository.addDeviceFromDataConversion(param);
					if (addDeviceFromDataConversion > 0) {
						logger.info(String.format("[synchroDevicesForDataConversion->硬件id为%s新增成功]", hardwareId));
					} else {
						logger.error(String.format("[synchroDevicesForDataConversion->硬件id为%s新增失败]", hardwareId));
					}
				}
			}

			return RpcResponseBuilder.buildSuccessRpcResp("success", null);
		} catch (Exception e) {
			logger.error("[synchroDevicesForDataConversion --> Exception: ]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
