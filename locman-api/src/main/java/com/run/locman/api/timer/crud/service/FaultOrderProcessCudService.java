/*
* File name: FaultOrderProcessCudServiceTimer.java								
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
* 1.0			guofeilong		2018年7月4日
* ...			...			...
*
***************************************************/

package com.run.locman.api.timer.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年7月4日
*/

public interface FaultOrderProcessCudService {
	
	/**
	  * 
	  * @Description:设备超时未上报添加故障工单
	  * @param 
	  * @return
	  */
	
	RpcResponse<String> addFaultOrderForDeviceDeletion(JSONObject jsonObject);

	/**
	  * 
	  * @Description:已生成超时故障工单的设备,检测到正常之后,撤销超时故障工单
	  * @param 
	  * @return
	  */
	
	RpcResponse<String> undoTimeoutFaultOrder(JSONObject orderInfo);
}
