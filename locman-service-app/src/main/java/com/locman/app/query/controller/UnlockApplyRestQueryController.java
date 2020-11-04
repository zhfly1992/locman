package com.locman.app.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.query.service.UnlockApplyQueryService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("unlockApply")
public class UnlockApplyRestQueryController {

	@Autowired
	private UnlockApplyQueryService unlockApplyQueryService;



	@CrossOrigin("*")
	@GetMapping("/getDeviceOnlineState/{deviceId}")
	public Result<String> getDeviceOnlineState(@PathVariable String deviceId) {
		return unlockApplyQueryService.getDeviceOnlineState(deviceId);
	}



	@CrossOrigin("*")
	@PostMapping("/checkUnlockOrder/{orderId}")
	public Result<Boolean> checkUnlockOrder(@PathVariable String orderId, @RequestBody String param) {
		return unlockApplyQueryService.checkUnlockOrder(orderId, param);
	}



	@CrossOrigin("*")
	@PostMapping("/getBalanceState")
	public Result<?> getBalanceState(@RequestBody String param) {
		return unlockApplyQueryService.getBalanceState(param);
	}
}
