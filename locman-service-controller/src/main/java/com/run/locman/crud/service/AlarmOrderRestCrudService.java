/*
 * File name: AlarmOrderRestCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年12月5日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.activity.api.crud.ActivityProgressCrud;
import com.run.activity.api.query.ActivityProgressQuery;
import com.run.activity.api.query.service.ProcessFileQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.ParamChecker;
import com.run.common.util.StringUtil;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.AlarmInfoCrudService;
import com.run.locman.api.crud.service.AlarmOrderCrudService;
import com.run.locman.api.crud.service.DeviceInfoCudService;
import com.run.locman.api.crud.service.FaultOrderProcessCudService;
import com.run.locman.api.drools.service.AlarmRuleInvokInterface;
import com.run.locman.api.dto.CountAlarmOrderDto;
import com.run.locman.api.entity.AlarmInfo;
import com.run.locman.api.entity.AlarmOrder;
import com.run.locman.api.entity.Device;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.query.service.FaultOrderProcessQueryService;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.api.timer.crud.service.AlramOrderRemindTimerService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.ConvertUtil;
import com.run.locman.api.util.InfoPushUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.AlarmOrderConstants;
import com.run.locman.constants.AlarmOrderCountConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.SimpleOrderConstants;
import com.run.locman.filetool.ExcelView;
import com.run.locman.filetool.FastDfsUtil;
import com.run.rabbit.activity.server.RabbitMqCrudService;
import com.run.usc.base.query.UserBaseQueryService;

/**
 *
 * @Description:告警工单写入
 * @author: lkc
 * @version: 1.0, 2017年12月5日
 */
@Service
public class AlarmOrderRestCrudService {
	private Logger							logger		= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private AlarmOrderQueryService			alarmOrderQueryService;
	@Autowired
	private ActivityProgressCrud			activityProgressCrud;
	@Autowired
	private ActivityProgressQuery			activityProgressQuery;
	@Autowired
	private AlarmOrderCrudService			alarmOrderCrudService;

	@Autowired
	private FaultOrderProcessCudService		faultOrderProcessCudService;

	@Autowired
	private FaultOrderProcessQueryService	faultOrderProcessQueryService;

	@Autowired
	private AlarmRuleInvokInterface			alarmRuleInvokInterface;

	@Autowired
	private FactoryQueryService				factoryQueryService;

	@Autowired
	private AlarmInfoQueryService			alarmInfoQueryService;

	@Autowired
	private AlarmInfoCrudService			alarmInfoCrudService;
	@Autowired
	private UserBaseQueryService			userBaseQueryService;
	@Autowired
	private OrderProcessQueryService		orderProcessQueryService;
	@Autowired
	DeviceQueryService						deviceQueryService;

	@Autowired
	private ProcessFileQueryService			processFileQueryService;

	@Autowired
	private RabbitMqCrudService				rabbitMqSendClient;

	@Autowired
	private DeviceInfoCudService			deviceInfoCudService;

	@Value("${api.host}")
	private String							ip;

	@Autowired
	private HttpServletRequest				request;

	private static final String				SIX			= "完成审核中";
	
	private static final String				NUMBER_FIVE	= "5";

	private static final String				FIVE		= "待处理";

	private static final String				FORE		= "已完成";

	private static final String				THREE		= "转故障已完成";

	private static final String				TWO			= "转故障被拒绝";

	private static final String				ONE			= "转故障审批中";

	private static final String				ZERO		= "处理中";
	@Autowired
	private AlramOrderRemindTimerService       alramOrderRemindTimerService;


