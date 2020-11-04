/*
 * File name: AlarmOrderQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年12月5日 ... ... ...
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

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.dto.CountAlarmOrderDto;
import com.run.locman.api.entity.AlarmCountDetails;
import com.run.locman.api.entity.AlarmOrderCount;
import com.run.locman.api.entity.Pages;
import com.run.locman.constants.AlarmOrderCountConstants;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.AlarmOrderRestQueryService;

/**
 * 
 * @Description:告警工单查询
 * @author: lkc
 * @version: 1.0, 2017年12月5日
 */
@RestController
@RequestMapping(UrlConstant.ALARM_ORDER)
public class AlarmOrderQueryController {

	@Autowired
	private AlarmOrderRestQueryService alarmOrderRestQueryService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_GET_ALL_BY_PAGE, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getAlarmOrderByPage(@RequestBody String queryParams) {
		return alarmOrderRestQueryService.getAlarmOrderByPage(queryParams);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_GET_ALL_TODO_BY_PAGE, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getAlarmOrderTodoByPage(@RequestBody String queryParams) {
		return alarmOrderRestQueryService.getAlarmOrderTodoByPage(queryParams);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_GET_HAVE_DEAL_BY_PAGE, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> getAlarmOrderHaveDealByPage(@RequestBody String queryParams) {
		return alarmOrderRestQueryService.getAlarmOrderHaveDealByPage(queryParams);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_GET_REJECT_CAUSE, method = RequestMethod.GET)
	public Result<List<Map<String, Object>>> getRejectCause(@PathVariable String orderId) {
		return alarmOrderRestQueryService.getRejectCause(orderId);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_GET_COMPLETEINFO, method = RequestMethod.GET)
	public Result<JSONObject> getCompleteInfo(@PathVariable String orderId) {
		return alarmOrderRestQueryService.getCompleteInfo(orderId);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_GET_POWERLESS_INFO, method = RequestMethod.GET)
	public Result<List<Map<String, Object>>> getPowerlessInfo(@PathVariable String orderId) {
		return alarmOrderRestQueryService.getPowerLessCause(orderId);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_GET_STATE, method = RequestMethod.GET)
	public Result<List<Map<String, Object>>> getState() {
		return alarmOrderRestQueryService.getState(1);

	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_GET_ORDER_TYPE, method = RequestMethod.GET)
	public Result<List<Map<String, Object>>> getOrderType() {
		return alarmOrderRestQueryService.getState(2);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.ALARM_ORDER_GET_QUESTION_TYPE, method = RequestMethod.GET)
	public Result<List<Map<String, Object>>> getQuestionType() {
		return alarmOrderRestQueryService.getState(3);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ALARM_NOT_CLAIM)
	public Result<Pages<Map<String, Object>>> notClaimAlarmOrder(@RequestBody JSONObject orderInfo) {
		return alarmOrderRestQueryService.notClaimAlarmOrder(orderInfo);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ALARM_NOT_CLAIM_TOTAL)
	public Result<Integer> getNotClaimAlarmOrderTotal(@RequestBody JSONObject orderInfo) {
		return alarmOrderRestQueryService.getNotClaimAlarmOrderTotal(orderInfo);
	}



	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ALARM_ORDER_COUNT)
	public Result<PageInfo<Map>> alarmOrderCountInfo(@RequestBody AlarmOrderCount alarmOrderCount) {
		return alarmOrderRestQueryService.alarmOrderCountInfo(alarmOrderCount);
	}



	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.ALARM_DETAILS_COUNT)
	public Result<PageInfo<Map>> alarmDetailsCount(@RequestBody AlarmCountDetails alarmCountDetails) {
		return alarmOrderRestQueryService.alarmDetailsCount(alarmCountDetails);
	}



	@PostMapping(value = UrlConstant.ALARM_EXCEL_ALL)
	@CrossOrigin(origins = "*")
	public ModelAndView exportAlarmOrderExcelInfo(@RequestBody JSONObject orderExcelJson, ModelMap model) {
		return alarmOrderRestQueryService.exportAlarmOrderExcelInfo(
				orderExcelJson.getString(AlarmOrderCountConstants.ACCESSSECRET),
				orderExcelJson.getString(AlarmOrderCountConstants.COMPLETEADDRESS),
				orderExcelJson.getString(AlarmOrderCountConstants.AOST_NAME),
				orderExcelJson.getString(AlarmOrderCountConstants.FACILITIES_TYPE_ID), model);
	}



	@PostMapping(value = UrlConstant.ALARM_EXCEL_COUNT)
	@CrossOrigin(origins = "*")
	public ModelAndView exprotAlarmOrderCount(@RequestBody JSONObject orderExcelJson, ModelMap model) {
		return alarmOrderRestQueryService.exportAlarmOrderCount(
				orderExcelJson.getString(AlarmOrderCountConstants.ACCESSSECRET),
				orderExcelJson.getString(AlarmOrderCountConstants.STARTTIME),
				orderExcelJson.getString(AlarmOrderCountConstants.ENDTIME),
				orderExcelJson.getString(AlarmOrderCountConstants.FACILITIES_TYPE_ID), model);
	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.COUNT_ALL_ALARMORDER)
	public Result<PageInfo<CountAlarmOrderDto>> countAllAlarmOrder(@RequestBody String queryParams) {
		return alarmOrderRestQueryService.countAllAlarmOrder(queryParams);
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.COUNT_ALARM_ORDER_BY_ORG, method = RequestMethod.POST)
	public Result<PageInfo<Map<String, Object>>> countAlarmOrderByOrg(@RequestBody String queryParams) {
		return alarmOrderRestQueryService.countAlarmOrderByOrg(queryParams);
	}
	
	@PostMapping(value = UrlConstant.EXPROT_ALARM_ORDER_COUNT)
	@CrossOrigin(origins = "*")
	public ModelAndView exprotAlarmOrderCountByOrg(@RequestBody JSONObject orderExcelJson, ModelMap model) {
		return alarmOrderRestQueryService.exprotAlarmOrderCountByOrg(orderExcelJson, model);
	}
	
	
	@CrossOrigin(origins = "*")
	@GetMapping(value = UrlConstant.ALARM_ORDER_PROCESS_INFO)
	public Result<JSONObject> getOrderProcessInfo(@PathVariable String orderId) {
		return alarmOrderRestQueryService.getOrderProcessInfo(orderId);
	}
	
	@CrossOrigin(origins = "*")
	@GetMapping(value = UrlConstant.GET_HIDDEN_TROUBLE_TYPE)
	public Result<List<Map<String, Object>>> getHiddenType() {
		return alarmOrderRestQueryService.getHiddenTroubleType();
	}
	

}
