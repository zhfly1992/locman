/*
 * File name: RemoteControlRecordQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月11日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.run.entity.common.Result;
import com.run.locman.query.service.RemoteControlRecordRestQueryService;

/**
 * @Description: 远控查询
 * @author: qulong
 * @version: 1.0, 2017年12月11日
 */

@RestController
@RequestMapping("/remoteControl")
public class RemoteControlRecordQueryController {

	@Autowired
	private RemoteControlRecordRestQueryService remoteControlRecordRestQueryService;



	@CrossOrigin(origins = "*")
	@PostMapping("/getControlList")
	public Result<Map<String, Object>> getControlList(@RequestBody String params) {
		return remoteControlRecordRestQueryService.getControlList(params);
	}



	@CrossOrigin(origins = "*")
	@PostMapping("/getControlItem")
	public Result<List<Map<String, Object>>> getControlItem(@RequestBody String params) {
		return remoteControlRecordRestQueryService.getControlItem(params);
	}

}
