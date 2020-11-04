/*
 * File name: AlarmInfoQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2018年1月18日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.run.activity.api.query.service.ProcessFileQueryService;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.common.util.UUIDUtil;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.AlarmInfoCrudRepository;
import com.run.locman.api.crud.repository.AlarmOrderCrudRepository;
import com.run.locman.api.crud.repository.BalanceSwitchStateRecordCurdRepository;
import com.run.locman.api.crud.repository.FacilitiesCrudRepository;
import com.run.locman.api.crud.repository.RemoteControlRecordCrudRepository;
import com.run.locman.api.crud.repository.SmsRegistCrudRepository;
import com.run.locman.api.crud.service.AlarmInfoCrudService;
import com.run.locman.api.crud.service.AlarmOrderCrudService;
import com.run.locman.api.crud.service.SmsRegistService;
import com.run.locman.api.crud.service.UpdateRedisCrudService;
import com.run.locman.api.entity.AlarmInfo;
import com.run.locman.api.entity.AlarmOrder;
import com.run.locman.api.entity.AlarmRule;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.entity.RemoteControlRecord;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.query.service.BalanceSwitchStateRecordQueryService;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.api.query.service.SmsQueryService;
import com.run.locman.api.query.service.SwitchLockRecordQueryService;
import com.run.locman.api.timer.crud.service.AlramOrderRemindTimerService;
import com.run.locman.api.util.InfoPushUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.PhoneFormatCheckUtil;
import com.run.locman.api.util.SendSms;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.RuleContants;
import com.run.rabbit.activity.server.RabbitMqCrudService;
import com.run.sms.api.JiguangService;

import entity.JiguangEntity;

/**
 * @Description:告警信息cud
 * @author: lkc
 * @version: 1.0, 2017年11月2日
 */
@Transactional(rollbackFor = Exception.class)
public class AlarmInfoCrudServiceImpl implements AlarmInfoCrudService {

	@Autowired
	private AlarmInfoCrudRepository					deviceAlarmInfoCrudRepository;

	@Autowired
	private AlarmOrderCrudRepository				alarmOrderCrudRepository;

	@Autowired
	private FacilitiesCrudRepository				faciRespon;

	@Autowired
	private BalanceSwitchStateRecordCurdRepository	balanceRespon;

	@Autowired
	private RemoteControlRecordCrudRepository		remoteRespon;

	@Autowired
	private UserRoleBaseQueryService				userRoleQueryService;

	@Autowired
	private AlarmInfoQueryService					alarmInfoQueryService;

	@Autowired
	private AlarmOrderCrudService					alarmOrderCrudService;

	@Autowired
	private FactoryQueryService						factoryQueryService;

	@Autowired
	private RabbitMqCrudService						rabbitMqSendClient;

	@Autowired
	private OrderProcessQueryService				orderProcessQueryService;

	@Autowired
	private ProcessFileQueryService					processFileQueryService;

	@Autowired
	private SimpleOrderQueryService					simpleOrderQueryService;

	@Autowired
	private BalanceSwitchStateRecordQueryService	balanceService;

	@Autowired
	SwitchLockRecordQueryService					lockService;

	@Autowired
	private SmsQueryService							smsQueryService;
	@Autowired
	private SmsRegistService						smsRegistService;
	@Autowired
	private JiguangService							jiguangService;
	@Autowired
	private SmsRegistCrudRepository					smsRegistCrudRepository;
	@Autowired
	private AlarmOrderQueryService					alarmOrderQueryService;
	@Autowired
	private UpdateRedisCrudService					updateRedisCrudService;
	@Value("${api.host}")
	private String									ip;
	@Value("${sms.catelogId}")
	private String									catelogId;

	@Autowired
	private AlramOrderRemindTimerService			alramOrderRemindTimerService;
	private static final Logger						logger	= Logger.getLogger(CommonConstants.LOGKEY);

	/**
	 * 专用变量,不能他用
	 */
	private Object									rollBackPoint;
	/**
	 * 地址文本显示长度最大值
	 */
	private int										size	= 25;



