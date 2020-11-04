package com.run.locman.api.crud.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * 
 * @Description: 设施与设备的关系接口(更改)
 * @author: guolei
 * @version: 1.0, 2017年9月14日
 */

public interface FacilityDeviceCudService {

	/**
	 * 
	 * @Description:绑定设施与设备的关系
	 * @param json
	 * @return RpcResponse<Map<String, Object>>
	 */
	RpcResponse<Map<String, Object>> bindFacilityWithDevices(JSONObject json);



	/**
	 * 
	 * @Description:解绑设施与设备
	 * @param facilityId
	 * @param devicesId
	 *            多个以逗号分割
	 * @return
	 */
	RpcResponse<Map<String, Object>> unbindFacilityWithDevices(String facilityId, String devicesId);

}
