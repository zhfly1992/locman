/*
 * File name: RemoteControlRecordCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月8日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.crud.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.locman.crud.service.RemoteControlRecordCrudRestService;

/**
 * @Description: 远控
 * @author: qulong
 * @version: 1.0, 2017年12月8日
 */

@RestController
@RequestMapping(value = "/remoteControl")
public class RemoteControlRecordCrudController {

	@Autowired
	private RemoteControlRecordCrudRestService remoteControlRecordCrudRestService;



	/**
	 * 
	 * @Description:远程命令(分权分域)
	 * @param params
	 * @return
	 */
	@CrossOrigin("*")
	@PostMapping(value = "/controlByRemote")
	public Result<String> controlByRemote(@RequestBody String params) {
		return remoteControlRecordCrudRestService.control(params);
	}



	/**
	 * 
	 * @Description:作业工单命令
	 * @param params
	 * @param orderId
	 * @return
	 */
	@CrossOrigin("*")
	@PostMapping(value = "/controlByOrder/{orderId}")
	public Result<String> contorlByOrder(@RequestBody String params, @PathVariable String orderId) {
		return remoteControlRecordCrudRestService.contorlByOrder(params, orderId);
	}



	/**
	 * 
	 * @Description:手机端APP流量下发命令
	 * @param params
	 * @return
	 */
	@CrossOrigin("*")
	@PostMapping(value = "/openLock")
	public Result<String> openLock(@RequestBody String params) {
		return remoteControlRecordCrudRestService.openLock(params);
	}



	/**
	 * 
	 * @Description:地图命令(存在分权分域或工单权限)
	 * @param params
	 * @return Result<String>
	 */
	@CrossOrigin("*")
	@PostMapping(value = "/controlByMap")
	public Result<String> controlByMap(@RequestBody String params) {
		return remoteControlRecordCrudRestService.controlByMap(params);
	}



	/**
	 * 
	 * @Description:告警下发命令
	 * @param params
	 * @param orderId
	 * @return
	 */
	@CrossOrigin("*")
	@PostMapping(value = "/controlByAlarmOrder/{orderId}")
	public Result<String> controlByAlarmOrder(@RequestBody String params, @PathVariable String orderId) {
		return remoteControlRecordCrudRestService.controlByAlarmOrder(params, orderId);
	}



	/**
	 * 
	 * @Description:告警下发命令(APP)
	 * @param params
	 * @param orderId
	 * @return
	 */
	@CrossOrigin("*")
	@PostMapping(value = "/controlByAlarmOrderApp/{orderId}")
	public Result<String> controlByAlarmOrderApp(@RequestBody String params, @PathVariable String orderId) {
		return remoteControlRecordCrudRestService.controlByAlarmOrderApp(params, orderId);
	}
	
	/**
	 * 
	* @Description:wingsIot开锁演示
	* @param jsonObject
	* @return
	 */
	@CrossOrigin("*")
	@PostMapping(value = "/wingsIotOpenLock")
	public Result<String> openLockForWingsIot(@RequestBody JSONObject jsonObject){
		return remoteControlRecordCrudRestService.wingsIotOpenLock(jsonObject);
	}
}
