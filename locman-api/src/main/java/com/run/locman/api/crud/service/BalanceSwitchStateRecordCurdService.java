/*
 * File name: BalanceSwitchStateRecordService.java
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

package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月15日
 */

public interface BalanceSwitchStateRecordCurdService {

	/**
	 * 
	 * @Description:开启或者关闭平衡告警开关
	 * @param jsonParam
	 * @return RpcResponse<Boolean>
	 */
	public RpcResponse<Boolean> openOrClose(JSONObject jsonParam);
}
