/*
 * File name: SimpleOrderQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月6日
 * ... ... ...
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

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.activity.api.constans.ActivityConstans;
import com.run.activity.api.query.ActivityProgressQuery;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.model.FacilitiesModel;
import com.run.locman.api.query.repository.FacInfoOrderQueryRepository;
import com.run.locman.api.query.repository.SimpleOrderQueryRepository;
import com.run.locman.api.query.repository.SimpleOrderTypeQueryRepository;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.PageUtils;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.AlarmOrderConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceInfoConvertConstans;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderConstants;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.SimpleOrderConstants;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * @Description: 一般流程query实现类
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public class SimpleOrderQueryServiceImpl implements SimpleOrderQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	SimpleOrderQueryRepository			simpleOrderRepository;

	@Autowired
	SimpleOrderTypeQueryRepository		simpleOrderTypeRepository;

	@Autowired
	private UserBaseQueryService		userBaseQueryService;

	@Autowired
	private ActivityProgressQuery		activityProgressQuery;

	@Autowired
	private FacInfoOrderQueryRepository	facInfoQueryRepository;



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#getFacilityDeviceList(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getFacilityDeviceList(JSONObject paramInfo) throws Exception {
		logger.info(String.format("[getFacilityDeviceList()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[getAlarmOrderBypage()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getAlarmOrderBypage()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmOrderBypage()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getAlarmOrderBypage()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.TYPE))) {
				logger.error("[getAlarmOrderBypage()->invalid：添加还是修改类型不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("添加还是修改类型不能为空!");
			}

			Map map = new HashMap<>(16);
			map.put(SimpleOrderConstants.ACCESSSECRET, paramInfo.getString(SimpleOrderConstants.ACCESSSECRET));
			map.put(SimpleOrderConstants.FACILITIESTYPEID, paramInfo.getString(SimpleOrderConstants.FACILITIESTYPEID));
			map.put(SimpleOrderConstants.SELECTKEY, paramInfo.getString(SimpleOrderConstants.SELECTKEY));
			// 若新增查询所有
			if (paramInfo.getString(SimpleOrderConstants.TYPE).equals(PublicConstants.ADD)) {
				map.put("selectAll", "true");
				map.put("select", "true");
				map.put("binding", "null");
			} else if (paramInfo.getString(SimpleOrderConstants.TYPE).equals(PublicConstants.UPDATE)) {
				if (StringUtils.isBlank(paramInfo.getString(OrderConstants.BINDING))) {
					logger.error("[getAlarmOrderBypage()->invalid：绑定状态不能为空!]");
					return RpcResponseBuilder.buildErrorRpcResp("绑定状态不能为空!]");
				}
				if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.SIMPLEORDERID))) {
					logger.error("[getAlarmOrderBypage()->invalid：一般流程工单id不能为空!]");
					return RpcResponseBuilder.buildErrorRpcResp("一般流程工单id不能为空!]");
				}
				map.put("selectAll", "false");
				map.put("select", "false");
				map.put("simpleOrderId", paramInfo.getString("simpleOrderId"));
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
			List<Map<String, Object>> page = simpleOrderRepository.getFacilityDeviceList(map);
			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			logger.info(String.format("[getFacilityDeviceList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getFacilityDeviceList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#findById(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Map<String, Object>> findById(String id) throws Exception {
		logger.info(String.format("[findById()方法执行开始...,参数：【%s】]", id));

		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[findById()->invalid：一般流程工单id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("一般流程工单id不能为空!]");
			}
			Map<String, Object> order = (Map<String, Object>) simpleOrderRepository.findById(id);
			if (null != order) {
				logger.info("[findById()->invalid：查询成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, order);
			}

			logger.info("[findById()->invalid：查询成功!]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_SUCCESS);
		} catch (Exception e) {
			logger.error("findById()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#getSimpleOrderList(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getSimpleOrderList(JSONObject paramInfo) throws Exception {
		logger.info(String.format("[getSimpleOrderList()方法执行开始...,参数：【%s】]", paramInfo));
		try {

			if (paramInfo == null) {
				logger.error("[getAlarmOrderBypage()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getAlarmOrderBypage()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmOrderBypage()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getAlarmOrderBypage()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.USERID))) {
				logger.error("[getAlarmOrderBypage()->invalid：用户id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空！");
			}

			Map map = new HashMap<>(16);
			map.put(SimpleOrderConstants.ACCESSSECRET, paramInfo.getString(SimpleOrderConstants.ACCESSSECRET));
			map.put(SimpleOrderConstants.USERID, paramInfo.getString(SimpleOrderConstants.USERID));
			map.put(SimpleOrderConstants.SELECTKEY, paramInfo.getString(SimpleOrderConstants.SELECTKEY));
			map.put(SimpleOrderConstants.PROCESSSTATE, paramInfo.getString(SimpleOrderConstants.PROCESSSTATE));
			map.put(SimpleOrderConstants.ORDERTYPE, paramInfo.getString(SimpleOrderConstants.ORDERTYPE));
			map.put(SimpleOrderConstants.PROCESSSTARTTIME, paramInfo.getString(SimpleOrderConstants.PROCESSSTARTTIME));
			map.put(SimpleOrderConstants.PROCESSENDTIME, paramInfo.getString(SimpleOrderConstants.PROCESSENDTIME));

			int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> page = simpleOrderRepository.getSimpleOrderList(map);

			// 查询发起人名称 和是否审核过
			if (null != page && page.size() > 0) {
				String userId = page.get(0).get(SimpleOrderConstants.USERID).toString();
				for (int i = 0; i < page.size(); i++) {
					RpcResponse<Map> userInfo = userBaseQueryService.getUserByUserId(userId);
					if (userInfo.isSuccess()) {
						page.get(i).put(SimpleOrderConstants.CREATEBY, userInfo.getSuccessValue().get("userName"));
					} else {
						page.get(i).put(SimpleOrderConstants.CREATEBY, "");
					}
					JSONObject jsonParam = new JSONObject();
					jsonParam.put(SimpleOrderConstants.PROCESSID, page.get(i).get(SimpleOrderConstants.PROCESSID));
					RpcResponse<Map<String, Object>> result = activityProgressQuery.getJudgmentProcess(jsonParam);
					if (result.isSuccess()) {
						page.get(i).put("operaType", result.getSuccessValue().get("state"));
					} else {
						return RpcResponseBuilder.buildErrorRpcResp("调用工作流,获取是否审核过工单状态失败!");
					}
				}
			}

			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			logger.info(String.format("[getSimpleOrderList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getSimpleOrderList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#getSimpleOrderAgencyList(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getSimpleOrderAgencyList(JSONObject paramInfo) throws Exception {
		logger.info(String.format("[getSimpleOrderAgencyList()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[getSimpleOrderAgencyList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getSimpleOrderAgencyList()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getSimpleOrderAgencyList()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.USERID))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：用户id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(OrderConstants.MODEL_TYPE))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：查询的模块类型不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询的模块类型不能为空!");
			}

			// 参数封装
			JSONObject jsonParam = new JSONObject();
			jsonParam.put(SimpleOrderConstants.TYPE, "generalProcess");
			jsonParam.put(SimpleOrderConstants.USERID, paramInfo.getString(SimpleOrderConstants.USERID));
			jsonParam.put(ActivityConstans.ACTIVITY_CURRENTPAGE, paramInfo.getIntValue(FacilitiesContants.PAGE_NO));
			jsonParam.put(ActivityConstans.ACTIVITY_PAGESIZE, paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE));

			Set<String> processSet = new HashSet<>();
			Long total = 0L;
			Boolean checkParamsSelect = checkParamsSelect(paramInfo);
			// 判断调用哪种方法
			if (!checkParamsSelect || paramInfo.getString(OrderConstants.MODEL_TYPE).equals(OrderConstants.HAVE_DONE)) {
				RpcResponse<List<Map<String, Object>>> result = null;
				// 调用工作流,查询我的代办
				if (paramInfo.getString(OrderConstants.MODEL_TYPE).equals(OrderConstants.NOT_DONE)) {
					result = activityProgressQuery.getAllACTProgress(jsonParam);
				} else if (paramInfo.getString(OrderConstants.MODEL_TYPE).equals(OrderConstants.HAVE_DONE)) {
					jsonParam.put("nodeId", AlarmOrderConstants.PROCCESS_NODE_REMOVEN_SIGN_SIM);
					result = activityProgressQuery.getAllHIProgress(jsonParam);
				}
				if (null == result || !result.isSuccess()) {
					logger.error("[getSimpleOrderAgencyList()->invalid：查询失败!]");
					return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
				}
				// 获取代办或者经办过的工单id集合
				List<Map<String, Object>> list = result.getSuccessValue();

				if (null != list && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						String string = "" + list.get(i).get(SimpleOrderConstants.ID);
						processSet.add(string);
					}
				} else {
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, null);
				}
				int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
				int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
				PageHelper.startPage(pageNo, pageSize);

			} else {

				RpcResponse<Map> taskWaitfor = null;
				// 调用工作流,查询我的代办
				if (paramInfo.getString(OrderConstants.MODEL_TYPE).equals(OrderConstants.NOT_DONE)) {
					taskWaitfor = activityProgressQuery.getTaskWaitfor(jsonParam);
					if (null == taskWaitfor || !taskWaitfor.isSuccess()) {
						logger.error("[getSimpleOrderAgencyList()->invalid：查询失败!]");
						return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
					}
					processSet = (Set) taskWaitfor.getSuccessValue().get("data");
					total = (long) taskWaitfor.getSuccessValue().get("total");
				}

			}

			Map map = JSONObject.toJavaObject(paramInfo, Map.class);

			map.put("set", processSet);

			List<Map<String, Object>> page = simpleOrderRepository.getSimpleOrderAgencyList(map);

			// 查询发起人名称
			if (null != page && page.size() > 0) {
				for (int i = 0; i < page.size(); i++) {
					String userId = page.get(i).get(SimpleOrderConstants.USERID).toString();
					RpcResponse<Map> userInfo = userBaseQueryService.getUserByUserId(userId);
					if (userInfo.isSuccess() && userInfo.getSuccessValue() != null) {
						page.get(i).put(SimpleOrderConstants.CREATEBY, userInfo.getSuccessValue().get("userName"));
					} else {
						page.get(i).put(SimpleOrderConstants.CREATEBY, "");
					}
				}
			}

			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			info.setPageNum(paramInfo.getIntValue(FacilitiesContants.PAGE_NO));
			info.setPageSize(paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE));

			if (checkParamsSelect && paramInfo.getString(OrderConstants.MODEL_TYPE).equals(OrderConstants.NOT_DONE)) {
				info.setTotal(total);
			}

			logger.info(String.format("[getSimpleOrderAgencyList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getSimpleOrderAgencyList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:判断是直接分页，还是page分页
	 * @param paramInfo
	 * @return
	 * @throws Exception
	 */
	private Boolean checkParamsSelect(JSONObject paramInfo) throws Exception {

		String selectKey = paramInfo.getString(SimpleOrderConstants.SELECTKEY);
		String processstartTime = paramInfo.getString(SimpleOrderConstants.PROCESSSTARTTIME);
		String processendTime = paramInfo.getString(SimpleOrderConstants.PROCESSENDTIME);
		String orderType = paramInfo.getString(SimpleOrderConstants.ORDERTYPE);
		String processState = paramInfo.getString(SimpleOrderConstants.PROCESSSTATE);

		if (!StringUtils.isBlank(selectKey)) {
			return false;
		}
		if (!StringUtils.isBlank(processstartTime)) {
			return false;
		}
		if (!StringUtils.isBlank(processendTime)) {
			return false;
		}
		if (!StringUtils.isBlank(orderType)) {
			return false;
		}
		if (!StringUtils.isBlank(processState)) {
			return false;
		}

		return true;
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#whetherExistOrder(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Boolean> whetherExistOrder(JSONObject paramInfo) throws Exception {
		logger.info(String.format("[whetherExistOrder()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[getSimpleOrderAgencyList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.DEVICE_ID)) {
				logger.error("[getSimpleOrderAgencyList()->invalid：设备id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空！");
			}
			String pointOfTime = "pointOfTime";
			if (!paramInfo.containsKey(pointOfTime)) {
				logger.error("[getSimpleOrderAgencyList()->invalid：查询的时间点不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询的时间点不能为空！");
			}

			Map map = new HashMap<>(16);
			map.put(AlarmInfoConstants.DEVICE_ID, paramInfo.getString(AlarmInfoConstants.DEVICE_ID));
			map.put("pointOfTime", paramInfo.getString("pointOfTime"));

			List<Map<String, Object>> list = simpleOrderRepository.whetherExistOrder(map);
			if (null != list && list.size() > 0) {
				logger.info(String.format("[whetherExistOrder()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, Boolean.TRUE);
			} else if (null != list && list.size() == 0) {
				logger.info(String.format("[whetherExistOrder()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, Boolean.FALSE);
			}
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
		} catch (Exception e) {
			logger.error("whetherExistOrder()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#getOrderNodeDetails(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Map<String, Object>> getOrderNodeDetails(JSONObject paramInfo) throws Exception {
		logger.info(String.format("[getOrderNodeDetails()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[getSimpleOrderAgencyList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.ID))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：工单id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("工单id不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.USERID))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：用户id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.PROCESSID))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：工单流程id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("工单流程id不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.ACCESSSECRET))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：接入方秘钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空！");
			}

			RpcResponse<Map<String, Object>> resultMap = activityProgressQuery.getNodeInfo(paramInfo);
			if (!resultMap.isSuccess() || null == resultMap.getSuccessValue()
					|| resultMap.getSuccessValue().isEmpty()) {
				logger.error("[getSimpleOrderAgencyList()->invalid：查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
			}

			Map<String, Object> map = resultMap.getSuccessValue();
			// 历史节点
			if (map.containsKey(OrderConstants.HIS_EXCU_LIST) && null != map.get(OrderConstants.HIS_EXCU_LIST)) {
				List<Map> list = (List<Map>) map.get(OrderConstants.HIS_EXCU_LIST);
				for (Map hisMap : list) {
					List<String> userIdList = (List<String>) hisMap.get("excuUser");
					List<String> userNameList = new ArrayList<>();
					for (String userId : userIdList) {
						RpcResponse<Map> userInfo = userBaseQueryService.getUserByUserId(userId);
						if (userInfo.isSuccess() && userInfo.getSuccessValue() != null) {
							Object name = userInfo.getSuccessValue().get("userName");
							userNameList.add(
									name == null ? ("" + userInfo.getSuccessValue().get("loginAccount")) : ("" + name));
						} else {
							userNameList.add("null");
						}
					}
					hisMap.put("userName", userNameList.toString().replaceAll("\\u005b", "").replaceAll("\u005d", "")
							.replaceAll(",", "、"));
				}

			}
			if (resultMap.isSuccess()) {
				logger.info(String.format("[getOrderNodeDetails()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, map);
			}
			logger.info(String.format("[getOrderNodeDetails()方法执行结束!]"));
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
		} catch (Exception e) {
			logger.error("getOrderNodeDetails()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#findFacInfo(com.run.locman.api.entity.Facilities)
	 */
	@Override
	public RpcResponse<PageInfo<Facilities>> findFacInfo(FacilitiesModel facilitiesModel) throws Exception {
		logger.info(String.format("[findFacInfo()方法执行开始...,参数：【%s】]", facilitiesModel));

		try {
			// 校验参数 业务参数与分页参数
			RpcResponse<PageInfo<Facilities>> checkObjectBusinessKey = CheckParameterUtil.checkObjectBusinessKey(logger,
					"findConvertAll", facilitiesModel, true, DeviceInfoConvertConstans.CONVERT_ACCESSSECRET);

			if (checkObjectBusinessKey != null) {
				return checkObjectBusinessKey;
			}

			// 通过工单id查询设施id集合
			if (facilitiesModel.getSimplerOrFacId() != null && facilitiesModel.getBinding() != null
					&& !"".equals(facilitiesModel.getSimplerOrFacId()) && !"".equals(facilitiesModel.getBinding())) {
				List<String> findFacIdsByOrerId = facInfoQueryRepository.findFacIdsByOrerId(facilitiesModel);
				facilitiesModel.setFacIds(findFacIdsByOrerId);
			}

			// 分页
			PageInfo<Facilities> pageStart = PageUtils.pageStart(facInfoQueryRepository, facilitiesModel);

			logger.info(String.format("[findFacInfo()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(DeviceInfoConvertConstans.OPERATION_SUCCESS, pageStart);

		} catch (Exception e) {
			logger.error("findFacInfo()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#getOrderByDeviceId(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<Boolean> getOrderByDeviceId(JSONObject paramInfo) throws Exception {
		logger.info(String.format("[getOrderByDeviceId()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[getOrderByDeviceId()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.DEVICEID))) {
				logger.error("[getOrderByDeviceId()->invalid：设备id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.ACCESSSECRET))) {
				logger.error("[getOrderByDeviceId()->invalid：接入方秘钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空！");
			}

			List<Map<String, Object>> order = simpleOrderRepository.getOrderByDeviceId(paramInfo);
			if (order != null && order.size() > 0) {
				logger.info("[getOrderByDeviceId()->success：校验成功,该设备存在该期间处理中的工单!]");
				return RpcResponseBuilder.buildSuccessRpcResp("校验成功,该设备存在该期间处理中的工单!", Boolean.TRUE);
			} else if (order != null && order.size() == 0) {
				logger.info("[getOrderByDeviceId()->success：校验成功,该设备存不在该期间处理中的工单!]");
				return RpcResponseBuilder.buildSuccessRpcResp("校验成功,该设备存不在该期间处理中的工单!", Boolean.FALSE);
			}
			logger.error("[getOrderByDeviceId()->fial：查询失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("查询工单失败!");
		} catch (Exception e) {
			logger.error("getOrderByDeviceId()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#getSimpleOrderAgencyListForApp(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map<String, Object>>> getSimpleOrderAgencyListForApp(JSONObject paramInfo)
			throws Exception {
		logger.info(String.format("[getSimpleOrderAgencyListForApp()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[getSimpleOrderAgencyList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：接入方密钥accessSecret不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥accessSecret不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.USERID))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：用户userId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户userId不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(FacilitiesContants.FACILITY_ID))) {
				logger.error("[getSimpleOrderAgencyList()->invalid：设施facilityId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设施facilityId不能为空!");
			}

			List<Map<String, Object>> page = simpleOrderRepository.getSimpleOrderAgencyListForApp(paramInfo);

			// 查询发起人名称 和是否审核过
			if (null != page && page.size() > 0) {
				String userId = page.get(0).get(SimpleOrderConstants.USERID) + "";
				for (int i = 0; i < page.size(); i++) {
					RpcResponse<Map> userInfo = userBaseQueryService.getUserByUserId(userId);
					if (userInfo.isSuccess()) {
						page.get(i).put(SimpleOrderConstants.CREATEBY, userInfo.getSuccessValue().get("userName"));
					} else {
						page.get(i).put(SimpleOrderConstants.CREATEBY, "");
					}
					JSONObject jsonParam = new JSONObject();
					jsonParam.put(SimpleOrderConstants.PROCESSID, page.get(i).get(SimpleOrderConstants.PROCESSID));
					RpcResponse<Map<String, Object>> result = activityProgressQuery.getJudgmentProcess(jsonParam);
					if (result.isSuccess()) {
						page.get(i).put("operaType", result.getSuccessValue().get("state"));
					} else {
						return RpcResponseBuilder.buildErrorRpcResp("调用工作流,获取是否审核过工单状态失败!");
					}
				}
			}
			logger.info(String.format("[getSimpleOrderAgencyListForApp()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, page);
		} catch (Exception e) {
			logger.error("getSimpleOrderAgencyList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#findOrderNumber()
	 */
	@Override
	public RpcResponse<String> findOrderNumber(String accessSecret) {
		logger.info(String.format("[findOrderNumber()方法执行开始...,参数：【%s】]", accessSecret));

		try {

			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[findOrderNumber()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}

			String number = simpleOrderRepository.findOrderNumber(accessSecret);

			logger.info(String.format("[findOrderNumber()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功！", number);

		} catch (Exception e) {
			logger.error("findOrderNumber()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.SimpleOrderQueryService#querySimpleOrderStartTime(java.lang.String, java.lang.String)
	 */
	@Override
	public RpcResponse<String> querySimpleOrderStartTime(String accessSecret, String deviceId) {
		logger.info(String.format("[findOrderNumber()方法执行开始...,参数：【%s】【%s】]", accessSecret,deviceId));

		try {

			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[findOrderNumber()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[findOrderNumber()->invalid：设备deviceId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备deviceId不能为空！");
			}

			String time = simpleOrderRepository.querySimpleOrderStartTime(accessSecret, deviceId);

			logger.info(String.format("[findOrderNumber()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功！", time);

		} catch (Exception e) {
			logger.error("findOrderNumber()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}


}
