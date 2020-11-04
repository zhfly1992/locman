/*
 * File name: OpenRecordQueryDto.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年7月24日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.dto;

import java.io.Serializable;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2018年7月24日
 */

public class OpenRecordQueryDto implements Serializable {

	private static final long	serialVersionUID	= 7904729445155821481L;
	/** 设施序列号 */
	private String				facilitiesCode;
	/** 设施地址 */
	private String				address;
	/** 设备id */
	private String				deviceId;
	/** 设施类型 */
	private String				facilityTypeAlias;
	/** 开启时间 */
	private String				controlTime;

	/** 开启说明，备注 */
	private String				reason;

	/** 开启人员姓名 */
	private String				operateUserName;

	/** 命令下发销毁时间 */
	private String				controlDestroyTime;



	/**
	 * 
	 */
	public OpenRecordQueryDto() {
		super();
	}



	/**
	 * @param facilitiesCode
	 * @param address
	 * @param deviceId
	 * @param facilityTypeAlias
	 * @param controlTime
	 * @param reason
	 * @param operateUserName
	 * @param controlDestroyTime
	 */
	public OpenRecordQueryDto(String facilitiesCode, String address, String deviceId, String facilityTypeAlias,
			String controlTime, String reason, String operateUserName, String controlDestroyTime) {
		super();
		this.facilitiesCode = facilitiesCode;
		this.address = address;
		this.deviceId = deviceId;
		this.facilityTypeAlias = facilityTypeAlias;
		this.controlTime = controlTime;
		this.reason = reason;
		this.operateUserName = operateUserName;
		this.controlDestroyTime = controlDestroyTime;
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
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}



	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



	/**
	 * @return the facilityTypeAlias
	 */
	public String getFacilityTypeAlias() {
		return facilityTypeAlias;
	}



	/**
	 * @param facilityTypeAlias
	 *            the facilityTypeAlias to set
	 */
	public void setFacilityTypeAlias(String facilityTypeAlias) {
		this.facilityTypeAlias = facilityTypeAlias;
	}



	/**
	 * @return the controlTime
	 */
	public String getControlTime() {
		return controlTime;
	}



	/**
	 * @param controlTime
	 *            the controlTime to set
	 */
	public void setControlTime(String controlTime) {
		this.controlTime = controlTime;
	}



	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}



	/**
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}



	/**
	 * @return the operateUserName
	 */
	public String getOperateUserName() {
		return operateUserName;
	}



	/**
	 * @param operateUserName
	 *            the operateUserName to set
	 */
	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}



	/**
	 * @return the controlDestroyTime
	 */
	public String getControlDestroyTime() {
		return controlDestroyTime;
	}



	/**
	 * @param controlDestroyTime
	 *            the controlDestroyTime to set
	 */
	public void setControlDestroyTime(String controlDestroyTime) {
		this.controlDestroyTime = controlDestroyTime;
	}

}
