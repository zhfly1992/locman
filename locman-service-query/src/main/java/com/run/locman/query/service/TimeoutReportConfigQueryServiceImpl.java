/*
 * File name: TimeoutReportConfigQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年6月26日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.DeviceAndTimeDto;
import com.run.locman.api.dto.TimeoutReportConfigQueryDto;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.entity.TimeoutReportConfig;
import com.run.locman.api.model.FacilitiesDtoModel;
import com.run.locman.api.query.repository.TimeoutReportConfigQueryRepository;
import com.run.locman.api.query.service.FacilitiesTimeoutReportConfigQueryService;
import com.run.locman.api.query.service.TimeoutReportConfigQueryService;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.PageUtils;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceInfoConvertConstans;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.TimeoutReportConfigConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年6月26日
 */

public class TimeoutReportConfigQueryServiceImpl implements TimeoutReportConfigQueryService {

	private static final Logger							logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private TimeoutReportConfigQueryRepository			timeoutReportConfigQueryRepository;

	@Autowired
	private FacilitiesTimeoutReportConfigQueryService	facilitiesTimeoutReportConfigQueryService;
	
/*	private static final List<Object> serchKeys = Lists.newArrayList("5101080",
			"510108","101080",
			"51010","10108","01080",
			"5101","1010","0108","1080",
			"510","101","010","108","080",
			"51","10","01","10","08","80",
			"5","1","8","0",
			"四川省成都市成华区",
			"四川省成都市成华","川省成都市成华区",
			"四川省成都市成","川省成都市成华","省成都市成华区",
			"四川省成都市","川省成都市成","省成都市成华","成都市成华区",
			"四川省成都","川省成都市","省成都市成","成都市成华","都市成华区",
			"四川省成","川省成都","省成都市","成都市成","都市成华","市成华区",
			"四川省","川省成","省成都","成都市","都市成","市成华","成华区",
			"四川","川省","省成","成都","都市","市成","成华","华区",
			"四","川","省","成","都","市","华","区"
			,
			"cd2","cd",
			"c","d","2",
			//12
			"000866971033",
			//11
			"00086697103","00866971033",
			//10
			"0008669710","008669710","086697103",
			//9
			"000866971","008669710","086697103","866971033",
			//8
			"00086697","00866971","08669710","86697103","66971033","","","","",
			//7
			"0008669","0086697","0866971","8669710","6697103","6971033","","","",
			//6
			"000866","008669","08669","866971","669710","697103","971033","","",
			//5
			"00086","00866","08669","86697","66971","69710","97103","71033","",
			//4
			"0008","0086","0866","8669","6697","6971","9710","7103","1033","","","",
			//3
			"000","008","086","866","669","697","971","710","103","033","","",
			//2
			"00","08","86","66","69","97","71","10","33",
			"6","9","7","3"
			);*/
	



