/*
 * File name: ProcessTypeBaseRestQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tianming 2018年02月02日 ...
 * ... ...
 *
 ***************************************************/
package com.run.locman.query.controller;

import com.run.entity.common.Result;
import com.run.locman.api.entity.ProcessTypeBase;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.ProcessTypeBaseRestQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 工单流程类型
 * @author: 田明
 * @version: 1.0, 2018年02月02日
 */
@RestController
@RequestMapping(UrlConstant.ORDER_PROCESS_CONFIG)
public class ProcessTypeBaseRestQueryController {

	@Autowired
	private ProcessTypeBaseRestQueryService processTypeBaseRestQueryService;



	@CrossOrigin(origins = "*")
	@GetMapping(UrlConstant.QUERY_ORDERPROCESS_TYPE)
	public Result<List<ProcessTypeBase>> queryOrderProcessType() {
		return processTypeBaseRestQueryService.queryOrderProcessType();
	}
}
