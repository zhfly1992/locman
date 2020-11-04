/*
 * File name: BalanceSwitchPowers.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月4日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月4日
 */

public class BalanceSwitchPowers implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String				id;
	private String				facilityTypeId;
	private String				organizationId;
	private String				postId;
	private String				staffType;
	private String				startTime;
	private String				endTime;
	private String				remark;
	private String				accessSecret;
	private String				manageState;
	private String				facilityTypeName;
	private String				organizationName;
	private String				postName;



	/**
	 * 
	 */
	public BalanceSwitchPowers() {
		super();
	}



	/**
	 * @param id
	 * @param facilityTypeId
	 * @param organizationId
	 * @param postId
	 * @param staffType
	 * @param startTime
	 * @param endTime
	 * @param remark
	 * @param accessSecret
	 * @param manageState
	 * @param facilityTypeName
	 * @param organizationName
	 * @param postName
	 */
	public BalanceSwitchPowers(String id, String facilityTypeId, String organizationId, String postId, String staffType,
			String startTime, String endTime, String remark, String accessSecret, String manageState,
			String facilityTypeName, String organizationName, String postName) {
		super();
		this.id = id;
		this.facilityTypeId = facilityTypeId;
		this.organizationId = organizationId;
		this.postId = postId;
		this.staffType = staffType;
		this.startTime = startTime;
		this.endTime = endTime;
		this.remark = remark;
		this.accessSecret = accessSecret;
		this.manageState = manageState;
		this.facilityTypeName = facilityTypeName;
		this.organizationName = organizationName;
		this.postName = postName;
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
	 * @return the organizationName
	 */
	public String getOrganizationName() {
		return organizationName;
	}



	/**
	 * @param organizationName
	 *            the organizationName to set
	 */
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}



	/**
	 * @return the postName
	 */
	public String getPostName() {
		return postName;
	}



	/**
	 * @param postName
	 *            the postName to set
	 */
	public void setPostName(String postName) {
		this.postName = postName;
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
	 * @return the organizationId
	 */
	public String getOrganizationId() {
		return organizationId;
	}



	/**
	 * @param organizationId
	 *            the organizationId to set
	 */
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}



	/**
	 * @return the postId
	 */
	public String getPostId() {
		return postId;
	}



	/**
	 * @param postId
	 *            the postId to set
	 */
	public void setPostId(String postId) {
		this.postId = postId;
	}



	/**
	 * @return the staffType
	 */
	public String getStaffType() {
		return staffType;
	}



	/**
	 * @param staffType
	 *            the staffType to set
	 */
	public void setStaffType(String staffType) {
		this.staffType = staffType;
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BalanceSwitchPowers [id=" + id + ", facilityTypeId=" + facilityTypeId + ", organizationId="
				+ organizationId + ", postId=" + postId + ", staffType=" + staffType + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", remark=" + remark + ", accessSecret=" + accessSecret + ", manageState="
				+ manageState + ", facilityTypeName=" + facilityTypeName + ", organizationName=" + organizationName
				+ ", postName=" + postName + "]";
	}

}
