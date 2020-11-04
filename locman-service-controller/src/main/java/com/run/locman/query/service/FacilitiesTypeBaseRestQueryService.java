/*
 * File name: FacilitiesTypeBaseRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年8月31日 ... ...
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
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.FacilitiesTypeBase;
import com.run.locman.api.query.service.FacilitiesTypeBaseQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 查询基础设施类型
 * @author: qulong
 * @version: 1.0, 2017年8月31日
 */

@Service
public class FacilitiesTypeBaseRestQueryService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesTypeBaseQueryService	facilitiesTypeBaseQueryService;



	/**
	 * 
	 * @Description: 根据主键ID 查询基础设施类型
	 * @param params
	 *            查询参数 {"id":""}
	 * @return
	 */
	public Result<FacilitiesTypeBase> getById(String params) {
		if (ParamChecker.isNotMatchJson(params)) {
			logger.error(String.format("[getById()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		JSONObject idJson = JSONObject.parseObject(params);
		if (!idJson.containsKey(CommonConstants.ID)) {
			logger.error("[getById()->error:参数必须包含id]");
			return ResultBuilder.noBusinessResult();
		}

		try {
			String id = idJson.getString(CommonConstants.ID);
			RpcResponse<FacilitiesTypeBase> facilitiesTypeBase = facilitiesTypeBaseQueryService
					.getFacilitiesTypeBaseById(id);
			if (facilitiesTypeBase != null && facilitiesTypeBase.isSuccess()) {
				logger.info("[getById()->success]");
				return ResultBuilder.successResult(facilitiesTypeBase.getSuccessValue(), "查询成功");
			}
			logger.error("[getById()->fail:查询失败]");
			return ResultBuilder.failResult("查询失败");
		} catch (Exception e) {
			logger.error("getById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description: 分页查询所有基础设施类型
	 * @param params
	 *            查询参数 {"pageNum":"", "pageSize":"", "searchKey":""}
	 * @return
	 */
	public Result<PageInfo<FacilitiesTypeBase>> getFacilitiesTypeBasePage(String params) {
		if (ParamChecker.isNotMatchJson(params)) {
			logger.error(
					String.format("[getFacilitiesTypeBasePage()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramJson = JSONObject.parseObject(params);
			if (!paramJson.containsKey(CommonConstants.PAGE_NUM)) {
				logger.error(String.format("[getFacilitiesTypeBasePage()->error:%s--%s]",
						LogMessageContants.NO_PARAMETER_EXISTS, CommonConstants.PAGE_NUM));
				return ResultBuilder.noBusinessResult();
			}
			if (!paramJson.containsKey(CommonConstants.PAGE_SIZE)) {
				logger.error(String.format("[getFacilitiesTypeBasePage()->error:%s--%s]",
						LogMessageContants.NO_PARAMETER_EXISTS, CommonConstants.PAGE_SIZE));
				return ResultBuilder.noBusinessResult();
			}
			if (!StringUtils.isNumeric(paramJson.getString(CommonConstants.PAGE_NUM))) {
				logger.error(String.format("[getFacilitiesTypeBasePage()->error:%s--%s]",
						LogMessageContants.PARAMETERS_OF_ILLEGAL, CommonConstants.PAGE_NUM));
				return ResultBuilder.invalidResult();
			}
			if (!StringUtils.isNumeric(paramJson.getString(CommonConstants.PAGE_SIZE))) {
				logger.error(String.format("[getFacilitiesTypeBasePage()->error:%s--%s]",
						LogMessageContants.PARAMETERS_OF_ILLEGAL, CommonConstants.PAGE_SIZE));
				return ResultBuilder.invalidResult();
			}
			int pageNum = paramJson.getIntValue(CommonConstants.PAGE_NUM);
			int pageSize = paramJson.getIntValue(CommonConstants.PAGE_SIZE);
			Map<String, String> map = Maps.newHashMap();
			if (paramJson.containsKey(CommonConstants.SEARCH_KEY)) {
				map.put(CommonConstants.SEARCH_KEY, paramJson.getString(CommonConstants.SEARCH_KEY));
			}
			RpcResponse<PageInfo<FacilitiesTypeBase>> facilitiesTypeBasePage = facilitiesTypeBaseQueryService
					.getFacilitiesTypeBasePage(pageNum, pageSize, map);
			if (facilitiesTypeBasePage != null && facilitiesTypeBasePage.isSuccess()) {
				logger.info("[getFacilitiesTypeBasePage()->success]");
				return ResultBuilder.successResult(facilitiesTypeBasePage.getSuccessValue(), "查询成功");
			}
			logger.error("[getFacilitiesTypeBasePage()->fail:查询失败]");
			return ResultBuilder.failResult("查询失败");
		} catch (Exception e) {
			logger.error("getFacilitiesTypeBasePage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	public Result<List<Map<String, Object>>> getAllFacilitiesTypeBase() {
		try {
			RpcResponse<List<Map<String, Object>>> result = facilitiesTypeBaseQueryService.findAllFacilitiesTypeBase();
			if (!result.isSuccess()) {
				logger.error("getFacilitiesTypeBasePage()->fail:获取基础设施类型集合失败!");
				return ResultBuilder.failResult("获取基础设施类型集合失败!");
			}
			logger.error("getFacilitiesTypeBasePage()->fail:获取基础设施类型集合成功!");
			return ResultBuilder.successResult(result.getSuccessValue(), "获取基础设施类型集合成功!");
		} catch (Exception e) {
			logger.error("getFacilitiesTypeBasePage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}

}
