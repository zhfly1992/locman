/*
 * File name: DeviceDataStorageCudService.java
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

package com.run.locman.api.crud.service;

import java.util.List;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.DeviceDataDto;
import com.run.locman.api.entity.DeviceDataStorage;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月26日
 */

public interface DeviceDataStorageCudService {

	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public RpcResponse<Integer> addDeviceData(List<DeviceDataStorage> deviceDataStorageList);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public RpcResponse<Integer> deleteById(String deviceDataId, String userId);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public RpcResponse<Integer> updateDeviceData(DeviceDataStorage deviceDataStorage);



	/**
	 * 
	 * @param accessSecret
	 * @param userId
	 * @param organizationId
	 * @Description:
	 * @param
	 * @return
	 */

	public RpcResponse<Boolean> synchronizeToFacilities(DeviceDataDto deviceDataDto, String accessSecret, String userId,
			String organizationId);

	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */
	/*
	 * public List<String> deleteUnusedFile(int size);
	 * 
	 *//**
		 * 
		 * @Description:
		 * @param
		 * @return
		 */
	/*
	 * 
	 * public int updateDeleteState(List<String> deleteFiles);
	 * 
	 *//**
		 * 
		 * @Description:
		 * @param
		 * @return
		 *//*
		 * 
		 * public int getUnusedFileCount();
		 */

}
