package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.CountFaultOrderDto;
import com.run.locman.api.dto.FaultOrderHistogramDto;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
public interface FaultOrderProcessQueryService {

	/**
	 * @Description; 根据故障工单id查询详情
	 * 
	 * @param id
	 *            故障工单id
	 * @return
	 */
	RpcResponse<Map<String, Object>> queryFaultOrderInfoById(String id);



	/**
	 * @Description: 查询设施设备列表
	 * @param paramInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<PageInfo<Map<String, Object>>> queryDevicesForFaultOrder(JSONObject paramInfo) throws Exception;



	/**
	 * @Description: 获取故障工单列表
	 * @param paramInfo
	 *            查询条件
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getFaultOrderList(JSONObject paramInfo);



	/**
	 * @Description: 获取待办流程/流程列表故障工单列表
	 * @param paramInfo
	 *            查询条件
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, Object>>> queryAgendaOrProcessFaultOrderList(JSONObject paramInfo);



	/**
	 * 
	 * @Description:查询工单流水号
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> findFaultOrderNumber(String accessSecret) throws Exception;



	/**
	 * 
	 * @param token
	 * @param pageSize
	 * @param pageNum
	 * @Description:统计告警工单
	 * @return
	 * @throws Exception
	 */
	/*
	 * RpcResponse<PageInfo<CountFaultOrderDto>> countFaultOrderInfoByAS(String
	 * accessSecret, String token, Integer pageNum, Integer pageSize);
	 */

	/**
	 * 
	 * @Description:统计告警工单
	 * @param
	 * @return
	 */

	RpcResponse<PageInfo<CountFaultOrderDto>> countFaultOrderInfoByAccessSecret(JSONObject json);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	RpcResponse<List<FaultOrderHistogramDto>> faultOrder2Histogram(JSONObject json);



	/**
	 * 
	 * @Description:通过设备id查询故障工单0 6 2 状态的not in
	 * @param deviceId
	 * @return
	 */
	RpcResponse<List<String>> findFaultOrderByDeviceId(List<String> deviceIds);



	/**
	 * 
	 * @Description:通过设备id查询故障工单 0 6 2 1 状态的not in
	 * @param deviceId
	 * @return
	 */
	RpcResponse<String> findFaultOrderByDeviceIdTo(String deviceId);



	/**
	 * 
	 * @Description:故障工单统计，按照组织和故障类型统计
	 * @param accessSecret
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public RpcResponse<Object> countFaultOrderNumByOrganizationAndType(JSONObject jsonObject);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<PageInfo<Map<String, Object>>> queryAgendaOrProcessFaultOrderListNew(JSONObject json);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<PageInfo<Map<String, Object>>> getFaultOrderListNew(JSONObject json);



	/**
	* @Description:故障列表
	* @param jsonObject
	* @return
	*/
	
	RpcResponse<PageInfo<Map<String, Object>>> faultOrderList(JSONObject jsonObject);
	/**
	 * 
	* @Description:故障状态数量
	* @param jsonObject
	* @return
	 */
	
	RpcResponse<List<Map<String,Object>>> faultOrderStateCount(JSONObject jsonObject);
}
