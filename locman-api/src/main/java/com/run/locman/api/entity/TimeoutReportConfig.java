/*
* File name: TimeoutReportConfig.java								
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
* 1.0			guofeilong		2018年6月22日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月22日
*/

public class TimeoutReportConfig  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键id */
	private String				id;
	/** 配置名称 */
	private String				name;
	/** 创建时间 */
	private String				createTime;
	/** 创建人 */
	private String				createUserId;
	/** 更新时间 */
	private String				updateTime;
	/** 修改人 */
	private String				updateUserId;
	/** 时间,单位:小时 */
	private Integer				timeoutReportTime;
	/** 接入方密钥 */
	private String				accessSecret;
	/** 启用/停用状态(假删除) */
	private String				managerState;
	/**
	 * 
	 */
	public TimeoutReportConfig() {
		super();
		
	}
	/**
	 * @param id
	 * @param name
	 * @param createTime
	 * @param createUserId
	 * @param updateTime
	 * @param updateUserId
	 * @param timeoutReportTime
	 * @param accessSecret
	 * @param managerState
	 */
	public TimeoutReportConfig(String id, String name, String createTime, String createUserId, String updateTime,
			String updateUserId, Integer timeoutReportTime, String accessSecret, String managerState) {
		super();
		this.id = id;
		this.name = name;
		this.createTime = createTime;
		this.createUserId = createUserId;
		this.updateTime = updateTime;
		this.updateUserId = updateUserId;
		this.timeoutReportTime = timeoutReportTime;
		this.accessSecret = accessSecret;
		this.managerState = managerState;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	/**
	 * @return the createUserId
	 */
	public String getCreateUserId() {
		return createUserId;
	}
	/**
	 * @param createUserId the createUserId to set
	 */
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	/**
	 * @return the updateTime
	 */
	public String getUpdateTime() {
		return updateTime;
	}
	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * @return the updateUserId
	 */
	public String getUpdateUserId() {
		return updateUserId;
	}
	/**
	 * @param updateUserId the updateUserId to set
	 */
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	/**
	 * @return the timeoutReportTime
	 */
	public Integer getTimeoutReportTime() {
		return timeoutReportTime;
	}
	/**
	 * @param timeoutReportTime the timeoutReportTime to set
	 */
	public void setTimeoutReportTime(Integer timeoutReportTime) {
		this.timeoutReportTime = timeoutReportTime;
	}
	/**
	 * @return the accessSecret
	 */
	public String getAccessSecret() {
		return accessSecret;
	}
	/**
	 * @param accessSecret the accessSecret to set
	 */
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}
	/**
	 * @return the managerState
	 */
	public String getManagerState() {
		return managerState;
	}
	/**
	 * @param managerState the managerState to set
	 */
	public void setManagerState(String managerState) {
		this.managerState = managerState;
	}
	
}
