/*
* File name: ReflectUtil.java								
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
* 1.0			钟滨远		2020年9月2日
* ...			...			...
*
***************************************************/

package com.run.redis.config;

import java.lang.reflect.Field;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年9月2日
*/

public class ReflectUtil {
	
	public static Field getField(Class<?> clazz,String fieldName){
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if(!clazz.getSuperclass().isAssignableFrom(Object.class)){
                return getField(clazz.getSuperclass(),fieldName);
            }
            return null;
        }
    }
 
    @SuppressWarnings("unchecked")
	public static <T> T getObject(Object obj,String fieldName){
        try {
            Field field = getField(obj.getClass(),fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }


}
