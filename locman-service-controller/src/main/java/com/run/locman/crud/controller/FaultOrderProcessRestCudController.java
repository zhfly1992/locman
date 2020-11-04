/*
 * File name: FaultOrderProcessRestCudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年12月06日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.FaultOrderProcessRestCudService;

/**
 * @Description: 故障工单添加修改
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
@RestController
@RequestMapping(UrlConstant.ORDER_FAULT)
public class FaultOrderProcessRestCudController {

	@Autowired
	private FaultOrderProcessRestCudService faultOrderProcessRestCudService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ADD_OR_UPDATE_FAULT_ORDER, method = RequestMethod.POST)
	public Result<String> addOrUpdateFaultOrder(@RequestBody String requestParams) {
		return faultOrderProcessRestCudService.addOrUpdateFaultOrder(requestParams);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.UPDATE_FAULT_ORDER_STATE, method = RequestMethod.POST)
	public Result<String> updateFaultOrderState(@RequestBody String orderInfo) {
		return faultOrderProcessRestCudService.updateFaultOrderState(orderInfo);
	}



	@PostMapping(value = UrlConstant.FAULT_ORDER_EXCEL)
	@CrossOrigin(origins = "*")
	public ModelAndView exportFaultOrderExcelInfo(@RequestBody JSONObject accessSecretJson, ModelMap model) {
		return faultOrderProcessRestCudService.exportFaultOrderExcelInfo(accessSecretJson, model);
	}
}
