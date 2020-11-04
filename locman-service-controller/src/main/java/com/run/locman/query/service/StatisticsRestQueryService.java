/*
 * File name: StatisticsRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年10月31日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.activity.api.query.ActivityProgressQuery;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.StatisticsQueryService;
import com.run.locman.constants.AlarmOrderConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.SimpleOrderConstants;
import com.run.locman.constants.StatisticsConstants;

/**
 * @Description: 工单统计
 * @author: lkc
 * @version: 1.0, 2017年10月31日
 */
@Service
public class StatisticsRestQueryService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private StatisticsQueryService	statisticsQueryService;
	@Autowired
	private ActivityProgressQuery	activityProgressQuery;



	public Result<Map<String, Map<String, Integer>>> getStatisticInfo(String accessSecret, String userId) {
		logger.info(
				String.format("[getStatisticInfo()->request params--accessSecret:%s,userId:%s]", accessSecret, userId));
		try {
			if (userId == null || StringUtils.isBlank(userId) || accessSecret == null
					|| StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[getStatisticInfo()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = new JSONObject();
			parseObject.put(StatisticsConstants.USERID, userId);
			parseObject.put(StatisticsConstants.USC_ACCESS_SECRET, accessSecret);
			Map<String, Map<String, Integer>> data = Maps.newHashMap();
			// 处理我的流程数据
			RpcResponse<Map<String, Integer>> res = statisticsQueryService.getStatisticInfo(parseObject);

			if (res == null || !res.isSuccess() || res.getSuccessValue() == null) {
				logger.error("[getStatisticInfo()->fail:数据异常，稍后重试！]");
				return ResultBuilder.failResult("数据异常，稍后重试！");
			}
			data.put("myProcess", res.getSuccessValue());

			// 处理待办流程数据
			data.put("myProcessTodo", getStatisticValueMap(userId));

			// 处理流程列表数据
			data.put("myProcessAll", getStatisticValueAllPro(userId));

			// 处理统计数据
			data.put("total", getStatisticTotal(data));

			if (res.isSuccess()) {
				logger.info(String.format("[getStatisticInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(data, MessageConstant.SEARCH_SUCCESS);
			} else {
				logger.error(String.format("[getStatisticInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(MessageConstant.SEARCH_FAIL);
			}
		} catch (Exception e) {
			logger.error("getStatisticInfo()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:记录总条数
	 * @param data
	 * 
	 */
	private Map<String, Integer> getStatisticTotal(Map<String, Map<String, Integer>> data) {
		Map<String, Integer> map = Maps.newHashMap();
		map.put("simOrd", new Integer(0));
		map.put("alaOrd", new Integer(0));
		map.put("fauOrd", new Integer(0));
		for (String mapT : data.keySet()) {
			Map<String, Integer> value = data.get(mapT);
			for (String key : value.keySet()) {
				map.put(key, map.get(key) + new Integer(value.get(key) + ""));
			}
		}
		return map;
	}



	/**
	 * 
	 * @Description:待办流程封装
	 * @param userId
	 * @return
	 */
	private Map<String, Integer> getStatisticValueMap(String userId) {
		Map<String, Integer> map = Maps.newHashMap();
		map.put("simOrd", getStatisticValueTodo("generalProcess", userId));
		map.put("alaOrd", getStatisticValueTodo("alarmProcess", userId));
		map.put("fauOrd", getStatisticValueTodo("alarmFailureProcess", userId)
				+ getStatisticValueTodo("manualFailureProcess", userId));
		return map;
	}



	/**
	 * 
	 * @Description:待办流程
	 * @param type
	 * @param userId
	 * @return
	 */
	private Integer getStatisticValueTodo(String type, String userId) {
		logger.info(String.format("[getStatisticValueTodo()->request params--type:%s,userId:%s]", type, userId));
		JSONObject param = new JSONObject();
		param.put(StatisticsConstants.USERID, userId);
		param.put(StatisticsConstants.PROCCESS_TYPE, type);
		RpcResponse<List<Map<String, Object>>> allACTProgress = activityProgressQuery.getAllACTProgress(param);
		if (CommonConstants.GENERALPROCESS.equals(type)) {
			return getSimpleCount(allACTProgress);
		}
		if (CommonConstants.ALARMFAILUREPROCESS.equals(type) || CommonConstants.MANUALFAILUREPROCESS.equals(type)) {
			return getFaultOrderCount(allACTProgress);
		}
		if (CommonConstants.ALARMPROCESS.equals(type)) {
			return getAlarmOrderCount(allACTProgress);
		}

		return new Integer(0);
	}



	private Integer getAlarmOrderCount(RpcResponse<List<Map<String, Object>>> allACTProgress) {
		if (allACTProgress == null || !allACTProgress.isSuccess() || allACTProgress.getSuccessValue() == null) {
			logger.error("[getAlarmOrderCount()->error:传入参数错误]");
			return 0;
		} else {
			List<Map<String, Object>> value = allACTProgress.getSuccessValue();
			List<String> ids = new ArrayList<>();
			for (Map<String, Object> map : value) {
				String object = (String) map.get(AlarmOrderConstants.ORDER_ID);
				ids.add(object);
			}
			RpcResponse<Integer> statisticTodoInfo = statisticsQueryService.getStatisticTodoInfo(ids, "alarm");
			if (statisticTodoInfo == null || !statisticTodoInfo.isSuccess()) {
				logger.error("[getAlarmOrderCount()->fail:查询失败]");
				return new Integer(0);
			} else {
				logger.info(String.format("[getAlarmOrderCount()->success:%s]", statisticTodoInfo.getMessage()));
				return statisticTodoInfo.getSuccessValue();
			}
		}
	}



	/**
	 * 巡检工单统计处理
	 * 
	 * @param allACTProgress
	 * @return
	 */
	private Integer getFaultOrderCount(RpcResponse<List<Map<String, Object>>> allACTProgress) {
		if (allACTProgress == null || !allACTProgress.isSuccess() || allACTProgress.getSuccessValue() == null) {
			logger.error("[getFaultOrderCount()->error:传入参数错误]");
			return 0;
		} else {
			List<Map<String, Object>> value = allACTProgress.getSuccessValue();
			List<String> ids = new ArrayList<>();
			for (Map<String, Object> map : value) {
				String object = (String) map.get("id");
				ids.add(object);
			}
			RpcResponse<Integer> statisticTodoInfo = statisticsQueryService.getStatisticTodoInfo(ids, "fault");
			if (statisticTodoInfo == null || !statisticTodoInfo.isSuccess()) {
				logger.error("[getFaultOrderCount()->fail:查询失败]");
				return new Integer(0);
			} else {
				logger.info(String.format("[getFaultOrderCount()->success:%s]", statisticTodoInfo.getMessage()));
				return statisticTodoInfo.getSuccessValue();
			}

		}
	}



	/**
	 * 一般流程统计处理
	 * 
	 * @param allACTProgress
	 * @return
	 */
	private Integer getSimpleCount(RpcResponse<List<Map<String, Object>>> allACTProgress) {
		if (allACTProgress == null || !allACTProgress.isSuccess() || allACTProgress.getSuccessValue() == null) {
			logger.error("[getSimpleCount()->error:传入参数错误]");
			return 0;
		} else {
			List<Map<String, Object>> value = allACTProgress.getSuccessValue();
			List<String> ids = new ArrayList<>();
			for (Map<String, Object> map : value) {
				String object = (String) map.get(SimpleOrderConstants.ID);
				ids.add(object);
			}
			RpcResponse<Integer> statisticTodoInfo = statisticsQueryService.getStatisticTodoInfo(ids, "simple");
			if (statisticTodoInfo == null || !statisticTodoInfo.isSuccess()) {
				logger.error("[getSimpleCount()->fail:查询失败]");
				return new Integer(0);
			} else {
				logger.info(String.format("[getSimpleCount()->success:%s]", statisticTodoInfo.getMessage()));
				return statisticTodoInfo.getSuccessValue();
			}

		}
	}



	/**
	 * 
	 * @Description:处理过的流程
	 * @param userId
	 * @return
	 */
	private Map<String, Integer> getStatisticValueAllPro(String userId) {
		logger.info(String.format("[getStatisticValueAllPro()->request params--userId:%s]", userId));
		Map<String, Integer> map = Maps.newHashMap();
		map.put("simOrd",
				getStatisticValueAllPro("generalProcess", userId, StatisticsConstants.PROCCESS_NODE_REMOVEN_SIGN_SIM));
		map.put("alaOrd",
				getStatisticValueAllPro("alarmProcess", userId, StatisticsConstants.PROCCESS_NODE_REMOVEN_SIGN_ALA));
		map.put("fauOrd",
				getStatisticValueAllPro("alarmFailureProcess", userId,
						StatisticsConstants.PROCCESS_NODE_REMOVEN_SIGN_ALA_FAU)
						+ getStatisticValueAllPro("manualFailureProcess", userId,
								StatisticsConstants.PROCCESS_NODE_REMOVEN_SIGN_NEW_FAU));
		return map;
	}



	// 处理过的流程
	/**
	 * 
	 * @Description:查找处理过的流程
	 * @param type
	 * @param userId
	 * @param node
	 *            需要剔除的节点名
	 * @return
	 */
	private Integer getStatisticValueAllPro(String type, String userId, String node) {
		JSONObject param = new JSONObject();
		param.put(StatisticsConstants.USERID, userId);
		param.put(StatisticsConstants.PROCCESS_TYPE, type);
		param.put(AlarmOrderConstants.PROCESS_NODE_ID, node);
		RpcResponse<List<Map<String, Object>>> allHIProgress = activityProgressQuery.getAllHIProgress(param);
		if (CommonConstants.GENERALPROCESS.equals(type)) {
			return getSimpleCount(allHIProgress);
		}
		if (CommonConstants.ALARMFAILUREPROCESS.equals(type) || CommonConstants.MANUALFAILUREPROCESS.equals(type)) {
			return getFaultOrderCount(allHIProgress);
		}
		if (CommonConstants.ALARMPROCESS.equals(type)) {
			return getAlarmOrderCount(allHIProgress);
		}

		return new Integer(0);
	}



	/**
	 * 
	 * @Description:查询我的流程-各个流程统计总数
	 * @param accessSecret
	 * @param userId
	 * @return
	 */
	public Result<Map<String, Integer>> getMyProcessInfo(String accessSecret, String userId) {
		logger.info(
				String.format("[getMyProcessInfo()->request params--accessSecret:%s,userId:%s]", accessSecret, userId));
		try {
			if (userId == null || StringUtils.isBlank(userId) || accessSecret == null
					|| StringUtils.isBlank(accessSecret)) {
				logger.error("[getMyProcessInfo()->error:传入参数错误]");
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = new JSONObject();
			parseObject.put(StatisticsConstants.USERID, userId);
			parseObject.put(StatisticsConstants.USC_ACCESS_SECRET, accessSecret);

			// 处理我的流程数据
			RpcResponse<Map<String, Integer>> res = statisticsQueryService.getStatisticInfo(parseObject);

			if (res == null || !res.isSuccess() || res.getSuccessValue() == null) {
				logger.error("[getMyProcessInfo()->fail:查询失败]");
				return ResultBuilder.failResult("数据异常，稍后重试！");
			}
			logger.info(String.format("[getMyProcessInfo()->success:%s]", res.getMessage()));
			return ResultBuilder.successResult(res.getSuccessValue(), MessageConstant.SEARCH_SUCCESS);

		} catch (Exception e) {
			logger.error("getStatisticInfo()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:查询我的待办流程
	 * @param accessSecret
	 * @param userId
	 * @return
	 */
	public Result<Map<String, Integer>> getMyProcessTodoInfo(String accessSecret, String userId) {
		logger.info(String.format("[getMyProcessTodoInfo()->request params--accessSecret:%s,userId:%s]", accessSecret,
				userId));
		try {
			Map<String, Integer> statisticValueMap = getStatisticValueMap(userId);
			logger.info("[getMyProcessTodoInfo()->success:查询成功]");
			return ResultBuilder.successResult(statisticValueMap, MessageConstant.SEARCH_SUCCESS);

		} catch (Exception e) {
			logger.error("getStatisticInfo()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:查询我的已办流程
	 * @param accessSecret
	 * @param userId
	 * @return
	 */
	public Result<Map<String, Integer>> getMyProcessHTodoInfo(String accessSecret, String userId) {
		logger.info(String.format("[getMyProcessHTodoInfo()->request params--accessSecret:%s,userId:%s]", accessSecret,
				userId));
		try {
			Map<String, Integer> statisticValueAllPro = getStatisticValueAllPro(userId);
			logger.info("[getMyProcessHTodoInfo()->success:查询成功]");
			return ResultBuilder.successResult(statisticValueAllPro, MessageConstant.SEARCH_SUCCESS);

		} catch (Exception e) {
			logger.error("getStatisticInfo()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:传入我的，已办，代办数据，计算总条数
	 * @param data
	 * 
	 */
	public Result<Map<String, Integer>> getProcessTotal(Map<String, Map<String, Integer>> data) {
		try {
			Map<String, Integer> map = Maps.newHashMap();
			map.put("simOrd", new Integer(0));
			map.put("alaOrd", new Integer(0));
			map.put("fauOrd", new Integer(0));
			for (String mapT : data.keySet()) {
				Map<String, Integer> value = data.get(mapT);
				for (String key : value.keySet()) {
					map.put(key, map.get(key) + new Integer(value.get(key) + ""));
				}
			}
			return ResultBuilder.successResult(map, MessageConstant.SEARCH_SUCCESS);
		} catch (Exception e) {
			logger.error("getProcessTotal()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
