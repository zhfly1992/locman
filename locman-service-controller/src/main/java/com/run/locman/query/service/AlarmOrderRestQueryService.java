/*
 * File name: AlarmOrderRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年12月5日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.activity.api.query.ActivityProgressQuery;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.CountAlarmOrderDto;
import com.run.locman.api.entity.AlarmCountDetails;
import com.run.locman.api.entity.AlarmOrderCount;
import com.run.locman.api.entity.Pages;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.AlarmOrderConstants;
import com.run.locman.constants.AlarmOrderCountConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.filetool.ExcelView;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * 
 * @Description:告警工单查询
 * @author: lkc
 * @version: 1.0, 2017年12月5日
 */
@Service
public class AlarmOrderRestQueryService {
	private static final Logger		logger				= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private AlarmOrderQueryService	alarmOrderQueryService;
	@Autowired
	private ActivityProgressQuery	activityProgressQuery;
	@Autowired
	private UserBaseQueryService	userBaseQueryService;
	@Autowired
	private HttpServletRequest		request;
	@Value("${api.host}")
	String							ip;

	/**
	 * 审核不通过
	 */
	private static final String		notPass				= "no";
	/**
	 * 审核不通过
	 */
	private static final String		notPass_ch			= "拒绝";
	/**
	 * 审核通过
	 */
	private static final String		pass				= "yes";
	/**
	 * 审核通过
	 */
	private static final String		pass_ch				= "通过";
	/**
	 * 转故障已完成
	 */
	private static final String		powerlessed			= "3";
	/**
	 * 转故障被拒绝
	 */
	private static final String		powerlessReject		= "2";
	/**
	 * 转故障审批中
	 */
	private static final String		powerlessing		= "1";
	/**
	 * 完工审批中
	 */
	private static final String		approve_before_end	= "6";
	/*
	 * 处理中
	 */
	private static final String		dealing				= "0";



