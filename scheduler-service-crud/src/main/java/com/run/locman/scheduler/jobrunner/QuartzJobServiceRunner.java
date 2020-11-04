/*
* File name: QuartzJobServiceRunner.java								
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
* 1.0			钟滨远		2020年6月5日
* ...			...			...
*
***************************************************/

package com.run.locman.scheduler.jobrunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.run.locman.api.timer.crud.repository.ScheduledTasksCudRepository;
import com.run.locman.api.timer.crud.service.FocusSecuritysTimerCrudService;
import com.run.locman.constants.CommonConstants;

/**
* @Description:	定时任务监听，dubbo没有实现Applicationlistenr,只能初始化Bean
* @author: 钟滨远
* @version: 1.0, 2020年6月5日
*/
//@Component
public class QuartzJobServiceRunner implements InitializingBean {
	
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private FocusSecuritysTimerCrudService focusSecuritysTimerCrudService;
	
	@Autowired
	private ScheduledTasksCudRepository scheduledTasksCudRepository; 

	/**
	 * @see  org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception{
		try {
			logger.info("开始执行QuartzJobServiceRunner方法");
			
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
					//开启新的定时任务（重启）
					focusSecuritysTimerCrudService.focusSecurityIssued(hashMap);
				}
				//将任务表选中的任务的状态转为4，特殊状态
				scheduledTasksCudRepository.updateScheduledTasks(4, securityId);
			}else {
				logger.info("不存在需要重新启动的定时任务");
			}
		}catch(Exception e) {
			logger.error(String.format("[afterPropertiesSet()->error:%s]", e));
		}
		
		
	}

}
