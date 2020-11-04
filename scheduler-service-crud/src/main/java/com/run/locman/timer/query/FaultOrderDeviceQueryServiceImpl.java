/*
 * File name: FaultOrderDeviceQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年7月10日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.timer.query;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.FaultOrderDetetionDto;
import com.run.locman.api.timer.crud.repository.FaultOrderDeviceCudRepository;
import com.run.locman.api.timer.query.service.FaultOrderDeviceQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年7月10日
 */

public class FaultOrderDeviceQueryServiceImpl implements FaultOrderDeviceQueryService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FaultOrderDeviceCudRepository	faultOrderDeviceCudRepository;



	/**
	 * @see com.run.locman.api.timer.query.service.FaultOrderDeviceQueryService#getOrderInfo(java.util.List)
	 */
	@Override
	public RpcResponse<List<FaultOrderDetetionDto>> getOrderInfo(List<String> deviceIds) {
		if (null == deviceIds || deviceIds.isEmpty()) {
			logger.error("getOrderInfo()-->设备id集合不能为空");
			return RpcResponseBuilder.buildErrorRpcResp("设备id集合不能为空");
		}
		logger.info(String.format("[getOrderInfo()->request params--deviceIds:%s]", deviceIds));
		try {
			List<FaultOrderDetetionDto> orderInfo = faultOrderDeviceCudRepository.getOrderInfo(deviceIds);
			if (null == orderInfo) {
				logger.error("getOrderInfo()-->查询超时未上报故障工单信息失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询超时未上报故障工单信息失败");
			} else {
				logger.info("getOrderInfo()-->查询超时未上报故障工单信息成功");
				return RpcResponseBuilder.buildSuccessRpcResp("查询超时未上报故障工单信息成功", orderInfo);
			}

		} catch (Exception e) {
			logger.error("getOrderInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
