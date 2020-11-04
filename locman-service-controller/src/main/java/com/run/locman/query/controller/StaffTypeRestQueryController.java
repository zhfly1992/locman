/*
 * File name: StaffTypeRestQueryController.java
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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.api.entity.StaffType;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.StaffTypeRestQueryService;

/**
 * @Description: 人员类型管理查询
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */

@RestController
@RequestMapping(value = UrlConstant.STAFFTYPE)
public class StaffTypeRestQueryController {

	@Autowired
	private StaffTypeRestQueryService staffTypeRestQueryService;



	@CrossOrigin("*")
	@GetMapping(value = UrlConstant.FIND_STAFFTYPE_BY_ACCESSSCRET)
	public Result<List<StaffType>> getByAccessSecret() {
		return staffTypeRestQueryService.getByAccessSecret();
	}
	
	@CrossOrigin("*")
	@GetMapping(value = UrlConstant.FIND_STAFFTYPE_BY_ID)
	public Result<StaffType> getById(@PathVariable String id) {
		return staffTypeRestQueryService.getById(id);
	}

}
