/*
 * File name: SimpleOrderProcessModel.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年12月28日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.model;

import java.io.Serializable;

/**
 * @Description: controller 作业工单实体接受类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年12月28日
 */

public class SimpleOrderProcessModel implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3603184487315788476L;

	/**
	 * 作业工单id
	 */
	private String				simpleOrderId;

	/**
	 * 接入方密钥
	 */
	private String				accessSecret;

	/**
	 * 处理人id
	 */
	private String				userId;

	/**
	 * 流程实例Id
	 */
	private String				processId;

	/**
	 * 延时时间
	 */
	private Integer				hours;

	/**
	 * 备注
	 */
	private String				detail;



	/**
	 * @return the hours
	 */
	public Integer getHours() {
		return hours;
	}



	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}



	/**
	 * @param detail
	 *            the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}



	/**
	 * @param hours
	 *            the hours to set
	 */
	public void setHours(Integer hours) {
		this.hours = hours;
	}



	/**
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}



	/**
	 * @param processId
	 *            the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}



	/**
	 * @return the simpleOrderId
	 */
	public String getSimpleOrderId() {
		return simpleOrderId;
	}



	/**
	 * @param simpleOrderId
	 *            the simpleOrderId to set
	 */
	public void setSimpleOrderId(String simpleOrderId) {
		this.simpleOrderId = simpleOrderId;
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimpleOrderProcessModel [simpleOrderId=" + simpleOrderId + ", accessSecret=" + accessSecret
				+ ", userId=" + userId + ", processId=" + processId + ", hours=" + hours + ", detail=" + detail + "]";
	}

}
