/*
 * File name: AlarmInfoCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年11月20日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.AlarmInfoCrudService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 修改告警信息
 * @author: Administrator
 * @version: 1.0, 2017年11月20日
 */
@Service
public class AlarmInfoCrudRestService {

	@Autowired
	public AlarmInfoCrudService	alarmInfoCrudService;

	private Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Value("${api.host}")
	private String							ip;


	public Result<String> updateTheDel(String alarmInfo) {
		try {
			logger.info(String.format("[updateTheDel()->request params:%s]", alarmInfo));
			// 参数校验
			Result<String> result = ExceptionChecked.checkRequestParam(alarmInfo);
			if (result != null) {
				logger.error(String.format("[updateTheDel()->error:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonInfo = JSON.parseObject(alarmInfo);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", jsonInfo.getString("id"));
			map.put("isDel", 2);

			RpcResponse<String> res = alarmInfoCrudService.updateTheDel(map);
			if (res.isSuccess()) {
				logger.info(String.format("[updateTheDel()->success:%s, result:%s]", LogMessageContants.SAVE_SUCCESS,
						res.getSuccessValue()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateTheDel()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("updateTheDel()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
