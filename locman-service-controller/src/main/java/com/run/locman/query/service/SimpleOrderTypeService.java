/*
 * File name: SimpleOrderTypeService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月7日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.SimpleOrderProcessType;
import com.run.locman.api.query.service.SimpleOrderTypeQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 获取一般工单流程状态
 * @author:王胜
 * @version: 1.0, 2017年12月7日
 */
@Service
public class SimpleOrderTypeService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private SimpleOrderTypeQueryService	simpleOrderTypeQueryService;



	public Result<List<SimpleOrderProcessType>> findOrderTypeBySecret() {
		try {
			RpcResponse<List<SimpleOrderProcessType>> resultType = simpleOrderTypeQueryService
					.findOrderType();
			if (resultType.isSuccess()) {
				logger.info(String.format("[findOrderTypeBySecret()->success:%s]", resultType.getMessage()));
				return ResultBuilder.successResult(resultType.getSuccessValue(), resultType.getMessage());
			}
			logger.error(String.format("[findOrderTypeBySecret()->fail:%s]", resultType.getMessage()));
			return ResultBuilder.failResult(resultType.getMessage());
		} catch (Exception e) {
			logger.error("findOrderTypeBySecret()->Exception:",e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("rawtypes")
	public Result<List<Map>> findOrderState() {
		try {
			RpcResponse<List<Map>> resultType = simpleOrderTypeQueryService.findOrderState();
			if (resultType.isSuccess()) {
				logger.info(String.format("[findOrderState()->success:%s]", resultType.getMessage()));
				return ResultBuilder.successResult(resultType.getSuccessValue(), resultType.getMessage());
			}
			logger.error(String.format("[findOrderState()->fail:%s]", resultType.getMessage()));
			return ResultBuilder.failResult(resultType.getMessage());
		} catch (Exception e) {
			logger.error("findOrderState()->Exception:",e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
