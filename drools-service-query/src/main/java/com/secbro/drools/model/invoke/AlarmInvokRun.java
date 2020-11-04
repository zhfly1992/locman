
package com.secbro.drools.model.invoke;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.run.locman.api.crud.service.AlarmInfoCrudService;

/**
 * @Description:
 * @author: lkc
 * @version: 1.0, 2017年11月3日
 */
@Component
public class AlarmInvokRun {

	private static Logger				logger	= Logger.getLogger(AlarmInvokRun.class);

	private static AlarmInfoCrudService	alarmService;



	/**
	 * @param alarmService
	 *            the alarmService to set
	 */
	@Autowired
	public void setAlarmService(AlarmInfoCrudService alarmService) {
		logger.info(String.format("[setAlarmService()方法执行开始...,参数：【%s】]", alarmService));
		AlarmInvokRun.alarmService = alarmService;
		logger.info(String.format("[setAlarmService()方法执行结束!]"));
	}



	public static void locmanAlarmRun(Map<String, Object> obj, String rule) {
		logger.info(String.format("[locmanAlarmRun()方法执行开始...,参数：【%s】【%s】]", obj, rule));
		alarmService.add(obj);
		logger.info(String.format("[locmanAlarmRun()方法执行结束!]"));
	}

}
