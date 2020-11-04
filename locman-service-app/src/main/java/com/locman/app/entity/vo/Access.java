package com.locman.app.entity.vo;

public class Access {

	private String	accessId;
	private String	accessTenementName;	// 租户
	private String	accessName;			// 接入方名称
	private String	accessTenementId;
	private String	accessSecret;
	private String	ipAddress;			// 设备上报地址和端口



	public String getAccessId() {
		return accessId;
	}



	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}



	public String getAccessTenementName() {
		return accessTenementName;
	}



	public void setAccessTenementName(String accessTenementName) {
		this.accessTenementName = accessTenementName;
	}



	public String getAccessName() {
		return accessName;
	}



	public void setAccessName(String accessName) {
		this.accessName = accessName;
	}



	public String getAccessTenementId() {
		return accessTenementId;
	}



	public void setAccessTenementId(String accessTenementId) {
		this.accessTenementId = accessTenementId;
	}



	public String getAccessSecret() {
		return accessSecret;
	}



	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}



	public String getIpAddress() {
		return ipAddress;
	}



	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
