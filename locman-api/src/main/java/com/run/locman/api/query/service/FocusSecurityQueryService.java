/*
* File name: FocusSecurityQueryService.java								
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

package com.run.locman.api.query.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年4月28日
*/

public interface FocusSecurityQueryService {
	
	/**
	 * 
	* @Description:保障新增页面分页查询
	* @param map
	* @return
	 */
	
	public RpcResponse<PageInfo<Map<String,Object>>> getFocusSecurityInfoPage(Map<String,Object> map);

	/**
	  * 
	  * @Description:查询重保配置相关设施命令接收情况
	  * @param 
	  * @return
	  */
	
	public RpcResponse<PageInfo<Map<String, Object>>> commandReceiveStates(Map<String, Object> queryMap);

}
