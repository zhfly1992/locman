package com.locman.app.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.DeviceQueryService;

@Service
public class AlarmInfoAppQueryService {

	@Autowired
	private DeviceQueryService	deviceQueryService;

	@Autowired
	private CommonService		commonService;



	/**
	 * 查询告警信息列表 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Result<JSONObject> getAlarmInfoList(String param) {
		try {
			Result<JSONObject> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return result;
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("userId") || StringUtils.isBlank(jsonObject.getString("userId"))) {
				return ResultBuilder.noBusinessResult();
			}
			jsonObject.put("accessSecret", Constant.GETACCESSSECRET(jsonObject.getString("userId")));
			jsonObject.put("address", "");
			jsonObject.put("alarmTimeStart", "");
			jsonObject.put("alarmTimeEnd", "");
			jsonObject.put("facilitiesTypeId", "");
			jsonObject.put("facilityCodeOrId", "");
			System.out.println(jsonObject);
			String url = "stateInfo/getAlarmInfoList";
			String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, jsonObject.toString());
			Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
			if (resultValue.containsKey("success")) {
				if (resultValue.get("success") != null) {
					JSONObject resultJson = JSONObject.parseObject(resultValue.get("success").toString());
					Object obj = resultJson.get("list");
					if (obj != null && !"{}".equals(obj.toString()) && obj instanceof JSONArray) {
						List<Map<String, Object>> lists = (List<Map<String, Object>>) obj;
						List<Map<String, Object>> objList = Lists.newArrayList();
						for (Map<String, Object> map : lists) {
							String isDel = map.get("isDel") == null ? "1" : map.get("isDel").toString();
							if(StringUtils.equals(isDel, "1")) {
								Map<String, Object> value = Maps.newHashMap();
								value.put("alarmLevel", map.get("alarmLevel"));
								value.put("id", map.get("id"));
								value.put("isDel", map.get("isDel"));
								String deviceName = "";
								List<String> ids = Lists.newArrayList();
								ids.add(map.get("deviceId").toString());
								RpcResponse<List<Map<String, Object>>> getDeviceIdInfoByDeviceId = deviceQueryService
										.queryBatchDeviceInfoForDeviceIds(ids);
								if (getDeviceIdInfoByDeviceId.isSuccess()
										&& getDeviceIdInfoByDeviceId.getSuccessValue() != null
										&& !getDeviceIdInfoByDeviceId.getSuccessValue().isEmpty()) {
									Map<String, Object> devMap = getDeviceIdInfoByDeviceId.getSuccessValue().get(0);
									if (devMap != null && !devMap.isEmpty()) {
										deviceName = devMap.get("deviceName").toString();
									}
								}
								value.put("message",
										map.get("serialNum") + "   序列号为" + map.get("facilitiesCode") + "的"
												+ map.get("facilityTypeAlias") + "下的" + deviceName + "设备,在"
												+ map.get("address") + "于" + map.get("alarmTime").toString() + "发生"
												+ map.get("alarmDesc") + "告警,请注意及时到场处理。");
								objList.add(value);
							}
						}
						resultJson.put("list", objList);
					}
					return ResultBuilder.successResult(resultJson, "查询成功");
				}
			} else {
				return ResultBuilder.failResult(resultValue.get("error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
		return null;
	}
}
