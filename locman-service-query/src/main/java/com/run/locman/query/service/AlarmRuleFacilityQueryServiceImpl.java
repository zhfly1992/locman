/*
 * File name: AlarmRuleFacilityQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 dingrunkang 2018年1月18日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.AlarmRuleFacilityQueryRepository;
import com.run.locman.api.query.service.AlarmRuleFacilityQueryService;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderConstants;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.RuleContants;
import com.run.locman.constants.SimpleOrderConstants;

/**
 * @Description:自定义告警规则配置信息查询
 * @author: dingrunkang
 * @version: 3.0, 2017年12月4日
 */

public class AlarmRuleFacilityQueryServiceImpl implements AlarmRuleFacilityQueryService {
	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);


	@Autowired
	private AlarmRuleFacilityQueryRepository	alarmRuleFacilityQueryRepository;



	/**
	 * @see com.run.locman.api.query.service.AlarmRuleFacilityQueryService#getFacilityDeviceList(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getFacilityDeviceList(JSONObject paramInfo) throws Exception {
		logger.info(String.format("[getFacilityDeviceList()方法执行开始...,参数：【%s】]", paramInfo));
		try {
			if (paramInfo == null) {
				logger.error("[getAlarmOrderBypage()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_SIZE))) {
				logger.error("[getAlarmOrderBypage()->invalid：页大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页大小必须为数字！");
			}
			if (!paramInfo.containsKey(AlarmInfoConstants.PAGE_NO)) {
				logger.error("[getAlarmOrderBypage()->invalid：页码不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码不能为空！");
			}
			if (!StringUtils.isNumeric(paramInfo.getString(AlarmInfoConstants.PAGE_NO))) {
				logger.error("[getAlarmOrderBypage()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (StringUtils.isBlank(paramInfo.getString(AlarmInfoConstants.USC_ACCESS_SECRET))) {
				logger.error("[getAlarmOrderBypage()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(paramInfo.getString(SimpleOrderConstants.TYPE))) {
				logger.error("[getAlarmOrderBypage()->invalid：添加还是修改类型不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("添加还是修改类型不能为空!");
			}

			Map map = new HashMap<>(16);
			map.put(SimpleOrderConstants.ACCESSSECRET, paramInfo.getString(SimpleOrderConstants.ACCESSSECRET));
			map.put(SimpleOrderConstants.FACILITIESTYPEID, paramInfo.getString(SimpleOrderConstants.FACILITIESTYPEID));
			map.put("deviceTypeId", paramInfo.getString("deviceTypeId"));
			map.put(SimpleOrderConstants.SELECTKEY, paramInfo.getString(SimpleOrderConstants.SELECTKEY));
			// 若新增查询所有
			

			if (paramInfo.getString(SimpleOrderConstants.TYPE).equals(PublicConstants.ADD)) {
				map.put("selectAll", "true");
				map.put("select", "true");
				map.put("binding", "null");
			} else if (paramInfo.getString(SimpleOrderConstants.TYPE).equals(PublicConstants.UPDATE)) {
				if (StringUtils.isBlank(paramInfo.getString(OrderConstants.BINDING))) {
					logger.error("[getAlarmOrderBypage()->invalid：绑定状态不能为空!]");
					return RpcResponseBuilder.buildErrorRpcResp("绑定状态不能为空!]");
				}
				if (StringUtils.isBlank(paramInfo.getString(RuleContants.ALARM_RULE_ID))) {
					logger.error("[getAlarmOrderBypage()->invalid：告警规则id不能为空!]");
					return RpcResponseBuilder.buildErrorRpcResp("告警规则id不能为空!]");
				}
				map.put("selectAll", "false");
				map.put("select", "false");
				map.put("alarmRuleId", paramInfo.getString("alarmRuleId"));
				// 查询绑定的
				if (paramInfo.getString(OrderConstants.BINDING).equals(OrderConstants.BOUND)) {
					map.put("binding", "bound");
				}
				// 查询未绑定的
				if (paramInfo.getString(OrderConstants.BINDING).equals(OrderConstants.UNBOUND)) {
					map.put("binding", "unBound");
				}
			}

			int pageNo = paramInfo.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = paramInfo.getIntValue(FacilitiesContants.PAGE_SIZE);
			PageHelper.startPage(pageNo, pageSize);
			List<Map<String, Object>> page = alarmRuleFacilityQueryRepository.getFacilityDeviceList(map);
			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			logger.info(String.format("[getAllDrollsByPage()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);
		} catch (Exception e) {
			logger.error("getFacilityDeviceList()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
	// 此接口没有被调用过 2018年3月13日16:06:41
	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Override public RpcResponse<List<Map<String, Object>>>
	 * getAlarmRuleFacilityBindingStatus(JSONObject parseObject) { try {
	 * 
	 * String facilitiesTypeId = parseObject.getString("facilitiesTypeId");
	 * String accessSecret = parseObject.getString("accessSecret"); String
	 * deviceTypeId = parseObject.getString("deviceTypeId"); String deviceName =
	 * parseObject.getString("deviceName"); String searchKey =
	 * parseObject.getString("searchKey"); String deviceId =
	 * parseObject.getString("deviceId"); String alarmId =
	 * parseObject.getString("alarmId"); String state =
	 * parseObject.getString("state"); String area =
	 * parseObject.getString("area");
	 * 
	 * if (StringUtils.isBlank(state)) {
	 * logger.error("[alarmRuleFacilityQueryRepository() -> error: 绑定条件异常]");
	 * return RpcResponseBuilder.buildErrorRpcResp("绑定条件异常  绑定条件不能为空"); } if
	 * (!parseObject.containsKey("facilitiesTypeId")) {
	 * logger.error("[getAlarmRuleFacilityBindingStatus() -> error: 设施类型异常]");
	 * return RpcResponseBuilder.buildErrorRpcResp("设施类型异常  传入条件异常"); } if
	 * (!parseObject.containsKey("accessSecret")) {
	 * logger.error("[getAlarmRuleFacilityBindingStatus() -> error: 接入方密钥异常]");
	 * return RpcResponseBuilder.buildErrorRpcResp("接入方密钥异常  传入条件异常"); } if
	 * (!parseObject.containsKey("searchKey")) {
	 * logger.error("[getAlarmRuleFacilityBindingStatus() -> error: 模糊查询值异常]");
	 * return RpcResponseBuilder.buildErrorRpcResp("模糊查询值异常  传入条件异常"); } if
	 * (!parseObject.containsKey("deviceId")) {
	 * logger.error("[getAlarmRuleFacilityBindingStatus() -> error: 设备Id异常]");
	 * return RpcResponseBuilder.buildErrorRpcResp("设备Id异常  传入条件异常"); } if
	 * (!parseObject.containsKey("area")) {
	 * logger.error("[getAlarmRuleFacilityBindingStatus() -> error: 区域地址异常]");
	 * return RpcResponseBuilder.buildErrorRpcResp("区域地址  传入条件异常"); } if
	 * (!parseObject.containsKey("state")) {
	 * logger.error("[getAlarmRuleFacilityBindingStatus() -> error: 绑定条件异常]");
	 * return RpcResponseBuilder.buildErrorRpcResp("绑定条件  传入条件异常"); } if
	 * (!parseObject.containsKey("alarmId")) {
	 * logger.error("[getAlarmRuleFacilityBindingStatus() -> error: 规则绑定Id条件异常]"
	 * ); return RpcResponseBuilder.buildErrorRpcResp("规则绑定Id  传入条件异常"); } if
	 * (!parseObject.containsKey("deviceTypeId")) {
	 * logger.error("[getAlarmRuleFacilityBindingStatus() -> error: 设备类型Id条件异常]"
	 * ); return RpcResponseBuilder.buildErrorRpcResp("设备类型Id  传入条件异常"); }
	 * 
	 * Map<String, Object> page = new HashMap<>(); page.put("pageNo",
	 * parseObject.get("pageNo")); page.put("pageSize",
	 * parseObject.get("pageSize")); if (page == null) {
	 * logger.error("[getAlarmRulePage()->invalid：参数不能为空！]"); return
	 * RpcResponseBuilder.buildErrorRpcResp("参数不能为空！"); } if
	 * (!page.containsKey(AlarmInfoConstants.PAGE_SIZE)) {
	 * logger.error("[getAlarmRulePage()->invalid：页大小不能为空！]"); return
	 * RpcResponseBuilder.buildErrorRpcResp("页大小不能为空！"); } if
	 * (!page.containsKey(AlarmInfoConstants.PAGE_NO)) {
	 * logger.error("[getAlarmRulePage()->invalid：页码不能为空！]"); return
	 * RpcResponseBuilder.buildErrorRpcResp("页码不能为空！"); }
	 * 
	 * Map<String, Object> map = new HashMap<>(); map.put("facilitiesTypeId",
	 * facilitiesTypeId); map.put("deviceTypeId", deviceTypeId);
	 * map.put("accessSecret", accessSecret); map.put("deviceName", deviceName);
	 * map.put("searchKey", searchKey); map.put("deviceId", deviceId);
	 * map.put("alarmId", alarmId); map.put("state", state); map.put("area",
	 * area); map.putAll(page);
	 * 
	 * List<Map<String, Object>> queryfacility =
	 * alarmRuleFacilityQueryRepository.queryAcilitiesBindingStatus(map); for
	 * (int i = 0; i < queryfacility.size(); i++) { String arr = (String)
	 * queryfacility.get(i).get("deviceId"); String[] deviceIdStr = { arr }; //
	 * 获取设备ID批量查询设信息 RpcResponse<Map<String, Object>> deviceStr =
	 * deviceEsQueryService .queryDeviceListByDeviceIds(deviceIdStr, 0, 0); //
	 * 通过设备类型Id 获取信息 RpcResponse<Map<String, Object>> deviceTypeStr =
	 * deviceEsQueryService.queryDeviceList(new String[] {}, deviceTypeId, null,
	 * null, 0, 0); Map<String, Object> deviceInfoList =
	 * deviceStr.getSuccessValue(); Map<String, Object> deviceTypeInfoList =
	 * deviceTypeStr.getSuccessValue(); List<Map<String, Object>>
	 * deviceInfoTypeList = new ArrayList<>(); List<Map<String, Object>>
	 * deviceInfoTypeList1 = new ArrayList<>(); if (deviceTypeInfoList == null)
	 * { logger.info("[alarmRuleFacilityQueryRepository() -> init: 查询成功]");
	 * return RpcResponseBuilder.buildSuccessRpcResp("查询成功", queryfacility); }
	 * 
	 * if (deviceInfoList == null) {
	 * logger.info("[alarmRuleFacilityQueryRepository() -> init: 查询成功]"); return
	 * RpcResponseBuilder.buildSuccessRpcResp("查询成功", queryfacility); } int v =
	 * 0; while (deviceInfoTypeList.size() < deviceInfoList.size()) {
	 * deviceInfoTypeList.add(deviceInfoList); v++; } while
	 * (deviceInfoTypeList1.size() < deviceInfoTypeList.size()) {
	 * deviceInfoTypeList1.add(deviceTypeInfoList); v++; }
	 * deviceInfoTypeList.addAll(deviceInfoTypeList1); while
	 * (deviceInfoTypeList.size() < v) { if
	 * (deviceInfoTypeList.get(v).get("deviceTypeId")
	 * .equals(deviceInfoTypeList.get(v).get("deviceTypeId"))) {
	 * queryfacility.add( (Map<String, Object>) map.put("deviceTypeId",
	 * deviceTypeInfoList.get("deviceTypeId"))); } }
	 * 
	 * List<Map<String, Object>> device = (List<Map<String, Object>>)
	 * deviceInfoList.get("deviceInfoList"); for (int C = 0; C <
	 * queryfacility.size(); C++) { Map<String, Object> newList =
	 * queryfacility.get(C);
	 * 
	 * for (int j = 0; j < newList.size(); j++) { for (int a = 0; a <
	 * device.size(); a++) {
	 * 
	 * if (device.get(a).get("deviceId").equals(newList.get(j))) {
	 * newList.put("deviceName", device.get(i).get("deviceName"));
	 * newList.put("deviceTypeId", deviceInfoList.get("deviceTypeId")); } int
	 * count = queryfacility.size(); 判断设施类型名是否为空 <如果为空将其替换> --count; if
	 * (queryfacility.get(C).get("facilityTypeName") == null) { Object transform
	 * = newList.put("facilityTypeName",
	 * queryfacility.get(C).get("facilityTypeAlias"));
	 * newList.put("facilityTypeAlias", transform);
	 * logger.info("[alarmRuleFacilityQueryRepository() -> init: 查询成功]"); return
	 * RpcResponseBuilder.buildSuccessRpcResp("查询成功", queryfacility); } else if
	 * (count == 0) {
	 * logger.info("[alarmRuleFacilityQueryRepository() -> init: 查询成功]"); return
	 * RpcResponseBuilder.buildSuccessRpcResp("查询成功", queryfacility); } } } } }
	 * 
	 * logger.info("[alarmRuleFacilityQueryRepository() -> init: 查询成功]"); return
	 * RpcResponseBuilder.buildSuccessRpcResp("查询成功", queryfacility); } catch
	 * (Exception e) {
	 * logger.error("getAlarmRuleFacilityBindingStatus()->exception",e); return
	 * RpcResponseBuilder.buildExceptionRpcResp(e); } }
	 */
}
