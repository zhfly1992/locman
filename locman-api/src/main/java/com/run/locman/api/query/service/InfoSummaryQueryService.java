package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * 
 * @Description:信息概述页面查询接口
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface InfoSummaryQueryService {

	/**
	 * @Description: 根据accessSecret查询设备总数和未绑定的数量
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<Map<String, Object>> getDeviceCountByAccessSecret(String accessSecret);



	/**
	 * @Description: 根据accessSecret查询设施总数和未绑定的数量
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<Map<String, Object>> getFacilityCountByAccessSecret(String accessSecret);



	/**
	 * @Description: 根据accessSecret查询告警和工单未完成工单数量
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<Map<String, Object>> getUnprocessedOrderCountByAccessSecret(String accessSecret);



	/**
	 * @Description: 根据accessSecret查询超过指定天数未上报设备数量
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<String> getDeviceCountNotReportInSetDayByAccessSecret(String accessSecret);



	/**
	 * @Description: 根据accessSecret查询正常设备数
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<String> getNormalDeviceCountByAccessSecret(String accessSecret);



	/**
	 * @Description: 根据accessSecret查询用户数目（不算超级管理员）
	 * @param accessSecret
	 * @param token
	 * @return
	 */
	RpcResponse<String> getUserNumberByAccessSecret(String accessSecret, String token);



	/**
	 * @Description: 根据accessSecret获取当前接入方信息
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<String> getAccessInformationByAccessSecret(String accessSecret, String token);



	/**
	 * @Description: 根据accessSecret获取当前接入方用户使用率
	 * @param accessSecret
	 * @param token
	 * @return
	 */
	RpcResponse<String> getUsageRateByAccessSecret(String accessSecret, String token);



	/**
	 * @Description: 根据accessSecret获取当前接入方每天的告警数量（30天）
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<Map<String, Object>> getDailyAlarmCountInMonth(String accessSecret);



	/**
	 * 
	 * @Description:根据accessSecret获取当前接入方每天的故障设备数（30天）
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<Map<String, Object>> getDailyFaultCountInMonth(String accessSecret);



	/**
	 * 
	 * @Description: 根据accessSecret获取当前接入方每天的告警数量（最多查询结束时间前30天）
	 * @param
	 * @return
	 */

	RpcResponse<Map<String, Object>> countAlarmNumByDate(String accessSecret, String endTime, int dayNum);



	/**
	 * 
	 * @Description:工单处理统计,按日期统计每天的已处理和未处理工单数，默认显示当前时间往前30天的工单处理统计走势，查询支持最大查询范围为30天，可查询故障工单或者告警工单
	 * @param dayCount
	 *            查询天数
	 * @param endTime
	 * @param accessSecret
	 * @param orderType
	 * @return
	 */
	public RpcResponse<List<Map<String, Object>>> getOrderProcessStateNumStatistic(int dayCount, String endTime,
			String accessSecret, String orderType);



	/**
	 * 
	 * @Description:成华区用，统计街道办数量和街道数量(街道办是组织，街道是街道办下的子组织)
	 * @param accessSecret
	 * @param token
	 * @return
	 */
	public RpcResponse<Map<String, Object>> countStreetAndStreetOfficeNum(String accessSecret, String token);



	/**
	 * 
	 * @Description:成华区用，用于展示告警等级分别为1,2和无告警的设施数量 规则：将device上报的fv为空和等于1的不计入在内
	 * @param accessSecret
	 * @return
	 */
	public RpcResponse<Map<String, Object>> countFacAlarmNum(String accessSecret);



	/**
	 * <<<<<<< Updated upstream
	 * 
	 * @Description:每天设备上报数量统计
	 * @param param
	 * @return
	 */
	RpcResponse<Map<String, Object>> countDeviceReportNum(String param);



	/**
	 * =======
	 * 
	 * 
	 * @Description:成华区用，按日期统计七天内每天的设备的告警等级数量，分为一般告警（2）和紧急告警（1）和正常
	 * @param accessSecret
	 * @return
	 */
	public RpcResponse<JSONObject> countDeviceByAlarmLevelInSevenDays(String accessSecret);



	/**
	 * 
	 * @Description:成华区用,按等级和状态统计告警工单数量
	 * @param accessSecret
	 * @return
	 */
	public RpcResponse<JSONObject> countAlarmOrderNumByStateAndAlarm(String accessSecret);



	/**
	 * 
	 * @Description:成华区用,按街道统计告警设施
	 * @param accessSecret
	 * @return
	 */
	public RpcResponse<Map<String, Object>> countAlarmFacByOrg(String accessSecret, String token);



	/**
	 * @Description:
	 * @param jsonObject
	 * @return
	 */

	public RpcResponse<List<Map<String, Object>>> countAlarmByIdAndTime(JSONObject jsonObject);



	/**
	 * @Description:
	 * @param accessSecret
	 * @return
	 */

	public RpcResponse<List<Map<String, Object>>> countAlarmDevByRule(String accessSecret);
	
	public RpcResponse<List<Map<String,Object>>> countTriggerTop(String params);
	
	public RpcResponse<Map<String,Object>> detailsByDeviceId(JSONObject json );

}
