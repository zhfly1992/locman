/*
 * File name: DeviceStateHistory.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2018年1月15日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 设备历史数据实体类
 * @author: qulong
 * @version: 1.0, 2018年1月15日
 */

public class DeviceStateHistory implements Serializable {

	private static final long	serialVersionUID	= 1376454152039376013L;

	private String				id;
	/**
	 * 设备id
	 */
	private String				deviceId;
	/**
	 * 上报数据（json）
	 */
	private String				datas;
	/**
	 * 上报时间
	 */
	private String				reportTime;



	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}



	/**
	 * @param id
	 *            the id to set
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
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



	/**
	 * @return the datas
	 */
	public String getDatas() {
		return datas;
	}



	/**
	 * @param datas
	 *            the datas to set
	 */
	public void setDatas(String datas) {
		this.datas = datas;
	}



	/**
	 * @return the reportTime
	 */
	public String getReportTime() {
		return reportTime;
	}



	/**
	 * @param reportTime
	 *            the reportTime to set
	 */
	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceStateHistory [id=" + id + ", deviceId=" + deviceId + ", datas=" + datas + ", reportTime="
				+ reportTime + "]";
	}

}
