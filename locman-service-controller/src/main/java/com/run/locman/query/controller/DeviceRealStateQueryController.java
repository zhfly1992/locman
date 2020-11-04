/*
 * File name: DeviceRealStateQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年11月7日 ... ...
 * ...
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Pagination;
import com.run.entity.common.Result;
import com.run.locman.api.entity.Pages;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.DeviceRealStateQueryService;

/**
 * @Description: 设备状态查询
 * @author: qulong
 * @version: 1.0, 2017年11月7日
 */

@RestController
@RequestMapping(value = UrlConstant.DEVICE_REAL_STATE)
@CrossOrigin(origins = "*")
public class DeviceRealStateQueryController {

	@Autowired
	private DeviceRealStateQueryService deviceRealStateQueryService;



	@CrossOrigin(origins = "*")
	@GetMapping(value = UrlConstant.GET_REAL_STATE)
	public Result<Map<String, Object>> getRealState(@PathVariable String accessSecret, @PathVariable String deviceId) {
		return deviceRealStateQueryService.getRealState(accessSecret, deviceId);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.GET_HISTORY_STATE)
	public Result<Pagination<Map<String, Object>>> getHistoryStateList(@RequestBody String params) {
		return deviceRealStateQueryService.getHistoryStateList(params);
	}



	@PostMapping(value = UrlConstant.EXPORT_HISTORY_STATE)
	public ModelAndView exportAllAlarmOrder(@RequestBody String params, ModelMap model) {
		return deviceRealStateQueryService.exportHistoryStateList(params, model);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.GET_COUNT_DEVICE_REAL_STATE)
	public Result<PageInfo<Map<String, Object>>> getCountDeviceRealState(@RequestBody String params) {
		return deviceRealStateQueryService.getCountDeviceRealState(params);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.EXPORT_COUNT_DEVICE_REAL_STATE)
	public ModelAndView exportCountDeviceRealState(@RequestBody String params, ModelMap model) {
		return deviceRealStateQueryService.exportCountDeviceRealState(params, model);
	}
	
	@CrossOrigin
	@PostMapping(value=UrlConstant.COUNT_DEVICE_TIMING_TRIGGER)
	public Result<Pages<Map<String, Object>>> countDeviceTimingTrigger(@RequestBody String params){
		return deviceRealStateQueryService.countDeviceTimingTrigger(params);
		
	}
	
}
