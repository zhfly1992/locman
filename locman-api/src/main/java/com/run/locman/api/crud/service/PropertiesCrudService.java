package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DeviceProperties;

/**
 * 
 * @Description:属性crud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface PropertiesCrudService {

	/**
	 * 添加设备属性
	 * 
	 * @Description:
	 * @param deviceProperties
	 * @return
	 */
	RpcResponse<String> add(DeviceProperties deviceProperties);



	/**
	 * 修改设备属性
	 * 
	 * @Description:
	 * @param deviceProperties
	 * @return
	 */
	RpcResponse<String> update(DeviceProperties deviceProperties);



	/**
	 * 删除设备属性
	 * 
	 * @Description:
	 * @param id
	 * @return
	 */
	RpcResponse<String> delete(String id);



	/**
	 * 更新排序
	 * 
	 * @Description:
	 * @param templateId
	 * @param propertiesId
	 * @param order
	 * @return
	 */
	RpcResponse<Boolean> updateOrder(String templateId, String propertiesId, Integer order);

}
