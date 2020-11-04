/*
 * File name: StaffType.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月14日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 人员类型实体类
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */

public class StaffType implements Serializable {

	private static final long	serialVersionUID	= 6623499271434606359L;

	private String				id;
	private String				staffTypeName;
	private String				manageState;
	private String				createTime;
	private String				editTime;



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
	 * @return the staffTypeName
	 */
	public String getStaffTypeName() {
		return staffTypeName;
	}



	/**
	 * @param staffTypeName
	 *            the staffTypeName to set
	 */
	public void setStaffTypeName(String staffTypeName) {
		this.staffTypeName = staffTypeName;
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
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}



	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}



	/**
	 * @return the editTime
	 */
	public String getEditTime() {
		return editTime;
	}



	/**
	 * @param editTime
	 *            the editTime to set
	 */
	public void setEditTime(String editTime) {
		this.editTime = editTime;
	}

}
