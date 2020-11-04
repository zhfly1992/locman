/*
 * File name: FacilityDeviceQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guolei 2017年9月15日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.FacilityDeviceQueryRepository;
import com.run.locman.api.query.service.DeviceInfoConvertQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceInfoConvertConstans;
import com.run.locman.constants.FacilityDeviceContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.MessageConstant;

/**
 * 
 * @Description: 设施与设备绑定关系query实现类
 * @author: guolei
 * @version: 1.0, 2017年9月15日
 */

public class FacilityDeviceQueryServiceImpl implements FacilityDeviceQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilityDeviceQueryRepository	facilityDeviceQueryRepository;
	@Autowired

	@Value("${api.host}")
	private String							ip;
	@Autowired
	private  MongoTemplate					mongoTemplate;
	@Autowired
	private DeviceInfoConvertQueryService	deviceInfoConvertQueryService;



	@Override
	public RpcResponse<String> queryDeviceBindingState(String deviceId) {
		logger.info(String.format("[queryDeviceBindingState()方法执行开始...,参数：【%s】]", deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[queryDeviceBindingState()->invalid：查询设备的绑定状态,设备id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施与设备的关系信息：查询设备的绑定状态,设备id不能为空！");
			}
			String facilityId = facilityDeviceQueryRepository.queryDeviceBindingState(deviceId);
			logger.info(String.format("[queryDeviceBindingState()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询设备的绑定状态成功", facilityId);
		} catch (Exception e) {
			logger.error("queryDeviceBindingState()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<String>> queryFacilityBindingState(String facilityId) {
		logger.info(String.format("[queryFacilityBindingState()方法执行开始...,参数：【%s】]", facilityId));
		try {
			if (StringUtils.isBlank(facilityId)) {
				logger.error("[queryFacilityBindingState()->invalid：查询设施的绑定状态,设施id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施与设备的关系信息：查询设施的绑定状态,设施id不能为空！");
			}
			List<String> deviceIdList = facilityDeviceQueryRepository.queryFacilityBindingState(facilityId);
			logger.info(String.format("[queryFacilityBindingState()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询设备的绑定状态成功", deviceIdList);
		} catch (Exception e) {
			logger.error("queryFacilityBindingState()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> queryFacilityById(String facilityId) {
		logger.info(String.format("[queryFacilityById()方法执行开始...,参数：【%s】]", facilityId));
		try {
			if (StringUtils.isBlank(facilityId)) {
				logger.error("[queryFacilityById()->invalid：查询设施详情,设施id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施与设备的关系信息：查询设施详情,设施id不能为空！");
			}
			Map<String, Object> facilityInfo = facilityDeviceQueryRepository.queryFacilityById(facilityId);
			logger.info(String.format("[queryFacilityById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询设备的绑定状态成功", facilityInfo);
				
			} catch (Exception e) {
				logger.error("queryFacilityById()->exception", e);
				return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
			}
		}



	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> queryFacilityBindListByPage(String accessSecret, int pageNo,
			int pageSize, Map<String, Object> params) {

		logger.info(String.format("[queryFacilityBindListByPage()方法执行开始...,参数：【%s】【%s】【%s】【%s】]", accessSecret, pageNo,
				pageSize, params));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[queryFacilityBindListByPage()->invalid：查询设施详情,接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施与设备的关系信息：查询设施详情,接入方密钥不能为空！");
			}
			// 获取Token
			String token = null;
			if (null != params) {
				params.put(FacilityDeviceContants.USC_ACCESS_SECRET, accessSecret.trim());
				token = params.get(InterGatewayConstants.TOKEN) + "";
			} else {
				logger.error("参数为null,token获取失败");
				params = Maps.newHashMap();
				params.put(FacilityDeviceContants.USC_ACCESS_SECRET, accessSecret.trim());
			}
			// 获取组织id
			String orgId = params.get(FacilityDeviceContants.ORGANIZATION_ID) + "";

			// 存储组织id
			List<String> organizationIdList = Lists.newArrayList();
			// 存储组织id,name
			Map<String, String> organizationInfo = Maps.newHashMap();
			// 如果组织id不为空,查询其组织信息及子组织信息,获取到id和name,存储起来.等待查询结果然后匹配
			if (!StringUtils.isBlank(orgId)) {
				if (!StringUtils.isBlank(ip)) {
					queryOrganizationInfo(token, orgId, organizationIdList, organizationInfo);
				} else {
					// 获取IP失败或者为null则不访问
					logger.error("[queryFacilityBindListByPage()->invalid：IP获取失败,组织查询失败!]");
					organizationIdList.add(orgId);
					organizationInfo.put(orgId, "");
				}
				params.put(FacilityDeviceContants.ORGANIZATION_ID, organizationIdList);
			}

			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> facilityList = facilityDeviceQueryRepository.queryFacilityBindListByPage(params);

			StringBuffer resultOrganizationIdsBuffer = new StringBuffer();
			for (Map<String, Object> mapInfo : facilityList) {
				if (null != mapInfo) {
					// 有组织时,匹配已经获取的组织名
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
				PageInfo<Map<String, Object>> info = new PageInfo<>(facilityList);
				logger.info(MessageConstant.INFO_SERCH_SUCCESS);
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
			} else {
				// 查询列表
				// 获取IP成功,查询组织名
				if (!StringUtils.isBlank(ip)) {
					JSONObject json = new JSONObject();
					int length = resultOrganizationIdsBuffer.length();
					if (length > 0) {
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
						logger.error("[queryFacilityBindListByPage()->invalid：组织查询失败!]");
						PageInfo<Map<String, Object>> info = new PageInfo<>(facilityList);
						logger.info(MessageConstant.INFO_SERCH_SUCCESS);
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
					} else {
						JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
						for (Map<String, Object> mapInfo : facilityList) {
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
						PageInfo<Map<String, Object>> info = new PageInfo<>(facilityList);
						logger.info(MessageConstant.INFO_SERCH_SUCCESS);
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
					}
				} else { // 获取IP失败,直接返回
					PageInfo<Map<String, Object>> info = new PageInfo<>(facilityList);
					logger.error("获取ip失败,未能查询组织名,直接返回数据");
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
				}

			}

		} catch (Exception e) {
			logger.error("queryFacilityBindListByPage()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @Description:
	 * @param token
	 * @param orgId
	 * @param organizationIdList
	 * @param organizationInfo
	 */

	@SuppressWarnings("rawtypes")
	private void queryOrganizationInfo(String token, String orgId, List<String> organizationIdList,
			Map<String, String> organizationInfo) {
		String httpValueByGet = InterGatewayUtil.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + orgId, ip,
				token);
		if (null == httpValueByGet) {
			logger.error("[queryFacilityBindListByPage()->invalid：组织查询失败!]");
			organizationIdList.add(orgId);
			organizationInfo.put(orgId, "");
		} else {
			JSONArray jsonArray = JSON.parseArray(httpValueByGet);
			List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
			// 存储组织id和name对应关系
			for (Map map : organizationInfoList) {
				organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
				organizationInfo.put("" + map.get(InterGatewayConstants.ORGANIZATION_ID),
						"" + map.get(InterGatewayConstants.ORGANIZATION_NAME));
			}
		}
	}



	/**
	 * 
	 * @Description:封装组织信息
	 * @param facilityPage
	 */
	/*
	 * @SuppressWarnings({ "rawtypes", "unchecked" }) private void
	 * pageOrg(List<Map<String, Object>> facilityPage) { RpcResponse<List<Map>>
	 * orgInfo = accSourceQuery
	 * .getListMenuByIds(UtilTool.getListNoRepeat(facilityPage,
	 * FacilitiesContants.ORGANIZATION_ID)); List<Map> orgLists = new
	 * ArrayList<Map>(); if (orgInfo.isSuccess()) { orgLists =
	 * orgInfo.getSuccessValue(); } for (Map<String, Object> map : facilityPage)
	 * { if (map.containsKey(FacilitiesContants.ORGANIZATION_ID)) { String orgId
	 * = String.valueOf(map.get(FacilitiesContants.ORGANIZATION_ID)); String
	 * orgName = String.valueOf( ((Map<String, Object>)
	 * UtilTool.getStringByKey(orgLists, FacilitiesContants.ID, orgId))
	 * .getOrDefault(FacilityDeviceContants.SOURCE_NAME, ""));
	 * map.put(FacilityDeviceContants.ORGANIZATION_NAME, orgName); } } }
	 */

	@Override
	public RpcResponse<List<String>> queryAllBoundDeviceId() {
		logger.info(String.format("[queryAllBoundDeviceId()方法执行开始...]"));
		try {
			List<String> deviceList = facilityDeviceQueryRepository.queryAllBoundDeviceId();
			logger.info(String.format("[queryAllBoundDeviceId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询所有已绑定设备的id", deviceList);
		} catch (Exception e) {
			logger.error("queryAllBoundDeviceId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<String>> queryAllDeviceByFacId(String facId, String secreat) {
		logger.info(String.format("[queryAllDeviceByFacId()方法执行开始...,参数：【%s】【%s】]", facId, secreat));
		try {
			if (StringUtils.isBlank(facId)) {
				return RpcResponseBuilder.buildErrorRpcResp("设施id不能为空！");
			}
			if (StringUtils.isBlank(secreat)) {
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			Map<String, String> parm = new HashMap<>(16);
			parm.put("facId", facId);
			parm.put("secreat", secreat);
			List<String> deviceList = facilityDeviceQueryRepository.queryAllDeviceInfo(parm);
			logger.info(String.format("[queryAllDeviceByFacId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, deviceList);
		} catch (Exception e) {
			logger.error("queryAllDeviceByFacId()->exception", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilityDeviceQueryService#findDeviceByFacIds(java.util.List)
	 */
	@Override
	public RpcResponse<List<String>> findDeviceByFacIds(List<String> facIds) {
		logger.info(String.format("[findDeviceByFacIds()方法执行开始...,参数：【%s】]", facIds));

		try {

			// 校验数据
			if (facIds == null || facIds.size() == 0) {
				logger.error(String.format("[findDeviceByFacIds()->%s]", "设施ids不能为空！"));
				return RpcResponseBuilder.buildErrorRpcResp("设施ids不能为空！");
			}

			// 分装信息
			Map<String, Object> facIdsMap = Maps.newHashMap();
			facIdsMap.put("facIds", facIds);
			List<String> findDeviceByFacIds = facilityDeviceQueryRepository.findDeviceByFacIds(facIdsMap);

			logger.info(String.format("[findDeviceByFacIds()->%s---%s]", MessageConstant.SEARCH_SUCCESS,
					findDeviceByFacIds));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, findDeviceByFacIds);
		} catch (Exception e) {
			logger.error("findDeviceByFacIds()->", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());

		}

	}



	/**
	 * @see com.run.locman.api.query.service.FacilityDeviceQueryService#findMongDbDeviceState(java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Map<String, Object>> findMongDbDeviceState(String deviceId,String accessSecret) {
		logger.info(String.format("[findMongDbDeviceState()方法执行开始...,参数：【%s】]", deviceId));
		try {
			if (StringUtils.isBlank(deviceId)) {
				return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空！");
			}
			DBObject dbconditon=new BasicDBObject();
			dbconditon.put("deviceId", deviceId);
			Query query =new BasicQuery(dbconditon);
			Map<String,Object>  deviceState1=mongoTemplate.findOne(query, Map.class, "deviceState");
			if(null !=deviceState1 && !deviceState1.isEmpty()) {
				List<Map<String,Object>> deviceState=(List<Map<String, Object>>) deviceState1.get("attributeInfo");
				Map<String,Object>  deviceThisState = new HashMap<>(16);
				for(Map<String,Object> map:deviceState) {
					if(null !=map) {
					String attributeAlias=map.get("attributeAlias")+"";
					String attributeReportedValue=map.get("attributeReportedValue")+"";
					deviceThisState.put(attributeAlias, attributeReportedValue);
					RpcResponse<Map> dataConvertInfo =deviceInfoConvertQueryService.dataConvert(attributeReportedValue, accessSecret);
					if(dataConvertInfo.isSuccess()) {
						String value=dataConvertInfo.getSuccessValue().get(DeviceInfoConvertConstans.CHINA_KEY)+"";
						deviceThisState.put(attributeAlias,value);
					}
					}
				}
				logger.info("[findMongDbDeviceState()]->方法执行成功！");
				return RpcResponseBuilder.buildSuccessRpcResp("查询MongoDB中设备状态成功！", deviceThisState);
			}
			return RpcResponseBuilder.buildErrorRpcResp("查询MongoDB中设备状态失败！");
			
		}catch(Exception e) {
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		
	}



	/**
	 * @see com.run.locman.api.query.service.FacilityDeviceQueryService#onlineQuery()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map<String, Object>>> onlineQuery(String reportType) {
		logger.info(String.format("[onlineQuery()方法执行开始...,参数：【%s】]", reportType));
		try {
		int sum=0;
		int avg=0;
		int num=0;
		
		List<Map<String,Object>> mapList=facilityDeviceQueryRepository.findOnlineQuery();
		for(Map<String,Object> map:mapList) {
			if(null != map) {
			String deviceId=map.get("deviceId")+"";
//			String deviceId="a43eed4269aa89a813f0d1ce7979d74d";
			DBObject dbconditon=new BasicDBObject();
			dbconditon.put("deviceId", deviceId);
//			dbconditon.put("attributeReportedValue", "timing");//trigger
			Query query =new BasicQuery(dbconditon);
			query.addCriteria(Criteria.where("attributeInfo.attributeReportedValue").is(reportType));
			List<Map> deviceState1=mongoTemplate.find(query, Map.class,"deviceHistoryState");
			if(null != deviceState1 && !deviceState1.isEmpty()) {
			List<Integer> input =new ArrayList<Integer>();
			for(Map<String,Object> deviceThisState:deviceState1) {//all
				if(null != deviceThisState) {
				List<Map<String,Object>> lists=(List<Map<String, Object>>) deviceThisState.get("attributeInfo");
				logger.info("onlineQuery()——>lists"+lists.size());
				logger.info("onlineQuery()——>lists"+lists.get(26));
				Map<String,Object> map26=lists.get(26);
				String xinzaobizhi=map26.get("attributeReportedValue")+"";
				if(null != xinzaobizhi &&"".equals(xinzaobizhi)) {
					num=Integer.parseInt(xinzaobizhi);
					input.add(num);
					sum=num+sum;
				}else {
					input.add(0);
				}
//sss
				}	
			}
			int max=Collections.max(input);
			int min=Collections.min(input);
			avg=(sum-max-min)/(deviceState1.size()-2);
			//添加信噪比平均值
			map.put("信噪比平均值", avg);
			}else {
				map.put("信噪比平均值", 0);
			}
			map.put("上报类型", "timing");
			map.put("上报次数",deviceState1.size());
			}	
		}
		return RpcResponseBuilder.buildSuccessRpcResp("查询成功！ ", mapList);
	}catch(Exception e) {
		logger.error("onlineQuery()->exception",e);
		return RpcResponseBuilder.buildExceptionRpcResp(e);
	}
	}
	
	public RpcResponse<List<Map<String, Object>>> onlineQueryNew(String reportType) {
		
		
		return null;
		
	}
	

}
