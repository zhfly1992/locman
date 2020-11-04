/*
* File name: TimeoutReportConfigRestQueryService.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			guofeilong		2018年6月26日
* ...			...			...
*
***************************************************/

package com.run.locman.query.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.entity.TimeoutReportConfig;
import com.run.locman.api.model.FacilitiesDtoModel;
import com.run.locman.api.query.service.TimeoutReportConfigQueryService;
import com.run.locman.api.timer.query.service.DeviceQuery;
import com.run.locman.constants.CommonConstants;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年6月26日
*/

@Service
public class TimeoutReportConfigRestQueryService {
	
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private TimeoutReportConfigQueryService	timeoutReportConfigQueryService;
	
	
	@Autowired
	DeviceQuery deviceQuery;
	
	
	public Result<PageInfo<TimeoutReportConfig>> queryTimeoutReportConfigList(String accessSecret, String pageNumParam, String pageSizeParam, String name) {
		logger.info(String.format("[queryTimeoutReportConfigList()->request params--accessSecret:%s]", accessSecret));
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("接入方密钥不能为空");
			return ResultBuilder.invalidResult();
		}
		//分页参数默认值
		int pageNum;
		int pageSize;
		if (StringUtils.isNumeric(pageNumParam)) {
			pageNum = Integer.parseInt(pageNumParam);
		} else {
			pageNum = 1;
		}
		if (StringUtils.isNumeric(pageSizeParam)) {
			pageSize = Integer.parseInt(pageSizeParam);
		} else {
			pageSize = 10;
		}
		try {
			RpcResponse<PageInfo<TimeoutReportConfig>> timeoutConfigList = timeoutReportConfigQueryService
					.getTimeoutConfigList(accessSecret,pageNum, pageSize, name);
			if (timeoutConfigList.isSuccess()) {
				logger.info(String.format("[queryTimeoutReportConfigList()->success:%s]", timeoutConfigList.getMessage()));
				return ResultBuilder.successResult(timeoutConfigList.getSuccessValue(), timeoutConfigList.getMessage());
			} else {
				logger.error(String.format("[queryTimeoutReportConfigList()->fail:%s]", timeoutConfigList.getMessage()));
				return ResultBuilder.failResult(timeoutConfigList.getMessage());
			} 
		} catch (Exception e) {
			logger.error("queryTimeoutReportConfigList()->exception",e);
			return ResultBuilder.exceptionResult(e);
		}
		
	}

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public Result<Map<String, Object>> queryTimeoutReportConfigById(String id) {
		logger.info(String.format("[queryTimeoutReportConfigById()->request params--id:%s]", id));
		if (StringUtils.isBlank(id)) {
			logger.error("id不能为空");
			return ResultBuilder.invalidResult();
		}
		try {
			RpcResponse<Map<String, Object>> timeoutConfigById = timeoutReportConfigQueryService
					.getTimeoutConfigById(id);
			Map<String, Object> successValue = timeoutConfigById.getSuccessValue();
			if (!timeoutConfigById.isSuccess() || null == successValue) {
				logger.error(String.format("[queryTimeoutReportConfigById()->fail:%s]", timeoutConfigById.getMessage()));
				return ResultBuilder.failResult(timeoutConfigById.getMessage());
			} else {
				logger.info(String.format("[queryTimeoutReportConfigById()->success:%s]", timeoutConfigById.getMessage()));
				return ResultBuilder.successResult(successValue, timeoutConfigById.getMessage());
			}
		} catch (Exception e) {
			logger.error("queryTimeoutReportConfigById()->exception",e);
			return ResultBuilder.exceptionResult(e);
		}
	}

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public Result<PageInfo<Facilities>> findFacInfo(FacilitiesDtoModel facilitiesDtoModel) {
		try {
			if (facilitiesDtoModel == null) {
				logger.error("[findFacInfo()->error:传入参数为空]");
				return ResultBuilder.invalidResult();
			}
            logger.info(String.format("[findFacInfo()->request params--accessSecret:%s]", facilitiesDtoModel.getAccessSecret()));
			RpcResponse<PageInfo<Facilities>> findFacInfo = timeoutReportConfigQueryService.findFacInfo(facilitiesDtoModel);

			if (findFacInfo.isSuccess()) {
				logger.info(String.format("[findFacInfo()->success:%s]", findFacInfo.getMessage()));
				return ResultBuilder.successResult(findFacInfo.getSuccessValue(), findFacInfo.getMessage());
			}
			logger.error(String.format("[findFacInfo()->fail:%s]", findFacInfo.getMessage()));
			return ResultBuilder.failResult(findFacInfo.getMessage());

		} catch (Exception e) {
			logger.error("findFacInfo()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	public Result<String> test() {
		try {
			logger.info("开始插入数据");
			for (int i = 1; i < 10; i++) {
				deviceQuery.insertCountTimingAndTrigger(i);
				logger.info("数据");
				Thread.sleep(200);
			}
			return ResultBuilder.successResult("操作成功", "操作成功");

		} catch (Exception e) {
			logger.error("findFacInfo()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
