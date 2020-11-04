/**
 * 
 */
package com.run.locman.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.dto.OpenRecordQueryDto;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.OpenRecordRestQueryService;

/**
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = UrlConstant.OPEN_RECORD)
public class OpenRecordQueryController {
	@Autowired
	private OpenRecordRestQueryService openRecordRestQueryService;



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.QUERY_OPEN_RECORD, method = RequestMethod.POST)
	Result<PageInfo<OpenRecordQueryDto>> queryOpenRecord(@RequestBody String parms) {
		return openRecordRestQueryService.getOpenRecordByPage(parms);
	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.OPEN_RECORD_EXCEL, method = RequestMethod.POST)
	public ModelAndView excelOpenRecord(@RequestBody String parms, ModelMap model) {
		return openRecordRestQueryService.exportOpenRecordInfo(parms, model);
	}
}
