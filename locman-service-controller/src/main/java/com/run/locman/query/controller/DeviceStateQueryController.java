/*
 * File name: DeviceStateQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年9月28日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.DeviceStateRestQueryService;

/**
 * @Description: 导出设备状态工具类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年9月28日
 */
@RestController
@RequestMapping(value = UrlConstant.DEVICE_STATE_URL)
public class DeviceStateQueryController {

	@Autowired
	private DeviceStateRestQueryService deviceStateRestQueryService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.DEVICE_STATE_EXPORT, method = RequestMethod.GET)
	public ModelAndView exportDeviceState(@PathVariable String accessSecret, ModelMap model) {
		return deviceStateRestQueryService.exportDeviceState(accessSecret, model);
	}

}
