/*
 * File name: SimpleOrderTypeQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月7日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.api.entity.SimpleOrderProcessType;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.SimpleOrderTypeService;

/**
 * @Description: 获取一般工单流程状态
 * @author:王胜
 * @version: 1.0, 2017年12月7日
 */
@RestController
@RequestMapping(UrlConstant.SMIPLE_ORDER)
public class SimpleOrderTypeQueryController {

	@Autowired
	private SimpleOrderTypeService simpleOrderTypeService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETORDERTYPELIST, method = RequestMethod.POST)
	public Result<List<SimpleOrderProcessType>> findOrderTypeBySecret() {
		return simpleOrderTypeService.findOrderTypeBySecret();
	}



	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETSIMPLEORDERSTATELIST, method = RequestMethod.POST)
	public Result<List<Map>> findOrderState() {
		return simpleOrderTypeService.findOrderState();
	}
}
