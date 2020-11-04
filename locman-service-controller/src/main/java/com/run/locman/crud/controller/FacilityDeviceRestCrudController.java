/*
 * File name: FacilitiesRestCudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年8月29日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.FacilityDeviceRestCudService;

/**
 * 
 * @Description: 设施与设备关系rest接口
 * @author: guolei
 * @version: 1.0, 2017年9月14日
 */
@RestController
@RequestMapping(UrlConstant.BINDING)
public class FacilityDeviceRestCrudController {

	@Autowired
	private FacilityDeviceRestCudService facilityDeviceRestCudService;



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.BIND_FACILITY_WITH_DEVICES)
	public Result<Map<String, Object>> bindFacilityWithDevices(@RequestBody String param) {
		return facilityDeviceRestCudService.bindFacilityWithDevices(param);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.UNBIND_FACILITY_WITH_DEVICES)
	public Result<Map<String, Object>> unbindFacilityWithDevices(@RequestBody String param) {
		return facilityDeviceRestCudService.unbindFacilityWithDevices(param);
	}
}
