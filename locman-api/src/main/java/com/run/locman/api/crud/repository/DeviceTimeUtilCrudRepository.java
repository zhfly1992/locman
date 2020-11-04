/*
 * File name: DeviceFirstTimeCrudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年9月25日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2018年9月25日
 */

public interface DeviceTimeUtilCrudRepository {

	public int addDeviceLastReportTime(@SuppressWarnings("rawtypes") List<Map> list);



	public int addDeviceOnLineState(@SuppressWarnings("rawtypes") List<Map> list);



	public List<String> getDeviceIdList();


}
