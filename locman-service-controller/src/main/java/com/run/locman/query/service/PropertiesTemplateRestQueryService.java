/*
 * File name: PropertiesTemplateRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年11月20日 ... ...
 * ...
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
import com.run.locman.api.entity.DevicePropertiesTemplate;
import com.run.locman.api.query.service.PropertiesTemplateQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 分页查询设备属性模版
 * @author: qulong
 * @version: 1.0, 2017年11月20日
 */
@Service
public class PropertiesTemplateRestQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private PropertiesTemplateQueryService	propertiesTemplateQueryService;



	public Result<PageInfo<DevicePropertiesTemplate>> list(String accessSecret, String queryParams) {
		logger.info(String.format("[list()->request params:%s]", queryParams));
		if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
			logger.error(String.format("[list()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
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
			if (!paramsObject.containsKey(CommonConstants.SEARCH_KEY)) {
				logger.error("[list()->error:参数必须包含searchKey]");
				return ResultBuilder.emptyResult();
			}
			if (!StringUtils.isNumeric(paramsObject.getString(CommonConstants.PAGE_NUM))
					|| !StringUtils.isNumeric(paramsObject.getString(CommonConstants.PAGE_SIZE))) {
				logger.error("[list()->error:pageSize和pageNum必须为数字]");
				return ResultBuilder.invalidResult();
			}
			int pageNum = paramsObject.getIntValue(CommonConstants.PAGE_NUM);
			int pageSize = paramsObject.getIntValue(CommonConstants.PAGE_SIZE);
			String searchKey = paramsObject.getString(CommonConstants.SEARCH_KEY);
			String manageState = paramsObject.getString("manageState");
			RpcResponse<PageInfo<DevicePropertiesTemplate>> rpcResponse = propertiesTemplateQueryService
					.list(accessSecret, pageNum, pageSize, searchKey, manageState);
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[list()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[list()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("list()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Boolean> checkTemplateName(String templateInfo) {
		logger.info(String.format("[checkTemplateName()->request templateInfo:%s ]", templateInfo));
		try {
			JSONObject json = JSON.parseObject(templateInfo);
			String accessSecret = json.getString("accessSecret");
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("checkTemplateName()->fail:接入方密钥不能为空");
				return ResultBuilder.invalidResult();
			}
			String templateName = json.getString("templateName");
			if (StringUtils.isBlank(templateName)) {
				logger.error("checkTemplateName()->fail:设备属性模板名称不能为空");
				return ResultBuilder.invalidResult();
			}
			RpcResponse<Boolean> checkTemplateName = propertiesTemplateQueryService.checkTemplateName(accessSecret,
					templateName);
			if (checkTemplateName == null) {
				logger.error("[checkTemplateName()->fail:设备属性模板名称校验失败,接口返回值为null]");
				return ResultBuilder.failResult("设备属性模板名称校验失败");
			}
			if (checkTemplateName.isSuccess() && checkTemplateName.getSuccessValue() != null) {
				logger.info(String.format("[checkTemplateName()->success:%s]", checkTemplateName.getMessage()));
				return ResultBuilder.successResult(checkTemplateName.getSuccessValue(), checkTemplateName.getMessage());
			} else {
				logger.error(String.format("[checkTemplateName()->fail:%s]", checkTemplateName.getMessage()));
				return ResultBuilder.failResult(checkTemplateName.getMessage());
			}

		} catch (Exception e) {
			logger.error("checkTemplateName()->Exception:" + e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
