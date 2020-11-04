/*
 * File name: DroolsListController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年10月16日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.drools.service.TestInterface;
import com.run.locman.api.query.service.DroolsQueryService;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.UrlConstant;

/**
 * @Description: 规则引擎查询类
 * @author: zhabing
 * @version: 1.0, 2017年10月16日
 */

@RestController
@RequestMapping(UrlConstant.DROOLS)
@CrossOrigin(value = "*")
public class DroolsQueryController {

	private static final Logger	logger	= Logger.getLogger(DroolsQueryController.class);

	private DroolsQueryService	droolsQueryService;

	private TestInterface		testInterface;



	/**
	 * 
	 * @Description:分页查询所有规则
	 * @param droolsParams
	 * @return
	 */
	@PostMapping(value = UrlConstant.GET_ALLDROOLS_BYPAGE)
	public Result<PageInfo<Map<String, Object>>> getAllRoolByPage(@RequestBody String droolsParams) {

		logger.info("[getAllRoolByPage()->request param:" + droolsParams);
		try {

			// 参数校验
			Result<PageInfo<Map<String, Object>>> result = ExceptionChecked.checkRequestParam(droolsParams);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}

			JSONObject droolJson = JSONObject.parseObject(droolsParams);
			int pageNum = droolJson.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = droolJson.getIntValue(FacilitiesContants.PAGE_SIZE);
			String paranValue = droolJson.getString(FacilitiesContants.SEARCH_KEY);

			Map<String, Object> searchParam = Maps.newHashMap();
			searchParam.put(FacilitiesContants.SEARCH_KEY, paranValue);

			RpcResponse<PageInfo<Map<String, Object>>> res = droolsQueryService.getAllDrollsByPage(pageNum, pageSize,
					searchParam);
			if (res.isSuccess()) {
				logger.info(String.format("[getAllRoolByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getAllRoolByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getAllRoolByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:根据规则id查询规则信息
	 * @param droolsParams
	 * @return
	 */
	@PostMapping(value = UrlConstant.GET_DROOLINFO_BYID)
	public Result<Map<String, Object>> getDroolInfoById(@RequestBody String droolsParams) {
		
		logger.info(String.format("[getAllRoolByPage()->request param:%s", droolsParams));
		try {
			// 参数校验
			Result<PageInfo<Map<String, Object>>> result = ExceptionChecked.checkRequestParam(droolsParams);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}

			JSONObject droolJson = JSONObject.parseObject(droolsParams);
			String id = droolJson.getString(FacilitiesContants.FACILITES_ID);

			RpcResponse<Map<String, Object>> res = droolsQueryService.getDroolInfoById(id);
			if (res.isSuccess()) {
				logger.info(String.format("[getAllRoolByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getAllRoolByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getAllRoolByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:重新加载规则
	 * @param droolsParams
	 * @return
	 */
	@PostMapping(value = UrlConstant.RELOAD_DROOLS)
	public Result<String> reloadDrools() {
		try {
			String result = testInterface.reload();
			logger.info(String.format("[reloadDrools()->success:%s]", result));
			return ResultBuilder.successResult(null, result);
		} catch (Exception e) {
			logger.error(String.format("[reloadDrools()->exception:%s]", e.getMessage()));
			return ResultBuilder.failResult("规则错误:"+e.getMessage());
		}

	}

}
