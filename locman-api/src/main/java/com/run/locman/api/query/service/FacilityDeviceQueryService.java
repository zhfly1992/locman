package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;

/**
 * 
 * @Description: 设施与设备的关系接口(查询)
 * @author: guolei
 * @version: 1.0, 2017年9月15日
 */

public interface FacilityDeviceQueryService {
	/**
	 * 
	 * @Description:查询设施的绑定状态
	 * @param facilityId
	 * @return
	 */
	RpcResponse<List<String>> queryFacilityBindingState(String facilityId);

	/**
	 * 
	 * @Description:查询设备的绑定状态
	 * @param deviceId
	 * @return
	 */
	RpcResponse<String> queryDeviceBindingState(String deviceId);

	/**
	 * 
	 * @Description:查询设施的绑定状态
	 * @param facilityId
	 * @return
	 */
	RpcResponse<Map<String, Object>> queryFacilityById(String facilityId);

	/**
	 * 
	 * @Description:查询设施绑定列表（分页）
	 * @param params
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, Object>>> queryFacilityBindListByPage(String accessSecret, int pageNo,
			int pageSize, Map<String, Object> params);

	/**
	 * 
	 * @Description:查询所有已绑定设备的id
	 * @return
	 */
	RpcResponse<List<String>> queryAllBoundDeviceId();

	/**
	 * 
	 * @Description:查询设施下所有绑定设备的实时状态信息
	 * @param facId
	 * @return
	 */
	RpcResponse<List<String>> queryAllDeviceByFacId(String facId, String secreat);

	/**
	 * 
	 * @Description:通过设施id集合查询这些设施下所有的设备
	 * @param facIds
	 *            设施id集合
	 * @return
	 */
	RpcResponse<List<String>> findDeviceByFacIds(List<String> facIds);
	/**
	 * 
	* @Description:通过设备id查询MongoDB社保上报属性
	* @param deviceId
	* @return
	 */
	RpcResponse<Map<String ,Object>> findMongDbDeviceState(String deviceId,String accessSecret);
	
	RpcResponse<List<Map<String,Object>>> onlineQuery(String reportType);

}
