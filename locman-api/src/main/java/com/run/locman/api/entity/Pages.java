/*
* File name: Pages.java								
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
* 1.0			guofeilong		2018年9月3日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;
import java.util.List;

/**
* @Description:	分页实体类
* @author: guofeilong
* @version: 1.0, 2018年9月3日
*/

public class Pages<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 10000L;
	/** 当前页 */
	private int pageNum; 
	/** 页大小 */
	private int pageSize;
	/** 总条数*/
	private int total;
	/** 当前页数据 */
	private List<T> list;
	/** 总页数*/
	private int pages;
	
	
	/**
	  * 
	  * @Description:返回查询开始页,并自动校验pageNum和pageSize赋值到对象
	  * @param 
	  * @return 返回查询开始页
	  */
	public int getStart(int pageNum, int pageSize) {
		int start = (pageNum - 1) * pageSize;
		if (pageNum <= 0 || pageSize <= 0) {
			start = 0;
			pageSize = 0;
		}
		this.setPageNum(pageNum);
		this.setPageSize(pageSize);
		return start;
		
	}
	
	
	/**
	 * 
	 */
	public Pages() {
		super();
		
	}


	/**
	 * @param pageNum
	 * @param pageSize
	 * @param total
	 * @param list
	 * @param pages
	 */
	public Pages(int pageNum, int pageSize, int total, List<T> list, int pages) {
		super();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.total = total;
		this.list = list;
		this.pages = pages;
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
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}


	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
		if (total == -1) {
            pages = 1;
            return;
        }
        if (pageSize > 0) {
            pages = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
        } else {
            pages = 0;
        }
	}


	/**
	 * @return the list
	 */
	public List<T> getList() {
		return list;
	}


	/**
	 * @param list the list to set
	 */
	public void setList(List<T> list) {
		this.list = list;
	}


	/**
	 * @return the pages
	 */
	public int getPages() {
		return pages;
	}


	/**
	 * @param pages the pages to set
	 */
	public void setPages(int pages) {
		this.pages = pages;
	}



	
}
