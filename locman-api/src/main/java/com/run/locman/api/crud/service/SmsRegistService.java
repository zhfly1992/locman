/*
 * File name: SmsRegistService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年6月21日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;

/**
 * @Description:短信网关授权接口
 * @author: 张贺
 * @version: 1.0, 2018年6月21日
 */

public interface SmsRegistService {
	RpcResponse<String> smsRegist(String accessName, String accessSecret, String catelogId);
}
