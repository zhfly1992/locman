package com.run.locman.api.crud.service;

import java.util.List;
import java.util.Map;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.AlarmInfo;
import com.run.locman.api.entity.AlarmOrder;

/**
 * 
 * @Description:告警信息crud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface AlarmInfoCrudService {

	RpcResponse<AlarmInfo> add(Map<String, Object> map);



	/**
	 * 
	 * @Description:修改忽略状态
	 * @param map
	 * @return RpcResponse<String>
	 */
	RpcResponse<String> updateTheDel(Map<String, Object> map);



	/**
	 *
	 * @Description:通过组合条件修改一般告警信息的状态（除开忽略状态）
	 * @param map
	 * @return RpcResponse<String>
	 */
	RpcResponse<String> updateTheDelByCondition(Map<String, Object> map);



	/**
	 * 
	 * @Description: 处理告警
	 * @param map
	 * @return
	 */
	RpcResponse<Boolean> disposeAlarmInfo(Map<String, Object> map);



	/**
	 * 
	 * @Description:添加告警信息
	 * @param alarmInfo
	 * @return RpcResponse<Integer>
	 */
	RpcResponse<Integer> saveAlarmInfo(AlarmInfo alarmInfo);



	/**
	 * 
	 * @Description:针对于完成告警工单时事务问题，在一个rpc中修改告警工单表以及告警信息表
	 * @param map
	 * @param alarmOrder
	 * @return
	 */
	RpcResponse<Boolean> updateAlarmOrderAndInfo(Map<String, Object> map, AlarmOrder alarmOrder);



	/**
	 * 
	 * @Description:当告警工单完成被拒绝时，针对于完成告警工单时事务问题，在一个rpc中修改告警工单表以及告警信息表
	 * @param map
	 * @param alarmOrder
	 * @return
	 */
	RpcResponse<Boolean> updateAlarmOrderAndInfoForCompeleteReject(List<String> alarmIds, AlarmOrder alarmOrder);
}
