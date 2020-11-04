/*
 * File name: FacilitiesTimeoutReportConfigQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年6月27日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.FacilitiesTimeoutReportConfigQueryRepository;
import com.run.locman.api.query.service.FacilitiesTimeoutReportConfigQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年6月27日
 */

public class FacilitiesTimeoutReportConfigQueryServiceImpl implements FacilitiesTimeoutReportConfigQueryService {

	private static final Logger						logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	FacilitiesTimeoutReportConfigQueryRepository	facilitiesTimeoutReportConfigQueryRepository;



	/**
	 * @see com.run.locman.api.query.service.FacilitiesTimeoutReportConfigQueryService#queryFacilityTimeoutReportConfigByCId(java.lang.String)
	 */
	@Override
	public RpcResponse<List<String>> queryFacilityTimeoutReportConfigByCId(String id) {
		logger.info(String.format("[queryFacilityTimeoutReportConfigByCId()方法执行开始...,参数：【%s】]", id));
		if (StringUtils.isBlank(id)) {
			logger.error("id不能为空!!!");
			return RpcResponseBuilder.buildErrorRpcResp("id不能为空!!!");
		}
		try {
			List<String> facilityTimeoutReportConfigList = facilitiesTimeoutReportConfigQueryRepository
					.queryFacilityTimeoutReportConfigByCId(id);
			if (null == facilityTimeoutReportConfigList || facilityTimeoutReportConfigList.isEmpty()) {
				logger.info(MessageConstant.SEARCH_FAIL);
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
			} else {
				logger.info(MessageConstant.SEARCH_SUCCESS);
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS,
						facilityTimeoutReportConfigList);
			}
		} catch (Exception e) {
			logger.error("queryFacilityTimeoutReportConfigByCId()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
