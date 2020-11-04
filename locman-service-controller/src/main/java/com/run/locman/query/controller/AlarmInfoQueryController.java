/*
 * File name: AlarmInfoQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年10月31日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.Pages;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.AlarmInfoRestQueryService;

/**
 * @Description: 告警信息查询
 * @author: lkc
 * @version: 1.0, 2017年10月31日
 */
@RestController
@RequestMapping(UrlConstant.STATE_INFO)
public class AlarmInfoQueryController {
	@Autowired
	private AlarmInfoRestQueryService alarmInfoRestQueryService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.STATE_INFO_PAGE, method = RequestMethod.POST)
	public Result<Pages<Map<String, Object>>> getAlarmInfoByPage(@RequestBody String alarmInfo) {
		return alarmInfoRestQueryService.getAlarmInfoByPage(alarmInfo);
	}



	@CrossOrigin(value = "*")
	@RequestMapping(value = UrlConstant.STATE_INFO_BY_FAC_ID, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getAlarmInfoByfacId(@RequestBody String alarmInfo) {
		return alarmInfoRestQueryService.getAlarmInfoByfacId(alarmInfo);
	}



	@CrossOrigin(value = "*")
	@PostMapping(value = "/getNearlyAlarmInfo")
	public Result<List<Map<String, Object>>> getNearlyAlarmInfo(@RequestBody String params) {
		return alarmInfoRestQueryService.getNearlyAlarmInfo(params);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETALARMINFOLIST, method = RequestMethod.POST)
	public Result<Pages<Map<String, Object>>> getAlarmInfoList(@RequestBody String alarmInfo) {
		return alarmInfoRestQueryService.getAlarmInfoList(alarmInfo);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.STATISTICS_ALARM_INFO, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> statisticsAlarmInfo(@RequestBody String params) {
		return alarmInfoRestQueryService.statisticsAlarmInfo(params);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.EXPORT_ALARM_INFO, method = RequestMethod.POST)
	public ModelAndView exportAlarmInfo(@RequestBody String params, ModelMap model) {
		return alarmInfoRestQueryService.exportAlarmInfo(params, model);
	}



	@CrossOrigin(value = "*")
	@PostMapping(value = UrlConstant.ALARM_INFO_ROLL)
	public Result<List<Map<String, Object>>> alarmInfoRoll(@RequestBody String params) {
		return alarmInfoRestQueryService.alarmInfoRoll(params);
	}



	@CrossOrigin(value = "*")
	@GetMapping(value = UrlConstant.FIND_ALARM_DEVICE_DATA)
	public Result<JSONObject> findAlarmDeviceData(@PathVariable String alarmInfoId) {
		return alarmInfoRestQueryService.findAlarmDeviceData(alarmInfoId);
	}

}
