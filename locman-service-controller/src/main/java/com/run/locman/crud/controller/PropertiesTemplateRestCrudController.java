/*
* File name: PropertiesTemplateRestCrudController.java								
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
* 1.0			田明		2017年11月20日
* ...			...			...
*
***************************************************/
package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.PropertiesTemplateRestCrudService;

/**
 * @Description: 设备属性模板管理
 * @author: 田明
 * @version: 1.0, 2017年11月20日
 */
@RestController
@RequestMapping(UrlConstant.DEVICE_BASE_PATH)
public class PropertiesTemplateRestCrudController {
	@Autowired
	private PropertiesTemplateRestCrudService propertiesTemplateRestCrudService;



	@CrossOrigin(origins = "*")
	@PostMapping(UrlConstant.DEVICE_PROPERTIES_TEMPLATE_ADD)
	public Result<String> add(@PathVariable String accessSecret, @RequestBody String addParams) {
		return propertiesTemplateRestCrudService.add(accessSecret, addParams);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(UrlConstant.DEVICE_PROPERTIES_TEMPLATE_UPDATESTATE)
	public Result<String> updateState(@PathVariable String id, @RequestBody String updateParams) {
		return propertiesTemplateRestCrudService.updateState(id, updateParams);
	}
}
