/*
 * File name: FaultOrderProcessRestCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年12月06日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.FaultOrderProcessCudService;
import com.run.locman.api.dto.CountFaultOrderDto;
import com.run.locman.api.query.service.FaultOrderProcessQueryService;
import com.run.locman.api.util.ConvertUtil;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FaultOrderCountContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.SimpleOrderConstants;
import com.run.locman.filetool.ExcelView;

/**
 * @Description: 故障工单添加修改
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
@Service
public class FaultOrderProcessRestCudService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FaultOrderProcessCudService		faultOrderProcessCudService;

	@Autowired
	private FaultOrderProcessQueryService	faultOrderProcessQueryService;

	@Value("${api.host}")
	private String							ip;

	@Autowired
	private HttpServletRequest				request;



	@SuppressWarnings("unchecked")
	public Result<String> addOrUpdateFaultOrder(String requestParams) {
		logger.info(String.format("[addOrUpdateFaultOrder()->request params:%s]", requestParams));
		if (ParamChecker.isBlank(requestParams) || ParamChecker.isNotMatchJson(requestParams)) {
			logger.error(String.format("[addOrUpdateFaultOrder()->error:%s or %s]",
					LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsObject = JSONObject.parseObject(requestParams);

			// 获取当前组织id
			String orgId = paramsObject.getString(SimpleOrderConstants.ORGANIZEID);

			// 校验参数
			if (StringUtils.isBlank(orgId)) {
				ResultBuilder.failResult("组织id不能为null！");
			}

			// 获取父类资源id
			String parentId = InterGatewayUtil.getHttpValueByGet("/interGateway/v3/organization/parentIds/" + orgId, ip,
					request.getHeader(InterGatewayConstants.TOKEN));
			paramsObject.put(SimpleOrderConstants.ORGANIZEID, parentId);

			// 通过设备dis查询这些设备id是否存在正在进行工单
			List<String> deviceIdsAdd = (List<String>) paramsObject.get("deviceIdsAdd");
			// deviceCount 不能为0
			Integer deviceCount = paramsObject.getInteger("deviceCount");
			if (deviceCount == null || deviceCount <= 0) {
				logger.error("[addOrUpdateFaultOrder()->error:申请故障工单！必须选择设备！]");
				return ResultBuilder.failResult("申请故障工单！必须选择设备！");
			}
			// 设备ids可能为null 针对前端传递参数 做出的业务判断
			if (deviceIdsAdd != null && !deviceIdsAdd.isEmpty()) {
				RpcResponse<List<String>> findFaultOrderByDeviceIdRes = faultOrderProcessQueryService
						.findFaultOrderByDeviceId(deviceIdsAdd);
				if (!findFaultOrderByDeviceIdRes.isSuccess()) {
					logger.info("[addOrUpdateFaultOrder()->error:]" + findFaultOrderByDeviceIdRes.getMessage());
					return ResultBuilder.failResult(findFaultOrderByDeviceIdRes.getMessage());
				}
				List<String> deviceIds = findFaultOrderByDeviceIdRes.getSuccessValue();
				if (!deviceIds.isEmpty()) {
					logger.info("[addOrUpdateFaultOrder()->error:]" + deviceIds.toString());
					return ResultBuilder.failResult("选择的设备中已存在故障工单！");
				}
			}

			RpcResponse<JSONObject> resultInfo = faultOrderProcessCudService.addOrUpdateFaultOrder(paramsObject);
			if (resultInfo.isSuccess()) {
				logger.info("[addOrUpdateFaultOrder()->success:]" + resultInfo.getMessage());
				return ResultBuilder.successResult(resultInfo.getSuccessValue().getString("faultOrderId"), resultInfo.getMessage());
			}
			logger.error("[addOrUpdateFaultOrder()->fail:]" + resultInfo.getMessage());
			return ResultBuilder.failResult(resultInfo.getMessage());
		} catch (Exception e) {
			logger.error("addOrUpdateFaultOrder()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> updateFaultOrderState(String requestParams) {
		logger.info(String.format("[updateFaultOrderState()->request params:%s]", requestParams));
		if (ParamChecker.isBlank(requestParams) || ParamChecker.isNotMatchJson(requestParams)) {
			logger.error(String.format("[updateFaultOrderState()->error:%s or %s]",
					LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));

			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsObject = JSONObject.parseObject(requestParams);
			RpcResponse<String> resultInfo = faultOrderProcessCudService.updateFaultOrderState(paramsObject);
			if (resultInfo.isSuccess()) {
				logger.info(String.format("[updateFaultOrderState()->success:%s]", resultInfo.getMessage()));
				return ResultBuilder.successResult(resultInfo.getSuccessValue(), resultInfo.getMessage());
			}
			logger.error(String.format("[updateFaultOrderState()->fail:%s]", resultInfo.getMessage()));
			return ResultBuilder.failResult(resultInfo.getMessage());
		} catch (Exception e) {
			logger.error("updateFaultOrderState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public ModelAndView exportFaultOrderExcelInfo(JSONObject accessSecretJson, ModelMap model) {
		logger.info(String.format("[exportFaultOrderExcelInfo()->request params:%s]", accessSecretJson));
		try {
			Object accessSecretStr = accessSecretJson.get(FaultOrderCountContants.ACCESS_SECRET);
			if (null == accessSecretStr || StringUtils.isBlank(accessSecretStr.toString())) {
				logger.error(String.format("[exportFaultOrderExcelInfo()->error:%s]", "查询业务对象为null！"));
				return null;
			}
			// 设置分页参数,查询所有数据
			accessSecretJson.put(FaultOrderCountContants.PAGE_NUM, 0);
			accessSecretJson.put(FaultOrderCountContants.PAGE_SIZE, 0);
			// 构建导出格式
			Map<String, Object> map = Maps.newLinkedHashMap();
			map.put(FaultOrderCountContants.SERIAL_NUMBER, FaultOrderCountContants.SERIAL_NUMBER_CH);
			map.put(FaultOrderCountContants.CREATE_TIME, FaultOrderCountContants.CREATE_TIME_CH);
			map.put(FaultOrderCountContants.AREA, FaultOrderCountContants.AREA_CH);
			map.put(FaultOrderCountContants.FAULT_TYPE, FaultOrderCountContants.FAULT_TYPE_CH);
			map.put(FaultOrderCountContants.FACTORY_NAME, FaultOrderCountContants.FACTORY_NAME_CH);
			map.put(FaultOrderCountContants.ORDER_STATE, FaultOrderCountContants.ORDER_STATE_CH);
			map.put(FaultOrderCountContants.PERSON_NAME, FaultOrderCountContants.PERSON_NAME_CH);
			map.put(FaultOrderCountContants.PHONE_NUMBEI, FaultOrderCountContants.PHONE_NUMBEI_CH);

			model.put(ExcelView.EXCEL_NAME, FaultOrderCountContants.EXCEL_NAME);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			// 获取所有数据
			RpcResponse<PageInfo<CountFaultOrderDto>> result = faultOrderProcessQueryService
					.countFaultOrderInfoByAccessSecret(accessSecretJson);
			PageInfo<CountFaultOrderDto> successValue = result.getSuccessValue();
			if (null == successValue) {
				logger.error("获取数据失败");
				return null;
			}
			List<CountFaultOrderDto> list = result.getSuccessValue().getList();
			List<Map<String, Object>> resultList = Lists.newArrayList();
			for (CountFaultOrderDto countFaultOrderDto : list) {
				Map<String, Object> resultMap = ConvertUtil.beanToMap(countFaultOrderDto);
				resultList.add(resultMap);
			}

			// 成功封裝数据
			if (result.isSuccess()) {
				logger.info(String.format("[exportFaultOrderExcelInfo()->success:%s]", result.getMessage()));
				model.put(ExcelView.EXCEL_DATASET, resultList);
			} else {
				logger.error(String.format("[exportFaultOrderExcelInfo()->error:%s]", result.getException()));
				return null;
			}

			View excelView = new ExcelView();
			return new ModelAndView(excelView);
		} catch (Exception e) {
			logger.error("exportFaultOrderExcelInfo()->exception", e);
			return null;
		}

	}

}
