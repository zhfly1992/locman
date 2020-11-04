/*
* File name: TimeoutReportConfigQueryDto.java								
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
* 1.0			guofeilong		2018年6月26日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月26日
*/

public class TimeoutReportConfigQueryDto {
	
	/** 密钥 */
	private	String	accessSecret;
	/** 页码 */
	private	int		pageNum;
	/** 页大小 */
	private int		pageSize;
	/** 页大小 */
	private String	name;
	/**
	 * 
	 */
	public TimeoutReportConfigQueryDto() {
		super();
		
	}
	/**
	 * @param accessSecret
	 * @param pageNum
	 * @param pageSize
	 * @param name
	 */
	public TimeoutReportConfigQueryDto(String accessSecret, int pageNum, int pageSize, String name) {
		super();
		this.accessSecret = accessSecret;
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.name = name;
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
	 * @return the pageNum
	 */
	public int getPageNum() {
		return pageNum;
	}
	/**
	 * @param pageNum the pageNum to set
	 */
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	
}
