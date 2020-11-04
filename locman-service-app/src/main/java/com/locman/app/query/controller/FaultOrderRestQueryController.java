package com.locman.app.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.query.service.FaultOrderAppQueryService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("faultOrder")
public class FaultOrderRestQueryController {

	@Autowired
	private FaultOrderAppQueryService faultOrderAppQueryService;



	@SuppressWarnings("rawtypes")
	@CrossOrigin("*")
	@PostMapping("faultOrderList")
	public Result faultOrderList(@RequestBody String param) {
		return faultOrderAppQueryService.findFaultOrderList(param);
	}

	
	
	@SuppressWarnings("rawtypes")
	@CrossOrigin("*")
	@RequestMapping("faultOrderDetail")
	public Result findOrderById(@RequestBody String param) {
		return faultOrderAppQueryService.findOrderById(param);
	}
}
