package com.run.locman.api.entity;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author
 */
public class AlarmOrder implements Serializable {
	/**
	 * 主键ID
	 */
	private String		id;

	/**
	 * 告警流水单号
	 */
	private BigInteger	serialNum;

	/**
	 * 告警信息Id
	 */
	private String		alarmId;

	/**
	 * 流程id
	 */
	private String		processId;

	/**
	 * 接入方秘钥
	 */
	private String		accessSecret;
	/**
	 * 流程状态
	 * 0.处理中
	 * 1.转故障审批中
	 * 2.转故障被拒绝
	 * 3.转故障已完成
	 * 4.已完成
	 * 5.待处理
	 */
	private String		processState;
	/**
	 * 处理工单时用到的标识信息
	 */
	private String		orderType;
	/**
	 * 图片地址信息
	 */
	private String		manageInfoPic;
	/**
	 * 标识无法修复还是完成处理
	 */
	private String		manageState;
	/**
	 * 用户id
	 */
	private String		userId;
	/**
	 * 发起的用户名
	 */
	private String		userName;
	/**
	 * 发起的用户手机号
	 */
	private String		phone;
	/**
	 * 工单创建时间
	 */
	private String		createTime;
	/**
	 * 故障工单id
	 */
	private String		faultOrderId;

	/**
	 * 工单问题类型id
	 */
	private String		alarmOrderStateTypeId;

	/**
	 * 接受工单时间
	 */
	private String		receiveTime;



	/**
	 * @return the receiveTime
	 */
	public String getReceiveTime() {
		return receiveTime;
	}



	/**
	 * @param receiveTime
	 *            the receiveTime to set
	 */
	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}



	/**
	 * @return the alarmOrderStateTypeId
	 */
	public String getAlarmOrderStateTypeId() {
		return alarmOrderStateTypeId;
	}



	/**
	 * @param alarmOrderStateTypeId
	 *            the alarmOrderStateTypeId to set
	 */
	public void setAlarmOrderStateTypeId(String alarmOrderStateTypeId) {
		this.alarmOrderStateTypeId = alarmOrderStateTypeId;
	}



	public String getFaultOrderId() {
		return faultOrderId;
	}



	public void setFaultOrderId(String faultOrderId) {
		this.faultOrderId = faultOrderId;
	}



	public String getUserId() {
		return userId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getUserName() {
		return userName;
	}



	public void setUserName(String userName) {
		this.userName = userName;
	}



	public String getPhone() {
		return phone;
	}



	public void setPhone(String phone) {
		this.phone = phone;
	}



	public String getProcessState() {
		return processState;
	}



	public void setProcessState(String processState) {
		this.processState = processState;
	}



	public String getOrderType() {
		return orderType;
	}



	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}



	public String getManageInfoPic() {
		return manageInfoPic;
	}



	public void setManageInfoPic(String manageInfoPic) {
		this.manageInfoPic = manageInfoPic;
	}



	public String getManageState() {
		return manageState;
	}



	public void setManageState(String manageState) {
		this.manageState = manageState;
	}

	private static final long serialVersionUID = 1L;



	public String getId() {
		return id;
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



	public void setId(String id) {
		this.id = id;
	}



	public BigInteger getSerialNum() {
		return serialNum;
	}



	public void setSerialNum(BigInteger serialNum) {
		this.serialNum = serialNum;
	}



	public String getAlarmId() {
		return alarmId;
	}



	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}



	public String getProcessId() {
		return processId;
	}



	public void setProcessId(String processId) {
		this.processId = processId;
	}



	public String getAccessSecret() {
		return accessSecret;
	}



	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AlarmOrder [id=" + id + ", serialNum=" + serialNum + ", alarmId=" + alarmId + ", processId=" + processId
				+ ", accessSecret=" + accessSecret + ", processState=" + processState + ", orderType=" + orderType
				+ ", manageInfoPic=" + manageInfoPic + ", manageState=" + manageState + ", userId=" + userId
				+ ", userName=" + userName + ", phone=" + phone + ", createTime=" + createTime + ", faultOrderId="
				+ faultOrderId + ", alarmOrderStateTypeId=" + alarmOrderStateTypeId + "]";
	}

}