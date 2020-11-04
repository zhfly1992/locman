/*
* File name: DeviceTypeInfoDto.java								
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
* 1.0			guofeilong		2018年12月13日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

/**
* @Description:	接收从新iot查询到的产品信息
* @author: guofeilong
* @version: 1.0, 2018年12月13日
*/

public class DeviceTypeInfoDto {
	
	/** 产品id(设备类型id) */
	private String				id;

	/** 产品名称(设备类型名称) */
	private String				productName;
	
	/** 产品协议类型 */
	private String				productProtocolType;
	
	/** 开放/私有协议 */
	private String				productLevel;

	/**
	 * 
	 */
	public DeviceTypeInfoDto() {
		super();
		
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
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the productProtocolType
	 */
	public String getProductProtocolType() {
		return productProtocolType;
	}

	/**
	 * @param productProtocolType the productProtocolType to set
	 */
	public void setProductProtocolType(String productProtocolType) {
		this.productProtocolType = productProtocolType;
	}

	/**
	 * @return the productLevel
	 */
	public String getProductLevel() {
		return productLevel;
	}

	/**
	 * @param productLevel the productLevel to set
	 */
	public void setProductLevel(String productLevel) {
		this.productLevel = productLevel;
	}
	
	
}
