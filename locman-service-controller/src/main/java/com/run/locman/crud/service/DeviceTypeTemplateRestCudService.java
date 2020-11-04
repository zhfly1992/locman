/*
 * File name: DeviceTypeTemplateRestCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年09月15日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.DeviceTypeTemplateCudService;
import com.run.locman.api.entity.DevicePropertiesTemplate;
import com.run.locman.api.query.service.PropertiesTemplateQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceTypeContants;

/**
 * @Description: 设备类型绑定,解绑
 * @author: 田明
 * @version: 1.0, 2017年09月15日
 */
@Service
public class DeviceTypeTemplateRestCudService {

	@Autowired
	private DeviceTypeTemplateCudService	deviceTypeTemplateCudService;

	@Autowired
	private PropertiesTemplateQueryService	propertiesTemplateQueryService;

	private Logger							logger	= Logger.getLogger(CommonConstants.LOGKEY);



	public Result<String> unbindDeviceTypePropertyAndTemplate(String param) {
		try {
			logger.info(String.format("[unbindDeviceTypePropertyAndTemplate()->request params:%s]", param));
			if (ParamChecker.isBlank(param)) {
				logger.error("[unbindDeviceTypePropertyAndTemplate()--invalid：参数不能空!]");
				return ResultBuilder.failResult("参数不能空!");
			}
			if (ParamChecker.isNotMatchJson(param)) {
				logger.error("[unbindDeviceTypePropertyAndTemplate()--invalid：参数必须为json格式!]");
				return ResultBuilder.failResult("参数必须为json格式!");
			}
			JSONObject paramsJson = JSONObject.parseObject(param);
			if (StringUtils.isBlank(paramsJson.getString(DeviceTypeContants.DEVICETYPEPROPERTYCONFIGID))) {
				logger.error("[unbindDeviceTypePropertyAndTemplate()--invalid：设备类型属性deviceTypePropertyConfigId不能空!]");
				return ResultBuilder.failResult("设备类型属性deviceTypePropertyConfigId不能空!");
			}
			if (StringUtils.isBlank(paramsJson.getString(DeviceTypeContants.ACCESSSECRET))) {
				logger.error("[unbindDeviceTypePropertyAndTemplate()--invalid：接入方秘钥不能空!]");
				return ResultBuilder.failResult("接入方秘钥不能空!");
			}
			String deviceTypePropertyConfigId = paramsJson.getString(DeviceTypeContants.DEVICETYPEPROPERTYCONFIGID);
			String devicePropertyTemplateId = paramsJson.getString(DeviceTypeContants.DEVICEPROPERTYTEMPLATEID);
			String accessSecret = paramsJson.getString(DeviceTypeContants.ACCESSSECRET);
			RpcResponse<String> bindResult = deviceTypeTemplateCudService.unbindDeviceTypePropertyAndTemplate(
					deviceTypePropertyConfigId, devicePropertyTemplateId, accessSecret);
			if (!bindResult.isSuccess()) {
				logger.error(String.format(
						"[unbindDeviceTypePropertyAndTemplate()->fail：设备类型属性deviceTypePropertyConfigId：%s]",
						deviceTypePropertyConfigId));
				return ResultBuilder.failResult(bindResult.getMessage());
			}
			logger.info(String.format(
					"[unbindDeviceTypePropertyAndTemplate()->success：设备类型属性deviceTypePropertyConfigId：%s]",
					deviceTypePropertyConfigId));
			return ResultBuilder.successResult(bindResult.getSuccessValue(), bindResult.getMessage());
		} catch (Exception e) {
			logger.error("unbindDeviceTypePropertyAndTemplate()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> bindDeviceTypePropertyAndTemplate(String param) {
		try {
			logger.info(String.format("[bindDeviceTypePropertyAndTemplate()->request params:%s]", param));
			if (ParamChecker.isBlank(param)) {
				logger.error("[bindDeviceTypePropertyAndTemplate()--invalid：参数不能空!]");
				return ResultBuilder.failResult("参数不能空!");
			}
			if (ParamChecker.isNotMatchJson(param)) {
				logger.error("[bindDeviceTypePropertyAndTemplate()--invalid：参数必须为json格式!]");
				return ResultBuilder.failResult("参数必须为json格式!");
			}
			JSONObject paramsJson = JSONObject.parseObject(param);
			if (StringUtils.isBlank(paramsJson.getString(DeviceTypeContants.DEVICETYPEPROPERTYCONFIGID))) {
				logger.error("[bindDeviceTypePropertyAndTemplate()--invalid：设备类型属性deviceTypePropertyConfigId不能空!]");
				return ResultBuilder.failResult("设备类型属性deviceTypePropertyConfigId不能空!");
			}
			if (StringUtils.isBlank(paramsJson.getString(DeviceTypeContants.DEVICEPROPERTYTEMPLATEID))) {
				logger.error("[bindDeviceTypePropertyAndTemplate()--invalid：设备属性模板devicePropertyTemplateId不能为空!]");
				return ResultBuilder.failResult("设备属性模板devicePropertyTemplateId不能为空!");
			}
			if (StringUtils.isBlank(paramsJson.getString(DeviceTypeContants.ACCESSSECRET))) {
				logger.error("[bindDeviceTypePropertyAndTemplate()--invalid：接入方秘钥不能空!]");
				return ResultBuilder.failResult("接入方秘钥不能空!");
			}
			String deviceTypePropertyConfigId = paramsJson.getString(DeviceTypeContants.DEVICETYPEPROPERTYCONFIGID);
			String devicePropertyTemplateId = paramsJson.getString(DeviceTypeContants.DEVICEPROPERTYTEMPLATEID);
			String accessSecret = paramsJson.getString(DeviceTypeContants.ACCESSSECRET);
			// 检查可用性
			RpcResponse<DevicePropertiesTemplate> devicePropertiesTemplate = propertiesTemplateQueryService
					.findById(devicePropertyTemplateId);
			String templateState = devicePropertiesTemplate.getSuccessValue().getManageState();
			if (!CommonConstants.ENABLED.equals(templateState)) {
				logger.error(String.format("[bindDeviceTypePropertyAndTemplate()->fail:绑定失败,该设备属性模板处于停用状态:%s]",
						devicePropertyTemplateId));
				return ResultBuilder.failResult(String.format("绑定失败,该设备属性模板处于停用状态:%s", devicePropertyTemplateId));
			}
			RpcResponse<String> bindResult = deviceTypeTemplateCudService.bindDeviceTypePropertyAndTemplate(
					deviceTypePropertyConfigId, devicePropertyTemplateId, accessSecret);
			if (!bindResult.isSuccess()) {
				logger.error(
						String.format("[bindDeviceTypePropertyAndTemplate()->fail：设备类型属性deviceTypePropertyConfigId：%s]",
								deviceTypePropertyConfigId));
				return ResultBuilder.failResult(bindResult.getMessage());
			}
			logger.info(
					String.format("[bindDeviceTypePropertyAndTemplate()->success：设备类型属性deviceTypePropertyConfigId：%s]",
							deviceTypePropertyConfigId));
			return ResultBuilder.successResult(bindResult.getSuccessValue(), bindResult.getMessage());
		} catch (Exception e) {
			logger.error("bindDeviceTypePropertyAndTemplate()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}
}