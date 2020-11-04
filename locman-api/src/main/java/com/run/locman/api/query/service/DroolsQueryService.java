/*
 * File name: DroolsQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年10月16日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 规则引擎查询接口
 * @author: zhabing
 * @version: 1.0, 2017年10月16日
 */

public interface DroolsQueryService {
	
	/**
	 * 
	* @Description:分页查询所有规则
	* @param pageNum 分页页数
	* @param pageSize 分页大小
	* @param searchParam 查询条件
	* @return 分页结果
	 */
	public RpcResponse<PageInfo<Map<String, Object>>> getAllDrollsByPage(int pageNum, int pageSize,
			Map<String, Object> searchParam);
	
	/**
	 * 
	* @Description:根据规则id查询规则信息
	* @param id 规则id
	* @return 规则信息
	 */
	public RpcResponse<Map<String, Object>> getDroolInfoById(String id);
}
