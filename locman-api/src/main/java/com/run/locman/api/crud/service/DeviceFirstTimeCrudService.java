/*
 * File name: DeviceFirstTimeCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年9月20日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2018年9月20日
 */

public interface DeviceFirstTimeCrudService {
	public RpcResponse<String> addDeviceFirstTime();



	public RpcResponse<String> addDeviceLastReportTime();



	public RpcResponse<String> addDeviceOnLineState();
	
	public RpcResponse<String> addDeviceHardwareId();
}
