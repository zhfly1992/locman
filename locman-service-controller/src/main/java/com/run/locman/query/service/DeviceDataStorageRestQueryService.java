/*
 * File name: DeviceDataStorageRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月27日 ...
 * ... ...
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
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.dto.DeviceDataDto;
import com.run.locman.api.query.service.DeviceDataStorageQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月27日
 */

@Service
public class DeviceDataStorageRestQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceDataStorageQueryService	deviceDataStorageQueryService;



	public Result<PageInfo<DeviceDataDto>> queryDeviceDataStorageList(String param) {
		logger.info(String.format("[queryDeviceDataStorageList()->request param:%s]", param));
		try {
			Result<PageInfo<DeviceDataDto>> checkRequestParam = ExceptionChecked.checkRequestParam(param);
			if (null != checkRequestParam) {
				return checkRequestParam;
			}
			JSONObject json = JSONObject.parseObject(param);
			RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger, "queryDeviceDataStorageList",
					json, "pageNumParam", "pageSizeParam", "searchKey", "address", "areaCode", "bindingState", "synchronizationState");
			if (null != containsParamKey) {
				logger.error(String.format("[queryDeviceDataStorageList()->:%s]", containsParamKey.getMessage()));
				return ResultBuilder.failResult(containsParamKey.getMessage());
			}
			String pageNumParam = json.getString("pageNumParam");
			String pageSizeParam = json.getString("pageSizeParam");
	
			// 分页参数默认值
			int pageNum;
			int pageSize;
			if (StringUtils.isNumeric(pageNumParam)) {
				pageNum = Integer.parseInt(pageNumParam);
			} else {
				pageNum = 1;
			}
			if (StringUtils.isNumeric(pageSizeParam)) {
				pageSize = Integer.parseInt(pageSizeParam);
			} else {
				pageSize = 10;
			}
		
			RpcResponse<PageInfo<DeviceDataDto>> deviceDataStorageList = deviceDataStorageQueryService
					.queryDeviceDataStorageList(pageNum, pageSize, json);
			if (null == deviceDataStorageList) {
				logger.error("[queryTimeoutReportConfigList()->fail:查询列表失败!!!]");
				return ResultBuilder.failResult("查询列表失败");
			} else if (!deviceDataStorageList.isSuccess() || deviceDataStorageList.getSuccessValue() == null) {
				logger.error(
						String.format("[queryTimeoutReportConfigList()->fail:%s]", deviceDataStorageList.getMessage()));
				return ResultBuilder.failResult("查询列表失败");
			} else {
				logger.info(String.format("[queryTimeoutReportConfigList()->success:%s]",
						deviceDataStorageList.getMessage()));
				return ResultBuilder.successResult(deviceDataStorageList.getSuccessValue(),
						deviceDataStorageList.getMessage());
			}
		} catch (Exception e) {
			logger.error("queryDeviceDataStorageList()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}
	
	public Result<List<DeviceDataDto>> getDeviceDataStorageById(String id) {
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("查询id不能为空");
				return ResultBuilder.emptyResult();
			}
			List<String> idList = Lists.newArrayList();
			idList.add(id);
			RpcResponse<List<DeviceDataDto>> deviceDataStorage = deviceDataStorageQueryService
					.getDeviceDataStorageById(idList);
			if (null == deviceDataStorage) {
				logger.error("[getDeviceDataStorageById()->fail:查询失败!!!]");
				return ResultBuilder.failResult("查询失败");
			} else if (!deviceDataStorage.isSuccess() || deviceDataStorage.getSuccessValue() == null) {
				logger.error(
						String.format("[getDeviceDataStorageById()->fail:%s]", deviceDataStorage.getMessage()));
				return ResultBuilder.failResult("查询失败");
			} else {
				logger.info(String.format("[getDeviceDataStorageById()->success:%s]",
						deviceDataStorage.getMessage()));
				return ResultBuilder.successResult(deviceDataStorage.getSuccessValue(),
						deviceDataStorage.getMessage());
			}
		} catch (Exception e) {
			logger.error("getDeviceDataStorageById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}
	
	public Result<List<Map<String, Object>>> getAllArea() {
		try {
			RpcResponse<List<Map<String, Object>>> areaList = deviceDataStorageQueryService
					.getAllArea();
			if (null == areaList) {
				logger.error("[getDeviceDataStorageById()->fail:查询失败!!!]");
				return ResultBuilder.failResult("查询失败");
			} else if (!areaList.isSuccess() || areaList.getSuccessValue() == null) {
				logger.error(
						String.format("[getDeviceDataStorageById()->fail:%s]", areaList.getMessage()));
				return ResultBuilder.failResult("查询失败");
			} else {
				logger.info(String.format("[getDeviceDataStorageById()->success:%s]",
						areaList.getMessage()));
				return ResultBuilder.successResult(areaList.getSuccessValue(),
						areaList.getMessage());
			}
		} catch (Exception e) {
			logger.error("getDeviceDataStorageById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}
	
	public Result<Boolean> checkDeviceNumberExist(String checkParam) {
		try {
			logger.info(String.format("[checkDeviceNumberExist()->进入方法,参数:%s]", checkParam));
			Result<Boolean> checkRequestParam = ExceptionChecked.checkRequestParam(checkParam);
			if (null != checkRequestParam) {
				logger.error(checkRequestParam.getValue());
				return checkRequestParam;
			}
			JSONObject json = JSONObject.parseObject(checkParam);
			String deviceNumber = json.getString("deviceNumber");
			if (StringUtils.isBlank(deviceNumber)) {
				logger.error("设备编号不能为空");
				return ResultBuilder.failResult("设备编号不能为空");
			}
			String id = json.getString("id");
			
			 RpcResponse<Boolean> checkResult = deviceDataStorageQueryService.
					 checkDeviceNumberExist(deviceNumber, id);
			if (!checkResult.isSuccess()) {
				logger.error("[checkDeviceNumberExist()->fail:查询失败!!!]");
				return ResultBuilder.failResult("查询失败");
			} else if (checkResult.getSuccessValue()) {
				logger.info(
						String.format("[checkDeviceNumberExist()->设备编号已存在:%s]", deviceNumber));
				return ResultBuilder.successResult(checkResult.getSuccessValue(), "设备编号已存在");
			} else {
				logger.info(String.format("[checkDeviceNumberExist()->设备编号可以使用:%s]",
						deviceNumber));
				return ResultBuilder.successResult(checkResult.getSuccessValue(),
						"设备编号可以使用");
			}
		} catch (Exception e) {
			logger.error("checkDeviceNumberExist()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}
	
	public Result<Boolean> checkSerialNumberExist(String checkParam) {
		try {
			logger.info(String.format("[checkSerialNumberExist()->进入方法,参数:%s]", checkParam));
			Result<Boolean> checkRequestParam = ExceptionChecked.checkRequestParam(checkParam);
			if (null != checkRequestParam) {
				logger.error(checkRequestParam.getValue());
				return checkRequestParam;
			}
			JSONObject json = JSONObject.parseObject(checkParam);
			String serialNumber = json.getString("serialNumber");
			if (StringUtils.isBlank(serialNumber)) {
				logger.error("设施序列号不能为空");
				return ResultBuilder.failResult("序列号不能为空");
			}
			String id = json.getString("id");
			
			 RpcResponse<Boolean> checkResult = deviceDataStorageQueryService.
					 checkSerialNumberExist(serialNumber, id);
			if (!checkResult.isSuccess()) {
				logger.error("[checkSerialNumberExist()->fail:查询失败!!!]");
				return ResultBuilder.failResult("查询失败");
			} else if (checkResult.getSuccessValue()) {
				logger.info(
						String.format("[checkSerialNumberExist()->序列号已存在:%s]", serialNumber));
				return ResultBuilder.successResult(checkResult.getSuccessValue(), "序列号已存在");
			} else {
				logger.info(String.format("[checkSerialNumberExist()->序列号可以使用:%s]",
						serialNumber));
				return ResultBuilder.successResult(checkResult.getSuccessValue(),
						"序列号可以使用");
			}
		} catch (Exception e) {
			logger.error("checkSerialNumberExist()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}
	
}
