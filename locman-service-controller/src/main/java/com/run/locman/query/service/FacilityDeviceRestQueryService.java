/*
 * File name: FacilityDeviceRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guolei 2017年9月15日 ... ...
 * ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.base.query.FacilitesService;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.DeviceInfoConvertQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.DeviceTypeQueryService;
import com.run.locman.api.query.service.DeviceTypeTemplateService;
import com.run.locman.api.query.service.DistributionPowersQueryService;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.query.service.FacilitiesRenovationQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.query.service.PropertiesQueryService;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.DeviceInfoConvertConstans;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.FacilityDeviceContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.filetool.ExcelView;
import com.run.usc.base.query.AccSourceBaseQueryService;

/**
 *
 * @Description: 设施与设备关系rest服务
 * @author: guolei
 * @version: 1.0, 2017年9月15日
 */
@Service
public class FacilityDeviceRestQueryService {

	private static final Logger				logger			= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilityDeviceQueryService		facilityDeviceQueryService;

	@Autowired
	private DeviceTypeQueryService			deviceTypeQueryService;

	@Autowired
	private FactoryQueryService				factoryQueryService;

	@Autowired
	private FacilitiesQueryService			facilitiesQueryService;

	@Autowired
	private AlarmInfoQueryService			alarmInfoQueryService;

	@Autowired
	private FacilitesService				facilitesService;

	@Autowired
	private PropertiesQueryService			propertiesQueryService;

	@Autowired
	private DeviceQueryService				deviceQueryService;

	@Autowired
	private DeviceInfoConvertQueryService	deviceInfoConvertQueryService;

	@Autowired
	private DeviceTypeTemplateService		deviceTypeTemplateService;

	@Autowired
	private DistributionPowersQueryService	distributionPowersQueryService;

	@Autowired
	private SimpleOrderQueryService			simpleOrderQueryService;

	@Value("${api.host}")
	private String							ip;

	@Autowired
	private HttpServletRequest				request;

	@Autowired
	private AccSourceBaseQueryService		accSourceBaseQueryService;
	
	@Autowired
	private FacilitiesRenovationQueryService		facilitiesRenovationQueryService;

	private int								returnNum		= 0;

	private long							returnTotal		= 0;

	/**
	 * 设备属性告警警告图片标签
	 */
	private static final String				WARNING_PICTURE	= "fa fa-exclamation-circle alarm-icon";
	
	
/*	private static final List<Object> serchKeys = Lists.newArrayList("5101080",
			"510108","101080",
			"51010","10108","01080",
			"5101","1010","0108","1080",
			"510","101","010","108","080",
			"51","10","01","10","08","80",
			"5","1","8","0",
			"cd2","cd",
			"c","d","2"
			);*/



