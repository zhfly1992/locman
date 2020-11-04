package com.run.locman.query.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.dto.OpenRecordQueryDto;
import com.run.locman.api.query.service.OpenRecordQueryService;
import com.run.locman.api.util.ConvertUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.OpenRecordConstants;
import com.run.locman.filetool.ExcelView;
/**
 * 
* @Description:	开锁记录查询
* @author: Administrator
* @version: 1.0, 2018年11月21日
 */
@Service
public class OpenRecordRestQueryService {
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private OpenRecordQueryService	openRecordQueryService;





	public Result<PageInfo<OpenRecordQueryDto>> getOpenRecordByPage(String parm) {
		logger.info(String.format("[getOpenRecordBypage()->request parm:%s]", parm));
		try {
			if (ExceptionChecked.checkRequestParam(parm) != null) {
				logger.error(String.format("[getOpenRecordByPage()->:request parm:%s]",
						LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return ResultBuilder.invalidResult();
			}
			// 添加ip好token,调用intergateway接口需要用到
			JSONObject jsonObject = JSONObject.parseObject(parm);

			RpcResponse<PageInfo<OpenRecordQueryDto>> res = openRecordQueryService.getOpenRecordByPage(jsonObject);
			if (res.isSuccess()) {
				logger.info(String.format("[getOpenRecordByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getOpenRecordByPage()->error:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("getOpenRecordByPage()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public ModelAndView exportOpenRecordInfo(String parm, ModelMap model) {
		logger.info(String.format("[exportOpenRecordBypage()->request parm:%s]", parm));
		try {
			if (parm == null || StringUtils.isBlank(parm)) {
				logger.error(String.format("[exportOpenRecordInfo()->:requset parm:%s]",
						LogMessageContants.PARAMETERS_OF_ILLEGAL));
				return null;
			}

			JSONObject jsonObject = JSONObject.parseObject(parm);
			String accessSecret = jsonObject.getString(OpenRecordConstants.ACCESSSECRET);
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("[exportOpenRecordInfo()->:error,接入方密匙不能为空]");
				return null;
			}

			// 构建查询接口所需参数
			jsonObject.put(CommonConstants.PAGE_NUM, 0);
			jsonObject.put(CommonConstants.PAGE_SIZE, 0);


			// 构建导出格式
			Map<String, Object> map = new LinkedHashMap<>();
			map.put(OpenRecordConstants.FACILITIES_CODE, OpenRecordConstants.FACILITIES_CODE_CH);
			map.put(OpenRecordConstants.FACILITIES_TYPE_ALIAS, OpenRecordConstants.FACILITIES_TYPE_ALIAS_CH);
			map.put(OpenRecordConstants.DEIVE_ID, OpenRecordConstants.DEIVE_ID_CH);
			map.put(OpenRecordConstants.ADDRESS, OpenRecordConstants.ADDRESS_CH);
			map.put(OpenRecordConstants.CONTROL_TIME, OpenRecordConstants.CONTROL_TIME_CH);
			map.put(OpenRecordConstants.OPERATE_USER_NAME, OpenRecordConstants.OPERATE_USER_NAME_CH);
			map.put(OpenRecordConstants.RERSON, OpenRecordConstants.RERSON_CH);

			model.put(ExcelView.EXCEL_NAME, OpenRecordConstants.EXCEL_NAME);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			RpcResponse<PageInfo<OpenRecordQueryDto>> res = openRecordQueryService.getOpenRecordByPage(jsonObject);
			List<OpenRecordQueryDto> list = res.getSuccessValue().getList();

			List<Map<String, Object>> resultList = Lists.newArrayList();
			for (OpenRecordQueryDto openRecordQueryDto : list) {
				Map<String, Object> resultMap = ConvertUtil.beanToMap(openRecordQueryDto);
				resultList.add(resultMap);
			}
			// 成功封装数据
			if (res.isSuccess()) {
				logger.info(String.format("[exportOpenRecord()->success:%s]", res.getMessage()));
				model.put(ExcelView.EXCEL_DATASET, resultList);
			} else {
				logger.error(String.format("[exportOpenRecord()->fail:%s]", res.getMessage()));
				return null;
			}

			View excelView = new ExcelView();
			return new ModelAndView(excelView);
		} catch (Exception e) {
			logger.error("[exportOpenRecord()->exception]", e);
			return null;
		}
	}
}
