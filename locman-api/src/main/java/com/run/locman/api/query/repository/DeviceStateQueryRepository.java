/*
 * File name: DeviceStateQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年9月28日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

/**
 * @Description: 临时针对设备导出
 * @author: zhaoweizhi
 * @version: 1.0, 2018年9月28日
 */

public interface DeviceStateQueryRepository {

	/**
	 * 
	 * @Description:临时设备状态导出功能
	 * @param accessSecret
	 * @return
	 */
	List<Map<String, String>> findDeviceState(String accessSecret);

}
