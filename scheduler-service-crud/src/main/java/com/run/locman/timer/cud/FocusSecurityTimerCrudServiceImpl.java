/*
* File name: FocusSecurityTimerCrudServiceImpl.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			钟滨远		2020年4月27日
* ...			...			...
*
***************************************************/

package com.run.locman.timer.cud;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.run.activity.api.crud.service.ProcessFileCurdService;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.ProcessInfoListDto;
import com.run.locman.api.entity.ProcessInfo;
import com.run.locman.api.entity.ScheduledTasks;
import com.run.locman.api.timer.crud.repository.ScheduledTasksCudRepository;
import com.run.locman.api.timer.crud.service.FocusSecuritysTimerCrudService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderProcessContants;
import com.run.locman.scheduler.util.QuartzManager;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年4月27日
*/

public class FocusSecurityTimerCrudServiceImpl implements FocusSecuritysTimerCrudService{
	
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private ScheduledTasksCudRepository scheduledTasksCudRepository;

	/**
	 * @see com.run.locman.api.timer.crud.service.FocusSecuritysTimerCrudService#focusSecurity()
	 */
	@Override
	public RpcResponse<String> focusSecurityIssued(Map<String ,Object> map) {
		
		if (null == map) {
			logger.error("[focusSecurityIssued()-->map不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("map不能为null");
		}
		try {
			if(!map.containsKey("performTime")) {
				String startTimeStamp=map.get("startTimeStamp")+"";
				String performTime=startTimeStamp;
				if(map.containsKey("timerTimeStampLong")) {
					String timerTimeStampLong=map.get("timerTimeStampLong")+"";
					performTime=timerTimeStampLong;
				}
				map.put("performTime", performTime);
			}
			boolean managerJobTrigger=QuartzManager.securityFacilitiesOrders(map);
			
			if(managerJobTrigger) {
				String querystatusByTrrigerName = scheduledTasksCudRepository.querystatusByTrrigerName(map);
				if(null ==querystatusByTrrigerName||querystatusByTrrigerName.length() == 0) {
					
					//持久化
					ScheduledTasks scheduledTasks = new ScheduledTasks();
					scheduledTasks.setId(UtilTool.getUuId());
					scheduledTasks.setStatus(ScheduledTasks.ST_START);
					scheduledTasks.setPerformTime(map.get("performTime")+"");
					scheduledTasks.setTrrigerName(map.get("focusSecurityId")+"");
					scheduledTasks.setTrrigerGroup(map.get("focusSecurityId")+"");
					scheduledTasks.setJobDescribe("focusSecurity");
					String facInfo=map.get("facInfo")+"";
					scheduledTasks.setDataMap(facInfo);
					//放入任务表
					scheduledTasksCudRepository.insertScheduledTasks(scheduledTasks);
				}
				
				
				logger.info("[focusSecurityIssued()->定时器开启成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("定时器开启成功", "定时器开启成功");
			} else {
				logger.error("[focusSecurityIssued()->定时器开启失败]");
				return RpcResponseBuilder.buildErrorRpcResp("定时器开启失败");
			}
		}catch(Exception e) {
			logger.error("focusSecurityIssued()->exception",e);
			 return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
	
	
	

	

	/**
	 * @see com.run.locman.api.timer.crud.service.FocusSecuritysTimerCrudService#closeFocusSecurityIssued(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> closeFocusSecurityIssued(String securityId) {
		if (StringUtils.isBlank(securityId)) {
			logger.error("[closeFocusSecurityIssued()-->保障id不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("保障id不能为空");
		}
		try {
			boolean disableSchedule = QuartzManager.deleteJob(securityId, securityId);
			List<String> security=new ArrayList<String>();
			security.add(securityId);
			int status=ScheduledTasks.ST_DELETE;
			scheduledTasksCudRepository.updateScheduledTasks(status, security);
			return RpcResponseBuilder.buildSuccessRpcResp("定时器关闭操作执行完毕", disableSchedule);
		} catch (Exception e) {
			logger.error("closeFocusSecurityIssued()-exception",e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
