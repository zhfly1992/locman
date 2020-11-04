/*
 * File name: DeviceDetetionTimer.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年6月14日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.timer;


import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.run.entity.tool.DateUtils;
import com.run.locman.api.crud.service.DeviceReportedCrudService;
import com.run.locman.api.timer.crud.service.SendTextMessageTaskService;
import com.run.locman.api.timer.query.service.DeviceQuery;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 定时检测设备,超时未上报产生故障工单
 * @author: guofeilong
 * @version: 1.0, 2018年6月14日
 */
@Component
@EnableScheduling
public class DeviceDetetionTimer {
	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	DeviceQuery					deviceQuery;

	@Autowired
	DeviceReportedCrudService	deviceReportedCrudService;

	@Autowired
	SendTextMessageTaskService	sendTextMessageTaskService;



	@Scheduled(cron = "${execution.frequency:0 0 12 * * ?}")
	public void startDeviceDetetion() {
		logger.info(DateUtils.formatDate(new Date()) + "超时未上报设备每日例行检测开始");
		deviceQuery.deviceDetetion();
		logger.info(DateUtils.formatDate(new Date()) + "检测结束");
	}



	@Scheduled(cron = "${execution.frequency:0 0 12 * * ?}")
	public void startcheckFaultOrder() {
		logger.info(DateUtils.formatDate(new Date()) + "每日例行检测开始:检测已生成超时未上报但未审核的故障工单的设备是否正常上报");
		deviceQuery.checkFaultOrder();
		logger.info(DateUtils.formatDate(new Date()) + "检测结束");
	}



	@Scheduled(fixedRate = 8000)
	public void startRetryCacheData() {
//		logger.info(DateUtils.formatDate(new Date()) + "开始检测未处理完成的上报信息");
		deviceReportedCrudService.startRetryCacheData();
//		logger.info(DateUtils.formatDate(new Date()) + "检测结束");
	}



	@Scheduled(cron = "0 0 1 * * ?")
	public void getDeviceCountTimingAndTrigger() {

		logger.info(DateUtils.formatDate(new Date()) + "查询前一天的设备上报timing和trigger数量以及上报总数量");
		deviceQuery.insertCountTimingAndTrigger();
		logger.info(DateUtils.formatDate(new Date()) + "查询结束");
	}



	/**
	 * 
	 * @Description:定时发送短信功能，每15s执行一次，成华区用
	 */
	@Scheduled(fixedRate = 15 * 1000)
	public void sendMessageTask() {
	//	logger.info(String.format("[短信定时器任务开始->%s]", DateUtils.formatDate(new Date())));
		sendTextMessageTaskService.sendAlarmTextMessage();
	//	logger.info(String.format("[短信定时任务结束->%s]", DateUtils.formatDate(new Date())));
	}



	@Scheduled(cron = "0 0 2 * * ?")
	public void automaticMaintenanceByTrigger() {
		logger.info(DateUtils.formatDate(new Date()) + "自动维护trigger次数多的设备");
		deviceQuery.automaticMaintenanceByTrigger();
		logger.info(DateUtils.formatDate(new Date()) + "自动解除timing次数正常（并无trigger多）的设备");
		deviceQuery.automaticLiftingByTiming();
		logger.info(DateUtils.formatDate(new Date()) + "查询结束");

	}
	
	
	
	@Scheduled(cron = "0 0 3 * * ?")
	public void queryAndAlarm() {
		logger.info(DateUtils.formatDate(new Date()) + "开始xyz");
		deviceQuery.queryAndAlarm();
		logger.info(DateUtils.formatDate(new Date()) + "结束");
	}
	
	@Scheduled(fixedRate = 60 * 1000 *3)
	public void querySecurityFacilitiesOrders() {
		deviceQuery.querySecurityFacilitiesOrders();
	}

}