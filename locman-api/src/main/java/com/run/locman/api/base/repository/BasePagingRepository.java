/*
* File name: BaseRepository.java								
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

package com.run.locman.api.base.repository;

import com.github.pagehelper.Page;

/**
 * @Description: 父类分页对象
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月29日
 */

public interface BasePagingRepository<T> {
	/**
	 * 
	 * @Description:分页查询数据
	 */
	Page<T> findPageList(Object convert);
}
