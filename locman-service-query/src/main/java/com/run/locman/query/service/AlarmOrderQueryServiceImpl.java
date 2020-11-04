/*
 * File name: AlarmOrderQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2018年1月18日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;




import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.CountAlarmOrderDto;
import com.run.locman.api.entity.AlarmCountDetails;
import com.run.locman.api.entity.AlarmOrderCount;
import com.run.locman.api.entity.Pages;
import com.run.locman.api.query.repository.AlarmOrderQueryRepository;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.AlarmOrderCountConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.PublicConstants;
import com.run.usc.base.query.AccSourceBaseQueryService;

/**
 * @Description:告警信息查询
 * @author: lkc
 * @version: 1.0, 2017年11月2日
 */

public class AlarmOrderQueryServiceImpl implements AlarmOrderQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private AlarmOrderQueryRepository	alarmOrderQueryRepository;

	@Autowired
	private AccSourceBaseQueryService	accSourceQuery;

	@Value("${api.host}")
	private String						ip;



	@Override
	public RpcResponse<List<Map<String, Object>>> getState(int type) {
		logger.info(String.format("[getState()方法执行开始...,参数：【%s】]", type));
		try {
			List<Map<String, Object>> selectState = alarmOrderQueryRepository.getSelectState(type);
			logger.info(MessageConstant.INFO_SERCH_SUCCESS);
			logger.info(String.format("[getState()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, selectState);
		} catch (Exception e) {
			logger.error("getState()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getAlarmOrderBypage(JSONObject orderInfo) {
		logger.info(String.format("[getAlarmOrderBypage()方法执行开始...,参数：【%s】]", orderInfo));
		try {

			if (orderInfo == null) {
				logger.error("[getAlarmOrderBypage()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!orderInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(orderInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!orderInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getAlarmOrderBypage()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(orderInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmOrderBypage()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (!orderInfo.containsKey(AlarmInfoConstants.USC_ACCESS_SECRET)
					|| StringUtils.isBlank(orderInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getAlarmOrderBypage()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(orderInfo.getString(AlarmInfoConstants.USER_ID))) {
				logger.error("[getAlarmOrderBypage()->invalid：人员id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("人员id不能为空！");
			}

			String orgId = orderInfo.getString("orgId");
			String token = orderInfo.getString(InterGatewayConstants.TOKEN);

			if (!StringUtils.isBlank(token)) {

				// 存储组织id
				List<String> organizationIdList = Lists.newArrayList();
				// 存储组织id,name
				Map<String, String> organizationInfo = Maps.newHashMap();
				getOwnAndSonOrgNameId(orderInfo, orgId, token, organizationIdList, organizationInfo, "orgId",
						"notClaimAlarmOrder");

				Map<String, Object> javaObject = JSONObject.toJavaObject(orderInfo, Map.class);

				int pageNo = orderInfo.getIntValue(FacilitiesContants.PAGE_NO);
				int pageSize = orderInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
				PageHelper.startPage(pageNo, pageSize);

				// 新增 查询条件userId不为null --》 为分化接受工单以及我的工单
				List<Map<String, Object>> page = alarmOrderQueryRepository.getAlarmOrderByAccessSercret(javaObject);

				// 封装组织信息
				// pageOrg(page);
				// 封装工单状态
				List<Map<String, Object>> selectState = alarmOrderQueryRepository.getSelectState(1);
				for (Map<String, Object> map : page) {
					map.put("processStateName", getStateName((String) map.get("processState"), selectState));
				}

				return matchOrgName(token, organizationIdList, organizationInfo, page, "getAlarmOrderTodoByPage");
			} else {
				// 无TOKEN时,调用原本的代码
				Map<String, Object> javaObject = JSONObject.toJavaObject(orderInfo, Map.class);
				// 判断当前人员是否有权限

				int pageNo = orderInfo.getIntValue(FacilitiesContants.PAGE_NO);
				int pageSize = orderInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
				PageHelper.startPage(pageNo, pageSize);

				// 新增 查询条件userId不为null --》 为分化接受工单以及我的工单
				List<Map<String, Object>> page = alarmOrderQueryRepository.getAlarmOrderByAccessSercret(javaObject);

				// 封装组织信息
				pageOrg(page);
				// 封装工单状态
				List<Map<String, Object>> selectState = alarmOrderQueryRepository.getSelectState(1);
				for (Map<String, Object> map : page) {
					map.put("processStateName", getStateName((String) map.get("processState"), selectState));
				}
				PageInfo<Map<String, Object>> info = new PageInfo<>(page);
				logger.info(String.format("[getAlarmOrderBypage()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);

			}

		} catch (Exception e) {
			logger.error("getAlarmOrderBypage()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	private String getStateName(String sign, List<Map<String, Object>> mapList) {
		logger.info(String.format("[getStateName()方法执行开始...,参数：【%s】【%s】]", sign, mapList));
		for (Map<String, Object> map : mapList) {
			if (sign != null && sign.equals((String) map.get("sign"))) {
				return (String) map.get("name");
			}
		}
		return "";
	}



	@Override
	public RpcResponse<Map<String, Object>> getAlarmOrderInfoById(String orderId) {
		logger.info(String.format("[getAlarmOrderInfoById()方法执行开始...,参数：【%s】]", orderId));
		try {
			if (StringUtils.isBlank(orderId)) {
				return RpcResponseBuilder.buildErrorRpcResp("工单id不能为空！");
			}
			Map<String, Object> info = alarmOrderQueryRepository.getAlarmOrderInfoById(orderId);
			logger.info(String.format("[getAlarmOrderInfoById()方法执行结束!返回信息：【%s】]", info));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getAlarmOrderInfoById()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> getAlarmOrderInfoByFaultOrderId(String faultOrderId) {
		logger.info(String.format("[getAlarmOrderInfoByFaultOrderId()方法执行开始...,参数：【%s】]", faultOrderId));
		try {
			if (StringUtils.isBlank(faultOrderId)) {
				return RpcResponseBuilder.buildErrorRpcResp("故障工单id不能为空！");
			}
			Map<String, Object> info = alarmOrderQueryRepository.getAlarmOrderInfoByFaultOrderId(faultOrderId);
			logger.info(String.format("[getAlarmOrderInfoByFaultOrderId()方法执行结束!返回信息：【%s】]", info));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getAlarmOrderInfoByFaultOrderId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@SuppressWarnings("rawtypes")
	private void pageOrg(List<Map<String, Object>> facInfo) {
		logger.info(String.format("[pageOrg()方法执行开始...,参数：【%s】]", facInfo));
		RpcResponse<List<Map>> orgInfo = accSourceQuery
				.getListMenuByIds(UtilTool.getListNoRepeat(facInfo, FacilitiesContants.ORGANIZATION_ID));
		if (orgInfo.isSuccess()) {
			List<Map> orgLists = orgInfo.getSuccessValue();
			for (Map<String, Object> map : facInfo) {
				if (map.containsKey(FacilitiesContants.ORGANIZATION_ID)) {
					String orgId = (String) map.get(FacilitiesContants.ORGANIZATION_ID);
					map.put(FacilitiesContants.ORG_INFO,
							UtilTool.getStringByKey(orgLists, FacilitiesContants.ID, orgId));
				}
			}
		}
		logger.info(String.format("[pageOrg()方法执行结束!"));
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getAlarmOrderTodoByPage(JSONObject orderInfo, List<String> list) {
		logger.info(String.format("[getAlarmOrderTodoByPage()方法执行开始...,参数：【%s】【%s】]", orderInfo, list));
		try {

			if (orderInfo == null) {
				logger.error("[getAlarmOrderTodoByPage()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!orderInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getAlarmOrderTodoByPage()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(orderInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmOrderTodoByPage()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!orderInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getAlarmOrderTodoByPage()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(orderInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmOrderTodoByPage()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (!orderInfo.containsKey(AlarmInfoConstants.USC_ACCESS_SECRET)
					|| StringUtils.isBlank(orderInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getAlarmOrderTodoByPage()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}

			String orgId = orderInfo.getString("orgId");
			String token = orderInfo.getString(InterGatewayConstants.TOKEN);

			if (!StringUtils.isBlank(token)) {
				// 存储组织id
				List<String> organizationIdList = Lists.newArrayList();
				// 存储组织id,name
				Map<String, String> organizationInfo = Maps.newHashMap();
				getOwnAndSonOrgNameId(orderInfo, orgId, token, organizationIdList, organizationInfo, "orgId",
						"getAlarmOrderTodoByPage");

				int pageNo = orderInfo.getIntValue(FacilitiesContants.PAGE_NO);
				int pageSize = orderInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
				PageHelper.startPage(pageNo, pageSize);
				Map javaObject = JSONObject.toJavaObject(orderInfo, Map.class);
				javaObject.put("list", list);

				// 为配合app需求，由于有两个审批状态，app端传入状态参数为"a,b",所以需要将orderState转换为list
				String orderState = orderInfo.getString("orderState");
				if (StringUtils.isBlank(orderState)) {
					javaObject.put("orderState", null);
				} else {
					String[] split = orderState.split(",");
					List<String> orderStateList = Arrays.asList(split);

					javaObject.put("orderState", orderStateList);
				}

				List<Map<String, Object>> page = alarmOrderQueryRepository.getAlarmOrderById(javaObject);

				List<Map<String, Object>> selectState = alarmOrderQueryRepository.getSelectState(1);
				for (Map<String, Object> map : page) {
					map.put("processStateName", getStateName((String) map.get("processState"), selectState));

				}
				return matchOrgName(token, organizationIdList, organizationInfo, page, "getAlarmOrderTodoByPage");

			} else {
				// 无TOKEN是调用原本的代码
				int pageNo = orderInfo.getIntValue(FacilitiesContants.PAGE_NO);
				int pageSize = orderInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
				PageHelper.startPage(pageNo, pageSize);
				Map javaObject = JSONObject.toJavaObject(orderInfo, Map.class);
				javaObject.put("list", list);

				// 为配合app需求，由于有两个审批状态，app端传入状态参数为"a,b",所以需要将orderState转换为list
				String orderState = orderInfo.getString("orderState");
				if (StringUtils.isBlank(orderState)) {
					javaObject.put("orderState", null);
				} else {
					String[] split = orderState.split(",");
					List<String> orderStateList = Arrays.asList(split);

					javaObject.put("orderState", orderStateList);
				}
				List<Map<String, Object>> page = alarmOrderQueryRepository.getAlarmOrderById(javaObject);

				PageInfo<Map<String, Object>> info = new PageInfo<>(page);
				// 封装组织信息
				pageOrg(page);
				// 封装工单状态
				List<Map<String, Object>> selectState = alarmOrderQueryRepository.getSelectState(1);
				for (Map<String, Object> map : page) {
					map.put("processStateName", getStateName((String) map.get("processState"), selectState));

				}
				logger.info(String.format("[getAlarmOrderTodoByPage()方法执行结束!"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
			}

		} catch (Exception e) {
			logger.error("getAlarmOrderTodoByPage()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmOrderQueryService#notClaimAlarmOrder(com.alibaba.fastjson.JSONObject)
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Pages<Map<String, Object>>> notClaimAlarmOrder(JSONObject orderInfo) {
		logger.info(String.format("[notClaimAlarmOrder()方法执行开始...,参数：【%s】]", orderInfo));

		try {

			// 校验基础参数
			RpcResponse<Pages<Map<String, Object>>> resBusiness = CheckParameterUtil.checkBusinessKey(logger,
					"notClaimAlarmOrder", orderInfo, AlarmInfoConstants.USC_ACCESS_SECRET);
			if (resBusiness != null) {
				return resBusiness;
			}
			RpcResponse<Pages<Map<String, Object>>> resPage = CheckParameterUtil.checkPageKey(logger,
					"notClaimAlarmOrder", orderInfo, AlarmInfoConstants.PAGE_NO, AlarmInfoConstants.PAGE_SIZE);
			if (resPage != null) {
				return resPage;
			}

			String orgId = orderInfo.getString("orgId");
			String token = orderInfo.getString(InterGatewayConstants.TOKEN);
			Pages<Map<String, Object>> pages = new Pages<Map<String, Object>>();
			int pageNo = orderInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = orderInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			int start = pages.getStart(pageNo, pageSize);

			if (!StringUtils.isBlank(token)) {
				// 存储组织id
				List<String> organizationIdList = Lists.newArrayList();
				// 存储组织id,name
				Map<String, String> organizationInfo = Maps.newHashMap();
				getOwnAndSonOrgNameId(orderInfo, orgId, token, organizationIdList, organizationInfo, "orgId",
						"notClaimAlarmOrder");

				// PageHelper.startPage(pageNo, pageSize);

				Map orderQuery = JSONObject.toJavaObject(orderInfo, Map.class);
				orderQuery.put("start", start);
				orderQuery.put("size", pageSize);
				int total = alarmOrderQueryRepository.getNotClaimAlarmOrderTotal(orderQuery);
				pages.setTotal(total);
				List<Map<String, Object>> notClaimAlarmOrder = alarmOrderQueryRepository.notClaimAlarmOrder(orderQuery);
				// 封装返回数据
				pages.setList(notClaimAlarmOrder);
				StringBuffer resultOrganizationIdsBuffer = new StringBuffer();
				for (Map<String, Object> mapInfo : notClaimAlarmOrder) {
					if (null != mapInfo) {
						// 有组织时,匹配已经获取的组织名
						if (organizationIdList.size() != 0) {
							String organizationName = organizationInfo.get("" + mapInfo.get(AlarmInfoConstants.ORG_ID));
							if (!StringUtils.isBlank(organizationName)) {
								mapInfo.put(AlarmInfoConstants.ORG_NAME, organizationName);
							} else {
								mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
							}
						} else {
							// 暂存organizationId
							resultOrganizationIdsBuffer.append(mapInfo.get(AlarmInfoConstants.ORG_ID) + ",");
						}
					}
				}
				// 按组织查询时
				if (organizationIdList.size() != 0) {
					// PageInfo<Map<String, Object>> info = new
					// PageInfo<>(notClaimAlarmOrder);
					logger.info(MessageConstant.INFO_SERCH_SUCCESS);
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
				} else {
					// 查询列表
					// 获取IP成功,查询组织名
					if (!StringUtils.isBlank(ip)) {
						JSONObject json = new JSONObject();
						int length = resultOrganizationIdsBuffer.length();
						if (length > 0) {
							StringBuffer resultOrganizationIds = resultOrganizationIdsBuffer
									.deleteCharAt(resultOrganizationIdsBuffer.lastIndexOf(","));
							json.put("organizationIds", resultOrganizationIds);
						} else {
							json.put("organizationIds", resultOrganizationIdsBuffer);
						}

						String httpValueByPost = InterGatewayUtil
								.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, json, ip, token);
						if (null == httpValueByPost) {
							// 没查到直接返回
							logger.error("[" + notClaimAlarmOrder + "()->invalid：组织查询失败!]");
							// PageInfo<Map<String, Object>> info = new
							// PageInfo<>(notClaimAlarmOrder);
							logger.info(MessageConstant.INFO_SERCH_SUCCESS);
							return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
						} else {
							JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
							for (Map<String, Object> mapInfo : notClaimAlarmOrder) {
								if (null != mapInfo) {
									if (null != mapInfo && null != httpValueJson
											.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID))) {
										mapInfo.put(AlarmInfoConstants.ORG_NAME, ""
												+ httpValueJson.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID)));
									} else {
										mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
									}
								}
							}
							// PageInfo<Map<String, Object>> info = new
							// PageInfo<>(notClaimAlarmOrder);
							logger.info(MessageConstant.INFO_SERCH_SUCCESS);
							return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
						}
					} else { // 获取IP失败,直接返回
						// PageInfo<Map<String, Object>> info = new
						// PageInfo<>(notClaimAlarmOrder);
						logger.error("获取ip失败,未能查询组织名,直接返回数据");
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
					}

				}
				/*
				 * return matchOrgName(token, organizationIdList,
				 * organizationInfo, notClaimAlarmOrder, "notClaimAlarmOrder");
				 */
				/*
				 * // 封装组织信息 pageOrg(notClaimAlarmOrder); PageInfo<Map<String,
				 * Object>> info = new PageInfo<>(notClaimAlarmOrder); return
				 * RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.
				 * INFO_SERCH_SUCCESS, info);
				 */
			} else {
				// 存储组织id
				// List<String> organizationIdList = Lists.newArrayList();
				// 存储组织id,name
				// Map<String, String> organizationInfo = Maps.newHashMap();
				// getOwnAndSonOrgNameId(orderInfo, orgId, token,
				// organizationIdList,
				// organizationInfo,"orgId","notClaimAlarmOrder");

				// PageHelper.startPage(pageNo, pageSize);
				Map orderQuery = JSONObject.toJavaObject(orderInfo, Map.class);

				orderQuery.put("start", start);
				orderQuery.put("size", pageSize);
				int total = alarmOrderQueryRepository.getNotClaimAlarmOrderTotal(orderQuery);
				pages.setTotal(total);
				List<Map<String, Object>> notClaimAlarmOrder = alarmOrderQueryRepository.notClaimAlarmOrder(orderQuery);
				pages.setList(notClaimAlarmOrder);
				// return matchOrgName(token, organizationIdList,
				// organizationInfo, notClaimAlarmOrder,"notClaimAlarmOrder");
				// 封装组织信息
				pageOrg(notClaimAlarmOrder);
				// PageInfo<Map<String, Object>> info = new
				// PageInfo<>(notClaimAlarmOrder);
				logger.info(String.format("[notClaimAlarmOrder()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);

			}

		} catch (Exception e) {
			logger.error("notClaimAlarmOrder()->exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}

	/*
	 * @SuppressWarnings("rawtypes")
	 * 
	 * @Override public RpcResponse<PageInfo<Map<String, Object>>>
	 * notClaimAlarmOrder(JSONObject orderInfo) {
	 * logger.info(String.format("[notClaimAlarmOrder()方法执行开始...,参数：【%s】]",
	 * orderInfo));
	 * 
	 * try {
	 * 
	 * // 校验基础参数 RpcResponse<PageInfo<Map<String, Object>>> resBusiness =
	 * CheckParameterUtil.checkBusinessKey(logger, "notClaimAlarmOrder",
	 * orderInfo, AlarmInfoConstants.USC_ACCESS_SECRET); if (resBusiness !=
	 * null) { return resBusiness; } RpcResponse<PageInfo<Map<String, Object>>>
	 * resPage = CheckParameterUtil.checkPageKey(logger, "notClaimAlarmOrder",
	 * orderInfo, AlarmInfoConstants.PAGE_NO, AlarmInfoConstants.PAGE_SIZE); if
	 * (resPage != null) { return resPage; }
	 * 
	 * String orgId = orderInfo.getString("orgId"); String token =
	 * orderInfo.getString(InterGatewayConstants.TOKEN);
	 * 
	 * if (!StringUtils.isBlank(token)) { // 存储组织id List<String>
	 * organizationIdList = Lists.newArrayList(); // 存储组织id,name Map<String,
	 * String> organizationInfo = Maps.newHashMap();
	 * getOwnAndSonOrgNameId(orderInfo, orgId, token, organizationIdList,
	 * organizationInfo, "orgId", "notClaimAlarmOrder");
	 * 
	 * int pageNo = orderInfo.getIntValue(FacilitiesContants.PAGE_NO); int
	 * pageSize = orderInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
	 * PageHelper.startPage(pageNo, pageSize);
	 * 
	 * Map orderQuery = JSONObject.toJavaObject(orderInfo, Map.class);
	 * 
	 * List<Map<String, Object>> notClaimAlarmOrder =
	 * alarmOrderQueryRepository.notClaimAlarmOrder(orderQuery); return
	 * matchOrgName(token, organizationIdList, organizationInfo,
	 * notClaimAlarmOrder, "notClaimAlarmOrder");
	 * 
	 * // 封装组织信息 pageOrg(notClaimAlarmOrder); PageInfo<Map<String, Object>> info
	 * = new PageInfo<>(notClaimAlarmOrder); return
	 * RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.
	 * INFO_SERCH_SUCCESS, info);
	 * 
	 * } else { // 存储组织id // List<String> organizationIdList =
	 * Lists.newArrayList(); // 存储组织id,name // Map<String, String>
	 * organizationInfo = Maps.newHashMap(); // getOwnAndSonOrgNameId(orderInfo,
	 * orgId, token, // organizationIdList, //
	 * organizationInfo,"orgId","notClaimAlarmOrder");
	 * 
	 * int pageNo = orderInfo.getIntValue(FacilitiesContants.PAGE_NO); int
	 * pageSize = orderInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
	 * PageHelper.startPage(pageNo, pageSize);
	 * 
	 * Map orderQuery = JSONObject.toJavaObject(orderInfo, Map.class);
	 * 
	 * List<Map<String, Object>> notClaimAlarmOrder =
	 * alarmOrderQueryRepository.notClaimAlarmOrder(orderQuery); // return
	 * matchOrgName(token, organizationIdList, // organizationInfo,
	 * notClaimAlarmOrder,"notClaimAlarmOrder"); // 封装组织信息
	 * pageOrg(notClaimAlarmOrder); PageInfo<Map<String, Object>> info = new
	 * PageInfo<>(notClaimAlarmOrder);
	 * logger.info(String.format("[notClaimAlarmOrder()方法执行结束!]")); return
	 * RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.
	 * INFO_SERCH_SUCCESS, info);
	 * 
	 * }
	 * 
	 * } catch (Exception e) { logger.error("notClaimAlarmOrder()->exception:",
	 * e); return RpcResponseBuilder.buildErrorRpcResp(e.getMessage()); }
	 * 
	 * }
	 */



	/**
	 * 
	 * @Description 此方法和getOwnAndSonOrgNameId()一起使用,先调getOwnAndSonOrgNameId()再调此方法
	 * @param organizationIdList:存储的组织id
	 * @param organizationInfo
	 *            存储的组织id,name
	 * @param notClaimAlarmOrder
	 *            查询获得的结果集
	 * @param methodName
	 *            调用本方法的方法名
	 * @return
	 */

	private RpcResponse<PageInfo<Map<String, Object>>> matchOrgName(String token, List<String> organizationIdList,
			Map<String, String> organizationInfo, List<Map<String, Object>> notClaimAlarmOrder, String methodName) {
		logger.info(String.format("[matchOrgName()方法执行开始...,参数：【%s】【%s】【%s】【%s】【%s】]", token, organizationIdList,
				organizationInfo, notClaimAlarmOrder, methodName));
		StringBuffer resultOrganizationIdsBuffer = new StringBuffer();
		for (Map<String, Object> mapInfo : notClaimAlarmOrder) {
			if (null != mapInfo) {
				// 有组织时,匹配已经获取的组织名
				if (organizationIdList.size() != 0) {
					String organizationName = organizationInfo.get("" + mapInfo.get(AlarmInfoConstants.ORG_ID));
					if (!StringUtils.isBlank(organizationName)) {
						mapInfo.put(AlarmInfoConstants.ORG_NAME, organizationName);
					} else {
						mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
					}
				} else {
					// 暂存organizationId
					resultOrganizationIdsBuffer.append(mapInfo.get(AlarmInfoConstants.ORG_ID) + ",");
				}
			}
		}
		// 按组织查询时
		if (organizationIdList.size() != 0) {
			PageInfo<Map<String, Object>> info = new PageInfo<>(notClaimAlarmOrder);
			logger.info(MessageConstant.INFO_SERCH_SUCCESS);
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} else {
			// 查询列表
			// 获取IP成功,查询组织名
			if (!StringUtils.isBlank(ip)) {
				JSONObject json = new JSONObject();
				int length = resultOrganizationIdsBuffer.length();
				if (length > 0) {
					StringBuffer resultOrganizationIds = resultOrganizationIdsBuffer
							.deleteCharAt(resultOrganizationIdsBuffer.lastIndexOf(","));
					json.put("organizationIds", resultOrganizationIds);
				} else {
					json.put("organizationIds", resultOrganizationIdsBuffer);
				}

				String httpValueByPost = InterGatewayUtil
						.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, json, ip, token);
				if (null == httpValueByPost) {
					// 没查到直接返回
					logger.error("[" + methodName + "()->invalid：组织查询失败!]");
					PageInfo<Map<String, Object>> info = new PageInfo<>(notClaimAlarmOrder);
					logger.info(MessageConstant.INFO_SERCH_SUCCESS);
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
				} else {
					JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
					for (Map<String, Object> mapInfo : notClaimAlarmOrder) {
						if (null != mapInfo) {
							if (null != mapInfo
									&& null != httpValueJson.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID))) {
								mapInfo.put(AlarmInfoConstants.ORG_NAME,
										"" + httpValueJson.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID)));
							} else {
								mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
							}
						}
					}
					PageInfo<Map<String, Object>> info = new PageInfo<>(notClaimAlarmOrder);
					logger.info(MessageConstant.INFO_SERCH_SUCCESS);
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
				}
			} else { // 获取IP失败,直接返回
				PageInfo<Map<String, Object>> info = new PageInfo<>(notClaimAlarmOrder);
				logger.error("获取ip失败,未能查询组织名,直接返回数据");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
			}

		}
	}



	/**
	 * 
	 * @Description 此方法和matchOrgName()一起使用,先调此方法
	 * @param orderInfo
	 *            请求参数json
	 * @param organizationIdList
	 *            用于存储组织id
	 * @param organizationInfo
	 *            用于存储组织id和组织名
	 * @param organizationKey
	 *            组织id的key
	 * @param orgId
	 *            组织id
	 * @param methodName
	 *            调用此方法的方法名
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	private void getOwnAndSonOrgNameId(JSONObject orderInfo, String orgId, String token,
			List<String> organizationIdList, Map<String, String> organizationInfo, String organizationKey,
			String methodName) {
		logger.info(String.format("[getOwnAndSonOrgNameId()方法执行开始...,参数：【%s】【%s】【%s】【%s】【%s】【%s】【%s】]", orderInfo,
				orgId, token, organizationIdList, organizationInfo, organizationKey, methodName));
		// 如果组织id不为空,查询其组织信息及子组织信息,获取到id和name,存储起来.等待查询结果然后匹配
		if (!StringUtils.isBlank(orgId)) {
			if (!StringUtils.isBlank(ip)) {
				String httpValueByGet = InterGatewayUtil
						.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + orgId, ip, token);
				if (null == httpValueByGet) {
					logger.error("[" + methodName + "()->invalid：组织查询失败!]");
					organizationIdList.add(orgId);
					organizationInfo.put(orgId, "");
				} else {
					JSONArray jsonArray = JSON.parseArray(httpValueByGet);
					List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
					// 存储组织id和name对应关系
					for (Map map : organizationInfoList) {
						organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
						organizationInfo.put("" + map.get(InterGatewayConstants.ORGANIZATION_ID),
								"" + map.get(InterGatewayConstants.ORGANIZATION_NAME));
					}
				}
			} else {
				// 获取IP失败或者为null则不访问
				logger.error("[" + methodName + "()->invalid：IP获取失败,组织查询失败!]");
				organizationIdList.add(orgId);
				organizationInfo.put(orgId, "");
			}

			orderInfo.replace(organizationKey, organizationIdList);
		}
		logger.info(String.format("[getAllDrollsByPage()方法执行结束!]"));
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmOrderQueryService#alarmOrderCountInfo(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<PageInfo<Map>> alarmOrderCountInfo(AlarmOrderCount alarmOrderCount) {
		logger.info(String.format("[alarmOrderCountInfo()方法执行开始...,参数：【%s】]", alarmOrderCount));

		try {

			RpcResponse<PageInfo<Map>> resPage = CheckParameterUtil.checkObjectBusinessKey(logger,
					"alarmOrderCountInfo", alarmOrderCount, true, AlarmOrderCountConstants.ACCESSSECRET);

			if (resPage != null) {
				return resPage;
			}

			PageHelper.startPage(Integer.valueOf(alarmOrderCount.getPageNum()),
					Integer.valueOf(alarmOrderCount.getPageSize()));

			// 封装查询条件
			Map<String, Object> orderQueryMap = Maps.newHashMap();
			orderQueryMap.put(AlarmOrderCountConstants.ACCESSSECRET, alarmOrderCount.getAccessSecret());
			orderQueryMap.put(AlarmOrderCountConstants.STARTTIME, alarmOrderCount.getStartTime());
			orderQueryMap.put(AlarmOrderCountConstants.ENDTIME, alarmOrderCount.getEndTime());
			orderQueryMap.put(AlarmOrderCountConstants.FACILITIES_TYPE_ID, alarmOrderCount.getFacilitiesTypeId());

			List<Map> alarmOrderCountInfo = alarmOrderQueryRepository.alarmOrderCountInfo(orderQueryMap);

			PageInfo<Map> info = new PageInfo<>(alarmOrderCountInfo);

			logger.info(String.format("[alarmOrderCountInfo()->suc:%s]", PublicConstants.PARAM_SUCCESS));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, info);

		} catch (Exception e) {
			logger.error("alarmOrderCountInfo()->exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}



	/**
	 * @see com.run.locman.api.query.service.AlarmOrderQueryService#alarmDetailsCount(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<PageInfo<Map>> alarmDetailsCount(AlarmCountDetails alarmCountDetails) {
		logger.info(String.format("[alarmDetailsCount()方法执行开始...,参数：【%s】]", alarmCountDetails));

		try {

			RpcResponse<PageInfo<Map>> resPage = CheckParameterUtil.checkObjectBusinessKey(logger, "alarmDetailsCount",
					alarmCountDetails, true, AlarmOrderCountConstants.ACCESSSECRET,
					AlarmOrderCountConstants.COMPLETEADDRESS);

			if (resPage != null) {
				return resPage;
			}

			// 判断是否是无法修复->特殊处理
			alarmCountDetails = getProcessState(alarmCountDetails);
			PageHelper.startPage(Integer.valueOf(alarmCountDetails.getPageNum()),
					Integer.valueOf(alarmCountDetails.getPageSize()));

			List<Map> alarmOrderDetailsInfo = alarmOrderQueryRepository.alarmOrderDetailsInfo(alarmCountDetails);

			PageInfo<Map> info = new PageInfo<>(alarmOrderDetailsInfo);

			logger.info(String.format("[alarmDetailsCount()->suc:%s]", PublicConstants.PARAM_SUCCESS));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, info);

		} catch (Exception e) {
			logger.error("alarmDetailsCount()->exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}



	private AlarmCountDetails getProcessState(AlarmCountDetails alarmCountDetails) throws Exception {
		logger.info(String.format("[getProcessState()方法执行开始...,参数：【%s】]", alarmCountDetails));
		if (AlarmOrderCountConstants.CANTREPAIR.equals(alarmCountDetails.getAostName())) {
			alarmCountDetails.setAostName(null);
			alarmCountDetails.setProcessState("3");
		} else {
			alarmCountDetails.setProcessState("4");
		}
		logger.info(String.format("[getProcessState()方法执行结束!返回信息：【%s】]", alarmCountDetails));
		return alarmCountDetails;

	}



	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<PageInfo<CountAlarmOrderDto>> countAllAlarmOrder(JSONObject json) {
		logger.info(String.format("[countAllAlarmOrder()方法执行开始...,参数：【%s】]", json));
		try {
			RpcResponse<PageInfo<CountAlarmOrderDto>> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"countAllAlarmOrder", json, AlarmOrderCountConstants.ACCESSSECRET,
					AlarmOrderCountConstants.PAGE_NUM, AlarmOrderCountConstants.PAGE_SIZE);

			if (checkBusinessKey != null) {
				logger.error(checkBusinessKey.getMessage());
				return checkBusinessKey;
			}
			RpcResponse<PageInfo<CountAlarmOrderDto>> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"countAllAlarmOrder", json, AlarmOrderCountConstants.FACILITIES_CODE,
					AlarmOrderCountConstants.USERNAME_PAR, AlarmOrderCountConstants.ORGANIZATION_ID,
					AlarmOrderCountConstants.PROCESS_STATE);
			if (containsParamKey != null) {
				logger.error(containsParamKey.getMessage());
				return containsParamKey;
			}

			Integer pageNum = json.getInteger(AlarmOrderCountConstants.PAGE_NUM);
			if (null == pageNum) {
				pageNum = 1;
			}
			Integer pageSize = json.getInteger(AlarmOrderCountConstants.PAGE_SIZE);
			if (null == pageSize) {
				pageSize = 10;
			}
			String accessSecret = json.getString(AlarmOrderCountConstants.ACCESSSECRET);
			String token = json.getString(InterGatewayConstants.TOKEN);

			// 查询该组织及其子组织的id和name
			getOwnAndSonInfo(json, token);

			// 开始分页
			PageHelper.startPage(pageNum, pageSize);
			// 统计告警工单部分所需信息
			// 备注:字段processState为工单状态 (0-未处理 1-处理中 2-已完成)
			Map<String, Object> json2Map = json.toJavaObject(Map.class);
			List<CountAlarmOrderDto> countAllAlarmOrder = alarmOrderQueryRepository.countAllAlarmOrder(json2Map);
			if (null == countAllAlarmOrder) {
				logger.error("统计告警工单信息失败!返回值为null");
				return RpcResponseBuilder.buildErrorRpcResp("统计告警工单信息失败!");
			} else {
				PageInfo<CountAlarmOrderDto> result = new PageInfo<>(countAllAlarmOrder);
				List<String> allOrgIds = alarmOrderQueryRepository.getAllOrgIds(accessSecret);
				if (null == allOrgIds) {
					logger.error("查询所有组织id失败,直接返回告警工单统计数据");
					return RpcResponseBuilder.buildSuccessRpcResp("查询成功", result);
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
						return RpcResponseBuilder.buildSuccessRpcResp("查询成功", result);
					} else {
						// 将统计结果的组织id与查询到的组织对比,获得组织名
						JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
						for (CountAlarmOrderDto countAlarmOrderDto : countAllAlarmOrder) {
							if (null != countAlarmOrderDto) {
								if (null != httpValueJson.getString(countAlarmOrderDto.getOrganizationId())) {
									countAlarmOrderDto.setOrganizationName(
											httpValueJson.getString(countAlarmOrderDto.getOrganizationId()));
								}
							}
						}
					}
					logger.info("告警工单信息统计成功!");
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, result);
				} else {
					logger.error("ip注入获取失败,直接返回告警工单统计数据");
					return RpcResponseBuilder.buildSuccessRpcResp("查询成功", result);
				}

			}

		} catch (Exception e) {
			logger.error("countAllAlarmOrder()->exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * 
	 * @Description: 查询该组织及其子组织的id和name
	 * @param
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	private void getOwnAndSonInfo(JSONObject json, String token) {
		logger.info(String.format("[getOwnAndSonInfo()方法执行开始...,参数：【%s】【%s】]", json, token));
		// 如果组织id不为空,查询其组织信息及子组织信息,获取到id和name,存储起来.等待查询结果然后匹配
		if (!StringUtils.isBlank(json.getString(AlarmOrderCountConstants.ORGANIZATION_ID))) {
			List<String> organizationIdList = Lists.newArrayList();
			String organizationId = json.getString(AlarmOrderCountConstants.ORGANIZATION_ID);

			if (!StringUtils.isBlank(ip)) {
				String httpValueByGet = InterGatewayUtil
						.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip, token);
				if (null == httpValueByGet) {
					logger.error("[countAllAlarmOrder()->invalid：组织查询失败!]");
					organizationIdList.add(organizationId);
				} else {
					JSONArray jsonArray = JSON.parseArray(httpValueByGet);
					List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
					// 存储组织id和name对应关系
					for (Map map : organizationInfoList) {
						organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
					}
				}
			} else {
				// 获取IP失败或者为null则不访问
				logger.error("[countAllAlarmOrder()->invalid：IP获取失败,组织查询失败!]");
				organizationIdList.add(organizationId);
			}

			json.replace(AlarmOrderCountConstants.ORGANIZATION_ID, organizationIdList);
		}
		logger.info(String.format("[getAllDrollsByPage()方法执行结束!]"));
	}



	@Override
	public RpcResponse<List<Map<String, Object>>> getAlarmOrderAndAllAlarmInfo(String orderId) {
		logger.info(String.format("[getAlarmOrderAndAllAlarmInfo()方法执行开始...,参数：【%s】]", orderId));
		try {
			if (StringUtils.isBlank(orderId)) {
				return RpcResponseBuilder.buildErrorRpcResp("工单id不能为空！");
			}
			List<Map<String, Object>> info = alarmOrderQueryRepository.getAlarmOrderAndAllAlarmInfo(orderId);
			logger.info(String.format("[getAllDrollsByPage()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getAlarmOrderInfoById()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Integer> getNotClaimAlarmOrderTotal(JSONObject orderInfo) {
		logger.info(String.format("[getState()方法执行开始...,参数：orderInfo:%s ]", orderInfo));
		RpcResponse<Integer> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
				"getNotClaimAlarmOrderTotal", orderInfo, AlarmInfoConstants.USC_ACCESS_SECRET);
		if (checkBusinessKey != null) {
			return checkBusinessKey;
		}
		try {
			List<String> organizationIdList = Lists.newArrayList();
			// 存储组织id,name
			Map<String, String> organizationInfo = Maps.newHashMap();
			String orgId = orderInfo.getString("orgId");
			String token = orderInfo.getString(InterGatewayConstants.TOKEN);
			getOwnAndSonOrgNameId(orderInfo, orgId, token, organizationIdList, organizationInfo, "orgId",
					"notClaimAlarmOrder");

			Map orderQuery = JSONObject.toJavaObject(orderInfo, Map.class);

			int total = alarmOrderQueryRepository.getNotClaimAlarmOrderTotal(orderQuery);
			logger.info(MessageConstant.INFO_SERCH_SUCCESS);
			logger.info(String.format("[getState()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, total);
		} catch (Exception e) {
			logger.error("getState()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmOrderQueryService#getAlarmOrderInfoByAlarmId(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> getAlarmOrderInfoByAlarmId(String alarmId) {
		logger.info(String.format("[getAlarmOrderInfoByAlarmId()方法执行开始...,参数：alarmId:%s ]", alarmId));
		try {
			if (StringUtils.isBlank(alarmId)) {
				logger.error("[getAlarmOrderInfoByAlarmId()->error,alarmId为空]");
				return RpcResponseBuilder.buildErrorRpcResp("alarmId不能为空");
			}
			Map<String, Object> res = alarmOrderQueryRepository.getAlarmOrderInfoByAlarmId(alarmId);
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);
		} catch (Exception e) {
			logger.error("getAlarmOrderInfoByAlarmId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmOrderQueryService#getChangedAlarmOrderInfoByOrderId(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> getChangedAlarmOrderInfoByOrderId(String alarmOrderId) {
		logger.info(String.format("[getChangedAlarmOrderInfoByOrderId()方法执行开始...,参数：alarmId:%s ]", alarmOrderId));
		try {
			if (StringUtils.isBlank(alarmOrderId)) {
				logger.error("[getChangedAlarmOrderInfoByOrderId()->error,alarmOrderId为空]");
				return RpcResponseBuilder.buildErrorRpcResp("alarmOrderId不能为空");
			}
			Map<String, Object> alarmOrderInfo = alarmOrderQueryRepository
					.getChangedAlarmOrderInfoByOrderId(alarmOrderId);
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", alarmOrderInfo);
		} catch (Exception e) {
			logger.error("getChangedAlarmOrderInfoByOrderId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmOrderQueryService#countAlarmOrderByOrg(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> countAlarmOrderByOrg(JSONObject json) {
		logger.info(String.format("[countAllAlarmOrder()方法执行开始...,参数：【%s】]", json));
		try {
			RpcResponse<PageInfo<Map<String, Object>>> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"countAlarmOrderByOrg", json, AlarmOrderCountConstants.ACCESSSECRET,
					AlarmOrderCountConstants.PAGE_NUM, AlarmOrderCountConstants.PAGE_SIZE);

			if (checkBusinessKey != null) {
				logger.error(checkBusinessKey.getMessage());
				return checkBusinessKey;
			}
			RpcResponse<PageInfo<Map<String, Object>>> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"countAlarmOrderByOrg", json, AlarmOrderCountConstants.ORGANIZATION_ID,
					AlarmOrderCountConstants.FACILITIES_TYPE_ID, AlarmOrderCountConstants.STARTTIME,
					AlarmOrderCountConstants.ENDTIME);
			if (containsParamKey != null) {
				logger.error(containsParamKey.getMessage());
				return containsParamKey;
			}

			Integer pageNum = json.getInteger(AlarmOrderCountConstants.PAGE_NUM);
			if (null == pageNum) {
				pageNum = 1;
			}
			Integer pageSize = json.getInteger(AlarmOrderCountConstants.PAGE_SIZE);
			if (null == pageSize) {
				pageSize = 10;
			}
			String accessSecret = json.getString(AlarmOrderCountConstants.ACCESSSECRET);
			String token = json.getString(InterGatewayConstants.TOKEN);

			// 查询该组织及其子组织的id和name
			getOwnAndSonInfo(json, token);

			// 开始分页
			PageHelper.startPage(pageNum, pageSize);
			// 统计告警工单部分所需信息
			// 备注:字段processState为工单状态 (0-未处理 1-处理中 2-已完成)
			Map<String, Object> json2Map = json.toJavaObject(Map.class);
			List<Map<String, Object>> countAlarmOrderByOrg = alarmOrderQueryRepository.countAlarmOrderByOrg(json2Map);
			if (null == countAlarmOrderByOrg) {
				logger.error("统计告警工单信息失败!返回值为null");
				return RpcResponseBuilder.buildErrorRpcResp("统计告警工单信息失败!");
			} else {
				PageInfo<Map<String, Object>> result = new PageInfo<>(countAlarmOrderByOrg);
				List<String> allOrgIds = alarmOrderQueryRepository.getAllOrgIds(accessSecret);
				if (null == allOrgIds) {
					logger.error("查询所有组织id失败,直接返回告警工单统计数据");
					return RpcResponseBuilder.buildSuccessRpcResp("查询成功", result);
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
						return RpcResponseBuilder.buildSuccessRpcResp("查询成功", result);
					} else {
						// 将统计结果的组织id与查询到的组织对比,获得组织名
						JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
						for (Map<String, Object> map : countAlarmOrderByOrg) {
							if (null != map) {
								Object orgIdObject = map.get(AlarmOrderCountConstants.ORGANIZATION_ID);
								String orgId = "" + orgIdObject;
								if (null != httpValueJson.getString(orgId)) {
									map.put(AlarmOrderCountConstants.ORGANIZATION_NAME, httpValueJson.getString(orgId));
								}
							}
						}
					}
					logger.info("告警工单信息统计成功!");
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, result);
				} else {
					logger.error("ip注入获取失败,直接返回告警工单统计数据");
					return RpcResponseBuilder.buildSuccessRpcResp("查询成功", result);
				}

			}

		} catch (Exception e) {
			logger.error("countAllAlarmOrder()->exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmOrderQueryService#getAlarmOrderAndFacInfo(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> getAlarmOrderAndFacInfo(String alarmOrderId) {
		logger.info(String.format("[getAlarmOrderAndFacInfo()方法执行开始...,参数：alarmId:%s ]", alarmOrderId));
		try {
			if (StringUtils.isBlank(alarmOrderId)) {
				logger.error("[getAlarmOrderAndFacInfo()->error,alarmOrderId为空]");
				return RpcResponseBuilder.buildErrorRpcResp("alarmOrderId不能为空");
			}
			Map<String, Object> alarmOrderInfo = alarmOrderQueryRepository.getAlarmOrderAndFacInfo(alarmOrderId);
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", alarmOrderInfo);
		} catch (Exception e) {
			logger.error("getAlarmOrderAndFacInfo()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmOrderQueryService#getAlarmIdByAlarmOrderId(java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getAlarmIdByAlarmOrderId(String alarmOrderId) {
		logger.info(String.format("[进入getAlarmIdByAlarmOrderId()-->orderId:%s]", alarmOrderId));
		try {
			if (StringUtils.isBlank(alarmOrderId)) {
				logger.error("[getAlarmIdByAlarmOrderId--->error:alarmOrderId为空]");
				return RpcResponseBuilder.buildErrorRpcResp("alarmOrderId不能为空");
			}

			List<Map<String, Object>> alarmIdByAlarmOrderId = alarmOrderQueryRepository
					.getAlarmIdByAlarmOrderId(alarmOrderId);
			if (alarmIdByAlarmOrderId == null) {
				logger.error("[getAlarmIdByAlarmOrderId--->error:查询结果为空]");
				return RpcResponseBuilder.buildErrorRpcResp("查询出来alarmId为空");
			}
			logger.info("[getAlarmIdByAlarmOrderId--->success:查询成功]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", alarmIdByAlarmOrderId);
		} catch (Exception e) {
			logger.error("getAlarmIdByAlarmOrderId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmOrderQueryService#getHiddenTroubleType()
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getHiddenTroubleType() {
		// TODO Auto-generated method stub
		logger.info("[进入getHiddenTroubleType()]");
		try {
			 List<Map<String, Object>> hiddenTroubleType = alarmOrderQueryRepository.getHiddenTroubleType();
			if (null == hiddenTroubleType || hiddenTroubleType.isEmpty()) {
				logger.error("[getHiddenTroubleType()--->error:查询结果为空]");
				return RpcResponseBuilder.buildErrorRpcResp("查询隐患类型失败");
			}
		
			else {
				logger.info(String.format("[getHiddenTroubleType()--->success:%s]", hiddenTroubleType.toString()));
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", hiddenTroubleType);
			}
		} catch (Exception e) {
			logger.error("[getHiddenTroubleType()->exception]", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
		
	}

}
