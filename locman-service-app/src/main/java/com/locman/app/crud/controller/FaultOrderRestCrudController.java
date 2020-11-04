package com.locman.app.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.crud.service.FaultOrderAppCrudService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("faultOrder")
public class FaultOrderRestCrudController {

	@Autowired
	private FaultOrderAppCrudService faultOrderAppCrudService;



	@CrossOrigin("*")
	@PostMapping("saveFaultOrder")
	public Result<String> saveFaultOrder(@RequestBody String param) {
		return faultOrderAppCrudService.saveOrder(param);
	}



	@CrossOrigin("*")
	@PostMapping("faultOrderHandle")
	public Result<String> faultOrderHandle(@RequestBody String param) {
		return faultOrderAppCrudService.faultOrderHandle(param);
	}
}
