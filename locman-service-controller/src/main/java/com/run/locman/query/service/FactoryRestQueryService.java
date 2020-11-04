/*
 * File name: FactoryRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年11月20日 ... ...
 * ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.dto.AppTagDto;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 厂家查询
 * @author: qulong
 * @version: 1.0, 2017年11月20日
 */
@Service
public class FactoryRestQueryService {

	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FactoryQueryService	factoryQueryService;

	@Autowired
	private HttpServletRequest	request;


	public Result<Map<String, Object>> findFactoryById(String id) {
		logger.info(String.format("[findFactoryById()->request params--id:%s]", id));
		try {
			RpcResponse<Map<String, Object>> rpcResponse = factoryQueryService.findFactoryById(id);
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[findFactoryById()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[findFactoryById()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("findFactoryById()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<List<Map<String, String>>> queryFactoryNameList(String accessSecret) {
		logger.info(String.format("[queryFactoryNameList()->request params--accessSecret:%s]", accessSecret));
		if (ParamChecker.isBlank(accessSecret)) {
			logger.error(String.format("[queryFactoryNameList()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.invalidResult();
		}
		try {
			RpcResponse<List<Map<String, String>>> rpcResponse = factoryQueryService.queryFactoryNameList(accessSecret);
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[queryFactoryNameList()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[queryFactoryNameList()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("[queryFactoryNameList()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Boolean> checkAppTagExist(String queryParams) {
		logger.info(String.format("[checkAppTagExist()->request params:%s]", queryParams));
		if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
			logger.error(String.format("[checkAppTagExist()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsObject = JSON.parseObject(queryParams);
			String accessSecret = paramsObject.getString("accessSecret");
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[checkAppTagExist()->error:accessSecret不能为空]");
				return ResultBuilder.invalidResult();
			}
			String appId = paramsObject.getString("appId");
			if (StringUtils.isBlank(appId)) {
				logger.error("[checkAppTagExist()->error:appId不能为空]");
				return ResultBuilder.invalidResult();
			}
			String appKey = paramsObject.getString("appKey");
			if (StringUtils.isBlank(appKey)) {
				logger.error("[checkAppTagExist()->error:appKey不能为空]");
				return ResultBuilder.invalidResult();
			}
			AppTagDto appTagDto = new AppTagDto();
			appTagDto.setAppId(appId);
			appTagDto.setAppKey(appKey);
			String factoryId = paramsObject.getString("factoryId");
			// 编辑时校验需要传厂家id以排除自身
			if (StringUtils.isNotBlank(factoryId)) {
				appTagDto.setId(factoryId);
			}
			RpcResponse<Boolean> rpcResponse = factoryQueryService.checkAppTagExist(appTagDto, accessSecret, null,
					request.getHeader(InterGatewayConstants.TOKEN));
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[checkAppTagExist()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[checkAppTagExist()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("checkAppTagExist()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<Map<String, Object>>> getNewFactoryList(String accessSecret, String queryParams) {
		logger.info(String.format("[getNewFactoryList()->request params:%s]", queryParams));
		if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
			logger.error(String.format("[getNewFactoryList()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsObject = JSON.parseObject(queryParams);
			if (!paramsObject.containsKey(CommonConstants.PAGE_NUM)) {
				logger.error("[getNewFactoryList()->error:参数无pageNum]");
				return ResultBuilder.emptyResult();
			}
			if (!paramsObject.containsKey(CommonConstants.PAGE_SIZE)) {
				logger.error("[getNewFactoryList()->error:参数无pageSize]");
				return ResultBuilder.emptyResult();
			}
			if (!paramsObject.containsKey(CommonConstants.SEARCH_KEY)) {
				logger.error("[getNewFactoryList()->error:参数无searchKey]");
				return ResultBuilder.emptyResult();
			}
			if (!paramsObject.containsKey(CommonConstants.MANAGESTATE)) {
				logger.error("[getNewFactoryList()->error:参数无ManageState]");
				return ResultBuilder.emptyResult();
			}
			if (!StringUtils.isNumeric(paramsObject.getString(CommonConstants.PAGE_NUM))
					|| !StringUtils.isNumeric(paramsObject.getString(CommonConstants.PAGE_SIZE))) {
				logger.error("[getNewFactoryList()->error:pageNum和pageSize必须为数字]");
				return ResultBuilder.invalidResult();
			}
			int pageNum = paramsObject.getIntValue(CommonConstants.PAGE_NUM);
			int pageSize = paramsObject.getIntValue(CommonConstants.PAGE_SIZE);
			String searchKey = paramsObject.getString(CommonConstants.SEARCH_KEY);
			String manageState = paramsObject.getString(CommonConstants.MANAGESTATE);
			RpcResponse<PageInfo<Map<String, Object>>> rpcResponse = factoryQueryService.getNewFactoryList(accessSecret,
					pageNum, pageSize, searchKey, manageState);
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[getNewFactoryList()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[getNewFactoryList()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("getNewFactoryList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:通过接入方密钥加载厂家
	 * @param accessSecret
	 * @return
	 */
	public Result<List<Map<String, String>>> downBoxFactory(String accessSecret) {
		logger.info(String.format("[downBoxFactory()->request params:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[downBoxFactory()->error:参数无accessSecret]");
				return ResultBuilder.emptyResult();
			}

			RpcResponse<List<Map<String, String>>> rpcResponse = factoryQueryService.downBoxFactory(accessSecret);

			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[downBoxFactory()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[downBoxFactory()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("downBoxFactory()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}