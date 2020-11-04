package com.locman.app.crud.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.locman.app.entity.vo.Role;
import com.locman.app.query.service.UscQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.BalanceSwitchStateRecordCurdService;
import com.run.locman.api.crud.service.DeviceSchedulerService;
import com.run.locman.api.crud.service.RemoteControlRecordCrudService;
import com.run.locman.api.entity.RemoteControlRecord;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.constants.CommonConstants;

@Service
public class UnlockAppCrudService extends BaseAppController {

	private static final Logger					logger		= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private UscQueryService						uscQueryService;

	@Autowired
	private RemoteControlRecordCrudService		remoteControlRecordCrudService;

	@Autowired
	SimpleOrderQueryService						simpleOrderQueryService;

	@Autowired
	DeviceQueryService							deviceQueryService;

	@Autowired
	private CommonService						commonService;

	@Autowired
	private BalanceSwitchStateRecordCurdService	balanceSwitchStateRecordCurdService;

	@Autowired
	private DeviceSchedulerService				deviceSchedulerService;

	@Autowired
	private AlarmOrderQueryService				alarmOrderQueryService;

	String										path		= "remoteControl/";

	String										sdfFormat	= "yyyy-MM-dd HH:mm:ss";



	/**
	 * 工单开锁 <method description>
	 *
	 * @param param
	 * @param orderId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result<String> contorlByOrder(String param, String orderId) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			RpcResponse<Map<String, Object>> rpcResponse = simpleOrderQueryService.findById(orderId);
			if (!rpcResponse.isSuccess() || rpcResponse.getSuccessValue() == null
					|| rpcResponse.getSuccessValue().isEmpty()) {
				return ResultBuilder.failResult("工单不存在");
			}
			if (!jsonObject.containsKey("organizationId")) {
				return ResultBuilder.emptyResult();
			}
			Map<String, Object> simpleOrderMap = rpcResponse.getSuccessValue();
			Date now = getNowDate();
			if (DateUtils.toDate(simpleOrderMap.get("processStartTime").toString()).compareTo(now) == 1
					|| DateUtils.toDate(simpleOrderMap.get("processEndTime").toString()).compareTo(now) == -1) {
				return ResultBuilder.failResult("当前时间不在工单时间范围内");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			if (!simpleOrderMap.get("userId").equals(userJson.get("_id"))) {
				return ResultBuilder.failResult("没有操作权限");
			}
			String accessSecret = Constant.GETACCESSSECRET(userJson.get("_id").toString());
			jsonObject.put("accessSecret", accessSecret);
			jsonObject.put("userId", userJson.get("_id"));
			jsonObject.put("operateUserPhone", userJson.get("mobile"));
			jsonObject.put("operateUserName", userJson.get("userName"));
			jsonObject.put("openType", "taskOrder");
			jsonObject.put("reason", "工单开锁");
			String url = path + "openLock";
			logger.info("工单开锁");
			logger.info("流量开锁==》 " + jsonObject.toJSONString());
			String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, jsonObject.toString());
			Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
			String value = "";
			if (resultValue.containsKey("success")) {
				value = resultValue.get("success");
				return ResultBuilder.successResult(value, "开锁成功");
			} else {
				value = resultValue.get("error");
				return ResultBuilder.failResult(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 添加开锁记录 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result<String> saveRemoteControlRecord(String param) {
		try {
			logger.info("进入方法：saveRemoteControlRecord----------------");
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("deviceId")) {
				return ResultBuilder.emptyResult();
			}
			String deviceId = jsonObject.getString("deviceId");
			if (!jsonObject.containsKey("controlItem")) {
				return ResultBuilder.emptyResult();
			}
			String controlItem = jsonObject.getString("controlItem");
			if (!jsonObject.containsKey("controlValue")) {
				return ResultBuilder.emptyResult();
			}
			String controlValue = jsonObject.getString("controlValue");
			if (!jsonObject.containsKey("organizationId")) {
				return ResultBuilder.emptyResult();
			}
			String token = request.getHeader("token");
			if (token == null || token.equals("")) {
				return ResultBuilder.failResult("token不能为空");
			}

			List<String> devIds = Lists.newArrayList();
			devIds.add(deviceId);
			RpcResponse<List<Map<String, Object>>> devRpc = deviceQueryService.queryBatchDeviceInfoForDeviceIds(devIds);
			if (!devRpc.isSuccess() || devRpc.getSuccessValue() == null || devRpc.getSuccessValue().isEmpty()) {
				logger.info("开锁记录添加失败" + devRpc.getException() + "| " + devRpc.getMessage() + "| ");
				return ResultBuilder.failResult("开锁记录添加失败");
			}
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				logger.info("无效的token");
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			RemoteControlRecord remoteControlRecord = new RemoteControlRecord();
			remoteControlRecord.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			remoteControlRecord.setDeviceId(deviceId);
			remoteControlRecord.setControlItem(controlItem);
			remoteControlRecord.setControlValue(controlValue);
			remoteControlRecord.setControlUserId(userJson.getString("_id"));
			remoteControlRecord.setOperateUserName(userJson.getString("userName"));
			remoteControlRecord.setOperateUserPhone(userJson.getString("mobile"));
			remoteControlRecord.setReason("工单开锁");
			if (jsonObject.containsKey("reason") && !"".equals(jsonObject.getString("reason"))) {
				remoteControlRecord.setReason(jsonObject.getString("reason"));
			}
			remoteControlRecord.setControlTime(DateUtils.formatDate(getNowDate()));
			remoteControlRecord.setControlType(1); // 控制方式：工单
			remoteControlRecord.setControlState("valid");
			RpcResponse<String> saveResult = remoteControlRecordCrudService
					.saveRemoteControlRecord(remoteControlRecord);
			logger.info("命令记录保存结果：" + saveResult.getMessage() + "| " + saveResult.getException() + "| "
					+ saveResult.getSuccessValue());
			if (saveResult.isSuccess() && saveResult.getSuccessValue() != null) {
				JSONObject schedJson = new JSONObject();
				schedJson.put("deviceId", deviceId);
				schedJson.put("key", controlItem);
				schedJson.put("deviceId", deviceId);
				schedJson.put("organizationId", jsonObject.getString("organizationId"));
				schedJson.put("userId", userJson.getString("_id"));
				schedJson.put("keyValue", controlValue);
				deviceSchedulerService.deviceSchedulerStart(schedJson);
				return ResultBuilder.successResult(remoteControlRecord.getId(), saveResult.getMessage());
			} else {
				return ResultBuilder.failResult(saveResult.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 分权分域开锁 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result<String> inspectionContor(String param) {
		try {
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject paramJson = JSONObject.parseObject(param);
			if (!paramJson.containsKey("roleId")) {
				return ResultBuilder.noBusinessResult();
			}
			String roleId = paramJson.getString("roleId");
			if (!paramJson.containsKey("propertiesId")) {
				return ResultBuilder.noBusinessResult();
			}
			String propertiesId = paramJson.getString("propertiesId");
			if (!paramJson.containsKey("button")) {
				return ResultBuilder.noBusinessResult();
			}
			String button = paramJson.getString("button");
			if (!paramJson.containsKey("deviceId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!paramJson.containsKey("organizationId")) {
				return ResultBuilder.noBusinessResult();
			}
			String deviceId = paramJson.getString("deviceId");
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userName = userJson.getString("userName");
			String userId = userJson.getString("_id");
			String mobile = userJson.getString("mobile");
			String accessSecret = Constant.GETACCESSSECRET(userId);
			Role role = uscQueryService.findOrganizationByRoleId(accessSecret, roleId, userId);
			JSONObject inspetJSON = new JSONObject();
			if (role != null && !"".equals(role.getOrganizeId()) && !"".equals(role.getRoleId())) {
				inspetJSON.put("accessSecret", accessSecret);
				inspetJSON.put("deviceId", deviceId);
				inspetJSON.put("userId", userId);
				inspetJSON.put("operateUserName", userName);
				inspetJSON.put("operateUserPhone", mobile);
				inspetJSON.put("propertiesId", propertiesId);
				inspetJSON.put("button", button);
				inspetJSON.put("reason", paramJson.getString("reason"));
				inspetJSON.put("openType", "power");
				String url = path + "openLock";
				logger.info("开锁");
				logger.info("分权分域开锁==》 " + inspetJSON.toJSONString());
				String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, inspetJSON.toString());
				Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
				String value = "";
				if (resultValue.containsKey("success")) {
					value = resultValue.get("success");
					return ResultBuilder.successResult(value, "命令发送成功");
				} else {
					value = resultValue.get("error");
					return ResultBuilder.failResult(value);
				}
			} else {
				return ResultBuilder.failResult("用户组织信息异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 开启或关闭平和告警开关 <method description>
	 *
	 * @param param
	 * @return
	 */
	public Result<?> openBalance(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject parseJson = JSONObject.parseObject(param);
			if (!parseJson.containsKey("deviceId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("facilityId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("deviceTypeId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("state")) {
				return ResultBuilder.noBusinessResult();
			}
			String token = request.getHeader("token");
			String userId = uscQueryService.findUserIdByToken(token);
			if (userId == null) {
				return ResultBuilder.failResult("无效的token");
			}
			String accessSecret = Constant.GETACCESSSECRET(userId);
			parseJson.put("accessSecret", accessSecret);
			RpcResponse<Boolean> response = balanceSwitchStateRecordCurdService.openOrClose(parseJson);
			if (response != null && response.isSuccess() && response.getSuccessValue() != null) {
				return ResultBuilder.successResult(response.getSuccessValue(), "操作成功");
			}
			return ResultBuilder.failResult(response.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 告警工单流量开锁 <method description>
	 *
	 * @param orderId
	 * @param param
	 * @return
	 */
	public Result<?> alarmOrderUnlock(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject parseJson = JSONObject.parseObject(param);
			if (!parseJson.containsKey("userId")) {
				return ResultBuilder.noBusinessResult();
			}
			String userId = parseJson.getString("userId");
			if (!parseJson.containsKey("orderId")) {
				return ResultBuilder.noBusinessResult();
			}
			String orderId = parseJson.getString("orderId");
			if (!parseJson.containsKey("deviceId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("operateUserName")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("operateUserPhone")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("propertiesId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("reason")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("button")) {
				return ResultBuilder.noBusinessResult();
			}
			String accessSecret = Constant.GETACCESSSECRET(userId);
			parseJson.put("accessSecret", accessSecret);
			parseJson.put("openType", "alarmOrder");
			parseJson.put("receiveTime", "");
			RpcResponse<Map<String, Object>> rpcResponse = alarmOrderQueryService.getAlarmOrderInfoById(orderId);
			if (rpcResponse != null && rpcResponse.isSuccess() && rpcResponse.getSuccessValue() != null) {
				Map<String, Object> orderMap = rpcResponse.getSuccessValue();
				parseJson.put("receiveTime", orderMap.get("receiveTime")==null?"":orderMap.get("receiveTime").toString());
			}
			String url = path + "controlByAlarmOrderApp/" + orderId;
			parseJson.remove("orderId");
			String resultHttp = commonService.requestRest(Constant.LOCMAN_PORT, url, parseJson.toJSONString());
			Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(resultHttp));
			String value = "";
			if (resultValue.containsKey("success")) {
				value = resultValue.get("success");
				return ResultBuilder.successResult(value, "命令发送成功");
			} else {
				value = resultValue.get("error");
				return ResultBuilder.failResult(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}

}
