/*
 * File name: AreaQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年6月20日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description:行政区域查询
 * @author: 王胜
 * @version: 1.0, 2018年6月20日
 */

public interface AreaQueryService {
	/**
	 * 
	 * @Description:根据区域码查询区域
	 * @param areaCode
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getAreaByCode(JSONObject areaCode);



	/**
	 * 
	 * @Description:根据密钥查询区域信息
	 * @param accessSecret
	 * @return
	 */

	RpcResponse<Map<String, Object>> getAreaInfo(String accessSecret);



	/**
	  * 
	  * @Description:获取首页显示名称
	  * @param 
	  * @return
	  */
	
	RpcResponse<String> getAccessInfoByUrl(String urlStr);
	
	/**
	 * 
	* @Description:划线地图区域展示
	* @param accessSecret
	* @return
	 */
	RpcResponse<JSONObject> getCrossedMapInfo(String accessSecret);
}
