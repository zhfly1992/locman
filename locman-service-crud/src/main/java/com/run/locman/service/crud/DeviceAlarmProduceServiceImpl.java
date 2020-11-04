/*
 * File name: DeviceAlarmProduceServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年10月19日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.DeviceAlarmProduceService;
import com.run.locman.api.drools.service.AlarmRuleInvokInterface;
import com.run.locman.api.entity.AlarmRule;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.query.service.AlarmRuleQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;

/**
 * @Description: 设备告警实现实体
 * @author: 王胜
 * @version: 1.0, 2018年10月19日
 */

public class DeviceAlarmProduceServiceImpl implements DeviceAlarmProduceService {

	@Autowired
	private FacilityDeviceQueryService	facilityDeviceQueryService;

	@Autowired
	private AlarmRuleQueryService		alarmRuleQueryService;

	@Autowired
	private AlarmRuleInvokInterface		alarmRuleInvokInterface;

	@Autowired
	private FacilitiesQueryService		facilitiesQueryService;

	@Autowired
	DeviceQueryService					deviceQueryService;


	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);



	/**
	 * @return 
	 * @see com.run.locman.api.crud.service.DeviceAlarmProduceService#alarmProduce(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> alarmProduce(JSONObject deviceRepotedData, String deviceId, String timestamp) {
		try {
			logger.info(String.format("[alarmProduce()->request params:%s,%s,%s]", deviceRepotedData,
					deviceId, timestamp));
			if (null == deviceRepotedData || deviceRepotedData.isEmpty()) {
				logger.error("[alarmProduce()->invalid：设备上报数据为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备上报数据为空!");
			}
			if (StringUtils.isBlank(deviceId) || StringUtils.isBlank(timestamp)) {
				logger.error("[alarmProduce()->invalid：设备id为空或上报时间为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备id为空或上报时间为空!");
			}
			// 设备上报时间
			String reportTime = UtilTool.timeStampToDate(Long.parseLong(timestamp));
			RpcResponse<String> deviceBindingState = facilityDeviceQueryService.queryDeviceBindingState(deviceId);
			if (!deviceBindingState.isSuccess()) {
				logger.error("[alarmProduce()->invalid：查询设备同设施的绑定状态失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询设备同设施的绑定状态失败!");
			}
			// 设备未绑定
			if ("".equals(deviceBindingState.getSuccessValue()) || deviceBindingState.getSuccessValue() == null) {
				logger.error("[alarmProduce()->invalid：设备：" + deviceId + "未绑定设施！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备未绑定设施！");
			}
			// 获取所绑定设施
			String facilityId = deviceBindingState.getSuccessValue();
			// 获取设施类型ID及接入方秘钥
			RpcResponse<Facilities> findById = facilitiesQueryService.findById(facilityId);
			if (!findById.isSuccess()) {
				logger.error("[alarmProduce()->invalid：设施详情查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施详情查询失败！");
			}
			Facilities facilities = findById.getSuccessValue();
			// 设施停用
			if (FacilitiesContants.DISABLE.equals(facilities.getManageState())) {
				logger.warn("[alarmProduce()->warn：设施已停用！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施已停用！");
			}
			String accessSecret = facilities.getAccessSecret();
			// 查询设备下的告警规则
			RpcResponse<List<AlarmRule>> queryAlarmRuleByDeviceId = alarmRuleQueryService
					.queryAlarmRuleByDeviceId(deviceId, accessSecret); 
			if (!queryAlarmRuleByDeviceId.isSuccess()) {
				logger.warn("[alarmProduce()->warn：根据设备ID查询告警规则失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("根据设备ID查询告警规则失败！");
			}
			List<AlarmRule> alarmRules = queryAlarmRuleByDeviceId.getSuccessValue();
			// 设备上未绑定自定义规则
			if (alarmRules == null || alarmRules.size() == 0) {
				alarmRules = findAlarmRule(deviceId, accessSecret);
			}
			if (alarmRules == null) {
				logger.error("alarmProduce()-->没有查询到此设备类型的告警规则!!!");
				return RpcResponseBuilder.buildErrorRpcResp("没有查询到此设备类型的告警规则!!!");
			}
			// 封装完整规则数据
			List<String> rules = Lists.newArrayList(); 
			// 封装上报数据和设备设施数据
			List<Map<String, Object>> datas = Lists.newArrayList(); 
			// 组装数据
			for (AlarmRule alarmRule : alarmRules) {
				dealData(deviceRepotedData, deviceId, reportTime, rules, datas, alarmRule);
			}
			logger.info("告警规则：" + rules);
			logger.info("上报数据：" + datas);
			logger.info(DateUtils.formatDate(new Date()) + "***alarmProduce获取设备上报数据，调用规则引擎，产生告警进入add方法:"
					+ System.currentTimeMillis());
			// 调用规则引擎方法，datas：部分数据作为中转传递到工单生成的方法
			alarmRuleInvokInterface.invokAlarm(datas, rules);
			//规则引擎方法没有catch异常,如果有异常信息会被本方法catch,所以不用判断
			return RpcResponseBuilder.buildSuccessRpcResp("告匹配警流程执行完毕", "告匹配警流程执行完毕");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	  * 
	  * @Description: 获取该类型对应的告警规则
	  * @param 
	  * @return
	  */
	
	private List<AlarmRule> findAlarmRule(String deviceId, String accessSecret) {
		List<AlarmRule> alarmRules;
		List<String> listDeviceId = new ArrayList<>();
		listDeviceId.add(deviceId);
		RpcResponse<List<Map<String, Object>>> deviceDetail = deviceQueryService
				.queryBatchDeviceInfoForDeviceIds(listDeviceId);
		List<Map<String, Object>> successValue = deviceDetail.getSuccessValue();
		if (successValue == null || !deviceDetail.isSuccess()) {
			logger.error("[alarmProduce()->invalid：设备类型获取失败]");
			return null;
		}
		Map<String, Object> map = null;
		if (successValue.size() > 0) {
			map = successValue.get(0);
		}
		String deviceTypeId = "";
		if (null != map) {
			deviceTypeId = map.get("deviceTypeId").toString();
		}
		// 获取该类型对应的告警规则
		RpcResponse<List<AlarmRule>> response = alarmRuleQueryService.getByDeviceTypeId(deviceTypeId,
				accessSecret);
		if (!response.isSuccess()) {
			logger.error("[alarmProduce()->invalid：获取设备类型id:" + deviceTypeId + "对应的告警规则失败！]");
			return null;
		}
		alarmRules = response.getSuccessValue();
		return alarmRules;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private void dealData(JSONObject deviceRepotedData, String deviceId, String reportTime, List<String> rules,
			List<Map<String, Object>> datas, AlarmRule alarmRule) {
		Map<String, Object> deviceRealState = Maps.newHashMap();
		/** 设备上报数据 */
		if (deviceRepotedData != null) { 
			// 解析上报数据
			Set<String> keySet = deviceRepotedData.keySet();
			for (String propName : keySet) {
				deviceRealState.put(propName, deviceRepotedData.get(propName));
			}
		}
		// 封装完整规则数据
		rules.add(alarmRule.getRule()); 
		/** 告警规则相关数据 */
		deviceRealState.put(AlarmInfoConstants.REPORTTIME, reportTime);
		deviceRealState.put("_rule", alarmRule);
		deviceRealState.put(AlarmInfoConstants.DEVICEID, deviceId);
		deviceRealState.put("_engine_check", new Boolean(false));
		datas.add(deviceRealState);
	}

}
