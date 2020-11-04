/*
* File name: SmsRegistCrudRepository.java								
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
* 1.0			Administrator		2018年6月21日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.repository;

import java.util.Map;

/**
* @Description:	短信模块crud接口
* @author: 张贺
* @version: 1.0, 2018年6月21日
*/

public interface  SmsRegistCrudRepository {
	/**
	* @Description:	授权地址保存
	* @author: 张贺
	* @version: 1.0, 2018年6月21日
	*/
	int saveSmsRegistInformation(Map<String, String> map);
	
	/**
	* @Description:	告警短信保存
	* @author: 张贺
	* @version: 1.0, 2018年8月13日
	*/
	int addSmsRecord(Map<String,Object> map);
}
