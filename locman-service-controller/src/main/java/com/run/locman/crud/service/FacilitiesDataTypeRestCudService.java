/*
 * File name: FacilitiesDataTypeRestCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年08月31日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.service;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.FacilitiesDataTypeCudService;
import com.run.locman.api.entity.FacilitiesDataType;
import com.run.locman.api.query.service.FacilitiesDataTypeQueryRpcService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 设施扩展cud
 * @author: 田明
 * @version: 1.0, 2017年08月31日
 */
@Service
public class FacilitiesDataTypeRestCudService {

	@Autowired
	private FacilitiesDataTypeCudService		facilitiesDataTypeCudService;

	@Autowired
	private FacilitiesDataTypeQueryRpcService	facilitiesDataTypeQueryRpcService;

	private Logger								logger	= Logger.getLogger(CommonConstants.LOGKEY);



	/**
	 * @Description: 改变设施扩展的管理状态n
	 * @param param
	 * @return
	 */
	public Result<FacilitiesDataType> switchFacilitiesDataTypeState(String param) {
		logger.info(String.format("[switchFacilitiesDataTypeState()->request params:%s]", param));
		try {
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

			if (!paramsJson.containsKey(CommonConstants.FACILITIESTYPEID)) {
				return ResultBuilder.noBusinessResult();
			}
			String facilitiesTypeId = paramsJson.getString(CommonConstants.FACILITIESTYPEID);

			FacilitiesDataType facilitiesDataType = new FacilitiesDataType();
			facilitiesDataType.setId(id);
			facilitiesDataType.setState(state);
			facilitiesDataType.setFacilitiesTypeId(facilitiesTypeId);

			RpcResponse<FacilitiesDataType> updateFacilitiesDataTypeResult = facilitiesDataTypeCudService
					.updateFacilitiesDataType(facilitiesDataType);
			if (updateFacilitiesDataTypeResult == null || !updateFacilitiesDataTypeResult.isSuccess()) {
				logger.error("[switchFacilitiesDataTypeState()->fail:状态更新失败]");
				return ResultBuilder.failResult("状态更新失败");
			}
			logger.info(String.format("[switchFacilitiesDataTypeState()->success;%s]",
					updateFacilitiesDataTypeResult.getMessage()));
			return ResultBuilder.successResult(facilitiesDataType, updateFacilitiesDataTypeResult.getMessage());
		} catch (Exception e) {
			logger.error("switchFacilitiesDataTypeState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description: 修改设施扩展相关信息
	 * @param param
	 * @return
	 */
	public Result<FacilitiesDataType> updateFacilityDataType(String param) {
		try {
			logger.info(String.format("[updateFacilityDataType()->request params:%s]", param));
			if (ParamChecker.isBlank(param)) {
				return ResultBuilder.emptyResult();
			}
			if (ParamChecker.isNotMatchJson(param)) {
				return ResultBuilder.invalidResult();
			}
			JSONObject paramsJson = JSONObject.parseObject(param);
			Result<Object> dataTypeParamCheck = dataTypeParamCheck(paramsJson);
			if (null != dataTypeParamCheck) {
				return ResultBuilder.noBusinessResult();
			}
			String id = paramsJson.getString(CommonConstants.ID);
			String facilitiesTypeId = paramsJson.getString(CommonConstants.FACILITIESTYPEID);
			String name = paramsJson.getString(CommonConstants.NAME);
			String isNotMandatory = paramsJson.getString(CommonConstants.ISNOTMANDATORY);
			String initialValue = paramsJson.getString(CommonConstants.INITIALVALUE);
			String dataType = paramsJson.getString(CommonConstants.DATATYPE);
			String remarks = paramsJson.getString(CommonConstants.REMARKS);
			String sign = paramsJson.getString(CommonConstants.SIGN);

			FacilitiesDataType facilitiesDataType = new FacilitiesDataType();
			facilitiesDataType.setId(id);
			facilitiesDataType.setFacilitiesTypeId(facilitiesTypeId);
			facilitiesDataType.setName(name);
			facilitiesDataType.setIsNotMandatory(isNotMandatory);
			// facilitiesDataType.setRegex(regex);
			facilitiesDataType.setDataType(dataType);
			facilitiesDataType.setRemarks(remarks);
			facilitiesDataType.setInitialValue(initialValue);
			facilitiesDataType.setSign(sign);
			// facilitiesDataType.setRegexRemark(paramsJson.getString("regexRemark"));
			Map<String, String> map = Maps.newHashMap();
			map.put(CommonConstants.SIGN, sign);
			map.put(CommonConstants.NAME, name);
			map.put(CommonConstants.ID, id);
			map.put(CommonConstants.FACILITIESTYPEID, facilitiesTypeId);
			RpcResponse<Integer> count = facilitiesDataTypeQueryRpcService.validFacilitiesDataTypeName(map);

			if (count.isSuccess() && count.getSuccessValue() != null && count.getSuccessValue() == 0) {
				RpcResponse<FacilitiesDataType> updateFacilitiesDataTypeResult = facilitiesDataTypeCudService
						.updateFacilitiesDataType(facilitiesDataType);

				if (updateFacilitiesDataTypeResult == null) {
					logger.error("[updateFacilityDataType()->fail:返回对象为null]");
					return ResultBuilder.failResult("设施扩展信息修改失败");
				} else if (!updateFacilitiesDataTypeResult.isSuccess()) {
					logger.error(String.format("[updateFacilityDataType()->fail:%s]",
							updateFacilitiesDataTypeResult.getMessage()));

					return ResultBuilder.failResult("设施扩展信息修改失败");
				} else {
					logger.info(String.format("[updateFacilityDataType()->success:%s]",
							updateFacilitiesDataTypeResult.getMessage()));
					return ResultBuilder.successResult(facilitiesDataType, updateFacilitiesDataTypeResult.getMessage());
				}

			} else {
				logger.error("[updateFacilityDataType()->fail:修改失败:设施扩展信息名称或标识重复]");
				return ResultBuilder.failResult("修改失败:设施扩展信息名称或标识重复");
			}

		} catch (Exception e) {
			logger.error("updateFacilityDataType()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param paramsJson
	 */

	private Result<Object> dataTypeParamCheck(JSONObject paramsJson) {
		if (!paramsJson.containsKey(CommonConstants.ID)) {
			return ResultBuilder.noBusinessResult();
		}
		if (!paramsJson.containsKey(CommonConstants.FACILITIESTYPEID)) {
			return ResultBuilder.noBusinessResult();
		}
		if (!paramsJson.containsKey(CommonConstants.NAME)) {
			return ResultBuilder.noBusinessResult();
		}
		if (!paramsJson.containsKey(CommonConstants.ISNOTMANDATORY)) {
			return ResultBuilder.noBusinessResult();
		}
		if (!paramsJson.containsKey(CommonConstants.INITIALVALUE)) {
			return ResultBuilder.noBusinessResult();
		}
		if (!paramsJson.containsKey(CommonConstants.DATATYPE)) {
			return ResultBuilder.noBusinessResult();
		}
		if (!paramsJson.containsKey(CommonConstants.REMARKS)) {
			return ResultBuilder.noBusinessResult();
		}
		if (!paramsJson.containsKey(CommonConstants.SIGN)) {
			return ResultBuilder.noBusinessResult();
		}
		return null;
	}



	/**
	 * 
	 * @Description:设施类型扩展必填参数校验
	 * @param param
	 * @return
	 */

	public Result<FacilitiesDataType> addFacilitiesDataType(String param) {
		logger.info(String.format("[addFacilitiesDataType()->request params:%s]", param));
		if (ParamChecker.isBlank(param)) {
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(param)) {
			return ResultBuilder.invalidResult();
		}
		JSONObject paramsJson = JSONObject.parseObject(param);

		RpcResponse<FacilitiesDataType> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
				"addFacilitiesDataType", paramsJson, "facilitiesTypeId", "name", "sign", "isNotMandatory", "dataType");
		if (null != checkBusinessKey) {
			return ResultBuilder.failResult(checkBusinessKey.getMessage());
		}
		RpcResponse<FacilitiesDataType> containsParamKey = CheckParameterUtil.containsParamKey(logger,
				"addFacilitiesDataType", paramsJson, "initialValue", "remarks");
		if (null != containsParamKey) {
			return ResultBuilder.failResult(containsParamKey.getMessage());
		}

		String facilitiesTypeId = paramsJson.getString("facilitiesTypeId");

		String name = paramsJson.getString("name");

		String isNotMandatory = paramsJson.getString("isNotMandatory");

		String initialValue = paramsJson.getString("initialValue");

		// String regex = paramsJson.getString("regex");

		String dataType = paramsJson.getString("dataType");

		String remarks = paramsJson.getString("remarks");

		String sign = paramsJson.getString("sign");

		FacilitiesDataType facilitiesDataType = new FacilitiesDataType();
		facilitiesDataType.setId(UtilTool.getUuId());
		facilitiesDataType.setFacilitiesTypeId(facilitiesTypeId);
		facilitiesDataType.setName(name);
		facilitiesDataType.setIsNotMandatory(isNotMandatory);
		// facilitiesDataType.setRegex(regex);
		facilitiesDataType.setDataType(dataType);
		facilitiesDataType.setRemarks(remarks);
		facilitiesDataType.setInitialValue(initialValue);
		facilitiesDataType.setSign(sign);
		facilitiesDataType.setState("enabled");
		// if (!StringUtils.isBlank(paramsJson.getString("regexRemark"))) {
		// facilitiesDataType.setRegexRemark(paramsJson.getString("regexRemark"));
		// }

		try {
			Map<String, String> map = Maps.newHashMap();
			map.put("sign", sign);
			map.put("name", name);
			map.put("facilitiesTypeId", facilitiesTypeId);
			RpcResponse<Integer> count = facilitiesDataTypeQueryRpcService.validFacilitiesDataTypeName(map);
			if (count.isSuccess() && count.getSuccessValue() != null && count.getSuccessValue() == 0) {
				RpcResponse<FacilitiesDataType> addResult = facilitiesDataTypeCudService
						.addFacilitiesDataType(facilitiesDataType);
				if (addResult != null && addResult.isSuccess()) {
					logger.info("[addFacilitiesDataType()->success]");
					return ResultBuilder.successResult(addResult.getSuccessValue(), "新增成功");
				}
				logger.error("[addFacilitiesDataType()->fail]");
				return ResultBuilder.failResult("新增失败");
			} else {
				logger.error("[addFacilitiesDataType()->fail:新增失败:设施扩展信息名称或标识重复]");
				return ResultBuilder.failResult("新增失败:设施扩展信息名称或标识重复");
			}
		} catch (Exception e) {
			logger.error("addFacilitiesDataType()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
