/*
 * File name: DistributionPowersQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月14日 ... ...
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

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.DistributionPowers;
import com.run.locman.api.query.repository.DistributionPowersQueryRepository;
import com.run.locman.api.query.service.DistributionPowersQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.DistributionPowersConstants;
import com.run.locman.constants.FacilitiesContants;

/**
 * @Description: 分权分域query实现类
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */

public class DistributionPowersQueryServiceImpl implements DistributionPowersQueryService {

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DistributionPowersQueryRepository	distributionPowersQueryRepository;



	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getDistributionPowersListPage(String accessSecret, int pageNum,
			int pageSize, Map<String, String> searchParam) {
		logger.info(String.format("[getDistributionPowersListPage()方法执行开始...,参数：【%s】【%s】【%s】【%s】]", accessSecret,
				pageNum, pageSize, searchParam));
		if (accessSecret == null) {
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空");
		}
		if (searchParam != null) {
			searchParam.put("accessSecret", accessSecret);
		} else {
			searchParam = new HashMap<>(16);
			searchParam.put("accessSecret", accessSecret);
		}
		try {
			PageHelper.startPage(pageNum, pageSize);
			List<Map<String, Object>> distributionPowersListPage = distributionPowersQueryRepository
					.queryDistributionPowersListPage(searchParam);
			if (distributionPowersListPage == null) {
				logger.error("[getDistributionPowersListPage()->failed: 查询失败，数据库异常]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败，数据库异常");
			}
			PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(distributionPowersListPage);
			if (CollectionUtils.isEmpty(distributionPowersListPage)) {
				logger.info(String.format("[getDistributionPowersListPage()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("暂无数据", pageInfo);
			}
			logger.info(String.format("[getDistributionPowersListPage()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", pageInfo);
		} catch (Exception e) {
			logger.error("getDistributionPowersListPage()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<DistributionPowers> getDistributionPowersById(String id) {
		logger.info(String.format("[getDistributionPowersById()方法执行开始...,参数：【%s】]", id));
		if (id == null) {
			return RpcResponseBuilder.buildErrorRpcResp("主键ID不能为空");
		}
		try {
			DistributionPowers distributionPowers = distributionPowersQueryRepository.findById(id);
			if (distributionPowers == null) {
				logger.error("[getDistributionPowersById()->failed: 查询失败，数据库异常]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败，数据库异常");
			}
			logger.info(String.format("[getDistributionPowersById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", distributionPowers);
		} catch (Exception e) {
			logger.error("getDistributionPowersById()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DistributionPowersQueryService#getDistributionPowersByFacilityTypeId(java.lang.String)
	 */
	@Override
	public RpcResponse<DistributionPowers> getDistributionPowersByFacilityTypeId(String facilityTypeId) {
		logger.info(String.format("[getDistributionPowersByFacilityTypeId()方法执行开始...,参数：【%s】]", facilityTypeId));
		if (facilityTypeId == null) {
			logger.info(String.format("[getDistributionPowersByFacilityTypeId()：设施类型ID不能为空]"));
			return RpcResponseBuilder.buildErrorRpcResp("设施类型ID不能为空");
		}
		try {
			DistributionPowers distributionPowers = distributionPowersQueryRepository
					.getDistributionPowersByFacilityTypeId(facilityTypeId);
			logger.info(String.format("[getDistributionPowersByFacilityTypeId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", distributionPowers);
		} catch (Exception e) {
			logger.error("getDistributionPowersByFacilityTypeId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DistributionPowersQueryService#getDistributionPowersByFacilityTypeId(java.lang.String)
	 */
	@Override
	public RpcResponse<DistributionPowers> getByFacilityTypeId(String facilityTypeId) {
		logger.info(String.format("[getByFacilityTypeId()方法执行开始...,参数：【%s】]", facilityTypeId));
		if (facilityTypeId == null) {
			return RpcResponseBuilder.buildErrorRpcResp("设施类型ID不能为空");
		}
		try {
			DistributionPowers distributionPowers = distributionPowersQueryRepository
					.getByFacilityTypeId(facilityTypeId);
			logger.info(String.format("[getByFacilityTypeId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", distributionPowers);
		} catch (Exception e) {
			logger.error("getByFacilityTypeId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DistributionPowersQueryService#getPowersByParam(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<Boolean> getPowersByParam(JSONObject param) {
		logger.info(String.format("[getPowersByParam()方法执行开始...,参数：【%s】]", param));
		if (param == null) {
			logger.error("[getPowersByParam()->failed: 参数不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
		}
		if (StringUtils.isBlank(param.getString(DistributionPowersConstants.ORGANIZATIONID))) {
			logger.error("[getPowersByParam()->failed: 人员组织及下级组织集合参数organizationId不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("人员组织及下级组织集合参数organizationId不能为空!");
		}
		if (StringUtils.isBlank(param.getString(DistributionPowersConstants.ORG_ID_FOR_USER))) {
			logger.error("[getPowersByParam()->failed: 人员组织参数orgIdForUser不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("人员组织参数orgIdForUser不能为空!");
		}
		if (StringUtils.isBlank(param.getString(DistributionPowersConstants. USER_ID))) {
			logger.error("[getPowersByParam()->failed: 人员userId不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("人员userId不能为空!");
		}
		if (StringUtils.isBlank(param.getString(FacilitiesContants.FACILITY_ID))) {
			logger.error("[getPowersByParam()->failed: 设施facilityId不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("设施facilityId不能为空!");
		}
		if (StringUtils.isBlank(param.getString(DistributionPowersConstants.ACCESSSECRET))) {
			logger.error("[getPowersByParam()->failed: 接入方参数accessSecret不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方参数accessSecret不能为空!");
		}
		try {
			List<Map<String, Object>> powersByParam = distributionPowersQueryRepository.getPowersByParam(param);
			if (powersByParam != null && powersByParam.size() > 0) {
				logger.info("[getPowersByParam()->success：查询成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("校验成功,该设施类型该组织人员在当前时间存在分权分域!", Boolean.TRUE);
			} else if (powersByParam != null && powersByParam.size() == 0) {
				logger.info("[getPowersByParam()->success：查询成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("校验成功,该设施类型该组织人员在当前时间不存在分权分域!", Boolean.FALSE);
			}
			logger.error("[getPowersByParam()->fial：查询失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("查询分权分域失败!");
		} catch (Exception e) {
			logger.error("getPowersByParam()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.DistributionPowersQueryService#getPowersTimeByDeviceId(java.lang.String)
	 */
	/**
	 * @see com.run.locman.api.query.service.DistributionPowersQueryService#getPowersTimeByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> getPowersTime(JSONObject jsonParam) {
		logger.info(String.format("[getPowersTimeByDeviceId()方法执行开始...,参数：【%s】]", jsonParam));
		if (StringUtils.isBlank(jsonParam.getString(DeviceContants.DEVICEID))) {
			logger.error("deviceSchedulerStart()--fail:设备deviceId不能为空!");
			return RpcResponseBuilder.buildErrorRpcResp("设备deviceId不能为空!");
		}
		if (StringUtils.isBlank(jsonParam.getString(DistributionPowersConstants. USER_ID))) {
			logger.error("deviceSchedulerStart()--fail:下发命令的userId不能空!");
			return RpcResponseBuilder.buildErrorRpcResp("下发命令的userId不能空!");
		}
		if (StringUtils.isBlank(jsonParam.getString(DistributionPowersConstants.ACCESSSECRET))) {
			logger.error("deviceSchedulerStart()--fail:下发命令的accessSecret不能空!");
			return RpcResponseBuilder.buildErrorRpcResp("下发命令的accessSecret不能空!");
		}
		try {
			Map<String, Object> powersTimeByDeviceId = distributionPowersQueryRepository.getPowersTime(jsonParam);
			logger.info("[getPowersTimeByDeviceId()->success：查询成功!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", powersTimeByDeviceId);
		} catch (Exception e) {
			logger.error("getPowersTimeByDeviceId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}

}
