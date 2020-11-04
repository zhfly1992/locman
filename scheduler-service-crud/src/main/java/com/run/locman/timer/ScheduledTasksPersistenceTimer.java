/*
* File name: ScheduledTasksPersistenceTimer.java								
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
* 1.0			钟滨远		2020年6月15日
* ...			...			...
*
***************************************************/

package com.run.locman.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.timer.crud.repository.ScheduledTasksCudRepository;
import com.run.locman.api.timer.crud.service.FocusSecuritysTimerCrudService;
import com.run.locman.constants.CommonConstants;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年6月15日
*/
@Component
@EnableScheduling
public class ScheduledTasksPersistenceTimer {
	
private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private FocusSecuritysTimerCrudService focusSecuritysTimerCrudService;
	
	@Autowired
	private ScheduledTasksCudRepository scheduledTasksCudRepository; 

	
	@Scheduled(fixedDelay = 60 * 1000 * 60 * 24 * 365)
	public void RebootScheduledTasks() {
		try {
			logger.info("开始执行RebootScheduledTasks方法");
			
			List<Map<String, Object>> findScheduledTasksInfo = scheduledTasksCudRepository.findScheduledTasksInfo();
			if(null !=findScheduledTasksInfo && findScheduledTasksInfo.size()>0) {
				logger.info(String.format("存在需要重新启动的定时任务：%s个", findScheduledTasksInfo.size()));
				//重保Id List
				List<String> securityId=new ArrayList<String>();
				
				for(Map<String ,Object> map:findScheduledTasksInfo) {
					
					String id=map.get("id")+"";
					String trrigerName =map.get("trrigerName")+"";
					securityId.add(trrigerName);
					
					HashMap<String, Object> hashMap = new HashMap<String,Object>();
					hashMap.put("focusSecurityId", trrigerName);
					hashMap.put("facInfo", map.get("dataMap")+"");
					hashMap.put("performTime", map.get("performTime")+"");
					
					logger.info(String.format("重新开启任务ID为%s的定时任务", id));
					//开启新的定时任务
					RpcResponse<String> focusSecurityIssued = focusSecuritysTimerCrudService.focusSecurityIssued(hashMap);
					if(focusSecurityIssued.isSuccess()) {
						logger.info("重启成功！");
					}
				}
				//将任务表选中的任务的状态转为4，特殊状态
				Integer updateScheduledTasks = scheduledTasksCudRepository.updateScheduledTasks(4, securityId);
				if(updateScheduledTasks >= 0) {
					logger.info("修改成功！");
				}
			}else {
				logger.info("不存在需要重新启动的定时任务");
			}
		}catch(Exception e) {
			logger.error(String.format("[RebootScheduledTasks()->error:%s]", e));
		}
	}
}
