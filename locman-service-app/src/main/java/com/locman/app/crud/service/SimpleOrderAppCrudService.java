package com.locman.app.crud.service;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.*;
import com.locman.app.query.service.UscQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.SimpleOrderCrudService;
import com.run.locman.constants.CommonConstants;
import com.run.usc.base.query.TenAccBaseQueryService;
import com.run.usc.base.query.UserBaseQueryService;

@Service
public class SimpleOrderAppCrudService extends BaseAppController {
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	public SimpleOrderCrudService	simpleOrderCrudService;

	@Autowired
	private UserBaseQueryService	userBaseQueryService;

	@Autowired
	TenAccBaseQueryService			tenAccBaseQueryService;

	@Autowired
	private UscQueryService			uscQueryService;

	@Autowired
	CommonService					commonService;



	/**
	 * 添加一般流程 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Result<String> saveSimpleOrder(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject simpleInfo = JSONObject.parseObject(param);
			if (!simpleInfo.containsKey("userId")) {
				return ResultBuilder.noBusinessResult();
			}
			String userId = simpleInfo.getString("userId");
			String accessSecret = Constant.GETACCESSSECRET(userId);
			simpleInfo.put("type", "add");
			simpleInfo.put("id", "");
			simpleInfo.put("processId", "");
			simpleInfo.put("mark", simpleInfo.get("orderName"));
			simpleInfo.put("deviceIdsDel", "");
			simpleInfo.put("accessSecret", accessSecret);
			simpleInfo.put("remindRule", "default");
			simpleInfo.put("remindTime", "");
			String urls = simpleInfo.get("orderImg").toString();
			String orderImg = "";
			if (urls != null && !urls.equals("")) {
				String[] strArr = urls.split(",");
				ArrayList<String> strUrl = Lists.newArrayList();
				for (String str : strArr) {
					strUrl.add("\"" + str.toString() + "\"");
				}
				orderImg = strUrl.toString();
			}
			simpleInfo.put("orderImg", orderImg);
			RpcResponse<Map<String, Object>> userMap = userBaseQueryService.getUserByUserId(userId);
			if (!userMap.isSuccess() || userMap.getSuccessValue() == null) {
				return ResultBuilder.failResult("用户不存在");
			}
			Map<String, Object> map = userMap.getSuccessValue();
			if (map.get("userName") != null && !map.get("userName").equals("")) {
				simpleInfo.put("manager", map.get("userName"));
			} else {
				simpleInfo.put("manager", map.get("loginAccount"));
			}
			simpleInfo.put("phone", map.get("mobile"));
			RpcResponse<Map<String, Object>> accRpc = tenAccBaseQueryService.getTenmentAccBySecret(accessSecret);
			if (!accRpc.isSuccess() || accRpc.getSuccessValue() == null || accRpc.getSuccessValue().isEmpty()) {
				simpleInfo.put("constructBy", "");
			} else {
				Map<String, Object> accMap = accRpc.getSuccessValue();
				simpleInfo.put("constructBy", accMap.get("accessTenementName"));
			}
			logger.info("param==> " + simpleInfo.toJSONString());
			String url = "order/simpleOrder/addOrUpdateSimpleOrder";
			String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, simpleInfo.toJSONString());
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
	 * 处理一般流程 <method description>
	 *
	 * @param param
	 *            orderId 工单id operationType 操作类型 值为:0-撤回、1-通过、2-拒绝、3-完成
	 *            processId 流程id userId 用户id detail 拒绝或完工时提交的信息
	 * @return
	 */
	public Result<String> simpleOrderHandle(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject orderJson = JSONObject.parseObject(param);
			if (!orderJson.containsKey("userId")) {
				return ResultBuilder.noBusinessResult();
			}
			String accessSecret = Constant.GETACCESSSECRET(orderJson.getString("userId"));
			orderJson.put("accessSecret", accessSecret);
			if (StringUtils.isBlank(orderJson.getString("operationType"))) {
				return ResultBuilder.emptyResult();
			}
			String opearType = orderJson.getString("operationType");
			if (!StringUtils.equals(opearType, Constant.BUTTUN.BT_FIVE)) {// 对工单进行非作废的操作
				orderJson.remove("operationType");
				orderJson.put("operationType", Constant.SIMOPERATIONTYPE.TYPEMAP.get(opearType));
				logger.info("param==> " + orderJson.toJSONString());
				String url = "order/simpleOrder/updateSimpleOrderState";
				String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, orderJson.toJSONString());
				Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
				String value = "";
				if (resultValue.containsKey("success")) {
					value = resultValue.get("success");
					return ResultBuilder.successResult(value, "操作成功");
				} else {
					value = resultValue.get("error");
					return ResultBuilder.failResult(value);
				}
			} else {// 作废工单
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("simpleOrderId", orderJson.getString("id"));
				jsonObject.put("processId", orderJson.getString("processId"));
				jsonObject.put("userId", orderJson.getString("userId"));
				jsonObject.put("accessSecret", accessSecret);
				logger.info("param==> " + jsonObject.toJSONString());
				String url = "order/simpleOrder/invalidateSimpleOrder";
				String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, jsonObject.toJSONString());
				Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
				String value = "";
				if (resultValue.containsKey("success")) {
					value = resultValue.get("success");
					return ResultBuilder.successResult(value, "操作成功");
				} else {
					value = resultValue.get("error");
					return ResultBuilder.failResult(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 工单延时 <method description>
	 *
	 * @param param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result<String> delayedSimpleOrder(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject orderJson = JSONObject.parseObject(param);
			if (!orderJson.containsKey("simpleOrderId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!orderJson.containsKey("processId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!orderJson.containsKey("hours") || StringUtils.isEmpty(orderJson.getString("hours"))) {
				orderJson.put("hours", "1");
			}
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			String accessSecret = Constant.GETACCESSSECRET(userId);
			orderJson.put("userId", userId);
			orderJson.put("accessSecret", accessSecret);
			String url = "order/simpleOrder/delayedSimpleOrder";
			String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, orderJson.toJSONString());
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
}
