/*
 * File name: BalanceSwitchPowersQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月7日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.BalanceSwitchPowers;
import com.run.locman.api.query.repository.BalanceSwitchPowersQueryRepository;
import com.run.locman.api.query.service.BalanceSwitchPowersQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DistributionPowersConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月7日
 */

public class BalanceSwitchPowersQueryServiceImpl implements BalanceSwitchPowersQueryService {

	private Logger								logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private BalanceSwitchPowersQueryRepository	balanceSwitchPowersQueryRepository;

	@Value("${api.host}")
	private String								ip;



	/*
	 * @Autowired public HttpServletRequest request;
	 */

	/**
	 * @see com.run.locman.api.query.service.BalanceSwitchPowersQueryService#getBalanceSwitchPowersList(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<PageInfo<BalanceSwitchPowers>> getBalanceSwitchPowersList(JSONObject findParam) {
		logger.info(String.format("[getBalanceSwitchPowersList()方法执行开始...,参数：【%s】]", findParam));
		try {
			if (null == findParam || findParam.isEmpty()) {
				logger.error("[getBalanceSwitchPowersList():valid--fail:查询平衡开关权限配置，参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询平衡开关权限配置，参数不能为空!");
			}
			if (StringUtils.isBlank(findParam.getString(PublicConstants.ACCESSSECRET))) {
				logger.error("[getBalanceSwitchPowersList():valid--fail:查询平衡开关权限配置，接入方秘钥accessSecret不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询平衡开关权限配置，接入方秘钥accessSecret不能为空!");
			}
			if (!StringUtils.isNumeric(findParam.getString(PublicConstants.PUBLIC_PAGE_NUM))) {
				logger.error("[getBalanceSwitchPowersList():valid--fail:参数pageNum错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数pageNum错误!");
			}
			if (!StringUtils.isNumeric(findParam.getString(PublicConstants.PUBLIC_PAGE_SIZE))) {
				logger.error("[getBalanceSwitchPowersList():valid--fail:参数pageSize错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数pageSize错误!");
			}

			int pageNum = findParam.getInteger(PublicConstants.PUBLIC_PAGE_NUM);
			int pageSize = findParam.getInteger(PublicConstants.PUBLIC_PAGE_SIZE);
			PageHelper.startPage(pageNum, pageSize);
			// 分页查询告警开关权限配置
			List<BalanceSwitchPowers> balanceList = balanceSwitchPowersQueryRepository
					.getBalanceSwitchPowersList(findParam);
			PageInfo<BalanceSwitchPowers> pageInfo = new PageInfo<>(balanceList);

			if (null == balanceList) {
				logger.error("[getBalanceSwitchPowersList()--fail:查询失败,数据库异常!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败,数据库异常!");
			}
			logger.info("[getBalanceSwitchPowersList()--success:查询平衡开关权限配置成功!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询平衡开关权限配置成功!", pageInfo);
		} catch (Exception e) {
			logger.error("getBalanceSwitchPowersList()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.BalanceSwitchPowersQueryService#getBalanceSwitchPowersById(java.lang.String)
	 */
	@Override
	public RpcResponse<BalanceSwitchPowers> getBalanceSwitchPowersById(String id) {
		logger.info(String.format("[getBalanceSwitchPowersById()方法执行开始...,参数：【%s】]", id));
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[getBalanceSwitchPowersList():valid--fail:查询平衡开关权限配置，参数id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询平衡开关权限配置，参数id不能为空!");
			}
			BalanceSwitchPowers balanceSwitchPowers = balanceSwitchPowersQueryRepository.getBalanceSwitchPowersById(id);
			if (null == balanceSwitchPowers) {
				logger.error("[getBalanceSwitchPowersList()--fail:查询失败,数据库异常!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败,数据库异常!");
			}
			logger.info("[getBalanceSwitchPowersList()--success:查询平衡开关权限配置成功!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询平衡开关权限配置成功!", balanceSwitchPowers);
		} catch (Exception e) {
			logger.error("getBalanceSwitchPowersList()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.BalanceSwitchPowersQueryService#checkByFacilityTypeId(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkByFacilityTypeId(String facilityTypeId) {
		logger.info(String.format("[checkByFacilityTypeId()方法执行开始...,参数：【%s】]", facilityTypeId));
		try {
			if (StringUtils.isBlank(facilityTypeId)) {
				logger.error("[getBalanceSwitchPowersList():valid--fail:校验平衡开关权限配置，参数facilityTypeId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("校验平衡开关权限配置，参数facilityTypeId不能为空!");
			}
			List<BalanceSwitchPowers> resut = balanceSwitchPowersQueryRepository.checkByFacilityTypeId(facilityTypeId);
			if (null != resut && resut.size() > 0) {
				logger.info("[getBalanceSwitchPowersList()--success:查询平衡开关权限配置成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询平衡开关权限配置成功!", Boolean.TRUE);
			} else if (null != resut && resut.size() == 0) {
				logger.info("[getBalanceSwitchPowersList()--success:查询平衡开关权限配置成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询平衡开关权限配置成功!", Boolean.FALSE);
			}
			logger.error("[getBalanceSwitchPowersList()--success:检验平衡开关权限配置失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("检验平衡开关权限配置失败!");
		} catch (Exception e) {
			logger.error("getBalanceSwitchPowersList()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.BalanceSwitchPowersQueryService#checkBalanceSwitchPowers(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Boolean> checkBalanceSwitchPowers(JSONObject checkParam) {
		logger.info(String.format("[checkBalanceSwitchPowers()方法执行开始...,参数：【%s】]", checkParam));
		try {
			if (checkParam == null) {
				logger.error("[getPowersByParam()->failed: 参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
			}
			if (StringUtils.isBlank(checkParam.getString(FacilitiesContants.FACILITY_TYPE_ID))) {
				logger.error("[getPowersByParam()->failed: 设施类型参数facilityTypeId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设施类型参数facilityTypeId不能为空!");
			}
			if (StringUtils.isBlank(checkParam.getString(FacilitiesContants.ORGANIZATION_ID))) {
				logger.error("[getPowersByParam()->failed: 组织参数organizationId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("组织参数organizationId不能为空!");
			}
			if (StringUtils.isBlank(checkParam.getString(DistributionPowersConstants.POSTID))) {
				logger.error("[getPowersByParam()->failed: 岗位参数postId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("岗位参数postId不能为空!");
			}
			if (StringUtils.isBlank(checkParam.getString(PublicConstants.ACCESSSECRET))) {
				logger.error("[getPowersByParam()->failed: 接入方参数accessSecret不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方参数accessSecret不能为空!");
			}

			// 先获取该组织及其下所有组织id
			String organizationId = checkParam.getString("organizationId");
			String token = checkParam.getString(InterGatewayConstants.TOKEN);
			List<String> organizationIdList = Lists.newArrayList();
			String httpValueByGet = InterGatewayUtil
					.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip, token);
			if (null == httpValueByGet) {
				logger.error("[deviceCheck()->invalid：查询该组织的子组织失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询该组织的子组织失败!");
			} else {
				JSONArray jsonArray = JSON.parseArray(httpValueByGet);
				List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
				for (Map map : organizationInfoList) {
					organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
				}
			}

			// 参数组装组织id集合
			checkParam.put("organizationId", organizationIdList);

			List<BalanceSwitchPowers> resut = null;
			if (organizationIdList.size() > 0) {
				resut = balanceSwitchPowersQueryRepository.checkBalanceSwitchPowers(checkParam);
			}

			if (null != resut && resut.size() > 0) {
				logger.info("[getBalanceSwitchPowersList()--success:检验平衡开关权限配置成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("检验平衡开关权限配置成功!", Boolean.TRUE);
			} else if (null != resut && resut.size() == 0) {
				logger.info("[getBalanceSwitchPowersList()--success:检验平衡开关权限配置成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("检验平衡开关权限配置成功!", Boolean.FALSE);
			}
			logger.error("[getBalanceSwitchPowersList()--success:检验平衡开关权限配置失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("检验平衡开关权限配置失败!");
		} catch (Exception e) {
			logger.error("getBalanceSwitchPowersList()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}

}
