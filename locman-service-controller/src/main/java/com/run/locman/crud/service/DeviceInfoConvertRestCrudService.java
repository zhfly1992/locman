/*
* File name: DeviceInfoConvertRestService.java								
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

package com.run.locman.crud.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.DeviceInfoConvertCrudService;
import com.run.locman.api.model.DeviceInfoConvertModel;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description:设备信息转换rest
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月28日
 */
@Service
public class DeviceInfoConvertRestCrudService {

	private Logger logger = Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceInfoConvertCrudService deviceInfoConvertCrudService;
	public Result<String> convertInfoAdd(DeviceInfoConvertModel convertInfo) {
		try {
			
			// 参数校验
			if (convertInfo == null) {
				logger.error(String.format("[convertInfoAdd()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
				return ResultBuilder.invalidResult();
			}
			logger.info(String.format("[convertInfoAdd()->request params--accessSecret:%s,dicKey:%s,dicValue:%s]",convertInfo.getAccessSecret(),convertInfo.getDicKey(),convertInfo.getDicValue()));
			RpcResponse<String> addConvertInfo = deviceInfoConvertCrudService.addConvertInfo(addAttribute(convertInfo));

			if (addConvertInfo.isSuccess()) {
				logger.info(String.format("[convertInfoAdd()->success:%s->%s]", PublicConstants.PARAM_SUCCESS,
						addConvertInfo.getSuccessValue()));
				return ResultBuilder.successResult(addConvertInfo.getSuccessValue(), addConvertInfo.getMessage());
			} else {
				logger.error(String.format("[convertInfoAdd()->error:%s->%s]", PublicConstants.PARAM_ERROR,
						addConvertInfo.getMessage()));
				return ResultBuilder.failResult(addConvertInfo.getMessage());
			}

		} catch (Exception e) {
			logger.error("convertInfoAdd()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	public Result<String> convertInfoUpdate(DeviceInfoConvertModel convertInfo) {
		try {
			// 参数校验
			if (convertInfo == null) {
				logger.error(String.format("[convertInfoUpdate()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
				return ResultBuilder.invalidResult();
			}
			logger.info(String.format("[convertInfoUpdate()->request params--accessSecret:%s,dicKey:%s,dicValue:%s]",convertInfo.getAccessSecret(),convertInfo.getDicKey(),convertInfo.getDicValue()));
			RpcResponse<String> updateConvertInfo = deviceInfoConvertCrudService
					.updateConvertInfo(addAttribute(convertInfo));

			if (updateConvertInfo.isSuccess()) {
				logger.info(String.format("[convertInfoUpdate()->success:%s->%s]", PublicConstants.PARAM_SUCCESS,
						updateConvertInfo.getSuccessValue()));
				return ResultBuilder.successResult(updateConvertInfo.getSuccessValue(), updateConvertInfo.getMessage());
			} else {
				logger.error(String.format("[convertInfoUpdate()->error:%s->%s]", PublicConstants.PARAM_ERROR,
						updateConvertInfo.getMessage()));
				return ResultBuilder.failResult(updateConvertInfo.getMessage());
			}

		} catch (Exception e) {
			logger.error("convertInfoUpdate()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	public Result<String> convertInfoDelete(DeviceInfoConvertModel convertInfo) {
		try {

			// 参数校验
			if (convertInfo == null) {
				logger.error(String.format("[convertInfoDelete()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
				return ResultBuilder.invalidResult();
			}
			logger.info(String.format("[convertInfoDelete()->request params--accessSecret:%s,dicKey:%s]",convertInfo.getAccessSecret(),convertInfo.getDicKey()));
			// 調用rpc
			RpcResponse<String> deleteConvertInfo = deviceInfoConvertCrudService.deleteConvertInfo(convertInfo);

			if (deleteConvertInfo.isSuccess()) {
				logger.info(String.format("[convertInfoDelete()->success:%s->%s]", PublicConstants.PARAM_SUCCESS,
						deleteConvertInfo.getSuccessValue()));
				return ResultBuilder.successResult(deleteConvertInfo.getSuccessValue(), deleteConvertInfo.getMessage());
			} else {
				logger.error(String.format("[convertInfoDelete()->error:%s->%s]", PublicConstants.PARAM_ERROR,
						deleteConvertInfo.getMessage()));
				return ResultBuilder.failResult(deleteConvertInfo.getMessage());
			}

		} catch (Exception e) {
			logger.error("convertInfoDelete()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	private DeviceInfoConvertModel addAttribute(DeviceInfoConvertModel deviceInfoConvertModel) {

		// 判断是新增还是修改
		if (StringUtils.isBlank(deviceInfoConvertModel.getId())) {
			deviceInfoConvertModel.setId(UtilTool.getUuId());
			deviceInfoConvertModel.setCreateTime(DateUtils.formatDate(new Date()));
		}
		deviceInfoConvertModel.setUpdateTime(DateUtils.formatDate(new Date()));
		return deviceInfoConvertModel;
	}
	
	public Result<String> deviceSynchronization(JSONObject deviceInfo){
		logger.info(String.format("[deviceSynchronization()->同步电信Iot的设备信息：%s]", deviceInfo.toString()));
		try {
			String productId=deviceInfo.getString("productId");
			String accessSecret=deviceInfo.getString("accessSecret");
			if(StringUtils.isBlank(productId)) {
				logger.error("[deviceSynchronization()->productId不能为空！]");
				return ResultBuilder.failResult("产品Id不能为空！");
			}
			if(StringUtils.isBlank(accessSecret)) {
				logger.error("[deviceSynchronization()->accessSecret不能为空！]");
				return ResultBuilder.failResult("接入方密钥不能为空！");
			}
			
			RpcResponse<String> deviceSynchronization=deviceInfoConvertCrudService.deviceSynchronization(deviceInfo);
			if(deviceSynchronization.isSuccess()) {
				logger.info("[deviceSynchronization()->设备同步成功");
				return ResultBuilder.successResult(deviceSynchronization.getSuccessValue(), "设备同步成功");
			}else {
				logger.error("[deviceSynchronization()->设备同步失败");
				return ResultBuilder.failResult(deviceSynchronization.getMessage());
			}
		
			
		}catch(Exception e) {
			logger.error("deviceSynchronization()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
		
	}

}
