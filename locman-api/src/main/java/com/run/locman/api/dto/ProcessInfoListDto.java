/*
* File name: ProcessInfoListDto.java								
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
* 1.0			guofeilong		2018年2月2日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

import java.io.Serializable;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年2月2日
*/

public class ProcessInfoListDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键id */
	private String				id;
	/** 基础流程类型ID */
	private String				processType;
	/** 基础流程类型名称 */
	private String				processTypeName;
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
	/** 重保时启用标志 */
	private String				mark;
	/**
	 * 
	 */
	public ProcessInfoListDto() {
		super();
	}


	/**
	 * @return the mark
	 */
	public String getMark() {
		return mark;
	}


	/**
	 * @param mark the mark to set
	 */
	public void setMark(String mark) {
		this.mark = mark;
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

	public String getProcessTypeName() {
		return processTypeName;
	}

	public void setProcessTypeName(String processTypeName) {
		this.processTypeName = processTypeName;
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

	@Override
	public String toString() {
		return "ProcessInfoListDto{" +
				"id='" + id + '\'' +
				", processType='" + processType + '\'' +
				", processTypeName='" + processTypeName + '\'' +
				", updateTime='" + updateTime + '\'' +
				", createTime='" + createTime + '\'' +
				", createBy='" + createBy + '\'' +
				", updateBy='" + updateBy + '\'' +
				", manageState='" + manageState + '\'' +
				", accessSecret='" + accessSecret + '\'' +
				", fileId='" + fileId + '\'' +
				'}';
	}
}
