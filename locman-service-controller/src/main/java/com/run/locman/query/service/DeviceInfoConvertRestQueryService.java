/*
* File name: DeviceInfoConvertRestQueryService.java								
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
* 1.0			Administrator		2018年3月28日
* ...			...			...
*
***************************************************/

package com.run.locman.query.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.DeviceInfoConvert;
import com.run.locman.api.model.DeviceInfoConvertModel;
import com.run.locman.api.query.service.DeviceInfoConvertQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description: 转换信息查询rest
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月28日
 */
@Service
public class DeviceInfoConvertRestQueryService {

	private Logger logger = Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceInfoConvertQueryService deviceInfoConvertQueryService;

	public Result<PageInfo<DeviceInfoConvert>> convertInfoQueryAll(DeviceInfoConvertModel convertInfo) {
		try {
			// 参数校验
			if (convertInfo == null) {
				logger.error("[convertInfoQueryAll()->error:传入参数为空]");
				return ResultBuilder.invalidResult();
			}
            logger.info(String.format("[convertInfoQueryAll()->request params--dicKey:%s,dicVaule:%s]", convertInfo.getDicKey(),convertInfo.getDicKey()));
			RpcResponse<PageInfo<DeviceInfoConvert>> findConvertAll = deviceInfoConvertQueryService
					.findConvertAll(convertInfo);

			if (findConvertAll.isSuccess()) {
				logger.info(String.format("[convertInfoQueryAll()->success:%s->%s]", PublicConstants.PARAM_SUCCESS,
						findConvertAll.getSuccessValue()));
				return ResultBuilder.successResult(findConvertAll.getSuccessValue(), findConvertAll.getMessage());
			} else {
				logger.error(String.format("[convertInfoQueryAll()->error:%s->%s]", PublicConstants.PARAM_ERROR,
						findConvertAll.getMessage()));
				return ResultBuilder.failResult(findConvertAll.getMessage());
			}

		} catch (Exception e) {
			logger.error("convertInfoQueryAll()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	public Result<DeviceInfoConvert> convertInfoQueryById(DeviceInfoConvertModel convertInfo) {
		try {
			// 参数校验
			if (convertInfo == null) {
				logger.error("[convertInfoQueryById()->error:传入参数为空]");
				return ResultBuilder.invalidResult();
			}
            logger.info(String.format("[convertInfoQueryById()->request params--id:%s]", convertInfo.getId()));
			RpcResponse<DeviceInfoConvert> findConvertById = deviceInfoConvertQueryService.findConvertById(convertInfo);

			if (findConvertById.isSuccess()) {
				logger.info(String.format("[convertInfoQueryById()->success:%s->%s]", PublicConstants.PARAM_SUCCESS,
						findConvertById.getSuccessValue()));
				return ResultBuilder.successResult(findConvertById.getSuccessValue(), findConvertById.getMessage());
			} else {
				logger.error(String.format("[convertInfoQueryById()->error:%s->%s]", PublicConstants.PARAM_ERROR,
						findConvertById.getMessage()));
				return ResultBuilder.failResult(findConvertById.getMessage());
			}

		} catch (Exception e) {
			logger.error("convertInfoQueryById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	public Result<String> existConvertInfo(DeviceInfoConvertModel convertInfo) {
		try {
			// 参数校验
			if (convertInfo == null) {
				logger.error("[existConvertInfo()->error:传入参数为空]");
				return ResultBuilder.invalidResult();
			}
			logger.info(String.format("[existConvertInfo()->request params--dicKey:%s,dicVaule:%s]", convertInfo.getDicKey(),convertInfo.getDicKey()));
			RpcResponse<String> existConvertInfo = deviceInfoConvertQueryService.existConvertInfo(convertInfo);

			if (existConvertInfo.isSuccess()) {
				logger.info(String.format("[existConvertInfo()->success:%s->%s]", PublicConstants.PARAM_SUCCESS,
						existConvertInfo.getSuccessValue()));
				return ResultBuilder.successResult(existConvertInfo.getSuccessValue(), existConvertInfo.getMessage());
			} else {
				logger.error(String.format("[existConvertInfo()->error:%s->%s]", PublicConstants.PARAM_ERROR,
						existConvertInfo.getMessage()));
				return ResultBuilder.failResult(existConvertInfo.getMessage());
			}

		} catch (Exception e) {
			logger.error("existConvertInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
