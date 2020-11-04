/*
 * File name: BalanceSwitchPowersCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月4日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.BalanceSwitchPowers;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月4日
 */

public interface BalanceSwitchPowersCrudService {

	/**
	 * 
	 * @Description:保存平衡告警开关分权分域权限配置
	 * @param balanceSwitchPowers
	 * @return RpcResponse<String>
	 */
	public RpcResponse<String> balanceSwitchPowersSave(BalanceSwitchPowers balanceSwitchPowers);



	/**
	 * 
	 * @Description:修改平衡告警开关分权分域权限配置
	 * @param balanceSwitchPowers
	 * @return RpcResponse<String>
	 */
	public RpcResponse<String> balanceSwitchPowersUpdate(BalanceSwitchPowers balanceSwitchPowers);



	/**
	 * 
	 * @Description:删除平衡告警开关分权分域权限配置
	 * @param balanceSwitchPowers
	 * @return RpcResponse<String>
	 */
	public RpcResponse<String> balanceSwitchPowersDel(String id);

}