	public Result<Boolean> saveAlarmOrder(String param) {
		logger.info(String.format("[saveAlarmOrder()->request params:%s]", param));
		if (StringUtil.isEmpty(param)) {
			logger.error(String.format("[saveAlarmOrder()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(param)) {
			logger.error(String.format("[saveAlarmOrder()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject json = JSON.parseObject(param);
			String alarmId = json.getString("alarmId");
			String accessSecret = json.getString("accessSecret");
			RpcResponse<AlarmInfo> findById = alarmInfoQueryService.findById(alarmId);
			if (!findById.isSuccess()) {
				logger.error(String.format("[saveAlarmOrder()->error:%s]", findById.getMessage()));
				return ResultBuilder.failResult(findById.getMessage());
			}
			AlarmInfo alarmInfo = findById.getSuccessValue();
			AlarmOrder alarmOrder = new AlarmOrder();
			Result<Object> saveAlarmOrderHandle = saveAlarmOrderHandle(alarmId, accessSecret, alarmInfo, alarmOrder);
			if (null != saveAlarmOrderHandle && !CommonConstants.NUMBER_FOUR_ZERO
					.equals(saveAlarmOrderHandle.getResultStatus().getResultCode())) {
				return ResultBuilder.failResult(saveAlarmOrderHandle.getResultStatus().getResultMessage());
			}

			// 保存告警工单和告警信息关系
			JSONObject jsonParm = new JSONObject();
			jsonParm.put("alarmId", alarmId);
			RpcResponse<Map<String, Object>> saveAlarmOrder = alarmOrderCrudService.saveAlarmOrderandInfo(jsonParm);
			if (!saveAlarmOrder.isSuccess()) {
				logger.info(String.format("[saveAlarm_Order()->error:%s]", "保存告警工单和告警信息关系失败!"));
			}
			// 告警工单保存成功，更新告警信息(将此告警信息之前的告警信息都置为已处理)
			Map<String, Object> map = getMap(alarmInfo);
			RpcResponse<Boolean> disposeAlarmInfo = alarmInfoCrudService.disposeAlarmInfo(map);
			if (!disposeAlarmInfo.isSuccess() || !disposeAlarmInfo.getSuccessValue()) {
				logger.info("[saveAlarm_Order()->error:告警信息状态更新失败]");
				return ResultBuilder.failResult("告警信息状态更新失败");
			}
			// 告警工单保存成功，推送消息到队列中
			// 查询告警工单流程图id
			JSONObject processInfo = getProcessInfo(accessSecret);

			RpcResponse<String> findBpmnId = orderProcessQueryService.findBpmnId(processInfo);
			String bpmnId = findBpmnId.getSuccessValue();
			if (!findBpmnId.isSuccess() || StringUtils.isBlank(bpmnId)) {
				logger.error(String.format("[saveAlarmOrder()->invalid:%s]", "请检查是否配置或开启流程图！"));
				return ResultBuilder.failResult("告警工单流程图id不存在！");
			}
			// 通过文件id获取发起人节点key(流程图上配置)
			String taskKey = startTaskKeyMethod(accessSecret, processInfo, bpmnId);

			processInfo.put("nodeId", taskKey);
			// 通过节点key查询相应的人
			RpcResponse<List<String>> startUsers = orderProcessQueryService.findStartUsers(processInfo);
			List<String> startUsersId = startUsers.getSuccessValue();
			//开启定时器
			RpcResponse<String> alarmOrderNotReceive=alramOrderRemindTimerService.AlarmOrderNotReceive(startUsersId, alarmOrder);
			if(!alarmOrderNotReceive.isSuccess()) {
				logger.error(String.format("[saveAlarmOrder()->error:%s]", alarmOrderNotReceive.getMessage()));
			}
			if(!NUMBER_FIVE.equals(alarmOrder.getProcessState())) {
				RpcResponse<Boolean> closeScheduleJob = alramOrderRemindTimerService.closeScheduleJob(alarmOrder.getId());
				logger.info("updateAlarmOrder()-->关闭定时器:" + closeScheduleJob.getSuccessValue());
			}
			
			paramCheck(accessSecret, startUsers, startUsersId);
			logger.info(String.format("[saveAlarmOrder()->告警工单id:%s]", alarmOrder.getId()));
			//注释推送
			//getInfoPush(alarmOrder.getId(), FIVE);
			logger.info(String.format("[saveAlarmOrder()->success:%s]", LogMessageContants.SAVE_SUCCESS));
			return ResultBuilder.successResult(Boolean.TRUE, "保存成功");
		} catch (Exception e) {
			logger.error("saveAlarmOrder()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param alarmId
	 * @param accessSecret
	 * @param alarmInfo
	 */

	private Result<Object> saveAlarmOrderHandle(String alarmId, String accessSecret, AlarmInfo alarmInfo,
			AlarmOrder alarmOrder) {
		if (alarmInfo == null) {
			logger.error("[saveAlarmOrder()->error:告警信息id有误]");
			return ResultBuilder.failResult("告警信息id有误");
		}
		if (alarmInfo.getIsDel() == 0) {
			logger.error("[saveAlarmOrder()->error:本条告警已被处理]");
			return ResultBuilder.failResult("本条告警已被处理");
		}

		AlarmOrder alarmOrderSave = getAlarmOrder(alarmId, accessSecret);

		Map<String, Object> updateParamMap = getUpdateParamMap(alarmInfo);
		RpcResponse<String> updateOrderAlarmId = alarmOrderCrudService.updateOrderAlarmId(updateParamMap);
		// update操作成功且返回的告警工单id不为null说明已经有告警工单
		if (updateOrderAlarmId.isSuccess() && !StringUtils.isBlank(updateOrderAlarmId.getSuccessValue())) {
			alarmOrder.setId(updateOrderAlarmId.getSuccessValue());
		} else {
			alarmOrder.setId(alarmOrderSave.getId());
			//++
			alarmOrder.setCreateTime(alarmOrderSave.getCreateTime());
			alarmOrder.setAlarmId(alarmOrderSave.getAlarmId());
			alarmOrder.setProcessState(alarmOrderSave.getProcessState());
			RpcResponse<String> saveAlarmOrderResult = alarmOrderCrudService.saveAlarmOrder(alarmOrderSave);
			if (!saveAlarmOrderResult.isSuccess()) {
				logger.error(String.format("[saveAlarmOrder()->error:%s]", saveAlarmOrderResult.getMessage()));
				return ResultBuilder.failResult(saveAlarmOrderResult.getMessage());
			}
		}
		return null;
	}



	/**
	 * @Description:
	 * @param accessSecret
	 * @param processInfo
	 * @param bpmnId
	 * @return
	 */

	private String startTaskKeyMethod(String accessSecret, JSONObject processInfo, String bpmnId) {
		processInfo.put(AlarmInfoConstants.USC_ACCESS_SECRET, accessSecret);
		processInfo.put("id", bpmnId);
		RpcResponse<String> startTaskKey = processFileQueryService.queryStartTaskKey(processInfo);
		String taskKey = startTaskKey.getSuccessValue();
		if (!startTaskKey.isSuccess() || StringUtils.isBlank(taskKey)) {
			logger.error(String.format("[saveAlarmOrder()->invalid:%s]", "未查找到发起人节点key！请检查流程图配置!"));
		}
		return taskKey;
	}



	/**
	 * @Description:
	 * @param accessSecret
	 * @param startUsers
	 * @param startUsersId
	 */

	private void paramCheck(String accessSecret, RpcResponse<List<String>> startUsers, List<String> startUsersId) {
		if (!startUsers.isSuccess() || startUsersId.size() == 0) {
			logger.error(String.format("[saveAlarmOrder()->invalid:%s]", "未查询到发起节点下人员配置！"));
			// return ResultBuilder.failResult("未查找到节点下人员,推送消息失败!");
		}

		// 调用rabbitMq推送消息
		for (String userId : startUsersId) {
			if (!jointPushQueue(userId, accessSecret)) {
				logger.error(String.format("[saveAlarmOrder()->error:%s]", "推送消息失败：" + userId));
			}
		}
	}



	/**
	 * @Description:
	 * @param alarmId
	 * @param accessSecret
	 * @return
	 */

	private AlarmOrder getAlarmOrder(String alarmId, String accessSecret) {
		AlarmOrder alarmOrder = new AlarmOrder();
		String alarmOrderId = UtilTool.getUuId();
		alarmOrder.setId(alarmOrderId);
		alarmOrder.setAlarmId(alarmId);
		alarmOrder.setAccessSecret(accessSecret);
		// 生成工单，默认状态为待处理
		alarmOrder.setProcessState("5");
		alarmOrder.setCreateTime(DateUtils.formatDate(new Date()));
		return alarmOrder;
	}



	/**
	 * @Description:
	 * @param accessSecret
	 * @return
	 */

	private JSONObject getProcessInfo(String accessSecret) {
		JSONObject processInfo = new JSONObject();
		processInfo.put("processSign", "alarmProcess");
		processInfo.put("manageState", "enabled");
		processInfo.put("accessSecret", accessSecret);
		return processInfo;
	}



	/**
	 * @Description:
	 * @param alarmInfo
	 * @return
	 */

	private Map<String, Object> getUpdateParamMap(AlarmInfo alarmInfo) {
		Map<String, Object> updateParamMap = Maps.newHashMap();
		updateParamMap.put("deviceId", alarmInfo.getDeviceId());
		// updateParamMap.put("alarmDesc", alarmInfo.getAlarmDesc());
		// updateParamMap.put("rule", alarmInfo.getRule());
		updateParamMap.put("alarmId", alarmInfo.getId());
		updateParamMap.put("createTime", DateUtils.formatDate(new Date()));
		return updateParamMap;
	}



	/**
	 * @Description:
	 * @param alarmInfo
	 * @return
	 */

	private Map<String, Object> getMap(AlarmInfo alarmInfo) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("deviceId", alarmInfo.getDeviceId());
		map.put("alarmDesc", alarmInfo.getAlarmDesc());
		map.put("rule", alarmInfo.getRule());
		map.put("alarmTime", alarmInfo.getAlarmTime());
		return map;
	}



	// TODO 工单改变时参数
	/**
	 * 
	 * @Description: 获取工单信息并推送
	 * @param
	 * @return
	 */
	private void getInfoPush(String orderId, String alarmOrderState) {
		try {
			RpcResponse<Map<String, Object>> changedAlarmOrderInfoByOrderId = alarmOrderQueryService
					.getChangedAlarmOrderInfoByOrderId(orderId);
			if (!changedAlarmOrderInfoByOrderId.isSuccess()) {
				logger.error(String.format("[getInfoPush()-->%s]", changedAlarmOrderInfoByOrderId.getMessage()));
				return;
			}
			Map<String, Object> orderInfo = changedAlarmOrderInfoByOrderId.getSuccessValue();
			Object object = orderInfo.get("accessSecret");
			if (null == object || "".equals(object + "")) {
				logger.error(String.format("[getInfoPush()-->接入方密钥为空:%s]", object));
				return;
			}
			String accessSecret = object + "";
			String receiveUrl = InfoPushUtil.WhetherPush(accessSecret, ip);
			if (StringUtils.isBlank(receiveUrl)) {
				logger.info(String.format("[getInfoPush()-->密钥为:%s的接入方没有配置推送接收地址,故不推送]", accessSecret));
				return;
			}
			JSONObject json = new JSONObject();
			json.put("alarmOrderId", orderId);
			json.put("serialNums", orderInfo.getOrDefault("serialNum", ""));
			json.put("deviceId", orderInfo.getOrDefault("deviceId", ""));
			json.put("facilitiesId", orderInfo.getOrDefault("facilitiesId", ""));
			// 告警工单各个环节为不同方法,所以操作成功后可以直接判断当前状态
			json.put("alarmOrderState", alarmOrderState);
			json.put("stateChangeTime", new Date());
			InfoPushUtil.InfoPush(receiveUrl, json, InfoPushUtil.ALARM_ORDER_CHANGE);
			return;
		} catch (Exception e) {
			logger.error("[getInfoPush()-->Exception:" + e);
		}

		/*
		 * new Thread(){ public void run(){ try { sleep(1500);
		 * RpcResponse<Map<String, Object>> changedAlarmOrderInfoByOrderId =
		 * alarmOrderQueryService .getChangedAlarmOrderInfoByOrderId(orderId);
		 * if (!changedAlarmOrderInfoByOrderId.isSuccess()) {
		 * logger.error(String.format("[getInfoPush()-->%s]",
		 * changedAlarmOrderInfoByOrderId.getMessage())); return; } Map<String,
		 * Object> orderInfo = changedAlarmOrderInfoByOrderId.getSuccessValue();
		 * Object object = orderInfo.get("accessSecret"); if (null == object ||
		 * "".equals(object + "")) {
		 * logger.error(String.format("[getInfoPush()-->接入方密钥为空:%s]", object));
		 * return; } String accessSecret = object + ""; String receiveUrl =
		 * InfoPushUtil.WhetherPush(accessSecret, ip); if
		 * (StringUtils.isBlank(receiveUrl)) { logger.info(String.format(
		 * "[getInfoPush()-->密钥为:%s的接入方没有配置推送接收地址,故不推送]", accessSecret));
		 * return; } JSONObject json = new JSONObject();
		 * json.put("alarmOrderId", orderId); json.put("serialNums",
		 * orderInfo.getOrDefault("serialNum","")); json.put("deviceId",
		 * orderInfo.getOrDefault("deviceId","")); json.put("alarmOrderState",
		 * orderInfo.getOrDefault("orderState",""));
		 * InfoPushUtil.InfoPush(receiveUrl, json,
		 * InfoPushUtil.ALARM_ORDER_CHANGE); return; } catch (Exception e) {
		 * logger.error("[getInfoPush()-->Exception:" + e); } } }.start();
		 */

	}



	public Result<Boolean> orderComplete(String params) {
		try {
			logger.info(String.format("[orderComplete()->request params:%s]", params));
			Result<String> result = ExceptionChecked.checkRequestParam(params);
			if (result != null) {
				logger.error(String.format("[orderComplete()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
						LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(params);
			Result<Object> noBusinessResult = noBusinessResult(parseObject);
			if (null != noBusinessResult) {
				return ResultBuilder.noBusinessResult();
			}
			String orderId = parseObject.getString(AlarmOrderConstants.ORDER_ID);
			// 查询告警工单及其对应的所有告警信息(按规则分组)
			RpcResponse<List<Map<String, Object>>> alarmOrderAndAllAlarmInfo = alarmOrderQueryService
					.getAlarmOrderAndAllAlarmInfo(orderId);

			if (!alarmOrderAndAllAlarmInfo.isSuccess() || alarmOrderAndAllAlarmInfo.getSuccessValue() == null) {
				logger.error(String.format("[orderComplete()->error:%s]", "告警工单信息和相关告警信息查询失败！工单id:" + orderId));
				return ResultBuilder.failResult("告警工单信息和相关告警信息查询失败！");
			}

			List<Map<String, Object>> alarmOrderAndAllAlarmInfoValue = alarmOrderAndAllAlarmInfo.getSuccessValue();

			// 流程id 此循环内流程id都一样,所以存一个在后面使用
			String processId = "";
			StringBuffer alarmDescInfo = new StringBuffer();
			Map<String, Object> parmaMap = Maps.newHashMap();
			List<String> alarmDescs = Lists.newArrayList();
			// 一个工单对于一个设备id的原则
			String deviceId = "";
			// 此循环中,还需要:1.对告警信息的修改操作进行修改 2.日志打印具体没完成验证的规则描述
			for (Map<String, Object> info : alarmOrderAndAllAlarmInfoValue) {
				processId = info.get(AlarmOrderConstants.PROCESS_ID) + "";
				// 得到设备id，校验设备状态是否真实完成
				deviceId = (String) info.get(AlarmOrderConstants.DEVICE_ID);
				String rule = (String) info.get(AlarmOrderConstants.DEVICE_ALARM_RULE);
				String alarmDesc = info.get(AlarmInfoConstants.ALARM_DESC) + "";
				String alarmLevel = info.get(AlarmInfoConstants.ALARMI_LEVEL) + "";

				Result<Object> methodFor = methodFor(alarmDescInfo, parmaMap, alarmDescs, deviceId, rule, alarmDesc,
						alarmLevel);
				if (null != methodFor) {
					return ResultBuilder.failResult(methodFor.getResultStatus().getResultMessage());
				}

			}

			Result<Object> orderHandle = orderHandle(parseObject, orderId, processId, alarmDescInfo, parmaMap,
					alarmDescs, deviceId);
			if (null != orderHandle) {
				return ResultBuilder.failResult(orderHandle.getResultStatus().getResultMessage());
			}
			
			//注释推送
			//getInfoPush(orderId, SIX);
			logger.info(String.format("[orderComplete()->suc:%s！]", MessageConstant.UPDATE_SUCCESS));
			return ResultBuilder.successResult(true, MessageConstant.UPDATE_SUCCESS);

		} catch (Exception e) {
			logger.error("orderComplete()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param parseObject
	 * @param orderId
	 * @param processId
	 * @param alarmDescInfo
	 * @param parmaMap
	 * @param alarmDescs
	 * @param deviceId
	 */

	private Result<Object> orderHandle(JSONObject parseObject, String orderId, String processId,
			StringBuffer alarmDescInfo, Map<String, Object> parmaMap, List<String> alarmDescs, String deviceId) {
		// 设备id查询是否存在故障工单，并且通过一审的
		RpcResponse<String> findFaultOrderByDeviceIdTo = faultOrderProcessQueryService
				.findFaultOrderByDeviceIdTo(deviceId);
		if (!findFaultOrderByDeviceIdTo.isSuccess()) {
			logger.error(String.format("[orderComplete()->error:%s]", findFaultOrderByDeviceIdTo.getMessage()));
			return ResultBuilder
					.failResult(String.format("[orderComplete()->error:%s]", findFaultOrderByDeviceIdTo.getMessage()));
		}

		// 如果查询出来的故障工单设备id不存在，那么就没有故障工单，需要检查是否修复！
		String faultOrderDeviceId = findFaultOrderByDeviceIdTo.getSuccessValue();

		if (StringUtils.isBlank(faultOrderDeviceId)) {
			// 告警规则描述信息不为空,则有未修复的属性,故不能完成
			if (alarmDescInfo.length() > 0) {
				alarmDescInfo.deleteCharAt(alarmDescInfo.lastIndexOf("/"));
				logger.error("设备未修复完成！告警规则描述:" + alarmDescInfo);
				return ResultBuilder.failResult("设备未修复完成！告警规则描述:" + alarmDescInfo);
			}
		}
		// 判断是否存在到场图片presentPic
		Map<String, Object> findPresentPic = alarmOrderQueryService.getAlarmOrderInfoById(orderId).getSuccessValue();
		String presentPic = findPresentPic.get("presentPic") + "";
		if (presentPic.equals("")) {
			return ResultBuilder.failResult("还未上传到场图片，不能完成处理！");
		}

		// 具体工单完成操作
		parseObject.put(AlarmOrderConstants.PROCESS_ID, processId);
		parseObject.put(AlarmOrderConstants.USERID, parseObject.getString(AlarmOrderConstants.APPLY_USERID));
		RpcResponse<Map<String, Object>> acceptProgress = activityProgressCrud.rejectProgress(parseObject);

		if (!acceptProgress.isSuccess() || acceptProgress.getSuccessValue() == null
				|| "false".equals(acceptProgress.getSuccessValue().get(AlarmOrderConstants.STATE) + "")) {
			logger.error("处理工单信息失败-->完成工单失败");
			return ResultBuilder.failResult("处理工单信息失败");
		}

		RpcResponse<Map<String, Object>> processStatus = activityProgressQuery.getProcessStatus(parseObject);
		if (processStatus == null || !processStatus.isSuccess() || processStatus.getSuccessValue() == null) {
			logger.error("[orderComplete()->error:查询工单状态数据异常！]");
			return ResultBuilder.failResult("查询工单状态数据异常！");
		}

		// 工单完成图片数据存储到mysql（endPic）
		List<Object> endPic = parseObject.getJSONArray("orderPic");
		RpcResponse<String> addEndPicAlarmOrder = alarmOrderCrudService.addEndPicAlarmOrder(orderId, endPic);
		if (!addEndPicAlarmOrder.isSuccess() || addEndPicAlarmOrder.getSuccessValue() == null) {
			logger.error("存储完成处理图片路径失败！");
			return ResultBuilder.failResult("完成处理图片数据存储异常！");
		}

		// 更改工单状态信息和告警列表信息
		// 3表示告警已处理的状态
		parmaMap.put("isDel", 3);
		AlarmOrder ala = new AlarmOrder();
		ala.setId(orderId);
		ala.setProcessState((String) processStatus.getSuccessValue().get(AlarmOrderConstants.STATE));
		ala.setAlarmOrderStateTypeId(parseObject.getString("tId"));

		// 需要修改多条告警信息的状态
		parmaMap.put(AlarmInfoConstants.ALARM_DESC, alarmDescs);

		RpcResponse<Boolean> updateAlarmOrderAndInfo = alarmInfoCrudService.updateAlarmOrderAndInfo(parmaMap, ala);

		if (!updateAlarmOrderAndInfo.isSuccess()) {
			logger.error(String.format("[orderComplete()->error:%s]", "更新告警工单or告警信息失败！" + parmaMap));
			return ResultBuilder.failResult(updateAlarmOrderAndInfo.getMessage());
		}
		return null;
	}



	/**
	 * @Description:
	 * @param alarmDescInfo
	 * @param parmaMap
	 * @param alarmDescs
	 * @param deviceId
	 * @param rule
	 * @param alarmDesc
	 * @param alarmLevel
	 * @throws Exception
	 */

	private Result<Object> methodFor(StringBuffer alarmDescInfo, Map<String, Object> parmaMap, List<String> alarmDescs,
			String deviceId, String rule, String alarmDesc, String alarmLevel) throws Exception {
		RpcResponse<JSONObject> lastState = deviceQueryService.queryDeviceLastState(deviceId);
		JSONObject reportedJson = null;
		if (lastState.isSuccess() && null != lastState.getSuccessValue()) {
			reportedJson = lastState.getSuccessValue();
		} else {
			logger.error(String.format("[orderComplete()->error:%s]", "查询设备当前状态失败，或该设备不存在！id:" + deviceId));
			return ResultBuilder.failResult("查询设备当前状态失败，或该设备不存在！");
		}

		JSONObject reported = UtilTool.getReported(reportedJson);
		// 获取实时数据
		Map<String, Object> deviceRealState = Maps.newHashMap();
		if (reported != null) {
			// 解析上报数据 Set<String> keySet =
			Set<String> keySet = reported.keySet();

			for (String propName : keySet) {
				deviceRealState.put(propName, reported.get(propName));
			}
		}
		deviceRealState.put(AlarmInfoConstants.DEVICEID, deviceId);
		deviceRealState.put(AlarmInfoConstants.ENGINE_CHECK, new Boolean(true));

		// 校验该设备是否为平衡告警设备,若是则不检测当前状态是否正常,直接进行完成操作
		RpcResponse<Device> device = deviceQueryService.queryDeviceByDeviceId(deviceId);
		// 检测是否是平衡告警设备
		// 不是平衡告警设备
		boolean isBalance = true;
		if (device.isSuccess() && null != device.getSuccessValue()) {
			// 是平衡告警设备
			isBalance = false;
		} else if (!device.isSuccess()) {
			logger.error("[deviceQueryService.queryDeviceByDeviceId()->warn：告警时校验该设备是否为平衡告警设备失败！" + device.getMessage()
					+ "]");
			return ResultBuilder.failResult("[告警时校验该设备是否为平衡告警设备失败！]");
		}

		// 调用规则引擎api接口，检查设备是否真的处理完结并且显示正常 ;如果是平衡告警设备,不检查设备是否正常
		Boolean invokAlarmCheck = alarmRuleInvokInterface.invokAlarmCheck(deviceRealState, rule);
		if (invokAlarmCheck && isBalance) {
			logger.error("设备未修复完成！告警规则描述:" + alarmDesc);
			alarmDescInfo.append(alarmDesc + "/");
			// return ResultBuilder.failResult("设备未修复完成！告警规则描述:" +
			// alarmDesc);
		}
		parmaMap.put(AlarmOrderConstants.DEVICE_ID, deviceId);
		parmaMap.put(AlarmInfoConstants.ALARMI_LEVEL, alarmLevel);
		alarmDescs.add(alarmDesc);
		return null;
	}



	/**
	 * @Description:
	 * @param parseObject
	 */

	private Result<Object> noBusinessResult(JSONObject parseObject) {
		if (!parseObject.containsKey(AlarmOrderConstants.ORDER_ID)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.ORDER_ID))) {
			logger.error(String.format("[orderComplete()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.ORDER_ID));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.NORMAL_ORDER_TYPE)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.NORMAL_ORDER_TYPE))) {
			logger.error(String.format("[orderComplete()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.NORMAL_ORDER_TYPE));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.NORMAL_REMARK)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.NORMAL_REMARK))) {
			logger.error(String.format("[orderComplete()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.NORMAL_REMARK));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.USC_ACCESS_SECRET)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.USC_ACCESS_SECRET))) {
			logger.error(String.format("[orderComplete()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.USC_ACCESS_SECRET));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.APPLY_USERID)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.APPLY_USERID))) {
			logger.error(String.format("[orderComplete()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.APPLY_USERID));
			return ResultBuilder.noBusinessResult();
		}
		return null;
	}



	/**
	 * @Description:修改告警工单状态
	 * @param orderId
	 * @param processStatus
	 */

	private void updateOrderInfo(String orderId, RpcResponse<Map<String, Object>> processStatus) {
		AlarmOrder ala = new AlarmOrder();
		ala.setId(orderId);
		ala.setProcessState((String) processStatus.getSuccessValue().get(AlarmOrderConstants.STATE));
		alarmOrderCrudService.updateAlarmOrder(ala);
	}



	public Result<Boolean> orderPowerless(String params) {
		try {
			logger.info(String.format("[orderPowerless()->request params:%s]", params));
			Result<String> result = ExceptionChecked.checkRequestParam(params);
			if (result != null) {
				logger.error(String.format("[orderPowerless()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
						LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(params);
			Result<Object> orderPowerlessParamCheck = orderPowerlessParamCheck(parseObject);
			if (null != orderPowerlessParamCheck) {
				return ResultBuilder.noBusinessResult();
			}
			String orderId = parseObject.getString(AlarmOrderConstants.ORDER_ID);
			// 根据工单id查询工单信息
			RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService
					.getAlarmOrderInfoById(parseObject.getString(AlarmOrderConstants.ORDER_ID));
			if (!alarmOrderInfoById.isSuccess() || alarmOrderInfoById.getSuccessValue() == null) {
				logger.error("[orderPowerless()->error:未查到工单信息！]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> successValue = alarmOrderInfoById.getSuccessValue();
			parseObject.put(AlarmOrderConstants.PROCESS_ID, successValue.get(AlarmOrderConstants.PROCESS_ID));
			parseObject.put(AlarmOrderConstants.USERID, parseObject.getString(AlarmOrderConstants.POWERLESS_USERID));

			// 通过设备id查询该设备id是否存在故障工单
			String deviceId = (String) successValue.get(DeviceContants.DEVICEID);
			List<String> deviceIds = Lists.newArrayList();
			deviceIds.add(deviceId);
			RpcResponse<List<String>> findFaultOrderByDeviceIdRes = faultOrderProcessQueryService
					.findFaultOrderByDeviceId(deviceIds);
			if (!findFaultOrderByDeviceIdRes.isSuccess()) {
				logger.info("[orderPowerless()->error:]" + findFaultOrderByDeviceIdRes.getMessage());
				return ResultBuilder.failResult(findFaultOrderByDeviceIdRes.getMessage());
			}
			List<String> faultOrderIds = findFaultOrderByDeviceIdRes.getSuccessValue();
			if (!faultOrderIds.isEmpty()) {
				logger.info("[orderPowerless()->error:]" + deviceIds.toString());
				return ResultBuilder.failResult("该告警设备已存在故障工单！请直接结束告警工单！");
			}

			// 流程处理
			RpcResponse<Map<String, Object>> acceptProgress = activityProgressCrud.acceptProgress(parseObject);
			if (null == acceptProgress) {
				logger.error("通过流程失败");
				return ResultBuilder.failResult("流程处理失败-->通过流程失败");
			}
			if (!acceptProgress.isSuccess() || acceptProgress.getSuccessValue() == null
					|| "false".equals(acceptProgress.getSuccessValue().get(AlarmOrderConstants.STATE))) {
				logger.error(String.format("[orderPowerless()->error:%s]", acceptProgress.getMessage()));
				return ResultBuilder.failResult(acceptProgress.getMessage());
			}
			RpcResponse<Map<String, Object>> processStatus = activityProgressQuery.getProcessStatus(parseObject);
			if (processStatus == null || !processStatus.isSuccess() || processStatus.getSuccessValue() == null) {
				logger.error(String.format("[orderPowerless()->error:%s]", acceptProgress.getMessage()));
				return ResultBuilder.failResult(acceptProgress.getMessage());
			}
			updateOrderInfo(orderId, processStatus);
			//注释推送
			//getInfoPush(orderId, ONE);
			logger.info("[[orderPowerless()->success]");
			return ResultBuilder.successResult(true, MessageConstant.UPDATE_SUCCESS);
		} catch (Exception e) {
			logger.error("orderPowerless()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param parseObject
	 */

	private Result<Object> orderPowerlessParamCheck(JSONObject parseObject) {
		if (!parseObject.containsKey(AlarmOrderConstants.ORDER_ID)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.ORDER_ID))) {
			logger.error(String.format("[orderPowerless()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.ORDER_ID));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.POWERLESS_ORDER_TYPE)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.POWERLESS_ORDER_TYPE))) {
			logger.error(String.format("[orderPowerless()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.POWERLESS_ORDER_TYPE));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.POWERLESS_REMARK)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.POWERLESS_REMARK))) {
			logger.error(String.format("[orderPowerless()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.POWERLESS_REMARK));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.USC_ACCESS_SECRET)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.USC_ACCESS_SECRET))) {
			logger.error(String.format("[orderPowerless()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.USC_ACCESS_SECRET));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.POWERLESS_USERID)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.POWERLESS_USERID))) {
			logger.error(String.format("[orderPowerless()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.POWERLESS_USERID));
			return ResultBuilder.noBusinessResult();
		}
		return null;
	}



	public Result<String> orderApprove(String params) {
		try {
			logger.info(String.format("[orderApprove->request params:%s]", params));
			Result<String> result = ExceptionChecked.checkRequestParam(params);
			if (result != null) {
				logger.error(String.format("[orderApprove()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
						LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(params);

			Result<Object> orderApproveParamCheck = orderApproveParamCheck(parseObject);
			if (null != orderApproveParamCheck) {
				return ResultBuilder.noBusinessResult();
			}
			String orderId = parseObject.getString(AlarmOrderConstants.ORDER_ID);

			// 根据工单id查询工单信息
			RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService
					.getAlarmOrderInfoById(parseObject.getString(AlarmOrderConstants.ORDER_ID));

			if (!alarmOrderInfoById.isSuccess() || alarmOrderInfoById.getSuccessValue() == null) {
				logger.error("[orderApprove()->error:未查到工单信息]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> successValue = alarmOrderInfoById.getSuccessValue();

			// 通过设备id查询该设备id是否存在故障工单
			String deviceId = (String) successValue.get(DeviceContants.DEVICEID);
			Result<String> orderExist = orderExist(successValue, deviceId);
			if (null != orderExist) {
				return ResultBuilder.failResult(orderExist.getResultStatus().getResultMessage());
			}

			JSONObject parm = getJsonObject(parseObject, successValue);
			// 生成故障工单,先生成工单在告诉规则引擎进行到下一步
			JSONObject parmFau = new JSONObject();
			Result<String> orderApproveHandle = orderApproveHandle(parseObject, orderId, successValue, deviceId, parm,
					parmFau);
			if (null != orderApproveHandle) {
				return ResultBuilder.failResult(orderApproveHandle.getResultStatus().getResultMessage());
			}

			// 查询父类工单
			String parentId = InterGatewayUtil.getHttpValueByGet(
					"/interGateway/v3/organization/parentIds/" + parseObject.getString("organizeId"), ip,
					request.getHeader(InterGatewayConstants.TOKEN));
			parmFau.put(SimpleOrderConstants.ORGANIZEID, parentId);

			// 调用生成工单
			RpcResponse<JSONObject> addOrUpdateFaultOrder = faultOrderProcessCudService.addOrUpdateFaultOrder(parmFau);
			if (null == addOrUpdateFaultOrder) {
				logger.error("工单生成失败");
				return ResultBuilder.failResult("工单生成失败");
			}
			if (addOrUpdateFaultOrder == null || addOrUpdateFaultOrder.getSuccessValue() == null
					|| !addOrUpdateFaultOrder.isSuccess()) {
				logger.error(String.format("[orderApprove()->error:%s]", addOrUpdateFaultOrder.getMessage()));
				return ResultBuilder.failResult(addOrUpdateFaultOrder.getMessage());
			}
			
			String faultOrderId = addOrUpdateFaultOrder.getSuccessValue().getString("faultOrderId");
			AlarmOrder ala = new AlarmOrder();
			Result<String> alaHandle = alaHandle(orderId, deviceId, parm, faultOrderId, ala);
			if (null != alaHandle) {
				return ResultBuilder.failResult(alaHandle.getResultStatus().getResultMessage());
			}
			//注释推送
			//getInfoPush(orderId, THREE);
			logger.info("[orderApprove()->success:转故障工单成功！]");
			return ResultBuilder.successResult(faultOrderId, "转故障工单成功！");

		} catch (Exception e) {
			logger.error("orderApprove()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param successValue
	 * @param deviceId
	 */

	private Result<String> orderExist(Map<String, Object> successValue, String deviceId) {
		List<String> deviceIds = Lists.newArrayList();
		deviceIds.add(deviceId);
		RpcResponse<List<String>> findFaultOrderByDeviceIdRes = faultOrderProcessQueryService
				.findFaultOrderByDeviceId(deviceIds);
		if (!findFaultOrderByDeviceIdRes.isSuccess()) {
			logger.info("[orderPowerless()->error:]" + findFaultOrderByDeviceIdRes.getMessage());
			return ResultBuilder.failResult(findFaultOrderByDeviceIdRes.getMessage());
		}
		List<String> faultOrderIds = findFaultOrderByDeviceIdRes.getSuccessValue();
		if (!faultOrderIds.isEmpty()) {
			logger.info("[orderPowerless()->error:]" + deviceIds.toString());
			return ResultBuilder.failResult("该告警设备已存在故障工单！无法再生成故障工单，请拒绝！");
		}

		// 判断是否已经生成故障工单
		if (successValue.get(AlarmOrderConstants.FAULT_ORDER_ID) != null) {
			logger.info(String.format("[orderApprove()->suc:已生成故障工单:%s]",
					successValue.get(AlarmOrderConstants.FAULT_ORDER_ID).toString()));
			return ResultBuilder.successResult(successValue.get(AlarmOrderConstants.FAULT_ORDER_ID).toString(),
					"已生成故障工单！");
		}
		return null;
	}



	/**
	 * @Description:
	 * @param parseObject
	 * @param successValue
	 * @return
	 */

	private JSONObject getJsonObject(JSONObject parseObject, Map<String, Object> successValue) {
		JSONObject parm = new JSONObject();
		parm.put(AlarmOrderConstants.PROCESS_ID, successValue.get(AlarmOrderConstants.PROCESS_ID));
		parm.put(AlarmOrderConstants.APPROVE_USERID, parseObject.getString(AlarmOrderConstants.APPROVE_USERID));
		parm.put(AlarmOrderConstants.USERID, parseObject.getString(AlarmOrderConstants.APPROVE_USERID));
		parm.putAll(parseObject);
		return parm;
	}



	/**
	 * @Description:
	 * @param orderId
	 * @param deviceId
	 * @param parm
	 * @param faultOrderId
	 * @param ala
	 */

	private Result<String> alaHandle(String orderId, String deviceId, JSONObject parm, String faultOrderId,
			AlarmOrder ala) {
		ala.setId(orderId);
		ala.setFaultOrderId(faultOrderId);
		// 告警工单修改操作（添加故障工单外键faultOrderId）
		alarmOrderCrudService.updateAlarmOrder(ala);
		// 通知规则引擎
		RpcResponse<Map<String, Object>> acceptProgress = activityProgressCrud.acceptProgress(parm);
		if (!acceptProgress.isSuccess() || acceptProgress.getSuccessValue() == null
				|| "false".equals(acceptProgress.getSuccessValue().get(AlarmOrderConstants.STATE))) {
			logger.error(String.format("[orderApprove()->error:%s]", acceptProgress.getMessage()));
			return ResultBuilder.failResult(acceptProgress.getMessage());
		}
		RpcResponse<Map<String, Object>> processStatus = activityProgressQuery.getProcessStatus(parm);
		if (processStatus == null || !processStatus.isSuccess() || processStatus.getSuccessValue() == null) {
			logger.error("[orderApprove()->error:查询工单数据异常！]");
			return ResultBuilder.failResult("查询工单数据异常！");
		}
		updateOrderInfo(orderId, processStatus);
		// 修改设备状态，转故障之后，设备维护
		updateDeviceState(deviceId);
		return null;
	}



	/**
	 * @Description:
	 * @param parseObject
	 * @param orderId
	 * @param successValue
	 * @param deviceId
	 * @param parm
	 * @param parmFau
	 * @return
	 * @throws Exception
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Result<String> orderApproveHandle(JSONObject parseObject, String orderId, Map<String, Object> successValue,
			String deviceId, JSONObject parm, JSONObject parmFau) throws Exception {
		parmFau.put("organizeId", parseObject.getString("organizeId"));
		RpcResponse<Map<String, Object>> processVariables = activityProgressQuery.getProcessVariables(parm);

		if (processVariables == null || processVariables.getSuccessValue() == null) {
			logger.error("[orderApprove()->error:告警工单完成，转故障工单失败！]");
			return ResultBuilder.failResult("告警工单完成，转故障工单失败！");
		}
		Map<String, Object> mapVariables = processVariables.getSuccessValue();
		parmFauPut(parseObject, orderId, parmFau, mapVariables);

		String managerId = (String) mapVariables.get(AlarmOrderConstants.POWERLESS_USERID);
		RpcResponse userByUserId = userBaseQueryService.getUserByUserId(managerId);
		if (!userByUserId.isSuccess()) {
			logger.error(String.format("[orderApprove()->error:%s]", userByUserId.getMessage()));
			return ResultBuilder.failResult(userByUserId.getMessage());
		}
		if (userByUserId.getSuccessValue() == null) {
			logger.error("[orderApprove()->error:未查到用户工单申请人信息!]");
			return ResultBuilder.failResult("未查到用户工单申请人信息!");
		}
		Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
		if (userInfo.get(CommonConstants.USERNAME) == null) {
			parmFau.put("manager", userInfo.get("loginAccount"));
		} else {
			parmFau.put("manager", userInfo.get("userName"));
		}
		parmFau.put("phone", userInfo.get("mobile"));
		if (successValue.containsKey(AlarmOrderConstants.DEVICE_ID)) {
			List<String> deviceList = new ArrayList<>();
			deviceId = (String) successValue.get(AlarmOrderConstants.DEVICE_ID);
			deviceList.add(deviceId);
			parmFau.put("deviceIdsAdd", deviceList);
			RpcResponse<List<Map<String, Object>>> queryDeviceDetail = deviceQueryService
					.queryBatchDeviceInfoForDeviceIds(deviceList);
			if (queryDeviceDetail == null || !queryDeviceDetail.isSuccess()
					|| queryDeviceDetail.getSuccessValue() == null || queryDeviceDetail.getSuccessValue().isEmpty()) {
				logger.error("[orderApprove()->error:设备未绑定设备组 !]");
				return ResultBuilder.failResult("设备未绑定设备组 !");
			}
			Map<String, Object> factoryInfo = queryDeviceDetail.getSuccessValue().get(0);
			if (factoryInfo == null || !factoryInfo.containsKey(CommonConstants.APPTAG)) {
				logger.error("[orderApprove()->error:未查到有效appTage!]");
				return ResultBuilder.failResult("未查到有效appTage");
			}

			String appTag = (String) factoryInfo.get("appTag");

			RpcResponse<List<Map<String, Object>>> queryFactoryInfoByAppTag = factoryQueryService
					.queryFactoryInfoByAppTag(appTag);
			if (queryFactoryInfoByAppTag == null || !queryFactoryInfoByAppTag.isSuccess()
					|| queryFactoryInfoByAppTag.getSuccessValue() == null) {
				logger.error("[orderApprove()->error:未查到厂家信息，appTage可能未绑定厂家！]");
				return ResultBuilder.failResult("未查到厂家信息，appTage可能未绑定厂家！");
			}
			List<Map<String, Object>> facInfo = queryFactoryInfoByAppTag.getSuccessValue();
			if (facInfo == null || facInfo.isEmpty() || !facInfo.get(0).containsKey(CommonConstants.FACTORYID)) {
				logger.error("[orderApprove()->error:未查到厂家信息，appTage可能未绑定厂家！]");
				return ResultBuilder.failResult("未查到厂家信息，appTage可能未绑定厂家！");
			}
			parmFau.put("factoryId", facInfo.get(0).get("factoryId"));
		}
		return null;
	}



	/**
	 * @Description:
	 * @param parseObject
	 * @param orderId
	 * @param parmFau
	 * @param mapVariables
	 */

	private void parmFauPut(JSONObject parseObject, String orderId, JSONObject parmFau,
			Map<String, Object> mapVariables) {
		if (mapVariables.containsKey(AlarmOrderConstants.POWERLESS_ORDER_PIC)) {
			parmFau.put(AlarmOrderConstants.POWERLESS_ORDER_PIC,
					mapVariables.get(AlarmOrderConstants.POWERLESS_ORDER_PIC));
		}
		if (mapVariables.containsKey(AlarmOrderConstants.POWERLESS_REMARK)) {
			parmFau.put("mark", mapVariables.get(AlarmOrderConstants.POWERLESS_REMARK));
		}
		if (mapVariables.containsKey(AlarmOrderConstants.POWERLESS_ORDER_TYPE)) {
			parmFau.put("faultType", mapVariables.get(AlarmOrderConstants.POWERLESS_ORDER_TYPE));
		}
		parmFau.put("type", "add");
		parmFau.put("accessSecret", parseObject.getString(AlarmOrderConstants.USC_ACCESS_SECRET));
		parmFau.put("deviceCount", 1);
		parmFau.put("faultProcessType", "1");
		parmFau.put("orderName", "告警转故障工单");
		parmFau.put("orderImg", mapVariables.get(AlarmOrderConstants.POWERLESS_ORDER_PIC));
		// 通过工单id获取流程id（告警流程） ——》获取发起人 ws1
		parmFau.put("userId", (String) mapVariables.get(AlarmOrderConstants.POWERLESS_USERID));
		parmFau.put("orderId", orderId);
	}



	/**
	 * @Description:
	 * @param parseObject
	 */

	private Result<Object> orderApproveParamCheck(JSONObject parseObject) {
		if (!parseObject.containsKey(AlarmOrderConstants.ORDER_ID)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.ORDER_ID))) {
			logger.error(String.format("[orderApprove()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.ORDER_ID));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(CommonConstants.ORGANIZEID)
				|| StringUtils.isBlank(parseObject.getString(CommonConstants.ORGANIZEID))) {
			logger.error(String.format("[orderApprove()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					"organizeId"));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.USC_ACCESS_SECRET)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.USC_ACCESS_SECRET))) {
			logger.error(String.format("[orderApprove()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.USC_ACCESS_SECRET));
			return ResultBuilder.noBusinessResult();
		}
		if (!parseObject.containsKey(AlarmOrderConstants.APPROVE_USERID)
				|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.APPROVE_USERID))) {
			logger.error(String.format("[orderApprove()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					AlarmOrderConstants.APPROVE_USERID));
			return ResultBuilder.noBusinessResult();
		}
		return null;
	}



	/**
	 * @Description:修改设备的状态
	 */

	private void updateDeviceState(String deviceId) {
		List<String> deviceIds = Lists.newArrayList();
		deviceIds.add(deviceId);
		RpcResponse<Boolean> updateDeviceDefendState = deviceInfoCudService.updateDeviceDefendState(deviceIds, false);
		if (!updateDeviceDefendState.isSuccess()) {
			logger.error(String.format("[updateDeviceState()->error:%s]", updateDeviceDefendState.getMessage()));
		}
	}



	public Result<Boolean> orderReject(String params) {
		try {
			logger.info(String.format("[orderReject->request params:%s]", params));
			Result<String> result = ExceptionChecked.checkRequestParam(params);
			if (result != null) {
				logger.error(String.format("[orderRejects()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
						LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(params);

			if (!parseObject.containsKey(AlarmOrderConstants.ORDER_ID)
					|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.ORDER_ID))) {
				logger.error(String.format("orderReject()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
						AlarmOrderConstants.ORDER_ID));
				return ResultBuilder.noBusinessResult();
			}
			if (!parseObject.containsKey(AlarmOrderConstants.REJECT_REMARK)
					|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.REJECT_REMARK))) {
				logger.error(String.format("[orderReject()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
						AlarmOrderConstants.REJECT_REMARK));
				return ResultBuilder.noBusinessResult();
			}
			if (!parseObject.containsKey(AlarmOrderConstants.REJECT_USERID)
					|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.REJECT_USERID))) {
				logger.error(String.format("[orderReject()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
						AlarmOrderConstants.REJECT_USERID));
				return ResultBuilder.noBusinessResult();
			}
			if (!parseObject.containsKey(AlarmOrderConstants.USC_ACCESS_SECRET)
					|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.USC_ACCESS_SECRET))) {
				logger.error(String.format("[orderReject()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
						AlarmOrderConstants.USC_ACCESS_SECRET));
				return ResultBuilder.noBusinessResult();
			}
			String orderId = parseObject.getString(AlarmOrderConstants.ORDER_ID);
			// 根据工单id查询工单信息
			RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService
					.getAlarmOrderInfoById(parseObject.getString(AlarmOrderConstants.ORDER_ID));
			if (!alarmOrderInfoById.isSuccess() || alarmOrderInfoById.getSuccessValue() == null) {
				logger.error("[orderReject()->error:未查到工单信息！]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> successValue = alarmOrderInfoById.getSuccessValue();
			JSONObject parm = new JSONObject();
			parm.put(AlarmOrderConstants.PROCESS_ID, successValue.get(AlarmOrderConstants.PROCESS_ID));
			parm.put(AlarmOrderConstants.USERID, parseObject.getString(AlarmOrderConstants.REJECT_USERID));
			parm.putAll(parseObject);
			RpcResponse<Map<String, Object>> rejectProgress = activityProgressCrud.rejectProgress(parm);
			if (null == rejectProgress) {
				logger.error("流程拒绝失败");
				return ResultBuilder.failResult("流程拒绝失败");
			}

			if (rejectProgress == null || !rejectProgress.isSuccess() || rejectProgress.getSuccessValue() == null
					|| "false".equals(rejectProgress.getSuccessValue().get(AlarmOrderConstants.STATE))) {
				logger.error(String.format("[orderReject()->error:%s]", rejectProgress.getMessage()));
				return ResultBuilder.failResult(rejectProgress.getMessage());
			}
			RpcResponse<Map<String, Object>> processStatus = activityProgressQuery.getProcessStatus(parm);
			if (null == processStatus) {
				logger.error("查询流程状态失败");
				return ResultBuilder.failResult("流程处理失败");
			}
			if (processStatus == null || !processStatus.isSuccess() || processStatus.getSuccessValue() == null) {
				logger.error(String.format("[orderReject()->error:%s]", processStatus.getMessage()));
				return ResultBuilder.failResult(processStatus.getMessage());
			}
			updateOrderInfo(orderId, processStatus);
			//注释推送
			//getInfoPush(orderId, TWO);
			logger.info("[orderReject()->success]");
			return ResultBuilder.successResult(true, MessageConstant.UPDATE_SUCCESS);
		} catch (Exception e) {
			logger.error("orderReject()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> acceptAlarmOrder(String param) {
		logger.info(String.format("[acceptAlarmOrder()->request params:%s]", param));
		try {
			if (ParamChecker.isBlank(param)) {
				logger.error(String.format("[acceptAlarmOrder()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
				return ResultBuilder.emptyResult();
			}
			if (ParamChecker.isNotMatchJson(param)) {
				logger.error(String.format("[acceptAlarmOrder()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject paramJson = JSON.parseObject(param);
			String id = paramJson.getString("id");
			String organizeId = paramJson.getString("organizeId");
			String accessSecret = paramJson.getString("accessSecret");
			String alarmOrderNum = paramJson.getString("alarmOrderNum");
			Result<String> acceptAlarmOrderParamCheck = acceptAlarmOrderParamCheck(id, organizeId, accessSecret,
					alarmOrderNum);
			if (null != acceptAlarmOrderParamCheck) {
				return ResultBuilder.noBusinessResult();
			}
			// 查询父类资源id
			Result<JSONArray> organizeIdHandle = organizeIdHandle(paramJson, id, organizeId, accessSecret);
			if (!CommonConstants.NUMBER_FOUR_ZERO.equals(organizeIdHandle.getResultStatus().getResultCode())) {
				return ResultBuilder.failResult(organizeIdHandle.getResultStatus().getResultMessage());
			}
			String userId = paramJson.getString("userId");
			// 启动流程
			Result<String> startProgress = startProgress(id, accessSecret, alarmOrderNum, organizeIdHandle, userId);
			if (!CommonConstants.NUMBER_FOUR_ZERO.equals(startProgress.getResultStatus().getResultCode())) {
				return ResultBuilder.failResult(startProgress.getResultStatus().getResultMessage());
			}
			// 添加了发起人节点
			JSONObject jsonAcceptParm = new JSONObject();
			jsonAcceptParm.put(AlarmOrderConstants.USERID, userId);
			jsonAcceptParm.put(AlarmOrderConstants.ORDER_ID, id);
			jsonAcceptParm.put(AlarmOrderConstants.PROCESS_ID, startProgress.getValue());
			RpcResponse<Map<String, Object>> acceptProgress = activityProgressCrud.acceptProgress(jsonAcceptParm);
			if (!acceptProgress.isSuccess()
					|| "false".equals((String) ("" + acceptProgress.getSuccessValue().get("state")))) {
				logger.error("[acceptAlarmOrdert()->error:节点处理失败]");
				return ResultBuilder.failResult("节点处理失败！:" + acceptProgress.getMessage());
			}
			String userName = paramJson.getString("userName");
			String phone = paramJson.getString("phone");
			if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(userName) || StringUtil.isEmpty(phone)) {
				logger.error("[acceptAlarmOrdert()->error:userId or UserName 为空]");
				return ResultBuilder.noBusinessResult();
			}
			AlarmOrder alarmOrder = new AlarmOrder();
			alarmOrder.setId(id);
			alarmOrder.setProcessId(startProgress.getValue());
			RpcResponse<Map<String, Object>> processStatus = activityProgressQuery.getProcessStatus(jsonAcceptParm);
			if (!processStatus.isSuccess()) {
				logger.error("[acceptAlarmOrdert()->error:工单状态查询失败！]");
				return ResultBuilder.failResult("工单状态查询失败！");
			}
			alarmOrder.setProcessState((String) processStatus.getSuccessValue().get("status"));
			alarmOrder.setUserId(userId);
			alarmOrder.setUserName(userName);
			alarmOrder.setPhone(phone);
			alarmOrder.setReceiveTime(DateUtils.formatDate(new Date()));
			// 更新工单
			RpcResponse<Boolean> updateAlarmOrder = alarmOrderCrudService.updateAlarmOrder(alarmOrder);
			if (!updateAlarmOrder.isSuccess()) {
				logger.error(String.format("[acceptAlarmOrdert()->error:%s]", updateAlarmOrder.getMessage()));
				return ResultBuilder.failResult(updateAlarmOrder.getMessage());
			}
			if (!updateAlarmOrder.getSuccessValue()) {
				logger.error("[acceptAlarmOrdert()->error:告警工单更新失败！]");
				return ResultBuilder.failResult("告警工单更新失败");
			}
			// TODO//注释推送
			//getInfoPush(id, ZERO);
			logger.info("[acceptAlarmOrdert()->success:告警工单更新成功]");
			return ResultBuilder.successResult(id, "告警工单更新成功");
		} catch (Exception e) {
			logger.error("acceptAlarmOrder()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param id
	 * @param accessSecret
	 * @param alarmOrderNum
	 * @param organizeIdHandle
	 * @param userId
	 * @return
	 */

	private Result<String> startProgress(String id, String accessSecret, String alarmOrderNum,
			Result<JSONArray> organizeIdHandle, String userId) {
		JSONObject json = new JSONObject();
		json.put("type", "alarmProcess");
		// 存入工单id
		json.put(AlarmOrderConstants.ORDER_ID, id);
		json.put(AlarmOrderConstants.ID, id);
		json.put(AlarmOrderConstants.USERID, userId);
		json.put(SimpleOrderConstants.ACTIVITY_USEROBJ, organizeIdHandle.getValue());
		json.put(SimpleOrderConstants.ACCESSSECRET, accessSecret);
		json.put(SimpleOrderConstants.ORDER_NUM, alarmOrderNum);
		RpcResponse<Map<String, Object>> startProgressResult = activityProgressCrud.startProgress(json);
		if (!startProgressResult.isSuccess()) {
			logger.error(String.format("[acceptAlarmOrdert()->error:%s]", startProgressResult.getMessage()));
			return ResultBuilder.failResult(startProgressResult.getMessage());
		}
		if (startProgressResult.getSuccessValue() == null) {
			logger.error("[acceptAlarmOrdert()->error:流程启动失败]");
			return ResultBuilder.failResult("流程启动失败");
		}
		String processId = startProgressResult.getSuccessValue().get("processId").toString();
		return ResultBuilder.successResult(processId, "执行成功!");
	}



	/**
	 * @Description:
	 * @param paramJson
	 * @param id
	 * @param organizeId
	 * @param accessSecret
	 * @return
	 */

	private Result<JSONArray> organizeIdHandle(JSONObject paramJson, String id, String organizeId,
			String accessSecret) {
		String parentId = InterGatewayUtil.getHttpValueByGet("/interGateway/v3/organization/parentIds/" + organizeId,
				ip, request.getHeader(InterGatewayConstants.TOKEN));
		paramJson.put(SimpleOrderConstants.ORGANIZEID, parentId);

		RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService.getAlarmOrderInfoById(id);
		if (!alarmOrderInfoById.isSuccess()) {
			logger.error(String.format("[acceptAlarmOrdert()->error:%s]", alarmOrderInfoById.getMessage()));
			return ResultBuilder.failResult(alarmOrderInfoById.getMessage());
		}

		if (alarmOrderInfoById.getSuccessValue() == null) {
			logger.error("[acceptAlarmOrdert()->error:该工单不存在]");
			return ResultBuilder.failResult("该工单不存在");
		}
		if (!NUMBER_FIVE.equals(alarmOrderInfoById.getSuccessValue().get(CommonConstants.PROCESSSTATE))) {
			logger.error("[acceptAlarmOrdert()->error:该工单已被接收，请刷新后重试]");
			return ResultBuilder.failResult("该工单已被接收，请刷新后重试");
		}

		// 根据组织id查询用户节点信息集合
		Map<String, Object> map = Maps.newHashMap();
		map.put(SimpleOrderConstants.ORGANIZEID, paramJson.getString(SimpleOrderConstants.ORGANIZEID));
		map.put("processType", "告警工单流程");
		map.put("accessSecret", accessSecret);
		RpcResponse<JSONArray> nodeInfoResponse = orderProcessQueryService.queryNodeInfoForActivity(map);
		JSONArray jsonArray = null;
		if (nodeInfoResponse.isSuccess()) {
			logger.info(nodeInfoResponse.getMessage());
			jsonArray = nodeInfoResponse.getSuccessValue();
			if (jsonArray.isEmpty()) {
				return ResultBuilder.failResult("请求人员非法操作");
			}
		} else {
			logger.info(nodeInfoResponse.getMessage());
			return ResultBuilder.failResult(nodeInfoResponse.getMessage());
		}
		return ResultBuilder.successResult(jsonArray, "执行成功!");
	}



	/**
	 * @Description:
	 * @param id
	 * @param organizeId
	 * @param accessSecret
	 * @param alarmOrderNum
	 */

	private Result<String> acceptAlarmOrderParamCheck(String id, String organizeId, String accessSecret,
			String alarmOrderNum) {
		if (StringUtil.isEmpty(id)) {
			logger.error(String.format("[acceptAlarmOrder()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					"id"));
			return ResultBuilder.noBusinessResult();
		}
		if (StringUtil.isEmpty(organizeId)) {
			logger.error(String.format("[acceptAlarmOrder()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					"organizeId"));
			return ResultBuilder.noBusinessResult();
		}
		if (StringUtil.isEmpty(accessSecret)) {
			logger.error(String.format("[acceptAlarmOrder()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					"accessSecret"));
			return ResultBuilder.noBusinessResult();
		}
		if (StringUtil.isEmpty(alarmOrderNum)) {
			logger.error(String.format("[acceptAlarmOrder()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
					"alarmOrderNum"));
			return ResultBuilder.noBusinessResult();
		}
		return null;
	}



	/**
	 *
	 * @Description:拼接队列,推送消息
	 * @param userId
	 * @return
	 */
	private boolean jointPushQueue(String userId, String accessSecret) {
		try {
			String queueName = accessSecret + userId + "alarmProcess-start";
			JSONObject alarmParam = new JSONObject();
			alarmParam.put(AlarmInfoConstants.USC_ACCESS_SECRET, accessSecret);
			alarmParam.put("userId", userId);
			rabbitMqSendClient.pushMessage(queueName, alarmParam);
		} catch (Exception e) {
			logger.error("jointPushQueue()->Exception", e);
			return false;
		}
		return true;
	}



	public ModelAndView exportAllAlarmOrder(JSONObject accessSecretJson, ModelMap model) {
		try {
			logger.info(String.format("[exportAllAlarmOrder()->request params:%s]", accessSecretJson.toString()));
			Object accessSecretStr = accessSecretJson.get(AlarmOrderCountConstants.ACCESSSECRET);
			if (null == accessSecretStr || StringUtils.isBlank(accessSecretStr.toString())) {
				logger.error(String.format("[exportAllAlarmOrder()->error:%s]", "查询业务对象为null！"));
				return null;
			}
			// 设置分页参数,查询所有数据
			accessSecretJson.put(AlarmOrderCountConstants.PAGE_NUM, 0);
			accessSecretJson.put(AlarmOrderCountConstants.PAGE_SIZE, 0);
			accessSecretJson.put(InterGatewayConstants.TOKEN, request.getHeader(InterGatewayConstants.TOKEN));
			// 构建导出格式

			Map<String, Object> map = Maps.newLinkedHashMap();
			map.put(AlarmOrderCountConstants.ORDER_NUMBER, AlarmOrderCountConstants.ORDER_NUMBER_CH);
			map.put(AlarmOrderCountConstants.ALARM_SERIALNUMBER, AlarmOrderCountConstants.ALARM_SERIALNUMBER_CH);
			map.put(AlarmOrderCountConstants.ALARM_TIME, AlarmOrderCountConstants.ALARM_TIME_CH);
			map.put(AlarmOrderCountConstants.ALARM_LEVEL, AlarmOrderCountConstants.ALARM_LEVEL_CH);
			map.put(AlarmOrderCountConstants.PERSON_NAME, AlarmOrderCountConstants.PERSON_NAME_CH);
			map.put(AlarmOrderCountConstants.ORDER_STATE, AlarmOrderCountConstants.ORDER_STATE_CH);
			map.put(AlarmOrderCountConstants.RECEIVE_TIME, AlarmOrderCountConstants.RECEIVE_TIME_CH);
			map.put(AlarmOrderCountConstants.FACILITIES_CODE, AlarmOrderCountConstants.FACILITIES_CODE_CH);
			map.put(AlarmOrderCountConstants.FACILITY_TYPE_ALIAS, AlarmOrderCountConstants.FACILITY_TYPE_ALIAS_CH);
			map.put(AlarmOrderCountConstants.ORGANIZATION_NAME, AlarmOrderCountConstants.ORGANIZATION_NAME_CH);

			model.put(ExcelView.EXCEL_NAME, AlarmOrderCountConstants.EXCEL_NAME_02);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			// 获取所有数据
			RpcResponse<PageInfo<CountAlarmOrderDto>> result = alarmOrderQueryService
					.countAllAlarmOrder(accessSecretJson);
			PageInfo<CountAlarmOrderDto> successValue = result.getSuccessValue();
			if (null == successValue) {
				logger.error("获取数据失败");
				return null;
			}
			List<CountAlarmOrderDto> list = result.getSuccessValue().getList();
			List<Map<String, Object>> resultList = Lists.newArrayList();
			for (CountAlarmOrderDto countAlarmOrderDto : list) {
				Map<String, Object> resultMap = ConvertUtil.beanToMap(countAlarmOrderDto);
				resultList.add(resultMap);
			}

			// 成功封裝数据
			if (result.isSuccess()) {
				logger.info(String.format("[exportAllAlarmOrder()->success:%s]", result.getMessage()));
				model.put(ExcelView.EXCEL_DATASET, resultList);
			} else {
				logger.error(String.format("[exportAllAlarmOrder()->error:%s]", result.getException()));
				return null;
			}

			View excelView = new ExcelView();
			return new ModelAndView(excelView);
		} catch (Exception e) {
			logger.error("exportAllAlarmOrder()->exception", e);
			return null;
		}
	}



	public Result<String> addPresentPic(String id, MultipartFile mfile) {
		if (mfile == null) {
			logger.error("[addPresentPic()->error:图片文件空参数！]");
			return ResultBuilder.noBusinessResult();
		}
		logger.info(String.format("[addPresentPic()->request params:%s,%s]", id, mfile.getName()));
		try {
			File file = new File("/" + mfile.getOriginalFilename());
			FileUtils.copyInputStreamToFile(mfile.getInputStream(), file);
			String picUrl = FastDfsUtil.uploadFile(file, file.getName(), file.length());

			RpcResponse<String> addPic = alarmOrderCrudService.addPresentPicAlarmOrder(id, picUrl);
			if (addPic.isSuccess()) {
				logger.info(String.format("[exportAllAlarmOrder()->success:%s]", addPic.getMessage()));
				return ResultBuilder.successResult(addPic.getSuccessValue(), "图片添加成功");
			} else {
				logger.error("[addPresentPic()->error:图片添加失败！]");
				return ResultBuilder.failResult("图片添加失败！");
			}
		} catch (Exception e) {
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * @Description:
	 * @param id
	 * @param file
	 * @return
	 */

	public Result<String> addAppPic(String params) {
		if (ParamChecker.isBlank(params)) {
			logger.error(String.format("[addAppPic()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(params)) {
			logger.error(String.format("[addAppPic()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject json = JSON.parseObject(params);
			String id = json.getString("id");
			String picUrl = json.getString("picUrl");
			RpcResponse<String> addPic = alarmOrderCrudService.addPresentPicAlarmOrder(id, picUrl);
			if (addPic.isSuccess()) {
				logger.info(String.format("[addAppPic()->success:%s]", addPic.getMessage()));
				return ResultBuilder.successResult(addPic.getSuccessValue(), "图片上传成功！");
			} else {
				logger.error("[addAppPic()->error:图片添加失败！]");
				return ResultBuilder.failResult("图片添加失败！");
			}

		} catch (Exception e) {
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:告警工单点击完成后进入完工审批流程，同意告警完工
	 * @param jsonObject
	 * @return
	 */
	public Result<Boolean> approveBeforeEnd(JSONObject jsonObject) {

		logger.info(String.format("[进入approveBeforeComplete()->request params:%s]", jsonObject.toString()));
		try {
			// 业务key校验
			RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger, "approveBeforeComplete",
					jsonObject, AlarmOrderConstants.ORDER_ID, AlarmOrderConstants.USC_ACCESS_SECRET, AlarmOrderConstants.APPROVE_COMPLETE_USERID,
					"processId", "processState");
			if (containsParamKey != null) {
				logger.error(String.format("[approveBeforeComplete->error:%s]", containsParamKey.getMessage()));
				return ResultBuilder.failResult(containsParamKey.getMessage());
			}
			// 业务value校验
			RpcResponse<Object> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger, "approveBeforeComplete",
					jsonObject, AlarmOrderConstants.ORDER_ID, AlarmOrderConstants.USC_ACCESS_SECRET,AlarmOrderConstants.APPROVE_COMPLETE_USERID,
					"processId", "processState");
			if (checkBusinessKey != null) {
				logger.error(String.format("[approveBeforeComplete->error:%s]", checkBusinessKey.getMessage()));
				return ResultBuilder.failResult(checkBusinessKey.getMessage());
			}

			if (jsonObject.getIntValue("processState") != 6) {
				logger.error("[approveBeforeComplete->error:该工单未处于完工待审核状态]");
				return ResultBuilder.failResult("该工单未处于完工待审核状态");
			}
			// 用message控制流程走向，yes代表审批通过

			jsonObject.put("message", "yes");
			//为了通过工作流的校验，需要userId
			jsonObject.put(AlarmOrderConstants.USERID, jsonObject.get(AlarmOrderConstants.APPROVE_COMPLETE_USERID));

			RpcResponse<Map<String, Object>> dealProgress = activityProgressCrud.dealProgress(jsonObject);
			if (null == dealProgress) {
				logger.error("[approveBeforeComplete->error:节点操作失败，流程完工审批失败]");
				return ResultBuilder.failResult("流程完工审批失败");
			}
			if (!dealProgress.isSuccess() || dealProgress.getSuccessValue() == null
					|| "false".equals(dealProgress.getSuccessValue().get(AlarmOrderConstants.STATE))) {
				logger.error(String.format("[approveBeforeComplete()->error:%s]", dealProgress.getMessage()));
				return ResultBuilder.failResult(dealProgress.getMessage());
			}
			// 查询工单状态
			RpcResponse<Map<String, Object>> processStatus = activityProgressQuery.getProcessStatus(jsonObject);
			if (processStatus == null || !processStatus.isSuccess() || processStatus.getSuccessValue() == null) {
				logger.error("[approveBeforeComplete()->error:查询工单状态数据异常！]");
				return ResultBuilder.failResult("查询工单状态数据异常！");
			}

			// 更新工单状态
			updateOrderInfo(jsonObject.getString(AlarmOrderConstants.ORDER_ID), processStatus);

			logger.info(String.format("[approveBeforeComplete->success:告警工单完工审核结束,orderId:%s,审核通过]",
					jsonObject.getString(AlarmOrderConstants.ORDER_ID)));
			//注释推送
			//getInfoPush(jsonObject.getString(AlarmOrderConstants.ORDER_ID), FORE);
			return ResultBuilder.successResult(true,
					"告警工单完工审核通过成功，工单id为" + jsonObject.getString(AlarmOrderConstants.ORDER_ID));

		} catch (Exception e) {
			logger.error("[approveBeforeEnd->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:告警工单点击完成后进入完工审批流程，拒绝告警完工
	 * @param jsonObject
	 * @return
	 */
	public Result<Boolean> rejectBeforeEnd(JSONObject jsonObject) {

		logger.info(String.format("[进入rejectBeforeEnd()->request params:%s]", jsonObject.toString()));
		try {
			// 业务key校验
			RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger, "rejectBeforeEnd",
					jsonObject, AlarmOrderConstants.ORDER_ID, AlarmOrderConstants.USC_ACCESS_SECRET, AlarmOrderConstants.REJECT_COMPLETE_USERID,
					"processId", "processState");
			if (containsParamKey != null) {
				logger.error(String.format("[rejectBeforeEnd->error:%s]", containsParamKey.getMessage()));
				return ResultBuilder.failResult(containsParamKey.getMessage());
			}
			// 业务value校验
			RpcResponse<Object> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger, "rejectBeforeEnd",
					jsonObject, AlarmOrderConstants.ORDER_ID, AlarmOrderConstants.USC_ACCESS_SECRET, AlarmOrderConstants.REJECT_COMPLETE_USERID,
					"processId", "processState");
			if (checkBusinessKey != null) {
				logger.error(String.format("[rejectBeforeEnd->error:%s]", checkBusinessKey.getMessage()));
				return ResultBuilder.failResult(checkBusinessKey.getMessage());
			}

			if (jsonObject.getIntValue("processState") != 6) {
				logger.error("[rejectBeforeEnd->error:该工单未处于完工待审核状态]");
				return ResultBuilder.failResult("该工单未处于完工待审核状态");
			}

			if (!jsonObject.containsKey(AlarmOrderConstants.REJECT_REMARK)) {
				logger.error("[rejectBeforeEnd()->error:传入key中无reject_remark]");
				return ResultBuilder.failResult("传入参数无拒绝理由");
			}
			if (StringUtils.isBlank(jsonObject.getString(AlarmOrderConstants.REJECT_REMARK))) {
				logger.error("[rejectBeforeEnd()->error：拒绝理由为空]");
				return ResultBuilder.failResult("拒绝理由不能为空");
			}
			// 用message控制流程走向，no代表审批拒绝
			jsonObject.put("message", "no");
			// 为了通过工作流的校验，需要userId
			jsonObject.put(AlarmOrderConstants.USERID, jsonObject.get(AlarmOrderConstants.REJECT_COMPLETE_USERID));

			RpcResponse<Map<String, Object>> dealProgress = activityProgressCrud.dealProgress(jsonObject);
			if (null == dealProgress) {
				logger.error("[rejectBeforeEnd->error:节点操作失败，流程完工审批失败]");
				return ResultBuilder.failResult("流程完工审批失败");
			}
			if (!dealProgress.isSuccess() || dealProgress.getSuccessValue() == null
					|| "false".equals(dealProgress.getSuccessValue().get(AlarmOrderConstants.STATE))) {
				logger.error(String.format("[rejectBeforeEnd()->error:%s]", dealProgress.getMessage()));
				return ResultBuilder.failResult(dealProgress.getMessage());
			}
			// 查询工单状态
			RpcResponse<Map<String, Object>> processStatus = activityProgressQuery.getProcessStatus(jsonObject);
			if (processStatus == null || !processStatus.isSuccess() || processStatus.getSuccessValue() == null) {
				logger.error("[rejectBeforeEnd()->error:查询工单状态数据异常！]");
				return ResultBuilder.failResult("查询工单状态数据异常！");
			}

			// 更新工单状态
			// updateOrderInfo(jsonObject.getString(AlarmOrderConstants.ORDER_ID),
			// processStatus);

			// 需要修改告警信息状态，否则不能继续完成
			// 查询工单对应的告警信息id
			RpcResponse<List<Map<String, Object>>> alarmIdByAlarmOrderId = alarmOrderQueryService
					.getAlarmIdByAlarmOrderId(jsonObject.getString("orderId"));
			if (alarmIdByAlarmOrderId == null || !alarmIdByAlarmOrderId.isSuccess()) {
				logger.error("[rejectBeforeEnd->error:查询告警信息失败]");
				return ResultBuilder.failResult("查询相应告警信息失败");
			}
			// 获取告警id
			List<String> alarmIds = new ArrayList<>();
			for (Map<String, Object> map : alarmIdByAlarmOrderId.getSuccessValue()) {
				alarmIds.add(map.get("alarmId").toString());
			}

			AlarmOrder ala = new AlarmOrder();
			ala.setId(jsonObject.getString(AlarmOrderConstants.ORDER_ID));
			ala.setProcessState((String) processStatus.getSuccessValue().get(AlarmOrderConstants.STATE));

			// 修改相应告警信息和工单状态
			RpcResponse<Boolean> updateAlarmOrderAndInfoForCompeleteReject = alarmInfoCrudService
					.updateAlarmOrderAndInfoForCompeleteReject(alarmIds, ala);
			if (!updateAlarmOrderAndInfoForCompeleteReject.isSuccess()) {
				logger.error("[rejectBeforeEnd->error:修改告警信息和工单状态失败]");
				return ResultBuilder.failResult("修改告警信息和工单状态失败");
			}

			logger.info(String.format("[rejectBeforeEnd->success:告警工单完工审核结束,orderId:%s,审核拒绝]",
					jsonObject.getString(AlarmOrderConstants.ORDER_ID)));
			//注释推送
			//getInfoPush(jsonObject.getString(AlarmOrderConstants.ORDER_ID), ZERO);
			return ResultBuilder.successResult(true,
					"告警工单完工审核拒绝成功，工单id为" + jsonObject.getString(AlarmOrderConstants.ORDER_ID));

		} catch (Exception e) {
			logger.error("[rejectBeforeEnd->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
