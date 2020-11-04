/*
* File name: FacilitiesRenovationCrudRepository.java								
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
* 1.0			guofeilong		2019年12月5日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.repository;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年12月5日
*/

public interface FacilityRenovationCrudRepository {
	
	public int addFacilitiesRenovation(@Param("id") String id,
			@Param("facilityId") String facilityId, @Param("applicationInfo") String json, @Param("userId") String userId);
	
	public int updateFacRenovationManageState(Map<String, Object> params);
	
}
