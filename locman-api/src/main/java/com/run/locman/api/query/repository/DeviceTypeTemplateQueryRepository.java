package com.run.locman.api.query.repository;

import com.run.locman.api.entity.DeviceTypeTemplate;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年09月14日 queryDeviceTypePropertyConfigList
 */
public interface DeviceTypeTemplateQueryRepository extends BaseQueryRepository<DeviceTypeTemplate, String> {

	/**
	 * @return
	 * @Description: 模糊查询设备类型属性配置列表接口（分页，设备类型名称）
	 */
	List<Map<String, String>> queryDeviceTypePropertyConfigList(String accessSecret);



	/**
	 * @param templateId
	 *            模板id
	 * @return
	 * @Description: 根据设备属性模板id查询设备类型的数量接口
	 */
	List<String> queryDeviceTypeListForTempId(String templateId);



	/**
	 * @param deviceTypeId
	 *            设备类型id
	 * @return
	 * @Description: 根据设备类型id查询设备属性模板id
	 */
	String queryTempIdForDeviceTypeId(String deviceTypeId);



	/**
	 * 
	 * @Description:查询该设备类型下是否配置有设备属性-用于地图远程命令是否置灰
	 * @param {"deviceTypeId":"","accessSecret":""}
	 * @return List<Map<String, String>>
	 */
	List<Map<String, String>> queryDeviceProperties(Map<String, Object> map);
}
