package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 设施实体
 * @author: wangsheng
 * @version: 1.0, 2017年8月8日
 */
public class Facilities implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String				id;
	private String				facilitiesCode;
	private String				facilitiesTypeId;
	private String				areaId;
	private String				longitude;
	private String				latitude;
	private String				address;
	private String				manageState;
	private String				accessSecret;
	private String				extend;
	private String				creationUserId;
	private String				creationTime;
	private String				editorUserId;
	private String				editorTime;
	private String				organizationId;
	private String				version;
	private String				defenseState = "1";



	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}



	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	private String	showExtend;
	private String	completeAddress;
	private String	facilityTypeAlias;
	private String	basicsFacType;
	private String	facilitiesType;
	private String	deviceId;
	private String	row;
	private String	deviceCode;



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



	/**
	 * @return the row
	 */
	public String getRow() {
		return row;
	}



	/**
	 * @param row
	 *            the row to set
	 */
	public void setRow(String row) {
		this.row = row;
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
	 * @return the basicsFacType
	 */
	public String getBasicsFacType() {
		return basicsFacType;
	}



	/**
	 * @param basicsFacType
	 *            the basicsFacType to set
	 */
	public void setBasicsFacType(String basicsFacType) {
		this.basicsFacType = basicsFacType;
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



	public String getOrganizationId() {
		return organizationId;
	}



	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getFacilitiesCode() {
		return facilitiesCode;
	}



	public void setFacilitiesCode(String facilitiesCode) {
		this.facilitiesCode = facilitiesCode;
	}



	public String getFacilitiesTypeId() {
		return facilitiesTypeId;
	}



	public void setFacilitiesTypeId(String facilitiesTypeId) {
		this.facilitiesTypeId = facilitiesTypeId;
	}



	public String getAreaId() {
		return areaId;
	}



	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}



	public String getLongitude() {
		return longitude;
	}



	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}



	public String getLatitude() {
		return latitude;
	}



	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getManageState() {
		return manageState;
	}



	public void setManageState(String manageState) {
		this.manageState = manageState;
	}



	public String getExtend() {
		return extend;
	}



	public void setExtend(String extend) {
		this.extend = extend;
	}



	public String getAccessSecret() {
		return accessSecret;
	}



	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}



	public String getCreationUserId() {
		return creationUserId;
	}



	public void setCreationUserId(String creationUserId) {
		this.creationUserId = creationUserId;
	}



	public String getCreationTime() {
		return creationTime;
	}



	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}



	public String getEditorUserId() {
		return editorUserId;
	}



	public void setEditorUserId(String editorUserId) {
		this.editorUserId = editorUserId;
	}



	public String getEditorTime() {
		return editorTime;
	}



	public void setEditorTime(String editorTime) {
		this.editorTime = editorTime;
	}



	public String getShowExtend() {
		return showExtend;
	}



	public void setShowExtend(String showExtend) {
		this.showExtend = showExtend;
	}



	public String getCompleteAddress() {
		return completeAddress;
	}



	public void setCompleteAddress(String completeAddress) {
		this.completeAddress = completeAddress;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Facilities [id=" + id + ", facilitiesCode=" + facilitiesCode + ", facilitiesTypeId=" + facilitiesTypeId
				+ ", areaId=" + areaId + ", longitude=" + longitude + ", latitude=" + latitude + ", address=" + address
				+ ", manageState=" + manageState + ", accessSecret=" + accessSecret + ", extend=" + extend
				+ ", creationUserId=" + creationUserId + ", creationTime=" + creationTime + ", editorUserId="
				+ editorUserId + ", editorTime=" + editorTime + ", organizationId=" + organizationId + ", showExtend="
				+ showExtend + ", completeAddress=" + completeAddress + ", facilityTypeAlias=" + facilityTypeAlias
				+ ", defenseState=" + defenseState + "]";
	}



	/**
	 * @return the defenseState
	 */
	public String getDefenseState() {
		return defenseState;
	}



	/**
	 * @param defenseState
	 *            the defenseState to set
	 */
	public void setDefenseState(String defenseState) {
		this.defenseState = defenseState;
	}
}