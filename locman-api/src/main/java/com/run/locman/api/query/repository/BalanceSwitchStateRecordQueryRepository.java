/*
 * File name: BalanceSwitchStateRecordQueryRepository.java
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

package com.run.locman.api.query.repository;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.BalanceSwitchStateRecord;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月15日
 */

public interface BalanceSwitchStateRecordQueryRepository {

	/**
	 * 
	 * @Description:获取平衡告警开关开启关闭状态
	 * @param paramJson
	 *            {"deviceId":"","facilityId":"","accessSecret":""}
	 * @return BalanceSwitchStateRecord
	 */
	public BalanceSwitchStateRecord getState(JSONObject paramJson);
	/**
	 * 
	 * @Description:获取最新关闭时间
	 * @param paramJson
	 *            {"deviceId":"","accessSecret":""}
	 * @return String 
	 */
	public String getLatestCloseOperationTime(Map<String, String> map);
}
