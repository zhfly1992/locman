/*
* File name: updateRedisCrudServiceImpl.java								
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
* 1.0			zhongbinyuan		2020年8月14日
* ...			...			...
*
***************************************************/

package com.run.locman.service.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FacilitiesCrudRepository;
import com.run.locman.api.crud.service.UpdateRedisCrudService;
import com.run.locman.constants.CommonConstants;

/**
* @Description:	
* @author: zhongbinyuan
* @version: 1.0, 2020年8月14日
*/

public class UpdateRedisCrudServiceImpl implements UpdateRedisCrudService{
	
	private static final Logger							logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private FacilitiesCrudRepository		facilitiesCrudRepository;
	@Autowired
	@Qualifier("functionDomainRedisTemplate")
	private RedisTemplate<String,String>  redisTemplate;
	
	@Autowired
	@Qualifier("facilityMapRedisTemplate")
	private RedisTemplate<String,Map<String,Object>> redisTemplateMap;
	
	private String 	judgmentExpired="judgmentExpired:";
	
	private Long timeout=24*60*60L;
	
	private String hashKey="fac:";

	/**
	 * @see com.run.locman.api.crud.service.UpdateRedisCrudService#updateFacMapCache(java.lang.String)
	 */
	@Override
	public RpcResponse<String> updateFacMapCache(Map<String,Object> map) {
		
//		logger.info(String.format("开始执行设施ID为%s的缓存操作", map));
		try {
			String id=hashKey+map.get("id");
			String accessSecret=map.get("accessSecret")+"";
			if(StringUtils.isBlank(id) || StringUtils.isBlank(accessSecret)) {
				logger.error("[updateFacMapCache()->error: 缓存失败，基础参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("缓存失败，基础参数不能为空");
			}
			//根据设施ID查询需要缓存的信息
			Map<String, Object> findInfoToCache = facilitiesCrudRepository.findInfoToCache(map);
			if(null != findInfoToCache && findInfoToCache.size()>0) {
				Map<String, Object> map1 = redisTemplateMap.opsForValue().get(id);
				if(null ==map1 || map1.size()==0) {
					redisTemplate.opsForList().rightPush("ch:"+"facID:"+accessSecret, id);
				}
				redisTemplateMap.opsForValue().set(id, findInfoToCache);
				redisTemplate.opsForValue().set(judgmentExpired+accessSecret, accessSecret, timeout, TimeUnit.SECONDS);
				logger.info("[updateFacMapCache()->缓存成功！设施号为:"+ id);
				return RpcResponseBuilder.buildSuccessRpcResp("缓存成功！设施号为:", id);
			}
			logger.error("[updateFacMapCache()->error: 缓存失败，查询需要缓存的信息失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("查询需要缓存的信息失败!");
		}catch(Exception e) {
			logger.error("updateFacMapCache()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		
	}

	/**
	 * @see com.run.locman.api.crud.service.UpdateRedisCrudService#batchUpdateFacMapCache(java.util.List, java.lang.String)
	 */
	@Override
	public RpcResponse<String> batchUpdateFacMapCache(List<String> facIds, String accessSecret) {
		logger.info(String.format("需要批量缓存的设施数为：", facIds.size()));
		try {
			if(null ==facIds || facIds.size()<=0) {
				logger.error("[batchUpdateFacMapCache()->error: 缓存失败，基础参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("缓存失败，基础参数不能为空");
			}
			if(StringUtils.isBlank(accessSecret)) {
				logger.error("[batchUpdateFacMapCache()->error: 缓存失败，基础参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("缓存失败，基础参数不能为空");
			}
			//根据设施ID查询需要缓存的信息
			List<Map<String, Object>> findInfoToCache = facilitiesCrudRepository.findBatchInfoToCache(facIds, accessSecret);
			List<String> arrayList = new ArrayList<String>();
			if(null != findInfoToCache && findInfoToCache.size()>0) {
				for(Map<String,Object> map:findInfoToCache) {
					String id=hashKey+map.get("id");
					Map<String, Object> map1 = redisTemplateMap.opsForValue().get(id);
					if(null ==map1 || map1.size()==0) {
						redisTemplate.opsForList().rightPush("ch:"+"facID:"+accessSecret, id);
					}
					redisTemplateMap.opsForValue().set(id, map);
					redisTemplate.opsForValue().set(judgmentExpired+accessSecret, accessSecret, timeout, TimeUnit.SECONDS);
					arrayList.add(id);
				}
				logger.info("[batchUpdateFacMapCache()->批量缓存成功！总数为:"+ arrayList.size());
				return RpcResponseBuilder.buildSuccessRpcResp("批量缓存成功！总数为:", arrayList.size()+"");
			}
			logger.error("[batchUpdateFacMapCache()->error: 批量缓存失败，查询需要缓存的信息失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("查询需要缓存的信息失败!");
		}catch(Exception e) {
			logger.error("batchUpdateFacMapCache()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
