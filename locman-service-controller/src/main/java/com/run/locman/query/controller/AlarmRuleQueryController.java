/*
* File name: AlarmRuleQueryController.java								
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
* 1.0			田明		2017年10月31日
* ...			...			...
*
***************************************************/
package com.run.locman.query.controller;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.AlarmRule;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.AlarmRuleRestQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: 告警规则查询
 * @author: 田明
 * @version: 1.0, 2017年10月31日
 */
@RestController
@RequestMapping(value = UrlConstant.ALARM_RULE)
public class AlarmRuleQueryController {
	@Autowired
	private AlarmRuleRestQueryService alarmRuleRestQueryService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.FIND_DATAPOINT_LIST_BY_DEVICETYPEID, method = RequestMethod.POST)
	public Result<List<Map<String, Object>>> getDataPointListByDeviceTypeId(@RequestBody String queryParams) {
		return alarmRuleRestQueryService.findDataPointByDeviceTypeId(queryParams);
	}



	@CrossOrigin(value = "*")
	@RequestMapping(value = UrlConstant.ALARM_RULE_QUERY, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getAlarmRuleList(@RequestBody String params) throws Exception {
		return alarmRuleRestQueryService.findAlarmRuleListByNameAndDeviceTypeId(params);
	}



	@CrossOrigin(value = "*")
	@RequestMapping(value = UrlConstant.ALARM_RULE_FIND_BY_ID, method = RequestMethod.POST)
	public Result<AlarmRule> findByRuleId(@PathVariable String id) {
		return alarmRuleRestQueryService.findByRuleId(id);
	}
}
