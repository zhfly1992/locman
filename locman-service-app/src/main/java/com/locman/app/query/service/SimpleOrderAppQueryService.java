package com.locman.app.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.SimpleOrderProcessType;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.api.query.service.SimpleOrderTypeQueryService;
import com.run.usc.base.query.UserBaseQueryService;

@Service
@SuppressWarnings("rawtypes")
public class SimpleOrderAppQueryService extends BaseAppController {

	@Autowired
	SimpleOrderQueryService				simpleOrderQueryService;

	@Autowired
	private SimpleOrderTypeQueryService	simpleOrderTypeQueryService;

	@Autowired
	DeviceAppQueryService				deviceAppQueryService;

	@Autowired
	private UserBaseQueryService		userBaseQueryService;

	@Autowired
	FacilitiesQueryService				facilitiesQueryService;

	@Autowired
	private FacilityDeviceQueryService	facilityDeviceQueryService;

	@Autowired
	private CommonService				commonService;

	@Autowired
	private UscQueryService				uscQueryService;

	@Autowired
	private DeviceQueryService			deviceQueryService;



	/**
	 * 分页查询一般流程列表 <method description>
	 *
	 * @param param
	 * @return
	 */
	public Result getSimpleOrderList(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return result;
			}
			JSONObject paramJson = JSONObject.parseObject(param);
			if (!paramJson.containsKey("stateNo")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!paramJson.containsKey("userId")) {
				return ResultBuilder.noBusinessResult();
			}
			String state = paramJson.getString("stateNo");
			paramJson.put("accessSecret", Constant.GETACCESSSECRET(paramJson.getString("userId")));
			if (Constant.OPTIONS_TABS.MY_PROCESS.equals(state)) {// 我的流程
				paramJson.remove("stateNo");
				paramJson.put("orderType", "");
				paramJson.put("processStartTime", "");
				paramJson.put("processEndTime", "");
				RpcResponse<PageInfo<Map<String, Object>>> rpcResponse = simpleOrderQueryService
						.getSimpleOrderList(paramJson);
				if (rpcResponse.isSuccess()) {
					PageInfo<Map<String, Object>> pages = getOrderList(rpcResponse,
							paramJson.get("accessSecret").toString());
					return ResultBuilder.successResult(pages, rpcResponse.getMessage());
				}
				return ResultBuilder.failResult(rpcResponse.getMessage());
			} else if (Constant.OPTIONS_TABS.BACKLOG_PROCESS.equals(state)) {// 代办流程
				paramJson.remove("stateNo");
				paramJson.put("modelType", "notDone");
				paramJson.put("type", "generalProcess");
				RpcResponse<PageInfo<Map<String, Object>>> rpcResponse = simpleOrderQueryService
						.getSimpleOrderAgencyList(paramJson);
				if (rpcResponse.isSuccess()) {
					PageInfo<Map<String, Object>> pages = getOrderList(rpcResponse,
							paramJson.get("accessSecret").toString());
					return ResultBuilder.successResult(pages, rpcResponse.getMessage());
				}
				return ResultBuilder.failResult(rpcResponse.getMessage());

			} else if (Constant.OPTIONS_TABS.RUN_PROCESS.equals(state)) {// 已办流程
				paramJson.remove("stateNo");
				paramJson.put("modelType", "haveDone");
				paramJson.put("type", "generalProcess");
				RpcResponse<PageInfo<Map<String, Object>>> rpcResponse = simpleOrderQueryService
						.getSimpleOrderAgencyList(paramJson);
				if (rpcResponse.isSuccess()) {
					PageInfo<Map<String, Object>> pages = getOrderList(rpcResponse,
							paramJson.get("accessSecret").toString());
					return ResultBuilder.successResult(pages, rpcResponse.getMessage());
				}
				return ResultBuilder.failResult(rpcResponse.getMessage());
			}
			return ResultBuilder.failResult("状态参数不合法");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 封装一般流程工单列表 <method description>
	 *
	 * @param rpcResponse
	 * @return
	 * @throws Exception
	 */
	public PageInfo<Map<String, Object>> getOrderList(RpcResponse<PageInfo<Map<String, Object>>> rpcResponse,
			String accessSecret) throws Exception {
		PageInfo<Map<String, Object>> pages = rpcResponse.getSuccessValue();
		if (pages != null) {
			List<Map<String, Object>> orderList = pages.getList();
			List<Map<String, Object>> resultMap = Lists.newArrayList();
			// 查询流程状态描述集合
			RpcResponse<List<Map>> proStastes = simpleOrderTypeQueryService.findOrderState();
			if (proStastes.isSuccess()) {
				List<Map> proList = proStastes.getSuccessValue();
				for (Map<String, Object> orderMap : orderList) {
					Map<String, Object> resMap = Maps.newHashMap();
					resMap.put("id", orderMap.get("id"));
					resMap.put("serialNumber", orderMap.get("serialNumber"));
					resMap.put("processState", orderMap.get("processState"));
					resMap.put("createTime", orderMap.get("createTime"));
					resMap.put("orderName", orderMap.get("orderName"));
					resMap.put("processId", orderMap.get("processId"));
					for (Map proMap : proList) {
						if (orderMap.get("processState").equals(proMap.get("sign"))) {
							resMap.put("processName", proMap.get("name"));
						}
					}
					RpcResponse<List<SimpleOrderProcessType>> orderStateRpc = simpleOrderTypeQueryService
							.findOrderType();
					if (orderStateRpc.isSuccess() && orderStateRpc.getSuccessValue() != null) {
						List<SimpleOrderProcessType> orderStateList = orderStateRpc.getSuccessValue();
						for (SimpleOrderProcessType simple : orderStateList) {
							if (simple.getId().equals(orderMap.get("orderType").toString())) {
								resMap.put("orderType", simple.getName());
							}
						}
					}
					JSONArray array = findFacAndDevice((String)orderMap.get("id"), accessSecret);
					if (array != null && !array.isEmpty()) {
						resMap.put("mark", orderMap.get("createBy") + "-" + array.getJSONObject(0).getString("address") + "-共"
								+ array.size() + "个设施");
					} else {
						resMap.put("mark", orderMap.get("createBy") + "-" + "未查询到关联设施");
					}
					resultMap.add(resMap);
				}
				pages.setList(resultMap);
			}
		} else {
			pages = new PageInfo<>();
		}
		return pages;

	}



