package com.locman.app.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.locman.app.query.service.AlarmInfoAppQueryService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("alarmInfoApp")
public class AlarmInfoRestQueryController {

	@Autowired
	AlarmInfoAppQueryService alarmInfoQueryService;



	@CrossOrigin("*")
	@PostMapping("getAlarmInfoList")
	public Result<JSONObject> getAlarmInfoList(@RequestBody String param) {
		return alarmInfoQueryService.getAlarmInfoList(param);
	}
}
