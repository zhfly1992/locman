/*
 * File name: DeviceStateRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年9月28日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.query.service.DeviceStateQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.filetool.ExcelView;

/**
 * @Description: 设备状态rest导出
 * @author: zhaoweizhi
 * @version: 1.0, 2018年9月28日
 */
@Service
public class DeviceStateRestQueryService {

	private Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceStateQueryService	deviceStateQueryService;



	/**
	 * 
	 * @Description:设备状态导出接口
	 * @param accessSecret
	 * @param model
	 * @return
	 */
	public ModelAndView exportDeviceState(String accessSecret, ModelMap model) {

		try {

			if (model == null) {
				logger.error("[exportDeviceState()->error:model为null！请检查接口参数！]");
				return null;
			}

			RpcResponse<List<Map<String, String>>> res = deviceStateQueryService.findDeviceState(accessSecret);
			if (!res.isSuccess()) {
				logger.error(String.format("[exportDeviceState()->error:%s]", res.getMessage()));
				return null;
			}

			List<Map<String, String>> successValue = res.getSuccessValue();
			// 封装导出数据

			Map<String, Object> map = Maps.newLinkedHashMap();
			map.put(DeviceContants.DEVICEID, "设备Id");
			map.put(DeviceContants.DEVICENAME, "设备名称");
			map.put("updateTime", "更新时间");
			map.put("bv", "电池电压");
			map.put("sig", "信号");
			map.put("hardwareid", "硬件编码");
			model.put(ExcelView.EXCEL_DATASET, successValue);
			model.put(ExcelView.EXCEL_NAME, "设备状态excel");
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			View excelView = new ExcelView();
			return new ModelAndView(excelView);
		} catch (Exception e) {
			logger.error("exportDeviceState()->exception", e);
			return null;
		}

	}

}
