/*
* File name: ExecuteMethodTimer.java								
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
* 1.0			guofeilong		2018年12月28日
* ...			...			...
*
***************************************************/

package com.run.locman.timer;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.locman.constants.CommonConstants;
import com.run.sms.api.JiguangService;

import entity.JiguangEntity;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年12月28日
*/

public class ExecuteMethodTimer {
	
	private static final Logger logger = Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
    	ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    	threadPoolTaskScheduler.setPoolSize(50);
        return threadPoolTaskScheduler;
    }
    
	@Autowired
	private JiguangService							jiguangService;
    
	
	public void executeMessagePush(JiguangEntity jiguangEntity, long startTimeout){
		//消息内容
		//推送到id
		//时间
		Runnable runnable = new Runnable(){

			@Override
			public void run() {
				RpcResponse<Object> jiguangPush = jiguangService.sendMessage(jiguangEntity);
				logger.info(DateUtils.formatDate(new Date()) + "***极光推送:" + System.currentTimeMillis());
				if (jiguangPush == null) {
					logger.error("AlarmInfoCrudServiceImpl.add()->error:极光推送失败,jiguangService.sendMessage()无返回信息");
				} else if (!jiguangPush.isSuccess()) {
					logger.error(jiguangPush.getMessage() + "AlarmInfoCrudServiceImpl.add()->error:极光推送失败");
				} else {
					logger.info(jiguangPush.getMessage() + "AlarmInfoCrudServiceImpl.add()->success:极光推送成功");
				}
			}

			
			
		};
		//threadPoolTaskScheduler.scheduleAtFixedRate(runnable, 1000);
		//threadPoolTaskScheduler.scheduleWithFixedDelay(runnable, 1000);
		threadPoolTaskScheduler.execute(runnable, startTimeout);
		logger.info("运行结束" + threadPoolTaskScheduler.getActiveCount() + "线程池:" + threadPoolTaskScheduler.getPoolSize());
	}

}
