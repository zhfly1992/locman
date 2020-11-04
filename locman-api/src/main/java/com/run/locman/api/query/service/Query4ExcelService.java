/*
* File name: Query4ExcelService.java								
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
* 1.0			guofeilong		2019年8月30日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年8月30日
*/

public interface Query4ExcelService {


	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<List<JSONObject>> deviceStateInfo4Excel(JSONObject jsonObject);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<List<JSONObject>> deviceStateInfoCount(JSONObject jsonObject);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<JSONObject> changeDeviceStateFv(JSONObject jsonObject);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	void saveFilePath(String fileName);
}
