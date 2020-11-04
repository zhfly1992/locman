/*
 * File name: FacilitiesRestCrudService.java
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
package com.run.locman.crud.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.DeviceInfoCudService;
import com.run.locman.api.crud.service.FacilitiesCrudService;
import com.run.locman.api.crud.service.FacilitiesRenovationCrudService;
import com.run.locman.api.crud.service.FaultOrderProcessCudService;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.query.service.FacilitiesRenovationQueryService;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.query.service.FaultOrderProcessQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.ConvertUtil;
import com.run.locman.api.util.InfoPushUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmOrderConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.PublicConstants;
import com.run.locman.filetool.ExcelView;

/**
 * @Description: 设施添加修改
 * @author: 李康诚
 * @version: 1.0, 2017年11月20日
 */
@Service
public class FacilitiesRestCrudService {

	@Autowired
	private FacilitiesCrudService	facilitiesCrudService;

	@Autowired
	private FacilitiesQueryService	facilitiesQueryService;
	
	@Autowired
	private FacilitiesRenovationCrudService facilitiesRenovationCrudService;
	
	
	@Autowired
	private FacilitiesRenovationQueryService	facilitiesRenovationQueryService;

	@Value("${api.host}")
	private String					ip;

	private Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private HttpServletRequest		request;

	@Autowired
	private FaultOrderProcessCudService		faultOrderProcessCudService;

	@Autowired
	private FaultOrderProcessQueryService	faultOrderProcessQueryService;
	
	@Autowired
	private FactoryQueryService				factoryQueryService;
	
	@Autowired
	private DeviceQueryService				deviceQueryService;
	
	@Autowired
	private DeviceInfoCudService			deviceInfoCudService;




