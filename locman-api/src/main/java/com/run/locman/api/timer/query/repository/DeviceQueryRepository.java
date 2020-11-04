/*
 * File name: DeviceQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 郭飞龙 2018年7月5日16:12:13 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.timer.query.repository;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.dto.DeviceAndTimeDto;
import com.run.locman.api.entity.Device;
import com.run.locman.api.query.repository.BaseQueryRepository;

/**
 * @Description:
 * @author: 郭飞龙
 * @version: 1.0, 2018年7月5日16:12:13
 */

public interface DeviceQueryRepository extends BaseQueryRepository<Device, String> {


	/**
	  * 
	  * @Description:查询设备列表所有接入方密钥
	  * @param 
	  * @return
	  */
	
	List<String> getAllAccessSecret();
	
	/**
	  * 
	  * @Description:查询发起故障工单所需部分数据
	  * @param 
	  * @return
	  */
	
	List<Map<String, Object>> getInfoForFaultOrderById(List<String> deviceIds);

	/**
	  * 
	  * @Description:检测是否存在超时未上报故障工单
	  * @param 
	  * @return
	  */
	
	List<String> checkOrderExist(String deviceId);
	
	/**
	  * 
	  * @Description:查询自动生成的超时未上报故障工单对应的设备和设定的超时时间
	  * @param 
	  * @return
	  */
	List<DeviceAndTimeDto> checkFaultOrderDevice();

	/**
	* @Description:
	* @return
	*/
	
	List<Map<String, Object>> getCountDeviceTimingTrigger();
	
	@SuppressWarnings("rawtypes")
	int insertToTrigger(List<Map> endList);
	
	List<Map<String,Object>> getDeviceInfo(Map<String,Object> map);
	
	List<Map<String,Object>> getTimingDeviceInfo(Map<String ,Object> map);
	
	List<Map<String,Object>> getAutomaticTimingDeviceInfo(Map<String ,Object> map);
	
	List<String> getNormalDeviceIds();
	int addDeviceRportedEffective(List<JSONObject> paramList);

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<JSONObject> queryXYZAvg();

	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	int addRportedAvg(List<JSONObject> xyzAvgs);
	
}
