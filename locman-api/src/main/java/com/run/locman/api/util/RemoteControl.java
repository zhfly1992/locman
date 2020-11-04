/*
 * File name: RemoteControl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年10月22日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import com.run.locman.constants.CommonConstants;
/**
 * @Description: 新iot平台命令下发工具
 * @author: 张贺
 * @version: 1.0, 2018年10月22日
 */

public class RemoteControl {

	private static String		urlFormat	= "ipAddress:8002/api/v1/applications/appId/shadows/attributes/gateways/gatewayId/subthings/subThingId";
	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);



	/**
	 * 
	 * @Description:用网关id和子设备id生成命令下发地址
	 * @param gatewayId
	 * @param subThingId
	 * @return
	 */
	public static String createUrl(String gatewayId, String subThingId,String appId,String ip) {
		String url = urlFormat.replace("gatewayId", gatewayId).replace("subThingId", subThingId).replace("appId", appId).replace("ipAddress", ip).replace("api/v1","v1/app-push");
		return url;
	}



	/**
	 * 
	 * @Description:通过put下发命令
	 * @param appKey
	 * @param appId
	 * @param content
	 * @return
	 */
	public static JSONObject remoteControlByPut(String appKey, String content, String url,String token) {
		logger.info(String.format("[remoteControlByPut()->进入方法，参数appKey:%s,命令：%s，url:%s]", appKey,
				content, url));
		HttpPut httpPut = new HttpPut(url);
		httpPut.setEntity(new StringEntity(content,"utf-8"));
		httpPut.addHeader("Content-Type", "application/json");
		httpPut.addHeader("appKey", appKey);
		httpPut.addHeader("Token",token);
		JSONObject jsonObject = null;
		try {
			HttpResponse httpResponse = HttpClients.createDefault().execute(httpPut);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				String result = EntityUtils.toString(httpEntity, "UTF-8");
				jsonObject = JSONObject.parseObject(result);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("[remoteControlByPut()-->UnsupportedEncodingException]", e);
		} catch (ClientProtocolException e) {
			logger.error("[remoteControlByPut()-->ClientProtocolException]", e);
		} catch (IOException e) {
			logger.error("[remoteControlByPut()-->IOException]", e);
		}
		return jsonObject;
	}

}
