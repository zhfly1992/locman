/*
* File name: DeviceTypeRestQueryController.java								
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
* 1.0			guofeilong		2019年5月8日
* ...			...			...
*
***************************************************/

package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.DeviceTypeRestQueryService;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年5月8日
*/
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = UrlConstant.DEVICETYPE)
public class DeviceTypeRestQueryController {
	
	@Autowired
	private DeviceTypeRestQueryService deviceTypeRestQueryService;

	
	//@GetMapping(value = "/all/{accessSecret}")DEVICETYPE_ALL_BY_ACCESS
	@GetMapping(value = UrlConstant.DEVICETYPE_ALL_BY_ACCESS)
	public Result<List<Map<String,String>>> findAllDeviceTypeAndNum(@PathVariable(value = "accessSecret") String accessSecret) {
		return deviceTypeRestQueryService.findAllDeviceTypeAndNum(accessSecret);
	}
}
