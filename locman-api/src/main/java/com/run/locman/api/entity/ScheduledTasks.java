/*
* File name: ScheduledTasks.java								
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

package com.run.locman.api.entity;

/**
* @Description:	定时任务实体类
* @author: 钟滨远
* @version: 1.0, 2020年6月5日
*/

public class ScheduledTasks {
	
	
	public static final int ST_START=1;
	
	public static final int ST_STOP=2;
	
	public static final int ST_DELETE=3;
	
	public static final int ST_SPECIAL=4;
	
	private String id;
	
	private int status;
	
	
	private String trrigerName;
	
	
	private String trrigerGroup;
	
	//实际执行时间
	private String performTime;
	
	//任务中数据
	private String  dataMap;
	
	//任务描述
	private String jobDescribe;

	/**
	 * @return the jobDescribe
	 */
	public String getJobDescribe() {
		return jobDescribe;
	}

	/**
	 * @param jobDescribe the jobDescribe to set
	 */
	public void setJobDescribe(String jobDescribe) {
		this.jobDescribe = jobDescribe;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	

	/**
	 * @return the trrigerName
	 */
	public String getTrrigerName() {
		return trrigerName;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @param trrigerName the trrigerName to set
	 */
	public void setTrrigerName(String trrigerName) {
		this.trrigerName = trrigerName;
	}

	/**
	 * @return the trrigerGroup
	 */
	public String getTrrigerGroup() {
		return trrigerGroup;
	}

	/**
	 * @param trrigerGroup the trrigerGroup to set
	 */
	public void setTrrigerGroup(String trrigerGroup) {
		this.trrigerGroup = trrigerGroup;
	}

	/**
	 * @return the performTime
	 */
	public String getPerformTime() {
		return performTime;
	}

	/**
	 * @param performTime the performTime to set
	 */
	public void setPerformTime(String performTime) {
		this.performTime = performTime;
	}

	/**
	 * @return the dataMap
	 */
	public String getDataMap() {
		return dataMap;
	}

	/**
	 * @param dataMap the dataMap to set
	 */
	public void setDataMap(String dataMap) {
		this.dataMap = dataMap;
	}
	
	
	
	
	
}
