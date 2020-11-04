
/*
 * File name: FacilitiesRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年08月08日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 在地图查询设施
 * @author: 田明
 * @version: 1.0, 2017年08月08日
 */
@Service
public class FacilitiesRestQueryService {
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesQueryService	facilitiesQueryService;
	
	@Value("${api.host}")
	private String					ip;
	
	@Autowired
	private HttpServletRequest		request;


	public Result<Map<String, Object>> queryMapFacilities(String queryParams) {
		logger.info(String.format("[queryMapFacilities()->request params:%s]", queryParams));
		Result<List<Map<String, Object>>> result = ExceptionChecked.checkRequestParam(queryParams);
		if (result != null) {
			logger.error(String.format("[queryMapFacilities()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsObject = JSON.parseObject(queryParams);
			// 必填参数校验
			RpcResponse<List<Map<String, Object>>> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"queryMapFacilities", paramsObject, FacilitiesContants.USC_ACCESS_SECRET);
			if (null != checkBusinessKey) {
				return ResultBuilder.invalidResult();
			}
			// 非必填参数校验
			RpcResponse<List<Map<String, Object>>> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"queryMapFacilities", paramsObject, FacilitiesContants.FACILITIES_TYPE_ID,
					FacilitiesContants.FACILITIES_SEARCHKEY, FacilitiesContants.COMPLETE_ADDRESS,
					FacilitiesContants.ALARM_WORST_LEVEL, FacilitiesContants.ORGANIZATION_ID, 
					FacilitiesContants.DEFENSE_STATE);
			if (null != containsParamKey) {
				return ResultBuilder.invalidResult();
			}
			
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			paramsObject.put(InterGatewayConstants.TOKEN, token);
			// 地图查询设施,
			RpcResponse<Map<String, Object>> factilitiesList = facilitiesQueryService.queryMapFacilities(paramsObject);

			if (factilitiesList.isSuccess() && factilitiesList.getSuccessValue() != null) {
				return ResultBuilder.successResult(factilitiesList.getSuccessValue(), factilitiesList.getMessage());
			} else {
				return ResultBuilder.failResult(factilitiesList.getMessage());
			}
		} catch (Exception e) {
			logger.error("queryMapFacilities()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
		
		public Result<String> updateKeyTime(String accessSecret){
		
		RpcResponse<String> updateKeyTime = facilitiesQueryService.updateKeyTime(accessSecret);
		
		return ResultBuilder.successResult(updateKeyTime.getSuccessValue(), updateKeyTime.getMessage());
	}


	/**
	 * @Description: 由设施id查询设施状态 * @author: 张贺
	 */
	public Result<Boolean> getFacilityMangerStateById(String idlist) {
		try {
			if (StringUtils.isBlank(idlist)) {
				logger.error("getFacilityMangerStateById--fail:参数校验失败,参数不能为空");
				return ResultBuilder.failResult("参数校验失败,参数不能为空");
			}
			if (ParamChecker.isNotMatchJson(idlist)) {
				logger.error("getFacilityMangerStateById--fail:参数校验失败,参数必须为json格式!]");
				return ResultBuilder.failResult("参数校验失败,参数必须为json格式!");
			}
			JSONObject json = JSONObject.parseObject(idlist);
			String facilityIdList = json.getString("facilityIds");
			if (StringUtils.isBlank(facilityIdList)) {
				logger.error("getFacilityMangerStateById--fail:参数校验失败,id不能为空");
				return ResultBuilder.failResult("参数校验失败,id不能为空");
			}
			String[] fIdlist = facilityIdList.split(",");
			List<String> asList = Arrays.asList(fIdlist);
			RpcResponse<Boolean> res = facilitiesQueryService.getFacilityMangerStateById(asList);
			if (res == null) {
				logger.info("getFacilityManageStateByIds--fail：设施查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			if (res.isSuccess()) {
				logger.info("getFacilityManageStateByIds--successful：设施查询陈宫");
				return ResultBuilder.successResult(res.getSuccessValue(), "查询成功");
			}
			logger.error("getFacilityManageStateByIds error");
			return ResultBuilder.failResult(res.getMessage());
		} catch (Exception e) {
			logger.error("getFacilityManageStateByIds()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	public Result<Map<String, Object>> countFacByTypeAndOrg(JSONObject jsonObject) {
		if (jsonObject == null) {
			logger.error("[countFacitiesByTypeAndOrganizationId()->error,传入参数为空 ");
		}
		try {
			logger.info(String.format("[countFacitiesByTypeAndOrganizationId()->进入方法,传入参数为%s]", jsonObject.toString()));
			if (!jsonObject.containsKey(FacilitiesContants.USC_ACCESS_SECRET)
					|| StringUtils.isBlank(jsonObject.getString(FacilitiesContants.USC_ACCESS_SECRET))) {
				logger.error("[countFacitiesByTypeAndOrganizationId()->error,accessSecret为空] ");
			}
			if (!jsonObject.containsKey(FacilitiesContants.ORGANIZATION_ID)
					|| StringUtils.isBlank(jsonObject.getString(FacilitiesContants.ORGANIZATION_ID))) {
				logger.error("[countFacitiesByTypeAndOrganizationId()->error,organizationId为空] ");
			}
			String accessSecret = jsonObject.getString(FacilitiesContants.USC_ACCESS_SECRET);
			String organizationId = jsonObject.getString(FacilitiesContants.ORGANIZATION_ID);
			RpcResponse<List<Map<String, Object>>> res = facilitiesQueryService
					.countFacByTypeAndOrg(accessSecret, organizationId);
			
			if (res.isSuccess()) {
				
				Map<String, Object> resultMap = Maps.newHashMap();
				resultMap.put("data", res.getSuccessValue());
				String token = request.getHeader(InterGatewayConstants.TOKEN);
				
				String httpValueByGet = InterGatewayUtil.getHttpValueByGet(InterGatewayConstants.U_ORGANIZATION_INFO_BYID + organizationId, ip, token);
				
				if (null == httpValueByGet) {
					logger.error("[MapToOrg()->invalid：组织查询失败!]");
					resultMap.put("startPoint", "");
					resultMap.put("stopPoint", "");
					resultMap.put("name", "");
				} else {
					JSONObject json = JSONObject.parseObject(httpValueByGet);
					JSONObject sourceInfo = json.getJSONObject("sourceInfo");
					resultMap.put("startPoint", sourceInfo.getOrDefault("startPoint", ""));
					resultMap.put("stopPoint", sourceInfo.getOrDefault("stopPoint", ""));
					resultMap.put("name", sourceInfo.getOrDefault("sourceName", ""));
				}
				
				
				logger.info("[countFacitiesByTypeAndOrganizationId()->查询成功]");
				return ResultBuilder.successResult(resultMap, "success");
			}
			logger.error("[countFacitiesByTypeAndOrganizationId()->查询失败]");
			return ResultBuilder.failResult(res.getMessage());
		} catch (Exception e) {
			logger.error("[countFacitiesByTypeAndOrganizationId()->e]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	
	public Result<List<Map<String, Object>>> countFacNumByStreet(String accessSecret) {
		logger.info(String.format("[countFacNumByStreet()->request accessSecret:%s]", accessSecret));
		
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("countFacNumByStreet--fail:accessSecret参数校验失败,accessSecret不能为空");
				return ResultBuilder.failResult("参数accessSecret校验失败,accessSecret不能为空");	
			}
			String token = request.getHeader(InterGatewayConstants.TOKEN);

			RpcResponse<List<Map<String, Object>>> response = facilitiesQueryService.countFacNumByStreet(accessSecret,token);
			
			if (response.isSuccess()) {
				logger.info("[countFacNumByStreet()->查询成功]");
				return ResultBuilder.successResult(response.getSuccessValue(), "success");
			} else {
				logger.info("[countFacNumByStreet()->查询失败]");
				return ResultBuilder.failResult("查询失败," + response.getMessage());
			}
			
		} catch (Exception e) {
			logger.error("queryMapFacilities()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	
	public Result<List<Map<String, Object>>> findFacilityLngLat(String accessSecret) {
		logger.info(String.format("[findFacilityLngLat()->request accessSecret:%s]", accessSecret));
		
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("findFacilityLngLat--fail:accessSecret参数校验失败,accessSecret不能为空");
				return ResultBuilder.failResult("参数accessSecret校验失败,accessSecret不能为空");	
			}
			
			RpcResponse<List<Map<String, Object>>> response = facilitiesQueryService.findFacilityLngLat(accessSecret);
			
			if (response.isSuccess()) {
				logger.info("[findFacilityLngLat()->查询成功]");
				return ResultBuilder.successResult(response.getSuccessValue(), "查询成功");
			} else {
				logger.info("[findFacilityLngLat()->查询失败]");
				return ResultBuilder.failResult("查询失败," + response.getMessage());
			}
			
		} catch (Exception e) {
			logger.error("findFacilityLngLat()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public Result<List<List<String>>> facilityLngLatList(String accessSecret) {
		logger.info(String.format("[facilityLngLatList()->request accessSecret:%s]", accessSecret));
		
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("facilityLngLatList--fail:accessSecret参数校验失败,accessSecret不能为空");
				return ResultBuilder.failResult("参数accessSecret校验失败,accessSecret不能为空");	
			}
			
			RpcResponse<List<Map<String, Object>>> response = facilitiesQueryService.findFacilityLngLat(accessSecret);
			
			if (response.isSuccess()) {
				logger.info("[facilityLngLatList()->查询成功]");
				List<Map<String, Object>> successValue = response.getSuccessValue();
				List<List<String>> resultList = Lists.newArrayList();
				for (Map<String, Object> map : successValue) {
					List<String> list = Lists.newLinkedList();
					list.add(map.getOrDefault("lng", "") + "");
					list.add(map.getOrDefault("lat", "") + "");
					list.add("1");
					resultList.add(list);
				}
				
				
				return ResultBuilder.successResult(resultList, "查询成功");
			} else {
				logger.info("[facilityLngLatList()->查询失败]");
				return ResultBuilder.failResult("查询失败," + response.getMessage());
			}
			
		} catch (Exception e) {
			logger.error("facilityLngLatList()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
