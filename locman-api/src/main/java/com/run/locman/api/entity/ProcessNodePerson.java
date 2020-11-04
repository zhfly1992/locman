/*
 * File name: ProcessNodePerson.java
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
 * @Description:工单流程与节点、人员、组织关系实体类
 * @author: 田明
 * @version: 1.0, 2018年02月01日
 */
public class ProcessNodePerson implements Serializable {

	private static final long	serialVersionUID	= 1L;

	/** 主键id */
	private String				id;
	/** 流程id */
	private String				processId;
	/** 节点 */
	private String				node;
	/** 人员id */
	private String				personId;
	/** 组织id */
	private String				organizeId;
	/** 节点名 */
	private String				nodeName;
	/** 节点排序 */
	private String				orderByNum;
	/** 真实组织id */
	private String				realOrganizeId;
	/** 组织名称 */
	private String				realOrganizeName;



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
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}



	/**
	 * @param orderByNum
	 *            the orderByNum to set
	 */
	public void setOrderByNum(String orderByNum) {
		this.orderByNum = orderByNum;
	}



	public ProcessNodePerson() {
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProcessNodePerson [id=" + id + ", processId=" + processId + ", node=" + node + ", personId=" + personId
				+ ", organizeId=" + organizeId + ", nodeName=" + nodeName + ", orderByNum=" + orderByNum
				+ ", realOrganizeId=" + realOrganizeId + "]";
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getProcessId() {
		return processId;
	}



	public void setProcessId(String processId) {
		this.processId = processId;
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

}
