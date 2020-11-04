/*
* File name: DeviceDetetionTimer.java								
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
* 1.0			guofeilong		2018年6月14日
* ...			...			...
*
***************************************************/

package com.run.locman.timer;

import org.springframework.stereotype.Component;

/**
* @Description:	定时检测设备,超时未上报产生故障工单
* @author: guofeilong
* @version: 1.0, 2018年6月14日
*/
@Component
public class DeviceDetetionTimer {
/*	private static final Logger logger = Logger.getLogger(DeviceDetetionTimer.class);
	@Autowired
	DeviceQueryService deviceQueryService;
	
	//@Scheduled(fixedRate=20000) 
	public void test1(){
		System.out.println("业火红莲60秒一次");
		Long queryMillisTime = GetInternateTimeUtil.queryMillisTime();
		String queryTime = GetInternateTimeUtil.queryTime();
		System.out.println(queryMillisTime);
		System.out.println(queryTime);
		//deviceQueryService.DeviceDetetion();
	}
	
	//@Scheduled(cron = "0 0 0 * * ?") //每天晚上0点00分调用此方法
	public void StartDeviceDetetion(){
		logger.info("超时未上报设备每日例行检测开始");
		deviceQueryService.DeviceDetetion();
	}
	//@Scheduled(cron = "0 * 11 * * ?") //每天晚上0点00分调用此方法
	public void test2(){
		logger.info(GetInternateTimeUtil.queryTime() + "超时未上报设备每日例行检测开始");
		deviceQueryService.DeviceDetetion();
		logger.info(GetInternateTimeUtil.queryTime() + "检测结束");
	}*/
}
