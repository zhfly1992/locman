/*
 * File name: SendTextMessageTaskService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2019年10月21日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.timer.crud.service;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2019年10月21日
 */

public interface SendTextMessageTaskService {

	/**
	 * 
	 * @Description:告警短信定时任务，每15秒发送五分钟前的未发送短信,成华区用
	 */
	public void sendAlarmTextMessage();
}
