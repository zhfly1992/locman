/*
 * File name: FacilitiesTypeCudRepository.java
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

package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.FacilitiesType;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年8月29日
 */

public interface FacilitiesTypeCudRepository extends BaseCrudRepository<FacilitiesType, String> {

	/**
	 * 
	 * @Description:保存设施类型
	 * @param FacilitesType
	 * @return int
	 * @throws Exception
	 */
	public int insertFacilitesType(Map<String, Object> facilitesType) throws Exception;
}
