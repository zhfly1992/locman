/*
* File name: SimpleOrderDto.java								
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
* 1.0			guofeilong		2019年1月3日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

import java.io.Serializable;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年1月3日
*/

public class SimpleOrderDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 工单id */
	private String				id;
	/** 工单流水号 */
	private String				serialNumber;
	/** 流程开始时间 */
	private String				processStartTime;
	/** 流程结束时间 */
	private String				processEndTime;
	/** 用户id */
	private String				userId;
	/** 提醒时间 */
	private String				remindTime;
	/** 接入方密钥 */
	private String				accessSecret;
	
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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
	 * @return the processStartTime
	 */
	public String getProcessStartTime() {
		return processStartTime;
	}
	/**
	 * @param processStartTime the processStartTime to set
	 */
	public void setProcessStartTime(String processStartTime) {
		this.processStartTime = processStartTime;
	}
	/**
	 * @return the processEndTime
	 */
	public String getProcessEndTime() {
		return processEndTime;
	}
	/**
	 * @param processEndTime the processEndTime to set
	 */
	public void setProcessEndTime(String processEndTime) {
		this.processEndTime = processEndTime;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the remindTime
	 */
	public String getRemindTime() {
		return remindTime;
	}
	/**
	 * @param remindTime the remindTime to set
	 */
	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
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
	
	
}
