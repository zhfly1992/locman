/*
 * File name: TimeoutReportConfigCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年6月22日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.run.common.util.UUIDUtil;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FacilitiesTimeoutReportConfigCrudRepository;
import com.run.locman.api.crud.repository.TimeoutReportConfigCrudRepository;
import com.run.locman.api.crud.service.TimeoutReportConfigCrudService;
import com.run.locman.api.entity.FacilityTimeoutReportConfig;
import com.run.locman.api.entity.TimeoutReportConfig;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.TimeoutReportConfigConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年6月22日
 */
@SuppressWarnings("unchecked")
@Transactional(rollbackFor = Exception.class)
public class TimeoutReportConfigCrudServiceImpl implements TimeoutReportConfigCrudService {
	private static final Logger							logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private TimeoutReportConfigCrudRepository			timeoutReportConfigCrudRepository;

	@Autowired
	private FacilitiesTimeoutReportConfigCrudRepository	facilitiesTimeoutReportConfigCrudRepository;



	/**
	 * @see com.run.locman.api.crud.service.TimeoutReportConfigCrudService#addTimeoutReportConfig(com.alibaba.fastjson.JSONObject)
	 */

	@Override
	public RpcResponse<String> addTimeoutReportConfig(JSONObject configInfo) {
		// 参数校验
		RpcResponse<Object> checkResult = CheckParameterUtil.checkBusinessKey(logger, "addTimeoutReportConfig",
				configInfo, TimeoutReportConfigConstants.NAME, TimeoutReportConfigConstants.USER_ID,
				TimeoutReportConfigConstants.TIMEOUT_REPORT_TIME, TimeoutReportConfigConstants.ACCESS_SECRET,
				TimeoutReportConfigConstants.FACILITY_ID_ADD);
		if (null != checkResult) {
			logger.error(checkResult.getMessage());
			return RpcResponseBuilder.buildErrorRpcResp(checkResult.getMessage());
		}
		String hour = configInfo.getString(TimeoutReportConfigConstants.TIMEOUT_REPORT_TIME);
		if (!StringUtils.isNumeric(hour)) {
			logger.error("超时时间格式不正确");
			return RpcResponseBuilder.buildErrorRpcResp("超时时间格式不正确");
		}
		logger.info(String.format("[addTimeoutReportConfig()->进入方法,参数:%s]", configInfo));
		try {
			String id = UUIDUtil.getUUID();

			List<String> facilityIds = (List<String>) configInfo.get(TimeoutReportConfigConstants.FACILITY_ID_ADD);
			// 插入关系表
			for (String facilityId : facilityIds) {
				if (StringUtils.isBlank(facilityId)) {
					logger.error("设施id为空的数据");
					continue;
				}
				FacilityTimeoutReportConfig facilityTimeoutReportConfig = new FacilityTimeoutReportConfig(
						UUIDUtil.getUUID(), facilityId, id);
				facilitiesTimeoutReportConfigCrudRepository.addBindFacility(facilityTimeoutReportConfig);
			}

			String name = configInfo.getString(TimeoutReportConfigConstants.NAME);
			String createUserId = configInfo.getString(TimeoutReportConfigConstants.USER_ID);
			int timeoutReportTime = Integer.parseInt(hour);
			String accessSecret = configInfo.getString(TimeoutReportConfigConstants.ACCESS_SECRET);
			// 插入超时设置
			TimeoutReportConfig timeoutReportConfig = new TimeoutReportConfig();

			timeoutReportConfig.setId(id);
			timeoutReportConfig.setName(name);
			timeoutReportConfig.setCreateUserId(createUserId);
			timeoutReportConfig.setTimeoutReportTime(timeoutReportTime);
			timeoutReportConfig.setAccessSecret(accessSecret);
			timeoutReportConfig.setCreateTime(DateUtils.formatDate(new Date()));
			timeoutReportConfig.setManagerState("enable");
			timeoutReportConfig.setUpdateTime(DateUtils.formatDate(new Date()));
			timeoutReportConfig.setUpdateUserId(createUserId);
			int insertModel = timeoutReportConfigCrudRepository.insertModel(timeoutReportConfig);

			if (insertModel > 0) {
				logger.info("[addTimeoutReportConfig()->success: " + MessageConstant.ADD_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.ADD_SUCCESS, id);
			} else {
				logger.error("[addTimeoutReportConfig()->error: " + MessageConstant.ADD_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL);
			}
		} catch (Exception e) {
			logger.error("addTimeoutReportConfig()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.TimeoutReportConfigCrudService#updateTimeoutReportConfig(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> updateTimeoutReportConfig(JSONObject configInfo) {
		RpcResponse<Object> checkResult = CheckParameterUtil.checkBusinessKey(logger, "updateTimeoutReportConfig",
				configInfo, TimeoutReportConfigConstants.ID, TimeoutReportConfigConstants.NAME,
				TimeoutReportConfigConstants.USER_ID, TimeoutReportConfigConstants.TIMEOUT_REPORT_TIME,
				TimeoutReportConfigConstants.ACCESS_SECRET);
		if (null != checkResult) {
			logger.error(checkResult.getMessage());
			return RpcResponseBuilder.buildErrorRpcResp(checkResult.getMessage());
		}

		RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger, "updateTimeoutReportConfig",
				configInfo, TimeoutReportConfigConstants.FACILITY_ID_ADD, TimeoutReportConfigConstants.FACILITY_ID_DEL);

		if (containsParamKey != null) {
			logger.error(containsParamKey.getMessage());
			return RpcResponseBuilder.buildErrorRpcResp(containsParamKey.getMessage());
		}
		logger.info(String.format("[updateTimeoutReportConfig()->进入方法,参数:%s]", configInfo));
		try {
			String hour = configInfo.getString(TimeoutReportConfigConstants.TIMEOUT_REPORT_TIME);
			if (!StringUtils.isNumeric(hour)) {
				return RpcResponseBuilder.buildErrorRpcResp("超时时间格式不正确");
			}
			String id = configInfo.getString(TimeoutReportConfigConstants.ID);
			// 删除的设施id
			List<String> delFacilityIds = (List<String>) configInfo.get(TimeoutReportConfigConstants.FACILITY_ID_DEL);
			// 添加的设施id
			List<String> addFacilityIds = (List<String>) configInfo.get(TimeoutReportConfigConstants.FACILITY_ID_ADD);
			// 删除中间表关系
			if (null != delFacilityIds && !delFacilityIds.isEmpty()) {
				facilitiesTimeoutReportConfigCrudRepository.deleteByFacilityIds(delFacilityIds);
			}
			// 插入中间表关系
			if (null != addFacilityIds && !addFacilityIds.isEmpty()) {
				for (String facilityId : addFacilityIds) {
					if (StringUtils.isBlank(facilityId)) {
						logger.error("设施id为空的数据");
						continue;
					}
					FacilityTimeoutReportConfig facilityTimeoutReportConfig = new FacilityTimeoutReportConfig(
							UUIDUtil.getUUID(), facilityId, id);
					facilitiesTimeoutReportConfigCrudRepository.addBindFacility(facilityTimeoutReportConfig);
				}

			}

			String name = configInfo.getString(TimeoutReportConfigConstants.NAME);
			String updateUserId = configInfo.getString(TimeoutReportConfigConstants.USER_ID);
			int timeoutReportTime = Integer.parseInt(hour);
			String accessSecret = configInfo.getString(TimeoutReportConfigConstants.ACCESS_SECRET);

			// 更新超时设置
			TimeoutReportConfig timeoutReportConfig = new TimeoutReportConfig();
			timeoutReportConfig.setId(id);
			timeoutReportConfig.setName(name);
			timeoutReportConfig.setUpdateUserId(updateUserId);
			timeoutReportConfig.setTimeoutReportTime(timeoutReportTime);
			timeoutReportConfig.setAccessSecret(accessSecret);
			timeoutReportConfig.setUpdateTime(DateUtils.formatDate(new Date()));
			int updatePart = timeoutReportConfigCrudRepository.updatePart(timeoutReportConfig);

			if (updatePart > 0) {
				logger.info("[updateTimeoutReportConfig()->success: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, id);
			} else {
				logger.error("[updateTimeoutReportConfig()->error: " + MessageConstant.UPDATE_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("updateTimeoutReportConfig()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.TimeoutReportConfigCrudService#delTimeoutReportConfig(java.util.List)
	 */
	@Override
	public RpcResponse<String> delTimeoutReportConfig(List<String> congigIds) {
		if (congigIds == null || congigIds.isEmpty()) {
			logger.error("要删除的配置id不能为空");
			return RpcResponseBuilder.buildErrorRpcResp("配置Id不能为空");
		}
		logger.info(String.format("[delTimeoutReportConfig()->进入方法,参数:%s]", congigIds));
		try {
			int deleteByIds = timeoutReportConfigCrudRepository.deleteByIds(congigIds);
			if (deleteByIds >= 0) {
				int deleteRS = facilitiesTimeoutReportConfigCrudRepository.deleteByTimeoutConfigIds(congigIds);
				logger.info("delTimeoutReportConfig()-->删除该超时配置的设施数量:" + deleteRS);
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.DEL_SUCCESS, "");
			} else {
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.DEL_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("delTimeoutReportConfig()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
