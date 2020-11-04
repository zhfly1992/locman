package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * 
 * @Description:工单统计类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface StatisticsQueryService {
	/**
	 * 
	 * @Description:查询工单统计
	 * @param parm
	 * @return
	 */
	public RpcResponse<Map<String, Integer>> getStatisticInfo(JSONObject parm);



	/**
	 * @Description:查询待办统计信息
	 * @param parm
	 * @return
	 */
	public RpcResponse<Integer> getStatisticTodoInfo(List<String> ids, String type);



	
}
