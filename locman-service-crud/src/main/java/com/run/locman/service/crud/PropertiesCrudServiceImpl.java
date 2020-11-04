/*
* File name: PropertiesCrudServiceImpl.java
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
* 1.0			tm		2017年10月20日
* ...			...			...
*
***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DevicePropertiesCrudRepository;
import com.run.locman.api.crud.service.PropertiesCrudService;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 设备属性cud实现类
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
@Transactional(rollbackFor = Exception.class)
public class PropertiesCrudServiceImpl implements PropertiesCrudService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DevicePropertiesCrudRepository	devicePropertiesCrudRepository;



	@Override
	public RpcResponse<String> add(DeviceProperties deviceProperties) {
		try {
			if (StringUtils.isBlank(deviceProperties.getTemplateId())) {
				logger.error("[add()->invalid: 属性密钥不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性密钥不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getDevicePropertiesName())) {
				logger.error("[add()->invalid: 属性名称不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性名称不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getDevicePropertiesSign())) {
				logger.error("[add()->invalid: 属性标识不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性标识不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getDataType())) {
				logger.error("[add()->invalid: 属性数据类型不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性数据类型不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getDataValue())) {
				logger.error("[add()->invalid: 属性数据值不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性数据值不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getIcon())) {
				logger.error("[add()->invalid: 属性图标不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性图标不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getAppIcon())) {
				logger.error("[add()->invalid: app属性图标不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("app属性图标不能为空");
			}
			if (deviceProperties.getReadWrite() == null) {
				logger.error("[add()->invalid: 属性读写类型不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性读写类型不能为空");
			}
			if (deviceProperties.getOrder() == null) {
				logger.error("[add()->invalid: 属性排序不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性排序不能为空");
			}
			logger.info(String.format("[add()->进入方法,参数:%s]", deviceProperties));
			List<JSONObject> jsonList = devicePropertiesCrudRepository.checkNameAndSigExist(deviceProperties);
			if (null != jsonList && jsonList.size() > 0) {
				for (JSONObject json : jsonList) {
					if (json.containsValue(deviceProperties.getDevicePropertiesSign())) {
						logger.error(String.format("[add()->invalid: 此模板已包含该标识名,不能重复添加!!!校验返回值:%s]",jsonList));
						return RpcResponseBuilder.buildErrorRpcResp("此模板已包含该标识名,不能重复添加!!!");
					}
					if (json.containsValue(deviceProperties.getDevicePropertiesName())) {
						logger.error(String.format("[add()->invalid: 此模板已包含该显示名,不能重复添加!!!校验返回值:%s]",jsonList));
						return RpcResponseBuilder.buildErrorRpcResp("此模板已包含该显示名,不能重复添加!!!");
					}
				}
			}
			
			deviceProperties.setCreationTime(DateUtils.formatDate(new Date()));
			int num = devicePropertiesCrudRepository.insertModel(deviceProperties);
			if (num > 0) {
				logger.info("[add()->success: 属性保存成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("属性保存成功", deviceProperties.getId());
			}
			logger.error("[add()->fail: 属性地址保存错误" + JSON.toJSONString(deviceProperties) + "]");
			return RpcResponseBuilder.buildErrorRpcResp("属性保存失败");
		} catch (Exception e) {
			logger.error("add()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> update(DeviceProperties deviceProperties) {
		try {
			if (StringUtils.isBlank(deviceProperties.getId())) {
				logger.error("[update()->invalid: 属性id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性id不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getDevicePropertiesName())) {
				logger.error("[update()->invalid: 属性属性名不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性属性名不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getDevicePropertiesSign())) {
				logger.error("[update()->invalid: 属性属性标识不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性属性标识不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getDataType())) {
				logger.error("[update()->invalid: 属性数据类型不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性数据类型不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getDataValue())) {
				logger.error("[update()->invalid: 属性数据值不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性数据值不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getIcon())) {
				logger.error("[update()->invalid: 属性图标不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性图标不能为空");
			}
			if (StringUtils.isBlank(deviceProperties.getAppIcon())) {
				logger.error("[update()->invalid: app属性图标不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("app属性图标不能为空");
			}
			if (deviceProperties.getReadWrite() == null) {
				logger.error("[update()->invalid: 属性读写类型不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性读写类型不能为空");
			}
			if (deviceProperties.getOrder() == null) {
				logger.error("[update()->invalid: 属性序号不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性序号不能为空");
			}
			
			logger.info(String.format("[update()->进入方法,参数:%s]", deviceProperties));
			List<JSONObject> jsonList = devicePropertiesCrudRepository.checkNameAndSigExist(deviceProperties);
			if (null != jsonList && jsonList.size() > 0) {
				for (JSONObject json : jsonList) {
					if (json.containsValue(deviceProperties.getDevicePropertiesSign())) {
						logger.error(String.format("[add()->invalid: 此模板已包含该标识名,不能重复添加!!!校验返回值:%s]",jsonList));
						return RpcResponseBuilder.buildErrorRpcResp("此模板已包含该标识名,不能重复添加!!!");
					}
					if (json.containsValue(deviceProperties.getDevicePropertiesName())) {
						logger.error(String.format("[add()->invalid: 此模板已包含该显示名,不能重复添加!!!校验返回值:%s]",jsonList));
						return RpcResponseBuilder.buildErrorRpcResp("此模板已包含该显示名,不能重复添加!!!");
					}
				}
			}
			deviceProperties.setCreationTime(DateUtils.formatDate(new Date()));
			int num = devicePropertiesCrudRepository.updatePart(deviceProperties);
			if (num > 0) {
				logger.info("[add()->success: 属性修改成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("属性修改成功", null);
			}
			logger.error("[add()->fail: 属性地址保存错误" + JSON.toJSONString(deviceProperties) + "]");
			return RpcResponseBuilder.buildErrorRpcResp("属性修改失败");
		} catch (Exception e) {
			logger.error("add()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> delete(String id) {
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[delete()->invalid: 属性id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("属性id不能为空");
			}
			logger.info(String.format("[delete()->进入方法,参数:id:%s]", id));
			int num = devicePropertiesCrudRepository.deleteById(id);
			if (num > 0) {
				logger.info("[delete()->success: 属性删除成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("属性删除成功", null);
			}
			logger.error("[delete()->fail: 属性删除错误" + id + "]");
			return RpcResponseBuilder.buildErrorRpcResp("属性删除失败");
		} catch (Exception e) {
			logger.error("delete()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> updateOrder(String templateId, String propertiesId, Integer order) {
		try {
			if (StringUtils.isBlank(templateId) || StringUtils.isBlank(propertiesId) || order == null) {
				logger.error("[existOrder()->invalid: 模版id或排序参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("模版id或排序参数不能为空");
			}
			Map<String, Object> params = Maps.newHashMap();
			params.put("templateId", templateId);
			params.put("propertiesId", propertiesId);
			params.put("order", order);
			logger.info(String.format("[updateOrder()->进入方法,参数:%s]", params));
			devicePropertiesCrudRepository.updateOrder(params);
			return RpcResponseBuilder.buildSuccessRpcResp("更新属性序号成功", true);
		} catch (Exception e) {
			logger.error("updateOrder()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
