/*
 * File name: BalanceSwitchStateRecordQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月15日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.BalanceSwitchStateRecord;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月15日
 */

public interface BalanceSwitchStateRecordQueryService {

	/**
	 * 
	 * @Description:获取平衡告警开关开启关闭状态
	 * @param paramJson
	 *            {"deviceId":"","facilityId":"","accessSecret":""}
	 * @return RpcResponse<BalanceSwitchStateRecord>
	 */
	public RpcResponse<BalanceSwitchStateRecord> getState(JSONObject paramJson);
	
	/**
	 * 
	 * @Description:获取平衡告警开关最新关闭时间
	 * @param paramJson
	 *            {"deviceId":"","accessSecret":""}
	 * @return String
	 */
	public RpcResponse<String> getLatestCloseTime(String deviceId,String accessSecret);
}
