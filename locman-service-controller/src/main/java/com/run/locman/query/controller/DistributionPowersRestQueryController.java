/*
 * File name: DistributionPowersRestQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月14日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.controller;

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
import com.run.locman.api.entity.DistributionPowers;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.DistributionPowersRestQueryService;

/**
 * @Description: 查询分权分域配置
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */
@RestController
@RequestMapping(value = UrlConstant.DISTRIBUTIONPOWERS)
public class DistributionPowersRestQueryController {

	@Autowired
	private DistributionPowersRestQueryService distributionPowersRestQueryService;



	@CrossOrigin(value = "*")
	@PostMapping(value = UrlConstant.GET_PAGE_BY_ACCESSSCRET)
	public Result<PageInfo<Map<String,Object>>> getDistributionPowersListPage(@RequestBody String params) {
		return distributionPowersRestQueryService.getDistributionPowersListPage(params);
	}



	@CrossOrigin(value = "*")
	@GetMapping(value = UrlConstant.GET_BY_ID)
	public Result<DistributionPowers> getById(@PathVariable String id) {
		return distributionPowersRestQueryService.getById(id);
	}
}
