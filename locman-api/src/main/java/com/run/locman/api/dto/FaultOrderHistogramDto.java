/*
* File name: FaultOrderHistogramDto.java								
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
* 1.0			guofeilong		2018年7月26日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

import java.io.Serializable;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年7月26日
*/

public class FaultOrderHistogramDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1845780835963516535L;
	/** 区域 */
	private String	areaName;
	/** 区域码*/
	private String	areaCode;
	
	/** 故障类型*/
	
	/** 其他*/
	private String	other;
	/** 硬件锁体故障 */
	private String	hardwareFault;
	/** 硬件控制盒 */
	private String	hardwareControl;
	/** 外力破坏 */
	private String	destroy;
	/** 钥匙孔故障 */
	private String	keyholeFault;
	/** 设备采集*/
	private String	deviceCollect;
	/** APP应用*/
	private String	app;
	/** 平台 */
	private String	iot;
	/** 系统使用误操作 */
	private String	wrong;
	/** 超时未上报 */
	private String	timeoutReport;
	/**
	 * 
	 */
	public FaultOrderHistogramDto() {
		super();
		
	}
	/**
	 * @param areaName 区域
	 * @param areaCode 区域码
	 * @param other 其他
	 * @param hardwareFault 硬件锁体故障
	 * @param hardwareControl 硬件控制盒
	 * @param destroy 外力破坏 
	 * @param keyholeFault 钥匙孔故障
	 * @param deviceCollect 设备采集
	 * @param app APP应用
	 * @param iot 平台
	 * @param wrong 系统使用误操作
	 * @param timeoutReport 超时未上报 
	 */
	public FaultOrderHistogramDto(String areaName, String areaCode, String other, String hardwareFault,
			String hardwareControl, String destroy, String keyholeFault, String deviceCollect, String app, String iot,
			String wrong, String timeoutReport) {
		super();
		this.areaName = areaName;
		this.areaCode = areaCode;
		this.other = other;
		this.hardwareFault = hardwareFault;
		this.hardwareControl = hardwareControl;
		this.destroy = destroy;
		this.keyholeFault = keyholeFault;
		this.deviceCollect = deviceCollect;
		this.app = app;
		this.iot = iot;
		this.wrong = wrong;
		this.timeoutReport = timeoutReport;
	}
	/**
	 * @return the areaName
	 */
	public String getAreaName() {
		return areaName;
	}
	/**
	 * @param areaName the areaName to set
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	/**
	 * @return the areaCode
	 */
	public String getAreaCode() {
		return areaCode;
	}
	/**
	 * @param areaCode the areaCode to set
	 */
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	/**
	 * @return the other
	 */
	public String getOther() {
		return other;
	}
	/**
	 * @param other the other to set
	 */
	public void setOther(String other) {
		this.other = other;
	}
	/**
	 * @return the hardwareFault
	 */
	public String getHardwareFault() {
		return hardwareFault;
	}
	/**
	 * @param hardwareFault the hardwareFault to set
	 */
	public void setHardwareFault(String hardwareFault) {
		this.hardwareFault = hardwareFault;
	}
	/**
	 * @return the hardwareControl
	 */
	public String getHardwareControl() {
		return hardwareControl;
	}
	/**
	 * @param hardwareControl the hardwareControl to set
	 */
	public void setHardwareControl(String hardwareControl) {
		this.hardwareControl = hardwareControl;
	}
	/**
	 * @return the destroy
	 */
	public String getDestroy() {
		return destroy;
	}
	/**
	 * @param destroy the destroy to set
	 */
	public void setDestroy(String destroy) {
		this.destroy = destroy;
	}
	/**
	 * @return the keyholeFault
	 */
	public String getKeyholeFault() {
		return keyholeFault;
	}
	/**
	 * @param keyholeFault the keyholeFault to set
	 */
	public void setKeyholeFault(String keyholeFault) {
		this.keyholeFault = keyholeFault;
	}
	/**
	 * @return the deviceCollect
	 */
	public String getDeviceCollect() {
		return deviceCollect;
	}
	/**
	 * @param deviceCollect the deviceCollect to set
	 */
	public void setDeviceCollect(String deviceCollect) {
		this.deviceCollect = deviceCollect;
	}
	/**
	 * @return the app
	 */
	public String getApp() {
		return app;
	}
	/**
	 * @param app the app to set
	 */
	public void setApp(String app) {
		this.app = app;
	}
	/**
	 * @return the iot
	 */
	public String getIot() {
		return iot;
	}
	/**
	 * @param iot the iot to set
	 */
	public void setIot(String iot) {
		this.iot = iot;
	}
	/**
	 * @return the wrong
	 */
	public String getWrong() {
		return wrong;
	}
	/**
	 * @param wrong the wrong to set
	 */
	public void setWrong(String wrong) {
		this.wrong = wrong;
	}
	/**
	 * @return the timeoutReport
	 */
	public String getTimeoutReport() {
		return timeoutReport;
	}
	/**
	 * @param timeoutReport the timeoutReport to set
	 */
	public void setTimeoutReport(String timeoutReport) {
		this.timeoutReport = timeoutReport;
	}
	
  
	
}
