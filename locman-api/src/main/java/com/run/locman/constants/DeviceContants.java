/*
 * File name: DeviceContants.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年3月6日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.constants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年3月6日
 */

public class DeviceContants {
	/**
	 * 信号强度 应与采集端同步
	 */
	public static final String	FA_WIFI			= "sig";
	/**
	 * 井盖状态 应与采集端同步
	 */
	public static final String	FA_UNLOCK_ALT	= "cs";
	/**
	 * 蓝牙
	 */
	public static final String		FA_DC		= "dc";
	/**
	 * Y轴震动时相对初始值的平均值
	 */
	public static final String      FA_YAVTV	="yavtv";
	/**
	 * X轴震动时相对初始值的平均值
	 */
	public static final String  	FA_XAVTV    ="xavtv";
	/**
	 * Z轴震动时相对初始值的平均值
	 */
	public static final String  	FA_ZAVTV    ="zavtv";
	/**
	 * 上报类型
	 */
	public static final String  	FA_RT    ="rt";
	/**
	 * 内井盖状态 应与采集端同步
	 */
	public static final String	FA_UNLOCK_ALT_O	= "ics";
	/**
	 * 震动计数
	 */
	public static final String  	FA_XGCM	   ="xgcm";
	/**
	 * 沉降等级计数
	 */
	public static final String  	FA_YGCM    ="ygcm";
	/**
	 * X轴当前角度阀值
	 */
	public static final String  	FA_XGTV    ="xgtv";
	/**
	 * Y轴当前角度阀值
	 */
	public static final String  	FA_YGTV    ="ygtv";
	/**
	 * X轴震动时相对初始值的平均值总平均值
	 */
	public static final String  	FA_XAVTV_AVG    ="xavtvAvg";
	/**
	 * Y轴震动时相对初始值的平均值总平均值
	 */
	public static final String  	FA_YAVTV_AVG    ="yavtvAvg";
	/**
	 * z轴震动时相对初始值的平均值总平均值
	 */
	public static final String  	FA_ZAVTV_AVG    ="zavtvAvg";
	/**
	 * 震动计数总平均值
	 */
	public static final String  	FA_XGCM_AVG    ="xgcmAvg";
	/**
	 * 沉降等级计数总平均值
	 */
	public static final String  	FA_YGCM_AVG    ="ygcmAvg";
	/**
	 * 总平均r值,根据xyz平均值计算得到
	 */
	public static final String  	FA_RACTV_AVG    ="ractvAvg";
	/**
	 * 可用状态
	 */
	public static final String  	MANAGESTATE    ="manageState";
	

	/** 设备状态打开 */
	public static final String	OPEN			= "open";

	/** 设备ID */
	public static final String	DEVICEID		= "deviceId";
	public static final String	DEVICENAME		= "deviceName";

	/** 设备所属网关id */
	public static final String	GATEWAYID		= "gatewayId";
	/** 设备状态 **/
	public static final String	STATE			= "state";
	/** 设备状态关闭 */
	public static final String	CLOSE			= "close";

	public static final String	DEVICE_STATE	= "deviceState";
	
	public static final String  SUB_DEVICID     = "subDeviceId";
	
	public static final String  DEVICE_BV_MIN                = "device_bvMin";
	
	public static final String  DEVICE_BV_MAX                = "device_bvMax";
	
	public static final String  DEVICE_SIG_MIN                = "device_sigMin";
	
	public static final String  DEVICE_SIG_MAX                = "device_sigMax";
	
	public static final String  DEVICE_RSRP_MIN                = "device_rsrpMin";
	
	public static final String  DEVICE_RSRP_MAX                = "device_rsrpMax";
	
	
	public static final String  DEVICE_SINR_MIN                = "device_sinrMin";
	
	public static final String  DEVICE_SINR_MAX                = "device_sinrMax";
	
	
}
