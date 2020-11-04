package com.run.locman.query.service;

import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.ProcessTypeBase;
import com.run.locman.api.query.service.ProcessTypeBaseQueryService;
import com.run.locman.constants.CommonConstants;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2018年02月02日
 */
@Service
public class ProcessTypeBaseRestQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private ProcessTypeBaseQueryService	processTypeBaseQueryService;



	public Result<List<ProcessTypeBase>> queryOrderProcessType() {
		try {
			RpcResponse<List<ProcessTypeBase>> rpcResponse = processTypeBaseQueryService.queryOrderProcessType();
			if (rpcResponse.isSuccess()) {
				logger.info(rpcResponse.getMessage());
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			} else {
				logger.info(rpcResponse.getMessage());
				return ResultBuilder.failResult(rpcResponse.getMessage());
			}
		} catch (Exception e) {
			logger.error("queryOrderProcessType()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
