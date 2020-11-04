/*
 * File name: RemoteControlRecordRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月11日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.common.util.StringUtil;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.AlarmRuleQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.RemoteControlRecordQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;

/**
 * @Description: 远控查询
 * @author: qulong
 * @version: 1.0, 2017年12月11日
 */

@Service
public class RemoteControlRecordRestQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private AlarmRuleQueryService			alarmRuleQueryService;

	@Autowired
	private RemoteControlRecordQueryService	remoteControlRecordQueryService;

	@Autowired
	DeviceQueryService						deviceQueryService;

	@Value("${api.host}")
	private String							ip;

	@Autowired
	private HttpServletRequest				request;

	private static final String				PAGENO	= "pageNo";



	public Result<Map<String, Object>> getControlList(String params) {
		logger.info(String.format("[getControlList()->request params:%s]", params));
		try {
			if (StringUtil.isEmpty(params)) {
				logger.error("[getControlList()->error:参数为空]");
				return ResultBuilder.emptyResult();
			}
			JSONObject json = JSON.parseObject(params);

			if (StringUtils.isBlank(json.getString(CommonConstants.USERID))) {
				logger.error("[getPowersByParam()->error: 人员userId不能为空!]");
				return ResultBuilder.failResult("人员userId不能为空!");
			}
			if (StringUtils.isBlank(json.getString(CommonConstants.ACCESSSECRET))) {
				logger.error("[getControlList()->error:接入方秘钥accessSecret不能为空!]");
				return ResultBuilder.failResult("接入方秘钥accessSecret不能为空!");
			}
			String organizationId = json.getString("organizationId");
			String facilitiesTypeId = json.getString("facilitiesTypeId");
			String address = json.getString("address");
			String code = json.getString("code");
			String accessSecret = json.getString(CommonConstants.ACCESSSECRET);
			if (!StringUtils.isNumeric(json.getString(PAGENO))
					|| !StringUtils.isNumeric(json.getString(CommonConstants.PAGE_SIZE))) {
				logger.error("[getControlList()->error:pageSize和pageNum必须为数字]");
				return ResultBuilder.invalidResult();
			}
			int pageNo = json.getInteger(PAGENO);
			int pageSize = json.getInteger(CommonConstants.PAGE_SIZE);

			if (StringUtil.isEmpty(accessSecret)) {
				return ResultBuilder.invalidResult();
			}

			// 先获取该组织及其下所有组织id
			// 存储组织id
			List<String> organizationIdList = getOrganizationIdList(organizationId);

			Map<String, Object> listParam = getListParam(json, organizationId, facilitiesTypeId, address, code,
					accessSecret, organizationIdList);
			RpcResponse<List<Map<String, Object>>> controlList = remoteControlRecordQueryService
					.getControlList(listParam, pageNo, pageSize);
			if (!controlList.isSuccess()) {
				logger.error(String.format("[getControlList()->fail:%s]", controlList.getMessage()));
				return ResultBuilder.failResult(controlList.getMessage());
			}

			List<Map<String, Object>> controlListInfo = controlList.getSuccessValue();
			StringBuilder orgIds = new StringBuilder();
			int total = 0;
			total = orgIdsHandle(controlListInfo, orgIds, total);

			int num = getNum(pageSize, total);
			Map<String, Object> map = getMap(pageNo, pageSize, total, num);

			JSONObject orgIdJson = new JSONObject();
			orgIdJson.put(InterGatewayConstants.ORGANIZATION_IDS, orgIds.toString());
			String httpValueByPost = InterGatewayUtil.getHttpValueByPost(
					InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, orgIdJson, ip,
					request.getHeader(InterGatewayConstants.TOKEN));
			if (null == httpValueByPost) {
				logger.error("通过interGateway查询组织名失败!");
				map.put("list", controlListInfo);
				return ResultBuilder.successResult(map, "查询成功!");
			} else {
				// 将统计结果的组织id与查询到的组织对比,获得组织名
				JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
				controlListInfoHandle(controlListInfo, httpValueJson);
			}

			map.put("list", controlListInfo);
			logger.info(String.format("[getControlList()->success:%s]", controlList.getMessage()));
			return ResultBuilder.successResult(map, controlList.getMessage());
		} catch (Exception e) {
			logger.error("getControlList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param organizationId
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	private List<String> getOrganizationIdList(String organizationId) {
		List<String> organizationIdList = Lists.newArrayList();
		if (!StringUtils.isBlank(organizationId)) {
			// 查询获取组织Name
			String httpValueByGet = InterGatewayUtil.getHttpValueByGet(
					InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip,
					request.getHeader(InterGatewayConstants.TOKEN));
			if (null == httpValueByGet) {
				logger.error("[getControlList()->invalid：组织查询失败!]");
				organizationIdList.add(organizationId);
			} else {
				JSONArray jsonArray = JSON.parseArray(httpValueByGet);
				List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
				for (Map map : organizationInfoList) {
					organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
				}
			}
		}
		return organizationIdList;
	}



	/**
	 * @Description:
	 * @param json
	 * @param organizationId
	 * @param facilitiesTypeId
	 * @param address
	 * @param code
	 * @param accessSecret
	 * @param organizationIdList
	 * @return
	 */

	private Map<String, Object> getListParam(JSONObject json, String organizationId, String facilitiesTypeId,
			String address, String code, String accessSecret, List<String> organizationIdList) {
		Map<String, Object> listParam = Maps.newHashMap();
		listParam.put("facilitiesTypeId", facilitiesTypeId);
		listParam.put("address", address);
		listParam.put("code", code);
		listParam.put("accessSecret", accessSecret);
		// 当前登录系统人员所用的组织及下级组织id集合
		listParam.put("organizationId", organizationIdList);
		// 当前登录系统人员所用的组织id
		listParam.put("orgIdForUser", organizationId);
		// 当前登录系统人员id
		listParam.put("userId", json.getString("userId"));
		return listParam;
	}



	/**
	 * @Description:
	 * @param pageSize
	 * @param total
	 * @return
	 */

	private int getNum(int pageSize, int total) {
		int num = 0;
		if (total % pageSize == 0) {
			num = (int) (total / pageSize);
		} else {
			num = (int) (total / pageSize + 1);
		}
		return num;
	}



	/**
	 * @Description:
	 * @param pageNo
	 * @param pageSize
	 * @param total
	 * @param num
	 * @return
	 */

	private Map<String, Object> getMap(int pageNo, int pageSize, int total, int num) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("pageNum", pageNo);
		map.put("pageSize", pageSize);
		map.put("total", total);
		map.put("pages", num);
		return map;
	}



	/**
	 * @Description:
	 * @param controlListInfo
	 * @param httpValueJson
	 */

	private void controlListInfoHandle(List<Map<String, Object>> controlListInfo, JSONObject httpValueJson) {
		for (Map<String, Object> control : controlListInfo) {
			if (null != control) {
				if (null != httpValueJson.getString(control.get("organizationId") + "")) {
					control.put("organizationName", httpValueJson.getString(control.get("organizationId") + ""));
				}
			}
		}
	}



	/**
	 * @Description:
	 * @param controlListInfo
	 * @param orgIds
	 * @param total
	 * @return
	 */

	private int orgIdsHandle(List<Map<String, Object>> controlListInfo, StringBuilder orgIds, int total) {
		if (null != controlListInfo && controlListInfo.size() > 0) {
			total = Integer.parseInt(controlListInfo.get(0).get("count") + "");
			for (int i = 0; i < controlListInfo.size(); i++) {
				if (i == 0) {
					orgIds.append(controlListInfo.get(i).get("organizationId"));
				} else {
					orgIds.append("," + controlListInfo.get(i).get("organizationId"));
				}
			}
		}
		return total;
	}



	public Result<List<Map<String, Object>>> getControlItem(String params) {
		logger.info(String.format("[getControlItem()->request params:%s]", params));
		try {
			if (StringUtil.isEmpty(params)) {
				logger.error("[getControlItem()->error:参数为空]");
				return ResultBuilder.emptyResult();
			}
			JSONObject json = JSON.parseObject(params);
			String deviceId = json.getString("deviceId");
			String accessSecret = json.getString("accessSecret");

			if (StringUtil.isEmpty(accessSecret)) {
				logger.error("[getControlItem()->error: accessSecret为空!]");
				return ResultBuilder.invalidResult();
			}
			if (StringUtil.isEmpty(deviceId)) {
				logger.error("[getControlItem()->error: deviceId为空!]");
				return ResultBuilder.invalidResult();
			}
			List<String> listDeviceId = new ArrayList<>();
			listDeviceId.add(deviceId);
			// 获取设备类型
			RpcResponse<List<Map<String, Object>>> deviceDetail = deviceQueryService
					.queryBatchDeviceInfoForDeviceIds(listDeviceId);
			List<Map<String, Object>> successValue = deviceDetail.getSuccessValue();
			if (successValue == null || !deviceDetail.isSuccess()) {
				logger.error("[getControlItem()->failed:设备类型不存在！]");
				return ResultBuilder.failResult("设备类型不存在！");
			}
			Map<String, Object> map = null;
			if (successValue.size() > 0) {
				map = successValue.get(0);
			}
			String deviceTypeId = "";
			if (null != map) {
				deviceTypeId = map.get("deviceTypeId").toString();
			}

			RpcResponse<List<Map<String, Object>>> result = alarmRuleQueryService
					.findDataPointByDeviceTypeId(deviceTypeId, accessSecret);
			if (!result.isSuccess()) {
				logger.error(String.format("[getControlItem()->fail:%s]", result.getMessage()));
				return ResultBuilder.failResult(result.getMessage());
			}
			logger.info(String.format("[getControlItem()->success:%s]", result.getMessage()));
			return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
		} catch (Exception e) {
			logger.error("getControlItem()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
