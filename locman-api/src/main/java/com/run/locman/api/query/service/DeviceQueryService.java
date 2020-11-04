package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.Pages;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2018年03月05日
 */
public interface DeviceQueryService {

	/**
	 * @Description: 组合条件查询设备集合信息
	 * @param pageNum
	 *            页码
	 * @param pageSize
	 *            分页大小
	 * @param accessSecret
	 *            接入方秘钥
	 * @param deviceTypeId
	 *            设备类型id
	 * @param bingStatus
	 *            绑定状态
	 * @param deviceId
	 *            设备id
	 * @param facilityId
	 *            设施id
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> queryDeviceInfoForCondition(int pageNum, int pageSize, String accessSecret,
			String deviceTypeId, String bingStatus, String deviceId, String facilityId, String factoryId, String whole,
			String startTime, String endTime, String onLineState);



	/**
	 * @Description: 根据设备id查询设备状态信息
	 * @param deviceId
	 *            设备id
	 * @return 设备信息数据
	 */
	RpcResponse<Map<String, Object>> queryDeviceBindingState(String deviceId);



	/**
	 * @Description: 查询批量设备信息
	 * @param deviceIds
	 *            设备编号集合
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> queryBatchDeviceInfoForDeviceIds(List<String> deviceIds);



	/**
	 * @Description: 根据设备id查询设备状态信息
	 * @param deviceId
	 *            设备id
	 * @return 设备信息数据
	 */
	RpcResponse<JSONObject> queryDeviceLastState(String deviceId);



	/**
	 * 
	 * @Description:校验该设备是否为平衡告警设备-告警校验
	 * @param deviceId
	 * @return RpcResponse<Device>
	 */
	RpcResponse<Device> queryDeviceByDeviceId(String deviceId);



	/**
	 * 
	 * @Description:根据id查询单个设备信息
	 * @param deviceId
	 *            设备id
	 * @return
	 */
	public RpcResponse<Device> queryDeviceInfoById(String deviceId);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public RpcResponse<Device> queryDeviceByhardwareIdAndAS(String hardwareId, String accessSecret);



	/**
	 * 
	 * @Description: 批量查询设备状态
	 * @param deviceIds设备id集合
	 * @return
	 */

	RpcResponse<List<JSONObject>> queryDevicesLastState(List<String> deviceIds);



	/**
	 * 
	 * @Description: 设备超时未上报检测
	 * @param
	 * @return
	 */
	RpcResponse<Integer> getDeviceListNum(String accessSecret, String deviceTypeId, String bingStatus, String deviceId,
			String factoryId, String whole, String startTime, String endTime, String onLineState);



	/**
	 * @Description:根据设备id查询设备网关id,appId,appKey
	 * @param deviceId
	 * @return
	 */
	RpcResponse<Map<String, String>> getDeviceInfoForRemoteControl(String deviceId);



	/**
	 * 
	 * @Description:Mogdb-根据设备id分页查询设备历史上报数据
	 * @param deviceId
	 * @param pageNum
	 * @param pageSize
	 * @return RpcResponse<Pagination<Map<String, Object>>>
	 */
	RpcResponse<Pagination<Map<String, Object>>> getHistorySateByPage(String deviceId, String pageNum, String pageSize,
			String startTime, String endTime);



	/**
	 * 
	 * @Description:统计设备实时电压、信号、锁状态等
	 * @param jsonParam{"accessSecret":"","deviceId":"","deviceType":"",
	 *            "searchKey":"","startTime":"","endTime":"","bvMin":"","bvMax":"",
	 *            "sigMin":"","sigMax":"","lsMin":"","lsMax":"","rsrpMin":"","rsrpMax":"",
	 *            "sinsMin":"","sinsMax":"","pageNum":"","pageSize":""}
	 * @return RpcResponse<Pagination<Map<String, Object>>>
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getCountDeviceRealState(JSONObject jsonParam);



	/**
	 * 
	 * @Description:导出设备实时电压、信号、锁状态等
	 * @param jsonParam{"accessSecret":"","deviceId":"","deviceType":"",
	 *            "searchKey":"","bvMin":"","bvMax":"",
	 *            "sigMin":"","sigMax":"","lsMin":"","lsMax":"","rsrpMin":"","rsrpMax":"",
	 *            "sinsMin":"","sinsMax":""}
	 * @return RpcResponse<Pagination<Map<String, Object>>>
	 */
	RpcResponse<List<Map<String, Object>>> exportCountDeviceRealState(JSONObject jsonParam);



	/**
	 * 
	 * @Description:通过设备id查询是否未完成的故障工单
	 * @param deviceId
	 * @return
	 */
	RpcResponse<String> findFaultOrderByDeviceId(String deviceId);



	/**
	 * 
	 * @Description:通过网关id和子设备id查询locman对应的设备id
	 * @param
	 * @return
	 */
	RpcResponse<List<String>> findDeviceId(String gatewayId, String subDeviceId);



	/**
	* @Description:信息概览，告警规则跳转
	* @param pageNo
	* @param pageSize
	* @param accessSecret
	* @param alarmDesc
	* @return
	*/
	
	RpcResponse<List<Map<String, Object>>> queryDeviceByRule(int pageNo, int pageSize, String accessSecret,
			String alarmDesc);
	
	RpcResponse<Pages<Map<String, Object>>> countDeviceTimingTrigger(JSONObject json);

//	RpcResponse<Integer> getDeviceListNumByRule(String accessSecret, String alarmDesc);
}
