/*
 * File name: SwitchLockRecord.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月3日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:开关锁记录实体类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月3日
 */
public class SwitchLockRecord implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 350666799349380249L;

	/**
	 * 主键
	 */
	private String				id;

	/**
	 * 开关锁状态
	 */
	private String				lockState;

	/**
	 * 上报时间
	 */
	private String				reportTime;

	/**
	 * 设备Id
	 */
	private String				deviceId;

	/**
	 * 操作人Id
	 */
	private String				arrangeUserId;

	/**
	 * 命令Id
	 */
	private String				remoteControlRecordId;

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
	 * 组织Id
	 */
	private String				orgId;

	/**
	 * 关键字查询
	 */
	private String				keyWord;

	/**
	 * 操作人id集合
	 */
	private List<String>		ids;

	/**
	 * 组织id集合
	 */
	private List<String>		orgIds;



	/**
	 * @return the orgIds
	 */
	public List<String> getOrgIds() {
		return orgIds;
	}



	/**
	 * @param orgIds
	 *            the orgIds to set
	 */
	public void setOrgIds(List<String> orgIds) {
		this.orgIds = orgIds;
	}



	/**
	 * @return the ids
	 */
	public List<String> getIds() {
		return ids;
	}



	/**
	 * @param ids
	 *            the ids to set
	 */
	public void setIds(List<String> ids) {
		this.ids = ids;
	}



	/**
	 * @return the keyWord
	 */
	public String getKeyWord() {
		return keyWord;
	}



	/**
	 * @param keyWord
	 *            the keyWord to set
	 */
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}



	/**
	 * @return the orgId
	 */
	public String getOrgId() {
		return orgId;
	}



	/**
	 * @param orgId
	 *            the orgId to set
	 */
	public void setOrgId(String orgId) {
		this.orgId = orgId;
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
	 * @return the remoteControlRecordId
	 */
	public String getRemoteControlRecordId() {
		return remoteControlRecordId;
	}



	/**
	 * @param remoteControlRecordId
	 *            the remoteControlRecordId to set
	 */
	public void setRemoteControlRecordId(String remoteControlRecordId) {
		this.remoteControlRecordId = remoteControlRecordId;
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
	 * @return the lockState
	 */
	public String getLockState() {
		return lockState;
	}



	/**
	 * @param lockState
	 *            the lockState to set
	 */
	public void setLockState(String lockState) {
		this.lockState = lockState;
	}



	/**
	 * @return the reportTime
	 */
	public String getReportTime() {
		return reportTime;
	}



	/**
	 * @param reportTime
	 *            the reportTime to set
	 */
	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
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
	 * @return the arrangeUserId
	 */
	public String getArrangeUserId() {
		return arrangeUserId;
	}



	/**
	 * @param arrangeUserId
	 *            the arrangeUserId to set
	 */
	public void setArrangeUserId(String arrangeUserId) {
		this.arrangeUserId = arrangeUserId;
	}

}
