/*
 * File name: OrderProcessCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tianming 2018年02月02日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 工单流程cud
 * @author: 田明
 * @version: 1.0, 2018年02月02日
 */

public interface OrderProcessCudService {
	/**
	 *
	 * @Description:创建工单流程
	 * @param orderProcessInfo
	 * @return
	 */
	RpcResponse<String> addOrderProcess(JSONObject orderProcessInfo) throws Exception;



	/**
	 * @Description: 启用/停用工单流程
	 * @param processId
	 *            : 流程ID ,manageState : 管理状态(启用enabled/停用disabled) ,updateBy :
	 *            修改人
	 * @param remarks 
	 * @return
	 */
	RpcResponse<String> updateState(String processId, String manageState, String updateBy, String fileId);



	/**
	 *
	 * @Description:编辑工单流程
	 * @param orderProcessInfo
	 * @return
	 */
	RpcResponse<String> updateOrderProcess(JSONObject orderProcessInfo) throws Exception;

}
