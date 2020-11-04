package com.run.locman.constants;

import java.io.Serializable;

/**
 * 
 * @Description:日志常量类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public class CommonConstants implements Serializable {
	private static final long	serialVersionUID				= 1L;

	public static final String	ID								= "id";
	/** 页面当前页 */
	public static final String	PAGE_NUM						= "pageNum";
	/** 页面当前页条数 */
	public static final String	PAGE_SIZE						= "pageSize";

	public static final String	SEARCH_KEY						= "searchKey";
	public static final String	NAME							= "name";
	public static final String	USERID							= "userId";

	public static final String	ENABLED							= "enabled";
	public static final String	DISABLED						= "disabled";
	public static final String	ROLE_NAME						= "roleName";
	public static final String	SOURCE_NAME						= "sourceName";
	public static final String	MANAGESTATE						= "manageState";
	public static final String	ORGANIZEID						= "organizeId";
	public static final String	USERNAME						= "userName";
	public static final String	APPTAG							= "appTag";
	public static final String	FACTORYID						= "factoryId";
	public static final String	PROCESSSTATE					= "processState";
	public static final String	POWERNAME						= "powerName";
	public static final String	USERINFO						= "userInfo";
	public static final String	STATE							= "state";
	public static final String	FACILITIESTYPEID				= "facilitiesTypeId";
	public static final String	ISNOTMANDATORY					= "isNotMandatory";
	public static final String	INITIALVALUE					= "initialValue";
	public static final String	DATATYPE						= "dataType";
	public static final String	REMARKS							= "remarks";
	public static final String	SIGN							= "sign";
	public static final String	DEVICENOBINDCOUNT				= "deviceNoBindCount";
	public static final String	FACILITYTYPENAME				= "facilityTypeName";
	public static final String	FACILITYTYPEALIAS				= "facilityTypeAlias";
	public static final String	FACILITYTYPEBASEID				= "facilityTypeBaseId";
	public static final String	NUMBER_TWO_HUNDRED				= "200";
	public static final String	NUMBER_FOUR_HUNDRED_AND_NINE	= "409";
	public static final String	NUMBER_ZERO						= "0";
	public static final String	NUMBER_TWO						= "2";
	public static final String	NUMBER_FOUR_ZERO				= "0000";
	public static final String	DEVICEID						= "deviceId";
	public static final String	OPERATEUSERNAME					= "operateUserName";
	public static final String	OPERATEUSERPHONE				= "operateUserPhone";
	public static final String	CONTROLITEM						= "controlItem";
	public static final String	CONTROLVALUE					= "controlValue";
	public static final String	REASON							= "reason";
	public static final String	PROCESSSTARTTIME				= "processStartTime";
	public static final String	ON								= "on";
	public static final String	POWER							= "power";
	public static final String	ACCESSSECRET					= "accessSecret";
	public static final String	OPENTYPE						= "openType";
	public static final String	DEVICETYPEID					= "deviceTypeId";
	public static final String	RULENAME						= "ruleName";
	public static final String	RULETYPE						= "ruleType";
	public static final String	STARTTIME						= "startTime";
	public static final String	ENDTIME							= "endTime";
	public static final String	GENERALPROCESS					= "generalProcess";
	public static final String	ALARMFAILUREPROCESS				= "alarmFailureProcess";
	public static final String	MANUALFAILUREPROCESS			= "manualFailureProcess";
	public static final String	ALARMPROCESS					= "alarmProcess";
	public static final String	FACILITYTYPEICO					= "facilityTypeIco";
	/** 主要信息 */
	public static final String	MAININFO						= "mainInfo";

	/** 日志记录key */
	public static final String	LOGKEY							= "locman";

	/** 下发命令的key */
	public static final String	KEY								= "key";
	/** 下发命令的keyValue */
	public static final String	KEY_VALUE						= "keyValue";
	/** 接入方密钥 */
	public static final String	ACCESS_SECRET					= "accessSecret";
	/** 设备状态:关闭 */
	public static final String	CLOSE							= "close";
	/** 设备未配置超时未关闭时间 */
	public static final String	ZERO							= "0";
	/** 设备在线离线状态 */
	public static final String	THING_STATUS					= "thingStatus";

	/** 信息概述页面未上报天数 **/
	public static final int		NOT_REPORT_SET_DAY				= 10;
	/** 信息概述页面正常上报天数 **/
	public static final int		NORMAL_REPORT_SET_DAY			= 3;
	/** 超时时间三分钟 */
	public static final int		OVER_TIME						= 180000;
	
	public static final String  NUM                             = "num";
	
	public static final String  DATE                            = "date";
	/** 组织id */
	public static final String  ORGANIZATION_ID                 =  "organizationId";
	/** 设施序列号 */
	public static final String  FACILITIES_CODE                 =  "facilitiesCode";
	/** 蓝牙编号（目前设备名称字段存的是蓝牙编号字段，若存表变化，应做相应改变） */
	public static final String  DEVICENAME               		=  "deviceName";

}
