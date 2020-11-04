/*
 * File name: OrderProcessQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年2月1日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.ProcessInfoListDto;

/**
 * @Description: 查询工单流程
 * @author: guofeilong
 * @version: 1.0, 2018年2月1日
 */

public interface OrderProcessQueryService {
	/**
	 * @Description： 根据工单流程类型模糊查询工单流程列表
	 * 
	 * @param queryInfo
	 *            查询参数
	 *            {"pageSize":"页大小","pageNum":"页码","accessSecret":"接入方密钥","processType":"工作流程类型"}
	 * @return
	 */
	RpcResponse<PageInfo<ProcessInfoListDto>> getOrderProcessList(int pageNum, int pageSize, String accessSecret,
			String processType);



	/**
	 * @Description： 根据工单流程id查询当前管理状态
	 * 
	 * @param processId
	 *            工单流程id
	 * @return
	 */
	RpcResponse<String> getManageStateById(String processId);



	/**
	 * @Description: 根据组织id查询用户节点信息
	 * @param queryMap
	 *            查询参数
	 * @return 用户节点信息集合
	 */
	RpcResponse<JSONArray> queryNodeInfoForActivity(Map<String, Object> queryMap);



	/**
	 * @Description:根据工单流程id查询流程信息
	 * @param id
	 *            工单流程id
	 * @return
	 */
	RpcResponse<Map<String, Object>> queryOrderProcessById(String id);



	/**
	 * @Description: 根据工单流程类型id统计启用状态的数量
	 * @param queryInfo：
	 *            processId 工单流程类型id , managerState 管理状态
	 * @return
	 */
	RpcResponse<Integer> countManagerState(Map<String, Object> queryInfo);



	/**
	 * 
	 * @Description:根据nodeId,流程标识，状态查询这个节点下所有的人
	 * @param findInfo
	 *            processSign manageState nodeId
	 */
	RpcResponse<List<String>> findStartUsers(Map<String, Object> findInfo);



	/**
	 * 
	 * @Description:通过流程标识以及状态查询文件id
	 * @param findBpmnInfo
	 *            processSign manageState
	 */
	RpcResponse<String> findBpmnId(Map<String, Object> findBpmnInfo);



	/**
	 * 
	 * @Description:通过用户id判断是否存在启用的流程图上
	 * @param userId
	 * 
	 */
	RpcResponse<List<String>> existUserInProcess(String userId);



	/**
	 * 
	 * @Description:通过用户id判断是否存在启用的告警流程图上
	 * @param userId
	 * @return
	 */
	RpcResponse<String> existUserInAlarm(String userId);
	/**
	 * 
	 * @Description:通过接入方和流程类型查询流程人员配置信息
	 * @param accessSecret 接入方密钥集合 ；processType 流程类型
	 * 
	 */
	RpcResponse<List<Map<String, Object>>> findPersonByAccessSecret(String accessSecret,String processType);
}
