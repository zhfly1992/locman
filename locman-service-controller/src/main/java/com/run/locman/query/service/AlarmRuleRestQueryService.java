/*
 * File name: AlarmRuleRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年10月31日 ... ... ...
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
import com.google.common.collect.Maps;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.AlarmRule;
import com.run.locman.api.query.service.AlarmRuleQueryService;
import com.run.locman.api.query.service.DeviceTypeQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DistributionPowersConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.usc.api.base.util.ParamChecker;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * @Description: 告警规则查询
 * @author: 田明
 * @version: 1.0, 2017年10月31日
 */
@Service
public class AlarmRuleRestQueryService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private AlarmRuleQueryService	alarmRuleQueryService;

	@Autowired
	private UserBaseQueryService	userQueryRpcService;

	@SuppressWarnings("unused")
	@Autowired
	private DeviceTypeQueryService	deviceTypeQueryService;



	public Result<List<Map<String, Object>>> findDataPointByDeviceTypeId(String queryParams) {
		logger.info(String.format("[findDataPointByDeviceTypeId()->request params:%s]", queryParams));
		if (ParamChecker.isNotMatchJson(queryParams)) {
			logger.error(String.format("[orderPowerless()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		JSONObject paramJson = JSONObject.parseObject(queryParams);
		if (!paramJson.containsKey(DistributionPowersConstants.ACCESSSECRET)) {
			logger.error(String.format("[findDataPointByDeviceTypeId()->error:%s--%s]",
					LogMessageContants.NO_PARAMETER_EXISTS, DistributionPowersConstants.ACCESSSECRET));
			return ResultBuilder.noBusinessResult();
		}
		if (!paramJson.containsKey(CommonConstants.DEVICETYPEID)) {
			logger.error(String.format("[findDataPointByDeviceTypeId()->error:%s--%s]",
					LogMessageContants.NO_PARAMETER_EXISTS, CommonConstants.DEVICETYPEID));
			return ResultBuilder.noBusinessResult();
		}
		String accessSecret = paramJson.getString(DistributionPowersConstants.ACCESSSECRET);
		String deviceTypeId = paramJson.getString(CommonConstants.DEVICETYPEID);
		try {
			RpcResponse<List<Map<String, Object>>> dataPointList = alarmRuleQueryService
					.findDataPointByDeviceTypeId(deviceTypeId, accessSecret);
			if (!dataPointList.isSuccess()) {
				logger.error(String.format("[findDataPointByDeviceTypeId()->fail:%s]", dataPointList.getMessage()));
				return ResultBuilder.failResult(dataPointList.getMessage());
			}
			logger.info(String.format("[findDataPointByDeviceTypeId()->success:%s]", dataPointList.getMessage()));
			return ResultBuilder.successResult(dataPointList.getSuccessValue(), dataPointList.getMessage());
		} catch (Exception e) {
			logger.error("findDataPointByDeviceTypeId()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("rawtypes")
	public Result<PageInfo<Map<String, Object>>> findAlarmRuleListByNameAndDeviceTypeId(String requestparams)
			throws Exception {
		logger.info(String.format("[findAlarmRuleListByNameAndDeviceTypeId()->request params:%s]", requestparams));
		if (ParamChecker.isNotMatchJson(requestparams)) {
			logger.error(String.format("[findAlarmRuleListByNameAndDeviceTypeId()->error:%s]",
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		JSONObject paramJson = JSONObject.parseObject(requestparams);
		String accessSecret = paramJson.getString(DistributionPowersConstants.ACCESSSECRET);
		String pageSizeStr = paramJson.getString(CommonConstants.PAGE_SIZE);
		int pageSize = Integer.valueOf(pageSizeStr);
		String pageNumStr = paramJson.getString(CommonConstants.PAGE_NUM);
		Result<Object> paramCheck = paramCheck(paramJson, pageSizeStr, pageNumStr);
		if (null != paramCheck) {
			return ResultBuilder.failResult(paramCheck.getResultStatus().getResultMessage());
		}
		Map<String, String> map = Maps.newHashMap();
		int pageNum = Integer.valueOf(pageNumStr);
		mapPut(paramJson, map);
		try {
			RpcResponse<PageInfo<Map<String, Object>>> alarmRuleList = alarmRuleQueryService
					.findAlarmRuleListByNameAndDeviceTypeId(accessSecret, pageNum, pageSize, map);
			if (!alarmRuleList.isSuccess()) {
				logger.error(String.format("[findAlarmRuleListByNameAndDeviceTypeId()->error:%s]",
						alarmRuleList.getMessage()));
				return ResultBuilder.failResult(alarmRuleList.getMessage());
			}
			PageInfo<Map<String, Object>> info = alarmRuleList.getSuccessValue();
			List<Map<String, Object>> alarmRule = info.getList();
			if (alarmRule != null && alarmRule.size() != 0) {
				for (int i = 0; i < alarmRule.size(); i++) {
					RpcResponse userInfo = userQueryRpcService.getUserByUserId((String) alarmRule.get(i).get("userId"));
					if (null != userInfo && userInfo.getSuccessValue() != null) {
						Map map1 = (Map) userInfo.getSuccessValue();
						if (map1.get("userName") != null) {
							alarmRule.get(i).put("userName", map1.get("userName"));
						} else {
							alarmRule.get(i).put("userName", map1.get("loginAccount"));
						}
					} else {
						alarmRule.get(i).put("userName", "");
						logger.error("[findAlarmRuleListByNameAndDeviceTypeId()->warn：数据有误:用户id不存在!]");
					}
				}
			}
			logger.info("[findAlarmRuleListByNameAndDeviceTypeId->success]");
			return ResultBuilder.successResult(info, alarmRuleList.getMessage());
		} catch (Exception e) {
			logger.error("findAlarmRuleListByNameAndDeviceTypeId()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param paramJson
	 * @param map
	 */

	private void mapPut(JSONObject paramJson, Map<String, String> map) {
		if (paramJson.containsKey(CommonConstants.RULENAME)) {
			map.put(CommonConstants.RULENAME, paramJson.getString(CommonConstants.RULENAME));
		}
		if (paramJson.containsKey(CommonConstants.DEVICETYPEID)) {
			map.put(CommonConstants.DEVICETYPEID, paramJson.getString(CommonConstants.DEVICETYPEID));
		}
		if (paramJson.containsKey(CommonConstants.RULETYPE)) {
			map.put(CommonConstants.RULETYPE, paramJson.getString(CommonConstants.RULETYPE));
		}
	}



	/**
	 * @Description:
	 * @param paramJson
	 * @param pageSizeStr
	 * @param pageNumStr
	 */

	private Result<Object> paramCheck(JSONObject paramJson, String pageSizeStr, String pageNumStr) {
		if (!paramJson.containsKey(DistributionPowersConstants.ACCESSSECRET)) {
			logger.error("[findAlarmRuleListByNameAndDeviceTypeId()->error:没有参数accessSecret]");
			return ResultBuilder.noBusinessResult();
		}
		if (!paramJson.containsKey(CommonConstants.PAGE_SIZE)) {
			logger.error("[findAlarmRuleListByNameAndDeviceTypeId()->error:缺少参数pageSize]");
			return ResultBuilder.noBusinessResult();
		}
		if (!StringUtils.isNumeric(pageSizeStr)) {
			logger.error("[findAlarmRuleListByNameAndDeviceTypeId()->error:pageSize传入数字异常!]");
			return ResultBuilder.failResult("查询分页信息,传入数字异常!");
		}
		if (!paramJson.containsKey(CommonConstants.PAGE_NUM)) {
			logger.error("[findAlarmRuleListByNameAndDeviceTypeId()->error:缺少参数pageNum]");
			return ResultBuilder.noBusinessResult();
		}
		if (!StringUtils.isNumeric(pageNumStr)) {
			logger.error("[findAlarmRuleListByNameAndDeviceTypeId()->error:pageNum传入数字异常!]");
			return ResultBuilder.failResult("查询分页信息,传入数字异常!");
		}
		return null;
	}



	public Result<AlarmRule> findByRuleId(String id) {
		logger.info(String.format("[findByRuleId()->request params--id:%s]", id));
		if (id == null || "".equals(id)) {
			logger.error("[findByRuleId()->error:传入参数错误]");
			return ResultBuilder.noBusinessResult();
		}
		try {
			RpcResponse<AlarmRule> alarmRuleRpcResponse = alarmRuleQueryService.findByRuleId(id);
			if (!alarmRuleRpcResponse.isSuccess()) {
				logger.error(String.format("[findByRuleId()->fail:%s]", alarmRuleRpcResponse.getMessage()));
				return ResultBuilder.failResult(alarmRuleRpcResponse.getMessage());
			}
			logger.info(String.format("[findByRuleId()->success:%s]", alarmRuleRpcResponse.getMessage()));
			return ResultBuilder.successResult(alarmRuleRpcResponse.getSuccessValue(),
					alarmRuleRpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("findByRuleId()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
