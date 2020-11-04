/*
 * File name: FocusSecurityCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 钟滨远 2020年4月26日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description:
 * @author: 钟滨远
 * @version: 1.0, 2020年4月26日
 */

public interface FocusSecurityCrudService {
	/**
	 * 
	* @Description:新增保障项目
	* @param json
	* @return
	 */
	public RpcResponse<String> addFocusSecurity(JSONObject json);
	

	public RpcResponse<String> enabledFocusSecurity(JSONObject json); 
	
	
	/**
	 * 
	* @Description:
	* @param facId
	* @param command 命令数据点和对应的值 eg:{"status":1,"temperature":26}
	* @return
	 */
	public RpcResponse<String> operateLock(String facId,JSONObject command,String securityId);
	
	/**
	 * 
	* @Description:重保时间结束，更改工单状态
	* @return
	 */
	public RpcResponse<Integer> querySecurityFacilitiesOrders();
}
