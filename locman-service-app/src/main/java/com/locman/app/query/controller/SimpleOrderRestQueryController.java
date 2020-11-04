package com.locman.app.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.query.service.SimpleOrderAppQueryService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("simpleOrderApp")
@SuppressWarnings("rawtypes")
public class SimpleOrderRestQueryController {

	@Autowired
	SimpleOrderAppQueryService simpleOrderAppQueryService;



	@CrossOrigin("*")
	@PostMapping("getSimpleOrderList")
	public Result getSimpleOrderList(@RequestBody String param) {
		return simpleOrderAppQueryService.getSimpleOrderList(param);
	}



	@CrossOrigin("*")
	@PostMapping("simpleOrderDetail")
	public Result simpleOrderDetail(@RequestBody String param) {
		return simpleOrderAppQueryService.getSimpleOrderById(param);
	}



	@CrossOrigin("*")
	@PostMapping("getFacDevices")
	public Result getFacilitiesAndDevice(@RequestBody String param) {
		return simpleOrderAppQueryService.getFacitiesAll(param);
	}



	@CrossOrigin("*")
	@PostMapping("/getReviewedUserPhone")
	public Result<List<Map<String, Object>>> getReviewedUserPhone(@RequestBody String param) {
		return simpleOrderAppQueryService.getReviewedUserPhone(param);
	}
}
