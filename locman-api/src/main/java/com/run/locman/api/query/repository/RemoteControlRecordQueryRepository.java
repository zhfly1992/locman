/*
 * File name: RemoteControlRecordQueryRepository.java
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

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.run.locman.api.entity.RemoteControlRecord;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年12月7日
 */

public interface RemoteControlRecordQueryRepository extends BaseQueryRepository<RemoteControlRecord, String> {

	/**
	 * 
	 * @Description:查询分权分域控制列表
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getControlList(Map<String, Object> param);



	/**
	 * 
	 * @Description:获取该设备最近下发的未销毁的命令记录(可满足多锁)
	 * @param deviceId
	 * @return List<RemoteControlRecord>
	 */
	public List<RemoteControlRecord> getControlByDeviceId(String deviceId);



	/**
	 * 
	 * @Description:根据id查询单个下发命令控制
	 * @param deviceId
	 * @return RemoteControlRecord
	 */
	public RemoteControlRecord getRemoteControlByDeviceId(String deviceId);

}
