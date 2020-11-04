/*
 * File name: MysqlDataQueryServiceImpl.java
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

package com.run.locman.query.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.CommandResult;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.MysqlDataQueryRepository;
import com.run.locman.api.query.service.MysqlDataQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: zhongbinyuan
 * @version: 1.0, 2019年2月27日
 */
public class MysqlDataQueryServiceImpl implements MysqlDataQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private MysqlDataQueryRepository	mysqlDataQueryRepository;

	@Autowired
	private MongoTemplate				mongoTemplate;



	/**
	 * @see com.run.locman.api.query.service.MysqlDataQueryService#getMysqlQueryResultInfo(java.lang.String)
	 */
	@Override
	public RpcResponse<List<LinkedHashMap<String, Object>>> getMysqlQueryResultInfo(String sqlStatement) {
		logger.info(String.format("[getMysqlQueryResultInfo()方法执行开始...,参数：【%s】]", sqlStatement));
		try {
			if (sqlStatement == null) {
				logger.error("[getMysqlQueryResultInfo()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			List<LinkedHashMap<String, Object>> listData = mysqlDataQueryRepository.getMysqlQueryResult(sqlStatement);
			if (listData == null || listData.size() == 0) {
				logger.info("查询成功但没有数据！");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功但没有数据！", listData);
			}
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", listData);
		} catch (Exception e) {
			logger.error("getMysqlQueryResultInfo()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.MysqlDataQueryService#getMongoQueryResultInfo(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> getMongoQueryResultInfo(String dbStatement) {
		logger.info(String.format("[getMongoQueryResultInfo()方法执行开始...,参数：【%s】]", dbStatement));
		
		try {
			CommandResult eval = mongoTemplate.getDb().doEval(dbStatement);
			Map<String, Object> resultMap = new HashMap<>(1);
			if(eval.ok()) {
				Object object = eval.get("retval");
				object = eval.get("retval");
				resultMap.put("result", object);
				
				return RpcResponseBuilder.buildSuccessRpcResp("ok，查询成功！", resultMap);
			}else {
				String errorMessage = eval.getErrorMessage();
				logger.error("[getMongoQueryResultInfo()->error：查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp(errorMessage);
			}
		} catch (Exception e) {
			logger.error("getMongoQueryResultInfo()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		} 

	}

}
