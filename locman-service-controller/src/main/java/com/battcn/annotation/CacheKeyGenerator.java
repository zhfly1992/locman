/*
* File name: CacheKeyGenerator.java								
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
* 1.0			zhabing		2018年8月17日
* ...			...			...
*
***************************************************/

package com.battcn.annotation;

import org.aspectj.lang.ProceedingJoinPoint;

/**
* @Description:	 key生成器
* @author: zhabing
* @version: 1.0, 2018年8月17日
*/

public interface CacheKeyGenerator {
	 /**
     * 获取AOP参数,生成指定缓存Key
     *
     * @param pjp PJP
     * @return 缓存KEY
     */
    String getLockKey(ProceedingJoinPoint pjp);
}
