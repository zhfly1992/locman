/*
* File name: FacilitiesRestQueryController.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			田明		2017年08月08日
* ...			...			...
*
***************************************************/
package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.FacilitiesRestQueryService;

/**
 * @Description: 在地图查询设施
 * @author: 田明
 * @version: 1.0, 2017年08月08日
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(UrlConstant.FACILITES)
public class FacilitiesRestQueryController {

	@Autowired
	private FacilitiesRestQueryService facilitiesRestQueryService;

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.FACILITIES_GIS_MAP, method = RequestMethod.POST)
	public Result<Map<String, Object>> queryMapFacilities(@RequestBody String queryParams) {
		return facilitiesRestQueryService.queryMapFacilities(queryParams);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/updateKeyTime/{accessSecret}", method = RequestMethod.GET)
	public Result<String> updateKeyTime(@PathVariable("accessSecret") String accessSecret) {
		return facilitiesRestQueryService.updateKeyTime(accessSecret);
	}

	/**
	 * @Description: 根绝id查询设施状态
	 */

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.QUERY_FACILITY_MANAGESTATE_BY_IDS, method = RequestMethod.POST)
	public Result<Boolean> queryFacilityManageStateByIds(@RequestBody String idlist) {
		return facilitiesRestQueryService.getFacilityMangerStateById(idlist);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.COUNT_FACILITES_BY_TYPE_AND_ORGAN, method = RequestMethod.POST)
	public Result<Map<String, Object>> countFacByTypeAndOrg(@RequestBody JSONObject jsonObject){
		return facilitiesRestQueryService.countFacByTypeAndOrg(jsonObject);
	}
	
	@CrossOrigin(origins = "*")
	@GetMapping(value = UrlConstant.COUNT_FACNUM_BY_STREET)
	public Result<List<Map<String, Object>>> countFacByTypeAndOrg(@PathVariable("accessSecret") String accessSecret){
		return facilitiesRestQueryService.countFacNumByStreet(accessSecret);
	}
	
	
	@GetMapping(value = UrlConstant.FIND_FACILITY_LNG_LAT)
	public Result<List<Map<String, Object>>> findFacilityLngLat(@PathVariable("accessSecret") String accessSecret){
		return facilitiesRestQueryService.findFacilityLngLat(accessSecret);
	}
	
	
	@GetMapping(value = UrlConstant.FACILITY_LNG_LAT_LIST)
	public Result<List<List<String>>> facilityLngLatList(@PathVariable("accessSecret") String accessSecret){
		return facilitiesRestQueryService.facilityLngLatList(accessSecret);
	}
}
