/*
 * File name: AddFactoryToolRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2019年1月14日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceType;
import com.run.locman.api.entity.Factory;

/**
 * @Description:新增厂家工具接口
 * @author: 王胜
 * @version: 1.0, 2019年1月14日
 */

public interface AddFactoryToolRepository {
	/**
	 * 
	 * @Description:添加厂家
	 * @param factory
	 * @return
	 */
	public int addFactory(Factory factory);



	/**
	 * 
	 * @Description:添加厂家同appTag关系
	 * @param factory
	 * @return
	 */
	public int addFactoryRsAppTag(Map<String, Object> map);



	/**
	 * 
	 * @Description:更新新设备同老设备对应的deviceId,appTag(中间表)
	 * @param map
	 * @return
	 */
	public int updateDeviceRsNewDevice(List<Device> list);



	/**
	 * 
	 * @Description:更新设备id为新设备id
	 * @param map
	 * @return
	 */
	public int updateDeviceIdToNewDeviceId(List<Map<String, Object>> list);



	/**
	 * 
	 * @Description:添加设备类型
	 * @param deviceType
	 * @return
	 */
	public int addDeviceType(DeviceType deviceType);



	/**
	 * 
	 * @Description:更新设施同设备的关系表
	 * @param map
	 * @return
	 */
	public int updateFacilityRsDevice(List<Map<String, Object>> list);



	/**
	 * 
	 * @Description:获取老设备同新设备的关系
	 * @return
	 */
	public List<Map<String, Object>> getOldDeviceRsNewDevice();
}
