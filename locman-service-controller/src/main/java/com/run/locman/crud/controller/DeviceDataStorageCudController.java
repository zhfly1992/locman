/*
 * File name: DeviceDataStorageCudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月23日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.DeviceDataStorageCudRestService;


/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月23日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = UrlConstant.DEVICE_DATA_STORAGE)
public class DeviceDataStorageCudController {

	@Autowired
	private DeviceDataStorageCudRestService deviceDataStorageCudRestService;



	@RequestMapping(value = UrlConstant.ADD_DEVICE_DATA_STORAGE, method = RequestMethod.POST)
	public Result<Boolean> basicFacilitiesSynchronous(@RequestBody String deviceDataInfo) {
		return deviceDataStorageCudRestService.addDeviceData(deviceDataInfo);
	}



	@GetMapping(value = UrlConstant.DELETE_DEVICE_DATA_STORAGE)
	public Result<Boolean> deleteById(@PathVariable String deviceDataId) {
		return deviceDataStorageCudRestService.deleteById(deviceDataId);
	}



	@RequestMapping(value = UrlConstant.UPDATE_DEVICE_DATA_STORAGE, method = RequestMethod.POST)
	public Result<Boolean> updateDeviceData(@RequestBody String deviceDataInfo) {
		return deviceDataStorageCudRestService.updateDeviceData(deviceDataInfo);
	}



	@PostMapping(value = UrlConstant.SYNCHRONIZE_TO_FACILITIES)
	public Result<Map<String, Object>> synchronizeToFacilities(@RequestBody String synchronizationParam) {
		return deviceDataStorageCudRestService.synchronizeToFacilities(synchronizationParam);
	}

}
