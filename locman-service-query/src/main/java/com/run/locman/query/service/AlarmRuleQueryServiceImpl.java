/*
 * File name: AlarmRuleQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年10月31日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.AlarmRule;
import com.run.locman.api.query.repository.AlarmRuleQueryRepository;
import com.run.locman.api.query.service.AlarmRuleQueryService;
import com.run.locman.constants.CommonConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 告警规则query实现类
 * @author: qulong
 * @version: 1.0, 2017年10月31日
 */
public class AlarmRuleQueryServiceImpl implements AlarmRuleQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private AlarmRuleQueryRepository	alarmRuleQueryRepository;



	@Override
	public RpcResponse<List<Map<String, Object>>> findDataPointByDeviceTypeId(String deviceTypeId,
			String accessSecret) {
		logger.info(String.format("[findDataPointByDeviceTypeId()方法执行开始...,参数：【%s】【%s】]", deviceTypeId, accessSecret));
		try {
			if (StringUtils.isBlank(deviceTypeId)) {
				logger.error("[findDataPointByDeviceTypeId()->invalid：查询状态属性集合时,设备类型id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询状态属性集合信息：设备类型id不能为空！");
			}
			Map<String, String> map = new HashMap<>(16);
			map.put("deviceTypeId", deviceTypeId);
			map.put("accessSecret", accessSecret);
			List<Map<String, Object>> dataPointList = alarmRuleQueryRepository.findDataPointByDeviceTypeId(map);
			logger.info(String.format("[findDataPointByDeviceTypeId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询状态属性集合成功", dataPointList);
		} catch (Exception e) {
			logger.error("findDataPointByDeviceTypeId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> findAlarmRuleListByNameAndDeviceTypeId(String accessSecret,
			int pageNum, int pageSize, Map<String, String> searchParam) {
		logger.info(String.format("[findAlarmRuleListByNameAndDeviceTypeId()方法执行开始...,参数：【%s】【%s】【%s】【%s】]",
				accessSecret, pageNum, pageSize, searchParam));
		if (accessSecret == null) {
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空");
		}
		if (searchParam != null) {
			searchParam.put("accessSecret", accessSecret);
		} else {
			searchParam = new HashMap<>(16);
			searchParam.put("accessSecret", accessSecret);
		}
		try {
			PageHelper.startPage(pageNum, pageSize);
			List<Map<String, Object>> alarmRuleList = alarmRuleQueryRepository
					.findAlarmRuleListByNameAndDeviceTypeId(searchParam);
			if (alarmRuleList == null) {
				logger.error("[findAlarmRuleListByNameAndDeviceTypeId()->failed: 查询失败，数据库异常]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败，数据库异常");
			}
			PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(alarmRuleList);
			if (CollectionUtils.isEmpty(alarmRuleList)) {
				return RpcResponseBuilder.buildSuccessRpcResp("暂无数据", pageInfo);
			}
			logger.info(String.format("[findAlarmRuleListByNameAndDeviceTypeId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", pageInfo);
		} catch (Exception e) {
			logger.error("findAlarmRuleListByNameAndDeviceTypeId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<AlarmRule> findByRuleId(String id) {
		logger.info(String.format("[findByRuleId()方法执行开始...,参数：【%s】]", id));
		if (id == null) {
			return RpcResponseBuilder.buildErrorRpcResp("主键ID不能为空");
		}
		try {
			AlarmRule alarmRule = alarmRuleQueryRepository.findByRuleId(id);
			if (alarmRule == null) {
				logger.error("[findByRuleId()->failed: 查询失败，数据库异常]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败，数据库异常");
			}
			logger.info(String.format("[findByRuleId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", alarmRule);
		} catch (Exception e) {
			logger.error("findByRuleId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<AlarmRule>> queryAlarmRuleByDeviceId(String deviceId, String accessSecret) {
		logger.info(String.format("[queryAlarmRuleByDeviceId()方法执行开始...,参数：【%s】【%s】]", deviceId, accessSecret));
		try {
			if (deviceId == null || deviceId.isEmpty()) {
				logger.error("[queryAlarmRuleByDeviceId()->invalid：设备ID不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备ID不能为空");
			}
			List<AlarmRule> list = alarmRuleQueryRepository.queryAlarmRuleByDeviceId(deviceId, accessSecret);
			logger.info("[queryAlarmRuleByDeviceId()->success：查询成功！]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", list);
		} catch (Exception e) {
			logger.error("queryAlarmRuleByDeviceId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp("查询失败，异常");
		}
	}



	@Override
	public RpcResponse<List<AlarmRule>> getByDeviceTypeId(String deviceTypeId, String accessSecret) {
		logger.info(String.format("[getByDeviceTypeId()方法执行开始...,参数：【%s】【%s】]", deviceTypeId, accessSecret));
		try {
			if (deviceTypeId == null || deviceTypeId.isEmpty()) {
				logger.error("[getByDeviceTypeId()->invalid：设备类型ID不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备类型ID不能为空");
			}
			List<AlarmRule> alarmRules = alarmRuleQueryRepository.getByDeviceTypeId(deviceTypeId, accessSecret);
			if (alarmRules != null && alarmRules.size() > 0) {
				logger.info("[getByDeviceTypeId()->success：查询成功！]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", alarmRules);
			}
			logger.warn("[getByDeviceTypeId()->fail：查询失败，暂无数据！]");
			return RpcResponseBuilder.buildErrorRpcResp("暂无数据");
		} catch (Exception e) {
			logger.error("getByDeviceTypeId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp("查询失败，异常");
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmRuleQueryService#geAlltBasicAlarmRule()
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getAllBasicAlarmRule() {
		logger.info(String.format("[getAllBasicAlarmRule()方法执行开始..."));
		try {
			List<Map<String, Object>> getAlltBasicAlarmRule = alarmRuleQueryRepository.getAllBasicAlarmRule();
			if (getAlltBasicAlarmRule == null) {
				logger.warn("[getAllBasicAlarmRule()->fail：查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败!");
			} else {
				logger.info("[getAllBasicAlarmRule()->success：查询成功！]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", getAlltBasicAlarmRule);
			}

		} catch (Exception e) {
			logger.error("getAllBasicAlarmRule()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp("查询失败，异常");
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmRuleQueryService#getAlltAlarmRule(java.lang.String)
	 */
	@Override
	public RpcResponse<List<AlarmRule>> getAllAlarmRule(String accessSecret) {
		logger.info(String.format("[getAllAlarmRule()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[getAllAlarmRule()-->%s]", "接入方秘钥不能为空!"));
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
			}
			List<AlarmRule> getAlltBasicAlarmRule = alarmRuleQueryRepository.getAllAlarmRule(accessSecret);
			if (getAlltBasicAlarmRule == null) {
				logger.warn("[getAllAlarmRule()->fail：查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败！");
			} else {
				logger.info("[getAllAlarmRule()->success：查询成功！]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", getAlltBasicAlarmRule);
			}

		} catch (Exception e) {
			logger.error("getAllAlarmRule()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp("查询失败，异常");
		}
	}

}
