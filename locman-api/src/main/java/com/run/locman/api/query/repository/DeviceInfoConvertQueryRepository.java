/*
* File name: DeviceInfoConvertRepository.java								
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
* 1.0			Administrator		2018年3月7日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.run.locman.api.base.repository.BasePagingRepository;
import com.run.locman.api.entity.DeviceInfoConvert;
import com.run.locman.api.model.DeviceInfoConvertModel;

/**
 * @Description:设备数据repository
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月7日
 */
public interface DeviceInfoConvertQueryRepository extends BasePagingRepository<DeviceInfoConvert> {

	/**
	 * 
	 * @Description:转换数据
	 * @param DeviceValue
	 * @return
	 */
	String dataConvert(Map<String, Object> convertMap);

	/**
	 * 
	 * @Description:分页查询数据
	 */
	Page<DeviceInfoConvert> findPageList(DeviceInfoConvertModel convert);

	/**
	 * 
	 * @Description:通过id查询数据
	 * @param id
	 * 
	 */
	DeviceInfoConvert findConvertById(DeviceInfoConvertModel convert);

	/**
	 * 
	 * @Description:判断该接入方下，key是否存在（唯一性）
	 * @param convert
	 * 
	 */
	String existConvertInfo(DeviceInfoConvertModel convert);
	
	/**
	 * 
	 * @Description:检查是否有特殊值转换（用于同步）
	 * @param accessSecret
	 * 
	 */
	List<DeviceInfoConvert> getDevieInfoConvert(String accessSecret);

}
