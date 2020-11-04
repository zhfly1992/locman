/*
 * File name: RemoteControlRecordCrudRestService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月8日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.google.common.collect.Lists;
import com.run.common.util.ParamChecker;
import com.run.common.util.StringUtil;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.AlarmInfoCrudService;
import com.run.locman.api.crud.service.DeviceSchedulerService;
import com.run.locman.api.crud.service.RemoteControlRecordCrudService;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.RemoteControlRecord;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.PropertiesQueryService;
import com.run.locman.api.query.service.RemoteControlRecordQueryService;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.api.timer.query.service.WingsIotConstansQuery;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.RemoteControl;
import com.run.locman.api.util.UtilTool;

import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.AlarmOrderConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.FactoryConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.RemoteControlRecordConstants;



/**
 * @Description: 远控
 * @author: qulong
 * @version: 1.0, 2017年12月8日
 */

@Service
@EnableApolloConfig
public class RemoteControlRecordCrudRestService {

	@Autowired
	private RemoteControlRecordCrudService	remoteControlRecordCrudService;

	@Autowired
	private RemoteControlRecordQueryService	remoteControlRecordQueryService;

	@Autowired
	private SimpleOrderQueryService			simpleOrderQueryService;

	@Autowired
	private PropertiesQueryService			propertiesQueryService;

	@Autowired
	AlarmInfoCrudService					alarmInfoCrudService;

	@Autowired
	DeviceSchedulerService					deviceSchedulerService;

	@Autowired
	private AlarmOrderQueryService			alarmOrderQueryService;
	@Autowired
	private DeviceQueryService				deviceQueryService;
	@Autowired
	private HttpServletRequest				httpRequest;
	@Autowired
	private WingsIotConstansQuery			WingsIotConstansQuery;

	@Value("${openLock.lockValue.on}")
	private String							lockOn;

	@Value("${openLock.lockValue.off}")
	private String							lockOff;
	@Value("${api.host}")
	private String							ip;

	private static Pattern					NUMBER_PATTERN	= Pattern.compile("[0-9]*");

	private static String					POWER			= "power";

	private Logger							logger			= Logger.getLogger(CommonConstants.LOGKEY);



