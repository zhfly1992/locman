/*
 * File name: ActiveMqReceiveServiceImpl.java
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

package com.run.activemq.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.run.activemq.service.util.ActiveMqReceive;
import com.run.locman.api.activemq.service.ActiveMqReceiveService;
import com.run.locman.constants.ActiveMqConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FactoryConstants;

/**
 * @Description: activeMq接收端实现
 * @author: zhaoweizhi
 * @version: 1.0, 2018年10月19日
 */

public class ActiveMqReceiveServiceImpl implements ActiveMqReceiveService {

	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private ActiveMqReceive		activeMqReceive;



	/**
	 * @see com.run.locman.api.activemq.service.ActiveMqReceiveService#startConsumerAMQPS(java.util.List)
	 */
	@Override
	public void startAllConsumerAMQPS(List<String> queueNames, String factoryId) {
		logger.info(String.format("[startAllConsumerAMQPS()->rpc params : queueNames = %s , factoryId = %s]",
				queueNames, factoryId));

		try {
			if (queueNames == null || queueNames.isEmpty() || StringUtils.isBlank(factoryId)) {
				logger.error(String.format("[startAllConsumerAMQPS()->error:queueNames = %s , factoryId = %s]",
						queueNames, factoryId));
				return;
			}

			for (String queueName : queueNames) {
				if (StringUtils.isBlank(queueName) || StringUtils.isBlank(factoryId)) {
					logger.error("[startAllConsumerAMQPS()->error:queueName或者factoryId为null！]");
					// 跳出当前循环
					continue;
				} else {
					activeMqReceive.work(queueName, factoryId);
				}
			}

			logger.info("[startAllConsumerAMQPS()->info: 启动执行完毕！]");
		} catch (Exception e) {
			logger.error(String.format("[startAllConsumerAMQPS()->error:%s]", e));
		}

	}



	/**
	 * @see com.run.locman.api.activemq.service.ActiveMqReceiveService#startConsumerAMQP(java.lang.String)
	 */
	@Override
	public void startConsumerAMQP(String queueName, String factoryId) {
		logger.info(String.format("[startConsumerAMQP()->rpc params : %s]", queueName));
		try {
			if (StringUtils.isBlank(queueName) || StringUtils.isBlank(factoryId)) {
				logger.error(String.format("[startConsumerAMQP()->error:queueName = %s , factoryId = %s]", queueName,
						factoryId));
				return;
			}

			activeMqReceive.work(queueName, factoryId);
			logger.info("[startConsumerAMQPS()->info: 启动执行完毕！]");
		} catch (Exception e) {
			logger.error(String.format("[startConsumerAMQP()->error:%s]", e));
		}

	}



	/**
	 * @see com.run.locman.api.activemq.service.ActiveMqReceiveService#startConsumerAMQPS(java.util.List)
	 */
	@Override
	public void startLoopConsumerAMQPS(List<Map<String, Object>> appTagList) {
		logger.info(String.format("[startLoopConsumerAMQPS()->rpc params : %s]", appTagList));
		try {

			if (appTagList == null || appTagList.isEmpty()) {
				logger.error("[startLoopConsumerAMQPS()->error:apptagList数据不存在！]");
				return;
			}

			for (Map<String, Object> map : appTagList) {

				String queueName = (String) map.get(FactoryConstants.FACTORY_APPTAG);
				String factoryId = (String) map.get(FactoryConstants.FACTORYID);

				if (StringUtils.isBlank(queueName) || StringUtils.isBlank(factoryId)) {
					logger.error(String.format("[startLoopConsumerAMQPS()->error:%s-->%s]",
							"appTag或者factoryId为null！启动失败!", map));
					// 跳出当前循环
					continue;
				} else {
					activeMqReceive.work(queueName, factoryId);
				}

			}

			logger.info("[startLoopConsumerAMQPS()->info: 启动执行完毕！]");
		} catch (Exception e) {
			logger.error(String.format("[startLoopConsumerAMQPS()->error:%s]", e));
		}
	}



	/**
	 * @see com.run.locman.api.activemq.service.ActiveMqReceiveService#startFactoryAMQPS(java.lang.String,
	 *      java.util.List)
	 */
	@Override
	public void startFactoryAMQPS(String factoryId, List<Map<String, String>> appQueue) {
		logger.info(String.format("[startConsumerAMQPS()->rpc params : factoryId = %s , appQueue = %s]", factoryId,
				appQueue));
		try {
			if (StringUtils.isBlank(factoryId) || appQueue == null || appQueue.isEmpty()) {
				logger.error("[startFactoryAMQPS()->error:factoryId或者appQueue数据不存在！]");
				return;
			}

			for (Map<String, String> appMap : appQueue) {
				// 获取appId和appKey
				String queueName = analysisQueueName(appMap);
				if (StringUtils.isBlank(queueName)) {
					continue;
				}
				activeMqReceive.work(queueName, factoryId);
			}

			logger.info("[startFactoryAMQPS()->info: 启动执行完毕！]");
		} catch (Exception e) {
			logger.error(String.format("[startFactoryAMQPS()->error:%s]", e));
		}
	}



	/**
	 * 
	 * @Description:拼接queueName
	 * @param appMap
	 * @return
	 */
	private String analysisQueueName(Map<String, String> appMap) {
		String appId = appMap.get(ActiveMqConstants.ACTIVEMQ_APP_ID);
		String appKey = appMap.get(ActiveMqConstants.ACTIVEMQ_APP_KEY);
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(appKey)) {
			logger.error(String.format("[analysisQueueName()->error:appId = %s , appKey = %s]", appId, appKey));
			return null;
		}
		return appId + "." + appKey + "." + ActiveMqConstants.ACTIVEMQ_CHANGE;
	}

}
