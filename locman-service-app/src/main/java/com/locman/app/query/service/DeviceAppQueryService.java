package com.locman.app.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.impl.util.json.JSONTokener;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.locman.app.entity.vo.DevicePropertiesVo;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.base.query.FacilitesService;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.AlarmRuleQueryService;
import com.run.locman.api.query.service.BalanceSwitchPowersQueryService;
import com.run.locman.api.query.service.DeviceInfoConvertQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.DistributionPowersQueryService;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.query.service.PropertiesQueryService;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceInfoConvertConstans;

@Service
@SuppressWarnings("rawtypes")
public class DeviceAppQueryService extends BaseAppController {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilityDeviceQueryService		facilityDeviceQueryService;

	@Autowired
	private FacilitesService				facilitiesService;

	@Autowired
	private FacilitiesQueryService			facilitiesQueryService;

	@Autowired
	private AlarmRuleQueryService			alarmRuleQueryService;

	@Autowired
	private DeviceQueryService				deviceQueryService;

	@Autowired
	private PropertiesQueryService			propertiesQueryService;

	@Autowired
	private DeviceInfoConvertQueryService	deviceInfoConvertQueryService;

	@Autowired
	private DistributionPowersQueryService	distributionPowersQueryService;

	@Autowired
	private UscQueryService					uscQueryService;

	@Autowired
	private AlarmOrderAppQueryService		alarmOrderAppQueryService;

	@Autowired
	private BalanceSwitchPowersQueryService	balanceSwitchPowersQueryService;

	@Autowired
	SimpleOrderQueryService					simpleOrderQueryService;

	@Autowired
	private CommonService					commonService;

	@Autowired
	private AlarmInfoQueryService			alarmInfoQueryService;

	@Value("${warning.picture}")
	private String							warningPicture;



	/**
	 * 
	 * 根据设施id查询设施详情
	 *
	 * @param facilitiesId
	 *            设施id
	 * @return 设施及下挂设备详情
	 */

