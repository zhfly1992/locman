/*
 * File name: SimpleOrderService.java
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

package com.run.locman.crud.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.SimpleOrderCrudService;
import com.run.locman.api.model.SimpleOrderProcessModel;
import com.run.locman.api.timer.crud.service.ExecuteMethodTimerService;
import com.run.locman.api.util.GetInternateTimeUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.SimpleOrderConstants;

/**
 * @Description: 一般流程工单增改
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */
@Service
public class SimpleOrderService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	public SimpleOrderCrudService	simpleOrderCrudService;
	@Autowired
	private ExecuteMethodTimerService		executeMethodTimerService;

	@Value("${api.host}")
	private String					ip;

	@Autowired
	private HttpServletRequest		request;
	
	private static long oneHour = 3600000L;
	private static long oneMinute = 60000L;



	public Result<String> simpleOrderAdd(String order) {
		logger.info(String.format("[simpleOrderAdd()->request params:%s]", order));
		try {
			// 参数校验
			Result<String> result = ExceptionChecked.checkRequestParam(order);
			if (result != null) {
				logger.error("数据不满足json格式!");
				return ResultBuilder.invalidResult();
			}
			JSONObject orderInfo = JSONObject.parseObject(order);
			String remindTime = orderInfo.getString(SimpleOrderConstants.REMIND_TIME);
			String processStartTime = orderInfo.getString(SimpleOrderConstants.PROCESSSTARTTIME);
			String processEndTime = orderInfo.getString(SimpleOrderConstants.PROCESSENDTIME);
			RpcResponse<String> checkTime = executeMethodTimerService.checkAndGetTime(processStartTime, processEndTime, remindTime);
			if (!checkTime.isSuccess()) {
				logger.error(String.format("时间校验:%s", checkTime.getMessage()));
				return ResultBuilder.failResult(checkTime.getMessage());
			}
			//多少秒之后提醒
			String msString = checkTime.getSuccessValue();
			int remindByNum = Integer.parseInt(msString);
			//转换为分钟
			int remindTimeLong = remindByNum / 60;
			String realRemindTime = String.valueOf(remindTimeLong);
			orderInfo.put(SimpleOrderConstants.REMIND_TIME, realRemindTime);
			// 获取当前组织id
			String orgId = orderInfo.getString(SimpleOrderConstants.ORGANIZEID);

			// 校验参数
			if (StringUtils.isBlank(orgId)) {
				ResultBuilder.failResult("组织id不能为null！");
			}

			// 获取父类资源id
			String parentId = InterGatewayUtil.getHttpValueByGet("/interGateway/v3/organization/parentIds/" + orgId, ip,
					request.getHeader(InterGatewayConstants.TOKEN));
			orderInfo.put(SimpleOrderConstants.ORGANIZEID, parentId);

			RpcResponse<String> resultInfo = simpleOrderCrudService.simpleOrderAdd(orderInfo);

			if (resultInfo.isSuccess()) {
				logger.info("[simpleOrderAdd()->success:新增作业工单成功]");
				return ResultBuilder.successResult(resultInfo.getSuccessValue(), resultInfo.getMessage());
			}
			logger.error("[simpleOrderAdd()->fail:新增作业工单失败]");
			return ResultBuilder.failResult(resultInfo.getMessage());
		} catch (Exception e) {
			logger.error("simpleOrderAdd()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> updateSimpleOrderState(String orderInfo) {
		logger.info(String.format("[updateSimpleOrderState()->request params:%s]", orderInfo));
		try {
			// 参数校验
			Result<String> result = ExceptionChecked.checkRequestParam(orderInfo);
			if (result != null) {
				logger.error("数据不满足json格式!");
				return ResultBuilder.invalidResult();
			}
			JSONObject order = JSONObject.parseObject(orderInfo);
			RpcResponse<String> resultInfo = simpleOrderCrudService.updateSimpleOrderState(order);
			if (resultInfo.isSuccess()) {
				logger.info("[updateSimpleOrderState()->success:作业工单状态改变成功]");
				return ResultBuilder.successResult(resultInfo.getSuccessValue(), resultInfo.getMessage());
			}
			logger.error("[updateSimpleOrderState()->fail:作业工单状态改变失败]");
			return ResultBuilder.failResult(resultInfo.getMessage());
		} catch (Exception e) {
			logger.error("updateSimpleOrderState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:延时审批接口
	 * @param simpleOrderProcessModel
	 * @return
	 */
	public Result<String> delayedSimpleOrder(SimpleOrderProcessModel simpleOrderProcessModel) {

		try {
			if (simpleOrderProcessModel == null) {
				logger.error("[delayedSimpleOrder()->error:作业工单model为null！]");
				return ResultBuilder.noBusinessResult();
			}
			logger.info(String.format("[delayedSimpleOrder()->request params:%s]", simpleOrderProcessModel.toString()));

			RpcResponse<String> rpc = simpleOrderCrudService.delayedSimpleOrder(simpleOrderProcessModel);
			if (rpc.isSuccess()) {
				logger.info(String.format("[delayedSimpleOrder()->suc:%s]", rpc.getMessage()));
				return ResultBuilder.successResult(rpc.getSuccessValue(), rpc.getMessage());
			}

			logger.error(String.format("[delayedSimpleOrder()->error:%s]", rpc.getMessage()));
			return ResultBuilder.failResult(rpc.getMessage());
		} catch (Exception e) {
			logger.error("delayedSimpleOrder()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	public Result<String> invalidateSimpleOrder(SimpleOrderProcessModel simpleOrderProcessModel) {
		try {
			if (simpleOrderProcessModel == null) {
				logger.error("[invalidateSimpleOrder()->error:作业工单model为null]");
				return ResultBuilder.noBusinessResult();
			}
			logger.info(String.format("[invalidateSimpleOrder()->request params:%s]", simpleOrderProcessModel.toString()));
			RpcResponse<String> rpcResponse = simpleOrderCrudService.invalidateSimpleOrder(simpleOrderProcessModel);
			if (!rpcResponse.isSuccess()) {
				logger.error(String.format("invalidateSimpleOrder()->error:%s", rpcResponse.getMessage()));
				return ResultBuilder.failResult(rpcResponse.getMessage());
			}
			logger.info(String.format("invalidateSimpleOrder()->success:%s", rpcResponse.getMessage()));
			return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
		}catch (Exception e) {
			logger.error("invalidateSimpleOrder()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	public RpcResponse<String> checkAndGetTime(String startTime, String endTime, String remindTimeByMinute) {
		logger.info(String.format("checkAndGetTime()-->进入方法,参数:[startTime:%s][endTime:%s][remindTimeByMinute:%s]", startTime, endTime, remindTimeByMinute));
		try {
			RpcResponse<Long> checkAndGetStart2EndTime = checkAndGetStart2EndTime(startTime, endTime, remindTimeByMinute);
			if (!checkAndGetStart2EndTime.isSuccess()) {
				return RpcResponseBuilder.buildErrorRpcResp(checkAndGetStart2EndTime.getMessage());
			}
			Long timeSlot = checkAndGetStart2EndTime.getSuccessValue();
			//实际毫秒后提醒
			long realRemindTimeLong = 0L;
			//提醒时间参数为空,使用默认提示
			if (StringUtils.isBlank(remindTimeByMinute)) {
				//大于1小时的工单，提醒时间在离结束时间的1小时提醒
				//小于1小时的工单，提醒时间在离结束时间的50%的时候提醒
				if (timeSlot > oneHour) {
					realRemindTimeLong = oneHour;
				} else {
					realRemindTimeLong = timeSlot / 2;
					
				}
				
			} else {
				Integer time = Integer.parseInt(remindTimeByMinute);
				int tenMinute = 10;
				if (time < tenMinute) {
					logger.error("[checkAndGetTime()-->自定义工单结束提醒时间不能小于10分钟]");
					return RpcResponseBuilder.buildErrorRpcResp("自定义工单结束提醒时间不能小于10分钟");
				}
				long remindTimeLong = time * oneMinute;

				if (remindTimeLong >= timeSlot) {
					logger.error("[checkAndGetTime()->自定义提示时间不能超过工单的总时长!]");
					return RpcResponseBuilder.buildErrorRpcResp("自定义提示时间不能超过工单的总时长!");
				}
				realRemindTimeLong = remindTimeLong;

			}
			//转换为秒数
			long remindTimeSeconds = realRemindTimeLong / 1000;
			String realRemind = String.valueOf(remindTimeSeconds);
			return RpcResponseBuilder.buildSuccessRpcResp("时间校验无误", realRemind);
		} catch (Exception e) {
			 logger.error("checkAndExecuteMessagePush()->exception",e);
			 return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		
	}
	
	private RpcResponse<Long> checkAndGetStart2EndTime(String startTime, String endTime, String remindTimeByMinute){
		try {
			if (StringUtils.isBlank(startTime)) {
				logger.error("[checkAndGetStart2EndTime()-->开始日期不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("开始日期不能为空");
			}
			if (StringUtils.isBlank(endTime)) {
				logger.error("[checkAndGetStart2EndTime()-->结束日期不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("结束日期不能为空");
			}
			boolean validStart = DateUtils.isValidDate(startTime);
			if (!validStart) {
				logger.error("[checkAndGetStart2EndTime()->开始日期格式错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("开始日期格式错误!");
			}
			boolean validEnd = DateUtils.isValidDate(endTime);
			if (!validEnd) {
				logger.error("[checkAndGetStart2EndTime()->结束日期格式错误!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束日期格式错误!");
			}
			//网络时间毫秒数
			Long internateMillisTime = GetInternateTimeUtil.queryMillisTime();
			if (null == internateMillisTime) {
				logger.info("checkAndGetStart2EndTime()->网络时间获取失败,采用系统时间");
				internateMillisTime = System.currentTimeMillis();
			}
			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long startTimeLong = Long.parseLong(startTimeStamp);
			if (startTimeLong - internateMillisTime <= 0) {
				logger.error("[checkAndGetStart2EndTime()->开始时间不能是过往时间!]");
				return RpcResponseBuilder.buildErrorRpcResp("开始时间不能是过往时间!");
			}
			String endTimeStamp = DateUtils.dateToStamp(endTime);
			
			long endTimeLong = Long.parseLong(endTimeStamp);
			// 工期:毫秒
			long timeSlot = endTimeLong - startTimeLong;
			if (timeSlot <= 0) {
				logger.error("[checkAndGetStart2EndTime()->结束时间不能小于等于开始时间!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束时间不能小于等于开始时间!");
			}
			long twentyMinute = 20 * oneMinute;
			if (timeSlot <= twentyMinute) {
				logger.error("[checkAndGetStart2EndTime()->结束时间与开始时间不能低于20分钟!]");
				return RpcResponseBuilder.buildErrorRpcResp("结束时间与开始时间不能低于20分钟!");
			}
			//不为空也不是数字时
			if (StringUtils.isNotBlank(remindTimeByMinute) && !StringUtils.isNumeric(remindTimeByMinute)) {
				logger.error("[checkAndGetStart2EndTime()->参数错误,提示时间格式必须是数字!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数错误,提示时间格式必须是数字!");
			}
			return RpcResponseBuilder.buildSuccessRpcResp("时间格式检验通过", timeSlot);
		} catch (Exception e) {
			logger.error("checkAndGetStart2EndTime",e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
	

}
