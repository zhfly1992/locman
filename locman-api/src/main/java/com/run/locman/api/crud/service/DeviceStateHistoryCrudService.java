/*
* File name: DeviceStateHistoryCrudService.java								
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
* 1.0			qulong		2018年1月15日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DeviceStateHistory;

/**
* @Description:	
* @author: qulong
* @version: 1.0, 2018年1月15日
*/

public interface DeviceStateHistoryCrudService {

	/**
	 * 
	* @Description:保存设备状态数据历史记录
	* @param DeviceStateHistory 设备历史数据对象
	* @return 设备历史数据的唯一编号
	 */
	public RpcResponse<String> saveDeviceStateHistory(DeviceStateHistory deviceStateHistory);
	
}
