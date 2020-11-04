/*
 * File name: DeviceRealStateQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年11月7日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Pagination;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceProperties;
import com.run.locman.api.entity.Pages;
import com.run.locman.api.query.service.DeviceInfoConvertQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.PropertiesQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceInfoConvertConstans;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.filetool.ExcelView;

/**
 * @Description: 设备状态查询
 * @author: qulong
 * @version: 1.0, 2017年11月7日
 */
@Service
public class DeviceRealStateQueryService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private PropertiesQueryService			propertiesQueryService;

	@Autowired
	private DeviceQueryService				deviceQueryService;

	@Autowired
	private DeviceInfoConvertQueryService	deviceInfoConvertQueryService;



	@SuppressWarnings("rawtypes")
	public Result<Map<String, Object>> getRealState(String accessSecret, String deviceId) {
		logger.info(String.format("[getRealState()->request params--deviceId:%s,%s]", deviceId, deviceId));
		if (ParamChecker.isBlank(deviceId) || ParamChecker.isBlank(accessSecret)) {
			logger.error(String.format("[getRealState()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.emptyResult();
		}
		try {
			// 获取设备实时数据
			RpcResponse<JSONObject> dLSInfo = deviceQueryService.queryDeviceLastState(deviceId);
			if (!dLSInfo.isSuccess()) {
				logger.error("[getRealState()->fail:设备查询失败]");
				return ResultBuilder.failResult(dLSInfo.getMessage());
			}
			JSONObject json = dLSInfo.getSuccessValue();
			if (json == null || json.isEmpty()) {
				logger.error("[getRealState()->fail:设备不存在]");
				return ResultBuilder.failResult("设备不存在！");
			}
			JSONObject reported = UtilTool.getReported(json);
			List<String> listDeviceId = new ArrayList<>();
			listDeviceId.add(deviceId);
			// 获取设备类型
			RpcResponse<List<Map<String, Object>>> deviceDetail = deviceQueryService
					.queryBatchDeviceInfoForDeviceIds(listDeviceId);
			List<Map<String, Object>> successValue = deviceDetail.getSuccessValue();
			if (successValue == null || !deviceDetail.isSuccess()) {
				logger.error("[getRealState()->fail:设备类型不存在]");
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
			// 获取设备对应的数据点
			RpcResponse<List<DeviceProperties>> findByDeviceTypeId = propertiesQueryService
					.findByDeviceTypeId(accessSecret, deviceTypeId);
			if (!findByDeviceTypeId.isSuccess()) {
				logger.error("[getRealState()->fail:数据点查询失败]");
				return ResultBuilder.failResult(findByDeviceTypeId.getMessage());
			}
			List<DeviceProperties> list = findByDeviceTypeId.getSuccessValue();
			if (list == null || list.size() == 0) {
				logger.error("[getRealState()->fail:数据点结果集为空]");
				return ResultBuilder.failResult("数据点结果集为空");
			}
			Map<String, Object> datas = Maps.newLinkedHashMap();
			// 解析实时报数据说
			if (reported != null) {
				Set<String> keySet = reported.keySet();
				for (DeviceProperties deviceProperties : list) {
					for (String propName : keySet) {
						if (deviceProperties.getDevicePropertiesSign().equals(propName)) {
							// 如果值是英文,转中文
							RpcResponse<Map> dataConvertInfo = deviceInfoConvertQueryService
									.dataConvert(String.valueOf(reported.get(propName)), accessSecret);
							String dataConvert = dataConvertInfo.isSuccess()
									? dataConvertInfo.getSuccessValue().get(DeviceInfoConvertConstans.CHINA_KEY) + ""
									: String.valueOf(reported.get(propName));
							// 封装数据
							datas.put(deviceProperties.getDevicePropertiesName(), dataConvert);
							break;
						}
					}
				}
			}
			logger.info("getRealState()->success:实时状态查询成功!");
			return ResultBuilder.successResult(datas, "查询成功!");
		} catch (Exception e) {
			logger.error("getRealState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Pagination<Map<String, Object>>> getHistoryStateList(String params) {
		try {
			if (ParamChecker.isNotMatchJson(params)) {
				logger.error("[getHistoryStateList()->fail:参数需为json格式!]");
				return ResultBuilder.failResult("参数需为json格式!");
			}
			JSONObject json = JSONObject.parseObject(params);
			if (StringUtils.isBlank(json.getString(CommonConstants.ACCESSSECRET))) {
				logger.error("[getHistoryStateList()->fail:接入方秘钥accessSecret不能为空!]");
				return ResultBuilder.failResult("接入方秘钥accessSecret不能为空!");
			}
			String accessSecret = json.getString(CommonConstants.ACCESSSECRET);
			String deviceId = json.getString(CommonConstants.DEVICEID);
			String pageNum = json.getString(CommonConstants.PAGE_NUM);
			String pageSize = json.getString(CommonConstants.PAGE_SIZE);
			String startTime = json.getString(CommonConstants.STARTTIME);
			String endTime = json.getString(CommonConstants.ENDTIME);

			// 获取设备历史上报数据
			RpcResponse<Pagination<Map<String, Object>>> historySate = deviceQueryService.getHistorySateByPage(deviceId,
					pageNum, pageSize, startTime, endTime);
			if (!historySate.isSuccess() || null == historySate.getSuccessValue()) {
				logger.error("[getRealState()->fail:设备历史数据上报状态查询失败！]");
				return ResultBuilder.failResult(historySate.getMessage());
			}
			Pagination<Map<String, Object>> state = historySate.getSuccessValue();
			List<Map<String, Object>> data = state.getDatas();
			if (null == data || data.isEmpty()) {
				logger.error("[getRealState()->fail:未查询到该设备的历史数据上报状态!]");
				return ResultBuilder.failResult("未查询到该设备的历史数据上报状态!");
			}

			List<String> listDeviceId = new ArrayList<>();
			listDeviceId.add(deviceId);
			// 获取设备类型
			RpcResponse<List<Map<String, Object>>> deviceDetail = deviceQueryService
					.queryBatchDeviceInfoForDeviceIds(listDeviceId);
			List<Map<String, Object>> successValue = deviceDetail.getSuccessValue();
			if (successValue == null || !deviceDetail.isSuccess()) {
				logger.error("[getRealState()->fail:设备类型不存在]");
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
			// 获取设备对应的数据点
			RpcResponse<List<DeviceProperties>> findByDeviceTypeId = propertiesQueryService
					.findByDeviceTypeId(accessSecret, deviceTypeId);
			if (!findByDeviceTypeId.isSuccess()) {
				logger.error("[getRealState()->fail:数据点查询失败]");
				return ResultBuilder.failResult(findByDeviceTypeId.getMessage());
			}
			List<DeviceProperties> list = findByDeviceTypeId.getSuccessValue();
			if (list == null || list.size() == 0) {
				logger.error("[getRealState()->fail:数据点结果集为空]");
				return ResultBuilder.failResult("数据点结果集为空");
			}

			// 封装数据
			List<Map<String, Object>> resultList = new ArrayList<>();
			dataHandle(accessSecret, data, list, resultList);

			state.setDatas(resultList);
			logger.info("getRealState()->success:设备历史数据上报状态查询成功!");
			return ResultBuilder.successResult(state, "设备历史数据上报状态查询成功!");

		} catch (Exception e) {
			logger.error("getRealState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param accessSecret
	 * @param data
	 * @param list
	 * @param resultList
	 */

	private void dataHandle(String accessSecret, List<Map<String, Object>> data, List<DeviceProperties> list,
			List<Map<String, Object>> resultList) {
		for (Map<String, Object> mapState : data) {
			JSONObject stateJson = JSONObject.parseObject(mapState.toString());
			JSONObject reported = UtilTool.getReported(stateJson);
			String timestamp = stateJson.getString("timestamp");
			String reportedTime = "";
			if (!StringUtils.isBlank(timestamp)) {
				reportedTime = UtilTool.timeStampToDate(Long.parseLong(timestamp));
			}
			int count = 0;
			Map<String, Object> datas = Maps.newLinkedHashMap();
			// 解析历史上报数据
			reportedHandle(accessSecret, list, reported, reportedTime, count, datas);
			resultList.add(datas);
		}
	}



	/**
	 * @Description:
	 * @param accessSecret
	 * @param list
	 * @param reported
	 * @param reportedTime
	 * @param count
	 * @param datas
	 */

	@SuppressWarnings("rawtypes")
	private void reportedHandle(String accessSecret, List<DeviceProperties> list, JSONObject reported,
			String reportedTime, int count, Map<String, Object> datas) {
		if (reported != null) {
			Set<String> keySet = reported.keySet();
			for (DeviceProperties deviceProperties : list) {
				for (String propName : keySet) {
					if (deviceProperties.getDevicePropertiesSign().equals(propName)) {
						if (count == 0) {
							datas.put("上报时间", reportedTime);
							count++;
						}
						// 如果值是英文,转中文
						RpcResponse<Map> dataConvertInfo = deviceInfoConvertQueryService
								.dataConvert(String.valueOf(reported.get(propName)), accessSecret);
						String dataConvert = dataConvertInfo.isSuccess()
								? dataConvertInfo.getSuccessValue().get(DeviceInfoConvertConstans.CHINA_KEY) + ""
								: String.valueOf(reported.get(propName));
						// 封装数据
						datas.put(deviceProperties.getDevicePropertiesName(), dataConvert);
						break;
					}
				}
			}
		}
	}



	public ModelAndView exportHistoryStateList(String params, ModelMap model) {
		try {
			if (ParamChecker.isNotMatchJson(params)) {
				logger.error("[exportHistoryStateList()->fail:参数需为json格式!]");
				return null;
			}
			JSONObject json = JSONObject.parseObject(params);
			String accessSecret = json.getString("accessSecret");
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[exportHistoryStateList()->fail:接入方秘钥accessSecret不能为空!]");
				return null;
			}
			String deviceId = json.getString("deviceId");
			if (StringUtils.isBlank(deviceId)) {
				logger.error("[exportHistoryStateList()->fail:接入方秘钥accessSecret不能为空!]");
				return null;
			}
			// 分页查询接口,页大小和页数为0时查询所有数据
			String pageNum = "0";
			String pageSize = "0";
			String startTime = json.getString("startTime");
			String endTime = json.getString("endTime");

			List<String> listDeviceId = new ArrayList<>();
			listDeviceId.add(deviceId);
			// 获取设备类型
			RpcResponse<Device> queryDeviceByDeviceId = deviceQueryService.queryDeviceInfoById(deviceId);
			if (null == queryDeviceByDeviceId || !queryDeviceByDeviceId.isSuccess()
					|| null == queryDeviceByDeviceId.getSuccessValue()) {
				logger.error("[exportHistoryStateList()->fail:查询设备类型失败]");
				return null;
			}
			Device device = queryDeviceByDeviceId.getSuccessValue();

			String deviceType = device.getDeviceType();
			String deviceName = device.getDeviceName();
			// 获取设备对应的数据点
			RpcResponse<List<DeviceProperties>> findByDeviceTypeId = propertiesQueryService
					.findByDeviceTypeId(accessSecret, deviceType);
			if (!findByDeviceTypeId.isSuccess()) {
				logger.error("[exportHistoryStateList()->fail:数据点查询失败]");
				return null;
			}
			List<DeviceProperties> list = findByDeviceTypeId.getSuccessValue();
			if (list == null || list.isEmpty()) {
				logger.error("[exportHistoryStateList()->fail:数据点结果集为空]");
				return null;
			}

			Map<String, Object> excelMap = Maps.newLinkedHashMap();
			// 组装Excel表头
			setExcelMap(model, deviceName, list, excelMap);

			// 获取设备历史上报数据
			RpcResponse<Pagination<Map<String, Object>>> historySate = deviceQueryService.getHistorySateByPage(deviceId,
					pageNum, pageSize, startTime, endTime);
			if (!historySate.isSuccess() || null == historySate.getSuccessValue()) {
				logger.error("[exportHistoryStateList()->fail:设备历史数据上报状态查询失败！]");
				return null;
			}
			Pagination<Map<String, Object>> state = historySate.getSuccessValue();
			List<Map<String, Object>> data = state.getDatas();
			if (null == data || data.isEmpty()) {
				logger.error("[exportHistoryStateList()->fail:未查询到该设备的历史数据上报状态!]");
				return null;
			} else {
				List<JSONObject> resultList = Lists.newArrayList();
				// 组装数据
				setResult(model, state, data, resultList);
			}

			View excelView = new ExcelView();
			return new ModelAndView(excelView);

		} catch (Exception e) {
			logger.error("exportHistoryStateList()->exception", e);
			return null;
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void setExcelMap(ModelMap model, String deviceName, List<DeviceProperties> list,
			Map<String, Object> excelMap) {
		excelMap.put("reportTime", "上报时间");
		for (DeviceProperties deviceProperties : list) {
			if (StringUtils.isNotBlank(deviceProperties.getDevicePropertiesSign())
					&& StringUtils.isNotBlank(deviceProperties.getDevicePropertiesName())) {
				excelMap.put(deviceProperties.getDevicePropertiesSign(), deviceProperties.getDevicePropertiesName());
			}
		}
		model.put(ExcelView.EXCEL_NAME, deviceName + "设备历史状态信息表");
		model.put(ExcelView.EXCEL_COLUMNHEADING, excelMap);
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void setResult(ModelMap model, Pagination<Map<String, Object>> state, List<Map<String, Object>> data,
			List<JSONObject> resultList) {
		for (Map<String, Object> map : data) {
			JSONObject stateJson = JSONObject.parseObject(map.toString());
			JSONObject reported = UtilTool.getReported(stateJson);
			String timestamp = stateJson.getString("timestamp");
			String reportedTime = "";
			if (!StringUtils.isBlank(timestamp)) {
				reportedTime = UtilTool.timeStampToDate(Long.parseLong(timestamp));
			}
			reported.put("reportTime", reportedTime);
			resultList.add(reported);
		}

		logger.info(String.format("[exportAllAlarmOrder()->success:共查询到%s条数据]", state.getTotalCount()));
		model.put(ExcelView.EXCEL_DATASET, resultList);
	}



	public Result<PageInfo<Map<String, Object>>> getCountDeviceRealState(String params) {
		try {
			if (StringUtils.isBlank(params)) {
				logger.error("[getCountDeviceRealState()->fail:参数不能为空!]");
				return ResultBuilder.failResult("参数不能为空!");
			}
			if (ParamChecker.isNotMatchJson(params)) {
				logger.error("[getCountDeviceRealState()->fail:参数需为json格式!]");
				return ResultBuilder.failResult("参数需为json格式!");
			}
			JSONObject json = JSONObject.parseObject(params);
			RpcResponse<PageInfo<Map<String, Object>>> result = deviceQueryService.getCountDeviceRealState(json);
			if (null != result && result.isSuccess()) {
				logger.info("getRealState()->success:" + result.getMessage() + "");
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			} else if (null != result) {
				logger.info("getRealState()->success:" + result.getMessage() + "");
				return ResultBuilder.failResult(result.getMessage());
			}
			logger.info("getRealState()->success:查询失败!");
			return ResultBuilder.failResult("查询失败!");

		} catch (Exception e) {
			logger.error("getRealState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public ModelAndView exportCountDeviceRealState(String params, ModelMap model) {
		try {
			if (ParamChecker.isNotMatchJson(params)) {
				logger.error("[exportCountDeviceRealState()->fail:参数需为json格式!]");
				return null;
			}
			JSONObject json = JSONObject.parseObject(params);
			String accessSecret = json.getString("accessSecret");
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[exportCountDeviceRealState()->fail:接入方秘钥accessSecret不能为空!]");
				return null;
			}

			Map<String, Object> excelMap = Maps.newLinkedHashMap();
			excelMap.put("deviceId", "设备编号");
			excelMap.put("deviceTypeName", "设备类型");
			excelMap.put("facilitiesCode", "设施序列号");
			excelMap.put("completeAddress", "区域");
			excelMap.put("address", "地址");
			excelMap.put("device_bv", "电池电压");
			excelMap.put("device_sig", "信号值");
			excelMap.put("device_ls", "锁状态");
			excelMap.put("device_rsrp", "信号接收功率");
			excelMap.put("device_sinr", "信噪比");

			model.put(ExcelView.EXCEL_NAME, "设备实时状态统计信息表");
			model.put(ExcelView.EXCEL_COLUMNHEADING, excelMap);

			// 获取设备实战上报数据
			RpcResponse<List<Map<String, Object>>> countDeviceRealState = deviceQueryService
					.exportCountDeviceRealState(json);
			List<Map<String, Object>> successValue = countDeviceRealState.getSuccessValue();
			if (!countDeviceRealState.isSuccess() || null == successValue) {
				logger.error("[exportCountDeviceRealState()->fail:设备数据上报状态查询失败！]");
				return null;
			}
			if (successValue.isEmpty()) {
				logger.error("[exportCountDeviceRealState()->fail:未查询到设备数据上报状态!]");
				return null;
			}
			model.put(ExcelView.EXCEL_DATASET, successValue);

			View excelView = new ExcelView();
			return new ModelAndView(excelView);

		} catch (Exception e) {
			logger.error("exportCountDeviceRealState()->exception", e);
			return null;
		}
	}
	
	public Result<Pages<Map<String, Object>>> countDeviceTimingTrigger(String params) {
		try {
			if (StringUtils.isBlank(params)) {
				logger.error("[countDeviceTimingTrigger()->fail:参数不能为空!]");
				return ResultBuilder.failResult("参数不能为空!");
			}
			if (ParamChecker.isNotMatchJson(params)) {
				logger.error("[countDeviceTimingTrigger()->fail:参数需为json格式!]");
				return ResultBuilder.failResult("参数需为json格式!");
			}
			
			JSONObject json = JSONObject.parseObject(params);
			RpcResponse<Pages<Map<String, Object>>> result = deviceQueryService.countDeviceTimingTrigger(json);
			if (null != result && result.isSuccess()) {
				logger.info("countDeviceTimingTrigger()->success:" + result.getMessage() + "");
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			} else if (null != result) {
				logger.info("countDeviceTimingTrigger()->success:" + result.getMessage() + "");
				return ResultBuilder.failResult(result.getMessage());
			}
			logger.info("countDeviceTimingTrigger()->success:查询失败!");
			return ResultBuilder.failResult("查询失败!");

		} catch (Exception e) {
			logger.error("countDeviceTimingTrigger()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
