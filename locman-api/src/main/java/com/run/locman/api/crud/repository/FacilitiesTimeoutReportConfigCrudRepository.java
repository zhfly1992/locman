/*
* File name: FacilitiesTimeoutReportConfigCrudRepository.java								
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
* 1.0			guofeilong		2018年6月25日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;

import com.run.locman.api.entity.FacilityTimeoutReportConfig;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月25日
*/

public interface FacilitiesTimeoutReportConfigCrudRepository {

	/**
     * @Description: 添加超时配置与设施关系
     * @param definedAlarmRuleDevice 规则和设备中间表
     * @return
     */
    int addBindFacility(FacilityTimeoutReportConfig facilityTimeoutReportConfig);

	/**
	  * 
	  * @Description: 根据超时配置id删除中间表关系
	  * @param 
	  * @return
	  */
	
	int deleteByTimeoutConfigIds(List<String> congigIds);

	/**
	  * 
	  * @Description: 根据设施id删除中间表关系
	  * @param 
	  * @return
	  */
	
	int deleteByFacilityIds(List<String> delFacilityIds);
}
