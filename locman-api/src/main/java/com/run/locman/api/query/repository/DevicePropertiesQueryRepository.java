package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.DeviceProperties;

/**
 * 
 * @Description:设备属性查询
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface DevicePropertiesQueryRepository extends BaseQueryRepository<DeviceProperties, String> {

	List<DeviceProperties> findByTemplateId(String templateId);



	/**
	 * 
	 * @Description:根据设备类型ID查询设备数据点
	 * 
	 * @param accessSecret
	 * @param deviceTypeId
	 * @return
	 */
	List<DeviceProperties> findByDeviceTypeId(@Param("accessSecret") String accessSecret,
			@Param("deviceTypeId") String deviceTypeId);



	int existOrder(Map<String, Object> params);



	/**
	 * 
	 * @Description:校验该设备属性模板是否存在属性名或标识名(不能同时校验两个参数)
	 * @param
	 * @return
	 */
	List<JSONObject> checkNameOrSigExist(DeviceProperties deviceProperties);
}