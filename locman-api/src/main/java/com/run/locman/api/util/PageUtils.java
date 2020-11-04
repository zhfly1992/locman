/*
* File name: PageUtils.java								
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

package com.run.locman.api.util;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.locman.api.base.repository.BasePagingRepository;
import com.run.locman.constants.PublicConstants;

/**
 * @Description: 分页工具类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月29日
 */

public class PageUtils {

	/**
	 * 
	 * @Description:分页工具类
	 * @param basePagingRepository
	 * @param obj
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	public static <T> PageInfo<T> pageStart(BasePagingRepository<T> basePagingRepository, Object obj) throws Exception {

		JSONObject jsonParam = (JSONObject) JSONObject.toJSON(obj);
		PageHelper.startPage(Integer.valueOf(jsonParam.getString(PublicConstants.PUBLIC_PAGE_NUM)),
				Integer.valueOf(jsonParam.getString(PublicConstants.PUBLIC_PAGE_SIZE)));
		Page<T> findPageList = basePagingRepository.findPageList(obj);
		return new PageInfo<>(findPageList);

	}
}
