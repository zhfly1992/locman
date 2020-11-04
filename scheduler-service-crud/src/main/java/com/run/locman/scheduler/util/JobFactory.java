/*
 * File name: JobFactory.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年7月4日 ... ... ...
 *
 ***************************************************/

package com.run.locman.scheduler.util;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;

/**
 * @Description:工具(使定时器对行在spring中托管)
 * @author: 王胜
 * @version: 1.0, 2018年7月4日
 */

public class JobFactory extends AdaptableJobFactory {
	/** 这个对象Spring会帮我们自动注入进来*/	
	@Autowired
	private AutowireCapableBeanFactory capableBeanFactory;


	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		// 调用父类的方法
		Object jobInstance = super.createJobInstance(bundle);
		// 进行注入
		capableBeanFactory.autowireBean(jobInstance);
		return jobInstance;
	}
}
