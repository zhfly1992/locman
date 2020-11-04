
package com.run.locman.query.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.InfoSummaryQueryRepository;
import com.run.locman.api.query.service.InfoSummaryQueryService;
import com.run.locman.api.util.GetInternateTimeUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.OrderConstants;

/**
 * 
 * @Description: 信息概览
 * @author: zhanghe
 * @version: 1.0, 2018年4月15日
 */
public class InfoSummaryQueryServiceImpl implements InfoSummaryQueryService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private InfoSummaryQueryRepository	infoSummaryQueryRepository;

	@Autowired
	private MongoTemplate				mongoTemplate;

	@Value("${api.host}")
	private String						ip;

	/*
	 * @Autowired public HttpServletRequest request;
	 */



	@Override
	public RpcResponse<Map<String, Object>> getDeviceCountByAccessSecret(String accessSecret) {
		logger.info(String.format("[getDeviceCountByAccessSecret()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getDeviceCount()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}
			Map<String, Object> res = infoSummaryQueryRepository.getDeviceCount(accessSecret);
			if (res == null) {
				logger.error("infoSummaryQueryRepository.getDeviceCount-->error,查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			logger.info("[getDeviceCountByAccessSecret()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);
		} catch (Exception e) {
			logger.error("getDeviceCount()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> getFacilityCountByAccessSecret(String accessSecret) {
		logger.info(String.format("[getFacilityCountByAccessSecret()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getFacilityCount()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}
			Map<String, Object> res = infoSummaryQueryRepository.getFacilityCount(accessSecret);
			if (res == null) {
				logger.error("infoSummaryQueryRepository.getFacilityCount-->error,查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			Object boundObj = res.get("bound");
			//设备数量1
			int bound = Integer.parseInt(boundObj + "")/* + 318*/;
			Object totalObj = res.get("total");
			int total = Integer.parseInt(totalObj + "")/* + 416*/;
			res.put("bound", bound);
			res.put("total", total);

			logger.info("[getFacilityCountByAccessSecret()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);
		} catch (Exception e) {
			logger.error("getFacilityCount()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> getUnprocessedOrderCountByAccessSecret(String accessSecret) {
		logger.info(String.format("[getUnprocessedOrderCountByAccessSecret()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getUnprocessedOrderCountByAccessSecret()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}
			Map<String, Object> orderCount = infoSummaryQueryRepository.getUpprocessedOrderCount(accessSecret);
			if (orderCount == null) {
				logger.error("infoSummaryQueryRepository.getUnprocessedOrderCountByAccessSecret-->error,查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			logger.info("[getUnprocessedOrderCountByAccessSecret()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", orderCount);
		} catch (Exception e) {
			logger.error("getUnprocessedOrderCount()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> getDeviceCountNotReportInSetDayByAccessSecret(String accessSecret) {
		logger.info(String.format("[getDeviceCountNotReportInSetDayByAccessSecret()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getDeviceCountNotReportInSetDay()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}
			List<String> idList = infoSummaryQueryRepository.getDeviceIdListByAccessSecret(accessSecret);
			// // 获取当前时间戳
			// long currentTimeStamp = System.currentTimeMillis() / 1000;
			// // 获得指定天数前时间戳
			// long setTimeStamp = currentTimeStamp -
			// CommonConstants.NOT_REPORT_SET_DAY * 86400;
			// DBObject dbCondition = new BasicDBObject();
			// // 时间判断条件
			// dbCondition.put("timestamp", new BasicDBObject("$lt",
			// setTimeStamp));
			// // id判断
			// dbCondition.put("deviceId", new BasicDBObject("$in", idList));
			// Query query = new BasicQuery(dbCondition);

			// 获取当前网络时间
			Long queryMillisTime = GetInternateTimeUtil.queryMillisTime();
			// 获取当前系统时间戳
			long currentTimeStamp = System.currentTimeMillis() / 1000;
			if (queryMillisTime != null) {
				// 获取当前网络时间戳
				currentTimeStamp = queryMillisTime / 1000;
			}
			// 获得指定时间前时间戳
			long setTimeStamp = currentTimeStamp - CommonConstants.NORMAL_REPORT_SET_DAY * 86400;
			// 兼容新老设备 时间判断条件,新设备timestamp为毫秒级,老设备为秒级
			Criteria orOperator = new Criteria().orOperator(
					new Criteria().andOperator(Criteria.where("timestamp").lt(setTimeStamp).and("deviceId").in(idList),
							Criteria.where("things.gatewayId").exists(false)),
					new Criteria().andOperator(
							Criteria.where("timestamp").lt(setTimeStamp * 1000).and("deviceId").in(idList),
							Criteria.where("things.gatewayId").exists(true)));

			Query query = new Query(orOperator);
			long res = mongoTemplate.count(query, "deviceState");
			logger.info("[getDeviceCountNotReportInSetDayByAccessSecret()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res + "");
		} catch (Exception e) {
			logger.error("getDeviceCountNotReportInSetTime()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> getNormalDeviceCountByAccessSecret(String accessSecret) {
		logger.info(String.format("[getNormalDeviceCountByAccessSecret()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error(" getNormalDeviceCount()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}
			List<String> idList = infoSummaryQueryRepository.getDeviceIdListByAccessSecret(accessSecret);
			// long currentTimeStamp = System.currentTimeMillis() / 1000;
			// long setTimeStamp = currentTimeStamp -
			// CommonConstants.NORMAL_REPORT_SET_DAY * 86400;
			// DBObject dbCondition = new BasicDBObject();
			// dbCondition.put("timestamp", new BasicDBObject("$gt",
			// setTimeStamp));
			// dbCondition.put("deviceId", new BasicDBObject("$in", idList));
			// Query query = new BasicQuery(dbCondition);

			// 获取当前网络时间
			Long queryMillisTime = GetInternateTimeUtil.queryMillisTime();
			// 获取当前系统时间戳
			long currentTimeStamp = System.currentTimeMillis() / 1000;
			if (queryMillisTime != null) {
				// 获取当前网络时间戳
				currentTimeStamp = queryMillisTime / 1000;
			}
			// 获得指定时间前时间戳
			long setTimeStamp = currentTimeStamp - CommonConstants.NORMAL_REPORT_SET_DAY * 86400;
			// 兼容新老设备 时间判断条件,新设备timestamp为毫秒级,老设备为秒级
			Criteria orOperator = new Criteria().orOperator(
					new Criteria().andOperator(Criteria.where("timestamp").lt(setTimeStamp).and("deviceId").in(idList),
							Criteria.where("things.gatewayId").exists(false)),
					new Criteria().andOperator(
							Criteria.where("timestamp").lt(setTimeStamp * 1000).and("deviceId").in(idList),
							Criteria.where("things.gatewayId").exists(true)));

			Query query = new Query(orOperator);
			long res = mongoTemplate.count(query, "deviceState");
			logger.info("[getNormalDeviceCountByAccessSecret()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", String.valueOf(res));
		} catch (Exception e) {
			logger.error(" getNormalDeviceCount()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> getUserNumberByAccessSecret(String accessSecret, String token) {
		logger.info(String.format("[getUserNumberByAccessSecret()方法执行开始...,参数：【%s】【%s】]", accessSecret, token));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getUserNumber()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}
			String httpValueByGet = InterGatewayUtil.getHttpValueByGet("/interGateway/v3/userNumber/" + accessSecret,
					ip, token);
			if (httpValueByGet == null || StringUtils.isBlank(httpValueByGet)) {
				logger.error("getUserNumber()-->fail:查询该接入方下用户数量失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询该接入方下用户数量失败");
			}
			int count = Integer.parseInt(httpValueByGet);
			logger.info("[getUserNumberByAccessSecret()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", String.valueOf(count));
		} catch (Exception e) {
			logger.error("getUserNumber()->Exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> getAccessInformationByAccessSecret(String accessSecret, String token) {
		logger.info(String.format("[getAccessInformationByAccessSecret()方法执行开始...,参数：【%s】【%s】]", accessSecret, token));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getAccessInformation()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}
			String httpValueByGet = InterGatewayUtil
					.getHttpValueByGet("/interGateway/v3/accessInformation/" + accessSecret, ip, token);

			if (httpValueByGet == null || StringUtils.isBlank(httpValueByGet)) {
				logger.error("getAccessInformation()-->fail:查询接入方信息失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询接入方信息失败");
			}
			JSONObject json = JSONObject.parseObject(httpValueByGet);
			String accessTenementName = json.getString("accessTenementName");
			logger.info("[getAccessInformationByAccessSecret()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", accessTenementName);
		} catch (Exception e) {
			logger.error("getAccessInformation()->Exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> getUsageRateByAccessSecret(String accessSecret, String token) {
		logger.info(String.format("[getUsageRateByAccessSecret()方法执行开始...,参数：【%s】【%s】]", accessSecret, token));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getUsageRate()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}

			String httpValueByGet = InterGatewayUtil.getHttpValueByGet("/interGateway/v3/user/usage/" + accessSecret,
					ip, token);
			if (httpValueByGet == null || StringUtils.isBlank(httpValueByGet)) {
				logger.error("getUsageRate()-->fail:查询使用率失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询使用率失败");
			}
			logger.info("[getUsageRateByAccessSecret()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", httpValueByGet + "%");
		} catch (Exception e) {
			logger.error("getUsageRate()->Exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#getDailyAlarmCountInMonth(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> getDailyAlarmCountInMonth(String accessSecret) {
		logger.info(String.format("[getDailyAlarmCountInMonth()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getDailyAlarmCountInMonth()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}

			// 查询，返回的数据需要处理
			// 查询紧急告警数量
			List<Map<String, Object>> urgentRes = infoSummaryQueryRepository
					.getDailyUrgentAlarmCountInMonth(accessSecret);
			if (urgentRes == null) {
				logger.error("getDailyAlarmCountInMonth-->error,查询紧急告警失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询紧急告警失败");
			}
			// 查询一般告警数量
			List<Map<String, Object>> normalRes = infoSummaryQueryRepository
					.getDailyNormalAlarmCountInMonth(accessSecret);
			if (normalRes == null) {
				logger.error("getDailyAlarmCountInMonth-->error,查询一般告警失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询一般告警失败");
			}
			// 将查询到的数据格式进行处理

			Map<String, Object> dailyUrgentAlarmCount = new LinkedHashMap<>();
			Map<String, Object> dailyNormalAlarmCount = new LinkedHashMap<>();
			for (int i = 0; i < urgentRes.size(); i++) {
				// alarmDate,count为sql语句中设置的查询列名
				dailyUrgentAlarmCount.put(urgentRes.get(i).get("alarmDate").toString(), urgentRes.get(i).get("count"));
			}

			for (int i = 0; i < normalRes.size(); i++) {
				// alarmDate,count为sql语句中设置的查询列名
				dailyNormalAlarmCount.put(normalRes.get(i).get("alarmDate").toString(), normalRes.get(i).get("count"));
			}

			Map<String, Object> res = new HashMap<>();
			res.put("urgentAlarm", dailyUrgentAlarmCount);
			res.put("normalAlarm", dailyNormalAlarmCount);

			logger.info("[getDailyAlarmCountInMonth()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);
		} catch (Exception e) {
			logger.error("getDailyAlarmCountInMonth()->Exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#getDailyFaultCountInMonth(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> getDailyFaultCountInMonth(String accessSecret) {
		logger.info(String.format("[getDailyFaultCountInMonth()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getDailyFaultCountInMonth()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}

			// 查询，返回的数据需要处理
			List<Map<String, Object>> res = infoSummaryQueryRepository.getDailyFaultCountInMonth(accessSecret);
			if (res == null) {
				logger.error("getDailyFaultCountInMonth-->error,查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}

			// 将查询到的数据格式进行处理
			Map<String, Object> dailyFaultCount = new LinkedHashMap<>();
			for (int i = 0; i < res.size(); i++) {
				// alarmDate,count为sql语句中设置的查询列名
				dailyFaultCount.put(res.get(i).get("date").toString(), res.get(i).get("count"));
			}

			logger.info("[getDailyFaultCountInMonth()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", dailyFaultCount);
		} catch (Exception e) {
			logger.error("getDailyFaultCountInMonth()->Exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countAlarmNumByDate(java.lang.String,
	 *      java.lang.String, int)
	 */
	@Override
	public RpcResponse<Map<String, Object>> countAlarmNumByDate(String accessSecret, String endTime, int dayNum) {
		logger.info(String.format("[countAlarmNumByDate()方法执行开始...,参数：accessSecret:%s , endTime:%s , dayNum:%s ]",
				accessSecret, endTime, dayNum));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("countAlarmNumByDate()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}

			// 查询，返回的数据需要处理
			List<Map<String, Object>> res = infoSummaryQueryRepository.countAlarmNumByDate(endTime, accessSecret,
					dayNum);
			if (res == null) {
				logger.error("countAlarmNumByDate-->error,查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}

			// 将查询到的数据格式进行处理
			Map<String, Object> dailyFaultCount = Maps.newLinkedHashMap();
			for (int i = res.size() - 1; i >= 0; i--) {
				// alarmDate,count为sql语句中设置的查询列名
				dailyFaultCount.put(res.get(i).get("date").toString(), res.get(i).get("num"));
			}

			logger.info("[countAlarmNumByDate()方法执行结束!]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", dailyFaultCount);
		} catch (Exception e) {
			logger.error("countAlarmNumByDate()->Exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.StatisticsQueryService#statisticAlarmOrderProcessState(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> getOrderProcessStateNumStatistic(int dayCount, String endTime,
			String accessSecret, String orderType) {

		logger.info(
				String.format("[getOrderProcessStateNumStatistic()方法执行开始...,查询天数：【%s】，endTime时间【%s】，accessSecret【%s】]",
						dayCount, endTime, accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[getOrderProcessStateNumStatistic()->error:accessSecret不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}

			// 因为每一天都有两条数据，需要取 天数*2条数据
			dayCount = dayCount * 2;
			List<Map<String, Object>> initalRes = new ArrayList<>();

			if (orderType.equals(OrderConstants.ALARM)) {
				initalRes = infoSummaryQueryRepository.getAlarmOrderProcessStateStatistic(endTime, accessSecret,
						dayCount);
			} else {
				initalRes = infoSummaryQueryRepository.getFaultOrderProcessStateStatistic(endTime, accessSecret,
						dayCount);
			}

			if (initalRes == null) {
				logger.error("[getOrderProcessStateNumStatistic->error]");
				return RpcResponseBuilder.buildErrorRpcResp("查询错误");
			}

			// 查询到的数据进行处理
			List<Map<String, Object>> res = new ArrayList<>();
			for (int m = 0; m < initalRes.size() - 1; m = m + 2) {
				Map<String, Object> map = new HashMap<>();
				map.put(CommonConstants.DATE, initalRes.get(m).get(CommonConstants.DATE));
				map.put(initalRes.get(m).get(CommonConstants.STATE).toString(),
						initalRes.get(m).get(CommonConstants.NUM));
				map.put(initalRes.get(m + 1).get(CommonConstants.STATE).toString(),
						initalRes.get(m + 1).get(CommonConstants.NUM));
				res.add(map);
			}
			Collections.reverse(res);
			logger.info(String.format("[getOrderProcessStateNumStatistic->success,orderType:%s]", orderType));
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);
		} catch (Exception e) {
			logger.error("[getOrderProcessStateNumStatistic()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countStreetAndStreetOfficeNum(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Map<String, Object>> countStreetAndStreetOfficeNum(String accessSecret, String token) {
		logger.info(
				String.format("[countStreetAndStreetOfficeNum()->进入方法，accessSecret:%s,token:%s]", accessSecret, token));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countStreetAndStreetOfficeNum()->error:accessSecret不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (!StringUtils.isBlank(ip)) {

				// 先暂时用已经有的接口，该功能最好是在usc的rpc中专门写功能，先查询街道办，在把街道办的idlist作为查询条件去查询
				String httpValueByGet = InterGatewayUtil
						.getHttpValueByGet(InterGatewayConstants.U_ORGANIZATIONS + accessSecret, ip, token);
				if (null == httpValueByGet) {
					logger.error("[countStreetAndStreetOfficeNum()->error:interGateway查询失败！]");
					return RpcResponseBuilder.buildErrorRpcResp("查询失败,interGateway返回为空");
				} else {
					int streetOfficeNum = 0;
					int streetNum = 0;
					JSONArray jsonArray = JSONArray.parseArray(httpValueByGet);
					List<Map> organInfo = jsonArray.toJavaList(Map.class);
					List<String> streetOfficeIds = new ArrayList<>();
					for (Map map : organInfo) {
						// 用名字中是否含街办作判断
						if (map.get("sourceName").toString().contains("街办")) {
							streetOfficeNum++;
							// 将街道办的id加入到队列
							streetOfficeIds.add(map.get("_id").toString());
						}
					}

					for (String id : streetOfficeIds) {
						for (Map map : organInfo) {
							if (map.containsKey("parentId")) {
								if (map.get("parentId").equals(id)) {
									streetNum++;
								}
							}
						}
					}
					Map<String, Object> result = new HashMap<>();
					result.put("streetOffice", streetOfficeNum);
					result.put("street", streetNum);
					logger.info("[countStreetAndStreetOfficeNum()->success]");
					return RpcResponseBuilder.buildSuccessRpcResp("查询成功", result);
				}
			} else {
				// ip为空则直接返回失败
				logger.error("[countStreetAndStreetOfficeNum()->fail:ip为空]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败，ip为空");
			}

		} catch (Exception e) {
			logger.error("[countStreetAndStreetOfficeNum()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countFacAlarmNum(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> countFacAlarmNum(String accessSecret) {
		logger.info(String.format("[进入countFacAlarmNum->accessSecret:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countFacAlarmNum()->error:accessSecret不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			Map<String, Object> countFacAlarmCount = infoSummaryQueryRepository.countFacAlarmCount(accessSecret);
			if (countFacAlarmCount == null) {
				logger.error("[countFacAlarmNum()->error:查询结果为null]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			logger.info("[countFacAlarmNum()->success]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", countFacAlarmCount);

		} catch (Exception e) {
			logger.error("[countFacAlarmNum()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

	
	
	/**
	 * <<<<<<< Updated upstream
	 * 
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countDeviceReportNum(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> countDeviceReportNum(String param) {
		logger.info(String.format("[进入countDeviceReportNum->param:%s]", param));
		try {
			if (StringUtils.isBlank(param)) {
				logger.error("[countDeviceReportNum()->error:param不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			SimpleDateFormat tempDate1 = new SimpleDateFormat("yyyy-MM-dd");
			String timeS = DateUtils.getDateString("end");
			// 当天23：59：59的时间戳timeStamp
			String timeStamp = DateUtils.dateToStamp(timeS);
			long timeInt = Long.parseLong(timeStamp);
			Map<String, Object> timeMap = new LinkedHashMap<String, Object>(16);
			Map<String, Object> facilityCount = infoSummaryQueryRepository.getFacilityCount(param);
			Object bound = facilityCount.get("bound");
			String boundStr = "0";
			if (null != bound) {
				boundStr = bound + "";
			}
			
			
			for (int i = 7; i >= 1; i--) {
				long timeStamp1 = timeInt - (86400000 * i);
				String timeInt1 = (timeStamp1 - 86399000) + "";
				/*BasicDBObject fieldsObject = new BasicDBObject();
				fieldsObject.put("deviceId", 1);
				DBObject dbObjectFv = new BasicDBObject("$gt", 1);
				DBObject dbObject = new BasicDBObject("$gte", timeInt1).append("$lte", timeStamp1 + "");
				DBObject ageCompare = new BasicDBObject("timestamp", dbObject);
				ageCompare.put("attributeInfo.attributeUnit", "");
				ageCompare.put("attributeInfo", new BasicDBObject("$elemMatch",
						new BasicDBObject("attributeName", "fv").append("attributeReportedValue", dbObjectFv)));
				Query query = new BasicQuery(ageCompare, fieldsObject);
				List<Map> deviceHistoryStateMap = mongoTemplate.find(query, Map.class, "deviceHistoryState");
				ArrayList<String> list = new ArrayList<String>();
				for (Map<String, Object> map : deviceHistoryStateMap) {
					String deviceId = map.get("deviceId") + "";
					list.add(deviceId);
				}
				Set<String> set = new HashSet<String>();
				set.addAll(list);
				logger.info("countDeviceReportNum()->上报设备set集合：" + set);*/
				String dateNum = tempDate1.format(new Date(Long.parseLong(timeInt1)));
				logger.info(String.format("%s--countDeviceReportNum（）历史表查询结果集大小为：%s", dateNum, boundStr));
				timeMap.put(dateNum, boundStr);
			}
			logger.info("countDeviceReportNum()->查询成功");
			return RpcResponseBuilder.buildSuccessRpcResp("统计前七天设备上报数量成功!", timeMap);

		} catch (Exception e) {
			logger.error("[countDeviceReportNum()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
	


	/**
	 * <<<<<<< Updated upstream
	 * 
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countDeviceReportNum(java.lang.String)
	 */
/*	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Map<String, Object>> countDeviceReportNum(String param) {
		logger.info(String.format("[进入countDeviceReportNum->param:%s]", param));
		try {
			if (StringUtils.isBlank(param)) {
				logger.error("[countDeviceReportNum()->error:param不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			SimpleDateFormat tempDate1 = new SimpleDateFormat("yyyy-MM-dd");
			String timeS = DateUtils.getDateString("end");
			// 当天23：59：59的时间戳timeStamp
			String timeStamp = DateUtils.dateToStamp(timeS);
			long timeInt = Long.parseLong(timeStamp);
			Map<String, Object> timeMap = new LinkedHashMap<String, Object>(16);
			for (int i = 7; i >= 1; i--) {
				long timeStamp1 = timeInt - (86400000 * i);
				String timeInt1 = (timeStamp1 - 86399000) + "";
				BasicDBObject fieldsObject = new BasicDBObject();
				fieldsObject.put("deviceId", 1);
				DBObject dbObjectFv = new BasicDBObject("$gte", 0);
				DBObject dbObject = new BasicDBObject("$gte", timeInt1).append("$lte", timeStamp1 + "");
				DBObject ageCompare = new BasicDBObject("timestamp", dbObject);
				ageCompare.put("attributeInfo.attributeUnit", "");
				ageCompare.put("attributeInfo", new BasicDBObject("$elemMatch",
						new BasicDBObject("attributeName", "fv").append("attributeReportedValue", dbObjectFv)));
				Query query = new BasicQuery(ageCompare, fieldsObject);
				List<Map> deviceHistoryStateMap = mongoTemplate.find(query, Map.class, "deviceHistoryState");
				ArrayList<String> list = new ArrayList<String>();
				for (Map<String, Object> map : deviceHistoryStateMap) {
					String deviceId = map.get("deviceId") + "";
					list.add(deviceId);
				}
				Set<String> set = new HashSet<String>();
				set.addAll(list);
				logger.info("countDeviceReportNum()->上报设备set集合：" + set);
				String dateNum = tempDate1.format(new Date(Long.parseLong(timeInt1)));
				logger.info(String.format("%s--countDeviceReportNum（）历史表查询结果集大小为：%s", dateNum, list.size()));
				timeMap.put(dateNum, set.size());
			}
			logger.info("countDeviceReportNum()->查询成功");
			return RpcResponseBuilder.buildSuccessRpcResp("统计前七天设备上报数量成功!", timeMap);

		} catch (Exception e) {
			logger.error("[countDeviceReportNum()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}*/



	/*
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#
	 * countDeviceByAlarmLevelInSevenDays(java.lang.String)
	 * 
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#
	 * countDeviceByAlarmLevelInSevenDays(java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> countDeviceByAlarmLevelInSevenDays(String accessSecret) {
		// TODO Auto-generated method stub
		logger.info(String.format("[进入countDeviceByAlarmLevelInSevenDays()->accessSecret:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("countDeviceByAlarmLevelInSevenDays->error:accessSecret为空");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			// 查询七天内每天的一般告警和紧急告警设备数量
			List<Map<String, Object>> initRes = infoSummaryQueryRepository
					.countDeviceNumByAlarmLevelInSevenDays(accessSecret);
			if (initRes.size() == 0) {
				logger.error("[countDeviceByAlarmLevelInSevenDays->()error:查询结果为空]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			// 将数据封装
			Set<String> dataList = new LinkedHashSet<>();
			List<String> urgentAlarm = new ArrayList<>();
			List<String> normalAlarm = new ArrayList<>();
			List<String> normalDevice = new ArrayList<>();

			int index;

			for (Map<String, Object> map : initRes) {
				if (null != map) {
					index = initRes.indexOf(map);
					dataList.add(map.get(CommonConstants.DATE) + "");
					if (null != map.get(AlarmInfoConstants.ALARMI_LEVEL)) {
						// 告警等级字段不为null且为1
						if (Integer.parseInt(map.get(AlarmInfoConstants.ALARMI_LEVEL) + "") == 1) {
							urgentAlarm.add(map.get("count") + "");

							if (index + 1 == initRes.size()) {
								// 最后一条的alarmLevel为1，则当天的alarmLevel为2的数量为0
								normalAlarm.add(0 + "");
							} else if (index + 1 < initRes.size()) {
								// 需要判断下一个数据的alarmLevel是否为1或者null，若为1或者null，则当天的告警等级为2的数量为0

								if (initRes.get(index + 1).get(AlarmInfoConstants.ALARMI_LEVEL) == null
										|| Integer.parseInt(initRes.get(index + 1).get(AlarmInfoConstants.ALARMI_LEVEL)
												+ "") == 1) {

									normalAlarm.add(0 + "");
								}
							}
						}
						// 告警等级字段不为null且为2
						else if (Integer.parseInt(map.get(AlarmInfoConstants.ALARMI_LEVEL) + "") == 2) {
							normalAlarm.add(map.get("count") + "");

							if (index == 0) {
								// 第一条数据的alarmLevel为2，则意味着当天的alarmLevel为1的数量为0
								urgentAlarm.add(0 + "");
							} else if (index - 1 >= 0) {
								// 需要判断上一个数据的alarmLevel是否为2或者null,若为2或者null,则当天的告警等级为1的数量为0

								if (initRes.get(index - 1).get(AlarmInfoConstants.ALARMI_LEVEL) == null
										|| Integer.parseInt(initRes.get(index - 1).get(AlarmInfoConstants.ALARMI_LEVEL)
												+ "") == 2) {
									urgentAlarm.add(0 + "");
								}

							}
						}
					} else {
						// 告警等级字段为null，则改天无告警
						urgentAlarm.add(0 + "");
						normalAlarm.add(0 + "");
					}
				}
			}

			// 获取每天的上报设备总数
			RpcResponse<Map<String, Object>> countDeviceReportNum = countDeviceReportNum(accessSecret);
			if (!countDeviceReportNum.isSuccess()) {
				logger.error("[countDeviceByAlarmLevelInSevenDays]->error:查询上报设备数失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询上报设备数失败");
			}
			Map<String, Object> countDevice = countDeviceReportNum.getSuccessValue();
			Iterator<String> iter = countDevice.keySet().iterator();

			// 每天正常上报数等于总数减去每天一般告警和紧急告警数
			for (int i = 0; i <= 6; i++) {
				while (iter.hasNext()) {
					String key = iter.next();
					String value = countDevice.get(key) + "";
					normalDevice.add(String.valueOf(Integer.parseInt(value) - Integer.parseInt(urgentAlarm.get(i))
							- Integer.parseInt(normalAlarm.get(i))));
				}
			}

			// 封装返回格式
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("date", dataList);
			jsonObject.put("urgentAlarm", urgentAlarm);
			jsonObject.put("normalAlarm", normalAlarm);
			jsonObject.put("normalDevice", normalDevice);
			logger.info("[countDeviceByAlarmLevelInSevenDays()->success]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询七天内每天设备上报数量按告警级别分类成功", jsonObject);

		} catch (Exception e) {
			logger.error("[countDeviceByAlarmLevelInSevenDays()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countAlarmOrderNumByStateAndAlarm(java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> countAlarmOrderNumByStateAndAlarm(String accessSecret) {
		// TODO Auto-generated method stub
		logger.info(String.format("[countAlarmOrderNumByStateAndAlarm()->进入方法,accessSecret:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countAlarmOrderNumByStateAndAlarm()->error:accessSecret为空]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			List<Map<String, Object>> initRes = infoSummaryQueryRepository
					.countAlarmOrderNumByAlarmLevelAndState(accessSecret);
			if (null == initRes || initRes.size() == 0) {
				logger.error("[countAlarmOrderNumByStateAndAlarm()->error:查询结果为空]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			JSONObject res = new JSONObject();

			logger.info("[countAlarmOrderNumByStateAndAlarm()->:查询数据库成功，开始处理数据]");

			for (Map<String, Object> map : initRes) {
				// 如果告警等级为1，则为紧急告警工单
				if (Integer.parseInt(map.get("alarmLevel") + "") == 1) {
					res.put("urgent" + map.get("processState"), map.get("count"));
				}
				// 如果告警等级为2，则为紧急告警工单
				else {
					res.put("normal" + map.get("processState"), map.get("count"));
				}
			}
			logger.info("[countAlarmOrderNumByStateAndAlarm()->success]");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", res);

		} catch (Exception e) {
			logger.error("[countAlarmOrderNumByStateAndAlarm()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countAlarmFacByOrg(java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Map<String, Object>> countAlarmFacByOrg(String accessSecret, String token) {
		logger.info(String.format("[进入countAlarmFacByOrg->accessSecret:%s]", accessSecret));
		try {

			List<Map<String, Object>> countAlarmFacByOrg = infoSummaryQueryRepository
					.getCountAlarmFacByOrg(accessSecret);

			List<String> listKey = new ArrayList<String>();
			for (Map<String, Object> resultMap : countAlarmFacByOrg) {
				if (!resultMap.isEmpty()) {
					String organizationId = resultMap.get("organizationId") + "";
					listKey.add(organizationId);
				}
			}
			JSONObject orgIdJson = new JSONObject();
			StringBuffer orgIdStr = new StringBuffer();
			for (int i = 0; i < listKey.size(); i++) {
				if (i == listKey.size()) {
					orgIdStr.append(listKey.get(i));
				}
				orgIdStr.append(listKey.get(i) + ',');
			}
			orgIdJson.put("organizationIds", orgIdStr);
			String httpValueByPost = InterGatewayUtil
					.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, orgIdJson, ip, token);
			if (null == httpValueByPost) {
				logger.error("通过interGateway查询组织名失败,直接返回告警工单统计数据");
				return RpcResponseBuilder.buildErrorRpcResp("查询组织名称失败！");
			} else {
				JSONObject httpValueByPostJson = JSON.parseObject(httpValueByPost);
				Map endMap = new LinkedHashMap<>();
				for (Map<String, Object> thisMap : countAlarmFacByOrg) {
					String orgId = thisMap.get("organizationId") + "";
					endMap.put(httpValueByPostJson.getString(orgId) + "", thisMap.get("count") + "");
				}
				logger.info("countAlarmFacByOrg()->success");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功！", endMap);
			}

		} catch (Exception e) {
			logger.error("[countAlarmFacByOrg()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countAlarmByIdAndTime(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("unused")
	@Override
	public RpcResponse<List<Map<String, Object>>> countAlarmByIdAndTime(JSONObject jsonObject) {
		logger.info(String.format("[进入countAlarmByIdAndTime->jsonObject:%s]", jsonObject.toJSONString()));
		try {
			if (StringUtils.isBlank(jsonObject.getString("deviceId"))) {
				logger.error("[countAlarmByIdAndTime()->error:设备Id为空]");
			}
			if (StringUtils.isBlank(jsonObject.getString("accessSecret"))) {
				logger.error("[countAlarmByIdAndTime()->error:厂家密钥为空]");
			}
			String deviceId = jsonObject.getString("deviceId");
			String accessSecret = jsonObject.getString("accessSecret");
			String endTime = jsonObject.getString("endTime");
			String startTime = jsonObject.getString("startTime");
			List<Map<String, Object>> countAlarmByIdAndTime = new ArrayList<Map<String, Object>>();
			if (StringUtils.isNotBlank(startTime)) {
				// 00:00:00-23:59:59
				if (StringUtils.isNotBlank(endTime)) {
					String sStartTime = DateUtils.formatDate(DateUtils.toDate(startTime), DateUtils.DATE_START);
					String eEndTime = DateUtils.formatDate(DateUtils.toDate(endTime), DateUtils.DATE_END);
					countAlarmByIdAndTime = infoSummaryQueryRepository.getCountAlarmByIdAndTime(accessSecret, deviceId,
							eEndTime, sStartTime);
				} else {
					String sStartTime = DateUtils.formatDate(DateUtils.toDate(startTime), DateUtils.DATE_START);
					String timeS = DateUtils.getDateString("end");
					String timeStamp = DateUtils.dateToStamp(timeS);
					long timeInt = Long.parseLong(timeStamp);
					String timeStamp1 = (timeInt - 86400000) + "";
					String eEndTime = DateUtils.stampToDate(timeStamp1);
					countAlarmByIdAndTime = infoSummaryQueryRepository.getCountAlarmByIdAndTime(accessSecret, deviceId,
							eEndTime, sStartTime);
				}
			} else {
				if (StringUtils.isNotBlank(endTime)) {
					String eEndTime = DateUtils.formatDate(DateUtils.toDate(endTime), DateUtils.END);
					String timeS = DateUtils.getDateString("start");
					String timeStamp = DateUtils.dateToStamp(timeS);
					long timeInt = Long.parseLong(timeStamp);
					String timeStamp1 = (timeInt - 86400000 * 30) + "";
					String sStartTime = DateUtils.stampToDate(timeStamp1);
					countAlarmByIdAndTime = infoSummaryQueryRepository.getCountAlarmByIdAndTime(accessSecret, deviceId,
							eEndTime, sStartTime);
				} else {
					String timeS = DateUtils.getDateString("end");
					String timeStamp = DateUtils.dateToStamp(timeS);
					long timeInt = Long.parseLong(timeStamp);
					String timeStamp1 = (timeInt - 86400000) + "";
					String eEndTime = DateUtils.stampToDate(timeStamp1);

					String timeS1 = DateUtils.getDateString("start");
					String timeStamp2 = DateUtils.dateToStamp(timeS);
					long timeInt1 = Long.parseLong(timeStamp);
					String timeStamp3 = (timeInt - 86400000 * 30) + "";
					String sStartTime = DateUtils.stampToDate(timeStamp3);
					countAlarmByIdAndTime = infoSummaryQueryRepository.getCountAlarmByIdAndTime(accessSecret, deviceId,
							eEndTime, sStartTime);
				}
			}
			if (countAlarmByIdAndTime != null && countAlarmByIdAndTime.size() > 0) {
				logger.info("countAlarmByIdAndTime()->查询成功");
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", countAlarmByIdAndTime);
			}
			logger.info("countAlarmByIdAndTime()->查询失败");
			return RpcResponseBuilder.buildErrorRpcResp("查询失败");

		} catch (Exception e) {
			logger.error("[countAlarmByIdAndTime()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countAlarmDevByRule(java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> countAlarmDevByRule(String accessSecret) {
		logger.info(String.format("[进入countAlarmDevByRule->accessSecret:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countAlarmDevByRule()->error:accessSecret为空]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			List<Map<String, Object>> countAlarmDevByRule = infoSummaryQueryRepository
					.getCountAlarmDevByRule(accessSecret);
			if (countAlarmDevByRule == null) {
				logger.info("countAlarmDevByRule()->查询失败");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			}
			logger.info("countAlarmDevByRule()->查询成功");
			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", countAlarmDevByRule);

		} catch (Exception e) {
			logger.error("[countAlarmDevByRule()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#countTriggerTop(java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<List<Map<String, Object>>> countTriggerTop(String params) {
		logger.info(String.format("[进入countTriggerTop->params:%s]", params));
		try {
			if (StringUtils.isBlank(params)) {
				logger.error("[countTriggerTop()->error:param不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			JSONObject json = JSONObject.parseObject(params);
			String accessSecret = json.getString("accessSecret") + "";
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countTriggerTop()->accessSecret不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("厂家密钥不能为空！");
			}
			List<Map<String, Object>> fdList = infoSummaryQueryRepository.getBandingDeviceId(accessSecret);
			List<String> idList = new ArrayList<>();
			for (Map<String, Object> maps : fdList) {
				String deviceid = maps.get("deviceId") + "";
				idList.add(deviceid);
			}
			int i = Integer.valueOf(json.getString("days"));
			//当前时间
			String timeS = DateUtils.formatDate(new Date());
			String timeStamp = DateUtils.dateToStamp(timeS);
			long timeInt = Long.parseLong(timeStamp);
			//当前与00:00:00时间差
			String thisTime=DateUtils.getDateString("start");
			String thisTime1 = DateUtils.dateToStamp(thisTime);
			long thistimeInt = Long.parseLong(thisTime1);
			long differenceTime=timeInt-thistimeInt;

			long timeStamp1 = timeInt -(86400000L * i)-differenceTime;

			BasicDBObject fieldsObject = new BasicDBObject();
			fieldsObject.put("deviceId", 1);
			fieldsObject.put("_id", 0);
			// DBObject dbObjectFv = new BasicDBObject("$gt", 1);
			DBObject dbObject = new BasicDBObject("$gte", timeStamp1 + "").append("$lte", timeStamp);
			DBObject ageCompare = new BasicDBObject("timestamp", dbObject);
			//彭州使用设备没有trigger属性只由xgiv确定
//			DBObject db1 = new BasicDBObject("attributeInfo", new BasicDBObject("$elemMatch",new BasicDBObject("attributeName","rt").append("attributeReportedValue","trigger")));
			
			DBObject db2 = new BasicDBObject("attributeInfo", new BasicDBObject("$elemMatch",new BasicDBObject("attributeName", "xgiv").append("attributeReportedValue", 1)));
			BasicDBList condList = new BasicDBList();
			
			//彭州使用去掉list中的db1条件
//			condList.add(db1);
			
			condList.add(db2);
			ageCompare.put("$and", condList);
			Query query = new BasicQuery(ageCompare, fieldsObject);
			List<Map> deviceHistoryStateMap = mongoTemplate.find(query, Map.class, "deviceHistoryState");
//			int pp=deviceHistoryStateMap.size();
			List<String> deviceIdList = new ArrayList<String>();
			for (Map<String, Object> map : deviceHistoryStateMap) {
				String deviceId = map.get("deviceId") + "";
				deviceIdList.add(deviceId);
			}
			Map<String, String> tempMap = new HashMap<>();
			for (int a = 0; a < deviceIdList.size(); a++) {
				int count = 1;
				if (!tempMap.containsKey(deviceIdList.get(a))) {
					for (int b = a + 1; b < deviceIdList.size(); b++) {
						if (deviceIdList.get(a).equals(deviceIdList.get(b)) ) {
							count++;
						}
					}
					
						tempMap.put(deviceIdList.get(a), count + "");
				}
			}
			//临时使用,数量大于15次，不显示出来
			Map<String, String> openMap = Maps.newHashMap();
			Set<Entry<String, String>> entrySet = tempMap.entrySet();
			for (Entry<String, String> entry : entrySet) {
				String value = entry.getValue();
				int parseInt = Integer.parseInt(value);
				if (parseInt <= 15) {
					openMap.put(entry.getKey(), entry.getValue());
				}
			}
			

			List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(openMap.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
				//
				public int compare(Entry<String, String> o1, Entry<String, String> o2) {
					return Integer.valueOf(o2.getValue()).compareTo(Integer.valueOf(o1.getValue()));
				}

			});
			List<Map<String, Object>> endList = new ArrayList<>();
			for (Entry<String, String> map : list) {
				Map<String, Object> endMap = new LinkedHashMap<>();
				endMap.put("deviceId", map.getKey());
				endMap.put("count", map.getValue());
				String deviceIds=endMap.get("deviceId")+"";
				if (idList.contains(deviceIds)) {
					
					for (Map<String, Object> fdMap : fdList) {
						if (fdMap.get("deviceId") .equals(endMap.get("deviceId")) ) {
							endMap.put("facilitiesCode", fdMap.get("facilitiesCode")+"");
							endMap.put("address", fdMap.get("address")+"");
						}

					}
					endList.add(endMap);
				}
			}
			logger.info("countTriggerTop()->成功,list的大小："+endList.size());
			if(endList.size() > 50){
				List nList=endList.subList(0, 50);
				logger.info("countTriggerTop()->查询成功");
				return RpcResponseBuilder.buildSuccessRpcResp("获取top50开井设施数量成功", nList);
			}else{
				logger.info("countTriggerTop()->查询成功");
				return RpcResponseBuilder.buildSuccessRpcResp("获取top50开井设施数量成功", endList);
			}

		} catch (Exception e) {
			logger.error("[countTriggerTop()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);

		}
	}



	/**
	 * @see com.run.locman.api.query.service.InfoSummaryQueryService#detailsByDeviceId(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Map<String, Object>> detailsByDeviceId(JSONObject json) {
		logger.info(String.format("[进入detailsByDeviceId->jsonObject:%s]", json.toJSONString()));
		try {
			if (StringUtils.isBlank(json.getString("deviceId"))) {
				logger.error("[detailsByDeviceId()->error:设备Id为空]");
			}
			if (StringUtils.isBlank(json.getString("days"))) {
				logger.error("[detailsByDeviceId()->error:时间段为空]");
			}
			String deviceId=json.getString("deviceId");
			long i = Integer.valueOf(json.getString("days"));
			String thisTime=DateUtils.getDateString("start");
			String thisTime1 = DateUtils.dateToStamp(thisTime);
			long thistimeInt = Long.parseLong(thisTime1);
			String timeS = DateUtils.formatDate(new Date());
			String timeStamp = DateUtils.dateToStamp(timeS);
			//当前时间时间戳timeInt
			long timeInt = Long.parseLong(timeStamp);
			long differenceTime=timeInt-thistimeInt;
			long timeStamp1 = timeInt -(86400000L * i)-differenceTime;

			BasicDBObject fieldsObject = new BasicDBObject();
//			fieldsObject.put("deviceId", 1);
			fieldsObject.put("_id", 0);
			fieldsObject.put("timestamp", 1);
			// DBObject dbObjectFv = new BasicDBObject("$gt", 1);
			DBObject dbObject = new BasicDBObject("$gte", timeStamp1 + "").append("$lte", timeStamp);
			DBObject ageCompare = new BasicDBObject("timestamp", dbObject);
			ageCompare.put("deviceId", deviceId);
			DBObject db1 = new BasicDBObject("attributeInfo", new BasicDBObject("$elemMatch",new BasicDBObject("attributeName","rt").append("attributeReportedValue","trigger")));
			DBObject db2 = new BasicDBObject("attributeInfo", new BasicDBObject("$elemMatch",new BasicDBObject("attributeName", "xgiv").append("attributeReportedValue", 1)));
			BasicDBList condList = new BasicDBList();
			condList.add(db1);
			condList.add(db2);
			ageCompare.put("$and", condList);
			Query query = new BasicQuery(ageCompare, fieldsObject);
			List<Map> deviceHistoryStateMap = mongoTemplate.find(query, Map.class, "deviceHistoryState");
			Map<String,Object> endMap=infoSummaryQueryRepository.getFacAndDevInfoById(deviceId);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        Map<String, Object> dateMap = new TreeMap<String, Object>();
			for(long j=0;j<=i;j++) {
				long jj=j-1;
				
				long timeStampEnd = timeInt -(86400000L * jj)-differenceTime;
				long timeStampEnd2 = timeInt -(86400000L * j)-differenceTime;
				List<String> timestampList1=new ArrayList<>();
				for(Map<String ,Object> timestampMap:deviceHistoryStateMap) {
					String timestamp1=timestampMap.get("timestamp")+"";
					Long timestamp2 =Long.parseLong(timestamp1);
					if(( timestamp2 >= timeStampEnd2) && (timestamp2 < timeStampEnd)) {
						String timestamp3=DateUtils.stampToDate(timestamp1);
						timestampList1.add(timestamp3);
					}
				}
				timestampList1.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
				long lt = new Long(timeStampEnd2);
				Date date = new Date(lt);
				if(!timestampList1.isEmpty()&&timestampList1.size() >= 0) {
					dateMap.put(simpleDateFormat.format(date),timestampList1);
				}
			}
			endMap.put("dateMap", dateMap);
			List<String> timestampList=new ArrayList<>();
			for(Map<String ,Object> timestampMap:deviceHistoryStateMap) {
				String timestamp=timestampMap.get("timestamp")+"";
				timestamp=DateUtils.stampToDate(timestamp);
				timestampList.add(timestamp);
			}
			timestampList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			
			endMap.put("date", timestampList);
			
			logger.info("detailsByDeviceId()->查询开井详情成功.");
			return RpcResponseBuilder.buildSuccessRpcResp("成功查询详情",endMap);
		}catch(Exception e) {
			logger.error("[detailsByDeviceId()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
