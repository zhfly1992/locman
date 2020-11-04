/*
 * File name: FacilityDeviceRestCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guolei 2017年9月14日 ... ...
 * ...
 *
 ***************************************************/
package com.run.locman.crud.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.base.query.FacilitesService;
import com.run.locman.api.crud.service.FacilityDeviceCudService;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.util.InfoPushUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.FacilityDeviceContants;
import com.run.locman.constants.InterGatewayConstants;

/**
 * 
 * @Description: 设施与设备关系rest服务
 * @author: guolei
 * @version: 1.0, 2017年9月14日
 */
@Service
public class FacilityDeviceRestCudService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilityDeviceCudService	facilityDeviceCudService;

	@Autowired
	private FacilitesService			facilitesService;
	
	@Autowired
	private FacilitiesQueryService		facilitiesQueryService;

	@Autowired
	private HttpServletRequest			request;
	
	@Value("${api.host}")
	private String					ip;


	/**
	 * 
	 * @Description: 绑定设施与设备
	 * @param param
	 *            {"facilityId":"", "devicesId":""}
	 * @return
	 */
	public Result<Map<String, Object>> bindFacilityWithDevices(String param) {
		logger.info(String.format("[bindFacilityWithDevices()->request params:%s]", param));
		try {
			if (ParamChecker.isBlank(param)) {
				return ResultBuilder.emptyResult();
			}
			if (ParamChecker.isNotMatchJson(param)) {
				return ResultBuilder.invalidResult();
			}
			JSONObject paramsJson = JSONObject.parseObject(param);
			if (StringUtils.isBlank(paramsJson.getString(FacilityDeviceContants.FACILITY_ID))) {
				return ResultBuilder.noBusinessResult();
			}
			String facilityId = paramsJson.getString(FacilityDeviceContants.FACILITY_ID);
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			// 检查可用性
			RpcResponse<Map<String, Object>> facInfo = facilitesService.getFacilitesInfoByFacId(facilityId, token);
			if (!facInfo.isSuccess()) {
				logger.error(
						String.format("[bindFacilityWithDevices()->fail--设施id：%s,验证设施失败或该设施不存在,绑定失败]", facilityId));
				return ResultBuilder.failResult(String.format("设施id：%s,验证设施失败或该设施不存在,绑定失败", facilityId));
			}
			Map<String, Object> facilityInfo = facInfo.getSuccessValue();
			if (!FacilitiesContants.ENABLE.equals(facilityInfo.getOrDefault(CommonConstants.MANAGESTATE, ""))) {
				logger.error(String.format("[bindFacilityWithDevices()->fail--设施id：%s,该设施不是启用状态,绑定失败]", facilityId));
				return ResultBuilder.failResult(String.format("设施id：%s,该设施不是启用状态,绑定失败", facilityId));
			}
			// 设施和设备均可用,开始绑定
			RpcResponse<Map<String, Object>> bindResult = facilityDeviceCudService.bindFacilityWithDevices(paramsJson);
			if (!bindResult.isSuccess()) {
				logger.error("[bindFacilityWithDevices()->fail:绑定失败]");
				return ResultBuilder.failResult(bindResult.getMessage());
			} else {
				//注释推送
				//infoPush(facilityId, facilityInfo, "bindFacilityWithDevices");
				
				logger.info("[bindFacilityWithDevices()->success:绑定成功]");
				return ResultBuilder.successResult(bindResult.getSuccessValue(), bindResult.getMessage());
			}
			
		} catch (Exception e) {
			logger.error("bindFacilityWithDevices()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private void infoPush(String facilityId, Map<String, Object> facilityInfo, String methodName) {
		try {
			Object object = facilityInfo.get(FacilitiesContants.USC_ACCESS_SECRET);
			if (null != object && !StringUtils.isBlank(object + "")) {
				String accessSecret = object + "";
				String receiveUrl = InfoPushUtil.WhetherPush(accessSecret, ip);
				if (StringUtils.isNotBlank(receiveUrl)) {
					JSONObject pushInfo = getPushInfo(facilityInfo);
					RpcResponse<List<Map<String, Object>>> boundDeviceInfo = facilitiesQueryService
							.getBoundDeviceInfo(facilityId, accessSecret);
					if (!boundDeviceInfo.isSuccess()) {
						pushInfo.put("boundDevices", Lists.newArrayList());
					} else {
						List<Map<String, Object>> boundDevices = boundDeviceInfo.getSuccessValue();
						pushInfo.put("boundDevices", boundDevices);
					}
					List<JSONObject> infoList = Lists.newArrayList();
					infoList.add(pushInfo);
					String infoPush = InfoPushUtil.InfoPush(receiveUrl, infoList, InfoPushUtil.FACILITIES_UPDATE);
					logger.info(String.format("[%s()->设施设备绑定成功,推送结果:%s", methodName, infoPush));
				}
			} 
		} catch (Exception e) {
			logger.error(String.format("%s()->推送时Exception:", methodName) + e);
		}
	}
	

	
	
	private JSONObject getPushInfo(Map<String, Object> facilityInfo) {
		JSONObject pushInfo = new JSONObject();
		pushInfo.put("id", facilityInfo.get("id"));
		pushInfo.put("facilityTypeAlias", facilityInfo.get("facilityTypeAlias"));
		pushInfo.put("facilitiesTypeId", facilityInfo.get("facilitiesTypeId"));
		pushInfo.put("facilitiesCode", facilityInfo.get("facilitiesCode"));
		pushInfo.put("longitude", facilityInfo.get("longitude"));
		pushInfo.put("latitude", facilityInfo.get("latitude"));
		pushInfo.put("address", facilityInfo.get("address"));
		pushInfo.put("manageState", facilityInfo.get("manageState"));
		pushInfo.put("creationTime", facilityInfo.get("creationTime"));
		pushInfo.put("editorTime", facilityInfo.get("editorTime"));
		pushInfo.put("completeAddress", facilityInfo.get("completeAddress"));
		pushInfo.put("extend", facilityInfo.get("extend"));
		pushInfo.put("showExtend", facilityInfo.get("showExtend"));
		pushInfo.put("areaId", facilityInfo.get("areaId"));
		return pushInfo;
	}


	/**
	 * 
	 * @Description: 解绑设施与设备
	 * @param param
	 *            {"facilityId":"", "devicesId":""}
	 * @return
	 */
	public Result<Map<String, Object>> unbindFacilityWithDevices(String param) {
		logger.info(String.format("[unbindFacilityWithDevices()->request params:%s]", param));
		try {
			if (ParamChecker.isBlank(param)) {
				return ResultBuilder.emptyResult();
			}
			if (ParamChecker.isNotMatchJson(param)) {
				return ResultBuilder.invalidResult();
			}
			JSONObject paramsJson = JSONObject.parseObject(param);
			if (!paramsJson.containsKey(FacilityDeviceContants.FACILITY_ID)
					|| !paramsJson.containsKey(FacilityDeviceContants.DEVICES_ID)) {
				return ResultBuilder.noBusinessResult();
			}
			String facilityId = paramsJson.getString(FacilityDeviceContants.FACILITY_ID);
			String deviceIds = paramsJson.getString(FacilityDeviceContants.DEVICES_ID);
			// 设施和设备均可用,开始解绑
			RpcResponse<Map<String, Object>> unbindResult = facilityDeviceCudService
					.unbindFacilityWithDevices(facilityId, deviceIds);
			if (!unbindResult.isSuccess()) {
				logger.error(String.format("[unbindFacilityWithDevices()->fail:%s]", unbindResult.getMessage()));
				return ResultBuilder.failResult(unbindResult.getMessage());
			} else {
				//注释推送
				/*String token = request.getHeader(InterGatewayConstants.TOKEN);
				// 检查可用性
				RpcResponse<Map<String, Object>> facInfo = facilitesService.getFacilitesInfoByFacId(facilityId, token);
				if (facInfo.isSuccess()) {
					Map<String, Object> facilityInfo = facInfo.getSuccessValue();
					infoPush(facilityId, facilityInfo, "unbindFacilityWithDevices");
				}*/
				logger.info(String.format("[unbindFacilityWithDevices()->success:%s]", unbindResult.getMessage()));
				return ResultBuilder.successResult(unbindResult.getSuccessValue(), unbindResult.getMessage());
			}
			
		} catch (Exception e) {
			logger.error("unbindFacilityWithDevices()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
