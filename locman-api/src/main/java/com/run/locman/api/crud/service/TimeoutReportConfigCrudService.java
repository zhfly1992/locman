/*
* File name: TimeoutReportConfigCrudService.java								
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
* 1.0			guofeilong		2018年6月22日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
* @Description:	超时未上报配置
* @author: guofeilong
* @version: 1.0, 2018年6月22日
*/

public interface TimeoutReportConfigCrudService {

	RpcResponse<String> addTimeoutReportConfig(JSONObject configInfo);
	
	RpcResponse<String> updateTimeoutReportConfig(JSONObject configInfo);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<String> delTimeoutReportConfig(List<String> congigIds);
}
