/*
 * File name: SimpleOrderCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月6日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.activity.api.constans.ActivityConstans;
import com.run.activity.api.crud.ActivityProgressCrud;
import com.run.activity.api.query.ActivityProgressQuery;
import com.run.common.util.UUIDUtil;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FacInfoOrderCrudRepository;
import com.run.locman.api.crud.repository.SimpleOrderCrudRepository;
import com.run.locman.api.crud.repository.SimpleOrderDeviceCrudRepository;
import com.run.locman.api.crud.service.SimpleOrderCrudService;
import com.run.locman.api.dto.SimpleOrderDto;
import com.run.locman.api.entity.SimpleOrderDevice;
import com.run.locman.api.entity.SimpleOrderProcess;
import com.run.locman.api.entity.SimpleTimerPush;
import com.run.locman.api.model.FacilitiesModel;
import com.run.locman.api.model.SimpleOrderProcessModel;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.api.timer.crud.service.ExecuteMethodTimerService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.SimpleOrderConstants;

/**
 * @Description:作业工单crud
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */
@Transactional(rollbackFor = Exception.class)
public class SimpleOrderCrudServiceImpl implements SimpleOrderCrudService {

	private static final Logger				logger			= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private SimpleOrderCrudRepository		simpleOrderRepository;

	@Autowired
	private SimpleOrderDeviceCrudRepository	orderDeviceCrudRepository;

	@Autowired
	private ActivityProgressCrud			activityProgressCrud;

	@Autowired
	private ActivityProgressQuery			activityProgressQuery;

	@Autowired
	private OrderProcessQueryService		orderProcessQueryService;

	@Autowired
	private FacilityDeviceQueryService		facilityDeviceQueryService;

	@Autowired
	private FacInfoOrderCrudRepository		facInfoOrderCrudRepository;

	@Autowired
	private FacilitiesQueryService			facilitiesQueryService;

	@Autowired
	private SimpleOrderQueryService			simpleOrderQueryService;

	@Autowired
	private ExecuteMethodTimerService		executeMethodTimerService;

	private static String					completeState	= "5";



