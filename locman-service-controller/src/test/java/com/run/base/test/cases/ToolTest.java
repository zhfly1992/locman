/*
* File name: TestTool.java								
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
* 1.0			qulong		2018年1月17日
* ...			...			...
*
***************************************************/

package com.run.base.test.cases;

import org.junit.Test;

/**
* @Description:	测试
* @author: qulong
* @version: 1.0, 2018年1月17日
*/

public class ToolTest {

	@Test
	public void test(){
		String str = "aaa";
		String[] split = str.split(",");
		for (String string : split) {
			System.err.println(string);
		}
	}
	
}
