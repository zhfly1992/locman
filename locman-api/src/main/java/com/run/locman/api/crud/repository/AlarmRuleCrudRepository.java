package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;
import com.run.locman.api.entity.AlarmRule;

/**
 * @Description:规则引擎数据库管理类
 * @author: lkc
 * @version: 1.0, 2017年09月15日
 */
public interface AlarmRuleCrudRepository extends BaseCrudRepository<AlarmRule, String> {
	/**
	 * 
	 * @Description:查询数据库中某字段的最大值
	 * @param map
	 * @return
	 */
	long getMaxValueBySecrete(Map<String, String> map);



	/**
	 * 
	 * @Description:更新告警规则
	 * @param alarmRule
	 * @return
	 */
	int updateAlarmRuleState(AlarmRule alarmRule);



	/**
	 * 
	 * @Description:保存基础通用告警规则(基础通用告警规则同步)
	 * @param list
	 * @return
	 */
	int basicAlarmRuleAdd(List<Map<String, Object>> list);
}
