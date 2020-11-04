/*
* File name: TimeoutReportConfigCrudRepository.java								
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
* 1.0			guofeilong		2018年6月22日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;

import com.run.locman.api.entity.TimeoutReportConfig;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月22日
*/

public interface TimeoutReportConfigCrudRepository extends BaseCrudRepository<TimeoutReportConfig, String>{

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int deleteByIds(List<String> congigIds);

}
