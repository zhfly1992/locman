/*
 * File name: BalanceSwitchPowersRestQueryService.java
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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.BalanceSwitchPowers;
import com.run.locman.api.entity.FacilitiesType;
import com.run.locman.api.query.service.BalanceSwitchPowersQueryService;
import com.run.locman.api.query.service.FacilitiesTypeQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.usc.base.query.AccSourceBaseQueryService;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月7日
 */
@Service
public class BalanceSwitchPowersRestQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private BalanceSwitchPowersQueryService	balanceSwitchPowersQueryService;
	@Autowired
	private UserRoleBaseQueryService		userRoleBaseQueryService;
	@Autowired
	private AccSourceBaseQueryService		accSourceBaseQueryService;
	@Autowired
	private FacilitiesTypeQueryService		facilitiesTypeQueryService;



	public Result<PageInfo<BalanceSwitchPowers>> getBalanceSwitchPowersList(String balanceSwitchPowers) {
		try {
			logger.info(String.format("[getBalanceSwitchPowersList()->request params:%s]", balanceSwitchPowers));
			if (null == balanceSwitchPowers || ParamChecker.isNotMatchJson(balanceSwitchPowers)) {
				logger.error("[balanceSwitchPowersSave():valid--fail:参数为空或不是json格式!]");
				return ResultBuilder.failResult("参数为空或不是json格式!");
			}

			JSONObject parseObject = JSONObject.parseObject(balanceSwitchPowers);
			RpcResponse<PageInfo<BalanceSwitchPowers>> list = balanceSwitchPowersQueryService
					.getBalanceSwitchPowersList(parseObject);

			if (!list.isSuccess()) {
				logger.error(String.format("[getBalanceSwitchPowersList()->fail:%s]", list.getMessage()));
				return ResultBuilder.failResult(list.getMessage());
			}
			PageInfo<BalanceSwitchPowers> info = list.getSuccessValue();
			List<BalanceSwitchPowers> balanceSwitchPowersList = info.getList();
			// 循环封装名称信息
			for (BalanceSwitchPowers balanceSwitch : balanceSwitchPowersList) {
				String facilityTypeId = balanceSwitch.getFacilityTypeId();
				String organizationId = balanceSwitch.getOrganizationId();
				String postId = balanceSwitch.getPostId();

				// -----------封装设施类型名称
				RpcResponse<FacilitiesType> queryFacilitiesByIdResult = facilitiesTypeQueryService
						.queryFacilitiesById(facilityTypeId);
				if (!queryFacilitiesByIdResult.isSuccess()) {
					logger.error("[balanceSwitchPowersSave()--fail:" + queryFacilitiesByIdResult.getMessage() + "]");
					return ResultBuilder.failResult("设施类型信息查询失败");
				}
				// 设施类型被删除
				if (queryFacilitiesByIdResult.getSuccessValue() == null) { 
					balanceSwitch.setFacilityTypeName("");
				} else {
					String facilityTypeAlias = queryFacilitiesByIdResult.getSuccessValue().getFacilityTypeAlias();
					if (facilityTypeAlias != null && !"".equals(facilityTypeAlias)) {
						balanceSwitch.setFacilityTypeName(facilityTypeAlias);
					} else {
						balanceSwitch.setFacilityTypeName(queryFacilitiesByIdResult.getSuccessValue()
								.getFacilitiesTypeBase().getFacilityTypeName());
					}
				}
				// --------------封装组织名称
				@SuppressWarnings("rawtypes")
				Map sourceMessById = accSourceBaseQueryService.getSourceMessById(organizationId);
				if (sourceMessById != null) {
					balanceSwitch.setOrganizationName(sourceMessById.get(CommonConstants.SOURCE_NAME).toString());
				} else {
					balanceSwitch.setOrganizationName("");
				}

				// ---------------封装岗位名称
				if (postId != null && !postId.equals("")) {
					RpcResponse<Map<String, Object>> roleMessById = userRoleBaseQueryService.getRoleMessById(postId);
					if (roleMessById.isSuccess()) {
						// 岗位被删除
						if (roleMessById.getSuccessValue() == null) { 
							balanceSwitch.setPostName("");
						} else {
							balanceSwitch.setPostName(
									roleMessById.getSuccessValue().get(CommonConstants.ROLE_NAME).toString());
						}
					} else {
						logger.error("[getBalanceSwitchPowersList()->fail:岗位信息查询失败]");
						return ResultBuilder.failResult("岗位信息查询失败");
					}
				}
			}

			info.setList(balanceSwitchPowersList);
			logger.info("[getBalanceSwitchPowersList()->success]");
			return ResultBuilder.successResult(info, list.getMessage());
		} catch (Exception e) {
			logger.error("balanceSwitchPowersSave()-->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<BalanceSwitchPowers> getBalanceSwitchPowersById(String id) {
		try {
			logger.info(String.format("[getBalanceSwitchPowersById()->request params--id:%s]", id));
			RpcResponse<BalanceSwitchPowers> result = balanceSwitchPowersQueryService.getBalanceSwitchPowersById(id);
			if (!result.isSuccess()) {
				logger.error(result.getMessage());
				return ResultBuilder.failResult(result.getMessage());
			}

			// 组装组织名称
			BalanceSwitchPowers distributionPowers = result.getSuccessValue();
			@SuppressWarnings("rawtypes")
			Map sourceMessById = accSourceBaseQueryService.getSourceMessById(distributionPowers.getOrganizationId());
			if (sourceMessById != null) {
				distributionPowers.setOrganizationName(sourceMessById.get(CommonConstants.SOURCE_NAME).toString());
			}
			logger.info(result.getMessage());
			return ResultBuilder.successResult(distributionPowers, result.getMessage());
		} catch (Exception e) {
			logger.error("getBalanceSwitchPowersById()-->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	// 测试代码
	/*
	 * public Result<Boolean> get(String balanceSwitchPowers) {
	 * RpcResponse<Boolean> checkBalanceSwitchPowers =
	 * balanceSwitchPowersQueryService
	 * .checkBalanceSwitchPowers(JSONObject.parseObject(balanceSwitchPowers));
	 * return
	 * ResultBuilder.successResult(checkBalanceSwitchPowers.getSuccessValue(),
	 * checkBalanceSwitchPowers.getMessage()); }
	 */

}
