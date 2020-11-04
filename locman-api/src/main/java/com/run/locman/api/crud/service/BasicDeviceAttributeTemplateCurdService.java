/*
 * File name: BasicDeviceAttributeTemplateCurdService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年4月23日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;

/**
 * @Description: 同步设备属性模板表service
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月23日
 */

public interface BasicDeviceAttributeTemplateCurdService {

	/**
	 * 
	 * @Description: 保存告警工单信息
	 * @param alarmOrder
	 *            告警工单实体类
	 * @return
	 */
	RpcResponse<Boolean> basicDeviceAttrbuteTemplate(String accessSecret);

}
