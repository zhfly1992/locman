package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 
 * @Description:工单统计
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface StatisticsRepository {
	/**
	 * 
	 * @Description:查询告警工单统计
	 * @param parm
	 * @return
	 */
	public Map<String, Integer> getStatisticsOrder(@Param("userId") String userId,
			@Param("accessSecret") String accessSecret);



	public List<Map<String, Object>> getAlarmOrderProcessStateStatistic(@Param("endTime") String endTime,
			@Param("accessSecret") String accessSecret, @Param("dayCount") int dayCount);
}
