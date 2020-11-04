/*
* File name: FacilitiesTypeBaseCudServiceImpl.java								
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
* 1.0			qulong		2017年8月31日
* ...			...			...
*
***************************************************/

package com.run.locman.service.crud;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FacilitiesTypeBaseCudRepository;
import com.run.locman.api.crud.service.FacilitiesTypeBaseCudService;
import com.run.locman.api.entity.FacilitiesTypeBase;
import com.run.locman.constants.CommonConstants;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Map;

/**
* @Description:	
* @author: qulong
* @version: 1.0, 2017年8月31日
*/
@Transactional(rollbackFor = Exception.class)
public class FacilitiesTypeBaseCudServiceImpl implements FacilitiesTypeBaseCudService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private FacilitiesTypeBaseCudRepository facilitiesTypeBaseCudRepository;
	
	@Override
	public RpcResponse<FacilitiesTypeBase> addFacilitiesTypeBase(FacilitiesTypeBase facilitiesTypeBase) {
		if (facilitiesTypeBase == null) {
			return RpcResponseBuilder.buildErrorRpcResp("基础设施类型对象不能为空");
		}
		try {
			logger.info(String.format("[addFacilitiesTypeBase()->进入方法,参数:%s]", facilitiesTypeBase));
			int insertModelResult = facilitiesTypeBaseCudRepository.insertModel(facilitiesTypeBase);
			if (insertModelResult > 0) {
				logger.debug("addFacilitiesTypeBase()->设施基础类型新增成功");
				return RpcResponseBuilder.buildSuccessRpcResp("新增成功", facilitiesTypeBase);
			}
			logger.error("addFacilitiesTypeBase()->设施基础类型新增失败");
			return RpcResponseBuilder.buildErrorRpcResp("设施基础类型新增失败");
		} catch (Exception e) {
			logger.error("addFacilitiesTypeBase()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<FacilitiesTypeBase> updateFacilitiesTypeBase(Map<String, String> map) {
		if (map == null) {
			return RpcResponseBuilder.buildErrorRpcResp("修改参数不能为空");
		}
		if (map.get(CommonConstants.ID) == null) {
			return RpcResponseBuilder.buildErrorRpcResp("基础设施类型对象唯一编号不能为空");
		}
		try {
			logger.info(String.format("[updateFacilitiesTypeBase()->进入方法,参数:%s]", map));
			int updatePartResult = facilitiesTypeBaseCudRepository.updatePart(map);
			if (updatePartResult > 0) {
				logger.debug("updateFacilitiesTypeBase()->设施基础类型更新成功");
				return RpcResponseBuilder.buildSuccessRpcResp("更新成功", null);
			}
			logger.error("updateFacilitiesTypeBase()->设施基础类型更新失败");
			return RpcResponseBuilder.buildErrorRpcResp("设施基础类型更新失败");
		} catch (Exception e) {
			logger.error("updateFacilitiesTypeBase()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
