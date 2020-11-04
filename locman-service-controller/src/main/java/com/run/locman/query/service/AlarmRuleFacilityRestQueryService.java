/*
* File name: AlarmRuleFacilityRestQueryService.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			dingrunkang		2012年12月5日
* ...			...			...
*
***************************************************/
package com.run.locman.query.service;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.AlarmRuleFacilityQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * 
 * @Description: 自定义告警规则配置设施与设备信息查询服务
 * @author: dingrunkang
 * @version: 3.0, 2012年12月5日
 * @param <AlarmRuleFacilitiesQueryServiceImpl>
 */

@Service
public class AlarmRuleFacilityRestQueryService {
	/**
	 * 自定义告警规则配置设施与设信息查询服务
	 */
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private AlarmRuleFacilityQueryService	alarmRuleFacilityQueryService;




	/**
	 * @Description: 查询告警设备绑定信息
	 * @param deviceId
	 * @return
	 */
	//此接口没有被调用过	2018年3月13日16:06:41
/*	@SuppressWarnings("unchecked")
	public Result<PageInfo<Map<String, Object>>> getAlarmRuleDeviceBindingState(String alarmstate) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(alarmstate);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}

			JSONObject paramJson = JSONObject.parseObject(alarmstate);

			if (!paramJson.containsKey("pageNo")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!paramJson.containsKey("pageSize")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!StringUtils.isNumeric(paramJson.getString("pageNo"))) {
				return ResultBuilder.invalidResult();
			}
			if (!StringUtils.isNumeric(paramJson.getString("pageSize"))) {
				return ResultBuilder.invalidResult();
			}
			if (!StringUtils.isNumeric(paramJson.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmRulePage()->invalid：页大小必须为数字！]");
				return ResultBuilder.failResult("页大小必须为数字");
			}
			if (!StringUtils.isNumeric(paramJson.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmRulePage()->invalid：页码必须为数字！]");
				return ResultBuilder.failResult("页码必须为数字");
			}

			JSONObject parseObject = JSON.parseObject(alarmstate);
			RpcResponse<List<Map<String, Object>>> res = alarmRuleFacilityQueryService
					.getAlarmRuleFacilityBindingStatus(parseObject);

			Integer pageNo = paramJson.getIntValue(FacilitiesContants.PAGE_NO);
			Integer pageSize = paramJson.getIntValue(FacilitiesContants.PAGE_SIZE);

			PageHelper.startPage(pageNo, pageSize);
			// 封装组织信息
			PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(res.getSuccessValue());

			if (res.isSuccess()) {
				return ResultBuilder.successResult(pageInfo, res.getMessage());
			} else {
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("getAlarmRuleDeviceBindingState()->exception",e);
			return ResultBuilder.exceptionResult(e);
		}
	}

*/

	public Result<PageInfo<Map<String, Object>>> getFacilityDeviceList(String queryParams) {
		logger.info(String.format("[getFacilityDeviceList()->request params:%s]", queryParams));
		try {
			if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> result = alarmRuleFacilityQueryService
					.getFacilityDeviceList(json);
			if (result.isSuccess()) {
				logger.info("[getFacilityDeviceList()->success:自定义告警规则配置设施与设备信息查询成功]");
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			}
			logger.error("[getFacilityDeviceList()->fail:自定义告警规则配置设施与设备信息查询失败]");
			return ResultBuilder.failResult(result.getMessage());
		} catch (Exception e) {
			logger.error("getFacilityDeviceList()->exception",e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
