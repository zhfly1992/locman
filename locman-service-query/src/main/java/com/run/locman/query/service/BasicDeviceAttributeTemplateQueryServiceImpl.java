/*
 * File name: BasicDeviceAttributeTemplateQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年4月25日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.DevicePropertiesTemplate;
import com.run.locman.api.query.repository.BasicDeviceAttributeTemplateQueryRepository;
import com.run.locman.api.query.service.BasicDeviceAttributeTemplateQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description: 设备属性模板同步query实现
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月25日
 */

public class BasicDeviceAttributeTemplateQueryServiceImpl implements BasicDeviceAttributeTemplateQueryService {

	@Autowired
	private BasicDeviceAttributeTemplateQueryRepository	basicDeviceAttributeTemplateQueryRepository;

	private static final Logger							logger	= Logger.getLogger(CommonConstants.LOGKEY);



	/**
	 * @see com.run.locman.api.query.service.BasicDeviceAttributeTemplateQueryService#existBasicData()
	 */
	@Override
	public RpcResponse<Boolean> existBasicData(String accessSecret) {
		logger.info(String.format("[existBasicData()方法执行开始...,参数：【%s】]", accessSecret));

		try {

			if (StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[basicDeviceAttrbuteTemplate()->%s-%s]", PublicConstants.ACCESSSECRET,
						PublicConstants.BUSINESS_INVALID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[basicDeviceAttrbuteTemplate()->%s-%s]",
						PublicConstants.ACCESSSECRET, PublicConstants.BUSINESS_INVALID));
			}

			// 查询数据库判断是否存在数据 设备类型与模板关系表，模板表
			String existDevicePropertiesTemplate = basicDeviceAttributeTemplateQueryRepository
					.existDevicePropertiesTemplate(accessSecret);
			String existDeviceTypeTemplate = basicDeviceAttributeTemplateQueryRepository
					.existDeviceTypeTemplate(accessSecret);

			if (!StringUtils.isBlank(existDevicePropertiesTemplate) || !StringUtils.isBlank(existDeviceTypeTemplate)) {
				logger.error(String.format("[existBasicData()->%s->%s]", PublicConstants.PARAM_ERROR,
						PublicConstants.PARAM_EXIST));
				return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_EXIST, false);
			}
		} catch (Exception e) {
			logger.error("existBasicData()->exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		logger.info(String.format("[getAllDrollsByPage()方法执行结束!]"));
		return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, false);
	}



	/**
	 * @see com.run.locman.api.query.service.BasicDeviceAttributeTemplateQueryService#queryDevicePropertiesTemplateBasic()
	 */
	@Override
	public RpcResponse<List<DevicePropertiesTemplate>> queryDevicePropertiesTemplateBasic() {
		logger.info(String.format("[queryDevicePropertiesTemplateBasic()方法执行开始...]"));
		try {
			// 获取设备属性模板表(同步表)
			List<DevicePropertiesTemplate> queryDevicePropertiesTemplateBasic = basicDeviceAttributeTemplateQueryRepository
					.queryDevicePropertiesTemplateBasic();
			logger.info(String.format("[queryDevicePropertiesTemplateBasic()方法执行结束!"));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS,
					queryDevicePropertiesTemplateBasic);

		} catch (Exception e) {
			logger.error("queryDevicePropertiesTemplateBasic()->exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.BasicDeviceAttributeTemplateQueryService#queryDevicePropertiesBasicByTemplateId(java.lang.String)
	 */
	@Override
	public RpcResponse<List<DeviceProperties>> queryDevicePropertiesBasicByTemplateId(String templateId) {
		logger.info(String.format("[queryDevicePropertiesBasicByTemplateId()方法执行开始...,参数：【%s】]", templateId));
		try {

			if (StringUtils.isBlank(templateId)) {
				logger.error(String.format("[basicDeviceAttrbuteTemplate()->%s-%s]", "templateId",
						PublicConstants.BUSINESS_INVALID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[basicDeviceAttrbuteTemplate()->%s-%s]",
						"templateId", PublicConstants.BUSINESS_INVALID));
			}

			// 通过模板id获取设备属性
			List<DeviceProperties> queryDevicePropertiesBasicByTemplateId = basicDeviceAttributeTemplateQueryRepository
					.queryDevicePropertiesBasicByTemplateId(templateId);

			logger.info(String.format("[queryDevicePropertiesBasicByTemplateId()方法执行结束!"));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS,
					queryDevicePropertiesBasicByTemplateId);

		} catch (Exception e) {
			logger.error("queryDevicePropertiesBasicByTemplateId()->exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.BasicDeviceAttributeTemplateQueryService#findBaseDeviceTypeId(java.lang.String)
	 */
	@Override
	public RpcResponse<String> findBaseDeviceTypeId(String templateId) {
		logger.info(String.format("[findBaseDeviceTypeId()方法执行开始...,参数：【%s】]", templateId));

		try {
			if (StringUtils.isBlank(templateId)) {
				logger.error(String.format("[basicDeviceAttrbuteTemplate()->%s-%s]", "templateId",
						PublicConstants.BUSINESS_INVALID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[basicDeviceAttrbuteTemplate()->%s-%s]",
						"templateId", PublicConstants.BUSINESS_INVALID));
			}

			// 通过模板id获取设备类型id 查询同步表中的关系
			String findBaseDeviceTypeId = basicDeviceAttributeTemplateQueryRepository.findBaseDeviceTypeId(templateId);

			logger.info(String.format("[findBaseDeviceTypeId()方法执行结束!"));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, findBaseDeviceTypeId);

		} catch (Exception e) {
			logger.error("findBaseDeviceTypeId()->exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}

}
