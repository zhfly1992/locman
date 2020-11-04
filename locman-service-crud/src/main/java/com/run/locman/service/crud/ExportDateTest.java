/*
 * File name: ExportDateTest.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年10月20日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import org.springframework.beans.factory.annotation.Autowired;

import com.run.locman.api.drools.service.ExportDate;
import com.run.locman.api.drools.service.TestInterface;
import com.run.locman.api.util.UtilTool;

/**
 * @Description:
 * @author: zhabing
 * @version: 1.0, 2017年10月20日
 */
public class ExportDateTest implements ExportDate {

	@Autowired
	private TestInterface testInterface;



	/**
	 * 
	 * @Description:测试一条数据上来之后判断是否是告警数据
	 */
	@Override
	public void testExport() {

		// 模拟数据id
		String id = UtilTool.getUuId();
		Boolean check = testInterface.run(id);
		if (!check) {
			// check==false ，执行更改这条数据为非法数据
		} else {
			
		}
	}
}
