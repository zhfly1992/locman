/*
 * File name: DeviceType.java
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
 * @Description: 设备类型实体类
 * @author: qulong
 * @version: 1.0, 2018年2月1日
 */

public class DeviceType implements Serializable {

	private static final long	serialVersionUID	= -2725877555008959046L;

	/**
	 * 设备类型id
	 */
	private String				id;
	/**
	 * 设备类型名称
	 */
	private String				deviceTypeName;
	/**
	 * 父类型id
	 */
	private String				parentId;
	/**
	 * 创建时间
	 */
	private String				createTime;
	/**
	 * 修改时间
	 */
	private String				updateTime;
	/**
	 * 创建人
	 */
	private String				createBy;
	/**
	 * 修改人
	 */
	private String				updateBy;
	/**
	 * 接入方密钥
	 */
	private String				accessSecret;
	/**
	 * 设备类型标记 ---区分该设备类型是否属于 1.一体化智能监控终端2.智能监测终端（II型）
	 */
	private String				typeSign;



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
	 * @return the deviceTypeName
	 */
	public String getDeviceTypeName() {
		return deviceTypeName;
	}



	/**
	 * @param deviceTypeName
	 *            the deviceTypeName to set
	 */
	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}



	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}



	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}



	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}



	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}



	/**
	 * @return the updateTime
	 */
	public String getUpdateTime() {
		return updateTime;
	}



	/**
	 * @param updateTime
	 *            the updateTime to set
	 */
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}



	/**
	 * @return the createBy
	 */
	public String getCreateBy() {
		return createBy;
	}



	/**
	 * @param createBy
	 *            the createBy to set
	 */
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}



	/**
	 * @return the updateBy
	 */
	public String getUpdateBy() {
		return updateBy;
	}



	/**
	 * @param updateBy
	 *            the updateBy to set
	 */
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}



	/**
	 * @return the typeSign
	 */
	public String getTypeSign() {
		return typeSign;
	}



	/**
	 * @param typeSign
	 *            the typeSign to set
	 */
	public void setTypeSign(String typeSign) {
		this.typeSign = typeSign;
	}

}
