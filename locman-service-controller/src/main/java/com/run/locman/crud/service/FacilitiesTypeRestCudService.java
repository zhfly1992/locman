/*
 * File name: FacilitiesRestCudService.java
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

package com.run.locman.crud.service;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.FacilitiesTypeCudService;
import com.run.locman.api.entity.FacilitiesType;
import com.run.locman.api.query.service.FacilitiesTypeQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * @Description: 设施类型修改
 * @author: qulong
 * @version: 1.0, 2017年8月29日
 */
@Service
public class FacilitiesTypeRestCudService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesTypeCudService	facilitiesTypeCudService;

	@Autowired
	private FacilitiesTypeQueryService	facilitiesTypeQueryService;

	@Autowired
	private UserBaseQueryService		userQueryRpcService;



	/**
	 * 
	 * @Description: 改变设施类型的管理状态
	 * @param state
	 *            管理状态(enabled / disabled) 参数格式{"id":"", "state":"",
	 *            "userId":""}
	 * @return
	 */
	public Result<FacilitiesType> changeState(String param) {
		logger.info(String.format("[changeState()->request params:%s]", param));
		if (ParamChecker.isBlank(param)) {
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(param)) {
			return ResultBuilder.invalidResult();
		}
		JSONObject paramsJson = JSONObject.parseObject(param);
		if (!paramsJson.containsKey(CommonConstants.ID)) {
			return ResultBuilder.noBusinessResult();
		}
		String id = paramsJson.getString(CommonConstants.ID);
		if (!paramsJson.containsKey(CommonConstants.STATE)) {
			return ResultBuilder.noBusinessResult();
		}
		String state = paramsJson.getString(CommonConstants.STATE);

		if (!paramsJson.containsKey(CommonConstants.USERID)) {
			return ResultBuilder.noBusinessResult();
		}
		String userId = paramsJson.getString(CommonConstants.USERID);

		// 校验设施类型是否存在
		RpcResponse<FacilitiesType> queryFacilitiesByIdResult = facilitiesTypeQueryService.queryFacilitiesById(id);
		if (queryFacilitiesByIdResult == null || !queryFacilitiesByIdResult.isSuccess()) {
			logger.error("[changeState()->fail:设施查询失败]");
			return ResultBuilder.failResult("设施查询失败");
		}
		FacilitiesType facilitiesType = queryFacilitiesByIdResult.getSuccessValue();
		if (facilitiesType == null) {
			logger.error("[changeState()->fail:该设施不存在]");
			return ResultBuilder.failResult("该设施不存在");
		}
		facilitiesType.setManageState(state);

		try {
			@SuppressWarnings("rawtypes")
			RpcResponse userByUserIdResult = userQueryRpcService.getUserByUserId(userId);
			if (userByUserIdResult == null || !userByUserIdResult.isSuccess()) {
				logger.error("[changeState()->fail:用户信息查询失败]");
				return ResultBuilder.failResult("用户信息查询失败");
			}
			facilitiesType.setEditorUserId(userId);
			facilitiesType.setEditorTime(DateUtils.formatDate(new Date()));
			RpcResponse<FacilitiesType> updateFacilitiesTypeResult = facilitiesTypeCudService
					.updateFacilitiesType(facilitiesType);
			if (updateFacilitiesTypeResult == null || !updateFacilitiesTypeResult.isSuccess()) {
				logger.error("[changeState()->fail:状态更新失败]");
				return ResultBuilder.failResult("状态更新失败");
			}
			logger.info(String.format("[changeState()->success:%s]", updateFacilitiesTypeResult.getMessage()));
			return ResultBuilder.successResult(facilitiesType, updateFacilitiesTypeResult.getMessage());
		} catch (Exception e) {
			logger.error("changeState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description: 修改设施类型的别名
	 * @param param
	 *            设施类型的别名：{"id":"", "facilityTypeAlias":"", "userId":""}
	 * @return
	 */
	public Result<FacilitiesType> updateFacilityTypeAlias(String param) {
		logger.info(String.format("[updateFacilityTypeAlias()->request params:%s]", param));
		if (ParamChecker.isBlank(param)) {
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(param)) {
			return ResultBuilder.invalidResult();
		}
		JSONObject paramsJson = JSONObject.parseObject(param);
		if (!paramsJson.containsKey(CommonConstants.ID)) {
			return ResultBuilder.noBusinessResult();
		}
		String id = paramsJson.getString(CommonConstants.ID);
		if (!paramsJson.containsKey(CommonConstants.FACILITYTYPEALIAS)) {
			return ResultBuilder.noBusinessResult();
		}
		String facilityTypeAlias = paramsJson.getString(CommonConstants.FACILITYTYPEALIAS);

		if (!paramsJson.containsKey(CommonConstants.USERID)) {
			return ResultBuilder.noBusinessResult();
		}
		if (StringUtils.isBlank(paramsJson.getString(CommonConstants.FACILITYTYPEBASEID))) {
			return ResultBuilder.noBusinessResult();
		}
		String userId = paramsJson.getString(CommonConstants.USERID);

		// 校验设施类型是否存在
		RpcResponse<FacilitiesType> queryFacilitiesByIdResult = facilitiesTypeQueryService.queryFacilitiesById(id);
		if (queryFacilitiesByIdResult == null || !queryFacilitiesByIdResult.isSuccess()) {
			logger.error("[updateFacilityTypeAlias()->fail:设施类型查询失败]");
			return ResultBuilder.failResult("设施类型查询失败");
		}
		FacilitiesType facilitiesType = queryFacilitiesByIdResult.getSuccessValue();
		if (facilitiesType == null) {
			logger.error("[updateFacilityTypeAlias()->fail:该设施类型不存在]");
			return ResultBuilder.failResult("该设施类型不存在");
		}
		facilitiesType.setFacilityTypeAlias(facilityTypeAlias);
		try {
			@SuppressWarnings("rawtypes")
			RpcResponse userByUserIdResult = userQueryRpcService.getUserByUserId(userId);
			if (userByUserIdResult == null || !userByUserIdResult.isSuccess()) {
				logger.error("[updateFacilityTypeAlias()->fail:用户信息查询失败]");
				return ResultBuilder.failResult("用户信息查询失败");
			}
			facilitiesType.setEditorUserId(userId);
			facilitiesType.setFacilityTypeBaseId(paramsJson.getString("facilityTypeBaseId"));
			facilitiesType.setRemark(paramsJson.getString("remark"));
			facilitiesType.setEditorTime(DateUtils.formatDate(new Date()));
			RpcResponse<FacilitiesType> updateFacilitiesTypeResult = facilitiesTypeCudService
					.updateFacilitiesType(facilitiesType);
			if (updateFacilitiesTypeResult == null) {
				logger.error("[updateFacilityTypeAlias()->fail:设施类型别名修改失败]");
				return ResultBuilder.failResult("设施类型别名修改失败");
			} else if (!updateFacilitiesTypeResult.isSuccess()) {
				logger.error("[updateFacilityTypeAlias()->fail:设施类型别名修改失败："+ updateFacilitiesTypeResult.getMessage() +"]");
				return ResultBuilder.failResult(updateFacilitiesTypeResult.getMessage());
			}
			logger.info(String.format("[updateFacilityTypeAlias()->success:%s]", updateFacilitiesTypeResult.getMessage()));
			return ResultBuilder.successResult(facilitiesType, updateFacilitiesTypeResult.getMessage());
		} catch (Exception e) {
			logger.error("updateFacilityTypeAlias()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> insertFacilityType(String param) {
		logger.info(String.format("[insertFacilityType()->request params:%s]", param));
		// 参数验证
		if (ParamChecker.isBlank(param)) {
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(param)) {
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsJson = JSONObject.parseObject(param);
			RpcResponse<Map<String, Object>> insertResult = facilitiesTypeCudService.insertFacilitesType(paramsJson);
			if (insertResult == null) {
				logger.error("insertFacilityType()->保存设施类型失败!");
				return ResultBuilder.failResult("[insertFacilityType()---保存设施类型失败!");
			}else if (!insertResult.isSuccess()) {
				logger.error("insertFacilityType()->保存设施类型失败!");
				return ResultBuilder.failResult(String.format("[insertFacilityType()->fail:%s]", insertResult.getMessage()));
			}
			logger.info("insertFacilityType()->success:保存设施类型成功!");
			return ResultBuilder.successResult(insertResult.getSuccessValue(), "[insertFacilityType()---保存设施类型成功!");
		} catch (Exception e) {
			logger.error("updateFacilityTypeAlias()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
