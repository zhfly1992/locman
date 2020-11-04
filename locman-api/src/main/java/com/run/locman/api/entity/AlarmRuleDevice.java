package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @author
 */
public class AlarmRuleDevice implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1245439183733723237L;
	/**
	 * 告警规则 主键ID
	 */
	private String id;
	/**
	 * 序号
	 */
	private Integer oderNum;
	/**
	 * 规则名称
	 */
	private String ruleName;
	/**
	 * 设备类型id
	 */
	private String deviceTypeId;
	/**
	 * 配置人
	 */
	private String userId;
	/**
	 * 配置时间
	 */
	private String crateTime;
	/**
	 * 修改时间
	 */
	private String updateTime;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 规则状态
	 */
	private String manageState;
	/**
	 * 发布状态
	 */
	private String publishState;
	/**
	 * 规则内容（原始）
	 */
	private String ruleContent;
	/**
	 * 规则
	 */
	private String rule;
	
	/**
	 * 接入方秘钥
	 */
	private String accessSecret;
	/**
	 * 是否删除
	 */
	private String isDelete;
	/**
	 * 告警等级
	 */
	private Integer alarmLevel;
	/**
	 * 匹配顺序
	 */
	private Boolean isMatchOrder;
	/**
	 * 设施编号
	 */

	private String facilitiesId;
	/**
	 * 设备编号
	 */
	private String deviceId;
	/**
	 * 告警规则ID
	 */
	private String alarmRuleId;
	/**
	 * 详细地址
	 */
	private String completeAddress;
	/**
	 * 地址ID
	 */
	private String address;
	/**
	 * 
	 * 区域
	 */
	private String areaId;
	

	public String getDeviceTypeId() {
		return deviceTypeId;
	}

	public void setDeviceTypeId(String deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(Integer alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getOderNum() {
		return oderNum;
	}

	public void setOderNum(Integer oderNum) {
		this.oderNum = oderNum;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getCrateTime() {
		return crateTime;
	}

	public void setCrateTime(String crateTime) {
		this.crateTime = crateTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getManageState() {
		return manageState;
	}

	public void setManageState(String manageState) {
		this.manageState = manageState;
	}

	public String getPublishState() {
		return publishState;
	}

	public void setPublishState(String publishState) {
		this.publishState = publishState;
	}

	public String getRuleContent() {
		return ruleContent;
	}

	public void setRuleContent(String ruleContent) {
		this.ruleContent = ruleContent;
	}

	public String getFacilitiesId() {
		return facilitiesId;
	}

	public void setFacilitiesId(String facilitiesId) {
		this.facilitiesId = facilitiesId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getAlarmRuleId() {
		return alarmRuleId;
	}

	public void setAlarmRuleId(String alarmRuleId) {
		this.alarmRuleId = alarmRuleId;
	}

	public String getCompleteAddress() {
		return completeAddress;
	}

	public void setCompleteAddress(String completeAddress) {
		this.completeAddress = completeAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	
	
	public Boolean isMatchOrder() {
		return isMatchOrder;
	}

	public void setMatchOrder(Boolean isMatchOrder) {
		this.isMatchOrder = isMatchOrder;
	}

	@Override
	public String toString() {
		return "AlarmRuleDevice [id=" + id + ", oderNum=" + oderNum + ", ruleName=" + ruleName + ", deviceTypeId="
				+ deviceTypeId + ", userId=" + userId + ", crateTime=" + crateTime + ", updateTime=" + updateTime
				+ ", remark=" + remark + ", manageState=" + manageState + ", publishState=" + publishState
				+ ", ruleContent=" + ruleContent + ", rule=" + rule + ", accessSecret=" + accessSecret + ", isDelete="
				+ isDelete + ", alarmLevel=" + alarmLevel + ", isMatchOrder=" + isMatchOrder + ", facilitiesId="
				+ facilitiesId + ", deviceId=" + deviceId + ", alarmRuleId=" + alarmRuleId + ", completeAddress="
				+ completeAddress + ", address=" + address + ", areaId=" + areaId + "]";
	}

	
}
