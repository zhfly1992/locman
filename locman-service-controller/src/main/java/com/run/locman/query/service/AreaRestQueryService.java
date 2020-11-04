/*
 * File name: AreaRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年6月21日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.AreaQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.usc.api.base.util.ParamChecker;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年6月21日
 */
@Service
public class AreaRestQueryService {

	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	AreaQueryService			areaQueryService;



	public Result<List<Map<String, Object>>> getAreaByCode(String areaCode) {
		logger.info(String.format("[getAreaByCode()->request params--areaCode:%s]", areaCode));
		if (ParamChecker.isNotMatchJson(areaCode)) {
			logger.error("[getAreaByCode()->warn：传参格式错误或为空!]");
			return ResultBuilder.invalidResult();
		}
		JSONObject paramJson = JSONObject.parseObject(areaCode);
		try {
			RpcResponse<List<Map<String, Object>>> areaByCode = areaQueryService.getAreaByCode(paramJson);
			if (!areaByCode.isSuccess()) {
				logger.error("[getAreaByCode()->fail：" + areaByCode.getMessage() + "]");
				return ResultBuilder.failResult(areaByCode.getMessage());
			}
			logger.info("[getAreaByCode()->success：" + areaByCode.getMessage() + "]");
			return ResultBuilder.successResult(areaByCode.getSuccessValue(), areaByCode.getMessage());
		} catch (Exception e) {
			logger.error("getAreaByCode()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	public Result<Map<String ,Object>> getAreaInfo(String accessSecret){
		
		logger.info(String.format("[getAreaInfo()->request params--accessSecret:%s]", accessSecret));
		try {
			RpcResponse<Map<String, Object>> areaInfo = areaQueryService.getAreaInfo(accessSecret);
			if(areaInfo.isSuccess()) {
				logger.info("[getAreaInfo()->success：" + areaInfo.getMessage() + "]");
				return ResultBuilder.successResult(areaInfo.getSuccessValue(),"查询成功！");
			}
			logger.error("[getAreaInfo()->fail：" + areaInfo.getMessage() + "]");
			return ResultBuilder.failResult(areaInfo.getMessage());
		}catch(Exception e){
			logger.error("getAreaInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
			
		}
	}


	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public Result<String> getAccessInfoByUrl(String urlStr) {

		logger.info(String.format("[getAccessInfoByUrl()->request params--urlStr:%s]", urlStr));
		try {
			RpcResponse<String> accessInfo = areaQueryService.getAccessInfoByUrl(urlStr);
			if(accessInfo.isSuccess()) {
				logger.info("[getAreaInfo()->success：" + accessInfo.getMessage() + "]");
				return ResultBuilder.successResult(accessInfo.getSuccessValue(),"查询成功！");
			}
			logger.error("[getAreaInfo()->fail：" + accessInfo.getMessage() + "]");
			return ResultBuilder.failResult(accessInfo.getMessage());
		}catch(Exception e){
			logger.error("getAreaInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
			
		}
	}
	
	public Result<JSONObject> getCrossedMapInfo(String accessSecret){
		logger.info(String.format("getCrossedMapInfo()->info:%s", accessSecret));
		try {
			if(StringUtils.isBlank(accessSecret)) {
				return ResultBuilder.failResult("接入方密钥不能为空！");
			}
			RpcResponse<JSONObject> crossedMapInfo = areaQueryService.getCrossedMapInfo(accessSecret);
			if(crossedMapInfo.isSuccess()) {
				return ResultBuilder.successResult(crossedMapInfo.getSuccessValue(), "查询成功！");
			}
			return ResultBuilder.failResult("查询失败！");
		}catch(Exception e) {
			return ResultBuilder.exceptionResult(e);
		}
		
	}

}
