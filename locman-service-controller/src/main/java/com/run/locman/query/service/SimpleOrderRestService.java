/*
 * File name: SimpleOrderService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月8日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.model.FacilitiesModel;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.SimpleOrderConstants;

/**
 * @Description: 获取设施设备
 * @author:王胜
 * @version: 1.0, 2017年12月8日
 */
@Service
public class SimpleOrderRestService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private SimpleOrderQueryService	simpleOrderQueryService;



	public Result<PageInfo<Map<String, Object>>> getFacilityDeviceList(String queryParams) {
		logger.info(String.format("[getFacilityDeviceList()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[getFacilityDeviceList()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> result = simpleOrderQueryService.getFacilityDeviceList(json);
			if (result.isSuccess()) {
				logger.info(String.format("[getFacilityDeviceList()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[getFacilityDeviceList()->fail:%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("getFacilityDeviceList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> getSimpleOrderById(String queryParams) {
		logger.info(String.format("[getSimpleOrderById()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[getSimpleOrderById()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			String id = json.getString(SimpleOrderConstants.ID);
			RpcResponse<Map<String, Object>> result = simpleOrderQueryService.findById(id);
			if (result.isSuccess()) {
				logger.info(String.format("[getSimpleOrderById()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[getSimpleOrderById()->fail:%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("getSimpleOrderById()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<Map<String, Object>>> getSimpleOrderList(String queryParams) {
		logger.info(String.format("[getSimpleOrderList()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[getSimpleOrderList()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> result = simpleOrderQueryService.getSimpleOrderList(json);
			if (result.isSuccess()) {
				logger.info(String.format("[getSimpleOrderList()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[getSimpleOrderList()->fail:%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("getSimpleOrderList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Boolean> whetherExistOrder(String queryParams) {
		logger.info(String.format("[whetherExistOrder()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[whetherExistOrder()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<Boolean> result = simpleOrderQueryService.whetherExistOrder(json);
			if (result.isSuccess()) {
				logger.info(String.format("[whetherExistOrder()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[whetherExistOrder()->fail:%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("whetherExistOrder()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<Map<String, Object>>> getSimpleOrderAgencyList(@RequestBody String queryParams) {
		logger.info(String.format("[getSimpleOrderAgencyList()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[getSimpleOrderAgencyList()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> result = simpleOrderQueryService.getSimpleOrderAgencyList(json);
			if (result.isSuccess()) {
				logger.info(String.format("[getSimpleOrderAgencyList()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[getSimpleOrderAgencyList()->fail:%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("getSimpleOrderAgencyList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> getOrderNodeDetails(String queryParams) {
		logger.info(String.format("[getOrderNodeDetails()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[getOrderNodeDetails()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<Map<String, Object>> result = simpleOrderQueryService.getOrderNodeDetails(json);
			if (result.isSuccess()) {
				logger.info(String.format("[getOrderNodeDetails()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[getOrderNodeDetails()->fail:%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("getOrderNodeDetails()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<Facilities>> findFacInfo(FacilitiesModel facilitiesModel) {      
		try {
			if (facilitiesModel == null) {
				logger.error("[findFacInfo()->error:传入参数为空]");
				return ResultBuilder.invalidResult();
			}
            logger.info(String.format("[findFacInfo()->request params--facilitiesId:%s]", facilitiesModel.getFacilitiesId()));
			RpcResponse<PageInfo<Facilities>> findFacInfo = simpleOrderQueryService.findFacInfo(facilitiesModel);

			if (findFacInfo.isSuccess()) {
				logger.info(String.format("[findFacInfo()->success:%s]", findFacInfo.getMessage()));
				return ResultBuilder.successResult(findFacInfo.getSuccessValue(), findFacInfo.getMessage());
			}
			logger.error(String.format("[findFacInfo()->fail:%s]", findFacInfo.getMessage()));
			return ResultBuilder.failResult(findFacInfo.getMessage());

		} catch (Exception e) {
			logger.error("findFacInfo()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}

	}
}
