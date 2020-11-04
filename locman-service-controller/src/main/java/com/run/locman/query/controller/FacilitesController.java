/*
 * File name: FacilitesController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 wangsheng 2017年11月20日 ...
 * ... ...
 *
 ***************************************************/
package com.run.locman.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.FacilitesQueryService;

/**
 * @Description: 设施查询
 * @author: wangsheng
 * @version: 1.0, 2017年11月20日
 */
@RestController
@RequestMapping(UrlConstant.FACILITES)
@CrossOrigin("*")
public class FacilitesController {
	@Autowired
	private FacilitesQueryService facilitesService;



	@CrossOrigin("*")
	@RequestMapping(value = { UrlConstant.FACILITES_BY_PAGE }, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> facilitesSerchByPage(@RequestBody String pageInfo) {
		return facilitesService.facilitesSerchByPage(pageInfo);
	}



	@CrossOrigin("*")
	@RequestMapping(value = { UrlConstant.FACILITES_BY_FAC_ID }, method = RequestMethod.POST)
	public Result<Map<String, Object>> facilitesSerchById(@RequestBody String idInfo) {
		return facilitesService.facilitesSerchById(idInfo);
	}



	@CrossOrigin("*")
	@RequestMapping(value = { UrlConstant.GET_FACILITIES_INFO }, method = RequestMethod.POST)
	public Result<Map<String, Object>> getFacilitesInfo(@RequestBody String info) {
		return facilitesService.getFacilitesInfo(info);
	}



	@CrossOrigin("*")
	@RequestMapping(value = { UrlConstant.FACILITES_BY_FAC_CODE }, method = RequestMethod.POST)
	public Result<Map<String, Object>> facilitesSerchByCode(@RequestBody String codeInfo) {
		return facilitesService.facilitesSerchByCode(codeInfo);
	}



	@CrossOrigin("*")
	@RequestMapping(value = { UrlConstant.FACILITIES_CHECK_FACILITIES_CODE }, method = RequestMethod.POST)
	public Result<Boolean> checkFacilitiesCode(@RequestBody String queryParams) {
		return facilitesService.checkFacilitiesCode(queryParams);
	}



	@CrossOrigin("*")
	@PostMapping(value = UrlConstant.EXPORT_FACILITES_INFO)
	public ModelAndView exportFacilitesInfo(@RequestBody String pageInfo, ModelMap model) {
		return facilitesService.exportFacilitesInfo(pageInfo, model);

	}
	
	
	
	@CrossOrigin("*")
	@PostMapping(value = { UrlConstant.COUNT_ALARM_FACILITIES })
	public Result<Integer> countAlarmFacilities(@RequestBody String pageInfo) {
		return facilitesService.countAlarmFacilities(pageInfo);
	}
	
	
	
	@CrossOrigin("*")
	@GetMapping(value = UrlConstant.FACILITY_BY_CODE_ACC)
	public Result<Map<String, Object>> facilitesSerchByCode(@PathVariable(value = "accessSecret") String accessSecret, @PathVariable(value = "facilitiesCode") String facilitiesCode) {
		return facilitesService.findFacilityInfoByCodeAndAcc(facilitiesCode, accessSecret);
	}
	
	
	
	@RequestMapping(value = { UrlConstant.FIND_FACILITY_RENOVATED_BY_PAGE }, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> findFacilityRenovatedByPage(@RequestBody String pageInfo) {
		return facilitesService.findFacilityRenovatedByPage(pageInfo);
	}

}
