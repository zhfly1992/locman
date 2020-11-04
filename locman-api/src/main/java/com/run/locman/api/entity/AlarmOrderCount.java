/*
 * File name: AlarmOrderCount.java
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
 * @Description:告警工单统计实体类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年7月18日
 */

public class AlarmOrderCount implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3221403009271851574L;

	/**
	 * 区域地址
	 */
	private String				completeAddress;

	/**
	 * 其他统计
	 */
	private String				other;

	/**
	 * 车辆碾压统计
	 */
	private String				vehicleAlarm;

	/**
	 * 设备误告警
	 */
	private String				equipmentAlarm;

	/**
	 * 施工放缆
	 */
	private String				constructionPutLAN;

	/**
	 * 设备维护
	 */
	private String				deviceMaintenance;

	/**
	 * 紧急抢处理
	 */
	private String				emergencyRepair;

	/**
	 * 起始时间
	 */
	private String				startTime;

	/**
	 * 结束时间
	 */
	private String				endTime;

	/**
	 * 接入方密钥
	 */
	private String				accessSecret;

	/**
	 * 分页大小
	 */
	private String				pageSize;

	/**
	 * 页码
	 */
	private String				pageNum;

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
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}



	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}



	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}



	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
	 * @return the other
	 */
	public String getOther() {
		return other;
	}



	/**
	 * @param other
	 *            the other to set
	 */
	public void setOther(String other) {
		this.other = other;
	}



	/**
	 * @return the vehicleAlarm
	 */
	public String getVehicleAlarm() {
		return vehicleAlarm;
	}



	/**
	 * @param vehicleAlarm
	 *            the vehicleAlarm to set
	 */
	public void setVehicleAlarm(String vehicleAlarm) {
		this.vehicleAlarm = vehicleAlarm;
	}



	/**
	 * @return the equipmentAlarm
	 */
	public String getEquipmentAlarm() {
		return equipmentAlarm;
	}



	/**
	 * @param equipmentAlarm
	 *            the equipmentAlarm to set
	 */
	public void setEquipmentAlarm(String equipmentAlarm) {
		this.equipmentAlarm = equipmentAlarm;
	}



	/**
	 * @return the constructionPutLAN
	 */
	public String getConstructionPutLAN() {
		return constructionPutLAN;
	}



	/**
	 * @param constructionPutLAN
	 *            the constructionPutLAN to set
	 */
	public void setConstructionPutLAN(String constructionPutLAN) {
		this.constructionPutLAN = constructionPutLAN;
	}



	/**
	 * @return the deviceMaintenance
	 */
	public String getDeviceMaintenance() {
		return deviceMaintenance;
	}



	/**
	 * @param deviceMaintenance
	 *            the deviceMaintenance to set
	 */
	public void setDeviceMaintenance(String deviceMaintenance) {
		this.deviceMaintenance = deviceMaintenance;
	}



	/**
	 * @return the emergencyRepair
	 */
	public String getEmergencyRepair() {
		return emergencyRepair;
	}



	/**
	 * @param emergencyRepair
	 *            the emergencyRepair to set
	 */
	public void setEmergencyRepair(String emergencyRepair) {
		this.emergencyRepair = emergencyRepair;
	}

}
