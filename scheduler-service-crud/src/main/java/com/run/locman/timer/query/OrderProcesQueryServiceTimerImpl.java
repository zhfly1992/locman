/*
* File name: OrderProcesQueryServiceTimerQuery.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			guofeilong		2018年7月4日
* ...			...			...
*
***************************************************/

package com.run.locman.timer.query;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.timer.query.repository.OrderProcessQueryRepository;
import com.run.locman.api.timer.query.service.OrderProcessQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年7月4日
*/

public class OrderProcesQueryServiceTimerImpl implements OrderProcessQueryService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	OrderProcessQueryRepository orderProcessQueryRepository;
	/**
	 * @see com.run.locman.api.timer.query.service.OrderProcessQueryService#findPersonByAccessSecret(java.lang.String, java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> findPersonByAccessSecret(String accessSecret, String processType) {
		// 查询人
		logger.info(String.format("[findPersonByAccessSecret()->request params--accessSecret:%s,processType:%s]", accessSecret,processType));
		Map<String, Object> map = Maps.newHashMap();
		map.put("accessSecret", accessSecret);
		map.put("processType", processType);
		try {
			List<Map<String, Object>> findPersonByAccessSecret = orderProcessQueryRepository
					.findPersonByAccessSecret(map);
			logger.info("findPersonByAccessSecret()-->查询成功");
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, findPersonByAccessSecret);
		} catch (Exception e) {
			logger.error("findPersonByAccessSecret()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
	/**
	 * @see com.run.locman.api.timer.query.service.OrderProcessQueryService#queryNodeInfoForActivity(java.util.Map)
	 */
	@Override
	public RpcResponse<JSONArray> queryNodeInfoForActivity(Map<String, Object> queryMap) {
		logger.info(String.format("[queryNodeInfoForActivity()->request params:%s]", queryMap));
		try {
			List<Map<String, Object>> list = orderProcessQueryRepository.queryNodeInfoForActivity(queryMap);
			JSONArray jsonArray = new JSONArray();
			for (Map<String, Object> map : list) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put((String) map.get("node"), map.get("personId"));
				jsonArray.add(jsonObject);
			}
			logger.info("queryNodeInfoForActivity()-->" + LogMessageContants.QUERY_SUCCESS);
			return RpcResponseBuilder.buildSuccessRpcResp("查询用户节点信息成功", jsonArray);
		} catch (Exception e) {
			logger.error("queryNodeInfoForActivity()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
