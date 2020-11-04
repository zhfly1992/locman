/*
 * File name: TimeoutReportConfigQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年7月4日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.timer.query;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.DeviceAndTimeDto;
import com.run.locman.api.timer.query.repository.TimeoutReportConfigQueryRepository;
import com.run.locman.api.timer.query.service.TimeoutReportConfigQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年7月4日
 */

public class TimeoutReportConfigQueryServiceImpl implements TimeoutReportConfigQueryService {
	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private TimeoutReportConfigQueryRepository	timeoutReportConfigQueryRepository;



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */
	@Override
	public RpcResponse<List<DeviceAndTimeDto>> getDeviceAndTime() {
		try {
			List<DeviceAndTimeDto> deviceAndTime = timeoutReportConfigQueryRepository.getDeviceAndTime();
			if (deviceAndTime == null || deviceAndTime.isEmpty()) {
				logger.info("getDeviceAndTime()-->没有查询到需要检测的设备");
				return RpcResponseBuilder.buildErrorRpcResp("没有查询到需要检测的设备");
			} else {
				logger.info("getDeviceAndTime()-->查询成功");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", deviceAndTime);
			}
		} catch (Exception e) {
			logger.error("getDeviceAndTime()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
