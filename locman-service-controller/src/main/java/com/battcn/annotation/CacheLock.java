/*
 * File name: CacheLock.java
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

/**
 * @Description: 锁
 * @author: zhabing
 * @version: 1.0, 2018年8月17日
 */

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {

	/**
	 * redis 锁key的前缀
	 *
	 * @return redis 锁key的前缀
	 */
	String prefix()

	default "";



	/**
	 * 过期秒数,默认为3秒
	 *
	 * @return 轮询锁的时间
	 */
	int expire()

	default 5;



	/**
	 * 超时时间单位
	 *
	 * @return 秒
	 */
	TimeUnit timeUnit()

	default TimeUnit.SECONDS;



	/**
	 * <p>
	 * Key的分隔符（默认 :）
	 * </p>
	 * <p>
	 * 生成的Key：N:SO1008:500
	 * </p>
	 *
	 * @return String
	 */
	String delimiter() default ":";
}
