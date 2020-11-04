/*
 * File name: RemoteControlRecord.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月7日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:远程控制记录
 * @author: qulong
 * @version: 1.0, 2017年12月7日
 */

public class RemoteControlRecord implements Serializable {

	private static final long	serialVersionUID	= -3109803966309243707L;

	/** 主键id */
	private String				id;

	/** 设备id */
	private String				deviceId;

	/** 远控项 */
	private String				controlItem;

	/** 远控值 */
	private String				controlValue;

	/** 控制时间 */
	private String				controlTime;

	/** 命令发起人ID */
	private String				controlUserId;

	/** 操作人姓名 */
	private String				operateUserName;

	/** 操作人电话 */
	private String				operateUserPhone;

	/** 远控原因 */
	private String				reason;

	/** 控制类型， 1：工单， 2：分权分域 3： 流量 */
	private Integer				controlType;

	/** 命令有效状态， valid：有效，invalid：无效 */
	private String				controlState;

	/** 下发命令销毁时间 */
	private String				controlDestroyTime;



	/**
	 * @param id
	 * @param deviceId
	 * @param controlItem
	 * @param controlValue
	 * @param controlTime
	 * @param controlUserId
	 * @param operateUserName
	 * @param operateUserPhone
	 * @param reason
	 * @param controlType
	 * @param controlState
	 * @param controlDestroyTime
	 */
	public RemoteControlRecord(String id, String deviceId, String controlItem, String controlValue, String controlTime,
			String controlUserId, String operateUserName, String operateUserPhone, String reason, Integer controlType,
			String controlState, String controlDestroyTime) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.controlItem = controlItem;
		this.controlValue = controlValue;
		this.controlTime = controlTime;
		this.controlUserId = controlUserId;
		this.operateUserName = operateUserName;
		this.operateUserPhone = operateUserPhone;
		this.reason = reason;
		this.controlType = controlType;
		this.controlState = controlState;
		this.controlDestroyTime = controlDestroyTime;
	}



	/**
	 * 
	 */
	public RemoteControlRecord() {
		super();
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
	 * @return the controlItem
	 */
	public String getControlItem() {
		return controlItem;
	}



	/**
	 * @param controlItem
	 *            the controlItem to set
	 */
	public void setControlItem(String controlItem) {
		this.controlItem = controlItem;
	}



	/**
	 * @return the controlValue
	 */
	public String getControlValue() {
		return controlValue;
	}



	/**
	 * @param controlValue
	 *            the controlValue to set
	 */
	public void setControlValue(String controlValue) {
		this.controlValue = controlValue;
	}



	/**
	 * @return the controlTime
	 */
	public String getControlTime() {
		return controlTime;
	}



	/**
	 * @param controlTime
	 *            the controlTime to set
	 */
	public void setControlTime(String controlTime) {
		this.controlTime = controlTime;
	}



	/**
	 * @return the controlUserId
	 */
	public String getControlUserId() {
		return controlUserId;
	}



	/**
	 * @param controlUserId
	 *            the controlUserId to set
	 */
	public void setControlUserId(String controlUserId) {
		this.controlUserId = controlUserId;
	}



	/**
	 * @return the operateUserName
	 */
	public String getOperateUserName() {
		return operateUserName;
	}



	/**
	 * @param operateUserName
	 *            the operateUserName to set
	 */
	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}



	/**
	 * @return the operateUserPhone
	 */
	public String getOperateUserPhone() {
		return operateUserPhone;
	}



	/**
	 * @param operateUserPhone
	 *            the operateUserPhone to set
	 */
	public void setOperateUserPhone(String operateUserPhone) {
		this.operateUserPhone = operateUserPhone;
	}



	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}



	/**
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}



	/**
	 * @return the controlType
	 */
	public Integer getControlType() {
		return controlType;
	}



	/**
	 * @param controlType
	 *            the controlType to set
	 */
	public void setControlType(Integer controlType) {
		this.controlType = controlType;
	}



	/**
	 * @return the controlState
	 */
	public String getControlState() {
		return controlState;
	}



	/**
	 * @param controlState
	 *            the controlState to set
	 */
	public void setControlState(String controlState) {
		this.controlState = controlState;
	}



	/**
	 * @return the controlDestroyTime
	 */
	public String getControlDestroyTime() {
		return controlDestroyTime;
	}



	/**
	 * @param controlDestroyTime
	 *            the controlDestroyTime to set
	 */
	public void setControlDestroyTime(String controlDestroyTime) {
		this.controlDestroyTime = controlDestroyTime;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RemoteControlRecord [id=" + id + ", deviceId=" + deviceId + ", controlItem=" + controlItem
				+ ", controlValue=" + controlValue + ", controlTime=" + controlTime + ", controlUserId=" + controlUserId
				+ ", operateUserName=" + operateUserName + ", operateUserPhone=" + operateUserPhone + ", reason="
				+ reason + ", controlType=" + controlType + ", controlState=" + controlState + ", controlDestroyTime="
				+ controlDestroyTime + "]";
	}

}
