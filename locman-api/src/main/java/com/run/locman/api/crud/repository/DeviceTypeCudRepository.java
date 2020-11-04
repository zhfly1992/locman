/*
 * File name: DeviceTypeCudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年10月23日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.entity.DeviceType;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年10月23日
 */

public interface DeviceTypeCudRepository {

	public Integer addDeviceType(List<DeviceType> deviceTypeList);



	public Integer deleteDeviceTypeById(List<DeviceType> deviceTypeList);



	public List<DeviceType> queryDeviceTypeByAS(String accessSecret);



	public Integer editDeviceType(DeviceType deviceType);



	/**
	 * 
	 * @Description: 清理接入方下没有设备的设备类型
	 * @param accessSecret
	 *            接入方密钥
	 * @return 删除的设备类型数量
	 */
	public Integer deleteUnusedDeviceTypeByAS(String accessSecret);



	/**
	 * 
	 * @Description:清理接入方下没有设备的设备类型所关联的告警规则
	 * @param accessSecret
	 * @return
	 */
	public Integer deleteCascadeAlarmRuleById(@Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @Description:查询该接入方下，设备id不存在的告警规则 并且删除 （关系表）
	 * @param accessSecret
	 * @return
	 */
	public Integer deleteAlarmRuleIdByAccess(@Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @Description:删除不存在设备的自定义规则
	 * @param accessSecret
	 * @return
	 */
	public Integer deleteAlarmRuleByNotDeviceId(@Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @Description:查询自定义告警规则对应的设备数量
	 * @param accessSecret
	 * @return
	 */
	public List<Map<String, String>> selectAlarmRuleDeviceCount(@Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @Description:批量修改告警规则deviceCount
	 * @param alarmRuleDeviceCounts
	 * @return
	 */
	public Integer updateAlarmRuleDeivceCount(@Param("alarmRules") List<Map<String, String>> alarmRuleDeviceCounts);

}
