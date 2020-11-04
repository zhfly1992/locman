/*
 * File name: ProcessTypeBaseQueryRepository.java
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

package com.run.locman.api.query.repository;

import java.util.List;

import com.run.locman.api.entity.ProcessTypeBase;

/**
 * @Description:工单流程类型DAO
 * @author: 田明
 * @version: 1.0, 2018年02月02日
 */
public interface ProcessTypeBaseQueryRepository{

	/**
	 * @Description: 查询基础工单流程类型
	 * @return 工单流程类型集合
	 */
	List<ProcessTypeBase> queryOrderProcessType();
}
