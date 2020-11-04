/*
* File name: OrderProcesQueryServiceTimer.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			guofeilong		2018年7月4日
* ...			...			...
*
***************************************************/

package com.run.locman.api.timer.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年7月4日
*/

public interface OrderProcessQueryService {

	/**
	 * 
	 * @Description:通过接入方和流程类型查询流程人员配置信息
	 * @param accessSecret 接入方密钥集合 ；processType 流程类型
	 * 
	 */
	RpcResponse<List<Map<String, Object>>> findPersonByAccessSecret(String accessSecret,String processType);
	
	/**
	 * @Description: 根据组织id查询用户节点信息
	 * @param queryMap
	 *            查询参数
	 * @return 用户节点信息集合
	 */
	RpcResponse<JSONArray> queryNodeInfoForActivity(Map<String, Object> queryMap);

}
