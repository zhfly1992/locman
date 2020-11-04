/*
 * File name: FactoryRestCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年11月20日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.FactoryRestCrudService;

/**
 * @Description: 厂家管理
 * @author: 田明
 * @version: 1.0, 2017年11月20日
 */
@RestController
@RequestMapping(UrlConstant.FACTORY_BASE_PATH)
@CrossOrigin(origins = "*")
public class FactoryRestCrudController {

	@Autowired
	private FactoryRestCrudService factoryRestCrudService;


	@CrossOrigin(origins = "*")
	@PostMapping(UrlConstant.NEW_FACTORY)
	public Result<String> addFactory(@PathVariable String accessSecret, @RequestBody String addParams,@RequestAttribute(required=false , name = "userId") String userId) {
		return factoryRestCrudService.addFactory(accessSecret, addParams);
	}
	
	@CrossOrigin(origins = "*")
	@PostMapping(UrlConstant.SYNCHRO_DATA)
	public Result<String> synchroData(@RequestBody String appTagParam) {
		return factoryRestCrudService.synchroData(appTagParam);
	}
	
	@PostMapping(UrlConstant.FACTORY_NEW_UPDATE)
	public Result<String> updateFactory(@PathVariable String id, @RequestBody String updateParams) {
		return factoryRestCrudService.updateFactory(id, updateParams);
	}
	
	@CrossOrigin(origins = "*")
	@PostMapping(UrlConstant.FACTORY_UPDATE_STATE)
	public Result<String> updateState(@PathVariable String id, @RequestBody String updateParams) {
		return factoryRestCrudService.updateState(id, updateParams);
	}
}
