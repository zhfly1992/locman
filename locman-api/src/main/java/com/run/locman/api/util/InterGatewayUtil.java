package com.run.locman.api.util;

import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.http.client.util.HttpClientUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;

/**
 * 
 * @Description:网关工具类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public class InterGatewayUtil {
	/**
	 * 状态
	 */
	private static final String	STATUS		= "status";
	/**
	 * 状态码
	 */
	private static final String	TO_VALUE	= "200";
	/**
	 * 返回值
	 */
	private static final String	RESULT_CODE	= "resultCode";
	/**
	 * 
	 */
	private static final String	ZERO_VALUE	= "0000";
	private static final Logger	logger		= Logger.getLogger(CommonConstants.LOGKEY);



	/**
	 * @param path
	 *            方法路径,如:/interGateway/v3/appTag/devices
	 * @return JSONObject value
	 * @Description:通过post请求链接interGateway
	 */

	public static String getHttpValueByPost(String path, JSONObject requestParameters, String ip, String token) {
		String result = HttpClientUtil.getInstance().doPost(ip + InterGatewayConstants.PORT + path,
				JSONObject.toJSONString(requestParameters), token);
		logger.info(
				"Url:" + ip + InterGatewayConstants.PORT + path + "###参数:" + requestParameters + "###TOKEN" + token);
		if (result == null) {
			logger.error(
					"Url:" + ip + InterGatewayConstants.PORT + path + "---参数:" + requestParameters + "TOKEN:" + token);
			logger.error("[getHttpValueByPost()-> error: interGateway请求失败]");
			return null;
		}
		JSONObject resultJson = JSON.parseObject(result);
		JSONObject resultStatus = resultJson.getJSONObject("resultStatus");
		// 请求失败,0000请求成功
		if (!ZERO_VALUE.equals(resultStatus.getString(RESULT_CODE))) {
			logger.error("[getHttpValueByPost()-> error: interGateway]" + resultStatus.getString("resultMessage"));
			return null;
		}
		String httpValue = resultJson.getString("value");
		if (httpValue == null) {
			logger.error("[getHttpValueByPost()-> error: HTTP请求返回value为空]" + resultStatus.getString("resultMessage"));
			return null;
		} else {
			logger.info("InterGateway返回数据:" + httpValue);
			return httpValue;
		}
	}



	/**
	 * @param path
	 *            方法路径,如:/interGateway/v3/appTag/devices
	 * @return JSONObject value
	 * @Description:通过get请求链接interGateway
	 */

	public static String getHttpValueByGet(String path, String ip, String token) {
		String result = HttpClientUtil.getInstance().doGet(ip + InterGatewayConstants.PORT + path, token);
		logger.info("Url:" + ip + InterGatewayConstants.PORT + path + "###TOKEN:" + token);
		if (result == null) {
			logger.error("Url:" + ip + InterGatewayConstants.PORT + path + "###TOKEN:" + token);
			logger.error("[getHttpValueByGet()-> error: interGateway请求失败]]");
			return null;
		}
		JSONObject resultJson = JSON.parseObject(result);
		JSONObject resultStatus = resultJson.getJSONObject("resultStatus");
		// 请求失败,0000请求成功
		if (!ZERO_VALUE.equals(resultStatus.getString(RESULT_CODE))) {
			logger.error("[getHttpValueByGet()-> error: interGateway]" + resultStatus.getString("resultMessage"));
			return null;
		}
		String valueString = resultJson.getString("value");
		if (valueString == null) {
			logger.error("[getHttpValueByGet()-> error: HTTP请求返回value为空]" + resultStatus.getString("resultMessage"));
			return null;
		} else {
			logger.info("InterGateway返回数据:" + valueString);
			return valueString;
		}
	}



	public static String getHttpValueByGet(String path, String ip, Map<String, String> headerParam) {
		// 请求头格式
		/*
		 * headerParam.put("Content-Type", "application/json"); String result =
		 * HttpClientUtil.getInstance() .doGet(ip //+ InterGatewayConstants.PORT
		 * + path, headerParam);
		 */
		headerParam.put("Content-Type", "application/json");
		String result = HttpClientUtil.getInstance().doGet(ip + InterGatewayConstants.PORT + path, headerParam);
		logger.info("Url:" + ip + InterGatewayConstants.PORT + path + "###headerParam:" + headerParam);
		if (result == null) {
			logger.error("Url:" + ip + InterGatewayConstants.PORT + path + "###headerParam:" + headerParam);
			logger.error("[getHttpValueByGet()-> error: interGateway进行HTTP请求失败]]");
			return null;
		}
		JSONObject resultJson = JSON.parseObject(result);
		// 请求失败,0000请求成功
		if (!TO_VALUE.equals(resultJson.getString(STATUS))) {
			logger.error("[getHttpValueByGet()-> error: interGateway]" + resultJson.getString("message"));
			return null;
		}
		String response = resultJson.getString("response");

		if (response == null) {
			logger.error("[getHttpValueByGet()-> error: HTTP请求返回response为空]" + resultJson.getString("message"));
			return null;
		} else {
			logger.info("HTTP请求返回数据:" + response);
			return response;
		}
	}

}
