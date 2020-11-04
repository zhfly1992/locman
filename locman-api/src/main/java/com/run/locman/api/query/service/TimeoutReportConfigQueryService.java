/*
 * File name: TimeoutReportConfigQueryService.java
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

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.DeviceAndTimeDto;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.entity.TimeoutReportConfig;
import com.run.locman.api.model.FacilitiesDtoModel;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年6月26日
 */

public interface TimeoutReportConfigQueryService {

	RpcResponse<PageInfo<TimeoutReportConfig>> getTimeoutConfigList(String accessSecret, int pageNum, int pageSize, String name);
	
	RpcResponse<Map<String, Object>> getTimeoutConfigById(String id);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<PageInfo<Facilities>> findFacInfo(FacilitiesDtoModel facilitiesDtoModel);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<List<DeviceAndTimeDto>> getDeviceAndTime();
}
