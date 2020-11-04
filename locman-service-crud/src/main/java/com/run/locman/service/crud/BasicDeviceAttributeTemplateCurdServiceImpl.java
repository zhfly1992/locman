/*
 * File name: BasicDeviceAttributeTemplateCurdServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年4月23日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.BaseDataSynchronousStateCrudRepository;
import com.run.locman.api.crud.repository.BasicDeviceAttributeTemplateCurdRepository;
import com.run.locman.api.crud.service.BasicDeviceAttributeTemplateCurdService;
import com.run.locman.api.entity.BaseDataSynchronousState;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.DevicePropertiesTemplate;
import com.run.locman.api.entity.DeviceTypeTemplate;
import com.run.locman.api.query.service.BasicDeviceAttributeTemplateQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description: 同步设备属性模板impl
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月23日
 */
@Transactional(rollbackFor = Exception.class)
public class BasicDeviceAttributeTemplateCurdServiceImpl implements BasicDeviceAttributeTemplateCurdService {

	private static final Logger							logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private BasicDeviceAttributeTemplateCurdRepository	basicDeviceAttributeTemplateCurdRepository;

	@Autowired
	private BasicDeviceAttributeTemplateQueryService	basicDeviceAttributeTemplateQueryService;

	@Autowired
	private BaseDataSynchronousStateCrudRepository		baseDataSynchronousStateCrudRepository;



