/*
 * File name: SwitchLockRecordCrudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月3日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.ManholeCoverSwitch;
import com.run.locman.api.entity.SwitchLockRecord;

/**
 * @Description: 开关锁记录dao
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月3日
 */

public interface SwitchLockRecordCrudRepository {

	/**
	 * 
	 * @Description:通过设备Id获取该设备的类型
	 * @param deviceId
	 * @return
	 * @throws Exception
	 */
	public String findDeviceType(String deviceId) throws Exception;



	/**
	 * 
	 * @Description:通过设备Id查询远程命令中最新的一条记录，并且在有效期间
	 * @param deviceId
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> findRemoteControlRecord(String deviceId) throws Exception;



	/**
	 * 
	 * @Description:保存开关锁记录的数据
	 * @param switchLockRecord
	 * @return
	 * @throws Exception
	 */
	public int insertSwitchLockRecord(SwitchLockRecord switchLockRecord) throws Exception;



	/**
	 * 
	 * @Description:查询开关锁记录中最新的一条->通过设备Id查询
	 * @param deviceId
	 * @return 
	 * @throws Exception
	 */
	public Map<String, String> findSwitchLockRecord(String deviceId) throws Exception;
	/**
	 * 
	* @Description:根据deviceID查询最新一条记录
	* @param deviceId
	* @return
	 */
	Map<String,Object> findManholeSwitchByDeviceID(String deviceId);
	/**
	 * 
	* @Description:插入开锁数据到表ManholeCoverSwitch
	* @param map
	* @return
	 */
	int insertManholeSwitch(ManholeCoverSwitch manholeCoverSwitch);
	
	int updateManholeSwitch(Map<String,Object> map);

}
