/*
* File name: AlramOrderRemindTimerService.java								
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

package com.run.locman.api.timer.crud.service;

import java.util.List;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.AlarmOrder;

/**
* @Description:	
* @author: zhongbinyuan
* @version: 1.0, 2019年7月15日
*/

public interface AlramOrderRemindTimerService {
	/**
	 * 
	* @Description:告警工单在15分钟未被接收，app推送
	* @param startUsers
	* @param alarmOrder
	* @return
	 */
	
	RpcResponse<String> AlarmOrderNotReceive(List<String> startUsers,AlarmOrder alarmOrder);
	/**
	 * 
	* @Description:接收告警工单之后一小时没有到场处理（上传到场图片），app推送
	* @param alarmOrder
	* @return
	 */
	RpcResponse<String> AlarmOrderNotPresentPic(AlarmOrder alarmOrder);
	/**
	 * 
	* @Description:关闭定时器
	* @param alarmOrderId
	* @return
	 */
	
	RpcResponse<Boolean > closeScheduleJob(String alarmOrderId);

}
