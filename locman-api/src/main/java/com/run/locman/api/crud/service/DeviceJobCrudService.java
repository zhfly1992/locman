/*
 * File name: DeviceJobCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年7月3日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 设备同定时器关系接口
 * @author: 王胜
 * @version: 1.0, 2018年7月3日
 */

public interface DeviceJobCrudService {
	/**
	 * 
	 * @Description:添加设备同启用的定时关系
	 * @param paramJson
	 * @return RpcResponse<Integer>
	 */
	public RpcResponse<Integer> saveDeviceRsJob(JSONObject paramJson);



	/**
	 * 
	 * @Description:根据设备id删除设备定时器关系
	 * @param map
	 * @return RpcResponse<Integer>
	 */
	public RpcResponse<Integer> deleteByDeviceId(Map<String,Object> map);



	/**
	 * 
	 * @Description:根据定时器id删除设备定时器关系
	 * @param jobId
	 * @return RpcResponse<Integer>
	 */
	public RpcResponse<Integer> deleteByJobId(String jobId);

}
