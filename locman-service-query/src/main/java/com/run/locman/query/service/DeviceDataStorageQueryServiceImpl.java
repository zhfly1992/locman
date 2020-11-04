/*
 * File name: DeviceDataStorageQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月28日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.DeviceDataDto;
import com.run.locman.api.query.repository.DeviceDataStorageQueryRepository;
import com.run.locman.api.query.service.DeviceDataStorageQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月28日
 */

public class DeviceDataStorageQueryServiceImpl implements DeviceDataStorageQueryService {

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceDataStorageQueryRepository	deviceDataStorageQueryRepository;



	/**
	 * @see com.run.locman.api.query.service.DeviceDataStorageQueryService#queryDeviceDataStorageList(int,
	 *      int, com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<PageInfo<DeviceDataDto>> queryDeviceDataStorageList(int pageNum, int pageSize, JSONObject json) {
		if (0 > pageNum) {
			pageNum = 1;
		}
		if (0 > pageSize) {
			pageSize = 10;
		}

		if (json.isEmpty()) {
			logger.error("json参数不能为空!!!");
			return RpcResponseBuilder.buildErrorRpcResp("json参数不能为空!!!");
		}
		logger.info(String.format("[queryDeviceDataStorageList()->request param:pageNum %s ; pageSize %s ; json %s]",
				pageNum, pageSize, json));
		try {
			RpcResponse<PageInfo<DeviceDataDto>> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"queryDeviceDataStorageList", json, "pageNumParam", "pageSizeParam", "searchKey", "address",
					"areaCode", "bindingState", "synchronizationState");
			if (null != containsParamKey) {
				logger.error(String.format("[queryDeviceDataStorageList()->:%s]", containsParamKey.getMessage()));
				return containsParamKey;
			}
			logger.info(String.format("[queryDeviceDataStorageList()方法,参数：pageNum:%s -- pageSize:%s -- json:%s]",
					pageNum, pageSize, json));
			PageHelper.startPage(pageNum, pageSize);
			List<DeviceDataDto> deviceDataStorageList = deviceDataStorageQueryRepository
					.queryDeviceDataStorageList(json);
			PageInfo<DeviceDataDto> page = new PageInfo<>(deviceDataStorageList);
			logger.info(LogMessageContants.QUERY_SUCCESS);
			logger.info(String.format("[queryDeviceDataStorageList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, page);
		} catch (Exception e) {
			logger.error("queryDeviceDataStorageList()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}



	/**
	 * @see com.run.locman.api.query.service.DeviceDataStorageQueryService#getDeviceDataStorageById(java.util.List)
	 */
	@Override
	public RpcResponse<List<DeviceDataDto>> getDeviceDataStorageById(List<String> idList) {
		if (idList.isEmpty()) {
			logger.error("查询id不能为空");
			return RpcResponseBuilder.buildErrorRpcResp("查询id不能为空");
		}
		logger.info(String.format("[getDeviceDataStorageById()->request param:%s]", idList));
		try {
			List<DeviceDataDto> deviceDataStorageList = deviceDataStorageQueryRepository
					.getDeviceDataStorageById(idList);
			if (null == deviceDataStorageList) {
				logger.error("[getDeviceDataStorageById()->fail:查询失败!!!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			} else {
				logger.info("[getDeviceDataStorageById()->success:查询成功!!!]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", deviceDataStorageList);
			}
		} catch (Exception e) {
			logger.error("getDeviceDataStorageById()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceDataStorageQueryService#getAllArea()
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getAllArea() {
		try {
			List<Map<String, Object>> areaList = deviceDataStorageQueryRepository.getAllArea();
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", areaList);
		} catch (Exception e) {
			logger.error("getAllArea()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceDataStorageQueryService#checkDeviceNumberExist(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkDeviceNumberExist(String deviceNumber, String id) {
		try {
			if (StringUtils.isBlank(deviceNumber)) {
				logger.error("设备编号不能为空");
				return RpcResponseBuilder.buildErrorRpcResp("设备编号不能为空");
			}
			logger.info(String.format("[checkDeviceNumberExist()->request param:deviceNumber %s ; id %s]", deviceNumber, id));
			int num = deviceDataStorageQueryRepository.checkDeviceNumberExist(deviceNumber, id);
			if (num > 0) {
				logger.info("设备编号重复" + deviceNumber);
				return RpcResponseBuilder.buildSuccessRpcResp("设备编号重复", true);
			} else {
				logger.info("设备编号不重复" + deviceNumber);
				return RpcResponseBuilder.buildSuccessRpcResp("设备编号不重复", false);
			}
		} catch (Exception e) {
			logger.error("checkDeviceNumberExist()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DeviceDataStorageQueryService#checkSerialNumberExist(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkSerialNumberExist(String serialNumber, String id) {
		try {
			if (StringUtils.isBlank(serialNumber)) {
				logger.error("设施序列号不能为空");
				return RpcResponseBuilder.buildErrorRpcResp("设施序列号不能为空");
			}
			logger.info(String.format("[checkSerialNumberExist()->request param:serialNumber %s ; id %s]", serialNumber, id));
			int num = deviceDataStorageQueryRepository.checkSerialNumberExist(serialNumber, id);
			if (num > 0) {
				logger.error("设施序列号重复" + serialNumber);
				return RpcResponseBuilder.buildSuccessRpcResp("设施序列号重复", true);
			} else {
				logger.error("设施序列号不重复" + serialNumber);
				return RpcResponseBuilder.buildSuccessRpcResp("设施序列号不重复", false);
			}
		} catch (Exception e) {
			logger.error("checkSerialNumberExist()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}

}
