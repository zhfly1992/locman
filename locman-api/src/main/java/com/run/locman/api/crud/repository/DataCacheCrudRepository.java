/*
* File name: DataCacheCrudRepository.java								
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
* 1.0			guofeilong		2019年9月14日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年9月14日
*/

public interface DataCacheCrudRepository {

	/**
	 * 
	 * @Description:保存设备上报数据到缓存表
	 * @param 
	 * @return
	 */
	public int saveCacheData(@Param("id") String id, @Param("dataCache") String dataCache);
	
	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	public int updateDataCacheRetryCount(@Param("id") String id);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public int deleteCacheDataById(String id);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public List<Map<String, Object>> findRetryCacheData();
}
