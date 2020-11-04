package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @author
 */
public class DeviceTypeTemplate implements Serializable {
	private String	id;

	/**
	 * 设备类型属性配置Id
	 */
	private String	deviceTypePropertyConfigId;

	/**
	 * 设备属性模板Id
	 */
	private String	devicePropertyTemplateId;
	/**
	 * 接入方秘钥
	 */
	private String	accessSecret;



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

	private static final long serialVersionUID = 1L;



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getDeviceTypePropertyConfigId() {
		return deviceTypePropertyConfigId;
	}



	public void setDeviceTypePropertyConfigId(String deviceTypePropertyConfigId) {
		this.deviceTypePropertyConfigId = deviceTypePropertyConfigId;
	}



	public String getDevicePropertyTemplateId() {
		return devicePropertyTemplateId;
	}



	public void setDevicePropertyTemplateId(String devicePropertyTemplateId) {
		this.devicePropertyTemplateId = devicePropertyTemplateId;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceTypeTemplate [id=" + id + ", deviceTypePropertyConfigId=" + deviceTypePropertyConfigId
				+ ", devicePropertyTemplateId=" + devicePropertyTemplateId + ", accessSecret=" + accessSecret + "]";
	}

	
}