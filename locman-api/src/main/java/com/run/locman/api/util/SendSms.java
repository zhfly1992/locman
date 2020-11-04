/*
 * File name: SendSms.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年6月25日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.SmsConstants;

/**
 * @Description:短信发送接口，调用短信网关发送短信接口
 * @author: 张贺
 * @version: 2018年6月25日
 */

public class SendSms {
	/**
	 * 状态码4
	 */
	private static final int	CODE_FOUR	= 4;
	/**
	 * 状态码3
	 */
	private static final int	CODE_THREE	= 3;
	/**
	 * 状态码2
	 */
	private static final int	CODE_TWO	= 2;
	/**
	 * 状态码
	 */
	private static final String	CODE		= "code";
	private static final Logger	logger		= Logger.getLogger(CommonConstants.LOGKEY);



	public static RpcResponse<String> sendMessage(String phonenumber, String content, String url) {
		if (!PhoneFormatCheckUtil.isPhoneLegal(phonenumber)) {
			logger.error("[sendMessage()->faile:手机号码不合法]");
			return RpcResponseBuilder.buildErrorRpcResp("手机号码不合法");
		}
		String extras = "";
		String key = SmsConstants.KEY;
		// 拼接顺序参考文档中心
		String str = content + extras + phonenumber + key;
		// md5加密
		String md5 = DigestUtils.md5Hex(str);
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);
		// 指定HttpClient请求的编码方式未"utf-8",解决POST中的参数中有中文的问题
		method.getParams().setContentCharset("utf-8");
		method.setRequestHeader("ContentType", "application/X-WWW-FORM-URLENCODED");
		NameValuePair[] data = { new NameValuePair("target", phonenumber), new NameValuePair("content", content),
				new NameValuePair("extras", extras), new NameValuePair("md5", md5) };
		method.setRequestBody(data);
		try {
			client.executeMethod(method);
			BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String temp = "";
			while ((temp = reader.readLine()) != null) {
				stringBuffer.append(temp);
			}
			String submitResult = stringBuffer.toString();
			if (submitResult == null) {
				logger.error("sendMessage()->fail:无法收到返回信息");
				return RpcResponseBuilder.buildErrorRpcResp("无响应");
			}
			JSONObject jsonObject = JSONObject.parseObject(submitResult);
			if (jsonObject.getInteger(CODE) == 1) {
				logger.error("sendMessage()->fail:key验证不通过");
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("msg"));
			}
			if (jsonObject.getInteger(CODE) == CODE_TWO) {
				logger.error("sendMessage()->fail:参数不能为空");
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("msg"));
			}
			if (jsonObject.getInteger(CODE) == CODE_THREE) {
				logger.error("sendMessage()->fail:接口错误或者提交失败");
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("msg"));
			}
			if (jsonObject.getInteger(CODE) == CODE_FOUR) {
				logger.error("sendMessage()->fail:接口受限");
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("msg"));
			}
			logger.info("sendMessage->:发送短信成功");
			return RpcResponseBuilder.buildSuccessRpcResp("发送成功", content);
		} catch (HttpException e) {
			logger.error(String.format("sendMessage()->error:%s", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		} catch (IOException e) {
			logger.error(String.format("sendMessage()->error:%s", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