	/**
	 * 触发了告警规则后进入的方法 map 参数封装了设备id，告警规则，上报时间
	 *
	 * @see com.run.locman.api.crud.service.AlarmInfoCrudService#add(java.util.Map)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public RpcResponse<AlarmInfo> add(Map<String, Object> map) {
		logger.info(DateUtils.formatDate(new Date()) + "***进入add方法:" + System.currentTimeMillis());
		logger.info("触发了告警规则，进入该方法，成功接收参数" + map);
		try {
			// 事务回滚点,如果出现异常,据此回滚到指定点,避免次要数据异常导致主要数据全部回滚;
			rollBackPoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
		} catch (NoTransactionException e) {
			logger.error("AlarmInfoCrudServiceImpl()->exception", e);
		}
		try {

			// 判断该设备是否绑定设施
			String deviceId = this.getStringValue(map.get(AlarmInfoConstants.DEVICEID));
			Facilities facility = faciRespon.getFacByDeviceId(deviceId);
			String facilityId = facility.getId();
			String accessSecret = facility.getAccessSecret();
			logger.info(DateUtils.formatDate(new Date()) + "***判断该设备是否绑定设施:" + System.currentTimeMillis());
			if (StringUtils.isBlank(facilityId)) {
				logger.warn("[AlarmInfoCrudServiceImpl->add()->warn：该设备下没有绑定设施，无法产生告警！]");
				return RpcResponseBuilder.buildErrorRpcResp(String.format("设备%s下没有绑定设施，无法产生告警！", deviceId));
			}

			// @6该设施不处于未屏蔽状态，不产生告警
			if (!facility.getDefenseState().equals("1")) {
				logger.info("add()-->该设施的defenseState不为1,，处于屏蔽（维护/待整治/故障中）状态，不产生告警，设施id:" + facilityId);
				return RpcResponseBuilder.buildSuccessRpcResp("不产生告警！", null);
			}

			// @1判断该设备是否是一体化智能人井，若是，且最近一条开关锁记录里为开，并且设备上报的也为开(则为设备正常开启，则X加速度变化产生的告警屏蔽)
			Boolean checkLock = checkLock(map, accessSecret, deviceId);
			logger.info(DateUtils.formatDate(new Date()) + "***判断该设备是否一体化智能人井:" + System.currentTimeMillis());
			if (checkLock) {
				logger.info("[add()-->checkLock()-->该设备为一体化智能人井,且此时已为正常开启状态,不产生告警!]");
				return RpcResponseBuilder.buildSuccessRpcResp("该设备为一体化智能人井,且此时已为正常开启状态,不产生告警!", null);
			}

			// @2 判断该设备是否是通过平衡开关方式打开的，如果此时状态为打开，不会执行告警判断
			Boolean checkBal = checkBalance(deviceId, facilityId, accessSecret);
			logger.info(DateUtils.formatDate(new Date()) + "***判断该设备是否是通过平衡开关方式打开的:" + System.currentTimeMillis());
			if (checkBal) {
				logger.info("[add()-->checkBalance()-->该设备为平衡告警设备,且此时已为开启状态,不产生告警!]");
				return RpcResponseBuilder.buildSuccessRpcResp("该设备为平衡告警设备,且此时已为开启状态,不产生告警!", null);
			}

			// @3判断命令开启是否合法
			boolean checkRec = checkControlRecord(map, deviceId);
			logger.info(DateUtils.formatDate(new Date()) + "***判断命令开启是否合法:" + System.currentTimeMillis());
			if (checkRec) {
				logger.info("add()-->checkControlRecord()-->设备正常远控开启,不产生告警!");
				return RpcResponseBuilder.buildSuccessRpcResp("设备正常远控开启", null);
			}

			// @4判断是否在点击关闭按钮后三分钟内有上报，若是，则不触发告警。如果超过三分钟。则告警
			boolean checkCloseTime = checkBalanceCloseRecord(deviceId, accessSecret, map);
			if (checkCloseTime) {
				logger.info("[add()-->checkBalanceCloseRecord()-->该设备为平衡告警设备，且关闭时间不超过3分钟，不产生告警！]");
				return RpcResponseBuilder.buildSuccessRpcResp("该设备为平衡告警设备，且关闭时间不超过3分钟，不产生告警", null);
			}

			// @5一般工单审批通过之后在工单开始时间前后三分钟不告警
			boolean simpleCloseAlarm = simpleOrderCloseAlarm(deviceId, accessSecret);
			if (simpleCloseAlarm) {
				logger.info("add()-->simpleOrderCloseAlarm()-->不产生告警！");
				return RpcResponseBuilder.buildSuccessRpcResp("不产生告警！", null);
			}
			// @7有未完成的告警工单，且该工单已经有到场图片，不产生告警
			boolean checkAlarmOrderPresenPic = checkAlarmOrderPresenPic(deviceId, accessSecret);
			if (checkAlarmOrderPresenPic) {
				logger.info("[add()-->checkAlarmOrderPresenPic()-->该设备已经有未完成的告警工单，且工单有到场图片，不产生告警]");
				return RpcResponseBuilder.buildSuccessRpcResp("该设备已经有未完成的告警工单，且工单有到场图片，不产生告警", null);
			}

			// @7其他方式（应急钥匙、强开）生成告警记录
			RpcResponse<AlarmInfo> result = createAlarmInfo(map, deviceId, facility, facilityId, accessSecret);
			// RpcResponse<AlarmInfo> result = createAlarmInfo(map, deviceId,
			// facility, facilityId, accessSecret);
			if (result.isSuccess()) {
				logger.info("add()-->createAlarmInfo()生成告警记录成功！");
				return result;
			} else {
				logger.error("add()-->createAlarmInfo()生成告警记录失败！");
				return RpcResponseBuilder.buildErrorRpcResp("保存失败");
			}

		} catch (Exception e) {
			logger.error("AlarmInfoCrudServiceImpl()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(rollBackPoint);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @Description:一般工单前后三分钟不告警
	 * @param deviceId
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private boolean simpleOrderCloseAlarm(String deviceId, String accessSecret) throws Exception {
		logger.info(String.format("[simpleOrderCloseAlarm()->进入方法 request 参数：[%s],[%s]]", deviceId, accessSecret));

		// 查询工单开始时间
		RpcResponse<String> orderStartTime = simpleOrderQueryService.querySimpleOrderStartTime(accessSecret, deviceId);
		// 判断时间是否在工单开始时间前后三分钟内，若在则不告警。
		if (!orderStartTime.isSuccess()) {
			return false;
		} else {
			String time = orderStartTime.getSuccessValue();
			if (null == time) {
				return false;
			} else {
				Long timeNow = System.currentTimeMillis();
				// 三分钟前
				Long threeMinBefore = DateUtils.toDate(time).getTime() - 180000;
				// 三分钟后
				Long threeMinAfter = DateUtils.toDate(time).getTime() + 180000;
				if (timeNow >= threeMinBefore && timeNow <= threeMinAfter) {
					return true;
				} else {
					return false;
				}
			}
		}
	}



	/**
	 * @Description:生成告警记录并且发消息
	 * @param map
	 * @param deviceId
	 * @param facility
	 * @param facilityId
	 * @param accessSecret
	 * @throws Exception
	 */
	private RpcResponse<AlarmInfo> createAlarmInfo(Map<String, Object> map, String deviceId, Facilities facility,
			String facilityId, String accessSecret) throws Exception {
		// 保存告警信息
		String facilityTypeName = facility.getFacilityTypeAlias();
		String facilityTypeId = facility.getFacilitiesTypeId();
		String facilitiesCode = facility.getFacilitiesCode();
		String organizationId = facility.getOrganizationId();
		String reportTime = this.getStringValue(map.get(AlarmInfoConstants.REPORTTIME));
		AlarmRule alarmRule = (AlarmRule) map.get(AlarmInfoConstants.RULE);
		String address = facility.getAddress();
		if (address.length() > size) {
			address = address.substring(0, 20) + "...";
		}
		String rule = alarmRule.getRule();
		int alarmLevel = alarmRule.getAlarmLevel();
		boolean isMatchOrder = alarmRule.getIsMatchOrder();
		String alarmTime = DateUtils.formatDate(new Date());
		String ruleName = alarmRule.getRuleName();
		JSONObject ruleContent = JSON.parseObject(alarmRule.getRuleContent());
		String alarmId = UtilTool.getUuId();

		String alarmItem = ruleContent.getString("key");
		AlarmInfo deviceAlarmInfo = createAlarmInfo(deviceId, facilityId, accessSecret, facilityTypeId, reportTime,
				rule, alarmLevel, isMatchOrder, alarmTime, ruleName, alarmId, alarmItem);

		logger.info("即将保存告警信息:" + deviceAlarmInfo);
		int insertModelResult = deviceAlarmInfoCrudRepository.insertModel(deviceAlarmInfo);
		logger.info(DateUtils.formatDate(new Date()) + "***告警信息保存时间:" + System.currentTimeMillis());
		if (insertModelResult > 0) {
			logger.info("告警信息保存成功!");
			
			//更新redis缓存
			Map<String,Object> redisMap=new HashMap<String,Object>();
			redisMap.put("id", facilityId);
			redisMap.put("accessSecret", accessSecret);
			updateRedisCrudService.updateFacMapCache(redisMap);
			
			// 校验当前是否存在该设备该规则的工单
			Map<String, Object> updateParamMap = Maps.newHashMap();
			updateParamMap.put("deviceId", deviceAlarmInfo.getDeviceId());
			// updateParamMap.put("alarmDesc", deviceAlarmInfo.getAlarmDesc());
			// updateParamMap.put("rule", deviceAlarmInfo.getRule());
			updateParamMap.put("alarmId", deviceAlarmInfo.getId());
			// updateParamMap.put("createTime", DateUtils.formatDate(new
			// Date()));
			RpcResponse<String> updateOrderAlarmId = alarmOrderCrudService.updateOrderAlarmId(updateParamMap);
			if (!updateOrderAlarmId.isSuccess()) {
				logger.error(updateOrderAlarmId.getMessage());
				throw new Exception(updateOrderAlarmId.getException());
			}
			logger.info(DateUtils.formatDate(new Date()) + "***同一个设备同一个规则的告警，仅更新告警ID:" + System.currentTimeMillis());
			String alarmOrderId = updateOrderAlarmId.getSuccessValue();

			// update操作成功且返回的告警工单id不为null说明已经有告警工单
			boolean isAlarmOrderIdBlank = StringUtils.isBlank(alarmOrderId);
			// 如果是自动生成工单。并且数据库里面没有该工单，则自动创建工单,isMatchOrder是否生成工单,true生成false不生成
			if (isMatchOrder && isAlarmOrderIdBlank) {
				// 创建工单以及推送小红点到pc
				logger.info(String.format("[createAlarmInfo()->createOrderAndSendRed(),参数:accessSecret:%s,alarmId:%s]",
						accessSecret, alarmId));
				createOrderAndSendRed(accessSecret, alarmId);
				// 发送短信和推送到app
				sendMessageAndPush(facilityTypeName, facilitiesCode, organizationId, address, alarmLevel, alarmTime,
						accessSecret, ruleName);

			} else if (isMatchOrder || !isAlarmOrderIdBlank) {
				// 保存告警工单和告警信息关系
				updateAlarmInfo(alarmId, alarmOrderId);
				//有工单时，首次触发一条告警规则时才发送短信
				List<String> distinctAlarmDesc = deviceAlarmInfoCrudRepository.getDistinctAlarmDescByDeviceId(deviceAlarmInfo.getDeviceId(),accessSecret);
				if (!distinctAlarmDesc.contains(deviceAlarmInfo.getAlarmDesc())) {
					sendMessageAndPush(facilityTypeName, facilitiesCode, organizationId, address, alarmLevel, alarmTime,
							accessSecret, ruleName);
				}
				
				// 没有工单。规则配置不产生工单
			} else if (isAlarmOrderIdBlank && !isMatchOrder) {
				// 发送短信和推送到app
				sendMessageAndPush(facilityTypeName, facilitiesCode, organizationId, address, alarmLevel, alarmTime,
						accessSecret, ruleName);
			}
			try {
				// 前端地图推送
				webMapPush(deviceId, facilitiesCode, organizationId, alarmLevel, alarmTime, ruleName, alarmId);
				logger.info(DateUtils.formatDate(new Date()) + "***前端地图推送:" + System.currentTimeMillis());
			} catch (Exception e) {
				logger.error("地图推送失败！！！");
			}

			// 告警信息推送,需要先用告警id查询告警信息，这样告警信息里面才有流水号
			//注释推送
			/*AlarmInfo alarmInfoById = deviceAlarmInfoCrudRepository.getAlarmInfoById(alarmId);
			if (alarmId != null) {
				logger.info(String.format("[createAlarmInfo->告警信息查询成功，进行告警信息推送,数据：%s]", alarmInfoById.toString()));
				pushAlarmInfo(alarmInfoById, facility, accessSecret);
			} else {
				logger.info("createAlarmInfo->告警信息查询失败，不进行告警信息推送");
			}*/

			return RpcResponseBuilder.buildSuccessRpcResp("告警信息保存成功", null);
		} else {
			logger.error("告警信息保存失败!");
			throw new Exception("告警信息保存失败");
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private AlarmInfo createAlarmInfo(String deviceId, String facilityId, String accessSecret, String facilityTypeId,
			String reportTime, String rule, int alarmLevel, boolean isMatchOrder, String alarmTime, String ruleName,
			String alarmId, String alarmItem) {
		AlarmInfo deviceAlarmInfo = new AlarmInfo();
		deviceAlarmInfo.setDeviceId(deviceId);
		deviceAlarmInfo.setReportTime(reportTime);
		deviceAlarmInfo.setFacilitiesId(facilityId);
		// 由告警名称来表示告警描述
		deviceAlarmInfo.setAlarmDesc(ruleName);
		deviceAlarmInfo.setRule(rule);
		deviceAlarmInfo.setAlarmLevel(alarmLevel);
		deviceAlarmInfo.setMatchOrder(isMatchOrder);
		deviceAlarmInfo.setAlarmTime(alarmTime);
		deviceAlarmInfo.setFacilitiesTypeId(facilityTypeId);
		deviceAlarmInfo.setAccessSecret(accessSecret);
		deviceAlarmInfo.setId(alarmId);
		deviceAlarmInfo.setAlarmItem(alarmItem);
		// 如果是自动生成工单,直接把isDel设置为0(即已生成工单)
		if (isMatchOrder) {
			deviceAlarmInfo.setIsDel(0);
		} else {
			deviceAlarmInfo.setIsDel(1);
		}
		return deviceAlarmInfo;
	}



	/**
	 * @Description:
	 * @param accessSecret
	 * @param alarmId
	 * @throws Exception
	 */

	private void createOrderAndSendRed(String accessSecret, String alarmId) throws Exception {
		AlarmOrder alarmOrder = new AlarmOrder();
		String alarmOrderId = UtilTool.getUuId();
		alarmOrder.setId(alarmOrderId);
		alarmOrder.setAlarmId(alarmId);
		alarmOrder.setAccessSecret(accessSecret);
		// 待处理
		alarmOrder.setProcessState("5");
		alarmOrder.setCreateTime(DateUtils.formatDate(new Date()));
		// 保存告警工单
		RpcResponse<String> saveAlarmOrder = alarmOrderCrudService.saveAlarmOrder(alarmOrder);
		logger.info(DateUtils.formatDate(new Date()) + "***保存告警工单:" + System.currentTimeMillis());
		if (!saveAlarmOrder.isSuccess()) {
			logger.error("告警工单保存失败!");
			throw new Exception("告警工单保存失败!");
		} else {
			logger.info("告警工单保存成功！");
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", UUIDUtil.getUUID());
		map.put("alarmId", alarmId);
		map.put("alarmOrderId", alarmOrderId);
		List<Map<String, Object>> list = Lists.newArrayList();
		list.add(map);
		int saveAlarmOrderAndInfo = alarmOrderCrudRepository.saveAlarmOrderAndInfo(list);
		logger.info(DateUtils.formatDate(new Date()) + "***保存告警信息和告警工单的关系:" + System.currentTimeMillis());
		if (saveAlarmOrderAndInfo <= 0) {
			logger.info(String.format("[createOrderAndSendRed()->saveAlarmOrderAndInfo()->invalid:%s]",
					"保存告警工单和告警信息关系失败!"));
			throw new Exception("保存告警工单和告警信息关系失败!");
		}

		// 告警工单保存成功，推送消息到队列中
		// 查询告警工单流程图id
		JSONObject processInfo = new JSONObject();
		processInfo.put("processSign", "alarmProcess");
		processInfo.put("manageState", "enabled");
		processInfo.put("accessSecret", accessSecret);

		RpcResponse<String> findBpmnId = orderProcessQueryService.findBpmnId(processInfo);
		logger.info(DateUtils.formatDate(new Date()) + "***通过流程标识以及状态查询文件id:" + System.currentTimeMillis());
		String bpmnId = findBpmnId.getSuccessValue();
		if (!findBpmnId.isSuccess() || StringUtils.isBlank(bpmnId)) {
			logger.error(String.format("[add()->invalid:%s]", "请检查是否配置或开启流程图！"));
		}

		// 通过文件id获取发起人节点key(流程图上配置)
		processInfo.put(AlarmInfoConstants.USC_ACCESS_SECRET, accessSecret);
		processInfo.put("id", bpmnId);
		RpcResponse<String> startTaskKey = processFileQueryService.queryStartTaskKey(processInfo);
		logger.info(DateUtils.formatDate(new Date()) + "***通过文件id获取发起人节点key(流程图上配置):" + System.currentTimeMillis());
		String taskKey = startTaskKey.getSuccessValue();
		if (!startTaskKey.isSuccess() || StringUtils.isBlank(taskKey)) {
			logger.error(String.format("[add()->invalid:%s]", "未查找到发起人节点key！请检查流程图配置!"));
		}
		processInfo.put("nodeId", taskKey);
		// 通过节点key查询相应的人
		RpcResponse<List<String>> startUsers = orderProcessQueryService.findStartUsers(processInfo);
		logger.info(DateUtils.formatDate(new Date()) + "***通过节点key查询相应的人:" + System.currentTimeMillis());
		List<String> startUsersId = startUsers.getSuccessValue();
		logger.info("createOrderAndSendRed()->节点人员id" + startUsersId);
		if (!startUsers.isSuccess() || startUsersId.size() == 0) {
			logger.error(String.format("[add()->invalid:%s]", "未查询到发起节点下人员配置！"));
		}

		// 调用rabbitMq推送消息
		for (String userId : startUsersId) {
			if (!jointPushQueue(userId, accessSecret)) {
				logger.info(DateUtils.formatDate(new Date()) + "***rabbitMq推送消息:" + System.currentTimeMillis());
				logger.error(String.format("[add()->error:%s]", "推送消息失败：" + userId));
			}
		}

		// 定时器任务15分钟未接受工单将产生推送
		// 工单生成，开启定时器
		AlarmOrder alarmOrder1 = alarmOrderCrudRepository.alarmOrderInfoByOrderId(alarmOrderId);
		RpcResponse<String> alarmOrderNotReceive = alramOrderRemindTimerService.AlarmOrderNotReceive(startUsersId,
				alarmOrder1);

		if (!alarmOrderNotReceive.isSuccess()) {
			logger.error(String.format("[createOrderAndSendRed()->error:%s]", alarmOrderNotReceive.getMessage()));
		}
		logger.info(String.format("[createOrderAndSendRed()->info:%s]", alarmOrderNotReceive.getMessage()));
		String nowState = alarmOrder1.getProcessState();
		if (!nowState.equals("待处理")) {
			RpcResponse<Boolean> closeScheduleJob = alramOrderRemindTimerService.closeScheduleJob(alarmOrderId);
			logger.info("createOrderAndSendRed()-->关闭定时器:" + closeScheduleJob.getSuccessValue());
		}
	}



	/**
	 * @Description:更新告警信息
	 * @param alarmId
	 * @throws Exception
	 */
	private void updateAlarmInfo(String alarmId, String alarmOrderId) throws Exception {
		JSONObject json = new JSONObject();
		json.put("alarmId", alarmId);
		logger.info(String.format("[updateAlarmInfo()->info:%s]", "告警id:" + alarmId));
		// 保存告警工单和告警信息的关系
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", UUIDUtil.getUUID());
		map.put("alarmId", alarmId);
		map.put("alarmOrderId", alarmOrderId);
		List<Map<String, Object>> list = Lists.newArrayList();
		list.add(map);
		int saveAlarmOrderAndInfo = alarmOrderCrudRepository.saveAlarmOrderAndInfo(list);
		logger.info(DateUtils.formatDate(new Date()) + "***更新时保存告警信息:" + System.currentTimeMillis());
		if (saveAlarmOrderAndInfo <= 0) {
			logger.info(String.format("[createOrderAndSendRed()->saveAlarmOrderAndInfo()->invalid:%s]",
					"保存告警工单和告警信息关系失败!"));
			throw new Exception("保存告警工单和告警信息关系失败!");
		}

		/*
		 * // 保存告警工单和告警信息的关系 RpcResponse<Map<String, Object>> saveAlarmOrder =
		 * alarmOrderCrudService.saveAlarmOrderandInfo(json); if
		 * (!saveAlarmOrder.isSuccess()) {
		 * logger.info("[saveAlarm_Order()->invalid:保存告警工单和告警信息关系失败!"); throw
		 * new Exception("保存告警工单和告警信息关系失败!"); }
		 *
		 * Map<String, Object> dispose = new HashMap<>();
		 * logger.info("updateAlarmInfo()-->修改告警信息,已成生工单:--Id:" + alarmId);
		 * dispose.put("id", alarmId); dispose.put("isDel", 0); // 已生成工单
		 *
		 * int updateTheDel =
		 * deviceAlarmInfoCrudRepository.updateTheDel(dispose); if (updateTheDel
		 * == 0) {
		 * logger.info("updateAlarmInfo()-->updateTheDel()-->告警信息状态更新失败"); throw
		 * new Exception("告警信息状态更新失败"); }
		 */

	}



	/**
	 * @Description:前端地图推送
	 * @param deviceId
	 * @param facilitiesCode
	 * @param organizationId
	 * @param alarmLevel
	 * @param alarmTime
	 * @param ruleName
	 * @param alarmId
	 */

	private void webMapPush(String deviceId, String facilitiesCode, String organizationId, int alarmLevel,
			String alarmTime, String ruleName, String alarmId) {
		RpcResponse<AlarmInfo> findById2 = alarmInfoQueryService.findById(alarmId);
		// 地图消息推送
		Map<String, Object> data = Maps.newHashMap();
		data.put("facilitiesCode", facilitiesCode);
		data.put("alarmTime", alarmTime);
		data.put("ruleName", ruleName);
		data.put("alarmLevel", alarmLevel);
		if (null != findById2.getSuccessValue()) {
			data.put("serialNum", findById2.getSuccessValue().getSerialNum());
		}
		data.put("organizationId", organizationId);
		data.put("deviceId", deviceId);
		// 查询接入方秘钥
		RpcResponse<String> accessSecrets = factoryQueryService.queryAccessSecretByDeviceId(deviceId);
		// 调用接口向前端推送实时告警信息
		logger.info("updateAlarmInfo()-->地图消息推送:密钥:" + accessSecrets.getSuccessValue() + "推送数据" + data);
		rabbitMqSendClient.pushMessageTopic(accessSecrets.getSuccessValue(), JSONObject.toJSONString(data));
	}



	/**
	 * @Description:检查操作是否合法
	 * @param map
	 * @param deviceId
	 */

	private boolean checkControlRecord(Map<String, Object> map, String deviceId) {
		RemoteControlRecord record = remoteRespon.getRemoteRecordByDevId(deviceId);
		if (record != null) {
			long contorlTimastamp = DateUtils.toDate(record.getControlTime()).getTime();
			long nowTimastamp = DateUtils.toDate(this.getStringValue(map.get(AlarmInfoConstants.REPORTTIME))).getTime();
			// 设备操作时间在命令发起时间的三分钟内，不告警
			if ((nowTimastamp - contorlTimastamp) <= CommonConstants.OVER_TIME) {
				return true;
			} else {
				// 超过三分钟，撤回命令 TODO 后期进行更改
				/*
				 * String desired = "{\"state\":{\"desired\":null}}";
				 * shadowCudService.updateThingShadowByConsole(deviceId,
				 * desired);
				 */
				return false;
			}
		} else {
			return false;
		}
	}



	/**
	 * @Description:检查平衡开关
	 * @param deviceId
	 * @param facilityId
	 * @param accessSecret
	 */

	private boolean checkBalance(String deviceId, String facilityId, String accessSecret) {
		Map<String, String> checkParam = Maps.newHashMap();
		checkParam.put("deviceId", deviceId);
		checkParam.put("facilityId", facilityId);
		checkParam.put("accessSecret", accessSecret);
		String state = balanceRespon.getBancanceStateByDevId(checkParam);
		if (DeviceContants.OPEN.equals(state)) {
			return true;
		} else {
			return false;
		}
	}



	/**
	 *
	 * @Description:校验一体化智能人井
	 * @param map
	 * @return checkLock
	 */
	private boolean checkLock(Map<String, Object> map, String accessSecret, String deviceId) {
		String lockState = this.getStringValue(map.get("ls"));
		// 上报状态为开
		if (null != lockState && DeviceContants.OPEN.equals(lockState)) {
			RpcResponse<String> checkLock = lockService.checkLock(deviceId, accessSecret);
			// 设备为一体化人井，且最近一条开关锁记录为开。
			if (checkLock.isSuccess() && null != checkLock.getSuccessValue()
					&& DeviceContants.OPEN.equals(checkLock.getSuccessValue())) {
				return true;
			} else {
				logger.info("[checkLock()->invalid:该设备不是一体化人井或没有开关锁记录或有开关锁记录但为关闭!");
				return false;
			}
		}
		logger.info("[checkLock()->invalid:不是一体化人井，或者是但上报的锁状态不为开!");
		return false;
	}



	/**
	 *
	 * @Description:校验平衡开关关闭命令，三分钟内有关闭命令不告警
	 * @param map
	 * @return checkLock
	 */

	private boolean checkBalanceCloseRecord(String deviceId, String accessSecret, Map<String, Object> map) {
		logger.info(String.format("[checkBalanceCloseRecord()->进入方法-requset parms--deviceId:%s,accessSecret:%s]",
				deviceId, accessSecret));
		String reportTime = map.get(AlarmInfoConstants.REPORTTIME).toString();
		RpcResponse<String> res = balanceService.getLatestCloseTime(deviceId, accessSecret);
		if (!res.isSuccess()) {
			logger.info(String.format("[checkBalanceCloseRecord()->%s]", res.getMessage()));
			return false;
		} else {
			String closeTime = res.getSuccessValue();
			// 点击关闭按钮后，开始计时3分钟内 该设备上报上来的数据不告警
			if ((DateUtils.toDate(reportTime).getTime()
					- DateUtils.toDate(closeTime).getTime()) <= CommonConstants.OVER_TIME) {
				return true;
			} else {
				logger.info(String.format("[checkBalanceCloseRecord()->最近关闭时间为：%s，超过三分钟，产生告警]", closeTime));
				return false;
			}
		}
	}



	/**
	 *
	 * @Description:发送短信和极光推送
	 * @param
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	private void sendMessageAndPush(String facilityTypeName, String facilitiesCode, String organizationId,
			String address, int alarmLevel, String alarmTime, String accessSecret, String ruleName) throws Exception {

		try {
			rollBackPoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
		} catch (NoTransactionException e) {
			logger.error("AlarmInfoCrudServiceImpl()->exception", e);
		}
		// 紧急告警
		if (alarmLevel == 1) {
			// 发送短信
			// 获取告警流水号
			Long alarmSerialNum = deviceAlarmInfoCrudRepository.getSerialNumBySecrete(accessSecret);
			logger.info("sendMessageAndPush()-->获取到告警流水号为:" + alarmSerialNum + ",接入方密钥为:" + accessSecret);
			JSONObject json = new JSONObject();
			json.put("organizeId", organizationId);
			json.put("receiveSms", "true");

			RpcResponse<List<Map>> userListByOrgId = userRoleQueryService.getAllUserListByOrgId(json);
			logger.info(DateUtils.formatDate(new Date()) + "***根据组织id查询该组织下能或不能接收短信的用户:" + System.currentTimeMillis());
			// 存储所有电话
			Set<String> mobiles = Sets.newHashSet();
			// 存储所有人员id
			List<String> userIds = Lists.newArrayList();
			// 存储姓名和电话对应关系
			Map<String, String> userNameAndMobile = Maps.newHashMap();
			getMobileAndUserName(userListByOrgId, mobiles, userIds, userNameAndMobile);
			
			Map<String, Object> personInCharge = deviceAlarmInfoCrudRepository.getPersonInCharge(facilitiesCode,accessSecret);
			if (null != personInCharge) {
				Object personTelObj = personInCharge.getOrDefault("personTel", null);
				if (null != personTelObj && StringUtils.isNotBlank(personTelObj + "")) {
					mobiles.add(personTelObj + "");
					userNameAndMobile.put(personTelObj + "", personInCharge.get("personName")+"");
				}
			}

			// 短信内容
			StringBuffer content = createMessage(facilityTypeName, facilitiesCode, address, alarmTime, ruleName);
			logger.info("sendMessageAndPush()->极光推送用户集合userIds" + userIds);
			// userIds.add("test002");//为了测试而添加的alias
			// 极光推送内容
			JiguangEntity jiguangEntity = new JiguangEntity();
			jiguangEntity.setAliasIds(userIds);
			jiguangEntity.setMsgContent(content.toString());
			jiguangEntity.setNotificationTitle("您有一条新的告警信息");
			jiguangEntity.setMsgTitle(ruleName);
			// 查询该接入方的短信授权接口地址
			RpcResponse<String> queryUrl = smsQueryService.getSmsUrl(accessSecret);
			// 该接入方下无授权
			if (null == queryUrl.getSuccessValue()) {
				// 查询接入方名称
				String httpValueByGet = InterGatewayUtil
						.getHttpValueByGet("/interGateway/v3/accessInformation/" + accessSecret, ip, "");
				if (httpValueByGet == null || StringUtils.isBlank(httpValueByGet)) {
					logger.error(
							"AlarmInfoCrudServiceImpl()-->fail:查询接入方信息失败,创建短信授权失败--" + "accessSecret :" + accessSecret);
					jiguangPush(jiguangEntity);
					return;
				}
				// 查询接入方名称成功，创建短信授权
				JSONObject jsonObject = JSONObject.parseObject(httpValueByGet);
				String accessName = jsonObject.getString("accessTenementName");
				// 创建短信授权
				RpcResponse<String> registUrl = smsRegistService.smsRegist(accessName, accessSecret, catelogId);
				if (null == registUrl || !registUrl.isSuccess()) {
					if (null == registUrl) {
						logger.error("[smsRegistService.smsRegist()->error:接口返回为null]");
					} else if (!registUrl.isSuccess()) {
						logger.error(String.format("[smsRegistService.smsRegist()->fail:%s,accessSecret:%s]",
								registUrl.getMessage(), accessSecret));
					}
					jiguangPush(jiguangEntity);
					return;
				}
				// 创建成功，保存短信发送记录
				// sendMessage(accessSecret, alarmSerialNum, mobiles,
				// userNameAndMobile, content, registUrl);
				saveMessageRecord(accessSecret, alarmSerialNum, mobiles, userNameAndMobile, content);
			}
			// 该接入方已有授权接口地址
			else {
				// sendWhenAuthorized(accessSecret, alarmSerialNum, mobiles,
				// userNameAndMobile, content, queryUrl);
				saveMessageRecord(accessSecret, alarmSerialNum, mobiles, userNameAndMobile, content);
			}
			// 极光推送
			jiguangPush(jiguangEntity);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	private void getMobileAndUserName(RpcResponse<List<Map>> userListByOrgId, Set<String> mobiles,
			List<String> userIds, Map<String, String> userNameAndMobile) {
		if (userListByOrgId.isSuccess()) {
			List<Map> userList = userListByOrgId.getSuccessValue();
			if (userList != null && userList.size() > 0) {
				for (Map map2 : userList) {
					String mobile = map2.get("mobile") == null ? null : String.valueOf(map2.get("mobile"));
					String userId = map2.get("_id") == null ? null : String.valueOf(map2.get("_id"));
					String userName = map2.get("userName") == null ? null : String.valueOf(map2.get("userName"));
					if (mobile != null) {
						mobiles.add(mobile);
						if (userName != null) {
							userNameAndMobile.put(mobile, userName);
						}
					}
					if (userId != null) {
						userIds.add(userId);
					}
				}
			}
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void jiguangPush(JiguangEntity jiguangEntity) {
		RpcResponse<Object> jiguangPush = jiguangService.sendMessage(jiguangEntity);
		logger.info(DateUtils.formatDate(new Date()) + "***极光推送:" + System.currentTimeMillis());
		if (jiguangPush == null) {
			logger.error("AlarmInfoCrudServiceImpl.add()->error:极光推送失败,jiguangService.sendMessage()无返回信息");
		} else if (!jiguangPush.isSuccess()) {
			logger.error(jiguangPush.getMessage() + "AlarmInfoCrudServiceImpl.add()->error:极光推送失败");
		} else {
			logger.info(jiguangPush.getMessage() + "AlarmInfoCrudServiceImpl.add()->success:极光推送成功");
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private StringBuffer createMessage(String facilityTypeName, String facilitiesCode, String address, String alarmTime,
			String ruleName) {
		StringBuffer content = new StringBuffer();
		content.append("设施序列号为");
		content.append(facilitiesCode);
		content.append("的");
		content.append(facilityTypeName);
		content.append(",在");
		content.append(alarmTime);
		content.append("发生");
		content.append(ruleName);
		content.append(",请处理!\n地址:");
		content.append(address);
		return content;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void sendWhenAuthorized(String accessSecret, Long alarmSerialNum, List<String> mobiles,
			Map<String, String> userNameAndMobile, StringBuffer content, RpcResponse<String> queryUrl) {
		for (String mobile : mobiles) {
			if (PhoneFormatCheckUtil.isPhoneLegal(mobile)) {
				RpcResponse<String> sendMessage = SendSms.sendMessage(mobile, content.toString(),
						queryUrl.getSuccessValue());
				if (null == sendMessage) {
					logger.error("[SendSms.sendMessage()->fail:接口返回为null], 手机号:" + mobile);
					continue;
				}
				if (!sendMessage.isSuccess()) {
					logger.error(String.format("[SendSms.SendMessage()->fail:%s,手机号 :%s]", sendMessage.getMessage(),
							mobile));
					continue;
				}
				logger.info(
						String.format("[SendSms.SendMessage()->success:%s,手机号 :%s]", sendMessage.getMessage(), mobile));
				// 保存短信记录
				Map<String, Object> smsRecord = Maps.newHashMap();
				smsRecord.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
				smsRecord.put("alarmSerialNum", alarmSerialNum);
				smsRecord.put("userName", userNameAndMobile.get(mobile));
				smsRecord.put("phoneNumber", mobile);
				smsRecord.put("smsContent", content.toString());
				smsRecord.put("sendTime", DateUtils.formatDate(new Date()));
				smsRecord.put("accessSecret", accessSecret);
				int record = smsRegistCrudRepository.addSmsRecord(smsRecord);
				logger.info(DateUtils.formatDate(new Date()) + "***告警短信保存:" + System.currentTimeMillis());
				if (record > 0) {
					logger.info(String.format("[短信记录保存成功，name:%s,mobile:%s]", userNameAndMobile.get(mobile), mobile));
				} else {
					logger.error(String.format("[短信记录保存失败，name:%s,mobile:%s]", userNameAndMobile.get(mobile), mobile));
				}
			} else {
				logger.error("[手机号码" + mobile + "不合法]");
			}
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void sendMessage(String accessSecret, Long alarmSerialNum, List<String> mobiles,
			Map<String, String> userNameAndMobile, StringBuffer content, RpcResponse<String> registUrl) {
		for (String mobile : mobiles) {
			if (PhoneFormatCheckUtil.isPhoneLegal(mobile)) {
				RpcResponse<String> sendMessage = SendSms.sendMessage(mobile, content.toString(),
						registUrl.getSuccessValue());
				if (null == sendMessage) {
					logger.error("[SendSms.sendMessage()->error:接口返回为null], 手机号:" + mobile);
					continue;
				}
				if (!sendMessage.isSuccess()) {
					logger.error(String.format("[SendSms.SendMessage()->fail:%s,手机号 :%s]", sendMessage.getMessage(),
							mobile));
					continue;
				}
				logger.info(
						String.format("[SendSms.SendMessage()->success:%s,手机号 :%s]", sendMessage.getMessage(), mobile));
				// 保存短信告警记录
				Map<String, Object> smsRecord = Maps.newHashMap();
				smsRecord.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
				smsRecord.put("alarmSerialNum", alarmSerialNum);
				smsRecord.put("userName", userNameAndMobile.get(mobile));
				smsRecord.put("phoneNumber", mobile);
				smsRecord.put("smsContent", content.toString());
				smsRecord.put("sendTime", DateUtils.formatDate(new Date()));
				smsRecord.put("accessSecret", accessSecret);
				int record = smsRegistCrudRepository.addSmsRecord(smsRecord);
				logger.info(DateUtils.formatDate(new Date()) + "***新增授权之后告警短信保存:" + System.currentTimeMillis());
				if (record > 0) {
					logger.info(String.format("[短信记录保存成功，name:%s,mobile:%s]", userNameAndMobile.get(mobile), mobile));
				} else {
					logger.error(String.format("[短信记录保存失败，name:%s,mobile:%s]", userNameAndMobile.get(mobile), mobile));
				}
			} else {
				logger.error("[手机号码" + mobile + "不合法]");
			}
		}
	}



	@Override
	public RpcResponse<String> updateTheDel(Map<String, Object> map) {
		try {
			if (map == null || map.isEmpty()) {
				logger.error("[updateTheDel()->error:参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空");
			}
			if (map.get(CommonConstants.ID) == null) {
				logger.error("[alarmRuleUpdate()->error:告警信息id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("告警信息id不能为空");
			}
			int updateModel = deviceAlarmInfoCrudRepository.updateTheDel(map);
			if (updateModel > 0) {
				//更新redis缓存
				Map<String,Object> redisMap=new HashMap<String,Object>();
				redisMap.put("id", map.get("facilityId")+"");
				redisMap.put("accessSecret", map.get("accessSecret")+"");
				updateRedisCrudService.updateFacMapCache(redisMap);
				
				logger.info("[alarmRuleUpdate()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, null);
			} else {
				logger.error("[alarmRuleUpdate()->error: " + MessageConstant.UPDATE_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("updateTheDel()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> updateTheDelByCondition(Map<String, Object> map) {
		try {
			if (map == null || map.isEmpty()) {
				logger.error("[updateTheDelByDeviceId()->error:参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空");
			}
			if (map.get(AlarmInfoConstants.DEVICE_ID) == null) {
				logger.error("[updateTheDelByDeviceId()->error:告警信息中设备id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("告警信息中设备id不能为空");
			}
			if (map.get(AlarmInfoConstants.ALARM_DESC) == null) {
				logger.error("[updateTheDelByDeviceId()->error:告警信息中告警描述不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("告警信息中告警描述不能为空");
			}
			if (map.get(AlarmInfoConstants.ALARMI_LEVEL) == null) {
				logger.error("[updateTheDelByDeviceId()->error:告警信息中告警等级不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("告警信息中告警等级不能为空");
			}
			int updateModel = deviceAlarmInfoCrudRepository.updateTheDelByCondition(map);
			if (updateModel > 0) {
				Facilities facilities = faciRespon.getFacByDeviceId(map.get("deviceId")+"");
				//更新redis缓存
				Map<String,Object> redisMap=new HashMap<String,Object>();
				redisMap.put("id", facilities.getId());
				redisMap.put("accessSecret", facilities.getAccessSecret());
				updateRedisCrudService.updateFacMapCache(redisMap);
				
				logger.info("[updateTheDelByDeviceId()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, null);
			} else {
				logger.error("[updateTheDelByDeviceId()->error: " + MessageConstant.UPDATE_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("updateTheDelByDeviceId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	private String getStringValue(Object obj) {
		return (obj == null) ? null : obj.toString();
	}



	@Override
	public RpcResponse<Boolean> disposeAlarmInfo(Map<String, Object> map) {
		if (map == null) {
			logger.error("[disposeAlarmInfo()->error: 更新参数不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("更新参数不能为空");
		}
		if (!map.containsKey(AlarmInfoConstants.DEVICE_ID) || !map.containsKey(AlarmInfoConstants.ALARM_DESC)
				|| !map.containsKey(RuleContants.RULE) || !map.containsKey(AlarmInfoConstants.ALARM_TIME)) {
			logger.error("[disposeAlarmInfo()->error: 传入参数不合法!]");
			return RpcResponseBuilder.buildErrorRpcResp("传入参数不合法");
		}
		try {
			deviceAlarmInfoCrudRepository.disposeAlarmInfo(map);
			//更新redis缓存
			Map<String,Object> redisMap=new HashMap<String,Object>();
			redisMap.put("id", map.get("facilityId")+"");
			redisMap.put("accessSecret", map.get("accessSecret")+"");
			updateRedisCrudService.updateFacMapCache(redisMap);
			
			
			logger.info("disposeAlarmInfo()-->更新成功");
			return RpcResponseBuilder.buildSuccessRpcResp("更新成功", Boolean.TRUE);

		} catch (Exception e) {
			logger.error("disposeAlarmInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
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



	/**
	 * @see com.run.locman.api.crud.service.AlarmInfoCrudService#saveAlarmInfo(com.run.locman.api.entity.AlarmInfo)
	 */
	@Override
	public RpcResponse<Integer> saveAlarmInfo(AlarmInfo alarmInfo) {
		try {
			if (alarmInfo == null) {
				logger.error("[saveAlarmInfo()->error:参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空");
			}
			int model = deviceAlarmInfoCrudRepository.insertModel(alarmInfo);
			if (model > 0) {
				//更新redis缓存
				Map<String,Object> redisMap=new HashMap<String,Object>();
				redisMap.put("id", alarmInfo.getFacilitiesId());
				redisMap.put("accessSecret", alarmInfo.getAccessSecret());
				updateRedisCrudService.updateFacMapCache(redisMap);
				
				
				logger.info("[saveAlarmInfo()->succes: 添加成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("添加成功!", model);
			} else {
				logger.error("[saveAlarmInfo()->error:添加失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("添加失败!");
			}
		} catch (Exception e) {
			logger.error("saveAlarmInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.AlarmInfoCrudService#updateAlarmOrderAndInfo(java.util.Map,
	 *      com.run.locman.api.entity.AlarmOrder)
	 */
	@Override
	public RpcResponse<Boolean> updateAlarmOrderAndInfo(Map<String, Object> map, AlarmOrder alarmOrder) {

		try {

			RpcResponse<Boolean> checkParams = checkParams(map, alarmOrder);
			if (checkParams != null) {
				return checkParams;
			}

			int updateModel = deviceAlarmInfoCrudRepository.updateTheDelByCondition(map);
			int updatePart = alarmOrderCrudRepository.updatePart(alarmOrder);

			if (updatePart > 0 && updateModel > 0) {
				Facilities facilities = faciRespon.getFacByDeviceId(map.get("deviceId")+"");
				//更新redis缓存
				Map<String,Object> redisMap=new HashMap<String,Object>();
				redisMap.put("id", facilities.getId());
				redisMap.put("accessSecret", facilities.getAccessSecret());
				updateRedisCrudService.updateFacMapCache(redisMap);

				
				logger.info("[updateAlarmOrderAndInfo()->suc:更新成功！ ]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, true);
			} else {
				logger.error("[updateAlarmOrderAndInfo()->suc:更新失败！ ]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}

		} catch (Exception e) {
			logger.error("updateAlarmOrderAndInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * 
	 * @Description:参数校验 updateAlarmOrderAndInfo
	 * @param map
	 * @param alarmOrder
	 * @return
	 */
	private RpcResponse<Boolean> checkParams(Map<String, Object> map, AlarmOrder alarmOrder) throws Exception {
		if (map == null || map.isEmpty()) {
			logger.error("[updateTheDelByDeviceId()->error:参数不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("参数不能为空");
		}
		if (map.get(AlarmInfoConstants.DEVICE_ID) == null) {
			logger.error("[updateTheDelByDeviceId()->error:告警信息中设备id不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("告警信息中设备id不能为空");
		}
		if (map.get(AlarmInfoConstants.ALARM_DESC) == null) {
			logger.error("[updateTheDelByDeviceId()->error:告警信息中告警描述不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("告警信息中告警描述不能为空");
		}
		if (map.get(AlarmInfoConstants.ALARMI_LEVEL) == null) {
			logger.error("[updateTheDelByDeviceId()->error:告警信息中告警等级不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("告警信息中告警等级不能为空");
		}
		if (alarmOrder == null) {
			logger.error("[updateAlarmOrder --> updateAlarmOrder(): invalid：告警工单修改参数不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("告警工单修改参数不能为空");
		}
		if (StringUtils.isBlank(alarmOrder.getId())) {
			logger.error("[updateAlarmOrder --> updateAlarmOrder(): invalid：告警工单id不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("告警工单id不能为空！");
		}
		return null;
	}



	private void pushAlarmInfo(AlarmInfo alarmInfo, Facilities facilities, String accessSecret) {
		try {

			try {
				rollBackPoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
			} catch (NoTransactionException e) {
				logger.error("pushAlarmInfo()->exception", e);
			}

			logger.info(
					String.format("[pushAlarmInfo->进入方法],告警信息-%s,accessSecret-%s", alarmInfo.toString(), accessSecret));

			// 获取推送地址
			String receiveUrl = InfoPushUtil.WhetherPush(accessSecret, ip);
			if (receiveUrl == null) {
				logger.info("[pushAlarmInfo->该接入方无推送地址，无需推送]");
				return;
			}

			// 获得需要推送的告警信息
			String facilitiesId = alarmInfo.getFacilitiesId();
			String address = facilities.getAddress();
			String facilitiesCode = facilities.getFacilitiesCode();
			String alarmTime = alarmInfo.getAlarmTime();
			String alarmDesc = alarmInfo.getAlarmDesc();
			long serialNum = alarmInfo.getSerialNum();
			String alarmId = alarmInfo.getId();

			logger.info("[pushAlarmInfo->推送告警信息获取成功]");

			// 查询相应的告警工单信息
			// RpcResponse<Map<String, Object>> res =
			// alarmOrderQueryService.getAlarmOrderInfoByAlarmId(alarmId);
			Map<String, Object> map = deviceAlarmInfoCrudRepository.getAlarmOrderInfoByAlarmId(alarmId);

			// 告警相关信息封装
			JSONObject alarmInfoForPush = new JSONObject();
			alarmInfoForPush.put("facilityId", facilitiesId);
			alarmInfoForPush.put("alarmId", alarmId);
			alarmInfoForPush.put("facilitiesCode", facilitiesCode);
			alarmInfoForPush.put("address", address);
			alarmInfoForPush.put("alarmTime", alarmTime);
			alarmInfoForPush.put("alarmDesc", alarmDesc);
			alarmInfoForPush.put("serialNum", serialNum);

			// 推送消息封装
			JSONObject infoPush = new JSONObject();
			infoPush.put("alarmInfo", alarmInfoForPush);
			if (map == null) {
				infoPush.put("alarmOrder", "");
			} else {
				infoPush.put("alarmOrder", map);
			}
			/*
			 * // 如果查询为空或者查询失败，为了保证推送信息里面有alarmOrder字段，value为"" if
			 * (!res.isSuccess() || res.getSuccessValue() == null) {
			 * infoPush.put("alarmOrder", ""); }
			 * 
			 * // res不为空 infoPush.put("alarmOrder", res.getSuccessValue());
			 */

			String pushResult = InfoPushUtil.InfoPush(receiveUrl, infoPush, InfoPushUtil.ALARM_INFO);
			logger.info(String.format("[pushAlarmInfo->%s]", pushResult));
			return;
		} catch (Exception e) {
			logger.error("[pushAlarmInfo->Exception]", e);
			return;
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.AlarmInfoCrudService#updateAlarmOrderAndInfoForCompeleteReject(java.util.List,
	 *      com.run.locman.api.entity.AlarmOrder)
	 */
	public RpcResponse<Boolean> updateAlarmOrderAndInfoForCompeleteReject(List<String> alarmIds,
			AlarmOrder alarmOrder) {
		logger.info(String.format(
				"[进入updateAlarmOrderAndInfoForCompeleteReject-->request params: alarmIds:%s,alarmOrder:%s]",
				alarmIds.toString(), alarmOrder.toString()));
		try {
			if (alarmIds.size() == 0) {
				logger.error("[updateAlarmOrderAndInfoForCompeleteReject-->error:alarmIds为空]");
				return RpcResponseBuilder.buildErrorRpcResp("alarmIds为空");
			}
			if (StringUtils.isBlank(alarmOrder.getId())) {
				logger.error("[updateAlarmOrderAndInfoForCompeleteReject --> 告警工单id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("告警工单id不能为空！");
			}
			int updatePart = alarmOrderCrudRepository.updatePart(alarmOrder);
			int updateTheDelByAlarmIds = deviceAlarmInfoCrudRepository.updateTheDelByAlarmIds(alarmIds);

			if (updatePart > 0 && updateTheDelByAlarmIds > 0) {
				String alarmId=alarmIds.get(0);
				RpcResponse<AlarmInfo> alarmInfo = alarmInfoQueryService.findById(alarmId);
				//更新redis缓存
				Map<String,Object> redisMap=new HashMap<String,Object>();
				redisMap.put("id", alarmInfo.getSuccessValue().getFacilitiesId());
				redisMap.put("accessSecret", alarmInfo.getSuccessValue().getAccessSecret());
				updateRedisCrudService.updateFacMapCache(redisMap);
				
				logger.info("[updateAlarmOrderAndInfoForCompeleteReject()->suc:更新成功！ ]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, true);
			} else {
				logger.error("[updateAlarmOrderAndInfoForCompeleteReject()->suc:更新失败！ ]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("updateAlarmOrderAndInfoForCompeleteReject()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:将短信保存到表里，另外设置个定时器发送短信
	 * @param
	 * @return
	 */
	public void saveMessageRecord(String accessSecret, Long alarmSerialNum, Set<String> mobiles,
			Map<String, String> userNameAndMobile, StringBuffer content) {
		logger.info(String.format("[进入saveMessageRecord->alarmSeriaNum:%s]", alarmSerialNum));
		for (String mobile : mobiles) {
			if (PhoneFormatCheckUtil.isPhoneLegal(mobile)) {
				// 保存短信告警记录
				Map<String, Object> smsRecord = Maps.newHashMap();
				smsRecord.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
				smsRecord.put("alarmSerialNum", alarmSerialNum);
				smsRecord.put("userName", userNameAndMobile.get(mobile));
				smsRecord.put("phoneNumber", mobile);
				smsRecord.put("smsContent", content.toString());
				smsRecord.put("createTime", DateUtils.formatDate(new Date()));
				smsRecord.put("accessSecret", accessSecret);
				smsRecord.put("state", 0);
				int record = smsRegistCrudRepository.addSmsRecord(smsRecord);
				if (record > 0) {
					logger.info(String.format("[待发送短信保存成功，name:%s,mobile:%s，alarmSerialNum：%s]",
							userNameAndMobile.get(mobile), mobile, alarmSerialNum));
				} else {
					logger.error(String.format("[待发送短信保存失败，name:%s,mobile:%s,alarmSerialNum：%s]",
							userNameAndMobile.get(mobile), mobile, alarmSerialNum));
				}
			} else {
				logger.error("[手机号码" + mobile + "不合法]");
			}
		}
	}



	/**
	 * 
	 * @Description:有到场图片的告警工单,且当前工单未完成,对应设施不再产生告警
	 * @return
	 */
	private boolean checkAlarmOrderPresenPic(String deviceId, String accessSecret) {
		logger.info(String.format("[checkAlarmOrderPresenPic()->进入方法,deviceId:%s]", deviceId));
		String alarmOrderId = alarmOrderCrudRepository.getAlarmOrderIdPresentPicExits(deviceId, accessSecret);
		if (StringUtils.isBlank(alarmOrderId)) {
			logger.info("[checkAlarmOrderPresenPic()->没有未完成的且有到场图片的告警工单]");
			return false;
		} else {
			return true;
		}

	}
}
