/*
 * File name: FaultOrderProcessRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年12月08日 ... ... ...
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

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.dto.CountFaultOrderDto;
import com.run.locman.api.dto.FaultOrderHistogramDto;
import com.run.locman.api.query.service.FaultOrderProcessQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FaultOrderCountContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description: 故障工单查询
 * @author: 田明
 * @version: 1.0, 2017年12月08日
 */
@Service
public class FaultOrderProcessRestQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FaultOrderProcessQueryService	faultOrderProcessQueryService;
	@Autowired
	private HttpServletRequest		request;





	public Result<PageInfo<Map<String, Object>>> queryDevicesForFaultOrder(String queryParams) {
		logger.info(String.format("[queryDevicesForFaultOrder()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[queryDevicesForFaultOrder()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> resultType = faultOrderProcessQueryService
					.queryDevicesForFaultOrder(json);
			if (resultType.isSuccess()) {
				logger.info(String.format("[queryDevicesForFaultOrder()->success:%s]", resultType.getMessage()));
				return ResultBuilder.successResult(resultType.getSuccessValue(), resultType.getMessage());
			}
			logger.error(String.format("[queryDevicesForFaultOrder()->fail:%s]", resultType.getMessage()));
			return ResultBuilder.failResult(resultType.getMessage());
		} catch (Exception e) {
			logger.error("queryDevicesForFaultOrder()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> queryFaultOrderInfoById(String id) {
		logger.info(String.format("[queryFaultOrderInfoById()->request params--id:%s]", id));
		try {
			JSONObject jsonObject = JSONObject.parseObject(id);
			RpcResponse<Map<String, Object>> rpcResponse = faultOrderProcessQueryService
					.queryFaultOrderInfoById(jsonObject.getString("id"));
			if (rpcResponse.isSuccess()) {
				logger.info(String.format("[queryFaultOrderInfoById()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error(String.format("[queryFaultOrderInfoById()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("queryFaultOrderInfoById()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<Map<String, Object>>> getFaultOrderList(String queryParams) {
		logger.info(String.format("[getFaultOrderList()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[getFaultOrderList()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> resultType = faultOrderProcessQueryService
					.getFaultOrderList(json);
			if (resultType.isSuccess()) {
				logger.info(String.format("[getFaultOrderList()->success:%s]", resultType.getMessage()));
				return ResultBuilder.successResult(resultType.getSuccessValue(), resultType.getMessage());
			}
			logger.error(String.format("[getFaultOrderList()->fail:%s]", resultType.getMessage()));
			return ResultBuilder.failResult(resultType.getMessage());
		} catch (Exception e) {
			logger.error("getFaultOrderList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<Map<String, Object>>> queryAgendaOrProcessFaultOrderList(String queryParams) {
		logger.info(String.format("[queryAgendaOrProcessFaultOrderList()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[queryAgendaOrProcessFaultOrderList()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> resultType = faultOrderProcessQueryService
					.queryAgendaOrProcessFaultOrderList(json);
			if (resultType.isSuccess()) {
				logger.info(String.format("[queryAgendaOrProcessFaultOrderList()->success:%s]", resultType.getMessage()));
				return ResultBuilder.successResult(resultType.getSuccessValue(), resultType.getMessage());
			}
			logger.error(String.format("[queryAgendaOrProcessFaultOrderList()->fail:%s]", resultType.getMessage()));
			return ResultBuilder.failResult(resultType.getMessage());
		} catch (Exception e) {
			logger.error("queryAgendaOrProcessFaultOrderList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<CountFaultOrderDto>> countFaultOrderInfoByAS(String queryParams) {
		logger.info(String.format("[countFaultOrderInfoByAS()->request params:%s]", queryParams));
		if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
			logger.error(String.format("[countFaultOrderInfoByAS()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<CountFaultOrderDto>> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"countFaultOrderInfoByAS", json, FaultOrderCountContants.ACCESS_SECRET,
					FaultOrderCountContants.PAGE_NUM, FaultOrderCountContants.PAGE_SIZE);
			if (null != checkBusinessKey) {
				logger.error(checkBusinessKey.getMessage());
				return ResultBuilder.noBusinessResult();
			}
			RpcResponse<PageInfo<CountFaultOrderDto>> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"countFaultOrderInfoByAS", json, FaultOrderCountContants.SEARCH_KEY,
					FaultOrderCountContants.START_TIME, FaultOrderCountContants.END_TIME,
					FaultOrderCountContants.FAULTTYPE_ID);
			if (null != containsParamKey) {
				logger.error(containsParamKey.getMessage());
				return ResultBuilder.noBusinessResult();
			}
			String pageNum = json.getString(FaultOrderCountContants.PAGE_NUM);
			String pageSize = json.getString(FaultOrderCountContants.PAGE_SIZE);
			if (!StringUtils.isNumeric(pageNum)) {
				logger.error("当前页码参数必须是数字格式");
				return ResultBuilder.failResult("当前页码参数必须是数字格式");
			}
			if (!StringUtils.isNumeric(pageSize)) {
				logger.error("当前页码参数必须是数字格式");
				return ResultBuilder.failResult("每页条数参数必须是数字格式");
			}

			RpcResponse<PageInfo<CountFaultOrderDto>> countFaultOrderInfoByAS = faultOrderProcessQueryService
					.countFaultOrderInfoByAccessSecret(json);
			PageInfo<CountFaultOrderDto> successValue = countFaultOrderInfoByAS.getSuccessValue();
			if (!countFaultOrderInfoByAS.isSuccess() || null == successValue) {
				logger.error(String.format("[countFaultOrderInfoByAS()->fail:%s]", countFaultOrderInfoByAS.getMessage()));
				return ResultBuilder.failResult(countFaultOrderInfoByAS.getMessage());
			} else {
				logger.info(String.format("[countFaultOrderInfoByAS()->success:%s]", countFaultOrderInfoByAS.getMessage()));
				return ResultBuilder.successResult(successValue, countFaultOrderInfoByAS.getMessage());
			}

		} catch (Exception e) {
			logger.error("countFaultOrderInfoByAS()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<List<FaultOrderHistogramDto>> faultOrder2Histogram(String queryParams) {
		logger.info(String.format("[faultOrder2Histogram()->request params:%s]", queryParams));
		if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
			logger.error(String.format("[faultOrder2Histogram()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<List<FaultOrderHistogramDto>> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"countFaultOrderInfoByAS", json, FaultOrderCountContants.ACCESS_SECRET);
			if (null != checkBusinessKey) {
				logger.error(checkBusinessKey.getMessage());
				return ResultBuilder.noBusinessResult();
			}
			RpcResponse<List<FaultOrderHistogramDto>> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"countFaultOrderInfoByAS", json, FaultOrderCountContants.SEARCH_KEY,
					FaultOrderCountContants.START_TIME, FaultOrderCountContants.END_TIME,
					FaultOrderCountContants.FAULTTYPE_ID);
			if (null != containsParamKey) {
				logger.error(containsParamKey.getMessage());
				return ResultBuilder.noBusinessResult();
			}
			RpcResponse<List<FaultOrderHistogramDto>> faultOrder2Histogram = faultOrderProcessQueryService
					.faultOrder2Histogram(json);
			List<FaultOrderHistogramDto> successValue = faultOrder2Histogram.getSuccessValue();
			if (!faultOrder2Histogram.isSuccess() || null == successValue) {
				logger.error("故障工单柱状图数据查询失败");
				return ResultBuilder.failResult(faultOrder2Histogram.getMessage());
			} else {
				logger.info(String.format("[faultOrder2Histogram()->success:%s]", faultOrder2Histogram.getMessage()));
				return ResultBuilder.successResult(successValue, faultOrder2Histogram.getMessage());
			}
		} catch (Exception e) {
			logger.error("faultOrder2Histogram()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	public Result<Object> countFaultOrderNumByOrganizationAndType(JSONObject jsonObject) {
		if (jsonObject == null || jsonObject.isEmpty()) {
			logger.error("[countFaultOrderNumByOrganizationAndType()->error,传入参数为空]");
			return ResultBuilder.failResult("传入参数为空");
		}
		String pageNum = jsonObject.getString(PublicConstants.PUBLIC_PAGE_NUM);
		String pageSize = jsonObject.getString(PublicConstants.PUBLIC_PAGE_SIZE);
		if (!StringUtils.isNumeric(pageNum)) {
			logger.error("当前页码参数必须是数字格式");
			return ResultBuilder.failResult("当前页码参数必须是数字格式");
		}
		if (!StringUtils.isNumeric(pageSize)) {
			logger.error("当前页码参数必须是数字格式");
			return ResultBuilder.failResult("每页条数参数必须是数字格式");
		}
		String token = request.getHeader(InterGatewayConstants.TOKEN);
		jsonObject.put(InterGatewayConstants.TOKEN, token);
		try {
			logger.info(String.format("[countFaultOrderNumByOrganizationAndType()->进入方法,参数:%s]", jsonObject.toString()));
			RpcResponse<Object> result = faultOrderProcessQueryService.countFaultOrderNumByOrganizationAndType(jsonObject);
			if (null!= result && result.isSuccess()) {
				logger.info("[countFaultOrderNumByOrganizationAndType()->success]");
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[countFaultOrderNumByOrganizationAndType()->error,%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("[countFaultOrderNumByOrganizationAndType()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	/**
	 * 
	* @Description:
	* @param jsonObject
	* @return
	 */
	public Result<PageInfo<Map<String,Object>>> faultOrderList(JSONObject jsonObject) {
		if (jsonObject == null || jsonObject.isEmpty()) {
			logger.error("[faultOrderList()->error,传入参数为空]");
			return ResultBuilder.failResult("传入参数为空");
		}
		String pageNo = jsonObject.getString("pageNo");
		String pageSize = jsonObject.getString(PublicConstants.PUBLIC_PAGE_SIZE);
		if (!StringUtils.isNumeric(pageNo)) {
			logger.error("当前页码参数必须是数字格式");
			return ResultBuilder.failResult("当前页码参数必须是数字格式");
		}
		if (!StringUtils.isNumeric(pageSize)) {
			logger.error("当前页码参数必须是数字格式");
			return ResultBuilder.failResult("每页条数参数必须是数字格式");
		}
		String token = request.getHeader(InterGatewayConstants.TOKEN);
		jsonObject.put(InterGatewayConstants.TOKEN, token);
		try {
			logger.info(String.format("[faultOrderList()->进入方法,参数:%s]", jsonObject.toString()));
			RpcResponse<PageInfo<Map<String,Object>>> result = faultOrderProcessQueryService.faultOrderList(jsonObject);
			if (null!= result && result.isSuccess()) {
				logger.info("[faultOrderList()->success]");
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[faultOrderList()->error,%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("[faultOrderList()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	public Result<List<Map<String,Object>>> faultOrderStateCount(JSONObject jsonObject) {
		if (jsonObject == null || jsonObject.isEmpty()) {
			logger.error("[faultOrderStateCount()->error,传入参数为空]");
			return ResultBuilder.failResult("传入参数为空");
		}
		try {
			logger.info(String.format("[faultOrderStateCount()->进入方法,参数:%s]", jsonObject.toString()));
			RpcResponse<List<Map<String,Object>>> result = faultOrderProcessQueryService.faultOrderStateCount(jsonObject);
			if (null!= result && result.isSuccess()) {
				logger.info("[faultOrderStateCount()->success]");
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error(String.format("[faultOrderStateCount()->error,%s]", result.getMessage()));
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("[faultOrderStateCount()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public Result<PageInfo<Map<String, Object>>> queryAgendaOrProcessFaultOrderListNew(String queryParams) {
		logger.info(String.format("[queryAgendaOrProcessFaultOrderListNew()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[queryAgendaOrProcessFaultOrderListNew()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> resultType = faultOrderProcessQueryService
					.queryAgendaOrProcessFaultOrderListNew(json);
			if (resultType.isSuccess()) {
				logger.info(String.format("[queryAgendaOrProcessFaultOrderListNew()->success:%s]", resultType.getMessage()));
				return ResultBuilder.successResult(resultType.getSuccessValue(), resultType.getMessage());
			}
			logger.error(String.format("[queryAgendaOrProcessFaultOrderListNew()->fail:%s]", resultType.getMessage()));
			return ResultBuilder.failResult(resultType.getMessage());
		} catch (Exception e) {
			logger.error("queryAgendaOrProcessFaultOrderListNew()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public Result<PageInfo<Map<String, Object>>> getFaultOrderListNew(String queryParams) {
		logger.info(String.format("[getFaultOrderListNew()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				logger.error(String.format("[getFaultOrderListNew()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> resultType = faultOrderProcessQueryService
					.getFaultOrderListNew(json);
			if (resultType.isSuccess()) {
				logger.info(String.format("[getFaultOrderListNew()->success:%s]", resultType.getMessage()));
				return ResultBuilder.successResult(resultType.getSuccessValue(), resultType.getMessage());
			}
			logger.error(String.format("[getFaultOrderListNew()->fail:%s]", resultType.getMessage()));
			return ResultBuilder.failResult(resultType.getMessage());
		} catch (Exception e) {
			logger.error("getFaultOrderListNew()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
