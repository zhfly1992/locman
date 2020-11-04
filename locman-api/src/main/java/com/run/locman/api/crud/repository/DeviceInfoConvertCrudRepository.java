/*
 * File name: DeviceInfoConvertRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年3月28日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import com.run.locman.api.model.DeviceInfoConvertModel;

/**
 * @Description: crud
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月28日
 */
public interface DeviceInfoConvertCrudRepository extends BaseCrudRepository<DeviceInfoConvertModel, String> {

	/**
	 * 
	 * @Description:批量删除
	 * @param ids
	 * @return
	 */
	int deleteConvertInfo(List<String> ids);
	
	//彭州临时使用设备同步
	
	/**
	 * 
	* @Description:根据接入方密钥查询设备类型ID  list集合
	* @param accessSecret
	* @return
	 */
	List<String>  deviceTypeIdListBYAccessSecret(String accessSecret);
	
	/**
	 * 
	* @Description:根据设备类型以及接入方密钥查询设备ID集合
	* @param map
	* @return
	 */
	List<String> deviceIdListByAccAndType(Map<String,Object> map);
	/**
	 * 
	* @Description:根据设备类型以及接入方密钥删除设备
	* @param map
	* @return
	 */
	
	Integer deleteDeviceByAccAndType(Map<String,Object> map);
	/**
	 * 
	* @Description:修改设备类型名称
	* @param map
	* @return
	 */
	
	Integer updateDeviceTypeNameByProductId(Map<String,Object> map);
	
	/**
	 * 
	* @Description:中间表信息删除
	* @param deviceId
	* @return
	 */
	int  deleteFacDevice(List<String> deviceIds);
}