	@Override
	public RpcResponse<String> simpleOrderAdd(JSONObject orderInfo) {
		String processId = "";
		try {
			RpcResponse<String> check = check(orderInfo);
			if (null != check) {
				return check;
			}
			SimpleOrderProcess order = new SimpleOrderProcess();
			String type = orderInfo.getString(SimpleOrderConstants.TYPE);
			// 添加
			if (SimpleOrderConstants.ADD.equals(type)) {
				RpcResponse<String> checkWhenAdd = checkWhenAdd(orderInfo);
				if (null != checkWhenAdd) {
					return checkWhenAdd;
				}
				// 获取工单uuid
				String uuid = UUIDUtil.getUUID().toString().replaceAll("-", "");
				// 根据组织id查询用户节点信息集合
				Map<String, Object> map = Maps.newHashMap();
				map.put(SimpleOrderConstants.ORGANIZEID, orderInfo.getString(SimpleOrderConstants.ORGANIZEID));
				map.put("processType", "作业工单流程");
				map.put("accessSecret", orderInfo.getString(SimpleOrderConstants.ACCESSSECRET));
				RpcResponse<JSONArray> nodeInfoResponse = orderProcessQueryService.queryNodeInfoForActivity(map);
				JSONArray jsonArray = null;
				if (nodeInfoResponse.isSuccess()) {
					logger.info(nodeInfoResponse.getMessage());
					jsonArray = nodeInfoResponse.getSuccessValue();
					if (jsonArray.isEmpty()) {
						logger.error("[saveRemoteControlRecord()->流程文件未启用或者未查找到当前用户发起权限 !]");
						return RpcResponseBuilder.buildErrorRpcResp("流程文件未启用或者未查找到当前用户发起权限 !");
					}
				} else {
					logger.info(nodeInfoResponse.getMessage());
					return RpcResponseBuilder.buildErrorRpcResp(nodeInfoResponse.getMessage());
				}
				JSONObject jsonParam = getJsonForStartProgress(orderInfo, uuid, jsonArray);
				// 通过设施id集合查询这些设施下的所有设备
				RpcResponse<List<String>> findDeviceByFacIds = facilityDeviceQueryService.findDeviceByFacIds(
						orderInfo.getJSONArray(SimpleOrderConstants.FACILITIES_LIST).toJavaList(String.class));
				if (!findDeviceByFacIds.isSuccess()) {
					logger.error("通过设施ids查询设备失败!");
					return RpcResponseBuilder.buildErrorRpcResp("通过设施ids查询设备失败!");
				}
				//绑定工单与设施关系
				bindingOrderRsFacitilties(orderInfo, uuid);
				// 绑定工单与设备关系
				List<String> devlist = bindingOrderRsDevice(uuid, findDeviceByFacIds);
				// 创建启动工作流程
				RpcResponse<Map<String, Object>> activatyInfo = activityProgressCrud.startProgress(jsonParam);
				if (!activatyInfo.isSuccess()) {
					logger.error("[simpleOrderAdd()->error: " + MessageConstant.ADD_FAIL + "]");
					return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL);
				}
				// 完成节点任务
				JSONObject jsonParamAccept = new JSONObject();
				processId = (String) activatyInfo.getSuccessValue().get(SimpleOrderConstants.PROCESSID);
				RpcResponse<String> completeFirstNodeAndInsert = completeFirstNodeAndInsert(orderInfo, processId, order,
						uuid, devlist, activatyInfo, jsonParamAccept);
				if (null != completeFirstNodeAndInsert) {
					return completeFirstNodeAndInsert;
				}
				// locman保存失败时进行删除工作流操作
				deleteProcess(processId);
				logger.error("[simpleOrderAdd()->error: " + MessageConstant.ADD_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL);
				// 修改
			} else if (SimpleOrderConstants.UPDATE.equals(type)) {
				return update(orderInfo, order);
			}
			return RpcResponseBuilder.buildSuccessRpcResp("添加或修改成功!", null);
		} catch (Exception e) {
			// 异常时删除工作流操作
			deleteProcess(processId);
			logger.error("simpleOrderAdd()->exception", e);
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
	
	private void bindingOrderRsFacitilties(JSONObject orderInfo, String uuid) throws Exception {
		// 绑定工单与设施关系表
		List<String> facLists = orderInfo.getJSONArray(SimpleOrderConstants.FACILITIES_LIST)
				.toJavaList(String.class);
		// 创建model对象 插入数据库
		addFacilities(uuid, facLists);
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> check(JSONObject orderInfo) {
		RpcResponse<String> checkParmer = checkParmer(orderInfo);
		if (null != checkParmer) {
			return checkParmer;
		}
		RpcResponse<String> checkFacilities = checkFacilities(orderInfo);
		if (null != checkFacilities) {
			return checkFacilities;
		}
		return null;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<String> completeFirstNodeAndInsert(JSONObject orderInfo, String processId,
			SimpleOrderProcess order, String uuid, List<String> devlist, RpcResponse<Map<String, Object>> activatyInfo,
			JSONObject jsonParamAccept) throws Exception {
		RpcResponse<String> completeFirstNode = completeFirstNode(orderInfo, processId, order, uuid, activatyInfo,
				jsonParamAccept);
		if (null != completeFirstNode) {
			return completeFirstNode;
		}

		setOrder(orderInfo, order, uuid, devlist);
		// 保存工单信息
		int model = simpleOrderRepository.insertModel(order);

		if (model > 0) {
			logger.info("[simpleOrderAdd()->succes: " + MessageConstant.ADD_SUCCESS + "]");
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.ADD_SUCCESS, uuid);
		}
		return null;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<String> checkFacilities(JSONObject orderInfo) {
		// 获取设施id集合
		List<String> facIds = orderInfo.getJSONArray(SimpleOrderConstants.FACILITIES_LIST).toJavaList(String.class);
		// 是否为启用
		RpcResponse<Boolean> facilityMangerStateById = facilitiesQueryService.getFacilityMangerStateById(facIds);

		if (facilityMangerStateById == null || !facilityMangerStateById.isSuccess()) {
			logger.error("[simpleOrderAdd()->invalid：保存工单过程中，设施被停用！]");
			return RpcResponseBuilder.buildErrorRpcResp("网络繁忙！请稍后重试！");
		}

		if (!facilityMangerStateById.getSuccessValue()) {
			logger.error("[simpleOrderAdd()->invalid：保存工单过程中，设施被停用！]");
			return RpcResponseBuilder.buildErrorRpcResp("已选设施中有设施已停用，请重新选择！");
		}
		return null;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void addFacilities(String uuid, List<String> facLists) throws Exception {
		FacilitiesModel facilitiesModel = new FacilitiesModel();
		facilitiesModel.setSimpleOrderId(uuid);
		for (String facId : facLists) {
			facilitiesModel.setFacilitiesId(facId);
			facilitiesModel.setSimplerOrFacId(UUIDUtil.getUUID().toString().replaceAll("-", ""));
			facInfoOrderCrudRepository.insertModel(facilitiesModel);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private List<String> bindingOrderRsDevice(String uuid, RpcResponse<List<String>> findDeviceByFacIds)
			throws Exception {
		SimpleOrderDevice orderDevice = new SimpleOrderDevice();
		orderDevice.setSimpleOrderId(uuid);
		List<String> devlist = findDeviceByFacIds.getSuccessValue();
		if (null != devlist && devlist.size() > 0) {
			for (int i = 0; i < devlist.size(); i++) {
				orderDevice.setId(UUIDUtil.getUUID().toString().replaceAll("-", ""));
				orderDevice.setDeviceId(devlist.get(i));
				orderDeviceCrudRepository.insertModel(orderDevice);
			}
		}
		return devlist;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<String> completeFirstNode(JSONObject orderInfo, String processId, SimpleOrderProcess order,
			String uuid, RpcResponse<Map<String, Object>> activatyInfo, JSONObject jsonParamAccept) {
		String userId = orderInfo.getString(SimpleOrderConstants.USERID);
		jsonParamAccept.put(SimpleOrderConstants.PROCESSID, processId);
		jsonParamAccept.put(SimpleOrderConstants.USERID, userId);
		jsonParamAccept.put(SimpleOrderConstants.ID, uuid);
		RpcResponse<Map<String, Object>> acceptProgress = activityProgressCrud.acceptProgress(jsonParamAccept);
		if (!acceptProgress.isSuccess()) {
			logger.error(String.format("[simpleOrderAdd()->error: %s", acceptProgress.getMessage()));
			return RpcResponseBuilder.buildErrorRpcResp(acceptProgress.getMessage());
		}
		// 根据流程id获取当前节点状态
		Map<String, Object> activtyMap = activatyInfo.getSuccessValue();
		JSONObject param = new JSONObject();

		if (null != activtyMap && activtyMap.containsKey(SimpleOrderConstants.PROCESSID)) {
			param.put(SimpleOrderConstants.PROCESSID, activtyMap.get(SimpleOrderConstants.PROCESSID).toString());
			order.setProcessId(activtyMap.get(SimpleOrderConstants.PROCESSID).toString());
			RpcResponse<Map<String, Object>> statusInfo = activityProgressQuery.getProcessStatus(param);
			if (!statusInfo.isSuccess()) {
				logger.error("[simpleOrderAdd()->error: " + MessageConstant.ADD_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL);
			}
			Map<String, Object> statusMap = statusInfo.getSuccessValue();
			if (null != statusMap && statusMap.containsKey(SimpleOrderConstants.STATUS)) {
				order.setProcessState(statusMap.get("status").toString());
			} else {
				logger.error("[simpleOrderAdd()->error: " + MessageConstant.ADD_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL);
			}
		} else {
			logger.error("[simpleOrderAdd()->error: " + MessageConstant.ADD_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL);
		}
		return null;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<String> update(JSONObject orderInfo, SimpleOrderProcess order) throws Exception {
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.ID))) {
			logger.error("[simpleOrderAdd()->invalid：工单id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单id不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.PROCESSID))) {
			logger.error("[simpleOrderAdd()->invalid：工单流程id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单流程id不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.FACILITIES_LIST))
				&& orderInfo.getJSONArray(SimpleOrderConstants.FACILITIES_LIST).toJavaList(String.class).size() != 0) {
			logger.error("[simpleOrderAdd()->invalid：设施id集合不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("设施id集合不能为空!");
		}
		// 验证单子是否审核过,审核过不能修改
		JSONObject jsonParam = new JSONObject();
		jsonParam.put(SimpleOrderConstants.PROCESSID, orderInfo.getString(SimpleOrderConstants.PROCESSID));
		RpcResponse<Map<String, Object>> result = activityProgressQuery.getJudgmentProcess(jsonParam);
		if (result.isSuccess() && SimpleOrderConstants.FALSE
				.equals(result.getSuccessValue().get(SimpleOrderConstants.STATE).toString())) {
			logger.error("该工单已审核过,不能修改!");
			return RpcResponseBuilder.buildErrorRpcResp("该工单已审核过,不能修改!");
		} else if (!result.isSuccess()) {
			logger.error("调用工作流,获取是否审核过工单状态失败!");
			return RpcResponseBuilder.buildErrorRpcResp("调用工作流,获取是否审核过工单状态失败!");
		}
		// 通过设施id集合查询这些设施下的所有设备
		RpcResponse<List<String>> findDeviceByFacIds = facilityDeviceQueryService.findDeviceByFacIds(
				orderInfo.getJSONArray(SimpleOrderConstants.FACILITIES_LIST).toJavaList(String.class));
		if (null == findDeviceByFacIds || !findDeviceByFacIds.isSuccess()) {
			logger.error("通过设施ids查询设备失败!");
			return RpcResponseBuilder.buildErrorRpcResp("通过设施ids查询设备失败!");
		}
		List<String> devList = findDeviceByFacIds.getSuccessValue();
		if (null == devList) {
			logger.error("通过设施ids查询设备失败!返回设备idlist为null");
			return RpcResponseBuilder.buildErrorRpcResp("通过设施ids查询设备失败!");
		}
		setOrder(orderInfo, order, devList);
		// 删除原有的设施与工单绑定关系
		facInfoOrderCrudRepository.deleteByOrderId(orderInfo.getString(SimpleOrderConstants.ID));
		List<String> facLists = orderInfo.getJSONArray(SimpleOrderConstants.FACILITIES_LIST).toJavaList(String.class);
		// 绑定工单与设施关系表
		// 创建model对象 插入数据库
		FacilitiesModel facilitiesModel = new FacilitiesModel();
		facilitiesModel.setSimpleOrderId(orderInfo.getString(SimpleOrderConstants.ID));
		for (String facId : facLists) {
			facilitiesModel.setFacilitiesId(facId);
			facilitiesModel.setSimplerOrFacId(UUIDUtil.getUUID().toString().replaceAll("-", ""));
			facInfoOrderCrudRepository.insertModel(facilitiesModel);
		}
		// 创建工单对象
		SimpleOrderDevice orderDevice = new SimpleOrderDevice();
		orderDevice.setSimpleOrderId(orderInfo.getString(SimpleOrderConstants.ID));
		// 删除原有的设备与工单绑定关系
		orderDeviceCrudRepository.deleteByOrderid(orderInfo.getString(SimpleOrderConstants.ID));
		// 工单绑定设备
		if (null != devList && devList.size() > 0) {
			for (int i = 0; i < devList.size(); i++) {
				orderDevice.setId(UUIDUtil.getUUID().toString().replaceAll("-", ""));
				orderDevice.setDeviceId(devList.get(i));
				orderDeviceCrudRepository.insertModel(orderDevice);
			}
		}
		int model = simpleOrderRepository.updateModel(order);
		if (model > 0) {
			logger.info("[simpleOrderAdd()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS,
					orderInfo.getString(SimpleOrderConstants.ID));
		}
		logger.error("[simpleOrderAdd()->error: " + MessageConstant.UPDATE_FAIL + "]");
		return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void setOrder(JSONObject orderInfo, SimpleOrderProcess order, List<String> devList) {
		order.setUpdateTime(DateUtils.formatDate(new Date()));
		order.setId(orderInfo.getString(SimpleOrderConstants.ID));
		order.setOrderName(orderInfo.getString(SimpleOrderConstants.ORDERNAME));
		order.setOrderType(orderInfo.getInteger(SimpleOrderConstants.ORDERTYPE));
		order.setConstructBy(orderInfo.getString(SimpleOrderConstants.CONSTRUCTBY));
		order.setManager(orderInfo.getString(SimpleOrderConstants.MANAGER));
		order.setPhone(orderInfo.getString(SimpleOrderConstants.PHONE));
		order.setProcessStartTime(orderInfo.getString(SimpleOrderConstants.PROCESSSTARTTIME));
		order.setProcessEndTime(orderInfo.getString(SimpleOrderConstants.PROCESSENDTIME));
		order.setMark(orderInfo.getString(SimpleOrderConstants.MARK));
		order.setOrderImg(orderInfo.getString(SimpleOrderConstants.ORDERIMG));
		order.setRemindTime(orderInfo.getString(SimpleOrderConstants.REMIND_TIME));
		order.setRemindRule(orderInfo.getString(SimpleOrderConstants.REMIND_RULE));
		// 新增设施信息
		order.setFacilitiesList(orderInfo.getString(SimpleOrderConstants.FACILITIES_LIST));
		// 设备总条数
		order.setDeviceCount(devList.size());
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void setOrder(JSONObject orderInfo, SimpleOrderProcess order, String uuid, List<String> devlist) {
		order.setId(uuid);
		order.setCreateTime(DateUtils.formatDate(new Date()));
		order.setUpdateTime(DateUtils.formatDate(new Date()));
		order.setOrderName(orderInfo.getString(SimpleOrderConstants.ORDERNAME));
		order.setOrderType(orderInfo.getInteger(SimpleOrderConstants.ORDERTYPE));
		order.setConstructBy(orderInfo.getString(SimpleOrderConstants.CONSTRUCTBY));
		order.setManager(orderInfo.getString(SimpleOrderConstants.MANAGER));
		order.setPhone(orderInfo.getString(SimpleOrderConstants.PHONE));
		order.setProcessStartTime(orderInfo.getString(SimpleOrderConstants.PROCESSSTARTTIME));
		order.setProcessEndTime(orderInfo.getString(SimpleOrderConstants.PROCESSENDTIME));
		order.setMark(orderInfo.getString(SimpleOrderConstants.MARK));
		order.setOrderImg(orderInfo.getString(SimpleOrderConstants.ORDERIMG));
		order.setUserId(orderInfo.getString(SimpleOrderConstants.USERID));
		order.setAccessSecret(orderInfo.getString(SimpleOrderConstants.ACCESSSECRET));
		order.setRemindTime(orderInfo.getString(SimpleOrderConstants.REMIND_TIME));
		order.setRemindRule(orderInfo.getString(SimpleOrderConstants.REMIND_RULE));
		// 保存设备总条数
		order.setDeviceCount(devlist.size());
		// 新增设施信息
		order.setFacilitiesList(orderInfo.getString(SimpleOrderConstants.FACILITIES_LIST));
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private JSONObject getJsonForStartProgress(JSONObject orderInfo, String uuid, JSONArray jsonArray) {
		JSONObject jsonParam = new JSONObject();
		jsonParam.put(SimpleOrderConstants.ID, uuid);
		jsonParam.put(SimpleOrderConstants.USERID, orderInfo.getString(SimpleOrderConstants.USERID));
		jsonParam.put(SimpleOrderConstants.TYPE, "generalProcess");
		jsonParam.put(SimpleOrderConstants.ACTIVITY_USEROBJ, jsonArray);
		jsonParam.put(SimpleOrderConstants.ACCESSSECRET, orderInfo.getString(SimpleOrderConstants.ACCESSSECRET));

		// 查询工单流水号-> 便于工作流调用激光推送
		RpcResponse<String> findOrderNumber = simpleOrderQueryService
				.findOrderNumber(orderInfo.getString(SimpleOrderConstants.ACCESSSECRET));

		if (!findOrderNumber.isSuccess()) {
			logger.error(String.format("[simpleOrderAdd()->error:%s----%s]", findOrderNumber.getException(),
					findOrderNumber.getMessage()));
		} else {
			jsonParam.put(SimpleOrderConstants.ORDER_NUM, findOrderNumber.getSuccessValue());
		}
		return jsonParam;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<String> checkWhenAdd(JSONObject orderInfo) {
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.ORDERNAME))) {
			logger.error("[simpleOrderAdd()->invalid：工单名称不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单名称不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.ORDERTYPE))) {
			logger.error("[simpleOrderAdd()->invalid：工单类型不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单类型不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.CONSTRUCTBY))) {
			logger.error("[simpleOrderAdd()->invalid：施工单位不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("施工单位不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.MANAGER))) {
			logger.error("[simpleOrderAdd()->invalid：联系人不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("联系人不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.PHONE))) {
			logger.error("[simpleOrderAdd()->invalid：联系方式不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("联系方式不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.PROCESSSTARTTIME))) {
			logger.error("[simpleOrderAdd()->invalid：工单预约起始时间 不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单预约起始时间 不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.PROCESSENDTIME))) {
			logger.error("[simpleOrderAdd()->invalid：工单预约结束时间不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单预约结束时间不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.MARK))) {
			logger.error("[simpleOrderAdd()->invalid：施工说明不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("施工说明不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.ORDERIMG))) {
			logger.error("[simpleOrderAdd()->invalid：上传图片不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("上传图片不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.USERID))) {
			logger.error("[simpleOrderAdd()->invalid：用户id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.ACCESSSECRET))) {
			logger.error("[simpleOrderAdd()->invalid：接入方秘钥不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.REMIND_RULE))) {
			logger.error("[simpleOrderAdd()->invalid：校验规则名不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("校验规则名不能为空!");
		}
		return null;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<String> checkParmer(JSONObject orderInfo) {
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.TYPE))) {
			logger.error("[simpleOrderAdd()->invalid：增加还是修改类型不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("增加还是修改类型不能为空！");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.ORGANIZEID))) {
			logger.error("[simpleOrderAdd()->invalid：增加还是修改组织id不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("增加还是修改组织id不能为空！");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.FACILITIES_LIST))) {
			logger.error("[simpleOrderAdd()->invalid：设施id集合不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("设施id集合不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.FACILITIES_LIST))
				&& orderInfo.getJSONArray(SimpleOrderConstants.FACILITIES_LIST).toJavaList(String.class).size() != 0) {
			logger.error("[simpleOrderAdd()->invalid：设施id集合不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("设施id集合不能为空!");
		}
		logger.info(String.format("[simpleOrderAdd()->进入方法,参数:%s]", orderInfo));
		return null;
	}



	/**
	 * @Description:伪事务操作->processId存在才进行删除
	 * @param processId
	 */

	private void deleteProcess(String processId) {

		if (!StringUtils.isBlank(processId)) {
			RpcResponse<Boolean> deleteProcess = activityProgressCrud.deleteProcess(processId);
			logger.info(String.format("[deleteProcess()->info:%s]", deleteProcess.getMessage()));
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.SimpleOrderCrudService#updateSimpleOrderState(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> updateSimpleOrderState(JSONObject orderInfo) throws Exception {
		try {
			RpcResponse<String> checkUpdateParmer = checkUpdateParmer(orderInfo);
			if (null != checkUpdateParmer) {
				return checkUpdateParmer;
			}

			// 参数封装
			String orderId = orderInfo.getString(SimpleOrderConstants.ID);
			SimpleOrderProcess order = new SimpleOrderProcess();
			order.setId(orderId);
			JSONObject json = new JSONObject();
			json.put(SimpleOrderConstants.PROCESSID, orderInfo.getString(SimpleOrderConstants.PROCESSID));
			orderInfo.remove(SimpleOrderConstants.PROCESSID);

			if (SimpleOrderConstants.WITHDRAW.equals(orderInfo.getString(SimpleOrderConstants.OPERATIONTYPE))) {
				RpcResponse<String> withdraw = withdraw(orderInfo, order, json);
				if (null != withdraw) {
					return withdraw;
				}
				// 通过和完成操作
			} else if (SimpleOrderConstants.PASS.equals(orderInfo.getString(SimpleOrderConstants.OPERATIONTYPE))
					|| SimpleOrderConstants.COMPLETE.equals(orderInfo.getString(SimpleOrderConstants.OPERATIONTYPE))) {

				RpcResponse<String> passOrComplete = passOrComplete(orderInfo, orderId, order, json);
				if (null != passOrComplete) {
					return passOrComplete;
				}
				// 拒绝操作
			} else if (orderInfo.getString(SimpleOrderConstants.OPERATIONTYPE).equals(SimpleOrderConstants.REFUSE)) {

				RpcResponse<String> refuse = refuse(orderInfo, order, json);
				if (null != refuse) {
					return refuse;
				}
			}

			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
		} catch (Exception e) {
			logger.error("updateSimpleOrderState()->exception", e);
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
	
	private RpcResponse<String> withdraw(JSONObject orderInfo, SimpleOrderProcess order, JSONObject json) throws Exception {
		// 调用工作流判断是否能撤回
		RpcResponse<Map<String, Object>> processState = activityProgressQuery.getJudgmentProcess(json);
		if (!processState.isSuccess()) {
			logger.error("[updateSimpleOrderState()->error: " + MessageConstant.SEARCH_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(processState.getMessage());
		}
		if (processState.getSuccessValue() == null
				|| !processState.getSuccessValue().containsKey(SimpleOrderConstants.STATE)) {
			logger.error("[updateSimpleOrderState()->error: " + MessageConstant.SEARCH_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(processState.getMessage());
		}
		if (!(Boolean) processState.getSuccessValue().get(SimpleOrderConstants.STATE)) {
			logger.error("此流程已撤回或已审核,不能撤回!请刷新当前页面!");
			return RpcResponseBuilder.buildErrorRpcResp("此流程已撤回或已审核,不能撤回!请刷新当前页面!");
		}
		// 调用工作流更新工单状态
		orderInfo.remove(SimpleOrderConstants.OPERATIONTYPE);
		json.put(SimpleOrderConstants.USERID, orderInfo.getString(SimpleOrderConstants.USERID));
		json.putAll(orderInfo);
		RpcResponse<Map<String, Object>> result = activityProgressCrud.retreatProgress(json);
		if (!result.isSuccess()) {
			logger.error("[simpleOrderAdd()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(result.getMessage());
		}

		// 根据流程id查询当前工单状态
		RpcResponse<Map<String, Object>> response = activityProgressQuery.getProcessStatus(json);
		Map<String, Object> statusMap = response.getSuccessValue();

		// 根据id更新数据库工单状态
		if (null != statusMap && statusMap.size() > 0) {
			String status = statusMap.get("status").toString();
			order.setProcessState(status);
			order.setUpdateTime(DateUtils.formatDate(new Date()));
			int model = simpleOrderRepository.updateModel(order);
			if (model > 0) {
				logger.info("[simpleOrderAdd()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS,
						orderInfo.getString(SimpleOrderConstants.ID));
			}
			logger.error("[simpleOrderAdd()->error: " + MessageConstant.UPDATE_FAIL + "]");
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
	
	private RpcResponse<String> refuse(JSONObject orderInfo, SimpleOrderProcess order, JSONObject json) throws Exception {
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.DETAIL))) {
			logger.error("[simpleOrderAdd()->invalid：操作理由不能空!]");
			return RpcResponseBuilder.buildErrorRpcResp("操作理由不能空!");
		}

		orderInfo.remove(SimpleOrderConstants.OPERATIONTYPE);
		json.put(SimpleOrderConstants.USERID, orderInfo.getString(SimpleOrderConstants.USERID));
		orderInfo.remove(SimpleOrderConstants.USERID);
		json.putAll(orderInfo);
		// 调用工作流更新工单状态
		RpcResponse<Map<String, Object>> result = activityProgressCrud.rejectProgress(json);
		if (!result.isSuccess()) {
			logger.error("[simpleOrderAdd()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(result.getMessage());
		}

		// 根据流程id查询当前工单状态
		RpcResponse<Map<String, Object>> response = activityProgressQuery.getProcessStatus(json);
		Map<String, Object> statusMap = response.getSuccessValue();

		// 根据id更新数据库工单状态
		if (null != statusMap && statusMap.size() > 0) {
			String status = statusMap.get("status").toString();
			order.setProcessState(status);
			order.setUpdateTime(DateUtils.formatDate(new Date()));
			int model = simpleOrderRepository.updateModel(order);
			if (model > 0) {
				logger.info("[simpleOrderAdd()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS,
						orderInfo.getString(SimpleOrderConstants.ID));
			}
			logger.error("[simpleOrderAdd()->error: " + MessageConstant.UPDATE_FAIL + "]");
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
	
	private RpcResponse<String> passOrComplete(JSONObject orderInfo, String orderId, SimpleOrderProcess order, JSONObject json)
			throws Exception {
		json.put(SimpleOrderConstants.USERID, orderInfo.getString(SimpleOrderConstants.USERID));
		orderInfo.remove(SimpleOrderConstants.USERID);
		orderInfo.remove(SimpleOrderConstants.OPERATIONTYPE);
		json.putAll(orderInfo);

		// 查询节点详细信息
		RpcResponse<Map<String, Object>> orderNodeDetails = simpleOrderQueryService.getOrderNodeDetails(json);
		if (!orderNodeDetails.isSuccess()) {
			logger.error("[simpleOrderAdd()->error: " + orderNodeDetails.getMessage() + "]");
			return RpcResponseBuilder.buildErrorRpcResp(orderNodeDetails.getMessage());
		}

		// 调用工作流更新工单状态
		RpcResponse<Map<String, Object>> result = activityProgressCrud.acceptProgress(json);
		if (!result.isSuccess()) {
			logger.error("[simpleOrderAdd()->error: " + MessageConstant.UPDATE_FAIL + "]");
			return RpcResponseBuilder.buildErrorRpcResp(result.getMessage());
		}

		// 根据流程id查询当前工单状态
		RpcResponse<Map<String, Object>> response = activityProgressQuery.getProcessStatus(json);
		Map<String, Object> statusMap = response.getSuccessValue();

		// 根据id更新数据库工单状态
		if (null != statusMap && statusMap.size() > 0) {
			String status = statusMap.get("status").toString();
			order.setProcessState(status);
			order.setUpdateTime(DateUtils.formatDate(new Date()));

			// 判断是否是延时操作
			checkDelayedDate(order, json, orderNodeDetails.getSuccessValue());

			// 新添加流程时判断是否是终审,终审开启定时器
			startRemindTimer(orderId, orderNodeDetails);

			int model = simpleOrderRepository.updateModel(order);
			if (model > 0) {
				//
				if (completeState.equals(status)) {
					RpcResponse<Boolean> closeScheduleJob = executeMethodTimerService.closeScheduleJob(orderId);
					logger.info("simpleOrderAdd()-->关闭定时器:" + closeScheduleJob.getSuccessValue());
				}

				logger.info("[simpleOrderAdd()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS,
						orderInfo.getString(SimpleOrderConstants.ID));
			}
			logger.error("[simpleOrderAdd()->error: " + MessageConstant.UPDATE_FAIL + "]");
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
	
	private RpcResponse<String> checkUpdateParmer(JSONObject orderInfo) {
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.ID))) {
			logger.error("[simpleOrderAdd()->invalid：作业流程工单id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("作业流程工单id不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.OPERATIONTYPE))) {
			logger.error("[simpleOrderAdd()->invalid：工单操作类型不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单操作类型不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.PROCESSID))) {
			logger.error("[simpleOrderAdd()->invalid：工单流程id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单流程id不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.USERID))) {
			logger.error("[simpleOrderAdd()->invalid：用户id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空!");
		}
		if (StringUtils.isBlank(orderInfo.getString(SimpleOrderConstants.ACCESSSECRET))) {
			logger.error("[simpleOrderAdd()->invalid：接入方秘钥不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
		}
		return null;
	}



	/**
	 * @Description:通过审核开启定时器提醒
	 * @param orderId
	 * @param orderNodeDetails
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void startRemindTimer(String orderId, RpcResponse<Map<String, Object>> orderNodeDetails) {
		Map<String, Object> orderNodeMap = orderNodeDetails.getSuccessValue();
		List<Map> orderInfoJson = (List<Map>) orderNodeMap.get(SimpleOrderConstants.HISEXCULIST);
		if (orderInfoJson == null || orderInfoJson.size() == 0) {
			logger.error("[simpleOrderAdd()->error:工单详细信息不存在]");
			return;
		}
		// 判断最后一个节点有没有finalAudit并且为true
		Map finalAuditJson = orderInfoJson.get(orderInfoJson.size() - 1);
		Map variablesMap = (Map) finalAuditJson.get(SimpleOrderConstants.ORDER_VARIABLE);
		if (variablesMap == null) {
			logger.info(String.format("[simpleOrderAdd()->info:当前task本地变量不存在,%s]", finalAuditJson));
			return;
		}
		String finalAudit = variablesMap.get(SimpleOrderConstants.FINAL_AUDIT) + "";
		if (SimpleOrderConstants.TRUE.equals(finalAudit)) {
			startTimerWhenAdd(orderId);
		}
	}



	/**
	 * 
	 * @Description:添加工单时(终审通过后)开启定时器
	 * @param
	 * @return
	 */

	private void startTimerWhenAdd(String orderId) {
		SimpleOrderDto simpleOrderDto = simpleOrderRepository.findProcessInfoById(orderId);
		SimpleTimerPush simpleTimerPush = new SimpleTimerPush();

		List<String> aliasIds = Lists.newArrayList();
		String userId = simpleOrderDto.getUserId();
		aliasIds.add(userId);
		simpleTimerPush.setSerialNumber(simpleOrderDto.getSerialNumber());
		simpleTimerPush.setAliasIds(aliasIds);
		simpleTimerPush.setOrderId(simpleOrderDto.getId());


		try {
			RpcResponse<String> checkAndExecuteMessagePush = executeMethodTimerService.checkAndExecuteMessagePush(
					simpleTimerPush, simpleOrderDto.getProcessStartTime(), simpleOrderDto.getProcessEndTime(),
					simpleOrderDto.getRemindTime());
			if (!checkAndExecuteMessagePush.isSuccess()) {
				logger.error(String.format("[startTimer()->error:%s]", checkAndExecuteMessagePush.getMessage()));
				return;
			}
			logger.info(String.format("[startTimer()->info:%s]", checkAndExecuteMessagePush.getMessage()));
		} catch (Exception e) {
			logger.error("定时器服务开启异常", e);
		}
	}



	/**
	 * @Description:判断是否增加工单结束时间
	 * @param order
	 * @param orderInfo
	 * @param map
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkDelayedDate(SimpleOrderProcess order, JSONObject orderInfo, Map<String, Object> map)
			throws Exception {

		// 获取hours和affirm
		List<Map> orderInfoJson = (List<Map>) map.get(SimpleOrderConstants.HISEXCULIST);
		if (orderInfoJson == null || orderInfoJson.size() == 0) {
			logger.error("[checkDelayedDate()->error:工单详细信息不存在]");
			return;
		}
		// 判断最后一个节点有没有affirm并且为true
		Map affirmJson = orderInfoJson.get(orderInfoJson.size() - 1);
		Map variablesMap = (Map) affirmJson.get(SimpleOrderConstants.ORDER_VARIABLE);
		if (variablesMap == null) {
			logger.info(String.format("[checkDelayedDate()->info:当前task本地变量不存在,%s]", affirmJson));
			return;
		}
		String affirm = (String) variablesMap.get(SimpleOrderConstants.AFFIRM);
		if (!SimpleOrderConstants.TRUE.equals(affirm)) {
			return;
		}

		// 如果为true 那么获取最新的延时审核时间
		String hours = null;
		for (Map taskMap : orderInfoJson) {
			variablesMap = (Map) taskMap.get(SimpleOrderConstants.ORDER_VARIABLE);
			if (variablesMap != null && variablesMap.get(SimpleOrderConstants.HOURS) != null) {
				hours = variablesMap.get(SimpleOrderConstants.HOURS) + "";
			}
		}

		// 延长时间
		if (StringUtils.isNumeric(hours)) {
			// 通过工单id获取时间，并且增加上小时
			String processEndTime = simpleOrderRepository
					.findProcessEndTimeById(orderInfo.getString(SimpleOrderConstants.ID));
			if (StringUtils.isBlank(processEndTime)) {
				logger.error("[checkDelayedDate()->error:该工单结束时间不存在！]");
				return;
			}
			// 加上时间

			SimpleDateFormat sDateFormat = new SimpleDateFormat(DateUtils.DATETIME_FORMAT);
			Date parse = sDateFormat.parse(processEndTime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(parse);
			// 24小时制
			cal.add(Calendar.HOUR, Integer.valueOf(hours));
			String formatDate = DateUtils.formatDate(cal.getTime());
			order.setProcessEndTime(formatDate);

			// 开启新的定时器
			startTimer(order);
			logger.info("[checkDelayedDate()->error:延时成功！]");
			return;
		}
		logger.error("[checkDelayedDate()->error:传入时间并不是一个数字！]");
	}



	/**
	 * @Description:开启新的提醒定时器
	 * @param order
	 */

	private void startTimer(SimpleOrderProcess order) throws Exception {
		// 查询工单开始时间 结束时间 以及发起人
		RpcResponse<Map<String, Object>> rpcOrderInfo = simpleOrderQueryService.findById(order.getId());
		if (!rpcOrderInfo.isSuccess()) {
			logger.error(String.format("[startTimer()->error:开启定时器失败！通过工单id查询工单信息失败！%s]", rpcOrderInfo.getMessage()));
		}
		Map<String, Object> orderMap = rpcOrderInfo.getSuccessValue();

		SimpleTimerPush simpleTimerPush = new SimpleTimerPush();

		List<String> aliasIds = Lists.newArrayList();
		aliasIds.add(orderMap.get(SimpleOrderConstants.USERID) + "");
		simpleTimerPush.setAliasIds(aliasIds);
		simpleTimerPush.setOrderId(order.getId());
		simpleTimerPush.setSerialNumber(orderMap.get(SimpleOrderConstants.SERIALNUMBER) + "");

		try {
			RpcResponse<String> checkAndExecuteMessagePush = executeMethodTimerService.checkAndExecuteMessagePush(
					simpleTimerPush, orderMap.get(SimpleOrderConstants.PROCESSSTARTTIME) + "",
					order.getProcessEndTime(), "30");
			if (!checkAndExecuteMessagePush.isSuccess()) {
				logger.error(String.format("[startTimer()->error:%s]", checkAndExecuteMessagePush.getMessage()));
				return;
			}
			logger.info(String.format("[startTimer()->info:%s]", checkAndExecuteMessagePush.getMessage()));
		} catch (Exception e) {
			logger.error("定时器服务开启异常", e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.SimpleOrderCrudService#delayedSimpleOrder(com.run.locman.api.model.SimpleOrderProcessModel)
	 */
	@Override
	public RpcResponse<String> delayedSimpleOrder(SimpleOrderProcessModel simpleOrderProcessModel) {
		if (simpleOrderProcessModel == null) {
			logger.error("[delayedSimpleOrder()->invalid：作业流程工单实体类不能为null!]");
			return RpcResponseBuilder.buildErrorRpcResp("作业流程工单实体类不能为null!");
		}
		logger.info(String.format("[delayedSimpleOrder()-rpc params: %s]", simpleOrderProcessModel.toString()));

		try {
			// 基础参数校验
			RpcResponse<String> rpc = CheckParameterUtil.checkObjectBusinessKey(logger, "delayedSimpleOrder",
					simpleOrderProcessModel, SimpleOrderConstants.ACCESSSECRET, SimpleOrderConstants.USERID,
					SimpleOrderConstants.SIMPLEORDERID, SimpleOrderConstants.PROCESSID, SimpleOrderConstants.HOURS);
			if (rpc != null) {
				return rpc;
			}

			// 判断当前工单是否过期 过期无法发起
			RpcResponse<Map<String, Object>> simpleOrderInfoRpc = simpleOrderQueryService
					.findById(simpleOrderProcessModel.getSimpleOrderId());

			if (!simpleOrderInfoRpc.isSuccess()) {
				logger.error(String.format("[delayedSimpleOrder()->error:%s]", simpleOrderInfoRpc.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(simpleOrderInfoRpc.getMessage());
			}

			// 查看工单状态是否为7 7代表是的已过期
			String processState = (String) simpleOrderInfoRpc.getSuccessValue()
					.get(SimpleOrderConstants.PROCESS_STATE_APP);

			if (SimpleOrderConstants.STATE_SEVEN.equals(processState)) {
				logger.error("[delayedSimpleOrder()->error:工单已过期无法发起延时审批！]");
				return RpcResponseBuilder.buildErrorRpcResp("工单已过期无法发起延时审批！");
			}

			// 延时审批
			JSONObject delayedParam = (JSONObject) JSONObject.toJSON(simpleOrderProcessModel);
			RpcResponse<Map<String, Object>> delayedProgress = activityProgressCrud.delayedProgress(delayedParam);

			if (!delayedProgress.isSuccess()) {
				logger.error(String.format("[delayedSimpleOrder()->error:%s]", delayedProgress.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(delayedProgress.getMessage());
			}

			// 查询流程状态
			JSONObject json = new JSONObject();
			json.put(SimpleOrderConstants.PROCESSID, simpleOrderProcessModel.getProcessId());
			RpcResponse<Map<String, Object>> response = activityProgressQuery.getProcessStatus(json);
			if (!response.isSuccess()) {
				logger.error(String.format("[delayedSimpleOrder()->error:%s]", response.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(response.getMessage());
			}

			// 更改locman作业工单状态
			SimpleOrderProcess order = new SimpleOrderProcess();
			order.setId(simpleOrderProcessModel.getSimpleOrderId());
			order.setProcessState(response.getSuccessValue().get(ActivityConstans.ACTIVITY_STATUS) + "");
			order.setUpdateTime(DateUtils.formatDate(new Date()));
			int model = simpleOrderRepository.updateModel(order);
			if (model <= 0) {
				logger.error("[delayedSimpleOrder()->error:修改数据库失败！<=0]");
				return RpcResponseBuilder.buildErrorRpcResp("[delayedSimpleOrder()->error:修改数据库失败！<=0]");
			}

			logger.info(String.format("[delayedSimpleOrder()->suc:%s]", delayedProgress.getMessage()));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS,
					simpleOrderProcessModel.getProcessId());
		} catch (Exception e) {
			logger.error("delayedSimpleOrder()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@Override
	public RpcResponse<String> invalidateSimpleOrder(SimpleOrderProcessModel simpleOrderProcessModel) {
		if (simpleOrderProcessModel == null) {
			logger.error("[invalidateSimpleOrder()->invalid：作业流程工单实体类不能为null!]");
			return RpcResponseBuilder.buildErrorRpcResp("作业流程工单实体类不能为null!");
		}
		logger.info(String.format("[invalidateSimpleOrder()-rpc params: %s]", simpleOrderProcessModel.toString()));
		try {
			RpcResponse<String> checkResult = CheckParameterUtil.checkObjectBusinessKey(logger, "invalidateSimpleOrder",
					simpleOrderProcessModel, SimpleOrderConstants.ACCESSSECRET, SimpleOrderConstants.PROCESSID,
					SimpleOrderConstants.SIMPLEORDERID, SimpleOrderConstants.USERID);
			if (checkResult != null) {
				return checkResult;
			}

			// 校验该工单是否过期
			RpcResponse<Map<String, Object>> simpleOrderInfo = simpleOrderQueryService
					.findById(simpleOrderProcessModel.getSimpleOrderId());
			if (!simpleOrderInfo.isSuccess()) {
				logger.error(String.format("invalidateSimpleOrder()->error:%s ", simpleOrderInfo.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(simpleOrderInfo.getMessage());
			}
			String processState = (String) simpleOrderInfo.getSuccessValue()
					.get(SimpleOrderConstants.PROCESS_STATE_APP);
			if (!SimpleOrderConstants.STATE_SEVEN.equals(processState)) {
				logger.error("invalidateSimpleOrder()->error:%s:该工单尚未过期");
				return RpcResponseBuilder.buildErrorRpcResp("该工单尚未过期，无法作废");
			}
			JSONObject invalidateParam = (JSONObject) JSONObject.toJSON(simpleOrderProcessModel);
			invalidateParam.put(SimpleOrderConstants.ID, simpleOrderProcessModel.getSimpleOrderId());
			invalidateParam.remove(SimpleOrderConstants.HOURS);
			invalidateParam.remove(SimpleOrderConstants.DETAIL);

			RpcResponse<Map<String, Object>> invalidateProgress = activityProgressCrud
					.invalidateProgress(invalidateParam);
			if (!invalidateProgress.isSuccess()) {
				logger.error(String.format("invalidateSimpleOrder()->error:%s", invalidateProgress.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(invalidateProgress.getMessage());
			}

			// 查询流程状态
			JSONObject json = new JSONObject();
			json.put(SimpleOrderConstants.PROCESSID, simpleOrderProcessModel.getProcessId());
			RpcResponse<Map<String, Object>> response = activityProgressQuery.getProcessStatus(json);
			if (!response.isSuccess()) {
				logger.error(String.format("[invalidateSimpleOrder()->error:%s]", response.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(response.getMessage());
			}

			// 更改locman作业工单状态
			SimpleOrderProcess order = new SimpleOrderProcess();
			order.setId(simpleOrderProcessModel.getSimpleOrderId());
			order.setProcessState(response.getSuccessValue().get(ActivityConstans.ACTIVITY_STATUS) + "");
			order.setUpdateTime(DateUtils.formatDate(new Date()));
			int model = simpleOrderRepository.updateModel(order);
			if (model <= 0) {
				logger.error("[invalidateSimpleOrder()->error:修改数据库失败！<=0]");
				return RpcResponseBuilder.buildErrorRpcResp("[invalidateSimpleOrder()->error:修改数据库失败！<=0]");
			}
			logger.info(String.format("invalidateSimpleOrder()->success:%s", invalidateProgress.getMessage()));
			return RpcResponseBuilder.buildSuccessRpcResp("success", simpleOrderProcessModel.getProcessId());
		} catch (Exception e) {
			logger.error("invalidateSimpleOrder()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
