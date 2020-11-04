package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年09月15日
 */
public interface DeviceTypeTemplateCudService {

	/**
	 * Description:解绑设备属性模板与设备类型关系
	 * 
	 * @param deviceTypePropertyConfigId
	 *            设备类型属性配置Id
	 * @return
	 */
	RpcResponse<String> unbindDeviceTypePropertyAndTemplate(String deviceTypePropertyConfigId,
			String devicePropertyTemplateId, String accessSecret);



	/**
	 * Description:绑定设备属性模板与设备类型关系
	 * 
	 * @param deviceTypePropertyConfigId
	 *            设备类型属性配置Id
	 * @param devicePropertyTemplateId
	 *            设备属性模板Id
	 * @return
	 */
	RpcResponse<String> bindDeviceTypePropertyAndTemplate(String deviceTypePropertyConfigId,
			String devicePropertyTemplateId, String accessSecret);
}
