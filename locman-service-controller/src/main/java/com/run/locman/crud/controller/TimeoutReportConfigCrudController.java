/*
* File name: TimeoutReportConfigCrudController.java								
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
* 1.0			guofeilong		2018年6月25日
* ...			...			...
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
import com.run.locman.crud.service.TimeoutReportConfigRestCrudService;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月25日
*/
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = UrlConstant.TIMEOUT)
public class TimeoutReportConfigCrudController {

	@Autowired TimeoutReportConfigRestCrudService timeoutReportConfigRestCrudService;
	
	@RequestMapping(value = UrlConstant.ADD_TIMEOUT_REPORT_CONFIG, method = RequestMethod.POST)
	public Result<String> addTimeoutReportConfig(@RequestBody String congigInfo) {
		return timeoutReportConfigRestCrudService.addTimeoutReportConfig(congigInfo);
	}
	
	@RequestMapping(value = UrlConstant.DELETE_TIMEOUT_REPORT_CONFIG, method = RequestMethod.POST)
	public Result<String> delTimeoutReportConfig(@RequestBody String congigIds) {
		return timeoutReportConfigRestCrudService.delTimeoutReportConfig(congigIds);
	}
	
	@RequestMapping(value = UrlConstant.UPDATE_TIMEOUT_REPORT_CONFIG, method = RequestMethod.POST)
	public Result<String> updateTimeoutReportConfig(@RequestBody String congigInfo) {
		return timeoutReportConfigRestCrudService.updateTimeoutReportConfig(congigInfo);
	}
	
}
