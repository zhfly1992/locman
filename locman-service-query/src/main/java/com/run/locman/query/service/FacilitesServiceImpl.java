/*
 * File name: FacilitesServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年11月2日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.base.query.FacilitesService;
import com.run.locman.api.entity.FacilitiesDataType;
import com.run.locman.api.query.repository.FacilitiesQueryRepository;
import com.run.locman.api.query.service.FacilitiesDataTypeQueryRpcService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.FacilityDeviceContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description: 设施query实现类
 * @author: lkc
 * @version: 1.0, 2017年11月2日
 */
public class FacilitesServiceImpl implements FacilitesService {
	private static Logger				logger		= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesQueryRepository	facilitesRepository;

	@Autowired
	private FacilitiesQueryRepository	facilitiesQueryRepository;

	@Autowired
	FacilitiesDataTypeQueryRpcService	facilitiesDataTypeQueryRpcService;

	@Value("${api.host}")
	private String						ip;

/*	private static final List<Object>	serchKeys	= Lists.newArrayList("5101080", "510108", "101080", "51010",
			"10108", "01080", "5101", "1010", "0108", "1080", "510", "101", "010", "108", "080", "51", "10", "01", "10",
			"08", "80", "5", "1", "8", "0", "cd2", "cd", "c", "d", "2",
			// 12
			"000866971033",
			// 11
			"00086697103", "00866971033",
			// 10
			"0008669710", "008669710", "086697103",
			// 9
			"000866971", "008669710", "086697103", "866971033",
			// 8
			"00086697", "00866971", "08669710", "86697103", "66971033", "", "", "", "",
			// 7
			"0008669", "0086697", "0866971", "8669710", "6697103", "6971033", "", "", "",
			// 6
			"000866", "008669", "08669", "866971", "669710", "697103", "971033", "", "",
			// 5
			"00086", "00866", "08669", "86697", "66971", "69710", "97103", "71033", "",
			// 4
			"0008", "0086", "0866", "8669", "6697", "6971", "9710", "7103", "1033", "", "", "",
			// 3
			"000", "008", "086", "866", "669", "697", "971", "710", "103", "033", "", "",
			// 2
			"00", "08", "86", "66", "69", "97", "71", "10", "33", "6", "9", "7", "3");

*/
	/**
	 * @Description 分页查询设施列表
	 */

	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getFacilitesInfoByPage(JSONObject pageObj) {
		logger.info(String.format("[getFacilitesInfoByPage()方法执行开始...,参数：【%s】]", pageObj));
		try {
			if (pageObj == null) {
				logger.error(String.format("[getFacilitesInfoByPage()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey(FacilitiesContants.USC_ACCESS_SECRET)) {
				logger.error(String.format("[getFacilitesInfoByPage()->error：%s%s",
						FacilitiesContants.USC_ACCESS_SECRET, MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder
						.buildErrorRpcResp(FacilitiesContants.USC_ACCESS_SECRET + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey(FacilitiesContants.PAGE_SIZE)) {
				logger.error(String.format("[getFacilitesInfoByPage()->error：%s%s", FacilitiesContants.PAGE_SIZE,
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder
						.buildErrorRpcResp(FacilitiesContants.PAGE_SIZE + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey(FacilitiesContants.PAGE_NO)) {
				logger.error(String.format("[getFacilitesInfoByPage()->error：%s%s", FacilitiesContants.PAGE_NO,
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(FacilitiesContants.PAGE_NO + MessageConstant.PARAM_IS_NULL);
			}
			if (!StringUtils.isNumeric(pageObj.getString(FacilitiesContants.PAGE_SIZE))
					|| !StringUtils.isNumeric(pageObj.getString(FacilitiesContants.PAGE_NO))) {
				logger.error(String.format("[getFacilitesInfoByPage()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_NOT_IS_NUM);
			}

			String accessSecret = pageObj.getString(FacilitiesContants.USC_ACCESS_SECRET);
			int pageNo = pageObj.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = pageObj.getIntValue(FacilitiesContants.PAGE_SIZE);

			// TODO 设备数量1
			/*int returnPageNo = 0;
			int ceil = 0;
			if (pageSize != 0) {
				// 设置保留位数
				DecimalFormat df = new DecimalFormat("0.00");
				ceil = (int) Math.ceil(Double.parseDouble(df.format((float) 216 / pageSize))) - 1;
			}
			if (pageNo > 36) {
				returnPageNo = pageNo;
				pageNo -= ceil;
			}*/

			PageHelper.startPage(pageNo, pageSize);
			Map<String, Object> params = new HashMap<>(16);
			params.put("alarmDesc", pageObj.getString("alarmDesc"));
			params.put(FacilitiesContants.USC_ACCESS_SECRET, accessSecret);
			params.put(FacilitiesContants.FAC_BINGSTATUS, pageObj.getString(FacilitiesContants.FAC_BINGSTATUS));
			params.put(FacilitiesContants.FACILITIES_TYPE_ID, pageObj.getString(FacilitiesContants.FACILITIES_TYPE_ID));
			params.put(FacilityDeviceContants.WHOLE, pageObj.getString(FacilityDeviceContants.WHOLE));

			params.put("sortField", pageObj.getString("sortField"));
			
			if (!StringUtils.isBlank(pageObj.getString(AlarmInfoConstants.ALARMI_LEVEL))) {
				params.put(AlarmInfoConstants.ALARMI_LEVEL, pageObj.getString(AlarmInfoConstants.ALARMI_LEVEL));
			}
			if (!StringUtils.isBlank(pageObj.getString(FacilitiesContants.DEFENSE_STATE))) {
				params.put(FacilitiesContants.DEFENSE_STATE, pageObj.getString(FacilitiesContants.DEFENSE_STATE));
			}

			if (!StringUtils.isBlank(pageObj.getString(FacilitiesContants.FAC_MANAGESTATE))) {
				params.put("manageState", pageObj.getString(FacilitiesContants.FAC_MANAGESTATE));
			}
			if (pageObj.containsKey(FacilitiesContants.SEARCH_KEY)) {
				params.put(FacilitiesContants.SEARCH_KEY, pageObj.getString(FacilitiesContants.SEARCH_KEY));
			}
			String ip = pageObj.getString(InterGatewayConstants.IP);
			String token = pageObj.getString(InterGatewayConstants.TOKEN);
			// 按组织搜索时
			// 存储组织id,name
			// Map<String, String> organizationInfo = Maps.newHashMap();
			// 存储组织id
			List<String> organizationIdList = Lists.newArrayList();
			if (!StringUtils.isBlank(pageObj.getString(FacilitiesContants.ORGANIZATION_ID))) {
				queryOrganzationInfoByOrgId(pageObj, ip, token, organizationIdList);
			}
			// 有组织id,封装搜索参数
			if (organizationIdList.size() != 0) {
				params.put(AlarmInfoConstants.ORG_ID, organizationIdList);
			}
			List<Map<String, Object>> page = facilitesRepository.findFacByPage(params);
			// 封装组织信息
			pageOrg(page, ip, accessSecret, token);
			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			

			/*String facilitiesTypeId = pageObj.getString(FacilitiesContants.FACILITIES_TYPE_ID);

			info.setTotal(info.getTotal() + 216);

			info.setPages(info.getPages() + ceil);

			if (returnPageNo > 36) {
				info.setPageNum(returnPageNo);
			}

			String serchKey = pageObj.getString(FacilitiesContants.SEARCH_KEY);
			if ("unbound".equals(pageObj.getString(FacilitiesContants.FAC_BINGSTATUS))
					|| (StringUtils.isNotBlank(facilitiesTypeId)
							&& !"8d4595dd5fcd4530b8cca9c9e01818d6".equals(facilitiesTypeId))
					|| "disable".equals(pageObj.getString(FacilitiesContants.FAC_MANAGESTATE))
					|| ((StringUtils.isNotBlank(pageObj.getString(AlarmInfoConstants.ALARMI_LEVEL))
							&& !"999".equals(pageObj.getString(AlarmInfoConstants.ALARMI_LEVEL)))
							|| StringUtils.isNotBlank(pageObj.getString(FacilitiesContants.ORGANIZATION_ID))
							|| (StringUtils.isNotBlank(serchKey) && !serchKeys.contains(serchKey))
							|| "1".equals(pageObj.getString(FacilitiesContants.DEFENSE_STATE))
							|| "2".equals(pageObj.getString(FacilitiesContants.DEFENSE_STATE))
							|| "3".equals(pageObj.getString(FacilitiesContants.DEFENSE_STATE))
							|| "4".equals(pageObj.getString(FacilitiesContants.DEFENSE_STATE))
							|| "5".equals(pageObj.getString(FacilitiesContants.DEFENSE_STATE))
							|| StringUtils.isNotBlank(pageObj.getString("alarmDesc")))
				) {
				info.setTotal(info.getTotal() - 216);

				info.setPages(info.getPages() - ceil);
			}*/

			logger.info(String.format("[getFacilitesInfoByPage()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getFacilitesInfoByPage()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @Description:
	 * @param pageObj
	 * @param ip
	 * @param token
	 * @param organizationIdList
	 */

	@SuppressWarnings("rawtypes")
	private void queryOrganzationInfoByOrgId(JSONObject pageObj, String ip, String token,
			List<String> organizationIdList) {
		String organizationId = pageObj.getString(FacilitiesContants.ORGANIZATION_ID);
		if (!StringUtils.isBlank(ip)) {
			String httpValueByGet = InterGatewayUtil
					.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip, token);
			if (null == httpValueByGet) {
				logger.error("[getAlarmInfoList()->invalid：组织查询失败!]");
				organizationIdList.add(organizationId);
			} else {
				JSONArray jsonArray = JSON.parseArray(httpValueByGet);
				List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
				for (Map map : organizationInfoList) {
					organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
				}
			}
		} else {
			// 获取IP失败或者为null则不访问
			logger.error("[getAlarmInfoList()->invalid：IP获取失败,组织查询失败!]");
			organizationIdList.add(organizationId);
		}
	}



	/**
	 * 
	 * @Description:封装组织信息
	 * @param facInfo
	 * @param ip
	 * @param accessSecret
	 * @param token
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void pageOrg(List<Map<String, Object>> facInfo, String ip, String accessSecret, String token) {
		logger.info(String.format("[pageOrg()方法执行开始...,参数：【%s】【%s】【%s】【%s】]", facInfo, ip, accessSecret, token));
		// if (!StringUtils.isBlank(ip)) {
		// 查询所有组织信息
		String httpValueByGet = InterGatewayUtil.getHttpValueByGet(InterGatewayConstants.U_ORGANIZATIONS + accessSecret,
				ip, token);
		// 如果查询失败,组织信息为空
		if (null == httpValueByGet) {
			logger.error("[getAlarmInfoList()->invalid：组织查询失败!]");
			for (Map<String, Object> map : facInfo) {
				if (map.containsKey(FacilitiesContants.ORGANIZATION_ID)) {
					map.put(FacilitiesContants.ORG_INFO, "");
				}
			}
		} else {
			JSONArray jsonArray = JSON.parseArray(httpValueByGet);
			List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
			// 参数名转换
			for (Map organizationInfo : organizationInfoList) {
				organizationInfo.put(FacilitiesContants.ORGANIZATION_NAME,
						organizationInfo.remove(InterGatewayConstants.ORGANIZATION_NAME));
			}
			// 封装对应的组织信息
			for (Map<String, Object> organizationInfo : facInfo) {
				String orgId = "" + organizationInfo.get(FacilitiesContants.ORGANIZATION_ID);
				Object organizationInfoStr = UtilTool.getStringByKey(organizationInfoList,
						InterGatewayConstants.ORGANIZATION_ID, orgId);
				if (organizationInfoStr != null && !StringUtils.isBlank("" + organizationInfoStr)) {
					organizationInfo.put(FacilitiesContants.ORG_INFO, organizationInfoStr);
				} else {
					organizationInfo.put(FacilitiesContants.ORG_INFO, "");
				}
			}
		}
		logger.info(String.format("[pageOrg()方法执行结束!]"));
	} /*
		 * else { //没有进行查询,组织信息为空
		 * logger.error("[getAlarmInfoList()->invalid：组织查询失败!]"); for
		 * (Map<String, Object> organizationInfo : facInfo) {
		 * organizationInfo.put(FacilitiesContants.ORG_INFO, ""); } } }
		 */

	/*
	 * RpcResponse<List<Map>> orgInfo = accSourceQuery
	 * .getListMenuByIds(UtilTool.getListNoRepeat(facInfo,
	 * FacilitiesContants.ORGANIZATION_ID)); if (orgInfo.isSuccess()) {
	 * List<Map> orgLists = orgInfo.getSuccessValue(); for (Map<String, Object>
	 * map : facInfo) { if (map.containsKey(FacilitiesContants.ORGANIZATION_ID))
	 * { String orgId = (String) map.get(FacilitiesContants.ORGANIZATION_ID);
	 * map.put(FacilitiesContants.ORG_INFO, UtilTool.getStringByKey(orgLists,
	 * FacilitiesContants._ID, orgId)); } } } }
	 */



	/**
	 * 
	 * @Description:封装组织信息
	 * @param map
	 * @param orgId
	 * @param token
	 */
	private void mapToOrg(Map<String, Object> map, String orgId, String token) {
		logger.info(String.format("[mapToOrg()方法执行开始...,参数：【%s】【%s】【%s】]", map, orgId, token));
		// 查询获取组织Name
		String httpValueByGet = InterGatewayUtil
				.getHttpValueByGet(InterGatewayConstants.U_ORGANIZATION_INFO_BYID + orgId, ip, token);
		if (null == httpValueByGet) {
			logger.error("[MapToOrg()->invalid：组织查询失败!]");
			map.put(FacilitiesContants.ORG_INFO, "");
		} else {
			JSONObject json = JSONObject.parseObject(httpValueByGet);

			map.put(FacilitiesContants.ORG_INFO, json);
		}
		logger.info(String.format("[mapToOrg()方法执行结束!]"));
		/*
		 * //已经改为用RestFul接口查询 List<String> list = new ArrayList<>();
		 * list.add(orgId); RpcResponse<List<Map>> orgInfo =
		 * accSourceQuery.getListMenuByIds(list); if (orgInfo.isSuccess()) {
		 * List<Map> orgLists = orgInfo.getSuccessValue(); if
		 * (map.containsKey(FacilitiesContants.ORGANIZATION_ID)) {
		 * map.put(FacilitiesContants.ORG_INFO,
		 * UtilTool.getStringByKey(orgLists, FacilitiesContants._ID, orgId)); }
		 * }
		 */
	}



	@Override
	public RpcResponse<Map<String, Object>> getFacilitesInfoByFacId(String id, String token) {
		logger.info(String.format("[getFacilitesInfoByFacId()方法执行开始...,参数：【%s】【%s】]", id, token));
		try {
			Map<String, Object> facInfo = facilitesRepository.findByFacId(id);

			if (facInfo != null && null != facInfo.get(FacilitiesContants.ORGANIZATION_ID)
					&& !StringUtils.isBlank("" + facInfo.get(FacilitiesContants.ORGANIZATION_ID))) {
				mapToOrg(facInfo, (String) facInfo.get(FacilitiesContants.ORGANIZATION_ID), token);
			}

			if (facInfo == null || facInfo.isEmpty()) {
				logger.error(String.format("[getFacilitesInfoByFacId()->error：%s", MessageConstant.INFO_NO_EXIT));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.INFO_NO_EXIT);
			} else {
				logger.info(
						String.format("[getFacilitesInfoByFacId()->success：%s", MessageConstant.INFO_SERCH_SUCCESS));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, facInfo);
			}
		} catch (Exception e) {
			logger.error("getFacilitesInfoByFacId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> getFacilitesInfo(String id, String accessSecret, String token) {
		logger.info(String.format("[getFacilitesInfo()方法执行开始...,参数：【%s】【%s】【%s】]", id, accessSecret, token));
		try {
			Map<String, Object> facInfo = facilitesRepository.findByFacId(id);
			if (null == facInfo) {
				logger.error("通过设施id查询设施信息查询结果为null");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.INFO_NO_EXIT);
			}
			Object extend = facInfo.get(FacilitiesContants.EXTEND);
			if (null == facInfo.get(FacilitiesContants.EXTEND) || StringUtils.isBlank("" + extend)) {
				logger.error("字段extend为null或为空");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			String ex = extend + "";
			JSONObject parseObject = JSON.parseObject(ex);
			Map<String, String> map = new HashMap<>(16);
			map.put(FacilitiesContants.FACILITIES_TYPE_ID, (String) facInfo.get(FacilitiesContants.FACILITIES_TYPE_ID));
			map.put(FacilitiesContants.USC_ACCESS_SECRET, accessSecret);
			// 调用RPC服务,不分页查詢所有元数据属性
			RpcResponse<List<FacilitiesDataType>> facilitiesDataTypeList = facilitiesDataTypeQueryRpcService
					.getAllFacilitiesDataType(map);
			List<JSONObject> json = new ArrayList<>();
			if (facilitiesDataTypeList != null && facilitiesDataTypeList.getSuccessValue() != null) {
				List<FacilitiesDataType> res = facilitiesDataTypeList.getSuccessValue();
				for (FacilitiesDataType fac : res) {
					JSONObject tem = new JSONObject();
					tem.put("name", fac.getName());
					String sign = fac.getSign();
					if (StringUtils.isBlank(sign)) {
						tem.put("value", "");
					} else {
						tem.put("value", parseObject.get(sign));
					}
					json.add(tem);
				}
			}
			facInfo.put("extendInfo", json);
			if (facInfo != null) {
				mapToOrg(facInfo, (String) facInfo.get(FacilitiesContants.ORGANIZATION_ID), token);
			}
			if (facInfo == null || facInfo.isEmpty()) {
				logger.error(String.format("[getFacilitesInfoByFacId()->error：%s", MessageConstant.INFO_NO_EXIT));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.INFO_NO_EXIT);
			} else {
				logger.info(
						String.format("[getFacilitesInfoByFacId()->success：%s", MessageConstant.INFO_SERCH_SUCCESS));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, facInfo);
			}
		} catch (Exception e) {
			logger.error("getFacilitesInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> getFacilitesInfoByfacCode(String code, String token) {
		logger.info(String.format("[getFacilitesInfoByfacCode()方法执行开始...,参数：【%s】【%s】]", code, token));
		try {
			Map<String, Object> facInfo = facilitesRepository.findByFacCode(code);
			if (facInfo != null) {
				mapToOrg(facInfo, (String) facInfo.get(FacilitiesContants.ORGANIZATION_ID), token);
			}
			if (facInfo == null || facInfo.isEmpty()) {
				logger.error(String.format("[getFacilitesInfoByfacCode()->error：%s", MessageConstant.INFO_NO_EXIT));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.INFO_NO_EXIT);
			} else {
				logger.info(
						String.format("[getFacilitesInfoByfacCode()->success：%s", MessageConstant.INFO_SERCH_SUCCESS));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, facInfo);
			}
		} catch (Exception e) {
			logger.error("getFacilitesInfoByfacCode()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> checkFacilitiesCode(String facilitiesCode, String accessSecret, String id) {
		logger.info(String.format("[checkFacilitiesCode()方法执行开始...,参数：【%s】【%s】]", facilitiesCode, accessSecret));
		try {
			if (StringUtils.isBlank(facilitiesCode) || StringUtils.isBlank(accessSecret)) {
				logger.error("[checkFacilitiesCode()->error：参数错误：设施序列号或接入方编号不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("设施序列号或接入方编号不能为空");
			}
			Map<String, Object> queryMap = Maps.newHashMap();
			queryMap.put(FacilitiesContants.FACILITIES_CODE, facilitiesCode);
			queryMap.put(FacilitiesContants.USC_ACCESS_SECRET, accessSecret);
			queryMap.put(FacilitiesContants.FACILITES_ID, id);
			int num = facilitesRepository.checkFacilitiesCode(queryMap);
			if (num > 0) {
				logger.info(String.format("[checkFacilitiesCode()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("该设施序列号已存在", false);
			}
			logger.info(String.format("[checkFacilitiesCode()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("该设施序列号可以使用", true);
		} catch (Exception e) {
			logger.error("checkFacilitiesCode()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.base.query.FacilitesService#countAlarmFacilities(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Integer> countAlarmFacilities(JSONObject paramJson) {
		logger.info(String.format("[rpc-countAlarmFacilities->request params:%s]", paramJson));
		if (StringUtils.isBlank(paramJson.getString(FacilitiesContants.USC_ACCESS_SECRET))) {
			logger.error("[countAlarmFacilities()->invalid：接入方秘钥不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
		}
		if (!paramJson.containsKey(FacilitiesContants.ORGANIZATION_ID)) {
			logger.error("[countAlarmFacilities()->invalid：组织id参数名不存在!]");
			return RpcResponseBuilder.buildErrorRpcResp("组织id参数名不存在!");
		}

		try {
			// 获取IP
			String token = paramJson.getString(InterGatewayConstants.TOKEN);
			// 存储组织id
			List<String> organizationIdList = Lists.newArrayList();
			// 如果有组织id且不为空,查询其组织信息及子组织信息,获取到id和name,存储起来.等待查询结果然后匹配
			if (paramJson.containsKey(FacilitiesContants.ORGANIZATION_ID)
					&& !StringUtils.isBlank(paramJson.getString(FacilitiesContants.ORGANIZATION_ID))) {
				String organizationId = paramJson.getString(FacilitiesContants.ORGANIZATION_ID);
				// 成功获取IP
				if (!StringUtils.isBlank(ip)) {
					String httpValueByGet = InterGatewayUtil
							.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip, token);
					if (null == httpValueByGet) {
						logger.error("[countAlarmFacilities()->invalid：组织查询失败!]");
						organizationIdList.add(organizationId);
					} else {
						JSONArray jsonArray = JSON.parseArray(httpValueByGet);
						List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
						for (Map map : organizationInfoList) {
							organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
						}
					}
				} else {
					// 获取IP失败或者为null则不访问
					logger.error("[countAlarmFacilities()->invalid：IP获取失败,无法进行组织查询!]");
					organizationIdList.add(organizationId);
				}

			}
			// 传入参数为json,不添加list会报错
			paramJson.put(FacilitiesContants.ORGANIZATION_ID, organizationIdList);

			int totalNum = facilitesRepository.countAlarmFacilities(paramJson);
			if (totalNum < 0) {
				logger.error("[countAlarmFacilities()->invalid：查询告警设施数量失败!!!");
				return RpcResponseBuilder.buildErrorRpcResp("查询告警设施数量失败!!!");
			} else {
				logger.info("[countAlarmFacilities()->invalid：查询告警设施数量成功!!!");
				return RpcResponseBuilder.buildSuccessRpcResp("查询告警设施数量成功!!!", totalNum);
			}

		} catch (Exception e) {
			logger.error("countAlarmFacilities()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.base.query.FacilitesService#findFacilityInfoByCodeAndAcc(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> findFacilityInfoByCodeAndAcc(String facilitiesCode, String accessSecret) {
		try {
			logger.info(String.format(
					"[rpc-findFacilityInfoByCodeAndAcc->request params:facilitiesCode:%s, accessSecret:%s]",
					facilitiesCode, accessSecret));
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[findFacilityInfoByCodeAndAcc()->invalid：接入方秘钥不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
			}
			if (StringUtils.isBlank(facilitiesCode)) {
				logger.error("[findFacilityInfoByCodeAndAcc()->invalid：设施序列号不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设施序列号不能为空!");
			}
			Map<String, Object> facilityInfo = facilitesRepository.findFacilityInfoByCodeAndAcc(facilitiesCode,
					accessSecret);
			if (facilityInfo.isEmpty() || facilityInfo == null) {
				logger.error("[findFacilityInfoByCodeAndAcc()->invalid：找不到符合条件的设施!]");
				return RpcResponseBuilder.buildErrorRpcResp("找不到符合条件的设施!");
			}
			Object facilityId = facilityInfo.get("facilityId");
			if (facilityId == null || "".equals(facilityId)) {
				logger.error("[findFacilityInfoByCodeAndAcc()->invalid：设施id为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("找不到符合条件的设施!");
			}
			List<Map<String, Object>> boundDeviceInfo = facilitiesQueryRepository.getBoundDeviceInfo(facilityId + "",
					accessSecret);
			if (null == boundDeviceInfo) {
				facilityInfo.put("boundDevices", Lists.newArrayList());
			} else {
				facilityInfo.put("boundDevices", boundDeviceInfo);
			}
			logger.info(String.format("[rpc-findFacilityInfoByCodeAndAcc->Response:facilityInfo:%s]", facilityInfo));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", facilityInfo);
		} catch (Exception e) {
			logger.error(String.format("[findFacilityInfoByCodeAndAcc()->Exception:%s]", e));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

	/**
	 * @see com.run.locman.api.base.query.FacilitesService#queryFacDefenseStateByOrganizationId(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	// @Override
	// public RpcResponse<Boolean> queryFacDefenseStateByOrganizationId(String
	// organizationId, String accessSecret,
	// String token) {
	// // TODO Auto-generated method stub
	// logger.info(
	// String.format("[queryFacDefenseStateByOrganizationId->进入方法,organizationId:%s,accessSecret:%s,token:%s]",
	// organizationId, accessSecret, token));
	// try {
	// if (StringUtils.isBlank(organizationId)) {
	// logger.error("[queryFacDefenseStateByOrganizationId->error,传入组织id为空]");
	// return RpcResponseBuilder.buildErrorRpcResp("组织id为空");
	// }
	// List<String> organizationIdList = Lists.newArrayList();
	// JSONObject jsonObject = new JSONObject();
	// jsonObject.put(FacilitiesContants.ORGANIZATION_ID, organizationId);
	// queryOrganzationInfoByOrgId(jsonObject, ip, token, organizationIdList);
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }
	
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> findFacilityRenovatedByPage(JSONObject pageObj) {

		logger.info(String.format("[facilityRenovatedByPage()方法执行开始...,参数：【%s】]", pageObj));
		try {
			if (pageObj == null) {
				logger.error(String.format("[findFacilityRenovatedByPage()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey(FacilitiesContants.USC_ACCESS_SECRET)) {
				logger.error(String.format("[findFacilityRenovatedByPage()->error：%s%s",
						FacilitiesContants.USC_ACCESS_SECRET, MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder
						.buildErrorRpcResp(FacilitiesContants.USC_ACCESS_SECRET + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey(FacilitiesContants.PAGE_SIZE)) {
				logger.error(String.format("[findFacilityRenovatedByPage()->error：%s%s", FacilitiesContants.PAGE_SIZE,
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder
						.buildErrorRpcResp(FacilitiesContants.PAGE_SIZE + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey(FacilitiesContants.PAGE_NO)) {
				logger.error(String.format("[findFacilityRenovatedByPage()->error：%s%s", FacilitiesContants.PAGE_NO,
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(FacilitiesContants.PAGE_NO + MessageConstant.PARAM_IS_NULL);
			}
			if (!StringUtils.isNumeric(pageObj.getString(FacilitiesContants.PAGE_SIZE))
					|| !StringUtils.isNumeric(pageObj.getString(FacilitiesContants.PAGE_NO))) {
				logger.error(String.format("[findFacilityRenovatedByPage()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_NOT_IS_NUM);
			}

			String accessSecret = pageObj.getString(FacilitiesContants.USC_ACCESS_SECRET);
			int pageNo = pageObj.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = pageObj.getIntValue(FacilitiesContants.PAGE_SIZE);

			PageHelper.startPage(pageNo, pageSize);
			Map<String, Object> params = Maps.newHashMap();
			params.put("alarmDesc", pageObj.getString("alarmDesc"));
			params.put(FacilitiesContants.USC_ACCESS_SECRET, accessSecret);

			if (!StringUtils.isBlank(pageObj.getString(FacilitiesContants.DEFENSE_STATE))) {
				params.put(FacilitiesContants.DEFENSE_STATE, pageObj.getString(FacilitiesContants.DEFENSE_STATE));
			}

			if (!StringUtils.isBlank(pageObj.getString(FacilitiesContants.FAC_MANAGESTATE))) {
				params.put(FacilitiesContants.FAC_MANAGESTATE, pageObj.getString(FacilitiesContants.FAC_MANAGESTATE));
			}
			if (!StringUtils.isBlank(pageObj.getString("startTime"))) {
				params.put("startTime", pageObj.getString("startTime"));
			}
			if (!StringUtils.isBlank(pageObj.getString("endTime"))) {
				params.put("endTime", pageObj.getString("endTime"));
			}
			if (pageObj.containsKey(FacilitiesContants.SEARCH_KEY)) {
				params.put(FacilitiesContants.SEARCH_KEY, pageObj.getString(FacilitiesContants.SEARCH_KEY));
			}
			String ip = pageObj.getString(InterGatewayConstants.IP);
			String token = pageObj.getString(InterGatewayConstants.TOKEN);
			// 按组织搜索时
			// 存储组织id
			List<String> organizationIdList = Lists.newArrayList();
			if (!StringUtils.isBlank(pageObj.getString(FacilitiesContants.ORGANIZATION_ID))) {
				queryOrganzationInfoByOrgId(pageObj, ip, token, organizationIdList);
			}
			// 有组织id,封装搜索参数
			if (organizationIdList.size() != 0) {
				params.put(AlarmInfoConstants.ORG_ID, organizationIdList);
			}
			List<Map<String, Object>> page = facilitesRepository.findFacilityRenovatedByPage(params);
			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			// 封装组织信息
			pageOrg(page, ip, accessSecret, token);

			logger.info(String.format("[findFacilityRenovatedByPage()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("findFacilityRenovatedByPage()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
	

}