/*
 * File name: FacilitiesImport.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月21日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:设施信息导入
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月21日
 */

public class FacilitiesImport implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4842874002634910329L;

	private String				id;

	/**
	 * 设施序列号
	 */
	private String				facilitiesCode;

	/**
	 * 设施类型
	 */
	private String				facilitiesType;

	/**
	 * 经度
	 */
	private String				longitude;

	/**
	 * 纬度
	 */
	private String				latitude;

	/**
	 * 地址
	 */
	private String				address;

	/**
	 * 设备编号->用于调用网关接口查询设备id
	 */
	private String				deviceCode;



	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}



	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}



	/**
	 * @return the facilitiesCode
	 */
	public String getFacilitiesCode() {
		return facilitiesCode;
	}



	/**
	 * @param facilitiesCode
	 *            the facilitiesCode to set
	 */
	public void setFacilitiesCode(String facilitiesCode) {
		this.facilitiesCode = facilitiesCode;
	}



	/**
	 * @return the facilitiesType
	 */
	public String getFacilitiesType() {
		return facilitiesType;
	}



	/**
	 * @param facilitiesType
	 *            the facilitiesType to set
	 */
	public void setFacilitiesType(String facilitiesType) {
		this.facilitiesType = facilitiesType;
	}



	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}



	/**
	 * @param longitude
	 *            the longitude to set
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
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}



	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}



	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}



	/**
	 * @return the deviceCode
	 */
	public String getDeviceCode() {
		return deviceCode;
	}



	/**
	 * @param deviceCode
	 *            the deviceCode to set
	 */
	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

}
