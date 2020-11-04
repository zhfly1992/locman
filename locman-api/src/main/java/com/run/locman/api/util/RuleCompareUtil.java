package com.run.locman.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.run.locman.constants.CommonConstants;

/**
 * 
 * @Description:规则类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public class RuleCompareUtil {

	/**
	 * 运算符号
	 */
	private static final String		STRING5	= "==";
	/**
	 * 运算符号
	 */
	private static final String		STRING4	= "<=";
	/**
	 * 运算符号
	 */
	private static final String		STRING3	= "<";
	/**
	 * 运算符号
	 */
	private static final String		STRING2	= ">=";
	/**
	 * 运算符号
	 */
	private static final String		STRING	= ">";
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);
	private static final Pattern	P		= Pattern.compile("[0-9]{1,}");



	public static boolean strCom(String str1, String symbol, String str2) {
		try {
			if (str1 == null || str1 == "") {
				logger.info("本条规则未触发告警");
				return false;
			}
			str1 = str1.replaceAll("(\\D+)", "@$1@");
			str1 = ("@".equals(str1.substring(0, 1))) ? str1.substring(1, str1.length()) : str1;
			str1 = ("@".equals(str1.substring(str1.length() - 1, str1.length()))) ? str1.substring(0, str1.length() - 1)
					: str1;
			String[] num1 = str1.split("@");

			str2 = str2.replaceAll("(\\D+)", "@$1@");
			str2 = ("@".equals(str2.substring(0, 1))) ? str2.substring(1, str2.length()) : str2;
			str2 = ("@".equals(str2.substring(str2.length() - 1, str2.length()))) ? str2.substring(0, str2.length() - 1)
					: str2;
			String[] num2 = str2.split("@");
			int count = (num1.length - num2.length < 0) ? num1.length : num2.length;
			int n = 0;
			int i = 0;
			int res = 0;
			do {

				Matcher m1 = P.matcher(num1[i]);
				boolean bl1 = m1.matches();
				Matcher m2 = P.matcher(num2[i]);
				boolean bl2 = m2.matches();
				if (bl1 == true && bl2 == true) {
					int n1 = Integer.parseInt(num1[i]);
					int n2 = Integer.parseInt(num2[i]);
					n = n1 - n2;
				} else {
					n = num1[i].compareTo(num2[i]);
				}
				if (n == 0) {
					if (i == count - 1) {
						if (num1.length < num2.length) {
							res = -1;
						} else if (num1.length > num2.length) {
							res = 1;
						} else {
							res = 0;
						}
					}
				} else {
					res = n;
				}
				i++;
			} while (n == 0 && i <= count - 1);
			if (STRING.equals(symbol)) {
				return res > 0;
			} else if (STRING2.equals(symbol)) {
				return res >= 0;
			} else if (STRING3.equals(symbol)) {
				return res < 0;
			} else if (STRING4.equals(symbol)) {
				return res <= 0;
			} else if (STRING5.equals(symbol)) {
				return res == 0;
			} else {
				return res != 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("规则匹配出错,错误信息 : " + e);
			return false;
		}
	}
}
