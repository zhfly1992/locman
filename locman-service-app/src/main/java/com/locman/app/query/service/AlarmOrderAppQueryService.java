package com.locman.app.query.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.base.query.FacilitesService;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.constants.CommonConstants;

@Service
@SuppressWarnings("rawtypes")
public class AlarmOrderAppQueryService extends BaseAppController {
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private AlarmOrderQueryService	alarmOrderQueryService;

	@Autowired
	FacilityDeviceQueryService		facilityDeviceQueryService;

	@Autowired
	private DeviceAppQueryService	deviceAppQueryService;

	@Autowired
	private FacilitesService		facilitesService;

	@Autowired
	private UscQueryService			uscQueryService;

	@Autowired
	private CommonService			commonService;

	private String					path	= "alarmOrder/";



	/**
	 * 根据id查询告警工单详情 <method description>
	 *
	 * @param id
	 * @return
	 */
	public Result getAlarmOrderById(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("id")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("selectNo")) {
				return ResultBuilder.noBusinessResult();
			}
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String id = jsonObject.getString("id");
			String selectNo = jsonObject.getString("selectNo");
			RpcResponse<Map<String, Object>> rpcResponse = alarmOrderQueryService.getAlarmOrderInfoById(id);
			if (rpcResponse.isSuccess() && rpcResponse.getSuccessValue() != null) {
				Map<String, Object> orderMap = rpcResponse.getSuccessValue();
				Map<String, Object> resultMap = Maps.newHashMap();
				resultMap.put("serialNum", orderMap.get("serialNum"));
				resultMap.put("alarmLevel", orderMap.get("alarmLevel"));
				resultMap.put("alarmDesc", orderMap.get("alarmDesc"));
				resultMap.put("alarmTime", orderMap.get("alarmTime"));
				resultMap.put("alarmSerialNum", orderMap.get("alarmSerialNum"));
				resultMap.put("id", orderMap.get("id"));
				resultMap.put("processState", orderMap.get("processState"));
				resultMap.put("processId", orderMap.get("processId"));
				resultMap.put("commandFlag", orderMap.get("commandFlag"));
				resultMap.put("presentPic", orderMap.get("presentPic"));
				resultMap.put("reject_remark", "");
				if ("1".equals(resultMap.get("processState"))) {
					// 获取工单无法修复原因
					String url = path + "getPowerlessInfo/" + id;
					String resultHttp = commonService.requestRestGet(Constant.LOCMAN_PORT, url, null);
					if (resultHttp != null) {
						Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(resultHttp));
						if (resultValue.containsKey("success")) {
							System.out.println(resultValue.get("success"));
							JSONArray array = JSONObject.parseArray(resultValue.get("success"));
							if (array != null && !array.isEmpty()) {
								Object object = array.get(0);
								JSONObject value = (JSONObject) object;
								if (value.containsKey("powerless_remark")) {
									resultMap.put("reject_remark", value.get("powerless_remark"));
									resultMap.put("active_orderImg", value.get("powerless_orderImg"));
									resultMap.put("imgFlag", "reject");
									resultMap.put("reject_name", "无法修复原因");
								}
							}

						}
					}
				}
				if ("2".equals(resultMap.get("processState"))) {
					String url2 = path + "getRejectCause/" + id;
					String resultHttp2 = commonService.requestRestGet(Constant.LOCMAN_PORT, url2, null);
					if (resultHttp2 != null) {
						Map<String, String> resultValue = commonService
								.checkResult(JSONObject.parseObject(resultHttp2));
						if (resultValue.containsKey("success")) {
							JSONArray array = JSONObject.parseArray(resultValue.get("success"));
							if (array != null && !array.isEmpty()) {
								Object object = array.get(0);
								JSONObject value = (JSONObject) object;
								if (value.containsKey("reject_remark")) {
									resultMap.put("reject_remark", value.get("reject_remark"));
									resultMap.put("reject_name", "审批拒绝原因");
								}
							}
						}
					}
				}
				// 查询工单流程状态
				RpcResponse<List<Map<String, Object>>> selectState = alarmOrderQueryService.getState(1);
				if (selectState.isSuccess() && selectState.getSuccessValue() != null) {
					List<Map<String, Object>> stateList = selectState.getSuccessValue();
					for (Map<String, Object> map : stateList) {
						if (map.get("sign").equals(orderMap.get("processState"))) {
							resultMap.put("processStateName", map.get("name"));
						}
					}
				}
				String button = "";
				String processState = orderMap.get("processState").toString();
				if (Constant.OPTIONS_TABS.MY_PROCESS.equals(selectNo)) {
					if ("5".equals(processState)) {
						button = Constant.BUTTUN.BT_THREE;
					} else if ("0".equals(processState) || "2".equals(processState)) {
						button = Constant.BUTTUN.BT_ONE;
					}
				} else if (Constant.OPTIONS_TABS.BACKLOG_PROCESS.equals(selectNo)) {
					if ("1".equals(processState)) {
						button = Constant.BUTTUN.BT_TWO;
					} else if (("0".equals(processState) && userJson.getString("_id").equals(orderMap.get("userId")))
							|| StringUtils.equals(processState, "2")) {
						button = Constant.BUTTUN.BT_ONE;
					} else if ("6".equals(processState)) {
						button = Constant.BUTTUN.BT_FOUR;
					}
				}
				resultMap.put("remark", "");
				String resultHttp = commonService.requestRestGet(Constant.LOCMAN_PORT, "alarmOrder/getComplete/" + id,
						null);
				Map<String, String> reMap = commonService.checkResult(JSONObject.parseObject(resultHttp));
				if (reMap.containsKey("success")) {
					JSONObject valueJson = JSONObject.parseObject(reMap.get("success"));
					resultMap.put("remark", valueJson.getString("remark"));
					if (resultMap.get("active_orderImg") == null) {
						resultMap.put("imgFlag", "complete");
						resultMap.put("active_orderImg", valueJson.getString("orderPic"));
					}
				}
				resultMap.put("button", button);
				if (orderMap.get("facilitiesId") != null) {
					RpcResponse<Map<String, Object>> response = facilitesService
							.getFacilitesInfoByFacId(orderMap.get("facilitiesId").toString(), token);
					Map<String, Object> respMap = response.getSuccessValue();
					Map<String, Object> facMap = Maps.newHashMap();
					if (respMap != null && !respMap.isEmpty()) {
						facMap.put("facilitiesId", respMap.get("id"));
						facMap.put("facilitiesCode", respMap.get("facilitiesCode"));
						facMap.put("address", respMap.get("address"));
						facMap.put("facilityTypeAlias", respMap.get("facilityTypeAlias"));
						facMap.put("longitude", respMap.get("longitude"));
						facMap.put("latitude", respMap.get("latitude"));
						resultMap.put("facilities", facMap);
					}

					// 查询设备信息
					List<String> ids = Lists.newArrayList();
					ids.add(orderMap.get("deviceId").toString());
					List<Map> deviceInfo = deviceAppQueryService.getDeviceInfoByIds(ids,
							Constant.GETACCESSSECRET(userJson.getString("_id")));
					facMap.put("deviceInfo", deviceInfo);
				}
				return ResultBuilder.successResult(resultMap, rpcResponse.getMessage());
			}
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 查询告警工单列表 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Result getAlarmOrderList(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("stateNo")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("orgId")) {
				return ResultBuilder.noBusinessResult();
			}
			String orgId = jsonObject.getString("orgId");
			String type = jsonObject.getString("stateNo");
			if (!jsonObject.containsKey("userId") || StringUtils.isBlank(jsonObject.getString("userId"))) {
				return ResultBuilder.noBusinessResult();
			}
			jsonObject.put("accessSecret", Constant.GETACCESSSECRET(jsonObject.getString("userId")));
			jsonObject.put("beginTime", "");
			jsonObject.put("endTime", "");
			jsonObject.put("facTypeId", "");
			jsonObject.put("orgId", "");
			jsonObject.put("address", "");
			jsonObject.put("alarmOrderId", "");
			// jsonObject.remove("orderState");
			jsonObject.put("num", jsonObject.get("selectKey"));
			jsonObject.remove("selectKey");
			String resultHttp = "";
			if (Constant.OPTIONS_TABS.MY_PROCESS.equals(type)) {// 我的流程
				jsonObject.remove("stateNo");
				String url = path + "getAllByPage";
				resultHttp = commonService.requestRest(Constant.LOCMAN_PORT, url, jsonObject.toJSONString());
				logger.info("我的流程===》 " + resultHttp);
				if (resultHttp != null) {
					Map<String, String> resultMap = commonService.checkResult(JSONObject.parseObject(resultHttp));
					if (resultMap.containsKey("success")) {
						JSONObject resultJson = JSONObject.parseObject(resultMap.get("success"));
						/*
						 * List<Map<String, Object>> orderList =
						 * castOrderObject( (List<Map<String, Object>>)
						 * resultJson.get("list"));
						 */
						List<Map<String, Object>> orderList = (List<Map<String, Object>>) resultJson.get("list");
						resultJson.put("list", orderList);
						return ResultBuilder.successResult(resultJson, "查询成功");
					} else {
						return ResultBuilder.failResult(resultMap.get("error"));
					}
				} else {
					return ResultBuilder.failResult("告警工单列表获取失败，请稍后重试！");
				}
			} else if (Constant.OPTIONS_TABS.BACKLOG_PROCESS.equals(type)) {// 代办流程
				jsonObject.remove("stateNo");
				String url = path + "getAllTodoByPage";
				resultHttp = commonService.requestRest(Constant.LOCMAN_PORT, url, jsonObject.toJSONString());
				logger.info("待办流程===》 " + resultHttp);
				if (resultHttp != null) {
					Map<String, String> resultMap = commonService.checkResult(JSONObject.parseObject(resultHttp));
					if (resultMap.containsKey("success")) {
						JSONObject resultJson = JSONObject.parseObject(resultMap.get("success"));
						/*
						 * List<Map<String, Object>> orderList =
						 * castOrderObject( (List<Map<String, Object>>)
						 * resultJson.get("list"));
						 */
						List<Map<String, Object>> orderList = (List<Map<String, Object>>) resultJson.get("list");
						resultJson.put("list", orderList);
						return ResultBuilder.successResult(resultJson, "查询成功");
					} else {
						return ResultBuilder.failResult(resultMap.get("error"));
					}
				} else {
					return ResultBuilder.failResult("告警工单列表获取失败，请稍后重试！");
				}
			} else if (Constant.OPTIONS_TABS.RUN_PROCESS.equals(type)) {// 已办流程
				jsonObject.remove("stateNo");
				String url = path + "getAllHaveDealByPage";
				resultHttp = commonService.requestRest(Constant.LOCMAN_PORT, url, jsonObject.toJSONString());
				logger.info("已办流程===》 " + resultHttp);
				if (resultHttp != null) {
					Map<String, String> resultMap = commonService.checkResult(JSONObject.parseObject(resultHttp));
					if (resultMap.containsKey("success")) {
						JSONObject resultJson = JSONObject.parseObject(resultMap.get("success"));
						/*
						 * List<Map<String, Object>> orderList =
						 * castOrderObject( (List<Map<String, Object>>)
						 * resultJson.get("list"));
						 */
						List<Map<String, Object>> orderList = (List<Map<String, Object>>) resultJson.get("list");
						resultJson.put("list", orderList);
						return ResultBuilder.successResult(resultJson, "查询成功");
					} else {
						return ResultBuilder.failResult(resultMap.get("error"));
					}
				} else {
					return ResultBuilder.failResult("告警工单列表获取失败，请稍后重试！");
				}
			} else if (Constant.OPTIONS_TABS.NOT_RECEIVED.equals(type)) {// 待接收工单
				if (StringUtils.isBlank(orgId)) {
					return ResultBuilder.failResult("组织id不能为空！");
				}
				jsonObject.put("orgId", orgId);
				String url = path + "notClaimAlarmOrder";
				resultHttp = commonService.requestRest(Constant.LOCMAN_PORT, url, jsonObject.toJSONString());
				if (resultHttp != null) {
					Map<String, String> httpValue = commonService.checkResult(JSONObject.parseObject(resultHttp));
					logger.info("接收工单===》 " + httpValue);
					if (httpValue.containsKey("success")) {
						JSONObject resultJosn = JSONObject.parseObject(httpValue.get("success"));
						// 查询工单流程状态
						RpcResponse<List<Map<String, Object>>> selectState = alarmOrderQueryService.getState(1);
						List<Map<String, Object>> stateList = Lists.newArrayList();
						if (selectState.isSuccess() && selectState.getSuccessValue() != null) {
							stateList = selectState.getSuccessValue();
						}
						List<Map<String, Object>> orderList = (List<Map<String, Object>>) resultJosn.get("list");
						List<Map<String, Object>> orders = Lists.newArrayList();
						if (orderList != null && !orderList.isEmpty()) {
							for (Map<String, Object> map : orderList) {
								Map<String, Object> objMap = map;
								objMap.put("processStateName", "");
								logger.info("stateList===> " + stateList.size());
								for (Map<String, Object> state : stateList) {
									logger.info(state.get("sign").toString() + "===" + state.get("name").toString());
									if (state.get("sign").toString().equals(map.get("processState").toString())) {
										objMap.put("processStateName", state.get("name"));
									}
								}
								orders.add(objMap);
							}
						}
						resultJosn.put("list", orders);
						logger.info("resultJosn ===> " + resultJosn);
						return ResultBuilder.successResult(resultJosn, "查询成功！");
					} else {
						return ResultBuilder.failResult(httpValue.get("error"));
					}
				} else {
					return ResultBuilder.failResult("接收工单列表获取失败，请稍后重试！");
				}
			}
			return ResultBuilder.failResult("未选择操作");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 封装工单列表返回结果 <method description>
	 *
	 * @param res
	 * @return
	 */
	public List<Map<String, Object>> castOrderObject(List<Map<String, Object>> list) {
		List<Map<String, Object>> resultList = Lists.newArrayList();
		if (list == null || list.isEmpty()) {
			return resultList;
		} else {
			for (Map<String, Object> map : list) {
				Map<String, Object> mapTemp = Maps.newHashMap();
				mapTemp.put("serialNum", map.get("serialNum"));
				mapTemp.put("processStateName", map.get("processStateName"));
				mapTemp.put("processState", map.get("processState"));
				mapTemp.put("alarmDesc", map.get("alarmDesc"));
				mapTemp.put("address", map.get("address"));
				mapTemp.put("alarmLevel", map.get("alarmLevel"));
				mapTemp.put("alarmTime", map.get("alarmTime"));
				mapTemp.put("orderId", map.get("orderId"));
				mapTemp.put("facilityTypeName", map.get("facilityTypeName"));
				mapTemp.put("facilitiesId", map.get("facilitiesId"));
				mapTemp.put("facilitiesCode", map.get("facilitiesCode"));
				mapTemp.put("alarmSerialNum", map.get("alarmSerialNum"));
				mapTemp.put("facilityTypeAlias", map.get("facilityTypeAlias"));
				mapTemp.put("processId", map.get("processId"));
				resultList.add(mapTemp);
			}
			return resultList;
		}
	}



	/**
	 * 获取待整治项 
	 *
	 * @return
	 */
	public Result getHiddenTroubleType() {
		String url = path + "getHiddenTroubleType";
		String jsonResult = commonService.requestRestGet(Constant.LOCMAN_PORT, url, null);
		Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
		String value = "";
		logger.info("获取待整治项 ");
		if (resultValue.containsKey("success")) {
			value = resultValue.get("success");
			return ResultBuilder.successResult(value, "待整治项成功");
		} else {
			value = resultValue.get("error");
			return ResultBuilder.failResult(value);
		}
	}
}
