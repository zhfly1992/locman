/*
 * File name: FacilitiesTypeBaseQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年8月31日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.FacilitiesTypeBase;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年8月31日
 */

public interface FacilitiesTypeBaseQueryService {

	/**
	 * 
	 * @Description: 根据唯一ID 查询基础设施类型详情
	 * @param id
	 *            主键ID
	 * @return
	 */
	public RpcResponse<FacilitiesTypeBase> getFacilitiesTypeBaseById(String id);



	/**
	 * 
	 * @Description: 分页查询基础设施类型
	 * @param pageNum
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @param param
	 *            查询参数
	 * @return
	 */
	public RpcResponse<PageInfo<FacilitiesTypeBase>> getFacilitiesTypeBasePage(int pageNum, int pageSize,
			Map<String, String> param);



	/**
	 * 
	 * @Description:校验设施基础类型名称是否重复
	 * @param facilityTypeName
	 *            基础设施类型名称
	 * @return >0 : 名称重复
	 */
	public RpcResponse<Integer> validFacilitiesTypeName(String facilityTypeName);



	/**
	 * 
	 * @Description:查询所有的基础设施类型
	 * @return RpcResponse<List<Map<String, Object>>>
	 */
	public RpcResponse<List<Map<String, Object>>> findAllFacilitiesTypeBase();



	/**
	 * 
	 * @Description:基础设施类型修改名称唯一性校验
	 * @param facilityTypeName
	 * @return RpcResponse<Boolean>
	 */
	public RpcResponse<Boolean> updateFacilitiesTypeBaseCheck(JSONObject jsonParam);

}
