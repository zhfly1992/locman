/*
 * File name: DeviceDataStorageCudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月26日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.entity.DeviceDataStorage;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月26日
 */

public interface DeviceDataStorageCudRepository {

	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	int addDeviceData(List<DeviceDataStorage> deviceDataStorageList);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	int deleteById(@Param("deviceDataId") String deviceDataId, @Param("userId") String userId);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	int updateDeviceData(DeviceDataStorage deviceDataStorage);

	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */
	/*
	 * List<String> getUnusedFile(int size);
	 * 
	 * int getUnusedFileCount();
	 * 
	 *//**
		 * 
		 * @Description:
		 * @param
		 * @return
		 *//*
		 * 
		 * int updateDeleteState(@Param("url")List<String> deleteFiles);
		 */



	/**
	 * 
	 * @Description:电信iot对接测试
	 * @param jsonString
	 * @param time
	 * @return
	 */
	int newIotDeviceDataChangeTest(String jsonString, String time);
}