	public Result<String> control(String params) {
		logger.info(String.format("[control()->request params:%s]", params));
		RpcResponse<JSONObject> paramCheck = paramCheck(params);
		if (!paramCheck.isSuccess()) {
			return ResultBuilder.failResult(paramCheck.getMessage());
		}

		Result<RemoteControlRecord> paramResult = this.analysisParams(params);
		RemoteControlRecord remoteControlRecord = paramResult.getValue();
		try {
			String deviceId = remoteControlRecord.getDeviceId();
			RpcResponse<Map<String, String>> deviceInfo = deviceQueryService.getDeviceInfoForRemoteControl(deviceId);
			if (!deviceInfo.isSuccess()) {
				logger.error(String.format("[control()->fail:%s]", deviceInfo.getMessage()));
				return ResultBuilder.failResult(deviceInfo.getMessage());
			}
			// 新方式下发命令
			JSONObject result = controlDevice(deviceInfo.getSuccessValue(), remoteControlRecord, ip);
			if (null == result) {
				return ResultBuilder.failResult("下发命令失败，http请求返回为空");
			}
			logger.info(String.format("[control()->命令下发返回信息:%s]", result.toJSONString()));
			// 控制命令下发成功，保存记录
			// if (updateThingShadowResult.getStatus() == 200)
			if (CommonConstants.NUMBER_TWO_HUNDRED.equals(result.getString(RemoteControlRecordConstants.STATUS))) {
				logger.info(String.format("[control()->命令发送成功，response:%s]", result.get("response")));
				JSONObject json = new JSONObject();
				json.put("deviceId", deviceId);
				json.put("key", remoteControlRecord.getControlItem());
				json.put("keyValue", remoteControlRecord.getControlValue());
				json.put("userId", paramCheck.getSuccessValue().getString("userId"));
				json.put("organizationId", paramCheck.getSuccessValue().getString("organizationId"));
				json.put("accessSecret", paramCheck.getSuccessValue().getString("accessSecret"));

				// 保存控制记录
				remoteControlRecord.setControlTime(DateUtils.formatDate(new Date()));
				// 控制方式：分权分域
				remoteControlRecord.setControlType(2);
				remoteControlRecord.setControlState("valid");
				RpcResponse<String> saveResult = remoteControlRecordCrudService
						.saveRemoteControlRecord(remoteControlRecord);
				logger.info("[control()->success:控制记录保存成功]");

				// 启动设备超时未关定时任务
				RpcResponse<String> deviceSchedulerStart = deviceSchedulerService.deviceSchedulerStart(json);
				if (!deviceSchedulerStart.isSuccess()) {
					logger.error("deviceSchedulerStart()--fail:" + deviceSchedulerStart.getMessage());
					return ResultBuilder.failResult(deviceSchedulerStart.getMessage());
				}

				return ResultBuilder.successResult(remoteControlRecord.getId(), saveResult.getMessage());
			}
			// if (updateThingShadowResult.getStatus() == 409)
			if (CommonConstants.NUMBER_FOUR_HUNDRED_AND_NINE
					.equals(result.getString(RemoteControlRecordConstants.STATUS))) {
				logger.error("[control()->fail:设备未接入]");
				return ResultBuilder.failResult("设备未接入");
			}

			// logger.error("[control()->fail:控制记录保存失败]");
			// return ResultBuilder.failResult("保存失败");
			logger.error(String.format("[control()->fail:发送命令失败，status:%s,message;%s]",
					result.get(RemoteControlRecordConstants.STATUS), result.getString("message")));
			return ResultBuilder.failResult("发送命令失败" + result.getString("message"));
		} catch (Exception e) {
			logger.error("control()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	private Result<RemoteControlRecord> analysisParams(String params) {
		if (StringUtil.isEmpty(params)) {
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(params)) {
			return ResultBuilder.invalidResult();
		}
		JSONObject object = JSONObject.parseObject(params);
		RemoteControlRecord remoteControlRecord = new RemoteControlRecord();

		if (StringUtil.isEmpty(object.getString(CommonConstants.DEVICEID))) {
			logger.error("[openLock()->error:没有设备deviceId]");
			return ResultBuilder.noBusinessResult();
		}
		if (StringUtil.isEmpty(object.getString(CommonConstants.USERID))) {
			logger.error("[openLock()->error:没有用户userId]");
			return ResultBuilder.noBusinessResult();
		}
		if (StringUtil.isEmpty(object.getString(CommonConstants.OPERATEUSERNAME))) {
			logger.error("[openLock()->error:没有开启人名称operateUserName]");
			return ResultBuilder.noBusinessResult();
		}
		if (StringUtil.isEmpty(object.getString(CommonConstants.OPERATEUSERPHONE))) {
			logger.error("[openLock()->error:没有operateUserPhone]");
			return ResultBuilder.noBusinessResult();
		}
		if (StringUtil.isEmpty(object.getString(CommonConstants.CONTROLITEM))) {
			logger.error("[openLock()->error:没有controlItem]");
			return ResultBuilder.noBusinessResult();
		}
		if (StringUtil.isEmpty(object.getString(CommonConstants.CONTROLVALUE))) {
			logger.error("[openLock()->error:没有controlValue]");
			return ResultBuilder.noBusinessResult();
		}

		remoteControlRecord.setId(UtilTool.getUuId());
		remoteControlRecord.setDeviceId(object.getString("deviceId"));
		remoteControlRecord.setControlUserId(object.getString("userId"));
		remoteControlRecord.setOperateUserName(object.getString("operateUserName"));
		remoteControlRecord.setOperateUserPhone(object.getString("operateUserPhone"));
		remoteControlRecord.setControlItem(object.getString("controlItem"));
		remoteControlRecord.setControlValue(object.getString("controlValue"));

		if (object.containsKey(CommonConstants.REASON)) {
			remoteControlRecord.setReason(object.getString(CommonConstants.REASON));
		}

		return ResultBuilder.successResult(remoteControlRecord, "");
	}



	public Result<String> contorlByOrder(String params, String orderId) {
		logger.info(String.format("[contorlByOrder()->request params--params:%s,order:%s]", params, orderId));
		if (ParamChecker.isBlank(orderId)) {
			logger.error("[contorlByOrder()->error:orderId不能为空]");
			return ResultBuilder.invalidResult();
		}
		try {
			Result<RemoteControlRecord> analysisParams = this.analysisParams(params);
			RemoteControlRecord remoteControlRecord = analysisParams.getValue();

			Result<Map<String, String>> results = getResult(orderId, remoteControlRecord);
			if (!CommonConstants.NUMBER_FOUR_ZERO.equals(results.getResultStatus().getResultCode())) {
				return ResultBuilder.failResult(results.getResultStatus().getResultMessage());
			}

			// 新的方式下发
			JSONObject result = controlDevice(results.getValue(), remoteControlRecord, ip);
			if (null == result) {
				return ResultBuilder.failResult("下发命令失败，http请求返回为空");
			}
			logger.info(String.format("[contorlByOrder()->命令下发返回信息，%s]", result.toJSONString()));
			// 控制命令下发成功，保存记录
			if (CommonConstants.NUMBER_TWO_HUNDRED.equals(result.getString(RemoteControlRecordConstants.STATUS)))
			// if (updateThingShadowResult.getStatus() == 200)
			{
				logger.info(String.format("[contorlByOrder()->命令发送成功，response:%s]", result.get("response")));
				// 保存控制记录
				remoteControlRecord.setControlTime(DateUtils.formatDate(new Date()));
				// 控制方式：工单
				remoteControlRecord.setControlType(1);
				remoteControlRecord.setControlState("valid");
				RpcResponse<String> saveResult = remoteControlRecordCrudService
						.saveRemoteControlRecord(remoteControlRecord);
				logger.info(String.format("[contorlByOrder()->success:%s]", saveResult.getMessage()));
				return ResultBuilder.successResult(remoteControlRecord.getId(), saveResult.getMessage());
			}
			if (CommonConstants.NUMBER_FOUR_HUNDRED_AND_NINE
					.equals(result.getString(RemoteControlRecordConstants.STATUS))) {
				logger.error("[contorlByOrder()->fail:设备没有上报记录，无法下发控制]");
				return ResultBuilder.failResult("设备没有上报记录，无法下发控制");
			}
			logger.error(String.format("[contorlByOrder()->fail:发送命令失败，status:%s,message;%s]",
					result.get(RemoteControlRecordConstants.STATUS), result.getString("message")));
			return ResultBuilder.failResult("发送命令失败" + result.getString("message"));

		} catch (Exception e) {
			logger.error("contorlByOrder()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param orderId
	 * @param remoteControlRecord
	 * @return
	 * @throws Exception
	 */

	private Result<Map<String, String>> getResult(String orderId, RemoteControlRecord remoteControlRecord)
			throws Exception {
		RpcResponse<Map<String, Object>> findById = simpleOrderQueryService.findById(orderId);
		if (!findById.isSuccess()) {
			logger.error(String.format("[contorlByOrder()->fail:%s]", findById.getMessage()));
			return ResultBuilder.failResult(findById.getMessage());
		}
		if (findById.getSuccessValue() == null) {
			logger.error("[contorlByOrder()->fail:该工单不存在]");
			return ResultBuilder.failResult("该工单不存在");
		}
		Map<String, Object> simpleOrderProcess = findById.getSuccessValue();
		Date now = new Date();
		if (DateUtils.toDate(simpleOrderProcess.get(CommonConstants.PROCESSSTARTTIME).toString()).compareTo(now) == 1
				|| DateUtils.toDate(simpleOrderProcess.get("processEndTime").toString()).compareTo(now) == -1) {
			logger.error("[contorlByOrder()->fail:当前时间不在工单时间范围内]");
			return ResultBuilder.failResult("当前时间不在工单时间范围内");
		}

		if (!simpleOrderProcess.get(CommonConstants.USERID).toString().equals(remoteControlRecord.getControlUserId())) {
			logger.error("[contorlByOrder()->fail:没有权限]");
			return ResultBuilder.failResult("没有权限");
		}
		String deviceId = remoteControlRecord.getDeviceId();
		// 获取设备信息
		RpcResponse<Map<String, String>> deviceInfo = deviceQueryService.getDeviceInfoForRemoteControl(deviceId);
		if (!deviceInfo.isSuccess()) {
			logger.error(String.format("[contorlByOrder()->fail:%s]", deviceInfo.getMessage()));
			return ResultBuilder.failResult(deviceInfo.getMessage());
		}
		return ResultBuilder.successResult(deviceInfo.getSuccessValue(), "执行成功!");
	}



	/**
	 * 
	 * @Description:统一开关锁接口
	 * @param params：
	 *            { "deviceId":"TTam8SOfy8bFqamnPZqA",
	 *            "propertiesId":"f2af7e76c52511e78d43d4ae52b55aae",
	 *            "button":"off", "userId":"123", "operateUserName":"111",
	 *            "operateUserPhone":"000", "reason":"000" }
	 * @return
	 */
	public Result<String> openLock(String params) {
		logger.info(String.format("[openLock()->request params:%s]", params));
		if (ParamChecker.isBlank(params)) {
			logger.error(String.format("[openLock()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.emptyResult();
		}
		try {
			RpcResponse<JSONObject> paramCheck = paramCheck(params);
			if (!paramCheck.isSuccess()) {
				return ResultBuilder.failResult(paramCheck.getMessage());
			}

			JSONObject paramJson = JSON.parseObject(params);
			String deviceId = paramJson.getString("deviceId");
			String propertiesId = paramJson.getString("propertiesId");
			String button = paramJson.getString("button");
			String userId = paramJson.getString("userId");

			RpcResponse<RemoteControlRecord> control = remoteControlRecordQueryService
					.getRemoteControlByDeviceId(deviceId);
			if (!control.isSuccess()) {
				ResultBuilder.failResult(control.getMessage());
			}

			String cotrolItem = control.getSuccessValue().getControlItem();
			String cotrolValue = control.getSuccessValue().getControlValue();
			if (CommonConstants.ON.equals(cotrolValue)) {
				cotrolValue = "open";
			}

			String operateUserName = paramJson.getString("operateUserName");
			String operateUserPhone = paramJson.getString("operateUserPhone");
			String reason = paramJson.getString("reason");
			RemoteControlRecord remoteControlRecord = new RemoteControlRecord(UtilTool.getUuId(), deviceId, cotrolItem,
					cotrolValue, DateUtils.formatDate(new Date()), userId, operateUserName, operateUserPhone, reason, 3,
					"valid", "");
			if (StringUtil.isEmpty(deviceId) || StringUtil.isEmpty(propertiesId) || StringUtil.isEmpty(button)
					|| StringUtil.isEmpty(userId)) {
				logger.error("[openLock()->error:业务参数为空]");
				return ResultBuilder.noBusinessResult();
			}

			switch (button) {
			case "on":
				if (StringUtil.isEmpty(lockOn)) {
					logger.error("[openLock()->fail:开启失败，未获取到配置]");
					return ResultBuilder.failResult("开启失败，未获取到配置");
				}
				logger.info("[openLock()->success:开启成功]");
				return this.controlLock(lockOn, propertiesId, remoteControlRecord, paramJson);

			case "off":
				if (StringUtil.isEmpty(lockOff)) {
					logger.error("[openLock()->fail:关闭失败，未获取到配置]");
					return ResultBuilder.failResult("关闭失败，未获取到配置");
				}
				logger.info("[openLock()->success:关闭成功]");
				return this.controlLock(lockOff, propertiesId, remoteControlRecord, paramJson);

			default:
				logger.error("[openLock()->fail:传入参数有误]");
				return ResultBuilder.failResult("传入参数有误");
			}
		} catch (Exception e) {
			logger.error("openLock()->()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	private Result<String> controlLock(String onOrOff, String propertiesId, RemoteControlRecord remoteControlRecord,
			JSONObject controlParam) {
		logger.info(String.format("[controlLock()->request params--popertiesId:%s,state:%s]", propertiesId, onOrOff));
		try {
			// 获取手动配置的on/off对应的值
			String[] configValueArr = onOrOff.split(",");
			RpcResponse<DeviceProperties> devicePropertiesResult = propertiesQueryService.findById(propertiesId);
			if (!devicePropertiesResult.isSuccess() || devicePropertiesResult.getSuccessValue() == null) {
				logger.error(String.format("[controlLock()->fail:%s]", devicePropertiesResult.getMessage()));
				return ResultBuilder.failResult(devicePropertiesResult.getMessage());
			}
			// 获取数据库存储的开锁字段的真实值
			String propertiesValueStr = devicePropertiesResult.getSuccessValue().getDataValue();
			// 获取控制项
			String controlItem = devicePropertiesResult.getSuccessValue().getDevicePropertiesSign();
			if (StringUtil.isEmpty(propertiesValueStr)) {
				logger.error("[controlLock()->fail:该属性点对应的值为空，请检查设备属性配置]");
				return ResultBuilder.failResult("该属性点对应的值为空，请检查设备属性配置");
			}

			String[] propertiesValueArr = propertiesValueStr.split(",");

			for (String configValue : configValueArr) {
				for (String propertiesValue : propertiesValueArr) {
					// 命令翻译
					if (configValue.equals(propertiesValue)) {
						// 获取设备信息
						RpcResponse<Map<String, String>> deviceInfo = deviceQueryService
								.getDeviceInfoForRemoteControl(remoteControlRecord.getDeviceId());
						if (!deviceInfo.isSuccess()) {
							logger.error(String.format("[controlLock()->fail:%s]", deviceInfo.getMessage()));
							return ResultBuilder.failResult(deviceInfo.getMessage());
						}

						JSONObject result = controlDevice(deviceInfo.getSuccessValue(), remoteControlRecord, ip);
						if (null == result) {
							return ResultBuilder.failResult("下发命令失败，http请求返回为空");
						}
						logger.info(String.format("[controlLockr()->命令发送返回信息，%s]", result.toJSONString()));
						// 控制命令下发成功，保存记录
						// if (updateThingShadowResult.getStatus() == 200)
						if ("200".equals(result.getString(RemoteControlRecordConstants.STATUS))) {
							logger.info(String.format("[controlLockr()->命令发送成功，response:%s]", result.get("response")));
							return controlHandle(remoteControlRecord, controlParam, controlItem, propertiesValue);
						}

						// if (updateThingShadowResult.getStatus() == 409)
						if ("409".equals(result.getString(RemoteControlRecordConstants.STATUS))) {
							logger.error("[controlLock()->fail:设备没有上报记录，无法下发控制]");
							return ResultBuilder.failResult("设备没有上报记录，无法下发控制");
						}
					}
				}
			}
			logger.error("[controlLock()->fail:该属性点的关系配置有误]");
			return ResultBuilder.failResult("该属性点的关系配置有误");
		} catch (Exception e) {
			logger.error("controlLock()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param remoteControlRecord
	 * @param controlParam
	 * @param controlItem
	 * @param propertiesValue
	 * @return
	 */

	private Result<String> controlHandle(RemoteControlRecord remoteControlRecord, JSONObject controlParam,
			String controlItem, String propertiesValue) {
		JSONObject json = getJson(remoteControlRecord, controlParam, controlItem, propertiesValue);
		String openType = controlParam.getString("openType");

		// 保存控制记录
		remoteControlRecord.setControlItem(controlItem);
		remoteControlRecord.setControlValue(propertiesValue);
		RpcResponse<String> saveResult = remoteControlRecordCrudService.saveRemoteControlRecord(remoteControlRecord);

		// 手机流量分权分域下发，启动设备超时未关定时任务
		if (null != openType && POWER.equals(openType)) {
			RpcResponse<String> deviceSchedulerStart = deviceSchedulerService.deviceSchedulerStart(json);
			if (!deviceSchedulerStart.isSuccess()) {
				logger.error("deviceSchedulerStart()--fail:" + deviceSchedulerStart.getMessage());
				return ResultBuilder.failResult(deviceSchedulerStart.getMessage());
			}
		}
		if (saveResult.isSuccess()) {
			logger.info(String.format("[controlLock()->success:%s]", saveResult.getMessage()));
			return ResultBuilder.successResult(remoteControlRecord.getId(), saveResult.getMessage());
		}
		logger.error("[controlLock()->fail:控制命令下发成功，命令记录保存失败]");
		return ResultBuilder.failResult("控制命令下发成功，命令记录保存失败");
	}



	/**
	 * @Description:
	 * @param remoteControlRecord
	 * @param controlParam
	 * @param controlItem
	 * @param propertiesValue
	 * @return
	 */

	private JSONObject getJson(RemoteControlRecord remoteControlRecord, JSONObject controlParam, String controlItem,
			String propertiesValue) {
		JSONObject json = new JSONObject();
		json.put("deviceId", remoteControlRecord.getDeviceId());
		json.put("key", controlItem);
		json.put("keyValue", propertiesValue);
		json.put("userId", controlParam.getString("userId"));
		json.put("organizationId", controlParam.getString("organizationId"));
		json.put("accessSecret", controlParam.getString("accessSecret"));
		return json;
	}



	/**
	 * 
	 * @Description:电子地图下发命令控制设备
	 * @param params
	 * @return
	 */
	public Result<String> controlByMap(String params) {
		logger.info(String.format("[controlByMap()->request params:%s]", params));
		try {
			RpcResponse<JSONObject> paramCheck = paramCheck(params);
			if (!paramCheck.isSuccess()) {
				return ResultBuilder.failResult(paramCheck.getMessage());
			}
			Result<RemoteControlRecord> paramResult = this.analysisParams(params);
			RemoteControlRecord remoteControlRecord = paramResult.getValue();
			String deviceId = remoteControlRecord.getDeviceId();

			RpcResponse<Map<String, String>> deviceInfo = deviceQueryService.getDeviceInfoForRemoteControl(deviceId);
			if (!deviceInfo.isSuccess()) {
				logger.error(String.format("[controlByMap()->fail:%s]", deviceInfo.getMessage()));
				return ResultBuilder.failResult(deviceInfo.getMessage());
			}

			JSONObject result = controlDevice(deviceInfo.getSuccessValue(), remoteControlRecord, ip);
			if (null == result) {
				return ResultBuilder.failResult("下发命令失败，http请求返回为空");
			}
			// 控制命令下发成功，保存记录
			logger.info(String.format("[controlByMap()->命令下发返回结果,%s]", result.toJSONString()));
			if (CommonConstants.NUMBER_TWO_HUNDRED.equals(result.getString(RemoteControlRecordConstants.STATUS))) {
				logger.info(String.format("[controlByMap()->命令发送成功，response:%s]", result.get("response")));
				String openType = paramCheck.getSuccessValue().getString("openType");
				JSONObject json = new JSONObject();
				jsonPut(paramCheck, remoteControlRecord, deviceId, json);
				RpcResponse<String> saveResult = remoteControlRecordCrudService
						.saveRemoteControlRecord(remoteControlRecord);
				logger.info("[controlByMap()->success:地图下发命令成功]");

				// 地图分权分域下发，启动设备超时未关定时任务
				if (null != openType && CommonConstants.POWER.equals(openType)) {
					RpcResponse<String> deviceSchedulerStart = deviceSchedulerService.deviceSchedulerStart(json);
					if (!deviceSchedulerStart.isSuccess()) {
						logger.error("deviceSchedulerStart()--fail:" + deviceSchedulerStart.getMessage());
						return ResultBuilder.failResult(deviceSchedulerStart.getMessage());
					}
				}

				return ResultBuilder.successResult(remoteControlRecord.getId(), saveResult.getMessage());
			}
			if (CommonConstants.NUMBER_FOUR_HUNDRED_AND_NINE
					.equals(result.getString(RemoteControlRecordConstants.STATUS))) {
				logger.error("[controlByMap()->fail:设备未接入]");
				return ResultBuilder.failResult("设备未接入");
			}
			// logger.error("[controlByMap()->fail:保存失败]");
			// return ResultBuilder.failResult("保存失败");

			logger.error(String.format("[controlByMap()->fail:发送命令失败，status:%s,message;%s]",
					result.get(RemoteControlRecordConstants.STATUS), result.getString("message")));
			return ResultBuilder.failResult("发送命令失败" + result.getString("message"));
		} catch (Exception e) {
			logger.error("control()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param paramCheck
	 * @param remoteControlRecord
	 * @param deviceId
	 * @param json
	 */

	private void jsonPut(RpcResponse<JSONObject> paramCheck, RemoteControlRecord remoteControlRecord, String deviceId,
			JSONObject json) {
		json.put("deviceId", deviceId);
		json.put("key", remoteControlRecord.getControlItem());
		json.put("keyValue", remoteControlRecord.getControlValue());
		json.put("userId", paramCheck.getSuccessValue().getString("userId"));
		json.put("organizationId", paramCheck.getSuccessValue().getString("organizationId"));
		json.put("accessSecret", paramCheck.getSuccessValue().getString("accessSecret"));

		// 保存控制记录
		remoteControlRecord.setControlTime(DateUtils.formatDate(new Date()));
		// 控制方式：地图下发命令,存在工单或分权分域或两者都存在
		remoteControlRecord.setControlType(4);
		remoteControlRecord.setControlState("valid");
	}



	private RpcResponse<JSONObject> paramCheck(String params) {
		if (StringUtil.isEmpty(params)) {
			return RpcResponseBuilder.buildErrorRpcResp("参数为空!");
		}
		if (ParamChecker.isNotMatchJson(params)) {
			return RpcResponseBuilder.buildErrorRpcResp("参数不为json格式!");
		}
		JSONObject cotrolParam = JSONObject.parseObject(params);
		if (StringUtils.isBlank(cotrolParam.getString(CommonConstants.ACCESSSECRET))) {
			logger.error("[invalid--fail:控制命令下发失败，接入方秘钥accessSecret不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("控制命令下发失败，接入方秘钥accessSecret不能为空!");
		}
		if (StringUtils.isBlank(cotrolParam.getString(CommonConstants.USERID))) {
			logger.error("[invalid--fail:控制命令下发失败，用户userId不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("控制命令下发失败，用户userId不能为空!");
		}
		if (StringUtils.isBlank(cotrolParam.getString(CommonConstants.OPENTYPE))) {
			logger.error("[invalid--fail:控制命令下发失败，开启类型openType不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("控制命令下发失败，开启类型openType不能为空!");
		}
		JSONObject json = JSONObject.parseObject(params);
		return RpcResponseBuilder.buildSuccessRpcResp("校验成功!", json);
	}



	/**
	 * 
	 * @Description:告警工单下发命令
	 * @param params
	 * @param orderId
	 * @return
	 * @throws Exception
	 */

	public Result<String> controlByAlarmOrder(String params, String orderId) {
		logger.info(String.format("[controlByAlarmOrder()->request params : %s , orderId = %s]", params, orderId));
		try {
			Result<String> paramsCheckAlarmOrder = paramsCheckAlarmOrder(params, orderId);
			if (paramsCheckAlarmOrder != null) {
				return paramsCheckAlarmOrder;
			}

			// 判断接受工单时间与当前时间相差是否一天
			JSONObject controlJson = JSON.parseObject(params);
			Result<String> check = controlByAlarmOrderCheck(orderId, controlJson);
			if (null != check) {
				return ResultBuilder.failResult(check.getResultStatus().getResultMessage());
			}

			// 下发命令
			Result<RemoteControlRecord> analysisParams = this.analysisParams(params);
			RemoteControlRecord remoteControlRecord = analysisParams.getValue();

			// 查询设备信息，用于下发命令
			RpcResponse<Map<String, String>> deviceInfo = deviceQueryService
					.getDeviceInfoForRemoteControl(remoteControlRecord.getDeviceId());
			if (!deviceInfo.isSuccess()) {
				logger.error(String.format("[controlByAlarmOrder->fail:%s]", deviceInfo.getMessage()));
				return ResultBuilder.failResult(deviceInfo.getMessage());
			}

			JSONObject result = controlDevice(deviceInfo.getSuccessValue(), remoteControlRecord, ip);
			if (null == result) {
				return ResultBuilder.failResult("下发命令失败，http请求返回为空");
			}
			logger.info(String.format("[controlByAlarmOrder()->命令发送返回信息,%s]", result.toJSONString()));
			// 控制命令下发成功，保存记录
			if (CommonConstants.NUMBER_TWO_HUNDRED.equals(result.getString(RemoteControlRecordConstants.STATUS)))
			// if (updateThingShadowResult.getStatus() == 200)
			{
				logger.info(String.format("[controlByAlarmOrder()->命令发送成功，response:%s]", result.get("response")));
				// 保存控制记录
				remoteControlRecord.setControlTime(DateUtils.formatDate(new Date()));
				// 控制方式：工单
				remoteControlRecord.setControlType(1);
				remoteControlRecord.setControlState("valid");
				RpcResponse<String> saveResult = remoteControlRecordCrudService
						.saveRemoteControlRecord(remoteControlRecord);
				logger.info(String.format("[contorlByOrder()->success:%s]", saveResult.getMessage()));
				return ResultBuilder.successResult(remoteControlRecord.getId(), saveResult.getMessage());
			}
			if (CommonConstants.NUMBER_FOUR_HUNDRED_AND_NINE
					.equals(result.getString(RemoteControlRecordConstants.STATUS)))
			// if (updateThingShadowResult.getStatus() == 409)
			{
				logger.error("[contorlByOrder()->fail:设备没有上报记录，无法下发控制]");
				return ResultBuilder.failResult("设备没有上报记录，无法下发控制");
			}

			// logger.error("[contorlByOrder()->fail:控制记录保存失败]");
			// return ResultBuilder.failResult("保存失败");
			logger.error(String.format("[controlByAlarmOrder()->fail,message:%s]", result.get("message")));
			return ResultBuilder.failResult("发送命令失败" + result.get("message").toString());

		} catch (Exception e) {
			logger.error("controlByAlarmOrder()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param orderId
	 * @param controlJson
	 * @throws Exception
	 */

	private Result<String> controlByAlarmOrderCheck(String orderId, JSONObject controlJson) throws Exception {
		if (!checkTimer(controlJson.getString(RemoteControlRecordConstants.ORDER_RECEIVETIME))) {
			return ResultBuilder.failResult("命令时间已超过接受工单一天时间，命令失败！");
		}

		// 通过工单ID查询告警工单
		RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService.getAlarmOrderInfoById(orderId);
		Map<String, Object> alarmOrderMap = alarmOrderInfoById.getSuccessValue();
		if (!alarmOrderInfoById.isSuccess()) {
			logger.info(String.format("[controlByAlarmOrder()->error:%s]", alarmOrderInfoById.getMessage()));
			return ResultBuilder.failResult(alarmOrderInfoById.getMessage());
		}
		if (alarmOrderMap == null) {
			logger.error("[controlByAlarmOrder()->error:工单信息不存在！]");
			return ResultBuilder.failResult("工单信息不存在！");
		}

		// 判断当前人的id和工单接收人的id是否是同一个人
		String userId = (String) alarmOrderMap.get(AlarmInfoConstants.USER_ID);
		if (!controlJson.getString(RemoteControlRecordConstants.CONTROL_CONTROLUSERID).equals(userId)) {
			logger.error("[controlByAlarmOrder()->error:当前人员没有命令权限！]");
			return ResultBuilder.failResult("当前人员没有命令权限！");
		}

		// 判断状态是处理中或者转故障被拒绝才可以进行命令
		String processState = (String) alarmOrderMap.get(AlarmOrderConstants.PROCESS_STATE);

		// 转故障是于2019.6.20添加，原本只用判断是否处理中
		if (!CommonConstants.NUMBER_ZERO.equals(processState) && !CommonConstants.NUMBER_TWO.equals(processState)) {
			logger.error(String.format("[controlByAlarmOrder()->error:工单状态：%s,不是处理中！]", processState));
			return ResultBuilder.failResult("工单状态未在处理中！");
		}
		return null;
	}



	/**
	 * 
	 * @Description:参数校验
	 * @param params
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	private Result<String> paramsCheckAlarmOrder(String params, String orderId) throws Exception {
		if (StringUtils.isBlank(orderId)) {
			logger.error(String.format("[controlByAlarmOrder()->error:%s]", "工单Id为null！"));
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(params)) {
			logger.error("[controlByAlarmOrder()->error:传入参数不是json!]");
			return ResultBuilder.invalidResult();
		}
		RpcResponse<String> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger, "controlByAlarmOrder",
				JSON.parseObject(params), RemoteControlRecordConstants.CONTROL_CONTROLITEM,
				RemoteControlRecordConstants.CONTROL_CONTROLUSERID, RemoteControlRecordConstants.CONTROL_CONTROLVALUE,
				RemoteControlRecordConstants.CONTROL_DEVICEID, RemoteControlRecordConstants.CONTROL_OPERATEUSERNAME,
				RemoteControlRecordConstants.CONTROL_OPERATEUSERPHONE, RemoteControlRecordConstants.CONTROL_REASON,
				RemoteControlRecordConstants.ORDER_RECEIVETIME);
		if (checkBusinessKey != null) {
			logger.error(String.format("[controlByAlarmOrder()->error:%s]", checkBusinessKey.getMessage()));
			return ResultBuilder.failResult(checkBusinessKey.getMessage());
		}
		return null;
	}



	/**
	 * 
	 * @Description:校验接受时间是否大于当前时间-1 date(日)
	 * @param receiveTime
	 * @return
	 */
	private Boolean checkTimer(String receiveTime) throws Exception {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 暂时为两分钟
		calendar.add(Calendar.DATE, -1);
		Date time = calendar.getTime();
		Date receiveDate = DateUtils.toDate(receiveTime);
		if (receiveDate.getTime() > time.getTime()) {
			return true;
		}
		return false;
	}



	/**
	 * @Description:告警工单app下发命令
	 * @param params
	 * @param orderId
	 * @return
	 */

	public Result<String> controlByAlarmOrderApp(String params, String orderId) {
		logger.info(String.format("[controlByAlarmOrderApp()->request params : %s , orderId = %s]", params, orderId));

		try {

			if (ParamChecker.isNotMatchJson(params) || StringUtils.isBlank(orderId)) {
				logger.error("[controlByAlarmOrderApp()->error:传入参数不是json!或者工单id不存在！]");
				return ResultBuilder.invalidResult();
			}

			// 判断接受工单时间与当前时间相差是否一天
			JSONObject controlJson = JSON.parseObject(params);
			if (StringUtils.isBlank(controlJson.getString(RemoteControlRecordConstants.ORDER_RECEIVETIME))) {
				logger.error("[controlByAlarmOrderApp()->error:receiveTime is null ! ]");
				return ResultBuilder.noBusinessResult();
			}

			if (!checkTimer(controlJson.getString(RemoteControlRecordConstants.ORDER_RECEIVETIME))) {
				return ResultBuilder.failResult("命令时间已超过接受工单一天时间，命令失败！");
			}

			// 通过工单ID查询告警工单
			RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService.getAlarmOrderInfoById(orderId);
			Map<String, Object> alarmOrderMap = alarmOrderInfoById.getSuccessValue();
			if (!alarmOrderInfoById.isSuccess()) {
				logger.info(String.format("[controlByAlarmOrder()->error:%s]", alarmOrderInfoById.getMessage()));
				return ResultBuilder.failResult(alarmOrderInfoById.getMessage());
			}
			if (alarmOrderMap == null) {
				logger.error("[controlByAlarmOrder()->error:工单信息不存在！]");
				return ResultBuilder.failResult("工单信息不存在！");
			}

			// 判断当前人的id和工单接收人的id是否是同一个人
			String userId = (String) alarmOrderMap.get(AlarmInfoConstants.USER_ID);
			if (!(controlJson.getString(RemoteControlRecordConstants.CONTROL_CONTROLUSERID) + "").equals(userId)) {
				logger.error("[controlByAlarmOrder()->error:当前人员没有命令权限！]");
				return ResultBuilder.failResult("当前人员没有命令权限！");
			}

			// 判断状态是处理中才可以进行命令
			String processState = (String) alarmOrderMap.get(AlarmOrderConstants.PROCESS_STATE);

			// 转故障是于2019.6.20添加，原本只用判断是否处理中
			if (!CommonConstants.NUMBER_ZERO.equals(processState) && !CommonConstants.NUMBER_TWO.equals(processState)) {
				logger.error(String.format("[controlByAlarmOrder()->error:工单状态：%s,不是处理中！]", processState));
				return ResultBuilder.failResult("工单状态未在处理中！");
			}

			Result<String> openLock = openLock(params);
			return openLock;

		} catch (Exception e) {
			logger.error("controlByAlarmOrderApp()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:判断设备是否包含网关id,以此决定命令下发方式
	 * @param map
	 * @return
	 */
	public Boolean checkDevice(Map<String, String> map) {
		logger.info(String.format("[checkDevice()->进入方法,参数:%s]", map));
		if (StringUtils.isBlank(map.get(DeviceContants.GATEWAYID))) {
			logger.info("该设备没有gatewayId,用老方式下发命令");
			return false;
		} else {
			logger.info("该设备有gatewayId,用新方式下发命令");
			return true;
		}
	}



	/**
	 * 
	 * @Description:下发命令给新iot的设备
	 * @param map
	 * @return
	 */
	public JSONObject controlDevice(Map<String, String> deviceInfo, RemoteControlRecord remoteControlRecord,
			String ip) {
		logger.info(String.format("[controlDevice()->进入方法，下发命令到新iot创建的设备,设备参数:%s,控制参数:%s,value:%s,ip:%s]", deviceInfo,
				remoteControlRecord.getControlItem(), remoteControlRecord.getControlValue(), ip));
		// 将命令转成json格式
		JSONObject orderContent = new JSONObject();
		JSONObject node = new JSONObject();
		node.put("attributeName", remoteControlRecord.getControlItem());
		String controlValue = remoteControlRecord.getControlValue();
		if (isNumeric(controlValue)) {
			node.put("attributeDesiredValue", Integer.valueOf(controlValue));
		} else {
			node.put("attributeDesiredValue", controlValue);
		}
		List<JSONObject> list = Lists.newArrayList();
		list.add(node);
		orderContent.put("attributeInfo", list);
		logger.info(String.format("[controlDevice()->命令内容:%s]", orderContent.toJSONString()));

		String gatewayId = deviceInfo.get(DeviceContants.GATEWAYID);
		String appId = deviceInfo.get(FactoryConstants.APPID);
		String appKey = deviceInfo.get(FactoryConstants.APPKEY);
		// String deviceId = deviceInfo.get(DeviceContants.DEVICEID) + "";
		// 获得设备真实id
		// String subThingId = deviceId.replace(gatewayId + "_", "");
		String subThingId = deviceInfo.get(DeviceContants.SUB_DEVICID);
		// 获取命令下发地址
		String url = RemoteControl.createUrl(gatewayId, subThingId, appId, ip);
		logger.info(String.format("[controlDevice()->命令下发地址:%s]", url));
		// 下发命令
		JSONObject jsonObject = RemoteControl.remoteControlByPut(appKey, orderContent.toJSONString(), url,
				httpRequest.getHeader(InterGatewayConstants.TOKEN));
		return jsonObject;
	}



	/**
	 * @Description:判断controlValue是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		return NUMBER_PATTERN.matcher(str).matches();
	}



	/**
	 * @Description:演示用开锁，直接下发到wingsIot
	 * @param jsonObject
	 * @return
	 */
	public Result<String> wingsIotOpenLock(JSONObject jsonObject) {
		try {
			if (jsonObject == null || jsonObject.isEmpty()) {
				logger.error("[wingsIotOpenLock()->error:传入参数为空]");
				return ResultBuilder.emptyResult();
			}

			logger.info(String.format("[wingsIotOpenLock()->request params:%s]", jsonObject.toJSONString()));
			// 参数校验
			RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger, "wingsIotOpenLock",
					jsonObject, "deviceId", "operateUserName", "operateUserPhone", "controlUserId","command");
			if (containsParamKey != null) {
				return ResultBuilder.failResult(containsParamKey.getMessage());
			}
			// 参数校验
			RpcResponse<Object> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger, "wingsIotOpenLock",
					jsonObject, "deviceId", "operateUserName", "operateUserPhone", "controlUserId","command");
			if (checkBusinessKey != null) {
				return ResultBuilder.failResult(checkBusinessKey.getMessage());
			}

			String deviceId = jsonObject.getString("deviceId");

			// 查询设备信息
			RpcResponse<Device> queryDeviceByDeviceId = deviceQueryService.queryDeviceInfoById(deviceId);
			if (!queryDeviceByDeviceId.isSuccess()) {
				logger.error(String.format("[wingsIotOpenLock()->error:查询设备失败，deivceId:%s]", deviceId));
				return ResultBuilder.failResult("查找设备信息失败");
			}
			if (StringUtils.isBlank(queryDeviceByDeviceId.getSuccessValue().getDeviceType())) {
				logger.error(String.format("[wingsIotOpenLock()->error:设备类型为空，deivceId:%s]", deviceId));
				return ResultBuilder.failResult("查找设备类型失败");
			}
			String deviceTypeId = queryDeviceByDeviceId.getSuccessValue().getDeviceType();
			Map<String, String> productApiKeyTable = WingsIotConstansQuery.getProductApiKeyTable();

			RemoteControlRecord remoteControlRecord = new RemoteControlRecord();
			remoteControlRecord.setId(UtilTool.getUuId());
			remoteControlRecord.setDeviceId(deviceId);
			remoteControlRecord.setControlUserId(jsonObject.getString("userId"));
			remoteControlRecord.setOperateUserName(jsonObject.getString("operateUserName"));
			remoteControlRecord.setOperateUserPhone(jsonObject.getString("operateUserPhone"));
			remoteControlRecord.setControlItem("lock_state");
			remoteControlRecord.setControlValue("0");
			remoteControlRecord.setControlTime(DateUtils.formatDate(new Date()));
			
		
			
			JSONObject command = jsonObject.getJSONObject("command");
			
			
			
			RpcResponse<String> sendCommandForWings = remoteControlRecordCrudService.sendCommandForWings(deviceId, deviceTypeId, command, productApiKeyTable, "Set_Lock");
			logger.info(String.format("[wingsIotOpenLock()->发送指令到设备--deviceId:%s,productId:%s,command:%s]", deviceId,deviceTypeId,command.toJSONString()));
			if (!sendCommandForWings.isSuccess()) {
				logger.error(String.format("wingsIotOpenLock()->error:deviceId:%s,errorMsg:%s", deviceId,
						sendCommandForWings.getMessage()));
				
				remoteControlRecord.setControlState("invalid");
				remoteControlRecord.setReason(sendCommandForWings.getMessage());
				// 保存记录
				remoteControlRecordCrudService.saveRemoteControlRecord(remoteControlRecord);
				logger.info("[wingsIotOpenLock()->success:控制记录保存成功]");
				return ResultBuilder.failResult("命令发送失败，" + sendCommandForWings.getMessage());
			} else {
				logger.info(String.format("wingsIotOpenLock()->success：发送指令成功设备Id：%s", deviceId));
				remoteControlRecord.setControlState("valid");
				// 保存记录
				remoteControlRecordCrudService.saveRemoteControlRecord(remoteControlRecord);
				logger.info("[wingsIotOpenLock()->success:控制记录保存成功]");
				return ResultBuilder.successResult(null, "命令发送成功");
			}
		} catch (Exception e) {
			logger.error("wingsIotOpenLock()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
