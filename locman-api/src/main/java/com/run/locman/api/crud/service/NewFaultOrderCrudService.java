/*
* File name: NewFaultOrderCrudService.java								
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
* 1.0			Administrator		2019年12月7日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.NewFaultOrder;


/**
* @Description:	
* @author: Administrator
* @version: 1.0, 2019年12月7日
*/

public interface NewFaultOrderCrudService {
	
	RpcResponse<NewFaultOrder> createNewFaultOrder(JSONObject jsonObject);
	
	
	
	RpcResponse<Object> changeOrderState(JSONObject jsonObject);

}
