/*
 * File name: DeviceQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年3月2日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.Device;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年3月2日
 */

public interface DeviceQueryRepository extends BaseQueryRepository<Device, String> {

	/**
	 * @Description: 组合条件查询设备信息
	 * @param queryParams
	 *            组合条件(设备类型,绑定状态,设备编号)
	 * @return
	 */
	List<Map<String, Object>> queryDeviceInfoForCondition(Map<String, Object> queryParams);



	/**
	 * @Description: 根据设备id查询设备状态信息
	 * @param deviceId
	 *            设备id
	 * @return 设备信息数据
	 */
	Map<String, Object> queryDeviceBindingState(String deviceId);



	/**
	 * @Description: 查询批量设备信息
	 * @param list
	 *            设备编号集合
	 * @return
	 */
	List<Map<String, Object>> queryBatchDeviceInfoForDeviceIds(List<String> list);



	/**
	 * 
	 * @Description:校验该设备是否为平衡告警设备-告警校验
	 * @param deviceId
	 * @return RpcResponse<Device>
	 */
	Device queryDeviceByDeviceId(@Param("deviceId") String deviceId);



	/**
	 * 
	 * @Description: 根据id查询单个设备信息
	 * @param deviceId
	 *            设备id
	 * @return
	 */
	Device queryDeviceInfoById(@Param("deviceId") String deviceId);



	/**
	 * 
	 * @Description:查询所有设备id
	 * @param deviceId
	 * @return RpcResponse<Device>
	 */
	List<String> queryDeviceIds();



	/**
	 * 
	 * @Description:查询发起故障工单所需部分数据
	 * @param
	 * @return
	 */

	List<Map<String, Object>> getInfoForFaultOrderById(List<String> deviceIds);



	/**
	 * 
	 * @Description:查询设备列表所有接入方密钥
	 * @param
	 * @return
	 */

	List<String> getAllAccessSecret();



	/**
	 * @Description: 组合条件查询设备总数
	 * @param queryParams
	 *            组合条件(设备类型,绑定状态,设备编号)
	 * @return
	 */
	int getDeviceListNum(Map<String, Object> queryParams);



	/**
	 * @Description:根据设备id查询设备网关id,appId,appKey,用于命令下发
	 * @param deivceId
	 * @return
	 */
	Map<String, String> getDeviceInfoForControl(String deviceId);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	Device queryDeviceByhardwareIdAndAS(@Param("hardwareId") String hardwareId,
			@Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @Description:统计中心统计设备实时电压、信号、锁状态等
	 * @param jsonParam
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getCountDeviceRealState(JSONObject jsonParam);



	/**
	 * 
	 * @Description:通过设备id查询是否未完成的故障工单
	 * @param deviceId
	 * @return
	 */
	String findFaultOrderByDeviceId(@Param("deviceId") String deviceId);



	/**
	  * 
	  * @Description:通过网关id和子设备id查询对应的locman的设备id
	  * @param 
	  * @return
	  */
	
	List<String> findDeviceId(@Param("gatewayId")String gatewayId, @Param("subDeviceId")String subDeviceId);
	/**
	 * 
	* @Description:信息概览，告警规则跳转
	* @param queryParams
	* @return
	 */
	List<Map<String, Object>> getQueryDeviceByRule(Map<String, Object> queryParams);
	/**
	 * 
	* @Description:设备timing和trigger次数统计
	* @param queryParams
	* @return
	 */
	
	List<Map<String, Object>> getCountDeviceTimingTrigger(Map<String ,Object> map);
	
	int getAllCountDeviceTimingTrigger(Map<String ,Object> map);
	/**
	 * 
	* @Description:信息概览，数量查询
	* @param queryParams
	* @return
	 */
//	int getDeviceListNumByRule(Map<String, Object> queryParams);

}
