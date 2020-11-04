/*
 * File name: DeviceInfoCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2018年2月2日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceType;

/**
 * @Description: 设备基础数据写入 Service
 * @author: qulong
 * @version: 1.0, 2018年2月2日
 */

public interface DeviceInfoCudService {


	/**
	 * 
	 * @Description: 批量删除设备数据
	 * @param ids
	 *            设备id集合
	 * @return 成功条数
	 */
	public RpcResponse<Integer> deleteDevices(List<String> ids);



	/**
	 * 
	 * @Description: 同步设备,设备类型数据
	 * @param devices
	 *            从新iot获取的设备信息
	 * @param appTagList
	 *            要同步的AppTag
	 * @param accessSecret
	 *            接入方密钥
	 * @return
	 */
	public RpcResponse<Boolean> synchroDevices(List<Device> devices, List<DeviceType> deviceTypes,
			List<String> appTagList, String accessSecret);



	/**
	 * 
	 * @Description:修改设备维护状态 true是正常 false是维护中
	 * @param deviceId
	 * @param deviceState
	 * @return
	 */
	public RpcResponse<Boolean> updateDeviceDefendState(List<String> deviceIds, Boolean deviceState);
	
	
	/**
	 * 
	 * @Description:从数据转换中心获取设备
	 * @return
	 * @author :zh
	 * @version 2020年9月2日
	 */
	public RpcResponse<String> synchroDevicesForDataConversion(JSONArray jsonArray,String accessSecret,String locmanDeviceTypeId,String dcDeviceTypeId,String applicationId);

}
