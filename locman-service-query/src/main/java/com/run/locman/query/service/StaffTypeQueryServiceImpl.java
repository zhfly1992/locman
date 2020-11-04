/*
 * File name: StaffTypeQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月14日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.service;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.StaffType;
import com.run.locman.api.query.repository.StaffTypeQueryRepository;
import com.run.locman.api.query.service.StaffTypeQueryService;
import com.run.locman.constants.CommonConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description: 人员类型query实现类
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */

public class StaffTypeQueryServiceImpl implements StaffTypeQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private StaffTypeQueryRepository	staffTypeQueryRepository;



	@Override
	public RpcResponse<List<StaffType>> findAllByAccessSecret() {
		logger.info(String.format("[findAllByAccessSecret()方法执行开始...]"));
		try {
			List<StaffType> staffTypeList = staffTypeQueryRepository.findAllAllStaffType();
			if (staffTypeList == null) {
				logger.warn("[findAllByAccessSecret()->invalid：查询失败，数据库异常]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败，数据库异常");
			}
			if (CollectionUtils.isEmpty(staffTypeList)) {
				logger.info(String.format("[findAllByAccessSecret()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("暂无数据", staffTypeList);
			}
			logger.info(String.format("[findAllByAccessSecret()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", staffTypeList);
		} catch (Exception e) {
			logger.error("findAllByAccessSecret()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<StaffType> findById(String id) {
		logger.info(String.format("[findById()方法执行开始...,参数：【%s】]", id));
		if (id == null) {
			return RpcResponseBuilder.buildErrorRpcResp("主键ID不能为空");
		}
		try {
			StaffType staffType = staffTypeQueryRepository.findById(id);
			if (staffType == null) {
				logger.error("[findById()->failed: 查询失败]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			logger.info(String.format("[findById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", staffType);
		} catch (Exception e) {
			logger.error("findById()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}

}
