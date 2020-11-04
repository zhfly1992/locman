/*
 * File name: SimpleOrderCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月6日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.model.SimpleOrderProcessModel;

/**
 * @Description: 一般工单流程Crud接口
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public interface SimpleOrderCrudService {
	/**
	 * 
	 * @Description:创建或编辑一般流程工单
	 * @param orderInfo
	 * @return
	 */
	RpcResponse<String> simpleOrderAdd(JSONObject orderInfo) throws Exception;



	/**
	 * 
	 * @Description:修改一般流程工单状态
	 * @param orderInfo
	 * @return RpcResponse<String>
	 * @throws Exception
	 */
	RpcResponse<String> updateSimpleOrderState(JSONObject orderInfo) throws Exception;



	/**
	 * 
	 * @Description:延时审批接口
	 * @param simpleOrderProcessModel
	 * @return
	 */
	RpcResponse<String> delayedSimpleOrder(SimpleOrderProcessModel simpleOrderProcessModel);
	/**
	 * 
	* @Description:工单作废接口
	* @param simpleOrderProcessModel
	* @return
	 */
	RpcResponse<String> invalidateSimpleOrder(SimpleOrderProcessModel simpleOrderProcessModel);
}
