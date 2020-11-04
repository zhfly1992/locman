/*
 * File name: FacilitiesTypeRestQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年08月09日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.FacilitiesType;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.FacilitiesTypeRestQueryService;

/**
 * @Description: 设施类型查询
 * @author: 田明
 * @version: 1.0, 2017年08月09日
 */
@RestController
@RequestMapping(UrlConstant.FACILITIES_TYPE_BASE_PATH)
@CrossOrigin(origins = "*")
public class FacilitiesTypeRestQueryController {

	@Autowired
	private FacilitiesTypeRestQueryService facilitiesTypeRestQueryService;



	@RequestMapping(value = UrlConstant.FACILITIES_TYPE_LIST, method = RequestMethod.POST)
	public Result<List<FacilitiesType>> queryAllFacilitiesType(@RequestBody String params) {
		return facilitiesTypeRestQueryService.queryAllFacilitiesType(params);
	}
	
	
	
	@GetMapping(value = UrlConstant.FACILITIES_TYPE_ALL_BY_ACCESS)
	public Result<List<Map<String,String>>> findAllFacilitiesTypeAndNum(@PathVariable(value = "accessSecret") String accessSecret) {
		return facilitiesTypeRestQueryService.findAllFacilitiesTypeAndNum(accessSecret);
	}



	@PostMapping(value = "/getById")
	public Result<FacilitiesType> queryFacilitiesTypeById(@RequestBody String params) {
		return facilitiesTypeRestQueryService.getById(params);
	}




	@PostMapping(value = "/getFacilitiesPage")
	public Result<PageInfo<FacilitiesType>> getFacilitiesPage(@RequestBody String param) {
		return facilitiesTypeRestQueryService.getFacilitiesPage(param);
	}



	@PostMapping(value = UrlConstant.CHECK_FACILITIES_NAME)
	public Result<Boolean> checkFacilitiesTypeName(@RequestBody String param) {
		return facilitiesTypeRestQueryService.checkFacilitiesTypeName(param);
	}
	
	
	
	@GetMapping(value = UrlConstant.FIND_ALL_FACTYPE_AND_DEVICETYPE_NUM)
	public Result<JSONObject> findAllFacTypeAndDeviceTypeNum(@PathVariable(value = "accessSecret") String accessSecret) {
		return facilitiesTypeRestQueryService.findAllFacTypeAndDeviceTypeNum(accessSecret);
	}
}
