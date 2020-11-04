/*
* File name: GetInternateTimeUtil.java								
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
* 1.0			guofeilong		2018年6月21日
* ...			...			...
*
***************************************************/

package com.run.locman.api.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.run.locman.constants.CommonConstants;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月21日
*/

public class GetInternateTimeUtil {
	private static final Logger logger = Logger.getLogger(CommonConstants.LOGKEY);
	/**
	 * 返回当前网络时间
	 * */
	public static String queryTime(){
		try {
			logger.info("即将访问http://www.baidu.com获取时间");
			// 中国科学院国家授时中心
            //URL url = new URL("http://www.ntsc.ac.cn");
			URL url = new URL("http://www.baidu.com");
            URLConnection conn = url.openConnection();
            conn.connect();
            long dateL = conn.getDate();
            Date date = new Date(dateL);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            return dateFormat.format(date);
        } catch (MalformedURLException e) {
        	logger.error("queryTime()-->e",e);
            e.printStackTrace();
        } catch (IOException e) {
        	logger.error("queryTime()-->e",e);
            e.printStackTrace();
        }
		logger.error("查询网络时间失败");
        return "";
	}
	
	public static Long queryMillisTime(){
		try {
			// 中国科学院国家授时中心
            //URL url = new URL("http://www.ntsc.ac.cn");
			URL url = new URL("http://www.baidu.com");
            URLConnection conn = url.openConnection();
            conn.connect();
            long dateL = conn.getDate();
            return dateL;
        } catch (MalformedURLException e) {
        	logger.error("queryMillisTime()-->e",e);
            e.printStackTrace();
        } catch (IOException e) {
        	logger.error("queryMillisTime()-->e",e);
            e.printStackTrace();
        }
		logger.error("查询网络时间失败");
		return null;
	}
}
