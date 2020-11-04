/*
 * File name: SimpleOrderQueryRepository.java
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

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.SimpleOrderProcess;

/**
 * @Description:
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public interface SimpleOrderQueryRepository extends BaseQueryRepository<SimpleOrderProcess, String> {
	/**
	 * 
	 * @Description:获取设施设备列表信息
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	List<Map<String, Object>> getFacilityDeviceList(Map map) throws Exception;



	/**
	 * 
	 * @Description:一般流程查询一般工单列表
	 * @param map
	 * @return List<Map<String, Object>>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	List<Map<String, Object>> getSimpleOrderList(Map map) throws Exception;



	/**
	 * 
	 * @Description:我的代办一般工单列表
	 * @param map
	 * @return List<Map<String, Object>>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	List<Map<String, Object>> getSimpleOrderAgencyList(Map map) throws Exception;



	/**
	 * 
	 * @Description:查询给定时间点该设备是否存在处理中的一般流程工单
	 * @param map
	 * @return List<Map<String, Object>>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	List<Map<String, Object>> whetherExistOrder(Map map) throws Exception;



	/**
	 * @Description:查询一般工单待办数
	 * @param map
	 * @return
	 * @throws Exception
	 */
	Integer getSimpleCount(List<String> processId) throws Exception;



	/**
	 * 
	 * @Description:根据设备id查询预约时间期间是否存在处理中的工单-用于地图设备远程命令是否能操作的判断之一
	 * @param Map<String,
	 *            Object>
	 * @return List<Map<String, Object>>
	 * @throws Exception
	 */
	List<Map<String, Object>> getOrderByDeviceId(Map<String, Object> map) throws Exception;



	/**
	 * 
	 * @Description:根据设施id用户id接入方查询一般工单代办流程-APP调用
	 * @param paramInfo
	 *            {"facilityId":"","userId":"","accessSecret":""}
	 * @return RpcResponse<Map<String, Object>>
	 * @throws Exception
	 */
	List<Map<String, Object>> getSimpleOrderAgencyListForApp(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description:查询工单流水号
	 * @return
	 * @throws Exception
	 */
	String findOrderNumber(String accessSecret) throws Exception;

	/**
	 * 
	* @Description:根据设备id查询一般工单开始时间，用于前后三分钟屏蔽告警
	* @param accessSecret
	* @param deviceId
	* @return String
	 */
	String querySimpleOrderStartTime(@Param("accessSecret")String accessSecret, @Param("deviceId")String deviceId);
}
