/*
 * File name: AlarmInfoQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2018年1月18日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.common.util.StringUtil;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.AlarmInfo;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.Pages;
import com.run.locman.api.query.repository.AlarmInfoQueryRepository;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.DeviceInfoConvertQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.query.service.PropertiesQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.DeviceInfoConvertConstans;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.MongodbConstans;
import com.run.locman.constants.PublicConstants;

/**
 * @Description:告警信息查询
 * @author: lkc
 * @version: 1.0, 2017年11月2日
 */
@SuppressWarnings("rawtypes")
public class AlarmInfoQueryServiceImpl implements AlarmInfoQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private AlarmInfoQueryRepository		alarmInfoQueryRepository;

	@Autowired
	DeviceQueryService						deviceQueryService;

	@Autowired
	FacilityDeviceQueryService				facilityDeviceQueryService;

	@Value("${api.host}")
	private String							ip;

	@Autowired
	private MongoTemplate					mongoTemplate;

	@Autowired
	private PropertiesQueryService			propertiesQueryService;

	@Autowired
	private DeviceInfoConvertQueryService	deviceInfoConvertQueryService;



	@Override
	public RpcResponse<Pages<Map<String, Object>>> getAlarmInfoBypage(JSONObject alarmInfo) {

		logger.info(String.format("[getAlarmInfoBypage()方法执行开始...,参数：【%s】]", alarmInfo));
		try {
			if (alarmInfo == null) {
				logger.error("[getAlarmInfoBypage()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getAlarmInfoBypage()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(alarmInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmInfoBypage()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getAlarmInfoBypage()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(alarmInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmInfoBypage()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.USC_ACCESS_SECRET)
					|| StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getAlarmInfoBypage()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}

			// 存储组织id和组织名的映射
			Map<String, String> organizationInfo = Maps.newHashMap();
			// 存储组织id
			List<String> organizationIdList = Lists.newArrayList();
			// 如果有组织id且不为空,查询其组织信息及子组织信息,获取到id和name,存储起来.等待查询结果然后匹配

			String token = alarmInfo.getString(InterGatewayConstants.TOKEN);

			if (alarmInfo.containsKey(AlarmInfoConstants.ORG_ID)
					&& !StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.ORG_ID))) {
				queryOrganizationInfo(alarmInfo, organizationInfo, organizationIdList, token);
			}

			Map<String, Object> map = getSearchMap(alarmInfo);
			// 有组织id,添加到搜索添加
			if (organizationIdList.size() != 0) {
				map.put(AlarmInfoConstants.ORG_ID, organizationIdList);
			}

			int size = alarmInfo.getIntValue(AlarmInfoConstants.PAGE_SIZE);
			Pages<Map<String, Object>> pages = new Pages<Map<String, Object>>();
			int start = pages.getStart(alarmInfo.getIntValue(AlarmInfoConstants.PAGE_NO), size);

			int total = alarmInfoQueryRepository.countTotaleOfStateList(map);
			map.put("start", start);
			map.put("size", size);
			pages.setTotal(total);

			// 存储组织id,用,隔开方便查询
			StringBuffer resultOrganizationIdsBuffer = new StringBuffer();
			List<Map<String, Object>> resPage = alarmInfoQueryRepository.getAlarmInfoByAccessSercret(map);

			pages.setList(resPage);

			addInfoToQueryResult(organizationInfo, organizationIdList, resultOrganizationIdsBuffer, resPage);

			// 按组织查询时
			if (organizationIdList.size() != 0) {
				logger.info(MessageConstant.INFO_SERCH_SUCCESS);
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
			} else {
				// 查询列表时
				// 获取IP成功,查询组织名
				if (!StringUtils.isBlank(ip)) {
					JSONObject json = new JSONObject();
					if (resultOrganizationIdsBuffer.length() > 0) {
						StringBuffer resultOrganizationIds = resultOrganizationIdsBuffer
								.deleteCharAt(resultOrganizationIdsBuffer.lastIndexOf(","));
						json.put("organizationIds", resultOrganizationIds);
					} else {
						json.put("organizationIds", resultOrganizationIdsBuffer);
					}

					String httpValueByPost = InterGatewayUtil
							.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, json, ip, token);
					if (null == httpValueByPost) {
						// 没查到直接返回
						logger.error("[getAlarmInfoBypage()->invalid：组织查询失败!]");
						logger.info(MessageConstant.INFO_SERCH_SUCCESS);
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
					} else {
						JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
						for (Map<String, Object> mapInfo : resPage) {
							if (null != mapInfo) {
								if (null != mapInfo && null != httpValueJson
										.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID))) {
									mapInfo.put(AlarmInfoConstants.ORG_NAME,
											"" + httpValueJson.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID)));
								} else {
									mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
								}
							}
						}
						logger.info(MessageConstant.INFO_SERCH_SUCCESS);
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
					}
				} else { // 获取IP失败,直接返回
					logger.error("[getAlarmInfoBypage()->invalid：IP获取失败,IP为空!]");
					logger.info(MessageConstant.INFO_SERCH_SUCCESS);
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
				}

			}

		} catch (Exception e) {
			logger.error("getAlarmInfoBypage()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}

	}



	/**
	 * @Description:
	 * @param organizationInfo
	 * @param organizationIdList
	 * @param resultOrganizationIdsBuffer
	 * @param resPage
	 */

	private void addInfoToQueryResult(Map<String, String> organizationInfo, List<String> organizationIdList,
			StringBuffer resultOrganizationIdsBuffer, List<Map<String, Object>> resPage) {
		for (Map<String, Object> mapInfo : resPage) {
			if (null != mapInfo) {
				// 按组织查询时,匹配已经获取的组织名
				if (organizationIdList.size() != 0) {
					String organizationName = organizationInfo.get("" + mapInfo.get(AlarmInfoConstants.ORG_ID));
					if (!StringUtils.isBlank(organizationName)) {
						mapInfo.put(AlarmInfoConstants.ORG_NAME, organizationName);
					} else {
						mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
					}
				} else {
					// 暂存organizationId
					resultOrganizationIdsBuffer.append(mapInfo.get(AlarmInfoConstants.ORG_ID) + ",");
				}
				RpcResponse<List<String>> queryFacilityBindingState = facilityDeviceQueryService
						.queryFacilityBindingState(mapInfo.get("id").toString());

				if (!queryFacilityBindingState.isSuccess()) {
					logger.error("[getAlarmInfoBypage()->invalid：查询该设施下绑定的设备集合出错！]");
					continue;
				}

				// 查询该设施下设备中最近的上报时间(注释:改为从mysql的Device_RealRported表查询)
				/*List<String> deviceIdList = queryFacilityBindingState.getSuccessValue();
				if (null != deviceIdList && deviceIdList.size() > 0) {
					for (String deviceId : deviceIdList) {
						RpcResponse<JSONObject> deviceLastState = deviceQueryService.queryDeviceLastState(deviceId);
						if (null != deviceLastState && null != deviceLastState.getSuccessValue()) {
							JSONObject json = deviceLastState.getSuccessValue();
							// 获取上报时间
							String timestamp = json.getString(AlarmInfoConstants.TIMESTAMP);
							if (StringUtils.isBlank(timestamp)) {
								mapInfo.put(AlarmInfoConstants.REPORT_TIME, "");
							} else {
								mapInfo.put(AlarmInfoConstants.REPORT_TIME, timestamp);
							}
						} else {
							logger.error("[getAlarmInfoBypage()->invalid：查询该设施下绑定的设备状态更新时间出错！]");
							continue;
						}
					}
				}*/

				if (StringUtils.isBlank((String) mapInfo.get(AlarmInfoConstants.ALARM_TIME))) {
					mapInfo.put(AlarmInfoConstants.ALARM_TIME, "暂无告警");
				}
			}
		}
	}



	/**
	 * @Description:如果有组织id,则根据组织id查询组织信息
	 * @param alarmInfo
	 * @param organizationInfo
	 * @param organizationIdList
	 * @param token
	 */

	private void queryOrganizationInfo(JSONObject alarmInfo, Map<String, String> organizationInfo,
			List<String> organizationIdList, String token) {
		
			String organizationId = alarmInfo.getString(AlarmInfoConstants.ORG_ID);
			// 成功获取IP
			if (!StringUtils.isBlank(ip)) {
				String httpValueByGet = InterGatewayUtil
						.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip, token);
				if (null == httpValueByGet) {
					logger.error("[queryOrganizationInfo()->invalid：组织查询失败!]");
					organizationIdList.add(organizationId);
					organizationInfo.put(organizationId, "");
				} else {
					JSONArray jsonArray = JSON.parseArray(httpValueByGet);
					List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
					for (Map map : organizationInfoList) {
						organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
						organizationInfo.put("" + map.get(InterGatewayConstants.ORGANIZATION_ID),
								"" + map.get(InterGatewayConstants.ORGANIZATION_NAME));
					}
				}
			} else {
				// 获取IP失败或者为null则不访问
				logger.error("[queryOrganizationInfo()->invalid：IP获取失败,IP为空!]");
				organizationIdList.add(organizationId);
				organizationInfo.put(organizationId, "");
			}

		}
	



	/**
	 * @Description:查询参数封装
	 * @param alarmInfo
	 * @return
	 */

	private Map<String, Object> getSearchMap(JSONObject alarmInfo) {
		logger.info(String.format("[getSearchMap()方法执行开始...,参数：【%s】]", alarmInfo));
		Map<String, Object> map = new HashMap<>(16);
		map.put(AlarmInfoConstants.USC_ACCESS_SECRET, alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET));
		if (alarmInfo.containsKey(AlarmInfoConstants.FACILITIES_TYPE_ID)
				&& !StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.FACILITIES_TYPE_ID))) {
			map.put(AlarmInfoConstants.FACILITIES_TYPE_ID, alarmInfo.getString(AlarmInfoConstants.FACILITIES_TYPE_ID));
		}
		if (alarmInfo.containsKey(AlarmInfoConstants.ALARMI_LEVEL)
				&& !StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.ALARMI_LEVEL))) {
			map.put(AlarmInfoConstants.ALARMI_LEVEL, alarmInfo.getString(AlarmInfoConstants.ALARMI_LEVEL));
		}
		if (alarmInfo.containsKey(AlarmInfoConstants.FACILITES_CODE)
				&& !StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.FACILITES_CODE))) {
			map.put(AlarmInfoConstants.FACILITES_CODE, alarmInfo.getString(AlarmInfoConstants.FACILITES_CODE));
		}
		if (alarmInfo.containsKey(AlarmInfoConstants.ADDRESS_KEY)
				&& !StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.ADDRESS_KEY))) {
			map.put(AlarmInfoConstants.ADDRESS_KEY, alarmInfo.getString(AlarmInfoConstants.ADDRESS_KEY));
		}
		logger.info(String.format("[getSearchMap()方法执行结束!返回信息：【%s】]", map));
		return map;
	}



	/**
	 * @Description:查询参数封装
	 * @param alarmInfo
	 * @return
	 */

	private Map<String, String> getSearchMapForByFacId(JSONObject alarmInfo) {
		logger.info(String.format("[getSearchMapForByFacId()方法执行开始...,参数：【%s】]", alarmInfo));
		Map<String, String> map = new HashMap<>(16);
		if (alarmInfo.containsKey(AlarmInfoConstants.FACILITES_ID)
				&& !StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.FACILITES_ID))) {
			map.put(AlarmInfoConstants.FACILITES_ID, alarmInfo.getString(AlarmInfoConstants.FACILITES_ID));
		}
		if (alarmInfo.containsKey(AlarmInfoConstants.USC_ACCESS_SECRET)
				&& !StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
			map.put(AlarmInfoConstants.USC_ACCESS_SECRET, alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET));
		}
		logger.info(String.format("[getSearchMapForByFacId()方法执行结束!返回信息：【%s】]", map));
		return map;
	}



	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getAlarmInfoByFacilitesId(JSONObject alarmInfo) {
		logger.info(String.format("[getAlarmInfoByFacilitesId()方法执行开始...,参数：【%s】]", alarmInfo));
		try {
			if (alarmInfo == null) {
				logger.error("[getAlarmInfoBypage()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.FACILITES_ID)
					|| StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.FACILITES_ID))) {
				logger.error("[getAlarmInfoBypage()->invalid：设施id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施id不能为空！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.USC_ACCESS_SECRET)
					|| StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getAlarmInfoBypage()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getAlarmInfoBypage()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(alarmInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmInfoBypage()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getAlarmInfoBypage()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(alarmInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmInfoBypage()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}

			PageHelper.startPage(alarmInfo.getIntValue(AlarmInfoConstants.PAGE_NO),
					alarmInfo.getIntValue(AlarmInfoConstants.PAGE_SIZE));
			Map<String, String> map = getSearchMapForByFacId(alarmInfo);
			List<Map<String, Object>> resPage = alarmInfoQueryRepository.getAlarmInfoByFacId(map);
			for (Map<String, Object> res : resPage) {
				if (res.containsKey(AlarmInfoConstants.DEVICE_ID)
						&& !StringUtils.isBlank((String) res.get(AlarmInfoConstants.DEVICE_ID))) {
					String deviceId = (String) res.get(AlarmInfoConstants.DEVICE_ID);
					List<String> list = new ArrayList<>();
					list.add(deviceId);
					RpcResponse<List<Map<String, Object>>> deviceInfo = deviceQueryService
							.queryBatchDeviceInfoForDeviceIds(list);
					if (deviceInfo.isSuccess() && null != deviceInfo.getSuccessValue()
							&& deviceInfo.getSuccessValue().size() > 0) {
						res.put("deviceInfo", deviceInfo.getSuccessValue().get(0));
					}
				}
			}
			PageInfo<Map<String, Object>> info = new PageInfo<>(resPage);
			logger.info(MessageConstant.INFO_SERCH_SUCCESS);
			logger.info(String.format("[getAlarmInfoByFacilitesId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getAlarmInfoByFacilitesId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<Map<String, Object>>> getNearlyAlarmInfo(String organizationId, String accessSecret) {
		logger.info(String.format("[getNearlyAlarmInfo()方法执行开始...,参数：【%s】【%s】]", organizationId, accessSecret));
		try {
			if (StringUtils.isEmpty(accessSecret)) {
				logger.error("接入方秘钥不能为空");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空");
			}
			List<Map<String, Object>> nearlyAlarmInfo = alarmInfoQueryRepository.getNearlyAlarmInfo(organizationId,
					accessSecret);
			// 若最近三天告警数据为空，则取前十条数据
			if (nearlyAlarmInfo == null || nearlyAlarmInfo.size() == 0) {
				nearlyAlarmInfo = alarmInfoQueryRepository.getNearlyTenAlarmInfo(organizationId, accessSecret);
			}
			logger.info(MessageConstant.INFO_SERCH_SUCCESS);
			logger.info(String.format("[getNearlyAlarmInfo()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", nearlyAlarmInfo);
		} catch (Exception e) {
			logger.error("getNearlyAlarmInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> getAlarmInfoBydeviceId(String deviceId) {
		logger.info(String.format("[getAlarmInfoBydeviceId()方法执行开始...,参数：【%s】]", deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[getAlarmInfoBydeviceId()->error:设备id不能为空！");
				return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空！");
			}
			Map<String, Object> alarmInfoByDeviceId = alarmInfoQueryRepository.getAlarmInfoByDeviceId(deviceId);
			logger.info(MessageConstant.INFO_SERCH_SUCCESS);
			logger.info(String.format("[getAlarmInfoBydeviceId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, alarmInfoByDeviceId);
		} catch (Exception e) {
			logger.error("getAlarmInfoBydeviceId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<Pages<Map<String, Object>>> getAlarmInfoList(JSONObject alarmInfo) {
		logger.info(String.format("[getAlarmInfoList()方法执行开始...,参数：【%s】]", alarmInfo));
		try {
			if (alarmInfo == null) {
				logger.error("[getAlarmInfoList()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getAlarmInfoList()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(alarmInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmInfoList()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getAlarmInfoList()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(alarmInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmInfoList()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getAlarmInfoList()->invalid：接入方秘钥不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
			}
			// 获取Token
			String token = alarmInfo.getString(InterGatewayConstants.TOKEN);
			// 存储组织id,name
			Map<String, String> organizationInfo = Maps.newHashMap();
			// 存储组织id
			List<String> organizationIdList = Lists.newArrayList();
			// 如果有组织id且不为空,查询其组织信息及子组织信息,获取到id和name,存储起来.等待查询结果然后匹配

			if (alarmInfo.containsKey(AlarmInfoConstants.ORG_ID)
					&& !StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.ORG_ID))) {
				queryOrganizationInfo(alarmInfo, organizationInfo, organizationIdList, token);
			}

			Map<String, Object> map = Maps.newHashMap();
			map.put(AlarmInfoConstants.FACILITY_CODE_OR_ID,
					alarmInfo.getString(AlarmInfoConstants.FACILITY_CODE_OR_ID));
			map.put(AlarmInfoConstants.ADDRESS, alarmInfo.getString("address"));
			map.put("alarmTimeStart", alarmInfo.getString("alarmTimeStart"));
			map.put("alarmTimeEnd", alarmInfo.getString("alarmTimeEnd"));
			map.put(AlarmInfoConstants.FACILITIES_TYPE_ID, alarmInfo.getString(AlarmInfoConstants.FACILITIES_TYPE_ID));
			map.put(AlarmInfoConstants.ALARMI_LEVEL, alarmInfo.getString(AlarmInfoConstants.ALARMI_LEVEL));
			map.put("accessSecret", alarmInfo.getString("accessSecret"));

			// 有组织id,添加到搜索添加
			if (organizationIdList.size() != 0) {
				map.put(AlarmInfoConstants.ORG_ID, organizationIdList);
			}

			int size = alarmInfo.getIntValue(AlarmInfoConstants.PAGE_SIZE);
			Pages<Map<String, Object>> pages = new Pages<Map<String, Object>>();
			int start = pages.getStart(alarmInfo.getIntValue(AlarmInfoConstants.PAGE_NO), size);
			int total = alarmInfoQueryRepository.getAlarmInfoListNum(map);
			map.put("start", start);
			map.put("size", size);
			pages.setTotal(total);

			List<Map<String, Object>> resPage = alarmInfoQueryRepository.getAlarmInfoList(map);
			pages.setList(resPage);
			if (resPage.size() == 0) {
				logger.info(MessageConstant.INFO_SERCH_SUCCESS);
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
			}
			StringBuffer resultOrganizationIdsBuffer = new StringBuffer();
			for (Map<String, Object> mapInfo : resPage) {
				if (null != mapInfo) {
					// 按组织查询时,匹配已经获取的组织名
					
					// 因为前端js前端获取long型数值精度丢失，preview与response显示不一致，导致数据出错，所以讲 serialNum从long 转换为string
					String serialNum = mapInfo.get("serialNum").toString();
					mapInfo.put("serialNum", serialNum);
					
					if (organizationIdList.size() != 0) {
						String organizationName = organizationInfo.get("" + mapInfo.get(AlarmInfoConstants.ORG_ID));
						if (!StringUtils.isBlank(organizationName)) {
							mapInfo.put(AlarmInfoConstants.ORG_NAME, organizationName);
						} else {
							mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
						}
					} else {
						// 暂存organizationId
						resultOrganizationIdsBuffer.append(mapInfo.get(AlarmInfoConstants.ORG_ID) + ",");
					}
				}
			}
			// 按组织查询时
			if (organizationIdList.size() != 0) {
				logger.info(MessageConstant.INFO_SERCH_SUCCESS);
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
			} else {
				// 查询列表
				// 获取IP成功,查询组织名
				if (!StringUtils.isBlank(ip)) {
					JSONObject json = new JSONObject();
					if (resultOrganizationIdsBuffer.length() > 0) {
						StringBuffer resultOrganizationIds = resultOrganizationIdsBuffer
								.deleteCharAt(resultOrganizationIdsBuffer.lastIndexOf(","));
						json.put("organizationIds", resultOrganizationIds);
					} else {
						json.put("organizationIds", resultOrganizationIdsBuffer);
					}

					String httpValueByPost = InterGatewayUtil
							.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, json, ip, token);
					if (null == httpValueByPost) {
						// 没查到直接返回
						logger.error("[getAlarmInfoList()->invalid：组织查询失败!]");
						logger.info(MessageConstant.INFO_SERCH_SUCCESS);
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
					} else {
						JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
						for (Map<String, Object> mapInfo : resPage) {
							if (null != mapInfo) {
								if (null != mapInfo && null != httpValueJson
										.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID))) {
									mapInfo.put(AlarmInfoConstants.ORG_NAME,
											"" + httpValueJson.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID)));
								} else {
									mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
								}
							}
						}
						logger.info(MessageConstant.INFO_SERCH_SUCCESS);
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
					}
				} else { // 获取IP失败,直接返回
					logger.info(MessageConstant.INFO_SERCH_SUCCESS);
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
				}

			}

		} catch (Exception e) {
			logger.error("getAlarmInfoList()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<AlarmInfo> findById(String id) {
		logger.info(String.format("[findById()方法执行开始...,参数：【%s】]", id));
		if (StringUtil.isEmpty(id)) {
			logger.error("[AlarmInfoQueryServiceImpl -> findById()-> error: id不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("id不能为空");
		}
		try {
			AlarmInfo alarmInfo = alarmInfoQueryRepository.findById(id);
			logger.info("查询成功");
			logger.info(String.format("[findById()方法执行结束!返回信息：【%s】]", alarmInfo));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", alarmInfo);
		} catch (Exception e) {
			logger.error("findById()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> statisticsAlarmInfo(JSONObject alarmInfo, String pageNo) {
		logger.info(String.format("[rpc-statisticsAlarmInfo->request params:%s,%s]", alarmInfo, pageNo));
		try {
			if (!alarmInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[statisticsAlarmInfo()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(alarmInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[statisticsAlarmInfo()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!alarmInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[statisticsAlarmInfo()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(alarmInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[statisticsAlarmInfo()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[statisticsAlarmInfo()->invalid：接入方秘钥不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
			}
			if (StringUtils.isNumeric(pageNo)) {
				alarmInfo.put(AlarmInfoConstants.PAGE_NO, pageNo);
			}
			// 获取IP和token
			String token = alarmInfo.getString(InterGatewayConstants.TOKEN);
			Map<String, String> organizationInfo = Maps.newHashMap();
			List<String> organizationIdList = new ArrayList<>();
			// 如果组织id不为空,查询其组织信息及子组织信息,获取到id和name,存储起来.等待查询结果然后匹配
			if (!StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.ORG_ID))) {
                  queryOrganizationInfo(alarmInfo, organizationInfo, organizationIdList, token);
			}
			PageHelper.startPage(alarmInfo.getIntValue(AlarmInfoConstants.PAGE_NO),
					alarmInfo.getIntValue(AlarmInfoConstants.PAGE_SIZE));

			// 封装查询条件
			Map<String, Object> searchMap = new HashMap<>(16);
			searchMap.put(AlarmInfoConstants.USC_ACCESS_SECRET,
					alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET));
			searchMap.put(AlarmInfoConstants.SEARCH_KEY, alarmInfo.getString(AlarmInfoConstants.SEARCH_KEY));
			// 有组织id,添加到搜索添加
			if (organizationIdList.size() != 0) {
				searchMap.put(AlarmInfoConstants.ORG_ID, organizationIdList);
			}
			
			//增加时间段搜索条件
			searchMap.put("alarmTimeStart", alarmInfo.getString("alarmTimeStart"));
			searchMap.put("alarmTimeEnd", alarmInfo.getString("alarmTimeEnd"));
			
			

			List<Map<String, Object>> res = alarmInfoQueryRepository.statisticsAlarmInfo(searchMap);
			if (res == null) {
				logger.error("statisticsAlarmInfo()->fail:查询数据库失败");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
			}

			// 查询条件中有组织id时直接查询组织名字和id的匹配
			if (organizationIdList.size() != 0) {
				
				//强转空指针异常，判断map.get()内容是否为空
				for (Map map : res) {
					map.put(AlarmInfoConstants.ORG_NAME, organizationInfo.get(map.get(AlarmInfoConstants.ORG_ID)));
					if(map.get(AlarmInfoConstants.ALARMI_LEVEL) == null) {
						 continue;
					}
					else if ((int) map.get(AlarmInfoConstants.ALARMI_LEVEL) == 1) {
						map.put(AlarmInfoConstants.ALARMI_LEVEL, "紧急告警");
					} else if ((int) map.get(AlarmInfoConstants.ALARMI_LEVEL) == 2) {
						map.put(AlarmInfoConstants.ALARMI_LEVEL, "一般告警");
					}
				}
				PageInfo<Map<String, Object>> result = new PageInfo<>(res);
				logger.info(String.format("[######statisticsAlarmInfo->result:%s]", result.getList()));
				logger.info("[statisticsAlarmInfo()->success:统计告警信息成功]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, result);
			}
			// 查询条件中无组织id时，则需根据结果中的组织id查询组织及子组织信息，添加组织名字
			else {
				// 获取组织id
				if (!StringUtils.isBlank(ip)) {
					List<String> resultIdList = new ArrayList<>();
					for (Map<String, Object> map : res) {
						String resultId = String.valueOf(map.get(AlarmInfoConstants.ORG_ID));
						if (!resultIdList.contains(resultId)) {
							resultIdList.add(resultId);
						}
					}

					// 将list转成String，方便传入json
					JSONObject json = new JSONObject();
					StringBuilder resultIds = new StringBuilder();
					boolean first = true;
					// 第一个前面不拼接","
					for (String string : resultIdList) {
						if (first) {
							first = false;
						} else {
							resultIds.append(",");
						}
						resultIds.append(string);
					}
					json.put("organizationIds", resultIds.toString());

					// 通过id查询组织名
					String httpValueByPost = InterGatewayUtil
							.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, json, ip, token);
					if (null == httpValueByPost) {
						// 没查到直接返回
						for (Map map : res) {
							
							if(map.get(AlarmInfoConstants.ALARMI_LEVEL) == null) {
								 continue;
							}
							else if ((int) map.get(AlarmInfoConstants.ALARMI_LEVEL) == 1) {
								map.put(AlarmInfoConstants.ALARMI_LEVEL, "紧急告警");
							} else if ((int) map.get(AlarmInfoConstants.ALARMI_LEVEL) == 2) {
								map.put(AlarmInfoConstants.ALARMI_LEVEL, "一般告警");
							}
						}
						PageInfo<Map<String, Object>> result = new PageInfo<>(res);
						logger.info("[statisticsAlarmInfo()->success:统计告警信息成功]");
						logger.error("[statisticsAlarmInfo()->invalid：组织名查询失败]");
						return RpcResponseBuilder.buildSuccessRpcResp("信息查询成功！组织名获取失败！", result);
					} else {
						JSONObject httpValueJson = JSON.parseObject(httpValueByPost);

						for (Map<String, Object> mapInfo : res) {
							if (null != mapInfo) {
								if (null != mapInfo && null != httpValueJson
										.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID))) {
									mapInfo.put(AlarmInfoConstants.ORG_NAME,
											httpValueJson.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID)));
									if(mapInfo.get(AlarmInfoConstants.ALARMI_LEVEL) == null) {
										 continue;
									}
									else if ((int) mapInfo.get(AlarmInfoConstants.ALARMI_LEVEL) == 1) {
										mapInfo.put(AlarmInfoConstants.ALARMI_LEVEL, "紧急告警");
									} else if ((int) mapInfo.get(AlarmInfoConstants.ALARMI_LEVEL) == 2) {
										mapInfo.put(AlarmInfoConstants.ALARMI_LEVEL, "一般告警");

									}
								} else {
									if(mapInfo.get(AlarmInfoConstants.ALARMI_LEVEL) == null) {
										 continue;
									}
									else if ((int) mapInfo.get(AlarmInfoConstants.ALARMI_LEVEL) == 1) {
										mapInfo.put(AlarmInfoConstants.ALARMI_LEVEL, "紧急告警");
									} else if ((int) mapInfo.get(AlarmInfoConstants.ALARMI_LEVEL) == 2) {
										mapInfo.put(AlarmInfoConstants.ALARMI_LEVEL, "一般告警");
									}
									mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
								}
							}
						}
						PageInfo<Map<String, Object>> result = new PageInfo<>(res);
						logger.info("[statisticsAlarmInfo()->success:统计告警信息成功");
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, result);
					}
				}

				// 获取ip失败或者为空，直接返回
				PageInfo<Map<String, Object>> result = new PageInfo<>(res);
				logger.info("[statisticsAlarmInfo()->success:统计告警信息成功");
				logger.error("[statisticsAlarmInfo()->invalid：获取ip失败，无法查询组织名]");
				return RpcResponseBuilder.buildSuccessRpcResp("信息查询成功！组织名获取失败！", result);
			}

		} catch (Exception e) {
			logger.error("statisticsAlarmInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmInfoQueryService#alarmInfoRoll(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> alarmInfoRoll(JSONObject alarmInfo) {
		logger.info(String.format("[rpc-alarmInfoRoll->request params:%s]", alarmInfo));
		if (StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
			logger.error("[alarmInfoRoll()->invalid：接入方秘钥不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥不能为空!");
		}
		if (!alarmInfo.containsKey(AlarmInfoConstants.ORG_ID)) {
			logger.error("[alarmInfoRoll()->invalid：组织id参数名不存在!]");
			return RpcResponseBuilder.buildErrorRpcResp("组织id参数名不存在!");
		}

		try {
			// 获取IP
			String token = alarmInfo.getString(InterGatewayConstants.TOKEN);
			// 存储组织id
			List<String> organizationIdList = Lists.newArrayList();
			// 如果有组织id且不为空,查询其组织信息及子组织信息,获取到id和name,存储起来.等待查询结果然后匹配
			if (alarmInfo.containsKey(AlarmInfoConstants.ORG_ID)
					&& !StringUtils.isBlank(alarmInfo.getString(AlarmInfoConstants.ORG_ID))) {
				String organizationId = alarmInfo.getString(AlarmInfoConstants.ORG_ID);
				// 成功获取IP
				if (!StringUtils.isBlank(ip)) {
					String httpValueByGet = InterGatewayUtil
							.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip, token);
					if (null == httpValueByGet) {
						logger.error("[alarmInfoRoll()->invalid：组织查询失败!]");
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
					logger.error("[alarmInfoRoll()->invalid：IP获取失败,无法进行组织查询!]");
					organizationIdList.add(organizationId);
				}

			}
			Map<String, Object> map = Maps.newHashMap();
			map.put(AlarmInfoConstants.ORG_ID, alarmInfo.getString(AlarmInfoConstants.FACILITIES_TYPE_ID));
			map.put(AlarmInfoConstants.USC_ACCESS_SECRET, alarmInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET));
			// 有组织id,添加到搜索添加
			if (organizationIdList.size() != 0) {
				map.put(AlarmInfoConstants.ORG_ID, organizationIdList);
			}
			List<Map<String, Object>> alarmInfoRoll = alarmInfoQueryRepository.alarmInfoRoll(map);
			if (null == alarmInfoRoll) {
				logger.error("查询最新的10条告警信息失败!!!");
				return RpcResponseBuilder.buildErrorRpcResp("查询最新的10条告警信息失败!!!");
			} else if (alarmInfoRoll.size() == 0) {
				/*
				 * Map<String, Object> newMap = Maps.newHashMap();
				 * newMap.put("alarmTime", ""); newMap.put("serialNum", "");
				 * newMap.put("alarmInfo", ""); alarmInfoRoll.add(newMap);
				 */
				logger.info("查询最新的10条告警信息成功!!!");
				return RpcResponseBuilder.buildSuccessRpcResp("查询最新的10条告警信息成功!!!", alarmInfoRoll);
			} else {
				// 组装需要滚动显示的告警信息
				for (Map<String, Object> alarmInfoMap : alarmInfoRoll) {
					Object facilitiesCode = alarmInfoMap.remove("facilitiesCode");
					Object facilityTypeAlias = alarmInfoMap.remove("facilityTypeAlias");
					Object alarmDesc = alarmInfoMap.remove("alarmDesc");
					Object address = alarmInfoMap.remove("address");
					Object alarmTime = alarmInfoMap.remove("alarmTime");
					StringBuffer append = new StringBuffer().append(alarmTime).append(" 设施序列号:").append(facilitiesCode).append("的")
							.append(facilityTypeAlias).append(",发生").append(alarmDesc).append("告警,位置:").append(address);
					alarmInfoMap.put("alarmInfo", append);
				}
				logger.info("查询最新的10条告警信息成功!!!");
				return RpcResponseBuilder.buildSuccessRpcResp("查询最新的10条告警信息成功!!!", alarmInfoRoll);
			}

		} catch (Exception e) {
			logger.error("alarmInfoRoll()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.AlarmInfoQueryService#queryAlarmDeviceData(java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> queryAlarmDeviceData(String alarmInfoId) {
		logger.info(String.format("[rpc-queryAlarmDeviceData->request params:%s]", alarmInfoId));
		try {
			if (StringUtils.isBlank(alarmInfoId)) {
				logger.error("[queryAlarmDeviceData()->invalid：告警信息ID不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("告警信息ID不能为空!");
			}

			// 查询告警信息time时间字段
			Map<String, Object> infoMap = alarmInfoQueryRepository.getTimeStampByAlarmId(alarmInfoId);

			// 基础判断业务参数
			if (infoMap == null || StringUtils.isBlank(String.valueOf((Long) infoMap.get(AlarmInfoConstants.TIMESTAMP)))
					|| StringUtils.isBlank((String) infoMap.get(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error(String.format("[queryAlarmDeviceData()->error:timeStamp或者接入方密钥不存在！%s]", infoMap));
				return RpcResponseBuilder.buildErrorRpcResp(
						String.format("[queryAlarmDeviceData()->error:timeStamp或者接入方密钥不存在！%s]", infoMap));
			}

			String timeStamp = String.valueOf((Long) infoMap.get(AlarmInfoConstants.TIMESTAMP));
			String accessSecret = (String) infoMap.get(AlarmInfoConstants.USC_ACCESS_SECRET);

			// 查询mongodb状态数据
			JSONObject deviceJson = findDeviceData(timeStamp);
			if (deviceJson == null) {
				logger.error("[queryAlarmDeviceData()->error:当前告警不存在设备上报状态！]");
				return RpcResponseBuilder.buildErrorRpcResp("当前告警不存在设备上报状态！");
			}

			// 通过设备ID获取设备类型ID
			String deviceTypeId = alarmInfoQueryRepository
					.getDeviceTypeByDeviceId(deviceJson.getString(DeviceContants.DEVICEID));

			// 通过设备类型ID获取模板 进行转换
			RpcResponse<List<DeviceProperties>> deviceTemplate = propertiesQueryService.findByDeviceTypeId(accessSecret,
					deviceTypeId);
			if (!deviceTemplate.isSuccess()) {
				logger.error("[queryAlarmDeviceData()->error:数据点查询失败]");
				return RpcResponseBuilder.buildErrorRpcResp(deviceTemplate.getMessage());
			}
			List<DeviceProperties> list = deviceTemplate.getSuccessValue();
			if (list == null || list.size() == 0) {
				logger.error("[queryAlarmDeviceData()->error:数据点结果集为空]");
				return RpcResponseBuilder.buildErrorRpcResp("数据点结果集为空");
			}

			// 解析封装设备上报数据
			JSONObject reported = UtilTool.getReported(deviceJson);
			JSONObject dataJson = potDeviceData(reported, accessSecret, list);

			logger.info(String.format("[queryAlarmDeviceData()->suc:封装后数据%s]", dataJson));
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, dataJson);

		} catch (Exception e) {
			logger.error("queryAlarmDeviceData()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:查询mongodb数据库设备上报数据
	 * @param timeStamp
	 * @return
	 * @throws Exception
	 */
	private JSONObject findDeviceData(String timeStamp) throws Exception {
		Query query = new Query();
		Criteria cr = new Criteria();
		query.addCriteria(cr.orOperator(Criteria.where(AlarmInfoConstants.TIMESTAMP).is(Integer.valueOf(timeStamp)),
				Criteria.where(AlarmInfoConstants.TIMESTAMP).is(timeStamp), Criteria.where(AlarmInfoConstants.TIMESTAMP)
						.regex(Pattern.compile(MongodbConstans.REGX_LEFT + timeStamp + MongodbConstans.REGX_RIGHT))));
		return mongoTemplate.findOne(query, JSONObject.class, MongodbConstans.MONGODB_DEVICESTATE_HISTORY);
	}



	/**
	 * 
	 * @Description:将数据进行封装返回
	 * @param reported
	 * @param accessSecret
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private JSONObject potDeviceData(JSONObject reported, String accessSecret, List<DeviceProperties> list)
			throws Exception {
		JSONObject dataJson = new JSONObject();
		// 解析实时报数据
		if (reported != null) {
			Set<String> keySet = reported.keySet();
			for (DeviceProperties deviceProperties : list) {
				for (String propName : keySet) {
					if (deviceProperties.getDevicePropertiesSign().equals(propName)) {
						// 如果值是英文,转中文
						RpcResponse<Map> dataConvertInfo = deviceInfoConvertQueryService
								.dataConvert(String.valueOf(reported.get(propName)), accessSecret);
						String dataConvert = dataConvertInfo.isSuccess()
								? dataConvertInfo.getSuccessValue().get(DeviceInfoConvertConstans.CHINA_KEY) + ""
								: String.valueOf(reported.get(propName));
						// 封装数据
						dataJson.put(deviceProperties.getDevicePropertiesName(), dataConvert);
						break;
					}
				}
			}
		}

		return dataJson;

	}



	/**
	 * @see com.run.locman.api.query.service.AlarmInfoQueryService#queryAlarmItemList(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<List<String>> queryAlarmItemList(String deviceId, String accessSecret) {
		logger.info(String.format("[rpc-queryAlarmItemList->request params:%s--%s]", deviceId, accessSecret));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[queryAlarmItemList()->invalid：告警信息deviceId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("告警信息deviceId不能为空!");
			}
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[queryAlarmItemList()->invalid：告警信息deviceId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("告警信息deviceId不能为空!");
			}
			List<String> queryAlarmItemList = alarmInfoQueryRepository.queryAlarmItemList(deviceId, accessSecret);
			if (null == queryAlarmItemList) {
				logger.error("[queryAlarmItemList()->fail：查询设备告警Key值集合失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询设备告警Key值集合失败!");
			}
			logger.info("[queryAlarmItemList()->fail：查询设备告警Key值集合成功!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", queryAlarmItemList);

		} catch (Exception e) {
			logger.error("queryAlarmItemList()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
