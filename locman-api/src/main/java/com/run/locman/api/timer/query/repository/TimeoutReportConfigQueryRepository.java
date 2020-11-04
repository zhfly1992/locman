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

package com.run.locman.api.timer.query.repository;

import java.util.List;

import com.run.locman.api.dto.DeviceAndTimeDto;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年6月26日
 */

public interface TimeoutReportConfigQueryRepository{

	public List<DeviceAndTimeDto> getDeviceAndTime();
}
