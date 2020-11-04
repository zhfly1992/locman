/*
* File name: updateRedisCrudService.java								
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
* 1.0			zhongbinyuan		2020年8月14日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import java.util.List;
import java.util.Map;

import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: zhongbinyuan
* @version: 1.0, 2020年8月14日
*/

public interface UpdateRedisCrudService {
	
	RpcResponse<String> updateFacMapCache(Map<String,Object> map);
	
	RpcResponse<String> batchUpdateFacMapCache(List<String> list,String acc);

}
