package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.DevicePropertiesTemplate;

/**
 * 
 * @Description:Drools
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface DroolsRepository extends BaseCrudRepository<DevicePropertiesTemplate, String> {

	/**
	 * 
	 * @Description:新增规则
	 * @param map
	 */
	void addDrools(Map<String, Object> map);



	/**
	 * 
	 * @Description:编辑规则
	 * @param map
	 */
	int updateDrools(Map<String, Object> map);
}
