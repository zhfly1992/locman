/*
 * File name: FaultOrderProcessQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tm 2017年9月15日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.run.activity.api.query.ActivityProgressQuery;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.CountFaultOrderDto;
import com.run.locman.api.dto.FaultOrderHistogramDto;
import com.run.locman.api.entity.FaultOrderProcessState;
import com.run.locman.api.entity.FaultOrderProcessType;
import com.run.locman.api.query.repository.FaultOrderProcessQueryRepository;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.query.service.FaultOrderProcessQueryService;
import com.run.locman.api.query.service.FaultOrderProcessStateQueryService;
import com.run.locman.api.query.service.FaultOrderProcessTypeQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.AlarmOrderConstants;
import com.run.locman.constants.AlarmOrderCountConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.FactoryConstants;
import com.run.locman.constants.FaultOrderCountContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderConstants;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.SimpleOrderConstants;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
public class FaultOrderProcessQueryServiceImpl implements FaultOrderProcessQueryService {

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FaultOrderProcessQueryRepository	faultOrderProcessQueryRepository;

	@Autowired
	private ActivityProgressQuery				activityProgressQuery;

	@Autowired
	private UserBaseQueryService				userBaseQueryService;

	@Autowired
	private FactoryQueryService					factoryQueryService;

	@Autowired
	private FaultOrderProcessTypeQueryService	faultOrderProcessTypeQueryService;

	@Autowired
	private FaultOrderProcessStateQueryService	faultOrderProcessStateQueryService;

	@Value("${api.host}")
	private String								ip;



	@SuppressWarnings("unused")
	@Override
	public RpcResponse<Map<String, Object>> queryFaultOrderInfoById(String id) {
		logger.info(String.format("[queryFaultOrderInfoById()方法执行开始...,参数：【%s】]", id));
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[queryFaultOrderInfoById()->invalid：参数id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数id不能为空");
			}
			Map<String, Object> map = faultOrderProcessQueryRepository.queryFaultOrderInfoById(id);
			if (null == map) {
				logger.error("根据id查询故障工单信息失败");
				return RpcResponseBuilder.buildErrorRpcResp("根据id查询故障工单信息失败");
			}
			Object faultType = map.get("faultType");
			if (null == faultType || StringUtils.isBlank("" + faultType)) {
				logger.error("字段faultType不存在或为null");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			RpcResponse<FaultOrderProcessType> faultOrderProcessTypeInfo = faultOrderProcessTypeQueryService
					.findById(map.get("faultType").toString());
			if (faultOrderProcessTypeInfo.isSuccess()) {
				map.put("faultTypeName", faultOrderProcessTypeInfo.getSuccessValue().getName());
			}
			if (map != null) {
				logger.info(String.format("[queryFaultOrderInfoById()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("故障工单详情查询成功", map);
			}
			logger.info(String.format("[queryFaultOrderInfoById()方法执行结束!]"));
			return RpcResponseBuilder.buildErrorRpcResp("该故障工单不存在");
		} catch (Exception e) {
			logger.error("queryFaultOrderInfoById()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> queryDevicesForFaultOrder(JSONObject paramInfo) throws Exception {
		logger.info(String.format("[queryDevicesForFaultOrder()方法执行开始...,参数：【%s】]", paramInfo));
		try {

			// RpcResponse<PageInfo<Map<String, Object>>> checkObjectBusinessKey
			// = CheckParameterUtil
			// .checkObjectBusinessKey(logger, "queryDevicesForFaultOrder",
			// paramInfo,
			// AlarmInfoConstants.USC_ACCESS_SECRET, SimpleOrderConstants.TYPE,
			// FactoryConstants.FACTORYID);
			RpcResponse<PageInfo<Map<String, Object>>> checkObjectBusinessKey = CheckParameterUtil
					.checkObjectBusinessKey(logger, "queryDevicesForFaultOrder", paramInfo,
							AlarmInfoConstants.USC_ACCESS_SECRET, SimpleOrderConstants.TYPE);

			if (null != checkObjectBusinessKey) {
				return checkObjectBusinessKey;
			}

			RpcResponse<PageInfo<Map<String, Object>>> checkPageKey = CheckParameterUtil.checkPageKey(logger,
					"queryDevicesForFaultOrder", paramInfo, AlarmInfoConstants.PAGE_NO, AlarmInfoConstants.PAGE_SIZE);

			if (null != checkPageKey) {
				return checkPageKey;
			}

			Map map = new HashMap<>(16);
			map.put(SimpleOrderConstants.ACCESSSECRET, paramInfo.getString(SimpleOrderConstants.ACCESSSECRET));
			map.put(SimpleOrderConstants.FACILITIESTYPEID, paramInfo.getString(SimpleOrderConstants.FACILITIESTYPEID));
			map.put(SimpleOrderConstants.SELECTKEY, paramInfo.getString(SimpleOrderConstants.SELECTKEY));
			map.put("factoryId", paramInfo.getString("factoryId"));
			// 若新增查询所有
			if (paramInfo.getString(SimpleOrderConstants.TYPE).equals(PublicConstants.ADD)) {
				map.put("selectAll", "true");
				map.put("select", "true");
				map.put("binding", "null");
			} else if (paramInfo.getString(SimpleOrderConstants.TYPE).equals(PublicConstants.UPDATE)) {
				if (StringUtils.isBlank(paramInfo.getString(OrderConstants.BINDING))) {
					logger.error("[queryDevicesForFaultOrder()->invalid：绑定状态不能为空!]");
					return RpcResponseBuilder.buildErrorRpcResp("绑定状态不能为空!]");
				}
				if (StringUtils.isBlank(paramInfo.getString(FaultOrderCountContants.FAULT_ORDER_ID))) {
					logger.error("[queryDevicesForFaultOrder()->invalid：故障工单流程id不能为空!]");
					return RpcResponseBuilder.buildErrorRpcResp("故障工单流程id不能为空!]");
				}
				map.put("selectAll", "false");
				map.put("select", "false");
				map.put("faultOrderId", paramInfo.getString("faultOrderId"));
				// 查询绑定的
				if (paramInfo.getString(OrderConstants.BINDING).equals(OrderConstants.BOUND)) {
					map.put("binding", "bound");
				}
				// 查询未绑定的
				if (paramInfo.getString(OrderConstants.BINDING).equals(OrderConstants.UNBOUND)) {
					map.put("binding", "unBound");
				}
			}
			int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> page = faultOrderProcessQueryRepository.queryDevicesForFaultOrder(map);
			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			logger.info(String.format("[queryDevicesForFaultOrder()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("queryDevicesForFaultOrder()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getFaultOrderList(JSONObject paramInfo) {
		logger.info(String.format("[getFaultOrderList()方法执行开始...,参数：【%s】]", paramInfo));
		try {

			if (paramInfo == null) {
				logger.error("[getFaultOrderList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getFaultOrderList()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getFaultOrderList()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getFaultOrderList()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getFaultOrderList()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getFaultOrderList()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.USERID))) {
				logger.error("[getFaultOrderList()->invalid：用户id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空！");
			}

			Map map = new HashMap<>(16);
			map.put("accessSecret", paramInfo.getString("accessSecret"));
			map.put("userId", paramInfo.getString("userId"));
			map.put("appointStartTime", paramInfo.getString("appointStartTime"));
			map.put("appointEndTime", paramInfo.getString("appointEndTime"));
			map.put("faultType", paramInfo.getString("faultType"));
			map.put("processState", paramInfo.getString("processState"));
			map.put("selectKey", paramInfo.getString("selectKey"));
			map.put("manager", paramInfo.getString("manager"));
			map.put("factoryId", paramInfo.getString("factoryId"));

			int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> page = faultOrderProcessQueryRepository.getFaultOrderList(map);

			// 查询发起人名称
			if (null != page && page.size() > 0) {
				String userId = page.get(0).get("userId").toString();
				for (int i = 0; i < page.size(); i++) {
					JSONObject jsonParam = new JSONObject();
					jsonParam.put("processId", page.get(i).get("processId"));
					RpcResponse<Map<String, Object>> result = activityProgressQuery.getJudgmentProcess(jsonParam);
					if (result.isSuccess()) {
						page.get(i).put("operaType", result.getSuccessValue().get("state"));
					} else {
						return RpcResponseBuilder.buildErrorRpcResp("获取工作流可操作状态失败!");
					}
					RpcResponse<Map> userInfo = userBaseQueryService.getUserByUserId(userId);
					if (userInfo.isSuccess()) {
						page.get(i).put("createBy", userInfo.getSuccessValue().get("userName"));
					}
					RpcResponse<Map<String, Object>> factoryInfo = factoryQueryService
							.findById(page.get(i).get("factoryId").toString());
					if (factoryInfo.isSuccess()) {
						page.get(i).put("factoryName", factoryInfo.getSuccessValue().get("factoryName"));
					} else {
						page.get(i).put("factoryName", "");
						logger.warn("[queryAgendaOrProcessFaultOrderList()->warn:获取故障工单id为:" + page.get(i).get("id")
								+ "的厂家名称失败]");
					}
					RpcResponse<FaultOrderProcessType> faultOrderProcessTypeInfo = faultOrderProcessTypeQueryService
							.findById(page.get(i).get("faultType").toString());
					if (faultOrderProcessTypeInfo.isSuccess() && faultOrderProcessTypeInfo.getSuccessValue() != null) {
						page.get(i).put("faultOrderProcessTypeName",
								faultOrderProcessTypeInfo.getSuccessValue().getName());
					} else {
						page.get(i).put("faultOrderProcessTypeName", "");
						logger.warn("[queryAgendaOrProcessFaultOrderList()->warn:获取故障工单id为:" + page.get(i).get("id")
								+ "的类型失败]");
					}
					RpcResponse<FaultOrderProcessState> faultOrderProcessStateInfo = faultOrderProcessStateQueryService
							.findBySign(page.get(i).get("processState").toString());
					FaultOrderProcessState successValue = faultOrderProcessStateInfo.getSuccessValue();
					if (null == successValue) {
						logger.warn("获取工单流程状态失败:" + page.get(i).get("id"));
						continue;
					}
					if (faultOrderProcessStateInfo.isSuccess()) {
						page.get(i).put("faultOrderProcessStateName", successValue.getName());
					} else {
						page.get(i).put("faultOrderProcessStateName", "");
						logger.warn("[queryAgendaOrProcessFaultOrderList()->warn:获取故障工单id为:" + page.get(i).get("id")
								+ "的状态失败]");
					}
				}
			}

			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			logger.info(String.format("[getFaultOrderList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getFaultOrderList()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> queryAgendaOrProcessFaultOrderList(JSONObject paramInfo) {
		logger.info(String.format("[queryAgendaOrProcessFaultOrderList()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.USERID))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：用户id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(OrderConstants.MODEL_TYPE))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：模块类型不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("模块类型不能为空！");
			}

			JSONObject jsonParam = new JSONObject();
			jsonParam.put(SimpleOrderConstants.TYPE, "manualFailureProcess");
			jsonParam.put(SimpleOrderConstants.USERID, paramInfo.getString(SimpleOrderConstants.USERID));

			JSONObject jsonParam2 = new JSONObject();
			jsonParam2.put(SimpleOrderConstants.TYPE, "alarmFailureProcess");
			jsonParam2.put(SimpleOrderConstants.USERID, paramInfo.getString(SimpleOrderConstants.USERID));

			String modelType = paramInfo.getString("modelType");
			RpcResponse<List<Map<String, Object>>> result = null;
			RpcResponse<List<Map<String, Object>>> result2 = null;
			// 判断查询模块（noDone:待办流程列表 haveDone:历史流程列表）
			if (OrderConstants.NOT_DONE.equals(modelType)) {
				result = activityProgressQuery.getAllACTProgress(jsonParam);
				result2 = activityProgressQuery.getAllACTProgress(jsonParam2);
				// 调用工作流,查询我经办过的
			} else if (OrderConstants.HAVE_DONE.equals(modelType)) {
				// 增加剔除的节点数据
				jsonParam.put("nodeId", AlarmOrderConstants.PROCCESS_NODE_REMOVEN_SIGN_NEW_FAU);
				jsonParam2.put("nodeId", AlarmOrderConstants.PROCCESS_NODE_REMOVEN_SIGN_ALA_FAU);
				result = activityProgressQuery.getAllHIProgress(jsonParam);
				result2 = activityProgressQuery.getAllHIProgress(jsonParam2);
			}
			if (null == result || result2 == null) {
				logger.error("通过工作流查询我经办过的历史流程失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			if (!result.isSuccess() || null == result.getSuccessValue()) {
				logger.error("通过工作流查询我经办过的历史流程失败--->" + result.getMessage());
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			if (!result2.isSuccess() || null == result2.getSuccessValue()) {
				logger.error("通过工作流查询我经办过的历史流程失败--->" + result2.getMessage());
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			// 获取代办或者经办过的工单id集合
			List<Map<String, Object>> list = result.getSuccessValue();
			List<Map<String, Object>> list2 = result2.getSuccessValue();
			Set<String> set = new HashSet<>();
			if (null != list && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).get("id") == null) {
						continue;
					}
					// 工单id
					set.add(list.get(i).get("id") + "");
				}
			}
			if (null != list2 && list2.size() > 0) {
				for (int i = 0; i < list2.size(); i++) {
					// 工单id
					set.add(list2.get(i).get("id") + "");
				}
			}
			if (set == null || set.size() == 0) {
				logger.info("[queryAgendaOrProcessFaultOrderList()->info:获取待办故障工单:没有需要待办/已办理的工单");
				return RpcResponseBuilder.buildSuccessRpcResp("没有需要待办/已办理的工单", new PageInfo<>());
			}
			Map map = JSONObject.toJavaObject(paramInfo, Map.class);
			map.put("set", set);

			int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> page = faultOrderProcessQueryRepository.queryAgendaOrProcessFaultOrderList(map);

			// 查询发起人名称
			if (null != page && page.size() > 0) {
				queryStarterName(page);
			}

			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			logger.info(String.format("[queryAgendaOrProcessFaultOrderList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("queryAgendaOrProcessFaultOrderList()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @Description:
	 * @param page
	 * @throws Exception
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void queryStarterName(List<Map<String, Object>> page) throws Exception {
		for (int i = 0; i < page.size(); i++) {
			String userId = page.get(i).get(SimpleOrderConstants.USERID).toString();
			RpcResponse<Map> userInfo = userBaseQueryService.getUserByUserId(userId);
			if (userInfo.isSuccess() && userInfo.getSuccessValue() != null) {
				page.get(i).put(SimpleOrderConstants.CREATEBY, userInfo.getSuccessValue().get("userName"));
			}
			RpcResponse<Map<String, Object>> factoryInfo = factoryQueryService
					.findById(page.get(i).get("factoryId").toString());
			if (factoryInfo.isSuccess()) {
				page.get(i).put("factoryName", factoryInfo.getSuccessValue().get("factoryName"));
			} else {
				page.get(i).put("factoryName", "");
				logger.warn(
						"[queryAgendaOrProcessFaultOrderList()->warn:获取故障工单id为:" + page.get(i).get("id") + "的厂家名称失败]");
			}

			RpcResponse<FaultOrderProcessType> faultOrderProcessTypeInfo = faultOrderProcessTypeQueryService
					.findById(page.get(i).get("faultType").toString());
			if (faultOrderProcessTypeInfo.isSuccess()) {
				page.get(i).put("faultOrderProcessTypeName", faultOrderProcessTypeInfo.getSuccessValue().getName());
			} else {
				page.get(i).put("faultOrderProcessTypeName", "");
				logger.warn(
						"[queryAgendaOrProcessFaultOrderList()->warn:获取故障工单id为:" + page.get(i).get("id") + "的类型失败]");
			}

			RpcResponse<FaultOrderProcessState> faultOrderProcessStateInfo = faultOrderProcessStateQueryService
					.findBySign(page.get(i).get("processState").toString());
			FaultOrderProcessState successValue = faultOrderProcessStateInfo.getSuccessValue();
			if (faultOrderProcessStateInfo.isSuccess() && null != successValue) {
				page.get(i).put("faultOrderProcessStateName", successValue.getName());
			} else {
				page.get(i).put("faultOrderProcessStateName", "");
				logger.warn(
						"[queryAgendaOrProcessFaultOrderList()->warn:获取故障工单id为:" + page.get(i).get("id") + "的状态失败]");
			}
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#findOrderNumber(java.lang.String)
	 */
	@Override
	public RpcResponse<String> findFaultOrderNumber(String accessSecret) throws Exception {
		logger.info(String.format("[findFaultOrderNumber()方法执行开始...,参数：【%s】]", accessSecret));

		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[findOrderNumber()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}

			String orderNumber = faultOrderProcessQueryRepository.findOrderNumber(accessSecret);
			logger.info(String.format("[findFaultOrderNumber()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, orderNumber);

		} catch (Exception e) {
			logger.error("findOrderNumber()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}

	/*	*//**
			 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#countFaultOrderInfoByAS(java.lang.String)
			 *//*
			 * @SuppressWarnings({ "unchecked", "rawtypes" })
			 * 
			 * @Override public RpcResponse<PageInfo<CountFaultOrderDto>>
			 * countFaultOrderInfoByAS(String accessSecret, String token,
			 * Integer pageNum, Integer pageSize) { if
			 * (StringUtils.isBlank(accessSecret)) { logger.error(
			 * "[countFaultOrderInfoByAS()->invalid：接入方密钥不能为空！]"); return
			 * RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！"); } if
			 * (StringUtils.isBlank(token)) {
			 * logger.error("[countFaultOrderInfoByAS()->invalid：token为空！]") ; }
			 * try { // 开始分页 PageHelper.startPage(pageNum, pageSize); //
			 * 统计故障工单部分所需信息 List<CountFaultOrderDto> countFaultOrderInfoByAS =
			 * faultOrderProcessQueryRepository
			 * .countFaultOrderInfoByAS(accessSecret); if (null ==
			 * countFaultOrderInfoByAS) { logger.error("统计故障工单信息失败!返回值为null");
			 * return RpcResponseBuilder.buildErrorRpcResp("统计故障工单信息失败!"); }
			 * PageInfo<CountFaultOrderDto> result = new
			 * PageInfo<>(countFaultOrderInfoByAS); if
			 * (countFaultOrderInfoByAS.isEmpty()) { logger.info("统计故障工单信息成功!");
			 * return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.
			 * INFO_SERCH_SUCCESS, result); } //
			 * 查询故障工单处理人姓名,电话,id等信息,(处理人是实际处理故障工单的人) for (CountFaultOrderDto
			 * countFaultOrderDto : countFaultOrderInfoByAS) { if (null ==
			 * countFaultOrderDto) { continue; } // 初始化处理人姓名,电话以及id,避免返回null
			 * countFaultOrderDto.setPersonName("");
			 * countFaultOrderDto.setPhoneNumber("");
			 * countFaultOrderDto.setPersonId(""); String processId =
			 * countFaultOrderDto.getProcessId(); if
			 * (StringUtils.isBlank(processId)) { continue; } else { JSONObject
			 * json = new JSONObject(); json.put("processId", processId); //
			 * 查询流程处理状况 RpcResponse<Map<String, Object>> nodeInfo =
			 * activityProgressQuery.getNodeInfo(json); Map<String, Object>
			 * successValue = nodeInfo.getSuccessValue(); if (null ==
			 * successValue || successValue.isEmpty()) { continue; } if
			 * (successValue.containsKey("hisExcuList") && null !=
			 * successValue.get("hisExcuList")) { List<Map> list = (List<Map>)
			 * successValue.get("hisExcuList"); for (Map map : list) { if (null
			 * == map) { continue; } Object node = map.get("node"); if (null ==
			 * node || StringUtils.isBlank("" + node)) { continue; } //
			 * 获取处理人(实际处理故障工单的人) if ("厂商人员".equals(node)) { Object variable =
			 * map.get("variable"); if (null == variable ||
			 * StringUtils.isBlank("" + variable)) { continue; } else {
			 * Map<String, Object> variableInfo = (Map<String, Object>)
			 * map.get("variable"); Object userId = variableInfo.get("userId");
			 * if (null == userId || StringUtils.isBlank("" + userId)) {
			 * logger.error("userId为" + userId); continue; }
			 * 
			 * countFaultOrderDto.setPersonId("" + userId);
			 * 
			 * if (StringUtils.isBlank(ip)) { logger.error(
			 * "countFaultOrderInfoByAS()-->获取ip失败,未能访问interGateway,处理人信息为空" );
			 * } else { String httpValue = InterGatewayUtil.getHttpValueByGet(
			 * InterGatewayConstants.U_PERSONINFO + userId, ip, token); if
			 * (StringUtils.isBlank(httpValue)) {
			 * logger.error("访问interGateway查询人员信息返回值为null"); continue; } boolean
			 * notMatchJson = ParamChecker.isNotMatchJson(httpValue); if
			 * (notMatchJson) { logger.error("interGateway返回信息不符合json格式");
			 * continue; } JSONObject resultJson =
			 * JSONObject.parseObject(httpValue);
			 * 
			 * Object mobile = resultJson.get("mobile"); if (null != mobile &&
			 * !StringUtils.isBlank("" + mobile)) {
			 * countFaultOrderDto.setPhoneNumber("" + mobile); } Object userName
			 * = resultJson.get("userName"); if (null != userName &&
			 * !StringUtils.isBlank("" + userName)) {
			 * countFaultOrderDto.setPersonName("" + userName); } }
			 * 
			 * } }
			 * 
			 * } }
			 * 
			 * } }
			 * 
			 * logger.info("故障工单信息统计成功!"); return
			 * RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.
			 * INFO_SERCH_SUCCESS, result);
			 * 
			 * } catch (Exception e) {
			 * logger.error("countFaultOrderInfoByAS()->Exception:", e); return
			 * RpcResponseBuilder.buildErrorRpcResp(e.getMessage()); } }
			 */



	/**
	 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#countFaultOrderInfoByAS(java.lang.String)
	 */
	@Override
	public RpcResponse<PageInfo<CountFaultOrderDto>> countFaultOrderInfoByAccessSecret(JSONObject json) {
		logger.info(String.format("[countFaultOrderInfoByAccessSecret()方法执行开始...,参数：【%s】]", json));
		try {
			RpcResponse<PageInfo<CountFaultOrderDto>> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"countFaultOrderInfoByAccessSecret", json, FaultOrderCountContants.ACCESS_SECRET,
					FaultOrderCountContants.PAGE_NUM, FaultOrderCountContants.PAGE_SIZE);
			if (null != checkBusinessKey) {
				logger.error(checkBusinessKey.getMessage());
				return RpcResponseBuilder.buildErrorRpcResp(checkBusinessKey.getMessage());
			}
			RpcResponse<PageInfo<CountFaultOrderDto>> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"countFaultOrderInfoByAccessSecret", json, FaultOrderCountContants.SEARCH_KEY,
					FaultOrderCountContants.START_TIME, FaultOrderCountContants.END_TIME,
					FaultOrderCountContants.FAULTTYPE_ID);
			if (null != containsParamKey) {
				logger.error(containsParamKey.getMessage());
				return RpcResponseBuilder.buildErrorRpcResp(containsParamKey.getMessage());
			}
			Integer pageNum = json.getInteger(FaultOrderCountContants.PAGE_NUM);
			if (null == pageNum) {
				pageNum = 1;
			}
			Integer pageSize = json.getInteger(FaultOrderCountContants.PAGE_SIZE);
			if (null == pageSize) {
				pageSize = 10;
			}
			// 开始分页
			PageHelper.startPage(pageNum, pageSize);
			// 统计故障工单部分所需信息
			List<CountFaultOrderDto> countFaultOrderInfoByAS = faultOrderProcessQueryRepository
					.countFaultOrderInfoByAS(json);
			if (null == countFaultOrderInfoByAS) {
				logger.error("统计故障工单信息失败!返回值为null");
				return RpcResponseBuilder.buildErrorRpcResp("统计故障工单信息失败!");
			}
			PageInfo<CountFaultOrderDto> result = new PageInfo<>(countFaultOrderInfoByAS);
			if (countFaultOrderInfoByAS.isEmpty()) {
				logger.info("统计故障工单信息成功!");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, result);
			}

			logger.info("故障工单信息统计成功!");
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, result);

		} catch (Exception e) {
			logger.error("countFaultOrderInfoByAccessSecret()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<FaultOrderHistogramDto>> faultOrder2Histogram(JSONObject json) {
		logger.info(String.format("[faultOrder2Histogram()方法执行开始...,参数：【%s】]", json));
		try {
			if (StringUtils.isBlank(json.getString(FaultOrderCountContants.ACCESS_SECRET))) {
				logger.error("faultOrder2Histogram()-->接入方密钥不能为空");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
			}
			List<FaultOrderHistogramDto> result = faultOrderProcessQueryRepository.faultOrder2Histogram(json);
			if (null == result) {
				logger.error("faultOrder2Histogram()-->柱状图数据查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("柱状图数据查询失败");
			} else {
				logger.info("faultOrder2Histogram()-->柱状图数据查询成功");
				return RpcResponseBuilder.buildSuccessRpcResp("柱状图数据查询成功", result);
			}
		} catch (Exception e) {
			logger.error("faultOrder2Histogram()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#findFaultOrderByDeviceId(java.util.List)
	 */
	@Override
	public RpcResponse<List<String>> findFaultOrderByDeviceId(List<String> deviceIds) {
		logger.info(String.format("[findFaultOrderByDeviceId()方法执行开始...,参数：【%s】]", deviceIds));
		try {
			if (deviceIds == null || deviceIds.isEmpty()) {
				logger.error("[findFaultOrderByDeviceId()-->设备ids不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备ids不能为空!");
			}
			// 查询这些设备id是否已经存在工单，如果存在了，返回那些存在工单的设备id
			List<String> findDeviceIds = Lists.newArrayList();
			for (String deviceId : deviceIds) {
				String findDeviceId = faultOrderProcessQueryRepository.queryFaultOrderByDeviceId(deviceId);
				if (!StringUtils.isBlank(findDeviceId)) {
					findDeviceIds.add(findDeviceId);
				}
			}

			logger.info(String.format("[findFaultOrderByDeviceId()->suc:%s,%s]", PublicConstants.PARAM_SUCCESS,
					findDeviceIds));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, findDeviceIds);

		} catch (Exception e) {
			logger.error("findFaultOrderByDeviceId()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#findFaultOrderByDeviceIdTo(java.lang.String)
	 */
	@Override
	public RpcResponse<String> findFaultOrderByDeviceIdTo(String deviceId) {
		logger.info(String.format("[findFaultOrderByDeviceId()方法执行开始...,参数：【%s】]", deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[findFaultOrderByDeviceIdTo()-->设备ids不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备ids不能为空!");
			}

			String faultOrderDeviceId = faultOrderProcessQueryRepository.queryFaultOrderByDeviceIdTo(deviceId);

			logger.info(String.format("[findFaultOrderByDeviceId()->suc:%s,%s]", PublicConstants.PARAM_SUCCESS,
					faultOrderDeviceId));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, faultOrderDeviceId);

		} catch (Exception e) {
			logger.error("findFaultOrderByDeviceIdTo()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#countFaultOrderNumByOrganizationAndType(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public RpcResponse<Object> countFaultOrderNumByOrganizationAndType(JSONObject jsonObject) {
		logger.info(String.format("[countFaultOrderByOrganizationAndType()->进入方法,参数%s]", jsonObject.toJSONString()));
		try {
			RpcResponse<Object> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"countFaultOrderNumByOrganizationAndType", jsonObject, PublicConstants.ACCESSSECRET,
					PublicConstants.PUBLIC_PAGE_NUM, PublicConstants.PUBLIC_PAGE_SIZE);

			if (checkBusinessKey != null) {
				logger.error(checkBusinessKey.getMessage());
				return checkBusinessKey;
			}
			RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"countFaultOrderNumByOrganizationAndType", jsonObject, CommonConstants.ORGANIZATION_ID,
					CommonConstants.STARTTIME, CommonConstants.ENDTIME);
			if (containsParamKey != null) {
				logger.error(containsParamKey.getMessage());
				return containsParamKey;
			}

			Integer pageNum = jsonObject.getInteger(PublicConstants.PUBLIC_PAGE_NUM);
			if (null == pageNum) {
				pageNum = 1;
			}
			Integer pageSize = jsonObject.getInteger(PublicConstants.PUBLIC_PAGE_SIZE);
			if (null == pageSize) {
				pageSize = 10;
			}

			String token = jsonObject.getString(InterGatewayConstants.TOKEN);
			// 获取该组织和子组织信息
			getOwnAndSonInfo(jsonObject, token);

			// 开始分页
			PageHelper.startPage(pageNum, pageSize);

			Map<String, Object> queryMap = jsonObject.toJavaObject(Map.class);

			List<Map<String, Object>> countFaultOrderNumByOrganizationAndType = faultOrderProcessQueryRepository
					.countFaultOrderNumByOrganizationAndType(queryMap);
			if (countFaultOrderNumByOrganizationAndType == null) {
				logger.error("[countFaultOrderNumByOrganizationAndType->error]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			// 查询成功后，需要将结果中的组织id替换为组织名

			PageInfo<Map<String, Object>> res = new PageInfo<>(countFaultOrderNumByOrganizationAndType);

			List<String> allOrgIds = new ArrayList<>();
			// 获取所有的组织id
			for (Map<String, Object> map : countFaultOrderNumByOrganizationAndType) {
				allOrgIds.add(map.get(CommonConstants.ORGANIZATION_ID).toString());
			}

			// 组织集合为空，说明查询结果为0，
			if (allOrgIds.size() == 0) {
				logger.info("[countFaultOrderNumByOrganizationAndType->success,查询无结果]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);
			}
			// 通过interGateway获取组织名
			if (!StringUtils.isBlank(ip)) {
				JSONObject orgIdJson = new JSONObject();
				StringBuffer orgIdStr = new StringBuffer();
				for (int i = 0; i < allOrgIds.size(); i++) {
					if (i == allOrgIds.size()) {
						orgIdStr.append(allOrgIds.get(i));
					}
					orgIdStr.append(allOrgIds.get(i) + ',');
				}
				orgIdJson.put(InterGatewayConstants.ORGANIZATION_IDS, orgIdStr);

				String httpValueByPost = InterGatewayUtil
						.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, orgIdJson, ip, token);
				if (null == httpValueByPost) {
					logger.error("通过interGateway查询组织名失败,直接返回告警工单统计数据");
					return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);
				} else {
					// 将统计结果的组织id与查询到的组织对比,获得组织名
					JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
					for (Map<String, Object> map : countFaultOrderNumByOrganizationAndType) {
						if (null != map) {
							String orgId = map.get(CommonConstants.ORGANIZATION_ID).toString();
							if (null != httpValueJson.getString(orgId)) {
								map.put(AlarmOrderCountConstants.ORGANIZATION_NAME, httpValueJson.getString(orgId));
							}
						}
					}
				}
				logger.info("告警工单信息统计成功!");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, res);
			} else {
				logger.error("ip注入获取失败,直接返回告警工单统计数据");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);
			}

		} catch (Exception e) {
			logger.error("[countFaultOrderNumByOrganizationAndType()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@SuppressWarnings("rawtypes")
	private void getOwnAndSonInfo(JSONObject json, String token) {
		logger.info(String.format("[getOwnAndSonInfo()方法执行开始...,参数：【%s】【%s】]", json, token));
		// 如果组织id不为空,查询其组织信息及子组织信息,获取到id和name,存储起来.等待查询结果然后匹配
		if (!StringUtils.isBlank(json.getString(CommonConstants.ORGANIZATION_ID))) {
			List<String> organizationIdList = Lists.newArrayList();
			String organizationId = json.getString(CommonConstants.ORGANIZATION_ID);

			if (!StringUtils.isBlank(ip)) {
				String httpValueByGet = InterGatewayUtil
						.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip, token);
				if (null == httpValueByGet) {
					logger.error("[countAllAlarmOrder()->invalid：组织查询失败!]");
					organizationIdList.add(organizationId);
				} else {
					JSONArray jsonArray = JSON.parseArray(httpValueByGet);
					List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
					for (Map map : organizationInfoList) {
						organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
					}
				}
			} else {
				// 获取IP失败或者为null则不访问
				logger.error("[getOwnAndSonInfo()->invalid：IP获取失败,组织查询失败!]");
				organizationIdList.add(organizationId);
			}

			json.replace(CommonConstants.ORGANIZATION_ID, organizationIdList);
		}
		logger.info(String.format("[getOwnAndSonInfo()方法执行结束!]"));
	}



	/**
	 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#queryAgendaOrProcessFaultOrderListNew(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> queryAgendaOrProcessFaultOrderListNew(JSONObject paramInfo) {
		logger.info(String.format("[queryAgendaOrProcessFaultOrderList()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.USERID))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：用户id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(OrderConstants.MODEL_TYPE))) {
				logger.error("[queryAgendaOrProcessFaultOrderList()->invalid：模块类型不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("模块类型不能为空！");
			}

			JSONObject jsonParam = new JSONObject();
			jsonParam.put(SimpleOrderConstants.TYPE, "manualFailureProcess");
			jsonParam.put(SimpleOrderConstants.USERID, paramInfo.getString(SimpleOrderConstants.USERID));

			JSONObject jsonParam2 = new JSONObject();
			jsonParam2.put(SimpleOrderConstants.TYPE, "alarmFailureProcess");
			jsonParam2.put(SimpleOrderConstants.USERID, paramInfo.getString(SimpleOrderConstants.USERID));

			String modelType = paramInfo.getString("modelType");
			RpcResponse<List<Map<String, Object>>> result = null;
			RpcResponse<List<Map<String, Object>>> result2 = null;
			// 判断查询模块（noDone:待办流程列表 haveDone:历史流程列表）
			if (OrderConstants.NOT_DONE.equals(modelType)) {
				result = activityProgressQuery.getAllACTProgress(jsonParam);
				result2 = activityProgressQuery.getAllACTProgress(jsonParam2);
				// 调用工作流,查询我经办过的
			} else if (OrderConstants.HAVE_DONE.equals(modelType)) {
				// 增加剔除的节点数据
				jsonParam.put("nodeId", AlarmOrderConstants.PROCCESS_NODE_REMOVEN_SIGN_NEW_FAU);
				jsonParam2.put("nodeId", AlarmOrderConstants.PROCCESS_NODE_REMOVEN_SIGN_ALA_FAU);
				result = activityProgressQuery.getAllHIProgress(jsonParam);
				result2 = activityProgressQuery.getAllHIProgress(jsonParam2);
			}
			if (null == result || result2 == null) {
				logger.error("通过工作流查询我经办过的历史流程失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			if (!result.isSuccess() || null == result.getSuccessValue()) {
				logger.error("通过工作流查询我经办过的历史流程失败--->" + result.getMessage());
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			if (!result2.isSuccess() || null == result2.getSuccessValue()) {
				logger.error("通过工作流查询我经办过的历史流程失败--->" + result2.getMessage());
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			// 获取代办或者经办过的工单id集合
			List<Map<String, Object>> list = result.getSuccessValue();
			List<Map<String, Object>> list2 = result2.getSuccessValue();
			Set<String> set = new HashSet<>();
			if (null != list && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).get("id") == null) {
						continue;
					}
					// 工单id
					set.add(list.get(i).get("id") + "");
				}
			}
			if (null != list2 && list2.size() > 0) {
				for (int i = 0; i < list2.size(); i++) {
					// 工单id
					set.add(list2.get(i).get("id") + "");
				}
			}
			if (set == null || set.size() == 0) {
				logger.info("[queryAgendaOrProcessFaultOrderList()->info:获取待办故障工单:没有需要待办/已办理的工单");
				return RpcResponseBuilder.buildSuccessRpcResp("没有需要待办/已办理的工单", new PageInfo<>());
			}
			Map map = JSONObject.toJavaObject(paramInfo, Map.class);
			map.put("set", set);

			int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> page = faultOrderProcessQueryRepository
					.queryAgendaOrProcessFaultOrderListNew(map);

			// 查询发起人名称
			if (null != page && page.size() > 0) {
				queryStarterName(page);
			}

			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			logger.info(String.format("[queryAgendaOrProcessFaultOrderList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("queryAgendaOrProcessFaultOrderList()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#getFaultOrderListNew(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getFaultOrderListNew(JSONObject paramInfo) {
		logger.info(String.format("[getFaultOrderList()方法执行开始...,参数：【%s】]", paramInfo));
		try {

			if (paramInfo == null) {
				logger.error("[getFaultOrderList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getFaultOrderList()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getFaultOrderList()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getFaultOrderList()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getFaultOrderList()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getFaultOrderList()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.USERID))) {
				logger.error("[getFaultOrderList()->invalid：用户id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空！");
			}

			Map map = new HashMap<>(16);
			map.put("accessSecret", paramInfo.getString("accessSecret"));
			map.put("userId", paramInfo.getString("userId"));
			map.put("appointStartTime", paramInfo.getString("appointStartTime"));
			map.put("appointEndTime", paramInfo.getString("appointEndTime"));
			map.put("faultType", paramInfo.getString("faultType"));
			map.put("processState", paramInfo.getString("processState"));
			map.put("selectKey", paramInfo.getString("selectKey"));
			map.put("manager", paramInfo.getString("manager"));
			map.put("factoryId", paramInfo.getString("factoryId"));

			int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> page = faultOrderProcessQueryRepository.getFaultOrderListNew(map);

			// 查询发起人名称
			if (null != page && page.size() > 0) {
				String userId = page.get(0).get("userId").toString();
				for (int i = 0; i < page.size(); i++) {
					JSONObject jsonParam = new JSONObject();
					jsonParam.put("processId", page.get(i).get("processId"));
					RpcResponse<Map<String, Object>> result = activityProgressQuery.getJudgmentProcess(jsonParam);
					if (result.isSuccess()) {
						page.get(i).put("operaType", result.getSuccessValue().get("state"));
					} else {
						return RpcResponseBuilder.buildErrorRpcResp("获取工作流可操作状态失败!");
					}
					RpcResponse<Map> userInfo = userBaseQueryService.getUserByUserId(userId);
					if (userInfo.isSuccess()) {
						page.get(i).put("createBy", userInfo.getSuccessValue().get("userName"));
					}
					RpcResponse<Map<String, Object>> factoryInfo = factoryQueryService
							.findById(page.get(i).get("factoryId").toString());
					if (factoryInfo.isSuccess()) {
						page.get(i).put("factoryName", factoryInfo.getSuccessValue().get("factoryName"));
					} else {
						page.get(i).put("factoryName", "");
						logger.warn("[queryAgendaOrProcessFaultOrderList()->warn:获取故障工单id为:" + page.get(i).get("id")
								+ "的厂家名称失败]");
					}
					RpcResponse<FaultOrderProcessType> faultOrderProcessTypeInfo = faultOrderProcessTypeQueryService
							.findById(page.get(i).get("faultType").toString());
					if (faultOrderProcessTypeInfo.isSuccess() && faultOrderProcessTypeInfo.getSuccessValue() != null) {
						page.get(i).put("faultOrderProcessTypeName",
								faultOrderProcessTypeInfo.getSuccessValue().getName());
					} else {
						page.get(i).put("faultOrderProcessTypeName", "");
						logger.warn("[queryAgendaOrProcessFaultOrderList()->warn:获取故障工单id为:" + page.get(i).get("id")
								+ "的类型失败]");
					}
					RpcResponse<FaultOrderProcessState> faultOrderProcessStateInfo = faultOrderProcessStateQueryService
							.findBySign(page.get(i).get("processState").toString());
					FaultOrderProcessState successValue = faultOrderProcessStateInfo.getSuccessValue();
					if (null == successValue) {
						logger.warn("获取工单流程状态失败:" + page.get(i).get("id"));
						continue;
					}
					if (faultOrderProcessStateInfo.isSuccess()) {
						page.get(i).put("faultOrderProcessStateName", successValue.getName());
					} else {
						page.get(i).put("faultOrderProcessStateName", "");
						logger.warn("[queryAgendaOrProcessFaultOrderList()->warn:获取故障工单id为:" + page.get(i).get("id")
								+ "的状态失败]");
					}
				}
			}

			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			logger.info(String.format("[getFaultOrderList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getFaultOrderList()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#faultOrderList(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> faultOrderList(JSONObject paramInfo) {
		logger.info(String.format("[faultOrderList()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[faultOrderList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[faultOrderList()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[faultOrderList()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[faultOrderList()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[faultOrderList()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[faultOrderList()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("markName", paramInfo.getString("markName"));
			map.put("processState", paramInfo.getString("processState"));
			map.put("accessSecret", paramInfo.getString("accessSecret"));
			map.put("facilitiesCode", paramInfo.getString("facilitiesCode"));
			map.put("faultProcessType", paramInfo.getString("faultProcessType"));
			int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> page = faultOrderProcessQueryRepository.getFaultOrderListInfoNew(map);
			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			logger.info(String.format("[faultOrderList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);

		} catch (Exception e) {
			logger.error("faultOrderList()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}



	/**
	 * @see com.run.locman.api.query.service.FaultOrderProcessQueryService#faultOrderStateCount(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> faultOrderStateCount(JSONObject paramInfo) {
		logger.info(String.format("[faultOrderStateCount()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[faultOrderStateCount()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[faultOrderStateCount()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("accessSecret", paramInfo.getString("accessSecret"));
			List<Map<String, Object>> countInfo = faultOrderProcessQueryRepository.faultOrderStateCount(map);

			logger.info(String.format("[faultOrderStateCount()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, countInfo);

		} catch (Exception e) {
			logger.error("faultOrderStateCount()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}

}
