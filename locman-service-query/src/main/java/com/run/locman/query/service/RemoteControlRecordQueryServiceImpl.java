/*
 * File name: RemoteControlRecordQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月7日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.run.common.util.StringUtil;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.RemoteControlRecord;
import com.run.locman.api.query.repository.RemoteControlRecordQueryRepository;
import com.run.locman.api.query.service.RemoteControlRecordQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 下发可控制query实现类
 * @author: qulong
 * @version: 1.0, 2017年12月7日
 */

public class RemoteControlRecordQueryServiceImpl implements RemoteControlRecordQueryService {

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private RemoteControlRecordQueryRepository	remoteControlRecordQueryRepository;



	/**
	 * @see com.run.locman.api.query.service.RemoteControlRecordQueryService#getControlList(java.util.Map)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getControlList(Map<String, Object> param, int pageNo, int pageSize) {
		logger.info(String.format("[getControlList()方法执行开始...,参数：【%s】【%s】【%s】]", param, pageNo, pageSize));
		if (param == null) {
			return RpcResponseBuilder.buildErrorRpcResp("查询参数不能为空");
		}
		try {
			int pageNumMySql = 0;
			if (pageNo > 0) {
				pageNumMySql = (pageNo - 1) * pageSize;
			}
			// PageHelper.startPage(pageNo, pageSize);
			param.put("numberPage", pageNumMySql);
			param.put("sizePage", pageSize);
			List<Map<String, Object>> controlList = remoteControlRecordQueryRepository.getControlList(param);
			logger.info(String.format("[getControlList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", controlList);
		} catch (Exception e) {
			logger.error("getControlList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.RemoteControlRecordQueryService#getByDeviceIdAndControlType(java.lang.String,
	 *      java.lang.Integer)
	 */
	@Override
	public RpcResponse<List<RemoteControlRecord>> getByDeviceIdAndControlType(String deviceId) {
		logger.info(String.format("[getByDeviceIdAndControlType()方法执行开始...,参数：【%s】]", deviceId));
		if (StringUtil.isEmpty(deviceId)) {
			logger.error("[RemoteControlRecordQueryServiceImpl -> getByDeviceId -> invalid: 设备id不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空");
		}
		try {
			Map<String, Object> paramMap = new HashMap<>(16);
			paramMap.put("deviceId", deviceId);
			List<RemoteControlRecord> remoteControlRecords = remoteControlRecordQueryRepository.findByParams(paramMap);
			logger.info(String.format("[getByDeviceIdAndControlType()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", remoteControlRecords);
		} catch (Exception e) {
			logger.error("getByDeviceIdAndControlType()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.RemoteControlRecordQueryService#getControlByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<List<RemoteControlRecord>> getControlByDeviceId(String deviceId) {
		logger.info(String.format("[getControlByDeviceId()方法执行开始...,参数：【%s】]", deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[getControlByDeviceId -> invalid: 设备id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空!");
			}

			List<RemoteControlRecord> control = remoteControlRecordQueryRepository.getControlByDeviceId(deviceId);
			if (null == control) {
				logger.error("[getControlByDeviceId -> fail: 查询命令记录失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询命令记录失败!");
			}
			logger.info("[getControlByDeviceId -> success:查询命令记录成功!]");
			logger.info(String.format("[getControlByDeviceId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询命令记录成功!", control);

		} catch (Exception e) {
			logger.error("getControlByDeviceId()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.RemoteControlRecordQueryService#getRemoteControlByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<RemoteControlRecord> getRemoteControlByDeviceId(String deviceId) {
		logger.info(String.format("[getRemoteControlByDeviceId()方法执行开始...,参数：【%s】]", deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[getRemoteControlByDeviceId -> invalid: 设备id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空!");
			}

			RemoteControlRecord control = remoteControlRecordQueryRepository.getRemoteControlByDeviceId(deviceId);
			if (null == control) {
				logger.error("[getRemoteControlByDeviceId -> fail: 查询命令记录失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询命令记录失败!");
			}
			logger.info("[getRemoteControlByDeviceId -> success:查询命令记录成功!]");
			logger.info(String.format("[getRemoteControlByDeviceId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询命令记录成功!", control);

		} catch (Exception e) {
			logger.error("getRemoteControlByDeviceId()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
