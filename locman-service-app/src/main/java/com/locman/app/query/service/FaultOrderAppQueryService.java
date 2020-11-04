package com.locman.app.query.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
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
import com.run.locman.api.entity.FaultOrderProcessState;
import com.run.locman.api.query.service.FaultOrderProcessStateQueryService;

@Service
public class FaultOrderAppQueryService extends BaseAppController {

	@Autowired
	private CommonService						commonService;

	@Autowired
	private UscQueryService						uscQueryService;

	@Autowired
	private FaultOrderProcessStateQueryService	faultOrderProcessStateQueryService;



	/**
	 * 查询故障工单列表 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result findFaultOrderList(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject parseJson = JSONObject.parseObject(param);
			if (!parseJson.containsKey("stateNo")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("pageSize")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("pageNo")) {
				return ResultBuilder.noBusinessResult();
			}
			String stateNo = parseJson.getString("stateNo");
			if (StringUtils.isBlank(stateNo)) {
				return ResultBuilder.failResult("未选择查询项");
			}
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			String accessSecret = Constant.GETACCESSSECRET(userId);
			JSONObject paramJson = new JSONObject();
			paramJson.put("userId", userId);
			paramJson.put("accessSecret", accessSecret);
			paramJson.put("appointStartTime", "");
			paramJson.put("appointEndTime", "");
			paramJson.put("faultType", "");
			paramJson.put("pageSize", parseJson.getString("pageSize"));
			paramJson.put("pageNo", parseJson.getString("pageNo"));
			paramJson.put("selectKey", parseJson.containsKey("selectKey") ? parseJson.getString("selectKey") : "");
			paramJson.put("processState", parseJson.containsKey("orderState") ? parseJson.getString("orderState") : "");
			String resultHttp = "";
			if (Constant.OPTIONS_TABS.MY_PROCESS.equals(stateNo)) {
				resultHttp = commonService.requestRest(Constant.LOCMAN_PORT, "order/faultOrder/getFaultOrderList",
						paramJson.toJSONString());
			} else if (Constant.OPTIONS_TABS.BACKLOG_PROCESS.equals(stateNo)) {
				paramJson.put("modelType", "notDone");
				paramJson.put("processId", "");
				paramJson.put("appOrderId", parseJson.getString("appOrderId"));
				resultHttp = commonService.requestRest(Constant.LOCMAN_PORT,
						"order/faultOrder/queryAgendaOrProcessFaultOrderList", paramJson.toJSONString());
			} else if (Constant.OPTIONS_TABS.RUN_PROCESS.equals(stateNo)) {
				paramJson.put("modelType", "haveDone");
				paramJson.put("processId", "");
				resultHttp = commonService.requestRest(Constant.LOCMAN_PORT,
						"order/faultOrder/queryAgendaOrProcessFaultOrderList", paramJson.toJSONString());
			} else {
				return ResultBuilder.failResult("参数错误");
			}
			Map<String, String> valueMap = commonService.checkResult(JSONObject.parseObject(resultHttp));
			if (valueMap.containsKey("success")) {
				String value = valueMap.get("success");
				JSONObject valueJson = JSONObject.parseObject(value);
				List<Map<String, Object>> orderList = (List<Map<String, Object>>) valueJson.get("list");
				List<Map<String, Object>> orders = Lists.newArrayList();
				if (orderList != null && !orderList.isEmpty()) {
					for (Map<String, Object> map : orderList) {
						Map<String, Object> obj = map;
						Map<String, Object> fac = findOrderFac(accessSecret, (String) map.get("factoryId"),
								(String) map.get("id"));
						obj.put("facilitiesInfo", fac);
						orders.add(obj);
					}
				}
				valueJson.put("list", orders);
				return ResultBuilder.successResult(valueJson, "查询成功");
			} else {
				return ResultBuilder.failResult(valueMap.get("error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 查询故障设施 <method description>
	 *
	 * @param accessSecret
	 * @param factoryId
	 * @param orderId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findOrderFac(String accessSecret, String factoryId, String orderId) {
		Map<String, Object> objMap = Maps.newHashMap();
		JSONObject tempJSON = new JSONObject();

		tempJSON.put("accessSecret", accessSecret);
		tempJSON.put("binding", "bound");
		tempJSON.put("facilitiesTypeId", "");
		tempJSON.put("factoryId", factoryId);
		tempJSON.put("faultOrderId", orderId);
		tempJSON.put("pageNo", 1);
		tempJSON.put("pageSize", 1);
		tempJSON.put("selectKey", "");
		tempJSON.put("type", "update");
		String faciResult = commonService.requestRest(Constant.LOCMAN_PORT,
				"order/faultOrder/queryDevicesForFaultOrder", tempJSON.toJSONString());
		Map<String, String> facMap = commonService.checkResult(JSONObject.parseObject(faciResult));

		if (facMap.containsKey("success")) {
			JSONObject facJSON = JSONObject.parseObject(facMap.get("success"));
			JSONArray list = (JSONArray) facJSON.get("list");
			if (list != null && !list.isEmpty()) {
				objMap = (Map<String, Object>) list.get(0);
			}
		}
		return objMap;
	}



	/**
	 * 根据工单id查询工单详情 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result findOrderById(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject parseJson = JSONObject.parseObject(param);
			if (!parseJson.containsKey("stateNo")) {
				return ResultBuilder.noBusinessResult();
			}
			String stateNo = parseJson.getString("stateNo");
			if (!parseJson.containsKey("id")) {
				return ResultBuilder.noBusinessResult();
			}
			String id = parseJson.getString("id");
			if (StringUtils.isBlank(id)) {
				return ResultBuilder.failResult("工单id不能为空");
			}
			if (StringUtils.isBlank(stateNo)) {
				return ResultBuilder.failResult("未选择操作选项");
			}
			JSONObject resultJSON = new JSONObject();
			JSONObject orderJSON = new JSONObject();
			orderJSON.put("id", id);
			String resutHttp = commonService.requestRest(Constant.LOCMAN_PORT,
					"order/faultOrder/queryFaultOrderInfoById", orderJSON.toJSONString());
			Map<String, String> valueMap = commonService.checkResult(JSONObject.parseObject(resutHttp));
			if (valueMap.containsKey("success")) {
				String value = valueMap.get("success");
				resultJSON = JSONObject.parseObject(value);
				if (resultJSON == null || resultJSON.isEmpty()) {
					return ResultBuilder.failResult("工单查询失败");
				}
				String token = request.getHeader("token");
				RpcResponse userRpc = uscQueryService.getUserByToken(token);
				if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
					return null;
				}
				JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
				String userId = userJson.getString("_id");
				String accessSecret = Constant.GETACCESSSECRET(userId);
				Map<String, Object> map = findOrderFac(accessSecret, resultJSON.getString("factoryId"),
						resultJSON.getString("id"));
				resultJSON.put("facilitiesInfo", map);
				String processState = resultJSON.getString("processState");
				String orderUserId = resultJSON.getString("userId");
				resultJSON.put("button", "");
				if (Constant.OPTIONS_TABS.MY_PROCESS.equals(stateNo)) {
					if ("1".equals(processState)) {
						resultJSON.put("button", Constant.BUTTUN.BT_ROLLBACK);
					} else if ("4".equals(processState)) {
						resultJSON.put("button", Constant.BUTTUN.BT_COMPLETE);
					}
				} else if (Constant.OPTIONS_TABS.BACKLOG_PROCESS.equals(stateNo)) {
					if ("1".equals(processState)) {
						resultJSON.put("button", Constant.BUTTUN.BT_APPROVE);
					} else if ("4".equals(processState)) {
						resultJSON.put("button", Constant.BUTTUN.BT_COMPLETE);
					} else if (("3".equals(processState) || "5".equals(processState)) && !StringUtils.isEquals(orderUserId, userId)) {
						resultJSON.put("button", Constant.BUTTUN.BT_CONFIRM);
					}
				}
				resultJSON.put("detail", "");
				JSONObject nodeJSON = new JSONObject();
				nodeJSON.put("accessSecret", accessSecret);
				nodeJSON.put("id", resultJSON.getString("id"));
				nodeJSON.put("processId", resultJSON.getString("processId"));
				nodeJSON.put("userId", userId);
				String node = commonService.requestRest(Constant.LOCMAN_PORT, "order/simpleOrder/getOrderNodeDetails",
						nodeJSON.toJSONString());
				Map<String, String> nodeMap = commonService.checkResult(JSONObject.parseObject(node));
				if (nodeMap.containsKey("success")) {
					String valuestr = nodeMap.get("success");
					JSONObject jsonObject = JSONObject.parseObject(valuestr);
					JSONArray nodeArray = (JSONArray) jsonObject.get("hisExcuList");
					if ("0".equals(processState) || "2".equals(processState) || "6".equals(processState)) {
						JSONObject temp = (JSONObject) nodeArray.get(nodeArray.size() - 1);
						if (temp.containsKey("variable")) {
							String variable = temp.getString("variable");
							if (!"{}".equals(variable)) {
								JSONObject variableJson = JSONObject.parseObject(variable);
								resultJSON.put("detail", variableJson.getString("detail"));
							}
						}
					} else {
						JSONObject temp = (JSONObject) nodeArray.get(nodeArray.size() - 2);
						if (temp.containsKey("variable")) {
							String variable = temp.getString("variable");
							if (!"{}".equals(variable)) {
								JSONObject variableJson = JSONObject.parseObject(variable);
								resultJSON.put("detail", variableJson.getString("detail"));
							}
						}
					}
				}
				resultJSON.put("faultOrderProcessStateName", "");
				List<FaultOrderProcessState> faultOrderProcessStates = getFaultOrderProState();
				if (faultOrderProcessStates != null && !faultOrderProcessStates.isEmpty()) {
					String state = resultJSON.getString("processState");
					for (FaultOrderProcessState faultState : faultOrderProcessStates) {
						if (!"".equals(state) && state.equals(faultState.getSign())) {
							resultJSON.put("faultOrderProcessStateName", faultState.getName());
						}
					}
				}
				return ResultBuilder.successResult(resultJSON, "查询成功");
			}
			return ResultBuilder.failResult(valueMap.get("error"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 获取故障工单流程状态 <method description>
	 *
	 * @return
	 */
	public List<FaultOrderProcessState> getFaultOrderProState() {
		List<FaultOrderProcessState> list = Lists.newArrayList();
		RpcResponse<List<FaultOrderProcessState>> rpcResponse = faultOrderProcessStateQueryService
				.getFaultOrderStateList();
		if (rpcResponse != null && rpcResponse.isSuccess() && !rpcResponse.getSuccessValue().isEmpty()) {
			list = rpcResponse.getSuccessValue();
		}
		return list;
	}
}
