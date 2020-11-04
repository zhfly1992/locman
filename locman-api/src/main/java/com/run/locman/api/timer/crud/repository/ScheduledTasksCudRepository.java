/*
* File name: ScheduledTasksCudRepository.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			钟滨远		2020年6月5日
* ...			...			...
*
***************************************************/

package com.run.locman.api.timer.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.dto.ProcessInfoListDto;
import com.run.locman.api.entity.ProcessInfo;
import com.run.locman.api.entity.ScheduledTasks;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年6月5日
*/

public interface ScheduledTasksCudRepository {
	
	/**
	 * 
	* @Description:任务新增
	* @param scheduledTasks
	* @return
	 */
	Integer insertScheduledTasks(ScheduledTasks scheduledTasks);
	
	/**
	 * 
	* @Description:修改任务状态
	* @param map
	* @return
	 */
	
	Integer updateScheduledTasks(@Param(value = "status") int status,@Param(value = "securityId") List<String> securityId);
	
	/**
	 * 
	* @Description:查询状态为1的任务信息list
	* @return
	 */
	List<Map<String,Object>> findScheduledTasksInfo();

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<ProcessInfoListDto> queryOrderProcessList(Map<String, Object> queryInfo);
	/**
	 * 
	* @Description:
	* @param map
	* @return
	 */
	String querystatusByTrrigerName(Map<String,Object> map);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int updatePart(ProcessInfo processInfo);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int updateSimpleOrderProcessVoid(@Param("accessSecret") String accessSecret, @Param("facilityIds") List<String> facilityIds);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	String facilityId4Access(String facilityId);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int updateAlarmOrderVoid(@Param("accessSecret") String accessSecret, @Param("facilityIds") List<String> facilityIds);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int updateAlarmInfoVoid(@Param("accessSecret") String accessSecret, @Param("facilityIds") List<String> facilityIds);

}
