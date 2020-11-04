/*
* File name: ExecuteMethodTimerService.java								
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
* 1.0			guofeilong		2018年12月28日
* ...			...			...
*
***************************************************/

package com.run.locman.api.timer.crud.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.SimpleTimerPush;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年12月28日
*/

public interface ExecuteMethodTimerService {

	/**
	  * 
	  * @Description:
	  * @param simpleTimerPush 配置推送相关内容(不能用,用时完善)
	  * @return
	  */
	
	RpcResponse<String> executeMessagePush(SimpleTimerPush simpleTimerPush);
	
	/**
	  * 
	  * @Description:检验提前提示时间是否符合规则并推送到极光
	  * @param simpleTimerPush配置推送相关内容  startTime 工单开始时间  endTime 工单结束时间  remindTimeByMinute 提前多少分钟提醒
	  * @return
	  */
	RpcResponse<String> checkAndExecuteMessagePush(SimpleTimerPush simpleTimerPush, String startTime, String endTime, String remindTimeByMinute);
	
	/**
	  * 
	  * @Description:检验提前提示时间是否符合规则
	  * @param startTime 工单开始时间  endTime 工单结束时间  remindTimeByMinute 提前多少分钟提醒
	  * @return 时间:多久之后提醒
	  */
	RpcResponse<String> checkAndGetTime(String startTime, String endTime, String remindTimeByMinute);

	
	RpcResponse<Boolean> closeScheduleJob(String simpleOrderId);
}
