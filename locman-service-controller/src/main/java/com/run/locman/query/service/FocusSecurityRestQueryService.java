/*
 * File name: FocusSecurityRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 钟滨远 2020年4月28日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.FocusSecurityQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;

/**
 * @Description:
 * @author: 钟滨远
 * @version: 1.0, 2020年4月28日
 */
@Service
public class FocusSecurityRestQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FocusSecurityQueryService	focusSecurityQueryService;
	@Autowired
	private HttpServletRequest		request;


	public Result<PageInfo<Map<String, Object>>> getFocusSecurityInfoPage(JSONObject paramsObject) {
		logger.info(String.format("getFocusSecurityInfoPage()->方法执行开始paramsObject：%s", paramsObject.toString()));

		try {
			if (!paramsObject.containsKey(CommonConstants.PAGE_NUM)) {
				logger.error("[getFocusSecurityInfoPage()->error:参数无pageNum]");
				return ResultBuilder.emptyResult();
			}
			if (!paramsObject.containsKey(CommonConstants.PAGE_SIZE)) {
				logger.error("[getFocusSecurityInfoPage()->error:参数无pageSize]");
				return ResultBuilder.emptyResult();
			}

			if (!paramsObject.containsKey("status")) {
				logger.error("[getFocusSecurityInfoPage()->error:参数无status]");
				return ResultBuilder.emptyResult();
			}
			if (!StringUtils.isNumeric(paramsObject.getString(CommonConstants.PAGE_NUM))
					|| !StringUtils.isNumeric(paramsObject.getString(CommonConstants.PAGE_SIZE))) {
				logger.error("[getFocusSecurityInfoPage()->error:pageNum和pageSize必须为数字]");
				return ResultBuilder.invalidResult();
			}
			int pageNum = Integer.parseInt(paramsObject.getString("pageNum"));
			int pageSize = Integer.parseInt(paramsObject.getString("pageSize"));
			String status = paramsObject.getString("status");
			String token=request.getHeader("Token");
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("pageNum", pageNum);
			queryMap.put("pageSize", pageSize);
			queryMap.put("status", status);
			queryMap.put("accessSecret", paramsObject.getString("accessSecret"));
			queryMap.put("token", token);
			RpcResponse<PageInfo<Map<String, Object>>> focusSecurityInfoPage = focusSecurityQueryService
					.getFocusSecurityInfoPage(queryMap);
			if (focusSecurityInfoPage.isSuccess()) {
				logger.info(
						String.format("[getFocusSecurityInfoPage()->success:%s]", focusSecurityInfoPage.getMessage()));
				return ResultBuilder.successResult(focusSecurityInfoPage.getSuccessValue(),
						focusSecurityInfoPage.getMessage());
			}
			logger.error(String.format("[getFocusSecurityInfoPage()->fail:%s]", focusSecurityInfoPage.getMessage()));
			return ResultBuilder.failResult(focusSecurityInfoPage.getMessage());

		} catch (Exception e) {
			logger.error("getFocusSecurityInfoPage()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public Result<PageInfo<Map<String, Object>>> commandReceiveStates(JSONObject jsonParams) {

		logger.info(String.format("commandReceiveStates()->方法执行开始paramsObject：%s", jsonParams));

		try {

			RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger, "commandReceiveStates",
					jsonParams, CommonConstants.ORGANIZATION_ID,CommonConstants.PAGE_NUM,CommonConstants.PAGE_SIZE,
					CommonConstants.SEARCH_KEY,CommonConstants.ACCESS_SECRET);
			if (containsParamKey != null) {
				return ResultBuilder.failResult(containsParamKey.getMessage());
			}
			if (StringUtils.isBlank(jsonParams.getString(CommonConstants.ACCESS_SECRET))) {
				logger.error("[commandReceiveStates()->error:接入方密钥不能为空]");
				return ResultBuilder.invalidResult();
			}
			if (!StringUtils.isNumeric(jsonParams.getString(CommonConstants.PAGE_NUM))
					|| !StringUtils.isNumeric(jsonParams.getString(CommonConstants.PAGE_SIZE))) {
				logger.error("[commandReceiveStates()->error:pageNum和pageSize必须为数字]");
				return ResultBuilder.invalidResult();
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> queryMap = jsonParams.toJavaObject(Map.class);
			queryMap.put(InterGatewayConstants.TOKEN, request.getHeader(InterGatewayConstants.TOKEN));
			RpcResponse<PageInfo<Map<String, Object>>> responseInfo = focusSecurityQueryService
					.commandReceiveStates(queryMap);
			if (responseInfo.isSuccess()) {
				logger.info(String.format("[commandReceiveStates()->success:%s]", responseInfo.getMessage()));
				return ResultBuilder.successResult(responseInfo.getSuccessValue(), responseInfo.getMessage());
			} else {
				logger.error(String.format("[commandReceiveStates()->fail:%s]", responseInfo.getMessage()));
				return ResultBuilder.failResult(responseInfo.getMessage());
			}

		} catch (Exception e) {
			logger.error("commandReceiveStates()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
