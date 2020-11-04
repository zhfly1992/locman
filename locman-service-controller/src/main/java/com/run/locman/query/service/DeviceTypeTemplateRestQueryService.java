/*
 * File name: DeviceTypeTemplateRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年09月14日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.DeviceTypeTemplate;
import com.run.locman.api.query.service.DeviceTypeQueryService;
import com.run.locman.api.query.service.DeviceTypeTemplateService;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.query.service.PropertiesTemplateQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceTypeContants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description: 设施数据类型查询Contro
 * @author: 田明
 * @version: 1.0, 2017年09月14日
 */
@Service
public class DeviceTypeTemplateRestQueryService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	DeviceTypeTemplateService				deviceTypeTemplateService;

	@Autowired
	private PropertiesTemplateQueryService	propertiesTemplateQueryService;

	@Autowired
	private FactoryQueryService				factoryQueryService;

	@Autowired
	DeviceTypeQueryService					deviceTypeQueryService;



	public Result<PageInfo<Map<String, Object>>> queryDeviceTypePropertyConfigList(
			String deviceTypePropertyConfigInfo) {
		try {
			logger.info(String.format("[queryDeviceTypePropertyConfigList()->request params:%s]",
					deviceTypePropertyConfigInfo));
			// 验证参数是否合法
			Result<List<DeviceTypeTemplate>> result = ExceptionChecked.checkRequestParam(deviceTypePropertyConfigInfo);
			if (result != null) {
				logger.error(String.format("[getAllFacilitiesDataType()->error:%s]", "参数验证失败:参数为空或格式错误!"));
				return ResultBuilder.failResult("参数验证失败:参数为空或格式错误!");
			}
			JSONObject jsonParam = JSON.parseObject(deviceTypePropertyConfigInfo);
			if (StringUtils.isBlank(jsonParam.getString(DeviceTypeContants.ACCESSSECRET))) {
				logger.error(String.format("[getAllFacilitiesDataType()->error:%s]", "接入方秘钥不能为空!"));
				return ResultBuilder.failResult("接入方秘钥不能为空!");
			}
			// 验证传入数字是否合法
			if (!StringUtils.isNumeric(jsonParam.getString(CommonConstants.PAGE_NUM))) {
				logger.error(String.format("[getFacilitiesDataTypeList()->error:%s]", "查询分页信息,传入数字异常!"));
				return ResultBuilder.failResult("查询分页信息,传入数字异常!");
			}

			// 获取页面页数
			Integer pageNum = jsonParam.getIntValue(CommonConstants.PAGE_NUM);

			// 默认分页大小
			Integer pageSize = 0;
			if (StringUtils.isBlank(jsonParam.getString(CommonConstants.PAGE_SIZE))) {
				pageSize = 10;
			} else {
				// 验证传入数字是否合法
				if (!StringUtils.isNumeric(jsonParam.getString(CommonConstants.PAGE_SIZE))) {
					logger.error(String.format("[queryDeviceTypePropertyConfigList()->error:%s]", "查询分页信息,传入数字异常!"));
					return ResultBuilder.failResult("查询分页信息,传入数字异常!");
				}
				pageSize = jsonParam.getIntValue(CommonConstants.PAGE_SIZE);
			}
			if (!jsonParam.containsKey(DeviceTypeContants.DEVICETYPENAME)) {
				logger.error("[queryDeviceTypePropertyConfigList()->error:必须传入deviceTypeName]");
				return ResultBuilder.invalidResult();
			}
			RpcResponse<List<String>> appTagInfo = factoryQueryService
					.queryAppTagForAccessSecret(jsonParam.getString(DeviceTypeContants.ACCESSSECRET));
			if (!appTagInfo.isSuccess()) {
				logger.error(String.format("[queryDeviceTypePropertyConfigList()->fail:%s]", appTagInfo.getMessage()));
				return ResultBuilder.failResult(appTagInfo.getMessage());
			}
			String deviceTypeName = jsonParam.getString(DeviceTypeContants.DEVICETYPENAME);
			if (StringUtils.isBlank(deviceTypeName)) {
				deviceTypeName = null;
			}
			RpcResponse<List<Map<String, String>>> dttList = deviceTypeTemplateService
					.queryDeviceTypePropertyConfigList(jsonParam.getString(DeviceTypeContants.ACCESSSECRET));
			Map<String, Object> queryDeviceTypeInfo = Maps.newHashMap();
			queryDeviceTypeInfo.put("accessSecret", jsonParam.getString(DeviceTypeContants.ACCESSSECRET));
			queryDeviceTypeInfo.put("deviceTypeName", deviceTypeName);
			queryDeviceTypeInfo.put("pageNum", pageNum);
			queryDeviceTypeInfo.put("pageSize", pageSize);
			RpcResponse<PageInfo<Map<String, Object>>> deviceTypelistResult = deviceTypeQueryService
					.queryDeviceTypeListByInfo(queryDeviceTypeInfo);
			Result<Object> queryStateHandle = queryStateHandle(dttList, deviceTypelistResult);
			if (null != queryStateHandle) {
				return ResultBuilder.failResult(queryStateHandle.getResultStatus().getResultMessage());
			}
			logger.info(
					String.format("[queryDeviceTypePropertyConfigList()->success:%s]", MessageConstant.SEARCH_SUCCESS));
			return ResultBuilder.successResult(deviceTypelistResult.getSuccessValue(), MessageConstant.SEARCH_SUCCESS);
		} catch (Exception e) {
			logger.error("rest-queryDeviceTypePropertyConfigList()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param dttList
	 * @param deviceTypelistResult
	 */

	private Result<Object> queryStateHandle(RpcResponse<List<Map<String, String>>> dttList,
			RpcResponse<PageInfo<Map<String, Object>>> deviceTypelistResult) {
		if (!dttList.isSuccess() || !deviceTypelistResult.isSuccess()) {
			logger.info(String.format("[queryDeviceTypePropertyConfigList()->fail:%s]", dttList.getMessage()));
			return ResultBuilder.failResult(dttList.getMessage());
		}
		List<Map<String, Object>> deviceTypelist = deviceTypelistResult.getSuccessValue().getList();
		if (null == deviceTypelist) {
			logger.error("[queryDeviceTypePropertyConfigList()->fail:设备最新状态查询失败]");
			return ResultBuilder.failResult("设备最新状态查询失败");
		}
		for (int p = 0; p < deviceTypelist.size(); p++) {
			if (!deviceTypelist.get(p).containsKey("parentId")) {
				deviceTypelist.remove(p);
				p--;
			}
		}
		return null;
	}



	public Result<PageInfo<Map<String, String>>> queryDevicePropertyTemplateList(String devicePropertyTemplateInfo) {
		try {
			logger.info(String.format("[queryDevicePropertyTemplateList()->request params:%s]",
					devicePropertyTemplateInfo));
			// 验证参数是否合法
			Result<List<DeviceTypeTemplate>> result = ExceptionChecked.checkRequestParam(devicePropertyTemplateInfo);
			if (result != null) {
				logger.error(String.format("[queryDevicePropertyTemplateList()->error:%s]", "参数验证失败:参数为空或格式错误!"));
				return ResultBuilder.failResult("参数验证失败:参数为空或格式错误!");
			}
			JSONObject jsonParam = JSON.parseObject(devicePropertyTemplateInfo);
			// 验证传入数字是否合法
			if (!StringUtils.isNumeric(jsonParam.getString(CommonConstants.PAGE_NUM))) {
				logger.error(String.format("[queryDevicePropertyTemplateList()->error:%s]", "查询分页信息,传入数字异常!"));
				return ResultBuilder.failResult("查询分页信息,传入数字异常!");
			}

			// 获取页面页数
			Integer pageNo = jsonParam.getIntValue(CommonConstants.PAGE_NUM);

			// 默认分页大小
			Integer pageSize = 0;
			if (StringUtils.isBlank(jsonParam.getString(CommonConstants.PAGE_SIZE))) {
				pageSize = 10;
			} else {
				// 验证传入数字是否合法
				if (!StringUtils.isNumeric(jsonParam.getString(CommonConstants.PAGE_SIZE))) {
					logger.error(String.format("[queryDevicePropertyTemplateList()->error:%s]", "查询分页信息,传入数字异常!"));
					return ResultBuilder.failResult("查询分页信息,传入数字异常!");
				}
				pageSize = jsonParam.getIntValue(CommonConstants.PAGE_SIZE);
			}
			if (!jsonParam.containsKey(DeviceTypeContants.DEVICEPROPERTYTEMPLATENAME)) {
				logger.error("[queryDevicePropertyTemplateList()->error:必须传入devicePropertyTemplateName]");
				return ResultBuilder.invalidResult();
			}
			if (!jsonParam.containsKey(FacilitiesContants.USC_ACCESS_SECRET)) {
				logger.error("[queryDevicePropertyTemplateList()->error:必须传入accessSecret]");
				return ResultBuilder.invalidResult();
			}
			if (!jsonParam.containsKey(DeviceTypeContants.DEVICETYPEID)) {
				logger.error("[queryDevicePropertyTemplateList()->error:必须传入deviceTypeId]");
				return ResultBuilder.invalidResult();
			}
			String devicePropertyTemplateName = jsonParam.getString(DeviceTypeContants.DEVICEPROPERTYTEMPLATENAME);
			String accessSecret = jsonParam.getString(FacilitiesContants.USC_ACCESS_SECRET);
			RpcResponse<PageInfo<Map<String, String>>> propertyTemplateMap = propertiesTemplateQueryService
					.templateListForDeviceType(accessSecret, pageNo, pageSize, devicePropertyTemplateName, "enabled");
			PageInfo<Map<String, String>> pp = propertyTemplateMap.getSuccessValue();
			logger.info(String.format("[queryDevicePropertyTemplateList()->success:%s]", propertyTemplateMap));
			return ResultBuilder.successResult(pp, "设备属性模板查询成功");
		} catch (Exception e) {
			logger.error("rest-queryDevicePropertyTemplateList()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
