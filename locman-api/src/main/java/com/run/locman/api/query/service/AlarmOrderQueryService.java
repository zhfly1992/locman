package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;



import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.CountAlarmOrderDto;
import com.run.locman.api.entity.AlarmCountDetails;
import com.run.locman.api.entity.AlarmOrderCount;
import com.run.locman.api.entity.Pages;

/**
 * @Description:告警信息接口类
 * @author: lkc
 * @version: 1.0, 2017年11月2日
 */

public interface AlarmOrderQueryService {
	/**
	 * 
	 * @Description:分页查询告警信息
	 * @param alarmInfo
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getAlarmOrderBypage(JSONObject orderInfo);



	/**
	 * 
	 * @Description:查询下拉框字段
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getState(int type);



	/**
	 * 
	 * @Description:通过工单id查询信息
	 * @param orderId
	 * @return
	 */

	RpcResponse<Map<String, Object>> getAlarmOrderInfoById(String orderId);



	/**
	 *
	 * @Description:通过故障工单id查询告警工单信息
	 * @param faultOrderId
	 * @return
	 */

	RpcResponse<Map<String, Object>> getAlarmOrderInfoByFaultOrderId(String faultOrderId);



	/**
	 * 
	 * @Description:查询待办流程
	 * @param list
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getAlarmOrderTodoByPage(JSONObject parm, List<String> list);



	/**
	 * 
	 * @Description:查询未接受的告警工单
	 * @param orderInfo
	 * @return
	 */
	RpcResponse<Pages<Map<String, Object>>> notClaimAlarmOrder(JSONObject orderInfo);



	/**
	 * 
	 * @Description:统计告警工单数量
	 * @param orderCountMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<PageInfo<Map>> alarmOrderCountInfo(AlarmOrderCount alarmOrderCount);



	/**
	 * 
	 * @Description:根据区域查询告警工单统计的详情列表
	 * @param detailsJsonObj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<PageInfo<Map>> alarmDetailsCount(AlarmCountDetails alarmCountDetails);



	/**
	 * 
	 * @Description:统计所有告警工单列表
	 * @param
	 * @return
	 */
	RpcResponse<PageInfo<CountAlarmOrderDto>> countAllAlarmOrder(JSONObject json);



	/**
	 * 
	 * @Description:查询告警工单及其对应的所有告警信息(按规则分组)
	 * @param
	 * @return
	 */

	RpcResponse<List<Map<String, Object>>> getAlarmOrderAndAllAlarmInfo(String orderId);



	/**
	 * 
	 * @Description: 查询总条数
	 * @param
	 * @return
	 */

	RpcResponse<Integer> getNotClaimAlarmOrderTotal(JSONObject orderInfo);



	/**
	 * 
	 * @Description:根据告警id查询对应工单信息
	 * @param alarmId
	 * @return
	 */
	RpcResponse<Map<String, Object>> getAlarmOrderInfoByAlarmId(String alarmId);



	/**
	 * 
	 * @Description:根据告警工单id查询对应工单信息(用于推送到指定地址)
	 * @param alarmOrderId
	 * @return
	 */
	RpcResponse<Map<String, Object>> getChangedAlarmOrderInfoByOrderId(String alarmOrderId);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	RpcResponse<PageInfo<Map<String, Object>>> countAlarmOrderByOrg(JSONObject json);



	/**
	 * 
	 * @Description: 查看告警工单详情(告警工单,查看工单按钮)
	 * @param
	 * @return
	 */

	RpcResponse<Map<String, Object>> getAlarmOrderAndFacInfo(String alarmOrderId);



	/**
	 * 
	 * @Description:通过告警工单id获取关联的所有告警id
	 * @param alarmOrderId
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getAlarmIdByAlarmOrderId(String alarmOrderId);



	/**
	 * 
	 * @Description:查询隐患类型
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getHiddenTroubleType();

}
