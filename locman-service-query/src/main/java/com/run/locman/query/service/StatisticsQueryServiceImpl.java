/*
 * File name: RemoteControlRecordQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lck 2017年9月14日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.AlarmOrderQueryRepository;
import com.run.locman.api.query.repository.FaultOrderProcessQueryRepository;
import com.run.locman.api.query.repository.SimpleOrderQueryRepository;
import com.run.locman.api.query.repository.StatisticsRepository;
import com.run.locman.api.query.service.StatisticsQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderConstants;
import com.run.locman.constants.StatisticsConstants;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 巡检query实现类
 * @author: lkc
 * @version: 1.0, 2017年9月14日
 */
public class StatisticsQueryServiceImpl implements StatisticsQueryService {
	/**
	 * 
	 */
	private static final String INSPECT = "inspect";

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private StatisticsRepository				statisticsRepository;
	@Autowired
	SimpleOrderQueryRepository					simpleOrderRepository;
	@Autowired
	private AlarmOrderQueryRepository			alarmOrderQueryRepository;
	@Autowired
	private FaultOrderProcessQueryRepository	faultOrderProcessQueryRepository;



	@Override
	public RpcResponse<Map<String, Integer>> getStatisticInfo(JSONObject parm) {
		logger.info(String.format("[getStatisticInfo()方法执行开始...,参数：【%s】]", parm));
		try {
			if (parm == null || parm.isEmpty()) {
				logger.error("[getStatisticInfo()->error:参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!parm.containsKey(StatisticsConstants.USERID)
					|| StringUtils.isBlank(parm.getString(StatisticsConstants.USERID))) {
				logger.error("[getStatisticInfo()->error:用户id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("用户id不能为空！");
			}
			if (!parm.containsKey(StatisticsConstants.USC_ACCESS_SECRET)
					|| StringUtils.isBlank(parm.getString(StatisticsConstants.USC_ACCESS_SECRET))) {
				logger.error("[getStatisticInfo()->error:接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			Map<String, Integer> statisticsOrder = statisticsRepository.getStatisticsOrder(
					parm.getString(StatisticsConstants.USERID), parm.getString(StatisticsConstants.USC_ACCESS_SECRET));
			logger.info(String.format("[getStatisticInfo()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, statisticsOrder);
		} catch (Exception e) {
			logger.error("getStatisticInfo()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Integer> getStatisticTodoInfo(List<String> ids, String type) {
		logger.info(String.format("[getStatisticTodoInfo()方法执行开始...,参数：【%s】【%s】]", ids, type));
		try {
			if (StringUtils.isBlank(type)) {
				return RpcResponseBuilder.buildErrorRpcResp("查询类型不能为空!");
			}
			if (ids == null || ids.isEmpty()) {
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, new Integer(0));
			}
			if (OrderConstants.SIMPLE.equals(type)) {
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS,
						simpleOrderRepository.getSimpleCount(ids));
			}

			if (OrderConstants.FAULT.equals(type)) {
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS,
						faultOrderProcessQueryRepository.getfaultOrderCount(ids));
			}

			if (OrderConstants.ALARM.equals(type)) {
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS,
						alarmOrderQueryRepository.getalarmOrderCount(ids));
			}
			if (INSPECT.equals(type)) {
				logger.info(String.format("[getStatisticTodoInfo()方法执行结束!]"));
				// 巡检工单未做，后期更正s
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, new Integer(0));
			}
			logger.info(String.format("[getStatisticTodoInfo()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, new Integer(0));
		} catch (Exception e) {
			logger.error("getStatisticTodoInfo()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}





}
