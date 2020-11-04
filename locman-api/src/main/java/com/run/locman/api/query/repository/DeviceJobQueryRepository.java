/*
 * File name: DeviceJobQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年7月3日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

/**
 * @Description:设备定时器关系查询接口
 * @author: 王胜
 * @version: 1.0, 2018年7月3日
 */

public interface DeviceJobQueryRepository {
	/**
	 * 
	 * @Description:根据设备id查询定时id集合
	 * @param deviceId
	 * @return RpcResponse<List<String>>
	 */
	List<Map<String, Object>> getJobIdsByDeviceId(String deviceId);
}
