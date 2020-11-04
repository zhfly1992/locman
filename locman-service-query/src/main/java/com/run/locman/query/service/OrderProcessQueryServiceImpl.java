/*
 * File name: OrderProcessQueryServiceImpl.java
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

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.activity.api.constans.ActivityConstans;
import com.run.activity.api.query.service.ProcessFileQueryService;
import com.run.activity.entity.ActivityBpmnFile;
import com.run.activity.util.ActivityTool;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.ProcessInfoDto;
import com.run.locman.api.dto.ProcessInfoListDto;
import com.run.locman.api.entity.ProcessNodeInfo;
import com.run.locman.api.query.repository.OrderProcessQueryRepository;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderProcessContants;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * @Description: 查询工单流程类
 * @author: guofeilong
 * @version: 1.0, 2018年2月1日
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class OrderProcessQueryServiceImpl implements OrderProcessQueryService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private OrderProcessQueryRepository	orderProcessQueryRepository;

	@Autowired
	private UserBaseQueryService		userBaseQueryService;

	@Autowired
	private ProcessFileQueryService		processFileQueryService;



	/**
	 * <method description> 分页模糊查询工单流程信息
	 *
	 * @param {
	 *            "pageSize":"页大小","pageNum":"页码","accessSecret":"接入方密钥","processType":"工作流程类型"
	 *            }
	 * @return PageInfo<ProcessInfoListDto>
	 */
	@Override
	public RpcResponse<PageInfo<ProcessInfoListDto>> getOrderProcessList(int pageNum, int pageSize, String accessSecret,
			String processType) {
		logger.info(String.format("[getOrderProcessList()方法执行开始...,参数：【%s】【%s】【%s】【%s】]", pageNum, pageSize,
				accessSecret, processType));
		try {
			// 开始分页
			PageHelper.startPage(pageNum, pageSize);
			HashMap<String, Object> queryInfo = Maps.newHashMap();
			queryInfo.put(OrderProcessContants.USC_ACCESS_SECRET, accessSecret);
			if (!StringUtils.isBlank(processType)) {
				queryInfo.put(OrderProcessContants.PROCESS_TYPE, processType);
			}
			// 查询
			List<ProcessInfoListDto> processInfoList = orderProcessQueryRepository.queryOrderProcessList(queryInfo);
			PageInfo<ProcessInfoListDto> page = new PageInfo<>(processInfoList);

			logger.info(LogMessageContants.QUERY_SUCCESS);
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, page);

		} catch (Exception e) {
			logger.error("getOrderProcessList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * <method description> 根据工单流程id查询管理状态
	 * 
	 * @see com.run.locman.api.query.service.OrderProcessQueryService#(java.lang.String)
	 */
	@Override
	public RpcResponse<String> getManageStateById(String processId) {
		logger.info(String.format("[getManageStateById()方法执行开始...,参数：【%s】]", processId));
		if (StringUtils.isBlank(processId)) {
			logger.error("工单流程id为空");
			return RpcResponseBuilder.buildErrorRpcResp("工单流程id为空");
		}
		try {
			// 查询工单管理状态
			String managerState = orderProcessQueryRepository.queryOrderProcessManagerState(processId);
			if (managerState != null) {
				logger.info(LogMessageContants.QUERY_SUCCESS);
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, managerState);
			} else {
				logger.error("流程id状态查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("流程id状态查询失败");
			}
		} catch (Exception e) {
			logger.error("getOrderProcessManageState()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<JSONArray> queryNodeInfoForActivity(Map<String, Object> queryMap) {
		logger.info(String.format("[queryNodeInfoForActivity()方法执行开始...,参数：【%s】]", queryMap));
		try {
			List<ProcessNodeInfo> list = orderProcessQueryRepository.queryNodeInfoForActivity(queryMap);
			JSONArray jsonArray = new JSONArray();
			for (ProcessNodeInfo processNodeInfo : list) {
				JSONObject jsonObject = new JSONObject();
				List<String> personId = processNodeInfo.getPersonId();
				jsonObject.put(processNodeInfo.getNode(), String.join(",", personId));
				jsonArray.add(jsonObject);
			}
			logger.info(LogMessageContants.QUERY_SUCCESS);
			return RpcResponseBuilder.buildSuccessRpcResp("查询用户节点信息成功", jsonArray);
		} catch (Exception e) {
			logger.error("queryNodeInfoForActivity()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> queryOrderProcessById(String id) {
		logger.info(String.format("[queryOrderProcessById()方法执行开始...,参数：【%s】]", id));
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[queryOrderProcessById()->invalid: 工单流程id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("工单流程id不能为空");
			}
			// 查询出的数据
			List<ProcessInfoDto> processInfos = orderProcessQueryRepository.queryOrderProcessById(id);
			if (!processInfos.isEmpty()) {
				Map<String, Object> map = Maps.newHashMap();
				List<Map<String, Object>> newArrayList = Lists.newArrayList();

				ProcessInfoDto processInfo = processInfos.get(0);
				map.put(OrderProcessContants.USC_ACCESS_SECRET, processInfo.getAccessSecret());
				map.put(OrderProcessContants.PROCESS_ID, processInfo.getId());
				map.put(OrderProcessContants.MANAGER_STATE, processInfo.getManageState());
				map.put(OrderProcessContants.PROCESS_TYPE_ID, processInfo.getProcessType());
				map.put(OrderProcessContants.CREATE_TIME, processInfo.getCreateTime());
				map.put(OrderProcessContants.CREATE_BY, processInfo.getCreateBy());
				map.put(OrderProcessContants.UPDATE_TIME, processInfo.getUpdateTime());
				map.put(OrderProcessContants.UPDATE_BY, processInfo.getUpdateBy());
				map.put(OrderProcessContants.FILE_ID, processInfo.getFileId());
				map.put(OrderProcessContants.NODE_INFO, newArrayList);
				// 构建数据查询文件名
				JSONObject queryInfo = new JSONObject();
				queryInfo.put(OrderProcessContants.USC_ACCESS_SECRET, processInfo.getAccessSecret());
				queryInfo.put("id", processInfo.getFileId());
				RpcResponse<ActivityBpmnFile> bpmn = processFileQueryService.queryBpmnById(queryInfo);
				if (!bpmn.isSuccess() || bpmn.getSuccessValue() == null
						|| bpmn.getSuccessValue().getFileName() == null) {
					map.put(OrderProcessContants.FILE_NAME, "");
				} else {
					map.put(OrderProcessContants.FILE_NAME, bpmn.getSuccessValue().getFileName());
				}

				// 解析文件流
				List<JSONObject> parseBpmn = ActivityTool.parseBpmn(bpmn.getSuccessValue().getXmlFile());
				for (ProcessInfoDto processInfoDto : processInfos) {
					if (!judgeRepeat(newArrayList, processInfoDto.getNode())) {
						Map<String, Object> personMap = Maps.newHashMap();
						personMap.put(OrderProcessContants.NODE_NAME, processInfoDto.getNodeName());
						personMap.put(OrderProcessContants.NODE_ID, processInfoDto.getNode());
						String viewFlag = getViewFlag(parseBpmn, processInfoDto.getNode());
						if (!StringUtils.isBlank(viewFlag)) {
							personMap.put(ActivityConstans.VIEW_FLAG, viewFlag);
						}
						List<Map<Object, Object>> personList = buildNodePerson(processInfos, processInfoDto.getId(),
								processInfoDto.getNode());
						personMap.put(OrderProcessContants.NODE_PERSON, personList);
						newArrayList.add(personMap);
					}
				}
				logger.info("查询成功");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", map);
			} else {
				logger.error("[queryOrderProcessById()->invalid: 查询失败,查询id不存在,id为 : " + id);
				return RpcResponseBuilder.buildSuccessRpcResp("查询失败,查询id不存在!", null);
			}
		} catch (Exception e) {
			logger.error("queryOrderProcessById()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:优化流程图配置所做出的调整，获取页面展示的flag和不展示的flag
	 * @param parseBpmn
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	private String getViewFlag(List<JSONObject> parseBpmn, String taskId) throws Exception {

		for (JSONObject jsonObject : parseBpmn) {
			if (taskId.equals(jsonObject.getString(ActivityConstans.ACTIVITY_TASK_ID))) {
				return jsonObject.getString(ActivityConstans.VIEW_FLAG);
			}
		}

		return null;
	}



	/**
	 * @Description 检验List中是否存在数据
	 *
	 * @param newArrayList
	 * @param id
	 * @param node
	 */

	private boolean judgeRepeat(List<Map<String, Object>> newArrayList, String node) {
		logger.info(String.format("[judgeRepeat()方法执行开始...,参数：【%s】【%s】]", newArrayList, node));

		if (node == null) {
			return true;
		}

		for (Map<String, Object> map : newArrayList) {
			if (node.equals(map.get(OrderProcessContants.NODE_ID))) {
				return true;
			}

		}
		return false;
	}



	/**
	 * @Description 构建人员数据
	 *
	 * @param processInfos
	 */

	@SuppressWarnings("unlikely-arg-type")
	private List<Map<Object, Object>> buildNodePerson(List<ProcessInfoDto> processInfos, String processId,
			String nodeId) {
		logger.info(String.format("[buildNodePerson()方法执行开始...,参数：【%s】【%s】【%s】]", processInfos, processId, nodeId));
		List<Map<Object, Object>> list = new ArrayList<>();

		for (ProcessInfoDto person : processInfos) {
			if (person.getId().equals(processId) && person.getNode().equals(nodeId)) {
				Map<Object, Object> personMap = Maps.newHashMap();
				personMap.put(OrderProcessContants.PERSON_ID, person.getPersonId());
				personMap.put(OrderProcessContants.TOP_ORGANIZE_ID, person.getOrganizeId());
				personMap.put(OrderProcessContants.ORGANIZE_ID, person.getRealOrganizeId());
				personMap.put(OrderProcessContants.REAL_ORGANIZE_NAME, person.getRealOrganizeName());
				RpcResponse<Map> userInfo;
				try {
					userInfo = userBaseQueryService.getUserByUserId(person.getPersonId());

					if (userInfo.isSuccess() && userInfo.getSuccessValue() != null
							&& !"".equals(userInfo.getSuccessValue())) {
						personMap.put(OrderProcessContants.PERSON_NAME, userInfo.getSuccessValue().get("userName"));
					} else {
						personMap.put(OrderProcessContants.PERSON_NAME, "");
					}
				} catch (Exception e) {
					logger.error("buildNodePerson()->exception", e);
				}
				list.add(personMap);
			}
		}
		logger.info(String.format("[buildNodePerson()方法执行结束!]"));
		return list;
	}



	/**
	 * @see com.run.locman.api.query.service.OrderProcessQueryService#countManagerState(java.util.Map)
	 */
	@Override
	public RpcResponse<Integer> countManagerState(Map<String, Object> queryInfo) {
		logger.info(String.format("[countManagerState()方法执行开始...,参数：【%s】]", queryInfo));
		try {
			// 查询该工单流程类型当前启用状态的工单流程配置的数量（同时只能启用一个工单流程配置）
			String countResult = orderProcessQueryRepository.countManagerState(queryInfo);
			if (!StringUtils.isNumeric(countResult)) {
				logger.error("该工单流程类型当前启用状态的工单流程配置的数量--->" + MessageConstant.SEARCH_FAIL);
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
			}
			int count = Integer.parseInt(countResult);
			logger.info(MessageConstant.SEARCH_SUCCESS);
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, count);
		} catch (Exception e) {
			logger.error("countManagerState()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.OrderProcessQueryService#findStartUsers(java.util.Map)
	 */
	@Override
	public RpcResponse<List<String>> findStartUsers(Map<String, Object> findInfo) {
		logger.info(String.format("[findStartUsers()方法执行开始...,参数：【%s】]", findInfo));

		try {
			// 通过节点id查询节点下所有的人
			List<String> findStartUsers = orderProcessQueryRepository.findStartUsers(findInfo);
			logger.info(MessageConstant.SEARCH_SUCCESS);
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, findStartUsers);
		} catch (Exception e) {
			logger.error("findStartUsers()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.OrderProcessQueryService#findBpmnId(java.util.Map)
	 */
	@Override
	public RpcResponse<String> findBpmnId(Map<String, Object> findBpmnInfo) {
		logger.info(String.format("[findBpmnId()方法执行开始...,参数：【%s】]", findBpmnInfo));
		try {
			// 通过流程标识以及状态查询文件id
			String findBpmnId = orderProcessQueryRepository.findBpmnId(findBpmnInfo);
			logger.info(MessageConstant.SEARCH_SUCCESS);
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, findBpmnId);
		} catch (Exception e) {
			logger.error("findBpmnId()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.OrderProcessQueryService#existUserInProcess(java.lang.String)
	 */
	@Override
	public RpcResponse<List<String>> existUserInProcess(String userId) {
		logger.info(String.format("[existUserInProcess()方法执行开始...,参数：【%s】]", userId));

		try {
			// 用户id校验
			if (StringUtils.isBlank(userId)) {
				logger.error(String.format("[existUserInProcess()->%s]", OrderProcessContants.PERSON_ID));
				return RpcResponseBuilder.buildErrorRpcResp("人员id不能为空！");
			}

			List<String> existUserInProcess = orderProcessQueryRepository.existUserInProcess(userId);

			// 是否存在
			if (existUserInProcess == null) {
				logger.info(String.format("[existUserInProcess()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, null);
			} else {
				logger.info(String.format("[existUserInProcess()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, existUserInProcess);
			}

		} catch (Exception e) {
			logger.error("existUserInProcess()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.OrderProcessQueryService#existUserInAlarm(java.lang.String)
	 */
	@Override
	public RpcResponse<String> existUserInAlarm(String userId) {
		logger.info(String.format("[existUserInAlarm()方法执行开始...,参数：【%s】]", userId));
		try {
			// 用户id校验
			if (StringUtils.isBlank(userId)) {
				logger.error(String.format("[existUserInAlarm()->%s]", OrderProcessContants.PERSON_ID));
				return RpcResponseBuilder.buildErrorRpcResp("人员id不能为空！");
			}

			String existUserInProcess = orderProcessQueryRepository.existUserInAlarm(userId);

			// 是否存在
			if (StringUtils.isBlank(existUserInProcess)) {
				logger.info(String.format("[existUserInAlarm()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, null);
			} else {
				logger.info(String.format("[existUserInAlarm()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, existUserInProcess);
			}

		} catch (Exception e) {
			logger.error("existUserInAlarm()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.OrderProcessQueryService#findPersonByAccessSecret(java.util.List,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> findPersonByAccessSecret(String accessSecret, String processType) {
		logger.info(String.format("[findPersonByAccessSecret()方法执行开始...,参数：【%s】【%s】]", accessSecret, processType));
		// 查询人
		Map<String, Object> map = Maps.newHashMap();
		map.put("accessSecret", accessSecret);
		map.put("processType", processType);
		try {
			List<Map<String, Object>> findPersonByAccessSecret = orderProcessQueryRepository
					.findPersonByAccessSecret(map);
			logger.info(String.format("[findPersonByAccessSecret()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, findPersonByAccessSecret);
		} catch (Exception e) {
			logger.error("findPersonByAccessSecret()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
