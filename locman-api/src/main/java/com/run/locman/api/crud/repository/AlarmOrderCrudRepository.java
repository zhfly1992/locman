/*
 * File name: AlarmOrderCrudRepository.java
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

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.entity.AlarmOrder;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年12月4日
 */

public interface AlarmOrderCrudRepository extends BaseCrudRepository<AlarmOrder, String> {

	/**
	 * 
	 * @Description: 同一个设备同一个规则的告警，仅更新告警ID
	 * @param map
	 * @return
	 */
	int updateOrderAlarmId(Map<String, Object> map);



	/**
	 * 
	 * @Description:保存告警信息和告警工单的关系
	 * @param map
	 * @return int
	 */
	int saveAlarmOrderAndInfo(List<Map<String, Object>> list);



	/**
	 * 
	 * @Description:获取生成的工单跟同设备通告警规则还未处理的的告警信息id关系集合
	 * @param alarmId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getAlarmIdAndOrderIdByAlarmId(String alarmId);



	/**
	 * 
	 * @Description:到场图片的添加
	 * @param map
	 * @return
	 */
	int addPresentPic(Map<String, Object> map);



	/**
	 * 
	 * @Description:完成图片的添加
	 * @param map
	 * @return
	 */
	int addEndPic(Map<String, Object> map);



	/**
	 * 
	 * @Description:跟据id查询到场图片是否存在
	 * @param id
	 * @return
	 */
	String IsTherePresentPic(String id);



	/**
	 * 
	 * @Description:根据工单Id查询工单信息
	 * @param orderId
	 * @return
	 */
	AlarmOrder alarmOrderInfoByOrderId(String alarmOrderId);



	/**
	 * 
	 * @Description:根据设施id将设施对应的告警工单状态修改为已完成，成华区设施屏蔽用,当屏蔽设施时，将设施对应的告警工单完成
	 * @param facIds
	 * @param accessSecret
	 * @return
	 */
	int updateAlarmOrderStateAndAlarmInfoIsDel(@Param("facIds") List<String> facIds,
			@Param("organizationId") List<String> organizationId, @Param("accessSecret") String accessSecret);
	
	/**
	 * 
	 * @Description:查询未完成且到场图片存在的告警工单
	 * @return
	 */
	String getAlarmOrderIdPresentPicExits(@Param("deviceId") String deviceId,@Param("accessSecret")String accessSecret);
}
