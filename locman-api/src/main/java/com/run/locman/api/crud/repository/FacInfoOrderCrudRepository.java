/*
* File name: SimpleOrderFacilitiesCrudRepository.java								
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
* 1.0			Administrator		2018年4月20日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.repository;

import com.run.locman.api.model.FacilitiesModel;

/**
 * @Description: 一般工单与设施关系crud
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月20日
 */

public interface FacInfoOrderCrudRepository {

	/**
	 * 
	 * @Description:插入一条数据，工单与设施关系
	 * @param model
	 * @return
	 * @throws Exception
	 */
	int insertModel(FacilitiesModel model) throws Exception;

	/**
	 * 
	 * @Description:通过工单id删除工单与设施关系
	 * @param simpleOrderId
	 * @return
	 * @throws Exception
	 */
	int deleteByOrderId(String simpleOrderId) throws Exception;
}
