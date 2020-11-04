package com.locman.app.query.service;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.util.Constant;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.BalanceSwitchStateRecord;
import com.run.locman.api.query.service.AlarmOrderQueryService;
import com.run.locman.api.query.service.BalanceSwitchStateRecordQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.constants.CommonConstants;

@Service
public class UnlockApplyQueryService extends BaseAppController {
	private static final Logger						logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	SimpleOrderQueryService							simpleOrderQueryService;

	@Autowired
	UscQueryService									uscQueryService;

	@Autowired
	DeviceQueryService								deviceQueryService;

	@Autowired
	private BalanceSwitchStateRecordQueryService	balanceSwitchStateRecordQueryService;

	@Autowired
	private AlarmOrderQueryService					alarmOrderQueryService;



	/**
	 * 查询设备在线状态 <method description>
	 *
	 * @param devId
	 * @return
	 */
	public Result<String> getDeviceOnlineState(String devId) {
		try {
			if (StringUtils.isBlank(devId)) {
				return ResultBuilder.noBusinessResult();
			}
			RpcResponse<Map<String, Object>> stateRpc = deviceQueryService.queryDeviceBindingState(devId);
			if (!stateRpc.isSuccess() || stateRpc.getSuccessValue() == null) {
				return ResultBuilder.failResult("数据查询异常");
			}
			Map<String, Object> devMap = stateRpc.getSuccessValue();
			String deviceOnlineStatusValue = devMap.get("deviceOnlineStatus").toString();
			String deviceOnlineStatus = "未知";
			if (deviceOnlineStatusValue.equals("online")) {
				deviceOnlineStatus = "在线";
			} else if (deviceOnlineStatusValue.equals("offline")) {
				deviceOnlineStatus = "离线";
			}
			return ResultBuilder.successResult(deviceOnlineStatus, stateRpc.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 验证开锁工单状态 <method description>
	 *
	 * @param orderId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result<Boolean> checkUnlockOrder(String orderId, String param) {
		try {
			if (StringUtils.isBlank(orderId)) {
				return ResultBuilder.noBusinessResult();
			}
			Result<?> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("orderType")) {
				return ResultBuilder.noBusinessResult();
			}
			String orderType = jsonObject.getString("orderType");
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			if (Constant.ORDERTYPE.UNLOCK.equals(orderType)) {
				RpcResponse<Map<String, Object>> rpcResponse = simpleOrderQueryService.findById(orderId);
				if (!rpcResponse.isSuccess() || rpcResponse.getSuccessValue() == null
						|| rpcResponse.getSuccessValue().isEmpty()) {
					return ResultBuilder.failResult("工单不存在");
				}
				Map<String, Object> simpleOrderMap = rpcResponse.getSuccessValue();
				System.out.println(simpleOrderMap);
				Date now = getNowDate();
				if (DateUtils.toDate(simpleOrderMap.get("processStartTime").toString()).compareTo(now) == 1
						|| DateUtils.toDate(simpleOrderMap.get("processEndTime").toString()).compareTo(now) == -1) {
					return ResultBuilder.failResult("当前时间不在工单时间范围内");
				}
				JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
				if (!simpleOrderMap.get("userId").equals(userJson.get("_id"))) {
					return ResultBuilder.failResult("没有操作权限");
				}
				if("5".equals(simpleOrderMap.get("processStateApp"))) {
					return ResultBuilder.failResult("工单状态已改变，请刷新重试！");
				}
				if("7".equals(simpleOrderMap.get("processStateApp"))) {
					return ResultBuilder.failResult("工单已过期，请刷新重试！");
				}
				return ResultBuilder.successResult(true, rpcResponse.getMessage());
			} else if (Constant.ORDERTYPE.ALARM.equals(orderType)) {
				RpcResponse<Map<String, Object>> rpcResponse = alarmOrderQueryService.getAlarmOrderInfoById(orderId);
				if(rpcResponse == null || !rpcResponse.isSuccess() || rpcResponse.getSuccessValue() == null) {
					return ResultBuilder.failResult("工单不存在");
				}
				Map<String, Object> orderMap = rpcResponse.getSuccessValue();
				logger.info("告警工单开锁验证 ==》 "+ orderMap);
				String commandFlag = orderMap.get("commandFlag")==null ? "false" :orderMap.get("commandFlag").toString();
				if("true".equals(commandFlag) && (orderMap.containsKey("processState")&& "0".equals(orderMap.get("processState")))){
					return ResultBuilder.successResult(true, rpcResponse.getMessage());
				}
				return ResultBuilder.failResult("开锁时间已过期！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
		return null;
	}



	/**
	 * 获取平衡告警开启状态 <method description>
	 *
	 * @param param
	 * @return
	 */
	public Result<?> getBalanceState(String param) {
		try {
			Result<?> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject parseJson = JSONObject.parseObject(param);
			if (!parseJson.containsKey("deviceId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!parseJson.containsKey("facilityId")) {
				return ResultBuilder.noBusinessResult();
			}
			String token = request.getHeader("token");
			String userId = uscQueryService.findUserIdByToken(token);
			String accessSercret = Constant.GETACCESSSECRET(userId);
			parseJson.put("accessSecret", accessSercret);
			RpcResponse<BalanceSwitchStateRecord> response = balanceSwitchStateRecordQueryService.getState(parseJson);
			if (response != null && response.isSuccess()) {
				return ResultBuilder.successResult(response.getSuccessValue(), response.getMessage());
			}
			return ResultBuilder.failResult(response.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}
}
