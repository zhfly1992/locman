package com.run.locman.api.crud.service;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
public interface FaultOrderProcessCudService {

	/**
	 * @Description: 添加或者修改故障工单
	 * @param jsonObject
	 *            传递参数
	 * @return
	 */
	RpcResponse<JSONObject> addOrUpdateFaultOrder(JSONObject jsonObject);



	/**
	 *
	 * @Description:更新故障工单状态
	 * @param orderInfo
	 * @return RpcResponse<String>
	 * @throws Exception
	 */
	RpcResponse<String> updateFaultOrderState(JSONObject orderInfo) throws Exception;

	/**
	 * 
	 * @Description:设备超时未上报添加故障工单
	 * @param
	 * @return
	 */

	/*
	 * RpcResponse<String> addFaultOrderForDeviceDeletion(JSONObject
	 * jsonObject);
	 */

}
