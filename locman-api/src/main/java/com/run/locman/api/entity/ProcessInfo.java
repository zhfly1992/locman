/*
 * File name: ProcessInfo.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tianming 2018年02月01日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 工单流程信息实体类
 * @author: 田明
 * @version: 1.0, 2018年02月01日
 */
public class ProcessInfo implements Serializable {

	private static final long	serialVersionUID	= 1L;

	/** 主键id */
	private String				id;
	/** 基础流程类型ID */
	private String				processType;
	/** 修改时间 */
	private String				updateTime;
	/** 创建时间 */
	private String				createTime;
	/** 创建人 */
	private String				createBy;
	/** 修改人 */
	private String				updateBy;
	/** 管理状态 */
	private String				manageState;
	/** 接入方秘钥 */
	private String				accessSecret;
	/** xml文件id */
	private String				fileId;
	/** 备注 */
	private String				remarks;
	


	public ProcessInfo() {
	}


	public ProcessInfo(String id, String processType, String updateTime, String createTime, String createBy, String updateBy, String manageState, String accessSecret, String fileId) {
		this.id = id;
		this.processType = processType;
		this.updateTime = updateTime;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateBy = updateBy;
		this.manageState = manageState;
		this.accessSecret = accessSecret;
		this.fileId = fileId;
	}

	
	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}


	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getManageState() {
		return manageState;
	}

	public void setManageState(String manageState) {
		this.manageState = manageState;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProcessInfo [id=" + id + ", processType=" + processType + ", updateTime=" + updateTime + ", createTime="
				+ createTime + ", createBy=" + createBy + ", updateBy=" + updateBy + ", manageState=" + manageState
				+ ", accessSecret=" + accessSecret + ", fileId=" + fileId + "]";
	}
	
}
