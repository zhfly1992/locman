/*
 * File name: DeviceReportedDataCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2020年4月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.ReportedDataCrudRestService;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2020年4月7日
 */
@RestController
@CrossOrigin(origins = "*")
public class ReportedDataCrudController {
	@Autowired
	private ReportedDataCrudRestService reportedDataCrudRestService;



	@RequestMapping(value = "/wingsIotPush/deviceReported", method = RequestMethod.POST)
	public Result<String> saveReportedData(@RequestBody JSONObject jsonObject) {
		return reportedDataCrudRestService.receiveReportedData(jsonObject);
	}



	@RequestMapping(value = UrlConstant.COMMANDRESPONSE, method = RequestMethod.POST)
	public Result<String> receiveCommandResponse(@RequestBody JSONObject jsonObject) {
		return reportedDataCrudRestService.receiveCommandResponse(jsonObject);
	}
	
	@RequestMapping(value = "/dataConversion/deviceInfoSyn", method = RequestMethod.POST)
	public Result<String> receiveDeviceInfo(@RequestBody JSONObject jsonObject){
		return reportedDataCrudRestService.synchroDevicesFromDataConversion(jsonObject);
	}
}
