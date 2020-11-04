/*
 * File name: DeviceReportedCrudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年12月13日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.Inspection;

/**
 * @Description: 设备属性上报中间表操作接口欧
 * @author: 王胜
 * @version: 1.0, 2018年12月13日
 */

public interface DeviceReportedCrudRepository {
	/**
	 * 
	 * @Description:设备上报后，保存到设备属性上报中间表
	 * @param paramMap{"id":"","deviceId":"","device_bv":"电池电压","device_ls":"锁状态"
	 *            ,"device_sig":"信号强度","device_rsrp":"信号接收功率","device_sinr":"信噪比"}
	 * @return
	 */
	public int saveDeviceRealReporte(Map<String, Object> paramMap);



	/**
	 * 
	 * @Description:设备上报后，更新设备属性上报中间表
	 * @param paramMap{"deviceId":"","device_bv":"电池电压","device_ls":"锁状态"
	 *            ,"device_sig":"信号强度","device_rsrp":"信号接收功率","device_sinr":"信噪比"}
	 * @return
	 */
	public int updateDeviceRealReporte(Map<String, Object> paramMap);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public JSONObject getXYZAvg(String locmanDeviceId);
	/**
	 * 
	* @Description:根据设备Id查询设备故障状态
	* @param locmanDeviceId
	* @return
	 */
	public Map<String,Object> getDeviceFaultOrderState(String locmanDeviceId);
	/**
	 * 
	* @Description:插入检测表
	* @param inspection
	* @return
	 */
	public int insertInspection(Inspection inspection);
	/**
	 * 
	* @Description:查询时间段数量
	* @param deviceId
	* @return
	 */
	public int countByDeviceId(Map<String ,Object> map);
	
	
	/**
	 * 
	* @Description:获取转换表
	* @return
	 */
	public List<Map<String, Object>> getTranTable();
	
	/**
	 * 
	* @Description:更新commandReceive字段
	* @param deviceId
	* @param commandReceive
	* @return
	 */
	public int updateCommandReceive(@Param("deviceId")String deviceId,@Param("commandReceive")String commandReceive);
}
