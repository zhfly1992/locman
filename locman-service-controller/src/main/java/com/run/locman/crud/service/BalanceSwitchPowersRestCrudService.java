/*
 * File name: BalanceSwitchPowersRestCrudService.java
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

package com.run.locman.crud.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.BalanceSwitchPowersCrudService;
import com.run.locman.api.crud.service.BalanceSwitchStateRecordCurdService;
import com.run.locman.api.entity.BalanceSwitchPowers;
import com.run.locman.api.entity.BalanceSwitchStateRecord;
import com.run.locman.api.query.service.BalanceSwitchPowersQueryService;
import com.run.locman.api.query.service.BalanceSwitchStateRecordQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月4日
 */
@Service
public class BalanceSwitchPowersRestCrudService {

	private Logger									logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private BalanceSwitchPowersCrudService			balanceSwitchPowersCrudService;

	@Autowired
	private BalanceSwitchPowersQueryService			balanceSwitchPowersQueryService;

	@Autowired
	private BalanceSwitchStateRecordCurdService		balanceSwitchStateRecordCurdService;

	@Autowired
	private BalanceSwitchStateRecordQueryService	balanceSwitchStateRecordQueryService;



	public Result<String> balanceSwitchPowersSave(String balanceSwitchPowers) {
		try {
			logger.info(String.format("[balanceSwitchPowersSave()->request params:%s]", balanceSwitchPowers));
			if (null == balanceSwitchPowers || ParamChecker.isNotMatchJson(balanceSwitchPowers)) {
				logger.error("[balanceSwitchPowersSave():valid--fail:参数为空或不是json格式!]");
				return ResultBuilder.failResult("参数为空或不是json格式!");
			}
			BalanceSwitchPowers parseObject = JSONObject.parseObject(balanceSwitchPowers, BalanceSwitchPowers.class);
			RpcResponse<String> balanceSwitchSave = balanceSwitchPowersCrudService.balanceSwitchPowersSave(parseObject);
			if (null == balanceSwitchSave) {
				logger.error("保存平衡告警开关分权分域权限配置失败");
				return ResultBuilder.failResult("保存平衡告警开关分权分域权限配置失败");
			}
			if (null != balanceSwitchSave && balanceSwitchSave.isSuccess()) {
				logger.info("[balanceSwitchPowersSave()--success:" + balanceSwitchSave.getMessage() + "]");
				return ResultBuilder.successResult(balanceSwitchSave.getSuccessValue(), balanceSwitchSave.getMessage());
			}
			logger.error("[balanceSwitchPowersSave()--fail:" + balanceSwitchSave.getMessage() + "]");
			return ResultBuilder.failResult(balanceSwitchSave.getMessage());
		} catch (Exception e) {
			logger.error("balanceSwitchPowersSave()-->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> balanceSwitchPowersUpdate(String balanceSwitchPowers) {
		try {
			logger.info(String.format("[balanceSwitchPowersUpdate()->request params:%s]", balanceSwitchPowers));
			if (null == balanceSwitchPowers || ParamChecker.isNotMatchJson(balanceSwitchPowers)) {
				logger.error("[balanceSwitchPowersSave():valid--fail:参数为空或不是json格式!]");
				return ResultBuilder.failResult("参数为空或不是json格式!");
			}
			BalanceSwitchPowers parseObject = JSONObject.parseObject(balanceSwitchPowers, BalanceSwitchPowers.class);
			RpcResponse<String> update = balanceSwitchPowersCrudService.balanceSwitchPowersUpdate(parseObject);
			if (null == update) {
				logger.error("修改平衡告警开关分权分域权限配置失败");
				return ResultBuilder.failResult("修改平衡告警开关分权分域权限配置失败");
			}
			if (null != update && update.isSuccess()) {
				logger.info("[balanceSwitchPowersSave()--success:" + update.getMessage() + "]");
				return ResultBuilder.successResult(update.getSuccessValue(), update.getMessage());
			}
			logger.error("[balanceSwitchPowersSave()--fail:" + update.getMessage() + "]");
			return ResultBuilder.failResult(update.getMessage());
		} catch (Exception e) {
			logger.error("balanceSwitchPowersSave()-->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> balanceSwitchPowersDel(String id) {
		try {
			logger.info(String.format("[balanceSwitchPowersDel()->request params:%s]", id));
			if (StringUtils.isBlank(id)) {
				logger.error("[balanceSwitchPowersSave():valid--fail:参数id为空！]");
				return ResultBuilder.failResult("参数id为空！");
			}
			RpcResponse<String> del = balanceSwitchPowersCrudService.balanceSwitchPowersDel(id);
			if (null == del) {
				logger.error("删除平衡告警开关分权分域权限配置失败");
				return ResultBuilder.failResult("删除平衡告警开关分权分域权限配置失败");
			}
			if (null != del && del.isSuccess()) {
				logger.info("[balanceSwitchPowersSave()--success:" + del.getMessage() + "]");
				return ResultBuilder.successResult(del.getSuccessValue(), del.getMessage());
			}
			logger.error("[balanceSwitchPowersSave()--fail:" + del.getMessage() + "]");
			return ResultBuilder.failResult(del.getMessage());
		} catch (Exception e) {
			logger.error("balanceSwitchPowersSave()-->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> balanceSwitchPowersStateChange(String balanceSwitchPowers) {
		try {
			logger.info(String.format("[balanceSwitchPowersStateChange()->request params:%s]", balanceSwitchPowers));
			if (null == balanceSwitchPowers || ParamChecker.isNotMatchJson(balanceSwitchPowers)) {
				logger.error("[balanceSwitchPowersSave():valid--fail:参数为空或不是json格式!]");
				return ResultBuilder.failResult("参数为空或不是json格式!");
			}
			BalanceSwitchPowers parseObject = JSONObject.parseObject(balanceSwitchPowers, BalanceSwitchPowers.class);
			if (StringUtils.isBlank(parseObject.getId())) {
				logger.error("[balanceSwitchPowersSave():valid--fail:修改平衡开关权限配置，id不能为空!]");
				return ResultBuilder.failResult("修改平衡开关权限配置，id不能为空!");
			}
			if (StringUtils.isBlank(parseObject.getFacilityTypeId())) {
				logger.error("[balanceSwitchPowersSave():valid--fail:修改平衡开关权限配置，设施类型facilityTypeId不能为空!]");
				return ResultBuilder.failResult("修改平衡开关权限配置，设施类型facilityTypeId不能为空!");
			}
			if (StringUtils.isBlank(parseObject.getManageState())) {
				logger.error("[balanceSwitchPowersSave():valid--fail:修改平衡开关权限配置，管理状态manageState不能为空!]");
				return ResultBuilder.failResult("修改平衡开关权限配置，管理状态manageState不能为空!");
			}
			// 启用校验，校验当前设施类型是否配有平衡告警开关权限(有效时间,启动状态)
			if (CommonConstants.ENABLED.equals(parseObject.getManageState())) {
				RpcResponse<Boolean> result = balanceSwitchPowersQueryService
						.checkByFacilityTypeId(parseObject.getFacilityTypeId());
				if (null != result && result.isSuccess() && result.getSuccessValue()) {
					logger.error("[balanceSwitchPowersSave():valid--fail:该设施类型下配有平衡告警开关,请先停用!]");
					return ResultBuilder.failResult("该设施类型下配有平衡告警开关,请先停用!");
				} else if (null != result && !result.isSuccess()) {
					logger.error("[balanceSwitchPowersSave():valid--fail:" + result.getMessage() + "]");
					return ResultBuilder.failResult(result.getMessage());
				}
			}

			RpcResponse<String> update = balanceSwitchPowersCrudService.balanceSwitchPowersUpdate(parseObject);
			if (null == update) {
				logger.error("修改平衡告警开关分权分域权限配置失败");
				return ResultBuilder.failResult("修改平衡告警开关分权分域权限配置失败");
			}
			if (null != update && update.isSuccess()) {
				logger.info("[balanceSwitchPowersSave()--success:" + update.getMessage() + "]");
				return ResultBuilder.successResult(update.getSuccessValue(), update.getMessage());
			}
			logger.error("[balanceSwitchPowersSave()--fail:" + update.getMessage() + "]");
			return ResultBuilder.failResult(update.getMessage());
		} catch (Exception e) {
			logger.error("balanceSwitchPowersSave()-->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description: 测试调用RPC接口，保存开启关闭平衡告警记录
	 * @param balanceSwitchPowers
	 * @return
	 */
	public Result<String> open(String balanceSwitchPowers) {
		RpcResponse<Boolean> openOrClose = balanceSwitchStateRecordCurdService
				.openOrClose(JSONObject.parseObject(balanceSwitchPowers));
		if (openOrClose.isSuccess()) {
			return ResultBuilder.successResult(null, openOrClose.getMessage());
		}
		return ResultBuilder.failResult(openOrClose.getMessage());
	}



	/**
	 * 
	 * @Description: 测试调用RPC接口，获取平衡告警状态
	 * @param balanceSwitchPowers
	 * @return
	 */
	public Result<BalanceSwitchStateRecord> getState(String balanceSwitchPowers) {
		RpcResponse<BalanceSwitchStateRecord> state = balanceSwitchStateRecordQueryService
				.getState(JSONObject.parseObject(balanceSwitchPowers));
		if (state.isSuccess()) {
			return ResultBuilder.successResult(state.getSuccessValue(), state.getMessage());
		}
		return ResultBuilder.failResult(state.getMessage());
	}
}
