package com.run.locman.api.crud.service;

import java.util.List;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.AppTagDto;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceType;
import com.run.locman.api.entity.Factory;

/**
 * 
 * @Description:厂家crud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface FactoryCrudService {
	
	

	/**
	 * 
	 * @Description: 删除厂家
	 * @param factoryId
	 *            厂家id
	 * @return
	 */
	RpcResponse<String> deleteFactoryById(String factoryId);



	/**
	 * 
	 * @Description: 新增厂家(对接新iot)
	 * @param factory
	 *            厂家信息
	 * @param devices
	 *            从新iot获取的设备信息
	 * @param deviceTypes
	 *            从新iot获取的设备类型信息
	 * @param appTagDtoList
	 *            厂家与appTag关系信息
	 * @return
	 */
	RpcResponse<String> addFactory(Factory factory, List<Device> devices, List<DeviceType> deviceTypes,
			List<AppTagDto> appTagDtoList, String accessSecret);



	/**
	 * 
	 * @Description: 修改厂家(对接新iot)
	 * @param factory
	 *            厂家信息
	 * @param devices
	 *            从新iot获取的需要添加的设备信息
	 * @param deviceTypes
	 *            从新iot获取的需要添加的设备类型信息
	 * @param newAppTagList
	 *            需要添加的厂家与appTag关系信息
	 * @param oldAppTagList
	 *            需要删除的厂家与appTag关系信息
	 * @param accessSecret
	 *            接入方密钥
	 * @return
	 */
	RpcResponse<String> updateFactory(Factory factory, List<Device> devices, List<DeviceType> deviceTypes,
			List<AppTagDto> newAppTagList, List<AppTagDto> oldAppTagList, String accessSecret);



	/**
	 * 
	 * 
	 * @Description: 启停用厂家
	 * @param factory
	 * @return
	 */
	RpcResponse<String> updateState(Factory factory);

}
