/*
 * File name: LockMethodInterceptor.java
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

import java.lang.reflect.Method;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.run.entity.common.Result;
import com.run.entity.common.ResultStatus;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description: redis 方案
 * @author: zhabing
 * @version: 1.0, 2018年8月17日
 */

@Aspect
@Configuration
public class LockMethodInterceptor {

	@Autowired
	public LockMethodInterceptor(RedisLockHelper redisLockHelper, CacheKeyGenerator cacheKeyGenerator) {
		this.redisLockHelper = redisLockHelper;
		this.cacheKeyGenerator = cacheKeyGenerator;
	}

	private final RedisLockHelper	redisLockHelper;
	private final CacheKeyGenerator	cacheKeyGenerator;



	@Around("@annotation(com.battcn.annotation.CacheLock)")
	public Object interceptor(ProceedingJoinPoint pjp) {
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		CacheLock lock = method.getAnnotation(CacheLock.class);
		if (StringUtils.isEmpty(lock.prefix())) {
			throw new RuntimeException("lock key don't null...");
		}
		final String lockKey = cacheKeyGenerator.getLockKey(pjp);
		String value = UUID.randomUUID().toString();
		try {
			// 假设上锁成功，但是设置过期时间失效，以后拿到的都是 false
			final boolean success = redisLockHelper.lock(lockKey, value, lock.expire(), lock.timeUnit());
			if (!success) {
				ResultStatus status = new ResultStatus();
				status.setResultCode("0006");
				status.setResultMessage("重复请求");
				status.setTimeStamp(DateUtils.stampToDate(System.currentTimeMillis() + ""));

				@SuppressWarnings("rawtypes")
				Result result = new Result();
				result.setResultStatus(status);
				return result;
			}
			try {
				return pjp.proceed();
			} catch (Throwable throwable) {
				throw new RuntimeException("系统异常");
			}
		} catch (Exception e) {
			return ResultBuilder.exceptionResult(e);

		} finally {

			/*
			 * if(lockKey!=null &&success){
			 * 
			 * lockHelper.unlock(lockName);//释放锁
			 * 
			 * }
			 */
			// TODO 如果演示的话需要注释该代码;实际应该放开
			// redisLockHelper.unlock(lockKey, value);
		}
	}
}
