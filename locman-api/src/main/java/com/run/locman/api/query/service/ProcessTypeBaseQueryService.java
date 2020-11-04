/*
 * File name: ProcessTypeBaseQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tianming 2018年02月02日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.ProcessTypeBase;

import java.util.List;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2018年02月02日
 */
public interface ProcessTypeBaseQueryService {

	/**
	 * @Description: 查询基础工单流程类型
	 * @return 工单流程类型集合
	 */
	RpcResponse<List<ProcessTypeBase>> queryOrderProcessType();
}
