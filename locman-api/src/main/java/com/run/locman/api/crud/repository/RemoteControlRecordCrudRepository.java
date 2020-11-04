/*
 * File name: RemoteControlRecordCrudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月7日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.RemoteControlRecord;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年12月7日
 */

public interface RemoteControlRecordCrudRepository extends BaseCrudRepository<RemoteControlRecord, String> {
	/**
	 * 
	 * @Description: 根据设备id查询该设备最新的一条命令记录
	 * @param deviceId
	 * @return
	 */
	public RemoteControlRecord getRemoteRecordByDevId(String deviceId);



	/**
	 * 
	 * @Description:修改命令记录为无效状态
	 * @param map
	 * @return public Integer
	 */
	public Integer updateControlState(Map<String, Object> map);
}
