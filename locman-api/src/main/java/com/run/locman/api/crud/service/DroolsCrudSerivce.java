/*
 * File name: DroolsCrudSerivce.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年10月16日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

import java.util.Map;

/**
 * @Description: 规则引擎crud接口类
 * @author: zhabing
 * @version: 1.0, 2017年10月16日
 */

public interface DroolsCrudSerivce {

	/***
	 * 
	 * @Description:添加规则迎请
	 * @param o
	 */

	@SuppressWarnings("rawtypes")
	public RpcResponse<Map<String, Object>> addDrools(Map map);



	/***
	 * 
	 * @Description:修改规则迎请
	 * @param map
	 */
	@SuppressWarnings("rawtypes")
	public RpcResponse<Map<String, Object>> updateDrools(Map map);



	/***
	 * 
	 * @Description:修改规则迎请启用或者禁用状态
	 * @param map
	 */
	public RpcResponse<JSONObject> swateDroolsState(String id, String state);
}
