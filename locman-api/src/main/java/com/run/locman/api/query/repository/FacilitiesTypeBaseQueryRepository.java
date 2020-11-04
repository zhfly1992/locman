/*
 * File name: FacilitiesTypeBaseQueryRepository.java
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

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.FacilitiesTypeBase;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年8月31日
 */

public interface FacilitiesTypeBaseQueryRepository extends BaseQueryRepository<FacilitiesTypeBase, String> {

	/**
	 * 
	 * @Description: 基础设施类型分页查询
	 * @param params
	 *            查询参数
	 * @return
	 */
	public List<FacilitiesTypeBase> queryFacilitiesTypeBaseListPage(Map<String, String> params);



	/**
	 * 
	 * @Description:查询某设基础施类型名称存在个数
	 * @param facilityTypeName
	 *            基础设施类型名称
	 * @return
	 */
	public Integer validFacilitiesTypeName(String facilityTypeName);



	/**
	 * 
	 * @Description:查询所有的基础设施类型
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> findAllFacilitiesTypeBase();



	/**
	 * 
	 * @Description:基础设施类型修改名称唯一性校验
	 * @param jsonParam
	 * @return int
	 */
	public int updateFacilitiesTypeBaseCheck(JSONObject jsonParam);

}
