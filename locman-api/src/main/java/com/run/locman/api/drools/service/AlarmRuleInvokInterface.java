package com.run.locman.api.drools.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @Description:告警规则类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface AlarmRuleInvokInterface {
	/**
	 * 
	 * @Description:告警规则匹配管理
	 * @param obj
	 *            原始数据对象
	 * @param rule
	 *            告警的规则信息
	 * @throws Exception
	 */
	public void invokAlarm(Map<String, Object> obj, List<String> rule) throws Exception;



	/**
	 * 
	 * @Description:告警规则匹配管理
	 * @param obj
	 *            原始数据对象
	 * @param rule
	 *            告警的规则信息
	 * @throws Exception
	 */
	public void invokAlarm(List<Map<String, Object>> obj, List<String> rule) throws Exception;



	/**
	 * 
	 * @Description:然会告警条数，同时触发告警
	 * @param obj
	 * @param rule
	 * @return
	 */
	Boolean invokAlarmCheck(Map<String, Object> obj, String rule) throws Exception;
}
