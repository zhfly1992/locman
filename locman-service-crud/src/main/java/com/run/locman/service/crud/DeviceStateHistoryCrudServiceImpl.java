/*
* File name: DeviceStateHistoryCrudServiceImpl.java								
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
* 1.0			qulong		2018年1月15日
* ...			...			...
*
***************************************************/

package com.run.locman.service.crud;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DeviceStateHistoryCrudRepository;
import com.run.locman.api.crud.service.DeviceStateHistoryCrudService;
import com.run.locman.api.entity.DeviceStateHistory;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
* @Description:	
* @author: qulong
* @version: 1.0, 2018年1月15日
*/
@Transactional(rollbackFor = Exception.class)
public class DeviceStateHistoryCrudServiceImpl implements DeviceStateHistoryCrudService {

	private static final Logger logger = Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private DeviceStateHistoryCrudRepository deviceStateHistoryCrudRepository;
	
	@Override
	public RpcResponse<String> saveDeviceStateHistory(DeviceStateHistory deviceStateHistory) {
		if (deviceStateHistory == null) {
			logger.error("[saveDeviceStateHistory --> saveDeviceStateHistory --> error: 设备状态数据不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设备状态数据不能为空");
		}
		if (deviceStateHistory.getId() == null || "".equals(deviceStateHistory.getId())) {
			deviceStateHistory.setId(UtilTool.getUuId());
		}
		try {
			logger.info(String.format("[saveDeviceStateHistory()->进入方法,参数%s]", deviceStateHistory));
			int result = deviceStateHistoryCrudRepository.insertModel(deviceStateHistory);
			if (result > 0) {
				logger.info("[saveDeviceStateHistory --> saveDeviceStateHistory --> success: 设备状态数据保存成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("设备状态数据保存成功", deviceStateHistory.getId());
			}
			logger.error("[saveDeviceStateHistory --> saveDeviceStateHistory --> error: 设备状态数据保存失败]");
			return RpcResponseBuilder.buildErrorRpcResp("设备状态数据保存失败");
		} catch (Exception e) {
			logger.error("saveDeviceStateHistory()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
