/*
* File name: FacilitiesRenovationQueryService.java								
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

package com.run.locman.api.query.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年12月9日
*/

public interface FacilitiesRenovationQueryService {

	/**
	  * 
	  * @Description: 验证设施是否存在处理中的待整治信息
	  * @param 
	  * @return
	  */
	
	RpcResponse<Integer> isExistNotDealFac(String facilityId);

	/**
	  * 
	  * @Description: 设施id查询对应的处理中的待整治信息
	  * @param 
	  * @return
	  */
	
	RpcResponse<JSONObject> findInfoByFacId(String facilityId);

}
