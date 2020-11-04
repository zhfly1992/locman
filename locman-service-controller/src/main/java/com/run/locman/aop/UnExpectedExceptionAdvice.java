/*
 * File name: UnExpectedExceptionAdvice.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年11月20日
 * ... ... ...
 *
 ***************************************************/
package com.run.locman.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.run.entity.common.Result;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2017年11月20日
 */
@ControllerAdvice({ "com.run.locman" })
public class UnExpectedExceptionAdvice {

	private static Logger LOG = LoggerFactory.getLogger(CommonConstants.LOGKEY);



	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Result<?> handleExp(Exception e) {
		if (e instanceof HttpMessageNotReadableException) {
			LOG.error("UnExpectedException:" + e.getMessage());
			return ResultBuilder.invalidResult();
		}
		return ResultBuilder.failResult(null);
	}
}
