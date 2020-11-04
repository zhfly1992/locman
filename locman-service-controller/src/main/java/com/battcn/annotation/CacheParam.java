/*
 * File name: CacheParam.java
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

import java.lang.annotation.*;

/**
 * @Description: 锁的参数
 * @author: zhabing
 * @version: 1.0, 2018年8月17日
 */

@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheParam {

	/**
	 * 字段名称
	 *
	 * @return String
	 */
	String name() default "";
}