/*
 * File name: DistributionPowers.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月14日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 分权分域配置实体
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */

public class DistributionPowers implements Serializable {
	private static final long	serialVersionUID	= 4592573252230368211L;
	private String				id;
	private String				facilityTypeId;
	private String				facilityTypeName;
	private String				startTime;
	private String				endTime;
	private String				remark;
	private String				accessSecret;
	private String				manageState;
	private String				hour;
	private String				minute;
	private String				powerName;
	private String				powerState;
	private String				userInfo;
	private String				orgName;
	private String				userName;



	/**
	 * 
	 */
	public DistributionPowers() {
		super();
	}



	/**
	 * @param id
	 * @param facilityTypeId
	 * @param facilityTypeName
	 * @param startTime
	 * @param endTime
	 * @param remark
	 * @param accessSecret
	 * @param manageState
	 * @param hour
	 * @param minute
	 * @param powerName
	 * @param powerState
	 * @param userInfo
	 * @param orgName
	 * @param userName
	 */
	public DistributionPowers(String id, String facilityTypeId, String facilityTypeName, String startTime,
			String endTime, String remark, String accessSecret, String manageState, String hour, String minute,
			String powerName, String powerState, String userInfo, String orgName, String userName) {
		super();
		this.id = id;
		this.facilityTypeId = facilityTypeId;
		this.facilityTypeName = facilityTypeName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.remark = remark;
		this.accessSecret = accessSecret;
		this.manageState = manageState;
		this.hour = hour;
		this.minute = minute;
		this.powerName = powerName;
		this.powerState = powerState;
		this.userInfo = userInfo;
		this.orgName = orgName;
		this.userName = userName;
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
	 * @return the facilityTypeId
	 */
	public String getFacilityTypeId() {
		return facilityTypeId;
	}



	/**
	 * @param facilityTypeId
	 *            the facilityTypeId to set
	 */
	public void setFacilityTypeId(String facilityTypeId) {
		this.facilityTypeId = facilityTypeId;
	}



	/**
	 * @return the facilityTypeName
	 */
	public String getFacilityTypeName() {
		return facilityTypeName;
	}



	/**
	 * @param facilityTypeName
	 *            the facilityTypeName to set
	 */
	public void setFacilityTypeName(String facilityTypeName) {
		this.facilityTypeName = facilityTypeName;
	}



	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}



	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}



	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}



	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}



	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}



	/**
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
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
	 * @return the manageState
	 */
	public String getManageState() {
		return manageState;
	}



	/**
	 * @param manageState
	 *            the manageState to set
	 */
	public void setManageState(String manageState) {
		this.manageState = manageState;
	}



	/**
	 * @return the hour
	 */
	public String getHour() {
		return hour;
	}



	/**
	 * @param hour
	 *            the hour to set
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}



	/**
	 * @return the minute
	 */
	public String getMinute() {
		return minute;
	}



	/**
	 * @param minute
	 *            the minute to set
	 */
	public void setMinute(String minute) {
		this.minute = minute;
	}



	/**
	 * @return the powerName
	 */
	public String getPowerName() {
		return powerName;
	}



	/**
	 * @param powerName
	 *            the powerName to set
	 */
	public void setPowerName(String powerName) {
		this.powerName = powerName;
	}



	/**
	 * @return the powerState
	 */
	public String getPowerState() {
		return powerState;
	}



	/**
	 * @param powerState
	 *            the powerState to set
	 */
	public void setPowerState(String powerState) {
		this.powerState = powerState;
	}



	/**
	 * @return the userInfo
	 */
	public String getUserInfo() {
		return userInfo;
	}



	/**
	 * @param userInfo
	 *            the userInfo to set
	 */
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}



	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}



	/**
	 * @param orgName
	 *            the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}



	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}



	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DistributionPowers [id=" + id + ", facilityTypeId=" + facilityTypeId + ", facilityTypeName="
				+ facilityTypeName + ", startTime=" + startTime + ", endTime=" + endTime + ", remark=" + remark
				+ ", accessSecret=" + accessSecret + ", manageState=" + manageState + ", hour=" + hour + ", minute="
				+ minute + ", powerName=" + powerName + ", powerState=" + powerState + ", userInfo=" + userInfo
				+ ", orgName=" + orgName + ", userName=" + userName + "]";
	}

}
