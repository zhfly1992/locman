/*
* File name: FocusSecurityQueryRepository.java								
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
* 1.0			钟滨远		2020年4月28日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年4月28日
*/

public interface FocusSecurityQueryRepository {
	
	List<Map<String,Object>> getFocusSecurityInfoPage(Map<String,Object> map);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<Map<String, Object>> commandReceiveStates(@Param("queryMap") Map<String, Object> queryMap,@Param("organizationId") List<String> organizationId);

}
