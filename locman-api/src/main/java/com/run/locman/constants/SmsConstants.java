/*
 * File name: SmsConstants.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年6月28日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.constants;

/**
 * @Description:发送短信服务常量
 * @author: 张贺
 * @version: 1.0, 2018年6月28日
 */

public class SmsConstants {
	/** 密匙key */
	public static final String	KEY			= "31538f53c6cc6d8dab6fa83a79bce059";
	/** 创建授权接口地址 */
	public static final String	REGIST_URL	= "http://sms.locman.cn/sefon/api/auth/regist";
	/** 创建授权接口返回状态编码 */
	public static final String	CODE		= "code";
	/** 创建授权接口返回状态信息 */
	public static final String	MSG			= "msg";

	public static final int[]	CODE_INT	= { 0, 1, 2, 3, 4 };
}
