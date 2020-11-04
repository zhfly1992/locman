/*
* File name: FacilitiesTimeoutReportConfigQueryRepository.java								
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

package com.run.locman.api.query.repository;

import java.util.List;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月27日
*/

public interface FacilitiesTimeoutReportConfigQueryRepository {

	public List<String> queryFacilityTimeoutReportConfigByCId(String id);
}
