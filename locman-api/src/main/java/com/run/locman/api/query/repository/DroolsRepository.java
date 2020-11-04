package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.run.locman.api.entity.DevicePropertiesTemplate;

/**
 * 
 * @Description:Drools
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface DroolsRepository extends BaseQueryRepository<DevicePropertiesTemplate, String> {
	/**
	 * 
	 * @Description:分页查询规则列表
	 * @param searchaParam
	 * @return
	 */
	List<Map<String, Object>> getAllDroolsByPage(Map<String, Object> searchaParam);



	/**
	 * 
	 * @Description:根據id查詢規則信息
	 * @param searchaParam
	 * @return
	 */
	Map<String, Object> getDroolInfoById(String id);



	/**
	 * 
	 * @Description:查询所有规则
	 * @return
	 */
	List<Map<String, String>> getAllDrools();

}
