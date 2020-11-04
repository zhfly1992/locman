/*
 * File name: DeviceJobQueryServiceImpl.java
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

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.DeviceJobQueryRepository;
import com.run.locman.api.query.service.DeviceJobQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年7月3日
 */

public class DeviceJobQueryServiceImpl implements DeviceJobQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceJobQueryRepository	deviceJobQueryRepository;



	/**
	 * @see com.run.locman.api.query.service.DeviceJobQueryService#getJobIdsByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getJobIdsByDeviceId(String deviceId) {
		logger.info(String.format("[getJobIdsByDeviceId()方法执行开始...,参数：【%s】]", deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[getJobIdsByDeviceId()->invalid：设备deviceId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备deviceId不能为空！");
			}
			List<Map<String, Object>> jobIdsByDeviceId = deviceJobQueryRepository.getJobIdsByDeviceId(deviceId);
			if (null == jobIdsByDeviceId) {
				logger.error("[getJobIdsByDeviceId()->fail：查询定时器id集合失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询定时器id集合失败！");
			}
			logger.info("[getJobIdsByDeviceId()->success：查询定时器id集合成功！]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询定时器id集合成功！", jobIdsByDeviceId);
		} catch (Exception e) {
			logger.error("getJobIdsByDeviceId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}

}
