/*
* File name: FocusSecuritysTimerCrudService.java								
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
* 1.0			钟滨远		2020年4月27日
* ...			...			...
*
***************************************************/

package com.run.locman.api.timer.crud.service;

import java.util.Map;

import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年4月27日
*/

public interface FocusSecuritysTimerCrudService {
	
	/**
	 * 
	* @Description:保障项目开启下发命令定时器
	* @param map
	* @return
	 */
	
	RpcResponse<String> focusSecurityIssued(Map<String,Object> map);
	
	/**
	 * 
	* @Description:关闭定时器
	* @param securityId
	* @return
	 */
	
	RpcResponse<Boolean> closeFocusSecurityIssued(String securityId);

}
