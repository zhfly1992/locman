/*
* File name: FacilitiesRenovationCrudService.java								
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
* 1.0			guofeilong		2019年12月5日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年12月5日
*/

public interface FacilitiesRenovationCrudService {
	/**
	  * 
	  * @Description:添加设施待整治申请信息
	  * @param 
	  * @return
	  */
	
	RpcResponse<Integer> addFacilitiesRenovation(JSONObject json);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<Integer> updateFacRenovationManageState(Map<String, Object> params);

}
