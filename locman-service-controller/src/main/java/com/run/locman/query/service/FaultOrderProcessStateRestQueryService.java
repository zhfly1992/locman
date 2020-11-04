/*
 * File name: FaultOrderProcessStateRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年12月07日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.FaultOrderProcessState;
import com.run.locman.api.query.service.FaultOrderProcessStateQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 故障流程查询
 * @author: 田明
 * @version: 1.0, 2017年12月07日
 */
@Service
public class FaultOrderProcessStateRestQueryService {

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FaultOrderProcessStateQueryService	faultOrderProcessStateQueryService;



	public Result<List<FaultOrderProcessState>> getFaultOrderStateList() {
		try {
			RpcResponse<List<FaultOrderProcessState>> result = faultOrderProcessStateQueryService
					.getFaultOrderStateList();
			if (result.isSuccess()) {
				logger.info(String.format("[getFaultOrderStateList()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[getFaultOrderStateList()->fail:%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("getFaultOrderStateList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
