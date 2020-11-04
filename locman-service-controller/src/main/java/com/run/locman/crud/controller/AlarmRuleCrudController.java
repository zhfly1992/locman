/*
* File name: AlarmRuleCrudController.java								
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
* 1.0			lkc		2017年10月27日
* ...			...			...
*
***************************************************/
package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.AlarmRuleCrudService;

/**
 * 
 * @Description: 告警规则crud
 * @author: lkc
 * @version: 1.0, 2017年10月27日
 */
@RestController
@RequestMapping(value = UrlConstant.ALARM_RULE)
public class AlarmRuleCrudController {
	@Autowired
	public AlarmRuleCrudService alarmRuleCrudService;

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_RULE_ADD, method = RequestMethod.POST)
	public Result<String> alarmRuleAdd(@RequestBody String alarmInfo) {
		return alarmRuleCrudService.alarmRuleAdd(alarmInfo);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_RULE_UPDATE, method = RequestMethod.POST)
	public Result<String> alarmRuleUpdate(@RequestBody String alarmInfo) {
		return alarmRuleCrudService.alarmRuleUpdate(alarmInfo);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_RULE_DELETE, method = RequestMethod.GET)
	public Result<Boolean> alarmRuleDel(@PathVariable String id, @PathVariable String userId) {
		return alarmRuleCrudService.alarmRuleDel(id, userId);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_RULE_STATE, method = RequestMethod.POST)
	public Result<Boolean> alarmRuleState(@RequestBody String alarmInfo) {
		return alarmRuleCrudService.alarmRuleState(alarmInfo);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_RULE_PULISH, method = RequestMethod.POST)
	public Result<Boolean> alarmRulePublish(@RequestBody String alarmInfo) {
		return alarmRuleCrudService.alarmRulePublish(alarmInfo);
	}
}
