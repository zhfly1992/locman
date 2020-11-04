/*
 * File name: OrderProcessQueryRepository.java
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

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.run.locman.api.dto.ProcessInfoDto;
import com.run.locman.api.dto.ProcessInfoListDto;
import com.run.locman.api.entity.ProcessInfo;
import com.run.locman.api.entity.ProcessNodeInfo;

/**
 * @Description: 查询工单流程
 * @author: guofeilong
 * @version: 1.0, 2018年2月1日
 */

public interface OrderProcessQueryRepository extends BaseQueryRepository<ProcessInfo, String> {

	/**
	 * @Description: 根据工单流程类型模糊查询工单流程列表
	 * @param queryInfo
	 *            模糊查询条件 "accessSecret":"接入方秘钥" "processType":"工作流程类型"
	 * @return
	 */
	public Page<ProcessInfoListDto> queryOrderProcessList(Map<String, Object> queryInfo);



	/**
	 * @Description: 根据工单流程id查询管理状态
	 * @param processId
	 *            工单流程id
	 * @return
	 */
	public String queryOrderProcessManagerState(String processId);



	/**
	 * @Description: 根据组织id查询用户节点信息
	 * @param map
	 *            查询参数
	 * @return 节点信息集合
	 */
	List<ProcessNodeInfo> queryNodeInfoForActivity(Map<String, Object> map);



	/**
	 * @Description: 根据工单流程id查询工单流程详情
	 * @param id
	 *            工单流程id
	 * @return
	 */
	List<ProcessInfoDto> queryOrderProcessById(String id);



	/**
	 * @Description: 根据工单流程类型id统计启用状态的数量
	 * @param queryInfo：
	 *            processId 工单流程类型id , managerState 管理状态
	 * @return
	 */
	String countManagerState(Map<String, Object> queryInfo);



	/**
	 * 
	 * @Description:根据nodeId,流程标识，状态查询这个节点下所有的人
	 * @param findInfo
	 *            processSign manageState nodeId
	 */
	List<String> findStartUsers(Map<String, Object> findInfo);



	/**
	 * 
	 * @Description:通过流程标识以及状态查询文件id
	 * @param findBpmnInfo
	 *            processSign manageState
	 */
	String findBpmnId(Map<String, Object> findBpmnInfo);



	/**
	 * 
	 * @Description:通过用户id判断是否存在启用的流程图上
	 * @param userId
	 * 
	 */
	List<String> existUserInProcess(String userId);



	/**
	 * 
	 * @Description:通过用户id判断是否存在启用的流程图(告警)上
	 * @param userId
	 * 
	 */
	String existUserInAlarm(String userId);



	/**
	 * 
	 * @Description: 通过接入方查询超时未上报故障流程人员配置信息
	 * @param
	 * @return
	 */

	public List<Map<String, Object>> findPersonByAccessSecret(Map<String, Object> map);
}
