/*
* File name: DeviceDataDto.java								
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
* 1.0			guofeilong		2018年11月28日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

import java.io.Serializable;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年11月28日
*/

public class DeviceDataDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -964529416952981490L;
	/**
	 * 主键id
	 */
	private String				id;
	/**
	 * 设备硬件编号
	 */
	private String				deviceNumber;
	/**
	 * 设备类型
	 */
	private String				deviceType;
	/**
	 * 设备安装地址
	 */
	private String				address;
	/**
	 * 设备安装经度
	 */
	private String				longitude;
	/**
	 * 设备安装纬度
	 */
	private String				latitude;
	/**
	 * 序列号（井盖序列号、井盖铭牌号）
	 */
	private String				facilitiesCode;
	/**
	 * 设施设备绑定状态
	 */
	private String				boundState;
	/**
	 * 设施类型
	 */
	private String				facilitiesType;
	/**
	 * 同步状态
	 */
	private String				synchronizationState;
	/**
	 * 区域id
	 */
	private String				areaCode;
	/**
	 * 区域名称
	 */
	private String				areaName;
	/**
	 * 设施类型id
	 */
	private String				facilityTypeId;
	/**
	 * 扩展属性
	 */
	private String				properties;
	/**
	 * 同步错误信息
	 */
	private String				errorInfo;
	/**
	 * 解析为显示扩展属性
	 */
	private String				showExtend;
	
	/**
	 * 解析为扩展属性
	 */
	private String				extend;
	
	/**
	 * 完整区域地址
	 */
	private String				completeAreaName;
	
	

	/**
	 * @return the completeAreaName
	 */
	public String getCompleteAreaName() {
		return completeAreaName;
	}
	/**
	 * @param completeAreaName the completeAreaName to set
	 */
	public void setCompleteAreaName(String completeAreaName) {
		this.completeAreaName = completeAreaName;
	}
	/**
	 * @return the showExtend
	 */
	public String getShowExtend() {
		return showExtend;
	}
	/**
	 * @param showExtend the showExtend to set
	 */
	public void setShowExtend(String showExtend) {
		this.showExtend = showExtend;
	}
	/**
	 * @return the extend
	 */
	public String getExtend() {
		return extend;
	}
	/**
	 * @param extend the extend to set
	 */
	public void setExtend(String extend) {
		this.extend = extend;
	}
	/**
	 * @return the errorInfo
	 */
	public String getErrorInfo() {
		return errorInfo;
	}
	/**
	 * @param errorInfo the errorInfo to set
	 */
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	/**
	 * @return the facilityTypeId
	 */
	public String getFacilityTypeId() {
		return facilityTypeId;
	}
	/**
	 * @param facilityTypeId the facilityTypeId to set
	 */
	public void setFacilityTypeId(String facilityTypeId) {
		this.facilityTypeId = facilityTypeId;
	}
	/**
	 * @return the properties
	 */
	public String getProperties() {
		return properties;
	}
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(String properties) {
		this.properties = properties;
	}
	/**
	 * @return the deviceType
	 */
	public String getDeviceType() {
		return deviceType;
	}
	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	/**
	 * 
	 */
	public DeviceDataDto() {
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
	 * @return the deviceNumber
	 */
	public String getDeviceNumber() {
		return deviceNumber;
	}
	/**
	 * @param deviceNumber the deviceNumber to set
	 */
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
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
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
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
	 * @return the boundState
	 */
	public String getBoundState() {
		return boundState;
	}
	/**
	 * @param boundState the boundState to set
	 */
	public void setBoundState(String boundState) {
		this.boundState = boundState;
	}
	/**
	 * @return the facilitiesType
	 */
	public String getFacilitiesType() {
		return facilitiesType;
	}
	/**
	 * @param facilitiesType the facilitiesType to set
	 */
	public void setFacilitiesType(String facilitiesType) {
		this.facilitiesType = facilitiesType;
	}
	/**
	 * @return the synchronizationState
	 */
	public String getSynchronizationState() {
		return synchronizationState;
	}
	/**
	 * @param synchronizationState the synchronizationState to set
	 */
	public void setSynchronizationState(String synchronizationState) {
		this.synchronizationState = synchronizationState;
	}
	/**
	 * @return the areaCode
	 */
	public String getAreaCode() {
		return areaCode;
	}
	/**
	 * @param areaCode the areaCode to set
	 */
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	/**
	 * @return the areaName
	 */
	public String getAreaName() {
		return areaName;
	}
	/**
	 * @param areaName the areaName to set
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceDataDto [id=" + id + ", deviceNumber=" + deviceNumber + ", address=" + address + ", longitude="
				+ longitude + ", latitude=" + latitude + ", facilitiesCode=" + facilitiesCode + ", boundState="
				+ boundState + ", facilitiesType=" + facilitiesType + ", synchronizationState=" + synchronizationState
				+ ", areaCode=" + areaCode + ", areaName=" + areaName + "]";
	}
	
	
}
