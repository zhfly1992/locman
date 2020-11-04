/*
 * File name: DeviceTypeTemplateCudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tm 2018年1月18日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DeviceTypeTemplateCudRepository;
import com.run.locman.api.crud.service.DeviceTypeTemplateCudService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceTypeContants;

/**
 * @Description: 设备类型模板cud
 * @author: 田明
 * @version: 1.0, 2017年09月15日
 */
@Transactional(rollbackFor = Exception.class)
public class DeviceTypeTemplateCudServiceImpl implements DeviceTypeTemplateCudService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceTypeTemplateCudRepository	deviceTypeTemplateCudRepository;



	@Override
	public RpcResponse<String> unbindDeviceTypePropertyAndTemplate(String deviceTypePropertyConfigId,
			String devicePropertyTemplateId, String accessSecret) {
		try {
			if (StringUtils.isBlank(deviceTypePropertyConfigId)) {
				logger.error("[unbindDeviceTypePropertyAndTemplate()->invalid：设备类型与设备属性模块的关系信息,设备类型id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备类型与设备属性模块的关系信息：设备类型id不能为空！");
			}
			if (StringUtils.isBlank(devicePropertyTemplateId)) {
				logger.error("[unbindDeviceTypePropertyAndTemplate()->invalid：设备类型与设备属性模块的关系信息,模版id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备类型与设备属性模块的关系信息：模版id不能为空！");
			}
			Map<String, String> map = Maps.newHashMap();
			map.put(DeviceTypeContants.DEVICETYPEPROPERTYCONFIGID, deviceTypePropertyConfigId);
			map.put(DeviceTypeContants.DEVICEPROPERTYTEMPLATEID, devicePropertyTemplateId);
			map.put(DeviceTypeContants.ACCESSSECRET, accessSecret);
			logger.info(String.format("[unbindDeviceTypePropertyAndTemplate()->进入方法,参数%s]", map));
			deviceTypeTemplateCudRepository.unbindDeviceTypePropertyAndTemplate(map);
			logger.debug("[unbindDeviceTypePropertyAndTemplate()->success:设备类型与设备属性模块的解绑成功]");
			return RpcResponseBuilder.buildSuccessRpcResp("已取消属性配置", deviceTypePropertyConfigId);
		} catch (Exception e) {
			logger.error("unbindDeviceTypePropertyAndTemplate()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> bindDeviceTypePropertyAndTemplate(String deviceTypePropertyConfigId,
			String devicePropertyTemplateId, String accessSecret) {
		try {
			if (StringUtils.isBlank(deviceTypePropertyConfigId)) {
				logger.error("[bindDeviceTypePropertyAndTemplate()->invalid：设备类型与设备属性模块的关系信息,设备类型id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备类型与设备属性模块的关系信息：设备类型id不能为空！");
			}
			if (StringUtils.isBlank(devicePropertyTemplateId)) {
				logger.error("[bindDeviceTypePropertyAndTemplate()->invalid：设备类型与设备属性模块的关系信息,设备属性模板id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备类型与设备属性模块的关系信息：设备属性模板id不能为空！");
			}
			Map<String, String> param = Maps.newHashMap();
			param.put(DeviceTypeContants.DEVICETYPEPROPERTYCONFIGID, deviceTypePropertyConfigId);
			param.put(DeviceTypeContants.DEVICEPROPERTYTEMPLATEID, devicePropertyTemplateId);
			param.put(DeviceTypeContants.ACCESSSECRET, accessSecret);
			// 绑定之前先做删除处理
			logger.info(String.format("[unbindDeviceTypePropertyAndTemplate()->进入方法,参数%s]", param));
			deviceTypeTemplateCudRepository.unbindDeviceTypePropertyAndTemplate(param);
			int addResult = deviceTypeTemplateCudRepository.bindDeviceTypePropertyAndTemplate(param);
			if (addResult > 0) {
				logger.debug("[bindDeviceTypePropertyAndTemplate()->success:设备类型与设备属性模块的绑定成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("已绑定属性配置", deviceTypePropertyConfigId);
			} else {
				logger.debug("[bindDeviceTypePropertyAndTemplate()->fail：设备类型与设备属性模块的绑定失败");
				return RpcResponseBuilder.buildErrorRpcResp("设备类型与设备属性模块的绑定失败");
			}
		} catch (Exception e) {
			logger.error("bindDeviceTypePropertyAndTemplate()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
