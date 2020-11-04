/*
 * File name: DeviceReportedDataCrudRestService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2020年4月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.DeviceInfoCudService;
import com.run.locman.api.crud.service.DeviceReportedCrudService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.wingiot.service.WingsIotDataTransform;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 用于处理从典型平台收到的数据
 * @author: Administrator
 * @version: 1.0, 2020年4月7日
 */
@Service
public class ReportedDataCrudRestService {
	private Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	DeviceReportedCrudService	deviceReportedCrudService;

	@Autowired
	private DeviceInfoCudService		deviceInfoCudService;

	private static ExecutorService		executorService	= Executors.newFixedThreadPool(3);

	/**
	 * 
	 * @Description:用于将设备的上报数据转换为老iot的格式
	 * @param reportedData,该参数是电信平台发来，固定为json格式
	 * @return
	 */
	public Result<String> receiveReportedData(JSONObject reportedData) {
		if (null == reportedData || reportedData.isEmpty()) {
			logger.error("receiveReportedData()->上报数据为空");
			return ResultBuilder.failResult("上报数据为空");
		}
		
		logger.info(String.format("[receiveReportedData()->接收到的数据:%s]", reportedData.toJSONString()));

		try {
			Map<String, String> transTable = deviceReportedCrudService.getTransTable();
			JSONObject transfromedData = WingsIotDataTransform.transfrom(reportedData,transTable);
			logger.info(String.format("[receiveReportedData()->转换后的数据:%s]", transfromedData.toJSONString()));
			RpcResponse<String> messageReceiveThreadPool = deviceReportedCrudService.messageReceiveThreadPool(transfromedData.toJSONString());
			if (messageReceiveThreadPool.isSuccess()) {
				logger.info("receiveReportedData()->success,数据已接收成功");
				return ResultBuilder.successResult(transfromedData.toJSONString(), "success");
			}
			else {
				logger.error("receiveReportedData()->fail,数据转换成功，接收失败" + messageReceiveThreadPool.getMessage());
				return ResultBuilder.failResult("数据转换成功，接收失败:" + messageReceiveThreadPool.getMessage());
			}
		} catch (Exception e) {
			logger.error("receiveReportedData()->转换数据错误", e);
			return ResultBuilder.failResult("转换数据错误");
		}

	}
	
	/**
	 * 
	* @Description:重保项目用于接收指令响应，当resultcode为SUCCESSFUL时表示指令生效，更改相应表字段，否则视为指令未生效
	* @return
	 */
	public Result<String> receiveCommandResponse(JSONObject commandResponse){
		if (null == commandResponse || commandResponse.isEmpty()) {
			logger.error("receiveCommandResponse()->指令响应数据为空");
			return ResultBuilder.failResult("指令响应数据为空");
		}
		logger.info(String.format("[receiveCommandResponse()->接收到的数据:%s]", commandResponse.toJSONString()));
		try {
			//resultcode为SUCCESSFUL时表示指令生效
			if (commandResponse.getJSONObject("result").getString("resultCode").equals("SUCCESSFUL")) {
				String deviceId = commandResponse.getString("deviceId");
				RpcResponse<String> commandResponseForWingsIot = deviceReportedCrudService.commandResponseForWingsIot(deviceId);
				if (commandResponseForWingsIot.isSuccess()) {
					logger.info("receiveCommandResponse()->success,是否收到指令字段更改成功");
				}
				else{
					logger.error("receiveCommandResponse()->fail,是否收到指令字段更改失败");
				}
				//对于指令响应推送来说，应用平台只要收到就视为推送成功
				return ResultBuilder.successResult("", "success");
			}
			else{
				logger.info(String.format("[receiveCommandResponse()->指令尚未执行:%s,deviceId:%s]", commandResponse.getString("deviceId")));
				//对于指令响应推送来说，应用平台只要收到就视为推送成功
				return ResultBuilder.successResult("", "success");
			}
		} catch (Exception e) {
			logger.error("receiveCommandResponse()->exception", e);
			return ResultBuilder.failResult(e.toString());
		}
	}
	
	public Result<String> synchroDevicesFromDataConversion(JSONObject paramJson) {
		try {
			logger.info("[synchroDevicesFromDataConversion()->进入方法");
			if (null == paramJson || paramJson.isEmpty()) {
				logger.error("[synchroDevicesFromDataConversion()->传入参数为空");
				return ResultBuilder.failResult("传入参数为空");
			}
			RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"synchroDevicesFromDataConversion", paramJson, "accessSecret", "locmanDeviceTypeId",
					"applicationId", "deviceList","dcDeviceTypeId");
			if (containsParamKey != null) {
				logger.error("[synchroDevicesFromDataConversion()->传入参数错误:" + containsParamKey.getMessage());
				return ResultBuilder.failResult(containsParamKey.getMessage());
			}
			RpcResponse<Object> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"synchroDevicesFromDataConversion", paramJson, "accessSecret", "applicationId");
			if (checkBusinessKey != null) {
				logger.error("[synchroDevicesFromDataConversion()->传入参数错误:" + checkBusinessKey.getMessage());
				return ResultBuilder.failResult(checkBusinessKey.getMessage());
			}
			String accessSecret = paramJson.getString("accessSecret");
			String locmanDeviceTypeId = paramJson.getString("locmanDeviceTypeId");
			String applicationId = paramJson.getString("applicationId");
			String dcDeviceTypeId = paramJson.getString("dcDeviceTypeId");
			JSONArray jsonArray = paramJson.getJSONArray("deviceList");
			logger.info(String.format(
					"[synchroDevicesFromDataConversion()->receivedata,accessSecret:%s,locmanDeviceTypeId:%s,applicationId:%s,dataSize:%s",
					accessSecret, locmanDeviceTypeId, applicationId, jsonArray.size()));

			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					deviceInfoCudService.synchroDevicesForDataConversion(jsonArray, accessSecret, locmanDeviceTypeId,dcDeviceTypeId,applicationId);
				}
			});

			return ResultBuilder.successResult("接受数据成功", "success");
		} catch (Exception e) {
			logger.error("[synchroDevicesFromDataConversion()->]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
