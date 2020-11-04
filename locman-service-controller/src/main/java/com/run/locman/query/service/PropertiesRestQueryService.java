/*
* File name: PropertiesRestQueryService.java								
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
* 1.0			qulong		2017年11月20日
* ...			...			...
*
***************************************************/
package com.run.locman.query.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.query.service.PropertiesQueryService;
import com.run.locman.constants.CommonConstants;
/**
 * @Description: 分页查询设备属性
 * @author: qulong
 * @version: 1.0, 2017年11月20日
 */
@Service
public class PropertiesRestQueryService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private PropertiesQueryService	propertiesQueryService;



	public Result<PageInfo<DeviceProperties>> list(String templateId, String queryParams) {
		logger.info(String.format("[list()->request params--templateId:%s,queryParams:%s]", templateId,queryParams));
		if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsObject = JSON.parseObject(queryParams);
			if (!paramsObject.containsKey(CommonConstants.PAGE_NUM)) {
				logger.error("[list()->error:参数必须包含pageNum]");
				return ResultBuilder.emptyResult();
			}
			if (!paramsObject.containsKey(CommonConstants.PAGE_SIZE)) {
				logger.error("[list()->error:参数必须包含pageSize]");
				return ResultBuilder.emptyResult();
			}
			if (!StringUtils.isNumeric(paramsObject.getString(CommonConstants.PAGE_NUM))
					|| !StringUtils.isNumeric(paramsObject.getString(CommonConstants.PAGE_SIZE))) {
				logger.error("[list()->error:pageSize和pageNum必须为数字]");
				return ResultBuilder.invalidResult();
			}
			int pageNum = paramsObject.getIntValue(CommonConstants.PAGE_NUM);
			int pageSize = paramsObject.getIntValue(CommonConstants.PAGE_SIZE);
			RpcResponse<PageInfo<DeviceProperties>> rpcResponse = propertiesQueryService.list(templateId, pageNum,
					pageSize);
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[list()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[list()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("list()->Exception:",e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	public Result<Boolean> checkNameOrSigExist(String queryParams) {
		logger.info(String.format("[list()->request params--queryParams:%s]",queryParams));
		if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsObject = JSON.parseObject(queryParams);
			String templateId = paramsObject.getString("templateId");
			if (StringUtils.isBlank(templateId)) {
				logger.error("checkNameAndSigExist()->invalid:模板id不能为空!!!");
				return ResultBuilder.invalidResult();
			}
			String devicePropertiesSign = paramsObject.getString("devicePropertiesSign");
			String devicePropertiesName = paramsObject.getString("devicePropertiesName");
			boolean nameBlank = StringUtils.isBlank(devicePropertiesName);
			boolean signBlank = StringUtils.isBlank(devicePropertiesSign);
			if (nameBlank && signBlank) {
				logger.error("checkNameOrSigExist()->invalid:显示名和标识名不能同时为空!!!");	
				return ResultBuilder.invalidResult();
			}
			if (!nameBlank && !signBlank) {
				logger.error("checkNameOrSigExist()->invalid:显示名和标识名不能同时校验!!!");	
				return ResultBuilder.invalidResult();
			}
			String id = paramsObject.getString("id");
			DeviceProperties deviceProperties = new DeviceProperties();
			if (!signBlank) {
				deviceProperties.setDevicePropertiesSign(devicePropertiesSign);
			}
			if (!nameBlank) {
				deviceProperties.setDevicePropertiesName(devicePropertiesName);
			}
			//id存在,重名校验排除自身
			if (!StringUtils.isBlank(id)) {
				deviceProperties.setId(id);
			}
			deviceProperties.setTemplateId(templateId);
			RpcResponse<Boolean> checkNameOrSigExist = propertiesQueryService.checkNameOrSigExist(deviceProperties);
			if (checkNameOrSigExist.isSuccess()) {
				logger.info(String.format("[checkNameOrSigExist()->success:%s]", checkNameOrSigExist.getMessage()));
				return ResultBuilder.successResult(checkNameOrSigExist.getSuccessValue(), checkNameOrSigExist.getMessage());
			}
			logger.error(String.format("[checkNameOrSigExist()->fail:%s]", checkNameOrSigExist.getMessage()));
			return ResultBuilder.failResult(checkNameOrSigExist.getMessage());
		} catch (Exception e) {
			logger.error("checkNameOrSigExist()->Exception:",e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	

}
