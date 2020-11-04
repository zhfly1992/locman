package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.FacilityDevice;

/**
 * 
 * @Description: 设施与设备关系Repository
 * @author: guolei
 * @version: 1.0, 2017年9月14日
 */
public interface FacilityDeviceCrudRepository extends BaseCrudRepository<FacilityDevice, String> {

	/**
	 * 
	 * @Description:批量删除设施和设备的关系
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int deleteByBatch(Map<String, Object> params) throws Exception;



	/**
	 * 
	 * @Description:批量新增设施和设备关系
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int insertModelByBatch(Map<String, Object> params) throws Exception;



	/**
	 * 
	 * @Description:批量新增设施和设备关系
	 * @param params
	 * @return int
	 * @throws Exception
	 */
	public int insertFacilityRsDevice(Map<String, Object> params) throws Exception;

}