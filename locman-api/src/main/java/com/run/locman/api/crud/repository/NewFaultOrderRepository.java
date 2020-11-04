/*
 * File name: NewFaultOrderRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2019年12月7日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.NewFaultOrder;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2019年12月7日
 */

public interface NewFaultOrderRepository {

	/**
	 * 
	 * @Description:获取工单信息
	 * @param orderId
	 * @return
	 */
	public NewFaultOrder getNewFaultOrder(String orderId);



	/**
	 * 
	 * @Description:更新工单信息
	 * @param map
	 * @return
	 */
	public int updateNewFaultOrder(Map<String, Object> map);



	/**
	 * 
	 * @Description:更新节点信息
	 * @param map
	 * @return
	 */
	public int updateNewFaultOrderNode(Map<String, Object> map);



	/**
	 * 
	 * @Description:新增节点
	 * @param map
	 * @return
	 */
	public int adddateNewFaultOrderNode(Map<String, Object> map);

}
