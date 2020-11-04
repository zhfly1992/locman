/*
* File name: FacilityTimeoutReportConfig.java								
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
* 1.0			guofeilong		2018年6月25日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月25日
*/

public class FacilityTimeoutReportConfig {
	/**
	 * D
	 */
	private String				id;
	
	/**
	 * 设施ID
	 */
	private String				facilityId;
	/**
	 * 超时配置ID
	 */
	private String				timeoutReportConfigId;
	/**
	 * 
	 */
	public FacilityTimeoutReportConfig() {
		super();
		
	}
	/**
	 * @param id
	 * @param facilityId
	 * @param timeoutReportConfigId
	 */
	public FacilityTimeoutReportConfig(String id, String facilityId, String timeoutReportConfigId) {
		super();
		this.id = id;
		this.facilityId = facilityId;
		this.timeoutReportConfigId = timeoutReportConfigId;
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
	 * @return the timeoutReportConfigId
	 */
	public String getTimeoutReportConfigId() {
		return timeoutReportConfigId;
	}
	/**
	 * @param timeoutReportConfigId the timeoutReportConfigId to set
	 */
	public void setTimeoutReportConfigId(String timeoutReportConfigId) {
		this.timeoutReportConfigId = timeoutReportConfigId;
	}
	
	
}
