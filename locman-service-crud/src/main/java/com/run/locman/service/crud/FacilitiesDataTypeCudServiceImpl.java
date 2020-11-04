/*
* File name: FacilitiesDataTypeCudServiceImpl.java
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
* 1.0			tm		2017年10月20日
* ...			...			...
*
***************************************************/

package com.run.locman.service.crud;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FacilitiesDataTypCudRepository;
import com.run.locman.api.crud.service.FacilitiesDataTypeCudService;
import com.run.locman.api.entity.FacilitiesDataType;
import com.run.locman.constants.CommonConstants;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @Description: 设施类型cud实现类
 * @author: 田明
 * @version: 1.0, 2017年08月31日
 */
@Transactional(rollbackFor = Exception.class)
public class FacilitiesDataTypeCudServiceImpl implements FacilitiesDataTypeCudService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesDataTypCudRepository	facilitiesDataTypCudRepository;



	@Override
	public RpcResponse<FacilitiesDataType> updateFacilitiesDataType(FacilitiesDataType facilitiesDataType) {
		if (facilitiesDataType == null) {
			logger.debug("[updateFacilitiesDataType()->invalid: 设施扩展对象不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设施扩展信息：设施扩展对象不能为空");
		}
		try {
			logger.info(String.format("[updateFacilitiesDataType()->进入方法,参数:%s]", facilitiesDataType));
			int updateResult = facilitiesDataTypCudRepository.updatePart(facilitiesDataType);
			if (updateResult > 0) {
				logger.info("[updateFacilitiesDataType()->success: " + facilitiesDataType + "]");
				return RpcResponseBuilder.buildSuccessRpcResp("设施扩展信息更新成功", facilitiesDataType);
			}
			logger.error("updateFacilitiesDataType()->设施扩展信息更新失败");
			return RpcResponseBuilder.buildErrorRpcResp("设施扩展信息更新失败");
		} catch (Exception e) {
			logger.error("updateFacilitiesDataType()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<FacilitiesDataType> addFacilitiesDataType(FacilitiesDataType facilitiesDataType) {
		if (facilitiesDataType == null) {
			logger.debug("[addFacilitiesDataType()->invalid: 设施扩展对象不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设施扩展信息：设施扩展对象不能为空");
		}
		try {
			logger.info(String.format("[addFacilitiesDataType()->进入方法,参数:%s]", facilitiesDataType));
			int insertResult = facilitiesDataTypCudRepository.insertModel(facilitiesDataType);
			if (insertResult > 0) {
				logger.debug("[addFacilitiesDataType()->success: " + facilitiesDataType + "]");
				return RpcResponseBuilder.buildSuccessRpcResp("设施扩展信息新增成功", facilitiesDataType);
			}
			logger.error("addFacilitiesDataType()->设施扩展信息更新失败");
			return RpcResponseBuilder.buildErrorRpcResp("设施扩展信息更新失败");
		} catch (Exception e) {
			logger.error("addFacilitiesDataType()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
