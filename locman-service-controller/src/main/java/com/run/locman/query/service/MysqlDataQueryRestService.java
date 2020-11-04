/*
* File name: MysqlDataQueryRestService.java								
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
* 1.0			zhongbinyuan		2019年2月27日
* ...			...			...
*
***************************************************/

package com.run.locman.query.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ParamChecker;
import com.run.common.util.StringUtil;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.MysqlDataQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;

/**
* @Description:	
* @author: zhongbinyuan
* @version: 1.0, 2019年2月27日
*/
@Service
public class MysqlDataQueryRestService {
	
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private MysqlDataQueryService mysqlDataQueryService;
	
	
	public Result<List<LinkedHashMap<String,Object>>> getMysqlQueryResultInfo(String param){
		logger.info(String.format("[getMysqlQueryResultInfo()->request params:%s]", param));
		
		try {
			if (ParamChecker.isBlank(param) ) {
			logger.error(String.format("[getMysqlQueryResultInfo()->error:%s ]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.noBusinessResult();
		}
			JSONObject json=JSONObject.parseObject(param);
			String str1=new String("select");
			String str2=new String("SELECT");
			String sqlStatement=json.getString("sqlStatement");
			String sqlInterception=sqlStatement.substring(0,6);
			if (StringUtil.isEmpty(sqlStatement) ) {
				logger.error("[getMysqlQueryResultInfo()->error:sql语句不能为空]");
				return ResultBuilder.failResult("sql语句不能为空！");
			}
			if(sqlInterception.equals(str1)) {
				sqlInterception=sqlInterception.toUpperCase();
			}
			if(!sqlInterception.equals(str2)) {
				logger.error("[getMysqlQueryResultInfo()->error:不能执行非查询功能！]");
				return ResultBuilder.failResult("不能执行"+sqlInterception+"功能！");
			}
			
			RpcResponse<List<LinkedHashMap<String, Object>>> mysqlQueryResultInfo=mysqlDataQueryService.getMysqlQueryResultInfo(sqlStatement);
			if (mysqlQueryResultInfo.isSuccess()) {
				logger.info("[getMysqlQueryResultInfo()->success:数据查询成功]");
				return ResultBuilder.successResult(mysqlQueryResultInfo.getSuccessValue(), mysqlQueryResultInfo.getMessage());
			}
			logger.error("[getMysqlQueryResultInfo()->fail:数据查询失败]");
			return ResultBuilder.failResult(mysqlQueryResultInfo.getMessage());
		}catch(Exception e) {
			logger.error("getMysqlQueryResultInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
		
	}
	
	public Result<Map<String, Object>>  getMongoQueryResultInfo(String params){
		logger.info(String.format("[getMongoQueryResultInfo()->request params:%s]", params));
		try {
			if (ParamChecker.isBlank(params) ) {
				logger.error(String.format("[getMongoQueryResultInfo()->error:%s ]", LogMessageContants.NO_PARAMETER_EXISTS));
				return ResultBuilder.noBusinessResult();
			}
			String str="function(){var a ="+params+";if(!isNaN(a)){return a;}var lista = [];"
					+ "for (var i = 0 ; i< a.size();i++){if( i == 50){return lista;}lista.push(a[i]);}return lista;}";
			//现阶段mongodb连接表名集合
			String[] colletionName= {"collectionName","deviceHistoryState","deviceLineState","deviceState"};
//			String[] commandSet= {"bulkWrite","copyTo","convertToCapped","createIndex","createIndexes","dataSize",
//					"deleteOne","deleteMany","distinct","drop","dropIndex","dropIndexes","ensureIndex","reIndex",
//					"findOneAndDelete","findOneAndReplace","findOneAndUpdate","getDB","getPlanCache","getIndexes",
//					"group","insert","insertOne","insertMany","mapReduce","aggregate","remove","replaceOne",
//					"renameCollection","runCommand","save","stats","storageSize","totalIndexSiz",
//					"totalSize","update","updateOne","updateMany","validate","getShardVersion",
//					"getShardDistribution","getSplitKeysForChunks","getWriteConcern","setWriteConcern",
//					"unsetWriteConcern","latencyStats"};
			////正则表达式判断现阶段除查询外的命令
			String regx = ".*(bulkWrite|copyTo|convertToCapped|createIndex|createIndexes|dataSize|deleteOne|deleteMany|distinct|drop|"
					+ "dropIndex|dropIndexes|ensureIndex|reIndex|findOneAndDelete|findOneAndReplace|findOneAndUpdate|getDB|getPlanCache|"
					+ "getIndexes|group|insert|insertOne|insertMany|mapReduce|aggregate|remove|replaceOne|renameCollection|runCommand|save|"
					+ "stats|storageSize|totalIndexSize|totalSize|update|updateOne|updateMany|validate|getShardVersion|getShardDistribution|"
					+ "getSplitKeysForChunks|getWriteConcern|setWriteConcern|unsetWriteConcern|latencyStats).*";
			int count1=0;
//			int count2=0;
			for(int i=0;i<colletionName.length;i++) {
				if(params.indexOf(colletionName[i])!=-1){
					count1=count1+1;
				}
				else {
					count1=count1+0;
				}
			}
//			for(int i=0;i<commandSet.length;i++) {
//				if(params.indexOf(commandSet[i])!=-1){
//					count2=count2+1;
//				}
//				else
//					count2=count2+0;
//			}
			if((!hasCrossScriptRisk(params,regx))&&count1>0) {
				RpcResponse<Map<String, Object>> mongoQueryResultInfo=mysqlDataQueryService.getMongoQueryResultInfo(str);
				if(mongoQueryResultInfo.isSuccess()) {
					logger.info("[getMongoQueryResultInfo()->success:数据查询成功]");
					return ResultBuilder.successResult(mongoQueryResultInfo.getSuccessValue(), mongoQueryResultInfo.getMessage());
				}
				logger.error("[getMongoQueryResultInfo()->fail:数据查询失败]");
				return ResultBuilder.failResult(mongoQueryResultInfo.getMessage());
			}
			logger.error("[getMongoQueryResultInfo()->fail:数据查询失败或者db超出权限！]");
			return ResultBuilder.failResult("查询失败");
			
		}catch(Exception e) {
			logger.error("getMongoQueryResultInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
		
	}
		
	/**
	 * 正则表达式判断命令集
	* @Description:
	* @param qString
	* @param regx
	* @return
	 */
	public static boolean hasCrossScriptRisk(String qString, String regx) {

		Pattern pattern = Pattern.compile(regx);
		return pattern.matcher(qString).matches();
}

	
}
