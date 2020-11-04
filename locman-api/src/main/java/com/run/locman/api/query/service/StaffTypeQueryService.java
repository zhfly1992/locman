/*
 * File name: StaffTypeQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月14日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.List;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.StaffType;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */

public interface StaffTypeQueryService {

	/**
	 * 
	 * @Description: 不分页查找指定接入方下的所有人员类型
	 * @return 所有人员类型的集合
	 */
	public RpcResponse<List<StaffType>> findAllByAccessSecret();
	
	/**
	 * 
	* @Description: 根据主键ID查询人员类型
	* @param id 主键ID
	* @return
	 */
	public RpcResponse<StaffType> findById(String id);

}
