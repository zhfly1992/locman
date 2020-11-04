/*
 * File name: FacilitiesDataTypeQueryRestService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 李康诚 2017年11月20日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.FacilitiesDataType;
import com.run.locman.api.query.service.FacilitiesDataTypeQueryRpcService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;

/**
 * @Description: 设施数据类型查询Contro
 * @author: 李康诚
 * @version: 1.0, 2017年11月20日
 */
@Service
public class FacilitiesDataTypeQueryRestService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	FacilitiesDataTypeQueryRpcService	facilitiesDataTypeQueryRpcService;



	public Result<List<FacilitiesDataType>> getAllFacilitiesDataType(String facilitiesDataTypeInfo) {
		logger.info(String.format("[getAllFacilitiesDataType()->request params:%s]", facilitiesDataTypeInfo));
		try {
			// 验证参数是否合法
			Result<List<FacilitiesDataType>> result = ExceptionChecked.checkRequestParam(facilitiesDataTypeInfo);
			if (result != null) {
				logger.error(String.format("[getAllFacilitiesDataType()->error:%s]", "参数验证失败:参数为空或格式错误!"));
				return ResultBuilder.failResult("参数验证失败:参数为空或格式错误!");
			}

			JSONObject jsonParam = JSON.parseObject(facilitiesDataTypeInfo);

			String facilitiesTypeId = jsonParam.getString(FacilitiesContants.FACILITIES_TYPE_ID);
			String accessSecret = jsonParam.getString(FacilitiesContants.USC_ACCESS_SECRET);
			Map<String, String> map = Maps.newHashMap();
			map.put(FacilitiesContants.FACILITIES_TYPE_ID, facilitiesTypeId);
			map.put(FacilitiesContants.USC_ACCESS_SECRET, accessSecret);

			// 调用RPC服务,不分页查詢所有元数据属性
			RpcResponse<List<FacilitiesDataType>> facilitiesDataTypeList = facilitiesDataTypeQueryRpcService
					.getAllFacilitiesDataType(map);
			if (facilitiesDataTypeList.isSuccess()) {
				logger.info(
						String.format("[getAllFacilitiesDataType()->success:%s]", facilitiesDataTypeList.getMessage()));
				return ResultBuilder.successResult(facilitiesDataTypeList.getSuccessValue(),
						facilitiesDataTypeList.getMessage());
			} else {
				logger.error(
						String.format("[getAllFacilitiesDataType()->fail:%s]", facilitiesDataTypeList.getMessage()));
				return ResultBuilder.failResult(facilitiesDataTypeList.getMessage());
			}
		} catch (Exception e) {
			logger.error("getAllFacilitiesDataType()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<FacilitiesDataType>> getFacilitiesDataTypeList(String facilitiesDataTypeInfo) {
		logger.info(String.format("[getFacilitiesDataTypeList()->request params:%s]", facilitiesDataTypeInfo));
		try {
			// 验证参数是否合法
			Result<PageInfo<FacilitiesDataType>> result = ExceptionChecked
					.checkRequestParamHasKey(facilitiesDataTypeInfo, CommonConstants.PAGE_NUM);
			if (result != null) {
				logger.error(String.format("[getFacilitiesDataTypeList()->error:%s]", "参数验证失败:参数为空或格式错误!"));
				return ResultBuilder.failResult("参数验证失败:参数为空或格式错误!");
			}

			JSONObject jsonParam = JSON.parseObject(facilitiesDataTypeInfo);
			// 验证传入数字是否合法
			if (!StringUtils.isNumeric(jsonParam.getString(CommonConstants.PAGE_NUM))) {
				logger.error(String.format("[getFacilitiesDataTypeList()->error:%s]", "查询分页信息,传入数字异常!"));
				return ResultBuilder.failResult("查询分页信息,传入数字异常!");
			}

			// 获取页面页数
			Integer pageNo = jsonParam.getIntValue(CommonConstants.PAGE_NUM);

			// 默认分页大小
			Integer pageSize = 0;
			if (StringUtils.isBlank(jsonParam.getString(CommonConstants.PAGE_SIZE))) {
				pageSize = 10;
			} else {
				// 验证传入数字是否合法
				if (!StringUtils.isNumeric(jsonParam.getString(CommonConstants.PAGE_SIZE))) {
					logger.error(String.format("[getFacilitiesDataTypeList()->error:%s]", "查询分页信息,传入数字异常!"));
					return ResultBuilder.failResult("查询分页信息,传入数字异常!");
				}
				pageSize = jsonParam.getIntValue(CommonConstants.PAGE_SIZE);
			}

			String facilitiesName = jsonParam.getString(CommonConstants.NAME);
			String facilitiesTypeId = jsonParam.getString(FacilitiesContants.FACILITIES_TYPE_ID);
			Map<String, String> map = Maps.newHashMap();
			map.put(CommonConstants.NAME, facilitiesName);
			map.put(FacilitiesContants.FACILITIES_TYPE_ID, facilitiesTypeId);
			map.put("state", jsonParam.getString("state"));

			// 调用RPC服务,分页查詢所有元数据属性
			RpcResponse<PageInfo<FacilitiesDataType>> facilitiesDataTypeList = facilitiesDataTypeQueryRpcService
					.getFacilitiesDataTypeList(pageNo, pageSize, map);
			if (facilitiesDataTypeList.isSuccess()) {
				logger.info(String.format("[getFacilitiesDataTypeList()->success:%s]",
						facilitiesDataTypeList.getMessage()));
				return ResultBuilder.successResult(facilitiesDataTypeList.getSuccessValue(),
						facilitiesDataTypeList.getMessage());
			} else {
				logger.error(
						String.format("[getFacilitiesDataTypeList()->fail:%s]", facilitiesDataTypeList.getMessage()));
				return ResultBuilder.failResult(facilitiesDataTypeList.getMessage());
			}
		} catch (Exception e) {
			logger.error("getFacilitiesDataTypeList()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<FacilitiesDataType> getById(String id) throws Exception {
		logger.info(String.format("[getById()->request params:%s]", id));
		if (ParamChecker.isNotMatchJson(id)) {
			logger.error("[getById()->error:传入参数必须为json格式]");
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject idJson = JSONObject.parseObject(id);
			if (!idJson.containsKey(CommonConstants.ID)) {
				logger.error("[getById()->error:传入参数无id]");
				return ResultBuilder.noBusinessResult();
			}
			String id1 = idJson.getString(CommonConstants.ID);
			RpcResponse<FacilitiesDataType> queryResult = facilitiesDataTypeQueryRpcService.getById(id1);
			if (queryResult != null && queryResult.isSuccess()) {
				logger.info("[getById()->success]");
				return ResultBuilder.successResult(queryResult.getSuccessValue(), "查询成功");
			}
			logger.error("[getById()->fail:设施扩展详情查询失败]");
			return ResultBuilder.failResult("设施扩展详情查询失败");
		} catch (Exception e) {
			logger.error("getById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	  * 
	  * @Description:设施扩展类型扩展列标识,扩展列名称重名校验
	  * @param 
	  * @return
	  */
	public Result<Boolean> validFacilitiesDataTypeName(String facilitiesDataTypeInfo) {
		logger.info(String.format("[validFacilitiesDataTypeName()->request params:%s]", facilitiesDataTypeInfo));
		try {
			// 验证参数是否合法
			Result<List<FacilitiesDataType>> result = ExceptionChecked.checkRequestParam(facilitiesDataTypeInfo);
			if (result != null) {
				logger.error(String.format("[validFacilitiesDataTypeName()->error:%s]", "参数验证失败:参数为空或格式错误!"));
				return ResultBuilder.failResult("参数验证失败:参数为空或格式错误!");
			}

			JSONObject jsonParam = JSON.parseObject(facilitiesDataTypeInfo);

			String facilitiesTypeId = jsonParam.getString("facilitiesTypeId");
			if (StringUtils.isBlank(facilitiesTypeId)) {
				logger.error("[validFacilitiesDataTypeName()->设施类型id不能为空!!!]");
				return ResultBuilder.invalidResult();
			}
			String sign = jsonParam.getString("sign");
			String name = jsonParam.getString("name");
			String id = jsonParam.getString("id");
			boolean signBlank = StringUtils.isBlank(sign);
			boolean nameBlank = StringUtils.isBlank(name);
			if (signBlank && nameBlank) {
				logger.error("[validFacilitiesDataTypeName()->设施扩展类型扩展列标识,扩展列名称不能同时为空!!!]");
				return ResultBuilder.invalidResult();
			}
			if (!signBlank && !nameBlank) {
				logger.error("[validFacilitiesDataTypeName()->设施扩展类型扩展列标识,扩展列名称不能同时校验!!!]");
				return ResultBuilder.invalidResult();
			}
			Map<String, String> map = Maps.newHashMap();
			map.put("facilitiesTypeId", facilitiesTypeId);
			// id不为空时,校验此id以外的扩展列标识,扩展列名称
			if (!StringUtils.isBlank(id)) {
				map.put("id", id);
			}

			// 用于拼接提示语
			StringBuffer message = new StringBuffer();
			if (!signBlank) {
				map.put("sign", sign);
				message.append("扩展列标识");
			}
			if (!nameBlank) {
				map.put("name", name);
				message.append("扩展列名称");
			}

			// 校验扩展列名称或 扩展列标识是否重名
			RpcResponse<Integer> validFacilitiesDataTypeName = facilitiesDataTypeQueryRpcService
					.validFacilitiesDataTypeName(map);
			if (validFacilitiesDataTypeName == null) {
				logger.error("设施扩展类型参数重名校验失败,接口返回值为null");
				return ResultBuilder.failResult("参数重名校验失败");
			}
			if (validFacilitiesDataTypeName.isSuccess()) {
				if (validFacilitiesDataTypeName.getSuccessValue() == 0) {
					logger.info(String.format("[getAllFacilitiesDataType()->success:%s]",
							validFacilitiesDataTypeName.getMessage()));
					return ResultBuilder.successResult(false, message + "未重复,可以使用");
				} else {
					logger.info(String.format("[getAllFacilitiesDataType()->success:%s]",
							validFacilitiesDataTypeName.getMessage()));
					return ResultBuilder.successResult(true, message + "重复,不能使用");
				}
			} else {
				logger.error(String.format("[getAllFacilitiesDataType()->fail:%s]",
						validFacilitiesDataTypeName.getMessage()));
				return ResultBuilder.failResult(message + "重复,不能使用");
			}
		} catch (Exception e) {
			logger.error("getAllFacilitiesDataType()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
