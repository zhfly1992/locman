/*
 * File name: SimpleOrderTypeQueryService.java
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

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.SimpleOrderProcessType;

/**
 * @Description:
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public interface SimpleOrderTypeQueryService {
	/**
	 * 
	 * @Description:
	 * @param
	 * @return RpcResponse<List<SimpleOrderProcessType>>
	 */
	RpcResponse<List<SimpleOrderProcessType>> findOrderType();



	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> findOrderState();
}
