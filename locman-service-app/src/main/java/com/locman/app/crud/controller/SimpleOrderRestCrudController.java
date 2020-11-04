package com.locman.app.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.crud.service.SimpleOrderAppCrudService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("simpleOrderApp")
public class SimpleOrderRestCrudController {

	@Autowired
	SimpleOrderAppCrudService simplOrderAppCrudService;



	@CrossOrigin("*")
	@PostMapping("saveSimpleOrder")
	public Result<String> saveSimpleOrder(@RequestBody String param) {
		return simplOrderAppCrudService.saveSimpleOrder(param);
	}



	@CrossOrigin("*")
	@PostMapping("simpleOrderHandle")
	public Result<String> simpleOrderHandle(@RequestBody String param) {
		return simplOrderAppCrudService.simpleOrderHandle(param);
	}



	@CrossOrigin("*")
	@PostMapping("/delayedSimpleOrder")
	public Result<String> delayedSimpleOrder(@RequestBody String param) {
		return simplOrderAppCrudService.delayedSimpleOrder(param);
	}
}
