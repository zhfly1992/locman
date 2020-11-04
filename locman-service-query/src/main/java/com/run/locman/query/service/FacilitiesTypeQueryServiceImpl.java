/*
 * File name: FacilitiesTypeQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tm 2017年08月09日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.constants.common.ResultMsgConstants;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.FacilitiesType;
import com.run.locman.api.query.repository.FacilitiesTypeQueryRepository;
import com.run.locman.api.query.service.FacilitiesTypeQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年08月09日
 */
@Repository
public class FacilitiesTypeQueryServiceImpl implements FacilitiesTypeQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesTypeQueryRepository	facilitiesTypeQueryRepository;

	private static final List<String> FAC_TYPE_LIST = Lists.newArrayList("雨水","污水","雨水篦子");
	
	private static final String YU_SHUI_BI_ZI = "雨水篦子";

	@Override
	public RpcResponse<List<FacilitiesType>> findAllFacilities(String accessSecret) {
		logger.info(String.format("[findAllFacilities()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			List<FacilitiesType> facilitiesTypeList = facilitiesTypeQueryRepository.findAllType(accessSecret);
			if (CollectionUtils.isEmpty(facilitiesTypeList)) {
				logger.warn("[findAllFacilities()->invalid：设施类型集合为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施类型信息：设施类型集合为空！ ");
			}
			logger.info(String.format("[findAllFacilities()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(ResultMsgConstants.INFO_SEARCH_SUCCESS, facilitiesTypeList);
		} catch (Exception e) {
			logger.error("findAllFacilities()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<FacilitiesType> queryFacilitiesById(String id) {
		logger.info(String.format("[queryFacilitiesById()方法执行开始...,参数：【%s】]", id));
		if (StringUtils.isEmpty(id)) {
			logger.debug("[queryFacilitiesById()->invalid：设施类型ID不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("设施类型信息：设施类型ID不能为空");
		}
		try {
			FacilitiesType facilitiesType = facilitiesTypeQueryRepository.findById(id);
			logger.info(String.format("[queryFacilitiesById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("信息查询成功", facilitiesType);
		} catch (Exception e) {
			logger.error("queryFacilitiesById()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<PageInfo<FacilitiesType>> getFacilitiesPage(String accessSecret, int pageNum, int pageSize,
			Map<String, String> param) {
		logger.info(String.format("[getFacilitiesPage()方法执行开始...,参数：【%s】【%s】【%s】【%s】]", accessSecret, pageNum, pageSize,
				param));
		try {
			if (accessSecret == null) {
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空");
			}

			if (param != null) {
				param.put("accessSecret", accessSecret);
			} else {
				param = new HashMap<>(16);
				param.put("accessSecret", accessSecret);
			}

			PageHelper.startPage(pageNum, pageSize);
			List<FacilitiesType> facilitiesTypeListPage = facilitiesTypeQueryRepository
					.queryFacilitiesTypeListPage(param);
			if (facilitiesTypeListPage != null) {
				PageInfo<FacilitiesType> pageInfo = new PageInfo<>(facilitiesTypeListPage);
				logger.info(String.format("[getFacilitiesPage()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, pageInfo);
			}
			logger.info(String.format("[getFacilitiesPage()方法执行结束!]"));
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
		} catch (Exception e) {
			logger.error("getFacilitiesPage()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesTypeQueryService#checkFacilitiesTypeName(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkFacilitiesTypeName(String facilitiesTypeName, String accessSecret, String facilityTypeId) {
		logger.info(
				String.format("[checkFacilitiesTypeName()方法执行开始...,参数：【%s】【%s】]", facilitiesTypeName, accessSecret));
		try {
			if (StringUtils.isBlank(facilitiesTypeName)) {
				logger.error("[checkFacilitiesTypeName()->invalid：设施类型名称不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施类型名称不能为空！ ");
			}
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[checkFacilitiesTypeName()->invalid：接入方秘钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空！ ");
			}
			
			Integer count = facilitiesTypeQueryRepository.checkFacilitiesTypeName(facilitiesTypeName, accessSecret, facilityTypeId);
			if (count == 0) {
				logger.info(String.format("[getFacilitiesPage()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, Boolean.FALSE);
			}
			logger.info(String.format("[getFacilitiesPage()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, Boolean.TRUE);
		} catch (Exception e) {
			logger.error("getFacilitiesPage()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesTypeQueryService#findAllFacilitiesTypeAndNum(java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, String>>> findAllFacilitiesTypeAndNum(String accessSecret) {
		logger.info(String.format("[checkFacilitiesTypeName()方法执行开始...,参数accessSecret：【%s】]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[findAllFacilitiesTypeAndNum()->invalid：接入方秘钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空！ ");
			}
			
			List<Map<String, String>> resultMap = facilitiesTypeQueryRepository.findAllFacilitiesTypeAndNum(accessSecret);
			if (resultMap == null ) {
				logger.error("[findAllFacilitiesTypeAndNum()-->查询接入方设施类型及数量失败,返回值为null]");
				return RpcResponseBuilder.buildErrorRpcResp("查询接入方设施类型及数量失败");
			} else {
				logger.info(String.format("[findAllFacilitiesTypeAndNum()-->查询成功!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, resultMap);
			}
			
		} catch (Exception e) {
			logger.error("findAllFacilitiesTypeAndNum()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesTypeQueryService#findAllFacTypeAndDeviceTypeNum(java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> findAllFacTypeAndDeviceTypeNum(String accessSecret) {
		logger.info(String.format("[findAllFacTypeAndDeviceTypeNum()方法执行开始...,参数accessSecret：【%s】]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[findAllFacTypeAndDeviceTypeNum()->invalid：接入方秘钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空！ ");
			}
			
			List<Map<String, Object>> total = facilitiesTypeQueryRepository.findAllFacTypeAndDeviceTypeNum(accessSecret, "");
			if (total == null ) {
				logger.error("[findAllFacTypeAndDeviceTypeNum()-->查询接入方设施类型及数量失败,返回值为null]");
				return RpcResponseBuilder.buildErrorRpcResp("查询接入方设施类型及数量失败");
			}
			
			List<Map<String, Object>> bound = facilitiesTypeQueryRepository.findAllFacTypeAndDeviceTypeNum(accessSecret, "bound");
			if (bound == null ) {
				logger.error("[findAllFacTypeAndDeviceTypeNum()-->查询接入方设施类型及数量失败,返回值为null]");
				return RpcResponseBuilder.buildErrorRpcResp("查询接入方设施类型及数量失败");
			}
			
			List<Map<String, Object>>  resultList = Lists.newArrayList();
			for (Map<String, Object> totalMap : total) {
				String facilityType = totalMap.get("facilityType") + "";
				Map<String, Object> resultMap = Maps.newHashMap();
				boolean isContainsStr = false;
				
				resultMap.put("boundNum", 0);
				resultMap.put("totalNum", totalMap.get("totalNum"));
				resultMap.put("facilityType", facilityType);
				
				for (Map<String, Object> boundMap : bound) {
					String boundFacilityType = boundMap.get("facilityType") + "";
					/*String boundFacilityType = boundMap.get("facilityType") + "";
					boolean flaga = facilityType.equals(boundFacilityType) && FAC_TYPE_LIST.contains(facilityType);
					if (flaga) {
						if ("雨水".equals(facilityType)) {
							Object totalNumObjectB = boundMap.get("totalNum");
							//设备数量
							int totalNumB = Integer.parseInt(totalNumObjectB + "") + 216;
							Object totalNumObjectT = totalMap.get("totalNum");
							int totalNumT = Integer.parseInt(totalNumObjectT + "") + 216;
							resultMap.put("boundNum", totalNumB);
							resultMap.put("totalNum", totalNumT);
							resultMap.put("facilityType", facilityType);
							resultList.add(resultMap);
						} else {
							resultMap.put("boundNum", boundMap.get("totalNum"));
							resultMap.put("totalNum", totalMap.get("totalNum"));
							resultMap.put("facilityType", facilityType);
							resultList.add(resultMap);
						}
						
					}*/
					if (facilityType.equals(boundFacilityType)) {
						resultMap.put("boundNum", boundMap.getOrDefault("totalNum", 0));
					}
					
					if (boundMap.containsValue(YU_SHUI_BI_ZI)) {
						isContainsStr = true;
					}
					
					
				}
				
				resultList.add(resultMap);
				
				boolean flagb = (YU_SHUI_BI_ZI.equals(facilityType) && !isContainsStr);
				if (flagb) {
					resultMap.put("boundNum", "");
					resultMap.put("totalNum", totalMap.get("totalNum"));
					resultMap.put("facilityType", facilityType);
					resultList.add(resultMap);
				}
				
			}
			
			
			logger.info(String.format("[findAllFacTypeAndDeviceTypeNum()-->查询成功!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, resultList);
			
		} catch (Exception e) {
			logger.error("findAllFacTypeAndDeviceTypeNum()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
