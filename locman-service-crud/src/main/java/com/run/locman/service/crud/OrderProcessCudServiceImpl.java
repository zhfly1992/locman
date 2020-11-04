/*
 * File name: OrderProcessCudServiceImpl.java
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

package com.run.locman.service.crud;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ParamChecker;
import com.run.common.util.UUIDUtil;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.OrderProcessCudRepository;
import com.run.locman.api.crud.repository.ProcessNodePersonCudRepository;
import com.run.locman.api.crud.service.OrderProcessCudService;
import com.run.locman.api.entity.ProcessInfo;
import com.run.locman.api.entity.ProcessNodePerson;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderProcessContants;

/**
 * @Description: 工单流程cud实现类
 * @author: 田明
 * @version: 1.0, 2018年02月02日
 */
@Transactional(rollbackFor = Exception.class)
public class OrderProcessCudServiceImpl implements OrderProcessCudService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private OrderProcessCudRepository		orderProcessCudRepository;

	@Autowired
	private OrderProcessQueryService		orderProcessQueryService;

	@Autowired
	private ProcessNodePersonCudRepository	processNodePersonCudRepository;

	@Value("${api.host}")
	private String							ip;



	@Override
	public RpcResponse<String> addOrderProcess(JSONObject orderProcessInfo) throws Exception {
		try {
			RpcResponse<String> checkParmer = checkParmer(orderProcessInfo);
			if (null != checkParmer) {
				return checkParmer;
			}
			// 封装信息添加至工单流程信息表
			ProcessInfo processInfo = new ProcessInfo();
			String id = UUIDUtil.getUUID();
			setProcessInfo(orderProcessInfo, processInfo, id);
			// 保存信息
			int model = orderProcessCudRepository.insertModel(processInfo);
			if (model <= 0) {
				logger.error("[addOrderProcess()->error: 添加工单流程表信息失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("添加工单流程表信息失败!");
			}
			// 解析节点信息集合数据
			JSONArray jsonArray = orderProcessInfo.getJSONArray(OrderProcessContants.NODE_INFO);
			// 用于标识节点顺序
			int sortSign = 1;
			for (Object obj : jsonArray) {
				boolean notMatchJson = ParamChecker.isNotMatchJson(obj + "");
				JSONObject nodeInfo = null;
				if (!notMatchJson) {
					nodeInfo = JSON.parseObject(obj + "");
				} else {
					logger.error("[addOrderProcess()->error: 节点信息非法!]");
					return RpcResponseBuilder.buildErrorRpcResp("节点信息非法");
				}
				//检测返回参数字段
				RpcResponse<String> checkInfo = checkInfo(nodeInfo);
				if (null != checkInfo) {
					return checkInfo;
				}
				JSONArray peopleInfo = nodeInfo.getJSONArray(OrderProcessContants.NODE_PERSON);
				JSONObject personInfo = null;
				for (Object person : peopleInfo) {
					boolean isTrueFormat = ParamChecker.isNotMatchJson(obj + "");
					if (!isTrueFormat) {
						personInfo = JSON.parseObject(person + "");
					} else {
						logger.error("[addOrderProcess()->error: 人员信息不能为空!]");
						return RpcResponseBuilder.buildErrorRpcResp("人员信息不能为空");
					}
					String organizeId = personInfo.getString(OrderProcessContants.ORGANIZE_ID);
					String topOrganizeId = personInfo.getString(OrderProcessContants.TOP_ORGANIZATIONID);
					if (StringUtils.isBlank(organizeId) || StringUtils.isBlank(topOrganizeId)) {
						logger.error("[addOrderProcess()->error: 组织id不能为空!]");
						return RpcResponseBuilder.buildErrorRpcResp(
								"非法参数：id为" + personInfo.getString(OrderProcessContants.PERSON_ID) + "的人员组织id为空");
					}
					String realOrganizeName = personInfo.getString(OrderProcessContants.REAL_ORGANIZE_NAME);
					if (StringUtils.isBlank(realOrganizeName)) {
						logger.error("[addOrderProcess()->error: 组织名称不能为空!]");
						return RpcResponseBuilder.buildErrorRpcResp("组织名称不能为空");
					}
					ProcessNodePerson processNodePerson = new ProcessNodePerson();
					setProcessNodePerson(id, sortSign, nodeInfo, personInfo, organizeId, topOrganizeId,
							realOrganizeName, processNodePerson);
					int insertModel = processNodePersonCudRepository.insertModel(processNodePerson);
					if (insertModel <= 0) {
						logger.error("[addOrderProcess()->error: 人员信息不能为空!]");
						return RpcResponseBuilder.buildErrorRpcResp("人员信息不能为空");
					}
				}
				sortSign++;
			}
			logger.info("[addOrderProcess()->success: " + MessageConstant.ADD_SUCCESS + "]");
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.ADD_SUCCESS, id);
		} catch (Exception e) {
			logger.error("addOrderProcess()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> checkInfo(JSONObject nodeInfo) {
		if (StringUtils.isBlank(nodeInfo.getString(OrderProcessContants.NODE_ID))) {
			logger.error("[addOrderProcess()->error: 节点id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("节点id不能为空");
		}

		if (StringUtils.isBlank(nodeInfo.getString(OrderProcessContants.NODE_ID))) {
			logger.error("[addOrderProcess()->error: 节点id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("节点id不能为空");
		}
		if (StringUtils.isBlank(nodeInfo.getString(OrderProcessContants.NODE_PERSON))) {
			logger.error("[addOrderProcess()->error: 人员信息不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("人员信息不能为空");
		}
		return null;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private void setProcessInfo(JSONObject orderProcessInfo, ProcessInfo processInfo, String id) {
		processInfo.setId(id);
		processInfo.setProcessType(orderProcessInfo.getString(OrderProcessContants.PROCESS_TYPE_ID));
		processInfo.setCreateTime(DateUtils.formatDate(new Date()));
		processInfo.setUpdateTime(DateUtils.formatDate(new Date()));
		processInfo.setCreateBy(orderProcessInfo.getString(OrderProcessContants.CREATE_BY));
		processInfo.setUpdateBy(orderProcessInfo.getString(OrderProcessContants.UPDATE_BY));
		processInfo.setManageState(OrderProcessContants.STATE_DISABLED);
		processInfo.setFileId(orderProcessInfo.getString(OrderProcessContants.FILE_ID));
		processInfo.setAccessSecret(orderProcessInfo.getString(OrderProcessContants.USC_ACCESS_SECRET));
		processInfo.setRemarks(orderProcessInfo.getString(OrderProcessContants.REMARKS));
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> checkParmer(JSONObject orderProcessInfo) {
		if (StringUtils.isBlank(orderProcessInfo.getString(OrderProcessContants.PROCESS_TYPE_ID))) {
			logger.error("[addOrderProcess()->invalid：工单流程类型不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("工单流程类型不能为空！");
		}
		if (StringUtils.isBlank(orderProcessInfo.getString(OrderProcessContants.CREATE_BY))) {
			logger.error("[addOrderProcess()->invalid：工单流程创建人不能不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("工单流程创建人不能不能为空!");
		}
		if (StringUtils.isBlank(orderProcessInfo.getString(OrderProcessContants.USC_ACCESS_SECRET))) {
			logger.error("[addOrderProcess()->invalid：接入方秘钥不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
		}
		if (StringUtils.isBlank(orderProcessInfo.getString(OrderProcessContants.NODE_INFO))) {
			logger.error("[addOrderProcess()->invalid：节点信息集合不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("节点信息集合不能为空！");
		}
		if (StringUtils.isBlank(orderProcessInfo.getString(OrderProcessContants.FILE_ID))) {
			logger.error("[addOrderProcess()->invalid：xml文件id不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("xml文件id不能为空！");
		}
		logger.info(String.format("[addOrderProcess()->进入方法,参数:%s]", orderProcessInfo));
		return null;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private void setProcessNodePerson(String id, int sortSign, JSONObject nodeInfo, JSONObject personInfo,
			String organizeId, String topOrganizeId, String realOrganizeName, ProcessNodePerson processNodePerson) {
		processNodePerson.setId(UUIDUtil.getUUID());
		processNodePerson.setProcessId(id);
		processNodePerson.setNode(nodeInfo.getString(OrderProcessContants.NODE_ID));
		processNodePerson.setPersonId(personInfo.getString(OrderProcessContants.PERSON_ID));
		processNodePerson.setRealOrganizeName(realOrganizeName);
		processNodePerson.setRealOrganizeId(organizeId);
		processNodePerson.setOrganizeId(topOrganizeId);
		processNodePerson.setNodeName(nodeInfo.getString(OrderProcessContants.NODE_NAME));
		processNodePerson.setOrderByNum(sortSign + "");
	}



	/**
	 * @see com.run.locman.api.crud.service.OrderProcessCudService(java.lang.String)
	 */

	@Override
	public RpcResponse<String> updateState(String processId, String manageState, String updateBy, String fileId) {
		try {

			// 查询当前管理状态
			RpcResponse<String> manageStateById = orderProcessQueryService.getManageStateById(processId);
			String successValue = manageStateById.getSuccessValue();
			// 健壮性逻辑判断
			if (!manageStateById.isSuccess() || successValue == null) {
				logger.error(MessageConstant.UPDATE_FAIL + "工单流程配置状态查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("工单流程配置状态查询失败");
			}
			if (OrderProcessContants.STATE_DISABLED.equals(successValue) && successValue.equals(manageState)) {
				logger.error(MessageConstant.UPDATE_FAIL + "此工单流程配置已停用!");
				return RpcResponseBuilder.buildErrorRpcResp("此工单流程配置已停用!");
			}
			if (OrderProcessContants.STATE_ENABLED.equals(successValue) && successValue.equals(manageState)) {
				logger.error(MessageConstant.UPDATE_FAIL + "此工单流程配置已启用!");
				return RpcResponseBuilder.buildErrorRpcResp("此工单流程配置已启用!");
			}
			// 流程信息封装
			ProcessInfo processInfo = new ProcessInfo();
			processInfo.setId(processId);
			processInfo.setManageState(manageState);
			processInfo.setUpdateBy(updateBy);
			processInfo.setUpdateTime(DateUtils.formatDate(new Date()));
			processInfo.setFileId(fileId);
			// 数据库更新
			logger.info(String.format("[updateState()->updatePart方法,参数:%s]", processInfo));
			int updatePart = orderProcessCudRepository.updatePart(processInfo);
			if (updatePart > 0) {
				logger.info("[updateState()->success: " + MessageConstant.UPDATE_SUCCESS + "]");
				return RpcResponseBuilder.buildSuccessRpcResp("工单流程配置状态更新成功", processId);
			} else {
				logger.error(MessageConstant.UPDATE_FAIL);
				return RpcResponseBuilder.buildErrorRpcResp("工单流程配置状态更新失败");
			}
		} catch (Exception e) {
			logger.error("updateState()->Exception:", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.OrderProcessCudService#updateOrderProcess(com.alibaba.fastjson.JSONObject)
	 */

	@Override
	public RpcResponse<String> updateOrderProcess(JSONObject orderProcessInfo) throws Exception {
		try {
			// 业务数据判断
			String processTypeId = orderProcessInfo.getString(OrderProcessContants.PROCESS_TYPE_ID);
			String id = orderProcessInfo.getString(OrderProcessContants.PROCESS_ID);
			RpcResponse<String> checkUpdateParmer = checkUpdateParmer(orderProcessInfo, processTypeId, id);
			if (null != checkUpdateParmer) {
				return checkUpdateParmer;
			}
			// 封装信息添加至工单流程信息表
			ProcessInfo processInfo = new ProcessInfo();
			setProcessInfo(orderProcessInfo, processTypeId, id, processInfo);
			// 查询验证是否存在工单流程信息
			RpcResponse<Map<String, Object>> queryOrderProcessById = orderProcessQueryService.queryOrderProcessById(id);
			if (queryOrderProcessById.isSuccess() && queryOrderProcessById.getSuccessValue() != null) {
				// 存在对应工单流程信息则执行修改
				orderProcessCudRepository.updatePart(processInfo);
			} else {
				logger.error("[updateOrderProcess()->invalid：没有查询到此工单流程id对应的信息!]");
				return RpcResponseBuilder.buildErrorRpcResp("没有查询到此工单流程信息!" + queryOrderProcessById.getMessage());
			}
			// 删除中间表关系
			processNodePersonCudRepository.deleteById(id);
			// 解析节点信息集合数据
			JSONArray jsonArray = orderProcessInfo.getJSONArray(OrderProcessContants.NODE_INFO);

			if (jsonArray == null || jsonArray.size() == 0) {
				logger.error("[updateOrderProcess()->invalid：未在节点上绑定人，请重新录入!]");
				return RpcResponseBuilder.buildErrorRpcResp("未在节点上绑定人，请重新录入!");
			}

			// 用于标识节点顺序
			int sortSign = 1;
			for (Object obj : jsonArray) {
				boolean notMatchJson = ParamChecker.isNotMatchJson(obj + "");
				JSONObject nodeInfo = null;
				if (!notMatchJson) {
					nodeInfo = JSON.parseObject(obj + "");
				} else {
					logger.error("[addOrderProcess()->error: 节点信息非法!]");
					return RpcResponseBuilder.buildErrorRpcResp("节点信息非法");
				}

				if (StringUtils.isBlank(nodeInfo.getString(OrderProcessContants.NODE_ID))) {
					logger.error("[addOrderProcess()->error: 节点id不能为空!]");
					return RpcResponseBuilder.buildErrorRpcResp("节点id不能为空");
				}
				if (StringUtils.isBlank(nodeInfo.getString(OrderProcessContants.NODE_PERSON))) {
					logger.error("[addOrderProcess()->error: 人员信息不能为空!]");
					return RpcResponseBuilder.buildErrorRpcResp("人员信息不能为空");
				}
				JSONArray peopleInfo = nodeInfo.getJSONArray(OrderProcessContants.NODE_PERSON);
				JSONObject personInfo = null;
				for (Object person : peopleInfo) {
					RpcResponse<String> checkAndInsert = checkAndInsert(id, sortSign, obj, nodeInfo, personInfo, person);
					if (null != checkAndInsert) {
						return checkAndInsert;
					}
				}
				sortSign++;
			}
			logger.info("[updateOrderProcess()->success: " + MessageConstant.UPDATE_SUCCESS + "]");
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, id);
		} catch (Exception e) {
			logger.error("updateOrderProcess()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> checkAndInsert(String id, int sortSign, Object obj, JSONObject nodeInfo, JSONObject personInfo,
			Object person) throws Exception {
		boolean isTrueFormat = ParamChecker.isNotMatchJson(obj + "");
		if (!isTrueFormat) {
			personInfo = JSON.parseObject(person + "");
		} else {
			logger.error("[addOrderProcess()->error: 人员信息不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("人员信息不能为空");
		}
		String organizeId = personInfo.getString(OrderProcessContants.ORGANIZE_ID);
		if (StringUtils.isBlank(organizeId)
				|| StringUtils.isBlank(personInfo.getString(OrderProcessContants.TOP_ORGANIZATIONID))) {
			logger.error("[addOrderProcess()->error: 组织id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp(
					"非法参数：id为" + personInfo.getString(OrderProcessContants.PERSON_ID) + "的人员组织id为空");
		}
		String realOrganizeName = personInfo.getString(OrderProcessContants.REAL_ORGANIZE_NAME);
		if (StringUtils.isBlank(realOrganizeName)) {
			logger.error("[addOrderProcess()->error: 组织名称不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("组织名称不能为空");
		}
		ProcessNodePerson processNodePerson = new ProcessNodePerson();
		setProcessNodePerson(id, sortSign, nodeInfo, personInfo, organizeId, realOrganizeName,
				processNodePerson);
		int insertModel = processNodePersonCudRepository.insertModel(processNodePerson);
		if (insertModel <= 0) {
			logger.error("[addOrderProcess()->error: 人员信息不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("人员信息不能为空");
		}
		return null;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> checkUpdateParmer(JSONObject orderProcessInfo, String processTypeId, String id) {
		if (StringUtils.isBlank(processTypeId)) {
			logger.error("[updateOrderProcess()->invalid：工单流程类型不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("工单流程类型不能为空！");
		}
		if (StringUtils.isBlank(id)) {
			logger.error("[updateOrderProcess()->invalid：工单流程id不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("工单流程id能为空！");
		}
		if (StringUtils.isBlank(orderProcessInfo.getString(OrderProcessContants.USC_ACCESS_SECRET))) {
			logger.error("[updateOrderProcess()->invalid：接入方秘钥不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
		}
		if (StringUtils.isBlank(orderProcessInfo.getString(OrderProcessContants.NODE_INFO))) {
			logger.error("[updateOrderProcess()->invalid：节点信息集合不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("节点信息集合不能为空！");
		}
		if (StringUtils.isBlank(orderProcessInfo.getString(OrderProcessContants.UPDATE_BY))) {
			logger.error("[updateOrderProcess()->invalid：修改人不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("修改人不能为空！");
		}
		if (StringUtils.isBlank(orderProcessInfo.getString(OrderProcessContants.FILE_ID))) {
			logger.error("[updateOrderProcess()->invalid：xml文件id不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("xml文件id不能为空！");
		}
		logger.info(String.format("[updateOrderProcess()->进入方法,参数:%s]", orderProcessInfo));
		return null;
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private void setProcessInfo(JSONObject orderProcessInfo, String processTypeId, String id, ProcessInfo processInfo) {
		processInfo.setId(id);
		processInfo.setProcessType(processTypeId);
		processInfo.setUpdateTime(DateUtils.formatDate(new Date()));
		processInfo.setUpdateBy(orderProcessInfo.getString(OrderProcessContants.UPDATE_BY));
		processInfo.setAccessSecret(orderProcessInfo.getString(OrderProcessContants.USC_ACCESS_SECRET));
		processInfo.setFileId(orderProcessInfo.getString(OrderProcessContants.FILE_ID));
		processInfo.setRemarks(orderProcessInfo.getString(OrderProcessContants.REMARKS));
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private void setProcessNodePerson(String id, int sortSign, JSONObject nodeInfo, JSONObject personInfo,
			String organizeId, String realOrganizeName, ProcessNodePerson processNodePerson) {
		processNodePerson.setId(UUIDUtil.getUUID());
		processNodePerson.setProcessId(id);
		processNodePerson.setNode(nodeInfo.getString(OrderProcessContants.NODE_ID));
		processNodePerson.setPersonId(personInfo.getString(OrderProcessContants.PERSON_ID));
		processNodePerson.setRealOrganizeName(realOrganizeName);
		processNodePerson.setRealOrganizeId(organizeId);
		processNodePerson.setOrganizeId(personInfo.getString(OrderProcessContants.TOP_ORGANIZATIONID));
		processNodePerson.setNodeName(nodeInfo.getString(OrderProcessContants.NODE_NAME));
		processNodePerson.setOrderByNum(sortSign + "");
	}

}