	/**
	 * @see com.run.locman.api.crud.service.BasicDeviceAttributeTemplateCurdService#basicDeviceAttrbuteTemplate(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> basicDeviceAttrbuteTemplate(String accessSecret) {

		try {
			logger.info(String.format("[basicDeviceAttrbuteTemplate()->进入方法,参数:accessSecret: %s]", accessSecret));

			if (StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[basicDeviceAttrbuteTemplate()->%s-%s]", PublicConstants.ACCESSSECRET,
						PublicConstants.BUSINESS_INVALID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[basicDeviceAttrbuteTemplate()->%s-%s]",
						PublicConstants.ACCESSSECRET, PublicConstants.BUSINESS_INVALID));
			}

			// 通过接入方查询是否是第一次同步(设备属性模板)
			BaseDataSynchronousState synchronousStateByAS = baseDataSynchronousStateCrudRepository
					.getSynchronousStateByAS(accessSecret);

			if (synchronousStateByAS != null && !synchronousStateByAS.getBaseDeviceTypeTemplate()) {
				logger.error("[basicDeviceAttrbuteTemplate()->该接入方已同步设备属性模板！");
				return RpcResponseBuilder.buildErrorRpcResp("该接入方已同步设备属性模板！");
			}

			// 校验数据设备属性模板表，以及设备属性表不存在数据才能进行同步
			RpcResponse<Boolean> existBasicData = basicDeviceAttributeTemplateQueryService.existBasicData(accessSecret);
			if (!existBasicData.isSuccess()) {
				logger.error(String.format("[basicDeviceAttrbuteTemplate()->%s]", existBasicData.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(existBasicData.getMessage());
			}

			if (existBasicData.getSuccessValue()) {
				logger.info(String.format("[basicDeviceAttrbuteTemplate()->%s]", PublicConstants.PARAM_EXIST));
				return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_EXIST, false);
			}

			// 获取设备属性模板表(同步表)
			RpcResponse<List<DevicePropertiesTemplate>> rpcTemplate = basicDeviceAttributeTemplateQueryService
					.queryDevicePropertiesTemplateBasic();

			if (!rpcTemplate.isSuccess()) {
				logger.error(String.format("[basicDeviceAttrbuteTemplate()->%s]", rpcTemplate.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(rpcTemplate.getMessage());
			}

			List<DevicePropertiesTemplate> queryDevicePropertiesTemplateBasic = rpcTemplate.getSuccessValue();

			for (DevicePropertiesTemplate devicePropertiesTemplate : queryDevicePropertiesTemplateBasic) {
				RpcResponse<Boolean> addDevicePropertiesAndRs = addDevicePropertiesAndRs(accessSecret, devicePropertiesTemplate);
				if (null != addDevicePropertiesAndRs) {
					return addDevicePropertiesAndRs;
				}

			}

			// 批量插入设备属性模板表(业务)
			basicDeviceAttributeTemplateCurdRepository
					.insertDevicePropertiesTemplate(queryDevicePropertiesTemplateBasic);

			// 改变同步表-默认只有第一次可以同步
			changeBaseDataSynchronousState(accessSecret);

			logger.info("[basicDeviceAttrbuteTemplate()->" + PublicConstants.PARAM_SUCCESS + "]");
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, true);

		} catch (Exception e) {
			logger.error("basicDeviceAttrbuteTemplate()->exception:", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<Boolean> addDevicePropertiesAndRs(String accessSecret, DevicePropertiesTemplate devicePropertiesTemplate) {
		// 获取设备模板id
		String uuId = UtilTool.getUuId();
		devicePropertiesTemplate.setAccessSecret(accessSecret);

		// 通过设备属性模板id查询设备类型id(同步表)
		RpcResponse<String> rpcDeviceType = basicDeviceAttributeTemplateQueryService
				.findBaseDeviceTypeId(devicePropertiesTemplate.getId());

		if (!rpcDeviceType.isSuccess()) {
			logger.error(String.format("[basicDeviceAttrbuteTemplate()->%s]", rpcDeviceType.getMessage()));
			return RpcResponseBuilder.buildErrorRpcResp(rpcDeviceType.getMessage());
		}
		String deviceTypeId = rpcDeviceType.getSuccessValue();

		//插入设备属性模板与设备类型关系表(业务表)
		getAndInsertDeviceTypeTemplate(accessSecret, uuId, deviceTypeId);

		// 通过设备模板id查询设备属性(同步表)
		RpcResponse<List<DeviceProperties>> rpcDevicePro = basicDeviceAttributeTemplateQueryService
				.queryDevicePropertiesBasicByTemplateId(devicePropertiesTemplate.getId());

		if (!rpcDevicePro.isSuccess()) {
			logger.error(String.format("[basicDeviceAttrbuteTemplate()->%s]", rpcDevicePro.getMessage()));
			return RpcResponseBuilder.buildErrorRpcResp(rpcDevicePro.getMessage());
		}

		List<DeviceProperties> queryDevicePropertiesBasicByTemplateId = rpcDevicePro.getSuccessValue();

		// 批量插入设备属性表
		for (DeviceProperties deviceProperties : queryDevicePropertiesBasicByTemplateId) {
			deviceProperties.setId(UtilTool.getUuId());
			deviceProperties.setTemplateId(uuId);
			deviceProperties.setCreationTime(DateUtils.formatDate(new Date()));
		}

		// 批量插入设备属性表(业务)
		basicDeviceAttributeTemplateCurdRepository
				.insertDeviceProperties(queryDevicePropertiesBasicByTemplateId);

		devicePropertiesTemplate.setId(uuId);
		devicePropertiesTemplate.setCreationTime(DateUtils.formatDate(new Date()));
		devicePropertiesTemplate.setEditorTime(DateUtils.formatDate(new Date()));
		return null;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private void changeBaseDataSynchronousState(String accessSecret) throws Exception {
		BaseDataSynchronousState modelData = new BaseDataSynchronousState();
		modelData.setAccessSecret(accessSecret);
		modelData.setBaseDeviceTypeTemplate(false);
		baseDataSynchronousStateCrudRepository.updatePart(modelData);
	}



	/**
	  * 
	  * @Description: 插入设备属性模板与设备类型关系表(业务表)
	  * @param 
	  * @return
	  */
	
	private void getAndInsertDeviceTypeTemplate(String accessSecret, String uuId, String deviceTypeId) {
		// 插入设备属性模板与设备类型关系表(业务表)
		DeviceTypeTemplate deviceTypeTemplate = new DeviceTypeTemplate();
		deviceTypeTemplate.setId(UtilTool.getUuId());
		deviceTypeTemplate.setDevicePropertyTemplateId(uuId);
		deviceTypeTemplate.setDeviceTypePropertyConfigId(deviceTypeId);
		deviceTypeTemplate.setAccessSecret(accessSecret);
		basicDeviceAttributeTemplateCurdRepository.insetDeviceTypeTemplate(deviceTypeTemplate);
	}

}
