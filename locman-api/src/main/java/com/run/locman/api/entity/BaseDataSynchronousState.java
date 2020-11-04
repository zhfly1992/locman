/*
* File name: BaseDataSynchronousState.java								
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
* 1.0			guofeilong		2018年4月25日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
* @Description:	基础数据同步状态
* @author: guofeilong
* @version: 1.0, 2018年4月25日
*/

public class BaseDataSynchronousState implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String				id;
	private Boolean				baseAlarmRule;
	private Boolean				baseDeviceInfoConvert;
	private Boolean				baseDeviceTypeTemplate;
	private Boolean				baseFacilitiesType;
	private String				accessSecret;
	/**
	 * 
	 */
	public BaseDataSynchronousState() {
		super();
		
	}
	/**
	 * @param id
	 * @param baseAlarmRule
	 * @param baseDeviceInfoConvert
	 * @param baseDeviceTypeTemplate
	 * @param baseFacilitiesType
	 * @param accessSecret
	 */
	public BaseDataSynchronousState(String id, Boolean baseAlarmRule, Boolean baseDeviceInfoConvert,
			Boolean baseDeviceTypeTemplate, Boolean baseFacilitiesType, String accessSecret) {
		super();
		this.id = id;
		this.baseAlarmRule = baseAlarmRule;
		this.baseDeviceInfoConvert = baseDeviceInfoConvert;
		this.baseDeviceTypeTemplate = baseDeviceTypeTemplate;
		this.baseFacilitiesType = baseFacilitiesType;
		this.accessSecret = accessSecret;
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
	 * @return the baseAlarmRule
	 */
	public Boolean getBaseAlarmRule() {
		return baseAlarmRule;
	}
	/**
	 * @param baseAlarmRule the baseAlarmRule to set
	 */
	public void setBaseAlarmRule(Boolean baseAlarmRule) {
		this.baseAlarmRule = baseAlarmRule;
	}
	/**
	 * @return the baseDeviceInfoConvert
	 */
	public Boolean getBaseDeviceInfoConvert() {
		return baseDeviceInfoConvert;
	}
	/**
	 * @param baseDeviceInfoConvert the baseDeviceInfoConvert to set
	 */
	public void setBaseDeviceInfoConvert(Boolean baseDeviceInfoConvert) {
		this.baseDeviceInfoConvert = baseDeviceInfoConvert;
	}
	/**
	 * @return the baseDeviceTypeTemplate
	 */
	public Boolean getBaseDeviceTypeTemplate() {
		return baseDeviceTypeTemplate;
	}
	/**
	 * @param baseDeviceTypeTemplate the baseDeviceTypeTemplate to set
	 */
	public void setBaseDeviceTypeTemplate(Boolean baseDeviceTypeTemplate) {
		this.baseDeviceTypeTemplate = baseDeviceTypeTemplate;
	}
	/**
	 * @return the baseFacilitiesType
	 */
	public Boolean getBaseFacilitiesType() {
		return baseFacilitiesType;
	}
	/**
	 * @param baseFacilitiesType the baseFacilitiesType to set
	 */
	public void setBaseFacilitiesType(Boolean baseFacilitiesType) {
		this.baseFacilitiesType = baseFacilitiesType;
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
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseDataSynchronousState [id=" + id + ", baseAlarmRule=" + baseAlarmRule + ", baseDeviceInfoConvert="
				+ baseDeviceInfoConvert + ", baseDeviceTypeTemplate=" + baseDeviceTypeTemplate + ", baseFacilitiesType="
				+ baseFacilitiesType + ", accessSecret=" + accessSecret + "]";
	}
	
}
