package com.run.locman.api.query.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DevicePropertiesTemplate;

/**
 * 
 * @Description:设备属性模板查询类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface PropertiesTemplateQueryService {

	/**
	 * 分页查询设备属性模版
	 * 
	 * @Description:
	 * @param accessSecret
	 * @param pageNum
	 * @param pageSize
	 * @param searchKey
	 * @param state
	 * @return
	 */
	RpcResponse<PageInfo<DevicePropertiesTemplate>> list(String accessSecret, int pageNum, int pageSize,
			String searchKey, String state);



	/**
	 * 为设备类型封装分页查询设备属性模版
	 * 
	 * @Description:
	 * @param accessSecret
	 * @param pageNum
	 * @param pageSize
	 * @param searchKey
	 * @param state
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, String>>> templateListForDeviceType(String accessSecret, int pageNum, int pageSize,
			String searchKey, String state);



	/**
	 * 根据id查询模版
	 * 
	 * @Description:
	 * @return
	 */
	RpcResponse<DevicePropertiesTemplate> findById(String id);



	/**
	 * 检查模版名称是否存在
	 * 
	 * @Description:
	 * @param accessSecret
	 * @param templateName
	 * @return
	 */
	RpcResponse<Boolean> checkTemplateName(String accessSecret, String templateName);

}
