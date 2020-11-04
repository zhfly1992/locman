/*
 * File name: MysqlDataQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhongbinyuan 2019年2月27日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.run.entity.common.RpcResponse;

/**
 * @Description:
 * @author: zhongbinyuan
 * @version: 1.0, 2019年2月27日
 */

public interface MysqlDataQueryService {

	/**
	 * 
	 * @Description:查询mysql
	 * @param sqlStatement
	 * @return
	 */
	RpcResponse<List<LinkedHashMap<String, Object>>> getMysqlQueryResultInfo(String sqlStatement);

	/**
	 * 
	* @Description:查询mongodb
	* @param dbStatement
	* @return
	 */

	RpcResponse<Map<String, Object>> getMongoQueryResultInfo(String dbStatement);
}
