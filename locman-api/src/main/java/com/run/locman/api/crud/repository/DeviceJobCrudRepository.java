/*
 * File name: DeviceJobCrudRepository.java
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

package com.run.locman.api.crud.repository;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description: 设备同定时器关系接口
 * @author: 王胜
 * @version: 1.0, 2018年7月3日
 */

public interface DeviceJobCrudRepository {
	/**
	 * 
	 * @Description:添加设备同启用的定时关系
	 * @param paramJson
	 * @return Integer
	 */
	public Integer saveDeviceRsJob(JSONObject paramJson);



	/**
	 * 
	 * @Description:根据设备id删除设备定时器关系
	 * @param deviceId
	 * @return RpcResponse<Integer>
	 */
	public Integer deleteByDeviceId(Map<String, Object> map);



	/**
	 * 
	 * @Description:根据定时器id删除设备定时器关系
	 * @param jobId
	 * @return RpcResponse<Integer>
	 */
	public Integer deleteByJobId(String jobId);
}
