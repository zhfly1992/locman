/*
* File name: FocusSecurityAndFac.java								
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
* 1.0			钟滨远		2020年4月27日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年4月27日
*/

public class FocusSecurityAndFac {
	private String id;
	private String securityId;
	private String facilityId;
	private String IotReceivingStatus;
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
	 * @return the securityId
	 */
	public String getSecurityId() {
		return securityId;
	}
	/**
	 * @param securityId the securityId to set
	 */
	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}
	/**
	 * @return the facilityId
	 */
	public String getFacilityId() {
		return facilityId;
	}
	/**
	 * @param facilityId the facilityId to set
	 */
	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	/**
	 * @return the iotReceivingStatus
	 */
	public String getIotReceivingStatus() {
		return IotReceivingStatus;
	}
	/**
	 * @param iotReceivingStatus the iotReceivingStatus to set
	 */
	public void setIotReceivingStatus(String iotReceivingStatus) {
		IotReceivingStatus = iotReceivingStatus;
	}
	

}
