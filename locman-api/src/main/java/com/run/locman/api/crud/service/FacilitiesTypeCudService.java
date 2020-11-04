/*
 * File name: FacilitiesTypeCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年8月29日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.FacilitiesType;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年8月29日
 */

public interface FacilitiesTypeCudService {

	/**
	 * 
	 * @Description: 修改指定设施类型的一个或多个参数的值
	 * @param facilitiesType
	 *            新的设施类型对象
	 * @return
	 */
	public RpcResponse<FacilitiesType> updateFacilitiesType(FacilitiesType facilitiesType);



	/**
	 * 
	 * @Description:保存设施类型
	 * @param facilitiesType
	 * @return RpcResponse<Map<String, Object>>
	 */
	public RpcResponse<Map<String, Object>> insertFacilitesType(JSONObject facilitiesType);

}
