/*
 * File name: FacilitiesTypeCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年8月29日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.run.common.util.UUIDUtil;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FacilitiesTypeCudRepository;
import com.run.locman.api.crud.service.FacilitiesTypeCudService;
import com.run.locman.api.entity.FacilitiesType;
import com.run.locman.api.query.service.FacilitiesTypeQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年8月29日
 */
@Transactional(rollbackFor = Exception.class)
public class FacilitiesTypeCudServiceImpl implements FacilitiesTypeCudService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesTypeCudRepository	facilitiesTypeCudRepository;

	@Autowired
	private FacilitiesTypeQueryService	facilitiesTypeQueryService;



	@Override
	public RpcResponse<FacilitiesType> updateFacilitiesType(FacilitiesType facilitiesType) {
		if (facilitiesType == null) {
			logger.debug("[updateFacilitiesType()->invalid: 设施类型对象不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设施类型信息：设施类型对象不能为空");
		}
		try {
			logger.info(String.format("[updateFacilitiesType()->进入方法,参数:%s]", facilitiesType));
			RpcResponse<Boolean> check = facilitiesTypeQueryService.checkFacilitiesTypeName(
					facilitiesType.getFacilityTypeAlias(), facilitiesType.getAccessSecret(), facilitiesType.getId());
			if (null == check || !check.isSuccess()) {
				logger.error("[insertFacilitesType()->invalid: 保存设施类型，重名校验失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("设施类型重名校验失败!!!");
			} else if (check.getSuccessValue()) {
				logger.error("[insertFacilitesType()->invalid: 设施类型重名,不能保存!]");
				return RpcResponseBuilder.buildErrorRpcResp("设施类型重名,不能保存,请修改后重试!!!");
			}
			
			int updateResult = facilitiesTypeCudRepository.updatePart(facilitiesType);
			if (updateResult > 0) {
				logger.debug("[updateFacilitiesType()->success: " + facilitiesType + "]");
				return RpcResponseBuilder.buildSuccessRpcResp("设施类型更新成功", facilitiesType);
			}
			logger.error("updateFacilitiesType()->设施类型更新失败");
			return RpcResponseBuilder.buildErrorRpcResp("设施类型更新失败");
		} catch (Exception e) {
			logger.error("updateFacilitiesType()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.FacilitiesTypeCudService#insertFacilitesType(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Map<String, Object>> insertFacilitesType(JSONObject facilitiesType) {
		if (facilitiesType == null || facilitiesType.isEmpty()) {
			logger.error("[insertFacilitesType()->invalid: 保存设施类型，参数不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("保存设施类型，参数不能为空!");
		}
		if (StringUtils.isBlank(facilitiesType.getString(FacilitiesContants.FACILITY_TYPE_BASE_ID))) {
			logger.error("[insertFacilitesType()->invalid: 保存设施类型，基础设施类型id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("保存设施类型，基础设施类型id不能为空!");
		}
		String facilityTypeAlias = facilitiesType.getString("facilityTypeAlias");
		if (StringUtils.isBlank(facilityTypeAlias)) {
			logger.error("[insertFacilitesType()->invalid: 保存设施类型，设施类型名称不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("保存设施类型，设施类型名称不能为空!");
		}
		String accessSecret = facilitiesType.getString("accessSecret");
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("[insertFacilitesType()->invalid: 保存设施类型，接入方秘钥不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("保存设施类型，接入方秘钥不能为空!");
		}
		if (StringUtils.isBlank(facilitiesType.getString(FacilitiesContants.CREATION_USER_ID))) {
			logger.error("[insertFacilitesType()->invalid: 保存设施类型，用户id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("保存设施类型，用户id不能为空!");
		}
		try {
			logger.info(String.format("[insertFacilitesType()->进入方法,参数:%s]", facilitiesType));
			// 添加设施类型时无id,所以传null
			RpcResponse<Boolean> check = facilitiesTypeQueryService.checkFacilitiesTypeName(facilityTypeAlias,
					accessSecret, null);
			if (null == check || !check.isSuccess()) {
				logger.error("[insertFacilitesType()->invalid: 保存设施类型，重名校验失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("设施类型重名校验失败!!!");
			} else if (check.getSuccessValue()) {
				logger.error("[insertFacilitesType()->invalid: 设施类型重名,不能保存!]");
				return RpcResponseBuilder.buildErrorRpcResp("设施类型重名,不能保存,请修改后重试!!!");
			}

			String uuid = UUIDUtil.getUUID().toString().replaceAll("-", "");
			facilitiesType.put("id", uuid);
			facilitiesType.put("manageState", "enabled");
			facilitiesType.put("creationTime", DateUtils.formatDate(new Date()));
			facilitiesType.put("editorTime", DateUtils.formatDate(new Date()));

			Map<String, Object> map = JSONObject.toJavaObject(facilitiesType, Map.class);
			logger.info(String.format("[insertFacilitesType()->即将保存设施类型,参数:%s]", map));
			int insertResult = facilitiesTypeCudRepository.insertFacilitesType(map);
			if (insertResult > 0) {
				logger.info("[insertFacilitesType()->success: " + facilitiesType + "]");
				return RpcResponseBuilder.buildSuccessRpcResp("保存设施类型成功", map);
			}
			logger.error("insertFacilitesType()->保存设施类型失败");
			return RpcResponseBuilder.buildErrorRpcResp("保存设施类型失败");
		} catch (Exception e) {
			logger.error("insertFacilitesType()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
