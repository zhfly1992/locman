/*
 * File name: FacilitiesQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tm 2017年08月08日 ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.constants.common.ResultMsgConstants;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.FacilitiesCrudService;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.query.repository.AreaQueryRepository;
import com.run.locman.api.query.repository.FacilitiesQueryRepository;
import com.run.locman.api.query.service.FacilitiesQueryService;

import com.run.locman.api.util.ConvertUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.LBSUtil;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.SimpleOrderConstants;
import com.run.redis.config.JedisClusterPipeLine;
import com.run.usc.base.query.UserBaseQueryService;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Description: 设施查询实现类
 * @author: 田明
 * @version: 1.0, 2017年08月08日
 */
@Repository
public class FacilitiesQueryServiceImpl implements FacilitiesQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesQueryRepository	facilitiesQueryRepository;

	@Autowired
	private FacilitiesCrudService		facilitiesCrudService;

	@Autowired
	private UserBaseQueryService		userBaseQueryService;

	@Autowired
	AreaQueryRepository					areaQueryRepository;
	
	@Autowired
	@Qualifier("functionDomainRedisTemplate")
	private RedisTemplate<String,String>  redisTemplate;
	
	@Autowired
	@Qualifier("facilityMapRedisTemplate")
	private RedisTemplate<String,Map<String,Object>> redisTemplateMap;
	
	@Autowired
	JedisPoolConfig jedisPoolConfig;

	@Value("${api.host}")
	private String						ip;
	
	private String 	judgmentExpired="judgmentExpired:";
	
	private Long timeout=24*60*60L;
	
	private JedisClusterPipeLine		jedisClusterPipeLine;
	/*
	 * @Autowired public HttpServletRequest request;
	 */



	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Map<String, Object>> queryMapFacilities(JSONObject paramsObject) {
		logger.info(String.format("[queryMapFacilities()方法执行开始...,参数：%s]", paramsObject));
		try {
			// 查询设施参数设置
			String accessSecret = paramsObject.getString(FacilitiesContants.USC_ACCESS_SECRET);
			String facilitiesTypeId = paramsObject.getString(FacilitiesContants.FACILITIES_TYPE_ID);
			String searchKey = paramsObject.getString(FacilitiesContants.FACILITIES_SEARCHKEY);
			String organizationId = paramsObject.getString(FacilitiesContants.ORGANIZATION_ID);
			String completeAddress = paramsObject.getString(FacilitiesContants.COMPLETE_ADDRESS);
			String defenseState = paramsObject.getString(FacilitiesContants.DEFENSE_STATE);
			String alarmWorstLevel = "";
			if (!StringUtils.isBlank(paramsObject.getString(FacilitiesContants.ALARM_WORST_LEVEL))) {
				alarmWorstLevel = paramsObject.getString(FacilitiesContants.ALARM_WORST_LEVEL);
			}
			String token = paramsObject.getString(InterGatewayConstants.TOKEN);
			// 成功获取IP
			List<String> organizationIdList = Lists.newArrayList();
			if (!StringUtils.isBlank(organizationId)) {
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
					// 获取IP失败或者为null则查自己
					logger.error("[getAlarmInfoList()->invalid：IP获取失败,组织查询失败!]");
					organizationIdList.add(organizationId);
				}
			}

			//判断是否存在除alarmWorstLevel和accessSecret
			boolean cond=true;
			String conditions=facilitiesTypeId+searchKey+organizationId+completeAddress+defenseState;
			if(conditions.length()==0 && "3".equals(alarmWorstLevel) ) {
				cond=true;
			}else {
				cond=false;
			}
			
			
			
			String hashKey="fac:";
			Set<HostAndPort> clusterNodes = new HashSet<>();
			//获取所有的集群节点信息
			Iterable<RedisClusterNode> clusterGetNodes = redisTemplateMap.getConnectionFactory().getClusterConnection().clusterGetNodes();
			logger.info("[getAlarmInfoList()->info：获取所有的集群节点信息!");
			for(RedisClusterNode a:clusterGetNodes) {
				Integer port = a.getPort();
				String host = a.getHost();
				HostAndPort hostAndPort = new HostAndPort(host, port);
				clusterNodes.add(hostAndPort);
			}
			//建立管道
			jedisClusterPipeLine = new JedisClusterPipeLine(clusterNodes,jedisPoolConfig);
			
			
			//结果集
			List<Map<String, Object>> queryList=new ArrayList<>();
			
			String je = redisTemplate.opsForValue().get(judgmentExpired+accessSecret);
			
			//无其他条件，且关键字存在
			if(null !=je && je !="" && cond) {
				logger.info("queryMapFacilities()->执行缓存搜索");
				//取出Redis中存储的设施ID集合（无条件的all）
				long s = System.currentTimeMillis();
				List<String> redisFacIdList=redisTemplate.opsForList().range("ch:"+"facID:"+accessSecret, 0, -1);
				long s1 = System.currentTimeMillis();
				//数据
				logger.info(String.format("时间s1-s:%s",s1-s));	
				//集群批量操作
				queryList = jedisClusterPipeLine.clusterPiplineGet(redisFacIdList, redisTemplateMap);
//				for(int i=0;i<redisFacIdList.size();i++) {
//					queryList.add(redisTemplateMap.opsForValue().get(redisFacIdList.get(i)));
//				}
				
				long s2 = System.currentTimeMillis();
				logger.info(String.format("时间s2-s1:%s",s2-s1));
				//刷新关键字的过期时间（这个方法中尽量不让它过期）
				redisTemplate.expire(judgmentExpired+accessSecret, timeout, TimeUnit.SECONDS);
				
				//无其他条件但没有关键字
			}else if(cond && null==je){
				logger.info("queryMapFacilities()->执行无条件mysql搜索");
			 queryList = facilitiesQueryRepository.queryMapFacilities(accessSecret,
					facilitiesTypeId, searchKey, organizationIdList, completeAddress, alarmWorstLevel, defenseState);
			 if(!CollectionUtils.isEmpty(queryList)) {
				 List<String> idAll = redisTemplate.opsForList().range("ch:"+"facID:"+accessSecret, 0, -1);
				 for(int i =0;i<idAll.size();i++) {
						redisTemplate.opsForList().leftPop("ch:"+"facID:"+accessSecret);
					}
				 //mysql查出的设施ID  facIdList
				 List<String> facIdList=new ArrayList<>();
				 
				 //放入前序列化
				 RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplateMap.getKeySerializer();
				 RedisSerializer<Map<String, Object>> valueSerializer =  (RedisSerializer<Map<String, Object>>) redisTemplateMap.getValueSerializer();
				
				 //集群刷新
				 jedisClusterPipeLine.refreshClusterInfo();
				 int count=0;
				 for(Map<String ,Object> facMap:queryList) {
					 String facId=hashKey.concat(facMap.get("id")+"");
					 facIdList.add(facId);
					 //循环放入Redis集群中（facID）
					 jedisClusterPipeLine.set(keySerializer.serialize(facId), valueSerializer.serialize(facMap));
					 //每5000关闭一次管道，避免数据丢失
					 if(count%5000==0) {
						 jedisClusterPipeLine.sync();
						 jedisClusterPipeLine.close();
					 }
				 }
				 //同步
				 jedisClusterPipeLine.sync();
				 //将设施ID list 放入Redis中管理 
				 redisTemplate.opsForList().rightPushAll("ch:"+"facID:"+accessSecret, facIdList);
				 
			//给Redis里面加一个判断是否需要更新数据（无查询条件的）的KEY （judgmentExpired）,过期时间为24小时。
			redisTemplate.opsForValue().set(judgmentExpired+accessSecret, accessSecret, timeout, TimeUnit.SECONDS);
			 }
			 
			//有其他条件
			}else {
				//mysql 查询设施ID
				logger.info("queryMapFacilities()->执行条件搜索");
				List<String> queryMapFacilitiesId = facilitiesQueryRepository.queryMapFacilitiesId(accessSecret,
						facilitiesTypeId, searchKey, organizationIdList, completeAddress, alarmWorstLevel, defenseState);
				logger.info(String.format("queryMapFacilitiesId的长度为：%s", queryMapFacilitiesId.size()));
				//管道获取数据
				queryList = jedisClusterPipeLine.clusterPiplineGet(queryMapFacilitiesId, redisTemplateMap);
			}

			
			if(CollectionUtils.isEmpty(queryList)|| null ==queryList.get(0)) {
				logger.error("[queryMapFacilities()->error：queryList为null，没有查找到设施信息！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施信息：没有查找到设施信息！ ");
			}

			Map<String, Object> resultMap = Maps.newHashMap();
			if(queryList.size()>0) {
				logger.info("queryList的第一个元素是："+queryList.get(0));
			}
			resultClassify(queryList, resultMap);
			// TODO

			/*
			 * Map<String, Object> tempMap = Maps.newHashMap(); for (int i = 1;
			 * i < queryList.size(); i++) { Map<String, Object> map =
			 * queryList.get(i-1); String longitude = map.get("longitude") + "";
			 * String latitude = map.get("latitude") + ""; for (int j = i + 1; j
			 * < queryList.size(); j++) {
			 * 
			 * Map<String, Object> map2 = queryList.get(i); String iLng =
			 * map2.get("longitude") + ""; String iLat = map2.get("latitude") +
			 * ""; double distanceI = AddressTool.distance(iLng, iLat, latitude,
			 * longitude);
			 * 
			 * Map<String, Object> map3 = queryList.get(j); String jLng =
			 * map3.get("longitude") + ""; String jLat = map3.get("latitude") +
			 * ""; double distanceJ = AddressTool.distance(jLng, jLat, latitude,
			 * longitude);
			 * 
			 * if (distanceJ < distanceI) { tempMap = map3; queryList.set(j,
			 * map2); queryList.set(i, tempMap);
			 * 
			 * }
			 * 
			 * 
			 * } }
			 */

			/*
			 * queryList.sort(new Comparator<Map<String, Object>>() {
			 * 
			 * @Override public int compare(Map<String, Object> arg0,
			 * Map<String, Object> arg1) { double distance0 =
			 * AddressTool.distance(arg0.get("longitude") + "",
			 * arg0.get("latitude") + "", latitude, longitude); double distance1
			 * = AddressTool.distance(arg0.get("longitude") + "",
			 * arg0.get("latitude") + "", latitude, longitude);
			 * 
			 * if (distance1 < distance0) { return 1; } else if (distance1 ==
			 * distance0) { return 0; } else { return -1; } }
			 * 
			 * });
			 */
			logger.info(String.format("[queryMapFacilities()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(ResultMsgConstants.INFO_SEARCH_SUCCESS, resultMap);
		} catch (Exception ex) {
			logger.error("queryMapFacilities()->exception", ex);
			return RpcResponseBuilder.buildExceptionRpcResp(ex);
		}finally {
			 jedisClusterPipeLine.close();
		}
	}



	/**
	 * @Description:将查询到的结果分类，先按照设施类型，再按照告警等级和绑定状态
	 * @param queryList
	 * @param resultMap
	 */

	private void resultClassify(List<Map<String, Object>> queryList, Map<String, Object> resultMap) throws Exception {
		// 污水井-绑定
		List<Map<String, Object>> drainWellBound = new ArrayList<>();
		// 污水井-未绑定
		List<Map<String, Object>> drainWellUnbound = new ArrayList<>();
		// 污水井-一般告警
		List<Map<String, Object>> drainWellNormalAlarm = new ArrayList<>();
		// 污水井-紧急告警
		List<Map<String, Object>> drainWellUrgentAlarm = new ArrayList<>();
		// 雨水篦子-绑定
		List<Map<String, Object>> rainGrateBound = new ArrayList<>();
		// 雨水篦子-未绑定
		List<Map<String, Object>> rainGrateUnbound = new ArrayList<>();
		// 雨水篦子-一般告警
		List<Map<String, Object>> rainGrateNormalAlarm = new ArrayList<>();
		// 雨水篦子-紧急告警
		List<Map<String, Object>> rainGrateUrgentAlarm = new ArrayList<>();
		// 雨水井-绑定
		List<Map<String, Object>> rainWellBound = new ArrayList<>();
		// 雨水井-未绑定
		List<Map<String, Object>> rainWellUnbound = new ArrayList<>();
		// 雨水井-一般告警
		List<Map<String, Object>> rainWellNormalAlarm = new ArrayList<>();
		// 雨水井-紧急告警
		List<Map<String, Object>> rainWellUrgentAlarm = new ArrayList<>();
		// 其他-绑定
		List<Map<String, Object>> otherBound = new ArrayList<>();
		// 其他-未绑定
		List<Map<String, Object>> otherUnbound = new ArrayList<>();
		// 其他-一般告警
		List<Map<String, Object>> otherNormalAlarm = new ArrayList<>();
		// 其他-紧急告警
		List<Map<String, Object>> otherUrgentAlarm = new ArrayList<>();

		for (Map<String, Object> map : queryList) {
			
			if ((FacilitiesContants.CH_RAIN_WELL).equals(map.get(FacilitiesContants.FAC_FACILITYTYPEALIAS)+"")) {
				if (map.get(FacilitiesContants.ALARM_WORST_LEVEL) != null
						&& "1".equals(map.get(FacilitiesContants.ALARM_WORST_LEVEL)+"")) {
					rainWellUrgentAlarm.add(map);
				} else if (map.get(FacilitiesContants.ALARM_WORST_LEVEL) != null
						&& "2".equals(map.get(FacilitiesContants.ALARM_WORST_LEVEL)+"")) {
					rainWellNormalAlarm.add(map);
				} else if ((FacilitiesContants.UNBOUND).equals(map.get(FacilitiesContants.BOUND_STATE)+"")) {
					rainWellUnbound.add(map);
				} else {
					rainWellBound.add(map);
				}
			} else if ((FacilitiesContants.CH_DRAIN_WELL).equals(map.get(FacilitiesContants.FAC_FACILITYTYPEALIAS)+"")) {
				if (map.get(FacilitiesContants.ALARM_WORST_LEVEL) != null
						&& "1".equals(map.get(FacilitiesContants.ALARM_WORST_LEVEL)+"")) {
					drainWellUrgentAlarm.add(map);
				} else if (map.get(FacilitiesContants.ALARM_WORST_LEVEL) != null
						&& "2".equals(map.get(FacilitiesContants.ALARM_WORST_LEVEL)+"")) {
					drainWellNormalAlarm.add(map);
				} else if ((FacilitiesContants.UNBOUND).equals(map.get(FacilitiesContants.BOUND_STATE)+"")) {
					drainWellUnbound.add(map);
				} else {
					drainWellBound.add(map);
				}
			} else if ((FacilitiesContants.CH_RAIN_GRATE).equals(map.get(FacilitiesContants.FAC_FACILITYTYPEALIAS)+"")) {
				if (map.get(FacilitiesContants.ALARM_WORST_LEVEL) != null
						&& "1".equals(map.get(FacilitiesContants.ALARM_WORST_LEVEL)+"")) {
					rainGrateUrgentAlarm.add(map);
				} else if (map.get(FacilitiesContants.ALARM_WORST_LEVEL) != null
						&& "2".equals(map.get(FacilitiesContants.ALARM_WORST_LEVEL)+"")) {
					rainGrateNormalAlarm.add(map);
				} else if ((FacilitiesContants.UNBOUND).equals(map.get(FacilitiesContants.BOUND_STATE)+"")) {
					rainGrateUnbound.add(map);
				} else {
					rainGrateBound.add(map);
				}
			} else {
				if (map.get(FacilitiesContants.ALARM_WORST_LEVEL) != null
						&& "1".equals(map.get(FacilitiesContants.ALARM_WORST_LEVEL)+"")) {
					otherUrgentAlarm.add(map);
				} else if (map.get(FacilitiesContants.ALARM_WORST_LEVEL) != null
						&& "2".equals(map.get(FacilitiesContants.ALARM_WORST_LEVEL)+"")) {
					otherNormalAlarm.add(map);
				} else if ((FacilitiesContants.UNBOUND).equals(map.get(FacilitiesContants.BOUND_STATE)+"")) {
					otherUnbound.add(map);
				} else {
					otherBound.add(map);
				}
			}
		}

		// 封装分类好的数据
		Map<String, Object> rainWell = new HashMap<>();
		rainWell.put(FacilitiesContants.BOUND, rainWellBound);
		rainWell.put(FacilitiesContants.UNBOUND, rainWellUnbound);
		rainWell.put(FacilitiesContants.NORMAL_ALARM, rainWellNormalAlarm);
		rainWell.put(FacilitiesContants.URGENT_ALARM, rainWellUrgentAlarm);

		Map<String, Object> drainWell = new HashMap<>();
		drainWell.put(FacilitiesContants.BOUND, drainWellBound);
		drainWell.put(FacilitiesContants.UNBOUND, drainWellUnbound);
		drainWell.put(FacilitiesContants.NORMAL_ALARM, drainWellNormalAlarm);
		drainWell.put(FacilitiesContants.URGENT_ALARM, drainWellUrgentAlarm);

		Map<String, Object> rainGrate = new HashMap<>();
		rainGrate.put(FacilitiesContants.BOUND, rainGrateBound);
		rainGrate.put(FacilitiesContants.UNBOUND, rainGrateUnbound);
		rainGrate.put(FacilitiesContants.NORMAL_ALARM, rainGrateNormalAlarm);
		rainGrate.put(FacilitiesContants.URGENT_ALARM, rainGrateUrgentAlarm);

		Map<String, Object> other = new HashMap<>();
		other.put(FacilitiesContants.BOUND, otherBound);
		other.put(FacilitiesContants.UNBOUND, otherUnbound);
		other.put(FacilitiesContants.NORMAL_ALARM, otherNormalAlarm);
		other.put(FacilitiesContants.URGENT_ALARM, otherUrgentAlarm);

		resultMap.put(FacilitiesContants.RAIN_WELL, rainWell);
		resultMap.put(FacilitiesContants.DRAIN_WELL, drainWell);
		resultMap.put(FacilitiesContants.RAIN_GRATE, rainGrate);
		resultMap.put(FacilitiesContants.OTHER, other);
	}



	@Override
	public RpcResponse<List<Map<String, Object>>> queryMapFacilitiesToApp(String longitude, String latitude,
			Integer scopeValue, String accessSecret, String facilitiesTypeId, String facilitiesCode,
			String organizationId, String address) {
		logger.info(String.format("[queryMapFacilitiesToApp()方法执行开始...,参数：【%s】【%s】【%s】【%s】【%s】【%s】【%s】【%s】]", longitude,
				latitude, scopeValue, accessSecret, facilitiesTypeId, facilitiesCode, organizationId, address));
		Map<String, Object> map = new HashMap<>(16);
		if (longitude != null && !"".equals(longitude) && latitude != null && !"".equals(latitude)) {
			Integer range = scopeValue;
			double loDis = LBSUtil.getMeterOneLo(Double.valueOf(latitude)) * range;
			double laDis = LBSUtil.getMeterOneLa() * range;
			String reduceLatitude = String.valueOf(Double.valueOf(latitude).doubleValue() - laDis);
			String plusLatitude = String.valueOf(Double.valueOf(latitude).doubleValue() + laDis);
			String reduceLongitude = String.valueOf(Double.valueOf(longitude).doubleValue() - loDis);
			String plusLongitude = String.valueOf(Double.valueOf(longitude).doubleValue() + loDis);

			map.put("reduceLatitude", reduceLatitude);
			map.put("plusLatitude", plusLatitude);
			map.put("reduceLongitude", reduceLongitude);
			map.put("plusLongitude", plusLongitude);
		}
		map.put("accessSecret", accessSecret);
		map.put("facilitiesTypeId", facilitiesTypeId);
		map.put("facilitiesCode", facilitiesCode);
		map.put("organizationId", userBaseQueryService.querySourceChild(organizationId).getSuccessValue());
		map.put("address", address);
		try {
			// 查询设施参数设置
			List<Map<String, Object>> queryList = facilitiesQueryRepository.queryMapFacilitiesToApp(map);
			if (CollectionUtils.isEmpty(queryList)) {
				logger.warn("[queryMapFacilities()->invalid：没有查找到设施信息！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施信息：没有查找到设施信息！ ");
			}
			logger.info(String.format("[queryMapFacilitiesToApp()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(ResultMsgConstants.INFO_SEARCH_SUCCESS, queryList);
		} catch (Exception ex) {
			logger.error("queryMapFacilitiesToApp()->exception", ex);
			return RpcResponseBuilder.buildExceptionRpcResp(ex);
		}
	}



	@Override
	public RpcResponse<Facilities> findById(String id) {
		logger.info(String.format("[findById()方法执行开始...,参数：【%s】]", id));
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[findById()->invalid: 设施id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("设施id不能为空");
			}
			Facilities facilities = facilitiesQueryRepository.findById(id);
			logger.info(String.format("[findById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("设施信息查询成功", facilities);
		} catch (Exception e) {
			logger.error("findById()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> queryFacilityWorstAlarm(String facilitiesId) {
		logger.info(String.format("[queryFacilityWorstAlarm()方法执行开始...,参数：【%s】]", facilitiesId));
		try {
			if (StringUtils.isBlank(facilitiesId)) {
				logger.error("[queryFacilityWorstAlarm()->invalid: 设施id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("设施id不能为空");
			}
			Map<String, Object> map = new HashMap<>(16);
			map.put("facilitiesId", facilitiesId);
			String alarmWorstLevel = facilitiesQueryRepository.queryFacilityWorstAlarm(map);
			logger.info(String.format("[queryFacilityWorstAlarm()方法执行结束!返回信息：【%s】]", alarmWorstLevel));
			return RpcResponseBuilder.buildSuccessRpcResp("设施最高告警等级查询成功", alarmWorstLevel);
		} catch (Exception e) {
			logger.error("queryFacilityWorstAlarm()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getFacilityListBySelectKeyAndTypeName(JSONObject paramInfo)
			throws Exception {
		logger.info(String.format("[getFacilityListBySelectKeyAndTypeName()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[getAlarmOrderBypage()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getAlarmOrderBypage()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmOrderBypage()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getAlarmOrderBypage()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			Map<String, Object> map = Maps.newHashMap();
			map.put("selectKey", paramInfo.get(SimpleOrderConstants.SELECTKEY));
			map.put("facilitiesTypeId", paramInfo.get(AlarmInfoConstants.FACILITIES_TYPE_ID));
			map.put("accessSecret", paramInfo.get(AlarmInfoConstants.USC_ACCESS_SECRET));
			int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> resultMap = facilitiesQueryRepository.getFacilityListBySelectKeyAndTypeName(map);
			PageInfo<Map<String, Object>> pages = new PageInfo<>(resultMap);
			logger.info(String.format("[queryFacilityWorstAlarm()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
		} catch (Exception e) {
			logger.error("getFacilityListBySelectKeyAndTypeName()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<List<String>> getAreaById(String id) throws Exception {
		logger.info(String.format("[getAreaById()方法执行开始...,参数：【%s】]", id));
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[getAreaById()->invalid：区域id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("区域id不能为空！");
			}
			List<String> areas = facilitiesQueryRepository.getAreaByIds(id);
			logger.info(String.format("[getAreaById()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, areas);
		} catch (Exception e) {
			logger.error("getAreaById()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> getFacilityMangerStateById(List<String> idlist) {
		logger.info(String.format("[getFacilityMangerStateById()方法执行开始...,参数：【%s】]", idlist));
		try {
			if (idlist == null || idlist.isEmpty()) {
				logger.error("[getFacilityMangerStateById --> error: 设施id集合不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("设施id集合不能为空");
			}
			List<String> facilityMangerState = facilitiesQueryRepository.getFacilityMangerStateById(idlist);
			if (null != facilityMangerState && facilityMangerState.size() > 0) {
				logger.info("[getFacilityMangerStateById --> success: 查询设施成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("其中存在停用的设施！", Boolean.FALSE);
			} else if (null != facilityMangerState && facilityMangerState.size() == 0) {
				logger.info("[getFacilityMangerStateById --> success: 查询设施成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("其中不存在停用的设施！", Boolean.TRUE);
			} else {
				logger.error("[getFacilityMangerStateById --> error: 查询设施失败]");
				return RpcResponseBuilder.buildErrorRpcResp("error 查询设施失败");
			}
		} catch (Exception e) {
			logger.error("getFacilityMangerStateById ->exceptio", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#getFacilityByParam(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Facilities>> getFacilityByParam(JSONObject jsonParam) {
		logger.info(String.format("[getFacilityByParam()方法执行开始...,参数：【%s】]", jsonParam));
		try {
			if (jsonParam == null) {
				logger.error("[getFacilityByParam()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (StringUtils.isBlank(jsonParam.getString(FacilitiesContants.USC_ACCESS_SECRET))) {
				logger.error("[getFacilityByParam()->invalid：接入方密钥accessSecret不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥accessSecret不能为空！");
			}
			if (!jsonParam.containsKey(FacilitiesContants.ORGANIZATION_ID)) {
				logger.error("[getFacilityByParam()->invalid：组织organizationId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("组织organizationId不能为空！");
			}
			if (StringUtils.isBlank(jsonParam.getString(FacilitiesContants.FAC_FACILITIESCODE))) {
				logger.error("[getFacilityByParam()->invalid：设施序列号facilitiesCode不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施序列号facilitiesCode不能为空！");
			}
			if (StringUtils.isBlank(jsonParam.getString(InterGatewayConstants.TOKEN))) {
				logger.error("[getFacilityByParam()->invalid：token获取失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("token获取失败！");
			}
			// 先获取该组织及其下所有组织id
			String organizationId = jsonParam.getString("organizationId");
			String token = jsonParam.getString(InterGatewayConstants.TOKEN);
			List<String> organizationIdList = Lists.newArrayList();
			if (!StringUtils.isBlank(organizationId)) {

				String httpValueByGet = InterGatewayUtil
						.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip, token);
				if (null == httpValueByGet) {
					logger.error("[deviceCheck()->invalid：查询该组织的子组织失败!]");
					return RpcResponseBuilder.buildErrorRpcResp("查询该组织的子组织失败!");
				} else {
					JSONArray jsonArray = JSON.parseArray(httpValueByGet);
					List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
					for (Map map : organizationInfoList) {
						organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
					}
				}
			}
			// 参数组装组织id集合
			jsonParam.put("organizationId", organizationIdList);

			List<Facilities> resut = facilitiesQueryRepository.getFacilityByParam(jsonParam);

			if (null != resut) {
				logger.info("[getFacilityByParam()--success:查询成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", resut);
			} else {
				logger.error("[getFacilityByParam()--fail:查询失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败!");
			}

		} catch (Exception e) {
			logger.error("getFacilityByParam ->exceptio", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#getFacilityIdList(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getFacilityIdList(JSONObject jsonParam) {
		logger.info(String.format("[getFacilityIdList()方法执行开始...,参数：【%s】]", jsonParam));
		List<Map<String, Object>> facilityIdList = facilitiesQueryRepository.getFacilityIdList(jsonParam);
		logger.info(String.format("[getAllDrollsByPage()方法执行结束!]"));
		return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", facilityIdList);
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#getDeviceIdList(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getDeviceIdList(JSONObject jsonParam) {
		logger.info(String.format("[getDeviceIdList()方法执行开始...,参数：【%s】]", jsonParam));
		List<Map<String, Object>> facilityIdList = facilitiesQueryRepository.getDeviceIdList(jsonParam);
		logger.info(String.format("[getDeviceIdList()方法执行结束!]"));
		return RpcResponseBuilder.buildSuccessRpcResp("查询成功!", facilityIdList);
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#analysisSheetFile(org.apache.poi.ss.usermodel.Sheet)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> analysisInsertSheetFile(List<Facilities> sheet) {

		try {
			List<Map<String, Object>> errorList = Lists.newArrayList();
			// 基础参数校验
			if (sheet == null || sheet.size() == 0) {
				logger.error("[analysisInsertSheetFile()--fail:设施信息集合为null!]");
				return RpcResponseBuilder.buildErrorRpcResp("设施信息集合为null!");
			}

			// 解析areaid
			String complateAddress = getComplateAddress(sheet.get(0).getAreaId());

			// 失败信息set集合
			boolean implement = false;
			analysisSheet(sheet, errorList, complateAddress, implement);

			// 批量保存设施信息
			if (implement) {
				RpcResponse<Integer> batchInsertFacilitiesRpc = facilitiesCrudService.batchInsertFacilities(sheet);

				if (!batchInsertFacilitiesRpc.isSuccess()) {
					logger.error(String.format("[analysisInsertSheetFile()->error:%s]",
							batchInsertFacilitiesRpc.getMessage()));
					return RpcResponseBuilder.buildErrorRpcResp(batchInsertFacilitiesRpc.getMessage());
				}

				// 提出设备ID不存在的数据
				List<Facilities> deviceList = sheetGetridOf(sheet);
				if (batchInsertFacilitiesRpc.getSuccessValue() > 0) {
					if (deviceList.size() > 0) {
						// 批量保存设施与设备信息
						RpcResponse<Integer> batchInsertFacAndDeviceRpc = facilitiesCrudService
								.batchInsertFacAndDevice(deviceList);
						if (!batchInsertFacAndDeviceRpc.isSuccess()) {
							logger.error(String.format("[analysisInsertSheetFile()->error:%s]",
									batchInsertFacAndDeviceRpc.getMessage()));
							return RpcResponseBuilder.buildErrorRpcResp(batchInsertFacAndDeviceRpc.getMessage());
						}

						if (batchInsertFacAndDeviceRpc.getSuccessValue() <= 0) {
							logger.error(String.format("[analysisInsertSheetFile()->error: %s = %s！ ]",
									"batchInsertFacAndDevice", PublicConstants.PARAM_ERROR));
							return RpcResponseBuilder
									.buildErrorRpcResp(String.format("[analysisInsertSheetFile()->error: %s = %s！ ]",
											"batchInsertFacAndDevice", PublicConstants.PARAM_ERROR));
						}
					}

				} else {
					logger.error(String.format("[analysisInsertSheetFile()->error: %s = %s！ ]", "batchInsertFacilities",
							PublicConstants.PARAM_ERROR));
					return RpcResponseBuilder
							.buildErrorRpcResp(String.format("[analysisInsertSheetFile()->error: %s = %s！ ]",
									"batchInsertFacilities", PublicConstants.PARAM_ERROR));
				}

			}

			return RpcResponseBuilder.buildSuccessRpcResp(
					String.format("[analysisInsertSheetFile()->suc: %s！ ]", PublicConstants.PARAM_SUCCESS), errorList);

		} catch (Exception e) {
			logger.error("analysisSheetFile ->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @Description:
	 * @param sheet
	 * @param errorList
	 * @param complateAddress
	 * @param implement
	 * @return
	 * @throws Exception
	 */

	private void analysisSheet(List<Facilities> sheet, List<Map<String, Object>> errorList, String complateAddress,
			boolean implement) throws Exception {
		for (Facilities facilities : sheet) {

			// 设置完整的地址
			facilities.setCompleteAddress(complateAddress + facilities.getAddress());

			Map<String, Object> facMap = ConvertUtil.beanToMap(facilities);
			// 设施序列号是否重复
			Integer checkFacilitiesCode = checkFacilitiesCode(facilities);
			if (checkFacilitiesCode > 0) {
				logger.info(String.format("[analysisInsertSheetFile()->error:当前第%s行,设施序列号重复！]", facilities.getRow()));
				facMap.put(PublicConstants.IDNEX, 0);
				errorList.add(facMap);
				continue;
			}

			// 设施序列号正则匹配
			String facilitiesCode = facilities.getFacilitiesCode();
			if (!facilitiesCode.matches(FacilitiesContants.CHECK_CODE)) {
				logger.info(String.format("[analysisInsertSheetFile()->error:当前第%s行,设施序列号只能输入数字+字母！]",
						facilities.getRow()));
				facMap.put(PublicConstants.IDNEX, 0);
				errorList.add(facMap);
				continue;
			}

			// 通过基础设施类型名称和设施类型名称获取设施类型ID
			String facilitiesTypeId = findFacilitiesTypeId(facilities);
			if (StringUtils.isBlank(facilitiesTypeId)) {
				logger.info(
						String.format("[analysisInsertSheetFile()->error:当前第%s行,查询设施类型id不存在！]", facilities.getRow()));
				facMap.put(PublicConstants.IDNEX, 1);
				errorList.add(facMap);
				continue;
			}
			facilities.setFacilitiesTypeId(facilitiesTypeId);

			// 设备ID不存在也可以进行保存
			if (StringUtils.isBlank(facilities.getDeviceId())) {
				implement = true;
				continue;
			}
			// 设备Id是否存在
			Integer checkDeviceId = checkDeviceId(facilities);
			if (checkDeviceId == 0) {
				logger.info(String.format("[analysisInsertSheetFile()->error:当前第%s行,设备ID不存在或设备已有绑定的设施！]",
						facilities.getRow()));
				facMap.put(PublicConstants.IDNEX, 5);
				errorList.add(facMap);
				continue;
			}

			implement = true;
		}
	}



	/**
	 * @Description:完整地址=区域+地址
	 * @param areaId
	 * @param address
	 * @return
	 */

	private String getComplateAddress(String areaId) throws Exception {
		List<String> asList = Arrays.asList(areaId.split(","));
		return areaQueryRepository.getAreaName(asList);
	}



	/**
	 * 
	 * @Description:校验设备id是否存在以及是否已经绑定与设施关系
	 * @param facilities
	 * @return
	 * @throws Exception
	 */
	private Integer checkDeviceId(Facilities facilities) throws Exception {

		Map<String, String> deviceMap = Maps.newHashMap();
		deviceMap.put(FacilitiesContants.GATEWAY_DEVICE_ID, facilities.getDeviceId());
		deviceMap.put(FacilitiesContants.USC_ACCESS_SECRET, facilities.getAccessSecret());

		// 设备Id是否存在
		int checkDeviceId = facilitiesQueryRepository.checkDeviceId(deviceMap);
		int checkDeviceOrFac = facilitiesQueryRepository.checkDeviceOrFac(facilities.getDeviceId());

		if (checkDeviceId > 0 && checkDeviceOrFac == 0) {
			return 1;
		}

		return 0;
	}



	/**
	 * 
	 * @Description:获取设施类型ID
	 * @param facilities
	 * @return
	 * @throws Exception
	 */
	private String findFacilitiesTypeId(Facilities facilities) throws Exception {

		Map<String, Object> facMaps = Maps.newHashMap();
		facMaps.put(FacilitiesContants.FACILITESTYPE, facilities.getFacilitiesType());
		facMaps.put(FacilitiesContants.BASICS_FACILITIES_TYPE, facilities.getBasicsFacType());
		facMaps.put(FacilitiesContants.USC_ACCESS_SECRET, facilities.getAccessSecret());
		return facilitiesQueryRepository.findFacilitiesTypeId(facMaps);
	}



	/**
	 * 
	 * @Description:校验设施序列号
	 * @param facilities
	 * @return
	 * @throws Exception
	 */
	private Integer checkFacilitiesCode(Facilities facilities) throws Exception {

		Map<String, Object> facMaps = Maps.newHashMap();
		facMaps.put(FacilitiesContants.FAC_FACILITIESCODE, facilities.getFacilitiesCode());
		facMaps.put(FacilitiesContants.USC_ACCESS_SECRET, facilities.getAccessSecret());

		return facilitiesQueryRepository.checkFacilitiesCode(facMaps);
	}



	private List<Facilities> sheetGetridOf(List<Facilities> sheet) {
		// 剔除设备ID不存在的数据
		List<Facilities> deviceList = Lists.newArrayList();
		for (Facilities facilities : sheet) {
			if (facilities.getDeviceId() != null) {
				deviceList.add(facilities);
			}
		}
		return deviceList;
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#getBoundDeviceInfo(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getBoundDeviceInfo(String facilityId, String accessSecret) {
		try {
			if (StringUtils.isBlank(facilityId)) {
				logger.error("[getBoundDeviceInfo()->invalid：设施id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施id不能为空！");
			}
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[getBoundDeviceInfo()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			List<Map<String, Object>> deviceInfo = facilitiesQueryRepository.getBoundDeviceInfo(facilityId,
					accessSecret);
			if (deviceInfo != null) {
				logger.info("getBoundDeviceInfo()->success:查询成功");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", deviceInfo);
			} else {
				logger.error("[getBoundDeviceInfo()->invalid：查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
		} catch (Exception e) {
			logger.error("getBoundDeviceInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#findFacitiesByIds(java.util.List)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> findFacitiesByIds(List<String> idList) {
		try {
			if (null == idList || idList.isEmpty()) {
				logger.error("[getBoundDeviceInfo()->invalid：设施id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施id不能为空！");
			}

			List<Map<String, Object>> facitiesInfo = facilitiesQueryRepository.findFacitiesByIds(idList);
			if (facitiesInfo != null) {
				logger.info("findFacitiesByIds()->success:查询成功");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", facitiesInfo);
			} else {
				logger.error("[findFacitiesByIds()->invalid：查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
		} catch (Exception e) {
			logger.error("findFacitiesByIds()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#getBoundDevicesInfoByfacilityIds(java.util.List,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getBoundDevicesInfoByfacIds(List<String> idList,
			String accessSecret) {
		try {
			logger.info(
					String.format("[getBoundDevicesInfoByfacIds()-->ids:%s ; accessSecret:%s]", idList, accessSecret));
			if (null == idList || idList.isEmpty()) {
				logger.error("[getBoundDevicesInfoByfacilityIds()->invalid：设施id集合不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施id集合不能为空！");
			}
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[getBoundDevicesInfoByfacilityIds()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			List<Map<String, Object>> devicesInfo = facilitiesQueryRepository.getBoundDevicesInfoByfacIds(idList,
					accessSecret);
			if (devicesInfo != null) {
				logger.info("getBoundDevicesInfoByfacilityIds()->success:查询成功");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", devicesInfo);
			} else {
				logger.error("[getBoundDevicesInfoByfacilityIds()->invalid：查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
		} catch (Exception e) {
			logger.error("getBoundDevicesInfoByfacilityIds()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#countFacByTypeAndOrg(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> countFacByTypeAndOrg(String accessSecret, String organizationId) {

		try {
			logger.info(String.format(
					"[countFacitiesByTypeAndOrganizationId()方法执行开始...,参数：accessSecret【%s】,organizationId 【%s】]",
					accessSecret, organizationId));

			List<Map<String, Object>> res = facilitiesQueryRepository.countFacByTypeAndOrg(organizationId,
					accessSecret);
			if (res == null) {
				logger.error("[countFacitiesByTypeAndOrganizationId()->error：查询结果为空，查询失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询结果为空，查询失败");
			}
			// 将返回的list转换为map
			/*
			 * Map<String, Object> data = Maps.newHashMap();
			 * 
			 * for (int i = 0; i < res.size(); i++) {
			 * data.put(res.get(i).get("facilityTypeAlias").toString(),
			 * res.get(i).get("count")); }
			 */
			logger.info("[countFacitiesByTypeAndOrganizationId()->success：查询成功！]");
			return RpcResponseBuilder.buildSuccessRpcResp("success", res);
		} catch (Exception e) {
			logger.error("countFacitiesByTypeAndOrganizationId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#countFacNumByStreet(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map<String, Object>>> countFacNumByStreet(String accessSecret, String token) {
		try {
			logger.info(
					String.format("[countFacByStreet()方法执行开始...,参数：accessSecret【%s】,token 【%s】]", accessSecret, token));

			// 查询所有组织信息
			String allOrgInfo = InterGatewayUtil.getHttpValueByGet(InterGatewayConstants.U_ORGANIZATIONS + accessSecret,
					ip, token);
			// 如果查询失败,组织信息为空
			if (null == allOrgInfo) {
				logger.error("[countFacByStreet()->invalid：组织查询失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败!未查询到街道办信息");
			} else {
				JSONArray allOrgInfoArray = JSON.parseArray(allOrgInfo);
				List<Map> organizationInfoList = allOrgInfoArray.toJavaList(Map.class);

				
				/*String firstOrgInfo = InterGatewayUtil.getHttpValueByGet(
						InterGatewayConstants.GET_ORGINFO_BY_AS.replace("{accessSecret}", accessSecret), ip, token);
				JSONObject json = JSONObject.parseObject(firstOrgInfo);
				JSONArray jsonArray = json.getJSONArray("datas");
				List<Map> jsonMap = jsonArray.toJavaList(Map.class);*/
				// 街道办id与名称
				// Map<String, Object> idRsName = Maps.newHashMap();
				// 街道办id(一级组织id)
				// List<String> firstOrgIds = Lists.newArrayList();
				
				//查询组织架构,循环遍历出四级组织(接入方作为一级组织)
				/*String allOrganization = InterGatewayUtil.getHttpValueByGet(InterGatewayConstants.U_ORGANIZATIONS + accessSecret, ip, token);
				JSONArray jsonArray = JSONObject.parseArray(allOrganization);
				List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);*/
				
				List<Map> organization3 = removeSecondOrg(organizationInfoList);
				
				

				List<Map<String, Object>> resultList = Lists.newArrayList();

				for (Map firstOrg : organization3) {
					Map<String, Object> firstOrgResult = Maps.newHashMap();
					// 街道办下所有街道的组织id
					List<String> ownAndSonIds = Lists.newArrayList();
					Object sourceName = firstOrg.get("sourceName");
					Object id = firstOrg.get(InterGatewayConstants.ORGANIZATION_ID);
					String idStr = id + "";
					if (null != id && StringUtils.isNotBlank(idStr)) {
						ownAndSonIds.add(idStr);
						for (Map orgInfo : organizationInfoList) {
							Object parentId = orgInfo.get("parentId");
							String parentIdStr = parentId + "";
							Object sonId = orgInfo.get(InterGatewayConstants.ORGANIZATION_ID);
							String sonIdStr = sonId + "";
							boolean isSonId = (null != parentId && null != sonId && StringUtils.isNotBlank(parentIdStr)
									&& StringUtils.isNotBlank(sonIdStr) && idStr.equals(parentIdStr));
							if (isSonId) {
								ownAndSonIds.add(sonIdStr);
							}
						}
						// 查询mysql
						List<Map<String, Object>> facTypeAndNumByStreet = facilitiesQueryRepository
								.countFacNumByStreet(ownAndSonIds, accessSecret);
						firstOrgResult.put("streetName", sourceName);
						firstOrgResult.put("data", facTypeAndNumByStreet);
					}
					resultList.add(firstOrgResult);
				}
				logger.info("[countFacitiesByTypeAndOrganizationId()->success：查询成功！]");
				return RpcResponseBuilder.buildSuccessRpcResp("success", resultList);

			}

		} catch (Exception e) {
			logger.error("countFacitiesByTypeAndOrganizationId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	  * 
	  * @Description: 将所有二级组织移除,把3级变为2级
	  * @param 
	  * @return
	  */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Map> removeSecondOrg(List<Map> jsonMap) {
		//存放二级组织map
		List<Map> organization2 = Lists.newArrayList();
		//存放2级组织id
		List<String> organizationId2 = Lists.newArrayList();
		
		//存放三级组织map,作为新的二级组织
		List<Map> organization3 = Lists.newArrayList();
		
		for (Map organization : jsonMap) {
			Object parentId = organization.get("parentId");
			String parentIdStr = parentId + "";
			if (null != parentId && StringUtils.isBlank(parentIdStr)) {
				organization2.add(organization);
				Object id = organization.get(InterGatewayConstants.ORGANIZATION_ID);
				String idStr = id + "";
				organizationId2.add(idStr);
			}
			
		}
		jsonMap.removeAll(organization2);
		
		for (Map organization : jsonMap) {
			Object parentId = organization.get("parentId");
			String parentIdStr = parentId + "";
			//判断是否是3级组织
			if (null != parentId && StringUtils.isNotBlank(parentIdStr) && organizationId2.contains(parentIdStr)) {
				organization.put("parentId", "");
				organization3.add(organization);
			}
			
		}
		jsonMap.removeAll(organization3);
		return organization3;
		
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#findFacilityLngLat(java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> findFacilityLngLat(String accessSecret) {
		try {
			logger.info(String.format("[findFacilityLngLat()方法执行开始...,参数：accessSecret【%s】]", accessSecret));
			
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[findFacilityLngLat()参数：accessSecret为空]");
				RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空!!!");
			}
			
			// 查询mysql
			List<Map<String, Object>> facTypeAndNumByStreet = facilitiesQueryRepository
					.findFacilityLngLat(accessSecret);
			if (null == facTypeAndNumByStreet) {
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			} else {
				logger.info("[findFacilityLngLat()-->查询成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", facTypeAndNumByStreet);
			}
		} catch (Exception e) {
			logger.error("findFacilityLngLat()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#findFacInfoByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> findFacInfoByDeviceId(String deviceId) {
		try {
			logger.info(String.format("[findFacInfoByDeviceId()方法执行开始...,参数：deviceId【%s】]", deviceId));
			
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[findFacInfoByDeviceId()参数：deviceId为空]");
				RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空!!!");
			}
			
			// 查询mysql
			JSONObject json = facilitiesQueryRepository.findFacInfoByDeviceId(deviceId);
			if (null == json) {
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			} else {
				logger.info("[findFacInfoByDeviceId()-->查询成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", json);
			}
		} catch (Exception e) {
			logger.error("findFacInfoByDeviceId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FacilitiesQueryService#updateKeyTime(java.lang.String)
	 */
	@Override
	public RpcResponse<String> updateKeyTime(String accessSecret) {
		try {
			String key = redisTemplate.opsForValue().get(judgmentExpired+accessSecret);
			
			if(null !=key && key !="") {
				redisTemplate.expire(judgmentExpired+accessSecret, 1, TimeUnit.SECONDS);
				logger.info("[updateKeyTime()-->查询成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", key);
			}
			logger.error("[updateKeyTime()——>key为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("查询成功，key不存在");
		}catch(Exception e) {
			logger.error("updateKeyTime()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



}
