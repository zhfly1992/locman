/*
* File name: Query4ExcelRepository.java								
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
* 1.0			guofeilong		2019年8月30日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;

/**
* @Description:	查询临时性的Excel信息
* @author: guofeilong
* @version: 1.0, 2019年8月30日
*/

public interface Query4ExcelRepository {

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<JSONObject> getAllDevicesRsFacilitiesInfo(@Param("accessSecret") String accessSecret, @Param("deviceType") String deviceType,
			@Param("lastTime") String lastTime);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int changeDeviceStateFv(@Param("dc") String fv, @Param("deviceId") String deviceId);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int saveFilePath(String fileName);
	
	List<JSONObject> getFacilitiesRsStateInfo();



}
