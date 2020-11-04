package com.locman.app.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.query.service.DeviceAppQueryService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("facilitiesApp")
public class FacilityDeviceRestQueryController {

	@Autowired
	DeviceAppQueryService deviceAppQueryService;



	@CrossOrigin("*")
	@PostMapping("/getFacilities/{facilityId}/{accessId}")
	public Result<Map<String, Object>> getFacilityDeviceByFacId(@PathVariable String facilityId,
			@PathVariable String accessId, @RequestBody String param) {
		return deviceAppQueryService.queryFacilityById(facilityId, accessId, param);
	}



	@CrossOrigin("*")
	@PostMapping("/findFacilitiesInfoByCode")
	public Result<List<Map<String, Object>>> findFacilitiesInfoByCode(@RequestBody String param) {
		return deviceAppQueryService.findFacilitiesInfoByCode(param);
	}



	@CrossOrigin("*")
	@PostMapping("/findFacilitiesExtend/{id}")
	public Result<?> findFacilitiesExtend(@PathVariable String id) {
		return deviceAppQueryService.findFacilitiesExtend(id);
	}
}
