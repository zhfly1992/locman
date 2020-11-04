/*
 * File name: BalanceSwitchPowersCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月4日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.BalanceSwitchPowersCrudRepository;
import com.run.locman.api.crud.service.BalanceSwitchPowersCrudService;
import com.run.locman.api.entity.BalanceSwitchPowers;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月4日
 */
@Transactional(rollbackFor = Exception.class)
public class BalanceSwitchPowersCrudServiceImpl implements BalanceSwitchPowersCrudService {

	private Logger								logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private BalanceSwitchPowersCrudRepository	balanceSwitchPowersCrudRepository;



	/**
	 * @see com.run.locman.api.crud.service.BalanceSwitchPowersCrudService#balanceSwitchPowersSave(com.run.locman.api.entity.BalanceSwitchPowers)
	 */
	@Override
	public RpcResponse<String> balanceSwitchPowersSave(BalanceSwitchPowers balanceSwitchPowers) {

		try {
			if (null == balanceSwitchPowers) {
				logger.error("[balanceSwitchPowersSave():valid--fail:保存平衡开关权限配置，参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关权限配置，参数不能为空!");
			}
			if (StringUtils.isBlank(balanceSwitchPowers.getFacilityTypeId())) {
				logger.error("[balanceSwitchPowersSave():valid--fail:保存平衡开关权限配置，设施类型facilityTypeId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关权限配置，设施类型facilityTypeId不能为空!");
			}
			if (StringUtils.isBlank(balanceSwitchPowers.getOrganizationId())) {
				logger.error("[balanceSwitchPowersSave():valid--fail:保存平衡开关权限配置，组织organizationId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关权限配置，组织organizationId不能为空!");
			}
			if (StringUtils.isBlank(balanceSwitchPowers.getStartTime())) {
				logger.error("[balanceSwitchPowersSave():valid--fail:保存平衡开关权限配置，起始时间startTime不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关权限配置，起始时间startTime不能为空!");
			}
			if (StringUtils.isBlank(balanceSwitchPowers.getEndTime())) {
				logger.error("[balanceSwitchPowersSave():valid--fail:保存平衡开关权限配置，结束时间endTime不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关权限配置，结束时间endTime不能为空!");
			}
			if (StringUtils.isBlank(balanceSwitchPowers.getAccessSecret())) {
				logger.error("[balanceSwitchPowersSave():valid--fail:保存平衡开关权限配置，接入方秘钥accessSecret不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关权限配置，接入方秘钥accessSecret不能为空!");
			}
			if (StringUtils.isBlank(balanceSwitchPowers.getPostId())) {
				balanceSwitchPowers.setPostId(null);
			}

			balanceSwitchPowers.setId(UtilTool.getUuId());
			// 默认停用
			balanceSwitchPowers.setManageState("disabled");
			
			logger.info(String.format("[balanceSwitchPowersSave()->即将保存平衡开关权限配置,参数:%s]", balanceSwitchPowers));
			
			int balanceSwitchPowersSave = balanceSwitchPowersCrudRepository
					.balanceSwitchPowersSave(balanceSwitchPowers);
			if (balanceSwitchPowersSave > 0) {
				logger.info("[balanceSwitchPowersSave()--success:保存平衡开关权限配置成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("保存平衡开关权限配置成功!", "保存成功!");
			}

			logger.error("[balanceSwitchPowersSave()--fail:保存平衡开关权限配置失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关权限配置失败!");

		} catch (Exception e) {
			logger.error("balanceSwitchPowersSave()-->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.BalanceSwitchPowersCrudService#balanceSwitchPowersUptate(com.run.locman.api.entity.BalanceSwitchPowers)
	 */
	@Override
	public RpcResponse<String> balanceSwitchPowersUpdate(BalanceSwitchPowers balanceSwitchPowers) {
		try {
			if (null == balanceSwitchPowers) {
				logger.error("[balanceSwitchPowersUptate():valid--fail:修改平衡开关权限配置，参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("修改平衡开关权限配置，参数不能为空!");
			}
			if (StringUtils.isBlank(balanceSwitchPowers.getId())) {
				logger.error("[balanceSwitchPowersUptate():valid--fail:修改平衡开关权限配置，参数id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("修改平衡开关权限配置，参数id不能为空!");
			}
			logger.info(String.format("[balanceSwitchPowersUpdate()->info:即将更新平衡开关权限配置,参数%s]", balanceSwitchPowers));
			int update = balanceSwitchPowersCrudRepository.balanceSwitchPowersUpdate(balanceSwitchPowers);
			if (update > 0) {
				logger.info("[balanceSwitchPowersUptate()--success:修改平衡开关权限配置成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("修改平衡开关权限配置成功!", "修改成功!");
			}

			logger.error("[balanceSwitchPowersUptate()--fail:修改平衡开关权限配置失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("修改失败!");

		} catch (Exception e) {
			logger.error("balanceSwitchPowersUptate()-->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.BalanceSwitchPowersCrudService#balanceSwitchPowersDel(java.lang.String)
	 */
	@Override
	public RpcResponse<String> balanceSwitchPowersDel(String id) {
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[balanceSwitchPowersUptate():valid--fail:删除平衡开关权限配置，参数id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("删除平衡开关权限配置，参数id不能为空!");
			}
			logger.info(String.format("[balanceSwitchPowersDel()->info即将删除平衡开关权限配置:%s]", id));
			int del = balanceSwitchPowersCrudRepository.balanceSwitchPowersDel(id);
			if (del > 0) {
				logger.info("[balanceSwitchPowersUptate()--success:删除平衡开关权限配置成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("删除平衡开关权限配置成功!", "删除成功!");
			}

			logger.error("[balanceSwitchPowersUptate()--fail:删除平衡开关权限配置失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("删除失败!");

		} catch (Exception e) {
			logger.error("balanceSwitchPowersUptate()-->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
