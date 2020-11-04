/*
 * File name: InfoPushUtil.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2019年5月10日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.http.client.util.HttpClientUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2019年5月10日
 */

public class InfoPushUtil {

	private static final Logger	logger				= Logger.getLogger(CommonConstants.LOGKEY);

	private static ExecutorService executorService = Executors.newFixedThreadPool(5);
	/**
	 * 接收地址字段
	 */
	private static final String	RECEIVE_URL			= "receiveUrl";
	/**
	 * 请求成功状态码
	 */
	private static final String	TO_VALUE			= "200";
	/**
	 * 状态
	 */
	private static final String	STATUS				= "status";
	/**
	 * 数据类型
	 */
	private static final String	DATA_TYPE			= "dataType";
	/**
	 * 数据类型
	 */
	private static final String	DATA				= "data";
	
	/**
	 * 设施数据添加
	 */
	public static final String	FACILITIES_ADD		= "addFacilities";
	/**
	 * 设施数据修改/同步(批量)
	 */
	public static final String	FACILITIES_UPDATE	= "updateFacilities";
	/**
	 * 实时状态数据。包含数据：IOT平台的推送的数据、设备ID、设施ID
	 */
	public static final String	DEVICE				= "device";
	/**
	 * 设施产生的告警数据（包含：告警信息、告警工单信息（可为空））（1）告警工单流水号、告警描述、工单号、告警工单状态。
	 * （2）告警信息：设施ID、设施序列号、设施告警地址。
	 */
	public static final String	ALARM_INFO			= "alarmInfo";
	/**
	 * 当告警工单状态改变，主动推送告警工单状态数据。 （1）包含数据：工单ID、工单流水号、设备ID、告警工单状态。
	 */
	public static final String	ALARM_ORDER_CHANGE	= "alarmOrderChange";
	
	/**
	 * 已执行推送
	 */
	public static final String MESSAGE = "已执行推送";

	
	
	

/*	public static ExecutorService newCachedThreadPool(){
	    return new ThreadPoolExecutor(8,Integer.MAX_VALUE,60L,TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>());
	}*/


	/**
	 * 
	 * @Description:是否需要推送
	 * @param
	 * @return 推送地址,为null表示不推送
	 */
	public static String WhetherPush(String accessSecret, String ip) {
		logger.info(String.format("[IsPush()->：accessSecret:%s, ip:%s]", accessSecret, ip));
		String httpValueByGet = InterGatewayUtil.getHttpValueByGet(
				InterGatewayConstants.GET_ASINFO_BY_AS.replace("{accessSecret}", accessSecret), ip, "");
		if (null == httpValueByGet) {
			logger.error("[IsPush()->invalid：接入方信息查询失败!]");
			return null;
		} else {
			JSONObject httpJson = JSONObject.parseObject(httpValueByGet);
			String receiveUrl = httpJson.getString(RECEIVE_URL);
			logger.info(String.format("[InfoPush()->接入方接收消息地址receiveUrl：%s]", receiveUrl));
			if (StringUtils.isBlank(receiveUrl)) {
				return null;
			} else {
				return receiveUrl;
			}
		}
	}



	/**
	  * 
	  * @Description: 推送数据到指定地址(调用WhetherPush方法可以获取推送地址)
	  * @param 
	  * @return 成功success/失败fail信息
	  */
	public static String InfoPush(String receiveUrl, Object info, String dataType) {

		logger.info(String.format("[InfoPush()->：receiveUrl:%s, info:%s, infoType:%s]",receiveUrl, info, dataType));

		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				data.put(DATA_TYPE, dataType);
				data.put(DATA, info);
				String result = HttpClientUtil.getInstance().doPost(receiveUrl, data.toJSONString(), "");
				logger.info(String.format("[InfoPush()-> http请求result: %s]", result));
				if (result == null) {
					return;
				}
				JSONObject resultJson = JSON.parseObject(result);
				String message = resultJson.getString("message");
				// 请求失败,200请求成功
				if (!TO_VALUE.equals(resultJson.getString(STATUS))) {
					logger.error("[InfoPush()-> error: ]" + message);
					//return null;
				} else {
					logger.info("HTTP请求返回message:" + message);
					//return message;
				}
				
			}
		});
		return MESSAGE;

		

	}

}
