/*
 * File name: DeviceSchedulerService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年7月11日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 定时器接口
 * @author: 王胜
 * @version: 1.0, 2018年7月11日
 */

public interface DeviceSchedulerService {
	/**
	 * 
	 * @Description:开启命令，定时器启动接口
	 * @param schedulerParam
	 *            {"deviceId":"","key":"下发命令的key","keyValue":"下发命令的key值"}
	 * @return RpcResponse<String>
	 */
	RpcResponse<String> deviceSchedulerStart(JSONObject schedulerParam);
}
