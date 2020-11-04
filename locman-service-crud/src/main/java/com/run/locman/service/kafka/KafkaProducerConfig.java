/*
* File name: KafkaProducerConfig.java
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
* 1.0			qulong		2017年10月20日
* ...			...			...
*
***************************************************/

package com.run.locman.service.kafka;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.google.common.collect.Maps;

/**
 * @Description: kafaka连接配置
 * @author: qulong
 * @version: 1.0, 2017年12月06日
 */

public class KafkaProducerConfig {

	@Value("${kafka.producer.bootstrap-servers}")
	private String ip;



	@Bean
	public ProducerFactory<String, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}



	@Bean
	public Map<String, Object> producerConfigs() {
		/*
		 * String keystorePath = null; String truststorePath = null; try {
		 * keystorePath =
		 * ResourceUtils.getFile("classpath:ssl/server.keystore.jks").getPath();
		 * truststorePath =
		 * ResourceUtils.getFile("classpath:ssl/client.truststore.jks").getPath(
		 * ); } catch (FileNotFoundException e) { e.printStackTrace(); }
		 */ // kafka 安全认证相关
		Map<String, Object> props = Maps.newHashMap();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ip);
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}



	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		return new KafkaTemplate<String, String>(producerFactory());
	}

}
