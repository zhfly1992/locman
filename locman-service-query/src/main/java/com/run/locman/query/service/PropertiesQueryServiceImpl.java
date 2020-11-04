/*
 * File name: PropertiesQueryServiceImpl.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.query.repository.DevicePropertiesQueryRepository;
import com.run.locman.api.query.service.PropertiesQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 故障工单query实现类
 * @author: 田明
 * @version: 1.0, 2017年12月07日
 */
public class PropertiesQueryServiceImpl implements PropertiesQueryService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private DevicePropertiesQueryRepository	devicePropertiesQueryRepository;



	@Override
	public RpcResponse<PageInfo<DeviceProperties>> list(String templateId, int pageNum, int pageSize) {
		logger.info(String.format("[list()方法执行开始...,参数：【%s】【%s】【%s】]", templateId, pageNum, pageSize));
		try {
			if (StringUtils.isBlank(templateId)) {
				logger.error("[list()->invalid: 模版id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("模版id不能为空");
			}
			PageHelper.startPage(pageNum, pageSize);
			List<DeviceProperties> propertiesList = devicePropertiesQueryRepository.findByTemplateId(templateId);
			PageInfo<DeviceProperties> page = new PageInfo<>(propertiesList);
			logger.info(String.format("[list()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("属性查询成功", page);
		} catch (Exception e) {
			logger.error("list()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> existOrder(String templateId, String id, Integer order) {
		logger.info(String.format("[existOrder()方法执行开始...,参数：【%s】【%s】【%s】]", templateId, id, order));
		try {
			if (StringUtils.isBlank(templateId) || order == null) {
				logger.error("[existOrder()->invalid: 模版id或排序参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("模版id或排序参数不能为空");
			}
			Map<String, Object> params = new HashMap<>(16);
			params.put("templateId", templateId);
			params.put("id", id);
			params.put("order", order);
			int num = devicePropertiesQueryRepository.existOrder(params);
			if (num > 0) {
				logger.info(String.format("[existOrder()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("已有该排序序号", true);
			}
			logger.info(String.format("[existOrder()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("该排序序号不存在", false);
		} catch (Exception e) {
			logger.error("existOrder()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.PropertiesQueryService#findByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<List<DeviceProperties>> findByDeviceTypeId(String accessSecret, String deviceTypeId) {
		logger.info(String.format("[findByDeviceTypeId()方法执行开始...,参数：【%s】【%s】]", accessSecret, deviceTypeId));
		try {
			if (StringUtils.isBlank(deviceTypeId)) {
				logger.error("[findByDeviceTypeId()->invalid: 设备类型id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("模版id不能为空");
			}
			List<DeviceProperties> devicePropertiesList = devicePropertiesQueryRepository
					.findByDeviceTypeId(accessSecret, deviceTypeId);
			logger.info(String.format("[findByDeviceTypeId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", devicePropertiesList);
		} catch (Exception e) {
			logger.error("findByDeviceTypeId()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.PropertiesQueryService#findById(java.lang.String)
	 */
	@Override
	public RpcResponse<DeviceProperties> findById(String id) {
		logger.info(String.format("[findById()方法执行开始...,参数：【%s】]", id));
		if (StringUtils.isBlank(id)) {
			logger.error("[findById()->invalid: 查询id不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("查询id不能为空");
		}
		try {
			DeviceProperties devicePropertie = devicePropertiesQueryRepository.findById(id);
			logger.info("[findById()->success: 查询成功]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", devicePropertie);
		} catch (Exception e) {
			logger.error("findById()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.PropertiesQueryService#checkNameAndSigExist(com.run.locman.api.entity.DeviceProperties)
	 */
	@Override
	public RpcResponse<Boolean> checkNameOrSigExist(DeviceProperties deviceProperties) {
		try {
			if (StringUtils.isBlank(deviceProperties.getTemplateId())) {
				logger.error("checkNameOrSigExist()->invalid:模板id不能为空!!!");
				return RpcResponseBuilder.buildErrorRpcResp("模板id不能为空!!!");
			}
			String devicePropertiesName = deviceProperties.getDevicePropertiesName();
			boolean nameBlank = StringUtils.isBlank(devicePropertiesName);
			boolean signBlank = StringUtils.isBlank(deviceProperties.getDevicePropertiesSign());
			if (nameBlank && signBlank) {
				logger.error("checkNameOrSigExist()->invalid:显示名和标识名不能同时为空!!!");	
				return RpcResponseBuilder.buildErrorRpcResp("需要校验的参数不能为空!!!");
			}
			if (!nameBlank && !signBlank) {
				logger.error("checkNameOrSigExist()->invalid:显示名和标识名不能同时校验!!!");	
				return RpcResponseBuilder.buildErrorRpcResp("只能校验一个字段!!!");
			}
			
			List<JSONObject> jsonList = devicePropertiesQueryRepository.checkNameOrSigExist(deviceProperties);
			if (null != jsonList && jsonList.size() > 0) {
				for (JSONObject json : jsonList) {
					if (!signBlank && json.containsValue(deviceProperties.getDevicePropertiesSign())) {
						logger.error(String.format("[checkNameOrSigExist()->invalid: 此模板已包含该标识名,不能重复添加!!!校验返回值:%s]",jsonList));
						return RpcResponseBuilder.buildSuccessRpcResp("此模板已包含该标识名!!!",true);
					}
					if (!nameBlank && json.containsValue(deviceProperties.getDevicePropertiesName())) {
						logger.error(String.format("[checkNameOrSigExist()->invalid: 此模板已包含该显示名,不能重复添加!!!校验返回值:%s]",jsonList));
						return RpcResponseBuilder.buildSuccessRpcResp("此模板已包含该显示名!!!",true);
					}
				}
			}
			logger.info("[checkNameOrSigExist()->invalid:查询成功,不重复!");
			return RpcResponseBuilder.buildSuccessRpcResp("可以使用", false);
			
		} catch (Exception e) {
			logger.error("checkNameOrSigExist()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	
	}

}
