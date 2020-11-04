/*
* File name: OrderProcessRestQueryService.java								
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
* 1.0			guofeilong		2018年2月1日
* ...			...			...
*
***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.common.util.ParamChecker;
import com.run.common.util.StringUtil;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.dto.ProcessInfoListDto;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.OrderProcessContants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年2月1日
 */
@Service
public class OrderProcessRestQueryService {
	private static final Logger logger = Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private OrderProcessQueryService orderProcessQueryService;

	/**
	 * <method description>
	 *
	 * @param queryInfo
	 *            {
	 *            "pageSize":"页大小","pageNum":"页码","accessSecret":"接入方密钥","processType":"工作流程类型"
	 *            }
	 * @return PageInfo<ProcessInfoListDto>
	 */

	public Result<PageInfo<ProcessInfoListDto>> getOrderProcesssList(String queryInfo) {
		logger.info(String.format("[getOrderProcesssList()->request params:%s]", queryInfo));
		// 参数校验
		if (ParamChecker.isBlank(queryInfo) || ParamChecker.isNotMatchJson(queryInfo)) {
			logger.error(LogMessageContants.PARAMETERS_OF_ILLEGAL);
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject json = JSON.parseObject(queryInfo);
			String accessSecret = json.getString(OrderProcessContants.USC_ACCESS_SECRET);
			if (StringUtils.isBlank(accessSecret)) {
				logger.error(LogMessageContants.NO_PARAMETER_EXISTS);
				return ResultBuilder.emptyResult();
			}
			// 校验是否为纯数字
			if (!StringUtil.isDigital(json.getString(OrderProcessContants.PAGE_SIZE))
					|| !StringUtil.isDigital(json.getString(OrderProcessContants.PAGE_NUM))) {
				logger.error(LogMessageContants.PARAMETERS_OF_ILLEGAL);
				return ResultBuilder.invalidResult();
			}

			int pageSize = json.getIntValue(OrderProcessContants.PAGE_SIZE);
			int pageNum = json.getIntValue(OrderProcessContants.PAGE_NUM);

			String processType = json.getString(OrderProcessContants.PROCESS_TYPE);
			// 查询信息
			RpcResponse<PageInfo<ProcessInfoListDto>> result = orderProcessQueryService.getOrderProcessList(pageNum,
					pageSize, accessSecret, processType);
			if (result.isSuccess()) {
				logger.info(LogMessageContants.QUERY_SUCCESS);
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			} else {
				logger.error(LogMessageContants.QUERY_FAIL);
				return ResultBuilder.failResult(result.getMessage());
			}
		} catch (Exception e) {
			logger.error("getOrderProcesssList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	/**
	 * <method description> 通过id查询工单流程
	 *
	 * @param id
	 *            : 工单流程id
	 * @return PageInfo<ProcessInfoListDto>
	 */
	public Result<Map<String, Object>> queryOrderProcessById(String id) {
		logger.info(String.format("[queryOrderProcessById()->request params--id:%s]", id));
		try {
			RpcResponse<Map<String, Object>> rpcResponse = orderProcessQueryService.queryOrderProcessById(id);
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[queryOrderProcessById()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[queryOrderProcessById()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("queryOrderProcessById()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	/**
	 * 
	 * @Description:判断用户是否绑定在流程图上
	 * @param userId
	 * 
	 */
	public Result<List<String>> existUserInProcess(String userId) {
		logger.info(String.format("[existUserInProcess()->request params--userId:%s]", userId));
		try {
			RpcResponse<List<String>> existUserInProcess = orderProcessQueryService.existUserInProcess(userId);
			if (existUserInProcess.isSuccess()) {
				logger.info(String.format("[existUserInProcess()->success:%s]", existUserInProcess.getMessage()));
				return ResultBuilder.successResult(existUserInProcess.getSuccessValue(),
						existUserInProcess.getMessage());
			}
			logger.error(String.format("[existUserInProcess()->fail:%s]", existUserInProcess.getMessage()));
			return ResultBuilder.failResult(existUserInProcess.getMessage());
		} catch (Exception e) {
			logger.error("existUserInProcess()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
