package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.entity.common.Result;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.InfoSummaryRestQueryService;
import com.run.locman.query.service.StatisticsRestQueryService;

@RestController
@RequestMapping(UrlConstant.INFO_SUMMARY)
@CrossOrigin(origins = "*")
/**
 * 
* @Description:	
* @author: Administrator
* @version: 1.0, 2018年11月21日
 */
public class InfoSummaryQueryController {
	@Autowired
	private InfoSummaryRestQueryService infoSummaryRestQueryService;
	@Autowired
	private StatisticsRestQueryService statisticsRestQueryService;

	@RequestMapping(value = UrlConstant.DEVICE_COUNT, method = RequestMethod.GET)
	public Result<Map<String, Object>> getDeviceTotalCount(@PathVariable String accessSecret) {
		return infoSummaryRestQueryService.getDeviceCount(accessSecret);
	}

	@RequestMapping(value = UrlConstant.FACILITY_TOTAL, method = RequestMethod.GET)
	public Result<Map<String, Object>> getFacilityTotalCount(@PathVariable String accessSecret) {
		return infoSummaryRestQueryService.getFacilityCount(accessSecret);
	}

	@RequestMapping(value = UrlConstant.UNPROCESSED_ORDER_COUNT, method = RequestMethod.GET)
	public Result<Map<String, Object>> getUnprocessedAlarmTotalCount(@PathVariable String accessSecret) {
		return infoSummaryRestQueryService.getUnprocessedOrderCount(accessSecret);
	}

	@RequestMapping(value = UrlConstant.ORDER_TO_DO_COUNT, method = RequestMethod.GET)
	public Result<Map<String, Integer>> getProcessTodoCount(@PathVariable String accessSecret,
			@PathVariable String userId) {
		Map<String, Integer> eachCountOfProcessToDo = Maps.newHashMap();
		eachCountOfProcessToDo = (statisticsRestQueryService.getMyProcessTodoInfo(accessSecret, userId)).getValue();
		int getSimOrdCount = eachCountOfProcessToDo.get("simOrd");
		int getFauOrdCount = eachCountOfProcessToDo.get("fauOrd");
		int totalCount = getSimOrdCount + getFauOrdCount;
		Map<String, Integer> processToDoTatalCount = Maps.newHashMap();
		processToDoTatalCount.put("processToDo", totalCount);
		return ResultBuilder.successResult(processToDoTatalCount, "查询成功");
	}

	@RequestMapping(value = UrlConstant.DEVICE_NOT_REPORT, method = RequestMethod.GET)
	public Result<Map<String, String>> getDeviceCountNotReportInSetDay(@PathVariable String accessSecret) {
		return infoSummaryRestQueryService.getDeviceCountNotReportInSetDay(accessSecret);
	}

	@RequestMapping(value = UrlConstant.NORMAL_DEVICE_COUNT, method = RequestMethod.GET)
	public Result<Map<String, String>> getNormalDeviceCount(@PathVariable String accessSecret) {
		return infoSummaryRestQueryService.getNormalDeviceCount(accessSecret);
	}

	@RequestMapping(value = UrlConstant.USER_NUMBER, method = RequestMethod.GET)
	public Result<Map<String, String>> getUserNumber(@PathVariable String accessSecret) {
		return infoSummaryRestQueryService.getUserNumber(accessSecret);
	}

	@RequestMapping(value = UrlConstant.ACCESS_INFORMATION, method = RequestMethod.GET)
	public Result<Map<String, String>> getAccessInformation(@PathVariable String accessSecret) {
		return infoSummaryRestQueryService.getAccessInformation(accessSecret);
	}

	@RequestMapping(value = UrlConstant.USAGE_RATE, method = RequestMethod.GET)
	public Result<Map<String, String>> getUsageRate(@PathVariable String accessSecret) {
		return infoSummaryRestQueryService.getUsageRate(accessSecret);
	}
	
	@RequestMapping(value = UrlConstant.DAILY_ALARM_COUNT,method = RequestMethod.GET)
	public Result<Map<String, Object>> getDailyAlarmCountInMonth(@PathVariable String accessSecret){
		return infoSummaryRestQueryService.getDailyAlarmCountInMonth(accessSecret);
	}
	@RequestMapping(value = UrlConstant.DAILY_FAULT_COUNT,method =RequestMethod.GET)
	public Result<Map<String, Object>> getDailyFaultCountInMonth(@PathVariable String accessSecret){
		return infoSummaryRestQueryService.getDailyFaultCountInMonth(accessSecret);
	}
	
