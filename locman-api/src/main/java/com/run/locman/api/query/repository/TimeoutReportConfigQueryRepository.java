/*
 * File name: TimeoutReportConfigQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年6月26日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;

import com.run.locman.api.base.repository.BasePagingRepository;
import com.run.locman.api.dto.DeviceAndTimeDto;
import com.run.locman.api.dto.TimeoutReportConfigQueryDto;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.entity.TimeoutReportConfig;
import com.run.locman.api.model.FacilitiesDtoModel;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年6月26日
 */

public interface TimeoutReportConfigQueryRepository extends BasePagingRepository<Facilities> {

	/**
	 * 
	 * @Description:查询超时时间配置列表
	 * @param timeoutReportConfigQueryDto
	 * @return
	 */
	public List<TimeoutReportConfig> getTimeoutReoprtConfigList(TimeoutReportConfigQueryDto timeoutReportConfigQueryDto);
	public TimeoutReportConfig getTimeoutConfigById(TimeoutReportConfig timeoutReportConfig);
	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public List<String> findFacIdsByConfigId(FacilitiesDtoModel facilitiesDtoModel);
	
	
	public List<DeviceAndTimeDto> getDeviceAndTime();
}
