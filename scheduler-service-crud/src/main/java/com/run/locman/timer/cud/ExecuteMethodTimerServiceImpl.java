/*
 * File name: ExecuteMethodTimerServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年12月28日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.timer.cud;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.SimpleTimerPush;
import com.run.locman.api.timer.crud.service.ExecuteMethodTimerService;
import com.run.locman.api.util.GetInternateTimeUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.scheduler.util.QuartzManager;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年12月28日
 */

public class ExecuteMethodTimerServiceImpl implements ExecuteMethodTimerService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	private static long oneHour = 3600000L;
	private static long oneMinute = 60000L;
	@Override
	public RpcResponse<String> executeMessagePush(SimpleTimerPush simpleTimerPush) {
		if (null == simpleTimerPush) {
			logger.error("[executeMessagePush()-->推送实体类不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("推送实体类不能为null");
		}
		long remindTime = simpleTimerPush.getRemindTime();
		if (remindTime < 0) {
			/*logger.error("[executeMessagePush()-->延迟时间不能小于0]");
			return RpcResponseBuilder.buildErrorRpcResp("延迟时间不能小于0");*/
			remindTime = 0;
		}
		try {
			logger.info("进入定时器时间:" + DateUtils.formatDate(new Date()));
			
			
			return RpcResponseBuilder.buildSuccessRpcResp("定时器设置成功", "定时器设置成功");
		} catch (Exception e) {
			logger.error("executeMessagePush()-->" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.timer.crud.service.ExecuteMethodTimerService#checkAndExecuteMessagePush(entity.JiguangEntity,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RpcResponse<String> checkAndExecuteMessagePush(SimpleTimerPush simpleTimerPush, String startTime, String endTime,
			String remindTimeByMinute) {
		if (null == simpleTimerPush) {
			logger.error("[checkAndExecuteMessagePush()-->推送实体类不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("推送实体类不能为null");
		}
		try {
			RpcResponse<String> checkTimeResult = checkTime(startTime, endTime, remindTimeByMinute);
			if (!checkTimeResult.isSuccess()) {
				logger.error(String.format("[checkAndExecuteMessagePush()->%s]", checkTimeResult.getMessage()));
				return checkTimeResult;
			}
			String successValue = checkTimeResult.getSuccessValue();
			//Long remindTime = Long.parseLong(successValue);
			//开始时间秒后提醒
			int remindTime = Integer.parseInt(successValue);
			simpleTimerPush.setRemindTime(remindTime);
			
			boolean myJobTrigger = QuartzManager.simpleOrderRemindJobTrigger(simpleTimerPush,startTime);
			if (myJobTrigger) {
				logger.info("[checkAndExecuteMessagePush()->定时器开启成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("定时器开启成功", "定时器开启成功");
			} else {
				logger.error("[checkAndExecuteMessagePush()->定时器开启失败]");
				return RpcResponseBuilder.buildErrorRpcResp("定时器开启失败");
			}

		} catch (Exception e) {
			 logger.error("checkAndExecuteMessagePush()->exception",e);
			 return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

		

	}

	
	
	private RpcResponse<String> checkTime(String startTime, String endTime, String remindTimeByMinute) {
		try {
			if (StringUtils.isBlank(startTime)) {
				logger.error("[checkTime()-->开始日期不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("开始日期不能为空");
			}
			if (StringUtils.isBlank(endTime)) {
				logger.error("[checkTime()-->结束日期不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("结束日期不能为空");
			}
			boolean validStart = DateUtils.isValidDate(startTime);
			if (!validStart) {
				logger.error("[checkTime()->开始日期格式错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("开始日期格式错误!");
			}
			boolean validEnd = DateUtils.isValidDate(endTime);
			if (!validEnd) {
				logger.error("[checkTime()->结束日期格式错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束日期格式错误!");
			}
			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long startTimeLong = Long.parseLong(startTimeStamp);
			//TODO 空指针
			String endTimeStamp = DateUtils.dateToStamp(endTime);
			long endTimeLong = Long.parseLong(endTimeStamp);
			// 工单施工时间段:毫秒
			long timeSlot = endTimeLong - startTimeLong;
			if (timeSlot <= 0) {
				logger.error("[checkTime()->结束时间不能小于等于开始时间!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束时间不能小于等于开始时间!");
			}
			//提前提醒时间不为空也不是数字时
			if (StringUtils.isNotBlank(remindTimeByMinute) && !StringUtils.isNumeric(remindTimeByMinute)) {
				logger.error("[checkTime()->参数错误,提示时间格式必须是数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数错误,提示时间格式必须是数字!");
			}
			long remindTimeLong = 0L;
			//当前时间
			long nowLong = GetInternateTimeUtil.queryMillisTime();
			//当前时间至结束时间段
			long now2end = endTimeLong - nowLong;
			if (now2end <= 0) {
				logger.error("[checkTime()->参数错误,工单结束时间不能为过去时间!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数错误,工单结束时间不能为过去时间!");
			}
			//提醒时间参数为空,使用默认提示
			if (StringUtils.isBlank(remindTimeByMinute)) {
				remindTimeLong = panduan1(timeSlot, now2end);
			} else {
				//自定义提醒时间
				Integer time = Integer.parseInt(remindTimeByMinute);
				int tenMinute = 10;
				if (time < tenMinute) {
					logger.error(String.format("[checkTime()-->自定义工单结束提醒时间不能小于10分钟:%s]", remindTimeByMinute));
					return RpcResponseBuilder.buildErrorRpcResp("自定义工单结束提醒时间不能小于10分钟");
				}
				long remindTimeByMinute2Long = time * oneMinute;

				if (remindTimeByMinute2Long >= timeSlot) {
					logger.error("[checkTime()->自定义提示时间不能超过工单的总时长!]");
					return RpcResponseBuilder.buildErrorRpcResp("自定义提示时间不能超过工单的总时长!");
				}
				remindTimeLong = timeSlot - remindTimeByMinute2Long;
			}
			//开始时间秒数之后提醒
			return tixing(remindTimeLong);
		} catch (Exception e) {
			 logger.error("checkAndExecuteMessagePush()->exception",e);
			 return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		
	}



	private RpcResponse<String> tixing(long remindTimeLong) {
		long remindTimeSeconds = remindTimeLong / 1000;
		String realRemind = String.valueOf(remindTimeSeconds);
		return RpcResponseBuilder.buildSuccessRpcResp("时间校验无误", realRemind);
	}



	private long panduan1(long timeSlot, long now2end) {
		long remindTimeLong;
		//大于1小时的工单，提醒时间在离结束时间的1小时提醒
		//小于1小时的工单，提醒时间在离结束时间的50%的时候提醒
		if (timeSlot > oneHour) {
			remindTimeLong = timeSlot - oneHour;
		} else {
			//提前提醒时间
			//TODO 空指针
			long timeLong = timeSlot /2;
			remindTimeLong = now2end - timeLong;
		}
		return remindTimeLong;
	}
	
	
	
/*	
	private RpcResponse<String> checkTime(String startTime, String endTime, String remindTimeByMinute) {
		
		try {
			if (StringUtils.isBlank(startTime)) {
				logger.error("[checkTime()-->开始日期不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("开始日期不能为空");
			}
			if (StringUtils.isBlank(endTime)) {
				logger.error("[checkTime()-->结束日期不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("结束日期不能为空");
			}
			boolean validStart = DateUtils.isValidDate(startTime);
			if (!validStart) {
				logger.error("[checkTime()->开始日期格式错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("开始日期格式错误!");
			}
			boolean validEnd = DateUtils.isValidDate(endTime);
			if (!validEnd) {
				logger.error("[checkTime()->结束日期格式错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束日期格式错误!");
			}
			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long startTimeLong = Long.parseLong(startTimeStamp);

			String endTimeStamp = DateUtils.dateToStamp(endTime);
			long endTimeLong = Long.parseLong(endTimeStamp);
			// 工单施工时间段:毫秒
			long timeSlot = endTimeLong - startTimeLong;
			if (timeSlot <= 0) {
				logger.error("[checkTime()->结束时间不能小于等于开始时间!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束时间不能小于等于开始时间!");
			}
			//提前提醒时间不为空也不是数字时
			if (StringUtils.isNotBlank(remindTimeByMinute) && !StringUtils.isNumeric(remindTimeByMinute)) {
				logger.error("[checkTime()->参数错误,提示时间格式必须是数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数错误,提示时间格式必须是数字!");
			}
			long remindTimeLong = 0L;
			//当前时间
			long nowLong = System.currentTimeMillis();
			//当前时间至结束时间段
			long now2end = endTimeLong - nowLong;
			if (now2end <= 0) {
				logger.error("[checkTime()->参数错误,工单结束时间不能为过去时间!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数错误,工单结束时间不能为过去时间!");
			}
			
			//提醒时间参数为空,使用默认提示
			if (StringUtils.isBlank(remindTimeByMinute)) {
				//大于1小时的工单，提醒时间在离结束时间的1小时提醒
				//小于1小时的工单，提醒时间在离结束时间的50%的时候提醒
				if (timeSlot > oneHour) {
					remindTimeLong = now2end - oneHour;
				} else {
					//提前提醒时间
					long timeLong = timeSlot /2;
					remindTimeLong = now2end - timeLong;
				}
				
			} else {
				Integer time = Integer.parseInt(remindTimeByMinute);
				int tenMinute = 10;
				if (time < tenMinute) {
					logger.error("[checkTime()-->自定义工单结束提醒时间不能小于10分钟]");
					return RpcResponseBuilder.buildErrorRpcResp("自定义工单结束提醒时间不能小于10分钟");
				}
				long remindTimeByMinute2Long = time * oneMinute;

				if (remindTimeByMinute2Long >= timeSlot) {
					logger.error("[checkTime()->自定义提示时间不能超过工单的总时长!]");
					return RpcResponseBuilder.buildErrorRpcResp("自定义提示时间不能超过工单的总时长!");
				}
				remindTimeLong = now2end - remindTimeByMinute2Long;

			}
			//开始时间秒数之后提醒
			long remindTimeSeconds = remindTimeLong / 1000;
			String realRemind = String.valueOf(remindTimeSeconds);
			return RpcResponseBuilder.buildSuccessRpcResp("时间校验无误", realRemind);
		} catch (Exception e) {
			 logger.error("checkAndExecuteMessagePush()->exception",e);
			 return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		
	}*/
	


	/**
	 * @see com.run.locman.api.timer.crud.service.ExecuteMethodTimerService#checkAndGetTime(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RpcResponse<String> checkAndGetTime(String startTime, String endTime, String remindTimeByMinute) {
		logger.info(String.format("checkAndGetTime()-->进入方法,参数:[startTime:%s][endTime:%s][remindTimeByMinute:%s]", startTime, endTime, remindTimeByMinute));
		try {
			RpcResponse<Long> checkAndGetStart2EndTime = checkAndGetStart2EndTime(startTime, endTime, remindTimeByMinute);
			if (!checkAndGetStart2EndTime.isSuccess()) {
				return RpcResponseBuilder.buildErrorRpcResp(checkAndGetStart2EndTime.getMessage());
			}
			Long timeSlot = checkAndGetStart2EndTime.getSuccessValue();
			//实际毫秒后提醒
			long realRemindTimeLong = 0L;
			//提醒时间参数为空,使用默认提示
			if (StringUtils.isBlank(remindTimeByMinute)) {
				//大于1小时的工单，提醒时间在离结束时间的1小时提醒
				//小于1小时的工单，提醒时间在离结束时间的50%的时候提醒
				if (timeSlot > oneHour) {
					realRemindTimeLong = oneHour;
				} else {
					realRemindTimeLong = timeSlot / 2;
					
				}
				
			} else {
				Integer time = Integer.parseInt(remindTimeByMinute);
				int tenMinute = 10;
				if (time < tenMinute) {
					logger.error("[checkTime()-->自定义工单结束提醒时间不能小于10分钟]");
					return RpcResponseBuilder.buildErrorRpcResp("自定义工单结束提醒时间不能小于10分钟");
				}
				long remindTimeLong = time * oneMinute;

				if (remindTimeLong >= timeSlot) {
					logger.error("[checkTime()->自定义提示时间不能超过工单的总时长!]");
					return RpcResponseBuilder.buildErrorRpcResp("自定义提示时间不能超过工单的总时长!");
				}
				realRemindTimeLong = remindTimeLong;

			}
			//转换为秒数
			long remindTimeSeconds = realRemindTimeLong / 1000;
			String realRemind = String.valueOf(remindTimeSeconds);
			return RpcResponseBuilder.buildSuccessRpcResp("时间校验无误", realRemind);
		} catch (Exception e) {
			 logger.error("checkAndExecuteMessagePush()->exception",e);
			 return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<Long> checkAndGetStart2EndTime(String startTime, String endTime, String remindTimeByMinute){
		try {
			if (StringUtils.isBlank(startTime)) {
				logger.error("[checkTime()-->开始日期不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("开始日期不能为空");
			}
			if (StringUtils.isBlank(endTime)) {
				logger.error("[checkTime()-->结束日期不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("结束日期不能为空");
			}
			boolean validStart = DateUtils.isValidDate(startTime);
			if (!validStart) {
				logger.error("[checkTime()->开始日期格式错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("开始日期格式错误!");
			}
			boolean validEnd = DateUtils.isValidDate(endTime);
			if (!validEnd) {
				logger.error("[checkTime()->结束日期格式错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束日期格式错误!");
			}
			//网络时间毫秒数
			Long internateMillisTime = GetInternateTimeUtil.queryMillisTime();
			if (null == internateMillisTime) {
				logger.info("checkTime()->网络时间获取失败,采用系统时间");
				internateMillisTime = System.currentTimeMillis();
			}
			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long startTimeLong = Long.parseLong(startTimeStamp);
			if (startTimeLong - internateMillisTime <= 0) {
				logger.error("[checkTime()->开始时间不能是过往时间!]");
				return RpcResponseBuilder.buildErrorRpcResp("开始时间不能是过往时间!");
			}
			String endTimeStamp = DateUtils.dateToStamp(endTime);
			
			long endTimeLong = Long.parseLong(endTimeStamp);
			// 工期:毫秒
			long timeSlot = endTimeLong - startTimeLong;
			if (timeSlot <= 0) {
				logger.error("[checkTime()->结束时间不能小于等于开始时间!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束时间不能小于等于开始时间!");
			}
			long twentyMinute = 20 * oneMinute;
			if (timeSlot <= twentyMinute) {
				logger.error("[checkTime()->结束时间与开始时间不能低于20分钟!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束时间与开始时间不能低于20分钟!");
			}
			//不为空也不是数字时
			if (StringUtils.isNotBlank(remindTimeByMinute) && !StringUtils.isNumeric(remindTimeByMinute)) {
				logger.error("[checkTime()->参数错误,提示时间格式必须是数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数错误,提示时间格式必须是数字!");
			}
			return RpcResponseBuilder.buildSuccessRpcResp("时间格式检验通过", timeSlot);
		} catch (Exception e) {
			logger.error("checkAndGetStart2EndTime",e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.timer.crud.service.ExecuteMethodTimerService#closeScheduleJob(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> closeScheduleJob(String simpleOrderId) {
		if (StringUtils.isBlank(simpleOrderId)) {
			logger.error("[closeScheduleJob()-->工单id不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("工单id不能为空");
		}
		try {
			boolean disableSchedule = QuartzManager.deleteJob(simpleOrderId, simpleOrderId);
			return RpcResponseBuilder.buildSuccessRpcResp("定时器关闭操作执行完毕", disableSchedule);
		} catch (Exception e) {
			logger.error("closeScheduleJob()",e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