	/**
	 * @see com.run.locman.api.query.service.TimeoutReportConfigQueryService#getTimeoutConfigList(int,
	 *      int, java.lang.String)
	 */
	@Override
	public RpcResponse<PageInfo<TimeoutReportConfig>> getTimeoutConfigList(String accessSecret, int pageNum,
			int pageSize, String name) {
		logger.info(String.format("[getTimeoutConfigList()方法执行开始...,参数：【%s】【%s】【%s】【%s】]", accessSecret, pageNum,
				pageSize, name));
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("接入方密钥不能为空!!!");
			return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空!!!");
		}
		if (0 >= pageNum) {
			pageNum = 1;
		}
		if (0 >= pageSize) {
			pageSize = 10;
		}
		try {
			TimeoutReportConfigQueryDto timeoutReportConfigQueryDto = new TimeoutReportConfigQueryDto(accessSecret,
					pageNum, pageSize, name);
			PageHelper.startPage(timeoutReportConfigQueryDto.getPageNum(), timeoutReportConfigQueryDto.getPageSize());
			List<TimeoutReportConfig> timeoutConfigList = timeoutReportConfigQueryRepository
					.getTimeoutReoprtConfigList(timeoutReportConfigQueryDto);
			PageInfo<TimeoutReportConfig> page = new PageInfo<>(timeoutConfigList);
			logger.info(LogMessageContants.QUERY_SUCCESS);
			logger.info(String.format("[getTimeoutConfigList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, page);
		} catch (Exception e) {
			logger.error("getTimeoutConfigList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.query.service.TimeoutReportConfigQueryService#getTimeoutConfigById(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> getTimeoutConfigById(String id) {
		logger.info(String.format("[getTimeoutConfigById()方法执行开始...,参数：【%s】]", id));
		if (StringUtils.isBlank(id)) {
			logger.error("id不能为空!!!");
			return RpcResponseBuilder.buildErrorRpcResp("id不能为空!!!");
		}
		try {
			Map<String, Object> resultMap = Maps.newHashMap();
			TimeoutReportConfig timeoutReportConfig = new TimeoutReportConfig();
			timeoutReportConfig.setId(id);
			timeoutReportConfig.setManagerState("enable");
			TimeoutReportConfig timeoutConfigById = timeoutReportConfigQueryRepository
					.getTimeoutConfigById(timeoutReportConfig);
			if (null == timeoutConfigById) {
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			} else {
				resultMap.put(TimeoutReportConfigConstants.CONFIG_INFO, timeoutConfigById);
				RpcResponse<List<String>> queryFacilityTimeoutReportConfigByCId = facilitiesTimeoutReportConfigQueryService
						.queryFacilityTimeoutReportConfigByCId(id);
				List<String> successValue = queryFacilityTimeoutReportConfigByCId.getSuccessValue();
				if (!queryFacilityTimeoutReportConfigByCId.isSuccess() || null == successValue) {
					resultMap.put(TimeoutReportConfigConstants.FACILITY_IDS, new ArrayList<String>());
					resultMap.put(TimeoutReportConfigConstants.SIZE, 0);
				} else {
					resultMap.put(TimeoutReportConfigConstants.FACILITY_IDS, successValue);
					resultMap.put(TimeoutReportConfigConstants.SIZE, successValue.size());
				}
				logger.info(String.format("[getTimeoutConfigById()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, resultMap);
			}
		} catch (Exception e) {
			logger.error("getTimeoutConfigById()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.TimeoutReportConfigQueryService#findFacInfo(com.run.locman.api.model.FacilitiesModel)
	 */
	@Override
	public RpcResponse<PageInfo<Facilities>> findFacInfo(FacilitiesDtoModel facilitiesDtoModel) {
		logger.info(String.format("[findFacInfo()方法执行开始...,参数：【%s】]", facilitiesDtoModel));
		try {
			// 校验参数 业务参数与分页参数
			RpcResponse<PageInfo<Facilities>> checkObjectBusinessKey = CheckParameterUtil.checkObjectBusinessKey(logger,
					"findFacInfo", facilitiesDtoModel, true, DeviceInfoConvertConstans.CONVERT_ACCESSSECRET);

			if (checkObjectBusinessKey != null) {
				return checkObjectBusinessKey;
			}
			
			String pageNoStr = facilitiesDtoModel.getPageNum();
			String pageSizeStr = facilitiesDtoModel.getPageSize();
			
			
			Integer pageNo = Integer.parseInt(pageNoStr);
			Integer pageSize = Integer.parseInt(pageSizeStr);
			//TODO 设备数量1
			/*int returnPageNo = 0;
			int ceil = 0;
			if (pageSize != 0) {
				//设置保留位数
				DecimalFormat df=new DecimalFormat("0.00");
				ceil = (int)Math.ceil(Double.parseDouble(df.format((float)216 / pageSize)));				
			}
			if (pageNo > 36) {
				returnPageNo = pageNo;
					pageNo -= ceil;
			}*/
			
			facilitiesDtoModel.setPageNum(pageNo + "");
			facilitiesDtoModel.setPageSize(pageSize + "");
			
			
			
			// 查询设施id集合
			if (!StringUtils.isBlank(facilitiesDtoModel.getConfigId())
					&& StringUtils.isBlank(facilitiesDtoModel.getBinding())) {
				List<String> findFacIdsByConfigId = timeoutReportConfigQueryRepository
						.findFacIdsByConfigId(facilitiesDtoModel);
				facilitiesDtoModel.setFacIds(findFacIdsByConfigId);
			}

			// 分页
			PageInfo<Facilities> pageStart = PageUtils.pageStart(timeoutReportConfigQueryRepository,
					facilitiesDtoModel);
			
			
			/*pageStart.setTotal(pageStart.getTotal() + 216);
			
			pageStart.setPages(pageStart.getPages() + ceil);

			
			if (returnPageNo > 36) {
				pageStart.setPageNum(returnPageNo);
			}
			

			
			String serchKey = facilitiesDtoModel.getSearchKey();
			String facilitiesTypeId = facilitiesDtoModel.getFacilitiesTypeId();
			if ((StringUtils.isNotBlank(facilitiesTypeId) && !"8d4595dd5fcd4530b8cca9c9e01818d6".equals(facilitiesTypeId))
					|| (StringUtils.isNotBlank(serchKey) && !serchKeys.contains(serchKey))
					) {
				pageStart.setTotal(pageStart.getTotal() - 216);
				
				pageStart.setPages(pageStart.getPages() - ceil);
			}*/
			
			

			logger.info(String.format("[findFacInfo()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(DeviceInfoConvertConstans.OPERATION_SUCCESS, pageStart);

		} catch (Exception e) {
			logger.error("findFacInfo()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<List<DeviceAndTimeDto>> getDeviceAndTime() {
		logger.info(String.format("[getDeviceAndTime()方法执行开始..."));

		try {
			List<DeviceAndTimeDto> deviceAndTime = timeoutReportConfigQueryRepository.getDeviceAndTime();
			if (deviceAndTime == null || deviceAndTime.isEmpty()) {
				logger.info(String.format("[getDeviceAndTime()方法执行结束!]"));
				return RpcResponseBuilder.buildErrorRpcResp("没有查询到需要检测的设备");
			} else {
				logger.info(String.format("[getDeviceAndTime()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("查询成功", deviceAndTime);
			}
		} catch (Exception e) {
			logger.error("getDeviceAndTime()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}

}
