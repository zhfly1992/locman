/*
 * File name: DeviceJobCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年7月3日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DeviceJobCrudRepository;
import com.run.locman.api.crud.service.DeviceJobCrudService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年7月3日
 */

public class DeviceJobCrudServiceImpl implements DeviceJobCrudService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceJobCrudRepository	deviceJobCrudRepository;

	private static final String	JOB_ID = "jobId";


	/**
	 * @see com.run.locman.api.crud.service.DeviceJobCrudService#saveDeviceRsJob(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<Integer> saveDeviceRsJob(JSONObject paramJson) {
		try {
			if (paramJson == null) {
				logger.error("[saveDeviceRsJob: invalid：参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
			}
			if (StringUtils.isBlank(paramJson.getString(DeviceContants.DEVICEID))) {
				logger.error("[saveDeviceRsJob: invalid：设备deviceId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备deviceId不能为空！");
			}
			if (StringUtils.isBlank(paramJson.getString(JOB_ID))) {
				logger.error("[saveDeviceRsJob: invalid：定时器jobId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("定时器jobId不能为空！");
			}
			paramJson.put("id", UtilTool.getUuId());
			logger.info(String.format("[saveDeviceRsJob()->进入方法,参数%s]", paramJson));
			int saveDeviceRsJob = deviceJobCrudRepository.saveDeviceRsJob(paramJson);
			if (saveDeviceRsJob > 0) {
				logger.info("[saveDeviceRsJob(): invalid：保存设备和定时器关系成功！]");
				return RpcResponseBuilder.buildSuccessRpcResp("保存设备和定时器关系成功！", saveDeviceRsJob);
			}
			logger.error("[saveDeviceRsJob(): invalid：保存设备和定时器关系失败！]");
			return RpcResponseBuilder.buildErrorRpcResp("保存设备和定时器关系失败！");

		} catch (Exception e) {
			logger.error("saveDeviceRsJob()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceJobCrudService#deleteByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<Integer> deleteByDeviceId(Map<String, Object> map) {
		try {
			if (null == map || map.isEmpty()) {
				logger.error("[deleteByDeviceId: invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			logger.info(String.format("[deleteByDeviceId()->进入方法,参数%s]", map));
			int del = deviceJobCrudRepository.deleteByDeviceId(map);
			if (del > 0) {
				logger.info("[deleteByDeviceId(): invalid：删除设备和定时器关系成功！]");
				return RpcResponseBuilder.buildSuccessRpcResp("删除设备和定时器关系成功！", del);
			}
			logger.error("[deleteByDeviceId(): invalid：删除设备和定时器关系失败！]");
			return RpcResponseBuilder.buildErrorRpcResp("删除设备和定时器关系失败！");

		} catch (Exception e) {
			logger.error("deleteByDeviceId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceJobCrudService#deleteByJobId(java.lang.String)
	 */
	@Override
	public RpcResponse<Integer> deleteByJobId(String jobId) {
		try {
			if (StringUtils.isBlank(jobId)) {
				logger.error("[deleteByJobId: invalid：定时器jobId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("定时器jobId不能为空！");
			}
			logger.info(String.format("[deleteByJobId()->进入方法,参数%s]", jobId));
			int del = deviceJobCrudRepository.deleteByJobId(jobId);
			if (del > 0) {
				logger.info("[deleteByJobId(): invalid：删除设备和定时器关系成功！]");
				return RpcResponseBuilder.buildSuccessRpcResp("删除设备和定时器关系成功！", del);
			}
			logger.error("[deleteByJobId(): invalid：删除设备和定时器关系失败！]");
			return RpcResponseBuilder.buildErrorRpcResp("删除设备和定时器关系失败！");

		} catch (Exception e) {
			logger.error("deleteByJobId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
