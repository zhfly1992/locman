/*
 * File name: Sign.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2020年4月2日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.wingiot.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2020年4月2日
 */

public class SignAndTimeOffset {
	public static String sign(Map<String, String> param, long timestamp, String application, String secret, byte[] body)
			throws Exception {
        // 连接系统参数
        StringBuffer sb = new StringBuffer();
        sb.append("application").append(":").append(application).append("\n");
        sb.append("timestamp").append(":").append(timestamp).append("\n");

        // 连接请求参数
        if (param != null) {
        TreeSet<String> keys = new TreeSet<String>(param.keySet());
        Iterator<String> i = keys.iterator();
        while (i.hasNext()) {
            String s = i.next();
            String val = param.get(s);
            sb.append(s).append(":").append(val == null ? "" : val).append("\n");
        }
    }

    //body数据写入需要签名的字符流中
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(sb.toString().getBytes("utf-8"));
    if (body != null && body.length > 0) {
        baos.write(body);
        baos.write("\n".getBytes("utf-8"));
    }

        // 得到需要签名的字符串
        String string = baos.toString();
        System.out.println("Sign string: " + string);

        // hmac-sha1编码
        byte[] bytes = null;
        SecretKey secretKey = new SecretKeySpec(secret.getBytes("utf-8"), "HmacSha1");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        bytes = mac.doFinal(string.getBytes("utf-8"));

        // base64编码
        String encryptedString = new String(Base64.encodeBase64(bytes));

        // 得到需要提交的signature签名数据
        return encryptedString;
	}
	
	
	 public static long getTimeOffset() {
	        long offset = 0;
	        HttpResponse response = null;

	        //构造httpGet请求
	        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	        HttpGet httpTimeGet = new HttpGet("https://ag-api.ctwing.cn/echo");

	        try {
	         long start = System.currentTimeMillis();
	         response = httpClient.execute(httpTimeGet);
	         long end = System.currentTimeMillis();
	         //时间戳在返回的响应的head的x-ag-timestamp中
	         Header[] headers = response.getHeaders("x-ag-timestamp");
	         if (headers.length > 0) {
	             long serviceTime = Long.parseLong(headers[0].getValue());
	             offset = serviceTime - (start + end) / 2L;
	         }
	         httpClient.close();
	        } catch (ClientProtocolException e) {
	         e.printStackTrace();
	        } catch (IOException e) {
	         e.printStackTrace();
	        }
	        return offset;
	    }
}