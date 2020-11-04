/*
 * File name: BasicDeviceAttributeTemplateQueryRepository.java
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

package com.run.locman.api.query.repository;

import java.util.List;

import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.DevicePropertiesTemplate;

/**
 * @Description: 设备属性模板查询
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月25日
 */

public interface BasicDeviceAttributeTemplateQueryRepository {

	/**
	 * 
	 * @Description:校验设备属性表是否有数据
	 * @return
	 */
	String existDeviceProperties();



	/**
	 * 
	 * @Description:校验设备属性模板表是否有数据
	 * @return
	 */
	String existDevicePropertiesTemplate(String accessSecret);



	/**
	 * 
	 * @Description:校验设备属性模板与设备类型关系表示否有数据
	 * @return
	 */
	String existDeviceTypeTemplate(String accessSecret);



	/**
	 * 
	 * @Description:通过属性模板id查询属性
	 * @param templateId
	 * @return
	 */
	List<DeviceProperties> queryDevicePropertiesBasicByTemplateId(String templateId);



	/**
	 * 
	 * @Description:获取所有的设备属性模板
	 * @return
	 */
	List<DevicePropertiesTemplate> queryDevicePropertiesTemplateBasic();



	/**
	 * 
	 * @Description:通过同步表中的设备模板id查询设备类型id
	 * @param id
	 * @return
	 */
	String findBaseDeviceTypeId(String templateId);

}
