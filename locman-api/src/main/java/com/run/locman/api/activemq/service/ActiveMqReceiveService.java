/*
 * File name: ActiveMqReceiveService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年10月19日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.activemq.service;

import java.util.List;
import java.util.Map;

/**
 * @Description:activemq消费端服务
 * @author: zhaoweizhi
 * @version: 1.0, 2018年10月19日
 */

public interface ActiveMqReceiveService {

	/**
	 * 
	 * @Description:批量启动queue接受数据
	 * @param queueNameslist中数据为xxx.xxx.dataChange(已经拼接好的queue)
	 * @param factoryId
	 */
	public void startAllConsumerAMQPS(List<String> queueNames, String factoryId);



	/**
	 * 
	 * @Description:启动单个queue接受数据
	 * @param queueName
	 *            xxx.xxx.dataChange(已经拼接好的queue)
	 * 
	 */
	public void startConsumerAMQP(String queueName, String factoryId);



	/**
	 * 
	 * @Description:批量启动queue接收数据
	 * @param appTagList
	 *            list中的数据为 map { appTag : xxx.xxx.dataChange,factoryId:xxxxxx }
	 */
	public void startLoopConsumerAMQPS(List<Map<String, Object>> appTagList);



	/**
	 * 
	 * @Description:批量启动queue接收数据
	 * @param factoryId
	 *            厂家id
	 * @param appQueue
	 *            list中的数据为 map { appId:xxx,appKey:xxx }
	 */
	public void startFactoryAMQPS(String factoryId, List<Map<String, String>> appQueue);

}
