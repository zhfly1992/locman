/*
* File name: FacilitiesTypeBaseCudService.java								
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
* 1.0			qulong		2017年8月31日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import java.util.Map;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.FacilitiesTypeBase;

/**
* @Description:	
* @author: qulong
* @version: 1.0, 2017年8月31日
*/

public interface FacilitiesTypeBaseCudService {

	/**
	 * 
	* @Description: 新增设施基础类型
	* @param facilitiesTypeBase 设施基础类型实体
	* @return
	 */
	public RpcResponse<FacilitiesTypeBase> addFacilitiesTypeBase(FacilitiesTypeBase facilitiesTypeBase);
	
	/**
	 * 
	* @Description: 更新设施基础类型
	* @param map 要更新的实体
	* @return
	 */
	public RpcResponse<FacilitiesTypeBase> updateFacilitiesTypeBase(Map<String, String> map);
}
