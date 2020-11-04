package com.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.battcn.annotation.CacheKeyGenerator;
import com.battcn.annotation.LockKeyGenerator;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

/**
 * 
 * @Description: Controller Main方法
 * @author: 王胜
 * @version: 1.0, 2019年2月22日
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@Configuration
@EnableApolloConfig(value = { "00001.common" }, order = 1)
@ImportResource("classpath:config/spring-context.xml")
@EnableScheduling
public class Main {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}



	@Bean
	public CacheKeyGenerator cacheKeyGenerator() {
		return new LockKeyGenerator();
	}

}
