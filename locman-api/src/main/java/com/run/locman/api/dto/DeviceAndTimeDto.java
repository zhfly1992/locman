/*
* File name: DeviceAndTimeDto.java								
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
* 1.0			guofeilong		2018年6月28日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月28日
*/

public class DeviceAndTimeDto {
	
	/**
	 * 设备id
	 */
	private String device;
	/**
	 * 配置的时间
	 */
	private int configTime;
	/**
	 * 
	 */
	public DeviceAndTimeDto() {
		super();
		
	}
	/**
	 * @param device
	 * @param configTime
	 */
	public DeviceAndTimeDto(String device, int configTime) {
		super();
		this.device = device;
		this.configTime = configTime;
	}
	/**
	 * @return the device
	 */
	public String getDevice() {
		return device;
	}
	/**
	 * @param device the device to set
	 */
	public void setDevice(String device) {
		this.device = device;
	}
	/**
	 * @return the configTime
	 */
	public int getConfigTime() {
		return configTime;
	}
	/**
	 * @param configTime the configTime to set
	 */
	public void setConfigTime(int configTime) {
		this.configTime = configTime;
	}

	
}
