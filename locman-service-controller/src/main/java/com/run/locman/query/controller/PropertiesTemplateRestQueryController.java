/*
* File name: PropertiesTemplateRestQueryController.java								
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
import com.run.locman.api.entity.DevicePropertiesTemplate;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.PropertiesTemplateRestQueryService;

/**
 * @Description: 分页查询设备属性模版
 * @author: 李康诚
 * @version: 1.0, 2017年11月20日
 */
@RestController
@RequestMapping(UrlConstant.DEVICE_BASE_PATH)
@CrossOrigin(origins = "*")
public class PropertiesTemplateRestQueryController {
	@Autowired
	private PropertiesTemplateRestQueryService propertiesTemplateRestQueryService;



	
	@PostMapping(UrlConstant.DEVICE_PROPERTIES_TEMPLATE_LIST)
	public Result<PageInfo<DevicePropertiesTemplate>> list(@PathVariable String accessSecret,
			@RequestBody String queryParams) {
		return propertiesTemplateRestQueryService.list(accessSecret, queryParams);
	}

	@PostMapping(UrlConstant.TEMPLATE_NAME_EXIST)
	public Result<Boolean> checkTemplateName(@RequestBody String  templateInfo) {
		return propertiesTemplateRestQueryService.checkTemplateName(templateInfo);
	}
}
