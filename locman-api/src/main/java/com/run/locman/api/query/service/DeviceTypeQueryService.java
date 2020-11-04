/*
* File name: DeviceTypeQueryService.java								
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
* 1.0			guofeilong		2018年3月2日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DeviceType;

/**
* @Description:	设备类型查询
* @author: guofeilong
* @version: 1.0, 2018年3月2日
*/

public interface DeviceTypeQueryService {
	/**
	 * * @Description: 查询设备类型接口
	 *
	 * @return	返回设备类型list
	 */
	RpcResponse<List<Map<String, Object>>> queryDeviceTypeList(String accessSecret);
	/**
	 * * @Description: 查询最新设备状态接口
	 * @param queryDeviceTypeInfo	{按需求组装} 
	 * 
	 * 			deviceTypeName 设备类型名字
	 * 			deviceTypeId 设备类型id
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, Object>>> queryDeviceTypeListByInfo(Map<String, Object> queryDeviceTypeInfo);
	/**
	  * 
	  * @Description:根据查询接入方特有的设备类型
	  * @param 
	  * @return
	  */
	RpcResponse<List<DeviceType>> queryDeviceTypeByAS(String accessSecret);
	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<List<Map<String, String>>> findAllDeviceTypeAndNum(String accessSecret);
}
