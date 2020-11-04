/*
* File name: FacilitiesRenovationQueryServiceImpl.java								
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
* 1.0			guofeilong		2019年12月9日
* ...			...			...
*
***************************************************/

package com.run.locman.query.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.FacilitiesRenovationQueryRepository;
import com.run.locman.api.query.service.FacilitiesRenovationQueryService;
import com.run.locman.constants.CommonConstants;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年12月9日
*/
@Repository
public class FacilitiesRenovationQueryServiceImpl implements FacilitiesRenovationQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private FacilitiesRenovationQueryRepository	facRenovationQueryRepository;
	
	@Value("${api.host}")
	private String						ip;

	/**
	 * @see com.run.locman.api.query.service.FacilitiesRenovationQueryService#isExistNotDealFac(java.lang.String)
	 */
	@Override
	public RpcResponse<Integer> isExistNotDealFac(String facilityId) {
		logger.info(String.format("[isExistNotDealFac()方法执行开始...,facilityId：%s]", facilityId));
		if (StringUtils.isEmpty(facilityId)) {
			logger.debug("[isExistNotDealFac()->invalid：设施ID不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("设施ID不能为空");
		}
		try {
			Integer num = facRenovationQueryRepository.isExistNotDealFac(facilityId);
			logger.info(String.format("[isExistNotDealFac()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", num);
		} catch (Exception e) {
			logger.error("isExistNotDealFac()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

	/**
	 * @see com.run.locman.api.query.service.FacilitiesRenovationQueryService#findInfoByFacId(java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> findInfoByFacId(String facilityId) {

		try {
			logger.info(String.format("[findInfoByFacId()方法执行开始...,facilityId：%s]", facilityId));
			JSONObject json = facRenovationQueryRepository.findInfoByFacId(facilityId);
			logger.info(String.format("[findInfoByFacId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", json);
		} catch (Exception e) {
			logger.error("findInfoByFacId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
