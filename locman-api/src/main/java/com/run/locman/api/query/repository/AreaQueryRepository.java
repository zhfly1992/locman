/*
 * File name: AreaQueryRepository.java
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

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description: 行政区域查询
 * @author: 王胜
 * @version: 1.0, 2018年6月20日
 */

public interface AreaQueryRepository {
	/**
	 * 
	 * @Description: 根据区域码查询区域
	 * @param areaCode
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getAreaByCode(JSONObject areaCode);



	/**
	 * 
	 * @Description:获取区域地址
	 * @param areaIds
	 * @return
	 */
	String getAreaName(List<String> areaIds);
	
	/**
	 * 
	* @Description:获取区域信息
	* @param accessSecret
	* @return
	 */
	
	Map<String ,Object> getAreaInfo(String accessSecret);



	/**
	  * 
	  * @Description: 获取首页显示名称
	  * @param 
	  * @return
	  */
	
	String getAccessInfoByUrl(String urlStr);
}
