package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.dto.CountFaultOrderDto;
import com.run.locman.api.dto.FaultOrderHistogramDto;
import com.run.locman.api.entity.FaultOrderProcess;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月05日
 */
public interface FaultOrderProcessQueryRepository extends BaseQueryRepository<FaultOrderProcess, String> {

	/**
	 * @Description: 根据id查询故障工单信息
	 * @param id
	 *            请求工单id
	 * @return 工单列表集合
	 */
	Map<String, Object> queryFaultOrderInfoById(String id);



	/**
	 * @Description: 获取故障工单列表
	 * @param map
	 *            请求参数
	 * @return 工单列表集合
	 */
	List<Map<String, Object>> getFaultOrderList(Map<String, String> map);



	/**
	 * @Description: 查询故障工单添加或修改时的设施设备
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> queryDevicesForFaultOrder(Map<String, Object> map);



	/**
	 * @Description: 获取待办流程/流程列表故障工单列表
	 * @param map
	 *            查询条件
	 * @return
	 */
	List<Map<String, Object>> queryAgendaOrProcessFaultOrderList(Map<String, Object> map);



	/**
	 * @Description: 获取故障工单数
	 * @param ids
	 * @return
	 */
	Integer getfaultOrderCount(List<String> ids);



	/**
	 * 
	 * @Description: 计算工单流水号
	 * @param accessSecret
	 * @return
	 * @throws Exception
	 */
	String findOrderNumber(String accessSecret) throws Exception;



	/**
	 * 
	 * @Description:统计故障工单信息
	 * @param
	 * @return
	 */

	List<CountFaultOrderDto> countFaultOrderInfoByAS(JSONObject json);



	/**
	 * 
	 * @Description:故障工单柱状图
	 * @param
	 * @return
	 */

	List<FaultOrderHistogramDto> faultOrder2Histogram(JSONObject json);



	/**
	 * 
	 * @Description:通过设备id查询故障工单
	 * @param deviceId
	 * @return
	 */
	String queryFaultOrderByDeviceId(String deviceId);



	/**
	 * 
	 * @Description:通过设备id查询已经过审的故障工单
	 * @param deviceId
	 * @return
	 */
	String queryFaultOrderByDeviceIdTo(String deviceId);



	/** 按照组织和类型统计故障工单数量 */
	public List<Map<String, Object>> countFaultOrderNumByOrganizationAndType(Map<String, Object> map);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<Map<String, Object>> queryAgendaOrProcessFaultOrderListNew(Map<String, Object> map);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<Map<String, Object>> getFaultOrderListNew(Map<String, String> map);
	/**
	 * 
	* @Description:故障列表
	* @param map
	* @return
	 */
	
	List<Map<String, Object>> getFaultOrderListInfoNew(Map<String, Object> map);
	/**
	 * 
	* @Description:状态数量
	* @param map
	* @return
	 */
	List<Map<String, Object>> faultOrderStateCount(Map<String,Object> map);
}
