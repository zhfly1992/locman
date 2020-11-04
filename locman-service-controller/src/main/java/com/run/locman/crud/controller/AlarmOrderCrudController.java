/*
 * File name: AlarmOrderCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年12月5日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.AlarmOrderRestCrudService;

/**
 * 
 * @Description: 告警工单查询
 * @author: lkc
 * @version: 1.0, 2017年12月5日
 */
@RestController
@RequestMapping(UrlConstant.ALARM_ORDER)
public class AlarmOrderCrudController {
	@Autowired
	private AlarmOrderRestCrudService alarmOrderCrudService;



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ALARM_ORDER_ACCEPT)
	public Result<String> acceptAlarmOrder(@RequestBody String param) {
		return alarmOrderCrudService.acceptAlarmOrder(param);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ALARM_ORDER_SAVE)
	public Result<Boolean> saveAlarmOrder(@RequestBody String param) {
		return alarmOrderCrudService.saveAlarmOrder(param);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_COMPLETE, method = RequestMethod.POST)
	public Result<Boolean> orderComplete(@RequestBody String queryParams) {
		return alarmOrderCrudService.orderComplete(queryParams);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_POWERLESS, method = RequestMethod.POST)
	public Result<Boolean> orderPowerless(@RequestBody String queryParams) {
		return alarmOrderCrudService.orderPowerless(queryParams);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_APPROVE, method = RequestMethod.POST)
	public Result<String> orderApprove(@RequestBody String queryParams) {
		return alarmOrderCrudService.orderApprove(queryParams);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_REJECT, method = RequestMethod.POST)
	public Result<Boolean> orderReject(@RequestBody String queryParams) {
		return alarmOrderCrudService.orderReject(queryParams);
	}



	@PostMapping(value = UrlConstant.ALARM_ORDER_EXCEL)
	@CrossOrigin(origins = "*")
	public ModelAndView exportAllAlarmOrder(@RequestBody JSONObject accessSecretJson, ModelMap model) {
		return alarmOrderCrudService.exportAllAlarmOrder(accessSecretJson, model);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ADD_PRESENT_PIC)
	public Result<String> addPresentPicAlarmOrder(@RequestHeader("id") String id,
			@RequestParam("file") MultipartFile file) {
		return alarmOrderCrudService.addPresentPic(id, file);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ADD_APP_PIC)
	public Result<String> addAppPicAlarmOrder(@RequestBody String params) {
		return alarmOrderCrudService.addAppPic(params);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ALARM_ORDER_APPROVAL_BEFORE_END)
	public Result<Boolean> approvalBeforeEnd(@RequestBody JSONObject jsonObject) {
		return alarmOrderCrudService.approveBeforeEnd(jsonObject);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ALARM_ORDER_REJECT_BEFORE_END)
	public Result<Boolean> rejectBeforeEnd(@RequestBody JSONObject jsonObject) {
		return alarmOrderCrudService.rejectBeforeEnd(jsonObject);
	}
}
