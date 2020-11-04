/*
 * File name: ConnectionFactoryConfigure.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年5月2日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.rabbitmq.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: SpringBoot与RabbitMq配置类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年5月2日
 */
@Configuration
public class ConnectionFactoryConfigure {

	/** 用户名 */
	@Value("${rabbitmq.username}")
	private String	username;

	/** 密码 */
	@Value("${rabbitmq.password}")
	private String	password;

	/** rabbitMq地址 -> haproxy */
	@Value("${rabbitmq.host}")
	private String	host;

	/** rabbitMq端口号 */
	@Value("${rabbitmq.port}")
	private String	port;



	/**
	 * 
	 * @Description:工厂实现对象
	 * @return
	 * 
	 */
	@Bean
	public CachingConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses(host);
		connectionFactory.setPort(Integer.valueOf(port));
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		// 必须要设置 确保消息不会丢失
		connectionFactory.setPublisherConfirms(true);
		return connectionFactory;
	}


}
