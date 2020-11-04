package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @author
 */
public class FacilityDevice implements Serializable {
	private String				id;

	/**
	 * 设备ID
	 */
	private String				deviceId;

	/**
	 * 设施ID
	 */
	private String				facilityId;

	private String				deviceTypeId;

	private static final long	serialVersionUID	= 1L;



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getDeviceId() {
		return deviceId;
	}



	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



	public String getFacilityId() {
		return facilityId;
	}



	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}



	/**
	 * @return the deviceTypeId
	 */
	public String getDeviceTypeId() {
		return deviceTypeId;
	}



	/**
	 * @param deviceTypeId
	 *            the deviceTypeId to set
	 */
	public void setDeviceTypeId(String deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}

}