/*
 * File name: DroolsQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年10月16日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.DroolsRepository;
import com.run.locman.api.query.service.DroolsQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description: 规则引擎查询类
 * @author: zhabing
 * @version: 1.0, 2017年10月16日
 */

public class DroolsQueryServiceImpl implements DroolsQueryService {
	private static final Logger	logger	= Logger.getLogger(DroolsQueryService.class);

	@Autowired
	private DroolsRepository	droolsRespository;



	/**
	 * @see com.run.locman.api.query.service.DroolsQueryService#getAllDrollsByPage(java.lang.Object,
	 *      java.lang.Object, java.util.Map)
	 */
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getAllDrollsByPage(int pageNum, int pageSize,
			Map<String, Object> searchParam) {
		logger.info(String.format("[getAllDrollsByPage()方法执行开始...,参数：【%s】【%s】【%s】]", pageNum, pageSize, searchParam));
		try {
			// 参数校验
			if (StringUtils.isEmpty(pageNum)) {
				logger.error(String.format("[getAllDrollsByPage()->error:%s-->%s]", MessageConstant.NO_BUSINESS,
						CommonConstants.PAGE_NUM));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
			}

			// 分页大小
			if (StringUtils.isEmpty(pageSize)) {
				logger.error(String.format("[getAllDrollsByPage()->error:%s-->%s]", MessageConstant.NO_BUSINESS,
						CommonConstants.PAGE_SIZE));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
			}

			PageHelper.startPage(pageNum, pageSize);
			List<Map<String, Object>> page = droolsRespository.getAllDroolsByPage(searchParam);
			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			// 封装分页后信息
			logger.info(String.format("[getAllDrollsByPage()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getAllDrollsByPage()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.DroolsQueryService#getDroolInfoById(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> getDroolInfoById(String id) {
		logger.info(String.format("[getDroolInfoById()方法执行开始...,参数：【%s】]", id));
		try {
			// 参数校验
			if (StringUtils.isEmpty(id)) {
				logger.error(String.format("[getAllDrollsByPage()->error:%s-->%s]", MessageConstant.NO_BUSINESS,
						CommonConstants.ID));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
			}
			Map<String, Object> info = droolsRespository.getDroolInfoById(id);
			logger.info(String.format("[getDroolInfoById()方法执行结束!返回信息：【%s】]", info));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getDroolInfoById()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
