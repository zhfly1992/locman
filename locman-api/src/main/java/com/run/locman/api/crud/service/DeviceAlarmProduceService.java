/*
 * File name: DeviceAlarmProduceService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年10月19日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 设备告警接口
 * @author: 王胜
 * @version: 1.0, 2018年10月19日
 */

public interface DeviceAlarmProduceService {

	/**
	 * 
	 * @Description:设备告警产生接口
	 * @param deviceRepotedData
	 */
	public RpcResponse<String> alarmProduce(JSONObject deviceRepotedData, String deviceId, String timestamp);

}
