/*
 * File name: BasicDeviceAttributeTemplateQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年4月25日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.List;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.DevicePropertiesTemplate;

/**
 * @Description: 设备属性模板同步查询
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月25日
 */

public interface BasicDeviceAttributeTemplateQueryService {

	/**
	 * 
	 * @Description:判断设备属性模板表，设备属性表，设备类型与模板关系是否存在数据
	 * @return
	 */
	RpcResponse<Boolean> existBasicData(String accessSecret);



	/**
	 * 
	 * @Description: 获取设备属性模板表(同步表)
	 * @return
	 */
	RpcResponse<List<DevicePropertiesTemplate>> queryDevicePropertiesTemplateBasic();



	/**
	 * 
	 * @Description: 通过设备模板id查询设备属性(同步表)
	 * @param templateId
	 * @return
	 */
	RpcResponse<List<DeviceProperties>> queryDevicePropertiesBasicByTemplateId(String templateId);



	/**
	 * 
	 * @Description:通过设备属性模板id查询关系表获取设备类型id
	 * @param templateId
	 * @return
	 */
	RpcResponse<String> findBaseDeviceTypeId(String templateId);

}
