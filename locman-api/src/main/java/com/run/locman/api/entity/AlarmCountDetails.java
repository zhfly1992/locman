/*
 * File name: AlarmCountDetails.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年7月18日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 告警统计详情实体类
 * @author: 赵伟志
 * @version: 1.0, 2018年7月18日
 */

public class AlarmCountDetails implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2210657809390867133L;

	/**
	 * 地址
	 */
	private String				completeAddress;

	/**
	 * 告警等级
	 */
	private String				alarmLevel;

	/**
	 * 告警描述
	 */
	private String				alarmDesc;

	/**
	 * 工单类型
	 */
	private String				orderType;

	/**
	 * 工单时间
	 */
	private String				alarmTime;

	/**
	 * 工单处理人
	 */
	private String				userName;

	/**
	 * 接入方密钥
	 */
	private String				accessSecret;

	/**
	 * 页码
	 */
	private String				pageNum;

	/**
	 * 页码对象
	 */
	private String				pageSize;

	/**
	 * 告警类型
	 */
	private String				aostName;

	/**
	 * 工单状态
	 */
	private String				processState;

	/**
	 * 设施类型ID
	 */
	private String				facilitiesTypeId;



	/**
	 * @return the facilitiesTypeId
	 */
	public String getFacilitiesTypeId() {
		return facilitiesTypeId;
	}



	/**
	 * @param facilitiesTypeId
	 *            the facilitiesTypeId to set
	 */
	public void setFacilitiesTypeId(String facilitiesTypeId) {
		this.facilitiesTypeId = facilitiesTypeId;
	}



	/**
	 * @return the processState
	 */
	public String getProcessState() {
		return processState;
	}



	/**
	 * @param processState
	 *            the processState to set
	 */
	public void setProcessState(String processState) {
		this.processState = processState;
	}



	/**
	 * @return the completeAddress
	 */
	public String getCompleteAddress() {
		return completeAddress;
	}



	/**
	 * @param completeAddress
	 *            the completeAddress to set
	 */
	public void setCompleteAddress(String completeAddress) {
		this.completeAddress = completeAddress;
	}



	/**
	 * @return the alarmLevel
	 */
	public String getAlarmLevel() {
		return alarmLevel;
	}



	/**
	 * @param alarmLevel
	 *            the alarmLevel to set
	 */
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}



	/**
	 * @return the alarmDesc
	 */
	public String getAlarmDesc() {
		return alarmDesc;
	}



	/**
	 * @param alarmDesc
	 *            the alarmDesc to set
	 */
	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}



	/**
	 * @return the orderType
	 */
	public String getOrderType() {
		return orderType;
	}



	/**
	 * @param orderType
	 *            the orderType to set
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}



	/**
	 * @return the alarmTime
	 */
	public String getAlarmTime() {
		return alarmTime;
	}



	/**
	 * @param alarmTime
	 *            the alarmTime to set
	 */
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
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
	 * @return the pageNum
	 */
	public String getPageNum() {
		return pageNum;
	}



	/**
	 * @param pageNum
	 *            the pageNum to set
	 */
	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}



	/**
	 * @return the pageSize
	 */
	public String getPageSize() {
		return pageSize;
	}



	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}



	/**
	 * @return the aostName
	 */
	public String getAostName() {
		return aostName;
	}



	/**
	 * @param aostName
	 *            the aostName to set
	 */
	public void setAostName(String aostName) {
		this.aostName = aostName;
	}

}
