/*
 * File name: BalanceSwitchPowersQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月7日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.BalanceSwitchPowers;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.BalanceSwitchPowersRestQueryService;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月7日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = UrlConstant.BALANCE)
public class BalanceSwitchPowersQueryController {

	@Autowired
	private BalanceSwitchPowersRestQueryService balanceSwitchPowersRestQueryService;



	@RequestMapping(value = UrlConstant.GET_BALANCESWITCHPOWERS_LIST, method = RequestMethod.POST)
	public Result<PageInfo<BalanceSwitchPowers>> getBalanceSwitchPowersList(@RequestBody String balanceSwitchPowers) {
		return balanceSwitchPowersRestQueryService.getBalanceSwitchPowersList(balanceSwitchPowers);
	}



	@GetMapping(value = UrlConstant.GET_BALANCESWITCHPOWERS_BY_ID)
	public Result<BalanceSwitchPowers> getBalanceSwitchPowersById(
			@RequestParam(value = "id", required = true) String id) {
		return balanceSwitchPowersRestQueryService.getBalanceSwitchPowersById(id);
	}

	// 测试调用RPC接口 无用代码
	/*
	 * @RequestMapping(value = "ss", method = RequestMethod.POST) public
	 * Result<Boolean> get(@RequestBody String balanceSwitchPowers) { return
	 * balanceSwitchPowersRestQueryService.get(balanceSwitchPowers); }
	 */
}
