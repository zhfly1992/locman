/*
 * File name: RedisConfig.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年5月10日
 * ... ... ...
 *
 ***************************************************/

package com.run.redis.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisPoolConfig;

/**
 * @Description:SpringBoot+redis配置
 * @author: zhaoweizhi
 * @version: 1.0, 2018年5月10日
 */
@Component
@Configuration
public class RedisConfig{

	/** 最大空闲数 */
	@Value("${spring.redis.maxIdle}")
	private String	maxIdle;

	/** 连接池的最大数据库连接数 */
	@Value("${spring.redis.maxTotal}")
	private String	maxTotal;

	/** 最大建立连接等待时间 */
	@Value("${spring.redis.maxWaitMillis}")
	private String	maxWaitMillis;

	/** 空闲时检查有效性 */
	@Value("${spring.redis.testWhileIdle}")
	private String	testWhileIdle;

	/** redis地址 */
	@Value("${spring.redis.clusterNodes}")
	private String	clusterNodes;

	/** 尝试二次连接 */
	@Value("${spring.redis.testOnBorrow}")
	private String	testOnBorrow;



	/**
	 * 
	 * @Description:jedis连接池配置
	 * @return
	 */
	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 最大空闲数
		jedisPoolConfig.setMaxIdle(Integer.valueOf(maxIdle));
		// 连接池的最大数据库连接数
		jedisPoolConfig.setMaxTotal(Integer.valueOf(maxTotal));
		// 最大建立连接等待时间
		jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(maxWaitMillis));
		// 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
		jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(testOnBorrow));
		// 在空闲时检查有效性, 默认false
		jedisPoolConfig.setTestWhileIdle(Boolean.valueOf(testWhileIdle));
		return jedisPoolConfig;
	}



	/**
	 * 
	 * @Description:redisCluster 集群配置
	 * @return
	 */
	@Bean
	public RedisClusterConfiguration redisClusterConfiguration() {
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
		String[] serverArray = clusterNodes.split(",");
		Set<RedisNode> nodes = new HashSet<RedisNode>();
		for (String ipPort : serverArray) {
			String[] ipAndPort = ipPort.split(":");
			nodes.add(new RedisNode(ipAndPort[0].trim(), Integer.valueOf(ipAndPort[1])));
		}
		redisClusterConfiguration.setClusterNodes(nodes);
		redisClusterConfiguration.setMaxRedirects(3);

		return redisClusterConfiguration;
	}



	/**
	 * 
	 * @Description:jedis工厂
	 * @param jedisPoolConfig
	 * @param redisClusterConfiguration
	 * @return
	 */
	@Bean
	public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig,
			RedisClusterConfiguration redisClusterConfiguration) {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration,
				jedisPoolConfig);
		return jedisConnectionFactory;
	}



	/**
	 * 
	 * @Description:redisTemplate模板对象
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	public RedisTemplate<String, String> functionDomainRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		initDomainRedisTemplate(redisTemplate, redisConnectionFactory);
		return redisTemplate;
	}



	/**
	 * 
	 * @Description:redis存储数据格式
	 * @param redisTemplate
	 * @param factory
	 */
	private void initDomainRedisTemplate(RedisTemplate<String, String> redisTemplate, RedisConnectionFactory factory) {
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		// 开启事务
		redisTemplate.setEnableTransactionSupport(true);
		redisTemplate.setConnectionFactory(factory);
	}

}
