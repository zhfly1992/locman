package com.locman.app.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.crud.service.UnlockAppCrudService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("unlockApply")
public class UnlockApplyRestCrudController {

	@Autowired
	private UnlockAppCrudService unlockAppCrudService;



	@CrossOrigin("*")
	@PostMapping(value = "/controlByOrder/{orderId}")
	public Result<String> contorlByOrder(@RequestBody String param, @PathVariable String orderId) {
		return unlockAppCrudService.contorlByOrder(param, orderId);
	}



	@CrossOrigin("*")
	@PostMapping(value = "/saveRemoteControlRecord")
	public Result<String> saveRemoteControlRecord(@RequestBody String param) {
		return unlockAppCrudService.saveRemoteControlRecord(param);
	}



	@CrossOrigin("*")
	@PostMapping("/inspectionUnlock")
	public Result<String> inspectionContor(@RequestBody String param) {
		return unlockAppCrudService.inspectionContor(param);
	}



	@CrossOrigin("*")
	@PostMapping("/openBalance")
	public Result<?> openBalance(@RequestBody String param) {
		return unlockAppCrudService.openBalance(param);
	}



	@CrossOrigin("*")
	@PostMapping("/alarmOrderUnlock")
	public Result<?> alarmOrderUnlock(@RequestBody String param) {
		return unlockAppCrudService.alarmOrderUnlock(param);
	}
}
