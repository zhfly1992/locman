/*
* File name: FacilitiesTypeBase.java								
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
* 1.0			qulong		2017年8月29日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
* @Description:	基础设施类型实体类
* @author: qulong
* @version: 1.0, 2017年8月29日
*/

public class FacilitiesTypeBase implements Serializable {

	private static final long serialVersionUID = -8002962805707140079L;
	
	private String id;
	private String facilityTypeName;
	private String facilityTypeIco;
	private String manageState;
	private String createTime;
	private String createUser;
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
	 * @return the facilityTypeName
	 */
	public String getFacilityTypeName() {
		return facilityTypeName;
	}
	/**
	 * @param facilityTypeName the facilityTypeName to set
	 */
	public void setFacilityTypeName(String facilityTypeName) {
		this.facilityTypeName = facilityTypeName;
	}
	/**
	 * @return the facilityTypeIco
	 */
	public String getFacilityTypeIco() {
		return facilityTypeIco;
	}
	/**
	 * @param facilityTypeIco the facilityTypeIco to set
	 */
	public void setFacilityTypeIco(String facilityTypeIco) {
		this.facilityTypeIco = facilityTypeIco;
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
	 * @return the createUser
	 */
	public String getCreateUser() {
		return createUser;
	}
	/**
	 * @param createUser the createUser to set
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FacilitiesTypeBase [id=" + id + ", facilityTypeName=" + facilityTypeName + ", facilityTypeIco="
				+ facilityTypeIco + ", manageState=" + manageState + ", createTime=" + createTime + ", createUser="
				+ createUser + "]";
	}
	
	
	
}
