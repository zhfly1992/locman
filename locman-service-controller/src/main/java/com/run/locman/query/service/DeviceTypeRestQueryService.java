/*
* File name: DeviceTypeRestQueryService.java								
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
* 1.0			guofeilong		2018年3月2日
* ...			...			...
*
***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.DeviceType;
import com.run.locman.api.query.service.DeviceTypeQueryService;
import com.run.locman.api.query.service.FacilitiesTypeQueryService;
import com.run.locman.constants.CommonConstants;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年3月2日
*/
@Service
public class DeviceTypeRestQueryService {
	
	@Autowired
	private DeviceTypeQueryService	deviceTypeQueryService;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CommonConstants.LOGKEY);
	public Result<List<DeviceType>> getDeviceTypeList(){
		
		return null;
		
	}
	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public Result<List<Map<String, String>>> findAllDeviceTypeAndNum(String accessSecret) {
		logger.info(String.format("[findAllDeviceTypeAndNum()-->参数:accessSecret:%s]", accessSecret));
		try {
			 RpcResponse<List<Map<String, String>>> result = deviceTypeQueryService.findAllDeviceTypeAndNum(accessSecret);
			if (result == null) {
				logger.error("[findAllDeviceTypeAndNum()->fail:查询接入方设备类型及数量失败,返回值为null]");
				return ResultBuilder.failResult("查询接入方设备类型及数量失败");
			} else if (!result.isSuccess() || result.getSuccessValue() == null) {
				logger.error(String.format("[findAllDeviceTypeAndNum()->fail:%s]", result.getMessage()));
				return ResultBuilder.failResult(result.getMessage());
			} else {
				logger.info(String.format("[findAllDeviceTypeAndNum()->success:%s]", result.getMessage()));
				return ResultBuilder.successResult(result.getSuccessValue(), "查询成功");
			}
		} catch (Exception e) {
			logger.error("findAllDeviceTypeAndNum()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
		
	}
}
