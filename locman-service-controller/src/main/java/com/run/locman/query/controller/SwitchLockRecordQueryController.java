/*
 * File name: SwitchLockRecordQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.SwitchLockRecord;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.SwitchLockRecordRestQueryService;

/**
 * @Description: 分页查询开关所记录
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月7日
 */
@RestController
@RequestMapping(value = UrlConstant.SWITCHLOCK_ROOT)
public class SwitchLockRecordQueryController {

	@Autowired
	private SwitchLockRecordRestQueryService switchLockRecordRestQueryService;



	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.SWITCHLOCK_PAGE)
	public Result<PageInfo<Map<String, String>>> listSwitchLockPage(@RequestBody SwitchLockRecord switchLockRecord) {
		return switchLockRecordRestQueryService.listSwitchLockPage(switchLockRecord);
	}



	@PostMapping(value = UrlConstant.SWITCHLOCK_EXPROT)
	@CrossOrigin(origins = "*")
	public ModelAndView exprotSwitchLockInfo(@RequestBody SwitchLockRecord switchLockRecord, ModelMap model) {
		return switchLockRecordRestQueryService.exprotSwitchLockInfo(switchLockRecord, model);
	}
	
	
	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.LIST_MANHOLE_COVER_SWITCH)
	public Result<PageInfo<Map<String, Object>>> listManholeCoverSwitch(@RequestBody JSONObject json) {
		return switchLockRecordRestQueryService.listManholeCoverSwitch(json);
	}
	
	@CrossOrigin(origins = "*")
	@PostMapping(value = UrlConstant.GET_MANHOLE_COVER_SWITCH_INFO)
	public Result<PageInfo<Map<String, Object>>> getManholeCoverSwitchInfo(@RequestBody JSONObject json) {
		return switchLockRecordRestQueryService.getManholeCoverSwitchInfo(json);
	}
}
