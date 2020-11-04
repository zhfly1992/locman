/*
 * File name: DeviceSchedulerServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年7月11日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.DeviceJobCrudService;
import com.run.locman.api.crud.service.DeviceSchedulerService;
import com.run.locman.api.crud.service.SchedulerCrudService;
import com.run.locman.api.query.service.DistributionPowersQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;

/**
 * @Description: 设备定时器
 * @author: 王胜
 * @version: 1.0, 2018年7月11日
 */

public class DeviceSchedulerServiceImpl implements DeviceSchedulerService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DistributionPowersQueryService	distributionPowersQueryService;

	@Autowired
	SchedulerCrudService					schedulerCrudService;

	@Autowired
	DeviceJobCrudService					deviceJobCrudService;



	/**
	 * @see com.run.locman.api.crud.service.DeviceSchedulerService#deviceSchedulerStart(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<String> deviceSchedulerStart(JSONObject schedulerParam) {
		try {
			RpcResponse<String> checkParam = checkParam(schedulerParam);
			if (null != checkParam) {
				return checkParam;
			}

			JSONObject jsonParam = new JSONObject();
			jsonParam.put("deviceId", schedulerParam.getString("deviceId"));
			jsonParam.put("userId", schedulerParam.getString("userId"));
			jsonParam.put("organizationId", schedulerParam.getString("organizationId"));
			jsonParam.put("accessSecret", schedulerParam.getString("accessSecret"));
			String key = schedulerParam.getString("key");
			String keyValue = schedulerParam.getString("keyValue");

			// 查询该设备所属的设施类型在分权分域所所配的超时未关的时间
			RpcResponse<Map<String, Object>> time = distributionPowersQueryService.getPowersTime(jsonParam);
			if (!time.isSuccess()) {
				logger.error("deviceSchedulerStart()--getPowersTimeByDeviceId()--fail:查询超时未关时间失败!");
				return RpcResponseBuilder.buildErrorRpcResp("查询超时未关时间失败!");
			}
			Map<String, Object> map = time.getSuccessValue();
			// 该设备在分权分域所属的设施类型下配了超时未关时间，且此时命令下发为开启，故启动该设备的超时未关定时器。
			if (map != null && !map.isEmpty() && !keyValue.equals(CommonConstants.CLOSE)) {

				JSONObject json = (JSONObject) JSONObject.toJSON(map);
				// 校验是否配置时间，未配置不用启动定时器。
				String hour = json.getString("hour");
				if (StringUtils.isBlank(hour) || CommonConstants.ZERO.equals(hour)) {
					logger.error("deviceSchedulerStart()--fail:该设备在分权分域未配置超时未关时间，不启动定时器!");
					return RpcResponseBuilder.buildSuccessRpcResp("该设备在分权分域未配置超时未关时间，不启动定时器!", null);
				}

				String jobId = UtilTool.getUuId();
				// 开启命令key
				json.put("item", key);
				json.put("jobId", jobId);

				// 启动定时器
				RpcResponse<Boolean> schedulerStart = schedulerCrudService.schedulerStart(json);
				if (!schedulerStart.isSuccess()) {
					logger.error("schedulerStart()--fail:启动定时器失败!" + schedulerStart.getMessage());
					return RpcResponseBuilder.buildErrorRpcResp("启动定时器失败!" + schedulerStart.getMessage());
				}

				// 保存设备与定时器关系
				json.put("openTime", DateUtils.formatDate(new Date()));
				RpcResponse<Integer> saveDeviceRsJob = deviceJobCrudService.saveDeviceRsJob(json);
				if (!saveDeviceRsJob.isSuccess() || null == saveDeviceRsJob.getSuccessValue()
						|| saveDeviceRsJob.getSuccessValue() <= 0) {
					logger.error("schedulerStart()--fail:启动定时器成功,但保存设备与定时器关系失败!" + saveDeviceRsJob.getMessage());
					return RpcResponseBuilder
							.buildErrorRpcResp("启动定时器成功,但保存设备与定时器关系失败!" + saveDeviceRsJob.getMessage());
				}
				logger.info("schedulerStart()--fail:启动定时器成功!" + schedulerStart.getMessage());
				return RpcResponseBuilder.buildSuccessRpcResp(schedulerStart.getMessage(), "操作成功!");
			}
			logger.info("schedulerStart()--fail:操作成功,该设备未配分权分域!");
			return RpcResponseBuilder.buildSuccessRpcResp("该设备未配分权分域!", "操作成功!");
		} catch (Exception e) {
			logger.error("control()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> checkParam(JSONObject schedulerParam) {
		if (null == schedulerParam) {
			logger.error("deviceSchedulerStart()--fail:参数不能为空!");
			return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
		}
		if (StringUtils.isBlank(schedulerParam.getString(DeviceContants.DEVICEID))) {
			logger.error("deviceSchedulerStart()--fail:设备deviceId不能为空!");
			return RpcResponseBuilder.buildErrorRpcResp("设备deviceId不能为空!");
		}
		if (StringUtils.isBlank(schedulerParam.getString(CommonConstants.KEY))) {
			logger.error("deviceSchedulerStart()--fail:下发命令的key不能空!");
			return RpcResponseBuilder.buildErrorRpcResp("下发命令的key不能空!");
		}
		if (StringUtils.isBlank(schedulerParam.getString(CommonConstants.KEY_VALUE))) {
			logger.error("deviceSchedulerStart()--fail:下发命令的keyValue不能空!");
			return RpcResponseBuilder.buildErrorRpcResp("下发命令的keyValue不能空!");
		}
		if (StringUtils.isBlank(schedulerParam.getString(CommonConstants.USERID))) {
			logger.error("deviceSchedulerStart()--fail:下发命令的userId不能空!");
			return RpcResponseBuilder.buildErrorRpcResp("下发命令的userId不能空!");
		}
		if (StringUtils.isBlank(schedulerParam.getString(CommonConstants.ACCESS_SECRET))) {
			logger.error("deviceSchedulerStart()--fail:下发命令的accessSecret不能空!");
			return RpcResponseBuilder.buildErrorRpcResp("下发命令的accessSecret不能空!");
		}
		logger.info(String.format("[deviceSchedulerStart()->进入方法,参数%s]", schedulerParam));
		return null;
	}

}
