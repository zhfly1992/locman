/*
* File name: OpenRecordQueryService.java								
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
* 1.0			Administrator		2018年7月17日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.OpenRecordQueryDto;
/**
 * @Description:开启记录查询接口
 * @author: 张贺
 * @version: 1.0, 2018年7月17日
 */

/**
* @Description:	
* @author: Administrator
* @version: 1.0, 2018年7月17日
*/

public interface OpenRecordQueryService {
	RpcResponse<PageInfo<OpenRecordQueryDto>> getOpenRecordByPage(JSONObject jsonObject);
}
