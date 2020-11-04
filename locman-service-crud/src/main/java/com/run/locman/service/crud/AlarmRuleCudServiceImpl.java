/*
 * File name: AlarmRuleCudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2018年1月18日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.AlarmRuleCrudRepository;
import com.run.locman.api.crud.repository.BaseDataSynchronousStateCrudRepository;
import com.run.locman.api.crud.repository.DefinedAlarmRuleDeviceCudRepository;
import com.run.locman.api.crud.service.AlarmRuleService;
import com.run.locman.api.entity.AlarmRule;
import com.run.locman.api.entity.BaseDataSynchronousState;
import com.run.locman.api.entity.DefinedAlarmRuleDevice;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceDeletionConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.RuleContants;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * @Description: 告警规则rpc
 * @author: lkc
 * @version: 1.0, 2017年10月27日
 */
@Transactional(rollbackFor = Exception.class)
public class AlarmRuleCudServiceImpl implements AlarmRuleService {
	private static final Logger						logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private AlarmRuleCrudRepository					ruleTemplate;

	@Autowired
	private DefinedAlarmRuleDeviceCudRepository		definedAlarmRuleDeviceCudRepository;

	@Autowired
	private BaseDataSynchronousStateCrudRepository	baseDataSynchronousStateCrudRepository;



	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<String> alarmRuleAdd(JSONObject jsonObject) {
		try {
			if (StringUtils.isBlank(jsonObject.getString(RuleContants.USC_ACCESS_SECRET))) {
				logger.error("[alarmRuleAdd()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(jsonObject.getString(RuleContants.RULE_TYPE ))) {
				logger.error("[alarmRuleAdd()->invalid：规则类型不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("规则类型不能为空！");
			}
			if (StringUtils.isBlank(jsonObject.getString(RuleContants.USER_ID))) {
				logger.error("[alarmRuleAdd()->invalid：用户不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户不能为空！");
			}
			if (StringUtils.isBlank(jsonObject.getString(RuleContants.DEVICE_TYPE_ID))) {
				logger.error("[alarmRuleAdd()->invalid：设备类型不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备类型不能为空！");
			}
			if (StringUtils.isBlank(jsonObject.getString(RuleContants.RULE_NAME))) {
				logger.error("[alarmRuleAdd()->invalid：规则名不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("规则名不能为空！");
			}
			if (StringUtils.isBlank(jsonObject.getString(RuleContants.RULE_CONTENT))) {
				logger.error("[alarmRuleAdd()->invalid：规则内容不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("规则内容不能为空！");
			}
			logger.info(String.format("[alarmRuleAdd()->进入方法,参数:%s]", jsonObject));
			AlarmRule alarmObj = new AlarmRule();
			alarmObj.setAccessSecret(jsonObject.getString("accessSecret"));
			alarmObj.setUserId(jsonObject.getString("userId"));
			alarmObj.setDeviceTypeId(jsonObject.getString("deviceTypeId"));
			alarmObj.setRuleName(jsonObject.getString("ruleName"));
			alarmObj.setRuleContent(jsonObject.getString("ruleContent"));
			alarmObj.setDeviceCount(jsonObject.getInteger("deviceCount"));
			String uuid = UtilTool.getUuId();
			alarmObj.setId(uuid);
			alarmObj.setCrateTime(DateUtils.formatDate(new Date()));
			alarmObj.setUpdateTime(DateUtils.formatDate(new Date()));
			alarmObj.setManageState(RuleContants.VALID);
			alarmObj.setIsDelete(RuleContants.VALID);
			alarmObj.setPublishState(RuleContants.INVALID);
			alarmObj.setAlarmLevel(Integer.parseInt(jsonObject.getString("alarmLevel")));
			alarmObj.setIsMatchOrder(jsonObject.getBooleanValue("isMatchOrder"));
			alarmObj.setRemark(jsonObject.getString("remark"));
			alarmObj.setRuleType(jsonObject.getString("ruleType"));

			// 设置规则
			StringBuffer rule = getRule(alarmObj.getRuleContent());
			if (StringUtils.isBlank(rule)) {
				logger.error("[alarmRuleAdd()->invalid：规则格式不合法！]");
				return RpcResponseBuilder.buildErrorRpcResp("规则格式不合法！");
			}
			alarmObj.setRule(rule.toString());
			List<String> deviceIdsAdd = (List<String>) jsonObject.get("deviceIdsAdd");
			// 批量添加规则和设备的中间表记录
			if (deviceIdsAdd != null && deviceIdsAdd.size() > 0) {
				for (int j = 0; j < deviceIdsAdd.size(); j++) {
					DefinedAlarmRuleDevice definedAlarmRuleDevice = new DefinedAlarmRuleDevice();
					definedAlarmRuleDevice.setId(UtilTool.getUuId());
					definedAlarmRuleDevice.setDeviceId(deviceIdsAdd.get(j));
					definedAlarmRuleDevice.setAlarmRuleId(uuid);
					definedAlarmRuleDeviceCudRepository.addBindDevices(definedAlarmRuleDevice);
				}
			}
			logger.info(String.format("[alarmRuleAdd()->即将保存告警规则信息,参数:%s]", alarmObj));
			int insertModel = ruleTemplate.insertModel(alarmObj);
			if (insertModel > 0) {
				logger.info("[alarmRuleAdd()->success: " + MessageConstant.ADD_SUCCESS + "id:" + uuid + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.ADD_SUCCESS, uuid);
			} else {
				logger.error("[alarmRuleAdd()->error: " + MessageConstant.ADD_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.ADD_FAIL);
			}
		} catch (Exception e) {
			logger.error("alarmRuleAdd()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @param string
	 * @return
	 * @Description: 组装规则引擎
	 */

	private StringBuffer getRule(String string) {
		JSONObject map = JSON.parseObject(string);
		StringBuffer rules = new StringBuffer();
		String keys = (String) map.get("key");
		String conditions = (String) map.get("condition");
		String values = (String) map.get("value");
		rules.append("package locman.alarm.rule\r\n");
		rules.append("import java.util.Map;\r\n");
		// 告警工具类
		rules.append("import com.secbro.drools.model.invoke.AlarmInvokRun" + "" + ";\r\n"); 
		rules.append("import com.run.locman.api.util.RuleCompareUtil;\r\n");
		rules.append("rule \"" + keys + "\"\r\n");
		rules.append("\twhen\r\n");
		rules.append("m : Map( ");
		String[] key = keys.split(",");
		String[] condition = conditions.split(",");
		String[] value = values.split(",");
		if (key.length >= 1) {
			for (int i = 0; i < key.length; i++) {
				rules.append(
						"RuleCompareUtil.strCom(m['" + key[i] + "'] , '" + condition[i] + "', '" + value[i] + "') ");
				if (i < key.length - 1) {
					rules.append("&&" + " ");
				}
			}
		}
		rules.append(")\r\n");
		rules.append("str : String()\r\n");
		rules.append("\t\nthen\r\n");
		// 调用告警工具类
		rules.append("\t\t if(m.get('_engine_check').equals(false)) AlarmInvokRun.locmanAlarmRun(m,str);\r\n"); 
		rules.append("end\r\n");
		logger.info("组装后的规则引擎为 : " + "\n" + rules.toString());
		return rules;
	}



	@Override
	@SuppressWarnings("unchecked")
	public RpcResponse<String> alarmRuleUpdate(JSONObject jsonObject) {
		try {
			if (jsonObject == null) {
				logger.error("[alarmRuleUpdate()->error:参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空");
			}
			if (StringUtils.isBlank(jsonObject.getString(RuleContants.RULE_ID))) {
				logger.error("[alarmRuleUpdate()->error:规则id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("规则id不能为空");
			}
			if (StringUtils.isBlank(jsonObject.getString(RuleContants.USER_ID))) {
				logger.error("[alarmRuleUpdate()->invalid：用户不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户不能为空！");
			}
			
			logger.info(String.format("[alarmRuleUpdate()->进入方法,参数:%s]", jsonObject));
			
			List<String> deviceIdsAdd = (List<String>) jsonObject.get("deviceIdsAdd");
			List<String> deviceIdsDel = (List<String>) jsonObject.get("deviceIdsDel");
			AlarmRule alarmRule = new AlarmRule();
			alarmRule.setId(jsonObject.getString("id"));
			if (jsonObject.containsKey(RuleContants.RULE_NAME)) {
				alarmRule.setRuleName(jsonObject.getString(RuleContants.RULE_NAME));
			}
			if (jsonObject.containsKey(RuleContants.ALARMI_LEVEL)) {
				alarmRule.setAlarmLevel(jsonObject.getInteger(RuleContants.ALARMI_LEVEL));
			}
			if (jsonObject.containsKey(DeviceDeletionConstants.DEVICE_COUNT)) {
				alarmRule.setDeviceCount(Integer.parseInt(jsonObject.getString(DeviceDeletionConstants.DEVICE_COUNT)));
			}
			alarmRule.setUserId(jsonObject.getString("userId"));
			alarmRule.setAccessSecret(jsonObject.getString("accessSecret"));
			alarmRule.setRule(getRule(jsonObject.getString("ruleContent")).toString());
			alarmRule.setIsMatchOrder(jsonObject.getBoolean("isMatchOrder"));
			if (jsonObject.containsKey(RuleContants.REMARK)) {
				alarmRule.setRemark(jsonObject.getString(RuleContants.REMARK));
			}
			if (jsonObject.containsKey(RuleContants.RULE_CONTENT)) {
				alarmRule.setRuleContent(jsonObject.getString(RuleContants.RULE_CONTENT));
			}
			// 删除规则和设备绑定关系
			if (deviceIdsDel != null && deviceIdsDel.size() > 0) {
				for (int i = 0; i < deviceIdsDel.size(); i++) {
					Map<String, Object> delParam = Maps.newHashMap();
					delParam.put("alarmRuleId", jsonObject.getString("id"));
					delParam.put("deviceId", deviceIdsDel.get(i));
					definedAlarmRuleDeviceCudRepository.delBindDevices(delParam);
				}
			}
			// 添加规则和设备绑定关系
			if (deviceIdsAdd != null && deviceIdsAdd.size() > 0) {
				for (int j = 0; j < deviceIdsAdd.size(); j++) {
					DefinedAlarmRuleDevice definedAlarmRuleDevice = new DefinedAlarmRuleDevice();
					definedAlarmRuleDevice.setId(UtilTool.getUuId());
					definedAlarmRuleDevice.setDeviceId(deviceIdsAdd.get(j));
					definedAlarmRuleDevice.setAlarmRuleId(jsonObject.getString("id"));
					definedAlarmRuleDeviceCudRepository.addBindDevices(definedAlarmRuleDevice);
				}
			}
			logger.info(String.format("[alarmRuleUpdate()->即将更新告警规则,参数:%s]", alarmRule));
			
			int updateModel = ruleTemplate.updateModel(alarmRule);
			if (updateModel > 0) {
				logger.info("[alarmRuleUpdate()->success: " + MessageConstant.UPDATE_SUCCESS + "参数" + alarmRule.getId() + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, alarmRule.getId());
			} else {
				logger.error("[alarmRuleUpdate()->error: " + MessageConstant.UPDATE_FAIL + "参数" + alarmRule.getId() + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("alarmRuleUpdate()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> alarmRuleDel(String id, String userId) {
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[alarmRuleDel()->error:规则id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("规则id不能为空");
			}
			if (StringUtils.isBlank(userId)) {
				logger.error("[alarmRuleDel()->error:用户id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空");
			}
			logger.info(String.format("[alarmRuleDel()->进入方法,参数:id:%s,userId:%s]", id,userId));
			AlarmRule alarmObj = new AlarmRule();
			alarmObj.setId(id);
			alarmObj.setUserId(userId);
			alarmObj.setIsDelete(RuleContants.INVALID);
			alarmObj.setPublishState(RuleContants.INVALID);
			logger.info(String.format("[alarmRuleDel()->即将删除告警规则,参数:%s]", alarmObj));
			int updateModel = ruleTemplate.updateModel(alarmObj);
			if (updateModel > 0) {
				logger.info("[alarmRuleDel()->succes: " + MessageConstant.DEL_SUCCESS + "参数:告警规则id" + id + "userId" + userId  + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.DEL_SUCCESS, true);
			} else {
				logger.error("[alarmRuleDel()->error: " + MessageConstant.DEL_FAIL + "参数:告警规则id" + id + "userId" + userId + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.DEL_FAIL);
			}
		} catch (Exception e) {
			logger.error("alarmRuleDel()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> alarmRuleState(JSONObject alarmParm) {
		try {
			if (alarmParm == null) {
				logger.error("[alarmRuleState()->error:参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空");
			}
			if (StringUtils.isBlank(alarmParm.getString(RuleContants.RULE_ID))) {
				logger.error("[alarmRuleState()->error:规则id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("规则id不能为空");
			}
			if (StringUtils.isBlank(alarmParm.getString(RuleContants.USER_ID))) {
				logger.error("[alarmRuleState()->error:用户id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空");
			}
			if (StringUtils.isBlank(alarmParm.getString(RuleContants.MANAGE_STATE))) {
				logger.error("[alarmRuleState()->error:管理状态不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("管理状态不能为空");
			}
			AlarmRule alarmRule = new AlarmRule();
			alarmRule.setManageState(alarmParm.getString(RuleContants.MANAGE_STATE));
			alarmRule.setId(alarmParm.getString(RuleContants.RULE_ID));
			alarmRule.setUserId(alarmParm.getString(RuleContants.USER_ID));
			logger.info(String.format("[alarmRuleState()->info:manageState:%s,规则id:%s,userId:%s]", alarmParm.getString(RuleContants.MANAGE_STATE),alarmParm.getString(RuleContants.RULE_ID),alarmParm.getString(RuleContants.USER_ID)));
			int updateModel = ruleTemplate.updateModel(alarmRule);
			if (updateModel > 0) {
				logger.info("[alarmRuleState()->succes: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, true);
			} else {
				logger.error("[alarmRuleState()->error: " + MessageConstant.UPDATE_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("alarmRuleState()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> alarmRulePublish(JSONObject alarmObj) {
		try {
			if (alarmObj == null) {
				logger.error("[alarmRuleState()->error:参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空");
			}
			if (StringUtils.isBlank(alarmObj.getString(RuleContants.USER_ID))) {
				logger.error("[alarmRuleState()->error:用户id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空");
			}
			if (StringUtils.isBlank(alarmObj.getString(RuleContants.RULE_ID))) {
				logger.error("[alarmRulePublish()->error:规则id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("规则id不能为空");
			}
			if (StringUtils.isBlank(alarmObj.getString(RuleContants.PUBLISH_STATE))) {
				logger.error("[alarmRulePublish()->error:发布状态不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("发布状态不能为空");
			}
			AlarmRule alarmRule = new AlarmRule();
			alarmRule.setPublishState(alarmObj.getString(RuleContants.PUBLISH_STATE));
			alarmRule.setId(alarmObj.getString(RuleContants.RULE_ID));
			alarmRule.setUserId(alarmObj.getString(RuleContants.USER_ID));
			logger.info(String.format("[alarmRulePublish()->publishState:%s,规则id:%s,userId:%s]", 
					alarmObj.getString(RuleContants.PUBLISH_STATE),alarmObj.getString(RuleContants.RULE_ID),alarmObj.getString(RuleContants.USER_ID)));
			int updateModel = ruleTemplate.updateModel(alarmRule);
			if (updateModel > 0) {
				logger.info("[alarmRulePublish()->succes: " + MessageConstant.PUBLISH_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.PUBLISH_SUCCESS, true);
			} else {
				logger.error("[alarmRulePublish()->error: " + MessageConstant.PUBLISH_FAIL + "]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("alarmRulePublish()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.AlarmRuleService#basicAlarmRuleAdd(java.util.List)
	 */
	@Override
	public RpcResponse<String> basicAlarmRuleAdd(List<Map<String, Object>> list) {
		try {
			if (list == null || list.size() == 0) {
				logger.error("[basicAlarmRuleAdd()->error:参数为空或暂无基础通用告警规则!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数为空或暂无基础通用告警规则!");
			}
			logger.info(String.format("[basicAlarmRuleAdd()-->进入方法%s]", list));
			int insertCount = ruleTemplate.basicAlarmRuleAdd(list);
			if (insertCount > 0) {
				// 设置为已同步过
				BaseDataSynchronousState updateInfo = new BaseDataSynchronousState();
				updateInfo.setAccessSecret(list.get(0).get(PublicConstants.ACCESSSECRET).toString());
				updateInfo.setBaseAlarmRule(false);
				baseDataSynchronousStateCrudRepository.updatePart(updateInfo);
				logger.info("[basicAlarmRuleAdd()->succes:基础通用告警规则同步成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("基础通用告警规则同步成功!", String.valueOf(insertCount));
			} else {
				logger.error("[basicAlarmRuleAdd()->error:基础通用告警规则同步失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("基础通用告警规则同步失败!");
			}
		} catch (Exception e) {
			logger.error("basicAlarmRuleAdd()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
