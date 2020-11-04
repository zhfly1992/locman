/*
 * File name: SimpleOrderCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月6日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.api.model.SimpleOrderProcessModel;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.SimpleOrderService;

/**
 * @Description: 一般流程工单Controller
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */
@RestController
@RequestMapping(value = UrlConstant.SMIPLE_ORDER)
public class SimpleOrderCrudController {

	@Autowired
	public SimpleOrderService simpleOrderService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ADD_OR_UPDATE_SIMPLE_ORDER, method = RequestMethod.POST)
	public Result<String> alarmRuleAdd(@RequestBody String orderInfo) {
		return simpleOrderService.simpleOrderAdd(orderInfo);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.UPDATESIMPLEORDERSTATE, method = RequestMethod.POST)
	public Result<String> updateSimpleOrderState(@RequestBody String orderInfo) {
		return simpleOrderService.updateSimpleOrderState(orderInfo);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.DELAYED_SIMPLEORDER)
	public Result<String> delayedSimpleOrder(@RequestBody SimpleOrderProcessModel simpleOrderProcessModel) {
		return simpleOrderService.delayedSimpleOrder(simpleOrderProcessModel);
	}
    
	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.INVALIDATE_SIMPLEORDER)
	public Result<String> invalidateSimpleOrder(@RequestBody SimpleOrderProcessModel simpleOrderProcessModel) {
		return simpleOrderService.invalidateSimpleOrder(simpleOrderProcessModel);
	}
}
