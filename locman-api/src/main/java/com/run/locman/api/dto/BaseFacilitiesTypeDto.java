/*
* File name: BaseFacilitiesType.java								
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
* 1.0			guofeilong		2018年4月23日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年4月23日
*/

public class BaseFacilitiesTypeDto {
	
	/** 主键id */
	private String						id;
	/** 设施基础类型id */
	private String		facilityTypeBaseId;
	/** 设施类型名称 */
	private String		facilityTypeAlias;
	/** 接入方密钥 */
	private String		accessSecret;
	/** 设施类型管理状态 */
	private String		manageState;
	/** 创建人id */
	private String		creationUserId;
	/** 创建时间 */
	private String		creationTime;
	/** 修改人id */
	private String		editorUserId;
	/** 修改时间 */
	private String		editorTime;
	/** 备注 */
	private String		remark;
	/**
	 * 
	 */
	public BaseFacilitiesTypeDto() {
		super();
		
	}
	/**
	 * @param id
	 * @param facilityTypeBaseId
	 * @param facilityTypeAlias
	 * @param accessSecret
	 * @param manageState
	 * @param creationUserId
	 * @param creationTime
	 * @param editorUserId
	 * @param editorTime
	 * @param remark
	 */
	public BaseFacilitiesTypeDto(String id, String facilityTypeBaseId, String facilityTypeAlias, String accessSecret,
			String manageState, String creationUserId, String creationTime, String editorUserId, String editorTime,
			String remark) {
		super();
		this.id = id;
		this.facilityTypeBaseId = facilityTypeBaseId;
		this.facilityTypeAlias = facilityTypeAlias;
		this.accessSecret = accessSecret;
		this.manageState = manageState;
		this.creationUserId = creationUserId;
		this.creationTime = creationTime;
		this.editorUserId = editorUserId;
		this.editorTime = editorTime;
		this.remark = remark;
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
	 * @return the facilityTypeBaseId
	 */
	public String getFacilityTypeBaseId() {
		return facilityTypeBaseId;
	}
	/**
	 * @param facilityTypeBaseId the facilityTypeBaseId to set
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
	 * @param facilityTypeAlias the facilityTypeAlias to set
	 */
	public void setFacilityTypeAlias(String facilityTypeAlias) {
		this.facilityTypeAlias = facilityTypeAlias;
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
	 * @return the manageState
	 */
	public String getManageState() {
		return manageState;
	}
	/**
	 * @param manageState the manageState to set
	 */
	public void setManageState(String manageState) {
		this.manageState = manageState;
	}
	/**
	 * @return the creationUserId
	 */
	public String getCreationUserId() {
		return creationUserId;
	}
	/**
	 * @param creationUserId the creationUserId to set
	 */
	public void setCreationUserId(String creationUserId) {
		this.creationUserId = creationUserId;
	}
	/**
	 * @return the creationTime
	 */
	public String getCreationTime() {
		return creationTime;
	}
	/**
	 * @param creationTime the creationTime to set
	 */
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	/**
	 * @return the editorUserId
	 */
	public String getEditorUserId() {
		return editorUserId;
	}
	/**
	 * @param editorUserId the editorUserId to set
	 */
	public void setEditorUserId(String editorUserId) {
		this.editorUserId = editorUserId;
	}
	/**
	 * @return the editorTime
	 */
	public String getEditorTime() {
		return editorTime;
	}
	/**
	 * @param editorTime the editorTime to set
	 */
	public void setEditorTime(String editorTime) {
		this.editorTime = editorTime;
	}
	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseFacilitiesTypeDto [id=" + id + ", facilityTypeBaseId=" + facilityTypeBaseId + ", facilityTypeAlias="
				+ facilityTypeAlias + ", accessSecret=" + accessSecret + ", manageState=" + manageState
				+ ", creationUserId=" + creationUserId + ", creationTime=" + creationTime + ", editorUserId="
				+ editorUserId + ", editorTime=" + editorTime + ", remark=" + remark + "]";
	}

	
	
}
