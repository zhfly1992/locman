/*
 * File name: DeviceInfoCudRepository.java
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

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.entity.Device;

/**
 * @Description: 设备基础数据写入
 * @author: qulong
 * @version: 1.0, 2018年2月2日
 */

public interface DeviceInfoCudRepository extends BaseCrudRepository<Device, String> {

	/**
	 * 
	 * @Description: 批量保存设备数据
	 * @param devices
	 *            设备集合
	 * @return 成功条数
	 */
	public Integer saveDeviceBatch(List<Device> devices);



	/**
	 * 
	 * @Description: 批量删除设备数据
	 * @param ids
	 *            设备id集合
	 * @return 成功条数
	 */
	public Integer deleteDeviceBatch(List<String> list);



	/**
	 * 
	 * @Description: 根据appTag批量删除设备
	 * @param list
	 *            list集合
	 * @return 成功条数
	 */
	public Integer deleteDeviceBatchByAppTags(List<String> list);



	/**
	 * 
	 * @Description:设备上报数据保存时间和硬件编码到mysql
	 * @param deviceId
	 * @param lastReportedTime
	 * @param hardwareId
	 * @return
	 */
	public Integer updateDeviceLastReportTime(@Param("deviceId") String deviceId,
			@Param("lastReportTime") String lastReportTime);



	/**
	 * 
	 * @Description:设备在线离线状态保存到mysql
	 * @param deviceId
	 * @param onLineState
	 * @return
	 */
	public Integer updateDeviceOnLineState(@Param("deviceId") String deviceId,
			@Param("onLineState") String onLineState);



	/**
	 * 
	 * @Description:修改设备weih
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Integer updateDeviceDefendState(Map map);
	
	
	
	
	/**
	 * 
	 * @Description:用于判断是否新增设备
	 * @param SubDeviceId
	 * @return
	 * @author :zh
	 * @version 2020年9月2日
	 */
	Integer queryDeviceExistsBySubDeviceId(@Param("subDeviceId") String SubDeviceId);
	
	/**
	 * 
	 * @Description:用于新增设备
	 * @param map
	 * @return
	 * @author :zh
	 * @version 2020年9月2日
	 */
	Integer addDeviceFromDataConversion(Map<String, Object> map);
	
	
}
