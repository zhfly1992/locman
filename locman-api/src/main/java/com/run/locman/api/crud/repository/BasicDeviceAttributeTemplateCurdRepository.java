/*
 * File name: BasicDeviceAttributeTemplateCurdRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年4月23日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;

import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.DevicePropertiesTemplate;
import com.run.locman.api.entity.DeviceTypeTemplate;

/**
 * @Description: 设备属性模板同步
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月23日
 */

public interface BasicDeviceAttributeTemplateCurdRepository {

	/**
	 * 
	 * @Description:批量插入设备属性模板数据
	 * @param templateLists
	 * @return
	 */
	int insertDevicePropertiesTemplate(List<DevicePropertiesTemplate> templateLists);



	/**
	 * 
	 * @Description:批量插入设备属性数据
	 * @param deviceProperties
	 * @return
	 */
	int insertDeviceProperties(List<DeviceProperties> deviceProperties);



	/**
	 * 
	 * @Description:插入设备类型与设备属性模板关系表
	 * @param deviceTypeTemplate
	 * @return
	 */
	int insetDeviceTypeTemplate(DeviceTypeTemplate deviceTypeTemplate);

}
