package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 
 * @Description:信息概览查询
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface InfoSummaryQueryRepository {

	public Map<String, Object> getDeviceCount(String accessSecret);



	public Map<String, Object> getFacilityCount(String accessSecret);



	public Integer getUnprocessedAlarmCount(String accessSecret);



	public Map<String, Object> getUpprocessedOrderCount(String accessSecret);



	public List<String> getDeviceIdListByAccessSecret(String accessSecret);



	/* 获取一个月（30天内）每天的紧急告警数量 */
	public List<Map<String, Object>> getDailyUrgentAlarmCountInMonth(String accessSecret);
	
	/* 获取一个月（30天内）每天的一般告警数量 */
	public List<Map<String, Object>> getDailyNormalAlarmCountInMonth(String accessSecret);



	/* 获取一个月(30天内）每天的故障工单中的故障设备数量 */
	public List<Map<String, Object>> getDailyFaultCountInMonth(String accessSecret);



	/* 统计每天的告警数量,最多一个月 */
	public List<Map<String, Object>> countAlarmNumByDate(@Param("endTime") String endTime,
			@Param("accessSecret") String accessSecret, @Param("dayNum") int dayNum);



	/* 告警工单处理情况统计，按天数统计，最多一个月 */
	public List<Map<String, Object>> getAlarmOrderProcessStateStatistic(@Param("endTime") String endTime,
			@Param("accessSecret") String accessSecret, @Param("dayCount") int dayCount);



	/** 故障工单处理情况统计，按天数统计，最多一个月 */
	public List<Map<String, Object>> getFaultOrderProcessStateStatistic(@Param("endTime") String endTime,
			@Param("accessSecret") String accessSecret, @Param("dayCount") int dayCount);



	/** 统计告警等级分别为1,2和无告警设施数量 */
	public Map<String, Object> countFacAlarmCount(@Param("accessSecret") String accessSecret);



	/** 按告警等级统计每天的告警设备数，日期为以昨天为截止的七天内 */
	public List<Map<String, Object>> countDeviceNumByAlarmLevelInSevenDays(@Param("accessSecret") String accessSecret);




	/** 按告警等级统计和工单状态统计告警工单数量 */
	public List<Map<String, Object>> countAlarmOrderNumByAlarmLevelAndState(@Param("accessSecret") String accessSecret);



	/** 成华区用,按街道统计告警设施数量 */
	public List<Map<String, Object>> getCountAlarmFacByOrg(@Param("accessSecret") String accessSecret);


	/** 成华用，单个设备告警走向*/
	public List<Map<String ,Object>> getCountAlarmByIdAndTime(@Param("accessSecret") String accessSecret,@Param("deviceId") String deviceId,
			@Param("endTime") String endTime,@Param("startTime") String startTime);
	/**成华区，统计触发告警规则设备数量*/
	public List<Map<String ,Object>> getCountAlarmDevByRule(@Param("accessSecret") String accessSecret);
	
	/**成华区，查询设备ID*/
	public List<Map<String ,Object>> getBandingDeviceId(@Param("accessSecret") String accessSecret);
	
	public Map<String,Object> getFacAndDevInfoById(@Param("deviceId") String deviceId);
}
