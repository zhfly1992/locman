/*
 * File name: OrderProcessRestQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年2月1日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.dto.ProcessInfoListDto;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.OrderProcessRestQueryService;

/**
 * @Description: 查询工单流程
 * @author: guofeilong
 * @version: 1.0, 2018年2月1日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(UrlConstant.ORDER_PROCESS_CONFIG)
public class OrderProcessRestQueryController {

	@Autowired
	private OrderProcessRestQueryService orderProcessRestQueryService;



	/**
	 * <method description>
	 *
	 * @param queryInfo
	 *            {
	 *            "pageSize":"页大小","pageNum":"页码","accessSecret":"接入方密钥","processType":"工作流程类型"
	 *            }
	 * @return PageInfo<ProcessInfoListDto>
	 */
	@PostMapping(UrlConstant.QUERY_ORDERPROCESS_LIST)
	public Result<PageInfo<ProcessInfoListDto>> getOrderProcesssList(@RequestBody String queryInfo) {
		return orderProcessRestQueryService.getOrderProcesssList(queryInfo);
	}



	@GetMapping(UrlConstant.QUERY_ORDERPROCESS_BY_ID)
	public Result<Map<String, Object>> queryOrderProcessById(@PathVariable String id) {
		return orderProcessRestQueryService.queryOrderProcessById(id);
	}



	@GetMapping(UrlConstant.EXIST_USER_INPRO)
	public Result<List<String>> existUserInProcess(@PathVariable String userId) {
		return orderProcessRestQueryService.existUserInProcess(userId);
	}
}
