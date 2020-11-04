/*
* File name: FocusSecurityQueryController.java								
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
* 1.0			钟滨远		2020年4月28日
* ...			...			...
*
***************************************************/

package com.run.locman.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.FocusSecurityRestQueryService;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年4月28日
*/
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = UrlConstant.FOCUSSECURITY)
public class FocusSecurityQueryController {
	
	@Autowired
	private FocusSecurityRestQueryService focusSecurityRestQueryService;
	
	
	@PostMapping(value = UrlConstant.GET_FOCUSSECURITY_INFO_PAGE)
	public Result<PageInfo<Map<String,Object>>> getFocusSecurityInfoPage(@RequestBody JSONObject json){
		return focusSecurityRestQueryService.getFocusSecurityInfoPage(json);
		
	}
	
	@PostMapping(value = UrlConstant.FACILITES_COMMAND_RECEIVE_STATES)
	public Result<PageInfo<Map<String, Object>>> commandReceiveStates(@RequestBody JSONObject json){
		return focusSecurityRestQueryService.commandReceiveStates(json);
		
	}
}
