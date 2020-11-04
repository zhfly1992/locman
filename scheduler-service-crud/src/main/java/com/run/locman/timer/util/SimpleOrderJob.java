/*
* File name: SimpleOrderJob.java								
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
* 1.0			guofeilong		2019年1月7日
* ...			...			...
*
***************************************************/

package com.run.locman.timer.util;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.SimpleTimerPush;
import com.run.locman.constants.CommonConstants;
import com.run.sms.api.JiguangService;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年1月7日
*/

public class SimpleOrderJob implements Job {
	
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private JiguangService	jiguangService;

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			/* 拿到传入的数据 */
			JobDetail jobDetail = context.getJobDetail();
			SimpleTimerPush simpleTimerPush = (SimpleTimerPush) jobDetail.getJobDataMap().get("data");
			logger.info("拿到传入的数据时间:" + DateUtils.formatDate(new Date()));
			
			RpcResponse<String> executeMessagePush = this.executeMessagePush(simpleTimerPush);
			if (executeMessagePush.isSuccess()) {
				logger.info(executeMessagePush.getMessage());
			} else {
				logger.error(executeMessagePush.getMessage());
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
		
	}
	
	
	public RpcResponse<String> executeMessagePush(SimpleTimerPush simpleTimerPush) {
		if (null == simpleTimerPush) {
			logger.error("[executeMessagePush()-->推送实体类不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("推送实体类不能为null");
		}
		try {
			logger.info("开始执行推送时间:" + DateUtils.formatDate(new Date()));
			
			RpcResponse<Object> jiguangPush = jiguangService.sendMessage2Drefent(simpleTimerPush.getJiguangEntity());
			logger.info(DateUtils.formatDate(new Date()) + "***极光推送:" + System.currentTimeMillis());
			if (jiguangPush == null) {
				logger.error("executeMessagePush->error:极光推送失败,jiguangService.sendMessage()无返回信息");
			} else if (!jiguangPush.isSuccess()) {
				logger.error(jiguangPush.getMessage() + "executeMessagePush->error:极光推送失败");
			} else {
				logger.info(jiguangPush.getMessage() + "executeMessagePush->success:极光推送成功");
			}
			logger.info("SimpleOrderJob进入定时器时间:" + DateUtils.formatDate(new Date()));
			return RpcResponseBuilder.buildSuccessRpcResp("定时器执行成功", "定时器执行成功");
		} catch (Exception e) {
			logger.error("executeMessagePush()-->" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}

}
