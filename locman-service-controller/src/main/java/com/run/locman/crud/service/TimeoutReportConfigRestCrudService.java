/*
 * File name: TimeoutReportConfigRestCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年6月25日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.TimeoutReportConfigCrudService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年6月25日
 */

@Service
public class TimeoutReportConfigRestCrudService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private TimeoutReportConfigCrudService	timeoutReportConfigCrudService;



	public Result<String> addTimeoutReportConfig(String congigInfo) {
		logger.info(String.format("[addTimeoutReportConfig()->request params:%s]", congigInfo));
		if (null != ExceptionChecked.checkRequestParam(congigInfo)) {
			logger.error("数据不满足json格式!");
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject congigInfoJson = JSONObject.parseObject(congigInfo);
			RpcResponse<String> addTimeoutReportConfig = timeoutReportConfigCrudService
					.addTimeoutReportConfig(congigInfoJson);
			String successValue = addTimeoutReportConfig.getSuccessValue();
			if (addTimeoutReportConfig.isSuccess() && null != successValue) {
				logger.info("addTimeoutReportConfig()-->增加成功");
				return ResultBuilder.successResult(successValue, addTimeoutReportConfig.getMessage());
			} else {
				logger.info("addTimeoutReportConfig()-->增加失败:" + addTimeoutReportConfig.getMessage());
				return ResultBuilder.failResult(addTimeoutReportConfig.getMessage());
			}
		} catch (Exception e) {
			logger.error("addTimeoutReportConfig()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public Result<String> delTimeoutReportConfig(String congigIdInfo) {
		logger.info(String.format("[delTimeoutReportConfig()->request params:%s]", congigIdInfo));
		try {
			JSONObject json = JSONObject.parseObject(congigIdInfo);
			JSONArray jsonArray = json.getJSONArray("id");
			if (jsonArray == null || jsonArray.isEmpty()) {
				logger.error("delTimeoutReportConfig()-->超时配置ID不能为空!!!");
				return ResultBuilder.invalidResult();
			}
			List<String> congigIds = jsonArray.toJavaList(String.class);
			if (congigIds == null || congigIds.isEmpty()) {
				logger.error("delTimeoutReportConfig()-->超时配置ID不能为空!!!");
				return ResultBuilder.invalidResult();
			}
			/*String[] split = congigIdInfo.split(",");
			if (split.length <= 0) {
				return ResultBuilder.failResult("delTimeoutReportConfig()-->超时配置ID格式有误!!!");
			}
			List<String> congigIds = Arrays.asList(split);*/
			RpcResponse<String> delResult = timeoutReportConfigCrudService.delTimeoutReportConfig(congigIds);
			if (null == delResult) {
				return ResultBuilder.failResult("删除配置失败");
			}
			if (delResult.isSuccess()) {
				logger.info("delTimeoutReportConfig()-->删除成功");
				return ResultBuilder.successResult(delResult.getMessage(), delResult.getMessage());
			} else {
				logger.info("delTimeoutReportConfig()-->删除失败:" + delResult.getMessage());
				return ResultBuilder.failResult(delResult.getMessage());
			}
		} catch (Exception e) {
			logger.error("delTimeoutReportConfig()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public Result<String> updateTimeoutReportConfig(String congigInfo) {
		logger.info(String.format("[updateTimeoutReportConfig()->request params:%s]", congigInfo));
		if (null != ExceptionChecked.checkRequestParam(congigInfo)) {
			logger.error("数据不满足json格式!");
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject congigInfoJson = JSONObject.parseObject(congigInfo);
			RpcResponse<String> addTimeoutReportConfig = timeoutReportConfigCrudService
					.updateTimeoutReportConfig(congigInfoJson);
			String successValue = addTimeoutReportConfig.getSuccessValue();
			if (addTimeoutReportConfig.isSuccess() && null != successValue) {
				logger.info("updateTimeoutReportConfig()-->success");
				return ResultBuilder.successResult(successValue, addTimeoutReportConfig.getMessage());
			} else {
				logger.info("updateTimeoutReportConfig()-->fail:" + addTimeoutReportConfig.getMessage());
				return ResultBuilder.failResult(addTimeoutReportConfig.getMessage());
			}
		} catch (Exception e) {
			logger.error("updateTimeoutReportConfig()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}

}
