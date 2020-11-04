/*
* File name: FaultOrderProcessTypeRestQueryService.java								
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
* 1.0			田明		2017年12月07日
* ...			...			...
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
import com.run.locman.api.entity.FaultOrderProcessType;
import com.run.locman.api.query.service.FaultOrderProcessTypeQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 获取故障类型
 * @author: 田明
 * @version: 1.0, 2017年12月07日
 */
@Service
public class FaultOrderProcessTypeRestQueryService {

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FaultOrderProcessTypeQueryService	faultOrderProcessTypeQueryService;



	public Result<List<FaultOrderProcessType>> getFaultOrderTypeList() {
		try {
			RpcResponse<List<FaultOrderProcessType>> result = faultOrderProcessTypeQueryService
					.getFaultOrderType();
			if (result.isSuccess()) {
				logger.info(String.format("[getFaultOrderTypeList()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[getFaultOrderTypeList()->fail:%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("getFaultOrderTypeList()->Exception:",e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
