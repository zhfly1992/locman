/*
 * File name: DistributionPowersRestCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月15日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.DistributionPowersCudService;
import com.run.locman.api.entity.DistributionPowers;
import com.run.locman.api.query.service.DistributionPowersQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DistributionPowersConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 分权分域管理
 * @author: qulong
 * @version: 1.0, 2017年9月15日
 */

@Service
public class DistributionPowersRestCudService {

	@Autowired
	private DistributionPowersCudService	distributionPowersCudService;
	@Autowired
	private DistributionPowersQueryService	distributionPowersQueryService;

	private static final String				NUMBER_ZERO	= "0";

	private Logger							logger		= Logger.getLogger(CommonConstants.LOGKEY);



	public Result<String> saveDistributionPowers(String params) {
		logger.info(String.format("[saveDistributionPowers()->request params:%s]", params));
		if (ParamChecker.isNotMatchJson(params)) {
			logger.error(
					String.format("[saveDistributionPowers()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		JSONObject paramObject = JSONObject.parseObject(params);
		StringBuffer orgName = new StringBuffer();
		StringBuffer userName = new StringBuffer();
		DistributionPowers distributionPowers = new DistributionPowers();

		Result<String> powerCheck = powerCheck(paramObject, orgName, userName);
		if (null != powerCheck) {
			return ResultBuilder.failResult(powerCheck.getResultStatus().getResultMessage());
		}

		Result<String> powerHandle = powerHandle(paramObject, orgName, userName, distributionPowers);
		if (null != powerHandle) {
			return ResultBuilder.failResult(powerHandle.getResultStatus().getResultMessage());
		}

		// 开始时间
		if (paramObject.containsKey(DistributionPowersConstants.STARTTIME)) {
			String startTime = paramObject.getString(DistributionPowersConstants.STARTTIME);
			if (startTime != null && !"".equals(startTime)) {
				distributionPowers.setStartTime(startTime);
			} else {
				distributionPowers
						.setStartTime(DateUtils.formatDate(Calendar.getInstance().getTime(), DateUtils.DATE_FORMAT));
			}
		}

		// 结束时间
		if (paramObject.containsKey(DistributionPowersConstants.ENDTIME)) {
			String endTime = paramObject.getString(DistributionPowersConstants.ENDTIME);
			if (endTime != null && !"".equals(endTime)) {
				distributionPowers.setEndTime(endTime);
			} else {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 99);
				distributionPowers.setEndTime(DateUtils.formatDate(cal.getTime(), DateUtils.DATE_FORMAT));
			}
		}

		// 备注
		if (paramObject.containsKey(DistributionPowersConstants.REMARK)) {
			distributionPowers.setRemark(paramObject.getString(DistributionPowersConstants.REMARK));
		}

		/** ---------------验证通过------- */
		try {
			RpcResponse<String> saveDistributionPowersResult = distributionPowersCudService
					.saveDistributionPowers(distributionPowers);
			if (saveDistributionPowersResult.isSuccess()) {
				logger.info("[saveDistributionPowers()->success]");
				return ResultBuilder.successResult(saveDistributionPowersResult.getSuccessValue(),
						saveDistributionPowersResult.getMessage());
			}
			logger.error("[saveDistributionPowers()->fail]");
			return ResultBuilder.failResult(saveDistributionPowersResult.getMessage());
		} catch (Exception e) {
			logger.error("saveDistributionPowers()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param paramObject
	 * @param orgName
	 * @param userName
	 * @param distributionPowers
	 */

	private Result<String> powerHandle(JSONObject paramObject, StringBuffer orgName, StringBuffer userName,
			DistributionPowers distributionPowers) {
		String hour = paramObject.getString("hour");

		if (StringUtils.isBlank(hour) || NUMBER_ZERO.equals(hour.trim())) {
			hour = NUMBER_ZERO;
		} else {
			// 1-23的正整数
			String hourPattern = "^([1-9]|1\\d|2[0-3])$";
			boolean hourMatch = Pattern.matches(hourPattern, hour);
			if (!hourMatch) {
				logger.error("[saveDistributionPowers()--volidfail:超时未关所配小时hour必须为1-23的正整数!]");
				return ResultBuilder.failResult("超时未关所配小时hour必须为1-23的正整数!");
			}
		}

		distributionPowers.setId(UtilTool.getUuId());
		// 默认停用
		distributionPowers.setManageState(CommonConstants.DISABLED);
		distributionPowers.setHour(hour);
		distributionPowers.setMinute(NUMBER_ZERO);
		distributionPowers.setPowerName(paramObject.getString("powerName"));
		distributionPowers.setUserInfo(paramObject.getString("userInfo"));
		distributionPowers.setOrgName(orgName.toString());
		distributionPowers.setUserName(userName.toString());

		// 接入方秘钥验证
		if (!paramObject.containsKey(DistributionPowersConstants.ACCESSSECRET)) {
			logger.error("[saveDistributionPowers()->error:key中必须包含接入方密匙]");
			return ResultBuilder.noBusinessResult();
		}
		String accessSecret = paramObject.getString(DistributionPowersConstants.ACCESSSECRET);
		if (accessSecret == null || "".equals(accessSecret)) {
			logger.error("[saveDistributionPowers()->error:接入方密匙不能为空]");
			return ResultBuilder.failResult("接入方秘钥不能为空");
		}
		distributionPowers.setAccessSecret(accessSecret);

		// 设施类型ID验证
		if (!paramObject.containsKey(DistributionPowersConstants.FACILITYTYPEID)) {
			logger.error("[saveDistributionPowers()->error:key中必须包含设施类型id]");
			return ResultBuilder.noBusinessResult();
		}
		String facilityTypeId = paramObject.getString(DistributionPowersConstants.FACILITYTYPEID);
		if (facilityTypeId == null || "".equals(facilityTypeId)) {
			logger.error("[saveDistributionPowers()->error:设施类型id不能为空]");
			return ResultBuilder.failResult("设施类型ID不能为空");
		}
		distributionPowers.setFacilityTypeId(facilityTypeId);
		return null;
	}



	/**
	 * @Description:
	 * @param paramObject
	 * @param orgName
	 * @param userName
	 */

	@SuppressWarnings("unchecked")
	private Result<String> powerCheck(JSONObject paramObject, StringBuffer orgName, StringBuffer userName) {
		if (StringUtils.isBlank(paramObject.getString(CommonConstants.POWERNAME))) {
			logger.error("[saveDistributionPowers()--volidfail:分权分域名称powerName不能为空!]");
			return ResultBuilder.failResult("分权分域名称powerName不能为空!");
		}
		if (StringUtils.isBlank(paramObject.getString(CommonConstants.USERINFO))) {
			logger.error("[saveDistributionPowers()--volidfail:分权分域所配的人员信息userInfo不能为空!]");
			return ResultBuilder.failResult("分权分域所配的人员信息userInfo不能为空!");
		}

		List<JSONObject> userList = (List<JSONObject>) paramObject.get("userInfo");
		if (null == userList || userList.size() == 0) {
			logger.error("[saveDistributionPowers()--volidfail:人员信息userInfo不能为空!]");
			return ResultBuilder.failResult("人员信息userInfo不能为空!");
		}

		// 校验所配的人员信息是否为空
		for (JSONObject jsonList : userList) {
			if (StringUtils.isBlank(jsonList.getString("organizationId"))) {
				logger.error("[saveDistributionPowers()--volidfail:组织organizationId不能为空!]");
				return ResultBuilder.failResult("组织organizationId不能为空!");
			}
			if (StringUtils.isBlank(jsonList.getString("userId"))) {
				logger.error("[saveDistributionPowers()--volidfail:人员userId不能为空!]");
				return ResultBuilder.failResult("人员userId不能为空!");
			}
			if (StringUtils.isBlank(jsonList.getString("userName"))) {
				logger.error("[saveDistributionPowers()--volidfail:人员名称userName不能为空!]");
				return ResultBuilder.failResult("人员名称userName不能为空!");
			}
			if (StringUtils.isBlank(jsonList.getString("orgName"))) {
				logger.error("[saveDistributionPowers()--volidfail:组织名称orgName不能为空!]");
				return ResultBuilder.failResult("组织名称orgName不能为空!");
			}
		}

		for (int i = 0; i < userList.size(); i++) {
			if (i == 0) {
				orgName.append(userList.get(i).getString("orgName"));
				userName.append(userList.get(i).getString("userName"));
			} else {
				orgName.append("/" + userList.get(i).getString("orgName"));
				userName.append("/" + userList.get(i).getString("userName"));
			}
		}

		return null;
	}



	public Result<String> editDistributionPowers(String params) {
		logger.info(String.format("[editDistributionPowers()->request params:%s]", params));
		if (ParamChecker.isNotMatchJson(params)) {
			logger.error(
					String.format("[editDistributionPowers()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		JSONObject paramObject = JSONObject.parseObject(params);
		StringBuffer orgName = new StringBuffer();
		StringBuffer userName = new StringBuffer();
		Map<String, String> map = Maps.newHashMap();

		Result<String> editPowerParamCheck = editPowerParamCheck(paramObject, orgName, userName, map);
		if (!CommonConstants.NUMBER_FOUR_ZERO.equals(editPowerParamCheck.getResultStatus().getResultCode())) {
			return ResultBuilder.failResult(editPowerParamCheck.getResultStatus().getResultMessage());
		}

		// 开始时间
		if (paramObject.containsKey(DistributionPowersConstants.STARTTIME)) {
			String startTime = paramObject.getString(DistributionPowersConstants.STARTTIME);
			if (startTime != null && !"".equals(startTime)) {
				map.put(DistributionPowersConstants.STARTTIME, startTime);
			}
		}

		// 结束时间
		if (paramObject.containsKey(DistributionPowersConstants.ENDTIME)) {
			String endTime = paramObject.getString(DistributionPowersConstants.ENDTIME);
			if (endTime != null && !"".equals(endTime)) {
				map.put(DistributionPowersConstants.ENDTIME, endTime);
			}
		}
		String hour = paramObject.getString("hour");
		if (StringUtils.isBlank(hour) || hour.trim().equals(NUMBER_ZERO)) {
			hour = NUMBER_ZERO;
		} else {
			// 1-23的正整数
			String hourPattern = "^([1-9]|1\\d|2[0-3])$";
			boolean hourMatch = Pattern.matches(hourPattern, hour);
			if (!hourMatch) {
				logger.error("[saveDistributionPowers()--volidfail:超时未关所配小时hour必须为1-23的正整数!]");
				return ResultBuilder.failResult("超时未关所配小时hour必须为1-23的正整数!");
			}
		}

		map.put("hour", hour);
		map.put("minute", "0");

		// 备注
		if (paramObject.containsKey(DistributionPowersConstants.REMARK)) {
			map.put(DistributionPowersConstants.REMARK, paramObject.getString(DistributionPowersConstants.REMARK));
		}
		try {
			RpcResponse<DistributionPowers> distributionPowersById = distributionPowersQueryService
					.getDistributionPowersById(editPowerParamCheck.getValue());
			if (!distributionPowersById.isSuccess()) {
				return ResultBuilder.failResult(distributionPowersById.getMessage());
			}
			RpcResponse<String> editDistributionPowersResult = distributionPowersCudService.editDistributionPowers(map);
			if (editDistributionPowersResult.isSuccess()) {
				return ResultBuilder.successResult(editDistributionPowersResult.getSuccessValue(),
						editDistributionPowersResult.getMessage());
			}
			return ResultBuilder.failResult(editDistributionPowersResult.getMessage());
		} catch (Exception e) {
			logger.error("editDistributionPowers()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param paramObject
	 * @param orgName
	 * @param userName
	 * @return
	 */

	@SuppressWarnings("unchecked")
	private Result<String> editPowerParamCheck(JSONObject paramObject, StringBuffer orgName, StringBuffer userName,
			Map<String, String> map) {
		if (StringUtils.isBlank(paramObject.getString(CommonConstants.POWERNAME))) {
			logger.error("[editDistributionPowers()--volidfail:分权分域名称powerName不能为空!]");
			return ResultBuilder.failResult("分权分域名称powerName不能为空!");
		}
		if (StringUtils.isBlank(paramObject.getString(CommonConstants.USERINFO))) {
			logger.error("[editDistributionPowers()--volidfail:分权分域所配的人员信息userInfo不能为空!]");
			return ResultBuilder.failResult("分权分域所配的人员信息userInfo不能为空!");
		}
		List<JSONObject> userList = (List<JSONObject>) paramObject.get("userInfo");
		if (null == userList || userList.size() == 0) {
			logger.error("[editDistributionPowers()--volidfail:人员信息userInfo不能为空!]");
			return ResultBuilder.failResult("人员信息userInfo不能为空!");
		}
		// 校验所配的人员信息是否为空
		for (JSONObject jsonList : userList) {
			if (StringUtils.isBlank(jsonList.getString("organizationId"))) {
				logger.error("[editDistributionPowers()--volidfail:组织organizationId不能为空!]");
				return ResultBuilder.failResult("组织organizationId不能为空!");
			}
			if (StringUtils.isBlank(jsonList.getString("userId"))) {
				logger.error("[editDistributionPowers()--volidfail:人员userId不能为空!]");
				return ResultBuilder.failResult("人员userId不能为空!");
			}
			if (StringUtils.isBlank(jsonList.getString("userName"))) {
				logger.error("[editDistributionPowers()--volidfail:人员名称userName不能为空!]");
				return ResultBuilder.failResult("人员名称userName不能为空!");
			}
			if (StringUtils.isBlank(jsonList.getString("orgName"))) {
				logger.error("[editDistributionPowers()--volidfail:组织名称orgName不能为空!]");
				return ResultBuilder.failResult("组织名称orgName不能为空!");
			}
		}

		for (int i = 0; i < userList.size(); i++) {
			if (i == 0) {
				orgName.append(userList.get(i).getString("orgName"));
				userName.append(userList.get(i).getString("userName"));
			} else {
				orgName.append("/" + userList.get(i).getString("orgName"));
				userName.append("/" + userList.get(i).getString("userName"));
			}
		}

		if (!paramObject.containsKey(DistributionPowersConstants.ID)) {
			logger.error("[editDistributionPowers()->error:key中必须包含id]");
			return ResultBuilder.noBusinessResult();
		}
		String id = paramObject.getString(DistributionPowersConstants.ID);
		if (id == null || "".equals(id)) {
			logger.error("[editDistributionPowers()->error:主键id不能为空]");
			return ResultBuilder.failResult("主键ID不能为空");
		}

		map.put(DistributionPowersConstants.ID, id);
		map.put("powerName", paramObject.getString("powerName"));
		map.put("userInfo", paramObject.getString("userInfo"));
		map.put("orgName", orgName.toString());
		map.put("userName", userName.toString());

		return ResultBuilder.successResult(id, "执行成功!");
	}



	public Result<Boolean> deleteDistributionPowers(String id) {
		logger.info(String.format("[deleteDistributionPowers()->request params:%s]", id));
		if (id == null || "".equals(id)) {
			logger.error(
					String.format("[deleteDistributionPowers()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.noBusinessResult();
		}
		try {
			RpcResponse<Boolean> deleteDistributionPowersResult = distributionPowersCudService
					.deleteDistributionPowers(id);
			if (deleteDistributionPowersResult.isSuccess()) {
				logger.info("[deleteDistributionPowers()->success]");
				return ResultBuilder.successResult(deleteDistributionPowersResult.getSuccessValue(),
						deleteDistributionPowersResult.getMessage());
			}
			logger.error(String.format("[deleteDistributionPowers()->fail:%s]",
					deleteDistributionPowersResult.getMessage()));
			return ResultBuilder.failResult(deleteDistributionPowersResult.getMessage());
		} catch (Exception e) {
			logger.error("deleteDistributionPowers()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> changeState(String params) {
		logger.info(String.format("[changeState()->request params:%s]", params));
		if (ParamChecker.isNotMatchJson(params)) {
			logger.error(String.format("[changeState()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		JSONObject paramObject = JSONObject.parseObject(params);
		if (!paramObject.containsKey(DistributionPowersConstants.ID)) {
			logger.error(String.format("[changeState()->error:%s--%s]", LogMessageContants.NO_PARAMETER_EXISTS,
					DistributionPowersConstants.ID));
			return ResultBuilder.noBusinessResult();
		}
		String id = paramObject.getString(DistributionPowersConstants.ID);
		if (id == null || "".equals(id)) {
			logger.error(String.format("[changeState()->error:%s--%s]", LogMessageContants.NO_PARAMETER_EXISTS,
					DistributionPowersConstants.ID));
			return ResultBuilder.failResult("主键ID不能为空");
		}
		String facilityTypeId = paramObject.getString(DistributionPowersConstants.FACILITYTYPEID);
		if (facilityTypeId == null || "".equals(facilityTypeId)) {
			logger.error(String.format("[changeState()->error:%s--%s]", LogMessageContants.NO_PARAMETER_EXISTS,
					DistributionPowersConstants.FACILITYTYPEID));
			return ResultBuilder.failResult("设施类型ID不能为空");
		}
		Map<String, String> map = Maps.newHashMap();
		map.put(DistributionPowersConstants.ID, id);

		if (!paramObject.containsKey(DistributionPowersConstants.MANAGESTATE)) {
			logger.error(String.format("[changeState()->error:%s--%s]", LogMessageContants.NO_PARAMETER_EXISTS,
					DistributionPowersConstants.MANAGESTATE));
			return ResultBuilder.noBusinessResult();
		}
		String manageState = paramObject.getString(DistributionPowersConstants.MANAGESTATE);
		if (!CommonConstants.ENABLED.equals(manageState) && !CommonConstants.DISABLED.equals(manageState)) {
			logger.error("[changeState()->error:管理状态输入有误]");
			return ResultBuilder.failResult("管理状态输入有误");
		}
		map.put(DistributionPowersConstants.MANAGESTATE, manageState);

		try {
			// if (CommonConstants.ENABLED.equals(manageState)) { //
			// 启用分权分域配置，校验当前是否已存在合法的配置（未过期，并且启用）
			// // 验证该设施类型的分权分域配置是否存在
			// RpcResponse<DistributionPowers>
			// distributionPowersByFacilityTypeId =
			// distributionPowersQueryService
			// .getByFacilityTypeId(facilityTypeId);
			// if (!distributionPowersByFacilityTypeId.isSuccess()) {
			// return
			// ResultBuilder.failResult(distributionPowersByFacilityTypeId.getMessage());
			// }
			// DistributionPowers powers =
			// distributionPowersByFacilityTypeId.getSuccessValue();
			// if (powers != null) {
			// return ResultBuilder.failResult("当前已存在启用的分权分域配置，请停用后再操作");
			// }
			// }

			RpcResponse<String> editDistributionPowersResult = distributionPowersCudService.editDistributionPowers(map);
			if (editDistributionPowersResult.isSuccess()) {
				logger.info("[changeState()->success]");
				return ResultBuilder.successResult(editDistributionPowersResult.getSuccessValue(),
						editDistributionPowersResult.getMessage());
			}
			logger.error(String.format("[changeState()->fail:%s]", editDistributionPowersResult.getMessage()));
			return ResultBuilder.failResult(editDistributionPowersResult.getMessage());
		} catch (Exception e) {
			logger.error("changeState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
