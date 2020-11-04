package com.locman.app.common.util;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

	private static final Logger						logger	= Logger.getLogger(RedisUtil.class);

	private static RedisTemplate<String, Object>	redisTemplate;
	


	/**
	 * 批量删除对应的value
	 *
	 * @param keys
	 */
	public void remove(final String... keys) {
		for (String key : keys) {
			remove(key);
		}
	}



	/**
	 * 批量删除key
	 *
	 * @param pattern
	 */
	public static void removePattern(final String pattern) {
		Set<String> keys = redisTemplate.keys(pattern);
		if (keys.size() > 0) {
			redisTemplate.delete(keys);
		}
	}



	/**
	 * 删除对应的value
	 * 
	 * @param key
	 */
	public static void remove(final String key) {
		if (exists(key)) {
			redisTemplate.delete(key);
		}
	}



	/**
	 * 判断缓存中是否有对应的value
	 * 
	 * @param key
	 * @return
	 */
	public static boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}



	/**
	 * 读取缓存
	 * 
	 * @param key
	 * @return
	 */
	public static Object get(final String key) {
		Object result = null;
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		return result;
	}



	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean set(final String key, Object value) {
		boolean result = false;
		try {
			ValueOperations<String, Object> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			result = true;
		} catch (Exception e) {
			logger.error("set cache error", e);
		}
		return result;
	}



	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean set(final String key, Object value, Long expireTime) {
		boolean result = false;
		try {
			ValueOperations<String, Object> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
			result = true;
		} catch (Exception e) {
			logger.error("set cache error", e);
		}
		return result;
	}



	public static long increment(final String key, long delta) {
		return redisTemplate.opsForValue().increment(key, delta);
	}



	@Resource(name = "redisTemplate")
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		RedisUtil.redisTemplate = redisTemplate;
	}

}
