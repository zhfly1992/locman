/*
* File name: DeviceTypeQueryRepository.java								
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
* 1.0			王胜		2018年3月2日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.run.locman.api.entity.DeviceType;

/**
* @Description:	设备类型查询接口
* @author: 王胜
* @version: 1.0, 2018年3月2日
*/

public interface DeviceTypeQueryRepository {
	/**
	 * @return
	 * @Description: 查询设备类型列表接口
	 */
	List<Map<String, Object>> queryDeviceTypeList(String accessSecret);
	
	/**
	 * @return
	 * @Description: 查询设备类型列表接口,只查询二级设备类型，可模糊查询
	 */
	List<Map<String, Object>> queryDeviceTypeListByDeviceTypeName(Map<String, Object> queryDeviceTypeInfo);
	
	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	List<DeviceType> queryDeviceTypeByAS(String accessSecret);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<Map<String, String>> findAllDeviceTypeAndNum(String accessSecret);
	
}
