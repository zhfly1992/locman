/*
* File name: PropertiesRestQueryController.java								
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
* 1.0			李康诚		2017年11月20日
* ...			...			...
*
***************************************************/
package com.run.locman.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.PropertiesRestQueryService;

/**
 * @Description: 分页查询设备属性 
 * @author: 李康诚
 * @version: 1.0, 2017年11月20日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(UrlConstant.DEVICE_BASE_PATH)
public class PropertiesRestQueryController {
	@Autowired
	private PropertiesRestQueryService propertiesRestQueryService;



	
	@PostMapping(UrlConstant.DEVICE_PROPERTIES_LIST)
	public Result<PageInfo<DeviceProperties>> list(@PathVariable String templateId, @RequestBody String queryParams) {
		return propertiesRestQueryService.list(templateId, queryParams);
	}
	
	@PostMapping(UrlConstant.NAME_OR_SIGN_EXIST)
	public Result<Boolean> checkNameOrSigExist(@RequestBody String queryParams) {
		return propertiesRestQueryService.checkNameOrSigExist(queryParams);
	}
}
