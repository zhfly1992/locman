/*
* File name: DistributionPowersCudRepository.java								
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

package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.DistributionPowers;

/**
* @Description:	
* @author: qulong
* @version: 1.0, 2017年9月14日
*/

public interface DistributionPowersCudRepository extends BaseCrudRepository<DistributionPowers, String> {

	/**
	 * 
	* @Description: 更新分权分域配置的部分属性
	* @param map 更新参数和值
	* @return
	 */
	public int updatePart(Map<String, String> map);
	
}
