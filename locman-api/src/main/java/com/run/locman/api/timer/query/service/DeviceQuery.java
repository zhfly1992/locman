/*
* File name: DeviceQuery.java								
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
* 1.0			guofeilong		2018年7月5日
* ...			...			...
*
***************************************************/

package com.run.locman.api.timer.query.service;

import java.util.List;
import java.util.Map;

import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年7月5日
*/

public interface DeviceQuery {

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	void deviceDetetion();
	
	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	RpcResponse<List<String>> checkOrderExist(String deviceId);
	
	
	void checkFaultOrder();
	
	RpcResponse<List<String>> checkFaultOrderDevice();
	
	
	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  *//*
	
	void getPersonIdAndOrganizeIdByAc1();*/
	/**
	 * 
	* @Description:mongodb数据封装之后插入mysql表
	 */
	void insertCountTimingAndTrigger();
	/**
	 * 
	* @Description:mongodb数据查询
	* @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getCountTimingAndTrigger();

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	void insertCountTimingAndTrigger(long day);
	/**
	 * 
	* @Description:自动维护设备
	 */
	void automaticMaintenanceByTrigger();

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	void queryAndAlarm();
	/**
	 * 
	* @Description:自动解除维护设施
	 */
	void automaticLiftingByTiming();
	/**
	 * 
	* @Description:重保时间结束，更改工单状态
	 */
	
	void querySecurityFacilitiesOrders();
}
