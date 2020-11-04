/*
 * File name: StaffTypeRestQueryService.java
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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.StaffType;
import com.run.locman.api.query.service.StaffTypeQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 人员类型管理查询
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */

@Service
public class StaffTypeRestQueryService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private StaffTypeQueryService	staffTypeQueryService;



	public Result<List<StaffType>> getByAccessSecret() {
		try {
			RpcResponse<List<StaffType>> staffTypeList = staffTypeQueryService.findAllByAccessSecret();
			if (!staffTypeList.isSuccess()) {
				logger.error(String.format("[getByAccessSecret()->fail:%s]", staffTypeList.getMessage()));
				return ResultBuilder.failResult(staffTypeList.getMessage());
			}
			logger.info(String.format("[getByAccessSecret()->success:%s]", staffTypeList.getMessage()));
			return ResultBuilder.successResult(staffTypeList.getSuccessValue(), staffTypeList.getMessage());
		} catch (Exception e) {
			logger.error("getByAccessSecret()->Exception:",e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<StaffType> getById(String id) {
		logger.info(String.format("[getById()->request params--id:%s]", id));
		if (id == null || "".equals(id)) {
			logger.error("[getById()->error:id为null或为空]");
			return ResultBuilder.noBusinessResult();
		}
		try {
			RpcResponse<StaffType> findByIdResult = staffTypeQueryService.findById(id);
			if (!findByIdResult.isSuccess()) {
				logger.error(String.format("[getById()->fail:%s]", findByIdResult.getMessage()));
				return ResultBuilder.failResult(findByIdResult.getMessage());
			}
			logger.info(String.format("[getById()->success:%s]", findByIdResult.getMessage()));
			return ResultBuilder.successResult(findByIdResult.getSuccessValue(), findByIdResult.getMessage());
		} catch (Exception e) {
			logger.error("getByAccessSecret()->Exception:",e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
