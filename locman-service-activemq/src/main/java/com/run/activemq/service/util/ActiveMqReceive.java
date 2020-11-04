/*
 * File name: ActiveMqReceive.java
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

package com.run.activemq.service.util;

import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.crud.service.DeviceReportedCrudService;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.constants.ActiveMqConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FactoryConstants;

/**
 * @Description: activemq消费端
 * @author: zhaoweizhi
 * @version: 1.0, 2018年10月19日
 */
@Component
@Scope("prototype")
public class ActiveMqReceive {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	private static final Integer		LENGTH	= 3;

	@Autowired
	private ActiveMQConnectionFactory	cachingConnectionFactory;

	@Autowired
	private FactoryQueryService			factoryQueryService;

	@Autowired
	private DeviceReportedCrudService	deviceReportedCrudService;



	/**
	 * 
	 * @Description:通过queueName和factoryId启动activemq
	 * @param queueName
	 * @param factoryId
	 */
	public void work(String queueName, String factoryId) {
		if (StringUtils.isBlank(queueName) || StringUtils.isBlank(factoryId)) {
			logger.error(String.format("[work()->error: 启动失败！queueName=%s,factoryId=%s]", queueName, factoryId));
			return;
		}
		try {
			// 解析用户名密码
			Map<String, String> analysisQueueName = analysisQueueName(queueName);
			if (analysisQueueName == null) {
				logger.error("[work()->error:解析用户名密码错误！请检查queueName]");
				return;
			}

			// 关闭监视主题通知
			cachingConnectionFactory.setWatchTopicAdvisories(false);
			// 创建连接
			Connection connection = cachingConnectionFactory.createConnection(
					analysisQueueName.get(ActiveMqConstants.ACTIVEMQ_USERNAME),
					analysisQueueName.get(ActiveMqConstants.ACTIVEMQ_PASSWORD));
			connection.start();

			// 创建session
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			session.run();

			// 绑定队列
			Queue queue = session.createQueue(queueName);

			MessageConsumer consumer = session.createConsumer(queue);
			startConsumer(factoryId, consumer, session, connection, queueName);

			logger.info(String.format("[work()->success: %s-->%s]", "开启activemq客户端接受数据！",
					"[" + queueName + "] : [" + factoryId + "]"));
		} catch (Exception e) {
			logger.error(String.format("[work()->error:%s]", e));
			System.out.println(e);
		}

	}



	/**
	 * 
	 * @Description:接收数据
	 * @param factoryId
	 * @param consumer
	 * @param session
	 * @param connection
	 * @throws Exception
	 */
	private void startConsumer(String factoryId, MessageConsumer consumer, Session session, Connection connection,
			String queueName) throws Exception {
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				logger.info(String.format("[onMessage()->success:%s-->%s]", "队列开始接收新消息!",
						"[" + queueName + "] : [" + factoryId + "]"));

				try {
					// 通过队列名（appTag）查询到厂家，没有close，有，查询厂家状态，停用 close；
					RpcResponse<List<Map<String, Object>>> factoryInfoRpc = factoryQueryService
							.queryFactoryInfoByAppTag(queueName);

					if (null == factoryInfoRpc) {
						logger.error("onMessage()-->查询失败");
					} else {
						if (!factoryInfoRpc.isSuccess()) {
							logger.error(String.format("[onMessage()->error:%s->%s]", "rpc接口返回失败！",
									factoryInfoRpc.getMessage()));
						} else {

							// 校验厂家
							if (!checkFactoryState(factoryInfoRpc.getSuccessValue(), factoryId)) {
								logger.error("[onMessage()->error:校验厂家数据失败!]");
								close(session, connection);
							} else {
								logger.info(String.format("[onMessage()->success:%s-->%s]", "正在执行消费！",
										"[" + queueName + "] : [" + factoryId + "]"));

								// 业务逻辑处理
								TextMessage textMessage = (TextMessage) message;
								RpcResponse<String> messageReceive = deviceReportedCrudService.messageReceiveThreadPool(textMessage.getText());
								if (!messageReceive.isSuccess()) {
									logger.error(String.format("[onMessage()->error:上报数据添加失败,%s]", messageReceive.getMessage()));
									
								} else {
									// 消息确认机制
									message.acknowledge();
									logger.info(String.format("[onMessage()->success:%s-->%s]", "消息消费成功！",
											"[" + queueName + "] : [" + factoryId + "]"));
								}
								

							}
						}
					}

				} catch (Exception e) {
					logger.error(String.format("[onMessage()->error:%s]", e));
				}

			}
		});

	}



	/**
	 * 
	 * @Description:关闭连接
	 * @param session
	 * @param connection
	 * @throws JMSException
	 */
	private void close(Session session, Connection connection) throws JMSException {
		session.close();
		connection.close();
	}



	/**
	 * 
	 * @Description:解析queueName获取userName和password
	 * @param queueName
	 * @return
	 */
	private Map<String, String> analysisQueueName(String queueName) throws Exception {

		String[] split = queueName.split("\\.");
		if (split.length < LENGTH || StringUtils.isBlank(split[0]) || StringUtils.isBlank(split[1])
				|| !ActiveMqConstants.ACTIVEMQ_CHANGE.equals(split[2])) {
			return null;
		}
		Map<String, String> queueMap = Maps.newHashMap();
		queueMap.put(ActiveMqConstants.ACTIVEMQ_USERNAME, split[0]);
		queueMap.put(ActiveMqConstants.ACTIVEMQ_PASSWORD, split[1]);

		return queueMap;
	}



	/**
	 * 
	 * @Description:校验厂家是否存在，如果存在再校验状态是否启用
	 * @return boolean
	 */
	private boolean checkFactoryState(List<Map<String, Object>> factoryInfos, String factoryId) {

		// 厂家是否存在
		if (factoryInfos == null || factoryInfos.size() == 0) {
			logger.error(String.format("[checkFactoryState()->error:%s]", "厂家信息查询为null！"));
			return false;
		}

		// 获取厂家信息状态
		Map<String, Object> factoryInfo = factoryInfos.get(0);
		String manageState = (String) factoryInfo.get("manageState");
		if (ActiveMqConstants.DISABLED.equals(manageState)) {
			logger.error(String.format("[checkFactoryState()->error:%s]", "厂家停用状态 ！"));
			return false;
		}

		// 创建这个链接时候的厂家id,要与从数据库查出来的id做匹配
		if (!factoryId.equals(factoryInfo.get(FactoryConstants.FACTORYID))) {
			logger.error(String.format("[checkFactoryState()->error:%s]", "启动连接时id与当前appTag绑定厂家id不同！及时关闭连接！"));
			return false;
		}

		logger.info(String.format("[checkFactoryState()->success:%s]", "校验厂家通过！"));
		return true;
	}
}
