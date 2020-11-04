package com.locman.app.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.crud.service.FacilityDeviceAppCrudService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("facilitiesApp")
public class FacilityDeviceRestCrudController {

	@Autowired
	FacilityDeviceAppCrudService facilityDeviceAppCrudService;



	@CrossOrigin("*")
	@PostMapping("updateFacilities/{id}")
	public Result<String> updateFacilitiesExtension(@RequestBody String reqParam, @PathVariable String id) {
		return facilityDeviceAppCrudService.updateFacilitiesExtension(reqParam, id);
	}

}
