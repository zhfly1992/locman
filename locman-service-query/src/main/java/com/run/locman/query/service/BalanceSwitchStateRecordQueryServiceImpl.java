/*
 * File name: BalanceSwitchStateRecordQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月15日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.BalanceSwitchStateRecord;
import com.run.locman.api.query.repository.BalanceSwitchStateRecordQueryRepository;
import com.run.locman.api.query.service.BalanceSwitchStateRecordQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月15日
 */

public class BalanceSwitchStateRecordQueryServiceImpl implements BalanceSwitchStateRecordQueryService {

	private Logger									logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private BalanceSwitchStateRecordQueryRepository	balanceSwitchStateRecordQueryRepository;



	/**
	 * @see com.run.locman.api.query.service.BalanceSwitchStateRecordQueryService#getState(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<BalanceSwitchStateRecord> getState(JSONObject paramJson) {
		logger.info(String.format("[getState()方法执行开始...,参数：【%s】]", paramJson));
		try {
			if (null == paramJson) {
				logger.error("[openOrClose():valid--fail:获取平衡开关状态，参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("获取平衡开关状态，参数不能为空!");
			}
			if (StringUtils.isBlank(paramJson.getString(DeviceContants.DEVICEID))) {
				logger.error("[openOrClose():valid--fail:获取平衡开关状态，设备deviceId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("获取平衡开关状态，设备deviceId不能为空!");
			}
			if (StringUtils.isBlank(paramJson.getString(FacilitiesContants.FACILITY_ID))) {
				logger.error("[openOrClose():valid--fail:获取平衡开关状态，设施facilityId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("获取平衡开关状态，设施facilityId不能为空!");
			}
			if (StringUtils.isBlank(paramJson.getString(PublicConstants.ACCESSSECRET))) {
				logger.error("[openOrClose():valid--fail:获取平衡开关状态，接入方秘钥accessSecret不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("获取平衡开关状态，接入方秘钥accessSecret不能为空!");
			}

			BalanceSwitchStateRecord state = balanceSwitchStateRecordQueryRepository.getState(paramJson);

			logger.info("[openOrClose()--success:查询成功!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", state);

		} catch (Exception e) {
			logger.error("openOrClose()-->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> getLatestCloseTime(String deviceId, String accessSecret) {
		logger.info(String.format("[getLatestCloseTime()->deviceId:%s,accessSecret:%s]", deviceId, accessSecret));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[getLatestCloseTime()->valid--fail:获取平衡开关最近关闭时间，设备deviceId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("获取平衡开关最近关闭时间失败，设备deviceId不能为空!");
			}
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[getLatestCloseTime()->valid--fail:获取平衡开关最近关闭时间，accessSecret不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("获取平衡开关最近关闭时间失败，accessSecret不能为空!");
			}
			Map<String, String> queryMap = new HashMap<>(16);
			queryMap.put("deviceId", deviceId);
			queryMap.put("accessSecret", accessSecret);
			String closeTime = balanceSwitchStateRecordQueryRepository.getLatestCloseOperationTime(queryMap);
			if (StringUtils.isBlank(closeTime)) {
				logger.info("[getLatestCloseTime()->success:该设备无平衡开关关闭命令]");
				return RpcResponseBuilder.buildErrorRpcResp("该设备无平衡开关无关闭命令");
			}
			logger.info("getLatestCloseTime()->seccess:查询平衡开关最近关闭时间成功,时间为：" + closeTime);
			return RpcResponseBuilder.buildSuccessRpcResp("查询平衡开关最近关闭时间成功", closeTime);
		} catch (Exception e) {
			logger.error("getLatestCloseTime()-->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
