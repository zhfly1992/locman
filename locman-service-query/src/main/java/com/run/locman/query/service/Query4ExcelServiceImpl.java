/*
 * File name: Query4ExcelServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2019年8月30日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.impl.util.json.CDL;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.AttributeInfo;
import com.run.locman.api.query.repository.Query4ExcelRepository;
import com.run.locman.api.query.service.Query4ExcelService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 此类中的方法不宜在正式环境调用
 * @author: guofeilong
 * @version: 1.0, 2019年8月30日
 */

public class Query4ExcelServiceImpl implements Query4ExcelService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private Query4ExcelRepository	query4ExcelRepository;

	@Autowired
	private MongoTemplate			mongoTemplate;



	@Override
	public RpcResponse<List<JSONObject>> deviceStateInfo4Excel(JSONObject jsonObject) {
		try {
			// TODO Auto-generated method stub
			logger.info("进入deviceStateInfo4Excel,rpc方法参数:" + "jsonObject");
			String accessSecret = jsonObject.getString("accessSecret");

			String deviceType = jsonObject.getString("deviceTypeId");

			List<JSONObject> resultList = Lists.newArrayList();

			String startTime = jsonObject.getString("startTime");
			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long time1 = System.currentTimeMillis();
			List<JSONObject> devicesRsFac = query4ExcelRepository.getAllDevicesRsFacilitiesInfo(accessSecret,
					deviceType, null);
			Map<String, JSONObject> deviceIdRsFacInfp = Maps.newHashMap();
			List<String> deviceIds = Lists.newArrayList();

			JSONObject AttributeName = new JSONObject();
			AttributeName.put("address", "空");
			AttributeName.put("longitude", "空");
			AttributeName.put("latitude", "空");
			AttributeName.put("facilitiesCode", "空");
			AttributeName.put("idInMysql", "空");

			AttributeName.put("最新上报时间", "空");
			AttributeName.put("deviceId", "空");
			AttributeName.put("上报次数", "空");

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");
				deviceIdRsFacInfp.put(idInMysql, deviceRsFacJson);
				deviceIds.add(idInMysql);
			}
			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));

			Criteria orOperator = new Criteria().andOperator(Criteria.where("timestamp").gt(startTimeStamp),
					Criteria.where("things.gatewayId").exists(true), Criteria.where("deviceId").in(deviceIds));
			Query query = new Query(orOperator);
			List<JSONObject> deviceHistoryState = mongoTemplate.find(query, JSONObject.class, "deviceHistoryState");

			JSONObject countNumber = new JSONObject();

			for (JSONObject json : deviceHistoryState) {

				String deviceId = json.getString("deviceId");
				if (countNumber.containsKey(deviceId)) {
					countNumber.replace(deviceId, countNumber.getInteger(deviceId) + 1);
				} else {
					countNumber.put(deviceId, 1);
				}
			}

			long time3 = System.currentTimeMillis();
			logger.info("查询设备历史上报次数,耗时:" + (time3 - time2));

			Criteria lastDataCriteria = new Criteria().andOperator(Criteria.where("things.gatewayId").exists(true),
					Criteria.where("deviceId").in(deviceIds));
			Query lastData = new Query(lastDataCriteria);
			List<JSONObject> deviceStateList = mongoTemplate.find(lastData, JSONObject.class, "deviceState");

			for (JSONObject deviceStateJson : deviceStateList) {
				JSONArray deviceStateArray = deviceStateJson.getJSONArray("attributeInfo");
				List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);
				// 属性点:属性值
				for (AttributeInfo attributeInfo : attributeInfoList) {
					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())) {
						AttributeName.put(attributeInfo.getAttributeName(), "空");
					}
				}
			}

			for (JSONObject deviceStateJson : deviceStateList) {
				JSONArray deviceStateArray = deviceStateJson.getJSONArray("attributeInfo");
				String deviceId = deviceStateJson.getString("deviceId");
				String timestamp = deviceStateJson.getString("timestamp");
				if (StringUtils.isBlank(timestamp) || !StringUtils.isNumeric(timestamp)) {
					logger.error("deviceStateInfo4Excel()-->时间戳值为空/非数值:" + timestamp);
				}
				long parseLong = Long.parseLong(timestamp);

				List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);
				JSONObject resultJson = new JSONObject();
				resultJson.putAll(AttributeName);
				resultJson.put("deviceId", deviceId);
				String reportTime = UtilTool.timeStampToDate(parseLong);
				resultJson.put("最新上报时间", reportTime);

				JSONObject facInJson = deviceIdRsFacInfp.get(deviceId);
				if (null != facInJson) {
					Set<Entry<String, Object>> entrySet = facInJson.entrySet();
					for (Entry<String, Object> entry : entrySet) {
						resultJson.put(entry.getKey(), entry.getValue());
					}
				}
				resultJson.put("上报次数", countNumber.getOrDefault(deviceId, 0));

				// 属性点:属性值
				for (AttributeInfo attributeInfo : attributeInfoList) {
					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& StringUtils.isNotBlank(attributeInfo.getAttributeReportedValue())) {
						resultJson.put(attributeInfo.getAttributeName(), attributeInfo.getAttributeReportedValue());
					}
				}

				resultList.add(resultJson);
			}

			long time4 = System.currentTimeMillis();
			logger.info("查询最新状态,耗时:" + (time4 - time3));

			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", resultList);
		} catch (Exception e) {
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@Override
	public RpcResponse<List<JSONObject>> deviceStateInfoCount(JSONObject jsonObject) {
		// return deviceSrareInfoCount4(jsonObject);
		// return deviceStateInfoCount3(jsonObject);
		//return deviceStateInfo1111(jsonObject);
		//return deviceStateInfoCountXYZavtv(jsonObject);
		return deviceStateInfo1206(jsonObject);
		// return deviceSrareInfoCount1(jsonObject);

	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<List<JSONObject>> deviceSrareInfoCount1(JSONObject jsonObject) {
		try {
			logger.info("进入deviceStateInfo4Excel,rpc方法参数:" + "jsonObject");
			// int size = jsonObject.getIntValue("size");

			List<JSONObject> resultList = Lists.newArrayList();

			String startTime = jsonObject.getString("startTime");

			String points = jsonObject.getString("points");
			String[] split = points.split(",");
			JSONObject allAttributeAlias = new JSONObject();
			for (String point : split) {
				allAttributeAlias.put(point, "空");
			}
			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long time1 = System.currentTimeMillis();

			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));

			Criteria orOperator = new Criteria().andOperator(Criteria.where("timestamp").gt(startTimeStamp),
					Criteria.where("things.gatewayId").exists(true));
			Query query = new Query(orOperator);
			List<JSONObject> deviceHistoryState = mongoTemplate.find(query, JSONObject.class, "deviceHistoryState");

			JSONObject countNumber = new JSONObject();

			for (JSONObject json : deviceHistoryState) {

				String deviceId = json.getString("deviceId");
				if (countNumber.containsKey(deviceId)) {
					countNumber.replace(deviceId, countNumber.getInteger(deviceId) + 1);
				} else {
					countNumber.put(deviceId, 1);
				}

			}

			for (JSONObject json : deviceHistoryState) {
				JSONArray deviceStateArray = json.getJSONArray("attributeInfo");
				String deviceId = json.getString("deviceId");
				String timestamp = json.getString("timestamp");
				if (StringUtils.isBlank(timestamp) || !StringUtils.isNumeric(timestamp)) {
					logger.error("deviceStateInfo4Excel()-->时间戳值为空/非数值:" + timestamp);
				}
				long parseLong = Long.parseLong(timestamp);

				List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);
				JSONObject resultJson = new JSONObject();
				resultJson.putAll(allAttributeAlias);
				resultJson.put("设备id", deviceId);
				String reportTime = UtilTool.timeStampToDate(parseLong);
				resultJson.put("上报时间", reportTime);

				resultJson.put("上报次数", countNumber.getOrDefault(deviceId, 0));

				// 属性点:属性值
				for (AttributeInfo attributeInfo : attributeInfoList) {
					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& allAttributeAlias.containsKey(attributeInfo.getAttributeName())) {
						resultJson.put(attributeInfo.getAttributeName(), attributeInfo.getAttributeReportedValue());
					}
				}
				resultList.add(resultJson);

			}
			long time3 = System.currentTimeMillis();
			logger.info("查询设备历史上报次数,耗时:" + (time3 - time2));

			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", resultList);
		} catch (Exception e) {
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<JSONObject> changeDeviceStateFv(JSONObject jsonObject) {
		try {
			if (!"19999".equals(jsonObject.getString("key"))) {
				return RpcResponseBuilder.buildErrorRpcResp("校验失败");
			}
			logger.info("进入deviceStateFv,rpc方法参数:" + "jsonObject");

			long time1 = System.currentTimeMillis();

			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));

			long time3 = System.currentTimeMillis();
			logger.info("查询设备历史上报次数,耗时:" + (time3 - time2));

			Criteria lastDataCriteria = new Criteria().andOperator(Criteria.where("things.gatewayId").exists(true));
			Query lastData = new Query(lastDataCriteria);
			List<JSONObject> deviceStateList = mongoTemplate.find(lastData, JSONObject.class, "deviceState");

			StringBuffer success = new StringBuffer();
			StringBuffer fault = new StringBuffer();
			logger.error("查询MongoDB数据完毕");

			for (JSONObject deviceStateJson : deviceStateList) {
				JSONArray deviceStateArray = deviceStateJson.getJSONArray("attributeInfo");
				List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);

				// 属性点:属性值
				for (AttributeInfo attributeInfo : attributeInfoList) {
					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& "dc".equals(attributeInfo.getAttributeName())) {

						String deviceId = deviceStateJson.getString("deviceId");
						String dc = attributeInfo.getAttributeReportedValue();
						if (StringUtils.isBlank(dc)) {
							dc = "";
						}

						int result = query4ExcelRepository.changeDeviceStateFv(dc, deviceId);

						if (result > 0) {
							success.append(deviceId + ",");
						} else {
							fault.append(deviceId + ",");
						}
					}
				}

			}

			JSONObject res = new JSONObject();
			res.put("success", success);
			res.put("fault", fault);
			long time4 = System.currentTimeMillis();
			logger.info("查询最新状态,耗时:" + (time4 - time3));

			return RpcResponseBuilder.buildSuccessRpcResp("操作成功", res);
		} catch (Exception e) {
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	public String Json2Csv(String json) {

		org.activiti.engine.impl.util.json.JSONArray jsonArray = CDL.toJSONArray(json);
		String csv = CDL.toString(jsonArray);
		return csv;

	}



	/**
	 * @see com.run.locman.api.query.service.Query4ExcelService#saveFilePath(java.lang.String)
	 */
	@Override
	public void saveFilePath(String fileName) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(fileName)) {
			logger.error("文件路径为空");
		}
		logger.info("文件路径:" + fileName);

		int result = query4ExcelRepository.saveFilePath(fileName);

		if (result > 0) {
			logger.info("文件路径存储成功");
		} else {
			logger.error("文件路径存储失败");
		}

	}



	public RpcResponse<List<JSONObject>> deviceStateInfoCount2(JSONObject jsonObject) {
		try {
			logger.info("进入deviceStateInfo4Excel,rpc方法参数:" + "jsonObject");

			List<JSONObject> resultList = Lists.newLinkedList();

			String startTime = jsonObject.getString("startTime");

			String points = jsonObject.getString("points");
			String[] split = points.split(",");
			JSONObject allAttributeAlias = new JSONObject();
			for (String point : split) {
				allAttributeAlias.put(point, "空");
			}

			allAttributeAlias.put("address", "空");
			allAttributeAlias.put("longitude", "空");
			allAttributeAlias.put("latitude", "空");
			allAttributeAlias.put("facilitiesCode", "空");
			allAttributeAlias.put("idInMysql", "空");

			allAttributeAlias.put("上报时间", "空");
			allAttributeAlias.put("deviceId", "空");
			allAttributeAlias.put("上报次数", "空");
			allAttributeAlias.put("timing次数", "空");
			allAttributeAlias.put("trigger次数", "空");

			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long time1 = System.currentTimeMillis();
			String accessSecret = jsonObject.getString("accessSecret");

			String deviceType = jsonObject.getString("deviceTypeId");
			List<JSONObject> devicesRsFac = query4ExcelRepository.getAllDevicesRsFacilitiesInfo(accessSecret,
					deviceType, null);
			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));
			Map<String, JSONObject> deviceIdRsFacInfp = Maps.newHashMap();
			List<String> deviceIds = Lists.newArrayList();

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");
				deviceIdRsFacInfp.put(idInMysql, deviceRsFacJson);
				deviceIds.add(idInMysql);
			}

			Criteria orOperator = new Criteria().andOperator(Criteria.where("timestamp").gt(startTimeStamp),
					Criteria.where("things.gatewayId").exists(true));
			Query query = new Query(orOperator);
			DBObject query1 = new BasicDBObject();
			query1.put("timestamp", new BasicDBObject("$gte", startTimeStamp));
			query1.containsKey("things.gatewayId");

			DBCursor dbCursor = mongoTemplate.getCollection("deviceHistoryState").find(query1);
			List<JSONObject> deviceHistoryState = Lists.newArrayList();
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				JSONObject te = new JSONObject();
				te.put("attributeInfo", object.get("attributeInfo"));
				te.put("timestamp", object.get("timestamp"));
				te.put("deviceId", object.get("deviceId"));
				deviceHistoryState.add(te);
			}

			// List<JSONObject> deviceHistoryState = mongoTemplate.find(query,
			// JSONObject.class, "deviceHistoryState");
			logger.error("查询MongoDB数据完毕");
			JSONObject countNumber = new JSONObject();
			JSONObject countTrigger = new JSONObject();
			JSONObject countTiming = new JSONObject();

			resultList.add(allAttributeAlias);
			for (JSONObject json : deviceHistoryState) {
				String deviceId = json.getString("deviceId");
				if (countNumber.containsKey(deviceId)) {
					countNumber.replace(deviceId, countNumber.getInteger(deviceId) + 1);
				} else {
					countNumber.put(deviceId, 1);
				}
				JSONArray deviceStateArray = json.getJSONArray("attributeInfo");
				List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);

				String timestamp = json.getString("timestamp");
				if (StringUtils.isBlank(timestamp) || !StringUtils.isNumeric(timestamp)) {
					logger.error("deviceStateInfo4Excel()-->时间戳值为空/非数值:" + timestamp);
				}
				long parseLong = Long.parseLong(timestamp);

				JSONObject resultJson = new JSONObject();
				resultJson.putAll(allAttributeAlias);
				JSONObject facInfo = deviceIdRsFacInfp.get(deviceId);
				if (null != facInfo) {
					resultJson.put("address", facInfo.getOrDefault("address", "空"));
					resultJson.put("longitude", facInfo.getOrDefault("longitude", "空"));
					resultJson.put("latitude", facInfo.getOrDefault("latitude", "空"));
					resultJson.put("facilitiesCode", facInfo.getOrDefault("facilitiesCode", "空"));
					resultJson.put("idInMysql", facInfo.getOrDefault("idInMysql", "空"));

				}
				resultJson.put("设备id", deviceId);
				String reportTime = UtilTool.timeStampToDate(parseLong);
				resultJson.put("上报时间", reportTime);

				// 属性点:属性值
				for (AttributeInfo attributeInfo : attributeInfoList) {
					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& allAttributeAlias.containsKey(attributeInfo.getAttributeName())) {
						resultJson.put(attributeInfo.getAttributeName(), attributeInfo.getAttributeReportedValue());
					}

					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& "rt".equals(attributeInfo.getAttributeName())) {
						if ("trigger".equals(attributeInfo.getAttributeReportedValue())) {
							if (countTrigger.containsKey(deviceId)) {
								countTrigger.replace(deviceId, countTrigger.getInteger(deviceId) + 1);
							} else {
								countTrigger.put(deviceId, 1);
							}
						} else if ("timing".equals(attributeInfo.getAttributeReportedValue())) {
							if (countTiming.containsKey(deviceId)) {
								countTiming.replace(deviceId, countTiming.getInteger(deviceId) + 1);
							} else {
								countTiming.put(deviceId, 1);
							}
						}

					}

				}
				resultList.add(resultJson);
				logger.info("-1-");

			}

			for (JSONObject resultJson : resultList) {
				String deviceId = resultJson.getString("设备id");
				resultJson.put("上报次数", countNumber.getOrDefault(deviceId, 0));
				resultJson.put("timing次数", countTiming.getOrDefault(deviceId, 0));
				resultJson.put("trigger次数", countTrigger.getOrDefault(deviceId, 0));
				logger.info("-2-");
			}

			long time3 = System.currentTimeMillis();
			logger.info("查询设备历史上报次数,耗时:" + (time3 - time2));

			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", resultList);
		} catch (Exception e) {
			logger.error(e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@SuppressWarnings("deprecation")
	public RpcResponse<List<JSONObject>> deviceStateInfoCount3(JSONObject jsonObject) {
		try {
			logger.info("进入deviceStateInfo4Excel,rpc方法参数:" + "jsonObject");

			List<JSONObject> resultList = Lists.newLinkedList();

			String startTime = jsonObject.getString("startTime");

			JSONObject allAttributeAlias = new JSONObject();
			allAttributeAlias.put("idInMysql", "空");
			allAttributeAlias.put("最新上报时间", "空");
			allAttributeAlias.put("蓝牙名称", "空");
			allAttributeAlias.put("facilitiesCode", "空");
			allAttributeAlias.put("latitude", "空");
			allAttributeAlias.put("longitude", "空");
			allAttributeAlias.put("address", "空");
			allAttributeAlias.put("organizationId", "空");

			allAttributeAlias.put("上报次数", "空");
			allAttributeAlias.put("timing次数", "空");
			allAttributeAlias.put("trigger次数", "空");

			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long time1 = System.currentTimeMillis();
			String accessSecret = jsonObject.getString("accessSecret");

			String deviceType = jsonObject.getString("deviceTypeId");
			List<JSONObject> devicesRsFac = query4ExcelRepository.getAllDevicesRsFacilitiesInfo(accessSecret,
					deviceType, null);
			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));
			Map<String, JSONObject> deviceIdRsFacInfp = Maps.newHashMap();
			List<String> deviceIds = Lists.newArrayList();

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");
				deviceIdRsFacInfp.put(idInMysql, deviceRsFacJson);
				deviceIds.add(idInMysql);
			}

			DBObject query1 = new BasicDBObject();
			query1.put("timestamp", new BasicDBObject("$gte", startTimeStamp));
			query1.containsKey("things.gatewayId");

			DBCursor dbCursor = mongoTemplate.getCollection("deviceHistoryState").find(query1);
			List<JSONObject> deviceHistoryState = Lists.newArrayList();
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				JSONObject te = new JSONObject();
				te.put("attributeInfo", object.get("attributeInfo"));
				te.put("timestamp", object.get("timestamp"));
				te.put("deviceId", object.get("deviceId"));
				deviceHistoryState.add(te);
			}

			logger.error("查询MongoDB数据完毕");
			JSONObject countNumber = new JSONObject();
			JSONObject countTrigger = new JSONObject();
			JSONObject countTiming = new JSONObject();

			resultList.add(0, allAttributeAlias);
			for (JSONObject json : deviceHistoryState) {
				String deviceId = json.getString("deviceId");
				if (countNumber.containsKey(deviceId)) {
					countNumber.replace(deviceId, countNumber.getInteger(deviceId) + 1);
				} else {
					countNumber.put(deviceId, 1);
				}
				JSONArray deviceStateArray = json.getJSONArray("attributeInfo");
				List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);

				// 属性点:属性值
				for (AttributeInfo attributeInfo : attributeInfoList) {

					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& "rt".equals(attributeInfo.getAttributeName())) {
						if ("trigger".equals(attributeInfo.getAttributeReportedValue())) {
							if (countTrigger.containsKey(deviceId)) {
								countTrigger.replace(deviceId, countTrigger.getInteger(deviceId) + 1);
							} else {
								countTrigger.put(deviceId, 1);
							}
						} else if ("timing".equals(attributeInfo.getAttributeReportedValue())) {
							if (countTiming.containsKey(deviceId)) {
								countTiming.replace(deviceId, countTiming.getInteger(deviceId) + 1);
							} else {
								countTiming.put(deviceId, 1);
							}
						}

					}

				}
				logger.info("-1-");

			}

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");

				deviceRsFacJson.put("上报次数", countNumber.getOrDefault(idInMysql, 0));
				deviceRsFacJson.put("timing次数", countTiming.getOrDefault(idInMysql, 0));
				deviceRsFacJson.put("trigger次数", countTrigger.getOrDefault(idInMysql, 0));
				resultList.add(deviceRsFacJson);
				logger.info("-2-");
			}

			long time3 = System.currentTimeMillis();
			logger.info("查询设备历史上报次数,耗时:" + (time3 - time2));

			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", resultList);
		} catch (Exception e) {
			logger.error(e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	private RpcResponse<List<JSONObject>> deviceSrareInfoCount4(JSONObject jsonObject) {
		try {
			logger.info("进入deviceStateInfo4Excel,rpc方法参数:" + "jsonObject");
			// int size = jsonObject.getIntValue("size");

			List<JSONObject> resultList = Lists.newArrayList();

			String startTime = jsonObject.getString("startTime");

			String points = jsonObject.getString("points");
			String[] split = points.split(",");
			JSONObject allAttributeAlias = new JSONObject();

			allAttributeAlias.put("address", "空");
			allAttributeAlias.put("longitude", "空");
			allAttributeAlias.put("latitude", "空");
			allAttributeAlias.put("facilitiesCode", "空");
			allAttributeAlias.put("idInMysql", "空");
			// allAttributeAlias.put("蓝牙名称", "空");
			// allAttributeAlias.put("最新上报时间", "空");
			allAttributeAlias.put("组织id", "空");

			allAttributeAlias.put("设备id", "空");
			allAttributeAlias.put("上报时间", "空");
			allAttributeAlias.put("上报次数", "空");

			for (String point : split) {
				allAttributeAlias.put(point, "空");
			}
			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long time1 = System.currentTimeMillis();

			String accessSecret = jsonObject.getString("accessSecret");

			String deviceType = jsonObject.getString("deviceTypeId");
			List<JSONObject> devicesRsFac = query4ExcelRepository.getAllDevicesRsFacilitiesInfo(accessSecret,
					deviceType, null);

			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));

			Map<String, JSONObject> deviceIdRsFacInfp = Maps.newHashMap();
			List<String> deviceIds = Lists.newArrayList();

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");
				deviceIdRsFacInfp.put(idInMysql, deviceRsFacJson);
				deviceIds.add(idInMysql);
			}

			/*
			 * Criteria orOperator = new
			 * Criteria().andOperator(Criteria.where("timestamp").gt(
			 * startTimeStamp),
			 * Criteria.where("things.gatewayId").exists(true)); Query query =
			 * new Query(orOperator); List<JSONObject> deviceHistoryState =
			 * mongoTemplate.find(query, JSONObject.class,
			 * "deviceHistoryState");
			 */

			DBObject query1 = new BasicDBObject();
			query1.put("timestamp", new BasicDBObject("$gte", startTimeStamp));
			query1.containsKey("things.gatewayId");

			DBCursor dbCursor = mongoTemplate.getCollection("deviceHistoryState").find(query1);
			List<JSONObject> deviceHistoryState = Lists.newArrayList();
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				JSONObject te = new JSONObject();
				te.put("attributeInfo", object.get("attributeInfo"));
				te.put("timestamp", object.get("timestamp"));
				te.put("deviceId", object.get("deviceId"));
				deviceHistoryState.add(te);
			}

			JSONObject countNumber = new JSONObject();

			resultList.add(0, allAttributeAlias);

			for (JSONObject json : deviceHistoryState) {

				String deviceId = json.getString("deviceId");
				if (countNumber.containsKey(deviceId)) {
					countNumber.replace(deviceId, countNumber.getInteger(deviceId) + 1);
				} else {
					countNumber.put(deviceId, 1);
				}

			}

			for (JSONObject json : deviceHistoryState) {
				JSONArray deviceStateArray = json.getJSONArray("attributeInfo");
				String deviceId = json.getString("deviceId");
				String timestamp = json.getString("timestamp");
				if (StringUtils.isBlank(timestamp) || !StringUtils.isNumeric(timestamp)) {
					logger.error("deviceStateInfo4Excel()-->时间戳值为空/非数值:" + timestamp);
				}
				long parseLong = Long.parseLong(timestamp);

				List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);
				JSONObject resultJson = new JSONObject();
				resultJson.putAll(allAttributeAlias);

				JSONObject facInfo = deviceIdRsFacInfp.get(deviceId);
				if (null != facInfo) {
					resultJson.put("address", facInfo.getOrDefault("address", "空"));
					resultJson.put("longitude", facInfo.getOrDefault("longitude", "空"));
					resultJson.put("latitude", facInfo.getOrDefault("latitude", "空"));
					resultJson.put("facilitiesCode", facInfo.getOrDefault("facilitiesCode", "空"));
					resultJson.put("idInMysql", facInfo.getOrDefault("idInMysql", "空"));
					resultJson.put("组织id", facInfo.getOrDefault("organizationId", "空"));

				}

				resultJson.put("设备id", deviceId);
				String reportTime = UtilTool.timeStampToDate(parseLong);
				resultJson.put("上报时间", reportTime);

				resultJson.put("上报次数", countNumber.getOrDefault(deviceId, 0));

				// 属性点:属性值
				for (AttributeInfo attributeInfo : attributeInfoList) {
					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& allAttributeAlias.containsKey(attributeInfo.getAttributeName())) {
						resultJson.put(attributeInfo.getAttributeName(), attributeInfo.getAttributeReportedValue());
					}
				}
				resultList.add(resultJson);

			}
			long time3 = System.currentTimeMillis();
			logger.info("查询设备历史上报次数,耗时:" + (time3 - time2));

			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", resultList);
		} catch (Exception e) {
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("deprecation")
	public RpcResponse<List<JSONObject>> deviceStateInfoCountRactv(JSONObject jsonObject) {
		try {
			logger.info("进入deviceStateInfo4Excel,rpc方法参数:" + "jsonObject");

			List<JSONObject> resultList = Lists.newLinkedList();

			String startTime = jsonObject.getString("startTime");

			JSONObject allAttributeAlias = new JSONObject();
			allAttributeAlias.put("idInMysql", "空");
			allAttributeAlias.put("最新上报时间", "空");
			allAttributeAlias.put("蓝牙名称", "空");
			allAttributeAlias.put("facilitiesCode", "空");
			allAttributeAlias.put("latitude", "空");
			allAttributeAlias.put("longitude", "空");
			allAttributeAlias.put("address", "空");
			allAttributeAlias.put("organizationId", "空");

			allAttributeAlias.put("上报次数", "空");
			allAttributeAlias.put("timing次数", "空");
			allAttributeAlias.put("trigger次数", "空");
			allAttributeAlias.put("R值", "空");

			String startTimeStamp = DateUtils.dateToStamp(startTime);
			long time1 = System.currentTimeMillis();
			String accessSecret = jsonObject.getString("accessSecret");

			String deviceType = jsonObject.getString("deviceTypeId");
			List<JSONObject> devicesRsFac = query4ExcelRepository.getAllDevicesRsFacilitiesInfo(accessSecret,
					deviceType, null);
			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));
			Map<String, JSONObject> deviceIdRsFacInfp = Maps.newHashMap();
			List<String> deviceIds = Lists.newArrayList();

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");
				deviceIdRsFacInfp.put(idInMysql, deviceRsFacJson);
				deviceIds.add(idInMysql);
			}

			DBObject query1 = new BasicDBObject();
			query1.put("timestamp", new BasicDBObject("$gte", startTimeStamp));
			query1.containsKey("things.gatewayId");

			DBCursor dbCursor = mongoTemplate.getCollection("deviceHistoryState").find(query1);
			List<JSONObject> deviceHistoryState = Lists.newArrayList();
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				JSONObject te = new JSONObject();
				te.put("attributeInfo", object.get("attributeInfo"));
				te.put("timestamp", object.get("timestamp"));
				te.put("deviceId", object.get("deviceId"));
				deviceHistoryState.add(te);
			}

			logger.error("查询MongoDB数据完毕");
			JSONObject countNumber = new JSONObject();
			JSONObject countTrigger = new JSONObject();
			JSONObject countTiming = new JSONObject();

			JSONObject appendRactv = new JSONObject();

			resultList.add(0, allAttributeAlias);
			for (JSONObject json : deviceHistoryState) {
				String deviceId = json.getString("deviceId");
				if (countNumber.containsKey(deviceId)) {
					countNumber.replace(deviceId, countNumber.getInteger(deviceId) + 1);
				} else {
					countNumber.put(deviceId, 1);
				}
				JSONArray deviceStateArray = json.getJSONArray("attributeInfo");
				List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);

				String rt = "";

				// 属性点:属性值
				for (AttributeInfo attributeInfo : attributeInfoList) {

					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& "rt".equals(attributeInfo.getAttributeName())) {
						if ("trigger".equals(attributeInfo.getAttributeReportedValue())) {
							if (countTrigger.containsKey(deviceId)) {
								countTrigger.replace(deviceId, countTrigger.getInteger(deviceId) + 1);
							} else {
								countTrigger.put(deviceId, 1);
							}
						} else if ("timing".equals(attributeInfo.getAttributeReportedValue())) {
							rt = "timing";
							if (countTiming.containsKey(deviceId)) {
								countTiming.replace(deviceId, countTiming.getInteger(deviceId) + 1);
							} else {
								countTiming.put(deviceId, 1);
							}
						}

					}

					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& "ractv".equals(attributeInfo.getAttributeName()) && "timing".equals(rt)) {
						String ractv = attributeInfo.getAttributeReportedValue();
						if (appendRactv.containsKey(deviceId)) {
							appendRactv.replace(deviceId, appendRactv.getString(deviceId) + "/" + ractv);
						} else {
							appendRactv.put(deviceId, ractv);
						}
					}

				}
				logger.info("-1-");

			}

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");

				deviceRsFacJson.put("上报次数", countNumber.getOrDefault(idInMysql, 0));
				deviceRsFacJson.put("timing次数", countTiming.getOrDefault(idInMysql, 0));
				deviceRsFacJson.put("trigger次数", countTrigger.getOrDefault(idInMysql, 0));
				deviceRsFacJson.put("R值", appendRactv.getOrDefault(idInMysql, ""));
				resultList.add(deviceRsFacJson);
				logger.info("-2-");
			}

			long time3 = System.currentTimeMillis();
			logger.info("查询设备历史上报次数,耗时:" + (time3 - time2));

			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", resultList);
		} catch (Exception e) {
			logger.error(e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	public RpcResponse<List<JSONObject>> deviceStateInfoCountXYZavtv(JSONObject jsonObject) {
		try {
			logger.info("进入deviceStateInfo4Excel,rpc方法参数:" + "jsonObject");

			List<JSONObject> resultList = Lists.newLinkedList();
			Set<String> faultIds = Sets.newHashSet();
			//List<String> faultIds = Lists.newArrayList();
			String points = jsonObject.getString("points");
			String[] split = points.split(",");
			// String startTime = jsonObject.getString("startTime");

			JSONObject allAttributeAlias = new JSONObject();

			allAttributeAlias.put("蓝牙名称", "空");
			allAttributeAlias.put("facilitiesCode", "空");
			allAttributeAlias.put("latitude", "空");
			allAttributeAlias.put("longitude", "空");
			allAttributeAlias.put("address", "空");
			allAttributeAlias.put("组织id", "空");

			allAttributeAlias.put("上报时间", "空");
			allAttributeAlias.put("上报次数", "空");
			allAttributeAlias.put("timing次数", "空");
			allAttributeAlias.put("trigger次数", "空");

			for (String point : split) {
				allAttributeAlias.put(point, "空");
			}
			// String startTimeStamp = DateUtils.dateToStamp(startTime);
			long time1 = System.currentTimeMillis();
			String accessSecret = jsonObject.getString("accessSecret");

			int dayTotal = jsonObject.getInteger("dayTotal");
			int dayFrom = jsonObject.getInteger("dayFrom");
			String deviceType = jsonObject.getString("deviceTypeId");
			List<JSONObject> devicesRsFac = query4ExcelRepository.getAllDevicesRsFacilitiesInfo(accessSecret,
					deviceType,null);
			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));
			Map<String, JSONObject> deviceIdRsFacInfp = Maps.newHashMap();
			List<String> deviceIds = Lists.newArrayList();

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");
				deviceIdRsFacInfp.put(idInMysql, deviceRsFacJson);
				deviceIds.add(idInMysql);
			}

			for (int i = dayFrom; i < dayTotal + 1; i++) {

				JSONObject countNumber = new JSONObject();
				JSONObject countTrigger = new JSONObject();
				JSONObject countTiming = new JSONObject();

				// 获取到一天的所有上报数据
				List<JSONObject> deviceHistoryState = getCountTimingAndTrigger(i);

				for (String deviceId : deviceIds) {

					for (JSONObject json : deviceHistoryState) {
						String deviceIdJson = json.getString("deviceId");

						int xgiv = 100;
						String rt = "";
						int onlyOnceTimming = 0;
						String xavtv = "";

						JSONObject tempJson = new JSONObject();
						JSONObject resultJson = new JSONObject();

						// 匹配单个设备每天的所有数据
						if (deviceId.equals(deviceIdJson)) {

							String timestamp = json.getString("timestamp");
							if (StringUtils.isBlank(timestamp) || !StringUtils.isNumeric(timestamp)) {
								logger.error("deviceStateInfo4Excel()-->时间戳值为空/非数值:" + timestamp);
							}
							long parseLong = Long.parseLong(timestamp);

							if (countNumber.containsKey(deviceIdJson)) {
								countNumber.replace(deviceIdJson, countNumber.getInteger(deviceIdJson) + 1);
							} else {
								countNumber.put(deviceIdJson, 1);
							}
							JSONArray deviceStateArray = json.getJSONArray("attributeInfo");
							List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);

							tempJson.putAll(allAttributeAlias);
							JSONObject facInfo = deviceIdRsFacInfp.get(deviceId);
							if (null != facInfo) {
								tempJson.put("蓝牙名称", facInfo.getOrDefault("蓝牙名称", "空"));
								tempJson.put("address", facInfo.getOrDefault("address", "空"));
								tempJson.put("longitude", facInfo.getOrDefault("longitude", "空"));
								tempJson.put("latitude", facInfo.getOrDefault("latitude", "空"));
								tempJson.put("facilitiesCode", facInfo.getOrDefault("facilitiesCode", "空"));
								tempJson.put("组织id", facInfo.getOrDefault("organizationId", "空"));

							}

							// 属性点:属性值
							for (AttributeInfo attributeInfo : attributeInfoList) {
								// 每天上报的timing和trigger次数
								if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
										&& "rt".equals(attributeInfo.getAttributeName())) {
									if ("trigger".equals(attributeInfo.getAttributeReportedValue())) {
										if (countTrigger.containsKey(deviceIdJson)) {
											countTrigger.replace(deviceIdJson,
													countTrigger.getInteger(deviceIdJson) + 1);
										} else {
											countTrigger.put(deviceIdJson, 1);
										}
									} else if ("timing".equals(attributeInfo.getAttributeReportedValue())) {
										rt = "timing";
										if (countTiming.containsKey(deviceIdJson)) {
											countTiming.replace(deviceIdJson, countTiming.getInteger(deviceIdJson) + 1);
										} else {
											countTiming.put(deviceIdJson, 1);
										}
									}

								}
								
								if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
										&& "xavtv".equals(attributeInfo.getAttributeName())) {
									xavtv = attributeInfo.getAttributeReportedValue();
								}

								if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
										&& allAttributeAlias.containsKey(attributeInfo.getAttributeName())) {
									tempJson.put(attributeInfo.getAttributeName(),
											attributeInfo.getAttributeReportedValue());
								}

							}
							logger.info("-1-");

							tempJson.put("设备id", deviceId);
							String reportTime = UtilTool.timeStampToDate(parseLong);
							tempJson.put("上报时间", reportTime);

							/*tempJson.put("上报次数", countNumber.getOrDefault(deviceId, 0));
							tempJson.put("timing次数", countTiming.getOrDefault(deviceId, 0));
							tempJson.put("trigger次数", countTrigger.getOrDefault(deviceId, 0));
*/
							// resultList.add(tempJson);

							// 当前数据是否可用
							if (xgiv != 2 && rt.equals("timing") && !"65535".equals(xavtv)) {
								onlyOnceTimming += 1;
								resultJson = tempJson;
							}

						}
						// 该设备当日可用数据有且仅有一条时,计算r值,及次数
						if (onlyOnceTimming == 1) {
							resultJson.put("上报次数", countNumber.getOrDefault(deviceId, 0));
							resultJson.put("timing次数", countTiming.getOrDefault(deviceId, 0));
							resultJson.put("trigger次数", countTrigger.getOrDefault(deviceId, 0));

							
							resultList.add(resultJson);

						}
						

					}

				}
				System.out.println("123");
				
				Set<Entry<String, Object>> entrySet = countTiming.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					String value = entry.getValue() + "";
					
					if (StringUtils.isNumeric(value)) {
						int time = Integer.parseInt(value);
						if (time > 3) {
							faultIds.add(entry.getKey());
						}
					}
				}
				
			}

			save2Localhost(faultIds);
			logger.info("故障设备id存入成功");

			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", resultList);
		} catch (Exception e) {
			logger.error(e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}
	
	
	
	
	public RpcResponse<List<JSONObject>> deviceStateInfo1111(JSONObject jsonObject) {
		try {
			// TODO Auto-generated method stub
			logger.info("进入deviceStateInfo1111,rpc方法参数:" + "jsonObject");
			
			String points = jsonObject.getString("points");
			String[] split = points.split(",");
			

			List<JSONObject> resultList = Lists.newArrayList();


			long time1 = System.currentTimeMillis();
			List<JSONObject> devicesRsFac = query4ExcelRepository.getFacilitiesRsStateInfo();
			Map<String, JSONObject> deviceIdRsFacInfp = Maps.newHashMap();
			List<String> deviceIds = Lists.newArrayList();

			JSONObject AttributeName = new JSONObject();
			AttributeName.put("facilitiesCode", "空");

			AttributeName.put("最新上报时间", "空");
			for (String point : split) {
				AttributeName.put(point, "空");
			}

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");
				deviceIdRsFacInfp.put(idInMysql, deviceRsFacJson);
				deviceIds.add(idInMysql);
			}
			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));

			

			long time3 = System.currentTimeMillis();
			logger.info("查询设备历史上报次数,耗时:" + (time3 - time2));

			Criteria lastDataCriteria = new Criteria().andOperator(Criteria.where("things.gatewayId").exists(true),
					Criteria.where("deviceId").in(deviceIds));
			Query lastData = new Query(lastDataCriteria);
			List<JSONObject> deviceStateList = mongoTemplate.find(lastData, JSONObject.class, "deviceState");

			for (JSONObject deviceStateJson : deviceStateList) {
				JSONArray deviceStateArray = deviceStateJson.getJSONArray("attributeInfo");
				String deviceId = deviceStateJson.getString("deviceId");
				String timestamp = deviceStateJson.getString("timestamp");
				if (StringUtils.isBlank(timestamp) || !StringUtils.isNumeric(timestamp)) {
					logger.error("deviceStateInfo1111()-->时间戳值为空/非数值:" + timestamp);
				}
				long parseLong = Long.parseLong(timestamp);

				List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);
				JSONObject resultJson = new JSONObject();
				resultJson.putAll(AttributeName);
				resultJson.put("deviceId", deviceId);
				String reportTime = UtilTool.timeStampToDate(parseLong);
				resultJson.put("最新上报时间", reportTime);

				JSONObject facInJson = deviceIdRsFacInfp.get(deviceId);
				if (null != facInJson) {
					Set<Entry<String, Object>> entrySet = facInJson.entrySet();
					for (Entry<String, Object> entry : entrySet) {
						resultJson.put(entry.getKey(), entry.getValue());
					}
				}


				// 属性点:属性值
				for (AttributeInfo attributeInfo : attributeInfoList) {
					if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
							&& StringUtils.isNotBlank(attributeInfo.getAttributeReportedValue())
							 && AttributeName.containsKey(attributeInfo.getAttributeName())) {
						resultJson.put(attributeInfo.getAttributeName(), attributeInfo.getAttributeReportedValue());
					}
				}

				resultList.add(resultJson);
			}

			long time4 = System.currentTimeMillis();
			logger.info("查询最新状态,耗时:" + (time4 - time3));

			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", resultList);
		} catch (Exception e) {
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}
	
	
	
	/**
	  * 
	  * @Description:查询一个月没上报的设备5天上报数据
	  * @param 
	  * @return
	  */
	public RpcResponse<List<JSONObject>> deviceStateInfo1206(JSONObject jsonObject) {
		try {
			logger.info("进入deviceStateInfo4Excel,rpc方法参数:" + "jsonObject");

			List<JSONObject> resultList = Lists.newLinkedList();
			//List<String> faultIds = Lists.newArrayList();
			String points = jsonObject.getString("points");
			String[] split = points.split(",");
			// String startTime = jsonObject.getString("startTime");

			JSONObject allAttributeAlias = new JSONObject();

			allAttributeAlias.put("蓝牙名称", "空");
			allAttributeAlias.put("facilitiesCode", "空");
			allAttributeAlias.put("latitude", "空");
			allAttributeAlias.put("longitude", "空");
			allAttributeAlias.put("address", "空");
			allAttributeAlias.put("组织id", "空");


			for (String point : split) {
				allAttributeAlias.put(point, "空");
			}
			// String startTimeStamp = DateUtils.dateToStamp(startTime);
			long time1 = System.currentTimeMillis();
			String accessSecret = jsonObject.getString("accessSecret");

			String timeFrom = jsonObject.getString("timeFrom");
			List<JSONObject> devicesRsFac = query4ExcelRepository.getAllDevicesRsFacilitiesInfo(accessSecret,null,
					timeFrom);
			long time2 = System.currentTimeMillis();
			logger.info("查询设施设备相关信息,耗时:" + (time2 - time1));
			Map<String, JSONObject> deviceIdRsFacInfp = Maps.newHashMap();
			List<String> deviceIds = Lists.newArrayList();

			for (JSONObject deviceRsFacJson : devicesRsFac) {
				String idInMysql = deviceRsFacJson.getString("idInMysql");
				deviceIdRsFacInfp.put(idInMysql, deviceRsFacJson);
				deviceIds.add(idInMysql);
			}

				

			for (String deviceId : deviceIds) {
				// 获取到一天的所有上报数据
				List<JSONObject> deviceHistoryState = getInfoLastNum(5, deviceId);

				for (JSONObject json : deviceHistoryState) {
					String deviceIdJson = json.getString("deviceId");

					JSONObject resultJson = new JSONObject();

					// 匹配单个设备每天的所有数据
					if (deviceId.equals(deviceIdJson)) {

						String timestamp = json.getString("timestamp");
						if (StringUtils.isBlank(timestamp) || !StringUtils.isNumeric(timestamp)) {
							logger.error("deviceStateInfo4Excel()-->时间戳值为空/非数值:" + timestamp);
						}
						long parseLong = Long.parseLong(timestamp);

						
						JSONArray deviceStateArray = json.getJSONArray("attributeInfo");
						List<AttributeInfo> attributeInfoList = deviceStateArray.toJavaList(AttributeInfo.class);

						resultJson.putAll(allAttributeAlias);
						JSONObject facInfo = deviceIdRsFacInfp.get(deviceId);
						if (null != facInfo) {
							resultJson.put("蓝牙名称", facInfo.getOrDefault("蓝牙名称", "空"));
							resultJson.put("address", facInfo.getOrDefault("address", "空"));
							resultJson.put("longitude", facInfo.getOrDefault("longitude", "空"));
							resultJson.put("latitude", facInfo.getOrDefault("latitude", "空"));
							resultJson.put("facilitiesCode", facInfo.getOrDefault("facilitiesCode", "空"));
							resultJson.put("组织id", facInfo.getOrDefault("organizationId", "空"));

						}

						// 属性点:属性值
						for (AttributeInfo attributeInfo : attributeInfoList) {
							

							if (StringUtils.isNotBlank(attributeInfo.getAttributeName())
									&& allAttributeAlias.containsKey(attributeInfo.getAttributeName())) {
								resultJson.put(attributeInfo.getAttributeName(),
										attributeInfo.getAttributeReportedValue());
							}

						}
						logger.info("-1-");

						resultJson.put("设备id", deviceId);
						String reportTime = UtilTool.timeStampToDate(parseLong);
						resultJson.put("上报时间", reportTime);

						resultList.add(resultJson);

					}
					

				}

			}

			return RpcResponseBuilder.buildSuccessRpcResp("查询成功", resultList);
		} catch (Exception e) {
			logger.error(e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	public List<JSONObject> getCountTimingAndTrigger(long day) {
		try {
			String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);
			// 1天前的00-昨天的23：59：59
			String startTime = (Long.parseLong(timeStamp1) - 86400000 * day) + "";
			String endTime = Long.parseLong(timeStamp1) - 1000 - (86400000 * (day - 1)) + "";

			DBObject query1 = new BasicDBObject();
			query1.put("timestamp", new BasicDBObject("$gte", startTime).append("$lte", endTime));
			query1.containsKey("things.gatewayId");

			DBObject query2 = new BasicDBObject();
			query2.put("attributeInfo.attributeReportedValue", 1);
			query2.put("attributeInfo.attributeName", 1);
			query2.put("timestamp", 1);
			query2.put("deviceId", 1);

			DBCursor dbCursor = mongoTemplate.getCollection("deviceHistoryState").find(query1, query2);
			List<JSONObject> deviceHistoryState = Lists.newArrayList();
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				JSONObject te = new JSONObject();
				te.put("attributeInfo", object.get("attributeInfo"));
				te.put("timestamp", object.get("timestamp"));
				te.put("deviceId", object.get("deviceId"));
				deviceHistoryState.add(te);
			}
			
			return deviceHistoryState;
		} catch (Exception e) {
			logger.error("checkFaultOrderDevice()->exception", e);
			return null;
		}
	}
	
	
	
	public List<JSONObject> getInfoLastNum(int time, String deviceId) {
		try {
			/*String timeS = DateUtils.getDateString("start");
			String timeStamp1 = DateUtils.dateToStamp(timeS);*/
			// 1天前的00-昨天的23：59：59
			/*String startTime = (Long.parseLong(timeStamp1) - 86400000 * day) + "";
			String endTime = Long.parseLong(timeStamp1) - 1000 - (86400000 * (day - 1)) + "";
*/
			/*DBObject query1 = new BasicDBObject();
			//query1.put("timestamp", new BasicDBObject("$gte", startTime).append("$lte", endTime));
			query1.containsKey("things.gatewayId");
			query1.put("deviceId", deviceId);
				
			DBObject query2 = new BasicDBObject();
			query2.put("attributeInfo.attributeReportedValue", 1);
			query2.put("attributeInfo.attributeName", 1);
			query2.put("timestamp", -1);
			query2.put("deviceId", 1);

			DBObject orderBy = new BasicDBObject();
			query2.put("timestamp", -1);
			
			DBCursor dbCursor = mongoTemplate.getCollection("deviceHistoryState").find(query1, query2).limit(time);
			List<JSONObject> deviceHistoryState = Lists.newArrayList();
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				JSONObject te = new JSONObject();
				te.put("attributeInfo", object.get("attributeInfo"));
				te.put("timestamp", object.get("timestamp"));
				te.put("deviceId", object.get("deviceId"));
				deviceHistoryState.add(te);
			}*/
			
			
			Criteria lastDataCriteria = new Criteria().andOperator(Criteria.where("things.gatewayId").exists(true),
					Criteria.where("deviceId").is(deviceId));
			Query lastData = new Query(lastDataCriteria);
			lastData.with(new Sort(new Order(Direction.DESC,"timestamp")));
			lastData.limit(5);
			List<JSONObject> deviceHistoryState = mongoTemplate.find(lastData, JSONObject.class, "deviceState");
			
			
			return deviceHistoryState;
		} catch (Exception e) {
			logger.error("checkFaultOrderDevice()->exception", e);
			return null;
		}
	}



	public void save2Localhost(Set<String> list) {
		String filePar = "d:\\test";// 文件夹路径
		File myPath = new File(filePar);
		if (!myPath.exists()) {// 若此目录不存在，则创建之
			myPath.mkdir();
			System.out.println("创建文件夹路径为：" + filePar);
		}
		StringBuffer stringBuffer = new StringBuffer();
		for (String string : list) {
			stringBuffer.append(string + ",");
		}
		Date date = new Date();
		// 文件夹路径存在的情况下
		String filename = "test.txt";// 文件名
		try {
			FileWriter fw = new FileWriter(filePar + "\\" + filename, true);// filePar
																			// +
																			// "\\"
																			// +
																			// filename,true
			// FileWriter 就是牛，如果文件名 的文件不存在，先创建再读写;存在的话直接追加写,关键字true表示追加
			

			fw.write(stringBuffer.toString());
			// 关闭写文件,每次仅仅写一行数据，因为一个读文件中仅仅一个唯一的od
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
