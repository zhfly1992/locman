/*
 * File name: SwitchLockRecordQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.SwitchLockRecord;

/**
 * @Description:
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月7日
 */

public interface SwitchLockRecordQueryService {

	/**
	 * 
	 * @Description:分页查询
	 * @param switchLockRecord
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, String>>> listSwitchLockPage(SwitchLockRecord switchLockRecord);



	/**
	 * 
	 * @Description:一体化智能人井校验最近一条开关记录是否为开
	 * @param deviceId
	 * @param accessSecret
	 * @return String
	 */
	RpcResponse<String> checkLock(String deviceId, String accessSecret);
	/**
	 * 
	* @Description:分页查询开关记录
	* @param json
	* @return
	 */
	RpcResponse<PageInfo<Map<String, Object>>> listManholeCoverSwitch(JSONObject json);
	/**
	 * 
	* @Description:根据设备Id查询设施开关井详细信息
	* @param json
	* @return
	 */
	
	RpcResponse<PageInfo<Map<String, Object>>> getManholeCoverSwitchInfo(JSONObject json);

}
