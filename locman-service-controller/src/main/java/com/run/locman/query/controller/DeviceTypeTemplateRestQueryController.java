/*
 * File name: DeviceTypeTemplateRestQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 wangsheng 2017年8月8日 ... ...
 * ...
 *
 ***************************************************/
package com.run.locman.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.DeviceTypeTemplateRestQueryService;

/**
 * @Description: 设备数据类型查询Contro
 * @author: wangsheng
 * @version: 1.0, 2017年8月8日
 */
@CrossOrigin(maxAge = 3600,origins = "*")
@RestController
@RequestMapping(value = UrlConstant.DEVICETYPE_PROPRT_CONFIG)
public class DeviceTypeTemplateRestQueryController {

	@Autowired
	private DeviceTypeTemplateRestQueryService deviceTypeTemplateRestQueryService;



	@RequestMapping(value = UrlConstant.QUERY_DEVICETYPE_PROPRT_CONFIG_LIST, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> queryDeviceTypePropertyConfigList(
			@RequestBody String deviceTypePropertyConfigInfo) {
		return deviceTypeTemplateRestQueryService.queryDeviceTypePropertyConfigList(deviceTypePropertyConfigInfo);
	}



	@RequestMapping(value = UrlConstant.QUERY_DEVICE_PROPERTY_TEMPLATE_LIST, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, String>>> queryDevicePropertyTemplateList(
			@RequestBody String devicePropertyTemplateInfo) {
		return deviceTypeTemplateRestQueryService.queryDevicePropertyTemplateList(devicePropertyTemplateInfo);
	}

}
