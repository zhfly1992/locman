/*
 * File name: FaultOrderProcessCudServiceTimerImpl.java
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

package com.run.locman.timer.cud;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.activity.api.crud.ActivityProgressCrud;
import com.run.activity.api.query.ActivityProgressQuery;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.FaultOrderDevice;
import com.run.locman.api.timer.crud.repository.FaultOrderDeviceCudRepository;
import com.run.locman.api.timer.crud.repository.FaultOrderProcessCudRepository;
import com.run.locman.api.timer.crud.service.FaultOrderProcessCudService;
import com.run.locman.api.timer.query.service.DeviceQuery;
import com.run.locman.api.timer.query.service.OrderProcessQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceDeletionConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.SimpleOrderConstants;
import com.run.locman.constants.TimeoutReportConfigConstants;
import com.run.sms.api.JiguangService;

import entity.JiguangEntity;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年7月4日
 */
public class FaultOrderProcessCudServiceTimerImpl implements FaultOrderProcessCudService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private ActivityProgressCrud			activityProgressCrud;

	@Autowired
	private ActivityProgressQuery			activityProgressQuery;

	@Autowired
	private FaultOrderProcessCudRepository	faultOrderProcessCudRepository;

	@Autowired
	private OrderProcessQueryService		orderProcessQueryService;

	@Autowired
	private FaultOrderDeviceCudRepository	faultOrderDeviceCudRepository;

	@Autowired
	private DeviceQuery						deviceQuery;

	@Autowired
	private JiguangService					jiguangService;


	/**
	 * @see com.run.locman.api.timer.crud.service.FaultOrderProcessCudService#addFaultOrderForDeviceDeletion(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> addFaultOrderForDeviceDeletion(JSONObject jsonObject) {
		logger.info(String.format("[addFaultOrderForDeviceDeletion()->request params:%s]", jsonObject));
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.ACCESS_SECRET))) {
			logger.error("[addFaultOrderForDeviceDeletion()->invalid：接入方密钥不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.FAULT_TYPE))) {
			logger.error("[addFaultOrderForDeviceDeletion()->invalid：faultType不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("faultType不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.MANAGER))) {
			logger.error("[addFaultOrderForDeviceDeletion()->invalid：manager申报人不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("manager申报人不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.DEVICE_COUNT))) {
			logger.error("[addFaultOrderForDeviceDeletion()->invalid：deviceCount不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("deviceCount不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.PHONE))) {
			logger.error("[addFaultOrderForDeviceDeletion()->invalid：phone不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("phone不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.USER_ID))) {
			logger.error("[addFaultOrderForDeviceDeletion()->invalid：userId不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("userId不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.FACTORY_ID))) {
			logger.error("[addFaultOrderForDeviceDeletion()->invalid：厂家Id不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("厂家Id不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.ORGANIZE_ID))) {
			logger.error("[addFaultOrderForDeviceDeletion()->invalid：组织id不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("组织id不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.DEVICE_IDS_ADD))) {
			logger.error("[addFaultOrderForDeviceDeletion()->invalid：deviceIdsAdd设备id集合数据格式错误！]");
			return RpcResponseBuilder.buildErrorRpcResp("设备id集合数据格式错误！");
		}
		try {
			return trythis(jsonObject);
		} catch (Exception e) {
			logger.error("addFaultOrderForDeviceDeletion()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}


	private RpcResponse<String> trythis(JSONObject jsonObject) throws Exception {
		String deviceId = jsonObject.getString(DeviceDeletionConstants.DEVICE_IDS_ADD);
		String userId = jsonObject.getString(DeviceDeletionConstants.USER_ID);
		RpcResponse<List<String>> result = deviceQuery.checkOrderExist(deviceId);
		if ( null == result || !result.isSuccess()) {
			return sss();
		}
		List<String> successValue = result.getSuccessValue();
		if (0 < successValue.size()) {
			return panduan1(deviceId, successValue);
		}
		Map<String, Object> params = Maps.newHashMap();
		String id = (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.FAULT_ORDER_ID)))
				? UtilTool.getUuId() : jsonObject.getString(DeviceDeletionConstants.FAULT_ORDER_ID);
		String accessSecret = jsonObject.getString(DeviceDeletionConstants.ACCESS_SECRET);
		// 根据组织id查询用户节点信息集合
		JSONArray jsonArray = null;
		// 启动流程
		JSONObject json = new JSONObject();
		// faultProcessType:1表示告警转故障的流程类型 2表示故障流程类型
		json.put("type", "manualFailureProcess");
		// 根据组织id查询用户节点信息集合
		Map<String, Object> map = map1(jsonObject, accessSecret);
		RpcResponse<JSONArray> nodeInfoResponse = orderProcessQueryService.queryNodeInfoForActivity(map);
		if (nodeInfoResponse.isSuccess()) {
			logger.info(nodeInfoResponse.getMessage());
			jsonArray = nodeInfoResponse.getSuccessValue();
			if (jsonArray.isEmpty()) {
				return RpcResponseBuilder.buildErrorRpcResp("请求人员非法操作!");
			}
		} else {
			return else1(nodeInfoResponse);
		}
		String serialNumber = addJson(userId, id, accessSecret, jsonArray, json);
		if (StringUtils.isBlank(serialNumber)) {
			return panduan10();
		}
		json.put(DeviceDeletionConstants.ACTIVITY_SERIAL_NUMBER, serialNumber);
		RpcResponse<Map<String, Object>> startProgressResult = activityProgressCrud.startProgress(json);
		if (!startProgressResult.isSuccess()) {
			return panduan9(startProgressResult);
		}
		if (startProgressResult.getSuccessValue() == null) {
			return panduan11();
		}
		String processId = startProgressResult.getSuccessValue().get(DeviceDeletionConstants.PROCESS_ID).toString();

		// 完成第一个节点
		json.put(DeviceDeletionConstants.PROCESS_ID, processId);
		RpcResponse<Map<String, Object>> acceptProgress = activityProgressCrud.acceptProgress(json);
		if (!acceptProgress.isSuccess()) {
			return panduan12(acceptProgress);
		}
		// 查询流程状态
		JSONObject queryProgressState = new JSONObject();
		queryProgressState.put(DeviceDeletionConstants.PROCESS_ID, processId);
		RpcResponse<Map<String, Object>> queryProgressResult = activityProgressQuery
				.getProcessStatus(queryProgressState);
		if (!queryProgressResult.isSuccess()) {
			return panduan9(startProgressResult);
		}
		if (queryProgressResult.getSuccessValue() == null) {
			return panduan8();
		}
		String processState = queryProgressResult.getSuccessValue().get("status").toString();
		params.put("processState", processState);
		// 添加故障工单
		// 向故障工单和设备绑定关系表添加数据
		int addNum = addData(jsonObject, deviceId, userId, params, id, accessSecret, serialNumber, processId);
		if (addNum > 0) {
			return panduan2(jsonObject, deviceId, userId, id);
		} else {
			return else2();
		}
	}


	private RpcResponse<String> panduan12(RpcResponse<Map<String, Object>> acceptProgress) {
		logger.error(
				String.format("[addFaultOrderForDeviceDeletion()->error: %s", acceptProgress.getMessage()));
		return RpcResponseBuilder.buildErrorRpcResp(acceptProgress.getMessage());
	}


	private RpcResponse<String> panduan11() {
		logger.error("[addFaultOrderForDeviceDeletion->add()->invalid: 流程开启失败!]");
		return RpcResponseBuilder.buildErrorRpcResp("流程开启失败!");
	}


	private RpcResponse<String> panduan10() {
		logger.error("addFaultOrderForDeviceDeletion()-->获取故障工单流水号失败");
		return RpcResponseBuilder.buildErrorRpcResp("获取故障工单流水号失败");
	}


	private RpcResponse<String> panduan9(RpcResponse<Map<String, Object>> startProgressResult) {
		logger.error(
				"[addFaultOrderForDeviceDeletion->add()->invalid: " + startProgressResult.getMessage() + "]");
		return RpcResponseBuilder.buildErrorRpcResp(startProgressResult.getMessage());
	}


	private RpcResponse<String> panduan8() {
		logger.error("[addFaultOrderForDeviceDeletion->add()->invalid: 查询流程状态失败!]");
		return RpcResponseBuilder.buildErrorRpcResp("查询流程状态失败!");
	}


	private RpcResponse<String> else2() {
		logger.error("[addFaultOrderForDeviceDeletion()->error: " + MessageConstant.ADD_FAIL + "]");
		return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL);
	}


	private RpcResponse<String> else1(RpcResponse<JSONArray> nodeInfoResponse) {
		logger.info("addFaultOrderForDeviceDeletion()-->" + nodeInfoResponse.getMessage());
		return RpcResponseBuilder.buildErrorRpcResp(nodeInfoResponse.getMessage());
	}


	private RpcResponse<String> sss() {
		logger.error("addFaultOrderForDeviceDeletion()-->检查设备是否存在工单失败");
		return RpcResponseBuilder.buildErrorRpcResp("检查设备是否存在工单失败");
	}


	private Map<String, Object> map1(JSONObject jsonObject, String accessSecret) {
		Map<String, Object> map = Maps.newHashMap();
		map.put(DeviceDeletionConstants.ORGANIZE_ID, jsonObject.getString(DeviceDeletionConstants.ORGANIZE_ID));
		map.put("processType", "超时故障工单流程");
		map.put(DeviceDeletionConstants.ACCESS_SECRET, accessSecret);
		return map;
	}


	private String addJson(String userId, String id, String accessSecret, JSONArray jsonArray, JSONObject json) {
		json.put(DeviceDeletionConstants.FAULT_ORDER_ID, id);
		json.put(DeviceDeletionConstants.USER_ID, userId);
		json.put(DeviceDeletionConstants.ACTIVITY_USEROBJ, jsonArray);
		json.put(DeviceDeletionConstants.ACCESS_SECRET, accessSecret);
		String serialNumber = faultOrderProcessCudRepository.querySerialNumber(accessSecret);
		return serialNumber;
	}


	private int addData(JSONObject jsonObject, String deviceId, String userId, Map<String, Object> params, String id,
			String accessSecret, String serialNumber, String processId) throws Exception {
		FaultOrderDevice faultOrderDevice = new FaultOrderDevice();
		faultOrderDevice.setId(UtilTool.getUuId());
		faultOrderDevice.setDeviceId(deviceId);
		faultOrderDevice.setFaultOrderId(id);

		faultOrderDeviceCudRepository.addBindDevices(faultOrderDevice);

		params.put("createTime", DateUtils.formatDate(new Date()));
		params.put("updateTime", DateUtils.formatDate(new Date()));
		params.put(DeviceDeletionConstants.PROCESS_ID, processId);

		Integer faultType = jsonObject.getInteger(DeviceDeletionConstants.FAULT_TYPE);
		String manager = jsonObject.getString(DeviceDeletionConstants.MANAGER);
		Integer deviceCount = jsonObject.getInteger(DeviceDeletionConstants.DEVICE_COUNT);
		String phone = jsonObject.getString(DeviceDeletionConstants.PHONE);
		String mark = jsonObject.getString(DeviceDeletionConstants.MARK);
		String factoryId = jsonObject.getString(DeviceDeletionConstants.FACTORY_ID);
		String orderImg = jsonObject.getString(DeviceDeletionConstants.ORDER_IMG);
		String orderName = jsonObject.getString(DeviceDeletionConstants.ORDER_NAME);

		params.put(DeviceDeletionConstants.ACCESS_SECRET, accessSecret);
		params.put(DeviceDeletionConstants.FAULT_TYPE, faultType);
		params.put(DeviceDeletionConstants.MANAGER, manager);
		params.put(DeviceDeletionConstants.DEVICE_COUNT, deviceCount);
		params.put(DeviceDeletionConstants.FACTORY_ID, factoryId);
		params.put(DeviceDeletionConstants.PHONE, phone);
		params.put(DeviceDeletionConstants.MARK, mark);
		params.put(DeviceDeletionConstants.ORDER_NAME, orderName);
		params.put(DeviceDeletionConstants.ORDER_IMG, orderImg);
		params.put(DeviceDeletionConstants.USER_ID, userId);
		params.put(DeviceDeletionConstants.FAULT_ORDER_ID, id);
		params.put(DeviceDeletionConstants.SERIAL_NUMBER, serialNumber);
		int addNum = faultOrderProcessCudRepository.addFaultOrder(params);
		return addNum;
	}


	private RpcResponse<String> panduan2(JSONObject jsonObject, String deviceId, String userId, String id) {
		logger.info("[addFaultOrderForDeviceDeletion()->success: " + MessageConstant.ADD_SUCCESS + "]");
		String time = jsonObject.getString(DeviceDeletionConstants.CONFIG_TIME);
		String deviceTypeName = jsonObject.getString(DeviceDeletionConstants.DEVICE_TYPE_NAME);
		List<String> userList = Lists.newArrayList();
		userList.add(userId);
		String message = "【超时未上报】监测到【 " + deviceTypeName + "】【 "+ deviceId + "】超过【" + time + "】小时未上报状态，请核查 ";
		JiguangEntity jiguangEntity = new JiguangEntity();
		jiguangEntity.setAliasIds(userList);
		jiguangEntity.setNotificationTitle("【超时未上报】");
		jiguangEntity.setMsgContent(message);
		jiguangEntity.setMsgTitle("【超时未上报】");
		RpcResponse<Object> sendMessage = jiguangService.sendMessage(jiguangEntity);
		if (sendMessage.isSuccess()) {
			logger.info("addFaultOrderForDeviceDeletion()-->" + sendMessage.getMessage() + "<-->" + message);
		} else {
			logger.error("addFaultOrderForDeviceDeletion()-->消息推送失败,内容为:" + message);
		}
		
		return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.ADD_SUCCESS, id);
	}


	private RpcResponse<String> panduan1(String deviceId, List<String> successValue) throws Exception {
		ArrayList<Object> successUpdate = Lists.newArrayList();
		for (String orderId : successValue) {
			logger.info("编号为" + deviceId + "的设备存在超时未上报故障工单,进行更新");
			HashMap<String, Object> params = Maps.newHashMap();
			params.put(DeviceDeletionConstants.FAULT_ORDER_ID, orderId);
			params.put("updateTime", DateUtils.formatDate(new Date()));
			int updateFaultOrder = faultOrderProcessCudRepository.updateFaultOrder(params);
			if (updateFaultOrder > 0) {
				logger.info("addFaultOrderForDeviceDeletion()-->超时未上报故障工单" + orderId + "更新成功");
				/*String time = jsonObject.getString(DeviceDeletionConstants.CONFIG_TIME);
				String deviceTypeName = jsonObject.getString(DeviceDeletionConstants.DEVICE_TYPE_NAME);
				List<String> userList = Lists.newArrayList();
				userList.add(userId);
				String message = "【超时未上报】监测到【 " + deviceTypeName + "】【 "+ deviceId + "】超过【" + time + "】小时未上报状态，请核查 ";
				JiguangEntity jiguangEntity = new JiguangEntity();
				jiguangEntity.setAliasIds(userList);
				jiguangEntity.setNotificationTitle("【超时未上报】");
				jiguangEntity.setMsgContent(message);
				jiguangEntity.setMsgTitle("【超时未上报】");
				RpcResponse<Object> sendMessage = jiguangService.sendMessage(jiguangEntity);
				if (sendMessage.isSuccess()) {
					logger.info(sendMessage.getMessage() + "<-->" + message);
				} else {
					logger.error("消息推送失败,内容为:" + message);
				}
				*/
				successUpdate.add(orderId);
			} else {
				logger.error("addFaultOrderForDeviceDeletion()-->超时未上报故障工单更新失败,id:" + orderId);
				// return
				// RpcResponseBuilder.buildErrorRpcResp("超时故障工单更新失败");
			}
		}
		logger.info("addFaultOrderForDeviceDeletion()-->超时未上报故障工单更新成功");
		return RpcResponseBuilder.buildSuccessRpcResp("超时未上报故障工单更新成功", successUpdate.toString());
	}


	@Override
	public RpcResponse<String> undoTimeoutFaultOrder(JSONObject orderInfo) {
		logger.info(String.format("[undoTimeoutFaultOrder()->request params:%s]", orderInfo));
		try {
			String id = orderInfo.getString(DeviceDeletionConstants.FAULT_ORDER_ID);
			if (StringUtils.isBlank(id)) {
				logger.error("[updateFaultOrderState()->invalid：故障工单id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("故障工单id不能为空!");
			}
			String processId = orderInfo.getString(DeviceDeletionConstants.PROCESS_ID);
			if (StringUtils.isBlank(processId)) {
				logger.error("[updateFaultOrderState()->invalid：工单流程id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("工单流程id不能为空!");
			}
			String userId = orderInfo.getString(DeviceDeletionConstants.USER_ID);
			if (StringUtils.isBlank(userId)) {
				logger.error("[updateFaultOrderState()->invalid：用户id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空!");
			}
			String accessSecret = orderInfo.getString(DeviceDeletionConstants.ACCESS_SECRET);
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[updateFaultOrderState()->invalid：接入方秘钥不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
			}
			JSONObject json = new JSONObject();
			json.put(DeviceDeletionConstants.PROCESS_ID, processId);
			// 调用工作流判断是否能撤回
			RpcResponse<Map<String, Object>> processState = activityProgressQuery.getJudgmentProcess(json);
			if (!processState.isSuccess()) {
				logger.error("[undoTimeoutFaultOrder()->error: " + MessageConstant.SEARCH_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(processState.getMessage());
			}
			if (processState.getSuccessValue() == null || !processState.getSuccessValue().containsKey(TimeoutReportConfigConstants.STATE)) {
				logger.error("[undoTimeoutFaultOrder()->error: " + MessageConstant.SEARCH_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(processState.getMessage());
			}
			if (!(Boolean) processState.getSuccessValue().get(TimeoutReportConfigConstants.STATE)) {
				logger.error("[undoTimeoutFaultOrder()-->error: " + MessageConstant.SEARCH_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp("此流程已撤回或已审核,不能撤回!请刷新当前页面!");
			}
			// 调用工作流更新工单状态
			json.putAll(orderInfo);
			RpcResponse<Map<String, Object>> result = activityProgressCrud.retreatProgress(json);
			if (!result.isSuccess() || result.getSuccessValue().get(TimeoutReportConfigConstants.STATE).equals(false)) {
		        logger.error("[undoTimeoutFaultOrder()->error: " + MessageConstant.UPDATE_FAIL + "]");
		        return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		      }
			// 根据流程id查询当前工单状态
			RpcResponse<Map<String, Object>> response = activityProgressQuery.getProcessStatus(json);
			Map<String, Object> statusMap = response.getSuccessValue();
			// 根据id更新数据库工单状态
			if (null != statusMap && statusMap.size() > 0) {
				String status = statusMap.get("status").toString();
				Map<String, Object> map = Maps.newHashMap();
				String orderId = orderInfo.getString("id");
				//故障工单id
				map.put(DeviceDeletionConstants.FAULT_ORDER_ID, orderId); 
				map.put("processState", status);
				map.put("updateTime", DateUtils.formatDate(new Date()));
				String queryMark = faultOrderProcessCudRepository.queryMark(orderId);
				String newMark = queryMark + "(此设备在" + DateUtils.formatDate(new Date()) + "系统检测时,检测到上报,且工单未处理,故撤回.)";
				map.put("mark", newMark);
				int model = faultOrderProcessCudRepository.updateFaultOrder(map);
				if (model > 0) {
					logger.info("[undoTimeoutFaultOrder()->success: " + MessageConstant.UPDATE_SUCCESS + "]");
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS,
							orderInfo.getString(SimpleOrderConstants.ID));
				}
				logger.error("[undoTimeoutFaultOrder()->error: " + MessageConstant.UPDATE_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			} else {
				logger.error("[undoTimeoutFaultOrder()->error: " + MessageConstant.UPDATE_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("undoTimeoutFaultOrder()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}

}
