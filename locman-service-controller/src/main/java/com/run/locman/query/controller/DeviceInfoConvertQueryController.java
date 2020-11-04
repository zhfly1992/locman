/*
 * File name: DeviceInfoConvertQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年3月28日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.DeviceInfoConvert;
import com.run.locman.api.model.DeviceInfoConvertModel;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.DeviceInfoConvertRestQueryService;

/**
 * @Description: 转换信息查询controller
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月28日
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = UrlConstant.DEVICE_INFO_CONVERT)
public class DeviceInfoConvertQueryController {

	@Autowired
	private DeviceInfoConvertRestQueryService deviceInfoConvertRestQueryService;



	@RequestMapping(value = UrlConstant.CONVERT_INFO_QUERYALL, method = RequestMethod.POST)
	public Result<PageInfo<DeviceInfoConvert>> convertInfoQueryAll(@RequestBody DeviceInfoConvertModel convertInfo) {
		return deviceInfoConvertRestQueryService.convertInfoQueryAll(convertInfo);
	}



	@RequestMapping(value = UrlConstant.CONVERT_INFO_QUERYBYID, method = RequestMethod.POST)
	public Result<DeviceInfoConvert> convertInfoQueryById(@RequestBody DeviceInfoConvertModel convertInfo) {
		return deviceInfoConvertRestQueryService.convertInfoQueryById(convertInfo);
	}



	@RequestMapping(value = UrlConstant.CONVERT_INFO_EXIST, method = RequestMethod.POST)
	public Result<String> existConvertInfo(@RequestBody DeviceInfoConvertModel convertInfo) {
		return deviceInfoConvertRestQueryService.existConvertInfo(convertInfo);
	}

}
