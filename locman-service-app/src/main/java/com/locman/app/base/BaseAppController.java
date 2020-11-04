package com.locman.app.base;

import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 默认的controller类
 * 
 * @author lovava
 * 
 */
public class BaseAppController {

	@Autowired
	public HttpServletRequest request;



	public HttpServletRequest getRequest() {
		return request;
	}



	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}



	/**
	 * 获取北京时区当前时间
	* <method description>
	*
	* @return
	 */
	public static Date getNowDate() {
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		Date now = new Date();
		return now;
	}

}