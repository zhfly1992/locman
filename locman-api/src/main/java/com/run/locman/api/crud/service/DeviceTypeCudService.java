/*
* File name: DeviceTypeCudService.java								
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
* 1.0			guofeilong		2018年10月23日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DeviceType;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年10月23日
*/

public interface DeviceTypeCudService {

	RpcResponse<Integer> addDeviceType(List<DeviceType> deviceTypeList);
	
	RpcResponse<Integer> deleteDeviceTypeById(List<DeviceType> deviceTypeList);
	
	RpcResponse<String> editDeviceType(JSONObject jsonObject);
}
