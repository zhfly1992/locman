/*
 * File name: AlarmOrderCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月4日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.AlarmOrder;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年12月4日
 */

public interface AlarmOrderCrudService {

	/**
	 * 
	 * @Description: 保存告警工单信息
	 * @param alarmOrder
	 *            告警工单实体类
	 * @return
	 */
	RpcResponse<String> saveAlarmOrder(AlarmOrder alarmOrder);



	/**
	 * 更新告警工单信息
	 * 
	 * @Description:
	 * @param map
	 *            更新参数
	 * @return
	 */
	RpcResponse<Boolean> updateAlarmOrder(AlarmOrder alarmOrder);



	/**
	 * 
	 * @Description: 同一个设备同一个规则的告警，仅更新告警ID
	 * @param map
	 * @return
	 */
	RpcResponse<String> updateOrderAlarmId(Map<String, Object> map);



	/**
	 * 
	 * @Description:保存告警信息和告警工单的关系
	 * @param alarmOrder
	 * @return RpcResponse<Map<String, Object>>
	 */
	RpcResponse<Map<String, Object>> saveAlarmOrderandInfo(JSONObject jsonParam);

	/**
	 * 
	 * @Description:到场图片  的添加
	 * @param alarmOrder
	 * @return RpcResponse<Map<String, Object>>
	 */
	RpcResponse<String> addPresentPicAlarmOrder(String id, String Url);



	/**
	* @Description:
	* @param id
	* @param picUrl
	* @return
	*/
	
	RpcResponse<String> addEndPicAlarmOrder(String id,List<Object> Url);
	

}


