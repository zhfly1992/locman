/*
* File name: BaseFacilitiesTypeCrudRepository.java								
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
* 1.0			guofeilong		2018年4月23日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import com.run.locman.api.dto.BaseFacilitiesTypeDto;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年4月23日
*/

public interface BaseFacilitiesTypeCrudRepository extends BaseCrudRepository<BaseFacilitiesTypeDto, String> {

	/**
	  * 
	  * @Description:	同步设施类型数据
	  * @param  synchronousInfo {accessSecret : 接入方密钥, userId : 用户id}
	  * @return
	  */
	
	public Boolean synchronous(Map<String, Object> synchronousInfo);

	/**
	  * 
	  * @Description:	同步基础设施类型数据
	  * @param   userId : 用户id
	  * @return
	  */
	public Boolean synchronousFacilitiesTypeBase(String userId);
	
	/**
	  * 
	  * @Description:	查询是否存在该接入方设施类型同步数据(根据remark 和接入方密钥判断)
	  * @param accessSecret:密钥
	  * @return 
	  */
	
	public List<String> getBFSStateByAS(String accessSecret);

}
