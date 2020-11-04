///*
//* File name: KafkaConsumerConfig.java								
//*
//* Purpose:
//*
//* Functions used and called:	
//* Name			Purpose
//* ...			...
//*
//* Additional Information:
//*
//* Development History:
//* Revision No.	Author		Date
//* 1.0			李康诚		2017年11月20日
//* ...			...			...
//*
//***************************************************/
//package com.run.locman.kafaka;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.config.KafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//
//import com.ctrip.framework.apollo.Config;
//import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
//import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
//
///**
// * @Description: Kafka
// * @author: 李康诚
// * @version: 1.0, 2017年11月20日
// */
//@Configuration
//@EnableKafka
//@EnableApolloConfig
//public class KafkaConsumerConfig {
//	@ApolloConfig("00001.rest.common")
//	private Config	config;
//
//	@Value("${kafka.producer.bootstrap-servers}")
//	private String	ip;	
//
//
//
//	/*@ApolloConfigChangeListener
//	private void someOnChange(ConfigChangeEvent changeEvent) {
//		if (changeEvent.isChanged("batch")) {
//			ip = config.getProperty("kafka.producer.bootstrap-servers",
//					"193.168.0.90:9092,193.168.0.91:9092,193.168.0.93:9092");
//		}
//	}*/
//
//
//
//	@Bean
//	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
//		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//		factory.setConsumerFactory(consumerFactory());
//		factory.setConcurrency(3);
//		factory.getContainerProperties().setPollTimeout(3000);
//		return factory;
//	}
//
//
//
//	public ConsumerFactory<String, String> consumerFactory() {
//		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
//	}
//
//
//
//	public Map<String, Object> consumerConfigs() {
//		Map<String, Object> propsMap = new HashMap<>();
//		propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ip);
//		propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//		propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
//		propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
//		propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "deviceRealState000");
//		propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//		return propsMap;
//	}
//
//
//
//	@Bean
//	public KafkaConsumerReceiver listener() {
//		return new KafkaConsumerReceiver();
//	}
//
//}