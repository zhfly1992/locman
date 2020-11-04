package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.DeviceProperties;

/**
 * 
 * @Description:设备属性crud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface DevicePropertiesCrudRepository extends BaseCrudRepository<DeviceProperties, String> {

	int updateOrder(Map<String, Object> params);



	List<JSONObject> checkNameAndSigExist(DeviceProperties deviceProperties);

}