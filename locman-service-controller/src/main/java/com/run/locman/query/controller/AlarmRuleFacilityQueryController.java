/*
* File name: AlarmRuleFacilityQueryController.java								
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
package com.run.locman.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.AlarmRuleFacilityRestQueryService;

/**
 * @Description: 自定义告警规则配置设施与设备信息查询服务
 * @author: 田明
 * @version: 1.0, 2017年11月20日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/regulation")
public class AlarmRuleFacilityQueryController {
	@Autowired
	private AlarmRuleFacilityRestQueryService alarmRuleFacilityRestQueryService;


	// 此接口没有被调用过	2018年3月13日16:06:41
	/*	@RequestMapping(value = "/getAlarmByConditions", method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getAlarmRuleDeviceByDeviceAll(@RequestBody String alarmstate) {

		return alarmRuleFacilityRestQueryService.getAlarmRuleDeviceBindingState(alarmstate);

	}*/



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.FACILITIES_DEVICE_LIST, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getFacilityDeviceList(@RequestBody String queryParams) {
		return alarmRuleFacilityRestQueryService.getFacilityDeviceList(queryParams);
	}

}
