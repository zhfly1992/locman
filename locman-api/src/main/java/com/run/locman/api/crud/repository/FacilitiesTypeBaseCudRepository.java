/*
* File name: FacilitiesTypeBaseCudRepository.java								
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
* 1.0			qulong		2017年8月31日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.FacilitiesTypeBase;

/**
* @Description:	
* @author: qulong
* @version: 1.0, 2017年8月31日
*/

public interface FacilitiesTypeBaseCudRepository extends BaseCrudRepository<FacilitiesTypeBase, String> {

	/**
	 * 
	* @Description: 修改基础设施类型的部分属性
	* @param map 要修改的参数
	* @return 
	 */
	public int updatePart(Map<String, String> map);
	
}
