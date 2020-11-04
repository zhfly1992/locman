/*
* File name: loginController.java								
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
* 1.0			guofeilong		2020年4月17日
* ...			...			...
*
***************************************************/

package com.run.locman.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2020年4月17日
*/

@Controller
public class loginController {

	@RequestMapping(value="/test")
	public String testF2F(HttpServletRequest request) {
		request.setAttribute("test", "test");
		System.out.println("1111111111111111111111111111111111111");
		
		return "login";
		
	}
	
	@RequestMapping(value="/test1")
	public String test(HttpServletResponse response) {
		return "login";
		
	}
}
