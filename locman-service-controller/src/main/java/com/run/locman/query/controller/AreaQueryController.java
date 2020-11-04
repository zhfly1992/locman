/*
 * File name: AreaQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年6月21日 ... ... ...
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
import com.run.locman.query.service.AreaRestQueryService;

/**
 * @Description:根据区域码查询区域
 * @author: 王胜
 * @version: 1.0, 2018年6月21日
 */
@RestController
@RequestMapping(value = UrlConstant.AREA)
public class AreaQueryController {

	@Autowired
	AreaRestQueryService areaRestQueryService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETAREABYCODE, method = RequestMethod.POST)
	public Result<List<Map<String, Object>>> getAreaByCode(@RequestBody String areaCode) {
		return areaRestQueryService.getAreaByCode(areaCode);
	}



	@CrossOrigin
	@GetMapping(value = UrlConstant.GET_ACCESS_PARTY_INFO_FOR_MAP)
	public Result<Map<String, Object>> getAreaInfo(@PathVariable(value = "accessSecret") String accessSecret) {

		return areaRestQueryService.getAreaInfo(accessSecret);
	}



	@CrossOrigin
	@GetMapping(value = UrlConstant.GET_INFO_BY_URL_STR)
	public Result<String> getAccessInfoByUrl(@PathVariable(value = "urlStr") String urlStr) {

		return areaRestQueryService.getAccessInfoByUrl(urlStr);
	}



	@CrossOrigin
	@GetMapping(value = UrlConstant.GET_CROSSEDMAP_BY_ACC)
	public Result<JSONObject> getCrossedMapInfo(@PathVariable(value = "accessSecret") String accessSecret) {

		return areaRestQueryService.getCrossedMapInfo(accessSecret);
	}
}
