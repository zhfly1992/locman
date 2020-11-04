package com.locman.app.query.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.locman.app.entity.vo.FacilitiesTypeVo;
import com.locman.app.entity.vo.FacilitiesVo;
import com.locman.app.query.service.FacilitiesTypeAppQueryService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("facilitiesTypeApp")
public class FacilitiesTypeRestQueryController {
	@Autowired
	FacilitiesTypeAppQueryService facilitiesTypeAppQueryService;



	@CrossOrigin("*")
	@GetMapping("/getFacilitiesTypes/{accessId}")
	public Result<List<FacilitiesTypeVo>> getFacilitiesTypes(@PathVariable String accessId) {
		return facilitiesTypeAppQueryService.findFacilitiesTypes(accessId);
	}



	@CrossOrigin("*")
	@PostMapping("/mapFacilities")
	public Result<List<FacilitiesVo>> getFacilitiesToMap(@RequestBody String param) {
		return facilitiesTypeAppQueryService.findMapFacilities(param);
	}

}
