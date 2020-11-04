package com.locman.app.query.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.locman.app.common.util.RedisUtil;
import com.locman.app.entity.vo.Access;
import com.locman.app.entity.vo.Role;
import com.locman.app.entity.vo.Usc;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.RegexUtil;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.usc.base.query.AccUserBaseQueryService;
import com.run.usc.base.query.TenAccBaseQueryService;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * 
 * <class description> 用户查询业务逻辑处理类
 * 
 * @author: liaodan
 * @version: 1.0, 2017年9月14日
 */
@Service
@SuppressWarnings("rawtypes")
public class UscQueryService {

	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	UserBaseQueryService		userBaseQueryService;
	@Autowired
	TenAccBaseQueryService		tenAccBaseQueryService;
	@Autowired
	UserRoleBaseQueryService	userRoleBaseQueryService;
	@Autowired
	AccUserBaseQueryService		accUserBaseQueryService;
	@Autowired
	private CommonService		commonService;
	/*
	 * @Autowired private static RedisTemplate<String, Object> redisTemplate;
	 */



	/**
	 * 
	 * app用户登录: 1、判断账号是否存在，不存在返回状态为false 2、判断输入的账号是邮箱还是手机号码，分别访问对应的登录方法
	 * 
	 * @param param
	 *            {"loginAccount":"","password":""}
	 * @return RpcResponse {success，successValue,msg}
	 *         successValue：包含用户个人信息，接入方信息，用户角色信息
	 */
	public Result<?> uscAppLogin(String param) {
		Usc user = new Usc();
		RpcResponse response = new RpcResponse<>();
		try {
			Result<?> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return result;
			}
			JSONObject paramsJson = JSONObject.parseObject(param);
			if (!paramsJson.containsKey("loginAccount")) {
				return ResultBuilder.noBusinessResult();
			}
			String loginAccount = paramsJson.getString("loginAccount");
			if (!paramsJson.containsKey("password")) {
				return ResultBuilder.noBusinessResult();
			}
			String password = paramsJson.getString("password");
			response = userBaseQueryService.appUserAuthz(loginAccount, password);
			if (response.getSuccessValue() == null) {
				return ResultBuilder.failResult("账号或密码错误");
			}
			Object object = response.getSuccessValue();
			JSONObject jObject = (JSONObject) JSONObject.toJSON(object);
			user = JSONObject.toJavaObject(jObject, Usc.class);
			RpcResponse uscInfoResponse = userBaseQueryService.getUserByToken(user.getToken());
			if (uscInfoResponse.getSuccessValue() == null) {
				return ResultBuilder.emptyResult();
			}
			Object uscInfoObj = uscInfoResponse.getSuccessValue();
			JSONObject uscInfo = (JSONObject) JSONObject.toJSON(uscInfoObj);
			String state = uscInfo.getString("state");
			if (Constant.INVALID.equals(state)) {
				return ResultBuilder.failResult("用户已禁用");
			}
			String isDelete = uscInfo.getString("isDelete");
			if (Constant.INVALID.equals(isDelete)) {
				return ResultBuilder.failResult("用户已删除");
			}
			user.setMobile(uscInfo.getString("mobile"));
			user.setState(state);
			user.setIsDelete(isDelete);
			user.setActivateState(uscInfo.getString("activateState"));
			user.setUserName(uscInfo.getString("userName"));
			user.setUserType(uscInfo.getString("userType"));
			user.setLoginAccount(uscInfo.getString("loginAccount"));
			user.setEmail(uscInfo.getString("email"));
			user.setUserCode(uscInfo.getString("userCode"));
			String accessId = null;
			RpcResponse<List<Map>> accResponse = accUserBaseQueryService.getListAccessByUserId(user.getUserId());
			Access access = new Access();
			if (accResponse.isSuccess() && accResponse.getSuccessValue() != null) {
				List<Map> accList = accResponse.getSuccessValue();
				if (!accList.isEmpty() && accList.size() != 0) {
					for (Map accMap : accList) {
						JSONObject accJSON = (JSONObject) JSONObject.toJSON(accMap);
						if (accJSON.containsKey("accessType") && accJSON.getString("accessType").equals("LOCMAN")) {
							access.setAccessId(accJSON.getString("_id"));
							access.setAccessName(accJSON.getString("accessName"));
							access.setAccessTenementName(accJSON.getString("accessTenementName"));
							access.setAccessTenementId(accJSON.getString("accessTenementId"));
							access.setAccessSecret(accJSON.getString("accessSecret"));
							access.setIpAddress(accJSON.getString("ipAddress"));
							accessId = access.getAccessSecret();
						}
					}
				}
			}

			user.setAccess(access);
			// redisTemplate.opsForValue().set(Constant.APP + user.getUserId(),
			// user);
			RedisUtil.set(Constant.APP + user.getUserId(), user.getAccess().getAccessId());
			List<Role> roleList = findRoleByUserId(user.getUserId(), accessId);

			user.setRole(roleList);
			return ResultBuilder.successResult(user, response.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 根据用户id和接入方查询组织和岗位 <method description>
	 *
	 * @param userId
	 * @param accessId
	 * @return
	 */
	public List<Role> findRoleByUserId(String userId, String accessId) {
		RpcResponse<List<Map>> roleResponse = userRoleBaseQueryService.getRoleMessBySecret(userId, accessId);
		List<Role> roleList = new ArrayList<Role>();
		if (roleResponse.getSuccessValue() == null) {
			ResultBuilder.emptyResult();
		} else {
			List<Map> rpcList = roleResponse.getSuccessValue();
			Role role = null;
			if (!rpcList.isEmpty() && rpcList.size() > 0) {
				for (Map map : rpcList) {
					role = new Role();
					role.setRoleId((String) map.get("roleId"));
					role.setRoleName((String) map.get("roleName"));
					role.setOrganizeId((String) map.get("organizeId"));
					role.setOrganizedName((String) map.get("organizedName"));
					roleList.add(role);
				}
			}
		}
		return roleList;
	}



	/**
	 * 
	 * <method description> 发送验证码
	 *
	 * @param param
	 *            手机号码或邮箱地址
	 * @return
	 */
	public Result<?> appSendMassage(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return result;
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("emailMob")) {
				return ResultBuilder.noBusinessResult();
			}
			String emailMob = jsonObject.getString("emailMob");
			if (!RegexUtil.validateMobile(emailMob)) {
				return ResultBuilder.failResult("手机号码错误");
			}
			if (!jsonObject.containsKey("loginAccount")) {
				return ResultBuilder.noBusinessResult();
			}
			String loginAccount = jsonObject.getString("loginAccount");
			RpcResponse<Boolean> blResponse = userBaseQueryService.checkUserExitByEmiMob(emailMob);
			if (blResponse.getSuccessValue() != true) {
				return ResultBuilder.failResult("手机号不存在");
			}
			RpcResponse<String> response = userBaseQueryService.sendEmiMob(emailMob, Constant.MOBILE, loginAccount);
			if (response != null && response.isSuccess()) {
				return ResultBuilder.successResult(response.getSuccessValue(), response.getMessage());
			}
			return ResultBuilder.failResult(response.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 根据token查询user <method description>
	 *
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public RpcResponse getUserByToken(String token) throws Exception {
		return userBaseQueryService.getUserByToken(token);
	}



	/**
	 * 根据用户的岗位id查询用户的组织id和岗位id <method description>
	 *
	 * @param accessSecret
	 * @param roleId
	 * @param userId
	 * @return
	 */
	public Role findOrganizationByRoleId(String accessSecret, String roleId, String userId) {
		// 查询用户组织和岗位
		List<Role> roles = findRoleByUserId(userId, accessSecret);
		if (roles != null && !roles.isEmpty()) {
			for (Role role : roles) {
				if (roleId.equals(role.getRoleId())) {
					return role;
				}
			}
		}
		return new Role();
	}



	/**
	 * 查询当前组织及下级所有组织id <method description>
	 *
	 * @param organizationId
	 * @return
	 */
	public List<String> findOrganizationAndLower(String organizationId) {
		List<String> organizationIdList = Lists.newArrayList();
		try {
			// 先获取该组织及其下所有组织id
			String httpValueByGet = commonService.requestRestGet(Constant.INTERGATEWAY_PORT,
					"v3/organization/ownAndSonInfo/" + organizationId, null);
			Map<String, String> orgMap = commonService.checkResult(JSONObject.parseObject(httpValueByGet));
			if (orgMap.containsKey("success")) {
				String orgIds = orgMap.get("success");
				if (orgIds != null && !"".equals(orgIds) && !"{}".equals(orgIds)) {
					JSONArray jsonArray = JSON.parseArray(orgMap.get("success"));
					List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
					for (Map map : organizationInfoList) {
						organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
					}
				}
			} else {
				logger.error("[findOrganizationAndLower()->invalid：组织查询失败!]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return organizationIdList;
	}



	/**
	 * 根据token查询userId <method description>
	 *
	 * @param token
	 * @return
	 */
	public String findUserIdByToken(String token) {
		try {
			RpcResponse userRpc = getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return null;
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			return userId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
