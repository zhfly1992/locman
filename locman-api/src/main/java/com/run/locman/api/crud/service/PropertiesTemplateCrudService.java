package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DevicePropertiesTemplate;

/**
 * 
 * @Description:属性模板crud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface PropertiesTemplateCrudService {

	/**
	 * 添加设备属性模版
	 * 
	 * @Description:
	 * @param template
	 * @return
	 */
	RpcResponse<String> add(DevicePropertiesTemplate template);



	/**
	 * 启停用设备属性模版
	 * 
	 * @Description:
	 * @param template
	 * @return
	 */
	RpcResponse<String> updateState(DevicePropertiesTemplate template);



	/**
	 * 修改设备模版最后修改时间
	 * 
	 * @Description:
	 * @param templateId
	 * @return
	 */
	RpcResponse<String> updateTime(String templateId);

}
