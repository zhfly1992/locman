/*
 * File name: MysqlDataQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhongbinyuan 2019年2月27日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.MysqlDataQueryRestService;

/**
 * @Description:
 * @author: zhongbinyuan
 * @version: 1.0, 2019年2月27日
 */
@RestController
@RequestMapping(value=UrlConstant.DATAQUERY)
public class MysqlDataQueryController {

	@Autowired
	private MysqlDataQueryRestService mysqlDataQueryRestService;


    /**
     * 页面sql查询功能暂时不用
     */
//	@CrossOrigin(origins = "*")
//	@RequestMapping(value = UrlConstant.MYSQLQUERY, method = RequestMethod.POST)
//	public Result<List<LinkedHashMap<String, Object>>> getMysqlQueryResult(@RequestBody String param) {
//		return mysqlDataQueryRestService.getMysqlQueryResultInfo(param);
//	}
//	
//	@CrossOrigin(origins = "*")
//	@RequestMapping(value = UrlConstant.MONGODBQUERY, method = RequestMethod.POST)
//	public Result<Map<String, Object>> getMongoDbQueryResult(@RequestBody String params) {
//		return mysqlDataQueryRestService.getMongoQueryResultInfo(params);
//	}

}
