/*
* File name: PropertiesTemplateRestCrudService.java								
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
* 1.0			田明		2017年11月20日
* ...			...			...
*
***************************************************/
package com.run.locman.crud.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.PropertiesTemplateCrudService;
import com.run.locman.api.entity.DevicePropertiesTemplate;
import com.run.locman.api.query.service.DeviceTypeTemplateService;
import com.run.locman.api.query.service.PropertiesTemplateQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 设备属性模板管理
 * @author: 田明
 * @version: 1.0, 2017年11月20日
 */
@Service
public class PropertiesTemplateRestCrudService {

	private static final Logger logger = Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private PropertiesTemplateCrudService	propertiesTemplateCrudService;

	@Autowired
	private DeviceTypeTemplateService		deviceTypeTemplateService;

	@Autowired
	private PropertiesTemplateQueryService	propertiesTemplateQueryService;



	public Result<String> add(String accessSecret, String addParams) {
		logger.info(String.format("[add()->request params-accessSecret:%s,addParams:%s]", accessSecret,addParams));
		if (ParamChecker.isBlank(addParams) || ParamChecker.isNotMatchJson(addParams)) {
			logger.error(String.format("[add()()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			DevicePropertiesTemplate template = JSON.parseObject(addParams, DevicePropertiesTemplate.class);
			RpcResponse<Boolean> queryRpc = propertiesTemplateQueryService.checkTemplateName(accessSecret,
					template.getTemplateName());
			if (!queryRpc.isSuccess()) {
				logger.error("[add()->fail:校验模版名称时发生异常]");
				return ResultBuilder.failResult("校验模版名称时发生异常");
			}
			if (queryRpc.getSuccessValue()) {
				logger.error("[add()->fail:该模版名称已被使用]");
				return ResultBuilder.getResult(null, "0006", "该模版名称已被使用", null);
			}
			template.setAccessSecret(accessSecret);
			RpcResponse<String> rpcResponse = propertiesTemplateCrudService.add(template);
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[add()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[add()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("add()->exception",e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> updateState(String id, String updateParams) {
		logger.info(String.format("[updateState()->request params-id:%s,updateParams:%s]", id,updateParams));
		if (ParamChecker.isBlank(updateParams) || ParamChecker.isNotMatchJson(updateParams)) {
			logger.error(String.format("[updateState()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			DevicePropertiesTemplate template = JSON.parseObject(updateParams, DevicePropertiesTemplate.class);
			RpcResponse<List<String>> deviceTypeRpc = deviceTypeTemplateService.queryDeviceTypeListForTempId(id);
			if (!deviceTypeRpc.isSuccess()) {
				logger.error("[updateState()->fail:查询关联设备类型时发生异常]");
				return ResultBuilder.failResult("查询关联设备类型时发生异常");
			}
			if (!CollectionUtils.isEmpty(deviceTypeRpc.getSuccessValue())) {
				logger.error("[updateState()->fail:该模版已应用到设备，取消关联后重试]");
				return ResultBuilder.getResult(null, "0006", "该模版已应用到设备，取消关联后重试", null);
			}
			template.setId(id);
			RpcResponse<String> rpcResponse = propertiesTemplateCrudService.updateState(template);
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[updateState()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[updateState()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("updateState()->exception",e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