	/**
	 * 新建工单查询设施及设施下设备信息 <method description>
	 *
	 * @return
	 */
	public Result getFacitiesAll(String param) {
		try {
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("userId") || StringUtils.isBlank(jsonObject.getString("userId"))) {
				return ResultBuilder.noBusinessResult();
			}
			jsonObject.put("accessSecret", Constant.GETACCESSSECRET(jsonObject.getString("userId")));
			RpcResponse<PageInfo<Map<String, Object>>> factilitiesList = facilitiesQueryService
					.getFacilityListBySelectKeyAndTypeName(jsonObject);
			if (factilitiesList.isSuccess()) {
				PageInfo<Map<String, Object>> pages = factilitiesList.getSuccessValue();
				List<Map<String, Object>> resultList = Lists.newArrayList();
				List<Map<String, Object>> facList = factilitiesList.getSuccessValue().getList();
				for (Map<String, Object> map : facList) {
					Map<String, Object> facMap = Maps.newHashMap();
					facMap.put("facilitiesId", map.get("facilitiesId"));
					facMap.put("address", map.get("address"));
					facMap.put("facilityTypeAlias", map.get("facilityTypeAlias"));
					facMap.put("facilitiesCode", map.get("facilitiesCode"));
					RpcResponse<List<String>> dList = facilityDeviceQueryService
							.queryFacilityBindingState(map.get("facilitiesId").toString());
					List<String> successValue = dList.getSuccessValue();
					if (successValue == null || successValue.isEmpty()) {
						facMap.put("deviceInfo", null);
					} else {
						RpcResponse<List<Map<String, Object>>> getDeviceIdInfoByDeviceId = deviceQueryService
								.queryBatchDeviceInfoForDeviceIds(successValue);
						List<Map<String, Object>> deviceInfo = getDeviceIdInfoByDeviceId.getSuccessValue();
						if (deviceInfo != null && !deviceInfo.isEmpty()) {
							List<Map<String, Object>> devList = Lists.newArrayList();
							for (Map<String, Object> dMap : deviceInfo) {
								Map<String, Object> devInfo = new HashMap<>();
								devInfo.put("deviceId", dMap.get("deviceId"));
								devInfo.put("deviceName", dMap.get("deviceName"));
								devInfo.put("deviceTypeName", dMap.get("deviceTypeName"));
								devList.add(devInfo);
							}
							facMap.put("deviceInfo", devList);
						} else {
							facMap.put("deviceInfo", null);
						}
					}
					resultList.add(facMap);
				}
				pages.setList(resultList);
				return ResultBuilder.successResult(pages, factilitiesList.getMessage());
			} else {
				return ResultBuilder.failResult(factilitiesList.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 根据id查询一般流程详情 <method description>
	 *
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	public Result getSimpleOrderById(String param) {
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
			String accessSercet = Constant.GETACCESSSECRET(userJson.getString("_id"));
			String id = jsonObject.getString("id");
			String selectNo = jsonObject.getString("selectNo");
			RpcResponse<Map<String, Object>> rpcResponse = simpleOrderQueryService.findById(id);
			if (rpcResponse.isSuccess() && rpcResponse.getSuccessValue() != null
					&& !rpcResponse.getSuccessValue().isEmpty()) {
				// 查询流程状态描述集合
				RpcResponse<List<Map>> proStastes = simpleOrderTypeQueryService.findOrderState();
				Map<String, Object> sProcess = rpcResponse.getSuccessValue();
				JSONObject simpleJson = new JSONObject();
				simpleJson.put("serialNumber", sProcess.get("serialNumber"));
				simpleJson.put("id", sProcess.get("id"));
				simpleJson.put("processStartTime", sProcess.get("processStartTime"));
				simpleJson.put("processEndTime", sProcess.get("processEndTime"));
				simpleJson.put("phone", sProcess.get("phone"));
				simpleJson.put("deviceCount", sProcess.get("deviceCount"));
				//工单真实流程，主要用来展示已过期是否作废过的工单，值为8时，工单过期且已作废过
				String processRealState = (String) sProcess.get("processState");
				simpleJson.put("processRealState", processRealState);
				//APP展示工单流程
				String processState = (String) sProcess.get("processStateApp");
				if (proStastes.isSuccess()) {
					List<Map> list = proStastes.getSuccessValue();
					for (Map map : list) {
						if (map.get("sign").equals(processState)) {
							simpleJson.put("processMark", map.get("name").toString());
						}
					}
				}
				String operaType = commonService.getProcessState((String) sProcess.get("processId"));
				String button = "";
				if (Constant.OPTIONS_TABS.MY_PROCESS.equals(selectNo)) {// 我的流程页
					// 判断流程为待审核且未审核过
					if ("1".equals(processState) && "true".equals(operaType)) {
						// 审核、撤回和联系审核人按钮
						button = Constant.BUTTUN.BT_THREE;
					} else if ((StringUtils.equals("1", processState))&& "false".equals(operaType)) {// 判断流程为待审核且已被审核过
						// 审核和联系审核人
						button = Constant.BUTTUN.BT_FOUR;
					} else if ("4".equals(processState)) {// 判断流程为处理中
						// 完成按钮
						button = Constant.BUTTUN.BT_TWO;
					} else if (StringUtils.equals("7", processState) && !StringUtils.equals("8", processRealState)) {
						//作废按钮
						button = Constant.BUTTUN.BT_FIVE;
					} else if (StringUtils.equals("9", processState)) {
						//延时审核状态开锁和联系审核人
						button = Constant.BUTTUN.BT_SIX;
					}
				} else if (Constant.OPTIONS_TABS.BACKLOG_PROCESS.equals(selectNo)) {// 待办流程页
					// 判读流程状态为待审核且为登陆者审核人员
					if ((StringUtils.equals("1", processState) || StringUtils.equals("9", processState)) && !"".equals(userJson.getString("_id"))
							&& !userJson.getString("_id").equals(sProcess.get("userId"))) {
						// 审核
						button = Constant.BUTTUN.BT_ONE;
					} else if ("4".equals(processState) && !"".equals(userJson.getString("_id"))
							&& userJson.getString("_id").equals(sProcess.get("userId"))) {// 判断流程状态为处理中且登录者为申请人
						// 完成按钮
						button = Constant.BUTTUN.BT_TWO;
					}
				}
				simpleJson.put("button", button);
				simpleJson.put("processState", processState);
				simpleJson.put("processId", sProcess.get("processId"));
				simpleJson.put("createBy", sProcess.get("createBy"));
				simpleJson.put("orderImg", sProcess.get("orderImg"));
				// 当工单状态为被拒绝时，查询工单被拒绝原因
				// if
				// (Constant.OPTIONS_TABS.BACKLOG_PROCESS.equals(processState))
				// {
				JSONObject nodeJSON = new JSONObject();
				nodeJSON.put("id", id);
				nodeJSON.put("userId", "123");
				nodeJSON.put("processId", sProcess.get("processId"));
				nodeJSON.put("accessSecret", accessSercet);
				RpcResponse<Map<String, Object>> nodeRpc = simpleOrderQueryService.getOrderNodeDetails(nodeJSON);
				if (!nodeRpc.isSuccess() || nodeRpc.getSuccessValue() == null) {
					simpleJson.put("detail", "无流程进度信息");
				} else {
					Map<String, Object> nodeList = nodeRpc.getSuccessValue();
					List<Map<String, Object>> nodeValue = (List<Map<String, Object>>) nodeList.get("hisExcuList");
					for (Map<String, Object> map : nodeValue) {
						Map<String, Object> obj = (Map<String, Object>) map.get("variable");
						if (obj.get("detail") != null && !"".equals(obj.get("detail"))) {
							simpleJson.put("detail", obj.get("detail"));
						}
					}
				}
				/*
				 * } else { simpleJson.put("detail", ""); }
				 */

				// 查询工单类型集合
				RpcResponse<List<SimpleOrderProcessType>> orderStateRpc = simpleOrderTypeQueryService.findOrderType();
				if (orderStateRpc.isSuccess() && orderStateRpc.getSuccessValue() != null) {
					List<SimpleOrderProcessType> orderStateList = orderStateRpc.getSuccessValue();
					for (SimpleOrderProcessType simple : orderStateList) {
						if (simple.getId().equals(String.valueOf(sProcess.get("orderType")))) {
							simpleJson.put("orderType", simple.getName());
						}
					}
				}

				RpcResponse<Map> userInfo = userBaseQueryService.getUserByUserId((String) sProcess.get("userId"));
				if (!userInfo.isSuccess() || userInfo.getSuccessValue() == null) {
					simpleJson.put("userName", "");
				} else {
					Map userMap = userInfo.getSuccessValue();
					if (!userMap.containsKey("userName")) {
						simpleJson.put("userName", userMap.get("loginAccount"));
					} else {
						simpleJson.put("userName", userMap.get("userName"));
					}
				}
				simpleJson.put("manager", sProcess.get("manager"));
				JSONArray array = findFacAndDevice(id, accessSercet);
				if(array != null && !array.isEmpty()) {
					simpleJson.put("mark", simpleJson.get("userName") + "-" + array.getJSONObject(0).getString("address") + "-共"
							+ array.size() + "个设施");
					simpleJson.put("list", array);
				}else {
					simpleJson.put("mark", simpleJson.get("userName") + "-null-null");
				}
				
				// 查询工单设施
//				RpcResponse<PageInfo<Map<String, Object>>> rpcResponseFac = findFacAndDevice(id, accessSercet);
//				if (rpcResponseFac.isSuccess() && rpcResponseFac.getSuccessValue() != null) {
//					List<Map<String, Object>> pages = rpcResponseFac.getSuccessValue().getList();
//					List<Map<String, Object>> facList = Lists.newArrayList();
//					if (!pages.isEmpty() && pages.size() != 0) {
//						simpleJson.put("mark", simpleJson.get("userName") + "-" + pages.get(0).get("address") + "-共"
//								+ pages.size() + "个设施");
//						// 封装设施及下挂设备id集合
//						for (Map<String, Object> facMap : pages) {
//							Map<String, Object> maps = Maps.newHashMap();
//							boolean bl = true;
//							for (Map<String, Object> map : facList) {
//								if (map.values().contains(facMap.get("facilitiesCode"))) {
//									bl = false;
//								}
//							}
//							if (bl) {
//								maps.put("facilityTypeName", facMap.get("facilityTypeName"));
//								maps.put("facilitiesCode", facMap.get("facilitiesCode"));
//								maps.put("facilitiesId", facMap.get("facilityId"));
//								maps.put("address", facMap.get("address"));
//								facList.add(maps);
//							}
//
//						}
//						for (Map<String, Object> facMap : pages) {
//							for (Map<String, Object> map : facList) {
//								if (facMap.get("facilitiesCode").equals(map.get("facilitiesCode"))) {
//									if (map.get("deviceId") != null && !map.get("deviceId").equals("")) {
//										map.put("deviceId", map.get("deviceId") + "," + facMap.get("deviceId"));
//									} else {
//										map.put("deviceId", facMap.get("deviceId"));
//									}
//								}
//							}
//						}
//						List<Map<String, Object>> resultMap = getFacilitiesDevices(facList, accessSercet);
//						simpleJson.put("list", resultMap);
//					} else {
//						simpleJson.put("mark", simpleJson.get("userName") + "-null-null");
//					}
//					return ResultBuilder.successResult(simpleJson, rpcResponseFac.getMessage());
//				}
//				simpleJson.put("list", null);
				return ResultBuilder.successResult(simpleJson, "查询成功");
			}
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (

		Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 查询工单下关联设施信息 <method description>
	 *
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public JSONArray findFacAndDevice(String orderId, String accessSecret) throws Exception {
		// 查询设施信息集合
		JSONObject faciJson = new JSONObject();
		faciJson.put("pageSize", 100);
		faciJson.put("pageNum", 1);
		faciJson.put("manageState", "enable");
		faciJson.put("binding", "bound");
		faciJson.put("accessSecret", accessSecret);
		faciJson.put("simplerOrFacId", orderId);
		String resultHttp = commonService.requestRest(Constant.LOCMAN_PORT, "order/simpleOrder/findFacInfo",
				faciJson.toJSONString());
		Map<String, String> map = commonService.checkResult(JSONObject.parseObject(resultHttp));
		if (map.containsKey("success")) {
			JSONObject value = JSONObject.parseObject(map.get("success"));
			if (value != null) {
				JSONArray array = (JSONArray) value.get("list");
				return array;
			}
		}
		return null;
	}



	/**
	 * 查询设施下设备及属性点 <method description>
	 *
	 * @param resultMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getFacilitiesDevices(List<Map<String, Object>> FacList, String accessSecret)
			throws Exception {
		List<Map<String, Object>> resultList = Lists.newArrayList();
		for (Map<String, Object> map : FacList) {
			Map<String, Object> mapObj = Maps.newHashMap();
			mapObj.putAll(map);
			List<String> ids = Lists.newArrayList();
			ids.add(mapObj.get("deviceId").toString());
			List<Map> deviceInfo = deviceAppQueryService.getDeviceInfoByIds(ids, accessSecret);
			mapObj.put("deviceInfo", deviceInfo);
			mapObj.remove("deviceId");
			resultList.add(mapObj);
		}
		return resultList;

	}



	/**
	 * 获取审批人电话 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Result<List<Map<String, Object>>> getReviewedUserPhone(String param) {
		try {

			if (ParamChecker.isBlank(param) || ParamChecker.isNotMatchJson(param)) {
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(param);
			if (!json.containsKey("userId")) {
				return ResultBuilder.noBusinessResult();
			}
			json.put("accessSecret", Constant.GETACCESSSECRET(json.getString("userId")));
			RpcResponse<Map<String, Object>> result = simpleOrderQueryService.getOrderNodeDetails(json);
			if (!result.isSuccess() || result.getSuccessValue() == null
					|| !result.getSuccessValue().containsKey("hisExcuList")) {
				return ResultBuilder.failResult("审批人查询失败");
			}
			List<Map<String, Object>> resultList = Lists.newArrayList();
			List<Map<String, Object>> listObject = (List<Map<String, Object>>) result.getSuccessValue()
					.get("hisExcuList");
			List<String> ids = Lists.newArrayList();
			if (listObject != null && !listObject.isEmpty()) {
				for (Map<String, Object> map : listObject) {
					if (map.containsKey("status")) {
						String status = map.get("status").toString();
						if(StringUtils.equals("审核中", status)) {
							ids = (List<String>) map.get("excuUser");
						}
					}
				}
			}
			if (ids != null) {
				for (String id : ids) {
					Map<String, Object> userMap = Maps.newHashMap();
					RpcResponse userRpc = userBaseQueryService.getUserByUserId(id);
					if (userRpc.isSuccess() && userRpc.getSuccessValue() != null) {
						JSONObject jsonObject = JSONObject.parseObject(userRpc.getSuccessValue().toString());
						userMap.put("userId", jsonObject.get("_id"));
						userMap.put("userName", jsonObject.get("userName"));
						userMap.put("mobile", jsonObject.get("mobile"));
					}
					resultList.add(userMap);
				}
				return ResultBuilder.successResult(resultList, "审批人联系方式查询成功");
			}
			return ResultBuilder.failResult("审批人联系方式查询失败");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}

}
