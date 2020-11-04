/*
 * File name: SwitchLockRecordService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月3日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description:开关锁记录保存接口
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月3日
 */

public interface SwitchLockRecordCrudService {

	/**
	 * 
	 * @Description:保存开关锁记录
	 * @param deviceJson
	 * @return 成功or失败
	 */
	RpcResponse<Boolean> insertSwitchLockInfo(String deviceId, JSONObject reported);
	
	
	/**
	 * 
	* @Description:成华区使用，保存井盖开启关闭数据
	* @param deviceId
	* @param reported
	* @return
	 */
	RpcResponse<Integer> insertManholeSwitch(String deviceId, JSONObject reported,String timestamp);
}
