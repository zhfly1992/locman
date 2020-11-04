/*
 * File name: BasicDataCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年4月23日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.run.entity.common.Result;
import com.run.locman.api.entity.BaseDataSynchronousState;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.BasicDataCrudRestService;

/**
 * @Description: 基础同步数据crud
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月23日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = UrlConstant.BASIC_DATA)
public class BasicDataCrudController {

	@Autowired
	private BasicDataCrudRestService basicDataCrudRestService;



	@RequestMapping(value = UrlConstant.BASIC_FACILITIES, method = RequestMethod.POST)
	public Result<Boolean> basicFacilitiesSynchronous(@RequestBody String synchronousInfo) {
		return basicDataCrudRestService.basicFacilitiesSynchronous(synchronousInfo);
	}



	@RequestMapping(value = UrlConstant.SYNCHRONOUS_STATE, method = RequestMethod.GET)
	public Result<BaseDataSynchronousState> getSynchronousState(@PathVariable String accessSecret) {
		return basicDataCrudRestService.getSynchronousState(accessSecret);
	}



	@RequestMapping(value = UrlConstant.BASIC_ALARMRULE, method = RequestMethod.POST)
	public Result<Boolean> basicAlarmRuleAdd(@RequestBody String accessSecret) {
		return basicDataCrudRestService.basicAlarmRuleAdd(accessSecret);
	}



	@RequestMapping(value = UrlConstant.BASIC_DEVICETYPE_TEMPLATE, method = RequestMethod.POST)
	public Result<Boolean> basicFacilitiesAdd(@RequestBody String accessSecret) {
		return basicDataCrudRestService.basicDeviceAttributeTemplate(accessSecret);
	}



	@RequestMapping(value = UrlConstant.BASIC_DEVICEINFO_CONVERT, method = RequestMethod.POST)
	public Result<Boolean> basicDeviceInfoConvert(@RequestBody String accessSecret) {
		return basicDataCrudRestService.basicDeviceInfoConvertAdd(accessSecret);
	}
}
