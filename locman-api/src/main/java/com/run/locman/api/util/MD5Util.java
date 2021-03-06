package com.run.locman.api.util;

import java.security.MessageDigest;

public class MD5Util {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 
	 * 转换字节数组为16进制字串
	 * 
	 * @param b 字节数组 * @return 16进制字串
	 * */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		int d1 = 0;
		int d2 = 0;
		if (n < 0) {
			n = 256 + n;
			d1 = n / 16;
			d2 = n % 16;
		}
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}
	
	
	public static String MD5(String str) {
		String encode = str;
		try {
			StringBuilder stringbuilder = new StringBuilder();
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(encode.getBytes());
			byte[] str_encoded = md5.digest();
			for (int i = 0; i < str_encoded.length; i++) {
				if ((str_encoded[i] & 0xff) < 0x10) {
					stringbuilder.append("0");
				}
				stringbuilder.append(Long.toString(str_encoded[i] & 0xff, 16));
			}
			return stringbuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
