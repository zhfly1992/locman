/*
 * File name: SchedulerCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年7月2日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 定时器调度接口
 * @author: 王胜
 * @version: 1.0, 2018年7月2日
 */

public interface SchedulerCrudService {
	/**
	 * 
	 * @Description:启动定时器
	 * @param map
	 * @return RpcResponse<Boolean>
	 */
	RpcResponse<Boolean> schedulerStart(JSONObject paramJson);



	/**
	 * 
	 * @Description:移除定时器
	 * @param jobId
	 * @return RpcResponse<Boolean>
	 */
	RpcResponse<Boolean> schedulerDelete(String jobId);
}
