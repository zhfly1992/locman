/*
 * File name: NewFaultOrderQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhongbinyuan 2019年12月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;

/**
 * @Description:
 * @author: zhongbinyuan
 * @version: 1.0, 2019年12月7日
 */

public interface NewFaultOrderQueryService {
	
/**
 * 
* @Description:
* @param json
* @return
 */
	public RpcResponse<PageInfo<Map<String, Object>>> getFaultListInfo(JSONObject json);
	
	public RpcResponse<Map<String, Object>> countCountDay(JSONObject param);

}
