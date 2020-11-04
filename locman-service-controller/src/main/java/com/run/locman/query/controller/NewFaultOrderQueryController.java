/*
 * File name: NewFaultOrderQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhongbinyuan 2019年12月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.query.service.NewFaultOrderRestQueryService;

/**
 * @Description:
 * @author: zhongbinyuan
 * @version: 1.0, 2019年12月7日
 */
@RestController
@RequestMapping(value = "/faultOrderQuery")
public class NewFaultOrderQueryController {

	@Autowired
	private NewFaultOrderRestQueryService newFaultOrderRestQueryService;



	@CrossOrigin
	@PostMapping(value = "/faultList")
	public Result<PageInfo<Map<String, Object>>> getFaultList(@RequestBody JSONObject json) {
		return newFaultOrderRestQueryService.getFaultListInfo(json);
	}
	
	@CrossOrigin
	@PostMapping(value = "/chaxuanmouyitianshangbao")
	public Result<Map<String, Object>> getCountDay(@RequestBody JSONObject json) {
		return newFaultOrderRestQueryService.getCountDay(json);
	}
	
}
