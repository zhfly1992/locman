/*
* File name: AlarmOrderJob.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			zhongbinyuan		2019年7月16日
* ...			...			...
*
***************************************************/

package com.run.locman.timer.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.AlarmInfo;
import com.run.locman.api.entity.AlarmOrder;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.timer.crud.service.AlramOrderRemindTimerService;
import com.run.locman.constants.CommonConstants;
import com.run.sms.api.JiguangService;

import entity.JiguangEntity;

/**
* @Description:	
* @author: zhongbinyuan
* @version: 1.0, 2019年7月16日
*/

public class AlarmOrderJob implements Job{
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private JiguangService	jiguangService;
	@Autowired
	private AlarmOrderQueryService alarmOrderQueryService;
	@Autowired
	private AlarmInfoQueryService alarmInfoQueryService;
	@Autowired
	private AlramOrderRemindTimerService alramOrderRemindTimerService;

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			/* 拿到传入的数据 */
			JobDetail jobDetail = context.getJobDetail();
			AlarmOrder alarmOrder1=(AlarmOrder) jobDetail.getJobDataMap().get("data1");
			List<String> userIds=(List<String>) jobDetail.getJobDataMap().get("userId");
			String alarmId2=alarmOrder1.getAlarmId();
			AlarmInfo alarmInfo1=alarmInfoQueryService.findById(alarmId2).getSuccessValue();
			String alarmSerialNum1=String.valueOf(alarmInfo1.getSerialNum());
			JiguangEntity jiguangEntity = new JiguangEntity();
			jiguangEntity.setAliasIds(userIds);
			jiguangEntity.setNotificationTitle("告警工单");
			jiguangEntity.setMsgTitle("告警工单");
			jiguangEntity.setMsgContent(String.format("您好！告警流水号为：%s，工单已经生成15分钟，请及时到场接收工单!", alarmSerialNum1));
			JSONObject json = new JSONObject();
			json.put("processType", "alarmProcess");
			json.put("orderId", alarmOrder1.getId());
			jiguangEntity.setExtrasparam(json.toJSONString());
			logger.info("AlarmOrderJob()->告警信息："+alarmInfo1);
			String orderId=alarmOrder1.getId();
			Map<String, Object> map=alarmOrderQueryService.getAlarmOrderInfoById(orderId).getSuccessValue();
			String processState=map.get("processState").toString();
				if(processState.equals("5")) {
					RpcResponse<String> executeMessagePush = this.executeMessagePush(jiguangEntity);
					if (executeMessagePush.isSuccess()) {
						logger.info(executeMessagePush.getMessage());
					} else {
						logger.error(executeMessagePush.getMessage());
					}
				}else {
					RpcResponse<Boolean> closeScheduleJob = alramOrderRemindTimerService.closeScheduleJob(orderId);
					if(closeScheduleJob.isSuccess()) {
						logger.info(closeScheduleJob.getMessage());
					}else {
						logger.error(closeScheduleJob.getMessage());
					}
				}
		}catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	* @Description:
	* @param jiguangEntity
	* @return
	*/
	
	private RpcResponse<String> executeMessagePush(JiguangEntity jiguangEntity) {
		if (null == jiguangEntity) {
			logger.error("[executeMessagePush()-->推送实体类不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("推送实体类不能为null");
		}
		try {
			logger.info("开始执行推送时间:" + DateUtils.formatDate(new Date()));
			
			RpcResponse<Object> jiguangPush = jiguangService.sendMessage2Drefent(jiguangEntity);
			logger.info(DateUtils.formatDate(new Date()) + "***极光推送:" + System.currentTimeMillis());
			if (jiguangPush == null) {
				logger.error("executeMessagePush->error:极光推送失败,jiguangService.sendMessage()无返回信息");
			} else if (!jiguangPush.isSuccess()) {
				logger.error(jiguangPush.getMessage() + "executeMessagePush->error:极光推送失败");
			} else {
				logger.info(jiguangPush.getMessage() + "executeMessagePush->success:极光推送成功");
			}
			logger.info("AlarmOrderJob进入定时器时间:" + DateUtils.formatDate(new Date()));
			return RpcResponseBuilder.buildSuccessRpcResp("定时器执行成功", "定时器执行成功");
		} catch (Exception e) {
			logger.error("executeMessagePush()-->" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}
	}

