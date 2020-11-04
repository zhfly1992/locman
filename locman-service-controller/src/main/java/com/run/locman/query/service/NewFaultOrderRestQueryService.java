/*
 * File name: NewFaultOrderRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhongbinyuan 2019年12月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.NewFaultOrderQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: zhongbinyuan
 * @version: 1.0, 2019年12月7日
 */
@Service
public class NewFaultOrderRestQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private NewFaultOrderQueryService	newFaultOrderQueryService;


	public Result<PageInfo<Map<String, Object>>> getFaultListInfo(JSONObject json) {
		logger.info(String.format("[getFaultListInfo()->request params--json:%s]", json.toString()));
		try {
			RpcResponse<PageInfo<Map<String, Object>>> res = newFaultOrderQueryService.getFaultListInfo(json);
			if (res == null || !res.isSuccess()) {
				logger.error("getFaultListInfo-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			logger.info(String.format("[getFaultListInfo()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(res.getSuccessValue(), "查询成功");
		} catch (Exception e) {
			logger.error("getDeviceCountByaccessSecret()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	public Result<Map<String, Object>> getCountDay(JSONObject param) {
		logger.info(String.format("[getCountDay()->request params--json:%s]",param));
		try {
			RpcResponse<Map<String, Object>> res = newFaultOrderQueryService.countCountDay(param);
			if (res == null || !res.isSuccess()) {
				logger.error("getCountDay-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			logger.info(String.format("[getCountDay()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(res.getSuccessValue(), "查询成功");
		} catch (Exception e) {
			logger.error("getCountDay()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	

}
