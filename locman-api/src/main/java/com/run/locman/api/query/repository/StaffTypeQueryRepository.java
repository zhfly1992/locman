/*
* File name: StaffTypeQueryRepository.java								
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
* 1.0			qulong		2017年9月14日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;

import com.run.locman.api.entity.StaffType;

/**
* @Description:	
* @author: qulong
* @version: 1.0, 2017年9月14日
*/

public interface StaffTypeQueryRepository extends BaseQueryRepository<StaffType, String> {

	/**
	 * 
	 * @Description: 不分页查找指定接入方下的所有人员类型
	 * @return 所有人员类型的集合
	 */
	public List<StaffType> findAllAllStaffType();
	
}
