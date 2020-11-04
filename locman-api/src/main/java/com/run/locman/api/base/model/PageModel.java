/*
* File name: PageModel.java								
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
* 1.0			Administrator		2018年3月29日
* ...			...			...
*
***************************************************/

package com.run.locman.api.base.model;

/**
 * @Description: 分页信息接收model类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月29日
 */

public class PageModel {

	/**
	 * 每页条数
	 */
	protected String pageSize;

	/**
	 * 当前页
	 */
	protected String pageNum;

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

}
