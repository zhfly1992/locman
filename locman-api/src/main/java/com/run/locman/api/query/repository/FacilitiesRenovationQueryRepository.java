/*
* File name: FacilitiesRenovationQueryRepository.java								
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
* 1.0			guofeilong		2019年12月9日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.repository;

import com.alibaba.fastjson.JSONObject;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年12月9日
*/

public interface FacilitiesRenovationQueryRepository {

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	Integer isExistNotDealFac(String facilityId);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	JSONObject findInfoByFacId(String facilityId);

}
