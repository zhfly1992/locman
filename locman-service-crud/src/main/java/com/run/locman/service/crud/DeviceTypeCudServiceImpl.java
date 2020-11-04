/*
 * File name: DeviceTypeCudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年10月23日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DeviceTypeCudRepository;
import com.run.locman.api.crud.service.DeviceTypeCudService;
import com.run.locman.api.entity.DeviceType;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年10月23日
 */
@Transactional(rollbackFor = Exception.class)
public class DeviceTypeCudServiceImpl implements DeviceTypeCudService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceTypeCudRepository	deviceTypeCudRepository;



	/**
	 * @see com.run.locman.api.crud.service.DeviceTypeCudService#addDeviceType(java.util.List)
	 */
	@Override
	public RpcResponse<Integer> addDeviceType(List<DeviceType> deviceTypeList) {
		try {
			if (null == deviceTypeList || deviceTypeList.isEmpty()) {
				return RpcResponseBuilder.buildErrorRpcResp("设备类型集合不能为空");
			}
			logger.info(String.format("[addDeviceType()->参数%s]", deviceTypeList));

			Integer addDeviceTypeNum = deviceTypeCudRepository.addDeviceType(deviceTypeList);

			if (addDeviceTypeNum == deviceTypeList.size()) {
				logger.info("[addDeviceType --> success: 保存成功(" + addDeviceTypeNum + ") ]");
				return RpcResponseBuilder.buildSuccessRpcResp("保存成功", addDeviceTypeNum);
			} else {
				logger.error("[addDeviceType --> error: 保存失败");
				return RpcResponseBuilder.buildErrorRpcResp("保存失败");
			}

		} catch (Exception e) {
			logger.error("addDeviceType()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceTypeCudService#deleteDeviceTypeById(java.util.List)
	 */
	@Override
	public RpcResponse<Integer> deleteDeviceTypeById(List<DeviceType> deviceTypeList) {
		try {
			if (null == deviceTypeList || deviceTypeList.isEmpty()) {
				return RpcResponseBuilder.buildErrorRpcResp("设备类型集合不能为空");
			}
			logger.info(String.format("[deleteDeviceTypeById()->参数%s]", deviceTypeList));

			Integer deleteDeviceTypeNum = deviceTypeCudRepository.deleteDeviceTypeById(deviceTypeList);

			if (deleteDeviceTypeNum >= 0) {
				logger.info("[deleteDeviceTypeById --> success: 删除成功(" + deleteDeviceTypeNum + ") ]");
				return RpcResponseBuilder.buildSuccessRpcResp("删除成功", deleteDeviceTypeNum);
			} else {
				logger.error("[deleteDeviceTypeById --> error: 删除失败");
				return RpcResponseBuilder.buildErrorRpcResp("删除失败");
			}

		} catch (Exception e) {
			logger.error("deleteDeviceTypeById()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}


	@Override
	public RpcResponse<String> editDeviceType(JSONObject jsonObject) {
		logger.info(String.format("[editDeviceType()-->参数:%s]", jsonObject));
		try {
			if (null == jsonObject || jsonObject.isEmpty()) {
				logger.error(String.format("[editDeviceType()-->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
				return RpcResponseBuilder.buildErrorRpcResp("传入参数为空");
			}
			DeviceType deviceType = jsonObject.toJavaObject(DeviceType.class);
			
			//设置更新时间
			deviceType.setUpdateTime(DateUtils.formatDate(new Date()));
			
			//如果TypeSign为"",则置为null
			if (deviceType.getTypeSign().isEmpty()) {
				deviceType.setTypeSign(null);
			}

			Integer res = deviceTypeCudRepository.editDeviceType(deviceType);
			if (res > 0) {
				logger.info("[editDeviceType-->success]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, res + "");
			}
			logger.error("[editDeviceType-->fail]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);

		} catch (Exception e) {
			logger.error("editDeviceType()->exceptio]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
