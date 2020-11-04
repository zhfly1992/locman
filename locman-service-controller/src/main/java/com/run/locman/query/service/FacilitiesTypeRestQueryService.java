/*
 * File name: FacilitiesTypeRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年08月09日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.FacilitiesType;
import com.run.locman.api.query.service.DeviceTypeQueryService;
import com.run.locman.api.query.service.FacilitiesTypeQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 设施类型查询
 * @author: 田明
 * @version: 1.0, 2017年08月09日
 */
@Service
public class FacilitiesTypeRestQueryService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private FacilitiesTypeQueryService	facilitiesTypeQueryService;

	@Autowired
	private DeviceTypeQueryService		deviceTypeQueryService;



	public Result<List<FacilitiesType>> queryAllFacilitiesType(String params) {
		logger.info(String.format("[queryAllFacilitiesType()->request params:%s]", params));
		try {
			if (ParamChecker.isNotMatchJson(params)) {
				logger.error("[queryAllFacilitiesType()->error:参数必须为json格式]");
				return ResultBuilder.invalidResult();
			}
			JSONObject accessSecretJson = JSONObject.parseObject(params);
			if (!accessSecretJson.containsKey(CommonConstants.ACCESSSECRET)) {
				logger.error("[queryAllFacilitiesType()->error:参数无accessSecret]");
				return ResultBuilder.noBusinessResult();
			}
			String accessSecret = accessSecretJson.getString(CommonConstants.ACCESSSECRET);
			RpcResponse<List<FacilitiesType>> ftList = facilitiesTypeQueryService.findAllFacilities(accessSecret);
			if (ftList.isSuccess()) {
				logger.info(String.format("[queryAllFacilitiesType()->success:%s]", ftList.getMessage()));
				return ResultBuilder.successResult(ftList.getSuccessValue(), ftList.getMessage());
			} else {
				logger.error(String.format("[queryAllFacilitiesType()->fail:%s]", ftList.getMessage()));
				return ResultBuilder.failResult(ftList.getMessage());
			}
		} catch (Exception e) {
			logger.error("queryAllFacilitiesType()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据设施类型ID 查询设施类型详情
	 * @param params
	 *            设施类型ID {"id":""}
	 * @return
	 */
	public Result<FacilitiesType> getById(String params) {
		logger.info(String.format("[getById()->request params:%s]", params));
		if (ParamChecker.isNotMatchJson(params)) {
			logger.error("[getById()->error:参数必须为json格式]");
			return ResultBuilder.invalidResult();
		}
		JSONObject idJson = JSONObject.parseObject(params);
		if (!idJson.containsKey(CommonConstants.ID)) {
			logger.error("[getById()->error:参数中没id]");
			return ResultBuilder.noBusinessResult();
		}

		String id = idJson.getString(CommonConstants.ID);
		try {
			RpcResponse<FacilitiesType> queryFacilitiesByIdResult = facilitiesTypeQueryService.queryFacilitiesById(id);
			if (queryFacilitiesByIdResult != null && queryFacilitiesByIdResult.isSuccess()) {
				logger.info(String.format("[getById()->success:%s]", queryFacilitiesByIdResult.getMessage()));
				return ResultBuilder.successResult(queryFacilitiesByIdResult.getSuccessValue(), "查询成功");
			}
			logger.error("[getById()->fail:设施类型查询失败]");
			return ResultBuilder.failResult("设施类型查询失败");
		} catch (Exception e) {
			logger.error("getById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description: 查询设施类型列表
	 * @param param
	 *            查询参数{"accessSecret":"", "pageNum":"", "pageSize":"",
	 *            "searchKey":""}
	 * @return
	 */
	public Result<PageInfo<FacilitiesType>> getFacilitiesPage(String param) {
		logger.info(String.format("[getFacilitiesPage()->request params:%s]", param));
		try {
			if (ParamChecker.isNotMatchJson(param)) {
				logger.error("[getFacilitiesPage()->error:参数必须为json格式]");
				return ResultBuilder.invalidResult();
			}
			JSONObject paramJson = JSONObject.parseObject(param);

			if (!paramJson.containsKey(CommonConstants.ACCESSSECRET)) {
				logger.error("[getFacilitiesPage()->error:参数无accessSecret]");
				return ResultBuilder.noBusinessResult();
			}
			if (!paramJson.containsKey(CommonConstants.PAGE_NUM)) {
				logger.error("[getFacilitiesPage()->error:参数无pageNum]");
				return ResultBuilder.noBusinessResult();
			}
			if (!paramJson.containsKey(CommonConstants.PAGE_SIZE)) {
				logger.error("[getFacilitiesPage()->error:参数无pageSize]");
				return ResultBuilder.noBusinessResult();
			}

			String accessSecret = paramJson.getString(CommonConstants.ACCESSSECRET);
			if (!StringUtils.isNumeric(paramJson.getString(CommonConstants.PAGE_NUM))) {
				logger.error("[getFacilitiesPage()->error:pageNum必须为数字]");
				return ResultBuilder.invalidResult();
			}
			if (!StringUtils.isNumeric(paramJson.getString(CommonConstants.PAGE_SIZE))) {
				logger.error("[getFacilitiesPage()->error:pageSize必须为数字]");
				return ResultBuilder.invalidResult();
			}
			int pageNum = paramJson.getIntValue(CommonConstants.PAGE_NUM);
			int pageSize = paramJson.getIntValue(CommonConstants.PAGE_SIZE);

			Map<String, String> map = Maps.newHashMap();
			if (paramJson.containsKey(CommonConstants.SEARCH_KEY)) {
				map.put(CommonConstants.SEARCH_KEY, paramJson.getString(CommonConstants.SEARCH_KEY));
			}
			map.put(CommonConstants.MANAGESTATE, paramJson.getString(CommonConstants.MANAGESTATE));

			RpcResponse<PageInfo<FacilitiesType>> facilitiesPage = facilitiesTypeQueryService
					.getFacilitiesPage(accessSecret, pageNum, pageSize, map);
			if (facilitiesPage != null && facilitiesPage.isSuccess()) {
				logger.info(String.format("[getFacilitiesPage()->success:%s]", facilitiesPage.getMessage()));
				return ResultBuilder.successResult(facilitiesPage.getSuccessValue(), "查询成功");
			}
			logger.error("[getFacilitiesPage()->fail:查询失败]");
			return ResultBuilder.failResult("查询失败");
		} catch (Exception e) {
			logger.error("getFacilitiesPage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Boolean> checkFacilitiesTypeName(String param) {
		logger.info(String.format("[checkFacilitiesTypeName()->request params:%s]", param));
		if (ParamChecker.isNotMatchJson(param)) {
			logger.error("[checkFacilitiesTypeName()->error:参数必须为json格式]");
			return ResultBuilder.invalidResult();
		}
		JSONObject json = JSONObject.parseObject(param);
		String facilitiesTypeName = json.getString("facilityTypeAlias");
		String accessSecret = json.getString("accessSecret");
		String facilityTypeId = json.getString("id");
		try {
			RpcResponse<Boolean> result = facilitiesTypeQueryService.checkFacilitiesTypeName(facilitiesTypeName,
					accessSecret, facilityTypeId);
			if (result == null) {
				logger.error("[checkFacilitiesTypeName()->fail:设施类型重名校验失败,返回值为null]");
				return ResultBuilder.failResult("设施类型重名校验失败");
			} else if (!result.isSuccess() || result.getSuccessValue() == null) {
				logger.error(String.format("[checkFacilitiesTypeName()->fail:%s]", result.getMessage()));
				return ResultBuilder.failResult(result.getMessage());
			} else {
				logger.info(String.format("[checkFacilitiesTypeName()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), "查询成功");
			}

		} catch (Exception e) {
			logger.error("checkFacilitiesTypeName()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public Result<List<Map<String, String>>> findAllFacilitiesTypeAndNum(String accessSecret) {
		logger.info(String.format("[findAllFacilitiesTypeAndNum()->request accessSecret:%s]", accessSecret));
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("[findAllFacilitiesTypeAndNum()->error:参数不能为空]");
			return ResultBuilder.emptyResult();
		}

		try {
			RpcResponse<List<Map<String, String>>> result = facilitiesTypeQueryService
					.findAllFacilitiesTypeAndNum(accessSecret);
			if (result == null) {
				logger.error("[findAllFacilitiesTypeAndNum()->fail:查询接入方设施类型及数量失败,返回值为null]");
				return ResultBuilder.failResult("查询接入方设施类型及数量失败");
			} else if (!result.isSuccess() || result.getSuccessValue() == null) {
				logger.error(String.format("[findAllFacilitiesTypeAndNum()->fail:%s]", result.getMessage()));
				return ResultBuilder.failResult(result.getMessage());
			} else {
				logger.info(String.format("[findAllFacilitiesTypeAndNum()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), "查询成功");
			}

		} catch (Exception e) {
			logger.error("findAllFacilitiesTypeAndNum()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public Result<JSONObject> findAllFacTypeAndDeviceTypeNum(String accessSecret) {
		logger.info(String.format("[findAllFacTypeAndDeviceTypeNum()->request accessSecret:%s]", accessSecret));
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("[findAllFacTypeAndDeviceTypeNum()->error:参数不能为空]");
			return ResultBuilder.emptyResult();
		}

		try {

			JSONObject resultJson = new JSONObject();
			logger.error("123开始查询" + DateUtils.stampToDate(System.currentTimeMillis() + ""));
			RpcResponse<List<Map<String, Object>>> result = facilitiesTypeQueryService
					.findAllFacTypeAndDeviceTypeNum(accessSecret);
			if (result.isSuccess() && null != result.getSuccessValue() && !result.getSuccessValue().isEmpty()) {
				resultJson.put("facilityType", result.getSuccessValue());
			} else {
				logger.error(String.format("[findAllFacTypeAndDeviceTypeNum()->fail:%s]", result.getMessage()));
				resultJson.put("facilityType", Lists.newArrayList());
			}
			
			logger.info(String.format("[findAllFacTypeAndDeviceTypeNum()->success:%s ]", result.getMessage()));
			return ResultBuilder.successResult(resultJson, "查询成功");

		} catch (Exception e) {
			logger.error("findAllFacTypeAndDeviceTypeNum()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
