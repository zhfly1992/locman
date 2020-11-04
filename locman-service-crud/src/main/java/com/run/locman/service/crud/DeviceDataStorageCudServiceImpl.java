/*
 * File name: DeviceDataStorageCudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月26日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.run.common.util.UUIDUtil;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.base.query.FacilitesService;
import com.run.locman.api.crud.repository.DeviceDataStorageCudRepository;
import com.run.locman.api.crud.repository.FacilitiesCrudRepository;
import com.run.locman.api.crud.repository.FacilityDeviceCrudRepository;
import com.run.locman.api.crud.service.DeviceDataStorageCudService;
import com.run.locman.api.crud.service.UpdateRedisCrudService;
import com.run.locman.api.dto.DeviceDataDto;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceDataStorage;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月26日
 */
@Transactional(rollbackFor = Exception.class)
public class DeviceDataStorageCudServiceImpl implements DeviceDataStorageCudService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceDataStorageCudRepository	deviceDataStorageCudRepository;

	@Autowired
	private FacilitiesCrudRepository		facilitiesCrudRepository;

	@Autowired
	private FacilityDeviceCrudRepository	facilityDeviceCrudRepository;

	@Autowired
	private FacilitesService				facilitesService;

	@Autowired
	private DeviceQueryService				deviceQueryService;
	
	@Autowired
	private UpdateRedisCrudService			updateRedisCrudService;



	/**
	 * @see com.run.locman.api.crud.service.DeviceDataStorageCudService#addDeviceData(java.util.List)
	 */
	@Override
	public RpcResponse<Integer> addDeviceData(List<DeviceDataStorage> deviceDataStorageList) {
		logger.info("addDeviceData()-->进入方法,参数:deviceDataStorageList:" + deviceDataStorageList);
		if (null == deviceDataStorageList || deviceDataStorageList.isEmpty()) {
			logger.error("[addDeviceData()-->集合参数不能为空!");
			return RpcResponseBuilder.buildErrorRpcResp("数据添加失败,集合参数不能为空!");
		}
		try {
			for (DeviceDataStorage deviceDataStorage : deviceDataStorageList) {
				deviceDataStorage.setId(UUIDUtil.getUUID());
				deviceDataStorage.allParamNull2Empty();
			}
			int addDeviceDataNum = deviceDataStorageCudRepository.addDeviceData(deviceDataStorageList);
			if (addDeviceDataNum > 0) {
				logger.info("addDeviceData()-->添加成功:" + addDeviceDataNum);
				return RpcResponseBuilder.buildSuccessRpcResp("数据添加成功", addDeviceDataNum);
			} else {
				logger.error("[addDeviceData()-->数据添加失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("数据添加失败");
			}
		} catch (Exception e) {
			logger.error("addDeviceData()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceDataStorageCudService#deleteById(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Integer> deleteById(String deviceDataId, String userId) {
		logger.info(String.format("[deleteById()->request params:%s]", deviceDataId));
		try {
			if (StringUtils.isBlank(deviceDataId)) {
				logger.error("[deleteById->error:id不能为空!!!]");
				return RpcResponseBuilder.buildErrorRpcResp("id不能为空");
			}
			int result = deviceDataStorageCudRepository.deleteById(deviceDataId, userId);
			if (result > 0) {
				logger.info("[deleteById->success:数据删除成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("数据删除成功!", result);
			} else {
				logger.error("[deleteById->error:数据删除失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("数据删除失败!");
			}
		} catch (Exception e) {
			logger.error("deleteById()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceDataStorageCudService#updateDeviceData(com.run.locman.api.entity.DeviceDataStorage)
	 */
	@Override
	public RpcResponse<Integer> updateDeviceData(DeviceDataStorage deviceDataStorage) {
		logger.info(String.format("[updateDeviceData()->request params:%s]", deviceDataStorage));
		try {
			if (StringUtils.isBlank(deviceDataStorage.getId())) {
				logger.error("[updateDeviceData->error:id不能为空!!!]");
				return RpcResponseBuilder.buildErrorRpcResp("id不能为空");
			}
			int result = deviceDataStorageCudRepository.updateDeviceData(deviceDataStorage);
			if (result > 0) {
				logger.info("[updateDeviceData->success:数据修改成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("数据修改成功!", result);
			} else {
				logger.error("[updateDeviceData->error:数据修改失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("数据修改失败!");
			}
		} catch (Exception e) {
			logger.error("updateDeviceData()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceDataStorageCudService#synchronizeToFacilities(com.run.locman.api.dto.DeviceDataDto,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> synchronizeToFacilities(DeviceDataDto deviceDataDto, String accessSecret, String userId,
			String organizationId) {
		logger.info(String.format("[synchronizeToFacilities()->request params:%s]", deviceDataDto));
		try {
			RpcResponse<Boolean> paramCheck = paramCheck(deviceDataDto, accessSecret);
			if (null != paramCheck) {
				return paramCheck;
			}
			// 新增时id为空,传null
			RpcResponse<Boolean> checkFacilitiesCode = facilitesService
					.checkFacilitiesCode(deviceDataDto.getFacilitiesCode(), accessSecret, null);
			if (null == checkFacilitiesCode || null == checkFacilitiesCode.getSuccessValue()) {
				logger.error("[synchronizeToFacilities()->error: 新增设施失败,校验设施序列号是否重复失败,返回参数为null]");
				return RpcResponseBuilder.buildErrorRpcResp("新增设施失败,校验设施序列号是否重复失败");
			} else if (!checkFacilitiesCode.isSuccess() || !checkFacilitiesCode.getSuccessValue()) {
				logger.error("[synchronizeToFacilities()->error: 新增设施失败,校验设施序列号是否重复失败]");
				return RpcResponseBuilder.buildErrorRpcResp(checkFacilitiesCode.getMessage());
			}
			Facilities facilities = setFacilities(deviceDataDto, accessSecret, userId, organizationId);
			

			logger.info(String.format("[synchronizeToFacilities()->进入方法,参数:%s]", facilities));

			Device deviceExist = new Device();
			String deviceNumber = deviceDataDto.getDeviceNumber();
			
			// 校验是否存在设备编号对应的设备,不存在或者设备已经被其他设施绑定则同步失败
			if (StringUtils.isNotBlank(deviceNumber)) {
				deviceNumber = deviceNumber.substring(3);
				RpcResponse<Device> deviceResult = deviceQueryService.queryDeviceByhardwareIdAndAS(deviceNumber,
						accessSecret);

				if (deviceResult.isSuccess() && null != deviceResult.getSuccessValue()) {
					Device device = deviceResult.getSuccessValue();
					String id = device.getId();
					if (StringUtils.isBlank(id)) {
						logger.error(
								String.format("[synchronizeToFacilities()->未能找到此设备编码对应的设备或设备已被绑定:%s]", deviceNumber));
						return RpcResponseBuilder.buildErrorRpcResp("未能找到此设备编码对应的设备或设备已被绑定:" + deviceNumber);
					} else {
						deviceExist.setId(id);
						deviceExist.setDeviceType(device.getDeviceType());
					}

				} else {
					logger.error("[synchronizeToFacilities()->error: 新增设施失败,查询设备失败]");
					return RpcResponseBuilder.buildErrorRpcResp("未能找到此设备编码对应的设备或设备已被绑定:" + deviceNumber);
				}
			}

			int num = facilitiesCrudRepository.insertModel(facilities);
			if (num > 0) {
				String deviceId = deviceExist.getId();
				// 存在设备,插入设施与设备的中间表关系
				if (StringUtils.isNotBlank(deviceId)) {
					Map<String, Object> addFacilityRsDecixe = Maps.newHashMap();
					addFacilityRsDecixe.put("deviceId", deviceId);
					addFacilityRsDecixe.put("facilityId", facilities.getId());
					addFacilityRsDecixe.put("deviceTypeId", deviceExist.getDeviceType());
					logger.info("[synchronizeToFacilities()->info: 存在对应设备,添加绑定关系"
							+ JSON.toJSONString(addFacilityRsDecixe) + "]");
					facilityDeviceCrudRepository.insertFacilityRsDevice(addFacilityRsDecixe);
				}
				
				//放入Redis缓存
				Map<String,Object> hashMap =Maps.newHashMap();
				hashMap.put("id", facilities.getId());
				hashMap.put("accessSecret", accessSecret);
				updateRedisCrudService.updateFacMapCache(hashMap);
				

				logger.info("[synchronizeToFacilities()->info: 新增设施成功" + JSON.toJSONString(facilities) + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.FACILITIES_ADD_SUCCESS, true);
			} else {
				logger.error("[synchronizeToFacilities()->error: 新增设施失败]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.FACILITIES_ADD_FAIL);
			}

		} catch (Exception e) {
			logger.error("synchronizeToFacilities()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<Boolean> paramCheck(DeviceDataDto deviceDataDto, String accessSecret) {
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("[synchronizeToFacilities()->error: 新增设施失败，接入方密钥不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
		}
		if (StringUtils.isBlank(deviceDataDto.getFacilityTypeId())) {
			logger.error("[synchronizeToFacilities()->error: 新增设施失败，设施类型id不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设施类型id不能为空");
		}
		if (StringUtils.isBlank(deviceDataDto.getFacilitiesCode())) {
			logger.error("[synchronizeToFacilities()->error: 新增设施失败，设施序列号不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设施序列号不能为空");
		}
		if (StringUtils.isBlank(deviceDataDto.getLatitude())) {
			logger.error("[synchronizeToFacilities()->error: 新增设施失败，纬度不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("纬度不能为空");
		}
		if (StringUtils.isBlank(deviceDataDto.getLongitude())) {
			logger.error("[synchronizeToFacilities()->error: 新增设施失败，经度不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("经度不能为空");
		}
		if (StringUtils.isBlank(deviceDataDto.getAddress())) {
			logger.error("[synchronizeToFacilities()->error: 新增设施失败，地址不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("地址不能为空");
		}
		if (StringUtils.isBlank(deviceDataDto.getAreaCode())) {
			logger.error("[synchronizeToFacilities()->error: 新增设施失败，区域code不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("区域code不能为空");
		}
		return null;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private Facilities setFacilities(DeviceDataDto deviceDataDto, String accessSecret, String userId,
			String organizationId) {
		Facilities facilities = new Facilities();
		facilities.setId(UUIDUtil.getUUID());
		facilities.setCreationUserId(userId);
		facilities.setAccessSecret(accessSecret);
		facilities.setOrganizationId(organizationId);
		facilities.setFacilitiesTypeId(deviceDataDto.getFacilityTypeId());
		facilities.setFacilitiesCode(deviceDataDto.getFacilitiesCode());
		facilities.setLongitude(deviceDataDto.getLongitude());
		facilities.setLatitude(deviceDataDto.getLatitude());
		facilities.setAddress(deviceDataDto.getAddress());
		facilities.setAreaId(deviceDataDto.getAreaCode());
		facilities.setExtend(deviceDataDto.getExtend());
		facilities.setShowExtend(deviceDataDto.getShowExtend());
		facilities.setManageState(FacilitiesContants.ENABLE);
		facilities.setCreationTime(DateUtils.formatDate(new Date()));
		facilities.setCompleteAddress(deviceDataDto.getCompleteAreaName() + deviceDataDto.getAddress());
		return facilities;
	}

	/**
	 * @see com.run.locman.api.crud.service.DeviceDataStorageCudService#deleteUnusedFile(int
	 *      size)
	 */
	/*
	 * @Override public List<String> deleteUnusedFile(int size) { // TODO
	 * Auto-generated method stub
	 * 
	 * List<String> unusedFile =
	 * deviceDataStorageCudRepository.getUnusedFile(size);
	 * 
	 * 
	 * 
	 * 
	 * return unusedFile; }
	 * 
	 * 
	 * 
	 *//**
		 * @see com.run.locman.api.crud.service.DeviceDataStorageCudService#updateDeleteState(java.util.List)
		 */
	/*
	 * @Override public int updateDeleteState(List<String> deleteFiles) { try {
	 * logger.info(deleteFiles); int updateDeleteState =
	 * deviceDataStorageCudRepository.updateDeleteState(deleteFiles);
	 * logger.info("修改数量" + updateDeleteState); return updateDeleteState; }
	 * catch (Exception e) { return 0; } }
	 * 
	 * 
	 * 
	 *//**
		 * @see com.run.locman.api.crud.service.DeviceDataStorageCudService#getUnusedFileCount()
		 *//*
		 * @Override public int getUnusedFileCount() { // TODO Auto-generated
		 * method stub
		 * 
		 * return deviceDataStorageCudRepository.getUnusedFileCount(); }
		 */

}
