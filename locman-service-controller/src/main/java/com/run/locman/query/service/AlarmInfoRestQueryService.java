/*
 * File name: AlarmInfoRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 lkc 2017年10月31日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.ParamChecker;
import com.run.common.util.StringUtil;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.Pages;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.PublicConstants;
import com.run.locman.filetool.ExcelView;
import com.run.locman.thread.AlarmInfoCallable;
import com.run.locman.thread.ThreadSingleCase;

/**
 * @Description: 告警信息查询
 * @author: lkc
 * @version: 1.0, 2017年10月31日
 */
@Service
public class AlarmInfoRestQueryService {
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private AlarmInfoQueryService	alarmInfoQueryService;

	@Value("${api.host}")
	private String					ip;

	@Autowired
	private HttpServletRequest		request;

	private static final int		NUMBER	= 20000;



	/**
	 * @Description:分页查询设施告警信息及状态
	 * @param deviceTypeId
	 * @return
	 */

	public Result<Pages<Map<String, Object>>> getAlarmInfoByPage(String alarmInfo) {
		try {
			logger.info(String.format("[getAlarmInfoByPage()->request params:%s]", alarmInfo));
			Result<String> result = ExceptionChecked.checkRequestParam(alarmInfo);
			if (result != null) {
				logger.error(String.format("[getAlarmInfoByPage()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(alarmInfo);
			parseObject.put(InterGatewayConstants.IP, ip);
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			parseObject.put(InterGatewayConstants.TOKEN, token);

			/*
			 * RpcResponse<PageInfo<Map<String, Object>>> res =
			 * alarmInfoQueryService.getAlarmInfoBypage(parseObject);
			 */
			RpcResponse<Pages<Map<String, Object>>> res = alarmInfoQueryService.getAlarmInfoBypage(parseObject);

			if (res.isSuccess()) {
				logger.info("[getAlarmInfoByPage()->success:告警信息查询成功]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[getAlarmInfoByPage()->fail:告警信息查询失败]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("getAlarmInfoByPage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:通过设施code 查询设备的情况
	 * @param alarmInfo
	 * @return
	 */

	public Result<PageInfo<Map<String, Object>>> getAlarmInfoByfacId(String alarmInfo) {
		try {
			logger.info(String.format("[getAlarmInfoByfacId()->request params:%s]", alarmInfo));
			Result<String> result = ExceptionChecked.checkRequestParam(alarmInfo);
			if (result != null) {
				logger.error(String.format("[getAlarmInfoByfacId()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(alarmInfo);
			RpcResponse<PageInfo<Map<String, Object>>> res = alarmInfoQueryService
					.getAlarmInfoByFacilitesId(parseObject);
			if (res.isSuccess()) {
				logger.info("[getAlarmInfoByfacId()->success:告警信息查询成功]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[getAlarmInfoByfacId()->fail:告警信息查询失败]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getAlarmInfoByfacId()->exception:%s]", e));
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Pages<Map<String, Object>>> getAlarmInfoList(String alarmInfo) {
		try {
			logger.info(String.format("[getAlarmInfoList()->request params:%s]", alarmInfo));
			Result<String> result = ExceptionChecked.checkRequestParam(alarmInfo);
			if (result != null) {
				logger.error(String.format("[getAlarmInfoList()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(alarmInfo);
			parseObject.put(InterGatewayConstants.IP, ip);
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			parseObject.put(InterGatewayConstants.TOKEN, token);
			RpcResponse<Pages<Map<String, Object>>> res = alarmInfoQueryService.getAlarmInfoList(parseObject);

			if (res.isSuccess()) {
				logger.info("[getAlarmInfoList()->success:告警信息查询成功]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[getAlarmInfoList()->fail:告警信息查询失败]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("getAlarmInfoList()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param params
	 * @return
	 */

	public Result<List<Map<String, Object>>> getNearlyAlarmInfo(String params) {
		logger.info(String.format("[getNearlyAlarmInfo()->request params:%s]", params));
		if (ParamChecker.isBlank(params)) {
			logger.error(String.format("[getNearlyAlarmInfo()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.noBusinessResult();
		}
		JSONObject json = JSONObject.parseObject(params);
		String organizationId = json.getString("organizationId");
		String accessSecret = json.getString("accessSecret");
		if (StringUtil.isEmpty(organizationId) && StringUtil.isEmpty(accessSecret)) {
			logger.error("[getNearlyAlarmInfo()->error:接入方ID和组织ID不能同时为空]");
			return ResultBuilder.failResult("接入方ID和组织ID不能同时为空");
		}
		try {
			RpcResponse<List<Map<String, Object>>> nearlyAlarmInfo = alarmInfoQueryService
					.getNearlyAlarmInfo(organizationId, accessSecret);
			if (nearlyAlarmInfo.isSuccess()) {
				logger.info("[getNearlyAlarmInfo()->success:告警信息查询成功]");
				return ResultBuilder.successResult(nearlyAlarmInfo.getSuccessValue(), nearlyAlarmInfo.getMessage());
			}
			logger.error("[getNearlyAlarmInfo()->fail:告警信息查询失败]");
			return ResultBuilder.failResult(nearlyAlarmInfo.getMessage());
		} catch (Exception e) {
			logger.error("getNearlyAlarmInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<PageInfo<Map<String, Object>>> statisticsAlarmInfo(String params) {
		try {
			logger.info(String.format("[statisticsAlarmInfo()->requset param:%s]", params));
			Result<String> result = ExceptionChecked.checkRequestParam(params);
			if (result != null) {
				logger.error(String.format("[statisticsAlarmInfo()->requset param:%s]",
						LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(params);
			parseObject.put(InterGatewayConstants.IP, ip);
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			parseObject.put(InterGatewayConstants.TOKEN, token);
			RpcResponse<PageInfo<Map<String, Object>>> res = alarmInfoQueryService.statisticsAlarmInfo(parseObject, "");
			if (res.isSuccess()) {
				logger.info(String.format("[statisticsAlarmInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[statisticsAlarmInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getAlarmInfoList()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public ModelAndView exportAlarmInfo(String params, ModelMap model) {
		try {
			logger.info(String.format("[exportAlarmInfo->request params:%s]", params));
			if (params == null || StringUtils.isBlank(params)) {
				logger.error(String.format("[exportOpenRecordInfo->request params:%s]",
						LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return null;
			}

			JSONObject jsonObject = JSONObject.parseObject(params);
			String accessSecret = jsonObject.getString(AlarmInfoConstants.USC_ACCESS_SECRET);
			String totalCount = jsonObject.getString(AlarmInfoConstants.TOTAL_COUNT);
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("[exporAlarmInfo()->:requset params:接入方密匙不能为空]");
				return null;
			}
			if (!StringUtils.isNumeric(totalCount)) {
				logger.error("[exporAlarmInfo()->:requset params:totalCount必须为数字！]");
				return null;
			}
			jsonObject.put(InterGatewayConstants.IP, ip);
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			jsonObject.put(InterGatewayConstants.TOKEN, token);

			// 构建导出格式
			Map<String, Object> map = new LinkedHashMap<>();
			map.put(AlarmInfoConstants.FACILITES_CODE, AlarmInfoConstants.FACILITES_CODE_CH);
			map.put(AlarmInfoConstants.ALARMI_LEVEL, AlarmInfoConstants.ALARMI_LEVEL_CH);
//			map.put(AlarmInfoConstants.SERIAL_NUM, AlarmInfoConstants.SERIAL_NUM_CH);
//			map.put(AreaConstants.AREA_NAME, AreaConstants.AREA_NAME_CH);
			map.put(AlarmInfoConstants.FACILITIES_ADDRESS,AlarmInfoConstants.FACILITIES_ADDRESS_CH);
			map.put(AlarmInfoConstants.FACILITIES_TYPE, AlarmInfoConstants.FACILITIES_TYPE_CH);
			map.put(AlarmInfoConstants.ORG_NAME, AlarmInfoConstants.ORG_NAME_CH);
			map.put(AlarmInfoConstants.ALARM_TIME, AlarmInfoConstants.ALARM_TIME_CH);
			map.put(AlarmInfoConstants.ALARM_DESC, AlarmInfoConstants.ALARM_DESC_CH);

			model.put(ExcelView.EXCEL_NAME, AlarmInfoConstants.EXCEL_NAME);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);
			// 线程模式
			List<Map<String, Object>> pottingExportData = pottingExportData(jsonObject, Integer.valueOf(totalCount));

			if (pottingExportData == null || pottingExportData.isEmpty()) {
				logger.error("[exportAlarmInfo()->error:封装数据失败！]");
				return null;
			}
			model.put(ExcelView.EXCEL_DATASET, pottingExportData);

			View excelView = new ExcelView();
			logger.info("[exportAlarmInfo()->info:导出成功！]");
			ModelAndView modelAndView = new ModelAndView(excelView);
			return modelAndView;

		} catch (Exception e) {
			logger.error("[exportAlarmInfo()->exception]", e);
			ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
			model.addAttribute(PublicConstants.RESULT_CODE, "0005");
			mav.addObject(PublicConstants.RESULT_DATA, e.getMessage());
			return mav;
		}
	}



	/**
	 * 
	 * @Description:计算总条数 并且返回开启多少个线程
	 * @param totalCount
	 * @return
	 * @throws Exception
	 */
	private Integer totalCountForThread(Integer totalCount) throws Exception {
		if (totalCount <= NUMBER) {
			return 0;
		}
		// 计算需要多少个线程最大线程数
		Integer totalThread = totalCount / NUMBER;
		// 预留+1
		return totalThread + 1;
	}



	/**
	 * 
	 * @Description: 线程并发获取数据
	 * @param jsonObject
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> pottingExportData(JSONObject jsonObject, Integer totalThread) throws Exception {
		List<Map<String, Object>> alarmInfoList = Lists.newArrayList();
		Integer totalCountForThread = totalCountForThread(totalThread);

		if (totalCountForThread.equals(0)) {
			return statisticsAlarmInfo(jsonObject);
		}

		// 获取线程池执行任务
		ExecutorService executorService = ThreadSingleCase.getInstance();
		CompletionService<List<Map<String, Object>>> completionService = new ExecutorCompletionService<List<Map<String, Object>>>(
				executorService);
		jsonObject.put(AlarmInfoConstants.PAGE_SIZE, 20000);

		List<Future<List<Map<String, Object>>>> futureList = new ArrayList<>();
		for (int i = 1; i <= totalCountForThread; i++) {
			AlarmInfoCallable callable = new AlarmInfoCallable(jsonObject, i, alarmInfoQueryService);
			Future<List<Map<String, Object>>> list = completionService.submit(callable);
			futureList.add(list);
		}

		for (int i = 0; i < futureList.size(); i++) {
			List<Map<String, Object>> list = futureList.get(i).get();
			alarmInfoList.addAll(list);
		}

		return alarmInfoList;
	}



	/**
	 * 
	 * 
	 * @Description:rpc接口调用封装数据
	 * @param jsonObject
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> statisticsAlarmInfo(JSONObject jsonObject) throws Exception {
		RpcResponse<PageInfo<Map<String, Object>>> res = alarmInfoQueryService.statisticsAlarmInfo(jsonObject, "");
		if (!res.isSuccess()) {
			logger.error(String.format("[statisticsAlarmInfo()->error:%s]", res.getMessage()));
			return null;
		} else {
			logger.info(String.format("[statisticsAlarmInfo()->success:%s]", res.getMessage()));
			return res.getSuccessValue().getList();
		}
	}



	public Result<List<Map<String, Object>>> alarmInfoRoll(String params) {
		logger.info(String.format("[alarmInfoRoll()->request params:%s]", params));
		if (ParamChecker.isBlank(params)) {
			logger.error(String.format("[alarmInfoRoll()->error:%s]", LogMessageContants.NO_PARAMETER_EXISTS));
			return ResultBuilder.noBusinessResult();
		}
		try {
			JSONObject json = JSONObject.parseObject(params);
			String accessSecret = json.getString(AlarmInfoConstants.USC_ACCESS_SECRET);
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[alarmInfoRoll()->error:接入方密钥不能为空]");
				return ResultBuilder.failResult("接入方密钥不能为空");
			}
			if (!json.containsKey(AlarmInfoConstants.ORG_ID)) {
				logger.error("[alarmInfoRoll()->error:组织id参数名不存在]");
				return ResultBuilder.failResult("组织id参数名不存在");
			}
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			json.put(InterGatewayConstants.TOKEN, token);

			RpcResponse<List<Map<String, Object>>> alarmInfoRoll = alarmInfoQueryService.alarmInfoRoll(json);
			if (alarmInfoRoll != null && alarmInfoRoll.isSuccess() && alarmInfoRoll.getSuccessValue() != null) {
				logger.info("[alarmInfoRoll()->success:告警信息查询成功]");
				return ResultBuilder.successResult(alarmInfoRoll.getSuccessValue(), alarmInfoRoll.getMessage());
			} else {
				logger.error("[alarmInfoRoll()->fail:告警信息查询失败]");
				return ResultBuilder.failResult(alarmInfoRoll.getMessage());
			}
		} catch (Exception e) {
			logger.error("alarmInfoRoll()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:查询设备产生告警当时上报的数据
	 * @param alarmInfoId
	 * @return
	 */
	public Result<JSONObject> findAlarmDeviceData(String alarmInfoId) {
		logger.info(String.format("[findAlarmDeviceData()->request params:%s]", alarmInfoId));
		try {
			if (StringUtils.isBlank(alarmInfoId)) {
				logger.error("[findAlarmDeviceData()->error:告警信息ID为null]");
				return ResultBuilder.emptyResult();
			}

			// 通过告警信息获取当前告警信息对应的设备的上报状态
			RpcResponse<JSONObject> res = alarmInfoQueryService.queryAlarmDeviceData(alarmInfoId);
			if (res.isSuccess()) {
				logger.info(String.format("[statisticsAlarmInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[statisticsAlarmInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("findAlarmDeviceData()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}

}
