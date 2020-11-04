/*
 * File name: TimeoutReportConfigQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年6月26日 ...
 * ... ...
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.entity.TimeoutReportConfig;
import com.run.locman.api.model.FacilitiesDtoModel;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.TimeoutReportConfigRestQueryService;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年6月26日
 */

@RestController
@RequestMapping(UrlConstant.TIMEOUT)
@CrossOrigin(origins = "*")
public class TimeoutReportConfigQueryController {

	@Autowired
	private TimeoutReportConfigRestQueryService timeoutReportConfigRestQueryService;
	
	/**
	  * 
	  * @Description: 接入方密钥查询超时未上报配置列表
	  * @param 
	  * @return 分页对象
	  */
	@RequestMapping(value = UrlConstant.QUERY_TIMEOUT_REPORT_CONFIG_LIST, method = RequestMethod.GET)
	public Result<PageInfo<TimeoutReportConfig>> queryTimeoutReportConfigList(@PathVariable String accessSecret,
			@RequestParam(value = "pageNum", required = true, defaultValue = "1") String pageNum,
			@RequestParam(value = "pageSize", required = true, defaultValue = "10") String pageSize,
			@RequestParam(value = "name", required = false) String name) {
		return timeoutReportConfigRestQueryService.queryTimeoutReportConfigList(accessSecret, pageNum, pageSize, name);
	}
	
	/**
	  * 
	  * @Description:通过id查询配置信息及绑定的设施id和数量
	  * @param 
	  * @return
	  */
	@RequestMapping(value = UrlConstant.QUERY_TIMEOUT_REPORT_CONFIG_BYID, method = RequestMethod.GET)
	public Result<Map<String, Object>> queryTimeoutReportConfigById(@PathVariable String id) {
		return timeoutReportConfigRestQueryService.queryTimeoutReportConfigById(id);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.QUERY_FACINFO, method = RequestMethod.POST)
	public Result<PageInfo<Facilities>> findFacInfo(@RequestBody FacilitiesDtoModel facilitiesDtoModel) {
		return timeoutReportConfigRestQueryService.findFacInfo(facilitiesDtoModel);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "once", method = RequestMethod.GET)
	public Result<String> test() {
		return timeoutReportConfigRestQueryService.test();
	}
}
