/*
 * File name: DeviceDataStorageRestQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月27日 ...
 * ... ...
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

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.dto.DeviceDataDto;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.DeviceDataStorageRestQueryService;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月27日
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = UrlConstant.DEVICE_DATA_STORAGE)
public class DeviceDataStorageRestQueryController {

	@Autowired
	private DeviceDataStorageRestQueryService deviceDataStorageRestQueryService;



	@RequestMapping(value = UrlConstant.DEVICE_DATA_STORAGE_LIST, method = RequestMethod.POST)
	public Result<PageInfo<DeviceDataDto>> getBalanceSwitchPowersList(@RequestBody String param) {
		return deviceDataStorageRestQueryService.queryDeviceDataStorageList(param);
	}



	@GetMapping(value = UrlConstant.DEVICE_DATA_STORAGE_BY_ID)
	public Result<List<DeviceDataDto>> getDeviceDataStorageById(@PathVariable String id) {
		return deviceDataStorageRestQueryService.getDeviceDataStorageById(id);
	}



	@GetMapping(value = UrlConstant.GET_ALL_AREA)
	public Result<List<Map<String, Object>>> getAllArea() {
		return deviceDataStorageRestQueryService.getAllArea();
	}



	@RequestMapping(value = UrlConstant.CHECK_DEVICE_NUMBER_EXIST, method = RequestMethod.POST)
	public Result<Boolean> checkDeviceNumberExist(@RequestBody String checkParam) {
		return deviceDataStorageRestQueryService.checkDeviceNumberExist(checkParam);
	}



	@RequestMapping(value = UrlConstant.CHECK_SERIAL_NUMBER_EXIST, method = RequestMethod.POST)
	public Result<Boolean> checkSerialNumberExist(@RequestBody String checkParam) {
		return deviceDataStorageRestQueryService.checkSerialNumberExist(checkParam);
	}
}
