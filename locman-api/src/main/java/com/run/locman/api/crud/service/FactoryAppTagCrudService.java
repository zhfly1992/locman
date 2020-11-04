package com.run.locman.api.crud.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.AppTagDto;

/**
 * 
 * @Description:厂家apptagcrud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface FactoryAppTagCrudService {

	RpcResponse<String> addDeviceState(JSONObject deviceStateJson, String gatewayId, String deviceId);



	RpcResponse<String> addDeviceLineState(JSONObject deviceStateJson);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	RpcResponse<String> addFactoryRsAppTag(String factoryId, List<AppTagDto> appTagDtoList);

}
