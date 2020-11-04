/*
 * File name: DistributionPowersRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月14日 ... ...
 * ...
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
import com.google.common.collect.Maps;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.DistributionPowers;
import com.run.locman.api.entity.FacilitiesType;
import com.run.locman.api.query.service.DistributionPowersQueryService;
import com.run.locman.api.query.service.FacilitiesTypeQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DistributionPowersConstants;
import com.run.usc.api.base.util.ParamChecker;

/**
 * @Description: 查询分权分域配置
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */
@Service
public class DistributionPowersRestQueryService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private DistributionPowersQueryService	distributionPowersQueryService;
	@Autowired
	private FacilitiesTypeQueryService		facilitiesTypeQueryService;



	public Result<PageInfo<Map<String, Object>>> getDistributionPowersListPage(String params) {
		logger.info(String.format("[getDistributionPowersListPage()->request params:%s]", params));
		if (ParamChecker.isNotMatchJson(params)) {
			logger.error("[getDistributionPowersListPage()->error:传入参数必须为json格式]");
			return ResultBuilder.invalidResult();
		}
		JSONObject paramJson = JSONObject.parseObject(params);
		String accessSecret = paramJson.getString(DistributionPowersConstants.ACCESSSECRET);
		String pageSizeStr = paramJson.getString(CommonConstants.PAGE_SIZE);
		int pageSize = Integer.valueOf(pageSizeStr);
		String pageNumStr = paramJson.getString(CommonConstants.PAGE_NUM);
		Result<Object> paramCheck = paramCheck(paramJson, pageSizeStr, pageNumStr);
		if (null != paramCheck) {
			return ResultBuilder.failResult(paramCheck.getResultStatus().getResultMessage());
		}
		Map<String, String> map = Maps.newHashMap();
		int pageNum = Integer.valueOf(pageNumStr);

		mapPut(paramJson, map);

		// 参数验证通过
		try {
			RpcResponse<PageInfo<Map<String, Object>>> distributionPowersListPage = distributionPowersQueryService
					.getDistributionPowersListPage(accessSecret, pageNum, pageSize, map);
			if (!distributionPowersListPage.isSuccess()) {
				logger.error(String.format("[getDistributionPowersListPage()->fail:%s]",
						distributionPowersListPage.getMessage()));
				return ResultBuilder.failResult(distributionPowersListPage.getMessage());
			}
			PageInfo<Map<String, Object>> info = distributionPowersListPage.getSuccessValue();
			List<Map<String, Object>> distributionPowersList = info.getList();
			// 循环封装名称信息
			for (Map<String, Object> distributionPowers : distributionPowersList) {
				String facilityTypeId = distributionPowers.get("facilityTypeId") + "";
				// -----------封装设施类型名称
				RpcResponse<FacilitiesType> queryFacilitiesByIdResult = facilitiesTypeQueryService
						.queryFacilitiesById(facilityTypeId);
				if (!queryFacilitiesByIdResult.isSuccess()) {
					logger.error("[getDistributionPowersListPage()->fail:设施类型信息查询失败]");
					return ResultBuilder.failResult("设施类型信息查询失败");
				}
				// 设施类型被删除
				if (queryFacilitiesByIdResult.getSuccessValue() == null) {
					distributionPowers.put("facilityTypeName", "");
				} else {
					distributionPowers.put("facilityTypeName",
							queryFacilitiesByIdResult.getSuccessValue().getFacilityTypeAlias());
				}
			}

			info.setList(distributionPowersList);
			logger.info(String.format("[getDistributionPowersListPage()->success:%s]",
					distributionPowersListPage.getMessage()));
			return ResultBuilder.successResult(info, distributionPowersListPage.getMessage());
		} catch (Exception e) {
			logger.error("getDistributionPowersListPage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * @Description:
	 * @param paramJson
	 * @param map
	 */

	private void mapPut(JSONObject paramJson, Map<String, String> map) {
		if (paramJson.containsKey(DistributionPowersConstants.FACILITYTYPEID)) {
			map.put(DistributionPowersConstants.FACILITYTYPEID,
					paramJson.getString(DistributionPowersConstants.FACILITYTYPEID));
		}
		if (paramJson.containsKey(DistributionPowersConstants.ORGANIZATIONID)) {
			map.put(DistributionPowersConstants.ORGANIZATIONID,
					paramJson.getString(DistributionPowersConstants.ORGANIZATIONID));
		}
		if (paramJson.containsKey(CommonConstants.USERNAME)) {
			map.put(CommonConstants.USERNAME, paramJson.getString(CommonConstants.USERNAME));
		}
		map.put(CommonConstants.MANAGESTATE, paramJson.getString(CommonConstants.MANAGESTATE));
	}



	/**
	 * @Description:
	 * @param paramJson
	 * @param pageSizeStr
	 * @param pageNumStr
	 */

	private Result<Object> paramCheck(JSONObject paramJson, String pageSizeStr, String pageNumStr) {
		if (!paramJson.containsKey(DistributionPowersConstants.ACCESSSECRET)) {
			logger.error("[getDistributionPowersListPage()->error:传入参数 无accessSecret]");
			return ResultBuilder.noBusinessResult();
		}
		if (!paramJson.containsKey(CommonConstants.PAGE_SIZE)) {
			logger.error("[getDistributionPowersListPage()->error:传入参数 无pageSize]");
			return ResultBuilder.noBusinessResult();
		}
		if (!StringUtils.isNumeric(pageSizeStr)) {
			logger.error("[getDistributionPowersListPage()->error:pageSize传入数字异常]");
			return ResultBuilder.failResult("查询分页信息,传入数字异常!");
		}
		if (!paramJson.containsKey(CommonConstants.PAGE_NUM)) {
			logger.error("[getDistributionPowersListPage()->error:传入参数 无pageNum]");
			return ResultBuilder.noBusinessResult();
		}
		if (!StringUtils.isNumeric(pageNumStr)) {
			logger.error("[getDistributionPowersListPage()->error:pageNum传入数字异常]");
			return ResultBuilder.failResult("查询分页信息,传入数字异常!");
		}
		return null;
	}



	public Result<DistributionPowers> getById(String id) {
		if (id == null || "".equals(id)) {
			return ResultBuilder.noBusinessResult();
		}
		try {
			RpcResponse<DistributionPowers> distributionPowers = distributionPowersQueryService
					.getDistributionPowersById(id);
			if (!distributionPowers.isSuccess()) {
				return ResultBuilder.failResult(distributionPowers.getMessage());
			}
			return ResultBuilder.successResult(distributionPowers.getSuccessValue(), distributionPowers.getMessage());
		} catch (Exception e) {
			logger.error("getById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
