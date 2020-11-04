/*
 * File name: DeviceStateQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年9月28日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.DeviceStateQueryRepository;
import com.run.locman.api.query.service.DeviceStateQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.MongodbConstans;
import com.run.locman.constants.PublicConstants;

/**
 * @Description:临时针对设备状态导出
 * @author: zhaoweizhi
 * @version: 1.0, 2018年9月28日
 */

public class DeviceStateQueryServiceImpl implements DeviceStateQueryService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceStateQueryRepository	deviceStateQueryRepository;

	@Autowired
	private MongoTemplate				mongoTemplate;



	/**
	 * @see com.run.locman.api.query.service.DeviceStateQueryService#findDeviceState(java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, String>>> findDeviceState(String accessSecret) {

		try {

			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[findDeviceState()->error:接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("[findDeviceState()->error:接入方密钥不能为空！]");
			}

			// 获取设备名称以及设备ID
			List<Map<String, String>> deviceList = deviceStateQueryRepository.findDeviceState(accessSecret);

			// 构建设备IDlist
			List<String> deviceIds = copyDeviceId(deviceList);

			// 查询monggodb
			Query query = new Query();
			query.addCriteria(Criteria.where(DeviceContants.DEVICEID).in(deviceIds));
			List<JSONObject> deviceStateList = mongoTemplate.find(query, JSONObject.class,
					MongodbConstans.MONGODB_DEVICESTATE);

			// 构建key为deviceId，value为deviceName的map
			Map<String, String> copyDeviceMap = copyDeviceMap(deviceList);

			// 构建数据
			List<Map<String, String>> copyDeviceState = copyDeviceState(copyDeviceMap, deviceStateList);

			logger.info("[findDeviceState()->suc:查询设备状态数据成功！]");
			return RpcResponseBuilder.buildSuccessRpcResp("[findDeviceState()->suc:查询设备状态数据成功！]", copyDeviceState);
		} catch (Exception e) {
			logger.error("findDeviceState()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * 
	 * @Description:构建设备IDs
	 * @param deviceList
	 * @return
	 * @throws Exception
	 */
	private List<String> copyDeviceId(List<Map<String, String>> deviceList) throws Exception {

		List<String> deviceIds = Lists.newArrayList();

		for (Map<String, String> deviceMap : deviceList) {
			deviceIds.add(deviceMap.get(PublicConstants.ID));
		}

		return deviceIds;

	}



	/**
	 * 
	 * @Description:构建key为deviceId，value为deviceName的map
	 * @param deviceList
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> copyDeviceMap(List<Map<String, String>> deviceList) throws Exception {
		Map<String, String> deviceMap = Maps.newHashMap();

		for (Map<String, String> map : deviceList) {
			deviceMap.put(map.get(PublicConstants.ID), map.get(DeviceContants.DEVICENAME));
		}

		return deviceMap;
	}



	/**
	 * 
	 * @Description:构建设备导出数据
	 * @param deviceList
	 * @param deviceStateList
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, String>> copyDeviceState(Map<String, String> deviceMap, List<JSONObject> deviceStateList)
			throws Exception {
		List<Map<String, String>> deviceList = Lists.newArrayList();
		for (JSONObject jsonObject : deviceStateList) {
			Map<String, String> exportMap = Maps.newHashMap();
			exportMap.put(DeviceContants.DEVICEID, jsonObject.getString(DeviceContants.DEVICEID));
			exportMap.put(DeviceContants.DEVICENAME, deviceMap.get(jsonObject.getString(DeviceContants.DEVICEID)));
			String date = DateUtils.formatDate(new Date(jsonObject.getDate("timestamp").getTime() * 1000L));
			exportMap.put("updateTime", date);
			JSONObject reportedObj = UtilTool.getReported(jsonObject);

			if (reportedObj != null) {
				exportMap.put("bv",
						StringUtils.isBlank(reportedObj.getString("bv")) ? "" : reportedObj.getString("bv"));
				exportMap.put("sig",
						StringUtils.isBlank(reportedObj.getString("sig")) ? "" : reportedObj.getString("sig"));
				exportMap.put("hardwareid", StringUtils.isBlank(reportedObj.getString("hardwareid")) ? ""
						: reportedObj.getString("hardwareid"));
				deviceList.add(exportMap);
				continue;
			}

			exportMap.put("bv", "");
			exportMap.put("sig", "");
			exportMap.put("hardwareid", "");
			deviceList.add(exportMap);
		}

		return deviceList;
	}
}
