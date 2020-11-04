/*
 * File name: RemoteControlRecordQueryService.java
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

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.RemoteControlRecord;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年12月7日
 */

public interface RemoteControlRecordQueryService {

	/**
	 * 
	 * @Description: 根据设备ID和命令类型查询远程控制记录
	 * @param deviceId
	 *            设备id
	 * @return
	 */
	public RpcResponse<List<RemoteControlRecord>> getByDeviceIdAndControlType(String deviceId);



	/**
	 * 
	 * @Description:查询分权分域控制列表
	 * @param param
	 * @return
	 */
	public RpcResponse<List<Map<String, Object>>> getControlList(Map<String, Object> param, int pageNo, int pageSize);



	/**
	 * 
	 * @Description:获取该设备最近下发的未销毁的命令记录(可满足多锁)
	 * @param deviceId
	 * @return RpcResponse<List<RemoteControlRecord>>
	 */
	public RpcResponse<List<RemoteControlRecord>> getControlByDeviceId(String deviceId);



	/**
	 * 
	 * @Description:根据id查询单个下发命令控制
	 * @param deviceId
	 * @return RemoteControlRecord
	 */
	public RpcResponse<RemoteControlRecord> getRemoteControlByDeviceId(String deviceId);

}
