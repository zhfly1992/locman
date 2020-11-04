/*
* File name: DeviceInfoConvertCrudService.java								
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
* 1.0			Administrator		2018年3月28日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.model.DeviceInfoConvertModel;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2018年3月28日
 */

public interface DeviceInfoConvertCrudService {

	/**
	 * 
	 * @Description:保存
	 * @param convertInfo
	 */
	RpcResponse<String> addConvertInfo(DeviceInfoConvertModel deviceInfoConvertModel);

	/**
	 * 
	 * @Description:修改
	 * @param convertInfo
	 */
	RpcResponse<String> updateConvertInfo(DeviceInfoConvertModel deviceInfoConvertModel);

	/**
	 * 
	 * @Description:删除
	 * @param convertInfo
	 */
	RpcResponse<String> deleteConvertInfo(DeviceInfoConvertModel deviceInfoConvertModel);
	
	
	//一下为彭州项目临时使用
	/**
	 * 
	* @Description:设备同步 
	* @param deviceInfo
	* @return
	 */
	RpcResponse<String> deviceSynchronization(JSONObject deviceInfo);
	
	

}
