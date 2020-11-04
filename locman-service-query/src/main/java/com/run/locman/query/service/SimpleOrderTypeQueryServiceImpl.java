/*
 * File name: SimpleOrderTypeQueryServiceImpl.java
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

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.SimpleOrderProcessType;
import com.run.locman.api.query.repository.SimpleOrderTypeQueryRepository;
import com.run.locman.api.query.service.SimpleOrderTypeQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description: 一般流程类型query实现类
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public class SimpleOrderTypeQueryServiceImpl implements SimpleOrderTypeQueryService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	SimpleOrderTypeQueryRepository	simpleOrderTypeRepository;



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderTypeQueryService#findOrderTypeBySecret(java.lang.String)
	 */
	@Override
	public RpcResponse<List<SimpleOrderProcessType>> findOrderType() {
		logger.info(String.format("[findOrderType()方法执行开始...]"));
		try {
			List<SimpleOrderProcessType> list = simpleOrderTypeRepository.findOrderTypeBySecret();
			logger.info(String.format("[findOrderType()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, list);
		} catch (Exception e) {
			logger.error("findOrderTypeBySecret()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderTypeQueryService#findOrderState(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> findOrderState() {
		logger.info(String.format("[findOrderState()方法执行开始...]"));
		try {
			List<Map> list = simpleOrderTypeRepository.findOrderState();
			logger.info(String.format("[findOrderState()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, list);
		} catch (Exception e) {
			logger.error("findOrderStateBySecret()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
