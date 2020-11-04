/*
 * File name: DeviceQuery.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年7月4日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.timer.query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.FacilitiesCrudService;
import com.run.locman.api.crud.service.FocusSecurityCrudService;
import com.run.locman.api.dto.DeviceAndTimeDto;
import com.run.locman.api.dto.FaultOrderDetetionDto;
import com.run.locman.api.entity.AttributeInfo;
import com.run.locman.api.timer.crud.service.FaultOrderProcessCudService;
import com.run.locman.api.timer.query.repository.DeviceQueryRepository;
import com.run.locman.api.timer.query.service.DeviceQuery;
import com.run.locman.api.timer.query.service.FaultOrderDeviceQueryService;
import com.run.locman.api.timer.query.service.OrderProcessQueryService;
import com.run.locman.api.timer.query.service.TimeoutReportConfigQueryService;
import com.run.locman.api.util.GetInternateTimeUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.DeviceDeletionConstants;
import com.run.locman.constants.InterGatewayConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年7月4日
 */

public class DeviceQueryImpl implements DeviceQuery {
	private static final Logger				logger			= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private TimeoutReportConfigQueryService	timeoutReportConfigQueryService;

	@Autowired
	private MongoTemplate					mongoTemplate;

	@Autowired
	private DeviceQueryRepository			deviceQueryRepository;

	@Value("${api.host}")
	private String							ip;

	@Autowired
	private OrderProcessQueryService		orderProcesQueryServiceTimer;

	@Autowired
	private FaultOrderProcessCudService		faultOrderProcessCudServiceTimer;

	@Autowired
	private FaultOrderDeviceQueryService	faultOrderDeviceQueryService;

	@Autowired
	private FacilitiesCrudService			facilitiesCrudService;
	@Autowired
	private FocusSecurityCrudService		focusSecurityCrudService;

	@Value("${continuous.days}")
	private String							continuousDays;

	@Value("${trigger.count}")
	private String							triggerCount;

	@Value("${timing.count}")
	private String							timingCount;

	@Value("${continuousTiming.days}")
	private String							continuousTimingDays;

	private static final List<String>		attributeNames	= Lists.newArrayList(DeviceContants.FA_YAVTV,
			DeviceContants.FA_ZAVTV, DeviceContants.FA_XGCM, DeviceContants.FA_YGCM, DeviceContants.FA_XGTV,
			DeviceContants.FA_YGTV);



	@Override
	public void deviceDetetion() {
		try {
			// 查询设备id和配置时间
			RpcResponse<List<DeviceAndTimeDto>> deviceAndTime = timeoutReportConfigQueryService.getDeviceAndTime();
			List<DeviceAndTimeDto> successValue = deviceAndTime.getSuccessValue();
			if (!deviceAndTime.isSuccess() || successValue == null || successValue.isEmpty()) {
				logger.error("deviceDetetion()-->未查询到设备及其配置时间:" + deviceAndTime.getMessage());
				return;
			}
			// 将相同配置时间的设备id转存在一个list中
			Map<Integer, List<String>> map = Maps.newHashMap();
			for (DeviceAndTimeDto deviceAndTimeDto : successValue) {
				if (null == deviceAndTimeDto) {
					continue;
				}
				String deviceId = deviceAndTimeDto.getDevice();
				Integer configTime = deviceAndTimeDto.getConfigTime();
				if (null == deviceAndTimeDto || null == configTime || StringUtils.isBlank(deviceId)) {
					continue;
				}
				if (configTime <= 0) {
					continue;
				}
				if (map.containsKey(configTime)) {
					map.get(configTime).add(deviceId);
				} else {
					map.put(configTime, Lists.newArrayList(deviceId));
				}

			}
			// List<String> deviceIds = Lists.newArrayList();
			List<Map<Integer, List<String>>> timeDeviceIds = Lists.newArrayList();
			// 遍历结果,查询获得超时的设备id
			for (Entry<Integer, List<String>> entry : map.entrySet()) {
				Map<Integer, List<String>> queryTimeoutReportDevice = queryTimeoutReportDevice(entry.getKey(),
						entry.getValue());

				if (null != queryTimeoutReportDevice && !queryTimeoutReportDevice.isEmpty()) {
					timeDeviceIds.add(queryTimeoutReportDevice);
				}
				// 睡眠66毫秒，防止并发过高
				Thread.sleep(66);

			}
			if (!timeDeviceIds.isEmpty()) {
				logger.info("DeviceDetetion()-->查询到超时未上报设备" + timeDeviceIds);
				startFaultOrderByIds(timeDeviceIds);
			}
		} catch (Exception e) {
			logger.error("DeviceDetetion()-->设备检测出现异常", e);
		}

	}



	/**
	 * 
	 * @Description: 查询获取指定时间未上报的设备
	 * @param
	 * @return
	 */
	private Map<Integer, List<String>> queryTimeoutReportDevice(Integer time, List<String> deviceIds) {
		logger.info("queryTimeoutReportDevices()-->查询获取指定时间未上报的设备,参数:" + "检测超时时间:" + time + "设备Id" + deviceIds);
		// 获取当前网络时间
		Long queryMillisTime = GetInternateTimeUtil.queryMillisTime();
		// List<String> deviceIds = deviceQueryRepository.queryDeviceIds();
		// 获取当前系统时间戳
		long currentTimeStamp = System.currentTimeMillis() / 1000;
		if (queryMillisTime != null) {
			// 获取当前网络时间戳
			currentTimeStamp = queryMillisTime / 1000;
		}

		// 获得指定时间前时间戳(单位:小时)
		long setTimeStamp = currentTimeStamp - time * 3600;
		// 获得指定时间前时间戳(单位:分钟)测试用
		// long setTimeStamp = currentTimeStamp - time * 60;
		// 兼容新老设备 时间判断条件,新设备timestamp为毫秒级,老设备为秒级
		Criteria orOperator = new Criteria().orOperator(
				new Criteria().andOperator(Criteria.where("timestamp").lt(setTimeStamp).and("deviceId").in(deviceIds),
						Criteria.where("things.gatewayId").exists(false)),
				new Criteria().andOperator(
						Criteria.where("timestamp").lt(setTimeStamp * 1000).and("deviceId").in(deviceIds),
						Criteria.where("things.gatewayId").exists(true)));

		/*
		 * DBObject dbCondition = new BasicDBObject();
		 * dbCondition.put("timestamp", new BasicDBObject("$lt", setTimeStamp));
		 * // 时间判断条件 dbCondition.put("deviceId", new BasicDBObject("$in",
		 * deviceIds)); // 判断 Query query = new BasicQuery(dbCondition);
		 */
		// 统计设备数量
		// long res = mongoTemplate.count(query, "deviceState");
		Query query = new Query(orOperator);
		List<JSONObject> result = mongoTemplate.find(query, JSONObject.class, "deviceState");

		// List<JSONObject> result = mongoTemplate.find(query, JSONObject.class,
		// "deviceState");
		Map<Integer, List<String>> resultMap = Maps.newHashMap();
		List<String> timeoutDeviceIds = Lists.newArrayList();
		for (JSONObject json : result) {
			String timeoutDeviceId = json.getString("deviceId");
			timeoutDeviceIds.add(timeoutDeviceId);
		}
		resultMap.put(time, timeoutDeviceIds);
		return resultMap;
	}



