package com.locman.app.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.locman.app.crud.service.AlarmOrderAppCrudService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("alarmOrderApp")
public class AlarmOrderRestCrudController {

	@Autowired
	AlarmOrderAppCrudService alarmOrderAppCrudService;



	@CrossOrigin("*")
	@PostMapping("saveAlarmOrder")
	public Result<String> saveAlarmOrder(@RequestBody String param) {
		return alarmOrderAppCrudService.saveAlarmOrder(param);
	}



	@CrossOrigin("*")
	@PostMapping("alarmOrderHandle")
	public Result<String> alarmOrderHandle(@RequestBody String param) {
		return alarmOrderAppCrudService.updateAlarmOrderState(param);
	}



	@CrossOrigin("*")
	@PostMapping("acceptOder")
	public Result<String> acceptOrder(@RequestBody String param) {
		return alarmOrderAppCrudService.acceptOrder(param);
	}



	@CrossOrigin("*")
	@PostMapping("uploadAlarmOrderPic/{ordId}")
	public Result<String> uploadAlarmOrderPic(@PathVariable String ordId,
			@RequestParam("files") MultipartFile[] files) {
		return alarmOrderAppCrudService.uploadAlarmOrderPic(ordId, files);
	}



	@CrossOrigin("*")
	@PostMapping("approvalAlarmOrder/{orderId}")
	public Result<Boolean> approvalAlarmOrder(@PathVariable String orderId, @RequestBody String param) {
		return alarmOrderAppCrudService.approvalAlarmOrder(orderId, param);
	}



	@CrossOrigin("*")
	@PostMapping("dredgeAlarmOrder/{orderId}")
	public Result<?> updateFacilitiesDenfenseState(@PathVariable String orderId, @RequestBody String param) {
		return alarmOrderAppCrudService.updateFacilitiesDenfenseState(orderId, param);
	}
}
