package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月05日
 */
public class SimpleOrderProcess implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private String				id;
	private String				orderName;
	private String				serialNumber;
	private Integer				orderType;
	private String				createBy;
	private String				createTime;
	private String				processStartTime;
	private String				constructBy;
	private String				accessSecret;
	private String				manager;
	private String				phone;
	private String				processEndTime;
	private String				mark;
	private String				orderImg;
	private Integer				deviceCount;
	private String				processState;
	private String				processId;
	private String				userId;
	private String				userName;
	private String				updateTime;
	private String              facilitiesList;
	private String              remindTime;
	private String              remindRule;
	
	
	/**
	 * @return the remindRule
	 */
	public String getRemindRule() {
		return remindRule;
	}



	/**
	 * @param remindRule the remindRule to set
	 */
	public void setRemindRule(String remindRule) {
		this.remindRule = remindRule;
	}



	/**
	 * @return the remindTime
	 */
	public String getRemindTime() {
		return remindTime;
	}



	/**
	 * @param remindTime the remindTime to set
	 */
	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
	}



	/**
	 * @return the facilitiesList
	 */
	public String getFacilitiesList() {
		return facilitiesList;
	}



	/**
	 * @param facilitiesList the facilitiesList to set
	 */
	public void setFacilitiesList(String facilitiesList) {
		this.facilitiesList = facilitiesList;
	}



	public SimpleOrderProcess() {
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getOrderName() {
		return orderName;
	}



	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}



	public String getSerialNumber() {
		return serialNumber;
	}



	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}



	public Integer getOrderType() {
		return orderType;
	}



	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}



	public String getCreateBy() {
		return createBy;
	}



	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}



	public String getCreateTime() {
		return createTime;
	}



	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}



	public String getProcessStartTime() {
		return processStartTime;
	}



	public void setProcessStartTime(String processStartTime) {
		this.processStartTime = processStartTime;
	}



	public String getConstructBy() {
		return constructBy;
	}



	public void setConstructBy(String constructBy) {
		this.constructBy = constructBy;
	}



	public String getAccessSecret() {
		return accessSecret;
	}



	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}



	public String getManager() {
		return manager;
	}



	public void setManager(String manager) {
		this.manager = manager;
	}



	public String getPhone() {
		return phone;
	}



	public void setPhone(String phone) {
		this.phone = phone;
	}



	public String getProcessEndTime() {
		return processEndTime;
	}



	public void setProcessEndTime(String processEndTime) {
		this.processEndTime = processEndTime;
	}



	public String getMark() {
		return mark;
	}



	public void setMark(String mark) {
		this.mark = mark;
	}



	public String getOrderImg() {
		return orderImg;
	}



	public void setOrderImg(String orderImg) {
		this.orderImg = orderImg;
	}



	public Integer getDeviceCount() {
		return deviceCount;
	}



	public void setDeviceCount(Integer deviceCount) {
		this.deviceCount = deviceCount;
	}



	public String getProcessState() {
		return processState;
	}



	public void setProcessState(String processState) {
		this.processState = processState;
	}



	public String getProcessId() {
		return processId;
	}



	public void setProcessId(String processId) {
		this.processId = processId;
	}



	public String getUserId() {
		return userId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getUpdateTime() {
		return updateTime;
	}



	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}



	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}



	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}



	@Override
	public String toString() {
		return "SimpleOrderProcess{" + "id='" + id + '\'' + ", orderName='" + orderName + '\'' + ", serialNumber='"
				+ serialNumber + '\'' + ", orderType='" + orderType + '\'' + ", createBy='" + createBy + '\''
				+ ", createTime='" + createTime + '\'' + ", processStartTime='" + processStartTime + '\''
				+ ", constructBy='" + constructBy + '\'' + ", accessSecret='" + accessSecret + '\'' + ", manager='"
				+ manager + '\'' + ", phone='" + phone + '\'' + ", processEndTime='" + processEndTime + '\''
				+ ", mark='" + mark + '\'' + ", orderImg='" + orderImg + '\'' + ", deviceCount='" + deviceCount + '\''
				+ ", processState='" + processState + '\'' + ", processId='" + processId + '\'' + ", userId='" + userId
				+ '\'' + ", userName='" + userName + '\'' + ", updateTime='" + updateTime + '\'' + '}';
	}
}
