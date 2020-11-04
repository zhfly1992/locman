/*
 * File name: UpdateZbyCrudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhongbinyuan 2019年1月15日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: 更新告警表，设备实时上报属性统计表
 * @author: zhongbinyuan
 * @version: 1.0, 2019年1月15日
 */

public interface UpdateZbyCrudRepository {
	
	/**
	 * 
	 * @Description:获取新老设备id
	 * @return
	 */
	List<Map<String,Object>> findOldORNewId();

	/**
	 * 
	 * @Description:更新告警表
	 * @return
	 */
	Integer updateAlarmInfoDeviceId(@Param("deviceIds") List<Map<String, Object>> deviceIds);



	/**
	 * 
	 * @Description:更新设备实时上报属性统计表
	 * @return
	 */
	Integer updateRealRportedDeviceId(@Param("deviceIds") List<Map<String, Object>> deviceIds);

}
