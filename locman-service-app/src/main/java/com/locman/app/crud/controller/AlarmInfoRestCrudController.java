package com.locman.app.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.crud.service.AlarmInfoAppCrudService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("alarmInfoApp")
public class AlarmInfoRestCrudController {

	@Autowired
	AlarmInfoAppCrudService alarmInfoCrudService;



	@CrossOrigin("*")
	@PostMapping("updateAlarmDel")
	public Result<String> updateAlarmDel(@RequestBody String param) {
		return alarmInfoCrudService.updateAlarmDel(param);
	}
}
