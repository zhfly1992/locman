/*
* File name: DeviceInfoConvertCrudController.java								
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
* 1.0			Administrator		2018年3月28日
* ...			...			...
*
***************************************************/

package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.locman.api.model.DeviceInfoConvertModel;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.DeviceInfoConvertRestCrudService;

/**
 * @Description: 设备信息转换crud
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月28日
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = UrlConstant.DEVICE_INFO_CONVERT)
public class DeviceInfoConvertCrudController {

	@Autowired
	private DeviceInfoConvertRestCrudService deviceInfoConvertCrudRestService;

	@RequestMapping(value = UrlConstant.CONVERT_INFO_SAVE, method = RequestMethod.POST)
	public Result<String> convertInfoAdd(@RequestBody DeviceInfoConvertModel convertInfo) {
		return deviceInfoConvertCrudRestService.convertInfoAdd(convertInfo);
	}

	@RequestMapping(value = UrlConstant.CONVERT_INFO_UPDATE, method = RequestMethod.POST)
	public Result<String> convertInfoUpdate(@RequestBody DeviceInfoConvertModel convertInfo) {
		return deviceInfoConvertCrudRestService.convertInfoUpdate(convertInfo);
	}

	@RequestMapping(value = UrlConstant.CONVERT_INFO_DELETE, method = RequestMethod.POST)
	public Result<String> convertInfoDelete(@RequestBody DeviceInfoConvertModel convertInfo) {
		return deviceInfoConvertCrudRestService.convertInfoDelete(convertInfo);
	}
	
	@PostMapping(value="/deviceSynchronization")
	public Result<String>  deviceSynchronization(@RequestBody JSONObject deviceInfo){
		return deviceInfoConvertCrudRestService.deviceSynchronization( deviceInfo);
	}
}
