/*
 * File name: SchedulerCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年7月2日 ... ... ...
 *
 ***************************************************/

package com.run.locman.scheduler.crud;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.SchedulerCrudService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.SchedulerContants;
import com.run.locman.scheduler.util.QuartzJob;
import com.run.locman.scheduler.util.QuartzManager;
import com.run.locman.scheduler.util.TimedJob;

/**
 * @Description:定时器
 * @author: 王胜
 * @version: 1.0, 2018年7月2日
 */

public class SchedulerCrudServiceImpl implements SchedulerCrudService {

	private static final Logger logger = Logger.getLogger(CommonConstants.LOGKEY);



	/**
	 * @see com.run.locman.api.crud.service.SchedulerCrudService#schedulerStart(java.util.Map)
	 */
	@Override
	public RpcResponse<Boolean> schedulerStart(JSONObject paramJson) {
        logger.info(String.format("[schedulerStart()->request params:%s]", paramJson));
		try {
			if (paramJson == null) {
				logger.error("[schedulerStart: invalid：参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
			}
			if (StringUtils.isBlank(paramJson.getString(SchedulerContants.SCHEDULER_DEVICEID))) {
				logger.error("[schedulerStart: invalid：设备deviceId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设备deviceId不能为空！");
			}
			if (StringUtils.isBlank(paramJson.getString(SchedulerContants.SCHEDULER_JOBID))) {
				logger.error("[schedulerStart: invalid：定时jobId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("定时jobId不能为空！");
			}
			if (StringUtils.isBlank(paramJson.getString(SchedulerContants.SCHEDULER_ITEM))) {
				logger.error("[schedulerStart: invalid：命令开启key(item)不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("命令开启key(item)不能为空！");
			}
			if (StringUtils.isBlank(paramJson.getString(SchedulerContants.SCHEDULER_HOUR))) {
				logger.error("[schedulerStart: invalid：小时hour不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("小时hour不能为空！");
			}
			if (StringUtils.isBlank(paramJson.getString(SchedulerContants.SCHEDULER__MINUTE))) {
				logger.error("[schedulerStart: invalid：分钟minute不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("分钟minute不能为空！");
			}

			int hour = Integer.parseInt(paramJson.getString("hour"));
			int minute = Integer.parseInt(paramJson.getString("minute"));

			// 创建该设备超时未关定时器任务
			TimedJob job = new TimedJob();
			job.setJobId(paramJson.getString("jobId"));
			job.setJobGroup(paramJson.getString("jobId") + "_group");
			// 每hour小时minute分钟后执行一次
			job.setCronExpression(UtilTool.getCorn(hour, minute));
			job.setStateFulljobExecuteClass(QuartzJob.class);

			// 放入查询数据，以便执行的时候取出来使用
			JobDataMap data = new JobDataMap();
			data.put("data", paramJson);

			// 启动定时任务
			QuartzManager.enableCronSchedule(job, data, true);

			logger.info("schedulerStart:启动定时器成功!  jobId[" + paramJson.getString("jobId") + "]");
			return RpcResponseBuilder.buildSuccessRpcResp(
					"schedulerStart:启动定时器成功!  jobId[" + paramJson.getString("jobId") + "]", Boolean.TRUE);

		} catch (Exception e) {
			logger.error("schedulerStart()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.SchedulerCrudService#schedulerDelete(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> schedulerDelete(String jobId) {
		logger.info(String.format("[schedulerDelete()->request params--jobId:%s]", jobId));
		try {
			if (StringUtils.isBlank(jobId)) {
				logger.error("[schedulerDelete: invalid：定时jobId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("定时jobId不能为空！");
			}
			QuartzManager.disableSchedule(jobId, jobId + "_group");
			logger.info("schedulerDelete:移除定时器成功!  jobId[" + jobId + "]");
			return RpcResponseBuilder.buildSuccessRpcResp("schedulerDelete:移除定时器成功!  jobId[" + jobId + "]",
					Boolean.TRUE);
		} catch (Exception e) {
			logger.error("schedulerStart()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
