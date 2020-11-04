/*
* File name: NewFaultOrder.java								
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
* 1.0			Administrator		2019年12月7日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
* @Description:	
* @author: 张贺
* @version: 1.0, 2019年12月7日
*/

public class NewFaultOrder implements Serializable {

	
	private static final long	serialVersionUID	= 1L;
	
	private String				id;
	private String				facilityId;
	private String				serialNumber;
	private String				createTime;
	private String				updateTime;
	private String				finishTime;
	private String				createBy;
	private String				updateBy;
	private String				assignUser;
	private String				orderState;
	private String				orderName;
	private String				accessSecret;
	private String				presentPic;
	private String				nodeNum;
	private String				creatFrom;
	private String				faultRemark;
	private String				alarmOrderId;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the facilityId
	 */
	public String getFacilityId() {
		return facilityId;
	}
	/**
	 * @param facilityId the facilityId to set
	 */
	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * @param createTime the createTime to set
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
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * @return the finishTime
	 */
	public String getFinishTime() {
		return finishTime;
	}
	/**
	 * @param finishTime the finishTime to set
	 */
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	/**
	 * @return the createBy
	 */
	public String getCreateBy() {
		return createBy;
	}
	/**
	 * @param createBy the createBy to set
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
	 * @param updateBy the updateBy to set
	 */
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	/**
	 * @return the assignUser
	 */
	public String getAssignUser() {
		return assignUser;
	}
	/**
	 * @param assignUser the assignUser to set
	 */
	public void setAssignUser(String assignUser) {
		this.assignUser = assignUser;
	}
	/**
	 * @return the orderState
	 */
	public String getOrderState() {
		return orderState;
	}
	/**
	 * @param orderState the orderState to set
	 */
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	/**
	 * @return the orderName
	 */
	public String getOrderName() {
		return orderName;
	}
	/**
	 * @param orderName the orderName to set
	 */
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	/**
	 * @return the accessSecret
	 */
	public String getAccessSecret() {
		return accessSecret;
	}
	/**
	 * @param accessSecret the accessSecret to set
	 */
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}
	/**
	 * @return the presentPic
	 */
	public String getPresentPic() {
		return presentPic;
	}
	/**
	 * @param presentPic the presentPic to set
	 */
	public void setPresentPic(String presentPic) {
		this.presentPic = presentPic;
	}
	/**
	 * @return the nodeNum
	 */
	public String getNodeNum() {
		return nodeNum;
	}
	/**
	 * @param nodeNum the nodeNum to set
	 */
	public void setNodeNum(String nodeNum) {
		this.nodeNum = nodeNum;
	}
	/**
	 * @return the creatFrom
	 */
	public String getCreatFrom() {
		return creatFrom;
	}
	/**
	 * @param creatFrom the creatFrom to set
	 */
	public void setCreatFrom(String creatFrom) {
		this.creatFrom = creatFrom;
	}
	/**
	 * @return the faultRemark
	 */
	public String getFaultRemark() {
		return faultRemark;
	}
	/**
	 * @param faultRemark the faultRemark to set
	 */
	public void setFaultRemark(String faultRemark) {
		this.faultRemark = faultRemark;
	}
	/**
	 * @return the alarmOrderId
	 */
	public String getAlarmOrderId() {
		return alarmOrderId;
	}
	/**
	 * @param alarmOrderId the alarmOrderId to set
	 */
	public void setAlarmOrderId(String alarmOrderId) {
		this.alarmOrderId = alarmOrderId;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NewFaultOrder [id=" + id + ", facilityId=" + facilityId + ", serialNumber=" + serialNumber
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", finishTime=" + finishTime
				+ ", createBy=" + createBy + ", updateBy=" + updateBy + ", assignUser=" + assignUser + ", orderState="
				+ orderState + ", orderName=" + orderName + ", accessSecret=" + accessSecret + ", presentPic="
				+ presentPic + ", nodeNum=" + nodeNum + ", creatFrom=" + creatFrom + ", faultRemark=" + faultRemark
				+ ", alarmOrderId=" + alarmOrderId + "]";
	}

}
