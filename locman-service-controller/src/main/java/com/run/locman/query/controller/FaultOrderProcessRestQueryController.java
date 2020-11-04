/*
 * File name: FaultOrderProcessRestQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年12月08日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.dto.CountFaultOrderDto;
import com.run.locman.api.dto.FaultOrderHistogramDto;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.FaultOrderProcessRestQueryService;

/**
 * @Description: 故障工单查询
 * @author: 田明
 * @version: 1.0, 2017年12月08日
 */
@RestController
@RequestMapping(UrlConstant.ORDER_FAULT)
public class FaultOrderProcessRestQueryController {

	@Autowired
	private FaultOrderProcessRestQueryService faultOrderProcessRestQueryService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.QUERY_DEV_FAC_FOR_FAULT_ORDER, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> queryDevicesForFaultOrder(@RequestBody String queryParams) {
		return faultOrderProcessRestQueryService.queryDevicesForFaultOrder(queryParams);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.QUERY_FAULT_ORDER_INFO_BY_ID, method = RequestMethod.POST)
	public Result<Map<String, Object>> queryFaultOrderInfoById(@RequestBody String id) {
		return faultOrderProcessRestQueryService.queryFaultOrderInfoById(id);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GET_FAULT_ORDER_LIST, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getFaultOrderList(@RequestBody String queryParams) {
		return faultOrderProcessRestQueryService.getFaultOrderList(queryParams);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GET_FAULT_ORDER_LIST_NEW, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getFaultOrderListNew(@RequestBody String queryParams) {
		return faultOrderProcessRestQueryService.getFaultOrderListNew(queryParams);
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.QUERY_AGENDA_OR_PROCESS, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> queryAgendaOrProcessFaultOrderList(@RequestBody String queryParams) {
		return faultOrderProcessRestQueryService.queryAgendaOrProcessFaultOrderList(queryParams);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.QUERY_AGENDA_OR_PROCESS_NEW, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> queryAgendaOrProcessFaultOrderListNew(@RequestBody String queryParams) {
		return faultOrderProcessRestQueryService.queryAgendaOrProcessFaultOrderListNew(queryParams);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.COUNT_FAULT_ORDERINFO_BY_AS, method = RequestMethod.POST)
	public Result<PageInfo<CountFaultOrderDto>> countFaultOrderInfoByAS(@RequestBody String queryParams) {
		return faultOrderProcessRestQueryService.countFaultOrderInfoByAS(queryParams);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.FAULT_ORDER_HISTOGRAM, method = RequestMethod.POST)
	public Result<List<FaultOrderHistogramDto>> faultOrder2Histogram(@RequestBody String queryParams) {
		return faultOrderProcessRestQueryService.faultOrder2Histogram(queryParams);
	}
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.COUNT_FAULT_ORDER_BY_ORG_AND_TYPE,method = RequestMethod.POST)
	public Result<Object> countFaultOrderNum(@RequestBody JSONObject jsonObject){
		return faultOrderProcessRestQueryService.countFaultOrderNumByOrganizationAndType(jsonObject);
	}
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.FAULT_ORDER_LIST,method = RequestMethod.POST)
	public Result<PageInfo<Map<String,Object>>> faultOrderList(@RequestBody JSONObject jsonObject){
		return faultOrderProcessRestQueryService.faultOrderList(jsonObject);
	}
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.FAULT_ORDER_STATE_COUNT,method = RequestMethod.POST)
	public Result<List<Map<String,Object>>> faultOrderStateCount(@RequestBody JSONObject jsonObject){
		return faultOrderProcessRestQueryService.faultOrderStateCount(jsonObject);
	}
}
