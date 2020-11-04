/*
 * File name: RemoteControlRecordCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月8日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.RemoteControlRecord;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年12月8日
 */

public interface RemoteControlRecordCrudService {

	/**
	 * 
	 * @Description: 保存远控记录
	 * @param remoteControlRecord
	 * @return
	 */
	public RpcResponse<String> saveRemoteControlRecord(RemoteControlRecord remoteControlRecord);



	/**
	 * 
	 * @Description:修改命令记录为无效状态
	 * @param map
	 * @return RpcResponse<String>
	 */
	public RpcResponse<String> updateControlState(Map<String, Object> map);
	
	
	
	public RpcResponse<String> sendCommandForWings(String deviceId, String productId,
			JSONObject order,Map<String, String> productTable,String serviceIdentifier);

}
