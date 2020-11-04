/*
* File name: TimeoutReportConfigQueryService.java								
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
* 1.0			guofeilong		2018年7月4日
* ...			...			...
*
***************************************************/

package com.run.locman.api.timer.query.service;

import java.util.List;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.DeviceAndTimeDto;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年7月4日
*/

public interface TimeoutReportConfigQueryService {

	RpcResponse<List<DeviceAndTimeDto>> getDeviceAndTime();
}
