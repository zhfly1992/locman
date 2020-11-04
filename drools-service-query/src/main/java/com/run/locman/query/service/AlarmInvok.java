package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.run.locman.api.drools.service.AlarmRuleInvokInterface;
import com.secbro.drools.config.ReloadDroolsRules;
import com.secbro.drools.utils.KieUtils;

@Service
public class AlarmInvok implements AlarmRuleInvokInterface {
	private static Logger		logger	= Logger.getLogger(AlarmInvok.class);
	@Autowired
	private ReloadDroolsRules	rules;



	@Override

	public void invokAlarm(Map<String, Object> obj, List<String> rule) {
		logger.info(String.format("[invokAlarm()方法执行开始...,参数：【%s】【%s】]", obj, rule));
		try {
			// 加载规则到引擎中
			for (String ruleStr : rule) {
				rules.reload(ruleStr);
				KieSession kieSession = KieUtils.getKieContainer().newKieSession();
				kieSession.insert(obj);
				kieSession.insert(ruleStr);
				int ruleFiredCount = kieSession.fireAllRules();
				logger.debug("触发了" + ruleFiredCount + "条告警!");
				kieSession.dispose();
			}
			logger.info(String.format("[invokAlarm()方法执行结束!]"));
		} catch (Exception e) {
			logger.error("invokAlarm()->exception", e);
		}
	}



	@Override
	public void invokAlarm(List<Map<String, Object>> obj, List<String> rule) throws Exception {
		logger.info(String.format("[invokAlarm()方法执行开始...,参数：【%s】【%s】]", obj, rule));
		if (obj != null && obj.size() > 0 && rule != null && rule.size() > 0) {
			// 加载规则到引擎中
			for (int i = 0; i < rule.size(); i++) {
				rules.reload(rule.get(i));
				KieSession kieSession = KieUtils.getKieContainer().newKieSession();
				kieSession.insert(rule.get(i));
				kieSession.insert(obj.get(i));
				int ruleFiredCount = kieSession.fireAllRules();
				logger.debug("触发了" + ruleFiredCount + "条告警!");
				kieSession.dispose();
			}
		}
		logger.info(String.format("[invokAlarm()方法执行结束!]"));
	}



	@Override
	public Boolean invokAlarmCheck(Map<String, Object> obj, String rule) throws Exception {
		logger.info(String.format("[invokAlarmCheck()方法执行开始...,参数：【%s】【%s】]", obj, rule));
		rules.reload(rule);
		KieSession kieSession = KieUtils.getKieContainer().newKieSession();
		kieSession.insert(rule);
		kieSession.insert(obj);
		int ruleFiredCount = kieSession.fireAllRules();
		kieSession.dispose();
		if (ruleFiredCount > 0) {
			logger.info(String.format("[invokAlarmCheck()方法执行结束!返回信息：【%s】]", true));
			return true;
		}
		logger.info(String.format("[invokAlarmCheck()方法执行结束!返回信息：【%s】]", false));
		return false;
	}
}
