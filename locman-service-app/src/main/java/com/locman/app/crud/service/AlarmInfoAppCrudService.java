package com.locman.app.crud.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.AlarmInfoCrudService;

@Service
public class AlarmInfoAppCrudService {

	@Autowired
	private AlarmInfoCrudService alarmInfoCrudService;



	/**
	 * 忽略告警信息 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unused")
	public Result<String> updateAlarmDel(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			JSONObject jsonInfo = JSON.parseObject(param);
			Map<String, Object> alarmInfoMap = Maps.newHashMap();
			alarmInfoMap.put("id", jsonInfo.getString("id"));
			alarmInfoMap.put("isDel", jsonInfo.getString("isDel"));
			RpcResponse<String> rpcResponse = alarmInfoCrudService.updateTheDel(alarmInfoMap);
			if (rpcResponse.isSuccess()) {
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}
	
	
	
}
