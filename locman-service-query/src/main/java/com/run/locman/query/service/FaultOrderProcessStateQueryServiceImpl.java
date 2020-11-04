/*
 * File name: FaultOrderProcessStateQueryServiceImpl.java
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
import com.run.locman.api.entity.FaultOrderProcessState;
import com.run.locman.api.query.repository.FaultOrderProcessStateQueryRepository;
import com.run.locman.api.query.service.FaultOrderProcessStateQueryService;
import com.run.locman.constants.CommonConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description: 故障工单流程状态query实现类
 * @author: 田明
 * @version: 1.0, 2017年12月07日
 */
public class FaultOrderProcessStateQueryServiceImpl implements FaultOrderProcessStateQueryService {

	private static final Logger						logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FaultOrderProcessStateQueryRepository	faultOrderProcessStateQueryRepository;



	@Override
	public RpcResponse<List<FaultOrderProcessState>> getFaultOrderStateList() {
		logger.info(String.format("[getFaultOrderStateList()方法执行开始...]"));
		try {
			List<FaultOrderProcessState> list = faultOrderProcessStateQueryRepository.getFaultOrderStateList();
			logger.info(String.format("[getFaultOrderStateList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("获取故障工单流程状态成功", list);
		} catch (Exception e) {
			logger.error("getFaultOrderStateList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<FaultOrderProcessState> findBySign(String sign) {
		logger.info(String.format("[findBySign()方法执行开始...,参数：【%s】]", sign));
		try {
			if (StringUtils.isBlank(sign)) {
				logger.error("[findBySign()->invalid: sign不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("sign不能为空");
			}
			FaultOrderProcessState faultOrderProcessState = faultOrderProcessStateQueryRepository.findBySign(sign);
			logger.info(String.format("[findBySign()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("通过sign获取故障工单流程状态成功", faultOrderProcessState);
		} catch (Exception e) {
			logger.error("findBySign()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
