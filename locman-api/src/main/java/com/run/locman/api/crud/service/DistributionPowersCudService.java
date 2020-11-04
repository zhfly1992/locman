/*
* File name: DistributionPowersCudService.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			qulong		2017年9月15日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import java.util.Map;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DistributionPowers;

/**
* @Description:	
* @author: qulong
* @version: 1.0, 2017年9月15日
*/

public interface DistributionPowersCudService {

	/**
	 * 
	* @Description: 保存分权分域配置
	* @param distributionPowers 分权分域对象
	* @return 保存成功后的主键ID
	 */
	public RpcResponse<String> saveDistributionPowers(DistributionPowers distributionPowers);
	
	/**
	 * 
	* @Description: 修改分权分域配置
	* @param params 要修改的参数名和值
	* @return 修改成功后的主键ID
	 */
	public RpcResponse<String> editDistributionPowers(Map<String, String> params);
	
	/**
	 * 
	* @Description: 删除分权分域配置（物理删除）
	* @param id 主键ID
	* @return 成功返回true 失败false
	 */
	public RpcResponse<Boolean> deleteDistributionPowers(String id);
	
}
