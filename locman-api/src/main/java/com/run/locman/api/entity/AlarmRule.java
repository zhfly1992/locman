
package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:告警规则实体类
 * @author: lkc
 * @version: 1.0, 2017年10月31日
 */

public class AlarmRule implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	/** 规则名称 */
	private String				ruleName;
	/** 规则id */
	private String				id;
	/** 完整规则内容 */
	private String				rule;
	/** 规则状态 */
	private String				manageState;
	/** 设备类型id */
	private String				deviceTypeId;
	/** 规则序列号 */
	private long				oderNum;
	/** 规则创建者 */
	private String				userId;
	/** 规则创建时间 */
	private String				crateTime;
	/** 规则更新时间 */
	private String				updateTime;
	/** 规则发布状态 */
	private String				publishState;
	/** 规则内容 */
	private String				ruleContent;
	/** 接入方编码 **/
	private String				accessSecret;
	/** 删除状态 */
	private String				isDelete;
	/** 告警等级 */
	private Integer				alarmLevel;
	/** 是否匹配工单 */
	private Boolean				isMatchOrder;
	/** 备注 */
	private String				remark;
	/** 设备数量 */
	private Integer				deviceCount;
	private String				ruleType;



	public Boolean getIsMatchOrder() {
		return isMatchOrder;
	}



	public void setIsMatchOrder(Boolean isMatchOrder) {
		this.isMatchOrder = isMatchOrder;
	}



	/**
	 * @return the ruleName
	 */
	public String getRuleName() {
		return ruleName;
	}



	/**
	 * @param ruleName
	 *            the ruleName to set
	 */
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
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
	 * @return the rule
	 */
	public String getRule() {
		return rule;
	}



	/**
	 * @param rule
	 *            the rule to set
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}



	/**
	 * @return the manageState
	 */
	public String getManageState() {
		return manageState;
	}



	/**
	 * @param manageState
	 *            the manageState to set
	 */
	public void setManageState(String manageState) {
		this.manageState = manageState;
	}



	/**
	 * @return the deviceTypeId
	 */
	public String getDeviceTypeId() {
		return deviceTypeId;
	}



	/**
	 * @param deviceTypeId
	 *            the deviceTypeId to set
	 */
	public void setDeviceTypeId(String deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}



	public long getOderNum() {
		return oderNum;
	}



	public void setOderNum(long oderNum) {
		this.oderNum = oderNum;
	}



	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}



	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}



	/**
	 * @return the crateTime
	 */
	public String getCrateTime() {
		return crateTime;
	}



	/**
	 * @param crateTime
	 *            the crateTime to set
	 */
	public void setCrateTime(String crateTime) {
		this.crateTime = crateTime;
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
	 * @return the publishState
	 */
	public String getPublishState() {
		return publishState;
	}



	/**
	 * @param publishState
	 *            the publishState to set
	 */
	public void setPublishState(String publishState) {
		this.publishState = publishState;
	}



	/**
	 * @return the ruleContent
	 */
	public String getRuleContent() {
		return ruleContent;
	}



	/**
	 * @param ruleContent
	 *            the ruleContent to set
	 */
	public void setRuleContent(String ruleContent) {
		this.ruleContent = ruleContent;
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
	 * @return the isDelete
	 */
	public String getIsDelete() {
		return isDelete;
	}



	/**
	 * @param isDelete
	 *            the isDelete to set
	 */
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}



	public Integer getAlarmLevel() {
		return alarmLevel;
	}



	public void setAlarmLevel(Integer alarmLevel) {
		this.alarmLevel = alarmLevel;
	}



	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}



	public Boolean getMatchOrder() {
		return isMatchOrder;
	}



	public void setMatchOrder(Boolean matchOrder) {
		isMatchOrder = matchOrder;
	}



	/**
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}



	public Integer getDeviceCount() {
		return deviceCount;
	}



	public void setDeviceCount(Integer deviceCount) {
		this.deviceCount = deviceCount;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AlarmRule [ruleName=" + ruleName + ", id=" + id + ", rule=" + rule + ", manageState=" + manageState
				+ ", deviceTypeId=" + deviceTypeId + ", oderNum=" + oderNum + ", userId=" + userId + ", crateTime="
				+ crateTime + ", updateTime=" + updateTime + ", publishState=" + publishState + ", ruleContent="
				+ ruleContent + ", accessSecret=" + accessSecret + ", isDelete=" + isDelete + ", alarmLevel="
				+ alarmLevel + ", isMatchOrder=" + isMatchOrder + ", remark=" + remark + ", deviceCount=" + deviceCount
				+ ", ruleType=" + ruleType + "]";
	}
	
	
}
