/*
 * File name: FacilitesQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 李康诚 2017年11月20日 ... ... ...
 *
 ***************************************************/
package com.run.locman.query.service;

import java.util.LinkedHashMap;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.base.query.FacilitesService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.filetool.ExcelView;

/**
 * @Description: 设施查询
 * @author: 李康诚
 * @version: 1.0, 2017年11月20日
 */
@Service
public class FacilitesQueryService {
	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private FacilitesService	facilitesService;

	@Value("${api.host}")
	private String				ip;

	@Autowired
	private HttpServletRequest	request;



	/**
	 * 
	 * @Description:设施分页查询
	 * @param pageInfo
	 * @return
	 */
	public Result<PageInfo<Map<String, Object>>> facilitesSerchByPage(String pageInfo) {
		logger.info(String.format("[facilitesSerchByPage()->request params:%s]", pageInfo));
		try {
			Result<JSONObject> result = ExceptionChecked.checkRequestParam(pageInfo);
			if (result != null) {
				logger.error(String.format("[facilitesSerchByPage()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject obj = JSON.parseObject(pageInfo);
			obj.put(InterGatewayConstants.IP, ip);
			obj.put(InterGatewayConstants.TOKEN, request.getHeader(InterGatewayConstants.TOKEN));
			RpcResponse<PageInfo<Map<String, Object>>> page = facilitesService.getFacilitesInfoByPage(obj);
			if (page.isSuccess()) {
				logger.info("[facilitesSerchByPage()->success]");
				return ResultBuilder.successResult(page.getSuccessValue(), page.getMessage());
			} else {
				logger.error(String.format("[facilitesSerchByPage()->fail:%s]", page.getMessage()));
				return ResultBuilder.failResult(page.getMessage());
			}
		} catch (Exception e) {
			logger.error("facilitesSerchByPage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:通过设施id查询
	 * @param idInfo
	 * @return
	 */
	public Result<Map<String, Object>> facilitesSerchById(String idInfo) {
		logger.info(String.format("[facilitesSerchById()->request params:%s]", idInfo));
		try {
			Result<JSONObject> result = ExceptionChecked.checkRequestParam(idInfo);
			if (result != null) {
				logger.error(String.format("[facilitesSerchById()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject obj = JSON.parseObject(idInfo);
			if (!obj.containsKey(FacilitiesContants.FACILITES_ID)) {
				logger.error("[facilitesSerchById()->error:参数无设施id]");
				return ResultBuilder.noBusinessResult();
			}

			RpcResponse<Map<String, Object>> info = facilitesService.getFacilitesInfoByFacId(
					obj.getString(FacilitiesContants.FACILITES_ID), request.getHeader(InterGatewayConstants.TOKEN));
			if (info.isSuccess()) {
				logger.info("[facilitesSerchById()->success]");
				return ResultBuilder.successResult(info.getSuccessValue(), info.getMessage());
			} else {
				logger.error(String.format("[facilitesSerchById()->fail:%s]", info.getMessage()));
				return ResultBuilder.failResult(info.getMessage());
			}
		} catch (Exception e) {
			logger.error("facilitesSerchById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:查询包含扩展值的信息
	 * @param idInfo
	 * @return
	 */
	public Result<Map<String, Object>> getFacilitesInfo(String idInfo) {
		logger.info(String.format("[getFacilitesInfo()->request params:%s]", idInfo));
		try {
			Result<JSONObject> result = ExceptionChecked.checkRequestParam(idInfo);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject obj = JSON.parseObject(idInfo);
			if (!obj.containsKey(FacilitiesContants.FACILITES_ID)) {
				logger.error("[getFacilitesInfo()->error:参数中无设施id]");
				return ResultBuilder.noBusinessResult();
			}
			if (!obj.containsKey(FacilitiesContants.USC_ACCESS_SECRET)) {
				logger.error("[getFacilitesInfo()->error:参数中无accessSecret]");
				return ResultBuilder.noBusinessResult();
			}
			RpcResponse<Map<String, Object>> info = facilitesService.getFacilitesInfo(
					obj.getString(FacilitiesContants.FACILITES_ID), obj.getString(FacilitiesContants.USC_ACCESS_SECRET),
					request.getHeader(InterGatewayConstants.TOKEN));
			if (info.isSuccess()) {
				logger.info("getFacilitesInfo()->success]");
				return ResultBuilder.successResult(info.getSuccessValue(), info.getMessage());
			} else {
				logger.error(String.format("[getFacilitesInfo()->fail:%s]", info.getMessage()));
				return ResultBuilder.failResult(info.getMessage());
			}
		} catch (Exception e) {
			logger.error("getFacilitesInfo()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:通过设施code查询
	 * @param codeInfo
	 * @return
	 */
	public Result<Map<String, Object>> facilitesSerchByCode(String codeInfo) {
		logger.info(String.format("[facilitesSerchByCode()->request params:%s]", codeInfo));
		try {
			Result<JSONObject> result = ExceptionChecked.checkRequestParam(codeInfo);
			if (result != null) {
				logger.error(String.format("[facilitesSerchByCode()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject obj = JSON.parseObject(codeInfo);
			if (!obj.containsKey(FacilitiesContants.FACILITES_CODE)) {
				logger.error("[facilitesSerchByCode()->error:参数中无设施code]");
				return ResultBuilder.noBusinessResult();
			}
			RpcResponse<Map<String, Object>> info = facilitesService.getFacilitesInfoByfacCode(
					obj.getString(FacilitiesContants.FACILITES_CODE), request.getHeader(InterGatewayConstants.TOKEN));
			if (info.isSuccess()) {
				logger.info("[facilitesSerchByCode()->success]");
				return ResultBuilder.successResult(info.getSuccessValue(), info.getMessage());
			} else {
				logger.error(String.format("[facilitesSerchByCode()->fail:%s]", info.getMessage()));
				return ResultBuilder.failResult(info.getMessage());
			}
		} catch (Exception e) {
			logger.error("facilitesSerchByCode()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 查询设施编号是否重复
	 * 
	 * @Description:
	 * @param queryParams
	 * @return
	 */
	public Result<Boolean> checkFacilitiesCode(String queryParams) {
		logger.info(String.format("[checkFacilitiesCode()->request params:%s]", queryParams));
		try {
			Result<JSONObject> result = ExceptionChecked.checkRequestParam(queryParams);
			if (result != null) {
				logger.error(String.format("[checkFacilitiesCode()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject queryObject = JSON.parseObject(queryParams);
			if (!queryObject.containsKey(FacilitiesContants.FACILITIES_CODE)) {
				logger.error("[checkFacilitiesCode()->error:参数中无设施code]");
				return ResultBuilder.invalidResult();
			}
			if (!queryObject.containsKey(FacilitiesContants.USC_ACCESS_SECRET)) {
				logger.error("[checkFacilitiesCode()->error:参数中accessSecret]");
				return ResultBuilder.invalidResult();
			}
			String facilitiesCode = queryObject.getString(FacilitiesContants.FACILITIES_CODE);
			String accessSecret = queryObject.getString(FacilitiesContants.USC_ACCESS_SECRET);
			String id = queryObject.getString(FacilitiesContants.FACILITES_ID);
			RpcResponse<Boolean> rpcResponse = facilitesService.checkFacilitiesCode(facilitiesCode, accessSecret, id);
			if (rpcResponse.isSuccess()) {
				logger.info("[checkFacilitiesCode()->success]");
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			} else {
				logger.error(String.format("[checkFacilitiesCode()->fail:%s]", rpcResponse.getMessage()));
				return ResultBuilder.failResult(rpcResponse.getMessage());
			}
		} catch (Exception e) {
			logger.error("checkFacilitiesCode()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("unchecked")
	public ModelAndView exportFacilitesInfo(String pageInfo, ModelMap model) {
		logger.info(String.format("[exportFacilitesInfo()->request param : %s]", pageInfo));
		try {
			Result<PageInfo<Map<String, Object>>> result = facilitesSerchByPage(pageInfo);
			if (!CommonConstants.NUMBER_FOUR_ZERO.equals(result.getResultStatus().getResultCode())) {
				logger.error(String.format("[exportFacilitesInfo()->error:%s]",
						result.getResultStatus().getResultMessage()));
				return null;
			}

			// 构建查询数据
			List<Map<String, Object>> facilitesList = result.getValue().getList();

			// 数据处理
			for (Map<String, Object> map : facilitesList) {
				Map<String, String> orgInfo = (Map<String, String>) map.get(FacilitiesContants.ORG_INFO);
				if (orgInfo != null) {
					map.put(FacilitiesContants.FAC_ORGANIZATIONNAME,
							orgInfo.get(FacilitiesContants.FAC_ORGANIZATIONNAME));
				}
				String s1 = map.get(FacilitiesContants.FAC_COMPLETEADDRESS) + "";
				String s2 = map.get(FacilitiesContants.FAC_ADDRESS) + "";
				String replace = s1.replace(s2, "");
				map.put(FacilitiesContants.FAC_COMPLETEADDRESS, replace);

				if ("disable".equals(map.get(FacilitiesContants.FAC_MANAGESTATE) + "")) {
					map.put(FacilitiesContants.FAC_MANAGESTATE, "已停用");
				} else {
					map.put(FacilitiesContants.FAC_MANAGESTATE, "已启用");
				}

				if ("unbound".equals(map.get(FacilitiesContants.FAC_BINGOUNTD) + "")) {
					map.put(FacilitiesContants.FAC_BINGOUNTD, "未绑定");
				} else {
					map.put(FacilitiesContants.FAC_BINGOUNTD, "已绑定");
				}

			}

			Map<String, Object> map = new LinkedHashMap<>();
			map.put(FacilitiesContants.FAC_FACILITIESCODE, FacilitiesContants.FAC_FACILITIESCODE_PAR);
			map.put(FacilitiesContants.FAC_COMPLETEADDRESS, FacilitiesContants.FAC_COMPLETEADDRESS_PAR);
			map.put(FacilitiesContants.FAC_FACILITYTYPENAME, FacilitiesContants.FAC_FACILITYTYPENAME_PAR);
			map.put(FacilitiesContants.FAC_FACILITYTYPEALIAS, FacilitiesContants.FAC_FACILITYTYPEALIAS_PAR);
			map.put(FacilitiesContants.FAC_ORGANIZATIONNAME, FacilitiesContants.FAC_ORGANIZATIONNAME_PAR);
			map.put(FacilitiesContants.FAC_MANAGESTATE, FacilitiesContants.FAC_MANAGESTATE_PAR);
			map.put(FacilitiesContants.FAC_BINGOUNTD, FacilitiesContants.FAC_BINGOUNTD_PAR);
			map.put(FacilitiesContants.FAC_LONGITUDE, FacilitiesContants.FAC_LONGITUDE_PAR);
			map.put(FacilitiesContants.FAC_LATITUDE, FacilitiesContants.FAC_LATITUDE_PAR);
			map.put(FacilitiesContants.FAC_ADDRESS, FacilitiesContants.FAC_ADDRESS_PAR);

			model.put(ExcelView.EXCEL_NAME, FacilitiesContants.FAC_EXPROT_NAME);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);
			model.put(ExcelView.EXCEL_DATASET, facilitesList);
			logger.info(String.format("[exprotSwitchLockInfo()->suc:%s]", result.getResultStatus().getResultMessage()));
			View excelView = new ExcelView();
			return new ModelAndView(excelView);
		} catch (Exception e) {
			logger.error("checkFacilitiesCode()->exception", e);
			return null;
		}

	}



	/**
	 * 
	 * @Description:查询告警设施数量
	 * @param paramStr
	 * @return
	 */
	public Result<Integer> countAlarmFacilities(String paramStr) {
		logger.info(String.format("[countAlarmFacilities()->request params:%s]", paramStr));
		try {
			Result<JSONObject> result = ExceptionChecked.checkRequestParam(paramStr);
			if (result != null) {
				logger.error(String.format("[countAlarmFacilities()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject paramJson = JSON.parseObject(paramStr);
			if (StringUtils.isBlank(paramJson.getString(FacilitiesContants.USC_ACCESS_SECRET))) {
				logger.error("接入方密钥不能为空!!!");
				return ResultBuilder.emptyResult();
			}
			if (!paramJson.containsKey(FacilitiesContants.ORGANIZATION_ID)) {
				logger.error("组织id参数名不能为空!!!");
				return ResultBuilder.noBusinessResult();
			}
			paramJson.put(InterGatewayConstants.IP, ip);
			paramJson.put(InterGatewayConstants.TOKEN, request.getHeader(InterGatewayConstants.TOKEN));
			RpcResponse<Integer> totalNum = facilitesService.countAlarmFacilities(paramJson);
			if (totalNum != null && totalNum.isSuccess()) {
				logger.info("[countAlarmFacilities()->success]");
				return ResultBuilder.successResult(totalNum.getSuccessValue(), totalNum.getMessage());
			} else {
				logger.error("[countAlarmFacilities()->fail:查询告警设施数失败,返回结果为null]");
				return ResultBuilder.failResult("查询告警设施数失败,返回结果为null");
			}
		} catch (Exception e) {
			logger.error("countAlarmFacilities()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	/**
	 * 
	 * @Description:设施序列号查询设施信息
	 * @param paramStr
	 * @return
	 */
	public Result<Map<String, Object>> findFacilityInfoByCodeAndAcc(String facilitiesCode, String accessSecret) {
		logger.info(String.format("[findFacilityInfoByCodeAndAcc()->request facilitiesCode:%s, accessSecret]", facilitiesCode, accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("接入方密钥不能为空!!!");
				return ResultBuilder.emptyResult();
			}
			if (StringUtils.isBlank(facilitiesCode)) {
				logger.error("设施序列号不能为空!!!");
				return ResultBuilder.emptyResult();
			}
			RpcResponse<Map<String, Object>> facilityInfo = facilitesService.findFacilityInfoByCodeAndAcc(facilitiesCode, accessSecret);
			if (facilityInfo != null && facilityInfo.isSuccess()) {
				logger.info(String.format("[findFacilityInfoByCodeAndAcc()->success %s]", facilityInfo.getSuccessValue()));
				return ResultBuilder.successResult(facilityInfo.getSuccessValue(), facilityInfo.getMessage());
			} else {
				logger.error(String.format("[findFacilityInfoByCodeAndAcc()->fail:查询告警设施数失败,返回结果为 %s]", facilityInfo));
				return ResultBuilder.failResult("查询设施信息失败");
			}
		} catch (Exception e) {
			logger.error("findFacilityInfoByCodeAndAcc()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	/**
	 * 
	 * @Description:待整治设施分页查询
	 * @param pageInfo
	 * @return
	 */
	public Result<PageInfo<Map<String, Object>>> findFacilityRenovatedByPage(String pageInfo) {
		logger.info(String.format("[findFacilityRenovatedByPage()->request params:%s]", pageInfo));
		try {
			Result<JSONObject> result = ExceptionChecked.checkRequestParam(pageInfo);
			if (result != null) {
				logger.error(String.format("[findFacilityRenovatedByPage()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject obj = JSON.parseObject(pageInfo);
			obj.put(InterGatewayConstants.IP, ip);
			obj.put(InterGatewayConstants.TOKEN, request.getHeader(InterGatewayConstants.TOKEN));
			RpcResponse<PageInfo<Map<String, Object>>> page = facilitesService.findFacilityRenovatedByPage(obj);
			if (page.isSuccess()) {
				logger.info("[findFacilityRenovatedByPage()->success]");
				return ResultBuilder.successResult(page.getSuccessValue(), page.getMessage());
			} else {
				logger.error(String.format("[findFacilityRenovatedByPage()->fail:%s]", page.getMessage()));
				return ResultBuilder.failResult(page.getMessage());
			}
		} catch (Exception e) {
			logger.error("findFacilityRenovatedByPage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
}
