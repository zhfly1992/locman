/*
 * File name: AreaQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年6月20日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.AreaQueryRepository;
import com.run.locman.api.query.service.AreaQueryService;
import com.run.locman.constants.AreaConstants;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年6月20日
 */

public class AreaQueryServiceImpl implements AreaQueryService {

	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	AreaQueryRepository			areaQueryRepository;
	
	@Autowired
	private MongoTemplate		mongoTemplateUsc;



	/**
	 * @see com.run.locman.api.query.service.AreaQueryService#getAreaByCode(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getAreaByCode(JSONObject areaCode) {
		logger.info(String.format("[getAreaByCode()方法执行开始...,参数：【%s】]", areaCode));
		try {
			if (areaCode == null) {
				logger.error("[getAreaByCode()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (StringUtils.isBlank(areaCode.getString(AreaConstants.TYPE))) {
				logger.error("[getAreaByCode()->invalid：查询省市县类型type不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询省市县类型type不能为空!");
			}
			if (StringUtils.isBlank(areaCode.getString(AreaConstants.AREA_CODE))) {
				logger.error("[getAreaByCode()->invalid：查询省市县区域码areaCode不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询省市县区域码areaCode不能为空!");
			}

			StringBuffer content = new StringBuffer();
			JSONObject json = new JSONObject();
			// 省
			if (areaCode.getString(AreaConstants.TYPE).equals(AreaConstants.PROVINCE)) {
				content.append("%");
				content.append(areaCode.getString("areaCode"));
				json.put("areaCode1", content.toString());
				json.put("areaCode2", null);
				// 市
			} else if (areaCode.getString(AreaConstants.TYPE).equals(AreaConstants.CITY)) {
				content.append(areaCode.getString("areaCode"));
				content.append("__00");
				json.put("areaCode1", content.toString());
				json.put("areaCode2", areaCode.getString("areaCode") + "0000");
				// 区县
			} else if (areaCode.getString(AreaConstants.TYPE).equals(AreaConstants.AREA)) {
				content.append(areaCode.getString("areaCode"));
				content.append("__");
				json.put("areaCode1", content.toString());
				json.put("areaCode2", areaCode.getString("areaCode") + "00");
			}

			List<Map<String, Object>> areaByCode = areaQueryRepository.getAreaByCode(json);
			if (null != areaByCode && areaByCode.size() != 0) {
				logger.info("[getAreaByCode()->success：查询成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", areaByCode);
			}
			logger.info("[getAreaByCode()->fail：查询失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("查询失败!");

		} catch (Exception e) {
			logger.error("getAreaByCode()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> getAreaInfo(String accessSecret) {
		logger.info(String.format("[getAreaInfo()——>方法开始执行，accessSecret：%s]", accessSecret));
		
		try {
			if(StringUtils.isBlank(accessSecret)) {
				logger.error("[getAreaInfo()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			Map<String, Object> areaInfo = areaQueryRepository.getAreaInfo(accessSecret);
			if(areaInfo !=null && areaInfo.size() >0) {
				logger.info("[getAreaInfo()->success：查询成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功！", areaInfo);
			}
			logger.error("[getAreaInfo()->fail：查询失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("查询失败！");
			
			
		}catch(Exception e) {
			logger.error("getAreaInfo()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AreaQueryService#getAccessInfoByUrl(java.lang.String)
	 */
	@Override
	public RpcResponse<String> getAccessInfoByUrl(String urlStr) {
		logger.info(String.format("[getAccessInfoByUrl()——>方法开始执行，urlStr：%s]", urlStr));
		
		try {
			if(StringUtils.isBlank(urlStr)) {
				logger.error("[getAccessInfoByUrl()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("地址参数不能为空！");
			}
			String areaInfo = areaQueryRepository.getAccessInfoByUrl(urlStr);
			if(StringUtils.isNotBlank(areaInfo)) {
				logger.info(String.format("[getAccessInfoByUrl()->查询成功：%s]", areaInfo));
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功！", areaInfo);
			} else {
				logger.error("[getAccessInfoByUrl()->fail：没有查询到相关信息!]");
				return RpcResponseBuilder.buildErrorRpcResp("没有查询到相关信息！");
			}
			
		}catch(Exception e) {
			logger.error("getAccessInfoByUrl()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AreaQueryService#getCrossedMapInfo(java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> getCrossedMapInfo(String accessSecret) {
		logger.info(String.format("[getCrossedMapInfo()——>方法开始执行，accessSecret：%s]", accessSecret));
		try {
			
			Criteria criteria =Criteria.where("_id").is(accessSecret);
			Query query = new Query(criteria);
			JSONObject findOne = mongoTemplateUsc.findOne(query, JSONObject.class, "AccessInfo");
			
			
			if(null !=findOne && findOne.size()>0) {
					Map<String, Object> areaInfo = areaQueryRepository.getAreaInfo(accessSecret);
					for(String key:areaInfo.keySet()) {
						findOne.put(key, areaInfo.get(key));
					}
					return RpcResponseBuilder.buildSuccessRpcResp("区域划线地图查询成功！", findOne);
					
			}
			return RpcResponseBuilder.buildErrorRpcResp("没有查询到相关信息！");
		}catch(Exception e) {
			logger.error("getCrossedMapInfo()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
