/*
 * File name: FaultOrderProcessTypeQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tm 2017年12月07日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.FaultOrderProcessType;
import com.run.locman.api.query.repository.FaultOrderProcessTypeQueryRepository;
import com.run.locman.api.query.service.FaultOrderProcessTypeQueryService;
import com.run.locman.constants.CommonConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description: 故障工单类型query实现类
 * @author: 田明
 * @version: 1.0, 2017年12月07日
 */
public class FaultOrderProcessTypeQueryServiceImpl implements FaultOrderProcessTypeQueryService {

	private static final Logger						logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FaultOrderProcessTypeQueryRepository	faultOrderProcessTypeQueryRepository;



	@Override
	public RpcResponse<List<FaultOrderProcessType>> getFaultOrderType() {
		logger.info(String.format("[getFaultOrderType()方法执行开始...]"));
		try {
			List<FaultOrderProcessType> list = faultOrderProcessTypeQueryRepository.getFaultOrderTypeList();
			logger.info(String.format("[getFaultOrderType()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("获取故障工单类型成功", list);
		} catch (Exception e) {
			logger.error("getFaultOrderTypeList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<FaultOrderProcessType> findById(String id) {
		logger.info(String.format("[findById()方法执行开始...,参数：【%s】]", id));
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[findById()->invalid: id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("id不能为空");
			}
			FaultOrderProcessType faultOrderProcessType = faultOrderProcessTypeQueryRepository.findById(id);
			logger.info(String.format("[findById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("通过id获取故障工单类型成功", faultOrderProcessType);
		} catch (Exception e) {
			logger.error("findById()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
