/*
* File name: CountAlarmOrderDto.java								
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
* 1.0			guofeilong		2018年8月7日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

import java.io.Serializable;

/**
* @Description:	统计所有告警工单实体类
* @author: guofeilong
* @version: 1.0, 2018年8月7日
*/

public class CountAlarmOrderDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 密钥 */
	private String	accessSecret;
	/** 告警工单号*/
	private String	orderNumber;
	/** 告警流水号 */
	private String	alarmSerialNumber;
	/** 告警时间 */
	private String	alarmTime;
	/** 告警等级 */
	private String	alarmLevel;
	/** 工单处理人 */
	private String	personName;
	/** 工单状态 */
	private String	orderState;
	/** 区域 */
	private String	area;
	/** 设施序列号 */
	private String	facilitiesCode;
	/** 组织Id */
	private String	organizationId;
	/** 设施类型 */
	private String	facilityTypeAlias;
	/** 组织名 */
	private String	organizationName;
	/** 组织名 */
	private String	receiveTime;
	/** 告警工单id */
	private String	id;
	
	
	/**
	 * 
	 */
	public CountAlarmOrderDto() {
		super();
		
	}
	/**
	 * @param accessSecret 密钥
	 * @param orderNumber 告警工单号
	 * @param alarmSerialNumber 告警流水号
	 * @param alarmTime 告警时间 
	 * @param alarmLevel 告警等级
	 * @param personName 工单处理人
	 * @param orderState 工单状态
	 * @param area 区域
	 * @param facilitiesCode 设施序列号 
	 * @param organizationId 组织id
	 * @param facilityTypeAlias 设施类型
	 * @param organizationName 组织名
	 */
 
	public CountAlarmOrderDto(String accessSecret, String orderNumber, String alarmSerialNumber, String alarmTime,
			String alarmLevel, String personName, String orderState, String area, String facilitiesCode,
			String organizationId, String facilityTypeAlias, String organizationName, String receiveTime,String id) {
		super();
		this.accessSecret = accessSecret;
		this.orderNumber = orderNumber;
		this.alarmSerialNumber = alarmSerialNumber;
		this.alarmTime = alarmTime;
		this.alarmLevel = alarmLevel;
		this.personName = personName;
		this.orderState = orderState;
		this.area = area;
		this.facilitiesCode = facilitiesCode;
		this.organizationId = organizationId;
		this.facilityTypeAlias = facilityTypeAlias;
		this.organizationName = organizationName;
		this.receiveTime = receiveTime;
		this.id = id;
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
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return orderNumber;
	}
	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return the alarmSerialNumber
	 */
	public String getAlarmSerialNumber() {
		return alarmSerialNumber;
	}
	/**
	 * @param alarmSerialNumber the alarmSerialNumber to set
	 */
	public void setAlarmSerialNumber(String alarmSerialNumber) {
		this.alarmSerialNumber = alarmSerialNumber;
	}
	/**
	 * @return the alarmTime
	 */
	public String getAlarmTime() {
		return alarmTime;
	}
	/**
	 * @param alarmTime the alarmTime to set
	 */
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	/**
	 * @return the alarmLevel
	 */
	public String getAlarmLevel() {
		return alarmLevel;
	}
	/**
	 * @param alarmLevel the alarmLevel to set
	 */
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	/**
	 * @return the personName
	 */
	public String getPersonName() {
		return personName;
	}
	/**
	 * @param personName the personName to set
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	/**
	 * @return the orderState
	 */
	public String getOrderState() {
		return orderState;
	}
	/**
	 * @param orderState the orderState to set
	 */
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	/**
	 * @return the area
	 */
	public String getArea() {
		return area;
	}
	/**
	 * @param area the area to set
	 */
	public void setArea(String area) {
		this.area = area;
	}
	/**
	 * @return the facilitiesCode
	 */
	public String getFacilitiesCode() {
		return facilitiesCode;
	}
	/**
	 * @param facilitiesCode the facilitiesCode to set
	 */
	public void setFacilitiesCode(String facilitiesCode) {
		this.facilitiesCode = facilitiesCode;
	}
	/**
	 * @return the organizationId
	 */
	public String getOrganizationId() {
		return organizationId;
	}
	/**
	 * @param organizationId the organizationId to set
	 */
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	/**
	 * @return the facilityTypeAlias
	 */
	public String getFacilityTypeAlias() {
		return facilityTypeAlias;
	}
	/**
	 * @param facilityTypeAlias the facilityTypeAlias to set
	 */
	public void setFacilityTypeAlias(String facilityTypeAlias) {
		this.facilityTypeAlias = facilityTypeAlias;
	}
	/**
	 * @return the organizationName
	 */
	public String getOrganizationName() {
		return organizationName;
	}
	/**
	 * @param organizationName the organizationName to set
	 */
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	/**
	 * @return the receiveTime
	 */
	public String getReceiveTime() {
		return receiveTime;
	}
	/**
	 * @param receiveTime the receiveTime to set
	 */
	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
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

	
	
}