	public Result<PageInfo<Map<String, Object>>> getAlarmOrderByPage(String alarmOrderInfo) {
		try {
			logger.info(String.format("[getAlarmOrderByPage()->request params:%s]", alarmOrderInfo));
			Result<String> result = ExceptionChecked.checkRequestParam(alarmOrderInfo);
			if (result != null) {
				logger.error(String.format("[getAlarmOrderByPage()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(alarmOrderInfo);
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			parseObject.put(InterGatewayConstants.TOKEN, token);
			RpcResponse<PageInfo<Map<String, Object>>> res = alarmOrderQueryService.getAlarmOrderBypage(parseObject);
			if (res.isSuccess()) {
				logger.info("[getAlarmOrderByPage()->success:告警工单查询成功]");
				res.getSuccessValue().setList(dataUserName(res.getSuccessValue().getList(), token));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[getAlarmOrderByPage()->fail:告警工单查询失败]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("getAlarmOrderByPage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:封装接受人名称
	 * @param list
	 */

	@SuppressWarnings("rawtypes")
	private List<Map<String, Object>> dataUserName(List<Map<String, Object>> list, String token) throws Exception {
		if (null == list || list.isEmpty()) {
			return list;
		}
		JSONObject userJson = new JSONObject();
		List<String> userIds = Lists.newArrayList();

		for (Map<String, Object> map : list) {
			userIds.add(map.get(AlarmOrderConstants.USERID) + "");
		}
		userJson.put(UscConstants.USC_USER_IDS, userIds);
		String restValue = InterGatewayUtil.getHttpValueByPost(InterGatewayConstants.FIND_USER_NAME, userJson, ip,
				token);
		if (StringUtils.isBlank(restValue)) {
			throw new MyException("http接口返回失败！" + InterGatewayConstants.FIND_USER_NAME);
		}

		Map userNameMap = JSON.parseObject(restValue).toJavaObject(Map.class);

		for (Map<String, Object> map : list) {
			map.put(AlarmOrderConstants.RECEIVE_USER_NAME, userNameMap.get(map.get(AlarmOrderConstants.USERID)));
		}

		return list;
	}



	public Result<List<Map<String, Object>>> getState(int type) {
		try {
			logger.info(String.format("[getState()->request params:%s]", type));
			RpcResponse<List<Map<String, Object>>> res = alarmOrderQueryService.getState(type);
			if (res.isSuccess()) {
				logger.info("[getState()->success]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[getState()->fail]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("getState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<Map<String, Object>>> getAlarmOrderTodoByPage(String queryParams) {
		try {
			logger.info(String.format("[getAlarmOrderTodoByPage()->request params:%s]", queryParams));
			Result<String> result = ExceptionChecked.checkRequestParam(queryParams);
			if (result != null) {
				logger.error(String.format("[getAlarmOrderTodoByPage()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(queryParams);
			if (!parseObject.containsKey(AlarmOrderConstants.USERID)
					|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.USERID))) {
				logger.error(String.format("[getAlarmOrderTodoByPage()->error:%s--%s]",
						LogMessageContants.NO_PARAMETER_EXISTS, AlarmOrderConstants.USERID));
				return ResultBuilder.noBusinessResult();
			}

			JSONObject parm = new JSONObject();
			parm.put(AlarmOrderConstants.USERID, parseObject.get(AlarmOrderConstants.USERID));
			parm.put(AlarmOrderConstants.PROCESS_TYPE, AlarmOrderConstants.PROCESS_TYPE_ALARMFAILURE);
			RpcResponse<List<Map<String, Object>>> allACTProgress = activityProgressQuery.getAllACTProgress(parm);
			if (allACTProgress == null || !allACTProgress.isSuccess() || allACTProgress.getSuccessValue() == null
					|| allACTProgress.getSuccessValue().isEmpty()) {
				logger.error("[getAlarmOrderTodoByPage()->fail:未查到用户工单！]");
				PageInfo<Map<String, Object>> pageInfo = new PageInfo<>();
				pageInfo.setList(Lists.newArrayList());
				return ResultBuilder.successResult(pageInfo, "未查到用户工单！");
			}
			List<Map<String, Object>> allTodo = allACTProgress.getSuccessValue();
			List<String> list = new ArrayList<>();
			for (Map<String, Object> mapTodo : allTodo) {
				if (mapTodo.containsKey(AlarmOrderConstants.ORDER_ID)) {
					list.add((String) mapTodo.get(AlarmOrderConstants.ORDER_ID));
				}
			}
			if (list.isEmpty()) {
				logger.error("[getAlarmOrderTodoByPage()->fail:未查到用户工单！]");
				PageInfo<Map<String, Object>> pageInfo = new PageInfo<>();
				pageInfo.setList(Lists.newArrayList());
				return ResultBuilder.successResult(pageInfo, "未查到用户工单！");
			}
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			parseObject.put(InterGatewayConstants.TOKEN, token);
			RpcResponse<PageInfo<Map<String, Object>>> res = alarmOrderQueryService.getAlarmOrderTodoByPage(parseObject,
					list);
			if (res.isSuccess()) {
				logger.info("[getAlarmOrderTodoByPage()->success:待办告警工单查询成功]");
				res.getSuccessValue().setList(dataUserName(res.getSuccessValue().getList(), token));
				return ResultBuilder.successResult(
						updatePageInfo(res.getSuccessValue(), parseObject.getString(AlarmOrderConstants.USERID)),
						res.getMessage());
			} else {
				logger.error("[getAlarmOrderTodoByPage()->fail:待办告警工单查询失败]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("getAlarmOrderTodoByPage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:修改命令返回参数
	 * @param alarmOrderInfo
	 * @return
	 * @throws Exception
	 */
	private PageInfo<Map<String, Object>> updatePageInfo(PageInfo<Map<String, Object>> alarmOrderInfo, String userId)
			throws Exception {

		List<Map<String, Object>> list = alarmOrderInfo.getList();
		for (Map<String, Object> map : list) {
			// 命令判断
			if (!userId.equals(map.get(AlarmOrderConstants.USERID))
					&& "true".equals(map.get(AlarmOrderConstants.COMMANDFLAG))) {
				map.put(AlarmOrderConstants.COMMANDFLAG, false);
			}
		}
		alarmOrderInfo.setList(list);

		return alarmOrderInfo;
	}



	public Result<PageInfo<Map<String, Object>>> getAlarmOrderHaveDealByPage(String queryParams) {
		try {
			logger.info(String.format("[getAlarmOrderHaveDealByPage()->request params:%s]", queryParams));
			Result<String> result = ExceptionChecked.checkRequestParam(queryParams);
			if (result != null) {
				logger.error(String.format("[getAlarmOrderHaveDealByPage()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(queryParams);
			if (!parseObject.containsKey(AlarmOrderConstants.USERID)
					|| StringUtils.isBlank(parseObject.getString(AlarmOrderConstants.USERID))) {
				logger.error(String.format("[getAlarmOrderHaveDealByPage()->error:%s--%s]",
						LogMessageContants.NO_PARAMETER_EXISTS, AlarmOrderConstants.USERID));
				return ResultBuilder.noBusinessResult();
			}
			JSONObject parm = new JSONObject();
			parm.put(AlarmOrderConstants.USERID, parseObject.get(AlarmOrderConstants.USERID));
			parm.put(AlarmOrderConstants.PROCESS_TYPE, AlarmOrderConstants.PROCESS_TYPE_ALARMFAILURE);
			parm.put(AlarmOrderConstants.PROCESS_NODE_ID, AlarmOrderConstants.PROCCESS_NODE_REMOVEN_SIGN_ALA);
			RpcResponse<List<Map<String, Object>>> allHIProgress = activityProgressQuery.getAllHIProgress(parm);
			if (allHIProgress == null || !allHIProgress.isSuccess() || allHIProgress.getSuccessValue() == null
					|| allHIProgress.getSuccessValue().isEmpty()) {
				logger.error("[getAlarmOrderHaveDealByPage()->fail:未查到用户工单！]");
				return ResultBuilder.failResult("未查到用户工单！");
			}
			// 不知为何这里有多余的代码,可以删除
			// TODO
			RpcResponse<List<Map<String, Object>>> allACTProgress = activityProgressQuery.getAllHIProgress(parm);
			List<Map<String, Object>> allOverOrder = allACTProgress.getSuccessValue();
			List<String> list = new ArrayList<>();
			for (Map<String, Object> mapTodo : allOverOrder) {
				if (mapTodo.containsKey(AlarmOrderConstants.ORDER_ID)) {
					list.add((String) mapTodo.get(AlarmOrderConstants.ORDER_ID));
				}
			}
			if (list.isEmpty()) {
				logger.error("[getAlarmOrderHaveDealByPage()->fail:未查到用户工单！]");
				return ResultBuilder.failResult("未查到用户工单！");
			}
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			parseObject.put(InterGatewayConstants.TOKEN, token);
			RpcResponse<PageInfo<Map<String, Object>>> res = alarmOrderQueryService.getAlarmOrderTodoByPage(parseObject,
					list);
			if (res.isSuccess()) {
				logger.info("[getAlarmOrderHaveDealByPage()->success:已办告警工单查询成功]");
				res.getSuccessValue().setList(dataUserName(res.getSuccessValue().getList(), token));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[getAlarmOrderHaveDealByPage()->fail:已办告警工单查询失败]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("getAlarmOrderHaveDealByPage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<List<Map<String, Object>>> getRejectCause(String orderId) {
		try {
			logger.info(String.format("[getRejectCause()->request params--orderId:%s]", orderId));
			if (StringUtils.isBlank(orderId)) {
				logger.error("[getRejectCause()->error:工单id不能为空！]");
				return ResultBuilder.failResult("工单id不能为空！");
			}
			RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService.getAlarmOrderInfoById(orderId);
			if (!alarmOrderInfoById.isSuccess() || alarmOrderInfoById.getSuccessValue() == null) {
				logger.error("[getRejectCause()->fail:未查到用户工单！]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> successValue = alarmOrderInfoById.getSuccessValue();
			JSONObject parm = new JSONObject();
			parm.put(AlarmOrderConstants.PROCESS_ID, successValue.get(AlarmOrderConstants.PROCESS_ID));
			RpcResponse<List<Map<String, Object>>> processVariables = activityProgressQuery.getNodeVariables(parm);
			if (processVariables == null || processVariables.getSuccessValue() == null) {
				logger.error("[getRejectCause()->fail:当前没有拒绝原因，或者工单未被拒绝！]");
				return ResultBuilder.failResult("当前没有拒绝原因，或者工单未被拒绝！");
			}
			// 封装拒绝原因
			List<Map<String, Object>> rejectCause = processVariables.getSuccessValue();
			List<Map<String, Object>> rejectCauseInfo = new ArrayList<>();
			for (Map<String, Object> map : rejectCause) {
				if (map.containsKey(AlarmOrderConstants.REJECT_REMARK)) {
					rejectCauseInfo.add(map);
					String userId = (String) map.get(AlarmOrderConstants.REJECT_USERID);
					RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
					if (userByUserId == null || userByUserId.getSuccessValue() == null) {
						map.put("userInfo", Maps.newHashMap());
					} else {
						Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
						map.put("userInfo", userInfo);
					}
				}
			}
			logger.info("[getRejectCause()->success:工单拒绝原因查询成功]");
			return ResultBuilder.successResult(reverseSort(rejectCauseInfo), MessageConstant.SEARCH_SUCCESS);
		} catch (Exception e) {
			logger.error("getRejectCause()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<List<Map<String, Object>>> getPowerLessCause(String orderId) {
		try {
			logger.info(String.format("[getPowerLessCause()->request params--orderId:%s]", orderId));
			if (StringUtils.isBlank(orderId)) {
				logger.error("[getPowerLessCause()->error:工单id不能为空！]");
				return ResultBuilder.failResult("工单id不能为空！");
			}
			RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService.getAlarmOrderInfoById(orderId);
			if (!alarmOrderInfoById.isSuccess() || alarmOrderInfoById.getSuccessValue() == null) {
				logger.error("[getPowerLessCause()->fail:未查到工单信息！！]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> successValue = alarmOrderInfoById.getSuccessValue();
			JSONObject parm = new JSONObject();
			parm.put(AlarmOrderConstants.PROCESS_ID, successValue.get(AlarmOrderConstants.PROCESS_ID));
			RpcResponse<List<Map<String, Object>>> processVariables = activityProgressQuery.getNodeVariables(parm);
			if (processVariables == null || processVariables.getSuccessValue() == null) {
				logger.error("[getPowerLessCause()->fail:当前没有无法处理原因，或者工单未被处理！]");
				return ResultBuilder.failResult("当前没有无法处理原因，或者工单未被处理！");
			}
			// 封装无法
			List<Map<String, Object>> powerlessCause = processVariables.getSuccessValue();
			List<Map<String, Object>> powerlessCauseInfo = new ArrayList<>();
			for (Map<String, Object> map : powerlessCause) {
				if (map.containsKey(AlarmOrderConstants.POWERLESS_REMARK)) {
					powerlessCauseInfo.add(map);
					// 封装用户信息
					String userId = map.get(AlarmOrderConstants.POWERLESS_USERID) + "";
					RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
					if (userByUserId == null || userByUserId.getSuccessValue() == null) {
						map.put("userInfo", Maps.newHashMap());
					} else {
						Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
						map.put("userInfo", userInfo);
					}
					// 封装类型信息
					RpcResponse<List<Map<String, Object>>> res = alarmOrderQueryService.getState(3);
					if (res != null && res.isSuccess() && res.getSuccessValue() != null) {
						List<Map<String, Object>> state = res.getSuccessValue();
						for (Map<String, Object> sta : state) {
							String sign = (String) sta.get("sign");
							if (sign.equals(map.get(AlarmOrderConstants.POWERLESS_ORDER_TYPE))) {
								map.put(AlarmOrderConstants.NORMAL_ORDER_TYPE_NAME, (String) sta.get("name"));
								break;
							}
						}

					}
				}
			}
			logger.info("[getPowerLessCause()->success:工单无法处理原因查询成功]");
			return ResultBuilder.successResult(reverseSort(powerlessCauseInfo), MessageConstant.SEARCH_SUCCESS);
		} catch (Exception e) {
			logger.error("getPowerLessCause()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<JSONObject> getCompleteInfo(String orderId) {
		try {
			logger.info(String.format("[getCompleteInfo()->request params--orderId:%s]", orderId));
			if (StringUtils.isBlank(orderId)) {
				logger.error("[getCompleteInfo()->error:工单id不能为空！]");
				return ResultBuilder.failResult("工单id不能为空！");
			}
			RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService.getAlarmOrderInfoById(orderId);
			if (!alarmOrderInfoById.isSuccess() || alarmOrderInfoById.getSuccessValue() == null) {
				logger.error("[getCompleteInfo()->fail:未查到工单信息！]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> successValue = alarmOrderInfoById.getSuccessValue();
			JSONObject parm = new JSONObject();
			parm.put(AlarmOrderConstants.PROCESS_ID, successValue.get(AlarmOrderConstants.PROCESS_ID));
			RpcResponse<Map<String, Object>> processVariables = activityProgressQuery.getProcessVariables(parm);
			if (processVariables == null || processVariables.getSuccessValue() == null) {
				logger.error("[getCompleteInfo()->fail:未查到工单信息！]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> rejectCause = processVariables.getSuccessValue();

			JSONObject re = new JSONObject();
			re.put(AlarmOrderConstants.NORMAL_REMARK, (String) rejectCause.get(AlarmOrderConstants.NORMAL_REMARK));
			re.put(AlarmOrderConstants.NORMAL_ORDER_PIC, rejectCause.get(AlarmOrderConstants.NORMAL_ORDER_PIC));
			re.put(AlarmOrderConstants.APPLY_USERID, (String) rejectCause.get(AlarmOrderConstants.APPLY_USERID));

			// 封装类型信息
			String typeId = (String) rejectCause.get(AlarmOrderConstants.NORMAL_ORDER_TYPE);
			re.put(AlarmOrderConstants.NORMAL_ORDER_TYPE, typeId);
			RpcResponse<List<Map<String, Object>>> res = alarmOrderQueryService.getState(2);
			if (res != null && res.isSuccess() && res.getSuccessValue() != null) {
				List<Map<String, Object>> state = res.getSuccessValue();
				for (Map<String, Object> map : state) {
					String sign = (String) map.get("sign");
					if (sign.equals(typeId)) {
						re.put(AlarmOrderConstants.NORMAL_ORDER_TYPE_NAME, (String) map.get("name"));
						break;
					}
				}

			}
			String userId = (String) rejectCause.get(AlarmOrderConstants.APPLY_USERID);
			RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
			if (userByUserId == null || userByUserId.getSuccessValue() == null) {
				re.put("userInfo", "");
			} else {
				Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
				re.put("userInfo", userInfo);
			}
			logger.info("[getCompleteInfo()->success]");
			return ResultBuilder.successResult(re, MessageConstant.SEARCH_SUCCESS);
		} catch (Exception e) {
			logger.error("getCompleteInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<JSONObject> getPowerlessInfo(String orderId) {
		try {
			logger.info(String.format("[getPowerlessInfo()->request params--orderId:%s]", orderId));
			if (StringUtils.isBlank(orderId)) {
				logger.error("[getPowerlessInfo()->error:工单id不能为空！]");
				return ResultBuilder.failResult("工单id不能为空！");
			}
			RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService.getAlarmOrderInfoById(orderId);
			if (!alarmOrderInfoById.isSuccess() || alarmOrderInfoById.getSuccessValue() == null) {
				logger.error("[getPowerlessInfo()->fail:未查到工单信息！]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> successValue = alarmOrderInfoById.getSuccessValue();
			JSONObject parm = new JSONObject();
			parm.put(AlarmOrderConstants.PROCESS_ID, successValue.get(AlarmOrderConstants.PROCESS_ID));
			RpcResponse<Map<String, Object>> processVariables = activityProgressQuery.getProcessVariables(parm);
			if (processVariables == null || processVariables.getSuccessValue() == null) {
				logger.error("[getPowerlessInfo()->fail:未查到工单信息！]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> rejectCause = processVariables.getSuccessValue();

			JSONObject re = new JSONObject();
			re.put(AlarmOrderConstants.POWERLESS_REMARK,
					(String) rejectCause.get(AlarmOrderConstants.POWERLESS_REMARK));
			re.put(AlarmOrderConstants.POWERLESS_ORDER_PIC, rejectCause.get(AlarmOrderConstants.POWERLESS_ORDER_PIC));
			re.put(AlarmOrderConstants.POWERLESS_USERID,
					(String) rejectCause.get(AlarmOrderConstants.POWERLESS_USERID));

			// 封装类型信息
			String typeId = (String) rejectCause.get(AlarmOrderConstants.POWERLESS_ORDER_TYPE);
			re.put(AlarmOrderConstants.POWERLESS_ORDER_TYPE, typeId);
			RpcResponse<List<Map<String, Object>>> res = alarmOrderQueryService.getState(3);
			if (res != null && res.isSuccess() && res.getSuccessValue() != null) {
				List<Map<String, Object>> state = res.getSuccessValue();
				for (Map<String, Object> map : state) {
					String sign = (String) map.get("sign");
					if (sign.equals(typeId)) {
						re.put(AlarmOrderConstants.NORMAL_ORDER_TYPE_NAME, (String) map.get("name"));
						break;
					}
				}

			}
			String userId = (String) rejectCause.get(AlarmOrderConstants.POWERLESS_USERID);
			RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
			if (userByUserId == null || userByUserId.getSuccessValue() == null) {
				re.put("userInfo", "");
			} else {
				Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
				re.put("userInfo", userInfo);
			}
			logger.info("[getPowerlessInfo()->success]");
			return ResultBuilder.successResult(re, MessageConstant.SEARCH_SUCCESS);
		} catch (Exception e) {
			logger.error("getPowerlessInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:倒叙排列list中的map
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> reverseSort(List<Map<String, Object>> list) {
		List<Map<String, Object>> listObj = new ArrayList<>();
		for (int i = list.size() - 1; i >= 0; i--) {
			listObj.add(list.get(i));
		}
		return listObj;
	}



	public Result<Pages<Map<String, Object>>> notClaimAlarmOrder(JSONObject orderInfo) {

		try {
			logger.info(String.format("[notClaimAlarmOrder()->request params:%s]", orderInfo));
			if (orderInfo == null) {
				logger.error(String.format("[notClaimAlarmOrder()->error:%s]", "查询业务对象为null！"));
				return ResultBuilder.failResult("查询业务对象为null！");
			}
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			orderInfo.put(InterGatewayConstants.TOKEN, token);
			RpcResponse<Pages<Map<String, Object>>> res = alarmOrderQueryService.notClaimAlarmOrder(orderInfo);
			if (res.isSuccess()) {
				logger.info("[notClaimAlarmOrder()->success]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[notClaimAlarmOrder()->fail]");
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("notClaimAlarmOrder()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:查询待接收告警工单总数
	 * @param
	 * @return
	 */
	public Result<Integer> getNotClaimAlarmOrderTotal(JSONObject orderInfo) {

		try {
			logger.info(String.format("[getNotClaimAlarmOrderTotal()->request params:%s]", orderInfo));
			if (orderInfo == null) {
				logger.error(String.format("[notClaimAlarmOrder()->error:%s]", "查询业务对象为null！"));
				return ResultBuilder.failResult("查询业务对象为null！");
			}
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			orderInfo.put(InterGatewayConstants.TOKEN, token);
			RpcResponse<Integer> res = alarmOrderQueryService.getNotClaimAlarmOrderTotal(orderInfo);
			if (res.isSuccess()) {
				logger.info("[notClaimAlarmOrder()->success]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[notClaimAlarmOrder()->fail]");
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("notClaimAlarmOrder()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:告警统计数量
	 * @param alarmOrderCount
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result<PageInfo<Map>> alarmOrderCountInfo(AlarmOrderCount alarmOrderCount) {

		try {
			if (alarmOrderCount == null) {
				logger.error(String.format("[alarmOrderCountInfo()->error:%s]", "查询业务对象为null！"));
				return ResultBuilder.failResult("查询业务对象为null！");
			}

			RpcResponse<PageInfo<Map>> res = alarmOrderQueryService.alarmOrderCountInfo(alarmOrderCount);
			if (res.isSuccess()) {
				logger.info("[alarmOrderCountInfo()->success:告警统计数量查询成功]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[alarmOrderCountInfo()->fail:告警统计数量查询失败]");
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("alarmOrderCountInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:通过区域进行查询告警工单统计详情
	 * @param detailsJsonObj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result<PageInfo<Map>> alarmDetailsCount(AlarmCountDetails alarmCountDetails) {

		try {

			if (alarmCountDetails == null) {
				logger.error(String.format("[alarmDetailsCount()->error:%s]", "查询业务对象为null！"));
				return ResultBuilder.failResult("查询业务对象为null！");
			}

			RpcResponse<PageInfo<Map>> res = alarmOrderQueryService.alarmDetailsCount(alarmCountDetails);

			if (res.isSuccess()) {
				logger.info("[alarmDetailsCount()->success:告警工单统计详情查询成功]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[alarmDetailsCount()->fail:告警工单统计详情查询失败]");
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("alarmDetailsCount()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:导出工单详情列表
	 * @param accessSecret
	 * @param completeAddress
	 * @param aostName
	 * @param model
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ModelAndView exportAlarmOrderExcelInfo(String accessSecret, String completeAddress, String aostName,
			String facilitiesTypeId, ModelMap model) {

		try {
			logger.info(String.format("[exportAlarmOrderExcelInfo()->request params--aostName:%s,completeAddress:%s]",
					aostName, completeAddress));
			if (StringUtils.isBlank(completeAddress) || StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[alarmDetailsCount()->error:%s]", "查询业务对象为null！"));
				return null;
			}

			// 封装查询对象
			AlarmCountDetails alarmCountDetails = new AlarmCountDetails();
			alarmCountDetails.setAccessSecret(accessSecret);
			alarmCountDetails.setPageNum("0");
			alarmCountDetails.setPageSize("0");
			alarmCountDetails.setCompleteAddress(completeAddress);
			alarmCountDetails.setAostName(aostName);
			alarmCountDetails.setFacilitiesTypeId(facilitiesTypeId);

			// 构建导出格式
			Map<String, Object> map = new LinkedHashMap<>();
			map.put(AlarmOrderCountConstants.FACILITIESCODE_PAR, AlarmOrderCountConstants.FACILITIESCODE);
			map.put(AlarmOrderCountConstants.FCOMPLETEADDRESS_PAR, AlarmOrderCountConstants.FCOMPLETEADDRESS);
			map.put(AlarmOrderCountConstants.ALARMLEVEL_PAR, AlarmOrderCountConstants.ALARMLEVEL);
			map.put(AlarmOrderCountConstants.FACILITYTYPEALIAS_PAR, AlarmOrderCountConstants.FACILITYTYPEALIAS);
			map.put(AlarmOrderCountConstants.ORDERTYPE_PAR, AlarmOrderCountConstants.ORDERTYPE);
			map.put(AlarmOrderCountConstants.ALARMDESC_PAR, AlarmOrderCountConstants.ALARMDESC);
			map.put(AlarmOrderCountConstants.ALARMTIME_PAR, AlarmOrderCountConstants.ALARMTIME);
			map.put(AlarmOrderCountConstants.USERNAME_PAR, AlarmOrderCountConstants.USERNAME);
			map.put(AlarmOrderCountConstants.PHONE_PAR, AlarmOrderCountConstants.PHONE);

			model.put(ExcelView.EXCEL_NAME, AlarmOrderCountConstants.EXCEL_NAME);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			// 获取所有数据
			RpcResponse<PageInfo<Map>> res = alarmOrderQueryService.alarmDetailsCount(alarmCountDetails);
			List<Map> list = res.getSuccessValue().getList();

			// 成功封裝數據
			if (res.isSuccess()) {
				logger.info(String.format("[exportAlarmOrderExcelInfo()->suc:%s]", res.getMessage()));
				model.put(ExcelView.EXCEL_DATASET, list);
			} else {
				logger.error(String.format("[exportAlarmOrderExcelInfo()->error:%s]", res.getException()));
				return null;
			}

			View excelView = new ExcelView();
			return new ModelAndView(excelView);
		} catch (Exception e) {
			logger.error("alarmDetailsCount()->exception", e);
			return null;
		}

	}



	/**
	 * 
	 * @Description:导出告警信息统计
	 * @param accessSecret
	 * @param model
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ModelAndView exportAlarmOrderCount(String accessSecret, String startTime, String endTime,
			String facilitiesTypeId, ModelMap model) {

		try {
			logger.info(String.format("[exportAlarmOrderCount()->request params--startTime:%s,endTime:%s]", startTime,
					endTime));
			if (StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[exportAlarmOrderCount()->error:%s]", "查询业务对象为null！"));
				return null;
			}

			// 构建对象
			AlarmOrderCount alarmOrderCount = new AlarmOrderCount();
			alarmOrderCount.setAccessSecret(accessSecret);
			alarmOrderCount.setPageNum("0");
			alarmOrderCount.setPageSize("0");
			alarmOrderCount.setStartTime(startTime);
			alarmOrderCount.setEndTime(endTime);
			alarmOrderCount.setFacilitiesTypeId(facilitiesTypeId);

			// 构建导出样式

			Map<String, Object> map = new LinkedHashMap<>();
			map.put(AlarmOrderCountConstants.RADDRESS_PAR, AlarmOrderCountConstants.RADDRESS);
			map.put(AlarmOrderCountConstants.DEVICEMAINTENANCE_PAR, AlarmOrderCountConstants.DEVICEMAINTENANCE);
			map.put(AlarmOrderCountConstants.OTHER_PAR, AlarmOrderCountConstants.OTHER);
			map.put(AlarmOrderCountConstants.EQUIPMENTALARM_PAR, AlarmOrderCountConstants.EQUIPMENTALARM);
			map.put(AlarmOrderCountConstants.CONSTRUCTIONPUTLAN_PAR, AlarmOrderCountConstants.CONSTRUCTIONPUTLAN);
			map.put(AlarmOrderCountConstants.EMERGENCYREPAIR_PAR, AlarmOrderCountConstants.EMERGENCYREPAIR);
			map.put(AlarmOrderCountConstants.VEHICLEALARM_PAR, AlarmOrderCountConstants.VEHICLEALARM);
			map.put(AlarmOrderCountConstants.CANTREPAIR_PAR, AlarmOrderCountConstants.CANTREPAIR);

			model.put(ExcelView.EXCEL_NAME, AlarmOrderCountConstants.EXCEL_NAME_02);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			RpcResponse<PageInfo<Map>> res = alarmOrderQueryService.alarmOrderCountInfo(alarmOrderCount);
			List<Map> list = res.getSuccessValue().getList();

			// 成功封裝數據
			if (res.isSuccess()) {
				logger.info(String.format("[exportAlarmOrderCount()->suc:%s]", res.getMessage()));
				model.put(ExcelView.EXCEL_DATASET, list);
			} else {
				logger.error(String.format("[exportAlarmOrderCount()->error:%s]", res.getException()));
				return null;
			}

			View excelView = new ExcelView();
			return new ModelAndView(excelView);
		} catch (Exception e) {
			logger.error("exportAlarmOrderCount()->exception", e);
			return null;
		}

	}



	/**
	 * 
	 * @Description: 统计所有告警工单
	 * @param
	 * @return
	 */

	public Result<PageInfo<CountAlarmOrderDto>> countAllAlarmOrder(String queryParams) {
		logger.info(String.format("[countAllAlarmOrder()->request params:%s]", queryParams));
		if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
			logger.error(String.format("[countAllAlarmOrder()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<CountAlarmOrderDto>> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"countAllAlarmOrder", json, AlarmOrderCountConstants.ACCESSSECRET,
					AlarmOrderCountConstants.PAGE_NUM, AlarmOrderCountConstants.PAGE_SIZE);
			if (null != checkBusinessKey) {
				logger.error(checkBusinessKey.getMessage());
				return ResultBuilder.noBusinessResult();
			}
			RpcResponse<PageInfo<CountAlarmOrderDto>> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"countAllAlarmOrder", json, AlarmOrderCountConstants.FACILITIES_CODE,
					AlarmOrderCountConstants.USERNAME_PAR, AlarmOrderCountConstants.ORGANIZATION_ID,
					AlarmOrderCountConstants.PROCESS_STATE);
			if (null != containsParamKey) {
				logger.error(containsParamKey.getMessage());
				return ResultBuilder.noBusinessResult();
			}
			String pageNum = json.getString(AlarmOrderCountConstants.PAGE_NUM);
			String pageSize = json.getString(AlarmOrderCountConstants.PAGE_SIZE);
			if (!StringUtils.isNumeric(pageNum)) {
				logger.error("当前页码参数必须是数字格式");
				return ResultBuilder.failResult("当前页码参数必须是数字格式");
			}
			if (!StringUtils.isNumeric(pageSize)) {
				logger.error("当前页码参数必须是数字格式");
				return ResultBuilder.failResult("每页条数参数必须是数字格式");
			}
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			json.put(InterGatewayConstants.TOKEN, token);
			RpcResponse<PageInfo<CountAlarmOrderDto>> countAllAlarmOrder = alarmOrderQueryService
					.countAllAlarmOrder(json);
			PageInfo<CountAlarmOrderDto> successValue = countAllAlarmOrder.getSuccessValue();
			if (!countAllAlarmOrder.isSuccess() || null == successValue) {
				logger.error(String.format("[countAllAlarmOrder()->fail:%s]", countAllAlarmOrder.getMessage()));
				return ResultBuilder.failResult(countAllAlarmOrder.getMessage());
			} else {
				logger.info(String.format("[countAllAlarmOrder()->success:%s]", countAllAlarmOrder.getMessage()));
				return ResultBuilder.successResult(successValue, countAllAlarmOrder.getMessage());
			}

		} catch (Exception e) {
			logger.error("countAllAlarmOrder()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public Result<PageInfo<Map<String, Object>>> countAlarmOrderByOrg(String queryParams) {
		logger.info(String.format("[countAlarmOrderByOrg()->request params:%s]", queryParams));
		if (ParamChecker.isBlank(queryParams) || ParamChecker.isNotMatchJson(queryParams)) {
			logger.error(String.format("[countAllAlarmOrder()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject json = JSONObject.parseObject(queryParams);
			RpcResponse<PageInfo<Map<String, Object>>> checkBusinessKey = CheckParameterUtil.checkBusinessKey(logger,
					"countAlarmOrderByOrg", json, AlarmOrderCountConstants.ACCESSSECRET,
					AlarmOrderCountConstants.PAGE_NUM, AlarmOrderCountConstants.PAGE_SIZE);
			if (null != checkBusinessKey) {
				logger.error(checkBusinessKey.getMessage());
				return ResultBuilder.noBusinessResult();
			}
			RpcResponse<PageInfo<Map<String, Object>>> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"countAlarmOrderByOrg", json, AlarmOrderCountConstants.ORGANIZATION_ID,
					AlarmOrderCountConstants.FACILITIES_TYPE_ID, AlarmOrderCountConstants.STARTTIME,
					AlarmOrderCountConstants.ENDTIME);
			if (null != containsParamKey) {
				logger.error(containsParamKey.getMessage());
				return ResultBuilder.noBusinessResult();
			}
			String pageNum = json.getString(AlarmOrderCountConstants.PAGE_NUM);
			String pageSize = json.getString(AlarmOrderCountConstants.PAGE_SIZE);
			if (!StringUtils.isNumeric(pageNum)) {
				logger.error("当前页码参数必须是数字格式");
				return ResultBuilder.failResult("当前页码参数必须是数字格式");
			}
			if (!StringUtils.isNumeric(pageSize)) {
				logger.error("当前页码参数必须是数字格式");
				return ResultBuilder.failResult("每页条数参数必须是数字格式");
			}
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			json.put(InterGatewayConstants.TOKEN, token);
			RpcResponse<PageInfo<Map<String, Object>>> countAlarmOrderByOrg = alarmOrderQueryService
					.countAlarmOrderByOrg(json);
			PageInfo<Map<String, Object>> successValue = countAlarmOrderByOrg.getSuccessValue();
			if (!countAlarmOrderByOrg.isSuccess() || null == successValue) {
				logger.error(String.format("[countAlarmOrderByOrg()->fail:%s]", countAlarmOrderByOrg.getMessage()));
				return ResultBuilder.failResult(countAlarmOrderByOrg.getMessage());
			} else {
				logger.info(String.format("[countAlarmOrderByOrg()->success:%s]", countAlarmOrderByOrg.getMessage()));
				return ResultBuilder.successResult(successValue, countAlarmOrderByOrg.getMessage());
			}

		} catch (Exception e) {
			logger.error("countAlarmOrderByOrg()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:导出告警信息统计
	 * @param accessSecret
	 * @param model
	 * @return
	 */
	public ModelAndView exprotAlarmOrderCountByOrg(JSONObject json, ModelMap model) {

		try {
			logger.info(String.format("[exprotAlarmOrderCountByOrg()->request params--startTime:%s,endTime:%s]",
					json.getString(AlarmOrderCountConstants.STARTTIME),
					json.getString(AlarmOrderCountConstants.ENDTIME)));
			if (StringUtils.isBlank(json.getString(AlarmOrderCountConstants.ACCESSSECRET))) {
				logger.error(String.format("[exprotAlarmOrderCountByOrg()->error:%s]", "查询业务对象为null！"));
				return null;
			}

			// 构建对象
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			json.put(InterGatewayConstants.TOKEN, token);
			json.put(AlarmOrderCountConstants.PAGE_NUM, "0");
			json.put(AlarmOrderCountConstants.PAGE_SIZE, "0");
			// 构建导出样式

			Map<String, Object> map = new LinkedHashMap<>();
			map.put(AlarmOrderCountConstants.ORGANIZATION_NAME, AlarmOrderCountConstants.ORGANIZATION_NAME_CH);
			map.put(AlarmOrderCountConstants.DEVICEMAINTENANCE_PAR, AlarmOrderCountConstants.DEVICEMAINTENANCE);
			map.put(AlarmOrderCountConstants.OTHER_PAR, AlarmOrderCountConstants.OTHER);
			map.put(AlarmOrderCountConstants.EQUIPMENTALARM_PAR, AlarmOrderCountConstants.EQUIPMENTALARM);
			map.put(AlarmOrderCountConstants.CONSTRUCTIONPUTLAN_PAR, AlarmOrderCountConstants.CONSTRUCTIONPUTLAN);
			map.put(AlarmOrderCountConstants.EMERGENCYREPAIR_PAR, AlarmOrderCountConstants.EMERGENCYREPAIR);
			map.put(AlarmOrderCountConstants.VEHICLEALARM_PAR, AlarmOrderCountConstants.VEHICLEALARM);
			map.put(AlarmOrderCountConstants.CANTREPAIR_PAR, AlarmOrderCountConstants.CANTREPAIR);
			map.put(AlarmOrderCountConstants.HANDLED_PAR, AlarmOrderCountConstants.HANDLED);
			map.put(AlarmOrderCountConstants.NOTHANDLED_PAR, AlarmOrderCountConstants.NOTHANDLED);

			model.put(ExcelView.EXCEL_NAME, AlarmOrderCountConstants.EXCEL_NAME_02);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			RpcResponse<PageInfo<Map<String, Object>>> res = alarmOrderQueryService.countAlarmOrderByOrg(json);
			List<Map<String, Object>> list = res.getSuccessValue().getList();

			// 成功封裝數據
			if (res.isSuccess()) {
				logger.info(String.format("[exprotAlarmOrderCountByOrg()->suc:%s]", res.getMessage()));
				model.put(ExcelView.EXCEL_DATASET, list);
			} else {
				logger.error(String.format("[exprotAlarmOrderCountByOrg()->error:%s]", res.getException()));
				return null;
			}

			View excelView = new ExcelView();
			return new ModelAndView(excelView);
		} catch (Exception e) {
			logger.error("exportAlarmOrderCount()->exception", e);
			return null;
		}

	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result<JSONObject> getOrderProcessInfo(String orderId) {
		try {
			logger.info(String.format("[getOrderProcessInfo()->request params--orderId:%s]", orderId));
			if (StringUtils.isBlank(orderId)) {
				logger.error("[getCompleteInfo()->error:工单id不能为空！]");
				return ResultBuilder.failResult("工单id不能为空！");
			}
			RpcResponse<Map<String, Object>> alarmOrderInfoById = alarmOrderQueryService
					.getAlarmOrderAndFacInfo(orderId);
			if (!alarmOrderInfoById.isSuccess() || alarmOrderInfoById.getSuccessValue() == null) {
				logger.error("[getCompleteInfo()->fail:未查到工单信息！]");
				return ResultBuilder.failResult("未查到工单信息！");
			}
			Map<String, Object> baseOrderInfo = alarmOrderInfoById.getSuccessValue();
			JSONObject resultJson = new JSONObject();

			resultJson.putAll(baseOrderInfo);
			// String processState =
			// baseOrderInfo.get(AlarmOrderConstants.PROCESS_STATE) + "";

			// 查询正常完成信息
			JSONObject parm = new JSONObject();
			parm.put(AlarmOrderConstants.PROCESS_ID, baseOrderInfo.get(AlarmOrderConstants.PROCESS_ID));
			RpcResponse<List<Map<String, Object>>> getNodeVariables = activityProgressQuery.getNodeVariables(parm);
			if (getNodeVariables == null || getNodeVariables.getSuccessValue() == null) {
				logger.error("[getOrderProcessInfo()->fail:无法查询到节点状态！]");
				resultJson.put("nodeInfo", Lists.newArrayList());
			}

			// 查询下拉框信息,告警工单类型
			RpcResponse<List<Map<String, Object>>> res = alarmOrderQueryService.getState(2);

			// 存储完成告警工单类型sign和名字对应关系
			Map<Object, Object> alarmOrderType = Maps.newHashMap();
			if (res != null && res.isSuccess() && res.getSuccessValue() != null) {
				List<Map<String, Object>> state = res.getSuccessValue();
				for (Map<String, Object> map : state) {
					alarmOrderType.put(map.get("sign"), map.get("name"));
				}
			}

			// 查询下拉框类型，告警转故障的问题类型
			RpcResponse<List<Map<String, Object>>> powerlessOrderTypeRes = alarmOrderQueryService.getState(3);
			Map<Object, Object> alarmToFaultProblemType = Maps.newHashMap();
			if (powerlessOrderTypeRes != null && powerlessOrderTypeRes.isSuccess()
					&& powerlessOrderTypeRes.getSuccessValue() != null) {
				List<Map<String, Object>> state = powerlessOrderTypeRes.getSuccessValue();
				for (Map<String, Object> map : state) {
					alarmToFaultProblemType.put(map.get("sign"), map.get("name"));
				}
			}

			// 新建数组，用于存储节点信息
			List<Map<String, Object>> nodeInfo = Lists.newLinkedList();

			for (Map<String, Object> nodeVariables : getNodeVariables.getSuccessValue()) {
				// 当节点中包含apply_userId时，则为告警完成节点
				if (nodeVariables.containsKey(AlarmOrderConstants.APPLY_USERID)) {
					// 当节点为完成节点时，提取其中信息
					HashMap<String, Object> oneNodeInfo = Maps.newHashMap();
					// 获取备注
					oneNodeInfo.put(AlarmOrderConstants.NORMAL_REMARK,
							(String) nodeVariables.get(AlarmOrderConstants.NORMAL_REMARK));
					// 获取完工图片
					oneNodeInfo.put(AlarmOrderConstants.NORMAL_ORDER_PIC,
							nodeVariables.get(AlarmOrderConstants.NORMAL_ORDER_PIC));

					// 获取处理类型
					String typeId = (String) nodeVariables.get(AlarmOrderConstants.NORMAL_ORDER_TYPE);
					oneNodeInfo.put(AlarmOrderConstants.NORMAL_ORDER_TYPE, typeId);
					oneNodeInfo.put(AlarmOrderConstants.NORMAL_ORDER_TYPE_NAME,
							alarmOrderType.getOrDefault(typeId, ""));
					// 获取处理人id
					String userId = (String) nodeVariables.get(AlarmOrderConstants.APPLY_USERID);
					oneNodeInfo.put("applyUserId", userId);
					// 获取处理人姓名
					RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
					if (userByUserId == null || userByUserId.getSuccessValue() == null) {
						oneNodeInfo.put("applyUserName", "");
					} else {
						Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
						oneNodeInfo.put("applyUserName", userInfo.getOrDefault("userName", ""));
					}
					nodeInfo.add(oneNodeInfo);
				}
				// 当节点中包含approveCompleteUserId时，则为完工审批节点，结果为同意
				else if (nodeVariables.containsKey(AlarmOrderConstants.APPROVE_COMPLETE_USERID)) {
					HashMap<String, Object> oneNodeInfo = Maps.newHashMap();
					// 获取完工审批人id
					String userId = (String) nodeVariables.get(AlarmOrderConstants.APPROVE_COMPLETE_USERID);
					oneNodeInfo.put("approvalCompleteUserId", userId);
					// 获取姓名
					RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
					if (userByUserId == null || userByUserId.getSuccessValue() == null) {
						oneNodeInfo.put("approvalCompleteUserName", "");
					} else {
						Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
						oneNodeInfo.put("approvalCompleteUserName", userInfo.getOrDefault("userName", ""));
					}
					oneNodeInfo.put("completeResult", pass_ch);
					nodeInfo.add(oneNodeInfo);
				}
				// 当节点中包含rejectCompleteUserId时，则为完工审批节点，结果为拒绝
				else if (nodeVariables.containsKey(AlarmOrderConstants.REJECT_COMPLETE_USERID)) {
					HashMap<String, Object> oneNodeInfo = Maps.newHashMap();
					// 获取完工审批人id
					String userId = (String) nodeVariables.get(AlarmOrderConstants.REJECT_COMPLETE_USERID);
					oneNodeInfo.put("approvalCompleteUserId", userId);
					// 获取姓名
					RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
					if (userByUserId == null || userByUserId.getSuccessValue() == null) {
						oneNodeInfo.put("approvalCompleteUserName", "");
					} else {
						Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
						oneNodeInfo.put("approvalCompleteUserName", userInfo.getOrDefault("userName", ""));
					}
					oneNodeInfo.put("completeRejectRemark",
							nodeVariables.getOrDefault(AlarmOrderConstants.REJECT_REMARK, ""));
					oneNodeInfo.put("completeResult", notPass_ch);
					nodeInfo.add(oneNodeInfo);
				}
				// 当节点中包含powerless_userId时，则为告警转故障节点
				else if (nodeVariables.containsKey(AlarmOrderConstants.POWERLESS_USERID)) {
					HashMap<String, Object> oneNodeInfo = Maps.newHashMap();
					// 获取人员id
					String userId = nodeVariables.get(AlarmOrderConstants.POWERLESS_USERID) + "";
					oneNodeInfo.put("powerlessUserId", userId);
					// 获取姓名
					RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
					if (userByUserId == null || userByUserId.getSuccessValue() == null) {
						oneNodeInfo.put("powerlessUserName", "");
					} else {
						Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
						oneNodeInfo.put("powerlessUserName", userInfo.getOrDefault("userName", ""));
					}
					if (nodeVariables.containsKey(AlarmOrderConstants.POWERLESS_ORDER_PIC)) {
						oneNodeInfo.put("powerlessImg", nodeVariables.get(AlarmOrderConstants.POWERLESS_ORDER_PIC));
					} else {
						oneNodeInfo.put("powerlessImg", Lists.newArrayList());
					}
					if (nodeVariables.containsKey(AlarmOrderConstants.POWERLESS_REMARK)) {
						oneNodeInfo.put("powerlessRemark", nodeVariables.get(AlarmOrderConstants.POWERLESS_REMARK));
					}
					String endTimestamp = checkTimestamp(nodeVariables.get(AlarmOrderConstants.END_TIME));

					oneNodeInfo.put("powerlessTime", endTimestamp);

					oneNodeInfo.put("faultType", alarmToFaultProblemType
							.getOrDefault(nodeVariables.get(AlarmOrderConstants.POWERLESS_ORDER_TYPE), ""));
					nodeInfo.add(oneNodeInfo);
				}
				// 当节点中包含reject_userId时，则为转故障审批节点，结果为拒绝
				else if (nodeVariables.containsKey(AlarmOrderConstants.REJECT_USERID)) {
					HashMap<String, Object> oneNodeInfo = Maps.newHashMap();
					// 获取审批人id
					String userId = nodeVariables.get(AlarmOrderConstants.REJECT_USERID) + "";
					oneNodeInfo.put("handlerUserId", userId);
					// 获取姓名
					RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
					if (userByUserId == null || userByUserId.getSuccessValue() == null) {
						oneNodeInfo.put("handlerUserName", "");
					} else {
						Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
						oneNodeInfo.put("handlerUserName", userInfo.getOrDefault("userName", ""));
					}
					// 获取拒绝理由
					if (nodeVariables.containsKey(AlarmOrderConstants.REJECT_REMARK)) {
						oneNodeInfo.put("powerlessRejectRemark", nodeVariables.get(AlarmOrderConstants.REJECT_REMARK));
					}
					String endTimestamp = checkTimestamp(nodeVariables.get(AlarmOrderConstants.END_TIME));

					oneNodeInfo.put(AlarmOrderConstants.END_TIME, endTimestamp);
					oneNodeInfo.put("powerlessResult", notPass_ch);
					nodeInfo.add(oneNodeInfo);
				}
				// 当节点中包含approve_userId时，则为转故障审批节点，结果为同意
				else if (nodeVariables.containsKey(AlarmOrderConstants.APPROVE_USERID)) {
					HashMap<String, Object> oneNodeInfo = Maps.newHashMap();
					// 获取审批人id
					String userId = nodeVariables.get(AlarmOrderConstants.APPROVE_USERID) + "";
					oneNodeInfo.put("handlerUserId", userId);
					// 获取姓名
					RpcResponse userByUserId = userBaseQueryService.getUserByUserId(userId);
					if (userByUserId == null || userByUserId.getSuccessValue() == null) {
						oneNodeInfo.put("handlerUserName", "");
					} else {
						Map<String, Object> userInfo = (Map<String, Object>) userByUserId.getSuccessValue();
						oneNodeInfo.put("handlerUserName", userInfo.getOrDefault("userName", ""));
					}
					String endTimestamp = checkTimestamp(nodeVariables.get(AlarmOrderConstants.END_TIME));

					oneNodeInfo.put(AlarmOrderConstants.END_TIME, endTimestamp);
					oneNodeInfo.put("powerlessResult", pass_ch);
					nodeInfo.add(oneNodeInfo);
				}

				// RpcResponse<Map<String, Object>> completeInfoRes =
				// activityProgressQuery.getProcessVariables(parm);
				// if (completeInfoRes == null ||
				// completeInfoRes.getSuccessValue()
				// == null) {
				// logger.error("[getCompleteInfo()->fail:未查到工单信息！]");
				// return ResultBuilder.failResult("未查到工单信息！");
				// }

				// Map<String, Object> completeInfo =
				// completeInfoRes.getSuccessValue();

				// 正常完成
				// String completed = "4";
				/*
				 * if (completed.equals(processState)) {
				 * 
				 * JSONObject re = new JSONObject();
				 * re.put(AlarmOrderConstants.NORMAL_REMARK, (String)
				 * completeInfo.get(AlarmOrderConstants.NORMAL_REMARK));
				 * re.put(AlarmOrderConstants.NORMAL_ORDER_PIC,
				 * completeInfo.get(AlarmOrderConstants.NORMAL_ORDER_PIC));
				 * re.put(AlarmOrderConstants.APPLY_USERID, (String)
				 * completeInfo.get(AlarmOrderConstants.APPLY_USERID));
				 * 
				 * // 封装类型信息 String typeId = (String)
				 * completeInfo.get(AlarmOrderConstants.NORMAL_ORDER_TYPE);
				 * re.put(AlarmOrderConstants.NORMAL_ORDER_TYPE, typeId);
				 * RpcResponse<List<Map<String, Object>>> res =
				 * alarmOrderQueryService.getState(2); if (res != null &&
				 * res.isSuccess() && res.getSuccessValue() != null) {
				 * List<Map<String, Object>> state = res.getSuccessValue(); for
				 * (Map<String, Object> map : state) { String sign = (String)
				 * map.get("sign"); if (sign.equals(typeId)) {
				 * re.put(AlarmOrderConstants.NORMAL_ORDER_TYPE_NAME, (String)
				 * map.get("name")); break; } }
				 * 
				 * }
				 * 
				 * String userId = (String)
				 * completeInfo.get(AlarmOrderConstants.APPLY_USERID);
				 * RpcResponse userByUserId =
				 * userBaseQueryService.getUserByUserId(userId); if
				 * (userByUserId == null || userByUserId.getSuccessValue() ==
				 * null) { re.put("userName", ""); // re1.put("userName", ""); }
				 * else { Map<String, Object> userInfo = (Map<String, Object>)
				 * userByUserId.getSuccessValue(); re.put("userName",
				 * userInfo.getOrDefault("userName", "")); }
				 * 
				 * resultJson.put("completeInfo", re);
				 * 
				 * } else { resultJson.put("completeInfo", null); }
				 */

				// if (powerlessed.equals(processState) ||
				// powerlessReject.equals(processState)
				// || powerlessing.equals(processState) ||
				// completed.equals(processState)) {
				//
				// // 转故障信息
				// RpcResponse<List<Map<String, Object>>> getNodeVariables =
				// activityProgressQuery.getNodeVariables(parm);
				// if (getNodeVariables == null ||
				// getNodeVariables.getSuccessValue() == null) {
				// logger.error("[getRejectCause()->fail:当前没有拒绝原因，或者工单未被拒绝！]");
				// resultJson.put("powerlessInfo", Lists.newArrayList());
				// } else {
				//
				// // 封装拒绝原因
				// List<Map<String, Object>> rejectCause =
				// getNodeVariables.getSuccessValue();
				// List<Map<String, Object>> powerlessInfo =
				// Lists.newLinkedList();
				// RpcResponse<List<Map<String, Object>>> powerlessOrderTypeRes
				// = alarmOrderQueryService.getState(3);
				// Map<Object, Object> typeRsSig = Maps.newHashMap();
				// if (powerlessOrderTypeRes != null &&
				// powerlessOrderTypeRes.isSuccess()
				// && powerlessOrderTypeRes.getSuccessValue() != null) {
				// List<Map<String, Object>> state =
				// powerlessOrderTypeRes.getSuccessValue();
				// for (Map<String, Object> map : state) {
				// typeRsSig.put(map.get("sign"), map.get("name"));
				// }
				//
				// }
				//
				// for (Map<String, Object> map : rejectCause) {
				// String handlerId = "";
				// HashMap<String, Object> oneInfoMap = Maps.newHashMap();
				// if (map.containsKey(AlarmOrderConstants.REJECT_USERID)) {
				// handlerId = map.get(AlarmOrderConstants.REJECT_USERID) + "";
				// } else if
				// (map.containsKey(AlarmOrderConstants.POWERLESS_USERID)) {
				// handlerId = map.get(AlarmOrderConstants.POWERLESS_USERID) +
				// "";
				// } else if
				// (map.containsKey(AlarmOrderConstants.APPROVE_USERID)) {
				// handlerId = map.get(AlarmOrderConstants.APPROVE_USERID) + "";
				// } else {
				// continue;
				// }
				// RpcResponse handlerInfo =
				// userBaseQueryService.getUserByUserId(handlerId);
				// if (handlerInfo == null || handlerInfo.getSuccessValue() ==
				// null) {
				// oneInfoMap.put("handlerName", "");
				// } else {
				// Map<String, Object> userInfo = (Map<String, Object>)
				// handlerInfo.getSuccessValue();
				// oneInfoMap.put("handlerName",
				// userInfo.getOrDefault("userName", ""));
				// }
				// oneInfoMap.put("handlerId", handlerId);
				//
				// if (map.containsKey(AlarmOrderConstants.POWERLESS_ORDER_PIC))
				// {
				// oneInfoMap.put("powerlessImg",
				// map.get(AlarmOrderConstants.POWERLESS_ORDER_PIC));
				// } else {
				// oneInfoMap.put("powerlessImg", Lists.newArrayList());
				// }
				//
				// if (map.containsKey(AlarmOrderConstants.REJECT_REMARK)) {
				// oneInfoMap.put("result",
				// map.get(AlarmOrderConstants.REJECT_REMARK));
				// } else if
				// (map.containsKey(AlarmOrderConstants.POWERLESS_REMARK)) {
				// oneInfoMap.put("result",
				// map.get(AlarmOrderConstants.POWERLESS_REMARK));
				// }
				//
				// String startTimestamp =
				// checkTimestamp(map.get(AlarmOrderConstants.START_TIME));
				// oneInfoMap.put(AlarmOrderConstants.START_TIME,
				// startTimestamp);
				//
				// String endTimestamp =
				// checkTimestamp(map.get(AlarmOrderConstants.END_TIME));
				//
				// oneInfoMap.put(AlarmOrderConstants.END_TIME, endTimestamp);
				//
				// if
				// (map.containsKey(AlarmOrderConstants.POWERLESS_ORDER_TYPE)) {
				// // oneInfoMap.put(AlarmOrderConstants.POWERLESS_ORDER_TYPE,
				// //
				// typeRsSig.getOrDefault(map.get(AlarmOrderConstants.POWERLESS_ORDER_TYPE),
				// // ""));
				// oneInfoMap.put(AlarmOrderConstants.MESSAGE,
				// typeRsSig.getOrDefault(map.get(AlarmOrderConstants.POWERLESS_ORDER_TYPE),
				// ""));
				// } else {
				//
				// if (notPass.equals(map.get(AlarmOrderConstants.MESSAGE))) {
				// oneInfoMap.put(AlarmOrderConstants.MESSAGE, notPass_ch);
				// } else if (pass.equals(map.get(AlarmOrderConstants.MESSAGE)))
				// {
				// oneInfoMap.put(AlarmOrderConstants.MESSAGE, pass_ch);
				// }
				// }
				//
				// powerlessInfo.add(oneInfoMap);
				//
				// }
				//
				// // List<Map<String, Object>> reverseSort =
				// // reverseSort(powerlessInfo);
				//
				// resultJson.put("powerlessInfo", powerlessInfo);
				// }
				//
				// } else {
				// resultJson.put("powerlessInfo", Lists.newArrayList());
				// }
				//
				// // 当工单处于完工待审批,处理中，完工状态时
				// if (approve_before_end.equals(processState) ||
				// completed.equals(processState)
				// || dealing.equals(processState)) {
				// RpcResponse<List<Map<String, Object>>> getNodeVariables =
				// activityProgressQuery.getNodeVariables(parm);
				// if (getNodeVariables == null ||
				// getNodeVariables.getSuccessValue() == null) {
				// logger.error("[getOrderProcessInfo()->fail:当前没有完工审批信息！]");
				// resultJson.put("approvalBeforeCompleteInfo",
				// Lists.newArrayList());
				// } else {
				// // 查询下拉框信息
				// RpcResponse<List<Map<String, Object>>> res =
				// alarmOrderQueryService.getState(2);
				//
				// // 数组用来封装各个节点信息
				// List<Map<String, Object>> approvalBeforeCompleteInfo =
				// Lists.newLinkedList();
				// // 存储完成sign和名字对应关系
				// Map<Object, Object> signAndName = Maps.newHashMap();
				// if (res != null && res.isSuccess() && res.getSuccessValue()
				// != null) {
				// List<Map<String, Object>> state = res.getSuccessValue();
				// for (Map<String, Object> map : state) {
				// signAndName.put(map.get("sign"), map.get("name"));
				// }
				// }
				// for (Map<String, Object> nodeMap :
				// getNodeVariables.getSuccessValue()) {
				// if (nodeMap.containsKey(AlarmOrderConstants.APPLY_USERID)) {
				// // 当节点为完成节点时，提取其中信息
				// HashMap<String, Object> oneCompleteInfo = Maps.newHashMap();
				// // 获取备注
				// oneCompleteInfo.put(AlarmOrderConstants.NORMAL_REMARK,
				// (String) nodeMap.get(AlarmOrderConstants.NORMAL_REMARK));
				// // 获取完工图片
				// oneCompleteInfo.put(AlarmOrderConstants.NORMAL_ORDER_PIC,
				// nodeMap.get(AlarmOrderConstants.NORMAL_ORDER_PIC));
				//
				// // 获取处理类型
				// String typeId = (String)
				// nodeMap.get(AlarmOrderConstants.NORMAL_ORDER_TYPE);
				// oneCompleteInfo.put(AlarmOrderConstants.NORMAL_ORDER_TYPE,
				// typeId);
				// oneCompleteInfo.put(AlarmOrderConstants.NORMAL_ORDER_TYPE_NAME,
				// signAndName.getOrDefault(typeId, ""));
				// // 获取处理人id
				// String userId = (String)
				// nodeMap.get(AlarmOrderConstants.APPLY_USERID);
				// oneCompleteInfo.put(AlarmOrderConstants.APPLY_USERID,
				// userId);
				// // 获取处理人姓名
				// RpcResponse userByUserId =
				// userBaseQueryService.getUserByUserId(userId);
				// if (userByUserId == null || userByUserId.getSuccessValue() ==
				// null) {
				// oneCompleteInfo.put("userName", "");
				// } else {
				// Map<String, Object> userInfo = (Map<String, Object>)
				// userByUserId.getSuccessValue();
				// oneCompleteInfo.put("userName",
				// userInfo.getOrDefault("userName", ""));
				// }
				// approvalBeforeCompleteInfo.add(oneCompleteInfo);
				//
				// } else if
				// (nodeMap.containsKey(AlarmOrderConstants.APPROVE_COMPLETE_USERID))
				// {
				// HashMap<String, Object> approveInfo = Maps.newHashMap();
				// // 获取完工审批同意人id
				// String userId = (String)
				// nodeMap.get(AlarmOrderConstants.APPROVE_COMPLETE_USERID);
				// approveInfo.put(AlarmOrderConstants.APPROVE_COMPLETE_USERID,
				// userId);
				// // 获取姓名
				// RpcResponse userByUserId =
				// userBaseQueryService.getUserByUserId(userId);
				// if (userByUserId == null || userByUserId.getSuccessValue() ==
				// null) {
				// approveInfo.put("approvalUserName", "");
				// } else {
				// Map<String, Object> userInfo = (Map<String, Object>)
				// userByUserId.getSuccessValue();
				// approveInfo.put("approvalUserName",
				// userInfo.getOrDefault("userName", ""));
				// }
				// approvalBeforeCompleteInfo.add(approveInfo);
				// } else if
				// (nodeMap.containsKey(AlarmOrderConstants.REJECT_COMPLETE_USERID))
				// {
				// HashMap<String, Object> rejectInfo = Maps.newHashMap();
				// // 获取完工审批拒绝人id
				// String userId = (String)
				// nodeMap.get(AlarmOrderConstants.REJECT_COMPLETE_USERID);
				// rejectInfo.put(AlarmOrderConstants.REJECT_COMPLETE_USERID,
				// userId);
				// // 获取姓名
				// RpcResponse userByUserId =
				// userBaseQueryService.getUserByUserId(userId);
				// if (userByUserId == null || userByUserId.getSuccessValue() ==
				// null) {
				// rejectInfo.put("approvalUserName", "");
				// } else {
				// Map<String, Object> userInfo = (Map<String, Object>)
				// userByUserId.getSuccessValue();
				// rejectInfo.put("approvalUserName",
				// userInfo.getOrDefault("userName", ""));
				// }
				// rejectInfo.put("reject_remark",
				// nodeMap.getOrDefault(AlarmOrderConstants.REJECT_REMARK, ""));
				// approvalBeforeCompleteInfo.add(rejectInfo);
				// }
				// }
				// resultJson.put("completeInfo", approvalBeforeCompleteInfo);
				// }
				// } else {
				// resultJson.put("completeInfo", Lists.newArrayList());
				// }

			}
			resultJson.put("nodeInfo", nodeInfo);
			return ResultBuilder.successResult(resultJson, "查询成功");
		} catch (Exception e) {
			logger.error("getCompleteInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private String checkTimestamp(Object timestamp) {

		String datestr = timestamp + "";
		// "Mon Aug 15 11:24:39 CST 2016";//Date的默认格式显示
		Date date;
		try {
			date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK).parse(datestr);
			// 格式化
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sDate = sdf.format(date);
			return sDate;
		} catch (ParseException e1) {
			return null;
		}

	}



	public Result<List<Map<String, Object>>> getHiddenTroubleType() {
		try {
			logger.info("[进入getHiddenTroubleType()]");
			RpcResponse<List<Map<String, Object>>> hiddenTroubleType = alarmOrderQueryService.getHiddenTroubleType();
			if (hiddenTroubleType.isSuccess()) {
				logger.info("[getHiddenTroubleType()->success]");
				return ResultBuilder.successResult(hiddenTroubleType.getSuccessValue(), "查询成功");
			} else {
				logger.error(String.format("[getHiddenTroubleType()->error:%s]", hiddenTroubleType.getMessage()));
				return ResultBuilder.failResult(hiddenTroubleType.getMessage());
			}

		} catch (Exception e) {
			logger.error("[getHiddenTroubleType()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
