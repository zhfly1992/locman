/*
 * File name: RedisLockHelper.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2018年8月17日 ... ...
 * ...
 *
 ***************************************************/

package com.battcn.annotation;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

/**
 * @Description: 需要定义成 Bean
 * @author: zhabing
 * @version: 1.0, 2018年8月17日
 */
@Component
public class RedisLockHelper {
	private static final String				DELIMITER	= "|";

	/**
	 * 如果要求比较高可以通过注入的方式分配
	 */
	private RedisTemplate<String, String>	redisTemplate;



	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}



	/**
	 * 获取锁（存在死锁风险）
	 *
	 * @param lockKey
	 *            lockKey
	 * @param value
	 *            value
	 * @param time
	 *            超时时间
	 * @param unit
	 *            过期单位
	 * @return true or false
	 */
	public boolean tryLock(final String lockKey, final String value, final long time, final TimeUnit unit) {

		return redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.set(lockKey.getBytes(), value.getBytes(), Expiration.from(time, unit),
						RedisStringCommands.SetOption.SET_IF_ABSENT);
				return true;
			}

		});

		// return stringRedisTemplate.execute((RedisCallback<Boolean>)
		// connection -> connection.set(lockKey.getBytes(), value.getBytes(),
		// Expiration.from(time, unit),
		// RedisStringCommands.SetOption.SET_IF_ABSENT));
	}



	/**
	 * 获取锁
	 *
	 * @param lockKey
	 *            lockKey
	 * @param uuid
	 *            UUID
	 * @param timeout
	 *            超时时间
	 * @param unit
	 *            过期单位
	 * @return true or false
	 */
	public boolean lock(String lockKey, final String uuid, long timeout, final TimeUnit unit) {
		final long milliseconds = Expiration.from(timeout, unit).getExpirationTimeInMilliseconds();
		boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey,
				(System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
		if (success) {
			redisTemplate.expire(lockKey, timeout, TimeUnit.SECONDS);
		} else {
			String oldVal = redisTemplate.opsForValue().getAndSet(lockKey,
					(System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
			final String[] oldValues = oldVal.split(Pattern.quote(DELIMITER));
			if (Long.parseLong(oldValues[0]) + 1 <= System.currentTimeMillis()) {
				return true;
			}
		}

		return success;
	}

	/**
	 * 死锁: setIfAbsent+expire 先执行第一个方法 第二个方法未执行，会导致死锁，数据一致存在redis中 set 方法 两句同时执行
	 * 要么都成功要么都失败，通过判断redis中数据是否存在，就可以解决死锁机制！
	 */

}
