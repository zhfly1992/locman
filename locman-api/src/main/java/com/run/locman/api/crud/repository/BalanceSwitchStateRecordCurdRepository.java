/*
 * File name: BalanceSwitchStateRecordRepository.java
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

package com.run.locman.api.crud.repository;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月15日
 */

public interface BalanceSwitchStateRecordCurdRepository {

	/**
	 * 
	 * @Description:保存平衡告警开启和关闭记录
	 * @param jsonParam
	 * @return int
	 */
	public int openOrClose(JSONObject jsonParam);



	/**
	 * 
	 * @Description:查询设备是否通过平衡开关开启
	 * @param paramMap
	 *            设备id，设施id，接入方秘钥
	 * @return 最后一次开启的状态
	 */
	public String getBancanceStateByDevId(Map<String, String> checkParam);
}
