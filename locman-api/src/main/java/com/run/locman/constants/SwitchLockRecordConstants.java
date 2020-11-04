/*
 * File name: SwitchLockRecordConstants.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月3日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.constants;

/**
 * @Description: 开关锁记录常量类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月3日
 */

public class SwitchLockRecordConstants {

	/** 设备状态数据字段 */
	public static final String	DATA_STATE							= "state";
	public static final String	DATA_REPORTED						= "reported";

	/** 设备Id */
	public static final String	DEVICE_ID							= "deviceId";
	/** 设备开关状态 */
	public static final String	DEVICE_LOCKSTATE					= "lockState";
	/** 命令发起人Id */
	public static final String	COMMAND_CONTROLUSERID				= "controlUserId";
	/** 命令时间 */
	public static final String	COMMAND_CONTROLTIME					= "controlTime";
	/** 命令有效性 */
	public static final String	COMMAND_CONTROLSTATE				= "controlState";
	/** 命令有效 */
	public static final String	COMMAND_VALID						= "valid";
	/** 主键id */
	public static final String	ID									= "id";
	/** 接入方密钥 */
	public static final String	COMMAND_ACCESSSECRET				= "accessSecret";
	/** 关键字查询 */
	public static final String	COMMAND_KEYWORD						= "keyWord";
	/** 组织id */
	public static final String	COMMAND_ORG_ID						= "organizationId";
	public static final String	COMMAND_ORG_ID_MONGO				= "organizedId";
	/** 操作人id */
	public static final String	COMMAND_USER_ID						= "arrangeUserId";
	/** 用户ids */
	public static final String	FEIGN_USERIDS						= "userIds";
	/** 组织ids */
	public static final String	FEIGN_ORGIDS						= "orgIds";

	/** 特殊处理的设备类型 (智能条形锁) */
	//public static final String	DEVICE_LIGHTIN_SPECIAL				= "ca604b088b5440e88e7d24424380bf9c";

	/** 开关锁状态 */
	public static final String	SWITCH_CLOSE						= "close";
	public static final String	SWITCH_OPEN							= "open";
	public static final String	SWITCH_ILLEGALLY					= "illegally open";
	public static final String	SWITCH_EMERGENCY					= "emergency open";
	public static final String	SWITCH_CLOSE_PAR					= "关闭";
	public static final String	SWITCH_OPEN_PAR						= "开启";

	/** 设备属性模板配置ls->锁 */
	public static final String	SWITCH_LS_0							= "ls";
	public static final String	SWITCH_LS_1							= "ls1";
	public static final String	SWITCH_LS_2							= "ls2";
	public static final String	SWITCH_CONTROLITEM					= "controlItem";

	/** 返回信息 */
	public static final String	SWITCH_NULL							= "未查找到远程命令!";
	public static final String	SWITCH_LS_NULL						= "未查找到ls,ls1或者ls2数据";
	public static final String	SWITCH_NO_SACE						= "开关状态无效，不进行保存！";
	public static final String	SWITCH_INVALID						= "未查找到开关锁记录！";
	public static final String	SWITCH_SUC							= "保存开关锁记录成功！";
	public static final String	SWITCH_NO							= "保存开关锁记录失败！";

	/** 导出key */
	public static final String	SWITCH_EXPROT_NAME					= "开关锁记录表";

	public static final String	SWITCH_EXPROT_FACILITIESCODE		= "facilitiesCode";
	public static final String	SWITCH_EXPROT_FACILITIESCODE_PAR	= "设施序列号";

	public static final String	SWITCH_EXPROT_FACILITYTYPEALIAS		= "facilityTypeAlias";
	public static final String	SWITCH_EXPROT_FACILITYTYPEALIAS_PAR	= "设施类型";

	public static final String	SWITCH_EXPROT_AREANAME				= "areaName";
	public static final String	SWITCH_EXPROT_AREANAME_PAR			= "所属区域";

	public static final String	SWITCH_EXPROT_ORGID					= "organizationId";
	public static final String	SWITCH_EXPROT_ORGID_PAR				= "所属组织";

	public static final String	SWITCH_EXPROT_LOCKSTATE				= "lockState";
	public static final String	SWITCH_EXPROT_LOCKSTATE_PAR			= "操作";

	public static final String	SWITCH_EXPROT_REPORTTIME			= "reportTime";
	public static final String	SWITCH_EXPROT_REPORTTIME_PAR		= "操作时间";

	public static final String	SWITCH_EXPROT_ARRANGEUSERID			= "arrangeUserId";
	public static final String	SWITCH_EXPROT_ARRANGEUSERID_PAR		= "操作人员";

	public static final String	REST_CODE							= "0000";

}
