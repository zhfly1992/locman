/*
 * File name: AlarmInfoCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年11月20日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.crud.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.AlarmInfoCrudRestService;

/**
 * @Description: 修改告警信息
 * @author: Administrator
 * @version: 1.0, 2017年11月20日
 */
@RestController
@RequestMapping(value = "alarmInfo")
public class AlarmInfoCrudController {

	@Autowired
	public AlarmInfoCrudRestService alarmInfoCrudRestService;

	/**
	 * 
	 * @Description:修改告警信息忽略状态
	 * @param alarmInfo
	 * @return Result<String>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.UPDATE_THE_DEL, method = RequestMethod.POST)
	public Result<String> updateTheDel(@RequestBody String alarmInfo) {
		return alarmInfoCrudRestService.updateTheDel(alarmInfo);
	}
}
