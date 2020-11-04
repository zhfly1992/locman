/*
* File name: SmsQueryService.java								
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
* 1.0			Administrator		2018年6月22日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.service;

import com.run.entity.common.RpcResponse;

/**
* @Description:	短信发送地址查询接口
* @author: 张贺
* @version: 1.0, 2018年6月22日
*/

public interface SmsQueryService {
	RpcResponse<String> getSmsUrl(String accessSecret);
}
