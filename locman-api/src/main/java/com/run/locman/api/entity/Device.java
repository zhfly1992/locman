/*
 * File name: Device.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2018年2月1日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 设备基础数据实体类
 * @author: qulong
 * @version: 1.0, 2018年2月1日
 */

public class Device implements Serializable {

	private static final long	serialVersionUID	= 6919977879880070854L;

	/**
	 * 设备id
	 */
	private String				id;
	/**
	 * 设备名称
	 */
	private String				deviceName;
	/**
	 * 设备秘钥
	 */
	private String				deviceKey;
	/**
	 * 协议类型
	 */
	private String				protocolType;
	/**
	 * 开放/私有协议
	 */
	private String				openProtocols;
	/**
	 * 设备类型id
	 */
	private String				deviceType;
	/**
	 * 首次上线时间
	 */
	private String				firstOnlineTime;
	/**
	 * 最新上报时间
	 */
	private String				lastReportTime;
	/**
	 * 在线状态：在线、离线、未知
	 */
	private String				onlineState;
	/**
	 * 设备所属appTag
	 */
	private String				appTag;
	/**
	 * 设备管理状态
	 */
	private String				manageState;
	/**
	 * 接入方秘钥
	 */
	private String				accessSecret;
	/**
	 * 网关id
	 */
	private String				gatewayId;
	/**
	 * 设备硬件编码
	 */
	private String				hardwareId;
	/**
	 * 设备维护状态
	 */
	private String				deviceDefendState;
	/**
	 * 子设备id
	 */
	private String				subDeviceId			= "";



	/**
	 * @return the subDeviceId
	 */
	public String getSubDeviceId() {
		return subDeviceId;
	}



	/**
	 * @param subDeviceId
	 *            the subDeviceId to set
	 */
	public void setSubDeviceId(String subDeviceId) {
		this.subDeviceId = subDeviceId;
	}



	/**
	 * @return the deviceDefendState
	 */
	public String getDeviceDefendState() {
		return deviceDefendState;
	}



	/**
	 * @param deviceDefendState
	 *            the deviceDefendState to set
	 */
	public void setDeviceDefendState(String deviceDefendState) {
		this.deviceDefendState = deviceDefendState;
	}



	/**
	 * @return the hardwareId
	 */
	public String getHardwareId() {
		return hardwareId;
	}



	/**
	 * @param hardwareId
	 *            the hardwareId to set
	 */
	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}



	/**
	 * @return the gatewayId
	 */
	public String getGatewayId() {
		return gatewayId;
	}



	/**
	 * @param gatewayId
	 *            the gatewayId to set
	 */
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}



	/**
	 * @return the accessSecret
	 */
	public String getAccessSecret() {
		return accessSecret;
	}



	/**
	 * @param accessSecret
	 *            the accessSecret to set
	 */
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}



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
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}



	/**
	 * @param deviceName
	 *            the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}



	/**
	 * @return the deviceKey
	 */
	public String getDeviceKey() {
		return deviceKey;
	}



	/**
	 * @param deviceKey
	 *            the deviceKey to set
	 */
	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}



	/**
	 * @return the protocolType
	 */
	public String getProtocolType() {
		return protocolType;
	}



	/**
	 * @param protocolType
	 *            the protocolType to set
	 */
	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}



	/**
	 * @return the openProtocols
	 */
	public String getOpenProtocols() {
		return openProtocols;
	}



	/**
	 * @param openProtocols
	 *            the openProtocols to set
	 */
	public void setOpenProtocols(String openProtocols) {
		this.openProtocols = openProtocols;
	}



	/**
	 * @return the deviceType
	 */
	public String getDeviceType() {
		return deviceType;
	}



	/**
	 * @param deviceType
	 *            the deviceType to set
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}



	/**
	 * @return the firstOnlineTime
	 */
	public String getFirstOnlineTime() {
		return firstOnlineTime;
	}



	/**
	 * @param firstOnlineTime
	 *            the firstOnlineTime to set
	 */
	public void setFirstOnlineTime(String firstOnlineTime) {
		this.firstOnlineTime = firstOnlineTime;
	}



	/**
	 * @return the lastReportTime
	 */
	public String getLastReportTime() {
		return lastReportTime;
	}



	/**
	 * @param lastReportTime
	 *            the lastReportTime to set
	 */
	public void setLastReportTime(String lastReportTime) {
		this.lastReportTime = lastReportTime;
	}



	/**
	 * @return the onlineState
	 */
	public String getOnlineState() {
		return onlineState;
	}



	/**
	 * @param onlineState
	 *            the onlineState to set
	 */
	public void setOnlineState(String onlineState) {
		this.onlineState = onlineState;
	}



	/**
	 * @return the appTag
	 */
	public String getAppTag() {
		return appTag;
	}



	/**
	 * @param appTag
	 *            the appTag to set
	 */
	public void setAppTag(String appTag) {
		this.appTag = appTag;
	}



	/**
	 * @return the manageState
	 */
	public String getManageState() {
		return manageState;
	}



	/**
	 * @param manageState
	 *            the manageState to set
	 */
	public void setManageState(String manageState) {
		this.manageState = manageState;
	}

}
