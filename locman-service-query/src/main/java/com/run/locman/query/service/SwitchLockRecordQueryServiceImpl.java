/*
 * File name: SwitchLockRecordQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.container.page.PageHandler;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.SwitchLockRecord;
import com.run.locman.api.query.repository.SwitchLockRecordQueryRepository;
import com.run.locman.api.query.service.SwitchLockRecordQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.SwitchLockRecordConstants;

/**
 * @Description: 开关锁记录查询实现类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月7日
 */

public class SwitchLockRecordQueryServiceImpl implements SwitchLockRecordQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private SwitchLockRecordQueryRepository	switchLockRecordQueryRepository;



	/**
	 * 一体化智能井盖锁IDlist
	 */

	@Override
	public RpcResponse<PageInfo<Map<String, String>>> listSwitchLockPage(SwitchLockRecord switchLockRecord) {
		logger.info(String.format("[listSwitchLockPage()方法执行开始...,参数：【%s】]", switchLockRecord));

		try {

			RpcResponse<PageInfo<Map<String, String>>> res = CheckParameterUtil.checkObjectBusinessKey(logger,
					"listSwitchLockPage", switchLockRecord, true, SwitchLockRecordConstants.COMMAND_ACCESSSECRET);

			if (res != null) {
				return res;
			}

			PageHelper.startPage(Integer.valueOf(switchLockRecord.getPageNum()),
					Integer.valueOf(switchLockRecord.getPageSize()));

			List<Map<String, String>> listSwitchLockPage = switchLockRecordQueryRepository
					.listSwitchLockPage(switchLockRecord);

			PageInfo<Map<String, String>> info = new PageInfo<>(listSwitchLockPage);

			logger.info(String.format("[listSwitchLockPage()->suc:%s]", PublicConstants.PARAM_SUCCESS));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, info);

		} catch (Exception e) {
			logger.error("listSwitchLockPage()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.SwitchLockRecordQueryService#checkLock(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<String> checkLock(String deviceId, String accessSecret) {
		logger.info(String.format("[checkLock()方法执行开始...,参数：【%s】【%s】]", deviceId, accessSecret));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[checkLock()->invalid：设备deviceId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备deviceId不能为空！");
			}
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[checkLock()->invalid：接入方秘钥accessSecret不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥accessSecret不能为空！");
			}
//			String[] ids = deviceTypeIdList.split(",");
//			List<String> idList = Arrays.asList(ids);
			String checkLock = switchLockRecordQueryRepository.checkLock(deviceId, accessSecret);
			logger.info(String.format("[checkLock()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", checkLock);
		} catch (Exception e) {
			logger.error("checkLock()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SwitchLockRecordQueryService#listManholeCoverSwitch(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> listManholeCoverSwitch(JSONObject pageObj) {
		logger.info(String.format("[listManholeCoverSwitch()方法执行开始...,参数：【%s】]", pageObj));
		try {
			if (pageObj == null) {
				logger.error(String.format("[listManholeCoverSwitch()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey("accessSecret")) {
				logger.error(String.format("[listManholeCoverSwitch()->error：%s%s", "accessSecret",
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp("accessSecret" + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey("pageSize")) {
				logger.error(String.format("[listManholeCoverSwitch()->error：%s%s", "pageSize",
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp("pageSize" + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey("pageNo")) {
				logger.error(String.format("[listManholeCoverSwitch()->error：%s%s", "pageNo",
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp("pageNo" + MessageConstant.PARAM_IS_NULL);
			}
			if (!StringUtils.isNumeric(pageObj.getString("pageSize"))
					|| !StringUtils.isNumeric(pageObj.getString("pageNo"))) {
				logger.error(String.format("[listManholeCoverSwitch()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_NOT_IS_NUM);
			}

			String accessSecret = pageObj.getString(FacilitiesContants.USC_ACCESS_SECRET);
			String keyWord=pageObj.getString("keyWord");
			int pageNo = pageObj.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = pageObj.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accessSecret", accessSecret);
			params.put("keyWord", keyWord);
			List<Map<String, Object>> page = switchLockRecordQueryRepository.listManholeCoverSwitch(params);

			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			
			logger.info(String.format("[listManholeCoverSwitch()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("listManholeCoverSwitch()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SwitchLockRecordQueryService#getManholeCoverSwitchInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getManholeCoverSwitchInfo(JSONObject pageObj) {
		logger.info(String.format("[getManholeCoverSwitchInfo()方法执行开始...,参数：【%s】]", pageObj));
		try {
			if (pageObj == null) {
				logger.error(String.format("[getManholeCoverSwitchInfo()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey("accessSecret")) {
				logger.error(String.format("[getManholeCoverSwitchInfo()->error：%s%s", "accessSecret",
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp("accessSecret" + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey("deviceId")) {
				logger.error(String.format("[getManholeCoverSwitchInfo()->error：%s%s", "deviceId",
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp("deviceId" + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey("pageSize")) {
				logger.error(String.format("[getManholeCoverSwitchInfo()->error：%s%s", "pageSize",
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp("pageSize" + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey("pageNo")) {
				logger.error(String.format("[getManholeCoverSwitchInfo()->error：%s%s", "pageNo",
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp("pageNo" + MessageConstant.PARAM_IS_NULL);
			}
			if (!StringUtils.isNumeric(pageObj.getString("pageSize"))
					|| !StringUtils.isNumeric(pageObj.getString("pageNo"))) {
				logger.error(String.format("[getManholeCoverSwitchInfo()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_NOT_IS_NUM);
			}

			String accessSecret = pageObj.getString(FacilitiesContants.USC_ACCESS_SECRET);
			String deviceId=pageObj.getString("deviceId");
			int pageNo = pageObj.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = pageObj.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accessSecret", accessSecret);
			params.put("deviceId", deviceId);
			List<Map<String, Object>> page = switchLockRecordQueryRepository.getManholeCoverSwitchInfo(params);

			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			
			logger.info(String.format("[getManholeCoverSwitchInfo()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getManholeCoverSwitchInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
