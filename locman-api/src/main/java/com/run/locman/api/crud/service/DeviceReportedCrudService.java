/*
 * File name: DeviceReportedCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年10月19日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.service;

import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 接收设备上报数据RPC接口
 * @author: 王胜
 * @version: 1.0, 2018年10月19日
 */

public interface DeviceReportedCrudService {
	/**
	 * sefon
	 * 
	 * @Description:接收设备上报数据
	 * @param deviceReportedMessage
	 * @return RpcResponse<String>
	 */
	public RpcResponse<String> messageReceive(String deviceReportedMessage);



	/**
	 * sefon
	 * 
	 * @Description:接收设备上报数据
	 * @param deviceReportedMessage
	 * @return RpcResponse<String>
	 */
	public RpcResponse<String> messageReceiveThreadPool(String deviceReportedMessage);



	/**
	 * sefon
	 * 
	 * @Description:设备上报后，保存到设备属性上报中间表
	 * @param paramMap{"id":"","deviceId":"","device_bv":"电池电压","device_ls":"锁状态","device_sig":"信号强度"
	 *            ,"device_rsrp":"信号接收功率","device_sinr":"信噪比"}
	 * @return
	 */
	public RpcResponse<String> saveDeviceRealReporte(Map<String, Object> paramMap);



	/**
	 * s
	 * 
	 * @Description:设备上报后，更新设备属性上报中间表
	 * @param paramMap{"deviceId":"","device_bv":"电池电压","device_ls":"锁状态","device_sig":"信号强度",
	 *            "device_rsrp":"信号接收功率","device_sinr":"信噪比"}
	 * @return
	 */
	public RpcResponse<Integer> updateDeviceRealReporte(Map<String, Object> paramMap);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	void startRetryCacheData();



	/**
	 * @Description:
	 * @param locmanDeviceId
	 * @param parseObject
	 */

	public void dealAlarm(String locmanDeviceId, JSONObject parseObject);




	
	/**
	 * 
	* @Description:获取上报数据中英文转换表
	* @return
	 */
	public Map<String, String> getTransTable();
	
	
	/**
	 * 
	* @Description:重保项目，命令下发后，设备返回的命令响应
	* @param deviceId
	* @return
	 */
	public RpcResponse<String> commandResponseForWingsIot(String deviceId);

}
