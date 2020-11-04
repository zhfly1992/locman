/*
 * File name: DeviceTypeTemplateRestCudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年09月15日 ... ... ...
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
import com.run.locman.crud.service.DeviceTypeRestCrudService;
import com.run.locman.crud.service.DeviceTypeTemplateRestCudService;

/**
 * @Description: 设备类型绑定
 * @author: 田明
 * @version: 1.0, 2017年09月15日
 */
@RestController
@RequestMapping(value = UrlConstant.DEVICETYPE_PROPRT_CONFIG)
public class DeviceTypeTemplateRestCudController {

	@Autowired
	private DeviceTypeTemplateRestCudService	deviceTypeTemplateRestCudService;
	@Autowired
	private DeviceTypeRestCrudService			deviceTypeRestCrudService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.UNBIND_DEVICETYPE_PROPRTY_AND_TEMP, method = RequestMethod.POST)
	public Result<String> unbindDeviceTypePropertyAndTemplate(@RequestBody String param) {
		return deviceTypeTemplateRestCudService.unbindDeviceTypePropertyAndTemplate(param);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.BIND_DEVICETYPE_PROPRTY_AND_TEMP, method = RequestMethod.POST)
	public Result<String> bindDeviceTypePropertyAndTemplate(@RequestBody String param) {
		return deviceTypeTemplateRestCudService.bindDeviceTypePropertyAndTemplate(param);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.EDIT_DEVICETYPE, method = RequestMethod.POST)
	public Result<String> editDeviceType(@RequestBody String param) {
		return deviceTypeRestCrudService.editDeviceType(param);
	}
}
