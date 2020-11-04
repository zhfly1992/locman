/*
* File name: MysqlDataQueryRepository.java								
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
* 1.0			zhongbinyuan		2019年2月27日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.repository;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
* @Description:	
* @author: zhongbinyuan
* @version: 1.0, 2019年2月27日
*/

public interface MysqlDataQueryRepository {
	
	/**
	 * 
	* @Description:mysql查询
	* @param sqlStatement
	* @return
	 */
	List<LinkedHashMap<String,Object>> getMysqlQueryResult(@Param("sqlStatement") String sqlStatement);
	
}