	@SuppressWarnings("rawtypes")
	public Result<String> addFacilities(String addParams) {
		logger.info(String.format("[addFacilities()->request params:%s]", addParams));
		Result result = ExceptionChecked.checkRequestParam(addParams);
		if (result != null) {
			logger.error(String.format("[addFacilities()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			Facilities facilities = JSON.parseObject(addParams, Facilities.class);
			RpcResponse<Facilities> rpcResponse = facilitiesCrudService.addFacilities(facilities);
			if (rpcResponse.isSuccess()) {
				Facilities successValue = rpcResponse.getSuccessValue();
				//注释推送
				//addInfoPush(facilities, successValue);
				logger.info(String.format("[addFacilities()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(successValue.getId(), rpcResponse.getMessage());
			}
			logger.error(String.format("[addFacilities()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("addFacilities()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void addInfoPush(Facilities facilities, Facilities successValue) {
		try {
			String accessSecret = facilities.getAccessSecret();
			String receiveUrl = InfoPushUtil.WhetherPush(accessSecret, ip);
			if (null != receiveUrl) {
				// 组装设施数据并推送
				JSONObject pushInfo = getPushInfo(successValue);
				pushInfo.put("boundDevices", Lists.newArrayList());
				List<JSONObject> infoList = Lists.newArrayList();
				infoList.add(pushInfo);
				String infoPush = InfoPushUtil.InfoPush(receiveUrl, infoList, InfoPushUtil.FACILITIES_ADD);
				logger.info(String.format("[addFacilities()->设施添加成功,推送结果:%s", infoPush));

			}
		} catch (Exception e) {
			logger.error("addFacilities()->推送时Exception:" + e);
		}
	}



	/**
	 * 
	 * @Description:设施id; 设施类型id; 设施类型名称; 设施序列号; 经度; 纬度; 地址; 管理状态(启用/停用); 创建时间;
	 *                    修改时间; 完整地址(省市县+地址); 扩展属性; 展示扩展属性; 区域编码
	 * @param
	 * @return
	 */

	private JSONObject getPushInfo(Facilities successValue) {
		JSONObject pushInfo = new JSONObject();
		pushInfo.put("facilityId", successValue.getId());
		pushInfo.put("facilityTypeAlias", successValue.getFacilityTypeAlias());
		pushInfo.put("facilityTypeId", successValue.getFacilitiesTypeId());
		pushInfo.put("facilityCode", successValue.getFacilitiesCode());
		pushInfo.put("longitude", successValue.getLongitude());
		pushInfo.put("latitude", successValue.getLatitude());
		pushInfo.put("address", successValue.getAddress());
		pushInfo.put("manageState", successValue.getManageState());
		pushInfo.put("creationTime", successValue.getCreationTime());
		pushInfo.put("editorTime", successValue.getEditorTime());
		pushInfo.put("completeAddress", successValue.getCompleteAddress());
		pushInfo.put("extend", successValue.getExtend());
		pushInfo.put("showExtend", successValue.getShowExtend());
		pushInfo.put("areaId", successValue.getAreaId());
		return pushInfo;
	}



	@SuppressWarnings("rawtypes")
	public Result<Facilities> updateFacilities(String updateParams) {
		logger.info(String.format("[updateFacilities()->request params:%s]", updateParams));
		Result result = ExceptionChecked.checkRequestParam(updateParams);
		if (result != null) {
			logger.error(String.format("[updateFacilities()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			Facilities facilities = JSON.parseObject(updateParams, Facilities.class);
			RpcResponse<Facilities> rpcResponse = facilitiesCrudService.updateFacilities(facilities);
			if (rpcResponse.isSuccess()) {

				Facilities successValue = rpcResponse.getSuccessValue();
				//注释推送
				//updateInfoPush(facilities, successValue);
				logger.info(String.format("[updateFacilities()->success:%s]", rpcResponse.getMessage()));
				return ResultBuilder.successResult(successValue, rpcResponse.getMessage());
			}
			logger.error(String.format("[updateFacilities()->fail:%s]", rpcResponse.getMessage()));
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("updateFacilities()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:修改时推送设施数据
	 * @param
	 * @return
	 */

	private void updateInfoPush(Facilities facilities, Facilities successValue) {
		try {
			String accessSecret = facilities.getAccessSecret();
			// 查询是否需要推送
			String receiveUrl = InfoPushUtil.WhetherPush(accessSecret, ip);
			logger.info(String.format("updateInfoPush()-->accessSecret:  , receiveUrl:%s", accessSecret, receiveUrl));
			if (null != receiveUrl) {
				JSONObject pushInfo = getPushInfo(successValue);
				RpcResponse<List<Map<String, Object>>> boundDeviceInfo = facilitiesQueryService
						.getBoundDeviceInfo(successValue.getId(), accessSecret);
				if (!boundDeviceInfo.isSuccess()) {
					pushInfo.put("boundDevices", Lists.newArrayList());
				} else {
					List<Map<String, Object>> boundDevices = boundDeviceInfo.getSuccessValue();
					pushInfo.put("boundDevices", boundDevices);
				}
				List<JSONObject> infoList = Lists.newArrayList();
				infoList.add(pushInfo);
				String infoPush = InfoPushUtil.InfoPush(receiveUrl, infoList, InfoPushUtil.FACILITIES_UPDATE);
				logger.info(String.format("[updateFacilities()->设施修改成功,推送结果:%s", infoPush));
			}
		} catch (Exception e) {
			logger.error("updateFacilities()->推送时Exception:" + e);
		}
	}



	public Result<String> bindFacilities(String bindFacilities) {
		logger.info(String.format("[bindFacilities()->request params:%s]", bindFacilities));
		JSONObject json = JSONObject.parseObject(bindFacilities);

		RpcResponse<List<Map<String, Object>>> deviceIdList = facilitiesQueryService.getDeviceIdList(json);
		RpcResponse<List<Map<String, Object>>> facilityIdList = facilitiesQueryService.getFacilityIdList(json);

		List<Map<String, Object>> bindList = new ArrayList<>();
		if (deviceIdList.isSuccess() && facilityIdList.isSuccess()) {
			List<Map<String, Object>> deviceList = deviceIdList.getSuccessValue();
			List<Map<String, Object>> facilityList = facilityIdList.getSuccessValue();

			List<Map<String, Object>> deviceIdListNew = new ArrayList<>();
			if (StringUtils.isBlank(json.getString(CommonConstants.DEVICENOBINDCOUNT))
					|| json.getIntValue("deviceNoBindCount") == 0) {
				deviceIdListNew.addAll(deviceIdListNew);
			} else {
				int count = json.getIntValue("deviceNoBindCount");
				deviceIdListNew.addAll(deviceList.subList(0, deviceList.size() - count));
			}

			if (null != deviceIdListNew && null != facilityList && deviceIdListNew.size() == facilityList.size()) {
				for (int i = 0; i < deviceIdListNew.size(); i++) {
					Map<String, Object> map = new HashMap<>(5);
					String id = UtilTool.getUuId();
					map.put("id", id);
					map.put("deviceId", deviceIdListNew.get(i).get("deviceId"));
					map.put("facilityId", facilityList.get(i).get("facilityId"));
					map.put("deviceTypeId", deviceIdListNew.get(i).get("deviceType"));
					bindList.add(map);
				}
			} else {
				logger.error(String.format("[bindFacilities()->fail:查询到的设备条数:%s,--查询到的设施条数:%s,绑定失败]",
						deviceIdList.getSuccessValue().size(), facilityIdList.getSuccessValue().size()));
				return ResultBuilder.successResult("查询到的设备条数:" + deviceIdList.getSuccessValue().size() + "--查询到的设施条数+"
						+ facilityIdList.getSuccessValue().size(), "绑定失败!");
			}

		}
		try {
			RpcResponse<String> addFacilitiesAndDeviceId = facilitiesCrudService.addFacilitiesAndDeviceId(bindList,json.getString("accessSecret"));
			logger.info(String.format("[bindFacilities()->success:绑定的条数-- %s]",
					addFacilitiesAndDeviceId.getSuccessValue()));
			return ResultBuilder.successResult("查询到的设备条数:" + deviceIdList.getSuccessValue().size() + "--查询到的设施条数+"
					+ facilityIdList.getSuccessValue().size() + "绑定的条数:" + addFacilitiesAndDeviceId.getSuccessValue(),
					addFacilitiesAndDeviceId.getMessage());
		} catch (Exception e) {
			logger.error("updateFacilities()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:导出设施模板,供用户写数据
	 * @param model
	 * @return
	 */
	public ModelAndView exportFacilitiesTemplate(ModelMap model) {
		logger.info("exportFacilitiesTemplate()->suc : 正在导出设施模板！");
		try {

			Map<String, Object> map = Maps.newLinkedHashMap();
			map.put(FacilitiesContants.FAC_FACILITIESCODE, FacilitiesContants.FAC_FACILITIESCODE_PAR);
			map.put(FacilitiesContants.FACILITESTYPE, FacilitiesContants.FACILITESTYPE_PAR);
			map.put(FacilitiesContants.FAC_LONGITUDE, FacilitiesContants.FAC_LONGITUDE_PAR);
			map.put(FacilitiesContants.FAC_LATITUDE, FacilitiesContants.FAC_LATITUDE_PAR);
			map.put(FacilitiesContants.FAC_ADDRESS, FacilitiesContants.FAC_ADDRESS_PAR);
			map.put(FacilitiesContants.DEVICECODE, FacilitiesContants.DEVICECODE_PAR);
			map.put(FacilitiesContants.BASICS_FACILITIES_TYPE, FacilitiesContants.BASICS_FACILITIES_TYPE_PAR);
			model.put(ExcelView.EXCEL_NAME, FacilitiesContants.FAC_EXPROT_NAME);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);
			List<Map<String, Object>> resultList = Lists.newArrayList();
			model.put(ExcelView.EXCEL_DATASET, resultList);
			View excelView = new ExcelView();

			logger.info("exportFacilitiesTemplate()->suc : 导出设施模板成功！");
			return new ModelAndView(excelView);

		} catch (Exception e) {
			logger.error("exportFacilitiesTemplate()->exception", e);
			return null;
		}

	}



	/**
	 * 
	 * @Description:导出网关模板
	 * @param model
	 * @return
	 */
	public ModelAndView exportGateWayTemplate(ModelMap model) {
		logger.info("exportGateWayTemplate()->suc : 正在导出网关设备模板！");
		try {

			Map<String, Object> map = Maps.newLinkedHashMap();
			map.put(FacilitiesContants.GATEWAY_DEVICE_CODE, FacilitiesContants.GATEWAY_DEVICE_CODE_PAR);
			map.put(FacilitiesContants.GATEWAY_DEVICE_ID, FacilitiesContants.GATEWAY_DEVICE_ID_PAR);
			model.put(ExcelView.EXCEL_NAME, FacilitiesContants.GATEWAY_EXPROT_NAME);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);
			List<Map<String, Object>> resultList = Lists.newArrayList();
			model.put(ExcelView.EXCEL_DATASET, resultList);
			View excelView = new ExcelView();

			logger.info("exportGateWayTemplate()->suc : 导出网关设备模板成功！");
			return new ModelAndView(excelView);

		} catch (Exception e) {
			logger.error("exportFacilitiesTemplate()->exception", e);
			return null;
		}

	}



	/**
	 * 
	 * @Description:导入excel文件设施信息与设备绑定
	 * @param file
	 * @param facilitiesJson
	 * @return
	 */
	public ModelAndView importExcelFacilities(MultipartFile file, MultipartFile fileGateway, String areaId,
			String organizationId, String accessSecret, String userId, ModelMap model) {
		logger.info(String.format(
				"[importExcelFacilities()->request params : areaId = %s , organizationId = %s , accessSecret = %s , userId = %s]",
				areaId, organizationId, accessSecret, userId));
		try {
			// 基础参数校验
			if (file == null || fileGateway == null || StringUtils.isBlank(areaId)
					|| StringUtils.isBlank(organizationId) || StringUtils.isBlank(accessSecret)
					|| StringUtils.isBlank(userId)) {
				logger.error("[importExcelFacilities()->error:文件或者业务参数为null !]");
				return null;
			}

			// 插入数据
			List<Map<String, Object>> failFacilities = insertFacilitiesInfo(file.getOriginalFilename(), file,
					fileGateway.getOriginalFilename(), fileGateway, areaId, organizationId, accessSecret, userId);

			if (failFacilities == null) {
				logger.info("[importExcelFacilities()->error:保存数据库失败!请查看具体日志信息！]");
				ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
				model.addAttribute(PublicConstants.RESULT_CODE, "0005");
				mav.addObject(PublicConstants.RESULT_DATA, "保存数据库失败!请查看具体日志信息！");
				return mav;
			} else if (failFacilities.size() == 0) {
				logger.info(String.format("[importExcelFacilities()->suc:失败记录行数 无！%s]", PublicConstants.PARAM_SUCCESS));
				ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
				model.addAttribute(PublicConstants.RESULT_CODE, "0000");
				mav.addObject(PublicConstants.RESULT_DATA, "操作成功！");
				return mav;
			}
			// 封装错误信息导出excel
			createView(model, failFacilities);
			View excelView = new ExcelView();

			logger.info("exportFacilitiesTemplate()->suc : 导出设施模板错误信息成功！");
			return new ModelAndView(excelView);

		} catch (Exception e) {
			logger.error("importExcelFacilities()->exception", e);
			ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
			model.addAttribute(PublicConstants.RESULT_CODE, "0005");
			mav.addObject(PublicConstants.RESULT_DATA, e.getMessage());
			return mav;
		}

	}



	/**
	 * @Description:
	 * @param model
	 * @param failFacilities
	 */

	private void createView(ModelMap model, List<Map<String, Object>> failFacilities) throws Exception {
		Map<String, Object> map = Maps.newLinkedHashMap();

		map.put(FacilitiesContants.FAC_FACILITIESCODE, FacilitiesContants.FAC_FACILITIESCODE_PAR);
		map.put(FacilitiesContants.FACILITESTYPE, FacilitiesContants.FACILITESTYPE_PAR);
		map.put(FacilitiesContants.FAC_LONGITUDE, FacilitiesContants.FAC_LONGITUDE_PAR);
		map.put(FacilitiesContants.FAC_LATITUDE, FacilitiesContants.FAC_LATITUDE_PAR);
		map.put(FacilitiesContants.FAC_ADDRESS, FacilitiesContants.FAC_ADDRESS_PAR);
		map.put(FacilitiesContants.DEVICECODE, FacilitiesContants.DEVICECODE_PAR);
		map.put(FacilitiesContants.BASICS_FACILITIES_TYPE, FacilitiesContants.BASICS_FACILITIES_TYPE_PAR);

		model.put(ExcelView.EXCEL_NAME, FacilitiesContants.ERROR_FACILITIES_EXCEL);
		model.put(ExcelView.EXCEL_COLUMNHEADING, map);
		model.put(ExcelView.EXCEL_DATASET, failFacilities);
	}



	/**
	 * 
	 * @Description:插入数据到mysql中
	 * @param fileName
	 * @param file
	 * @param areaId
	 * @param organizationId
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> insertFacilitiesInfo(String fileName, MultipartFile file, String fileGatewayName,
			MultipartFile fileGateway, String areaId, String organizationId, String accessSecret, String userId)
			throws Exception {

		if (!fileName.matches(FacilitiesContants.CHECK_XLX)) {
			logger.error("[insertFacilitiesInfo()->error:设施信息文件格式不正确]");
			throw new MyException("设施信息文件格式不正确!");
		}

		if (!fileGatewayName.matches(FacilitiesContants.CHECK_XLX)) {
			logger.error("[insertFacilitiesInfo()->error:网关设备文件格式不正确]");
			throw new MyException("网关设备文件格式不正确!");
		}

		Map<String, String> deviceMap = new HashMap<>(0);
		// 解析设备网关数据
		deviceMap = analysisDeviceSheet(fileGateway);

		if (deviceMap == null) {
			logger.error("[insertFacilitiesInfo()->error:解析网关数据失败！]");
			throw new MyException("解析网关数据失败！");
		}

		// 返回失败的set
		List<Map<String, Object>> analysisSheetFile = analysisSheetFile(file, deviceMap, areaId, organizationId,
				accessSecret, userId);

		if (analysisSheetFile == null) {
			return null;
		}

		return analysisSheetFile;
	}



	/**
	 * 
	 * @Description:解析excel返回list
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private List<Map<String, Object>> analysisSheetFile(MultipartFile file, Map<String, String> deviceMap,
			String areaId, String organizationId, String accessSecret, String userId) throws Exception {
		List<Map<String, Object>> errorList = Lists.newArrayList();
		// 转换为sheet对象
		InputStream is = file.getInputStream();
		Workbook wb = new XSSFWorkbook(is);
		Sheet sheet = wb.getSheetAt(0);

		// 校验模板信息
		Row title = sheet.getRow(0);
		Boolean checkRowTitle = checkRowTitle(title, 7, FacilitiesContants.FAC_FACILITIESCODE_PAR,
				FacilitiesContants.FACILITESTYPE_PAR, FacilitiesContants.FAC_LONGITUDE_PAR,
				FacilitiesContants.FAC_LATITUDE_PAR, FacilitiesContants.FAC_ADDRESS_PAR,
				FacilitiesContants.DEVICECODE_PAR, FacilitiesContants.BASICS_FACILITIES_TYPE_PAR);

		if (!checkRowTitle) {
			logger.error("[analysisDeviceSheet()->error:设施信息模板不正确！请重新下载后导入！]");
			throw new MyException("设施信息模板不正确！请重新下载后导入！");
		}

		List<Facilities> facSheet = Lists.newArrayList();
		// 循环行数 封装数据 基础匹配
		for (int r = 1; r <= sheet.getLastRowNum(); r++) {
			// 当前一行
			Row row = sheet.getRow(r);
			if (row == null) {
				logger.info(String.format("[analysisSheetFile()->suc:当前第%s行没有数据，跳过!]", r));
				continue;
			}
			// 封装对象
			Facilities facilities = createFacilities(accessSecret, areaId, userId, organizationId, null, r, row);
			Map<String, Object> facMap = ConvertUtil.beanToMap(facilities);
			// 判断当前一行值为null
			Map<String, Object> checkRowCell = checkRowCell(row, 7);

			if (!Boolean.valueOf(checkRowCell.get(PublicConstants.CHECKROWCELL).toString())) {
				logger.info(String.format("[analysisSheetFile()->error:当前第%s行,其中一列没有数据，解析设施设备模板失败！]", r));
				facMap.put(PublicConstants.IDNEX, checkRowCell.get(PublicConstants.IDNEX));
				errorList.add(facMap);
				continue;
			}

			// 通过设备code获取设备ID
			String deivceId = deviceMap.get(getStringValue(row, 5));
			// 校验经纬度
			if (!getStringValue(row, 2).matches(FacilitiesContants.CHECK_LONGITUDE)) {
				facMap.put(PublicConstants.IDNEX, 2);
				if (!getStringValue(row, 3).matches(FacilitiesContants.CHECK_LATITUDE)) {
					facMap.put(PublicConstants.IDNEX, 3);
				}
				logger.info(String.format("[analysisSheetFile()->error:当前第%s行,经纬度不正确，请重新填入！]", r));
				errorList.add(facMap);
				continue;
			}
			// 封装对象
			facilities = createFacilities(accessSecret, areaId, userId, organizationId, deivceId, r, row);

			facSheet.add(facilities);
		}

		// 错误数据大于0 并且解析出来的数据为0
		if (errorList.size() > 0 && facSheet.size() == 0) {
			return errorList;
		}

		// 插入mysql 返回失败的信息
		RpcResponse<List<Map<String, Object>>> rpcResponse = facilitiesQueryService.analysisInsertSheetFile(facSheet);
		if (rpcResponse.isSuccess()) {
			logger.info(String.format("[analysisSheetFile()->success:%s]", rpcResponse.getMessage()));
			List<Map<String, Object>> successValue = rpcResponse.getSuccessValue();
			errorList.addAll(successValue);
			return errorList;
		}

		logger.error(String.format("[analysisSheetFile()->fail:%s]", rpcResponse.getMessage()));
		throw new MyException(rpcResponse.getMessage());

	}



	/**
	 * 
	 * @Description:解析设备id与设备编号excel
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private Map<String, String> analysisDeviceSheet(MultipartFile file) throws Exception {

		// 转换为sheet对象
		InputStream is = file.getInputStream();
		Workbook wb = new XSSFWorkbook(is);
		Sheet sheet = wb.getSheetAt(0);

		// 获取标题 校验是否我们的模板
		Row titleRow = sheet.getRow(0);

		Boolean checkRowTitle = checkRowTitle(titleRow, 2, FacilitiesContants.GATEWAY_DEVICE_CODE_PAR,
				FacilitiesContants.GATEWAY_DEVICE_ID_PAR);

		if (!checkRowTitle) {
			logger.error("[analysisDeviceSheet()->error:网关设备模板不正确！请重新下载后导入！]");
			throw new MyException("网关设备模板不正确！请重新下载后导入！");
		}

		Map<String, String> deivceMap = Maps.newHashMap();

		// 循环行数
		for (int r = 1; r <= sheet.getLastRowNum(); r++) {
			// 当前一行
			Row row = sheet.getRow(r);
			if (row == null) {
				logger.info(String.format("[analysisDeviceSheet()->error:当前第%s行没有数据，停止解析网关设备模板！]", r));
				continue;
			}
			Map<String, Object> checkRowCell = checkRowCell(row, 2);
			if (!Boolean.valueOf(checkRowCell.get(PublicConstants.CHECKROWCELL).toString())) {
				logger.info(String.format("[analysisDeviceSheet()->error:当前第%s行,其中一列没有数据，停止解析网关设备模板！]", r));
				continue;
			}
			deivceMap.put(getStringValue(row, 0), getStringValue(row, 1));
		}

		return deivceMap;
	}



	/**
	 * 
	 * @Description:校验row titile的有效性
	 * @param row
	 *            work 行 数据
	 * @param i
	 *            校验的列有几位
	 * @param keys
	 *            数据匹配
	 * @return
	 */
	private Boolean checkRowTitle(Row row, int i, String... keys) throws Exception {

		for (int j = 0; j <= i - 1; j++) {
			Cell cell = row.getCell(j);
			if (cell == null) {
				return false;
			}
			String cellValue = cell.getStringCellValue();
			if (StringUtils.isBlank(cellValue)) {
				return false;
			}
			if (!keys[j].equals(cellValue)) {
				return false;
			}
		}

		return true;
	}



	/**
	 * 
	 * @Description:校验row cell的数据
	 * @param row
	 *            work 行 数据
	 * @param i
	 *            校验的列有几位
	 * @return
	 */
	private Map<String, Object> checkRowCell(Row row, int i) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		for (int j = 0; j < i - 1; j++) {
			// 基于设备code不校验
			if (j == 5) {
				continue;
			}
			if (row.getCell(j) == null || StringUtils.isBlank(getStringValue(row, j))) {
				map.put(PublicConstants.CHECKROWCELL, false);
				map.put(PublicConstants.IDNEX, j);
				return map;
			}
		}
		map.put(PublicConstants.CHECKROWCELL, true);
		return map;
	}



	/**
	 * 
	 * @Description:构建设施对象
	 * @param accessSecret
	 * @param areaId
	 * @param userId
	 * @param organizationId
	 * @param deivceId
	 * @param r
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private Facilities createFacilities(String accessSecret, String areaId, String userId, String organizationId,
			String deivceId, int r, Row row) throws Exception {
		Facilities facilities = new Facilities();
		facilities.setId(UtilTool.getUuId());
		facilities.setAccessSecret(accessSecret);
		facilities.setAreaId(areaId);
		facilities.setFacilitiesCode(getStringValue(row, 0));
		facilities.setLongitude(getStringValue(row, 2));
		facilities.setLatitude(getStringValue(row, 3));
		facilities.setAddress(getStringValue(row, 4));
		facilities.setManageState("enable");
		facilities.setCreationUserId(userId);
		facilities.setOrganizationId(organizationId);
		facilities.setCompleteAddress(getStringValue(row, 4));
		facilities.setFacilitiesType(getStringValue(row, 1));
		facilities.setBasicsFacType(getStringValue(row, 6));
		facilities.setDeviceId(deivceId);
		facilities.setRow(String.valueOf(r));
		facilities.setCreationTime(DateUtils.formatDate(new Date()));
		facilities.setDeviceCode(getStringValue(row, 5));
		return facilities;
	}



	/**
	 * 
	 * @Description:数据判断获取数据
	 * @param row
	 * @param index
	 * @return
	 * @throws Exception
	 */
	private String getStringValue(Row row, Integer index) throws Exception {
		String value = "";
		if (row.getCell(index) == null) {
			return "";
		}
		if (row.getCell(index).getCellType() == 0) {
			DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
			value = decimalFormat.format(row.getCell(index).getNumericCellValue());
		} else {
			value = row.getCell(index).getStringCellValue();
		}
		return value;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public Result<String> synchFacilities(String facilityParam) {
		try {
			logger.info(String.format("[synchFacilities()->request params:%s]", facilityParam));
			Result<Object> result = ExceptionChecked.checkRequestParam(facilityParam);
			if (result != null) {
				logger.error(String.format("[synchFacilities()->error:%s or %s]",
						LogMessageContants.NO_PARAMETER_EXISTS, LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			JSONObject facilitiesJson = JSONObject.parseObject(facilityParam);
			String accessSecret = facilitiesJson.getString(FacilitiesContants.USC_ACCESS_SECRET);
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[synchFacilities()->error:接入方密钥不能为空]");
				return ResultBuilder.invalidResult();
			}
			String facilityIds = facilitiesJson.getString("facilityIds");
			if (StringUtils.isBlank(facilityIds)) {
				logger.error("[synchFacilities()->error:要同步的设施id不能为空]");
				return ResultBuilder.invalidResult();
			}
			String receiveUrl = InfoPushUtil.WhetherPush(accessSecret, ip);
			if (StringUtils.isBlank(receiveUrl)) {
				logger.error("[synchFacilities()->error:推送信息接收地址为空]");
				return ResultBuilder.failResult("未查询到此接入方推送信息接收地址!!!");
			}
			String[] split = facilityIds.split(",");
			List<String> idList = Arrays.asList(split);
			if (null == idList || idList.isEmpty()) {
				logger.error("[synchFacilities()->fail:同步失败");
				return ResultBuilder.failResult("同步失败!设施id集合为空");
			}
			RpcResponse<List<Map<String, Object>>> faciliesInfo = facilitiesQueryService.findFacitiesByIds(idList);
			if (!faciliesInfo.isSuccess()) {
				logger.error("[synchFacilities()->fail:查询设施信息失败");
				return ResultBuilder.failResult("同步失败!查询设施信息失败");
			}
			RpcResponse<List<Map<String, Object>>> devicesInfo = facilitiesQueryService
					.getBoundDevicesInfoByfacIds(idList, accessSecret);
			if (!devicesInfo.isSuccess()) {
				logger.error("[synchFacilities()->fail:查询设备信息失败");
				return ResultBuilder.failResult("同步失败!查询设备信息失败");
			}
			List<Map<String, Object>> facilities = faciliesInfo.getSuccessValue();
			if (null != facilities && facilities.isEmpty()) {
				logger.error("[synchFacilities()->fail:没有查询到id对应的设施");
				return ResultBuilder.failResult("没有查询到id对应的设施,不需同步");
			}
			List<Map<String, Object>> devices = devicesInfo.getSuccessValue();
			List<JSONObject> pushInfoList = Lists.newArrayList();
			if (null != devices) {
				for (Map<String, Object> facility : facilities) {
					if (null == facility.get("id")) {
						continue;
					}
					JSONObject pushInfo = getPushInfo(facility);
					String facilityId = facility.get("id") + "";
					List<Map<String, Object>> devicesRsFac = Lists.newArrayList();
					for (Map<String, Object> device : devices) {
						if (null == device.get("facilityId")) {
							continue;
						}
						String deviceRsFacilityId = device.get("facilityId") + "";
						if (facilityId.equals(deviceRsFacilityId)) {
							device.remove("facilityId");
							devicesRsFac.add(device);
						}
					}
					pushInfo.put("boundDevices", devicesRsFac);
					pushInfoList.add(pushInfo);

				}
			}
			StringBuffer resultBuffer = new StringBuffer();
			int pointsDataLimit = 200;// 限制条数
			if (pushInfoList.size() > pointsDataLimit) {
				List<List<JSONObject>> splitList = UtilTool.splitList(pushInfoList, pointsDataLimit);
				for (int i = 0; i < splitList.size(); i++) {
					List<JSONObject> list = splitList.get(i);
					// TODO 设施批量同步
					String infoPush = InfoPushUtil.InfoPush(receiveUrl, list, InfoPushUtil.FACILITIES_UPDATE);
					resultBuffer.append(infoPush + ",");
				}
			} else {
				// TODO 设施批量同步
				String infoPush = InfoPushUtil.InfoPush(receiveUrl, pushInfoList, InfoPushUtil.FACILITIES_UPDATE);
				resultBuffer.append(infoPush + ",");
			}
			String resultStr = resultBuffer.toString();
			resultStr.substring(0, resultStr.length() - 1);

			return ResultBuilder.successResult(resultStr, "同步完毕");
		} catch (Exception e) {
			logger.error("synchFacilities()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	private JSONObject getPushInfo(Map<String, Object> facilityInfo) {
		JSONObject pushInfo = new JSONObject();
		pushInfo.put("id", facilityInfo.get("id"));
		pushInfo.put("facilityTypeAlias", facilityInfo.get("facilityTypeAlias"));
		pushInfo.put("facilitiesTypeId", facilityInfo.get("facilitiesTypeId"));
		pushInfo.put("facilityCode", facilityInfo.get("facilitiesCode"));
		pushInfo.put("longitude", facilityInfo.get("longitude"));
		pushInfo.put("latitude", facilityInfo.get("latitude"));
		pushInfo.put("address", facilityInfo.get("address"));
		pushInfo.put("manageState", facilityInfo.get("manageState"));
		pushInfo.put("creationTime", facilityInfo.get("creationTime"));
		pushInfo.put("editorTime", facilityInfo.get("editorTime"));
		pushInfo.put("completeAddress", facilityInfo.get("completeAddress"));
		pushInfo.put("extend", facilityInfo.get("extend"));
		pushInfo.put("showExtend", facilityInfo.get("showExtend"));
		pushInfo.put("areaId", facilityInfo.get("areaId"));
		return pushInfo;
	}



	public Result<Integer> updateFacilitiesDenfenseState(JSONObject jsonObject) {
		logger.info("[进入updateFacilitiesDenfenseState->参数:]" + jsonObject.toJSONString());
		try {
			String defenseState = jsonObject.getString("defenseState");
			//defenseState"调整:4为待整治状态,3改为转待整治审批中状态,审批状态为避免app修改传参修改而修改
			if ("3".equals(defenseState)) {
				RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger,
						"updateFacilitiesDenfenseState", jsonObject, "presentPic", "facilityId", "accessSecret",
						"defenseState","hiddenTroubleDesc","alarmOrderId","alarmOrderPresentPic","userId","marks");
				if (containsParamKey != null) {
					logger.error(containsParamKey.getMessage());
					return ResultBuilder.failResult(containsParamKey.getMessage());
				}
				String alarmOrderId = jsonObject.getString("alarmOrderId");
				String alarmOrderPresentPic = jsonObject.getString("alarmOrderPresentPic");
				String facilityId = jsonObject.getString(FacilitiesContants.FACILITY_ID);
				if (StringUtils.isBlank(alarmOrderId)) {
					logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，告警工单id为空]");
					return ResultBuilder.failResult("告警工单id不能为空");
				}
				if (StringUtils.isBlank(alarmOrderPresentPic)) {
					logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，到场图片为空]");
					return ResultBuilder.failResult("到场图片不能为空");
				}
				
				//TODO 查询设施是否存在未完成的待整治记录
				RpcResponse<Integer> numRpc = facilitiesRenovationQueryService.isExistNotDealFac(facilityId);
				if (!numRpc.isSuccess()) {
					logger.error("[updateFacilitiesDenfenseState->error,设施状态更改为待整治审批中失败,查询设施待整治信息失败]");
					return ResultBuilder.failResult("查询设施待整治信息失败");
				}
				if (numRpc.getSuccessValue() > 0) {
					logger.error("[updateFacilitiesDenfenseState->error,设施状态更改为待整治审批中失败,设施已存在处理中的待整治信息]");
					return ResultBuilder.failResult("设施已存在处理中的待整治信息");
				}

				RpcResponse<Integer> result = facilitiesRenovationCrudService.addFacilitiesRenovation(jsonObject);
				
				if (result.isSuccess()) {
					logger.info("[updateFacilitiesDenfenseState->success,设施状态更改为待整治审批中成功]");
					return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
				} else {
					logger.error("[updateFacilitiesDenfenseState->error,设施状态更改为待整治审批中失败]");
					return ResultBuilder.failResult(result.getMessage());
				}

			} else {

				RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger,
						"updateFacilitiesDenfenseState", jsonObject, "organizationId", "facilityId", "accessSecret",
						"defenseState");
				if (containsParamKey != null) {
					logger.error(containsParamKey.getMessage());
					return ResultBuilder.failResult(containsParamKey.getMessage());
				}
				String organizationId = jsonObject.getString(CommonConstants.ORGANIZATION_ID);
				String accessSecret = jsonObject.getString(CommonConstants.ACCESS_SECRET);
				// String defenseState = jsonObject.getString("defenseState");

				@SuppressWarnings("unchecked")
				List<String> facList = jsonObject.getObject(FacilitiesContants.FACILITY_ID, List.class);

				RpcResponse<Integer> updateRes = facilitiesCrudService.updateFacilitiesDenfenseState(organizationId,
						facList, request.getHeader(InterGatewayConstants.TOKEN), defenseState, accessSecret);
				if (updateRes.isSuccess()) {
					logger.info("[updateFacilitiesDenfenseState->success,设施屏蔽操作成功]");
					return ResultBuilder.successResult(updateRes.getSuccessValue(), updateRes.getMessage());
				}
				logger.error("[updateFacilitiesDenfenseState->error,设施屏蔽操作失败]");
				return ResultBuilder.failResult(updateRes.getMessage());
			}

		} catch (Exception e) {
			logger.error("[updateFacilitiesDenfenseState->Exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	  * 
	  * @Description: 审核待整治设施
	  * @param 
	  * @return
	  */
	
	public Result<String> examineRenovationFacility(JSONObject jsonObject) {
		try {
			RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger,
					"examineRenovationFacility", jsonObject, "facilityId", "accessSecret", "userId",
					"whetherPass");
			if (containsParamKey != null) {
				logger.error(containsParamKey.getMessage());
				return ResultBuilder.failResult(containsParamKey.getMessage());
			}
			String whetherPass = jsonObject.getString("whetherPass");
			if (!"yes".equals(whetherPass) && !"no".equals(whetherPass)) {
				logger.error("[examineRenovationFacility->error:传入参数值非法");
				return ResultBuilder.failResult("传入参数非法");
			}
			
			RpcResponse<Integer> result = facilitiesCrudService.examineRenovationFacility(jsonObject);
			
			if (result.isSuccess()) {
				logger.info("[examineRenovationFacility->success,设施状态更改为待整治成功]");
				return ResultBuilder.successResult("yes", "审核通过");
			} else {
				logger.error("[examineRenovationFacility->error,设施状态更改为待整治失败]");
				return ResultBuilder.failResult(result.getMessage());
			}
		} catch (Exception e) {
			logger.error("[examineRenovationFacility->Exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	  * 
	  * @Description: 待整治设施处理(转故障工单)
	  * @param 
	  * @return
	  */
	
	public Result<String> renovationFacility2FaultOrder(JSONObject jsonObject) {
		try {
			logger.info(String.format("[orderApprove->request params:%s]", jsonObject));
			
			//TODO  待整治设施处理(转故障工单)
			if (StringUtils.isBlank(jsonObject.getString("userId"))) {
				logger.error(String.format("[renovationFacility2FaultOrder()->error:%s--%s]", LogMessageContants.PARAMETERS_OF_ILLEGAL,
						"userId"));
				return ResultBuilder.noBusinessResult();
			}
			
			StringBuffer resultBuffer = new StringBuffer();
			StringBuffer successBuffer = new StringBuffer();
			StringBuffer faultBuffer = new StringBuffer();
			faultBuffer.append("生成故障工单失败:");
			successBuffer.append("生成故障工单成功:");
			boolean isFault = false;
			int countSuccess = 0;
			int countfault = 0;
			
			
			JSONArray facInfoArray = jsonObject.getJSONArray("facInfo");
			List<JSONObject> facInfoList = facInfoArray.toJavaList(JSONObject.class);
			for (JSONObject facInfo : facInfoList) {
				String facilityId = facInfo.getString("facilityId");
				String facilitiesCode = facInfo.getString("facilitiesCode");
				
				String accessSecret = jsonObject.getString("accessSecret");
				//根据设施id查询设备id
				RpcResponse<List<Map<String, Object>>> boundDeviceInfo = facilitiesQueryService.getBoundDeviceInfo(facilityId, accessSecret);
				if (!boundDeviceInfo.isSuccess()) {
					return ResultBuilder.failResult(String.format("查询设施%s绑定设备信息失败", facilitiesCode));
				}
				List<Map<String, Object>> boundDevices = boundDeviceInfo.getSuccessValue();
				if (boundDevices == null || boundDevices.isEmpty()) {
					return ResultBuilder.failResult(String.format("设施%s绑定信息为空", facilitiesCode));
				}
				List<String> deviceIds = Lists.newArrayList();
				for (Map<String, Object> map : boundDevices) {
					Object deviceId = map.get("deviceId");
					if (null != deviceId && StringUtils.isNotBlank(deviceId + "")) {
						deviceIds.add(deviceId + "");
					}
				}
				//验证设备是否存在未完成故障工单
				RpcResponse<List<String>> findFaultOrderByDeviceIdRes = faultOrderProcessQueryService
						.findFaultOrderByDeviceId(deviceIds);
				if (!findFaultOrderByDeviceIdRes.isSuccess()) {
					logger.info("[renovationFacility2FaultOrder()->error:]" + findFaultOrderByDeviceIdRes.getMessage());
					return ResultBuilder.failResult(findFaultOrderByDeviceIdRes.getMessage());
				}
				List<String> faultOrderIds = findFaultOrderByDeviceIdRes.getSuccessValue();
				if (!faultOrderIds.isEmpty()) {
					logger.info("[renovationFacility2FaultOrder()->error:]" + deviceIds.toString());
					return ResultBuilder.failResult(String.format("设施%s绑定的设备已存在故障工单！无法再生成故障工单！", facilitiesCode));
				}
				//生成新的故障工单并自动审核
				JSONObject parmFau = new JSONObject();
				String orderImg = jsonObject.getString("orderImg");
				String organizeId = facInfo.getString("organizeId");
				setParmFau(jsonObject, deviceIds, parmFau, orderImg, organizeId);
				
				// 调用生成工单
				RpcResponse<JSONObject> addOrUpdateFaultOrder = faultOrderProcessCudService.addOrUpdateFaultOrder(parmFau);
				if (null == addOrUpdateFaultOrder) {
					logger.error("工单生成失败");
					faultBuffer.append(facilitiesCode + ",");
					isFault = true;
					countfault++;
					continue;
				}
				if (addOrUpdateFaultOrder.getSuccessValue() == null
						|| !addOrUpdateFaultOrder.isSuccess()) {
					logger.error(String.format("[renovationFacility2FaultOrder()->error:%s]", addOrUpdateFaultOrder.getMessage()));
					faultBuffer.append(facilitiesCode + ",");
					isFault = true;
					countfault++;
					continue;
					//return ResultBuilder.failResult(addOrUpdateFaultOrder.getMessage());
				}
				String processId = addOrUpdateFaultOrder.getSuccessValue().getString("processId");
				String faultOrderId = addOrUpdateFaultOrder.getSuccessValue().getString("faultOrderId");
				//自动审批通过当前故障工单(需要操作人同时在发起和审核节点)
				JSONObject params4Update = new JSONObject();
				params4Update.put("processId", processId);
				params4Update.put("detail", "");
				params4Update.put("id", faultOrderId);
				params4Update.put("operationType", "通过");
				params4Update.put("accessSecret", accessSecret);
				params4Update.put("faultProcessType", "3");
				
				params4Update.put("userId", jsonObject.getString("userId"));
				RpcResponse<String> resultInfo = faultOrderProcessCudService.updateFaultOrderState(params4Update);
				if (!resultInfo.isSuccess()) {
					logger.info(String.format("[renovationFacility2FaultOrder()->自动审核通过fail:%s]", resultInfo.getMessage()));
					isFault = true;
					countfault++;
					continue;
				}
				
				//修改设备状态为维修中
				for (String string : deviceIds) {
					updateDeviceState(string);
				}
				//修改设施状态为5维修中
				List<String> facIds = Lists.newArrayList(facilityId);
				facilitiesCrudService.updateFacilitiesDenfenseState(null, facIds, null, "5", accessSecret);
				//修改待整治表设施状态为已处理
				Map<String, Object> params = Maps.newHashMap();
				params.put("userId", jsonObject.getString("userId"));
				params.put("facilityId", facilityId);
				//0,已转故障(已处理不可用)
				params.put("manageState", "0");
				
				facilitiesRenovationCrudService.updateFacRenovationManageState(params);
				logger.info("[orderApprove()->success:转故障工单成功！]");
				successBuffer.append(facilitiesCode + ",");
				countSuccess++;
				//return ResultBuilder.successResult(faultOrderId, "转故障工单成功！");
			}
			if (facInfoList.size() == 1) {
				if (isFault) {
					return ResultBuilder.failResult("转故障失败");
				} else {
					return ResultBuilder.successResult("转故障成功", "操作成功！");
				}
			} else {
				if (countfault == 0 && countSuccess > 0) {
					resultBuffer.append(successBuffer).append(";");
					return ResultBuilder.successResult(resultBuffer.toString(), "操作成功！");
				} else if (countSuccess == 0 && countfault > 0) {
					resultBuffer.append(successBuffer).append(";");
					return ResultBuilder.failResult("操作失败");
				} else {
					resultBuffer.append(successBuffer).append(";").append(faultBuffer);
					return ResultBuilder.successResult(resultBuffer.toString(), "操作成功！");
				}
				
			}
			
			
		} catch (Exception e) {
			logger.error("orderApprove()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	/**
	  * 
	  * @Description:
	  * @param 
	 * @return 
	  * @return
	  */
	
	private Result<String> setParmFau(JSONObject parseObject, List<String> deviceList, JSONObject parmFau, String orderImg, String organizeId) {
		parmFau.put("organizeId", organizeId);
		
		parmFau.put("mark", parseObject.getString("mark"));
		parmFau.put("faultType", parseObject.getString("faultType"));
		parmFau.put("type", "add");
		parmFau.put("accessSecret", parseObject.getString(AlarmOrderConstants.USC_ACCESS_SECRET));
		parmFau.put("deviceCount", deviceList.size());
		//faultProcessType:1-告警转故障,2-新建故障工单,3-待整治转故障
		parmFau.put("faultProcessType", "3");
		parmFau.put("orderName", parseObject.getString("orderName"));
		parmFau.put("orderImg", orderImg);
		// 通过工单id获取流程id（告警流程） ——》获取发起人 ws1
		;
		parmFau.put("userId",  parseObject.getString("userId"));
		parmFau.put("manager", parseObject.getString("manager"));
		parmFau.put("phone", parseObject.getString("phone"));
		parmFau.put("deviceIdsAdd", deviceList);
		RpcResponse<List<Map<String, Object>>> queryDeviceDetail = deviceQueryService
				.queryBatchDeviceInfoForDeviceIds(deviceList);
		if (queryDeviceDetail == null || !queryDeviceDetail.isSuccess()
				|| queryDeviceDetail.getSuccessValue() == null || queryDeviceDetail.getSuccessValue().isEmpty()) {
			logger.error("[orderApprove()->error:设备未绑定设备组 !]");
			return ResultBuilder.failResult("设备未绑定设备组 !");
		}
		Map<String, Object> factoryInfo = queryDeviceDetail.getSuccessValue().get(0);
		if (factoryInfo == null || !factoryInfo.containsKey(CommonConstants.APPTAG)) {
			logger.error("[orderApprove()->error:未查到有效appTage!]");
			return ResultBuilder.failResult("未查到有效appTage");
		}

		String appTag = (String) factoryInfo.get("appTag");

		RpcResponse<List<Map<String, Object>>> queryFactoryInfoByAppTag = factoryQueryService
				.queryFactoryInfoByAppTag(appTag);
		if (queryFactoryInfoByAppTag == null || !queryFactoryInfoByAppTag.isSuccess()
				|| queryFactoryInfoByAppTag.getSuccessValue() == null) {
			logger.error("[orderApprove()->error:未查到厂家信息，appTage可能未绑定厂家！]");
			return ResultBuilder.failResult("未查到厂家信息，appTage可能未绑定厂家！");
		}
		List<Map<String, Object>> facInfo = queryFactoryInfoByAppTag.getSuccessValue();
		if (facInfo == null || facInfo.isEmpty() || !facInfo.get(0).containsKey(CommonConstants.FACTORYID)) {
			logger.error("[orderApprove()->error:未查到厂家信息，appTage可能未绑定厂家！]");
			return ResultBuilder.failResult("未查到厂家信息，appTage可能未绑定厂家！");
		}
		parmFau.put("factoryId", facInfo.get(0).get("factoryId"));
	
		return null;
		
	}



	
	
	/**
	 * @Description:修改设备的状态
	 */

	private void updateDeviceState(String deviceId) {
		List<String> deviceIds = Lists.newArrayList();
		deviceIds.add(deviceId);
		RpcResponse<Boolean> updateDeviceDefendState = deviceInfoCudService.updateDeviceDefendState(deviceIds, false);
		if (!updateDeviceDefendState.isSuccess()) {
			logger.error(String.format("[updateDeviceState()->error:%s]", updateDeviceDefendState.getMessage()));
		}
	}
	

	
}
