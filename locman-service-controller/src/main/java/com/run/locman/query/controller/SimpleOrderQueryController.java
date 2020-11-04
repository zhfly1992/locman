/*
 * File name: SimpleOrderQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月8日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.model.FacilitiesModel;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.SimpleOrderRestService;

/**
 * @Description: 获取设施设备
 * @author:王胜
 * @version: 1.0, 2017年12月8日
 */
@RestController
@RequestMapping(UrlConstant.SMIPLE_ORDER)
public class SimpleOrderQueryController {

	@Autowired
	private SimpleOrderRestService simpleOrderRestService;

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETFACILITIESLIST, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getFacilityDeviceList(@RequestBody String queryParams) {
		return simpleOrderRestService.getFacilityDeviceList(queryParams);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETSIMPLEORDERBYID, method = RequestMethod.POST)
	public Result<Map<String, Object>> getSimpleOrderById(@RequestBody String queryParams) {
		return simpleOrderRestService.getSimpleOrderById(queryParams);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETSIMPLEORDERLIST, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getSimpleOrderList(@RequestBody String queryParams) {
		return simpleOrderRestService.getSimpleOrderList(queryParams);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.WHETHEREXISTORDER, method = RequestMethod.POST)
	public Result<Boolean> whetherExistOrder(@RequestBody String queryParams) {
		return simpleOrderRestService.whetherExistOrder(queryParams);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETSIMPLEORDERAGENCYLIST, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getSimpleOrderAgencyList(@RequestBody String queryParams) {
		return simpleOrderRestService.getSimpleOrderAgencyList(queryParams);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETORDERNODEDETAILS, method = RequestMethod.POST)
	public Result<Map<String, Object>> getOrderNodeDetails(@RequestBody String queryParams) {
		return simpleOrderRestService.getOrderNodeDetails(queryParams);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.FINDFACINFO, method = RequestMethod.POST)
	public Result<PageInfo<Facilities>> findFacInfo(@RequestBody FacilitiesModel facilitiesModel) {
		return simpleOrderRestService.findFacInfo(facilitiesModel);
	}

}
