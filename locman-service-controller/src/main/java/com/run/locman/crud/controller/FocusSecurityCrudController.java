/*
 * File name: FocusSecurityCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 钟滨远 2020年4月26日 ... ... ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.FocusSecurityRestCrudService;

/**
 * @Description: 重点保障CRUD
 * @author: 钟滨远
 * @version: 1.0, 2020年4月26日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = UrlConstant.FOCUSSECURITY)
public class FocusSecurityCrudController {

	@Autowired
	private FocusSecurityRestCrudService focusSecurityRestCrudService;


	/**
	 * 
	* @Description:新增保障项目
	* @param params
	* @return
	 */
	@PostMapping(value = UrlConstant.ADD_FOCUSSECURITY)
	public Result<String> addFocusSecurity(@RequestBody String params) {

		return focusSecurityRestCrudService.addFocusSecurity(params);

	}
	
	@PostMapping(value = UrlConstant.ENABLED_FOCUSSECURITY)
	public Result<String> enabledFocusSecurity(@RequestBody JSONObject json) {
		return focusSecurityRestCrudService.enabledFocusSecurity(json);
	}
	
	@PostMapping(value = UrlConstant.OPERATE_LOCK)
	public Result<String> operateLock(@RequestBody JSONObject json) {
		return focusSecurityRestCrudService.testSendCommand(json);
	}
}
