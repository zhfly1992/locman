package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @author 
 */
public class AlarmInfo implements Serializable {
   

    /**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AlarmInfo [id=" + id + ", facilitiesId=" + facilitiesId + ", deviceId=" + deviceId + ", alarmTime="
				+ alarmTime + ", alarmItem=" + alarmItem + ", alarmLevel=" + alarmLevel + ", alarmDesc=" + alarmDesc
				+ ", facilitiesTypeId=" + facilitiesTypeId + ", serialNum=" + serialNum + ", reportTime=" + reportTime
				+ ", rule=" + rule + ", isMatchOrder=" + isMatchOrder + ", accessSecret=" + accessSecret + ", isDel="
				+ isDel + "]";
	}

	private static final long serialVersionUID = 1L;
	
	private String id;

	/**
	 * 告警设施ID
	 */
    private String facilitiesId;

    /**
     * 告警设备ID
     */
    private String deviceId;

    /**
     * 告警时间
     */
    private String alarmTime;
    /**
     * 告警触发项
     */
    private String alarmItem;

    /**
     * 告警等级   1：紧急  2：一般
     */
    private Integer alarmLevel;

    /**
     * 告警描述
     */
    private String alarmDesc;

    /**
     * 告警规则id
     */
    private String facilitiesTypeId;

    /**
     * 告警流水号
     */
    private long serialNum;

    /**
     * 状态更新时间
     */
    private String reportTime;
    
    /**
     * 告警规则
     */
    private String rule;
    
    /**
     * 是否匹配工单
     */
    private boolean isMatchOrder;
    /**
     * 接入方秘钥
     */
    private String accessSecret;
    /**
     * 是否已处理
     * 0：已生成工单
     * 1：未处理
     * 2：已忽略
     * 3：正常告警流程处理完成
     * 4：告警转故障处理完成
     * */    
    private Integer isDel;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	/**
	 * @return the facilitiesId
	 */
	public String getFacilitiesId() {
		return facilitiesId;
	}

	/**
	 * @param facilitiesId the facilitiesId to set
	 */
	public void setFacilitiesId(String facilitiesId) {
		this.facilitiesId = facilitiesId;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the alarmTime
	 */
	public String getAlarmTime() {
		return alarmTime;
	}

	/**
	 * @param alarmTime the alarmTime to set
	 */
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}

	/**
	 * @return the alarmItem
	 */
	public String getAlarmItem() {
		return alarmItem;
	}

	/**
	 * @param alarmItem the alarmItem to set
	 */
	public void setAlarmItem(String alarmItem) {
		this.alarmItem = alarmItem;
	}

	/**
	 * @return the alarmLevel
	 */
	public Integer getAlarmLevel() {
		return alarmLevel;
	}

	/**
	 * @param alarmLevel the alarmLevel to set
	 */
	public void setAlarmLevel(Integer alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	/**
	 * @return the alarmDesc
	 */
	public String getAlarmDesc() {
		return alarmDesc;
	}

	/**
	 * @param alarmDesc the alarmDesc to set
	 */
	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}

	/**
	 * @return the facilitiesTypeId
	 */
	public String getFacilitiesTypeId() {
		return facilitiesTypeId;
	}

	/**
	 * @param facilitiesTypeId the facilitiesTypeId to set
	 */
	public void setFacilitiesTypeId(String facilitiesTypeId) {
		this.facilitiesTypeId = facilitiesTypeId;
	}

	/**
	 * @return the rule
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}

	/**
	 * @return the isMatchOrder
	 */
	public boolean isMatchOrder() {
		return isMatchOrder;
	}

	/**
	 * @param isMatchOrder the isMatchOrder to set
	 */
	public void setMatchOrder(boolean isMatchOrder) {
		this.isMatchOrder = isMatchOrder;
	}

	/**
	 * @return the serialNum
	 */
	public long getSerialNum() {
		return serialNum;
	}

	/**
	 * @param serialNum the serialNum to set
	 */
	public void setSerialNum(long serialNum) {
		this.serialNum = serialNum;
	}

	/**
	 * @return the reportTime
	 */
	public String getReportTime() {
		return reportTime;
	}

	/**
	 * @param reportTime the reportTime to set
	 */
	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
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
	 * @return the isDel
	 */
	public Integer getIsDel() {
		return isDel;
	}

	/**
	 * @param isDel the isDel to set
	 */
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

}