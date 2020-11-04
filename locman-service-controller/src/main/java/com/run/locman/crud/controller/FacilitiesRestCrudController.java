/*
 * File name: FacilitiesRestCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 李康诚 2017年11月20日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.locman.api.entity.Facilities;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.FacilitiesRestCrudService;

/**
 * @Description: 设施添加,修改.设施管理
 * @author: 李康诚
 * @version: 1.0, 2017年11月20日
 */
@RestController
@RequestMapping(UrlConstant.FACILITES)
public class FacilitiesRestCrudController {

	@Autowired
	private FacilitiesRestCrudService facilitiesRestCrudService;



	@CrossOrigin("*")
	@PostMapping(UrlConstant.FACILITIES_ADD)
	public Result<String> addFacilities(@RequestBody String addParams) {
		return facilitiesRestCrudService.addFacilities(addParams);
	}



	@CrossOrigin("*")
	@PutMapping(value = UrlConstant.FACILITIES_UPDATE)
	public Result<Facilities> updateFacilities(@RequestBody String updateParams) {
		return facilitiesRestCrudService.updateFacilities(updateParams);
	}



	@CrossOrigin("*")
	@PostMapping(value = "/bind")
	//疑似没调
	public Result<String> bindFacilities(@RequestBody String bindFacilities) {
		return facilitiesRestCrudService.bindFacilities(bindFacilities);
	}



	@CrossOrigin("*")
	@PostMapping(value = UrlConstant.IMPORT_FACILITES_INFO)
	public ModelAndView importExcelFacilities(@RequestParam(FacilitiesContants.FILE_FILE) MultipartFile file,
			@RequestParam(FacilitiesContants.FILE_GATEWAY) MultipartFile fileGateway,
			@RequestParam(FacilitiesContants.AREA_ID) String areaId,
			@RequestParam(FacilitiesContants.ORGANIZATION_ID) String organizationId,
			@RequestParam(FacilitiesContants.USC_ACCESS_SECRET) String accessSecret,
			@RequestParam(FacilitiesContants.USER_ID) String userId, ModelMap model) {
		return facilitiesRestCrudService.importExcelFacilities(file, fileGateway, areaId, organizationId, accessSecret,
				userId, model);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.EXPORT_FACILITES_TEMPLATE)
	public ModelAndView exportFacilitiesTemplate(ModelMap model) {
		return facilitiesRestCrudService.exportFacilitiesTemplate(model);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.EXPORT_GATEWAY_TEMPLATE)
	public ModelAndView exportGateWayTemplate(ModelMap model) {
		return facilitiesRestCrudService.exportGateWayTemplate(model);
	}
	
	
	
	@CrossOrigin("*")
	@PostMapping(value = UrlConstant.SYNCH_FACILITES_INFO)
	//疑似没调
	public Result<String> synchFacilities(@RequestBody String facilityParam) {
		return facilitiesRestCrudService.synchFacilities(facilityParam);
	}
	@CrossOrigin("*")
	@PostMapping(value = UrlConstant.UPDATE_FACILITIES_DENFENSE_STATE)
	public Result<Integer> updateFacilitiesDenfenseState(@RequestBody JSONObject jsonObject){
		return facilitiesRestCrudService.updateFacilitiesDenfenseState(jsonObject);
	}
	
	@CrossOrigin("*")
	@PostMapping(value = UrlConstant.EXAMINE_RENOVATION_FACILITY)
	public Result<String> examineRenovationFacility(@RequestBody JSONObject jsonObject){
		return facilitiesRestCrudService.examineRenovationFacility(jsonObject);
	}

	@CrossOrigin("*")
	@PostMapping(value = UrlConstant.RENOVATION_FACILITY_2_FAULTORDER)
	public Result<String> renovationFacility2FaultOrder(@RequestBody JSONObject jsonObject){
		return facilitiesRestCrudService.renovationFacility2FaultOrder(jsonObject);
	}
	
}
