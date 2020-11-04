/*
 * File name: SimpleOrderDeviceCrudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月6日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.SimpleOrderDevice;

/**
 * @Description:
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public interface SimpleOrderDeviceCrudRepository extends BaseCrudRepository<SimpleOrderDevice, String> {

	/**
	 * 
	 * @Description:根据工单id和设备id删除绑定关系
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	int deleteByids(Map map) throws Exception;

	/**
	 * 
	 * @Description:根据工单id直接删除关系表
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int deleteByOrderid(String orderId) throws Exception;
}
