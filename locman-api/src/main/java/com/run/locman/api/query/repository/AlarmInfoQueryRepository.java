
package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.entity.AlarmInfo;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年11月1日
 */

public interface AlarmInfoQueryRepository extends BaseQueryRepository<AlarmInfo, String> {
	/**
	 * 
	 * @Description:通过接入方密钥查询告警信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> getAlarmInfoByAccessSercret(Map<String, Object> map);



	/**
	 * 
	 * @Description:统计状态列表数据总条数
	 * @param
	 * @return
	 */
	int countTotaleOfStateList(Map<String, Object> map);



	/**
	 * 
	 * @Description:通过设施code查询设备信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> getAlarmInfoByFacId(Map<String, String> map);



	/**
	 * 根据组织ID 查询近三天的所有告警信息，若近三天没有，则返回最新十条告警
	 * 
	 * @Description:
	 * @param organizationId
	 *            组织ID集合
	 * @param accessSecret
	 *            接入方秘钥
	 * @return
	 */
	List<Map<String, Object>> getNearlyAlarmInfo(@Param("organizationId") String organizationId,
			@Param("accessSecret") String accessSecret);



	/**
	 * 根据组织ID 查询近三天的所有告警信息，若近三天没有，则返回最新十条告警
	 * 
	 * @Description:
	 * @param organizationId
	 *            组织ID集合
	 * @param accessSecret
	 *            接入方秘钥
	 * @return
	 */
	List<Map<String, Object>> getNearlyTenAlarmInfo(@Param("organizationId") String organizationId,
			@Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @Description:通过设备id查询告警信息
	 * @param map
	 * @return
	 */
	Map<String, Object> getAlarmInfoByDeviceId(String deviceId);



	/**
	 * 
	 * @Description:分页获取告警信息列表
	 * @param map
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getAlarmInfoList(Map<String, Object> map);



	/**
	 * 
	 * @Description:获取告警列表总条数
	 * @param
	 * @return
	 */
	int getAlarmInfoListNum(Map<String, Object> map);



	/**
	 * 
	 * @Description:统计告警信息
	 * @param map
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> statisticsAlarmInfo(Map<String, Object> map);



	/**
	 * 
	 * @Description:概览滚动告警信息(最近的10条不自动生成告警工单的告警信息,不区分设备)
	 * @param map
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> alarmInfoRoll(Map<String, Object> map);



	/**
	 * 
	 * @Description:通过告警信息id查询时间戳
	 * @param alarmId
	 * @return
	 */
	Map<String, Object> getTimeStampByAlarmId(String alarmInfoId);



	/**
	 * 
	 * @Description:获取设备类型ID
	 * @param deviceId
	 * @return
	 */
	String getDeviceTypeByDeviceId(String deviceId);



	/**
	 * 
	 * @Description:查询设备告警的属性点的Key值集合
	 * @param deviceId
	 * @param accessSecret
	 * @return
	 */
	List<String> queryAlarmItemList(@Param("deviceId") String deviceId, @Param("accessSecret") String accessSecret);

}
