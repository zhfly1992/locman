package com.locman.app.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.query.service.UscQueryService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("user")
public class UscRestQueryController {

	@Autowired
	UscQueryService userQueryService;



	@CrossOrigin("*")
	@PostMapping("/login")
	public Result<?> login(@RequestBody String param) throws Exception {
		return userQueryService.uscAppLogin(param);
	}



	@CrossOrigin("*")
	@PostMapping("/sendMessage")
	public Result<?> sendMessage(@RequestBody String param) throws Exception {
		return userQueryService.appSendMassage(param);
	}

}