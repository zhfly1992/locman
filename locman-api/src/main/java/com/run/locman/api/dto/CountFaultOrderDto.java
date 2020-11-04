/*
 * File name: CountFaultOrderDto.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年7月19日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.dto;

import java.io.Serializable;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年7月19日
 */

public class CountFaultOrderDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7904729445155821481L;
	/** 密钥 */
	private String	accessSecret;
	/** 工单名称 */
	private String	orderName;
	/** 工单流水号 */
	private String	serialNumber;
	/** 申告时间 */
	private String	createTime;
	/** 故障类型 */
	private String	faultType;
	/** 厂家名 */
	private String	factoryName;
	/** 工单状态 */
	private String	orderState;
	/** 区域 */
	private String	area;
	/** 流程id */
	private String	processId;
	/** 处理人姓名 */
	private String	personName;
	/** 处理人电话 */
	private String	phoneNumber;
	/** 位置*/
	private String	address;
	


	/**
	 * 
	 */
	public CountFaultOrderDto() {
		super();

	}



	/**
	 * @param accessSecret 密钥
	 * @param orderName 工单名称
	 * @param serialNumber 工单流水号
	 * @param createTime 申告时间
	 * @param faultType 故障类型
	 * @param factoryName 厂家名
	 * @param orderState 工单状态
	 * @param area 区域
	 * @param processId 流程id
	 * @param personName 处理人姓名
	 * @param phoneNumber 处理人电话 
	 * @param address 位置
	 */
	
	public CountFaultOrderDto(String accessSecret, String orderName, String serialNumber, String createTime,
			String faultType, String factoryName, String orderState, String area, String processId, String personName,
			String phoneNumber, String address) {
		super();
		this.accessSecret = accessSecret;
		this.orderName = orderName;
		this.serialNumber = serialNumber;
		this.createTime = createTime;
		this.faultType = faultType;
		this.factoryName = factoryName;
		this.orderState = orderState;
		this.area = area;
		this.processId = processId;
		this.personName = personName;
		this.phoneNumber = phoneNumber;
		this.address = address;
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
	 * @return the orderName
	 */
	public String getOrderName() {
		return orderName;
	}



	/**
	 * @param orderName the orderName to set
	 */
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}



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
	 * @return the faultType
	 */
	public String getFaultType() {
		return faultType;
	}



	/**
	 * @param faultType the faultType to set
	 */
	public void setFaultType(String faultType) {
		this.faultType = faultType;
	}



	/**
	 * @return the factoryName
	 */
	public String getFactoryName() {
		return factoryName;
	}



	/**
	 * @param factoryName the factoryName to set
	 */
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
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
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}



	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
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
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}



	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}



	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}



	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}



	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}




}
