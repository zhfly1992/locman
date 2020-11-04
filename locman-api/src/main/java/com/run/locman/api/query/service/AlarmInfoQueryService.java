package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.AlarmInfo;
import com.run.locman.api.entity.Pages;

/**
 * @Description:告警信息接口类
 * @author: lkc
 * @version: 1.0, 2017年11月2日
 */

public interface AlarmInfoQueryService {
	/**
	 * 
	 * @Description:分页查询告警信息
	 * @param alarmInfo
	 * @return
	 */
	RpcResponse<Pages<Map<String, Object>>> getAlarmInfoBypage(JSONObject alarmInfo);



	/**
	 * 
	 * @Description:通过设施id获取告警信息
	 * @param facId
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getAlarmInfoByFacilitesId(JSONObject alarmInfo);



	/**
	 * 根据组织ID 查询近三天的所有告警信息，若近三天没有，则返回最新十条告警
	 * 
	 * @Description:
	 * @param organizationId
	 *            组织ID
	 * @param accessSecret
	 *            接入方秘钥
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getNearlyAlarmInfo(String organizationId, String accessSecret);



	/**
	 * 
	 * @Description:通过设备Id查询设备告警信息
	 * @param deviceId
	 * @return
	 */
	RpcResponse<Map<String, Object>> getAlarmInfoBydeviceId(String deviceId);



	/**
	 * 
	 * @Description:分页获取告警列表信息
	 * @param alarmInfo
	 * @return RpcResponse<PageInfo<Map<String, Object>>>
	 */
	RpcResponse<Pages<Map<String, Object>>> getAlarmInfoList(JSONObject alarmInfo);



	/**
	 * 
	 * @Description:根据主键ID查询告警信息
	 * @param id
	 *            主键ID
	 * @return
	 */
	RpcResponse<AlarmInfo> findById(String id);



	/**
	 * 
	 * @Description:统计告警信息
	 * @param alarmInfo
	 * @return RpcResponse<PageInfo<Map<String, Object>>>
	 */
	RpcResponse<PageInfo<Map<String, Object>>> statisticsAlarmInfo(JSONObject alarmInfo, String pageNo);



	/**
	 * 
	 * @Description:概览滚动告警信息(最近的10条不自动生成告警工单的告警信息,不区分设备)
	 * @param alarmInfo
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> alarmInfoRoll(JSONObject alarmInfo);



	/**
	 * 
	 * @Description:查询设备产生告警当时上报的数据
	 * @param alarmInfoId
	 * @return
	 */
	RpcResponse<JSONObject> queryAlarmDeviceData(String alarmInfoId);



	/**
	 * 
	 * @Description:查询设备告警的属性点的Key值集合
	 * @param deviceId
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<List<String>> queryAlarmItemList(String deviceId, String accessSecret);

}
