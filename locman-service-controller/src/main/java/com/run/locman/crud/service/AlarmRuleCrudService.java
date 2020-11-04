/*
 * File name: AlarmRuleCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年10月27日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.AlarmRuleService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 告警规则cud
 * @author: lkc
 * @version: 1.0, 2017年10月27日
 */
@Service
public class AlarmRuleCrudService {
	@Autowired
	public AlarmRuleService	alarmRuleService;

	private Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);



	public Result<String> alarmRuleAdd(String alarmInfo) {
		try {
			logger.info(String.format("[alarmRuleAdd()->request params:%s]", alarmInfo));
			// 参数校验
			Result<String> result = ExceptionChecked.checkRequestParam(alarmInfo);
			if (result != null) {
				logger.error(String.format("[alarmRuleAdd()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonObject = JSON.parseObject(alarmInfo);
			RpcResponse<String> res = alarmRuleService.alarmRuleAdd(jsonObject);
			if (res.isSuccess()) {
				logger.info(String.format("[alarmRuleAdd()->success:%s]",res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[alarmRuleAdd()->error:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("alarmRuleAdd()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> alarmRuleUpdate(String alarmInfo) {
		try {
			logger.info(String.format("[alarmRuleUpdate()->request params:%s]", alarmInfo));
			// 参数校验
			Result<String> result = ExceptionChecked.checkRequestParam(alarmInfo);
			if (result != null) {
				logger.error(String.format("[alarmRuleUpdate()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonObject = JSON.parseObject(alarmInfo);
			RpcResponse<String> res = alarmRuleService.alarmRuleUpdate(jsonObject);
			if (res.isSuccess()) {
				logger.info(String.format("[alarmRuleUpdate()->success:%s]",res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[alarmRuleUpdate()->error:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("alarmRuleUpdate()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Boolean> alarmRuleDel(String id, String userId) {
		try {
			logger.info(String.format("[alarmRuleDel()->request params-id:%s,userid:%s]", id,userId));
			RpcResponse<Boolean> res = alarmRuleService.alarmRuleDel(id, userId);
			if (res.isSuccess()) {
				logger.info(String.format("[alarmRuleDel()->success:%s]",res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[alarmRuleDel()->error:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("alarmRuleDel()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Boolean> alarmRuleState(String alarmInfo) {
		try {
			logger.info(String.format("[alarmRuleState()->request params:%s]", alarmInfo));
			// 参数校验
			Result<String> result = ExceptionChecked.checkRequestParam(alarmInfo);
			if (result != null) {
				logger.error(String.format("[alarmRuleState()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(alarmInfo);
			RpcResponse<Boolean> res = alarmRuleService.alarmRuleState(parseObject);
			if (res.isSuccess()) {
				logger.info(String.format("[alarmRuleState()->success:%s]",res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[alarmRuleState()->error:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("alarmRuleState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Boolean> alarmRulePublish(String alarmInfo) {
		try {
			logger.info(String.format("[alarmRulePublish()->request params:%s]", alarmInfo));
			// 参数校验
			Result<String> result = ExceptionChecked.checkRequestParam(alarmInfo);
			if (result != null) {
				logger.error(String.format("[alarmRulePublish()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(alarmInfo);
			RpcResponse<Boolean> res = alarmRuleService.alarmRulePublish(parseObject);
			if (res.isSuccess()) {
				logger.info(String.format("[alarmRulePublish()->success:%s]",res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[alarmRulePublish()->error:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("alarmRulePublish()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
