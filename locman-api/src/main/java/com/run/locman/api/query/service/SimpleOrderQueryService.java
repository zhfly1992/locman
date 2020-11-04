/*
 * File name: SimpleOrderQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月6日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.model.FacilitiesModel;

/**
 * @Description: 一般工单流程查询接口
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public interface SimpleOrderQueryService {
	/**
	 * 
	 * @Description:获取设施设施列表
	 * @param paramInfo
	 * @return RpcResponse<List<Map>>
	 * @throws Exception
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getFacilityDeviceList(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description:根据id查询一般工单信息
	 * @param id
	 * @return RpcResponse<SimpleOrderProcess>
	 * @throws Exception
	 */
	RpcResponse<Map<String, Object>> findById(String id) throws Exception;



	/**
	 * 
	 * @Description: 一般流程查询一般工单列表
	 * @param paramInfo
	 * @return RpcResponse<PageInfo<Map<String, Object>>>
	 * @throws Exception
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getSimpleOrderList(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description:我的代办一般工单列表
	 * @param paramInfo
	 * @return RpcResponse<PageInfo<Map<String, Object>>>
	 * @throws Exception
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getSimpleOrderAgencyList(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description:查询给定时间点该设备是否存在处理中的一般流程工单
	 * @param paramInfo
	 * @return RpcResponse<Boolean>
	 * @throws Exception
	 */
	RpcResponse<Boolean> whetherExistOrder(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description:获取一般工单流程节点详情
	 * @param paramInfo
	 * @return RpcResponse<List<Map<String, Object>>>
	 * @throws Exception
	 */
	RpcResponse<Map<String, Object>> getOrderNodeDetails(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description:查询设施列表
	 * @param facilities
	 * @return
	 * @throws Exception
	 */
	RpcResponse<PageInfo<Facilities>> findFacInfo(FacilitiesModel facilitiesModel) throws Exception;



	/**
	 * 
	 * @Description:根据设备id查询预约时间期间是否存在处理中的工单-用于地图设备远程命令是否能操作的判断之一
	 * @param paramInfo
	 * @return RpcResponse<Boolean>
	 * @throws Exception
	 */
	RpcResponse<Boolean> getOrderByDeviceId(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description:根据设施id用户id接入方查询一般工单代办流程-APP调用
	 * @param paramInfo
	 *            {"facilityId":"","userId":"","accessSecret":""}
	 * @return RpcResponse<Map<String, Object>>
	 * @throws Exception
	 */
	RpcResponse<List<Map<String, Object>>> getSimpleOrderAgencyListForApp(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description:查询工单流水号
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> findOrderNumber(String accessSecret);



	/**
	 * 
	 * @Description:根据设备id查询一般工单开始时间，用于前后三分钟屏蔽告警
	 * @param accessSecret
	 * @return RpcResponse<String>
	 */
	RpcResponse<String> querySimpleOrderStartTime(String accessSecret, String deviceId);

}
