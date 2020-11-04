/*
 * File name: BalanceSwitchPowersCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月4日 ... ... ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.run.entity.common.Result;
import com.run.locman.api.entity.BalanceSwitchStateRecord;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.BalanceSwitchPowersRestCrudService;

/**
 * @Description:平衡告警开关权限配置Controller
 * @author: 王胜
 * @version: 1.0, 2018年5月4日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = UrlConstant.BALANCE)
public class BalanceSwitchPowersCrudController {

	@Autowired
	private BalanceSwitchPowersRestCrudService balanceSwitchPowersRestCrudService;



	@RequestMapping(value = UrlConstant.BALANCE_SWITCH_SAVE, method = RequestMethod.POST)
	public Result<String> balanceSwitchPowersSave(@RequestBody String balanceSwitchPowers) {
		return balanceSwitchPowersRestCrudService.balanceSwitchPowersSave(balanceSwitchPowers);
	}



	@RequestMapping(value = UrlConstant.BALANCE_SWITCH_UPDATE, method = RequestMethod.POST)
	public Result<String> balanceSwitchPowersUpdate(@RequestBody String balanceSwitchPowers) {
		return balanceSwitchPowersRestCrudService.balanceSwitchPowersUpdate(balanceSwitchPowers);
	}



	@PostMapping(value = UrlConstant.BALANCE_SWITCH_DEL)
	public Result<String> balanceSwitchPowersDel(@RequestParam(value = "id", required = true) String id) {
		return balanceSwitchPowersRestCrudService.balanceSwitchPowersDel(id);
	}



	@RequestMapping(value = UrlConstant.BALANCE_SWITCH_STATE_CHANGE, method = RequestMethod.POST)
	public Result<String> balanceSwitchPowersStateChange(@RequestBody String balanceSwitchPowers) {
		return balanceSwitchPowersRestCrudService.balanceSwitchPowersStateChange(balanceSwitchPowers);
	}


	/**
	 * 
	* @Description: 测试调用RPC接口，保存开启关闭平衡告警记录
	* @param balanceSwitchPowers
	* @return
	 */
	@RequestMapping(value = "open", method = RequestMethod.POST)
	public Result<String> open(@RequestBody String balanceSwitchPowers) {
		return balanceSwitchPowersRestCrudService.open(balanceSwitchPowers);
	}


	/**
	 * 
	* @Description: 测试调用RPC接口，获取平衡告警状态
	* @param balanceSwitchPowers
	* @return
	 */
	@RequestMapping(value = "getState", method = RequestMethod.POST)
	public Result<BalanceSwitchStateRecord> getState(@RequestBody String balanceSwitchPowers) {
		return balanceSwitchPowersRestCrudService.getState(balanceSwitchPowers);
	}
}
