/*
 * File name: FacilitiesTypeBaseRestQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年8月31日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.FacilitiesTypeBase;
import com.run.locman.query.service.FacilitiesTypeBaseRestQueryService;

/**
 * @Description: 查询基础设施类型
 * @author: qulong
 * @version: 1.0, 2017年8月31日
 */
@RestController
@RequestMapping(value = "facilitiesTypeBase")
public class FacilitiesTypeBaseRestQueryController {

	@Autowired
	private FacilitiesTypeBaseRestQueryService facilitiesTypeBaseRestQueryService;



	@CrossOrigin(value = "*")
	@PostMapping(value = "getById")
	public Result<FacilitiesTypeBase> getById(@RequestBody String params) {
		return facilitiesTypeBaseRestQueryService.getById(params);
	}



	@CrossOrigin(value = "*")
	@PostMapping(value = "getFacilitiesTypeBasePage")
	public Result<PageInfo<FacilitiesTypeBase>> getFacilitiesTypeBasePage(@RequestBody String params) {
		return facilitiesTypeBaseRestQueryService.getFacilitiesTypeBasePage(params);
	}



	@CrossOrigin(value = "*")
	@PostMapping(value = "getAllFacilitiesTypeBase")
	public Result<List<Map<String, Object>>> getAllFacilitiesTypeBase() {
		return facilitiesTypeBaseRestQueryService.getAllFacilitiesTypeBase();
	}
}
