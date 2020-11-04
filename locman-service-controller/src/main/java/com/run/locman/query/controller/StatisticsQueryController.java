/*
* File name: StatisticsQueryController.java								
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
* 1.0			lkc		2017年10月31日
* ...			...			...
*
***************************************************/
package com.run.locman.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.StatisticsRestQueryService;

/**
 * @Description: 工单统计
 * @author: lkc
 * @version: 1.0, 2017年10月31日
 */
@RestController
@RequestMapping(UrlConstant.STATISTICS)
@CrossOrigin(origins = "*")
public class StatisticsQueryController {
	@Autowired
	private StatisticsRestQueryService statisticsRestQueryService;

	@RequestMapping(value = UrlConstant.STATISTICS_ORDER, method = RequestMethod.GET)
	public Result<Map<String, Map<String, Integer>>> getStatisticInfo(@PathVariable String accessSecret,
			@PathVariable String userId) {
		return statisticsRestQueryService.getStatisticInfo(accessSecret, userId);
	}

	@RequestMapping(value = UrlConstant.PROCESS_MY, method = RequestMethod.GET)
	public Result<Map<String, Integer>> getMyProcessInfo(@PathVariable String accessSecret,
			@PathVariable String userId) {

		return statisticsRestQueryService.getMyProcessInfo(accessSecret, userId);
	}

	@RequestMapping(value = UrlConstant.PROCESS_MY_TODO, method = RequestMethod.GET)
	public Result<Map<String, Integer>> getMyProcessTodoInfo(@PathVariable String accessSecret,
			@PathVariable String userId) {

		return statisticsRestQueryService.getMyProcessTodoInfo(accessSecret, userId);
	}

	@RequestMapping(value = UrlConstant.PROCESS_MY_HTODO, method = RequestMethod.GET)
	public Result<Map<String, Integer>> getMyProcessHTodoInfo(@PathVariable String accessSecret,
			@PathVariable String userId) {
		return statisticsRestQueryService.getMyProcessHTodoInfo(accessSecret, userId);
	}

	@RequestMapping(value = UrlConstant.PROCESS_TOTAL, method = RequestMethod.POST)
	public Result<Map<String, Integer>> getProcessTotal(@RequestBody Map<String, Map<String, Integer>> data) {
		return statisticsRestQueryService.getProcessTotal(data);
	}

}