	/**
	 * 
	 * @Description:开始组装数据,然后发起故障工单流程
	 * @param
	 * @return
	 */
	private void startFaultOrderByIds(List<Map<Integer, List<String>>> timeDeviceIds) {

		for (Map<Integer, List<String>> map : timeDeviceIds) {
			for (Entry<Integer, List<String>> entry : map.entrySet()) {
				List<JSONObject> jsonList = Lists.newArrayList();

				getInfoForFaultOrderById(entry.getValue(), jsonList, entry.getKey());
				if (jsonList.isEmpty()) {
					logger.info("startFaultOrderByIds()-->发起故障工单数据集合为空");
					return;
				}
				for (JSONObject json : jsonList) {
					try {
						// 睡眠半秒，防止并发过高
						Thread.sleep(500);
						logger.info("addFaultOrderForDeviceDeletion()-->开始生成故障工单" + json);
						faultOrderProcessCudServiceTimer.addFaultOrderForDeviceDeletion(json);
					} catch (InterruptedException e) {
						logger.error("startFaultOrderByIds()->exception", e);
					}
				}
			}
		}

	}



	/**
	 * 查询各个接入方下，超时故障工单流程配置的人员id和组织id
	 */

	public JSONObject queryPersonIdAndOrganizeIdByAc() {
		// 查询所有接入方密钥
		List<String> allAccessSecret = deviceQueryRepository.getAllAccessSecret();
		// List<Map<String, Object>> resultInfoList = Lists.newArrayList();
		JSONObject result = new JSONObject();
		if (allAccessSecret.isEmpty()) {
			logger.error("queryPersonIdAndOrganizeIdByAc()-->没有查询到任何接入方密钥");
		}

		for (String accessSecret : allAccessSecret) {
			// 查询各个接入方，超时故障工单流程配置的人员id和组织id
			RpcResponse<List<Map<String, Object>>> personInfo = orderProcesQueryServiceTimer
					.findPersonByAccessSecret(accessSecret, "超时故障工单流程");
			List<Map<String, Object>> successValue = personInfo.getSuccessValue();
			if (!personInfo.isSuccess()) {
				logger.error("密钥为" + accessSecret + "的接入方查询超时故障工单流程失败");
				continue;
			}
			List<String> resultpersonId = Lists.newArrayList();
			// Map<String, Object> personMap = Maps.newHashMap();
			if (successValue != null && !successValue.isEmpty()) {

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
						String factoryIdsStr = InterGatewayUtil.getHttpValueByPost(InterGatewayConstants.U_FACTORY_IDS,
								json, ip, "");
						if (StringUtils.isBlank(factoryIdsStr)) {
							logger.error(
									"queryPersonIdAndOrganizeIdByAc()-->interGateway查询厂家id返回值为null" + personIdList);
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
			} else {
				logger.info("queryPersonIdAndOrganizeIdByAc()-->密钥为" + accessSecret + "的接入方没有查询到超时故障工单流程");
			}
		}

		logger.info("queryPersonIdAndOrganizeIdByAc()-->超时配置人员查询返回信息" + result);
		return result;
	}



	/**
	 * 
	 * @Description: 查询并组装发起故障工单所需字段
	 * @param
	 * @return
	 */
	private void getInfoForFaultOrderById(List<String> deviceIds, List<JSONObject> jsonList, Integer time) {
		// 若超时未上报设备id集合为空,直接返回
		if (null == deviceIds || deviceIds.isEmpty()) {
			logger.error("getInfoForFaultOrderById()-->设备id集合为空");
			return;
		}
		// 查询各个接入方下，超时故障工单流程配置的人员id和组织id
		JSONObject queryPersonIdAndOrganizeIdByAc = queryPersonIdAndOrganizeIdByAc();
		if (queryPersonIdAndOrganizeIdByAc.isEmpty()) {
			logger.error("getInfoForFaultOrderById()-->超时故障工单流程配置的人员为空或不是厂家人员");
			return;
		}
		// 查询超时未上报的设备的相关信息
		logger.info("getInfoForFaultOrderById()-->超时故障工单流程配置的人员检验通过,开始查询超时未上报的设备的相关信息");
		List<Map<String, Object>> deviceInfoForFaultOrderById = deviceQueryRepository
				.getInfoForFaultOrderById(deviceIds);
		// 组装每个设备发起故障工单所需信息
		for (Map<String, Object> deviceInfo : deviceInfoForFaultOrderById) {
			if (null == deviceInfo) {
				continue;
			}
			JSONObject json = new JSONObject();
			Object accessSecret = deviceInfo.get(DeviceDeletionConstants.ACCESS_SECRET);
			String accessSecretStr = "" + accessSecret;
			if (deviceInfo.get(DeviceDeletionConstants.ACCESS_SECRET) != null || StringUtils.isBlank(accessSecretStr)) {
				// 通过接入方密钥获取人员id
				JSONArray jsonArray = queryPersonIdAndOrganizeIdByAc.getJSONArray(accessSecretStr);
				if (null == jsonArray) {
					logger.error("getInfoForFaultOrderById()-->未能获取到密钥为:" + accessSecretStr + "的接入方配置的超时故障工单流程人员");
					continue;
				}
				List<String> personIdList = jsonArray.toJavaList(String.class);
				for (String personId : personIdList) {
					deviceInfo = for1(jsonList, time, queryPersonIdAndOrganizeIdByAc, deviceInfo, json, accessSecret,
							personId);

				}

			}
		}
	}



	@SuppressWarnings("rawtypes")
	private Map<String, Object> for1(List<JSONObject> jsonList, Integer time, JSONObject queryPersonIdAndOrganizeIdByAc,
			Map<String, Object> deviceInfo, JSONObject json, Object accessSecret, String personId) {
		if (null == deviceInfo) {
			return deviceInfo;
		}
		if (personId == null && StringUtils.isBlank(personId)) {
			logger.error("getInfoForFaultOrderById()-->人员id为空" + personId);
			return deviceInfo;
		} else {
			Map factoryIdAndOrgId = queryPersonIdAndOrganizeIdByAc.getJSONObject(personId);
			Object factoryId = factoryIdAndOrgId.get(personId);
			Object organizeId = factoryIdAndOrgId.get(factoryId);

			Object factoryIdByDeviceId = deviceInfo.get(DeviceDeletionConstants.FACTORY_ID);
			String factoryIdByDeviceIdStr = "" + factoryIdByDeviceId;
			if (factoryIdByDeviceId == null || StringUtils.isBlank("" + factoryIdByDeviceId)) {
				logger.error("getInfoForFaultOrderById()-->" + deviceInfo.get("id") + "设备厂家id为空");
				return deviceInfo;
			}
			// 设备的厂家id和配置人员的厂家id不匹配则不能发起故障工单
			if (!factoryIdByDeviceIdStr.equals(factoryId)) {
				return deviceInfo;
			}
			// 一个设备一旦匹配到厂家,就会发起故障工单,且只能发起一次
			String deviceId = deviceInfo.remove("id").toString();
			Object deviceTypeName = deviceInfo.get(DeviceDeletionConstants.DEVICE_TYPE_NAME);

			addJson(jsonList, time, deviceInfo, json, accessSecret, personId, organizeId, factoryIdByDeviceId, deviceId,
					deviceTypeName);
			deviceInfo = null;
		}
		return deviceInfo;
	}



	private void addJson(List<JSONObject> jsonList, Integer time, Map<String, Object> deviceInfo, JSONObject json,
			Object accessSecret, String personId, Object organizeId, Object factoryIdByDeviceId, String deviceId,
			Object deviceTypeName) {
		json.put(DeviceDeletionConstants.ACCESS_SECRET, accessSecret);
		json.put(DeviceDeletionConstants.MANAGER, "系统检测超时未上报");
		json.put(DeviceDeletionConstants.ORGANIZE_ID, organizeId);
		json.put(DeviceDeletionConstants.DEVICE_IDS_ADD, deviceId);
		json.put(DeviceDeletionConstants.FACTORY_ID, factoryIdByDeviceId);
		json.put(DeviceDeletionConstants.USER_ID, personId);
		// 故障工单类型为9，即：超时未上报
		json.put(DeviceDeletionConstants.FAULT_TYPE, 9);
		json.put(DeviceDeletionConstants.PHONE, deviceInfo.get(DeviceDeletionConstants.PHONE));
		// 每个超时未上报的设备生成一个工单，所以设备数量为1
		json.put(DeviceDeletionConstants.DEVICE_COUNT, 1);
		// 故障描述
		json.put(DeviceDeletionConstants.MARK, "【超时未上报】设施序列号为:" + deviceInfo.get("facilitiesCode") + ",设备编号:" + deviceId
				+ "超过【" + time + "】小时为上报状态，请核查");
		json.put(DeviceDeletionConstants.ORDER_NAME,
				"【超时未上报】设施序列号为:" + deviceInfo.get("facilitiesCode") + ",设备编号:" + deviceId);
		json.put(DeviceDeletionConstants.CONFIG_TIME, time);
		json.put(DeviceDeletionConstants.DEVICE_TYPE_NAME, deviceTypeName);
		jsonList.add(json);
	}



	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#checkOrderExist()
	 */
	@Override
	public RpcResponse<List<String>> checkOrderExist(String deviceId) {
		if (StringUtils.isBlank(deviceId)) {
			logger.error("设备id为空");
			return RpcResponseBuilder.buildErrorRpcResp("设备id为空");
		}
		logger.info(String.format("[checkOrderExist()->request params--deviceId:%s]", deviceId));
		List<String> processIds = deviceQueryRepository.checkOrderExist(deviceId);

		if (processIds == null) {
			logger.error("checkOrderExist()-->检验设备是否存在超时故障工单失败");
			return RpcResponseBuilder.buildErrorRpcResp("检验设备是否存在超时故障工单失败");
		} else {
			logger.info("checkOrderExist()-->检验设备是否存在超时故障工单成功" + processIds);
			return RpcResponseBuilder.buildSuccessRpcResp("检验设备是否存在超时故障工单成功", processIds);
		}
	}



	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#checkFaultOrder()
	 */
	@Override
	public void checkFaultOrder() {
		try {
			RpcResponse<List<String>> checkFaultOrderDevice = checkFaultOrderDevice();
			List<String> successValue = checkFaultOrderDevice.getSuccessValue();
			if (!checkFaultOrderDevice.isSuccess() || null == successValue) {
				logger.error("checkFaultOrder()-->查询超时故障工单正常上报设备失败,反馈:" + checkFaultOrderDevice.getMessage());
				return;
			}
			if (successValue.isEmpty()) {
				logger.info("checkFaultOrder()-->未查询到生成超时故障工单正常上报设备");
				return;
			}
			// 取得生成超时故障工单正常上报设备id,查询超时未上报故障工单信息
			RpcResponse<List<FaultOrderDetetionDto>> orderInfo = faultOrderDeviceQueryService
					.getOrderInfo(successValue);
			List<FaultOrderDetetionDto> result = orderInfo.getSuccessValue();
			if (!orderInfo.isSuccess() || null == result) {
				logger.error("checkFaultOrder()-->RPC--查询超时未上报故障工单信息失败");
				return;
			}
			if (result.isEmpty()) {
				logger.info("checkFaultOrder()-->RPC--未查询到超时未上报故障工单信息");
				return;
			} else {
				for (FaultOrderDetetionDto faultOrderDetetionDto : result) {
					JSONObject json = new JSONObject();
					json.put(DeviceDeletionConstants.FAULT_ORDER_ID, faultOrderDetetionDto.getId());
					json.put(DeviceDeletionConstants.PROCESS_ID, faultOrderDetetionDto.getProcessId());
					json.put(DeviceDeletionConstants.USER_ID, faultOrderDetetionDto.getUserId());
					json.put(DeviceDeletionConstants.ACCESS_SECRET, faultOrderDetetionDto.getAccessSecret());
					Thread.sleep(500);
					RpcResponse<String> resopnse = faultOrderProcessCudServiceTimer.undoTimeoutFaultOrder(json);
					String responseProcessId = resopnse.getSuccessValue();
					if (!resopnse.isSuccess() || StringUtils.isBlank(responseProcessId)) {
						logger.error(
								"checkFaultOrder()-->超时未上报故障工单撤回失败,返回信息:" + resopnse.getMessage() + "###请求参数:" + json);
						continue;
					} else {
						logger.info("checkFaultOrder()-->超时未上报故障工单撤回成功" + responseProcessId);
					}

				}
			}
		} catch (Exception e) {
			logger.error("checkFaultOrder()->exception", e);
		}
	}



	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#checkFaultOrderDevice()
	 */
	@Override
	public RpcResponse<List<String>> checkFaultOrderDevice() {
		try {
			// 将相同配置时间的设备id转存在一个list中
			Map<Integer, List<String>> map = Maps.newHashMap();
			List<String> deviceIds = Lists.newArrayList();
			List<DeviceAndTimeDto> deviceAndTimeDtos = deviceQueryRepository.checkFaultOrderDevice();
			if (null == deviceAndTimeDtos) {
				logger.error("checkFaultOrderDevice()-->超时故障工单关联的设备id查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("超时故障工单关联的设备id查询失败");
			}
			if (deviceAndTimeDtos.isEmpty()) {
				logger.info("checkFaultOrderDevice()-->未查询到超时故障工单关联的设备id");
				return RpcResponseBuilder.buildSuccessRpcResp("未查询到超时故障工单关联的设备id", Lists.newArrayList());
			}
			for (DeviceAndTimeDto deviceAndTimeDto : deviceAndTimeDtos) {
				if (null == deviceAndTimeDto) {
					continue;
				}
				String deviceId = deviceAndTimeDto.getDevice();
				Integer configTime = deviceAndTimeDto.getConfigTime();
				if (null == deviceAndTimeDto || null == configTime || StringUtils.isBlank(deviceId)) {
					continue;
				}
				if (configTime <= 0) {
					continue;
				}
				if (map.containsKey(configTime)) {
					map.get(configTime).add(deviceId);
				} else {
					map.put(configTime, Lists.newArrayList(deviceId));
				}
				deviceIds.add(deviceId);
			}

			// 遍历结果,查询获得超时的设备id
			for (Entry<Integer, List<String>> entry : map.entrySet()) {
				// 睡眠66毫秒，防止并发过高
				Thread.sleep(66);
				List<String> timeoutDeviceIds = queryTimeoutReportDevices(entry.getKey(), entry.getValue());
				if (!timeoutDeviceIds.isEmpty()) {
					deviceIds.removeAll(timeoutDeviceIds);
				}
			}
			logger.info("checkFaultOrderDevice()-->超时故障工单正常上报设备id查询成功:" + deviceIds);
			return RpcResponseBuilder.buildSuccessRpcResp("超时故障工单正常上报设备id查询成功", deviceIds);
		} catch (Exception e) {
			logger.error("checkFaultOrderDevice()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description: 查询获取指定时间未上报的设备
	 * @param
	 * @return
	 */
	private List<String> queryTimeoutReportDevices(Integer time, List<String> deviceIds) {
		logger.info("queryTimeoutReportDevices()-->参数:" + "检测超时时间:" + time + "设备Id" + deviceIds);
		// 获取当前网络时间
		Long queryMillisTime = GetInternateTimeUtil.queryMillisTime();
		// List<String> deviceIds = deviceQueryRepository.queryDeviceIds();
		// 获取当前系统时间戳
		long currentTimeStamp = System.currentTimeMillis() / 1000;
		if (queryMillisTime != null) {
			// 获取当前网络时间戳
			currentTimeStamp = queryMillisTime / 1000;
		}
		// 获得指定天数前时间戳(单位:小时)
		long setTimeStamp = currentTimeStamp - time * 3600;
		// long setTimeStamp = currentTimeStamp - time * 60;// 获得指定天数前时间戳(单位:分钟)

		// 兼容新老设备,时间判断条件,新设备timestamp为毫秒级,老设备为秒级
		Criteria orOperator = new Criteria().orOperator(
				new Criteria().andOperator(Criteria.where("timestamp").lt(setTimeStamp).and("deviceId").in(deviceIds),
						Criteria.where("things.gatewayId").exists(false)),
				new Criteria().andOperator(
						Criteria.where("timestamp").lt(setTimeStamp * 1000).and("deviceId").in(deviceIds),
						Criteria.where("things.gatewayId").exists(true)));

		Query query = new Query(orOperator);
		List<JSONObject> result = mongoTemplate.find(query, JSONObject.class, "deviceState");

		/*
		 * DBObject dbCondition = new BasicDBObject(); // 时间判断条件
		 * dbCondition.put("timestamp", new BasicDBObject("$lt", setTimeStamp));
		 * dbCondition.put("deviceId", new BasicDBObject("$in", deviceIds)); //
		 * 判断 Query query = new BasicQuery(dbCondition);
		 * 
		 * List<JSONObject> result = mongoTemplate.find(query, JSONObject.class,
		 * "deviceState");
		 */
		List<String> timeoutDeviceIds = Lists.newArrayList();
		for (JSONObject json : result) {
			String timeoutDeviceId = json.getString("deviceId");
			timeoutDeviceIds.add(timeoutDeviceId);
		}
		logger.info("queryTimeoutReportDevices()-->超时设备ID:" + timeoutDeviceIds);
		return timeoutDeviceIds;
	}



	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#insertCountTimingAndTrigger()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void insertCountTimingAndTrigger() {
		try {

			List<Map<String, Object>> listMap = deviceQueryRepository.getCountDeviceTimingTrigger();
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			// 1天前的00
			String startTime = Long.parseLong(timeStamp1) - 86400000 + "";
			RpcResponse<List<Map>> value = getCountTimingAndTrigger();
			List<Map> deviceHistoryStateMap = value.getSuccessValue();
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd");
			String dateNum = tempDate.format(new Date(Long.parseLong(startTime)));
			List<Map> endList = new ArrayList<Map>();

			for (Map<String, Object> deviceMap : listMap) {
				int countTiming = 0;
				int countTrigger = 0;

				String deviceId = deviceMap.get("deviceId") + "";
				deviceMap.put("ractv", 0);
				deviceMap.put("ractvTime", 0);
				for (Map<String, Object> deviceMap1 : deviceHistoryStateMap) {
					String deviceId1 = deviceMap1.get("deviceId") + "";
					int xgiv = 100;
					String rt = "";

					int onlyOnceTimming = 0;
					double ractvResult = 0;
					long xgtvResult = 0L;
					long ygtvResult = 0L;

					double ractvTemp = 0;
					long xgtvTemp = 0L;
					long ygtvTemp = 0L;

					// 匹配单个设备所有数据
					if (deviceId.equals(deviceId1)) {
						List<Map> attributeInfoMaps = (List<Map>) deviceMap1.get("attributeInfo");
						for (Map<String, Object> map : attributeInfoMaps) {

							String attributeReportedValue = map.get("attributeReportedValue") + "";
							String attributeName = map.get("attributeName") + "";
							if (attributeReportedValue.equals("timing")) {
								countTiming++;
								rt = attributeReportedValue;
							} else if (attributeReportedValue.equals("trigger")) {
								countTrigger++;
							}

							if ("xgiv".equals(attributeName)) {
								if (!UtilTool.isNumeric(attributeReportedValue)) {
									xgiv = 0;
								} else {
									xgiv = Integer.parseInt(attributeReportedValue);
								}
							}
							if ("ractv".equals(attributeName)) {
								if (!UtilTool.isNumeric(attributeReportedValue)) {
									ractvTemp = 0;
								} else {
									ractvTemp = Double.parseDouble(attributeReportedValue);
								}
							}
							if ("xgtv".equals(attributeName)) {
								if (!UtilTool.isNumeric(attributeReportedValue)) {
									xgiv = 0;
								} else {
									xgtvTemp = Long.parseLong(attributeReportedValue);
								}
							}
							if ("ygtv".equals(attributeName)) {
								if (!UtilTool.isNumeric(attributeReportedValue)) {
									ygtvTemp = 0;
								} else {
									ygtvTemp = Long.parseLong(attributeReportedValue);
								}
							}

						}

						// 当前数据是否可用
						if (xgiv != 2 && rt.equals("timing")) {
							onlyOnceTimming += 1;
							ractvResult = ractvTemp;
							xgtvResult = xgtvTemp;
							ygtvResult = ygtvTemp;

						}

					}
					// 该设备当日可用数据有且仅有一条时,计算r值,及次数
					if (onlyOnceTimming == 1) {
						deviceMap.put("ractv", ractvResult);
						long ractvTime = xgtvResult * 128 + ygtvResult;
						deviceMap.put("ractvTime", ractvTime);
					}

				}

				deviceMap.put("countTiming", countTiming);
				deviceMap.put("countTrigger", countTrigger);
				deviceMap.put("dateNum", dateNum);
				deviceMap.put("countAll", countTiming + countTrigger);
				deviceMap.put("id", UtilTool.getUuId());
				endList.add(deviceMap);
			}

			int i = deviceQueryRepository.insertToTrigger(endList);
			if (i < 1) {
				logger.error("insertCountTimingAndTrigger()-->信息添加失败:" + endList);
			}
		} catch (Exception e) {
			logger.error("insertCountTimingAndTrigger()->exception", e);

		}
	}

	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" })
	 * 
	 * @Override public void insertCountTimingAndTrigger() { try {
	 * 
	 * List<Map<String, Object>> listMap =
	 * deviceQueryRepository.getCountDeviceTimingTrigger(); String timeS =
	 * DateUtils.getDateString("start"); String timeStamp1 =
	 * DateUtils.dateToStamp(timeS); // 1天前的00 String startTime =
	 * Long.parseLong(timeStamp1) - 86400000 + ""; RpcResponse<List<Map>> value
	 * = getCountTimingAndTrigger(); List<Map> deviceHistoryStateMap =
	 * value.getSuccessValue(); SimpleDateFormat tempDate = new
	 * SimpleDateFormat("yyyy-MM-dd"); String dateNum = tempDate.format(new
	 * Date(Long.parseLong(startTime))); List<Map> endList = new
	 * ArrayList<Map>(); for (Map<String, Object> deviceMap : listMap) { int
	 * countTiming = 0; int countTrigger = 0; String deviceId =
	 * deviceMap.get("deviceId") + ""; for (Map<String, Object> deviceMap1 :
	 * deviceHistoryStateMap) { String deviceId1 = deviceMap1.get("deviceId") +
	 * ""; if (deviceId.equals(deviceId1)) { List<Map> attributeInfoMaps =
	 * (List<Map>) deviceMap1.get("attributeInfo"); for (Map<String, Object> map
	 * : attributeInfoMaps) { String rt = map.get("attributeReportedValue") +
	 * ""; if (rt.equals("timing")) { countTiming++; } else if
	 * (rt.equals("trigger")) { countTrigger++; } } }
	 * 
	 * } deviceMap.put("countTiming", countTiming);
	 * deviceMap.put("countTrigger", countTrigger); deviceMap.put("dateNum",
	 * dateNum); deviceMap.put("countAll", countTiming + countTrigger);
	 * deviceMap.put("id", UtilTool.getUuId()); endList.add(deviceMap); } int i
	 * = deviceQueryRepository.insertToTrigger(endList); if (i < 1) {
	 * logger.error("insertCountTimingAndTrigger()-->信息添加失败:" + endList); } }
	 * catch (Exception e) {
	 * logger.error("insertCountTimingAndTrigger()->exception", e);
	 * 
	 * } }
	 */



	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#insertCountTimingAndTrigger(long)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void insertCountTimingAndTrigger(long day) {
		try {

			List<Map<String, Object>> listMap = deviceQueryRepository.getCountDeviceTimingTrigger();
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			// 1天前的00
			String startTime = (Long.parseLong(timeStamp1) - (86400000 * day)) + "";
			RpcResponse<List<Map>> value = getCountTimingAndTrigger(day);
			List<Map> deviceHistoryStateMap = value.getSuccessValue();
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd");
			String dateNum = tempDate.format(new Date(Long.parseLong(startTime)));
			List<Map> endList = new ArrayList<Map>();

			for (Map<String, Object> deviceMap : listMap) {
				int countTiming = 0;
				int countTrigger = 0;

				String deviceId = deviceMap.get("deviceId") + "";
				deviceMap.put("ractv", 0);
				deviceMap.put("ractvTime", 0);
				for (Map<String, Object> deviceMap1 : deviceHistoryStateMap) {
					String deviceId1 = deviceMap1.get("deviceId") + "";
					int xgiv = 100;
					String rt = "";

					int onlyOnceTimming = 0;
					double ractvResult = 0;
					long xgtvResult = 0L;
					long ygtvResult = 0L;

					double ractvTemp = 0;
					long xgtvTemp = 0L;
					long ygtvTemp = 0L;

					// 匹配单个设备所有数据
					if (deviceId.equals(deviceId1)) {
						List<Map> attributeInfoMaps = (List<Map>) deviceMap1.get("attributeInfo");
						for (Map<String, Object> map : attributeInfoMaps) {

							String attributeReportedValue = map.get("attributeReportedValue") + "";
							String attributeName = map.get("attributeName") + "";
							if (attributeReportedValue.equals("timing")) {
								countTiming++;
								rt = attributeReportedValue;
							} else if (attributeReportedValue.equals("trigger")) {
								countTrigger++;
							}

							if ("xgiv".equals(attributeName)) {
								if (!UtilTool.isNumeric(attributeReportedValue)) {
									xgiv = 0;
								} else {
									xgiv = Integer.parseInt(attributeReportedValue);
								}
							}
							if ("ractv".equals(attributeName)) {
								if (!UtilTool.isNumeric(attributeReportedValue)) {
									ractvTemp = 0;
								} else {
									ractvTemp = Double.parseDouble(attributeReportedValue);
								}
							}
							if ("xgtv".equals(attributeName)) {
								if (!UtilTool.isNumeric(attributeReportedValue)) {
									xgiv = 0;
								} else {
									xgtvTemp = Long.parseLong(attributeReportedValue);
								}
							}
							if ("ygtv".equals(attributeName)) {
								if (!UtilTool.isNumeric(attributeReportedValue)) {
									ygtvTemp = 0;
								} else {
									ygtvTemp = Long.parseLong(attributeReportedValue);
								}
							}

						}

						// 当前数据是否可用
						if (xgiv != 2 && rt.equals("timing")) {
							onlyOnceTimming += 1;
							ractvResult = ractvTemp;
							xgtvResult = xgtvTemp;
							ygtvResult = ygtvTemp;

						}

					}
					// 该设备当日可用数据有且仅有一条时,计算r值,及次数
					if (onlyOnceTimming == 1) {
						deviceMap.put("ractv", ractvResult);
						long ractvTime = xgtvResult * 128 + ygtvResult;
						deviceMap.put("ractvTime", ractvTime);
					}

				}

				deviceMap.put("countTiming", countTiming);
				deviceMap.put("countTrigger", countTrigger);
				deviceMap.put("dateNum", dateNum);
				deviceMap.put("countAll", countTiming + countTrigger);
				deviceMap.put("id", UtilTool.getUuId());
				endList.add(deviceMap);
			}

			int i = deviceQueryRepository.insertToTrigger(endList);
			if (i < 1) {
				logger.error("insertCountTimingAndTrigger()-->信息添加失败:" + endList);
			}
		} catch (Exception e) {
			logger.error("insertCountTimingAndTrigger()->exception", e);

		}
	}



	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#getCountTimingAndTrigger()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getCountTimingAndTrigger() {
		try {
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			// 1天前的00-昨天的23：59：59
			String startTime = Long.parseLong(timeStamp1) - 86400000 + "";
			String endTime = Long.parseLong(timeStamp1) - 1000 + "";
			BasicDBObject fieldsObject = new BasicDBObject();
			fieldsObject.put("attributeInfo.attributeReportedValue", 1);
			fieldsObject.put("attributeInfo.attributeName", 1);
			fieldsObject.put("timestamp", 1);
			fieldsObject.put("deviceId", 1);
			DBObject dbObject = new BasicDBObject("$gte", startTime).append("$lte", endTime);
			DBObject ageCompare = new BasicDBObject("timestamp", dbObject);
			Query query = new BasicQuery(ageCompare, fieldsObject);
			List<Map> deviceHistoryStateMap = mongoTemplate.find(query, Map.class, "deviceHistoryState");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功！", deviceHistoryStateMap);
		} catch (Exception e) {
			logger.error("checkFaultOrderDevice()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	public RpcResponse<List<Map>> getCountTimingAndTrigger(long day) {
		try {
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			// 1天前的00-昨天的23：59：59
			String startTime = (Long.parseLong(timeStamp1) - 86400000 * day) + "";
			String endTime = Long.parseLong(timeStamp1) - 1000 - (86400000 * (day - 1)) + "";
			BasicDBObject fieldsObject = new BasicDBObject();
			fieldsObject.put("attributeInfo.attributeReportedValue", 1);
			fieldsObject.put("attributeInfo.attributeName", 1);
			fieldsObject.put("timestamp", 1);
			fieldsObject.put("deviceId", 1);
			DBObject dbObject = new BasicDBObject("$gte", startTime).append("$lte", endTime);
			DBObject ageCompare = new BasicDBObject("timestamp", dbObject);
			Query query = new BasicQuery(ageCompare, fieldsObject);
			List<Map> deviceHistoryStateMap = mongoTemplate.find(query, Map.class, "deviceHistoryState");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功！", deviceHistoryStateMap);
		} catch (Exception e) {
			logger.error("checkFaultOrderDevice()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#automaticMaintenanceByTrigger()
	 */
	@Override
	public void automaticMaintenanceByTrigger() {
		logger.info(
				"automaticMaintenanceByTrigger()-->进入方法trigger连续天数：" + continuousDays + "trigger次数：" + triggerCount);
		try {
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			String startTime = (Long.parseLong(timeStamp1) - 86400000 * Integer.parseInt(continuousDays)) + "";
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd");
			String dateNum = tempDate.format(new Date(Long.parseLong(startTime)));
			Map<String, Object> conditionsMap = new HashMap<String, Object>();
			conditionsMap.put("dateNum", dateNum);
			conditionsMap.put("triggerCount", Integer.parseInt(triggerCount));
			conditionsMap.put("continuousDays", Integer.parseInt(continuousDays));
			conditionsMap.put("timingCount", Integer.parseInt(timingCount));
			conditionsMap.put("continuousTimingDays", Integer.parseInt(continuousTimingDays));
			conditionsMap.put("defenseState", 1);
			// trigger
			List<Map<String, Object>> listMap = deviceQueryRepository.getDeviceInfo(conditionsMap);
			// timing
			List<Map<String, Object>> listMap1 = deviceQueryRepository.getTimingDeviceInfo(conditionsMap);
			String denfenseState = "2";
			String organizationId = "";
			String token = "";
			String accessSecret = "aecde01f-9ae2-4876-84b7-c08ea25a4788";
			ArrayList<String> arrayList = new ArrayList<String>();
			if (listMap != null && listMap.size() > 0) {
				for (Map<String, Object> map : listMap) {
					String facilityId = map.get("facilityId") + "";
					arrayList.add(facilityId);
				}
			}
			ArrayList<String> arrayList1 = new ArrayList<String>();
			if (listMap1 != null && listMap1.size() > 0) {
				for (Map<String, Object> map : listMap1) {
					String facilityId = map.get("facilityId") + "";
					arrayList1.add(facilityId);
				}
			}
			arrayList.addAll(arrayList1);
			if (arrayList != null && arrayList.size() > 0) {
				RpcResponse<Integer> updateRes = facilitiesCrudService.updateFacilitiesDenfenseState(organizationId,
						arrayList, token, denfenseState, accessSecret);
				if (updateRes.isSuccess()) {
					logger.info("automaticMaintenanceByTrigger()->进入维护态成功！%s" + arrayList);
				}
			} else {
				logger.info("automaticMaintenanceByTrigger()->没有设备进入维护态！%s" + arrayList);
			}

		} catch (Exception e) {
			logger.error("automaticMaintenanceByTrigger()->exception", e);
		}
	}
	
	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#automaticLiftingByTiming()
	 */
	@Override
	public void automaticLiftingByTiming() {
		logger.info("automaticLiftingByTiming()-->进入方法");
		try {
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			String startTime = (Long.parseLong(timeStamp1) - 86400000 * 3) + "";
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd");
			String dateNum = tempDate.format(new Date(Long.parseLong(startTime)));
			Map<String, Object> conditionsMap = new HashMap<String, Object>();
			conditionsMap.put("dateNum", dateNum);
			conditionsMap.put("triggerCount", Integer.parseInt(triggerCount));
			conditionsMap.put("continuousDays", Integer.parseInt(continuousDays));
			conditionsMap.put("timingCount", 1);
			conditionsMap.put("continuousTimingDays", 2);
			conditionsMap.put("defenseState", 2);
			// 3天，每天3次trigger的已经维护态的设施
			List<Map<String, Object>> listMap = deviceQueryRepository.getDeviceInfo(conditionsMap);
			// 3天，每天一次timing的以及维护的设施
			List<Map<String, Object>> listMap1 = deviceQueryRepository.getAutomaticTimingDeviceInfo(conditionsMap);
			String denfenseState = "1";
			String organizationId = "";
			String token = "";
			String accessSecret = "aecde01f-9ae2-4876-84b7-c08ea25a4788";
			ArrayList<String> arrayList = new ArrayList<String>();
			if (listMap != null && listMap.size() > 0) {
				for (Map<String, Object> map : listMap) {
					String facilityId = map.get("facilityId") + "";
					arrayList.add(facilityId);
				}
			}
			logger.info("automaticLiftingByTiming()->连续trigger的维护设备！%s" + arrayList);
			ArrayList<String> arrayList1 = new ArrayList<String>();
			if (listMap1 != null && listMap1.size() > 0) {
				for (Map<String, Object> map : listMap1) {
					String facilityId = map.get("facilityId") + "";
					arrayList1.add(facilityId);
				}
			}
			logger.info("automaticLiftingByTiming()->timing正常的维护设备！%s" + arrayList1);
			//排除1中的trigger
			ArrayList<String> newArrayList = new ArrayList<String>();
			for(String out:arrayList1) {
				if(!arrayList.contains(out)) {
					newArrayList.add(out);
				}
			}
			
			if (arrayList != null && arrayList.size() > 0) {
				RpcResponse<Integer> updateRes = facilitiesCrudService.updateFacilitiesDenfenseState(organizationId,
						newArrayList, token, denfenseState, accessSecret);
				if (updateRes.isSuccess()) {
					logger.info("automaticLiftingByTiming()->解除维护态成功！%s" + newArrayList);
				}
			} else {
				logger.info("automaticLiftingByTiming()->没有设备解除维护态！%s" + newArrayList);
			}

		} catch (Exception e) {
			logger.error("automaticLiftingByTiming()->exception", e);
		}
	}
		
		
	/*
	 * @Override public void getPersonIdAndOrganizeIdByAc1() {
	 */
	/*
	 * List<String> allAccessSecret =
	 * deviceQueryRepository.getAllAccessSecret(); Map<String, Object> map =
	 * Maps.newHashMap();
	 */
	/*
	 * RpcResponse<List<Map<String, Object>>> personInfo =
	 * orderProcessQueryService .findPersonByAccessSecret(allAccessSecret,
	 * "超时故障工单流程"); if (!personInfo.isSuccess() ||
	 * personInfo.getSuccessValue().isEmpty()) { logger.error("无超时故障工单流程"); }
	 * List<Map<String, Object>> successValue = personInfo.getSuccessValue();
	 * for (Map<String, Object> person : successValue) { if
	 * (person.containsValue("发起人")) { String string =
	 * person.get("personId").toString(); String[] personIdArray =
	 * string.split(","); String personId = personIdArray[0];
	 * map.put("personId", personId); } }
	 */
	// }



	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#queryAndAlarm()
	 */
	@Override
	public void queryAndAlarm() {
		try {
			// TODO Auto-generated method stub
			List<String> normalDeviceIds = deviceQueryRepository.getNormalDeviceIds();
			// 1天前的00
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			String startTime = Long.parseLong(timeStamp1) - 86400000 + "";
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd");
			String dateNum = tempDate.format(new Date(Long.parseLong(startTime)));
			List<JSONObject> deviceHistoryStateData = getDeviceHistoryStateData(1);

			List<JSONObject> resultList = Lists.newArrayList();
			JSONObject tempJson = new JSONObject();
			tempJson.put(DeviceContants.FA_XAVTV, "");
			tempJson.put(DeviceContants.FA_YAVTV, "");
			tempJson.put(DeviceContants.FA_ZAVTV, "");
			tempJson.put(DeviceContants.FA_XGCM, "");
			tempJson.put(DeviceContants.FA_YGCM, "");
			tempJson.put(DeviceContants.FA_XGTV, "");
			tempJson.put(DeviceContants.FA_YGTV, "");

			for (String deviceId : normalDeviceIds) {
				JSONObject resultJson = new JSONObject();
				int onlyOnceTimming = 0;
				// 循环匹配单个设备当天所有上报数据
				for (JSONObject jsonObject : deviceHistoryStateData) {
					String deviceIdJson = jsonObject.getString(DeviceContants.DEVICEID);

					int xgiv = 100;
					String rt = "";
					String xavtv = "";
					/*
					 * String yavtv = ""; String zavtv = ""; String xgcm = "";
					 * String ygcm = ""; String xgtv = ""; String ygtv = "";
					 */

					if (deviceId.equals(deviceIdJson)) {

						JSONArray deviceStateArray = jsonObject.getJSONArray("attributeInfo");
						List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);

						// 循环一条数据的属性点:属性值
						for (AttributeInfo attributeInfo : attributeInfoList) {

							if (StringUtils.isBlank(attributeInfo.getAttributeName())) {
								continue;
							}
							String attributeReportedValue = attributeInfo.getAttributeReportedValue();
							// 每天上报的timing和trigger次数
							if ("rt".equals(attributeInfo.getAttributeName())
									&& "timing".equals(attributeReportedValue)) {
								rt = "timing";
							}

							if (DeviceContants.FA_XAVTV.equals(attributeInfo.getAttributeName())) {
								xavtv = attributeInfo.getAttributeReportedValue();
								tempJson.put(DeviceContants.FA_XAVTV, xavtv);
							}

							if (attributeNames.contains(attributeInfo.getAttributeName())) {
								tempJson.put(attributeInfo.getAttributeName(), attributeReportedValue);
							}

							/*
							 * if (DeviceContants.FA_YAVTV.equals(attributeInfo.
							 * getAttributeName())) {
							 * tempJson.put(DeviceContants.FA_YAVTV,
							 * attributeReportedValue); } if
							 * (DeviceContants.FA_ZAVTV.equals(attributeInfo.
							 * getAttributeName())) {
							 * tempJson.put(DeviceContants.FA_ZAVTV,
							 * attributeReportedValue); } if
							 * (DeviceContants.FA_XGCM.equals(attributeInfo.
							 * getAttributeName())) {
							 * tempJson.put(DeviceContants.FA_XGCM,
							 * attributeReportedValue); } if
							 * (DeviceContants.FA_YGCM.equals(attributeInfo.
							 * getAttributeName())) {
							 * tempJson.put(DeviceContants.FA_YGCM,
							 * attributeReportedValue); } if
							 * (DeviceContants.FA_XGTV.equals(attributeInfo.
							 * getAttributeName())) {
							 * tempJson.put(DeviceContants.FA_XGTV,
							 * attributeReportedValue); } if
							 * (DeviceContants.FA_YGTV.equals(attributeInfo.
							 * getAttributeName())) {
							 * tempJson.put(DeviceContants.FA_YGTV,
							 * attributeReportedValue); }
							 */

						}

						String timestamp = jsonObject.getString("timestamp");
						if (StringUtils.isBlank(timestamp) || !StringUtils.isNumeric(timestamp)) {
							logger.error("deviceStateInfo4Excel()-->时间戳值为空/非数值:" + timestamp);
						}
						long parseLong = Long.parseLong(timestamp);
						String reportTime = UtilTool.timeStampToDate(parseLong);
						tempJson.put("reportTime", reportTime);

						// 当前数据是否可用
						if (xgiv != 2 && rt.equals("timing") && !"65535".equals(xavtv)) {
							onlyOnceTimming += 1;
							// resultJson = tempJson;
							resultJson.putAll(tempJson);
							tempJson.put(DeviceContants.FA_XAVTV, "");
							tempJson.put(DeviceContants.FA_YAVTV, "");
							tempJson.put(DeviceContants.FA_ZAVTV, "");
							tempJson.put(DeviceContants.FA_XGCM, "");
							tempJson.put(DeviceContants.FA_YGCM, "");
							tempJson.put(DeviceContants.FA_XGTV, "");
							tempJson.put(DeviceContants.FA_YGTV, "");
							resultJson.put(DeviceContants.DEVICEID, deviceId);
						}

					}

				}
				// 该设备当日可用数据有且仅有一条时,计算r值,及次数
				if (onlyOnceTimming == 1) {
					resultJson.put("dateNum", dateNum);
					resultJson.put("manageState", "enable");
					resultJson.put("id", UtilTool.getUuId());
					resultList.add(resultJson);
				}

			}

			if (!resultList.isEmpty()) {
				int num = deviceQueryRepository.addDeviceRportedEffective(resultList);
				logger.info(String.format("成功添加数据:%s条", num));
			} else {
				logger.info("无添加数据");
			}

			List<JSONObject> sumXYZ = deviceQueryRepository.queryXYZAvg();

			List<JSONObject> xyzAvgs = Lists.newArrayList();

			for (JSONObject sumXYZValue : sumXYZ) {
				String xavtv = sumXYZValue.getString(DeviceContants.FA_XAVTV);
				String yavtv = sumXYZValue.getString(DeviceContants.FA_YAVTV);
				String zavtv = sumXYZValue.getString(DeviceContants.FA_ZAVTV);
				String xgcm = sumXYZValue.getString(DeviceContants.FA_XGCM);
				String ygcm = sumXYZValue.getString(DeviceContants.FA_YGCM);
				String num = sumXYZValue.getString("num");

				Integer xavtvInt = getInteger(xavtv);
				Integer yavtvInt = getInteger(yavtv);
				Integer zavtvInt = getInteger(zavtv);
				Integer xgcmInt = getInteger(xgcm);
				Integer ygcmInt = getInteger(ygcm);
				Integer numInt = getInteger(num);

				if (null == xavtvInt || null == yavtvInt || null == zavtvInt || null == numInt || null == xgcmInt
						|| null == ygcmInt) {
					continue;
				}

				JSONObject xyzAvgJson = new JSONObject();
				int xavtvAvg = xavtvInt / numInt;
				int yavtvAvg = yavtvInt / numInt;
				int zavtvAvg = zavtvInt / numInt;
				int xgcmAvg = xgcmInt / numInt;
				int ygcmAvg = ygcmInt / numInt;

				// xyz的平均值的平方开根号得到总的r值
				double ractvAvg = Math.sqrt(xavtvAvg * xavtvAvg + yavtvAvg * yavtvAvg + zavtvAvg * zavtvAvg);

				xyzAvgJson.put(DeviceContants.FA_XAVTV_AVG, xavtvAvg);
				xyzAvgJson.put(DeviceContants.FA_YAVTV_AVG, yavtvAvg);
				xyzAvgJson.put(DeviceContants.FA_ZAVTV_AVG, zavtvAvg);
				xyzAvgJson.put("id", UtilTool.getUuId());
				xyzAvgJson.put(DeviceContants.DEVICEID, sumXYZValue.getString(DeviceContants.DEVICEID));
				xyzAvgJson.put(DeviceContants.FA_XGCM_AVG, xgcmAvg);
				xyzAvgJson.put(DeviceContants.FA_YGCM_AVG, ygcmAvg);
				xyzAvgJson.put(DeviceContants.FA_RACTV_AVG, ractvAvg);
				xyzAvgJson.put("dateNum", dateNum);
				xyzAvgJson.put("manageState", "enable");
				xyzAvgs.add(xyzAvgJson);

			}

			if (!xyzAvgs.isEmpty()) {
				int num = deviceQueryRepository.addRportedAvg(xyzAvgs);
				logger.info(String.format("成功添加平均值数据:%s条", num));
			} else {
				logger.info("无平均值数据添加");
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("queryAndAlarm()->exception", e);
		}

	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private Integer getInteger(String str) {
		int res;
		if (UtilTool.isNumeric(str)) {

			if (str.contains(".")) {
				String substring = str.substring(0, str.lastIndexOf("."));
				res = Integer.parseInt(substring);

			} else {
				res = Integer.parseInt(str);
			}
			return res;
		} else {
			return null;
		}

	}



	public List<JSONObject> getDeviceHistoryStateData(long day) {
		try {
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			// 1天前的00-昨天的23：59：59
			String startTime = (Long.parseLong(timeStamp1) - 86400000 * day) + "";
			String endTime = Long.parseLong(timeStamp1) - 1000 - (86400000 * (day - 1)) + "";
			BasicDBObject fieldsObject = new BasicDBObject();
			fieldsObject.put("attributeInfo.attributeReportedValue", 1);
			fieldsObject.put("attributeInfo.attributeName", 1);
			fieldsObject.put("timestamp", 1);
			fieldsObject.put("deviceId", 1);
			DBObject dbObject = new BasicDBObject("$gte", startTime).append("$lte", endTime);
			DBObject ageCompare = new BasicDBObject("timestamp", dbObject);
			Query query = new BasicQuery(ageCompare, fieldsObject);
			List<JSONObject> deviceHistoryStateList = mongoTemplate.find(query, JSONObject.class, "deviceHistoryState");
			return deviceHistoryStateList;
		} catch (Exception e) {
			logger.error("checkFaultOrderDevice()->exception", e);
			return null;
		}
	}



	/**
	 * @see com.run.locman.api.timer.query.service.DeviceQuery#querySecurityFacilitiesOrders()
	 */
	@Override
	public void querySecurityFacilitiesOrders() {
		try {
			
			RpcResponse<Integer> querySecurityFacilitiesOrders = focusSecurityCrudService.querySecurityFacilitiesOrders();
			if(querySecurityFacilitiesOrders.isSuccess()) {
				logger.info("重保时间结束，更改工单状态成功！");
			}
			logger.error("重保时间结束，更改工单状态失败！");
			
		}catch(Exception e) {
			logger.error("querySecurityFacilitiesOrders()->exception", e);
		}
		
		
	}



	

}
