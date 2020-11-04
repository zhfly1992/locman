/*
* File name: AlarmOrderRemindTimerServiceImpl.java								
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
* 1.0			zhongbinyuan		2019年7月15日
* ...			...			...
*
***************************************************/

package com.run.locman.timer.cud;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.AlarmOrder;
import com.run.locman.api.timer.crud.service.AlramOrderRemindTimerService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.scheduler.util.QuartzManager;

/**
* @Description:	
* @author: zhongbinyuan
* @version: 1.0, 2019年7月15日
*/

public class AlarmOrderRemindTimerServiceImpl implements AlramOrderRemindTimerService{
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	/**
	 * @see com.run.locman.api.timer.crud.service.AlramOrderRemindTimerService#AlarmOrderNotReceive( com.run.locman.api.entity.AlarmOrder)
	 */
	@Override
	public RpcResponse<String> AlarmOrderNotReceive(List<String> startUsers, AlarmOrder alarmOrder) {
		if (null == alarmOrder) {
			logger.error("[AlarmOrderNotReceive()-->工单实体类不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("工单实体类不能为null");
		}
		try {
			
			String creteaTime=alarmOrder.getCreateTime();
			String creteaTimes=DateUtils.dateToStamp(creteaTime);
			long creteaTimeL=Long.parseLong(creteaTimes);
			//执行时间 performTime
			Long performTimeL =creteaTimeL+900000;
			String performTimes=String.valueOf(performTimeL);
			String performTime=DateUtils.stampToDate(performTimes);
			
			boolean managerJobTrigger=QuartzManager.alarmOrderNotReceiveJobTrigger(startUsers, performTime,alarmOrder);
			if(managerJobTrigger) {
				logger.info("[AlarmOrderNotReceive()->定时器开启成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("定时器开启成功", "定时器开启成功");
			} else {
				logger.error("[AlarmOrderNotReceive()->定时器开启失败]");
				return RpcResponseBuilder.buildErrorRpcResp("定时器开启失败");
			}
		}catch(Exception e) {
			logger.error("AlarmOrderNotReceive()->exception",e);
			 return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

	/**
	 * @see com.run.locman.api.timer.crud.service.AlramOrderRemindTimerService#AlarmOrderNotPresentPic(com.run.locman.api.entity.AlarmOrder)
	 */
	@Override
	public RpcResponse<String> AlarmOrderNotPresentPic(AlarmOrder alarmOrder) {
		if (null == alarmOrder) {
			logger.error("[AlarmOrderNotPresentPic()-->推送实体类不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("推送实体类不能为null");
		}
		
		try {
			String receiveTime=alarmOrder.getReceiveTime();
			String receiveTimes=DateUtils.dateToStamp(receiveTime);
			long receiveTimeL=Long.parseLong(receiveTimes);
			
			Long performTimeL =receiveTimeL+3600000;
			String performTimes=String.valueOf(performTimeL);
			String performTime=DateUtils.stampToDate(performTimes);
			boolean alarmOrderNotPresentPicJobTrigger = QuartzManager.alarmOrderNotPresentPicJobTrigger(alarmOrder, performTime);
			if(alarmOrderNotPresentPicJobTrigger) {
				logger.info("[AlarmOrderNotPresentPic()->定时器开启成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("定时器开启成功", "定时器开启成功");
			} else {
				logger.error("[AlarmOrderNotPresentPic()->定时器开启失败]");
				return RpcResponseBuilder.buildErrorRpcResp("定时器开启失败");
			}
		} catch (Exception e) {
			logger.error("AlarmOrderNotPresentPic()->exception",e);
			 return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

	/**
	 * @see com.run.locman.api.timer.crud.service.AlramOrderRemindTimerService#closeScheduleJob(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> closeScheduleJob(String alarmOrderId) {
		if (StringUtils.isBlank(alarmOrderId)) {
			logger.error("[closeScheduleJob()-->工单id不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("工单id不能为空");
		}
		try {
			boolean disableSchedule = QuartzManager.deleteJob(alarmOrderId, alarmOrderId);
			return RpcResponseBuilder.buildSuccessRpcResp("定时器关闭操作执行完毕", disableSchedule);
		} catch (Exception e) {
			logger.error("closeScheduleJob()",e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
