package com.locman.app.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.crud.service.UscCrudService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("userCrud")
@SuppressWarnings("rawtypes")
public class UscRestCrudController {
	@Autowired
	UscCrudService uscCrudService;



	@CrossOrigin("*")
	@PostMapping("/restPassword")
	public Result restPassword(@RequestBody String param) throws Exception {
		return uscCrudService.appRestPassword(param);
	}



	@CrossOrigin("*")
	@PostMapping("/changePassword")
	public Result changePassword(@RequestBody String param) throws Exception {
		return uscCrudService.appChangePassword(param);
	}



	@CrossOrigin("*")
	@PostMapping("/loginOut")
	public Result loginOut() {
		return uscCrudService.appLoginOut();
	}
}
