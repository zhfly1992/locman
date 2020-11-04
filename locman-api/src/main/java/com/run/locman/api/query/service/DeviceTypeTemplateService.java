package com.run.locman.api.query.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年09月14日
 */
public interface DeviceTypeTemplateService {

	/**
	 * * @Description: 查询设备类型属性配置与模板中间表信息接口
	 *
	 * @return
	 */
	RpcResponse<List<Map<String, String>>> queryDeviceTypePropertyConfigList(String accessSecret);



	/**
	 * @param templateId
	 *            设备属性模板id
	 * @return
	 * @Description: 根据设备属性模板id查询设备类型的数量接口
	 */
	RpcResponse<List<String>> queryDeviceTypeListForTempId(String templateId);



	/**
	 * @param deviceTypeId
	 *            设备类型id
	 * @return
	 * @Description: 根据设备类型id查询设备属性模板id
	 */
	RpcResponse<String> queryTempIdForDeviceTypeId(String deviceTypeId);



	/**
	 * 
	 * @Description:查询该设备类型下是否配置有设备属性-用于地图远程命令是否置灰
	 * @param {"deviceTypeId":"","accessSecret":""}
	 * @return RpcResponse<Boolean>
	 */
	RpcResponse<Boolean> queryDeviceProperties(JSONObject findParam);

}
