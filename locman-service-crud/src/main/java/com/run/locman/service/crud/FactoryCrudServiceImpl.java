/*
 * File name: FactoryCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 heyuan 2017年10月20日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DeviceInfoCudRepository;
import com.run.locman.api.crud.repository.DeviceTypeCudRepository;
import com.run.locman.api.crud.repository.FactoryAppTagCrudRepository;
import com.run.locman.api.crud.repository.FactoryCrudRepository;
import com.run.locman.api.crud.service.FactoryCrudService;
import com.run.locman.api.dto.AppTagDto;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceType;
import com.run.locman.api.entity.Factory;
import com.run.locman.constants.CommonConstants;

/**
 *
 * @Description: 厂家cud实现类
 * @author: heyuan
 * @version: 1.0, 2017年9月14日
 */
@Transactional(rollbackFor = Exception.class)
public class FactoryCrudServiceImpl implements FactoryCrudService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FactoryCrudRepository		factoryCrudRepository;

	@Autowired
	private DeviceInfoCudRepository		deviceInfoCudRepository;

	@Autowired
	private DeviceTypeCudRepository		deviceTypeCudRepository;

	@Autowired
	private FactoryAppTagCrudRepository	factoryAppTagCrudRepository;



	/**
	 * @see com.run.locman.api.crud.service.FactoryCrudService#deleteFactoryById(java.lang.String)
	 */
	@Override
	public RpcResponse<String> deleteFactoryById(String factoryId) {
		try {
			if (StringUtils.isBlank(factoryId)) {
				logger.error("[deleteFactoryById()->invalid: 厂家id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("厂家id不能为空");
			}
			logger.info(String.format("[deleteFactoryById()->进入方法,参数:%s]", factoryId));
			int num = factoryCrudRepository.deleteFactoryById(factoryId);
			if (num > 0) {
				logger.info("[deleteFactoryById()->success: 厂家删除成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("厂家删除成功", "厂家删除成功");
			} else {
				return RpcResponseBuilder.buildErrorRpcResp("厂家删除失败");
			}
		} catch (Exception e) {
			logger.error("deleteFactoryById()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.FactoryCrudService#addFactory(com.run.locman.api.entity.Factory,
	 *      java.util.List, java.util.List, java.util.List, java.lang.String)
	 */
	@Override
	public RpcResponse<String> addFactory(Factory factory, List<Device> devices, List<DeviceType> deviceTypes,
			List<AppTagDto> appTagDtoList, String accessSecret) {
		RpcResponse<String> checkParmer = checkParmer(factory, devices, deviceTypes, appTagDtoList);
		if (null != checkParmer) {
			return checkParmer;
		}
		factory.setManageState("enabled");

		try {

			if (!devices.isEmpty()) {
				Integer saveDeviceBatch = deviceInfoCudRepository.saveDeviceBatch(devices);
				if (saveDeviceBatch < 0) {
					logger.error("[addFactory()->fail: 设备基础数据保存失败,厂家保存失败!!!" + JSON.toJSONString(devices) + "]");
					return RpcResponseBuilder.buildErrorRpcResp("设备基础数据保存失败,厂家保存失败!!!");
				}
				logger.info("[addFactory()->success: 设备基础数据保存成功]");
			}

			if (!deviceTypes.isEmpty()) {
				// 已有的设备类型不能重复添加 
				List<DeviceType> deviceTypeList = deviceTypeCudRepository.queryDeviceTypeByAS(accessSecret);
				removeRepetitiveDeviceType(deviceTypes, deviceTypeList);
				if (!deviceTypes.isEmpty()) {
					Integer addDeviceType = deviceTypeCudRepository.addDeviceType(deviceTypes);
					if (addDeviceType < 0) {
						logger.error("[addFactory()->fail: 设备类型保存失败,厂家保存失败!!!" + JSON.toJSONString(deviceTypes) + "]");
						return RpcResponseBuilder.buildErrorRpcResp("设备类型保存失败,厂家保存失败!!!");
					}
					logger.info("[addFactory()->success: 设备类型保存成功]");
				}
				
			}
			int num = factoryCrudRepository.insertModel(factory);
			if (num > 0) {
				logger.info("[addFactory()->success: 厂家保存成功]");
				Map<String, Object> addParams = Maps.newHashMap();
				addParams.put("factoryId", factory.getId());
				addParams.put("appTagDtoList", appTagDtoList);
				int addFactoryRsAppTag = factoryAppTagCrudRepository.addFactoryRsAppTag(addParams);
				if (addFactoryRsAppTag >= 0) {
					Integer deleteUnusedDeviceTypeByAS = deviceTypeCudRepository.deleteUnusedDeviceTypeByAS(accessSecret);
					logger.info(String.format("[addFactory()->success: 删除无设备的设备类型数量:%s]", deleteUnusedDeviceTypeByAS));
					logger.info("[addFactory()->success: 厂家与appTag关系保存成功]");
					return RpcResponseBuilder.buildSuccessRpcResp("厂家保存成功", factory.getId());

				} else {
					logger.error("[addFactory()->fail: 厂家与appTag关系保存失败,厂家保存失败" + JSON.toJSONString(addParams) + "]");
					return RpcResponseBuilder.buildErrorRpcResp("厂家与appTag关系保存失败,厂家保存失败");
				}
			} else {
				logger.error("[addFactory()->fail: 厂家保存错误" + JSON.toJSONString(factory) + "]");
				return RpcResponseBuilder.buildErrorRpcResp("厂家保存失败");
			}

		} catch (Exception e) {
			logger.error("addFactory()->exception", e);
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
	
	private RpcResponse<String> checkParmer(Factory factory, List<Device> devices, List<DeviceType> deviceTypes,
			List<AppTagDto> appTagDtoList) {
		if (StringUtils.isBlank(factory.getAccessSecret())) {
			logger.error("[addFactory()->invalid: 厂家密钥不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("厂家密钥不能为空");
		}
		if (StringUtils.isBlank(factory.getFactoryName())) {
			logger.error("[addFactory()->invalid: 厂家名称不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("厂家名称不能为空");
		}
		if (StringUtils.isBlank(factory.getContacts())) {
			logger.error("[addFactory()->invalid: 厂家联系人不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("厂家联系人不能为空");
		}
		if (StringUtils.isBlank(factory.getContactsPhone())) {
			logger.error("[addFactory()->invalid: 厂家联系方式不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("厂家联系方式不能为空");
		}
		if (StringUtils.isBlank(factory.getAddress())) {
			logger.error("[addFactory()->invalid: 厂家地址不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("厂家地址不能为空");
		}
		if (null == devices) {
			logger.error("[addFactory()->invalid: 设备集合不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("设备集合不能为null");
		}
		if (null == deviceTypes) {
			logger.error("[addFactory()->invalid: 设备类型集合不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("设备类型集合不能为null");
		}
		if (null == appTagDtoList) {
			logger.error("[addFactory()->invalid: appTag集合不能为null]");
			return RpcResponseBuilder.buildErrorRpcResp("appTag不能为null");
		}
		logger.info(String.format("[addFactory()->进入方法,参数:%s]", factory));
		return null;
	}



	/**
	 * @see com.run.locman.api.crud.service.FactoryCrudService#updateFactory(com.run.locman.api.entity.Factory, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.String)
	 */
	@Override
	public RpcResponse<String> updateFactory(Factory factory, List<Device> devices, List<DeviceType> deviceTypes,
			List<AppTagDto> newAppTagList, List<AppTagDto> oldAppTagList, String accessSecret) {
		logger.info(String.format(
				"[updateFactory()->参数:factory:%s ; devices:%s ; deviceTypes:%s ;"
						+ " newAppTagList:%s ; oldAppTagList:%s ; accessSecret:%s]",
						factory, devices, deviceTypes, newAppTagList, oldAppTagList, accessSecret));
		try {

			if (!devices.isEmpty()) {
				Integer saveDeviceNum = deviceInfoCudRepository.saveDeviceBatch(devices);
				if (saveDeviceNum < 0) {
					logger.error("[updateFactory()->fail: 设备基础数据修改失败,厂家修改失败!!!]");
					return RpcResponseBuilder.buildErrorRpcResp("设备基础数据修改失败,厂家修改失败!!!");
				}
				logger.info("[updateFactory()->success: 设备基础数据保存成功]");
			}
			// 已有的设备类型不能重复添加 
			List<DeviceType> deviceTypeList = deviceTypeCudRepository.queryDeviceTypeByAS(accessSecret);
			removeRepetitiveDeviceType(deviceTypes, deviceTypeList);
			
			if (!deviceTypes.isEmpty()) {
				Integer addDeviceTypeNum = deviceTypeCudRepository.addDeviceType(deviceTypes);
				if (addDeviceTypeNum < 0) {
					logger.error("[updateFactory()->fail: 设备类型保存失败,厂家修改失败!!!]");
					return RpcResponseBuilder.buildErrorRpcResp("设备类型保存失败,厂家修改失败!!!");
				}
				logger.info("[updateFactory()->success: 设备类型保存成功]");
			}
			int updateNum = factoryCrudRepository.updatePart(factory);
			if (updateNum < 0) {
				logger.error("[updateFactory()->fail: 厂家修改失败!!!]");
				return RpcResponseBuilder.buildErrorRpcResp("厂家修改失败!!!");
			}
			int deleteByFactoryId = factoryAppTagCrudRepository.deleteByFactoryId(factory.getId());
			if (deleteByFactoryId < 0) {
				logger.error("[updateFactory()->fail: 删除厂家关联的appTag失败!!!]");
				return RpcResponseBuilder.buildErrorRpcResp("厂家修改失败!!!");
			}
			logger.info("[updateFactory()->success: 删除原有绑定关系成功]");
			Map<String, Object> addParams = Maps.newHashMap();
			addParams.put("factoryId", factory.getId());
			addParams.put("appTagDtoList", newAppTagList);
			int addFactoryRsAppTag = factoryAppTagCrudRepository.addFactoryRsAppTag(addParams);
			if (addFactoryRsAppTag < 0) {
				logger.error("[updateFactory()->fail:添加厂家关联的appTag失败!!!]");
				return RpcResponseBuilder.buildErrorRpcResp("添加厂家关联的appTag失败!!!");
			}
			return deleteOldInfo(oldAppTagList, accessSecret);
		} catch (Exception e) {
			logger.error("updateFactory()->exception", e);
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
	
	private void removeRepetitiveDeviceType(List<DeviceType> deviceTypes, List<DeviceType> deviceTypeList) {
		for (DeviceType oldDeviceType : deviceTypeList) {
			Iterator<DeviceType> iterator = deviceTypes.iterator();
			while (iterator.hasNext()) {
				DeviceType newDeviceType = iterator.next();
				if (oldDeviceType.getId().equals(newDeviceType.getId())) {
					iterator.remove();
				}

			}
		}
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<String> deleteOldInfo(List<AppTagDto> oldAppTagList, String accessSecret) {
		if (!oldAppTagList.isEmpty()) {
			List<String> deleteAppTagList = Lists.newArrayList();
			for (AppTagDto appTagDto : oldAppTagList) {
				deleteAppTagList.add(appTagDto.getAppTag());
			}
			Integer deleteDeviceRsAppTags = deviceInfoCudRepository.deleteDeviceBatchByAppTags(deleteAppTagList);
			if (deleteDeviceRsAppTags < 0) {
				logger.error("[updateFactory()->fail:删除与厂家关联的appTag对应设备失败!!!]");
				return RpcResponseBuilder.buildErrorRpcResp("删除与厂家关联的appTag对应设备失败!!!");
			} else {
				Integer deleteUnusedDeviceTypeByAS = deviceTypeCudRepository.deleteUnusedDeviceTypeByAS(accessSecret);
				logger.info(String.format("[updateFactory()->success: 删除无设备的设备类型数量:%s]", deleteUnusedDeviceTypeByAS));
				logger.info("[updateFactory()->success: 删除与厂家关联的appTag对应设备成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("厂家信息修改成功", "厂家信息修改成功");
			}
		} else {
			Integer deleteUnusedDeviceTypeByAS = deviceTypeCudRepository.deleteUnusedDeviceTypeByAS(accessSecret);
			logger.info(String.format("[updateFactory()->success: 删除无设备的设备类型数量:%s]", deleteUnusedDeviceTypeByAS));
			logger.info("[updateFactory()->success: 厂家信息修改成功]");
			return RpcResponseBuilder.buildSuccessRpcResp("厂家信息修改成功", "厂家信息修改成功");
		}
	}
	
	
	
	@Override
	public RpcResponse<String> updateState(Factory factory) {
		try {
			if (StringUtils.isBlank(factory.getId())) {
				logger.error("[updateState()->invalid: 厂家id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("厂家id不能为空");
			}
			if (StringUtils.isBlank(factory.getManageState())) {
				logger.error("[updateState()->invalid: 目标状态不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("目标状态不能为空");
			}
			logger.info(String.format("[updateState()->进入方法,参数:%s]", factory));
			int num = factoryCrudRepository.updatePart(factory);
			if (num > 0) {
				logger.info("[updateState()->success: 厂家启/停用成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("厂家启/停用成功", null);
			} else {
				return RpcResponseBuilder.buildErrorRpcResp("厂家启/停用失败");
			}
		} catch (Exception e) {
			logger.error("updateState()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
	


}
