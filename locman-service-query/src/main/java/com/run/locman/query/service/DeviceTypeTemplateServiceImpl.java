/*
 * File name: DeviceTypeTemplateServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tm 2017年09月14日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.DeviceTypeTemplateQueryRepository;
import com.run.locman.api.query.service.DeviceTypeTemplateService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceTypeContants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Description: 设备类型模板query实现类
 * @author: 田明
 * @version: 1.0, 2017年09月14日
 */
public class DeviceTypeTemplateServiceImpl implements DeviceTypeTemplateService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	DeviceTypeTemplateQueryRepository	deviceTypeTemplateQueryRepository;



	@Override
	public RpcResponse<List<Map<String, String>>> queryDeviceTypePropertyConfigList(String accessSecret) {
		logger.info(String.format("[queryDeviceTypePropertyConfigList()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[queryDeviceTypePropertyConfigList()->invalid：接入方密钥不能为空！！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			List<Map<String, String>> deviceTypeTemplateList = deviceTypeTemplateQueryRepository
					.queryDeviceTypePropertyConfigList(accessSecret);
			if (deviceTypeTemplateList == null) {
				logger.info(String.format("[queryDeviceTypePropertyConfigList()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("该接入方下的设备类型未与任何设备属性模板绑定", null);
			}
			logger.info(String.format("[queryDeviceTypePropertyConfigList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("该接入方下的设备类型与设备属性模板存在绑定关系", deviceTypeTemplateList);
		} catch (Exception e) {
			logger.error("queryDeviceTypePropertyConfigList()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<List<String>> queryDeviceTypeListForTempId(String templateId) {
		logger.info(String.format("[queryDeviceTypeListForTempId()方法执行开始...,参数：【%s】]", templateId));
		try {
			if (StringUtils.isBlank(templateId)) {
				logger.error("[queryDeviceTypeListForTempId()->invalid：查询设备类型id集合时,设备属性模板id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询设备类型id集合信息：设备属性模板id不能为空！");
			}
			List<String> deviceTypeIdList = deviceTypeTemplateQueryRepository.queryDeviceTypeListForTempId(templateId);
			logger.info(String.format("[queryDeviceTypeListForTempId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询设备类型id集合成功", deviceTypeIdList);
		} catch (Exception e) {
			logger.error("queryDeviceTypeListForTempId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<String> queryTempIdForDeviceTypeId(String deviceTypeId) {
		logger.info(String.format("[queryTempIdForDeviceTypeId()方法执行开始...,参数：【%s】]", deviceTypeId));
		try {
			if (StringUtils.isBlank(deviceTypeId)) {
				logger.error("[queryDeviceTypeListForTempId()->invalid：查询设备属性模板id信息,设备类型id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询设备属性模板id信息：设备类型id不能为空！");
			}
			String templateId = deviceTypeTemplateQueryRepository.queryTempIdForDeviceTypeId(deviceTypeId);
			logger.info(String.format("[queryTempIdForDeviceTypeId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询设备属性模板id成功", templateId);
		} catch (Exception e) {
			logger.error("queryTempIdForDeviceTypeId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceTypeTemplateService#queryDeviceProperties(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<Boolean> queryDeviceProperties(JSONObject findParam) {
		logger.info(String.format("[queryDeviceProperties()方法执行开始...,参数：【%s】]", findParam));
		try {
			if (null == findParam) {
				logger.error("[queryDeviceProperties()->invalid：查询该设备类型下设备属性，参数不能空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询该设备类型下设备属性，参数不能空!");
			}
			if (StringUtils.isBlank(findParam.getString(DeviceTypeContants.DEVICETYPEID))) {
				logger.error("[queryDeviceProperties()->invalid：查询该设备类型下设备属性，设备类型deviceTypeId不能空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询该设备类型下设备属性，设备类型deviceTypeId不能空!");
			}
			if (StringUtils.isBlank(findParam.getString(DeviceTypeContants.ACCESSSECRET))) {
				logger.error("[queryDeviceProperties()->invalid：查询该设备类型下设备属性，接入方秘钥accessSecret不能空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询该设备类型下设备属性，接入方秘钥accessSecret不能空!");
			}
			List<Map<String, String>> queryDeviceProperties = deviceTypeTemplateQueryRepository
					.queryDeviceProperties(findParam);
			if (queryDeviceProperties != null && queryDeviceProperties.size() > 0) {
				logger.info("[queryDeviceProperties()->success：查询成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("校验成功,该设备类型下存在设备属性!", Boolean.TRUE);
			} else if (queryDeviceProperties != null && queryDeviceProperties.size() == 0) {
				logger.info("[queryDeviceProperties()->success：查询成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("校验成功,该设备类型下不存在设备属性!", Boolean.FALSE);
			}
			logger.error("[queryDeviceProperties()->fial：查询失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("校验该设备类型下是否存在设备属性失败!");
		} catch (Exception e) {
			logger.error("queryDeviceProperties()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}
}
