/*
* File name: Pager.java								
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
* 1.0			田明		2017年09月27日
* ...			...			...
*
***************************************************/
package com.run.locman.pagetool;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Description: 分页工具
 * @author: 田明
 * @version: 1.0, 2017年09月27日
 */
public class Pager<T> {

	/**
	 * 每页显示条数
	 */
	private int		pageSize;
	/**
	 * 原集合
	 */
	private List<T>	data;



	private Pager(List<T> data, int pageSize) {
		if (data == null || data.isEmpty()) {
			throw new IllegalArgumentException("data must be not empty!");
		}

		this.data = data;
		this.pageSize = pageSize;
	}



	/**
	 * 创建分页器
	 *
	 * @param data
	 *            需要分页的数据
	 * @param pageSize
	 *            每页显示条数
	 * @param <T>
	 *            业务对象
	 * @return 分页器
	 */
	@SuppressWarnings("unchecked")
	public static <T> Pager<T> create(List<Map<String, Object>> data, int pageSize) {
		return new Pager<>((List<T>) data, pageSize);
	}



	/**
	 * 得到分页后的数据
	 *
	 * @param pageNum
	 *            页码
	 * @return 分页后结果
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPagedList(int pageNum) {
		int fromIndex = (pageNum - 1) * pageSize;
		if (fromIndex >= data.size()) {
			return Collections.emptyList();
		}

		int toIndex = pageNum * pageSize;
		if (toIndex >= data.size()) {
			toIndex = data.size();
		}
		return (List<Map<String, Object>>) data.subList(fromIndex, toIndex);
	}



	public int getPageSize() {
		return pageSize;
	}



	public List<T> getData() {
		return data;
	}
}
