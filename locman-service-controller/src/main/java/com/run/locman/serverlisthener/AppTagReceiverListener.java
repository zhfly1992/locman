/*
 * File name: AppTagReceiverListener.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年3月22日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.serverlisthener;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.activemq.service.ActiveMqReceiveService;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.constants.ActiveMqConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FactoryConstants;

/**
 * @Description: 服务监听器
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月22日
 */
//@Component
public class AppTagReceiverListener implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private FactoryQueryService		factoryQueryService;

	private Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private ActiveMqReceiveService	activeMqReceiveService;



	/**
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		openClient();
	}



	private void openClient() {
		try {
			logger.info(String.format("[openClient()->success:%s]", "开始获取所有appTag！"));

			// 获取所有appTag
			RpcResponse<List<Map<String, Object>>> queryAllappTag = factoryQueryService.queryAllappTag();

			if (!queryAllappTag.isSuccess()) {
				logger.error(String.format("[openClient()->error:%s]", "查询appTag失败 ！"));
				return;
			}

			List<Map<String, Object>> appTagList = queryAllappTag.getSuccessValue();

			// openRabbitMqClient(appTagList);
			openActiveMqClient(appTagList);
		} catch (Exception e) {
			logger.error(String.format("[openClient()->error:%s]", e));
		}

	}


	/**
	 * 
	 * @Description:开启activemq客户端
	 * @param appTagList
	 */
	private void openActiveMqClient(List<Map<String, Object>> appTagList) throws Exception {
		List<Map<String, Object>> activemqList = Lists.newArrayList();
		for (Map<String, Object> map : appTagList) {
			// 判断是否为activemq的queue
			String apptagQuequName = (String) map.get(FactoryConstants.FACTORY_APPTAG);
			String factoryId = (String) map.get(FactoryConstants.FACTORYID);

			if (StringUtils.isBlank(apptagQuequName) || StringUtils.isBlank(factoryId)) {
				logger.error(
						String.format("[openActiveMqClient()->error:%s-->%s]", "appTag或者factoryId为null！启动失败!", map));
				// 跳出当前循环
				continue;
			} else {

				// 校验判断activemq的queue
				String[] split = apptagQuequName.split("\\.");
				if (split.length < 3 || StringUtils.isBlank(split[0]) || StringUtils.isBlank(split[1])
						|| !ActiveMqConstants.ACTIVEMQ_CHANGE.equals(split[2])) {
					continue;
				}
				activemqList.add(map);
			}
		}

		if (!activemqList.isEmpty()) {
			logger.info("[openActiveMqClient()->info:开始启动activemq客户端！]");
			activeMqReceiveService.startLoopConsumerAMQPS(activemqList);
		}

	}

}
