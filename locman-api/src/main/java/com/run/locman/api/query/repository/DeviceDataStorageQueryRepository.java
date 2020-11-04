/*
* File name: DeviceDataStorageQueryRepository.java								
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

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.dto.DeviceDataDto;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年11月27日
*/

public interface DeviceDataStorageQueryRepository {

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<DeviceDataDto> queryDeviceDataStorageList(JSONObject json);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<DeviceDataDto> getDeviceDataStorageById(List<String> idList);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<Map<String, Object>> getAllArea();

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int checkDeviceNumberExist(@Param("deviceNumber")String deviceNumber, @Param("id")String id);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int checkSerialNumberExist(@Param("serialNumber")String serialNumber, @Param("id")String id);

}
