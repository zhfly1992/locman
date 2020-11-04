package com.locman.app.crud.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.locman.app.query.service.UscQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.Factory;
import com.run.locman.api.query.service.FactoryQueryService;

@Service
public class FaultOrderAppCrudService extends BaseAppController {

	@Autowired
	private CommonService		commonService;

	@Autowired
	private UscQueryService		uscQueryService;

	@Autowired
	private FactoryQueryService	factoryQueryService;



	/**
	 * 新增故障工单 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Result<String> saveOrder(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("deviceIdsAdd")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("faultType")) {
				return ResultBuilder.noBusinessResult();
			}
			String faultType = jsonObject.getString("faultType");
			if (!jsonObject.containsKey("deviceCount")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("organizeId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("faultProcessType")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("mark")) {
				return ResultBuilder.noBusinessResult();
			}
			Result typeResult = commonService.getCommonValues(Constant.PROCESSTYPENAME.FAULT_TYPE_NAME);
			JSONArray array = JSONObject
					.parseArray(typeResult.getValue() == null ? "" : typeResult.getValue().toString());
			for (Object object : array) {
				JSONObject orderType = (JSONObject) object;
				if (faultType.equals(orderType.get("id"))) {
					jsonObject.put("orderName", orderType.get("name"));
				}
			}
			jsonObject.put("type", "add");
			jsonObject.put("deviceIdsDel", Lists.newArrayList());
			jsonObject.put("processId", "");
			jsonObject.put("id", "");

			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			String userName = userJson.getString("userName");
			String phone = userJson.getString("mobile");
			jsonObject.put("userId", userId);
			jsonObject.put("manager", userName);
			jsonObject.put("phone", phone);
			String accessSecret = Constant.GETACCESSSECRET(userId);
			jsonObject.put("accessSecret", accessSecret);
			List<String> deviceIds = (List<String>) jsonObject.get("deviceIdsAdd");
			String deviceId = "";
			if (deviceIds != null && !deviceIds.isEmpty()) {
				deviceId = deviceIds.get(0) == null || "".equals(deviceIds.get(0)) ? "" : deviceIds.get(0);
			}
			RpcResponse<Factory> response = factoryQueryService.findFactoryByDeviceId(deviceId, accessSecret);
			if (!response.isSuccess() || response.getSuccessValue() == null) {
				return ResultBuilder.failResult("厂家信息查询失败");
			}
			Factory factory = response.getSuccessValue();
			jsonObject.put("factoryId", factory.getId());
			String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT,
					"order/faultOrder/addOrUpdateFaultOrder", jsonObject.toJSONString());
			Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
			String value = "";
			if (resultValue.containsKey("success")) {
				value = resultValue.get("success");
				return ResultBuilder.successResult(value, "操作成功");
			} else {
				value = resultValue.get("error");
				return ResultBuilder.failResult(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 处理告警工单 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result<String> faultOrderHandle(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject paramJson = JSONObject.parseObject(param);
			if(!paramJson.containsKey("id")) {
				return ResultBuilder.noBusinessResult();
			}
			if(!paramJson.containsKey("operationType")) {
				return ResultBuilder.noBusinessResult();
			}
			if(!paramJson.containsKey("processId")) {
				return ResultBuilder.noBusinessResult();
			}
			String operationType = paramJson.getString("operationType");
			paramJson.put("operationType", Constant.SIMOPERATIONTYPE.TYPEMAP.get(operationType));
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			String accessSecret = Constant.GETACCESSSECRET(userId);
			paramJson.put("userId", userId);
			paramJson.put("accessSecret", accessSecret);
			String handResut = commonService.requestRest(Constant.LOCMAN_PORT, "order/faultOrder/updateFaultOrderState", paramJson.toJSONString());
			Map<String, String> resultMap = commonService.checkResult(JSONObject.parseObject(handResut));
			if(resultMap.containsKey("success")) {
				String value = resultMap.get("success");
				return ResultBuilder.successResult(value, "工单处理成功");
			}
			return ResultBuilder.failResult(resultMap.get("error"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}

}
