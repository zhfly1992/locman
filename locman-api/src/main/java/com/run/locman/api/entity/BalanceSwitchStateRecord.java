/*
 * File name: BalanceSwitchStateRecord.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月15日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:平衡告警开关开启关闭记录实体
 * @author: 王胜
 * @version: 1.0, 2018年5月15日
 */

public class BalanceSwitchStateRecord implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String				id;
	private String				deviceId;
	private String				facilityId;
	private String				deviceTypeId;
	private String				operationTime;
	private String				accessSecret;
	private String				state;



	/**
	 * 
	 */
	public BalanceSwitchStateRecord() {
		super();
	}



	/**
	 * @param id
	 * @param deviceId
	 * @param facilityId
	 * @param deviceTypeId
	 * @param operationTime
	 * @param accessSecret
	 * @param state
	 */
	public BalanceSwitchStateRecord(String id, String deviceId, String facilityId, String deviceTypeId,
			String operationTime, String accessSecret, String state) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.facilityId = facilityId;
		this.deviceTypeId = deviceTypeId;
		this.operationTime = operationTime;
		this.accessSecret = accessSecret;
		this.state = state;
	}



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
	 * @return the facilityId
	 */
	public String getFacilityId() {
		return facilityId;
	}



	/**
	 * @param facilityId
	 *            the facilityId to set
	 */
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



	/**
	 * @return the operationTime
	 */
	public String getOperationTime() {
		return operationTime;
	}



	/**
	 * @param operationTime
	 *            the operationTime to set
	 */
	public void setOperationTime(String operationTime) {
		this.operationTime = operationTime;
	}



	/**
	 * @return the accessSecret
	 */
	public String getAccessSecret() {
		return accessSecret;
	}



	/**
	 * @param accessSecret
	 *            the accessSecret to set
	 */
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}



	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}



	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BalanceSwitchStateRecord [id=" + id + ", deviceId=" + deviceId + ", facilityId=" + facilityId
				+ ", deviceTypeId=" + deviceTypeId + ", operationTime=" + operationTime + ", accessSecret="
				+ accessSecret + ", state=" + state + "]";
	}

}
