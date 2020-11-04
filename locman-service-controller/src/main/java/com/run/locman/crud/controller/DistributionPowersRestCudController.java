/*
 * File name: DistributionPowersRestCudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月15日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.DistributionPowersRestCudService;

/**
 * @Description: 分权分域管理
 * @author: qulong
 * @version: 1.0, 2017年9月15日
 */

@RestController
@RequestMapping(value = UrlConstant.DISTRIBUTIONPOWERS)
public class DistributionPowersRestCudController {

	@Autowired
	private DistributionPowersRestCudService distributionPowersRestCudService;



	@CrossOrigin("*")
	@PostMapping(value = UrlConstant.SAVE_DISTRIBUTIONPOWERS)
	public Result<String> saveDistributionPowers(@RequestBody String params) {
		return distributionPowersRestCudService.saveDistributionPowers(params);
	}



	@CrossOrigin("*")
	@PostMapping(value = UrlConstant.EDIT_DISTRIBUTIONPOWERS)
	public Result<String> editDistributionPowers(@RequestBody String params) {
		return distributionPowersRestCudService.editDistributionPowers(params);
	}



	@CrossOrigin("*")
	@GetMapping(value = UrlConstant.DELETE_DISTRIBUTIONPOWERS)
	public Result<Boolean> deleteDistributionPowers(@PathVariable String id) {
		return distributionPowersRestCudService.deleteDistributionPowers(id);
	}



	@CrossOrigin("*")
	@PostMapping(value = UrlConstant.CHANGE_STATE)
	public Result<String> changeState(@RequestBody String params) {
		return distributionPowersRestCudService.changeState(params);
	}

}
