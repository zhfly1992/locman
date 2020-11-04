/*
 * File name: PropertiesTemplateQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tm 2017年12月07日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.DevicePropertiesTemplate;
import com.run.locman.api.query.repository.DevicePropertiesTemplateQueryRepository;
import com.run.locman.api.query.service.PropertiesTemplateQueryService;
import com.run.locman.constants.CommonConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 属性模板query实现类
 * @author: 田明
 * @version: 1.0, 2017年12月07日
 */
public class PropertiesTemplateQueryServiceImpl implements PropertiesTemplateQueryService {
	private static final Logger						logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DevicePropertiesTemplateQueryRepository	devicePropertiesTemplateQueryRepository;



	@Override
	public RpcResponse<PageInfo<DevicePropertiesTemplate>> list(String accessSecret, int pageNum, int pageSize,
			String searchKey, String state) {
		logger.info(String.format("[list()方法执行开始...,参数：【%s】【%s】【%s】【%s】【%s】]", accessSecret, pageNum, pageSize,
				searchKey, state));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[list()->invalid: 接入方密钥不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
			}
			PageHelper.startPage(pageNum, pageSize);
			Map<String, Object> queryParams = new HashMap<>(16);
			queryParams.put("accessSecret", accessSecret);
			queryParams.put("searchKey", searchKey);
			queryParams.put("manageState", state);
			List<DevicePropertiesTemplate> templateList = devicePropertiesTemplateQueryRepository
					.findByAccessSecret(queryParams);
			PageInfo<DevicePropertiesTemplate> page = new PageInfo<>(templateList);
			logger.info(String.format("[list()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("模版查询成功", page);
		} catch (Exception e) {
			logger.error("list()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<DevicePropertiesTemplate> findById(String id) {
		logger.info(String.format("[findById()方法执行开始...,参数：【%s】]", id));
		try {
			if (StringUtils.isBlank(id)) {
				return RpcResponseBuilder.buildErrorRpcResp("id不能为空");
			}
			DevicePropertiesTemplate template = devicePropertiesTemplateQueryRepository.findById(id);
			logger.info(String.format("[findById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("模版查询成功", template);
		} catch (Exception e) {
			logger.error("findById()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<PageInfo<Map<String, String>>> templateListForDeviceType(String accessSecret, int pageNum,
			int pageSize, String searchKey, String state) {
		logger.info(String.format("[templateListForDeviceType()方法执行开始...,参数：【%s】【%s】【%s】【%s】【%s】]", accessSecret,
				pageNum, pageSize, searchKey, state));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[templateListForDeviceType()->invalid: 接入方密钥不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
			}
			PageHelper.startPage(pageNum, pageSize);
			Map<String, String> queryParams = new HashMap<>(16);
			queryParams.put("accessSecret", accessSecret);
			queryParams.put("searchKey", searchKey);
			queryParams.put("state", state);
			List<Map<String, String>> templateList = devicePropertiesTemplateQueryRepository
					.templateListForDeviceType(queryParams);
			PageInfo<Map<String, String>> page = new PageInfo<>(templateList);
			logger.info(String.format("[templateListForDeviceType()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("模版查询成功", page);
		} catch (Exception e) {
			logger.error("templateListForDeviceType()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> checkTemplateName(String accessSecret, String templateName) {
		logger.info(String.format("[checkTemplateName()方法执行开始...,参数：【%s】【%s】]", accessSecret, templateName));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[checkTemplateName()->invalid: 接入方密钥或模版名称不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥或模版名称不能为空");
			}
			Map<String, String> queryParams = new HashMap<>(16);
			queryParams.put("accessSecret", accessSecret);
			queryParams.put("templateName", templateName);
			int num = devicePropertiesTemplateQueryRepository.checkTemplateName(queryParams);
			if (num > 0) {
				logger.info(String.format("[checkTemplateName()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("该模版名称重复不能使用", true);
			}
			logger.info(String.format("[checkTemplateName()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("该模版名称可以使用", false);
		} catch (Exception e) {
			logger.error("checkTemplateName()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
