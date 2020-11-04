/*
 * File name: ProcessTypeBaseQueryServiceImpl.java
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

package com.run.locman.query.service;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.ProcessTypeBase;
import com.run.locman.api.query.repository.ProcessTypeBaseQueryRepository;
import com.run.locman.api.query.service.ProcessTypeBaseQueryService;
import com.run.locman.constants.CommonConstants;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description: 基础工单流程类型实现类
 * @author: 田明
 * @version: 1.0, 2018年02月02日
 */
public class ProcessTypeBaseQueryServiceImpl implements ProcessTypeBaseQueryService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	ProcessTypeBaseQueryRepository	processTypeBaseQueryRepository;



	@Override
	public RpcResponse<List<ProcessTypeBase>> queryOrderProcessType() {
		logger.info(String.format("[queryOrderProcessType()方法执行开始...]"));
		try {
			List<ProcessTypeBase> typeBaseList = processTypeBaseQueryRepository.queryOrderProcessType();
			logger.info("查询基础工单流程类型成功");
			logger.info(String.format("[getAllDrollsByPage()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询基础工单流程类型成功", typeBaseList);
		} catch (Exception e) {
			logger.error("queryOrderProcessType()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