	/**
	 *
	 * @Description: 查询设备绑定信息
	 * @param deviceId
	 * @return
	 */
	public Result<Map<String, Object>> queryDeviceBindingState(String deviceId) {
		logger.info(String.format("[queryDeviceBindingState()->request params-deviceId:%s]", deviceId));
		try {
			// 查询设备详情
			RpcResponse<Map<String, Object>> deviceInfo = deviceQueryService.queryDeviceBindingState(deviceId);
			if (!deviceInfo.isSuccess()) {
				logger.error(String.format("[queryDeviceBindingState()->fail:%s]", deviceInfo.getMessage()));
				return ResultBuilder.failResult(deviceInfo.getMessage());
			}
			Map<String, Object> result = deviceInfo.getSuccessValue();
			// 查询设施与设备关系
			RpcResponse<String> deviceBindingStateInfo = facilityDeviceQueryService.queryDeviceBindingState(deviceId);
			if (!deviceBindingStateInfo.isSuccess()) {
				logger.error(
						String.format("[queryDeviceBindingState()->fail:%s]", deviceBindingStateInfo.getMessage()));
				return ResultBuilder.failResult(deviceBindingStateInfo.getMessage());
			}
			String facilityId = deviceBindingStateInfo.getSuccessValue();
			if (StringUtils.isBlank(facilityId)) {
				// 无绑定信息
				result.put(FacilityDeviceContants.BINDING_STATE, null);
			} else {
				// 获取token
				String token = request.getHeader(InterGatewayConstants.TOKEN);
				// 查询设施详情
				RpcResponse<Map<String, Object>> facInfo = facilityDeviceQueryService.queryFacilityById(facilityId);
				if (!facInfo.isSuccess()) {
					logger.error(String.format("设备id：%s,查询设备绑定设施信息失败", deviceId));
					return ResultBuilder.failResult(String.format("设备id：%s,查询设备绑定设施信息失败", deviceId));
				}
				Map<String, Object> faciInfo = facInfo.getSuccessValue();
				if (null != faciInfo && !faciInfo.isEmpty()) {
					String orgId = faciInfo.get("organizationId") + "";
					List<String> allOrgParentId = accSourceBaseQueryService.findAllOrgParentId(orgId).getSuccessValue();
					JSONObject orgIdJson = new JSONObject();
					StringBuffer orgIdStr = new StringBuffer();
					for (int i = 0; i < allOrgParentId.size(); i++) {
						if (i == allOrgParentId.size() - 1) {
							orgIdStr.append(allOrgParentId.get(i));
						} else {
							orgIdStr.append(allOrgParentId.get(i) + ',');
						}
					}
					orgIdJson.put(InterGatewayConstants.ORGANIZATION_IDS, orgIdStr);

					String httpValueByPost = InterGatewayUtil
							.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, orgIdJson, ip, token);
					JSONObject valueJson = JSON.parseObject(httpValueByPost);
					StringBuffer orgAllName = new StringBuffer();
					for (int j = 0; j < allOrgParentId.size(); j++) {
						String orgName = valueJson.get(allOrgParentId.get(j)).toString();
						if (j == allOrgParentId.size() - 1) {
							orgAllName.append(orgName);
						} else {
							orgAllName.append(orgName + ',');
						}
					}
					result.put("orgAllName", orgAllName);
				}
				result.put(FacilityDeviceContants.BINDING_STATE, facInfo.getSuccessValue());
				String accessSecret = result.get("accessSecret") + "";
				// 设备最新上报信息
				RpcResponse<Map<String, Object>> map1 = facilityDeviceQueryService.findMongDbDeviceState(deviceId,
						accessSecret);
				if (map1.isSuccess()) {
					Map<String, Object> deviceState = map1.getSuccessValue();
					result.put("deviceThisState", deviceState);
				} else {
					result.put("deviceThisState", null);
				}
			}
			logger.info("[queryDeviceBindingState()->success:查询设备绑定信息成功]");
			return ResultBuilder.successResult(result, "查询设备绑定信息成功");
		} catch (Exception e) {
			logger.error("queryDeviceBindingState()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 *
	 * @Description: 查询设施绑定信息
	 * @param facilityId
	 * @return
	 */
	public Result<Map<String, Object>> queryFacilityBindingState(String facilityId) {
		logger.info(String.format("[queryFacilityBindingState()->request params--facilityId:%s]", facilityId));
		try {
			// 查询设施详情
			RpcResponse<Map<String, Object>> facInfo = facilityDeviceQueryService.queryFacilityById(facilityId);
			if (!facInfo.isSuccess()) {
				logger.error(
						String.format("[queryFacilityBindingState()->fail:设施id：%s,验证设施失败或该设施不存在,绑定失败]", facilityId));
				return ResultBuilder.failResult(String.format("设施id：%s,验证设施失败或该设施不存在,绑定失败", facilityId));
			}
			Map<String, Object> result = facInfo.getSuccessValue();
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			if (null != result && !result.isEmpty()) {
				String orgId = result.get("organizationId") + "";
				List<String> allOrgParentId = accSourceBaseQueryService.findAllOrgParentId(orgId).getSuccessValue();
				JSONObject orgIdJson = new JSONObject();
				StringBuffer orgIdStr = new StringBuffer();
				for (int i = 0; i < allOrgParentId.size(); i++) {
					if (i == allOrgParentId.size() - 1) {
						orgIdStr.append(allOrgParentId.get(i));
					} else {
						orgIdStr.append(allOrgParentId.get(i) + ',');
					}
				}
				orgIdJson.put(InterGatewayConstants.ORGANIZATION_IDS, orgIdStr);

				String httpValueByPost = InterGatewayUtil
						.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, orgIdJson, ip, token);
				JSONObject valueJson = JSON.parseObject(httpValueByPost);
				StringBuffer orgAllName = new StringBuffer();
				for (int j = 0; j < allOrgParentId.size(); j++) {
					String orgName = valueJson.get(allOrgParentId.get(j)).toString();
					if (j == allOrgParentId.size() - 1) {
						orgAllName.append(orgName);
					} else {
						orgAllName.append(orgName + ',');
					}
				}
				result.put("orgAllName", orgAllName);
			}

			// 查询设施绑定设备信息
			RpcResponse<List<String>> facilityBindingStateResult = facilityDeviceQueryService
					.queryFacilityBindingState(facilityId);
			if (!facilityBindingStateResult.isSuccess()) {
				logger.error(String.format("[queryFacilityBindingState()->fail:%s]",
						facilityBindingStateResult.getMessage()));
				return ResultBuilder.failResult(facilityBindingStateResult.getMessage());
			}
			List<String> deviceIds = facilityBindingStateResult.getSuccessValue();
			List<Map<String, Object>> deviceList = new ArrayList<>();
			if (null != deviceIds && !deviceIds.isEmpty()) {
				// 查询设备详情（增加属性点）
				RpcResponse<List<Map<String, Object>>> deviceInfo = deviceQueryService
						.queryBatchDeviceInfoForDeviceIds(deviceIds);
				if (deviceInfo.isSuccess()) {
					deviceList = deviceInfo.getSuccessValue();
				}
			}
			result.put(FacilityDeviceContants.BINDING_STATE, deviceList);
			
			Object hiddenTroubleDesc = result.get("hiddenTroubleDesc");
			Object defenseState = result.get("defenseState");
			if (StringUtils.isNotBlank(hiddenTroubleDesc + "")) {
				JSONObject parse = JSONObject.parseObject(hiddenTroubleDesc + "");
				result.put("hiddenTroubleDesc", parse);
			} else if ("3".equals(defenseState + "")) {
				//3为待整治审核中状态,此时只能从待整治信息表查找隐患描述图片等信息
				RpcResponse<JSONObject> findInfoByFacId = facilitiesRenovationQueryService.findInfoByFacId(facilityId);
				if (findInfoByFacId.isSuccess()) {
					JSONObject successValue = findInfoByFacId.getSuccessValue();
					JSONObject savedJson = successValue.getJSONObject("applicationInfo");
					String presentPic = savedJson.getString("presentPic");
					String marks = savedJson.getString("marks");
					JSONArray parseArray = savedJson.getJSONArray("hiddenTroubleDesc");
					JSONObject hiddenTroubleDescJson = new JSONObject(true);
					if (parseArray.size() > 0) {
						
						List<JSONObject> javaList = parseArray.toJavaList(JSONObject.class);
						int num = 1;
						for (JSONObject jsonObject : javaList) {
							String hiddenTroubleType = jsonObject.getString("hiddenTroubleType");
							String hiddenTroubleValue = jsonObject.getString("hiddenTroubleValue");
							
							if (StringUtils.isNotBlank(hiddenTroubleValue)) {
								StringBuffer stringBuffer = new StringBuffer();
								stringBuffer.append("值:").append(hiddenTroubleValue);
								if (hiddenTroubleDescJson.containsKey(hiddenTroubleType)) {
									hiddenTroubleDescJson.put(hiddenTroubleType + num, stringBuffer.toString());
									num++;
								} else {
									hiddenTroubleDescJson.put(hiddenTroubleType, stringBuffer.toString());
								}
							}
							
						}
						
						if (StringUtils.isBlank(marks)) {
							marks = " ";
						}
						
						hiddenTroubleDescJson.put("修改时间", DateUtils.formatDate(new Date()));
						hiddenTroubleDescJson.put("备注", marks);
						result.put("hiddenTroubleDesc", hiddenTroubleDescJson);
					}
					if (presentPic.contains("\"")) {
						presentPic = presentPic.replace("\"", "");
					}
					result.put("presentPic", presentPic);
					
				}
			}
			
			
			logger.info("[queryFacilityBindingState()->success:查询设施绑定信息成功]");
			return ResultBuilder.successResult(result, "查询设施绑定信息成功");
		} catch (Exception e) {
			logger.error("queryFacilityBindingState()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 *
	 * @Description:查询设施绑定列表
	 * @param params
	 * @return
	 */
	public Result<PageInfo<Map<String, Object>>> queryFacilitiesByPage(String params) {
		logger.info(String.format("[queryFacilitiesByPage()->request params:%s]", params));
		try {
			if (ParamChecker.isBlank(params)) {
				logger.error("[queryFacilitiesByPage()->error:参数为空]");
				return ResultBuilder.emptyResult();
			}
			if (ParamChecker.isNotMatchJson(params)) {
				logger.error("[queryFacilitiesByPage()->error:参数必须为json格式]");
				return ResultBuilder.invalidResult();
			}
			JSONObject paramsJson = JSONObject.parseObject(params);
			if (!paramsJson.containsKey(CommonConstants.PAGE_NUM) || !paramsJson.containsKey(CommonConstants.PAGE_SIZE)
					|| !paramsJson.containsKey(FacilityDeviceContants.USC_ACCESS_SECRET)
					|| !paramsJson.containsKey(FacilityDeviceContants.FACILITIES_TYPE_ID)
					|| !paramsJson.containsKey(FacilityDeviceContants.ORGANIZATION_ID)
					|| !paramsJson.containsKey(FacilityDeviceContants.BINGSTATUS)
					|| !paramsJson.containsKey(FacilityDeviceContants.SEARCH_KEY)) {
				logger.error("[queryFacilitiesByPage()->error:缺少业务参数]");
				return ResultBuilder.noBusinessResult();
			}
			int pageNo = paramsJson.getIntValue(CommonConstants.PAGE_NUM);
			int pageSize = paramsJson.getIntValue(CommonConstants.PAGE_SIZE);
			String accessSecret = paramsJson.getString(FacilityDeviceContants.USC_ACCESS_SECRET);
			String facilitiesTypeId = paramsJson.getString(FacilityDeviceContants.FACILITIES_TYPE_ID);
			String organizationId = paramsJson.getString(FacilityDeviceContants.ORGANIZATION_ID);
			String bingStatus = paramsJson.getString(FacilityDeviceContants.BINGSTATUS);
			Map<String, Object> queryParams = Maps.newHashMap();
			queryParams.put(FacilityDeviceContants.FACILITIES_TYPE_ID, facilitiesTypeId);
			queryParams.put(FacilityDeviceContants.ORGANIZATION_ID, organizationId);
			queryParams.put(FacilityDeviceContants.BINGSTATUS, bingStatus);
			// 根据停用启用状态查询
			if (paramsJson.containsKey(FacilityDeviceContants.FACILITIES_MANAGESTATE)) {
				queryParams.put(FacilityDeviceContants.FACILITIES_MANAGESTATE,
						paramsJson.getString(FacilityDeviceContants.FACILITIES_MANAGESTATE));
			}
			if (paramsJson.containsKey(FacilityDeviceContants.FACILITY_CODE)
					&& paramsJson.containsKey(FacilityDeviceContants.ADDRESS)) {
				queryParams.put(FacilityDeviceContants.FACILITY_CODE,
						paramsJson.getString(FacilityDeviceContants.FACILITY_CODE));
				queryParams.put(FacilityDeviceContants.ADDRESS, paramsJson.getString(FacilityDeviceContants.ADDRESS));
				// queryParams.put(FacilityDeviceContants.GIS,
				// paramsJson.getString(FacilityDeviceContants.GIS));
			} else if (paramsJson.containsKey(FacilityDeviceContants.SEARCH_KEY)) {
				queryParams.put(FacilityDeviceContants.SEARCH_KEY,
						paramsJson.getString(FacilityDeviceContants.SEARCH_KEY));

			} else {
				return ResultBuilder.noBusinessResult();
			}
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			queryParams.put(InterGatewayConstants.TOKEN, token);

			// 查询设施与设备关系
			RpcResponse<PageInfo<Map<String, Object>>> facilityBindListInfo = facilityDeviceQueryService
					.queryFacilityBindListByPage(accessSecret, pageNo, pageSize, queryParams);
			if (!facilityBindListInfo.isSuccess()) {
				logger.error(String.format("[queryFacilitiesByPage()->fail:%s]", facilityBindListInfo.getMessage()));
				return ResultBuilder.failResult(facilityBindListInfo.getMessage());
			}
			logger.info("[facilityBindListInfo.getMessage()->success:查询设施绑定列表成功]");
			return ResultBuilder.successResult(facilityBindListInfo.getSuccessValue(), "查询设施绑定列表成功");
		} catch (Exception e) {
			logger.error("queryFacilitiesByPage()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 *
	 * @Description:查询设备绑定列表
	 * @param params
	 * @return
	 */
	public Result<Map<String, Object>> queryDevicesByPage(String params) {

		//int returnPageNo = 0;

		logger.info(String.format("[queryDevicesByPage()->request params:%s]", params));
		try {
			if (ParamChecker.isBlank(params)) {
				logger.error("[queryDevicesByPage()->error:参数为空]");
				return ResultBuilder.emptyResult();
			}
			if (ParamChecker.isNotMatchJson(params)) {
				logger.error("[queryDevicesByPage()->error:参数必须为json格式]");
				return ResultBuilder.invalidResult();
			}
			JSONObject paramsJson = JSONObject.parseObject(params);
			Result<Object> paramJsonCheck = paramJsonCheck(paramsJson);
			if (null != paramJsonCheck) {
				return ResultBuilder.failResult(paramJsonCheck.getResultStatus().getResultMessage());
			}
			int pageNo = paramsJson.getIntValue(CommonConstants.PAGE_NUM);
			int pageSize = paramsJson.getIntValue(CommonConstants.PAGE_SIZE);
			// TODO 设备数量1
			/*if (pageNo > 36) {
				// 设置保留位数
				DecimalFormat df = new DecimalFormat("0.00");
				int ceil = (int) Math.ceil(Double.parseDouble(df.format((float) 216 / pageSize)));
				returnPageNo = pageNo;
				pageNo -= ceil;
			}*/

			String deviceId = paramsJson.getString(FacilityDeviceContants.DEVICE_ID);
			String accessSecret = paramsJson.getString(FacilityDeviceContants.USC_ACCESS_SECRET);
			String deviceTypeId = paramsJson.getString(FacilityDeviceContants.DEVICE_TYPE_ID);
			String bingStatus = paramsJson.getString(FacilityDeviceContants.BINGSTATUS);
			String factoryId = paramsJson.getString(FacilityDeviceContants.FACTORY_ID);
			String whole = paramsJson.getString(FacilityDeviceContants.WHOLE);
			String startTime = paramsJson.getString(FacilityDeviceContants.START_TIME);
			String endTime = paramsJson.getString(FacilityDeviceContants.END_TIME);
			String onLineState = paramsJson.getString(FacilityDeviceContants.ON_LINE_STATE);
			// 参数在SQL里校验是否为null或者""了
			String facilityId = paramsJson.getString(FacilityDeviceContants.FACILITY_ID);
			List<Map<String, Object>> deviceList = new ArrayList<>();
//			// 根据接入方秘钥查询厂家(启用好和停用的的厂家都需要查出来)绑定下的所有apptag
//			RpcResponse<List<String>> appTagInfo = factoryQueryService.findAppTagForAccessSecret(accessSecret);
//			if (!appTagInfo.isSuccess()) {
//				return ResultBuilder.failResult(appTagInfo.getMessage());
//			}
//			String[] appTags = appTagInfo.getSuccessValue().toArray(new String[0]);
			long total = 0L;
//			if (null != appTags && appTags.length > 0) {
				// 查询已绑定设备详情
				RpcResponse<List<Map<String, Object>>> pageResponse = deviceQueryService.queryDeviceInfoForCondition(
						pageNo, pageSize, accessSecret, deviceTypeId, bingStatus, deviceId, facilityId, factoryId,
						whole, startTime, endTime, onLineState);
				if (!pageResponse.isSuccess()) {
					logger.info("[queryDevicesByPage()->fail:" + pageResponse.getMessage() + "]");
					return ResultBuilder.failResult(pageResponse.getMessage());
				}
				deviceList = pageResponse.getSuccessValue();
				// 如果设备id不为空,精确查找设备数据
				if (!StringUtils.isBlank(deviceId)) {
					total = getDevcieIfDeviceIdNotNull(deviceId, accessSecret, deviceTypeId, bingStatus, factoryId,
							whole, startTime, endTime, onLineState, deviceList, total);
				} else {
					Result<Object> result = getDevcieIfDeviceIdisNull(deviceId, accessSecret, deviceTypeId, bingStatus,
							factoryId, whole, startTime, endTime, onLineState, deviceList, total, pageResponse);
					if (!CommonConstants.NUMBER_FOUR_ZERO.equals(result.getResultStatus().getResultCode())) {
						return ResultBuilder.failResult(result.getResultStatus().getResultMessage());
					}
					total = (long) result.getValue();
				}
//			}
			// 默认一条数据都没有为页码为0
			// TODO 设备数量1
			Map<String, Object> map = null;
			 int num = getNum(pageSize, total);

			/*if (StringUtils.isNoneBlank(deviceTypeId) && !"60b8a7bbe814dbaf7ffab0bab4b52099".equals(deviceTypeId)
					&& !"e8d9ed00278891ac4f89e96b9daf92ad".equals(deviceTypeId)) {
				returnTotal -= 216;
			}
			
			if ((StringUtils.isNotBlank(deviceId) && !serchKeys.contains(deviceId))
					|| StringUtils.isNotBlank(startTime) || StringUtils.isNotBlank(endTime)) {
				returnTotal -= 216;
			}

			returnNum = getNum(pageSize, returnTotal);
			if (returnPageNo > 36) {
				map = getMap(returnPageNo, pageSize, deviceList, returnTotal, returnNum);
			} else {
				map = getMap(pageNo, pageSize, deviceList, returnTotal, returnNum);
			}*/

			 map = getMap(pageNo, pageSize, deviceList, total, num);
			returnNum = 0;
			logger.info("[queryDevicesByPage()->success:查询设备列表信息成功]");
			return ResultBuilder.successResult(map, "查询设备列表信息成功");
		} catch (Exception e) {
			logger.error("queryDevicesByPage()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param paramsJson
	 */

	private Result<Object> paramJsonCheck(JSONObject paramsJson) {
		if (!paramsJson.containsKey(CommonConstants.PAGE_NUM) || !paramsJson.containsKey(CommonConstants.PAGE_SIZE)
				|| !paramsJson.containsKey(FacilityDeviceContants.DEVICE_ID)
				|| !paramsJson.containsKey(FacilityDeviceContants.USC_ACCESS_SECRET)
				|| !paramsJson.containsKey(FacilityDeviceContants.DEVICE_TYPE_ID)
				|| !paramsJson.containsKey(FacilityDeviceContants.BINGSTATUS)
				|| !paramsJson.containsKey(FacilityDeviceContants.FACTORY_ID)
				|| !paramsJson.containsKey(FacilityDeviceContants.START_TIME)
				|| !paramsJson.containsKey(FacilityDeviceContants.END_TIME)
				|| !paramsJson.containsKey(FacilityDeviceContants.ON_LINE_STATE)) {
			logger.error("[queryDevicesByPage()->error:缺少业务参数]");
			return ResultBuilder.noBusinessResult();
		}
		return null;
	}



	/**
	 * @Description:
	 * @param deviceId
	 * @param accessSecret
	 * @param deviceTypeId
	 * @param bingStatus
	 * @param factoryId
	 * @param whole
	 * @param startTime
	 * @param endTime
	 * @param onLineState
	 * @param deviceList
	 * @param total
	 * @param pageResponse
	 * @return
	 */

	private Result<Object> getDevcieIfDeviceIdisNull(String deviceId, String accessSecret, String deviceTypeId,
			String bingStatus, String factoryId, String whole, String startTime, String endTime, String onLineState,
			List<Map<String, Object>> deviceList, long total, RpcResponse<List<Map<String, Object>>> pageResponse) {
		if (StringUtils.isBlank(bingStatus)) {
			if (!pageResponse.isSuccess()) {
				logger.error(String.format("[queryDevicesByPage()->fail:%s]", pageResponse.getMessage()));
				return ResultBuilder.failResult(pageResponse.getMessage());
			}
			total = getTotal(deviceId, accessSecret, deviceTypeId, bingStatus, factoryId, whole, startTime, endTime,
					onLineState, deviceList, total);
			// TODO 设备数量1
			//returnTotal = total + 216;
		} else {
			RpcResponse<List<String>> deviceIdResult = facilityDeviceQueryService.queryAllBoundDeviceId();
			if (!deviceIdResult.isSuccess()) {
				logger.error(String.format("[queryDevicesByPage()->fail:%s]", deviceIdResult.getMessage()));
				return ResultBuilder.failResult(deviceIdResult.getMessage());
			}

			if (null != deviceIdResult.getSuccessValue() && bingStatus.equals(FacilityDeviceContants.BOUND)) {
				total = getTotal(deviceId, accessSecret, deviceTypeId, bingStatus, factoryId, whole, startTime, endTime,
						onLineState, deviceList, total);
				// TODO 设备数量1
				//returnTotal = total + 216;
			} else if (null != deviceIdResult.getSuccessValue() && bingStatus.equals(FacilityDeviceContants.UNBOUND)) {
				total = getTotal(deviceId, accessSecret, deviceTypeId, bingStatus, factoryId, whole, startTime, endTime,
						onLineState, deviceList, total);
				//returnTotal = total;
			}
		}
		return ResultBuilder.successResult(total, "执行成功!");
	}



	/**
	 * @Description:
	 * @param deviceId
	 * @param accessSecret
	 * @param deviceTypeId
	 * @param bingStatus
	 * @param factoryId
	 * @param whole
	 * @param startTime
	 * @param endTime
	 * @param onLineState
	 * @param deviceList
	 * @param total
	 * @return
	 */

	private long getDevcieIfDeviceIdNotNull(String deviceId, String accessSecret, String deviceTypeId,
			String bingStatus, String factoryId, String whole, String startTime, String endTime, String onLineState,
			List<Map<String, Object>> deviceList, long total) {
		@SuppressWarnings("unused")
		RpcResponse<Map<String, Object>> deviceInfo = null;
		if (StringUtils.isBlank(bingStatus)) {
			total = getTotal(deviceId, accessSecret, deviceTypeId, bingStatus, factoryId, whole, startTime, endTime,
					onLineState, deviceList, total);
			// TODO 设备数量1
			//returnTotal = total + 216;
		} else {
			RpcResponse<String> bindingStateRpc = facilityDeviceQueryService.queryDeviceBindingState(deviceId);
			if (bindingStateRpc.isSuccess()) {
				if ((FacilityDeviceContants.BOUND.equals(bingStatus)
				// 不注释的话,通过绑定状态和设施序列号搜索时会查不到数据
				) || (FacilityDeviceContants.UNBOUND.equals(bingStatus))) {
					if (StringUtils.isEmpty(bindingStateRpc.getSuccessValue())) {
						total = getTotal(deviceId, accessSecret, deviceTypeId, bingStatus, factoryId, whole, startTime,
								endTime, onLineState, deviceList, total);
						//returnTotal = total;
					}
					// TODO 设备数量1
					/*if (FacilityDeviceContants.BOUND.equals(bingStatus)) {
						returnTotal = total + 216;
					}*/

				}
			}
		}
		return total;
	}



	/**
	 * @Description:
	 * @param deviceId
	 * @param accessSecret
	 * @param deviceTypeId
	 * @param bingStatus
	 * @param factoryId
	 * @param whole
	 * @param startTime
	 * @param endTime
	 * @param onLineState
	 * @param deviceList
	 * @param total
	 * @return
	 */

	private long getTotal(String deviceId, String accessSecret, String deviceTypeId, String bingStatus,
			String factoryId, String whole, String startTime, String endTime, String onLineState,
			List<Map<String, Object>> deviceList, long total) {
		if (null != deviceList && deviceList.size() > 0) {
			total = deviceQueryService.getDeviceListNum(accessSecret, deviceTypeId, bingStatus, deviceId, factoryId,
					whole, startTime, endTime, onLineState).getSuccessValue();
		}
		return total;
	}



	/**
	 * @Description:
	 * @param pageSize
	 * @param total
	 * @return
	 */

	private int getNum(int pageSize, long total) {
		int num = 0;
		if (pageSize != 0) {
			if (total % pageSize == 0) {
				num = (int) (total / pageSize);
			} else {
				num = (int) (total / pageSize + 1);
			}
		}
		return num;
	}



	/**
	 * @Description:
	 * @param pageNo
	 * @param pageSize
	 * @param deviceList
	 * @param total
	 * @param num
	 * @return
	 */

	private Map<String, Object> getMap(int pageNo, int pageSize, List<Map<String, Object>> deviceList, long total,
			int num) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("pageNum", pageNo);
		map.put("pageSize", pageSize);
		map.put("total", total);
		map.put("pages", num);
		map.put("list", deviceList);
		return map;
	}



	/**
	 * @Description:查询设备类型列表
	 * @param accessSecret
	 * @return
	 */
	public Result<List<Map<String, Object>>> queryDeviceTypeList(String accessSecret) {
		logger.info(String.format("[queryDeviceTypeList()->request params--accessSecret:%s]", accessSecret));
		try {
			RpcResponse<List<Map<String, Object>>> result = deviceTypeQueryService.queryDeviceTypeList(accessSecret);
			result.getSuccessValue();
			if (!result.isSuccess() || result.getSuccessValue() == null) {
				logger.error(String.format("[queryDeviceTypeList()->fail:%s]", result.getMessage()));
				return ResultBuilder.failResult(result.getMessage());
			}
			List<Map<String, Object>> deviceTypeList = result.getSuccessValue().stream()
					.filter(map -> map.containsKey("parentId")).collect(Collectors.toList());
			logger.info("[queryDeviceTypeList()->:查询设备类型列表成功]");
			return ResultBuilder.successResult(deviceTypeList, "查询设备类型列表成功");
		} catch (Exception e) {
			logger.error("queryDeviceTypeList()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 *
	 * @Description:增加绑定状态
	 * @param map
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private Map<String, Object> bindStatus(Map<String, Object> map) {
		logger.info(String.format("[bindStatus()->request params:%s]", map));
		try {
			// 查询设施与设备关系
			String deviceId = String.valueOf(map.getOrDefault(FacilityDeviceContants.DEVICE_ID, null));
			RpcResponse<String> deviceBindingStateInfo = facilityDeviceQueryService.queryDeviceBindingState(deviceId);
			if (!deviceBindingStateInfo.isSuccess()) {
				map.put(FacilityDeviceContants.BINGSTATUS, "");
			}
			String facilityId = deviceBindingStateInfo.getSuccessValue();
			if (StringUtils.isBlank(facilityId)) {
				map.put(FacilityDeviceContants.BINGSTATUS, FacilityDeviceContants.UNBOUND);
				map.put(FacilityDeviceContants.FACILITIES_CODE, "");
			} else {
				RpcResponse<Facilities> findById = facilitiesQueryService.findById(facilityId);
				if (findById.isSuccess() && null != findById.getSuccessValue()) {
					map.put(FacilityDeviceContants.FACILITIES_CODE, findById.getSuccessValue().getFacilitiesCode());
				} else {
					map.put(FacilityDeviceContants.FACILITIES_CODE, "");
				}
				map.put(FacilityDeviceContants.BINGSTATUS, FacilityDeviceContants.BOUND);
			}
			// 查询厂家信息
			String appTag = String.valueOf(map.getOrDefault("appTag", null));
			if (StringUtils.isBlank(appTag)) {
				map.put(FacilityDeviceContants.FACTORY_NAME, "");
			} else {
				RpcResponse<List<String>> factoryInfo = factoryQueryService.queryFactoryByAppTag(appTag);
				if (!factoryInfo.isSuccess()) {
					map.put(FacilityDeviceContants.FACTORY_NAME, "");
				}
				if (null == factoryInfo.getSuccessValue() || factoryInfo.getSuccessValue().isEmpty()) {
					map.put(FacilityDeviceContants.FACTORY_NAME, "");
				} else {
					map.put(FacilityDeviceContants.FACTORY_NAME, factoryInfo.getSuccessValue().get(0));
				}
			}
			return map;
		} catch (Exception e) {
			logger.error("bindStatus()->Exception:", e);
			return (Map<String, Object>) Maps.newHashMap().put("Exception:", e);
		}

	}



	/**
	 *
	 * @Description:通过设施id查询设备绑定的实时状态
	 * @param
	 * @return
	 */
	public Result<Map<String, Object>> queryAllDeviceByFacId(String facInfo) {
		logger.info(String.format("[queryAllDeviceByFacId()->request params:%s]", facInfo));
		try {

			Result<String> result = ExceptionChecked.checkRequestParam(facInfo);
			if (result != null) {
				logger.error(String.format("[queryAllDeviceByFacId()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(facInfo);
			String facId = parseObject.getString(FacilitiesContants.FACILITES_ID);
			String accessSecret = parseObject.getString(FacilitiesContants.USC_ACCESS_SECRET);
			// 查询设施信息
			RpcResponse<Map<String, Object>> facilitesInfoByFacId = facilitesService.getFacilitesInfoByFacId(facId,
					request.getHeader(InterGatewayConstants.TOKEN));
			if (facilitesInfoByFacId == null || facilitesInfoByFacId.getSuccessValue() == null) {
				logger.error("[queryAllDeviceByFacId()->fail:设施不存在]");
				return ResultBuilder.failResult("设施不存在！");
			}
			RpcResponse<List<String>> queryAllDeviceByFacId = facilityDeviceQueryService.queryAllDeviceByFacId(facId,
					accessSecret);

			buildDevice(accessSecret, queryAllDeviceByFacId, facilitesInfoByFacId.getSuccessValue());
			logger.info(String.format("[queryAllDeviceByFacId()->success:%s]", MessageConstant.SEARCH_SUCCESS));
			return ResultBuilder.successResult(facilitesInfoByFacId.getSuccessValue(), MessageConstant.SEARCH_SUCCESS);
		} catch (Exception e) {
			logger.error("queryAllDeviceByFacId()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	private void buildDevice(String accessSecret, RpcResponse<List<String>> queryAllDeviceByFacId,
			Map<String, Object> facilitesInfo) throws Exception {
		logger.info(String.format("[buildDevice()->request params--accessSecret:%s]", accessSecret));
		try {
			if (queryAllDeviceByFacId != null && queryAllDeviceByFacId.isSuccess()
					&& queryAllDeviceByFacId.getSuccessValue() != null) {

				List<Map<String, Object>> listInfo = new ArrayList<>();

				List<String> successValue = queryAllDeviceByFacId.getSuccessValue();
				for (String devId : successValue) {
					if (!StringUtils.isBlank(devId)) {
						Map<String, Object> mapInfo = Maps.newHashMap();
						// mainInfo 封装主要显示信息 1.设备编号 2.状态更新时间 3.设备状态（如果存在：4.井盖状态
						// 5.内井盖状态 6.信号强度）
						Map<String, Object> mainInfo = Maps.newHashMap();
						// 封装设备真实状态
						buildDevice(accessSecret, mapInfo, devId, mainInfo);
						// 封装设备告警信息
						buildAlarmInfo(mapInfo, devId);
						// 封装设备信息
						builDeviceInfo(mapInfo, devId, mainInfo);
						mapInfo.put(CommonConstants.MAININFO, mainInfo);
						listInfo.add(mapInfo);
					}
				}

				facilitesInfo.put("devicesInfo", listInfo);
				// 封装设施绑定状态
				if (successValue.isEmpty()) {
					facilitesInfo.put("boundState", "unBound");
				} else {
					facilitesInfo.put("boundState", "bound");
				}
			}
		} catch (Exception e) {
			logger.error("buildDevice()->Exception:", e);
		}
	}



	private void builDeviceInfo(Map<String, Object> map, String deviceId, Map<String, Object> mainInfo)
			throws Exception {
		try {
			RpcResponse<Map<String, Object>> queryDeviceDetail = deviceQueryService.queryDeviceBindingState(deviceId);
			if (queryDeviceDetail != null && queryDeviceDetail.getSuccessValue() != null) {
				Map<String, Object> successValue = queryDeviceDetail.getSuccessValue();
				// 封装主要显示信息
				mainInfo.put("deviceId", successValue.remove("deviceId"));
				mainInfo.put("deviceName", successValue.remove("deviceName"));
				// 首次上报时间转换格式
				// queryDeviceBindingState 里面已经转换了
				/*
				 * String stampToDate =
				 * DateUtils.stampToDate(successValue.get("firstOnlineTime") +
				 * "000"); successValue.put("firstOnlineTime", stampToDate);
				 */
				// 封装其余信息
				map.put("deviceInfo", successValue);
			} else {
				map.put("deviceInfo", "");
			}
		} catch (Exception e) {
			logger.error("builDeviceInfo()->Exception:", e);
		}

	}



	private void buildAlarmInfo(Map<String, Object> map, String deviceId) {
		try {
			RpcResponse<Map<String, Object>> alarmInfoBydeviceId = alarmInfoQueryService
					.getAlarmInfoBydeviceId(deviceId);
			if (alarmInfoBydeviceId != null) {
				map.put("alarmInfo", alarmInfoBydeviceId.getSuccessValue());
			} else {
				map.put("alarmInfo", "");
			}
		} catch (Exception e) {
			logger.error("buildAlarmInfo()->Exception:", e);
		}

	}



	private void buildDevice(String accessSecret, Map<String, Object> map, String deviceId,
			Map<String, Object> mainInfo) throws Exception {
		try {
			// 初始化一下
			map.put("lastUpdateTime", "");
			map.put("stateInfo", null);
			// 获取设备实时数据
			RpcResponse<JSONObject> dLSInfo = deviceQueryService.queryDeviceLastState(deviceId);
			JSONObject json = dLSInfo.getSuccessValue();
			if (json == null) {
				return;
			}
			logger.info("获取设备实时数据");
			JSONObject reported = UtilTool.getReported(json);
			// JSONObject state = json.getJSONObject(AlarmInfoConstants.STATE);
			// JSONObject reported =
			// state.getJSONObject(AlarmInfoConstants.REPORTED);

			// 获取设备类型 (此处只需要查询出设备类型id)
			RpcResponse<Map<String, Object>> queryDeviceBindingState = deviceQueryService
					.queryDeviceBindingState(deviceId);
			Map<String, Object> successValue = queryDeviceBindingState.getSuccessValue();
			if (successValue == null || "".equals(successValue.get(AlarmInfoConstants.DEVICE_TYPE_ID))
					|| successValue.get(AlarmInfoConstants.DEVICE_TYPE_ID) == null) {
				return;
			}
			String deviceTypeId = successValue.get(AlarmInfoConstants.DEVICE_TYPE_ID).toString();
			// 获取设备对应的数据点
			RpcResponse<List<DeviceProperties>> findByDeviceTypeId = propertiesQueryService
					.findByDeviceTypeId(accessSecret, deviceTypeId);
			if (!findByDeviceTypeId.isSuccess() || findByDeviceTypeId.getSuccessValue() == null) {
				return;
			}
			// 查询设备告警的key集合
			RpcResponse<List<String>> keyList = alarmInfoQueryService.queryAlarmItemList(deviceId, accessSecret);
			List<String> itemKeyList = keyList.getSuccessValue();

			List<DeviceProperties> list = findByDeviceTypeId.getSuccessValue();
			List<Map<String, Object>> listInfo = new ArrayList<>();
			List<Map<String, Object>> mainStateInfoList = Lists.newArrayList();
			// 解析实时报数据
			reportedHandle(accessSecret, map, mainInfo, json, reported, itemKeyList, list, listInfo, mainStateInfoList);
		} catch (Exception e) {
			logger.error("buildDevice()->Exception:", e);
		}
	}



	/**
	 * @Description:
	 * @param accessSecret
	 * @param map
	 * @param mainInfo
	 * @param json
	 * @param reported
	 * @param itemKeyList
	 * @param list
	 * @param listInfo
	 * @param mainStateInfoList
	 */

	@SuppressWarnings("rawtypes")
	private void reportedHandle(String accessSecret, Map<String, Object> map, Map<String, Object> mainInfo,
			JSONObject json, JSONObject reported, List<String> itemKeyList, List<DeviceProperties> list,
			List<Map<String, Object>> listInfo, List<Map<String, Object>> mainStateInfoList) {
		if (reported != null) {
			Set<String> keySet = reported.keySet();
			for (DeviceProperties deviceProperties : list) {
				for (String propName : keySet) {
					// 匹配设备属性标识
					if (deviceProperties.getDevicePropertiesSign().equals(propName)) {
						Map<String, Object> datas = Maps.newHashMap();
						// 封装主要展示信息
						if (DeviceContants.FA_XGCM.equals(propName) || DeviceContants.FA_YGCM.equals(propName)) {
							Map<String, Object> mainStateInfo = Maps.newHashMap();
							mainStateInfo.put("icon", deviceProperties.getIcon());
							mainStateInfo.put("name", deviceProperties.getDevicePropertiesName());
							// 告警属性设置警告图片
							setWarningPicture(itemKeyList, propName, mainStateInfo);
							// 如果值是英文,转中文
							RpcResponse<Map> dataConvertInfo = deviceInfoConvertQueryService
									.dataConvert(String.valueOf(reported.get(propName)), accessSecret);
							String dataConvert = dataConvertInfo.isSuccess()
									? dataConvertInfo.getSuccessValue().get(DeviceInfoConvertConstans.CHINA_KEY) + ""
									: String.valueOf(reported.get(propName));
							mainStateInfo.put("value", dataConvert);
							mainStateInfoList.add(mainStateInfo);
							mainInfo.put("mainStateInfo", mainStateInfoList);
						} else {
							datas.put("icon", deviceProperties.getIcon());
							datas.put("name", deviceProperties.getDevicePropertiesName());
							// 告警属性设置警告图片
							setWarningPicture(itemKeyList, propName, datas);
							// 如果值是英文,转中文
							RpcResponse<Map> dataConvertInfo = deviceInfoConvertQueryService
									.dataConvert(String.valueOf(reported.get(propName)), accessSecret);
							String dataConvert = dataConvertInfo.isSuccess()
									? dataConvertInfo.getSuccessValue().get(DeviceInfoConvertConstans.CHINA_KEY) + ""
									: String.valueOf(reported.get(propName));
							datas.put("value", dataConvert);
							listInfo.add(datas);
						}
						break;
					}
				}
			}
		}
		// 设备上报时间
		String reportTime = "" + json.get(AlarmInfoConstants.TIMESTAMP);
		map.put("lastUpdateTime", reportTime);
		mainInfo.put("lastUpdateTime", reportTime);
		map.put("stateInfo", listInfo);
	}



	/**
	 * @Description:
	 * @param itemKeyList
	 * @param propName
	 * @param mainStateInfo
	 */

	private void setWarningPicture(List<String> itemKeyList, String propName, Map<String, Object> mainStateInfo) {
		if (null != itemKeyList && itemKeyList.size() > 0) {
			for (String key : itemKeyList) {
				if (key.equals(propName)) {
					mainStateInfo.put("icon", WARNING_PICTURE);
				}
			}
		}
	}



	public Result<Map<String, Object>> deviceCheck(String paramInfo) {
		logger.info(String.format("[deviceCheck()->request params:%s]", paramInfo));
		if (null == paramInfo || ParamChecker.isNotMatchJson(paramInfo)) {
			logger.error("deviceCheck()->fail:参数为空或格式不正确!");
			return ResultBuilder.failResult("参数为空或格式不正确!");
		}
		try {

			JSONObject parseObject = JSONObject.parseObject(paramInfo);
			String organizationId = parseObject.getString("organizationId");

			Map<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("trueOrFalse", Boolean.FALSE);
			resultMap.put("openType", null);

			// 校验该设备类型下是否配置了设备属性
			RpcResponse<Boolean> queryDeviceProperties = deviceTypeTemplateService.queryDeviceProperties(parseObject);
			if (null == queryDeviceProperties) {
				logger.error("deviceCheck()-->查询该设备类型下是否配置有设备属性失败");
				return ResultBuilder.failResult("查询该设备类型下是否配置有设备属性失败");
			}
			if (queryDeviceProperties != null && queryDeviceProperties.isSuccess()) {
				if (!StringUtils.isBlank(organizationId)) {
					logger.info("deviceCheck()->success:" + queryDeviceProperties.getMessage());
					if (queryDeviceProperties.getSuccessValue()) {
						// 校验当前设备在当前时间是否存在处理中的一般工单
						RpcResponse<Boolean> orderByDeviceId = simpleOrderQueryService.getOrderByDeviceId(parseObject);

						// 先获取该组织及其下所有组织id
						List<String> organizationIdList = Lists.newArrayList();
						Result<List<String>> idList = getIdList(organizationId, organizationIdList);
						if (!CommonConstants.NUMBER_FOUR_ZERO.equals(idList.getResultStatus().getResultCode())) {
							return ResultBuilder.failResult(idList.getResultStatus().getResultMessage());
						}

						parseObject.put("organizationId", organizationIdList);
						parseObject.put("orgIdForUser", organizationId);

						// 校验该设施类型该组织或该组织岗位在当前时间是否存在分权分域
						RpcResponse<Boolean> powersByParam = null;
						if (organizationIdList.size() > 0) {
							powersByParam = distributionPowersQueryService.getPowersByParam(parseObject);
						}
						if (null != orderByDeviceId && null != powersByParam && orderByDeviceId.isSuccess()
								&& powersByParam.isSuccess()) {

							Result<Map<String, Object>> powerCheck = powerCheck(resultMap, orderByDeviceId,
									powersByParam);
							if (null != powerCheck) {
								return powerCheck;
							}
							logger.info("deviceCheck()->success:" + "该设备在当前时间不存在处理中的工单或该设施类型该组织人员在当前时间不存在分权分域!");
							return ResultBuilder.successResult(resultMap, "该设备在当前时间不存在处理中的工单或该设施类型该组织人员在当前时间不存在分权分域!");
						} else {
							logger.error("deviceCheck()->fail:" + "校验该设备是否存在工单或分权分域失败!");
							return ResultBuilder.failResult("校验该设备是否存在工单或分权分域失败:[" + orderByDeviceId.getMessage() + "]["
									+ powersByParam.getMessage() + "]");
						}
					} else {
						logger.info("deviceCheck()->success:" + queryDeviceProperties.getMessage());
						return ResultBuilder.successResult(resultMap, queryDeviceProperties.getMessage());
					}
				} else {
					logger.info("deviceCheck()->success:超级管理员!");
					resultMap.put("trueOrFalse", Boolean.TRUE);
					resultMap.put("openType", "power");
					return ResultBuilder.successResult(resultMap, "超级管理员!");
				}
			}
			logger.error("deviceCheck()->fail:" + queryDeviceProperties.getMessage());
			return ResultBuilder.failResult(queryDeviceProperties.getMessage());
		} catch (Exception e) {
			logger.error("deviceCheck()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param resultMap
	 * @param orderByDeviceId
	 * @param powersByParam
	 */

	private Result<Map<String, Object>> powerCheck(Map<String, Object> resultMap, RpcResponse<Boolean> orderByDeviceId,
			RpcResponse<Boolean> powersByParam) {
		// 存在分权分域
		if (powersByParam.getSuccessValue()) {
			logger.info("deviceCheck()->success:" + powersByParam.getMessage());
			resultMap.put("trueOrFalse", powersByParam.getSuccessValue());
			resultMap.put("openType", "power");
			return ResultBuilder.successResult(resultMap, powersByParam.getMessage());
		}

		// 存在工单
		if (orderByDeviceId.getSuccessValue()) {
			logger.info("deviceCheck()->success:" + orderByDeviceId.getMessage());
			resultMap.put("trueOrFalse", orderByDeviceId.getSuccessValue());
			resultMap.put("openType", "taskOrder");
			return ResultBuilder.successResult(resultMap, orderByDeviceId.getMessage());
		}
		return null;
	}



	/**
	 * @Description:
	 * @param organizationId
	 * @param organizationIdList
	 */

	@SuppressWarnings("rawtypes")
	private Result<List<String>> getIdList(String organizationId, List<String> organizationIdList) {
		String httpValueByGet = InterGatewayUtil.getHttpValueByGet(
				InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip,
				request.getHeader(InterGatewayConstants.TOKEN));
		if (null == httpValueByGet) {
			logger.error("[deviceCheck()->invalid：组织查询失败!]");
			return ResultBuilder.failResult("组织查询失败!");
		} else {
			JSONArray jsonArray = JSON.parseArray(httpValueByGet);
			List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
			for (Map map : organizationInfoList) {
				organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
			}
		}
		return ResultBuilder.successResult(organizationIdList, "执行成功!");
	}



	@SuppressWarnings("unchecked")
	public ModelAndView exportDevices(String params, ModelMap model) {
		try {
			logger.info(String.format("[exportDevices->request params:%s]", params));
			if (params == null || StringUtils.isBlank(params)) {
				logger.error(
						String.format("[exportDevices->request params:%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return null;
			}
			// 构建导出格式，key值跟查询结果中的key值保持一致
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("deviceId", "设备编号");
			map.put("facilitiesCode", "设施序列号");
			map.put("deviceName", "蓝牙ID");
			map.put("hardwareId", "硬件ID");
			map.put("lastReportTime", "最新上报时间");
			map.put("deviceTypeName", "设备类型");
			map.put("bingStatus", "绑定状态");
			model.put(ExcelView.EXCEL_NAME, "设备列表导出");
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			Result<Map<String, Object>> result = queryDevicesByPage(params);

			if (CommonConstants.NUMBER_FOUR_ZERO.equals(result.getResultStatus().getResultCode())) {
				List<Map<String, Object>> res = (List<Map<String, Object>>) result.getValue().get("list");
				// 将英文值转换为中文
				for (Map<String, Object> map2 : res) {
					if (map2.get("bingStatus").equals("bound")) {
						map2.put("bingStatus", "已绑定");
					} else {
						map2.put("bingStatus", "未绑定");
					}
					
				}
				logger.info(
						String.format("[exportDevices()->success:%s]", result.getResultStatus().getResultMessage()));
				model.put(ExcelView.EXCEL_DATASET, res);
				View excelView = new ExcelView();
				return new ModelAndView(excelView);
			} else {
				logger.error(String.format("[exportDevices()->fail:%s]", result.getResultStatus().getResultMessage()));
				return null;
			}
		} catch (Exception e) {
			logger.error("[exportDevice()->Exception:]", e);
			return null;
		}

	}



	public Result<List<Map<String, Object>>> findOnlineQuery(String reportType) {

		try {
			RpcResponse<List<Map<String, Object>>> listMap1 = facilityDeviceQueryService.onlineQuery(reportType);
			List<Map<String, Object>> listMap = listMap1.getSuccessValue();
			for (Map<String, Object> map : listMap) {
				String deviceId = map.get("deviceId") + "";
				String accessSecret = map.get("accessSecret") + "";
				Map<String, Object> deviceState = facilityDeviceQueryService
						.findMongDbDeviceState(deviceId, accessSecret).getSuccessValue();
				if (null != deviceState && !deviceState.isEmpty()) {
					String lanya = deviceState.get("蓝牙名称") + "";
					map.put("蓝牙名称", lanya);
				} else {
					map.put("蓝牙名称", null);
				}
			}
			return ResultBuilder.successResult(listMap, "成功！");
		} catch (Exception e) {
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> queryDevicesByRule(String params) {
		logger.info(String.format("[queryDevicesByRule()->request params:%s]", params));
		try {
			if (ParamChecker.isBlank(params)) {
				logger.error("[queryDevicesByRule()->error:参数为空]");
				return ResultBuilder.emptyResult();
			}
			if (ParamChecker.isNotMatchJson(params)) {
				logger.error("[queryDevicesByRule()->error:参数必须为json格式]");
				return ResultBuilder.invalidResult();
			}
			JSONObject paramsJson = JSONObject.parseObject(params);
			// Result<Object> paramJsonCheck = paramJsonCheck(paramsJson);
			// if (null != paramJsonCheck) {
			// return
			// ResultBuilder.failResult(paramJsonCheck.getResultStatus().getResultMessage());
			// }
			int pageNo = paramsJson.getIntValue(CommonConstants.PAGE_NUM);
			int pageSize = paramsJson.getIntValue(CommonConstants.PAGE_SIZE);
			String accessSecret = paramsJson.getString(FacilityDeviceContants.USC_ACCESS_SECRET);
			String alarmDesc = paramsJson.getString("alarmDesc");
			long total = Long.parseLong(paramsJson.getString("total"));
			// String factoryId =
			// paramsJson.getString(FacilityDeviceContants.FACTORY_ID);
			List<Map<String, Object>> deviceList = new ArrayList<>();
			// 根据接入方秘钥查询厂家(启用好和停用的的厂家都需要查出来)绑定下的所有apptag
			RpcResponse<List<String>> appTagInfo = factoryQueryService.findAppTagForAccessSecret(accessSecret);
			if (!appTagInfo.isSuccess()) {
				return ResultBuilder.failResult(appTagInfo.getMessage());
			}
			String[] appTags = appTagInfo.getSuccessValue().toArray(new String[0]);

			if (null != appTags && appTags.length > 0) {
				// 查询已绑定设备详情
				RpcResponse<List<Map<String, Object>>> pageResponse = deviceQueryService.queryDeviceByRule(pageNo,
						pageSize, accessSecret, alarmDesc);
				if (!pageResponse.isSuccess()) {
					logger.info("[queryDevicesByPage()->fail:" + pageResponse.getMessage() + "]");
					return ResultBuilder.failResult(pageResponse.getMessage());
				}
				deviceList = pageResponse.getSuccessValue();
			}
			// 默认一条数据都没有为页码为0
			int num = getNum(pageSize, total);
			Map<String, Object> map = getMap(pageNo, pageSize, deviceList, total, num);

			logger.info("[queryDevicesByRule()->success:查询设备列表信息成功]");
			return ResultBuilder.successResult(map, "查询设备列表信息成功");
		} catch (Exception e) {
			logger.error("queryDevicesByRule()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	@SuppressWarnings("unchecked")
	public ModelAndView exportDevicesByRule(String params, ModelMap model) {
		try {
			logger.info(String.format("[exportDevicesByRule->request params:%s]", params));
			if (params == null || StringUtils.isBlank(params)) {
				logger.error(String.format("[exportDevicesByRule->request params:%s]",
						LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return null;
			}
			// 构建导出格式，key值跟查询结果中的key值保持一致
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("deviceId", "设备编号");
			map.put("facilitiesCode", "设施序列号");
			map.put("address", "设施地址");
			map.put("deviceTypeName", "设备类型");
			map.put("alarmTime", "规则最新告警时间");
			map.put("deviceName", "蓝牙ID");
			map.put("hardwareId", "硬件ID");
			map.put("alarmDesc", "告警规则");
			model.put(ExcelView.EXCEL_NAME, "设备列表导出");
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			Result<Map<String, Object>> result = queryDevicesByRule(params);

			if (CommonConstants.NUMBER_FOUR_ZERO.equals(result.getResultStatus().getResultCode())) {
				List<Map<String, Object>> res = (List<Map<String, Object>>) result.getValue().get("list");

				logger.info(String.format("[exportDevicesByRule()->success:%s]",
						result.getResultStatus().getResultMessage()));
				model.put(ExcelView.EXCEL_DATASET, res);
				View excelView = new ExcelView();
				return new ModelAndView(excelView);
			} else {
				logger.error(
						String.format("[exportDevicesByRule()->fail:%s]", result.getResultStatus().getResultMessage()));
				return null;
			}
		} catch (Exception e) {
			logger.error("[exportDevicesByRule()->Exception:]", e);
			return null;
		}

	}
}
