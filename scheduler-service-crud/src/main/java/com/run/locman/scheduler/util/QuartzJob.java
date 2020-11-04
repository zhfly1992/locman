/*
 * File name: QuartzJobOne.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年6月13日 ... ... ...
 *
 ***************************************************/

package com.run.locman.scheduler.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.AlarmInfoCrudService;
import com.run.locman.api.crud.service.DeviceJobCrudService;
import com.run.locman.api.crud.service.SmsRegistService;
import com.run.locman.api.entity.AlarmInfo;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.SmsQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.PhoneFormatCheckUtil;
import com.run.locman.api.util.SendSms;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.SchedulerContants;
import com.run.rabbit.activity.server.RabbitMqCrudService;
import com.run.sms.api.JiguangService;

import entity.JiguangEntity;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年6月13日
 */
@Component
public class QuartzJob implements Job {

	@Autowired
	AlarmInfoCrudService				alarmInfoCrudService;

	@Autowired
	DeviceJobCrudService				deviceJobCrudService;

	@Autowired
	private MongoTemplate				mongoTemplate;

	@Autowired
	private AlarmInfoQueryService  alarmInfoQueryService;

	@Autowired
	private RabbitMqCrudService			rabbitMqSendClient;

	@Autowired
	private UserRoleBaseQueryService	userRoleQueryService;

	@Autowired
	private SmsQueryService				smsQueryService;

	@Autowired
	private SmsRegistService			smsRegistService;

	@Autowired
	private JiguangService				jiguangService;

	@Value("${api.host}")
	private String						ip;

	@Value("${sms.catelogId}")
	private String						catelogId;

