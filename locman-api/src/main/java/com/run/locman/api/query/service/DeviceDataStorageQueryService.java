/*
* File name: DeviceDataStorageQueryService.java								
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
* 1.0			guofeilong		2018年11月27日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.DeviceDataDto;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年11月27日
*/

public interface DeviceDataStorageQueryService {

	
	/**
	  * 
	  * @param pageSize 
	 * @param pageNum 
	 * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<PageInfo<DeviceDataDto>> queryDeviceDataStorageList(int pageNum, int pageSize, JSONObject json);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<List<DeviceDataDto>> getDeviceDataStorageById(List<String> idList);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<List<Map<String, Object>>> getAllArea();

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<Boolean> checkDeviceNumberExist(String deviceNumber, String id);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<Boolean> checkSerialNumberExist(String serialNumber, String id);

}
