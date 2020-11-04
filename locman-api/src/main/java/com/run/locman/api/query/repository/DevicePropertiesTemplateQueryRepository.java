package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.run.locman.api.entity.DevicePropertiesTemplate;

/**
 * 
 * @Description:设备属性模板查询
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface DevicePropertiesTemplateQueryRepository extends BaseQueryRepository<DevicePropertiesTemplate, String> {

	List<DevicePropertiesTemplate> findByAccessSecret(Map<String, Object> queryParams);



	List<Map<String, String>> templateListForDeviceType(Map<String, String> queryParams);



	int checkTemplateName(Map<String, String> queryParams);
}