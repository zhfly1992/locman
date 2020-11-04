/*
* File name: FocusSecurity.java								
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
* 1.0			钟滨远		2020年4月26日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

/**
* @Description:	保障项目实体类
* @author: 钟滨远
* @version: 1.0, 2020年4月26日
*/

public class FocusSecurity {
	private String id;
	private String securityName;
	private String startTime;
	private String endTime;
	private String createTime;
	private String userId;
	private String organization;
	private String status;
	private String previewTime;
	private String accessSecret;
	private String personName;
	private String personTel;
	
	
	/**
	 * @return the personTel
	 */
	public String getPersonTel() {
		return personTel;
	}
	/**
	 * @param personTel the personTel to set
	 */
	public void setPersonTel(String personTel) {
		this.personTel = personTel;
	}
	/**
	 * @return the personId
	 */
	public String getPersonName() {
		return personName;
	}
	/**
	 * @param personId the personId to set
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
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
	 * @return the previewTime
	 */
	public String getPreviewTime() {
		return previewTime;
	}
	/**
	 * @param previewTime the previewTime to set
	 */
	public void setPreviewTime(String previewTime) {
		this.previewTime = previewTime;
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
	 * @return the securityName
	 */
	public String getSecurityName() {
		return securityName;
	}
	/**
	 * @param securityName the securityName to set
	 */
	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}
	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
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
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the organization
	 */
	public String getOrganization() {
		return organization;
	}
	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	

}
