/*
* File name: PropertiesRestCrudService.java								
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.PropertiesCrudService;
import com.run.locman.api.crud.service.PropertiesTemplateCrudService;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.query.service.PropertiesQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 设备属性增删改
 * @author: 田明
 * @version: 1.0, 2017年11月20日
 */
@Service
public class PropertiesRestCrudService {

	private static final Logger logger = Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private PropertiesCrudService			propertiesCrudService;
	@Autowired
	private PropertiesTemplateCrudService	propertiesTemplateCrudService;
	@Autowired
	private PropertiesQueryService			propertiesQueryService;



	public Result<String> add(String templateId, String addParams) {
		logger.info(String.format("[add()->addParams:%s,templateId:%s]", addParams,templateId));
		if (ParamChecker.isBlank(addParams) || ParamChecker.isNotMatchJson(addParams)) {
			logger.error(String.format("[add()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			DeviceProperties deviceProperties = JSON.parseObject(addParams, DeviceProperties.class);
			deviceProperties.setTemplateId(templateId);
			RpcResponse<String> rpcResponse = propertiesCrudService.add(deviceProperties);
			if (rpcResponse.isSuccess()) {
				RpcResponse<Boolean> existOrderRpc = propertiesQueryService.existOrder(templateId,
						rpcResponse.getSuccessValue(), deviceProperties.getOrder());
				if (existOrderRpc.isSuccess() && existOrderRpc.getSuccessValue()) {
					propertiesCrudService.updateOrder(templateId, rpcResponse.getSuccessValue(),
							deviceProperties.getOrder());
				}
				propertiesTemplateCrudService.updateTime(templateId);
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



	public Result<String> update(String templateId, String id, String updateParams) {
		logger.info(String.format("[update()->request params-templateId:%s,id:%s,updateParams:%s]", templateId,id,updateParams));
		if (ParamChecker.isBlank(updateParams) || ParamChecker.isNotMatchJson(updateParams)) {
			logger.error(String.format("[update()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			DeviceProperties deviceProperties = JSON.parseObject(updateParams, DeviceProperties.class);
			deviceProperties.setId(id);
			deviceProperties.setTemplateId(templateId);
			RpcResponse<String> rpcResponse = propertiesCrudService.update(deviceProperties);
			if (rpcResponse.isSuccess()) {
				RpcResponse<Boolean> existOrderRpc = propertiesQueryService.existOrder(templateId, id,
						deviceProperties.getOrder());
				if (existOrderRpc.isSuccess() && existOrderRpc.getSuccessValue()) {
					propertiesCrudService.updateOrder(templateId, id, deviceProperties.getOrder());
				}
				propertiesTemplateCrudService.updateTime(templateId);
				logger.info(String.format("[update()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[update()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("update()->exception",e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> delete(String templateId, String id) {
		logger.info(String.format("[delete()->request params-templateId:%s,id:%s]", templateId,id));
		try {
			RpcResponse<String> rpcResponse = propertiesCrudService.delete(id);
			if (rpcResponse.isSuccess()) {
				propertiesTemplateCrudService.updateTime(templateId);
				logger.info(String.format("[delete()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[delete()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("delete()->exception",e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
