/*
 * File name: FacilityDeviceRestQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guolei 2017年9月14日 ... ...
 * ...
 *
 ***************************************************/
package com.run.locman.query.controller;

import java.util.List;
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
import com.run.locman.query.service.FacilityDeviceRestQueryService;

/**
 * 
 * @Description: 设施与设备关系rest接口
 * @author: guolei
 * @version: 1.0, 2017年9月14日
 */
@RestController
@RequestMapping(UrlConstant.BINDING)
public class FacilityDeviceRestQueryController {

	@Autowired
	private FacilityDeviceRestQueryService facilityDeviceRestQueryService;



	@CrossOrigin(origins = "*")
	@GetMapping(value = UrlConstant.QUERY_DEVICE_BY_ID)
	public Result<Map<String, Object>> queryDeviceBindingState(@PathVariable String deviceId) {
		return facilityDeviceRestQueryService.queryDeviceBindingState(deviceId);
	}



	@CrossOrigin(origins = "*")
	@GetMapping(value = UrlConstant.QUERY_FACILITY_BY_ID)
	public Result<Map<String, Object>> unbindFacilityWithDevices(@PathVariable String facilityId) {
		return facilityDeviceRestQueryService.queryFacilityBindingState(facilityId);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.QUERY_FACILITIES_BY_PAGE)
	public Result<PageInfo<Map<String, Object>>> queryFacilitiesByPage(@RequestBody String params) {
		return facilityDeviceRestQueryService.queryFacilitiesByPage(params);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.QUERY_DEVICES_BY_PAGE)
	public Result<Map<String, Object>> queryDevicesByPage(@RequestBody String params) {
		return facilityDeviceRestQueryService.queryDevicesByPage(params);
	}



	@CrossOrigin(origins = "*")
	@GetMapping(value = UrlConstant.QUERY_DEVICE_TYPE_LIST)
	public Result<List<Map<String, Object>>> queryDeviceTypeList(@PathVariable String accessSecret) {
		return facilityDeviceRestQueryService.queryDeviceTypeList(accessSecret);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.FAC_BIND_DEVICE_INFO)
	public Result<Map<String, Object>> queryFacilityBindingState(@RequestBody String facInfo) {
		return facilityDeviceRestQueryService.queryAllDeviceByFacId(facInfo);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.DEVICE_CHECK)
	public Result<Map<String, Object>> deviceCheck(@RequestBody String paramInfo) {
		return facilityDeviceRestQueryService.deviceCheck(paramInfo);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.EXPORT_DEVICES, method = RequestMethod.POST)
	public ModelAndView exportDevices(@RequestBody String params, ModelMap model) {
		return facilityDeviceRestQueryService.exportDevices(params, model);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/findOnlineQuery", method = RequestMethod.POST)
	public Result<List<Map<String, Object>>> findOnlineQuery(@RequestBody String reportType) {
		return facilityDeviceRestQueryService.findOnlineQuery(reportType);
	}
	
	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.QUERY_DEVICES_BY_RULE)
	public Result<Map<String, Object>> queryDevicesByRule(@RequestBody String params) {
		return facilityDeviceRestQueryService.queryDevicesByRule(params);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.EXPORT_DEVICES_BY_RUlE, method = RequestMethod.POST)
	public ModelAndView exportDevicesByRule(@RequestBody String params, ModelMap model) {
		return facilityDeviceRestQueryService.exportDevicesByRule(params, model);
	}
}
