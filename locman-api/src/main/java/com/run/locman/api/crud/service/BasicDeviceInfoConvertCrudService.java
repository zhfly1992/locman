/*
* File name: BasicDeviceInfoConvertCrudService.java								
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
* 1.0			Administrator		2018年4月24日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;



import com.run.entity.common.RpcResponse;


/**
* @Description:	
* @author: Administrator
* @version: 1.0, 2018年4月24日
*/

public interface BasicDeviceInfoConvertCrudService {
	
	/**
	*/
	RpcResponse<Boolean> basicDeviceInfoConvertadd(String accessSecret);

}
