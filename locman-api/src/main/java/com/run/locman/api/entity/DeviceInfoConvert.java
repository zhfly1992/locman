/*
* File name: DeviceInfoConvert.java								
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
* 1.0			Administrator		2018年3月28日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 设备信息转换实体类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月28日
 */

public class DeviceInfoConvert implements Serializable {

	private static final long serialVersionUID = -2208786713166516831L;

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 英文key值
	 */
	private String dicKey;

	/**
	 * 汉语value
	 */
	private String dicValue;

	/**
	 * 创建时间
	 */
	private String createTime;

	/**
	 * 更新时间
	 */
	private String updateTime;

	/**
	 * 接入方密钥
	 */
	private String accessSecret;

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
	 * @return the dicKey
	 */
	public String getDicKey() {
		return dicKey;
	}

	/**
	 * @param dicKey
	 *            the dicKey to set
	 */
	public void setDicKey(String dicKey) {
		this.dicKey = dicKey;
	}

	/**
	 * @return the dicValue
	 */
	public String getDicValue() {
		return dicValue;
	}

	/**
	 * @param dicValue
	 *            the dicValue to set
	 */
	public void setDicValue(String dicValue) {
		this.dicValue = dicValue;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceInfoConvert [id=" + id + ", dicKey=" + dicKey + ", dicValue=" + dicValue + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", accessSecret=" + accessSecret + "]";
	}

}
