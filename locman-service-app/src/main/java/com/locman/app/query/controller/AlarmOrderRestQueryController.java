package com.locman.app.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.query.service.AlarmOrderAppQueryService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("alarmOrderApp")
@SuppressWarnings("rawtypes")
public class AlarmOrderRestQueryController {

	@Autowired
	AlarmOrderAppQueryService alarmOrderAppQueryServicel;



	@CrossOrigin("*")
	@PostMapping("alarmOrderList")
	public Result getAlarmOrderList(@RequestBody String param) {
		return alarmOrderAppQueryServicel.getAlarmOrderList(param);
	}



	@CrossOrigin("*")
	@PostMapping("alarmOrderDetail")
	public Result getAlarmOrderDetail(@RequestBody String param) {
		return alarmOrderAppQueryServicel.getAlarmOrderById(param);
	}



	@CrossOrigin("*")
	@PostMapping("getHiddenTroubleType")
	public Result getHiddenTroubleType() {
		return alarmOrderAppQueryServicel.getHiddenTroubleType();
	}
}