	@RequestMapping(value = UrlConstant.COUNT_ALARM_NUM_BY_DATE,method =RequestMethod.GET)
	public Result<Map<String, Object>> countAlarmNumByDate(@PathVariable("accessSecret") String accessSecret,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime){
		return infoSummaryRestQueryService.countAlarmNumByDate(accessSecret, startTime, endTime);
	}
	
	@RequestMapping(value = UrlConstant.COUNT_ORDER_STATE_NUM_BY_DATE,method =RequestMethod.POST)
	public Result<List<Map<String, Object>>> getAlarmOrderProcessStateStatistic(@RequestBody JSONObject jsonObject){
		return infoSummaryRestQueryService.getOrderStateStatistic(jsonObject);
	}
	
	@RequestMapping(value = UrlConstant.COUNT_STREET_OFFICE_AND_STREET_NUM,method = RequestMethod.POST)
	public Result<Map<String, Object>> countStreetAndStreetOfficeNum(@RequestBody JSONObject jsonObject){
		return infoSummaryRestQueryService.countStreetAndStreetOfficeNum(jsonObject);
	}
	
	
	@RequestMapping(value = UrlConstant.COUNT_FAC_ALARM_NUM, method = RequestMethod.GET)
	public Result<Map<String, Object>> countFacALarmNum(@PathVariable String accessSecret) {
		return infoSummaryRestQueryService.countFacAlarmNum(accessSecret);
	}
	
	@RequestMapping(value = UrlConstant.COUNT_DEVICE_REPORT_NUM, method = RequestMethod.POST)
	public Result<Map<String, Object>> countDeviceReportNum(@RequestBody String param) {
		return infoSummaryRestQueryService.getcountDeviceReportNum(param);
	}
	@RequestMapping(value = UrlConstant.COUNT_DAILY_DEVICE_NUM_BY_ALARM_LEVEL, method = RequestMethod.GET)
	public Result<JSONObject> countDeviceNumByAlarmLevelInSevenDays(@PathVariable  String accessSecret){
		return infoSummaryRestQueryService.countDeviceNumByAlarmLevelInSevenDays(accessSecret);
	}
	@RequestMapping(value = UrlConstant.COUNT_ALARM_ORDER_NUM_BY_STATE_AND_ALARM_LEVEL, method = RequestMethod.GET)
	public Result<JSONObject> countAlarmOrderNumByStateAndAlarm(@PathVariable  String accessSecret){
		return infoSummaryRestQueryService.countAlarmOrderNumByStateAndAlarm(accessSecret);
	}
	@RequestMapping(value = UrlConstant.COUNT_ALARM_FAC_BYORG, method = RequestMethod.GET)
	public Result<Map<String ,Object>> countAlarmFacByOrg(@PathVariable  String accessSecret){
		return infoSummaryRestQueryService.countAlarmFacByOrg(accessSecret);
	}
	@RequestMapping(value = UrlConstant.COUNT_ALARM_BYID_ANDTIME, method = RequestMethod.POST)
	public Result<List<Map<String,Object>>> countAlarmByIdAndTime(@RequestBody JSONObject jsonObject){
		return infoSummaryRestQueryService.countAlarmByIdAndTime(jsonObject);
	}
	@RequestMapping(value = UrlConstant.COUNT_ALARM_DEV_BYRULE, method = RequestMethod.GET)
	public Result<List<Map<String ,Object>>> countAlarmDevByRule(@PathVariable  String accessSecret){
		return infoSummaryRestQueryService.countAlarmDevByRule(accessSecret);
	}
	@RequestMapping(value = UrlConstant.COUNT_TRIGGER_TOP, method = RequestMethod.POST)
	public Result<List<Map<String ,Object>>> countTriggerTop(@RequestBody String params){
		
		return infoSummaryRestQueryService.countTriggerTop(params);
		
	}
	@RequestMapping(value = UrlConstant.DETAILS_BY_DEVICEID, method = RequestMethod.POST)
	public Result<Map<String ,Object>> detailsByDeviceId(@RequestBody JSONObject json){
		return infoSummaryRestQueryService.detailsByDeviceId(json);
	}
	
	@RequestMapping(value = UrlConstant.EXPORT_OPEN_DETAILS, method = RequestMethod.POST)
	public ModelAndView exportOpenDetails(@RequestBody JSONObject json , ModelMap model) {
		return infoSummaryRestQueryService.exportOpenDetails(json, model);
	} 
}
