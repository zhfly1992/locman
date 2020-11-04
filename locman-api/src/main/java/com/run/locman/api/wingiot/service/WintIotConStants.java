/*
* File name: WintIotConStants.java								
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
* 1.0			Administrator		2020年4月2日
* ...			...			...
*
***************************************************/

package com.run.locman.api.wingiot.service;


import java.util.HashMap;
import java.util.Map;

import com.ctg.ag.sdk.biz.aep_order.PriceRequest;



/**
* @Description:	
* @author: Administrator
* @version: 1.0, 2020年4月2日
*/

public class WintIotConStants {
	// 密钥，到控制台->应用管理打开应用可以找到此值
	public static final String secret = "DPw1zlldmK"  ;
	// appKey，到应用管理打开应用可以找到此值
	public static final String application = "cOw8ZbRuTK8"  ;
//	// MasterKey，在产品中心打开对应的产品查看此值
//	public static final String MasterKey = "cfbf94bfa411434a8802452a60b34b08";
	
	// 存储productID和 Master-APIkey映射关系
	public static Map<String, String> product;
	
	static{
		product = new HashMap<String, String>();
		product.put("10045636", "f0993c24b9514872a5e887845dfe0811");
		product.put("10030639", "cfbf94bfa411434a8802452a60b34b08");
		product.put("10025249", "5a4a60c81be54de68844f4fea3da134b");
		product.put("10055268", "ac184d5bd6fc411aba7fd7e882978718");
		product.put("10053083", "614dd732688f451888f7156b8915954b");
		product.put("10067156", "08e550aa105e4ae9a26490aa56f6533b");
		product.put("10072503", "43544dcabfe14de4847222e0d10b47c0");
	}

}
