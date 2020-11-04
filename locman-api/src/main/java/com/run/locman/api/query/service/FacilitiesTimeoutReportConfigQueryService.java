/*
* File name: FacilitiesTimeoutReportConfigQueryService.java								
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
* 1.0			guofeilong		2018年6月27日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.service;

import java.util.List;

import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月27日
*/

public interface FacilitiesTimeoutReportConfigQueryService {

	RpcResponse<List<String>>  queryFacilityTimeoutReportConfigByCId(String id);
}
