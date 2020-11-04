package com.run.locman.api.crud.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description:告警规则接口
 * @author: lkc
 * @version: 1.0, 2017年10月27日
 */

public interface AlarmRuleService {
	/**
	 * 
	 * @Description:告警规则添加
	 * @param jsonObject
	 * @return
	 */
	RpcResponse<String> alarmRuleAdd(JSONObject jsonObject);



	/**
	 * 
	 * @Description:告警规则更新
	 * @param jsonObject
	 * @return
	 */
	RpcResponse<String> alarmRuleUpdate(JSONObject jsonObject);



	/**
	 * 
	 * @Description:告警规则删除
	 * @param id
	 * @return
	 */
	RpcResponse<Boolean> alarmRuleDel(String id, String userId);



	/**
	 * 
	 * @Description:停用或者启用告警规则
	 * @param alarmObj
	 * @return
	 */
	RpcResponse<Boolean> alarmRuleState(JSONObject alarmParm);



	/**
	 * 
	 * @Description:发布告警规则
	 * @param alarmObj
	 * @return
	 */
	RpcResponse<Boolean> alarmRulePublish(JSONObject alarmObj);



	/**
	 * 
	 * @Description:保存基础通用告警规则(基础通用告警规则同步)
	 * @param list
	 * @return
	 */
	RpcResponse<String> basicAlarmRuleAdd(List<Map<String, Object>> list);

}