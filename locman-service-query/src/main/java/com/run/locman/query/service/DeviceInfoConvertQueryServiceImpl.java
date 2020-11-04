/*
 * File name: DeviceInfoConvertQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年3月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.DeviceInfoConvert;
import com.run.locman.api.model.DeviceInfoConvertModel;
import com.run.locman.api.query.repository.DeviceInfoConvertQueryRepository;
import com.run.locman.api.query.service.DeviceInfoConvertQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.PageUtils;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceInfoConvertConstans;

/**
 * @Description:设备信息翻译实体类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月7日
 */

public class DeviceInfoConvertQueryServiceImpl implements DeviceInfoConvertQueryService {

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceInfoConvertQueryRepository	deviceInfoConvertQueryRepository;



	/**
	 * @see com.run.locman.api.query.service.DeviceInfoConvertQueryService#dataConvert(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Map> dataConvert(String deviceKey, String accessSecret) {
		logger.info(String.format("[dataConvert()方法执行开始...,参数：【%s】【%s】]", deviceKey, accessSecret));

		try {
			Map<String, Object> newHashMap = Maps.newHashMap();
			newHashMap.put(DeviceInfoConvertConstans.CONVERT_DIC_KEY, deviceKey);
			newHashMap.put(DeviceInfoConvertConstans.CONVERT_ACCESSSECRET, accessSecret);
			// 翻译数据如果为null，返回原本的数据.
			String dataConvert = deviceInfoConvertQueryRepository.dataConvert(newHashMap);

			Map<String, Object> infoMap = Maps.newHashMap();
			if (dataConvert == null) {
				infoMap.put(DeviceInfoConvertConstans.CHINA_KEY, deviceKey);
			} else {
				infoMap.put(DeviceInfoConvertConstans.CHINA_KEY, dataConvert);
			}
			infoMap.put(DeviceInfoConvertConstans.ENGLISH_KEY, deviceKey);

			if (StringUtils.isBlank(dataConvert)) {
				logger.info(String.format("[dataConvert()方法执行结束!返回信息：【%s】]", infoMap));
				return RpcResponseBuilder.buildSuccessRpcResp("翻译数据不存在，返回原有数据！", infoMap);
			}
			logger.info(String.format("[dataConvert()方法执行结束!返回信息：【%s】]", infoMap));
			return RpcResponseBuilder.buildSuccessRpcResp("翻译成功，返回翻译数据！", infoMap);
		} catch (Exception e) {
			logger.error("dataConvert()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}



	/**
	 * @see com.run.locman.api.query.service.DeviceInfoConvertQueryService#findConvertAll(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<PageInfo<DeviceInfoConvert>> findConvertAll(DeviceInfoConvertModel convertParam) {
		logger.info(String.format("[findConvertAll()方法执行开始...,参数：【%s】]", convertParam));

		try {

			// 校验参数
			RpcResponse<PageInfo<DeviceInfoConvert>> checkObjectBusinessKey = CheckParameterUtil.checkObjectBusinessKey(
					logger, "findConvertAll", convertParam, true, DeviceInfoConvertConstans.CONVERT_ACCESSSECRET);

			if (checkObjectBusinessKey != null) {
				return checkObjectBusinessKey;
			}

			PageInfo<DeviceInfoConvert> pageStart = PageUtils.pageStart(deviceInfoConvertQueryRepository, convertParam);
			logger.info(String.format("[getAllDrollsByPage()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(DeviceInfoConvertConstans.OPERATION_SUCCESS, pageStart);

		} catch (Exception e) {
			logger.error("findConvertAll()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.DeviceInfoConvertQueryService#findConvertById(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<DeviceInfoConvert> findConvertById(DeviceInfoConvertModel convertParam) {
		logger.info(String.format("[findConvertById()方法执行开始...,参数：【%s】]", convertParam));

		try {

			// 参数校验
			RpcResponse<DeviceInfoConvert> checkObjectBusinessKey = CheckParameterUtil.checkObjectBusinessKey(logger,
					"findConvertById", convertParam, DeviceInfoConvertConstans.CONVERT_DIC_ID);

			if (checkObjectBusinessKey != null) {
				return checkObjectBusinessKey;
			}

			DeviceInfoConvert findConvertById = deviceInfoConvertQueryRepository.findConvertById(convertParam);

			logger.info(String.format("[findConvertById()->success:%s]", DeviceInfoConvertConstans.OPERATION_SUCCESS));
			return RpcResponseBuilder.buildSuccessRpcResp(DeviceInfoConvertConstans.OPERATION_SUCCESS, findConvertById);

		} catch (Exception e) {
			logger.error("findConvertById()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.DeviceInfoConvertQueryService#existConvertInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> existConvertInfo(DeviceInfoConvertModel convertParam) {
		logger.info(String.format("[existConvertInfo()方法执行开始...,参数：【%s】]", convertParam));

		try {

			// 参数校验
			CheckParameterUtil.checkObjectBusinessKey(logger, "existConvertInfo", convertParam, false,
					DeviceInfoConvertConstans.CONVERT_DIC_KEY, DeviceInfoConvertConstans.CONVERT_ACCESSSECRET);

			// 判断是否存在
			String existConvertInfo = deviceInfoConvertQueryRepository.existConvertInfo(convertParam);
			if (StringUtils.isBlank(existConvertInfo)) {
				logger.info(String.format("[existConvertInfo()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(DeviceInfoConvertConstans.OPERATION_SUCCESS,
						existConvertInfo);
			} else {
				logger.info(String.format("[existConvertInfo()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(DeviceInfoConvertConstans.OPERATION_ERROR,
						existConvertInfo);
			}

		} catch (Exception e) {
			logger.error("existConvertInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@Override
	public RpcResponse<List<DeviceInfoConvert>> getDeviceInfoConvert(String accessSecret) {
		logger.info(String.format("[getDeviceInfoConvert()方法执行开始...,参数：【%s】]", accessSecret));
		List<DeviceInfoConvert> getDeviceInfoConvert = deviceInfoConvertQueryRepository
				.getDevieInfoConvert(accessSecret);
		try {
			if (null == getDeviceInfoConvert) {
				logger.warn("[getDeviceInfoConvert()->fail：查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败！");
			} else {
				logger.info("[getDeviceInfoConvert()->success：查询成功！]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", getDeviceInfoConvert);
			}

		} catch (Exception e) {
			logger.error("getAllAlarmRule()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp("查询失败，异常");
		}

	}

}
