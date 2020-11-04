package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 设施类型实体
 * @author: wangsheng
 * @version: 1.0, 2017年8月8日
 */
public class FacilitiesType implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String				id;
	private String				facilityTypeBaseId;
	private String				facilityTypeAlias;
	private String				accessSecret;
	private String				manageState;
	private String				creationUserId;
	private String				creationTime;
	private String				editorUserId;
	private String				editorTime;
	private FacilitiesTypeBase	facilitiesTypeBase;
	private String				remark;



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



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	/**
	 * @return the facilityTypeBaseId
	 */
	public String getFacilityTypeBaseId() {
		return facilityTypeBaseId;
	}



	/**
	 * @param facilityTypeBaseId
	 *            the facilityTypeBaseId to set
	 */
	public void setFacilityTypeBaseId(String facilityTypeBaseId) {
		this.facilityTypeBaseId = facilityTypeBaseId;
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



	public String getAccessSecret() {
		return accessSecret;
	}



	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}



	/**
	 * @return the facilitiesTypeBase
	 */
	public FacilitiesTypeBase getFacilitiesTypeBase() {
		return facilitiesTypeBase;
	}



	/**
	 * @param facilitiesTypeBase
	 *            the facilitiesTypeBase to set
	 */
	public void setFacilitiesTypeBase(FacilitiesTypeBase facilitiesTypeBase) {
		this.facilitiesTypeBase = facilitiesTypeBase;
	}



	public String getManageState() {
		return manageState;
	}



	public void setManageState(String manageState) {
		this.manageState = manageState;
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



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FacilitiesType [id=" + id + ", facilityTypeBaseId=" + facilityTypeBaseId + ", facilityTypeAlias="
				+ facilityTypeAlias + ", accessSecret=" + accessSecret + ", manageState=" + manageState
				+ ", creationUserId=" + creationUserId + ", creationTime=" + creationTime + ", editorUserId="
				+ editorUserId + ", editorTime=" + editorTime + ", facilitiesTypeBase=" + facilitiesTypeBase
				+ ", remark=" + remark + "]";
	}
	
}