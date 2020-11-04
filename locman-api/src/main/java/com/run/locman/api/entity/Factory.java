package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @author
 */
public class Factory implements Serializable {
	private String				id;

	/**
	 * 厂家名称
	 */
	private String				factoryName;

	/**
	 * 联系人
	 */
	private String				contacts;

	/**
	 * 联系电话
	 */
	private String				contactsPhone;

	/**
	 * 地址
	 */
	private String				address;

	/**
	 * 备注
	 */
	private String				remark;

	/**
	 * 管理状态
	 */
	private String				manageState;
	/**
	 * 接入方密钥
	 */
	private String				accessSecret;

	private static final long	serialVersionUID	= 1L;



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getFactoryName() {
		return factoryName;
	}



	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}



	public String getContacts() {
		return contacts;
	}



	public void setContacts(String contacts) {
		this.contacts = contacts;
	}



	public String getContactsPhone() {
		return contactsPhone;
	}



	public void setContactsPhone(String contactsPhone) {
		this.contactsPhone = contactsPhone;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getRemark() {
		return remark;
	}



	public void setRemark(String remark) {
		this.remark = remark;
	}



	public String getAccessSecret() {
		return accessSecret;
	}



	public String getManageState() {
		return manageState;
	}



	public void setManageState(String manageState) {
		this.manageState = manageState;
	}



	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Factory [id=" + id + ", factoryName=" + factoryName + ", contacts=" + contacts + ", contactsPhone="
				+ contactsPhone + ", address=" + address + ", remark=" + remark + ", manageState=" + manageState
				+ ", accessSecret=" + accessSecret + "]";
	}
	
}