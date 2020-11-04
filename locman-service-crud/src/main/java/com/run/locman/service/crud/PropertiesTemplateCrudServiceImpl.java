/*
* File name: PropertiesTemplateCrudServiceImpl.java
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

import com.alibaba.fastjson.JSON;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DevicePropertiesTemplateCrudRepository;
import com.run.locman.api.crud.service.PropertiesTemplateCrudService;
import com.run.locman.api.entity.DevicePropertiesTemplate;
import com.run.locman.constants.CommonConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;

/**
 * @Description: 属性模板cud实现类
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
@Transactional(rollbackFor = Exception.class)
public class PropertiesTemplateCrudServiceImpl implements PropertiesTemplateCrudService {
	private static final Logger						logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DevicePropertiesTemplateCrudRepository	devicePropertiesTemplateCrudRepository;



	@Override
	public RpcResponse<String> add(DevicePropertiesTemplate template) {
		try {
			if (StringUtils.isBlank(template.getAccessSecret())) {
				logger.error("[add()->invalid: 接入方密钥不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
			}
			if (StringUtils.isBlank(template.getTemplateName())) {
				logger.error("[add()->invalid: 模版名称不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("模版名称不能为空");
			}
			template.setCreationTime(DateUtils.formatDate(new Date()));
			template.setEditorTime(DateUtils.formatDate(new Date()));
			template.setManageState("enabled");
			logger.info(String.format("[add()->进入方法,参数:%s]", template));
			int num = devicePropertiesTemplateCrudRepository.insertModel(template);
			if (num > 0) {
				logger.info("[add()->success: 模版保存成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("模版保存成功", template.getId());
			}
			logger.error("[add()->fail: 模版地址保存错误" + JSON.toJSONString(template) + "]");
			return RpcResponseBuilder.buildErrorRpcResp("模版保存失败");
		} catch (Exception e) {
			logger.error("add()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> updateState(DevicePropertiesTemplate template) {
		try {
			if (StringUtils.isBlank(template.getId())) {
				logger.error("[updateState()->invalid: 模版id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("模版id不能为空");
			}
			if (StringUtils.isBlank(template.getManageState())) {
				logger.error("[updateState()->invalid: 目标状态不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("目标状态不能为空");
			}
			template.setEditorTime(DateUtils.formatDate(new Date()));
			logger.info(String.format("[updateState()->进入方法,参数:%s]", template));
			int num = devicePropertiesTemplateCrudRepository.updatePart(template);
			if (num > 0) {
				logger.info("[updateState()->success: 模版保存成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("模版启/停用成功", null);
			}
			return RpcResponseBuilder.buildErrorRpcResp("模版启/停用修改失败");
		} catch (Exception e) {
			logger.error("updateState()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> updateTime(String templateId) {
		try {
			if (StringUtils.isBlank(templateId)) {
				logger.error("[updateTime()->invalid: 模版id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("模版id不能为空");
			}
			DevicePropertiesTemplate template = new DevicePropertiesTemplate();
			template.setId(templateId);
			template.setEditorTime(DateUtils.formatDate(new Date()));
			logger.info(String.format("[updateTime()->进入方法,参数:%s]", template));
			int num = devicePropertiesTemplateCrudRepository.updatePart(template);
			if (num > 0) {
				return RpcResponseBuilder.buildSuccessRpcResp("模版修改时间更新成功", null);
			}
			return RpcResponseBuilder.buildSuccessRpcResp("模版修改时间更新失败", null);
		} catch (Exception e) {
			logger.error("updateTime()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
