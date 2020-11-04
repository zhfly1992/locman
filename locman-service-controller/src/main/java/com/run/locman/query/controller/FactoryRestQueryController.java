/*
 * File name: FactoryRestQueryController.java
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
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.FactoryRestQueryService;

/**
 * @Description: 厂家查询
 * @author: 田明
 * @version: 1.0, 2017年11月20日
 */
@RestController
@RequestMapping(UrlConstant.FACTORY_BASE_PATH)
@CrossOrigin(origins = "*")
public class FactoryRestQueryController {

	@Autowired
	private FactoryRestQueryService factoryRestQueryService;


	@PostMapping(UrlConstant.NEW_FACTORY_LIST)
	public Result<PageInfo<Map<String, Object>>> getNewFactoryList(@PathVariable String accessSecret,
			@RequestBody String queryParams) {
		return factoryRestQueryService.getNewFactoryList(accessSecret, queryParams);
	}


	@PostMapping(UrlConstant.CHECK_APPTAG_EXIST)
	public Result<Boolean> checkAppTagExist(@RequestBody String queryParams) {
		return factoryRestQueryService.checkAppTagExist(queryParams);
	}


	@GetMapping(UrlConstant.FIND_FACTORY_BY_ID)
	public Result<Map<String, Object>> findFactoryById(@PathVariable String id) {
		return factoryRestQueryService.findFactoryById(id);
	}



	@CrossOrigin(origins = "*")
	@GetMapping(UrlConstant.QUERY_FACTORY_NAME_LIST)
	public Result<List<Map<String, String>>> queryFactoryNameList(@PathVariable String accessSecret) {
		return factoryRestQueryService.queryFactoryNameList(accessSecret);
	}



	@CrossOrigin(origins = "*")
	@GetMapping(UrlConstant.QUERY_FACTORY_BOX)
	public Result<List<Map<String, String>>> downBoxFactory(@PathVariable String accessSecret) {
		return factoryRestQueryService.downBoxFactory(accessSecret);
	}
}
