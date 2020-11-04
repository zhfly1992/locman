/*
* File name: Query4ExcelController.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			guofeilong		2019年8月30日
* ...			...			...
*
***************************************************/

package com.run.locman.query.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.http.client.util.HttpClientUtil;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.query.service.Query4ExcelRestQueryService;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年8月30日
*/

@RestController
@RequestMapping("/Excel")
@CrossOrigin(origins = "*")
public class Query4ExcelController {
	
	@Autowired
	private Query4ExcelRestQueryService query4ExcelRestQueryService;
	
	@RequestMapping(value = "/deviceStateInfo4Excel",method = RequestMethod.POST)
	public List<JSONObject> deviceStateInfo4Excel(@RequestBody JSONObject jsonObject){
		return query4ExcelRestQueryService.deviceStateInfo4Excel(jsonObject).getValue();
	}
	
	@RequestMapping(value = "/deviceStateInfoCount",method = RequestMethod.POST)
	public List<JSONObject> deviceStateInfoCount(@RequestBody JSONObject jsonObject){
		return query4ExcelRestQueryService.deviceStateInfoCount(jsonObject).getValue();
	}
	@RequestMapping(value = "/changeDeviceStateFv",method = RequestMethod.POST)
	public Result<JSONObject> changeDeviceStateFv(@RequestBody JSONObject jsonObject){
		return query4ExcelRestQueryService.changeDeviceStateFv(jsonObject);
	}
	
/*	@RequestMapping(value = "/deviceStateInfoCount1",method = RequestMethod.POST)
	public String deviceStateInfoCount1(@RequestBody JSONObject jsonObject){
		
		String result = HttpClientUtil.getInstance().doPost("http://140.246.137.19:8002/locman/Excel/deviceStateInfoCount",
				jsonObject.toJSONString(), jsonObject.getString("token"));
		
		
		return result;
	}*/
	
}
