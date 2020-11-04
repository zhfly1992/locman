/*
* File name: ManholeCoverSwitch.java								
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
* 1.0			zhongbinyuan		2019年12月13日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

/**
* @Description:	成华区，井盖开启关闭记录表实体类
* @author: zhongbinyuan
* @version: 1.0, 2019年12月13日
*/

public class ManholeCoverSwitch {
	
	private static final long	serialVersionUID	= 1L;
	
	private String id;
	private String deviceId;
	private String openTime;
	private String closeTime;
	private String differenceTime;
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
	 * @return the openTime
	 */
	public String getOpenTime() {
		return openTime;
	}
	/**
	 * @param openTime the openTime to set
	 */
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	/**
	 * @return the closeTime
	 */
	public String getCloseTime() {
		return closeTime;
	}
	/**
	 * @param closeTime the closeTime to set
	 */
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}
	/**
	 * @return the differenceTime
	 */
	public String getDifferenceTime() {
		return differenceTime;
	}
	/**
	 * @param differenceTime the differenceTime to set
	 */
	public void setDifferenceTime(String differenceTime) {
		this.differenceTime = differenceTime;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
