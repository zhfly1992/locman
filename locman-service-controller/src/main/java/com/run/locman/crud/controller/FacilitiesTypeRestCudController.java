/*
 * File name: FacilitiesRestCudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年8月29日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.api.entity.FacilitiesType;
import com.run.locman.crud.service.FacilitiesTypeRestCudService;

/**
 * @Description: 设施类型修改
 * @author: qulong
 * @version: 1.0, 2017年8月29日
 */
@RestController
@RequestMapping("/facilitiesType")
public class FacilitiesTypeRestCudController {

	@Autowired
	private FacilitiesTypeRestCudService facilitiesTypeRestCudService;



	@CrossOrigin(origins = "*")
	@PostMapping(value = "/changeState")
	public Result<FacilitiesType> changeState(@RequestBody String param) {
		return facilitiesTypeRestCudService.changeState(param);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = "/updateAlias")
	public Result<FacilitiesType> updateFacilityTypeAlias(@RequestBody String param) {
		return facilitiesTypeRestCudService.updateFacilityTypeAlias(param);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = "/saveFacilityType")
	public Result<Map<String, Object>> insertFacilityType(@RequestBody String param) {
		return facilitiesTypeRestCudService.insertFacilityType(param);
	}
}