	private static final Logger			logger	= Logger.getLogger(QuartzJob.class);



	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			/* 拿到传入的数据 */
			JobDetail jobDetail = context.getJobDetail();
			JSONObject jsonParam = (JSONObject) jobDetail.getJobDataMap().get("data");
			// RpcResponse<String> deviceScheduler =
			// alarmInfoCrudService.deviceScheduler(jsonParam);
			RpcResponse<String> deviceScheduler = this.deviceScheduler(jsonParam);
			if (!deviceScheduler.isSuccess()) {
				logger.error("deviceScheduler()---" + deviceScheduler.getMessage());
			}
			// 告警后关闭定时器
			QuartzManager.disableSchedule(jsonParam.getString("jobId"), jsonParam.getString("jobId") + "_group");
			logger.info("deviceScheduler()---定时器已移除【" + jsonParam.getString("jobId") + "】");
			// 删除设备定时器关系
			RpcResponse<Integer> deleteByJobId = deviceJobCrudService.deleteByJobId(jsonParam.getString("jobId"));
			if (!deleteByJobId.isSuccess()) {
				logger.error("deleteByJobId()---删除设备定时器关系失败!【" + jsonParam.getString("jobId") + "】");
			}
		} catch (Exception e) {
			logger.error("schedulerStart()->exception", e);
		}
	}



	/**
	 * 设备超时未关告警
	 * 
	 * @see com.run.locman.api.crud.service.AlarmInfoCrudService#deviceScheduler(com.alibaba.fastjson.JSONObject)
	 */
	public RpcResponse<String> deviceScheduler(JSONObject json) {
		try {
			return try1(json);
		} catch (Exception e) {
			return catch1(e);
		}
	}



	@SuppressWarnings("rawtypes")
	private RpcResponse<String> try1(JSONObject json) throws Exception {
		if (null == json) {
			return panduan00();
		}
		// 查询此时设备关闭状态
		Map<String, Object> deviceState = se3(json);
		if (null != deviceState) {
			JSONObject reportedJson = new JSONObject(deviceState);
			JSONObject deviceStats = UtilTool.getReported(reportedJson);
			if (null != deviceStats) {
				String closeState = deviceStats.get(json.getString("item")) + "";
				// ble为光交设备蓝牙下发命令，特殊处理
				if (!SchedulerContants.SCHEDULER_BLE.equals(json.getString(SchedulerContants.SCHEDULER_ITEM)) && null == deviceStats.get(json.getString(SchedulerContants.SCHEDULER_ITEM))) {
					return panduan22(json);
				} else if (SchedulerContants.SCHEDULER_BLE.equals(json.getString(SchedulerContants.SCHEDULER_ITEM))) {
					closeState = elseIf1(deviceStats, closeState);
				}
				// 该设备未关闭，产生告警
				if (!SchedulerContants.SCHEDULER_CLOSE.equals(closeState)) {
					AlarmInfo alarm = new AlarmInfo();
					String date = DateUtils.formatDate(new Date());
					String alarmId = alramPut(json, alarm, date);
					RpcResponse<Integer> saveAlarmInfo = alarmInfoCrudService.saveAlarmInfo(alarm);
					if (saveAlarmInfo.isSuccess()) {
						// 告警信息保存成功，更新告警信息为已生成工单
						Map<String, Object> dispose = mnb(alarmId); 
						RpcResponse<String> updateTheDel = alarmInfoCrudService.updateTheDel(dispose);
						if (!updateTheDel.isSuccess()) {
							return panduan8();
						}
						// 获取入库的告警信息，用于获取告警流水号
						RpcResponse<AlarmInfo> alarmInfo = alarmInfoQueryService.findById(alarmId);
						if (!alarmInfo.isSuccess()) {
							return panduan4(alarmInfo);
						}
						// 地图消息推送
						Map<String, Object> data = datePut(json, alarmInfo);

						// 调用接口向前端推送实时告警信息
						rabbitMqSendClient.pushMessageTopic(json.getString("accessSecret"),
								JSONObject.toJSONString(data));
						// 发送短信
						RpcResponse<List<Map>> userListByOrgId = faSong(json);
						// 所有电话
						List<String> mobiles = new ArrayList<>(); 
						// 所有人员id
						List<String> userIds = new ArrayList<>();
						if (userListByOrgId.isSuccess()) {
							panduan1(userListByOrgId, mobiles, userIds);
						}
						// 短信内容
						StringBuffer content = duanXinNeiRong(json, alarmInfo);
						int hour = json.getIntValue("hour");
						int minute = json.getIntValue("minute");
						// 极光推送内容
						StringBuffer pushContent = jiGuangTuiSong(json, hour, minute);
						JiguangEntity jiguangEntity = jiGuangEnitity1(alarmInfo, userIds, pushContent);
						// 短信
						for (String mobile : mobiles) {
							for3(json, content, mobile);
						}
						RpcResponse<Object> jiguangPush = jiguangService.sendMessage(jiguangEntity);
						if (jiguangPush == null || !jiguangPush.isSuccess()) {
							logger.error("deviceScheduler()->error:极光推送失败");
						} else {
							logger.info(jiguangPush.getMessage() + "deviceScheduler()->success:极光推送成功");
						}
					} else {
						return else7();
					}
				}
			} else {
				return else2(json);
			}
		} else {
			return else3();
		}
		return else5();
	}



	private Map<String, Object> mnb(String alarmId) {
		Map<String, Object> dispose = new HashMap<>(16);
		logger.info("修改告警信息,已成生工单:--Id:" + alarmId);
		dispose.put("id", alarmId);
		// 设备超时未关
		dispose.put("isDel", 5);
		return dispose;
	}



	private RpcResponse<String> panduan22(JSONObject json) {
		logger.info(String.format("[deviceScheduler()->info: 用%s获取设备对应状态为空!]", json.getString("item")));
		return RpcResponseBuilder.buildErrorRpcResp("用" + json.getString("item") + "获取设备对应状态为空!");
	}



	private RpcResponse<String> panduan00() {
		logger.error("[deviceScheduler()->error: 设备超时未关定时任务参数为空!]");
		return RpcResponseBuilder.buildErrorRpcResp("设备超时未关定时任务参数为空!");
	}



	private RpcResponse<String> else7() {
		logger.error("[deviceScheduler()->error: 设备超时未关定时任务保存告警信息失败!]");
		return RpcResponseBuilder.buildErrorRpcResp("设备超时未关定时任务保存告警信息失败!");
	}



	private RpcResponse<String> else5() {
		logger.info("[deviceScheduler()->error: 设备超时未关定时任务执行成功!]");
		return RpcResponseBuilder.buildSuccessRpcResp("设备超时未关定时任务执行成功!", null);
	}



	private RpcResponse<String> else3() {
		logger.error("[deviceScheduler()->error: 设备超时未关定时任务未查询到该设备上报状态!]");
		return RpcResponseBuilder.buildErrorRpcResp("设备超时未关定时任务未查询到该设备上报状态!");
	}



	private RpcResponse<String> catch1(Exception e) {
		logger.error("deviceScheduler()->exception", e);
		return RpcResponseBuilder.buildExceptionRpcResp(e);
	}



	private RpcResponse<String> panduan8() {
		logger.error("[deviceScheduler()->error: 设备超时未关定时任务告警信息状态更新失败!]");
		return RpcResponseBuilder.buildErrorRpcResp("设备超时未关定时任务告警信息状态更新失败!");
	}



	private RpcResponse<String> panduan4(RpcResponse<AlarmInfo> alarmInfo) {
		logger.error("[deviceScheduler()->error: 设备超时未关定时任务" + alarmInfo.getMessage() + "]");
		return RpcResponseBuilder.buildErrorRpcResp("设备超时未关定时任务" + alarmInfo.getMessage());
	}



	private RpcResponse<String> else2(JSONObject json) {
		logger.error("[deviceScheduler()->error: 设备超时未关定时任务查询到该设备数据库状态为空或用" + json.getString("item")
				+ "未查询到对应的value值!]");
		return RpcResponseBuilder.buildErrorRpcResp("[deviceScheduler()->error: 设备超时未关定时任务查询到该设备数据库状态为空或用"
				+ json.getString("item") + "未查询到对应的value值!]");
	}



	private Map<String, Object> se3(JSONObject json) {
		DBObject dbCondition = new BasicDBObject();
		dbCondition.put("deviceId", json.getString("deviceId"));
		Query query = new BasicQuery(dbCondition);
		@SuppressWarnings("unchecked")
		Map<String, Object> deviceState = mongoTemplate.findOne(query, Map.class, "deviceState");
		return deviceState;
	}



	private String elseIf1(JSONObject deviceStats, String closeState) {
		String[] itemKeyArray = new String[] { "ls1", "ls2", "ds1", "ds2" };
		for (String key : itemKeyArray) {
			// 此时数据库设备的开启关闭状态
			String dbCloseState = deviceStats.get(key) + "";
			
			if (null != deviceStats.get(key) && !dbCloseState.equals("close")) {
				// 只要一个锁为开启则告警
				closeState = dbCloseState;
				break;
			}
		}
		return closeState;
	}



	@SuppressWarnings("rawtypes")
	private RpcResponse<List<Map>> faSong(JSONObject json) throws Exception {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("organizeId", json.getString("organizationId"));
		jsonMessage.put("receiveSms", "true");

		RpcResponse<List<Map>> userListByOrgId = userRoleQueryService
				.getUserListByOrgId(jsonMessage);
		return userListByOrgId;
	}



	private String alramPut(JSONObject json, AlarmInfo alarm, String date) {
		alarm.setDeviceId(json.getString("deviceId"));
		alarm.setReportTime("");
		alarm.setFacilitiesId(json.getString("facilityId"));
		//告警描述
		alarm.setAlarmDesc("超时未关告警"); 
		alarm.setRule("");
		alarm.setAlarmLevel(1);
		alarm.setMatchOrder(false);
		alarm.setAlarmTime(date);
		alarm.setFacilitiesTypeId(json.getString("facilitiesTypeId"));
		alarm.setAccessSecret(json.getString("accessSecret"));
		String alarmId = UtilTool.getUuId();
		alarm.setId(alarmId);
		alarm.setAlarmItem("");
		return alarmId;
	}



	@SuppressWarnings("rawtypes")
	private void panduan1(RpcResponse<List<Map>> userListByOrgId, List<String> mobiles, List<String> userIds) {
		List<Map> userList = userListByOrgId.getSuccessValue();
		if (userList != null && userList.size() > 0) {
			for (Map map2 : userList) {
				String mobile = map2.get("mobile") == null ? null
						: String.valueOf(map2.get("mobile"));
				String userId = map2.get("_id") == null ? null
						: String.valueOf(map2.get("_id"));
				if (mobile != null) {
					mobiles.add(mobile);
				}
				if (userId != null) {
					userIds.add(userId);
				}
			}
		}
	}



	private Map<String, Object> datePut(JSONObject json, RpcResponse<AlarmInfo> alarmInfo) {
		Map<String, Object> data = new HashMap<>(16);
		data.put("facilitiesCode", json.getString("facilitiesCode"));
		data.put("organizationId", json.getString("organizationId"));
		data.put("deviceId", json.getString("deviceId"));
		if (null != alarmInfo.getSuccessValue()) {
			data.put("alarmTime", alarmInfo.getSuccessValue().getAlarmTime());
			data.put("ruleName", alarmInfo.getSuccessValue().getAlarmDesc());
			data.put("alarmLevel", alarmInfo.getSuccessValue().getAlarmLevel());
			data.put("serialNum", alarmInfo.getSuccessValue().getSerialNum());
		}
		return data;
	}



	private void for3(JSONObject json, StringBuffer content, String mobile) {
		if (PhoneFormatCheckUtil.isPhoneLegal(mobile)) {
			RpcResponse<String> queryUrl = smsQueryService
					.getSmsUrl(json.getString("accessSecret"));
			RpcResponse<String> sendMessage;
			if (queryUrl.getSuccessValue() != null) {
				sendMessage = SendSms.sendMessage(mobile, content.toString(),
						queryUrl.getSuccessValue());

				if (null == sendMessage || !sendMessage.isSuccess()) {
					logger.error("短信发送失败," + "手机号:" + mobile);
					return;
				}
				logger.info(sendMessage.getMessage() + "手机号:" + mobile);
			} else {
				String httpValueByGet = InterGatewayUtil.getHttpValueByGet(
						"/interGateway/v3/accessInformation/" + json.getString("accessSecret"),
						ip, "");
				if (httpValueByGet == null || StringUtils.isBlank(httpValueByGet)) {
					logger.error("deviceScheduler()-->fail:查询接入方信息失败,创建短信授权失败"
							+ "accessSecret :" + json.getString("accessSecret"));
					return;
				}
				JSONObject jsonObject = JSONObject.parseObject(httpValueByGet);
				String accessName = jsonObject.getString("accessTenementName");
				RpcResponse<String> registUrl = smsRegistService.smsRegist(accessName,
						json.getString("accessSecret"), catelogId);
				if (null == registUrl || !registUrl.isSuccess()) {
					logger.error("获取短信授权失败," + "accessSecret" + json.getString("accessSecret"));
					return;
				}
				sendMessage = SendSms.sendMessage(mobile, content.toString(),
						registUrl.getSuccessValue());
				if (null == sendMessage || !sendMessage.isSuccess()) {
					logger.error("短信发送失败," + "手机号:" + mobile);
					return;
				}
				logger.info(sendMessage.getMessage() + "手机号:" + mobile);
			}
		} else {
			logger.error("[手机号码" + mobile + "不合法]");
		}
	}



	private StringBuffer duanXinNeiRong(JSONObject json, RpcResponse<AlarmInfo> alarmInfo) {
		StringBuffer content = new StringBuffer();
		content.append("设施序列号为");
		content.append(json.getString("facilitiesCode"));
		content.append("的");
		content.append(json.getString("facilityTypeAlias"));
		content.append(",在");
		content.append(alarmInfo.getSuccessValue() == null ? "***"
				: alarmInfo.getSuccessValue().getAlarmTime());
		content.append("发生");
		content.append(alarmInfo.getSuccessValue() == null ? "***"
				: alarmInfo.getSuccessValue().getAlarmDesc());
		content.append(",请处理!\n地址:");
		content.append(json.getString("completeAddress"));
		return content;
	}



	private StringBuffer jiGuangTuiSong(JSONObject json, int hour, int minute) {
		StringBuffer pushContent = new StringBuffer();
		pushContent.append("检测到");
		pushContent.append("【" + json.getString("facilityTypeAlias") + "】");
		pushContent.append("【" + json.getString("facilitiesCode") + "】");
		pushContent.append("超过");
		if (hour > 0 && minute == 0) {
			pushContent.append("【" + json.getString("hour") + "】小时");
		} else if (minute > 0 && hour == 0) {
			pushContent.append("【" + json.getString("minute") + "】分钟");
		} else if (hour > 0 && minute > 0) {
			pushContent.append(
					"【" + json.getString("hour") + "】小时【" + json.getString("minute") + "】分钟");
		}
		pushContent.append("后仍未关闭，请核查！");
		return pushContent;
	}



	private JiguangEntity jiGuangEnitity1(RpcResponse<AlarmInfo> alarmInfo, List<String> userIds,
			StringBuffer pushContent) {
		JiguangEntity jiguangEntity = new JiguangEntity();
		jiguangEntity.setAliasIds(userIds);
		jiguangEntity.setMsgContent(pushContent.toString());
		jiguangEntity.setNotificationTitle("您有一条新的超时未关告警信息");
		jiguangEntity.setMsgTitle((alarmInfo.getSuccessValue() == null ? "***"
				: alarmInfo.getSuccessValue().getAlarmDesc()));
		return jiguangEntity;
	}

}
