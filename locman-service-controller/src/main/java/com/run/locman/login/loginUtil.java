/*
* File name: loginUtil.java								
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
* 1.0			guofeilong		2020年4月20日
* ...			...			...
*
***************************************************/

package com.run.locman.login;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.InterGatewayConstants;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2020年4月20日
*/

public class loginUtil {
	
	@Autowired
	HttpServletRequest request;
	
	public String login(HttpServletRequest request){
		
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		System.out.println("account:" + account);
		System.out.println("password:" + password);
		String stringToMD5 = stringToMD5(password);
		JSONObject json = new JSONObject();
		json.put("loginAccount", account);
		json.put("password", stringToMD5);
		json.put("userType", stringToMD5);
		//String scheme = request.getScheme();
		//System.out.println(scheme);
		String httpValueByPost = InterGatewayUtil.getHttpValueByPost(InterGatewayConstants.LOGIN, json, "http://182.43.224.122", null);
		System.out.println(json);
		System.out.println(httpValueByPost);
		return httpValueByPost;
		
		
	}
	
	public String checkUserExit(HttpServletRequest request){
		
		String account = request.getParameter("account");
		System.out.println("account:" + account);
		
		String httpValueByPost = InterGatewayUtil.getHttpValueByGet(InterGatewayConstants.CHECK_USER_EXIT + account, "http://182.43.224.122", "");
		System.out.println(httpValueByPost);
		request.setAttribute("v", "false");
		return httpValueByPost;
		
		
	}
	
	public static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
}
