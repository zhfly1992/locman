/*
 * File name: FactoryNewAMQPCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年10月23日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.google.common.collect.Lists;
import com.run.entity.common.Result;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.activemq.service.ActiveMqReceiveService;
import com.run.locman.constants.ActiveMqConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FactoryConstants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description:厂家新增activemq客户端service
 * @author: zhaoweizhi
 * @version: 1.0, 2018年10月23日
 */
@Service
public class FactoryActiveMqCrudService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private ActiveMqReceiveService	activeMqReceiveService;



	/**
	 * 
	 * @Description:新增厂家时调用activemq服务
	 * @param appQueue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Result<Boolean> addStartConsumerAMQPS(Map<String, Object> appQueue) {
		logger.info(String.format("[addStartConsumerAMQPS()->request params : %s]", appQueue));
		try {
			if (appQueue == null) {
				return ResultBuilder.emptyResult();
			}
			// 获取厂家id以及appList
			String factoryId = (String) appQueue.get(FactoryConstants.FACTORYID);
			List<Map<String, String>> apptagList = (List<Map<String, String>>) appQueue
					.get(FactoryConstants.FACTORY_APPTAG);
			if (StringUtils.isBlank(factoryId) || apptagList == null || apptagList.isEmpty()) {
				return ResultBuilder.emptyResult();
			}

			activeMqReceiveService.startFactoryAMQPS(factoryId, apptagList);

			logger.info(String.format("[addStartConsumerAMQPS()->suc : %s]", PublicConstants.PARAM_SUCCESS));
			return ResultBuilder.successResult(true, PublicConstants.PARAM_SUCCESS);

		} catch (Exception e) {
			logger.error("addStartConsumerAMQPS()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:修改厂家时调用activemq服务
	 * @param appQueue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Result<Boolean> updateStartConsumerAMQPS(Map<String, Object> appQueue) {
		logger.info(String.format("[updateStartConsumerAMQPS()->request params : %s]", appQueue));
		try {
			if (appQueue == null) {
				return ResultBuilder.emptyResult();
			}
			// 获取厂家id以及appList
			String factoryId = (String) appQueue.get(FactoryConstants.FACTORYID);
			List<Map<String, String>> apptagList = (List<Map<String, String>>) appQueue
					.get(FactoryConstants.FACTORY_APPTAG);
			List<Map<String, String>> oldApptagList = (List<Map<String, String>>) appQueue
					.get(FactoryConstants.FACTORY_OLD_APPTAG);
			if (StringUtils.isBlank(factoryId) || apptagList == null || apptagList.isEmpty() || oldApptagList == null
					|| oldApptagList.isEmpty()) {
				return ResultBuilder.emptyResult();
			}

			// 去重
			List<String> distinctQueue = distinctQueue(apptagList, oldApptagList);
			if (distinctQueue == null) {
				logger.error("[updateStartConsumerAMQPS()->error:去重校验错误！]");
				return ResultBuilder.noBusinessResult();
			}

			if (distinctQueue.size() == 0) {
				logger.info("[updateStartConsumerAMQPS()->info:无新增apptag无需启动客户端！]");
				return ResultBuilder.successResult(true, "无新增apptag无需启动客户端！");
			}

			activeMqReceiveService.startAllConsumerAMQPS(distinctQueue, factoryId);
			return ResultBuilder.successResult(true, PublicConstants.PARAM_SUCCESS);
		} catch (Exception e) {
			logger.error("updateStartConsumerAMQPS()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:获取新的queue
	 * @param apptagList
	 * @param oldApptagList
	 * @return
	 * @throws Exception
	 */
	private List<String> distinctQueue(List<Map<String, String>> apptagList, List<Map<String, String>> oldApptagList)
			throws Exception {

		List<String> queueList = Lists.newArrayList();
		List<String> oldQueueList = Lists.newArrayList();
		List<String> queueNames = Lists.newArrayList();
		// 封装queue
		for (Map<String, String> appMap : apptagList) {
			// 获取appId和appKey
			String queueName = analysisQueueName(appMap);
			if (StringUtils.isBlank(queueName)) {
				return null;
			}
			queueList.add(queueName);
		}

		// 封装queue
		for (Map<String, String> appMap : oldApptagList) {
			// 获取appId和appKey
			String queueName = analysisQueueName(appMap);
			if (StringUtils.isBlank(queueName)) {
				return null;
			}
			oldQueueList.add(queueName);
		}

		// 去重
		for (String appTag : queueList) {
			if (!oldQueueList.contains(appTag)) {
				queueNames.add(appTag);
			}
		}

		return queueNames;

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
