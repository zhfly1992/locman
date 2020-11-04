/*
 * File name: QuartzManager.java
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.run.entity.tool.DateUtils;
import com.run.locman.api.entity.AlarmOrder;
import com.run.locman.api.entity.SimpleTimerPush;
import com.run.locman.timer.util.AlarmOrderJob;
import com.run.locman.timer.util.NotPresentPicJob;
import com.run.locman.timer.util.SecurityJob;
import com.run.locman.timer.util.SimpleOrderJob;

/**
 * @Description:定时任务管理
 * @author: 王胜
 * @version: 1.0, 2018年6月13日
 */
@Component
@SuppressWarnings("resource")
public class QuartzManager {
	private static Scheduler scheduler;

	static {
		ApplicationContext context = new ClassPathXmlApplicationContext("config/spring-context.xml");
		scheduler = (StdScheduler) context.getBean("schedulerFactory");
	}

	private static final Logger			logger	= Logger.getLogger(QuartzManager.class);

	/**
	 * 启动一个自定义的job
	 * 
	 * @param schedulingJob
	 *            自定义的job
	 * @param paramsMap
	 *            传递给job执行的数据
	 * @param isStateFull
	 *            是否是一个同步定时任务，true：同步，false：异步
	 * @return 成功则返回true，否则返回false
	 */
	public static boolean enableCronSchedule(TimedJob schedulingJob, JobDataMap paramsMap, boolean isStateFull) {
		if (schedulingJob == null) {
			return false;
		}
		try {
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(schedulingJob.getTriggerName(),
					schedulingJob.getJobGroup());
			// 如果不存在该trigger则创建一个
			if (null == trigger) {
				JobDetail jobDetail = null;
				if (isStateFull) {
					jobDetail = new JobDetail(schedulingJob.getJobId(), schedulingJob.getJobGroup(),
							schedulingJob.getStateFulljobExecuteClass());
				} else {
					jobDetail = new JobDetail(schedulingJob.getJobId(), schedulingJob.getJobGroup(),
							schedulingJob.getJobExecuteClass());
				}
				jobDetail.setJobDataMap(paramsMap);
				trigger = new CronTrigger(schedulingJob.getTriggerName(), schedulingJob.getJobGroup(),
						schedulingJob.getCronExpression());
				scheduler.scheduleJob(jobDetail, trigger);
			} else {
				// Trigger已存在，那么更新相应的定时设置
				trigger.setCronExpression(schedulingJob.getCronExpression());
				scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}



	/**
	 * 禁用一个job
	 * 
	 * @param jobId
	 *            需要被禁用的job的ID
	 * @param jobGroupId
	 *            需要被警用的jobGroupId
	 * @return 成功则返回true，否则返回false
	 */
	public static boolean disableSchedule(String jobId, String jobGroupId) {
		if ("".equals(jobId) || "".equals(jobGroupId)) {
			return false;
		}
		try {
			Trigger trigger = getJobTrigger(jobId, jobGroupId);
			logger.info("trigger:" + trigger);
			if (null != trigger) {
				scheduler.deleteJob(jobId, jobGroupId);
			}
		} catch (SchedulerException e) {
			return false;
		}
		return true;
	}



	/**
	 * 得到job的详细信息
	 * 
	 * @param jobId
	 *            job的ID
	 * @param jobGroupId
	 *            job的组ID
	 * @return job的详细信息,如果job不存在则返回null
	 */
	public static JobDetail getJobDetail(String jobId, String jobGroupId) {
		if ("".equals(jobId) || "".equals(jobGroupId) || null == jobId || jobGroupId == null) {
			return null;
		}
		try {
			return scheduler.getJobDetail(jobId, jobGroupId);
		} catch (SchedulerException e) {
			return null;
		}
	}



	/**
	 * 得到job对应的Trigger
	 * 
	 * @param jobId
	 *            job的ID
	 * @param jobGroupId
	 *            job的组ID
	 * @return job的Trigger,如果Trigger不存在则返回null
	 */
	public static Trigger getJobTrigger(String jobId, String jobGroupId) {
		if ("".equals(jobId) || "".equals(jobGroupId) || null == jobId || jobGroupId == null) {
			return null;
		}
		try {
			return scheduler.getTrigger(jobId + "Trigger", jobGroupId);
		} catch (SchedulerException e) {
			return null;
		}
	}
	
	public static boolean securityFacilitiesOrders(Map<String ,Object> map ) {
		try {
			JobDataMap data =new JobDataMap();
			data.putAll(map);
			String focusSecurityId=map.get("focusSecurityId")+"";
			//创立触发器
			SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(focusSecurityId, focusSecurityId);
			
			//建立jobDetail实例
			JobDetail jobDetail=new JobDetail(focusSecurityId, focusSecurityId,SecurityJob.class);
			jobDetail.setJobDataMap(data);
			
			//实际执行时间
			Date executeDate=DateUtils.valueOf(DateUtils.stampToDate(map.get("performTime")+""));
			logger.info(String.format("securityFacilitiesOrders()-实际执行时间:%s", executeDate));
			System.out.println(executeDate+"+++++++++++++++");
			if (null == trigger) {
				// 如果不存在该trigger则创建一个
				trigger = new SimpleTrigger(focusSecurityId,focusSecurityId);
				logger.info(String.format("不存在该trigger则创建一个:%s", trigger.getJobName()));
				trigger.setStartTime(executeDate);
				scheduler.scheduleJob(jobDetail, trigger);
			} else {
				// Trigger已存在，更新相应的定时设置
				logger.info(String.format("Trigger已存在,更新相应的定时设置:JobName:%s", trigger.getJobName()));
				trigger.setStartTime(executeDate);
				scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
			}
			return true;
		}catch(Exception e) {
			return false;
		}
	}



	public static boolean simpleOrderRemindJobTrigger(SimpleTimerPush simpleTimerPush, String startTime) {

		try {
			String orderId = simpleTimerPush.getOrderId();
			JobDataMap data = new JobDataMap();
			data.put("data", simpleTimerPush);
			SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(orderId, orderId);

			JobDetail jobDetail = new JobDetail(orderId, orderId, SimpleOrderJob.class);
			jobDetail.setJobDataMap(data);
			Date startTimeDate = DateUtils.valueOf(startTime);
			int remindTime = simpleTimerPush.getRemindTime();
			//实际执行时间
			Date executeDate = DateUtils.addDate(startTimeDate, Calendar.SECOND, remindTime);
			logger.info(String.format("实际执行时间:%s", executeDate));
			if (null == trigger) {
				// 如果不存在该trigger则创建一个
				trigger = new SimpleTrigger(orderId,orderId);
				logger.info(String.format("不存在该trigger则创建一个:%s", trigger.getJobName()));
				trigger.setStartTime(executeDate);
				scheduler.scheduleJob(jobDetail, trigger);
			} else {
				// Trigger已存在，更新相应的定时设置
				logger.info(String.format("Trigger已存在,更新相应的定时设置:JobName:%s", trigger.getJobName()));
				trigger.setStartTime(executeDate);
				scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
			}
			return true;
		} catch (SchedulerException e) {
			return false;
		}

	}
	
	public static boolean alarmOrderNotReceiveJobTrigger(List<String> startUsers,String performTime,AlarmOrder alarmOrder) {
		try {
			JobDataMap data = new JobDataMap();
			data.put("data1", alarmOrder);
			data.put("userId", startUsers);
			String orderId=alarmOrder.getId();
			SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(orderId, orderId);

			JobDetail jobDetail = new JobDetail(orderId, orderId, AlarmOrderJob.class);
			jobDetail.setJobDataMap(data);
			Date executeDate=DateUtils.valueOf(performTime);
			logger.info(String.format("实际执行时间:%s", executeDate));
			if (null == trigger) {
				// 如果不存在该trigger则创建一个
				trigger = new SimpleTrigger(orderId,orderId);
				logger.info(String.format("不存在该trigger则创建一个:%s", trigger.getJobName()));
				trigger.setStartTime(executeDate);
				scheduler.scheduleJob(jobDetail, trigger);
			} else {
			// Trigger已存在，更新相应的定时设置
				logger.info(String.format("Trigger已存在,更新相应的定时设置:JobName:%s", trigger.getJobName()));
				trigger.setStartTime(executeDate);
				scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
			}
			return true;
		} catch (SchedulerException e) {
			return false;
		}

		
	}
	
	public static boolean alarmOrderNotPresentPicJobTrigger(AlarmOrder alarmOrder,String performTime) {
		try {
			JobDataMap data = new JobDataMap();
			String orderId=alarmOrder.getId();
			data.put("data", alarmOrder);
			SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(orderId+"NotPresent", orderId+"NotPresent");
			JobDetail jobDetail = new JobDetail(orderId+"NotPresent", orderId+"NotPresent", NotPresentPicJob.class);
			jobDetail.setJobDataMap(data);
			Date executeDate=DateUtils.valueOf(performTime);
			logger.info(String.format("实际执行时间:%s", executeDate));
			if (null == trigger) {
				// 如果不存在该trigger则创建一个
				trigger = new SimpleTrigger(orderId,orderId);
				logger.info(String.format("不存在该trigger则创建一个:%s", trigger.getJobName()));
				trigger.setStartTime(executeDate);
				scheduler.scheduleJob(jobDetail, trigger);
			} else {
			// Trigger已存在，更新相应的定时设置
				logger.info(String.format("Trigger已存在,更新相应的定时设置:JobName:%s", trigger.getJobName()));
				trigger.setStartTime(executeDate);
				scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
//				boolean deleteJob = scheduler.deleteJob(orderId, orderId);
//				// 如果不存在该trigger则创建一个
//				
//				trigger = new SimpleTrigger(orderId+"update",orderId+"update");
//				JobDetail jobDetail1 = new JobDetail(orderId+"update", orderId+"update", NotPresentPicJob.class);
//				jobDetail1.setJobDataMap(data);
//				trigger.setStartTime(executeDate);
//				logger.info(String.format("删除原有trigger新创建一个:%s", trigger));
//				scheduler.scheduleJob(jobDetail1, trigger);
			}
			return true;
		}catch (SchedulerException e) {
			
		}
		return false;
		
	}

	public static boolean deleteJob(String jobId, String jobGroupId) {
		if ("".equals(jobId) || "".equals(jobGroupId)) {
			return false;
		}
		try {
			Trigger trigger = scheduler.getTrigger(jobId, jobGroupId);
			logger.info("trigger:" + trigger);
			if (null != trigger) {
				boolean deleteJob = scheduler.deleteJob(jobId, jobGroupId);
				logger.info("删除job:" + deleteJob);
			}
			return true;
		} catch (SchedulerException e) {
			return false;
		}
		
	}


	public static void testJobTrigger(Trigger trigger) {

		try {
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
		}
	}

}
