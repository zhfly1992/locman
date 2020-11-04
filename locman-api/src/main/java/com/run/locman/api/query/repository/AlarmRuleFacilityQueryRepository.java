package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

/**
 * @Description:自定义告警规则配置查询类
 * @author: dingrunkang
 * @version: 3.0, 2017年12月4日
 */

public interface AlarmRuleFacilityQueryRepository {
	/**
	 * 
	 * @Description:未绑定状态下 通过告警设施序列号,设备编号,设备类型,设施类型 ,区域,地址 查询告警相应设施信息
	 * @param AlarmRuleDeviceConstants
	 * @return
	 */
	// 此接口没有被调用过	2018年3月13日16:06:41
/*	public List<Map<String, Object>> queryAcilitiesBindingStatus(Map<String, Object> parseObject);
*/


	/**
	 * 
	 * @Description:添加告警通用规则获取设施设备列表
	 * @param map
	 * @return List<Map<String, Object>>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	List<Map<String, Object>> getFacilityDeviceList(Map map) throws Exception;

}
