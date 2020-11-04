/*
 * File name: ProcessInfoDto.java
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

package com.run.locman.api.dto;

import java.io.Serializable;

/**
 * @Description: 工单流程信息dto
 * @author: 田明
 * @version: 1.0, 2018年02月01日
 */
public class ProcessInfoDto implements Serializable {

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
	/** 节点 */
	private String				node;
	/** 人员id */
	private String				personId;
	/** 组织id */
	private String				organizeId;
	/** xml文件id */
	private String				fileId;
	/** 节点名 */
	private String				nodeName;
	/** 节点排序用数字 */
	private String				orderByNum;
	/** 真实组织id */
	private String				realOrganizeId;
	/** 真实组织名称 */
	private String				realOrganizeName;



	/**
	 * @return the realOrganizeId
	 */
	public String getRealOrganizeId() {
		return realOrganizeId;
	}



	/**
	 * @param realOrganizeId
	 *            the realOrganizeId to set
	 */
	public void setRealOrganizeId(String realOrganizeId) {
		this.realOrganizeId = realOrganizeId;
	}



	/**
	 * @return the realOrganizeName
	 */
	public String getRealOrganizeName() {
		return realOrganizeName;
	}



	/**
	 * @param realOrganizeName
	 *            the realOrganizeName to set
	 */
	public void setRealOrganizeName(String realOrganizeName) {
		this.realOrganizeName = realOrganizeName;
	}



	/**
	 * @param id
	 * @param processType
	 * @param updateTime
	 * @param createTime
	 * @param createBy
	 * @param updateBy
	 * @param manageState
	 * @param accessSecret
	 * @param node
	 * @param personId
	 * @param organizeId
	 * @param fileId
	 * @param nodeName
	 * @param orderByNum
	 */
	public ProcessInfoDto(String id, String processType, String updateTime, String createTime, String createBy,
			String updateBy, String manageState, String accessSecret, String node, String personId, String organizeId,
			String fileId, String nodeName, String orderByNum) {
		super();
		this.id = id;
		this.processType = processType;
		this.updateTime = updateTime;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateBy = updateBy;
		this.manageState = manageState;
		this.accessSecret = accessSecret;
		this.node = node;
		this.personId = personId;
		this.organizeId = organizeId;
		this.fileId = fileId;
		this.nodeName = nodeName;
		this.orderByNum = orderByNum;
	}



	public ProcessInfoDto() {
	}



	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}



	/**
	 * @param nodeName
	 *            the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}



	/**
	 * @return the orderByNum
	 */
	public String getOrderByNum() {
		return orderByNum;
	}



	/**
	 * @param orderByNum
	 *            the orderByNum to set
	 */
	public void setOrderByNum(String orderByNum) {
		this.orderByNum = orderByNum;
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



	public String getNode() {
		return node;
	}



	public void setNode(String node) {
		this.node = node;
	}



	public String getPersonId() {
		return personId;
	}



	public void setPersonId(String personId) {
		this.personId = personId;
	}



	public String getOrganizeId() {
		return organizeId;
	}



	public void setOrganizeId(String organizeId) {
		this.organizeId = organizeId;
	}



	public String getFileId() {
		return fileId;
	}



	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
}
