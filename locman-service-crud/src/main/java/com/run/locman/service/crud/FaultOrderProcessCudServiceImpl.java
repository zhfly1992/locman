/*
 * File name: FaultOrderProcessCudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tm 2017年10月20日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import com.run.locman.api.crud.repository.FaultOrderDeviceCudRepository;
import com.run.locman.api.crud.repository.FaultOrderProcessCudRepository;
import com.run.locman.api.crud.service.AlarmInfoCrudService;
import com.run.locman.api.crud.service.DeviceInfoCudService;
import com.run.locman.api.crud.service.FacilitiesCrudService;
import com.run.locman.api.crud.service.FaultOrderProcessCudService;
import com.run.locman.api.entity.AlarmInfo;
import com.run.locman.api.entity.FaultOrderDevice;
import com.run.locman.api.entity.FaultOrderProcessType;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.query.service.FaultOrderProcessQueryService;
import com.run.locman.api.query.service.FaultOrderProcessTypeQueryService;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.AlarmOrderConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceDeletionConstants;
import com.run.locman.constants.FaultOrderCountContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.SimpleOrderConstants;

/**
 * @Description: 故障工单cud实现类
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
@Transactional(rollbackFor = Exception.class)
public class FaultOrderProcessCudServiceImpl implements FaultOrderProcessCudService {

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FaultOrderProcessCudRepository		faultOrderProcessCudRepository;

	@Autowired
	private ActivityProgressCrud				activityProgressCrud;

	@Autowired
	private ActivityProgressQuery				activityProgressQuery;

	@Autowired
	private FaultOrderDeviceCudRepository		faultOrderDeviceCudRepository;

	@Autowired
	private OrderProcessQueryService			orderProcessQueryService;

	@Autowired
	private FaultOrderProcessTypeQueryService	faultOrderProcessTypeQueryService;

	@Autowired
	private AlarmOrderQueryService				alarmOrderQueryService;

	@Autowired
	private AlarmInfoCrudService				alarmInfoCrudService;

	@Autowired
	private AlarmInfoQueryService				alarmInfoQueryService;

	@Autowired
	private FaultOrderProcessQueryService		faultOrderProcessQueryService;

	@Autowired
	private DeviceQueryService					deviceQueryService;

	@Autowired
	private DeviceInfoCudService				deviceInfoCudService;
	
	@Autowired
	private FacilitiesCrudService				facilitiesCrudService;
	
	@Autowired
	private FacilitiesQueryService				facilitiesQueryService;



	@Override
	@SuppressWarnings("unchecked")
	public RpcResponse<JSONObject> addOrUpdateFaultOrder(JSONObject jsonObject) {
		RpcResponse<JSONObject> checkParmer = checkParmer(jsonObject);
		if (null != checkParmer) {
			return checkParmer;
		}
		try {
			String id = ("".equals(jsonObject.getString("id")) || jsonObject.getString("id") == null)
					? UtilTool.getUuId() : jsonObject.getString("id");
			String accessSecret = jsonObject.get("accessSecret").toString();

			List<String> deviceIdsAdd = (List<String>) jsonObject.get("deviceIdsAdd");

			List<String> deviceIdsDel = (List<String>) jsonObject.get("deviceIdsDel");
			Integer faultType = jsonObject.getInteger("faultType");
			String userId = jsonObject.getString("userId");
			String faultProcessType = jsonObject.getString("faultProcessType");
			String type = jsonObject.getString("type");
			// 组装查询参数集合
			Map<String, Object> params = getParmerMap(jsonObject, id, accessSecret, faultType, userId);
			// 查询该接入方下的所有工单类型
			RpcResponse<JSONObject> checkOrderType = checkOrderType(faultProcessType, params);
			if (null != checkOrderType) {
				return checkOrderType;
			}
			JSONObject json = new JSONObject();
			// 启动流程
			RpcResponse<JSONArray> startProgress = startProgress(jsonObject, accessSecret, faultProcessType, json);
			if (!startProgress.isSuccess()) {
				return RpcResponseBuilder.buildErrorRpcResp(startProgress.getMessage());
			}
			JSONArray jsonArray = startProgress.getSuccessValue();
			RpcResponse<Map<String, Object>> startProgressResult = startProcess(jsonObject, id, accessSecret, userId,
					jsonArray, json);
			if (!startProgressResult.isSuccess()) {
				return RpcResponseBuilder.buildErrorRpcResp(startProgressResult.getMessage());
			}
			String processId = startProgressResult.getSuccessValue().get("processId").toString();

			// 完成第一个节点
			RpcResponse<JSONObject> completeTheFirstNode = completeTheFirstNode(params, json, startProgressResult,
					processId);
			if (null != completeTheFirstNode) {
				return completeTheFirstNode;
			}

			// 判断是添加还是修改故障工单
			if (type != null) {
				switch (type) {
				// 添加故障工单
				case "add":
					// 向故障工单和设备绑定关系表添加数据
					return addFaultOrder(id, deviceIdsAdd, params, processId);
				// 修改故障工单
				case "update":
					return updateFaultOrder(jsonObject, id, deviceIdsAdd, deviceIdsDel, params);
				default:
					logger.error("addOrUpdateFaultOrder()->添加或修改故障工单时类型传入值为空");
					return RpcResponseBuilder.buildErrorRpcResp("添加或修改故障工单时类型传入值为空");
				}
			}
			logger.error("addOrUpdateFaultOrder()->更新故障工单失败");
			return RpcResponseBuilder.buildErrorRpcResp("更新故障工单失败");
		} catch (Exception e) {
			logger.error("addOrUpdateFaultOrder()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<JSONArray> startProgress(JSONObject jsonObject, String accessSecret, String faultProcessType,
			JSONObject json) {
		JSONArray jsonArray = null;
		
		// faultProcessType:1表示告警转故障的流程类型 2表示故障流程类型
		if (DeviceDeletionConstants.CODE_TWO.equals(faultProcessType) || DeviceDeletionConstants.CODE_THREE.equals(faultProcessType)) {
			json.put("type", "manualFailureProcess");
			// 根据组织id查询用户节点信息集合
			Map<String, Object> map = Maps.newHashMap();
			map.put(SimpleOrderConstants.ORGANIZEID, jsonObject.getString(SimpleOrderConstants.ORGANIZEID));
			map.put("processType", "故障工单流程");
			map.put("accessSecret", accessSecret);
			RpcResponse<JSONArray> nodeInfoResponse = orderProcessQueryService.queryNodeInfoForActivity(map);
			if (nodeInfoResponse.isSuccess()) {
				logger.info(nodeInfoResponse.getMessage());
				jsonArray = nodeInfoResponse.getSuccessValue();
				if (jsonArray.isEmpty()) {
					logger.error("addOrUpdateFaultOrder()-->请求人员非法操作!");
					return RpcResponseBuilder.buildErrorRpcResp("请求人员非法操作!");
				}
			} else {
				logger.info(nodeInfoResponse.getMessage());
				return RpcResponseBuilder.buildErrorRpcResp(nodeInfoResponse.getMessage());
			}
		} else {
			json.put("type", "alarmFailureProcess");
			// 根据组织id查询用户节点信息集合
			Map<String, Object> map = Maps.newHashMap();
			map.put(SimpleOrderConstants.ORGANIZEID, jsonObject.getString(SimpleOrderConstants.ORGANIZEID));
			map.put("processType", "告警转故障工单流程");
			map.put("accessSecret", accessSecret);
			RpcResponse<JSONArray> nodeInfoResponse = orderProcessQueryService.queryNodeInfoForActivity(map);
			if (nodeInfoResponse.isSuccess()) {
				logger.info(nodeInfoResponse.getMessage());
				jsonArray = nodeInfoResponse.getSuccessValue();
				if (jsonArray.isEmpty()) {
					return RpcResponseBuilder.buildErrorRpcResp("当前人员也没有权限！->请确认节点上是否存在统一组织处理人！");
				}
			} else {
				logger.info(nodeInfoResponse.getMessage());
				return RpcResponseBuilder.buildErrorRpcResp(nodeInfoResponse.getMessage());
			}
		}
		return RpcResponseBuilder.buildSuccessRpcResp("", jsonArray);
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<Map<String, Object>> startProcess(JSONObject jsonObject, String id, String accessSecret,
			String userId, JSONArray jsonArray, JSONObject json) throws Exception {
		json.put(SimpleOrderConstants.ID, id);
		json.put(SimpleOrderConstants.USERID, userId);
		json.put(SimpleOrderConstants.ACTIVITY_USEROBJ, jsonArray);
		json.put(SimpleOrderConstants.ACCESSSECRET, jsonObject.getString("accessSecret"));

		// 查询计算工单号
		RpcResponse<String> res = faultOrderProcessQueryService.findFaultOrderNumber(accessSecret);
		if (!res.isSuccess()) {
			logger.error(String.format("[addFaultOrder()->error:%s---%s]", res.getException(), res.getMessage()));
		} else {
			json.put(SimpleOrderConstants.ORDER_NUM, res.getSuccessValue());
		}

		RpcResponse<Map<String, Object>> startProgressResult = activityProgressCrud.startProgress(json);
		if (!startProgressResult.isSuccess()) {
			logger.error("[addFaultOrder->add()->invalid: " + startProgressResult.getMessage() + "]");
			return RpcResponseBuilder.buildErrorRpcResp(startProgressResult.getMessage());
		}
		if (startProgressResult.getSuccessValue() == null) {
			logger.error("[addFaultOrder->add()->invalid: 流程开启失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("流程开启失败!");
		}
		return startProgressResult;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<JSONObject> completeTheFirstNode(Map<String, Object> params, JSONObject json,
			RpcResponse<Map<String, Object>> startProgressResult, String processId) {
		json.put("processId", processId);
		RpcResponse<Map<String, Object>> acceptProgress = activityProgressCrud.acceptProgress(json);
		if (!acceptProgress.isSuccess()) {
			logger.error(String.format("[addFaultOrder()->error: %s", acceptProgress.getMessage()));
			return RpcResponseBuilder.buildErrorRpcResp(acceptProgress.getMessage());
		}
		JSONObject json1 = new JSONObject();
		json1.put("processId", processId);
		RpcResponse<Map<String, Object>> queryProgressResult = activityProgressQuery.getProcessStatus(json1);
		if (!queryProgressResult.isSuccess()) {
			logger.error("[addFaultOrder->add()->invalid: " + startProgressResult.getMessage() + "]");
			return RpcResponseBuilder.buildErrorRpcResp(startProgressResult.getMessage());
		}
		if (queryProgressResult.getSuccessValue() == null) {
			logger.error("[addFaultOrder->add()->invalid: 查询流程状态失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("查询流程状态失败!");
		}
		String processState = queryProgressResult.getSuccessValue().get("status").toString();
		params.put("processState", processState);
		return null;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<JSONObject> checkOrderType(String faultProcessType, Map<String, Object> params) {
		RpcResponse<List<FaultOrderProcessType>> listRpcResponse = faultOrderProcessTypeQueryService
				.getFaultOrderType();
		if (listRpcResponse.isSuccess() && !listRpcResponse.getSuccessValue().isEmpty()) {
			for (FaultOrderProcessType faultOrderProcessType : listRpcResponse.getSuccessValue()) {
				// 判断添加工单的工单类型是否属于该接入方下存在的工单类型
				if (faultOrderProcessType.getId().equals(faultProcessType)) {
					params.put("faultProcessType", faultProcessType);
				}
			}
			if (!params.containsKey(DeviceDeletionConstants.FAULT_PROCESS_TYPE)) {
				logger.error("该接入方下未找到此工单类型!");
				return RpcResponseBuilder.buildErrorRpcResp("该接入方下未找到此工单类型!");
			}
		} else {
			// 判断该接入方下是否存在相关的工单类型基础表数据
			logger.error("该接入方下未找到任何相关的工单类型!");
			return RpcResponseBuilder.buildErrorRpcResp("该接入方下未找到任何相关的工单类型!");
		}
		return null;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<JSONObject> addFaultOrder(String id, List<String> deviceIdsAdd, Map<String, Object> params,
			String processId) throws Exception {
		if (deviceIdsAdd != null && deviceIdsAdd.size() > 0) {
			for (int i = 0; i < deviceIdsAdd.size(); i++) {
				FaultOrderDevice faultOrderDevice = new FaultOrderDevice();
				faultOrderDevice.setId(UtilTool.getUuId());
				faultOrderDevice.setDeviceId(deviceIdsAdd.get(i));
				faultOrderDevice.setFaultOrderId(id);
				faultOrderDeviceCudRepository.addBindDevices(faultOrderDevice);
			}
		}
		params.put("createTime", DateUtils.formatDate(new Date()));
		params.put("updateTime", DateUtils.formatDate(new Date()));
		params.put("processId", processId);
		int addNum = faultOrderProcessCudRepository.addFaultOrder(params);
		if (addNum > 0) {
			JSONObject result = new JSONObject();
			result.put("processId", processId);
			result.put("faultOrderId", id);
			logger.info("[addFaultOrder()->success: " + MessageConstant.ADD_SUCCESS + "]");
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.ADD_SUCCESS, result);
		} else {
			logger.error("[addFaultOrder()->error: " + MessageConstant.ADD_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<JSONObject> updateFaultOrder(JSONObject jsonObject, String id, List<String> deviceIdsAdd,
			List<String> deviceIdsDel, Map<String, Object> params) throws Exception {
		if (StringUtils.isBlank(DeviceDeletionConstants.PROCESS_ID)) {
			logger.error("[simpleOrderAdd()->invalid：工单流程id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单流程id不能为空!");
		}

		// 验证单子是否审核过,审核过不能修改
		JSONObject jsonParam = new JSONObject();
		jsonParam.put(SimpleOrderConstants.PROCESSID, jsonObject.getString("processId"));
		RpcResponse<Map<String, Object>> result = activityProgressQuery.getJudgmentProcess(jsonParam);
		if (result.isSuccess() && DeviceDeletionConstants.FALSE
				.equals(result.getSuccessValue().get(DeviceDeletionConstants.STATE).toString())) {
			return RpcResponseBuilder.buildErrorRpcResp("该工单已审核过,不能修改!");
		} else if (!result.isSuccess()) {
			logger.error("addOrUpdateFaultOrder()->调用工作流,获取是否审核过工单状态失败!");
			return RpcResponseBuilder.buildErrorRpcResp("调用工作流,获取是否审核过工单状态失败!");
		}
		params.put("id", id);
		params.put("updateTime", DateUtils.formatDate(new Date()));
		// 向故障工单和设备绑定关系表删除数据
		if (deviceIdsDel != null && deviceIdsDel.size() > 0) {
			for (int i = 0; i < deviceIdsDel.size(); i++) {
				Map<String, Object> delParam = Maps.newHashMap();
				delParam.put("deviceId", deviceIdsDel.get(i));
				delParam.put("faultOrderId", id);
				faultOrderDeviceCudRepository.delBindDevices(delParam);
			}
		}
		// 向故障工单和设备绑定关系表添加数据
		if (deviceIdsAdd != null && deviceIdsAdd.size() > 0) {
			for (int j = 0; j < deviceIdsAdd.size(); j++) {
				FaultOrderDevice faultOrderDevice = new FaultOrderDevice();
				faultOrderDevice.setId(UtilTool.getUuId());
				faultOrderDevice.setDeviceId(deviceIdsAdd.get(j));
				faultOrderDevice.setFaultOrderId(id);
				faultOrderDeviceCudRepository.addBindDevices(faultOrderDevice);
			}
		}
		int updateNum = faultOrderProcessCudRepository.updateFaultOrder(params);
		if (updateNum > 0) {
			JSONObject resultJson = new JSONObject();
			resultJson.put("processId", jsonObject.getString("processId"));
			resultJson.put("faultOrderId", id);
			logger.info("[updateFaultOrder()->success: " + MessageConstant.UPDATE_SUCCESS + "]");
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, resultJson);
		} else {
			logger.error("[updateFaultOrder()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private Map<String, Object> getParmerMap(JSONObject jsonObject, String id, String accessSecret, Integer faultType,
			String userId) {
		String manager = jsonObject.getString("manager");
		Integer deviceCount = jsonObject.getInteger("deviceCount");
		String phone = jsonObject.getString("phone");
		String mark = jsonObject.getString("mark");
		String factoryId = jsonObject.getString("factoryId");
		String orderImg = jsonObject.getString("orderImg");
		String orderName = jsonObject.getString("orderName");

		Map<String, Object> params = Maps.newHashMap();
		params.put("accessSecret", accessSecret);
		params.put("faultType", faultType);
		params.put("manager", manager);
		params.put("deviceCount", deviceCount);
		params.put("factoryId", factoryId);
		params.put("phone", phone);
		params.put("mark", mark);
		params.put("orderName", orderName);
		params.put("orderImg", orderImg);
		params.put("userId", userId);
		params.put("id", id);
		return params;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<JSONObject> checkParmer(JSONObject jsonObject) {
		if (StringUtils.isBlank(jsonObject.getString(FaultOrderCountContants.ACCESS_SECRET))) {
			logger.error("[addFaultOrder()->invalid：接入方密钥不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(FaultOrderCountContants.FAULT_TYPE))) {
			logger.error("[addFaultOrder()->invalid：faultType不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("faultType不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(FaultOrderCountContants.MANAGER))) {
			logger.error("[addFaultOrder()->invalid：manager不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("manager不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.DEVICE_COUNT))) {
			logger.error("[addFaultOrder()->invalid：deviceCount不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("deviceCount不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.PHONE))) {
			logger.error("[addFaultOrder()->invalid：phone不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("phone不能为空！");
		}
		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.USER_ID))) {
			logger.error("[addFaultOrder()->invalid：userId不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("userId不能为空！");
		}
//		if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.FACTORY_ID))) {
//			logger.error("[addFaultOrder()->invalid：厂家Id不能为空！]");
//			return RpcResponseBuilder.buildErrorRpcResp("厂家Id不能为空！");
//		}
		if (StringUtils.isBlank(jsonObject.getString(SimpleOrderConstants.ORGANIZEID))) {
			logger.error("[simpleOrderAdd()->invalid：增加还是修改组织id不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("增加还是修改组织id不能为空！");
		}
		
		String type = jsonObject.getString("type");
		boolean flag = StringUtils.isNotBlank(type) && "add".equals(type) && (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.DEVICE_IDS_ADD))
				|| jsonObject.getJSONArray("deviceIdsAdd").isEmpty());
		if (flag) {
			logger.error("[simpleOrderAdd()->invalid：deviceIdsAdd设备id集合数据格式错误！]");
			return RpcResponseBuilder.buildErrorRpcResp("设备id集合数据格式错误！");
		}
		/*if (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.DEVICE_IDS_ADD))
				|| jsonObject.getJSONArray("deviceIdsAdd").isEmpty()) {
			logger.error("[simpleOrderAdd()->invalid：deviceIdsAdd设备id集合数据格式错误！]");
			return RpcResponseBuilder.buildErrorRpcResp("设备id集合数据格式错误！");
		}*/
		logger.info(String.format("[addOrUpdateFaultOrder()->进入方法,参数:%s]", jsonObject));
		return null;
	}



	@Override
	public RpcResponse<String> updateFaultOrderState(JSONObject orderInfo) {
		try {
			RpcResponse<String> checkResult = checkUpdateFaultorderParmer(orderInfo);
			if (null != checkResult) {
				return checkResult;
			}

			JSONObject json = new JSONObject();
			json.put("processId", orderInfo.getString("processId"));
			orderInfo.remove("processId");
			// 通过工单id查询设备id
			List<String> findDeviceIdByFaultOrderId = faultOrderProcessCudRepository
					.findDeviceIdByFaultOrderId(orderInfo.getString(PublicConstants.ID));

			if (DeviceDeletionConstants.WITHDRAW.equals(orderInfo.getString(DeviceDeletionConstants.OPERATIONTYPE))) {
				//撤回时
				RpcResponse<String> withdraw = withdraw(orderInfo, json);
				if (null != withdraw) {
					return withdraw;
				}

			} else if (DeviceDeletionConstants.COMPLETE
					.equals(orderInfo.getString(DeviceDeletionConstants.OPERATIONTYPE))
					|| DeviceDeletionConstants.PASS
							.equals(orderInfo.getString(DeviceDeletionConstants.OPERATIONTYPE))) {

				RpcResponse<String> completeOrPass = completeOrPass(orderInfo, json, findDeviceIdByFaultOrderId);
				if (null != completeOrPass) {
					return completeOrPass;
				}

			} else if (DeviceDeletionConstants.REFUSE.equals(orderInfo.getString(DeviceDeletionConstants.OPERATIONTYPE))
					|| DeviceDeletionConstants.RETURN_TO_FACTORY
							.equals(orderInfo.getString(DeviceDeletionConstants.OPERATIONTYPE))) {

				RpcResponse<String> refuseOrReturn2Factory = refuseOrReturn2Factory(orderInfo, json, findDeviceIdByFaultOrderId);
				if (null != refuseOrReturn2Factory) {
					return refuseOrReturn2Factory;
				}

			}
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		} catch (Exception e) {
			logger.error("updateFaultOrderState()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> refuseOrReturn2Factory(JSONObject orderInfo, JSONObject json, List<String> findDeviceIdByFaultOrderId)
			throws Exception {
		if (StringUtils.isBlank(orderInfo.getString(DeviceDeletionConstants.DETAIL))) {
			logger.error("[updateFaultOrderState()->invalid：操作理由不能空!]");
			return RpcResponseBuilder.buildErrorRpcResp("操作理由不能空!");
		}
		String operationType = orderInfo.getString(DeviceDeletionConstants.OPERATIONTYPE);
		orderInfo.remove(DeviceDeletionConstants.OPERATIONTYPE);
		json.put(CommonConstants.USERID, orderInfo.getString(CommonConstants.USERID));
		orderInfo.remove(CommonConstants.USERID);
		json.putAll(orderInfo);
		// 调用工作流更新工单状态
		RpcResponse<Map<String, Object>> result = activityProgressCrud.rejectProgress(json);
		if (!result.isSuccess()) {
			logger.error("[updateFaultOrderState()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		}

		// 根据流程id查询当前工单状态
		RpcResponse<Map<String, Object>> response = activityProgressQuery.getProcessStatus(json);
		Map<String, Object> statusMap = response.getSuccessValue();

		// 根据id更新数据库工单状态
		if (null != statusMap && statusMap.size() > 0) {
			String status = statusMap.get("status").toString();
			// 参数封装
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", orderInfo.getString("id"));
			map.put("processState", status);
			map.put("updateTime", DateUtils.formatDate(new Date()));
			int model = faultOrderProcessCudRepository.updateFaultOrder(map);

			// 返厂的话 修改该工单关于的设备 修改为维护状态
			Integer updateDeviceDefendState = null;
			if (DeviceDeletionConstants.RETURN_TO_FACTORY.equals(operationType)) {
				updateDeviceDefendState = updateDeviceDefendState(operationType, orderInfo.getString("id"),
						findDeviceIdByFaultOrderId);
				if (model > 0 && updateDeviceDefendState > 0) {
					logger.info("[updateFaultOrderState()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS,
							orderInfo.getString("id"));
				}
			}

			if (model > 0) {
				logger.info("[updateFaultOrderState()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS,
						orderInfo.getString("id"));
			}

			logger.error("[updateFaultOrderState()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		}
		return null;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> completeOrPass(JSONObject orderInfo, JSONObject json, List<String> findDeviceIdByFaultOrderId)
			throws Exception {
		/*
		 * 开始检查设备状态并更新告警信息表中的idDel字段 faultProcessType：1表示告警转故障的流程类型
		 * 2表示故障流程类型 厂家确认完成操作（如果是告警转故障的工单需要验证设备状态是否正常）
		 */
		if (DeviceDeletionConstants.CODE_ONE
				.equals(orderInfo.getString(DeviceDeletionConstants.FAULT_PROCESS_TYPE))
				&& orderInfo.getString(DeviceDeletionConstants.OPERATIONTYPE)
						.equals(DeviceDeletionConstants.COMPLETE)) {

			RpcResponse<String> alarm2Fault = alarm2Fault(orderInfo);
			if (null != alarm2Fault) {
				return alarm2Fault;
			}
		}

		// 审核点击通过时校验，该设备是否已经存在故障工单
		if (orderInfo.getString(DeviceDeletionConstants.OPERATIONTYPE).equals(DeviceDeletionConstants.PASS)) {
			RpcResponse<List<String>> rpcFauOrderDeviceIds = faultOrderProcessQueryService
					.findFaultOrderByDeviceId(findDeviceIdByFaultOrderId);
			if (!rpcFauOrderDeviceIds.isSuccess()) {
				logger.info("[updateFaultOrderState()->error:]" + rpcFauOrderDeviceIds.getMessage());
				return RpcResponseBuilder.buildErrorRpcResp(rpcFauOrderDeviceIds.getMessage());
			}
			List<String> deviceIds = rpcFauOrderDeviceIds.getSuccessValue();
			if (!deviceIds.isEmpty()) {
				logger.info("[orderPowerless()->info:]" + deviceIds.toString());
				return RpcResponseBuilder.buildErrorRpcResp("设备已存在通过审核的故障工单，请拒绝！");
			}
		}

		// 先获取出来 下面用来做修改设备状态判断依据
		String operationType = orderInfo.getString("operationType");
		// 调用工作流
		json.put("userId", orderInfo.getString("userId"));
		orderInfo.remove("userId");
		orderInfo.remove("operationType");
		json.putAll(orderInfo);
		// 调用工作流更新工单状态
		RpcResponse<Map<String, Object>> result = activityProgressCrud.acceptProgress(json);
		if (!result.isSuccess() || result.getSuccessValue().get(DeviceDeletionConstants.STATE).equals(false)) {
			logger.error("[updateFaultOrderState()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		}

		// 根据流程id查询当前工单状态
		RpcResponse<Map<String, Object>> response = activityProgressQuery.getProcessStatus(json);
		Map<String, Object> statusMap = response.getSuccessValue();

		// 根据id更新数据库工单状态
		if (null != statusMap && statusMap.size() > 0) {
			String status = statusMap.get("status").toString();
			// 参数封装
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", orderInfo.getString("id"));
			map.put("processState", status);
			map.put("updateTime", DateUtils.formatDate(new Date()));
			int model = faultOrderProcessCudRepository.updateFaultOrder(map);
			// 通过工单id和当前是审核通过还是厂家确认，修改与该工单关联的设备
			Integer updateDeviceDefendState = updateDeviceDefendState(operationType,
					orderInfo.getString(PublicConstants.ID), findDeviceIdByFaultOrderId);
			if (model > 0 && updateDeviceDefendState > 0) {
				logger.info("[updateFaultOrderState()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS,
						orderInfo.getString("id"));
			}
			logger.error("[updateFaultOrderState()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		}
		return null;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> alarm2Fault(JSONObject orderInfo) throws Exception {
		// 获取告警转故障工单的工单id
		String orderId = orderInfo.getString("id");
		// 根据工单id查询工单信息,告警的规则和设备id
		RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService
				.getAlarmOrderInfoByFaultOrderId(orderId);
		if (!alarmOrderInfoById.isSuccess() || alarmOrderInfoById.getSuccessValue() == null) {
			logger.error("[updateFaultOrderState()->未查到工单信息!]");
			return RpcResponseBuilder.buildErrorRpcResp("未查到工单信息!");
		}
		Map<String, Object> successValue = alarmOrderInfoById.getSuccessValue();
		// 得到设备id，校验设备状态是否真实完成
		String deviceId = (String) successValue.get(AlarmOrderConstants.DEVICE_ID);

		RpcResponse<JSONObject> deviceLastState = deviceQueryService.queryDeviceLastState(deviceId);
		if (!deviceLastState.isSuccess() || null == deviceLastState.getSuccessValue()) {
			logger.error("[updateFaultOrderState()->查询设备当前状态失败!或该设备当前状态为空值！]");
			return RpcResponseBuilder.buildErrorRpcResp("查询设备当前状态失败!或该设备当前状态为空值！");
		}
		JSONObject jsonObject = deviceLastState.getSuccessValue();
		JSONObject reported = UtilTool.getReported(jsonObject);

		Map<String, Object> deviceRealState = Maps.newHashMap();
		// 解析上报数据
		if (reported != null) {
			Set<String> keySet = reported.keySet();
			for (String propName : keySet) {
				deviceRealState.put(propName, reported.get(propName));
			}
		}
		deviceRealState.put(AlarmInfoConstants.DEVICEID, deviceId);
		deviceRealState.put(AlarmInfoConstants.ENGINE_CHECK, new Boolean(true));
		// 在确定设备正常后，更新告警信息表中isDel字段为4（4表示正常告警流程处理完成）
		RpcResponse<Map<String, Object>> mapRpcResponse = alarmOrderQueryService
				.getAlarmOrderInfoByFaultOrderId(orderId);
		if (!mapRpcResponse.isSuccess() || mapRpcResponse.getSuccessValue() == null) {
			logger.info("updateFaultOrderState()->告警信息查询失败!");
			return RpcResponseBuilder.buildErrorRpcResp("告警信息查询失败!");
		} else {
			// 根据告警工单id查询对应告警信息，然后在获取这一类告警信息下相同的设备id
			RpcResponse<AlarmInfo> alarmInfoRpcResponse = alarmInfoQueryService
					.findById(String.valueOf(mapRpcResponse.getSuccessValue().get("alarmId")));

			if (!alarmInfoRpcResponse.isSuccess() && null == alarmInfoRpcResponse.getSuccessValue()) {
				logger.error("updateFaultOrderState()->告警信息查询失败!");
				return RpcResponseBuilder.buildErrorRpcResp(alarmInfoRpcResponse.getMessage());
			} else {
				Map<String, Object> map1 = Maps.newHashMap();
				map1.put("deviceId", alarmInfoRpcResponse.getSuccessValue().getDeviceId());

				// 修改告警信息状态
				List<String> findAlarmDescByFaultOrderId = findAlarmDescByFaultOrderId(orderId);
				map1.put("alarmDesc", findAlarmDescByFaultOrderId);

				map1.put("alarmLevel", alarmInfoRpcResponse.getSuccessValue().getAlarmLevel());
				// 4表示告警转故障已处理的状态
				map1.put("isDel", 4);
				RpcResponse<String> stringRpcResponse = alarmInfoCrudService.updateTheDelByCondition(map1);
				if (stringRpcResponse.isSuccess()) {
					logger.info("updateFaultOrderState()->设备告警状态信息更新成功!");
				} else {
					logger.error("updateFaultOrderState()->设备告警状态信息更新失败!");
					return RpcResponseBuilder.buildErrorRpcResp(stringRpcResponse.getMessage());
				}

			}
		}
		return null;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> withdraw(JSONObject orderInfo, JSONObject json) throws Exception {
		// 调用工作流判断是否能撤回
		RpcResponse<Map<String, Object>> processState = activityProgressQuery.getJudgmentProcess(json);
		if (!processState.isSuccess()) {
			logger.error("[updateSimpleOrderState()->error: " + MessageConstant.SEARCH_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(processState.getMessage());
		}
		if (processState.getSuccessValue() == null
				|| !processState.getSuccessValue().containsKey(DeviceDeletionConstants.STATE)) {
			logger.error("[updateSimpleOrderState()->error: " + MessageConstant.SEARCH_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(processState.getMessage());
		}
		if (!(Boolean) processState.getSuccessValue().get(DeviceDeletionConstants.STATE)) {
			logger.error("此流程已撤回或已审核,不能撤回!请刷新当前页面!");
			return RpcResponseBuilder.buildErrorRpcResp("此流程已撤回或已审核,不能撤回!请刷新当前页面!");
		}
		// 调用工作流更新工单状态
		orderInfo.remove("operationType");
		json.putAll(orderInfo);
		RpcResponse<Map<String, Object>> result = activityProgressCrud.retreatProgress(json);
		if (!result.isSuccess()) {
			logger.error("[faultOrderAdd()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		}

		// 根据流程id查询当前工单状态
		RpcResponse<Map<String, Object>> response = activityProgressQuery.getProcessStatus(json);
		Map<String, Object> statusMap = response.getSuccessValue();

		// 根据id更新数据库工单状态
		if (null != statusMap && statusMap.size() > 0) {
			String status = statusMap.get("status").toString();
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", orderInfo.getString("id"));
			map.put("processState", status);
			map.put("updateTime", DateUtils.formatDate(new Date()));
			int model = faultOrderProcessCudRepository.updateFaultOrder(map);
			if (model > 0) {
				logger.info("[updateFaultOrderState()->success: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS,
						orderInfo.getString(SimpleOrderConstants.ID));
			}
			logger.error("[updateFaultOrderState()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		}
		return null;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> checkUpdateFaultorderParmer(JSONObject orderInfo) {
		if (StringUtils.isBlank(orderInfo.getString(CommonConstants.ID))) {
			logger.error("[updateFaultOrderState()->invalid：故障工单id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("故障工单id不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(DeviceDeletionConstants.OPERATIONTYPE))) {
			logger.error("[updateFaultOrderState()->invalid：工单操作类型不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单操作类型不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(DeviceDeletionConstants.PROCESS_ID))) {
			logger.error("[updateFaultOrderState()->invalid：工单流程id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单流程id不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(CommonConstants.USERID))) {
			logger.error("[updateFaultOrderState()->invalid：用户id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(DeviceDeletionConstants.ACCESS_SECRET))) {
			logger.error("[updateFaultOrderState()->invalid：接入方秘钥不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
		}
		logger.info(String.format("[updateFaultOrderState()->进入方法,参数:%s]", orderInfo));
		return null;
	}



	/**
	 * 
	 * @Description:修改设备维护状态
	 * @param operationType
	 * @param faultOrderId
	 * @return
	 */
	private Integer updateDeviceDefendState(String operationType, String faultOrderId,
			List<String> findDeviceIdByFaultOrderId) {
		RpcResponse<Boolean> rpc = null;
		if (DeviceDeletionConstants.COMPLETE.equals(operationType)) {
			rpc = deviceInfoCudService.updateDeviceDefendState(findDeviceIdByFaultOrderId, true);
			
			String accessSecret = null;
			List<String> facIds = Lists.newArrayList();
			for (String deviceId : findDeviceIdByFaultOrderId) {
				RpcResponse<JSONObject> facInfoRpc = facilitiesQueryService.findFacInfoByDeviceId(deviceId);
				if (!facInfoRpc.isSuccess()) {
					logger.error(String.format("[updateDeviceDefendState()-error:%s]", facInfoRpc.getMessage()));
					continue;
				}
				JSONObject facInfo = facInfoRpc.getSuccessValue();
				String id = facInfo.getString("id");
				accessSecret = facInfo.getString("accessSecret");
				facIds.add(id);
			}
			//设施defenseState状态改为1正常
			facilitiesCrudService.updateFacilitiesDenfenseState(null, facIds, null, "1", accessSecret);
			
		} else if (DeviceDeletionConstants.PASS.equals(operationType)
				|| DeviceDeletionConstants.RETURN_TO_FACTORY.equals(operationType)) {
			rpc = deviceInfoCudService.updateDeviceDefendState(findDeviceIdByFaultOrderId, false);
		}

		if (rpc == null || !rpc.isSuccess()) {
			logger.error(String.format("[updateDeviceDefendState()-error:%s]", rpc));
			return -1;
		} else {
			return 1;
		}

	}



	/**
	 * 
	 * @Description:通过id 封装告警规则名称
	 * @param faultOrderId
	 * @return
	 * @throws Exception
	 */
	private List<String> findAlarmDescByFaultOrderId(String faultOrderId) throws Exception {
		List<String> findAlarmDescByFaultOrderId = faultOrderProcessCudRepository
				.findAlarmDescByFaultOrderId(faultOrderId);
		return findAlarmDescByFaultOrderId;
	}

	/*
	 * @Override public RpcResponse<String>
	 * addFaultOrderForDeviceDeletion(JSONObject jsonObject) { if
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.
	 * ACCESS_SECRET))) {
	 * logger.error("[addFaultOrderForDeviceDeletion()->invalid：接入方密钥不能为空！]");
	 * return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！"); } if
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.
	 * FAULT_TYPE))) {
	 * logger.error("[addFaultOrderForDeviceDeletion()->invalid：faultType不能为空！]"
	 * ); return RpcResponseBuilder.buildErrorRpcResp("faultType不能为空！"); } if
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.MANAGER
	 * ))) { logger.error(
	 * "[addFaultOrderForDeviceDeletion()->invalid：manager申报人不能为空！]"); return
	 * RpcResponseBuilder.buildErrorRpcResp("manager申报人不能为空！"); } if
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.
	 * DEVICE_COUNT))) { logger.error(
	 * "[addFaultOrderForDeviceDeletion()->invalid：deviceCount不能为空！]"); return
	 * RpcResponseBuilder.buildErrorRpcResp("deviceCount不能为空！"); } if
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.PHONE))
	 * ) {
	 * logger.error("[addFaultOrderForDeviceDeletion()->invalid：phone不能为空！]");
	 * return RpcResponseBuilder.buildErrorRpcResp("phone不能为空！"); } if
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.USER_ID
	 * ))) {
	 * logger.error("[addFaultOrderForDeviceDeletion()->invalid：userId不能为空！]");
	 * return RpcResponseBuilder.buildErrorRpcResp("userId不能为空！"); } if
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.
	 * FACTORY_ID))) {
	 * logger.error("[addFaultOrderForDeviceDeletion()->invalid：厂家Id不能为空！]");
	 * return RpcResponseBuilder.buildErrorRpcResp("厂家Id不能为空！"); } if
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.
	 * ORGANIZE_ID))) {
	 * logger.error("[addFaultOrderForDeviceDeletion()->invalid：组织id不能为空！]");
	 * return RpcResponseBuilder.buildErrorRpcResp("组织id不能为空！"); } if
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.
	 * DEVICE_IDS_ADD))) { logger.error(
	 * "[addFaultOrderForDeviceDeletion()->invalid：deviceIdsAdd设备id集合数据格式错误！]");
	 * return RpcResponseBuilder.buildErrorRpcResp("设备id集合数据格式错误！"); } try {
	 * Map<String, Object> params = new HashMap<>(); String id =
	 * (StringUtils.isBlank(jsonObject.getString(DeviceDeletionConstants.
	 * FAULT_ORDER_ID))) ? UtilTool.getUuId() :
	 * jsonObject.getString(DeviceDeletionConstants.FAULT_ORDER_ID); String
	 * accessSecret =
	 * jsonObject.getString(DeviceDeletionConstants.ACCESS_SECRET); String
	 * userId = jsonObject.getString(DeviceDeletionConstants.USER_ID);
	 * 
	 * // 根据组织id查询用户节点信息集合 JSONArray jsonArray = null; // 启动流程 JSONObject json =
	 * new JSONObject(); // faultProcessType:1表示告警转故障的流程类型 2表示故障流程类型
	 * json.put("type", "manualFailureProcess"); // 根据组织id查询用户节点信息集合 Map<String,
	 * Object> map = new HashMap<>();
	 * map.put(DeviceDeletionConstants.ORGANIZE_ID,
	 * jsonObject.getString(DeviceDeletionConstants.ORGANIZE_ID));
	 * map.put("processType", "超时故障工单流程");
	 * map.put(DeviceDeletionConstants.ACCESS_SECRET, accessSecret);
	 * 
	 * RpcResponse<JSONArray> nodeInfoResponse =
	 * orderProcessQueryService.queryNodeInfoForActivity(map); if
	 * (nodeInfoResponse.isSuccess()) {
	 * logger.info(nodeInfoResponse.getMessage()); jsonArray =
	 * nodeInfoResponse.getSuccessValue(); if (jsonArray.isEmpty()) { return
	 * RpcResponseBuilder.buildErrorRpcResp("请求人员非法操作!"); } } else {
	 * logger.info(nodeInfoResponse.getMessage()); return
	 * RpcResponseBuilder.buildErrorRpcResp(nodeInfoResponse.getMessage()); }
	 * json.put(DeviceDeletionConstants.FAULT_ORDER_ID, id);
	 * json.put(DeviceDeletionConstants.USER_ID, userId);
	 * json.put(DeviceDeletionConstants.ACTIVITY_USEROBJ, jsonArray);
	 * json.put(DeviceDeletionConstants.ACCESS_SECRET, accessSecret);
	 * json.put("orderNumber", "id"); RpcResponse<Map<String, Object>>
	 * startProgressResult = activityProgressCrud.startProgress(json); if
	 * (!startProgressResult.isSuccess()) { logger.error(
	 * "[addFaultOrderForDeviceDeletion->add()->invalid: " +
	 * startProgressResult.getMessage() + "]"); return
	 * RpcResponseBuilder.buildErrorRpcResp(startProgressResult.getMessage()); }
	 * if (startProgressResult.getSuccessValue() == null) {
	 * logger.error("[addFaultOrderForDeviceDeletion->add()->invalid: 流程开启失败!]"
	 * ); return RpcResponseBuilder.buildErrorRpcResp("流程开启失败!"); } String
	 * processId =
	 * startProgressResult.getSuccessValue().get(DeviceDeletionConstants.
	 * PROCESS_ID).toString();
	 * 
	 * // 完成第一个节点 json.put(DeviceDeletionConstants.PROCESS_ID, processId);
	 * RpcResponse<Map<String, Object>> acceptProgress =
	 * activityProgressCrud.acceptProgress(json); if
	 * (!acceptProgress.isSuccess()) { logger.error(
	 * String.format("[addFaultOrderForDeviceDeletion()->error: %s",
	 * acceptProgress.getMessage())); return
	 * RpcResponseBuilder.buildErrorRpcResp(acceptProgress.getMessage()); } //
	 * 查询流程状态 JSONObject queryProgressState = new JSONObject();
	 * queryProgressState.put(DeviceDeletionConstants.PROCESS_ID, processId);
	 * RpcResponse<Map<String, Object>> queryProgressResult =
	 * activityProgressQuery .getProcessStatus(queryProgressState); if
	 * (!queryProgressResult.isSuccess()) { logger.error(
	 * "[addFaultOrderForDeviceDeletion->add()->invalid: " +
	 * startProgressResult.getMessage() + "]"); return
	 * RpcResponseBuilder.buildErrorRpcResp(startProgressResult.getMessage()); }
	 * if (queryProgressResult.getSuccessValue() == null) { logger.
	 * error("[addFaultOrderForDeviceDeletion->add()->invalid: 查询流程状态失败!]");
	 * return RpcResponseBuilder.buildErrorRpcResp("查询流程状态失败!"); } String
	 * processState =
	 * queryProgressResult.getSuccessValue().get("status").toString();
	 * params.put("processState", processState);
	 * 
	 * // 添加故障工单 // 向故障工单和设备绑定关系表添加数据 String deviceId =
	 * jsonObject.getString(DeviceDeletionConstants.DEVICE_IDS_ADD);
	 * FaultOrderDevice faultOrderDevice = new FaultOrderDevice();
	 * faultOrderDevice.setId(UtilTool.getUuId());
	 * faultOrderDevice.setDeviceId(deviceId);
	 * faultOrderDevice.setFaultOrderId(id);
	 * 
	 * faultOrderDeviceCudRepository.addBindDevices(faultOrderDevice);
	 * 
	 * params.put("createTime", DateUtils.formatDate(new Date()));
	 * params.put(DeviceDeletionConstants.PROCESS_ID, processId);
	 * 
	 * Integer faultType =
	 * jsonObject.getInteger(DeviceDeletionConstants.FAULT_TYPE); String manager
	 * = jsonObject.getString(DeviceDeletionConstants.MANAGER); Integer
	 * deviceCount =
	 * jsonObject.getInteger(DeviceDeletionConstants.DEVICE_COUNT); String phone
	 * = jsonObject.getString(DeviceDeletionConstants.PHONE); String mark =
	 * jsonObject.getString(DeviceDeletionConstants.MARK); String factoryId =
	 * jsonObject.getString(DeviceDeletionConstants.FACTORY_ID); String orderImg
	 * = jsonObject.getString(DeviceDeletionConstants.ORDER_IMG); String
	 * orderName = jsonObject.getString(DeviceDeletionConstants.ORDER_NAME);
	 * 
	 * params.put(DeviceDeletionConstants.ACCESS_SECRET, accessSecret);
	 * params.put(DeviceDeletionConstants.FAULT_TYPE, faultType);
	 * params.put(DeviceDeletionConstants.MANAGER, manager);
	 * params.put(DeviceDeletionConstants.DEVICE_COUNT, deviceCount);
	 * params.put(DeviceDeletionConstants.FACTORY_ID, factoryId);
	 * params.put(DeviceDeletionConstants.PHONE, phone);
	 * params.put(DeviceDeletionConstants.MARK, mark);
	 * params.put(DeviceDeletionConstants.ORDER_NAME, orderName);
	 * params.put(DeviceDeletionConstants.ORDER_IMG, orderImg);
	 * params.put(DeviceDeletionConstants.USER_ID, userId);
	 * params.put(DeviceDeletionConstants.FAULT_ORDER_ID, id);
	 * 
	 * int addNum = faultOrderProcessCudRepository.addFaultOrder(params); if
	 * (addNum > 0) { logger.info("[addFaultOrderForDeviceDeletion()->success: "
	 * + MessageConstant.ADD_SUCCESS + "]"); return
	 * RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.ADD_SUCCESS, id);
	 * } else { logger.error("[addFaultOrderForDeviceDeletion()->error: " +
	 * MessageConstant.ADD_FAIL + "]"); return
	 * RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL); } } catch
	 * (Exception e) {
	 * logger.error("addFaultOrderForDeviceDeletion()->exception", e);
	 * TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	 * return RpcResponseBuilder.buildExceptionRpcResp(e); } }
	 */
}