	@SuppressWarnings("unchecked")
	public Result<Map<String, Object>> queryFacilityById(String facilitiesId, String accessSecret, String param) {
		try {
			if (StringUtils.isBlank(facilitiesId)) {
				return ResultBuilder.noBusinessResult();
			}
			if (StringUtils.isBlank(accessSecret)) {
				return ResultBuilder.noBusinessResult();
			}
			if (ParamChecker.isBlank(param) || ParamChecker.isNotMatchJson(param)) {
				return ResultBuilder.invalidResult();
			}
			JSONObject paramJson = JSONObject.parseObject(param);
			if (!paramJson.containsKey("roleId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!paramJson.containsKey("organizeId")) {
				return ResultBuilder.noBusinessResult();
			}
			String organizeId = paramJson.getString("organizeId");
			String roleId = paramJson.getString("roleId");
			String token = request.getHeader("token");
			RpcResponse<Map<String, Object>> response = facilitiesService.getFacilitesInfoByFacId(facilitiesId, token);
			Map<String, Object> respMap = response.getSuccessValue();
			if (respMap == null || respMap.isEmpty()) {
				return ResultBuilder.failResult("设施信息为空");
			}
			Map<String, Object> resultMap = Maps.newHashMap();
			JSONObject jsonObj = new JSONObject();
			if (respMap.get("orgInfo") == null || respMap.get("orgInfo") instanceof String
					|| "".equals(respMap.get("orgInfo"))) {
				jsonObj = null;
			} else {
				jsonObj = (JSONObject) JSONObject.toJSON(respMap.get("orgInfo"));
			}
			if (jsonObj != null) {
				JSONObject sourceJson = (JSONObject) JSONObject.toJSON(jsonObj.get("sourceInfo"));
				if (sourceJson != null) {
					resultMap.put("sourceName", sourceJson.get("sourceName"));
				}
			}
			if (!resultMap.containsKey("sourceName")) {
				resultMap.put("sourceName", "");
			}
			resultMap.put("gis", respMap.get("gis"));
			resultMap.put("facilitiesId", respMap.get("id"));
			resultMap.put("facilitiesCode", respMap.get("facilitiesCode"));
			resultMap.put("address", respMap.get("address"));
			resultMap.put("facilityTypeName", respMap.get("facilityTypeAlias"));
			resultMap.put("latitude", respMap.get("latitude"));
			resultMap.put("longitude", respMap.get("longitude"));
			resultMap.put("showExtend", "");
			String extend = String.valueOf(respMap.get("showExtend"));
			if (extend != null && !"".equals(extend) && !"[]".equals(extend)) {
				Object object = new JSONTokener(extend).nextValue();
				if (object instanceof org.activiti.engine.impl.util.json.JSONArray) {
					JSONArray jsonArray = JSONArray.parseArray(object.toString());
					JSONObject parseObject = new JSONObject();
					parseObject.put("accessSecret", accessSecret);
					parseObject.put("facilitiesTypeId", respMap.get("facilitiesTypeId"));
					// 传入设施扩展属性解析属性值
					String showExtend = showDataType(parseObject, jsonArray);
					resultMap.put("showExtend", showExtend);
				}
			}
			String[] areaIds = respMap.get("areaId").toString().split(",");
			resultMap.put("area", "");
			for (String id : areaIds) {
				// 获取设施所属区域
				RpcResponse<List<String>> areaList = facilitiesQueryService.getAreaById(id);
				if (areaList.isSuccess() && areaList.getSuccessValue() != null
						&& areaList.getSuccessValue().size() != 0) {
					String str = areaList.getSuccessValue().get(0);
					resultMap.put("area", resultMap.get("area") + str);
				}
			}
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			RpcResponse<List<String>> dList = facilityDeviceQueryService.queryFacilityBindingState(facilitiesId);
			List<String> successValue = dList.getSuccessValue();
			if (successValue == null || successValue.isEmpty()) {
				resultMap.put("deviceInfo", null);
			} else {
				// 获取设备信息及实时数据
				List<Map> devInfoList = getDeviceInfoByIds(successValue, accessSecret);
				for (int i = 0; i < (devInfoList == null || devInfoList.isEmpty() ? 0 : devInfoList.size()); i++) {
					Map deviceInfo = devInfoList.get(i);
					Map newMap = Maps.newHashMap();
					newMap = deviceInfo;
					String deviceId = (String) deviceInfo.get("deviceId");
					Map<String, String> alarmOrderId = findAlarmOrderByDId(deviceId, userId, organizeId);
					newMap.put("alarmOrderId", alarmOrderId.get("orderId"));
					newMap.put("commandFlag", alarmOrderId.get("commandFlag"));
					if (commonService.isBalanceDevice(StringUtils.isEmpty(deviceId) ? "" : deviceId)) {
						boolean balance = CheckBalance(accessSecret, (String) respMap.get("facilitiesTypeId"), roleId,
								userId, organizeId);
						newMap.put("balance", balance);
					} else {
						boolean inspection = checkInspection(accessSecret, facilitiesId, roleId, userId, organizeId);
						newMap.put("inspection", inspection);
					}
					devInfoList.set(i, newMap);
				}
				resultMap.put("deviceInfo", devInfoList);
			}
			String simpleOrderId = checkSimpleOrderByFacID((String) resultMap.get("facilitiesId"), userId,
					accessSecret);
			resultMap.put("simpleOrderId", simpleOrderId);
			return ResultBuilder.successResult(resultMap, response.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 解析设施扩展属性 <method description>
	 *
	 * @param parseObject
	 * @param jsonArray
	 * @return
	 */
	public String showDataType(JSONObject parseObject, JSONArray jsonArray) {
		String dataType = commonService.requestRest(Constant.LOCMAN_PORT, "facility/getAllFacilitiesDataType",
				parseObject.toJSONString());
		Map<String, String> dataMap = commonService.checkResult(JSONObject.parseObject(dataType));
		JSONArray resultArray = new JSONArray();
		if (dataMap.containsKey("success")) {
			String dataTypeStr = dataMap.get("success");
			if (dataTypeStr == null || "".equals(dataTypeStr) || "[]".equals(dataTypeStr)) {
				return "";
			}
			JSONArray dataArray = JSONArray.parseArray(dataTypeStr);
			JSONObject checkBox = new JSONObject();
			for (Object obj : jsonArray) {
				JSONObject objJSON = (JSONObject) obj;
				String objName = objJSON.getString("name");
				String objType = objJSON.getString("type");
				String objValue = objJSON.getString("value");
				JSONObject tempJSON = null;
				String value = "";
				if ("checkbox".equals(objType)) {
					tempJSON = objJSON;
				}
				if (!"checkbox".equals(objType) && !"select".equals(objType) && !"radio".equals(objType)) {
					resultArray.add(objJSON);
				} else {
					for (Object data : dataArray) {
						JSONObject dataJSON = (JSONObject) data;
						String dataName = dataJSON.getString("name");
						String dataTypes = dataJSON.getString("dataType");
						if (objName.equals(dataName) && objType.equals(dataTypes) && !"checkbox".equals(dataTypes)) {
							String initialValue = dataJSON.getString("initialValue");
							JSONArray array = JSONArray.parseArray(initialValue);
							for (Object object2 : array) {
								JSONObject temp = (JSONObject) object2;
								if (objValue.equals(temp.getString("value"))) {
									objJSON.put("value", temp.getString("name"));
									resultArray.add(objJSON);
									break;
								}
							}
							break;
						} else if (objName.equals(dataName) && objType.equals(dataTypes)) {
							String initialValue = dataJSON.getString("initialValue");
							JSONArray array = JSONArray.parseArray(initialValue);
							for (Object object2 : array) {
								JSONObject temp = (JSONObject) object2;
								if (objValue.equals(temp.getString("name"))) {
									value += temp.getString("name") + ",";
									tempJSON.put("value", value);
								}
							}
							if (checkBox.containsKey(objName)) {
								String tempStr = tempJSON.getString("value");
								JSONObject checkJSON = checkBox.getJSONObject(objName);
								checkJSON.put("value", checkJSON.getString("value") + tempStr);
								checkBox.put(objName, checkJSON);
							} else {
								checkBox.put(objName, tempJSON);
							}
						}
					}
				}
			}
			// 重组多选值
			if (!checkBox.isEmpty() && !StringUtils.equals("{}", checkBox.toString())) {
				Set<String> checkKey = checkBox.keySet();
				for (String key : checkKey) {
					JSONObject tempCheck = checkBox.getJSONObject(key);
					String str = tempCheck.getString("value");
					str = str.substring(0, str.lastIndexOf(","));
					tempCheck.put("value", str);
					resultArray.add(tempCheck);
				}
			}
		}
		return resultArray.toString();
	}



	/**
	 * 获取设备数据点 <method description>
	 *
	 * @param deviceId
	 * @return
	 */
	public List<Map<String, Object>> getControlItem(String deviceTypeId, String accessSecret) {
		try {
			RpcResponse<List<Map<String, Object>>> result = alarmRuleQueryService
					.findDataPointByDeviceTypeId(deviceTypeId, accessSecret);
			if (!result.isSuccess()) {
				return null;
			}
			return result.getSuccessValue();
		} catch (Exception e) {
			logger.error("getControlItem()->Exception:", e);
			return null;
		}
	}



	/**
	 * 根据设备id集合获取设备集合信息 <method description>
	 *
	 * @param ids
	 * @return
	 */
	public List<Map> getDeviceInfoByIds(List<String> ids, String accessSecret) {
		// 获取设备信息
		RpcResponse<List<Map<String, Object>>> getDeviceIdInfoByDeviceId = deviceQueryService
				.queryBatchDeviceInfoForDeviceIds(ids);
		if (getDeviceIdInfoByDeviceId.isSuccess() && getDeviceIdInfoByDeviceId.getSuccessValue() != null
				&& !getDeviceIdInfoByDeviceId.getSuccessValue().isEmpty()) {
			List<Map<String, Object>> deviceInfo = getDeviceIdInfoByDeviceId.getSuccessValue();
			Map<String, Object> devInfo = null;
			List<Map> devInfoList = Lists.newArrayList();
			for (Map<String, Object> dMap : deviceInfo) {
				devInfo = new HashMap<>();
				devInfo.put("deviceId", dMap.get("deviceId"));
				devInfo.put("deviceName", dMap.get("deviceName"));
				devInfo.put("deviceTypeName", dMap.get("deviceTypeName"));
				devInfo.put("deviceTypeId", dMap.get("deviceTypeId"));
				devInfo.put("timestamp", null);
				// 获取设备状态信息
				RpcResponse<Map<String, Object>> stateRpc = deviceQueryService
						.queryDeviceBindingState(dMap.get("deviceId").toString());
				String deviceOnlineStatus = "未知";
				if (stateRpc.isSuccess() && stateRpc.getSuccessValue() != null
						&& !stateRpc.getSuccessValue().isEmpty()) {
					Map<String, Object> stateMap = stateRpc.getSuccessValue();
					String deviceOnlineStatusValue = stateMap.get("deviceOnlineStatus").toString();
					if (deviceOnlineStatusValue.equals("online")) {
						deviceOnlineStatus = "在线";
					} else if (deviceOnlineStatusValue.equals("offline")) {
						deviceOnlineStatus = "离线";
					}
				}
				devInfo.put("deviceOnlineStatus", deviceOnlineStatus);
				// 获取设备实时数据
				RpcResponse<JSONObject> jsonRpc = deviceQueryService
						.queryDeviceLastState(dMap.get("deviceId").toString());
				JSONObject reported = null;
				if (jsonRpc.isSuccess() && jsonRpc.getSuccessValue() != null && !jsonRpc.getSuccessValue().isEmpty()) {
					JSONObject deviceRealJson = jsonRpc.getSuccessValue();
					devInfo.put("timestamp", deviceRealJson.getString("timestamp"));
					String gatewayId = UtilTool.getIotNewGatewayId(deviceRealJson);
					// 如果网关id为NULL值，则使用原版IOT解析数据，否则用新版IOT解析数据
					if (StringUtils.isBlank(gatewayId)) {
						reported = UtilTool.getReported(deviceRealJson);
					} else {
						reported = UtilTool.getIotNewRepoted(deviceRealJson);
					}
				}
				// 获取设备对应的数据点
				RpcResponse<List<DeviceProperties>> deviceProper = propertiesQueryService
						.findByDeviceTypeId(accessSecret, devInfo.get("deviceTypeId").toString());

				// 查询设备告警的key集合
				RpcResponse<List<String>> keyList = alarmInfoQueryService
						.queryAlarmItemList(UtilTool.objectToString(dMap.get("deviceId")), accessSecret);
				List<String> itemKeyList = keyList.getSuccessValue();

				List<DevicePropertiesVo> deviceProVoList = Lists.newArrayList();
				if (deviceProper.isSuccess() && deviceProper.getSuccessValue() != null
						&& !deviceProper.getSuccessValue().isEmpty()) {
					Set<String> keySet = Sets.newHashSet();
					if (reported != null && !reported.isEmpty()) {
						keySet = reported.keySet();
					}
					for (DeviceProperties devProper : deviceProper.getSuccessValue()) {
						DevicePropertiesVo devicePropertiesVo = new DevicePropertiesVo(devProper);
						for (String propName : keySet) {
							if (propName.equals(devProper.getDevicePropertiesSign())) {
								// 设置设备告警后的属性点警告图片
								setWarningPicture(itemKeyList, devicePropertiesVo, propName);
								String value = reported.getString(propName);
								if (value != null && !"".equals(value) && !"null".equals(value)) {
									devicePropertiesVo.setValue(value);
									// 如果值是英文,转中文
									RpcResponse<Map> dataConvertInfo = deviceInfoConvertQueryService
											.dataConvert(String.valueOf(value), accessSecret);
									String dataConvert = dataConvertInfo.isSuccess()
											? dataConvertInfo.getSuccessValue().get(DeviceInfoConvertConstans.CHINA_KEY)
													+ ""
											: String.valueOf(reported.get(propName));
									devicePropertiesVo.setDescription(dataConvert);
								}
								break;
							}
						}
						deviceProVoList.add(devicePropertiesVo);
					}
				}
				devInfo.put("deviceRealMap", deviceProVoList);
				devInfoList.add(devInfo);
			}
			return devInfoList;
		}
		return null;
	}



	/**
	 * @Description:
	 * @param itemKeyList
	 * @param devicePropertiesVo
	 * @param propName
	 */

	private void setWarningPicture(List<String> itemKeyList, DevicePropertiesVo devicePropertiesVo, String propName) {
		if (null != itemKeyList && itemKeyList.size() > 0) {
			for (String key : itemKeyList) {
				if (key.equals(propName)) {
					devicePropertiesVo.setAppIcon(warningPicture);
				}
			}
		}
	}



	/**
	 * 检验设施是否存在分权分域 <method description>
	 *
	 * @param accessSecret
	 * @param facilitiesTypeId
	 * @param roleId
	 * @param userId
	 * @return
	 */
	public boolean checkInspection(String accessSecret, String facilityId, String roleId, String userId,
			String organizeId) {
		boolean inspection = false;
		try {
			JSONObject parseObject = new JSONObject();
			parseObject.put("accessSecret", accessSecret);
			parseObject.put("facilityId", facilityId);
			parseObject.put("userId", userId);
			parseObject.put("orgIdForUser", organizeId);
			List<String> organizationIdList = uscQueryService.findOrganizationAndLower(organizeId);
			parseObject.put("organizationId", organizationIdList);
			// 校验该设施类型该组织或该组织岗位在当前时间是否存在分权分域
			RpcResponse<Boolean> powersByParam = distributionPowersQueryService.getPowersByParam(parseObject);
			if (powersByParam != null && powersByParam.isSuccess() && powersByParam.getSuccessValue()) {
				inspection = powersByParam.getSuccessValue();
			} else {
				logger.info("验证是否存在分权分域==》" + powersByParam.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}



	/**
	 * 校验当前组织及下级组织或当前组织岗位及下级组织岗位在当当前时间是否配有平衡告警开关权限 <method description>
	 *
	 * @param accessSecret
	 * @param facilitiesTypeId
	 * @param roleId
	 * @param userId
	 * @return
	 */
	public boolean CheckBalance(String accessSecret, String facilitiesTypeId, String roleId, String userId,
			String organizeId) {
		boolean balance = false;
		// Role role = uscQueryService.findOrganizationByRoleId(accessSecret,
		// roleId, userId);
		JSONObject balanceJson = new JSONObject();
		balanceJson.put("facilityTypeId", facilitiesTypeId);
		balanceJson.put("accessSecret", accessSecret);
		balanceJson.put("organizationId", organizeId);
		balanceJson.put("postId", roleId);
		balanceJson.put("Token", request.getHeader("token"));
		logger.info(balanceJson.toJSONString());
		RpcResponse<Boolean> response = balanceSwitchPowersQueryService.checkBalanceSwitchPowers(balanceJson);
		logger.info("告警平衡开关权限==》" + response.getSuccessValue());
		if (response != null && response.isSuccess() && response.getSuccessValue()) {
			return balance = response.getSuccessValue();
		}
		return balance;
	}



	/**
	 * 查询告警工单id <method description>
	 *
	 * @param facilitiesCode
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> findAlarmOrderByDId(String deviceId, String userId, String orgId) {
		String orderId = "";
		String commandFlag = "false";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("stateNo", "2");
		jsonObject.put("orderState", "");
		jsonObject.put("selectKey", deviceId);
		jsonObject.put("pageSize", "10");
		jsonObject.put("pageNo", "1");
		jsonObject.put("orgId", orgId);
		jsonObject.put("userId", userId);
		logger.info("获取告警工单参数==》 " + jsonObject.toString());
		Result result = alarmOrderAppQueryService.getAlarmOrderList(jsonObject.toString());
		logger.info("告警工单==》 " + result.toString());
		JSONObject pageInfo = (JSONObject) result.getValue();
		if (pageInfo != null && pageInfo.containsKey("list")) {
			List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageInfo.get("list");
			logger.info("告警工单返回list==》  " + resultList);
			if (resultList != null && !resultList.isEmpty() && resultList.size() != 0) {
				for (Map<String, Object> alarmOrder : resultList) {
					if (alarmOrder.containsKey("processState") && alarmOrder.get("processState") != "4") {
						orderId = alarmOrder.get("orderId").toString();
						commandFlag = alarmOrder.get("commandFlag").toString();
						break;
					}
				}
			}
		}
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.put("orderId", orderId);
		resultMap.put("commandFlag", commandFlag);
		return resultMap;
	}



	/**
	 * 根据设施序列号查询设备信息 <method description>
	 *
	 * @param param
	 * @return
	 */
	public Result<List<Map<String, Object>>> findFacilitiesInfoByCode(String param) {
		try {
			if (ParamChecker.isBlank(param) || ParamChecker.isNotMatchJson(param)) {
				return ResultBuilder.invalidResult();
			}
			JSONObject parseJson = JSONObject.parseObject(param);
			if (!parseJson.containsKey("facilitiesCode")) {
				return ResultBuilder.noBusinessResult();
			}
			String facilitiesCode = parseJson.getString("facilitiesCode");
			if (!parseJson.containsKey("organizationId")) {
				return ResultBuilder.noBusinessResult();
			}
			String organizationId = parseJson.getString("organizationId");

			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			String accessSecret = Constant.GETACCESSSECRET(userId);
			JSONObject facJSON = new JSONObject();
			// {"facilitiesCode":"","organizationId":"","accessSecret":""}
			facJSON.put("facilitiesCode", facilitiesCode);
			facJSON.put("organizationId", organizationId);
			facJSON.put("accessSecret", accessSecret);
			facJSON.put("Token", token);
			RpcResponse<List<Facilities>> response = facilitiesQueryService.getFacilityByParam(facJSON);
			if (!response.isSuccess() || response.getSuccessValue() == null) {
				return ResultBuilder.failResult(response.getMessage());
			}

			List<Facilities> facList = response.getSuccessValue();
			String facilitiesId = "";
			if (facList != null && !facList.isEmpty()) {
				facilitiesId = facList.get(0).getId();
			}
			List<Map<String, Object>> resultMap = Lists.newArrayList();
			Map<String, Object> devInfo = null;
			RpcResponse<List<String>> dList = facilityDeviceQueryService.queryFacilityBindingState(facilitiesId);
			List<String> successValue = dList.getSuccessValue();
			if (successValue != null && !successValue.isEmpty()) {
				// 获取设备信息
				RpcResponse<List<Map<String, Object>>> getDeviceIdInfoByDeviceId = deviceQueryService
						.queryBatchDeviceInfoForDeviceIds(successValue);
				if (getDeviceIdInfoByDeviceId.isSuccess() && getDeviceIdInfoByDeviceId.getSuccessValue() != null
						&& !getDeviceIdInfoByDeviceId.getSuccessValue().isEmpty()) {
					List<Map<String, Object>> deviceInfo = getDeviceIdInfoByDeviceId.getSuccessValue();
					for (Map<String, Object> dMap : deviceInfo) {
						devInfo = new HashMap<>();
						devInfo.put("deviceId", dMap.get("deviceId"));
						devInfo.put("deviceName", dMap.get("deviceName"));
						devInfo.put("deviceTypeName", dMap.get("deviceTypeName"));
						devInfo.put("deviceTypeId", dMap.get("deviceTypeId"));
						resultMap.add(devInfo);
					}
				}
			}
			if (resultMap.isEmpty()) {
				return ResultBuilder.failResult("该设施未下挂设备");
			}
			return ResultBuilder.successResult(resultMap, response.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 查询设施是否存在开锁工单 <method description>
	 *
	 * @param facilitiesId
	 * @return
	 */
	public String checkSimpleOrderByFacID(String facilitiesId, String userId, String accessSecret) {
		String simpleOrderId = null;
		try {
			JSONObject parseJSON = new JSONObject();
			parseJSON.put("facilityId", facilitiesId);
			parseJSON.put("userId", userId);
			parseJSON.put("accessSecret", accessSecret);
			RpcResponse<List<Map<String, Object>>> rpcResponse = simpleOrderQueryService
					.getSimpleOrderAgencyListForApp(parseJSON);
			if (rpcResponse != null && rpcResponse.isSuccess() && !rpcResponse.getSuccessValue().isEmpty()) {
				List<Map<String, Object>> list = rpcResponse.getSuccessValue();
				if (list.size() != 0) {
					logger.info("根据设施查询是否存在一般流程==》" + list.toString());
					for (Map<String, Object> map : list) {
						if ("4".equals(map.get("processStateApp")) || "9".equals(map.get("processStateApp"))) {
							simpleOrderId = (String) list.get(0).get("id");
						}
					}
				}
			}
			return simpleOrderId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



	/**
	 * 查询设施扩展属性及值
	 * <method description>
	 *
	 * @param facilitiesTypeId
	 * @return
	 */
	public Result<?> findFacilitiesExtend(String id) {
		try {
			if(StringUtils.isEmpty(id)) {
				return ResultBuilder.noBusinessResult();
			}
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject resultJSON = new JSONObject();
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			String accessSecret = Constant.GETACCESSSECRET(userId);
			JSONObject parseJSON = new JSONObject();
			parseJSON.put("accessSecret", accessSecret);
			RpcResponse<Map<String, Object>> response = facilitiesService.getFacilitesInfoByFacId(id, token);
			Map<String, Object> respMap = response.getSuccessValue();
			if (respMap == null || respMap.isEmpty()) {
				return ResultBuilder.failResult("设施信息为空");
			}
			JSONObject facJSON = (JSONObject) JSONObject.toJSON(respMap);
			parseJSON.put("facilitiesTypeId", facJSON.getString("facilitiesTypeId"));
			String handResut = commonService.requestRest(Constant.LOCMAN_PORT, "facility/getAllFacilitiesDataType", parseJSON.toJSONString());
			Map<String, String> resultMap = commonService.checkResult(JSONObject.parseObject(handResut));
			if(resultMap.containsKey("error")) {
				return ResultBuilder.failResult(resultMap.get("error"));
			}
			String value = resultMap.get("success");
			if(StringUtils.isEmpty(value) || StringUtils.equals(value, "[]")) {
				return ResultBuilder.failResult("当前设施类型没有扩展属性");
			}
			JSONArray extendArray = JSONArray.parseArray(value);
			JSONArray facDataType = new JSONArray();
			for (Object object : extendArray) {
				JSONObject jsonObject = (JSONObject) object;
				JSONObject tempJSON = new JSONObject();
				tempJSON.put("name", jsonObject.getString("name"));
				tempJSON.put("sign", jsonObject.getString("sign"));
				tempJSON.put("dataType", jsonObject.getString("dataType"));
				tempJSON.put("id", jsonObject.getString("id"));
				tempJSON.put("isNotMandatory", jsonObject.getString("isNotMandatory"));
				tempJSON.put("initialValue", jsonObject.getString("initialValue"));
				facDataType.add(tempJSON);
			}
			resultJSON.put("facDataType", facDataType.toJSONString());
			String extendJSON = facJSON.getString("extend");
			resultJSON.put("extend", extendJSON);
			return ResultBuilder.successResult(resultJSON,"查询成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}
}
