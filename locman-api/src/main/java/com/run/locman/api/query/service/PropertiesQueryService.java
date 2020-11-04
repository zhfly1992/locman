package com.run.locman.api.query.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DeviceProperties;

/**
 * 
 * @Description:设备属性模板query
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface PropertiesQueryService {

	/**
	 * 
	 * @Description:查询设备属性模板
	 * @param templateId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	RpcResponse<PageInfo<DeviceProperties>> list(String templateId, int pageNum, int pageSize);



	/**
	 * 
	 * 
	 * @Description:根据设备类型ID查询设备数据点
	 * @param accessSecret
	 * @param deviceTypeId
	 * @return
	 */
	RpcResponse<List<DeviceProperties>> findByDeviceTypeId(String accessSecret, String deviceTypeId);



	/**
	 * 
	 * @Description:判断是否存在
	 * @param templateId
	 * @param id
	 * @param order
	 * @return
	 */
	RpcResponse<Boolean> existOrder(String templateId, String id, Integer order);



	/**
	 * 
	 * @Description: 根据设备属性唯一id查询设备属性点
	 * @param id
	 *            唯一编号
	 * @return 设备属性点对象
	 */
	RpcResponse<DeviceProperties> findById(String id);



	/**
	 * 
	 * @Description:校验该设备属性模板是否存在属性名和标识名(不能同时校验两个参数)
	 * @param
	 * @return
	 */
	RpcResponse<Boolean> checkNameOrSigExist(DeviceProperties deviceProperties);

}
