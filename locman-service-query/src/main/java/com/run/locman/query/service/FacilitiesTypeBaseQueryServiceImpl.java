/*
 * File name: FacilitiesTypeBaseQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年8月31日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.FacilitiesTypeBase;
import com.run.locman.api.query.repository.FacilitiesTypeBaseQueryRepository;
import com.run.locman.api.query.service.FacilitiesTypeBaseQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年8月31日
 */

public class FacilitiesTypeBaseQueryServiceImpl implements FacilitiesTypeBaseQueryService {

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesTypeBaseQueryRepository	facilitiesTypeBaseQueryRepository;



	@Override
	public RpcResponse<FacilitiesTypeBase> getFacilitiesTypeBaseById(String id) {
		logger.info(String.format("[getFacilitiesTypeBaseById()方法执行开始...,参数：【%s】]", id));
		if (StringUtils.isEmpty(id)) {
			logger.debug("[getFacilitiesTypeBaseById()->invalid：基础设施类型ID不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("基础设施类型信息：基础设施类型ID不能为空");
		}
		try {
			FacilitiesTypeBase facilitiesTypeBase = facilitiesTypeBaseQueryRepository.findById(id);
			logger.info(String.format("[getFacilitiesTypeBaseById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", facilitiesTypeBase);
		} catch (Exception e) {
			logger.error("getFacilitiesTypeBaseById()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<PageInfo<FacilitiesTypeBase>> getFacilitiesTypeBasePage(int pageNum, int pageSize,
			Map<String, String> param) {
		logger.info(String.format("[getFacilitiesTypeBasePage()方法执行开始...,参数：【%s】【%s】【%s】]", pageNum, pageSize, param));

		PageHelper.startPage(pageNum, pageSize);
		try {
			List<FacilitiesTypeBase> queryFacilitiesTypeBaseListPageResult = facilitiesTypeBaseQueryRepository
					.queryFacilitiesTypeBaseListPage(param);

			if (queryFacilitiesTypeBaseListPageResult != null) {
				PageInfo<FacilitiesTypeBase> pageInfo = new PageInfo<>(queryFacilitiesTypeBaseListPageResult);
				logger.info(String.format("[getFacilitiesTypeBasePage()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", pageInfo);
			}
			logger.info(String.format("[getFacilitiesTypeBasePage()方法执行结束!]"));
			return RpcResponseBuilder.buildErrorRpcResp("查询失败");
		} catch (Exception e) {
			logger.error("getFacilitiesTypeBasePage()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Integer> validFacilitiesTypeName(String facilityTypeName) {
		logger.info(String.format("[validFacilitiesTypeName()方法执行开始...,参数：【%s】]", facilityTypeName));
		if (facilityTypeName == null || "".equals(facilityTypeName)) {
			return RpcResponseBuilder.buildErrorRpcResp("设施类型名称不能为空");
		}
		try {
			Integer validFacilitiesTypeNameResult = facilitiesTypeBaseQueryRepository
					.validFacilitiesTypeName(facilityTypeName);
			if (validFacilitiesTypeNameResult == 0) {
				logger.info(String.format("[validFacilitiesTypeName()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("验证成功", validFacilitiesTypeNameResult);
			}
			logger.info(String.format("[validFacilitiesTypeName()方法执行结束!]"));
			return RpcResponseBuilder.buildErrorRpcResp("基础设施类型名称重复");
		} catch (Exception e) {
			logger.error("validFacilitiesTypeName()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesTypeBaseQueryService#findFacilitiesTypeBase()
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> findAllFacilitiesTypeBase() {
		logger.info(String.format("[findAllFacilitiesTypeBase()方法执行开始...]"));
		try {
			List<Map<String, Object>> facilitiesTypeBaseList = facilitiesTypeBaseQueryRepository
					.findAllFacilitiesTypeBase();
			if (null == facilitiesTypeBaseList) {
				logger.error("[findAllFacilitiesTypeBase()->fail：查询基础设施类型集合失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询基础设施类型集合失败!");
			}
			logger.info("[findAllFacilitiesTypeBase()->fail：查询基础设施类型集合失败!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询基础设施类型集合成功!", facilitiesTypeBaseList);
		} catch (Exception e) {
			logger.error("findAllFacilitiesTypeBase()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesTypeBaseQueryService#updateFacilitiesTypeBaseCheck(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<Boolean> updateFacilitiesTypeBaseCheck(JSONObject jsonParam) {
		logger.info(String.format("[updateFacilitiesTypeBaseCheck()方法执行开始...,参数：【%s】]", jsonParam));
		try {
			if (null == jsonParam) {
				logger.error("[updateFacilitiesTypeBaseCheck()->invalid：参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空");
			}
			if (StringUtils.isBlank(jsonParam.getString(CommonConstants.ID))) {
				logger.error("[updateFacilitiesTypeBaseCheck()->invalid：参数id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数id不能为空");
			}
			if (StringUtils.isBlank(jsonParam.getString(FacilitiesContants.FAC_FACILITYTYPENAME))) {
				logger.error("[updateFacilitiesTypeBaseCheck()->invalid：参数facilityTypeName不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数facilityTypeName不能为空");
			}
			int num = facilitiesTypeBaseQueryRepository.updateFacilitiesTypeBaseCheck(jsonParam);
			if (num > 0) {
				logger.info(String.format("[updateFacilitiesTypeBaseCheck()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("该设施类型名称重复", false);
			}
			logger.info(String.format("[updateFacilitiesTypeBaseCheck()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("该appTag可以使用", true);
		} catch (Exception e) {
			logger.error("findAllFacilitiesTypeBase()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
