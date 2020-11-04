/*
* File name: Inspection.java								
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
* 1.0			zhongbinyuan		2020年2月28日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

/**
* @Description:	
* @author: zhongbinyuan
* @version: 1.0, 2020年2月28日
*/

public class Inspection {
	
	private static final long	serialVersionUID	= 1L;
	private String id;
	private String deviceId;
	private String reportedTime;
	private String inspectionTime;
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
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the reportedTime
	 */
	public String getReportedTime() {
		return reportedTime;
	}
	/**
	 * @param reportedTime the reportedTime to set
	 */
	public void setReportedTime(String reportedTime) {
		this.reportedTime = reportedTime;
	}
	/**
	 * @return the inspectionTime
	 */
	public String getInspectionTime() {
		return inspectionTime;
	}
	/**
	 * @param inspectionTime the inspectionTime to set
	 */
	public void setInspectionTime(String inspectionTime) {
		this.inspectionTime = inspectionTime;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
