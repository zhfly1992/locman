/*
* File name: DeviceInfoConvertQueryService.java								
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
* 1.0			Administrator		2018年3月7日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DeviceInfoConvert;
import com.run.locman.api.model.DeviceInfoConvertModel;

/**
 * @Description: 设备信息数据翻译
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月7日
 */

public interface DeviceInfoConvertQueryService {

	/**
	 * 
	 * @Description:数据转换翻译
	 * @param DeviceValue
	 * 
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<Map> dataConvert(String deviceKey, String accessSecret);

	/**
	 * 
	 * @Description:分页查询
	 * @param convertParam
	 * 
	 */
	RpcResponse<PageInfo<DeviceInfoConvert>> findConvertAll(DeviceInfoConvertModel convertParam);

	/**
	 * 
	 * @Description:通过id查询
	 * @param convertParam
	 * 
	 */
	RpcResponse<DeviceInfoConvert> findConvertById(DeviceInfoConvertModel convertParam);

	/**
	 * 
	 * @Description: 转换信息的key唯一
	 * @param convertParam
	 * 
	 */
	RpcResponse<String> existConvertInfo(DeviceInfoConvertModel convertParam);
	
	/**
	 * 
	 * @Description: 检查特殊值转换（用于同步）
	 * @param accessSecret
	 * 
	 */
	
	RpcResponse<List<DeviceInfoConvert>> getDeviceInfoConvert(String accessSecret);

}
