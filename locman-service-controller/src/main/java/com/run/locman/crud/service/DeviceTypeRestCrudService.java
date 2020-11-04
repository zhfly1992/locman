/*
 * File name: DeviceTypeCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2019年1月30日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.crud.service;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.base.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.DeviceTypeCudService;

import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 设备类型编辑
 * @author: 张贺
 * @version: 1.0, 2019年1月30日
 */
@Service
public class DeviceTypeRestCrudService {
	@Autowired
	DeviceTypeCudService	deviceTypeCudService;

	private Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);



	public Result<String> editDeviceType(String param) {
		logger.info(String.format("[editDeviceType()->request prarm:%s]", param));

		// 校验参数是否为空
		if (StringUtils.isBlank(param)) {
			logger.error(String.format("[editDeviceType()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.failResult("参数为空");
		}

		// 校验参数是否为json格式
		if (ParamChecker.isNotMatchJson(param)) {
			logger.error(String.format("[editDeviceType()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.failResult("参数必须为json格式");
		}
		
		
		JSONObject jsonObject = JSONObject.parseObject(param);

		// 校验参数是否包含业务参数
		RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger, "editDeviceType", jsonObject,
				"id", "deviceTypeName", "updateBy", "accessSecret", "typeSign");
		if (null != containsParamKey) {
			logger.error(String.format("[editDeviceType()->error:%s]", containsParamKey.getMessage()));
			return ResultBuilder.failResult(containsParamKey.getMessage());
		}

		// 校验参数业务参数是否为空
		RpcResponse<Object> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger, "editDeviceType", jsonObject,
				"id", "deviceTypeName", "updateBy", "accessSecret");
		if (null != checkBusinessKey) {
			logger.error(String.format("[editDeviceType()->error:%s]", checkBusinessKey.getMessage()));
			return ResultBuilder.failResult(checkBusinessKey.getMessage());
		}
		
		try {
			RpcResponse<String> response = deviceTypeCudService.editDeviceType(jsonObject);
			if (!response.isSuccess()) {
				logger.error(String.format("[editDeviceType()->fail:%s]", response.getMessage()));
				return ResultBuilder.failResult(response.getMessage());
			}
			logger.info(String.format("[editDeviceType()->success:%s]", response.getMessage()));
			return ResultBuilder.successResult(response.getSuccessValue(), response.getMessage());

		} catch (Exception e) {
			logger.error(String.format("[editDeviceType()->exception:%s]", e));
			return ResultBuilder.exceptionResult(e);
		}
	}
}
