/*
* File name: FacilityDeviceCudServiceImpl.java
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
* 1.0			guolei		2017年10月20日
* ...			...			...
*
***************************************************/

package com.run.locman.service.crud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FacilityDeviceCrudRepository;
import com.run.locman.api.crud.service.FacilityDeviceCudService;
import com.run.locman.api.crud.service.UpdateRedisCrudService;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilityDeviceContants;

/**
 * 
 * @Description: 设施与设备的关系实现(更改)
 * @author: guolei
 * @version: 1.0, 2017年9月14日
 */
@Transactional(rollbackFor = Exception.class)
public class FacilityDeviceCudServiceImpl implements FacilityDeviceCudService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private FacilityDeviceCrudRepository	facilityDeviceCrudRepository;
	
	@Autowired
	private FacilitiesQueryService			facilitiesQueryService;
	
	@Autowired
	private UpdateRedisCrudService			updateRedisCrudService;



	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Map<String, Object>> bindFacilityWithDevices(JSONObject paramJson) {
		try {
			if (StringUtils.isBlank(paramJson.getString(FacilityDeviceContants.FACILITY_ID))) {
				logger.error("[bindFacilityWithDevices()->invalid：绑定设施与设备,设施id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施与设备的关系信息：绑定设施与设备,设施id不能为空！");
			}
			if (StringUtils.isBlank(paramJson.getString(FacilityDeviceContants.DEVICE_INFO))) {
				logger.error("[bindFacilityWithDevices()->invalid：设备信息参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备信息参数不能为空!");
			}
			logger.info(String.format("[bindFacilityWithDevices()->进入方法,参数:%s]", paramJson));
			List<String> deviceIdList = new ArrayList<>();
			List list =(List) paramJson.get(FacilityDeviceContants.DEVICE_INFO);
//			List list = (List) JSONObject.parseArray(paramJson.getString("deviceInfo"), List.class);
			if (null != list && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					deviceIdList.add(map.get("deviceId").toString());
				}
			} else {
				logger.error("[bindFacilityWithDevices()->invalid：绑定设施与设备,设备id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施与设备的关系信息：绑定设施与设备,设备id不能为空！");
			}
			// 删除绑定避免重复
			Map<String, Object> insertParam = Maps.newHashMap();
			insertParam.put(FacilityDeviceContants.FACILITY_ID, paramJson.getString("facilityId"));
			insertParam.put(FacilityDeviceContants.DEVICE_ID_ARRAY, deviceIdList);
			facilityDeviceCrudRepository.deleteByBatch(insertParam);

			int count = 0;
			// 绑定设施与设备关系
			if (null != list && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					map.put("facilityId", paramJson.getString("facilityId"));
					int num = facilityDeviceCrudRepository.insertFacilityRsDevice(map);
					count += num;
				}
			}
			//redis缓存
			Map<String,Object> queryMap=new HashMap<String,Object>();
			RpcResponse<Facilities> findById = facilitiesQueryService.findById(paramJson.getString("facilityId"));
			Facilities facilities = findById.getSuccessValue();
			queryMap.put("id", paramJson.getString("facilityId"));
			queryMap.put("accessSecret",facilities.getAccessSecret());
			updateRedisCrudService.updateFacMapCache(queryMap);
			if (count == deviceIdList.size()) {
				logger.debug("[bindFacilityWithDevices()->success:绑定设施与设备成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("绑定设施与设备成功", insertParam);
			} else {
				logger.debug("[bindFacilityWithDevices()->fail：绑定设施与设备失败");
				return RpcResponseBuilder.buildErrorRpcResp("绑定设施与设备失败");
			}

		} catch (Exception e) {
			logger.error("bindFacilityWithDevices()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Map<String, Object>> unbindFacilityWithDevices(String facilityId, String devicesId) {
		try {
			if (StringUtils.isBlank(facilityId)) {
				logger.error("[unbindFacilityWithDevices()->invalid：解绑设施与设备,设施id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施与设备的关系信息：解绑设施与设备,设施id不能为空！");
			}
			if (StringUtils.isBlank(devicesId)) {
				logger.error("[unbindFacilityWithDevices()->invalid：解绑设施与设备,设备id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("设施与设备的关系信息：解绑设施与设备,设备id不能为空！");
			}
			logger.info(String.format("[unbindFacilityWithDevices()->进入方法,参数facilityId:%s,devicesId:%s]", facilityId,devicesId));
			String[] deviceIdArray = devicesId.split(",");
			// 开始解绑
			Map<String, Object> deleteParam = Maps.newHashMap();
			deleteParam.put(FacilityDeviceContants.FACILITY_ID, facilityId);
			deleteParam.put(FacilityDeviceContants.DEVICE_ID_ARRAY, deviceIdArray);
			int deleteResult = facilityDeviceCrudRepository.deleteByBatch(deleteParam);
			if (deleteResult >= 0) {
				//redis缓存
				Map<String,Object> queryMap=new HashMap<String,Object>();
				RpcResponse<Facilities> findById = facilitiesQueryService.findById(facilityId);
				Facilities facilities = findById.getSuccessValue();
				queryMap.put("id", facilityId);
				queryMap.put("accessSecret",facilities.getAccessSecret());
				updateRedisCrudService.updateFacMapCache(queryMap);
				
				
				logger.debug("[unbindFacilityWithDevices()->success:解绑设施与设备成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("解绑设施与设备成功", deleteParam);
			} else {
				logger.debug("[unbindFacilityWithDevices()->fail：解绑设施与设备失败");
				return RpcResponseBuilder.buildErrorRpcResp("解绑设施与设备失败");
			}

		} catch (Exception e) {
			logger.error("unbindFacilityWithDevices()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
