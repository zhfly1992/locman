/*
 * File name: FacilitiesDataTypeQueryRpcServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年11月2日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.constants.common.ResultMsgConstants;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.FacilitiesDataType;
import com.run.locman.api.query.repository.FacilitiesDataTypeQueryRpcRepository;
import com.run.locman.api.query.service.FacilitiesDataTypeQueryRpcService;
import com.run.locman.constants.CommonConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Description: 设施数据类型查询接口实现类
 * @author: lkc
 * @version: 1.0, 2017年11月2日
 */
public class FacilitiesDataTypeQueryRpcServiceImpl implements FacilitiesDataTypeQueryRpcService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	FacilitiesDataTypeQueryRpcRepository	facilitiesDataTypeQueryRpcRepository;



	@Override
	public RpcResponse<List<FacilitiesDataType>> getAllFacilitiesDataType(Map<String, String> map) throws Exception {
		logger.info(String.format("[getAllFacilitiesDataType()方法执行开始...,参数：【%s】]", map));
		try {
			List<FacilitiesDataType> facilitiesDataTypeList = facilitiesDataTypeQueryRpcRepository
					.findAllFacilitiesDataType(map);

			logger.info(String.format("[getAllFacilitiesDataType()->success:%s]", facilitiesDataTypeList));
			return RpcResponseBuilder.buildSuccessRpcResp(ResultMsgConstants.INFO_SEARCH_SUCCESS,
					facilitiesDataTypeList);
		} catch (Exception e) {
			logger.error("getAllFacilitiesDataType()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<PageInfo<FacilitiesDataType>> getFacilitiesDataTypeList(Integer pageNo, Integer pageSize,
			Map<String, String> map) throws Exception {
		logger.info(String.format("[getFacilitiesDataTypeList()方法执行开始...,参数：【%s】【%s】【%s】]", pageNo, pageSize, map));
		try {
			// 验证参数
			if (pageNo == null) {
				logger.error("[getFacilitiesDataTypeList()-fail:页面页数不能为空!");
				return RpcResponseBuilder.buildErrorRpcResp("页面页数不能为空!");
			}
			if (pageSize == null) {
				logger.error("[getFacilitiesDataTypeList()-fail:页面大小不能为空!");
				return RpcResponseBuilder.buildErrorRpcResp("页面大小不能为空!");
			}

			PageHelper.startPage(pageNo, pageSize);
			List<FacilitiesDataType> facilitiesDataTypeList = facilitiesDataTypeQueryRpcRepository
					.findFacilitiesDataTypeList(map);

			PageInfo<FacilitiesDataType> facilitiesDataTypePage = new PageInfo<>(facilitiesDataTypeList);

			logger.info(String.format("[getFacilitiesDataTypeList()->success:%s]", facilitiesDataTypeList));
			return RpcResponseBuilder.buildSuccessRpcResp(ResultMsgConstants.INFO_SEARCH_SUCCESS,
					facilitiesDataTypePage);
		} catch (Exception e) {
			logger.error("getFacilitiesDataTypeList()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<FacilitiesDataType> getById(String id) throws Exception {
		logger.info(String.format("[getById()方法执行开始...,参数：【%s】]", id));
		if (StringUtils.isEmpty(id)) {
			logger.debug("[getById()->invalid：设施扩展属性ID不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("设施扩展属性信息：设施扩展属性ID不能为空");
		}
		try {
			FacilitiesDataType facilitiesDataType = facilitiesDataTypeQueryRpcRepository.findById(id);
			logger.info(String.format("[getById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("信息查询成功", facilitiesDataType);
		} catch (Exception e) {
			logger.error("getById()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Integer> validFacilitiesDataTypeName(Map<String, String> map) {
		logger.info(String.format("[validFacilitiesDataTypeName()方法执行开始...,参数：【%s】]", map));
		try {
			int count = facilitiesDataTypeQueryRpcRepository.validFacilitiesDataTypeName(map);
			if (count == 0) {
				logger.info(String.format("[validFacilitiesDataTypeName()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(ResultMsgConstants.INFO_SEARCH_SUCCESS, count);
			} else {
				logger.debug(String.format("[queryIsRepeat()->invalid:%s]", count));
				return RpcResponseBuilder.buildSuccessRpcResp(ResultMsgConstants.INFO_SEARCH_SUCCESS, count);
			}
		} catch (Exception e) {
			logger.error("validFacilitiesDataTypeName()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
