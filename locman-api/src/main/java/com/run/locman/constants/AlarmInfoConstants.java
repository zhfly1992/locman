
package com.run.locman.constants;

/**
 * @Description:告警信息常量类
 * @author: lkc
 * @version: 1.0, 2017年11月2日
 */

public class AlarmInfoConstants {
	/** 当前页 */
	public static final String	PAGE_NO				= "pageNo";
	/** 页大小 */
	public static final String	PAGE_SIZE			= "pageSize";
	/** 接入方编码 **/
	public static final String	USC_ACCESS_SECRET	= "accessSecret";
	/** 查询关键字 **/
	public static final String	SEARCH_KEY			= "searchKey";
	/** 设施类型ID **/
	public static final String	FACILITIES_TYPE_ID	= "facilitiesTypeId";
	/** 告警等级 */
	public static final String	ALARMI_LEVEL		= "alarmLevel";
	public static final String	ALARMI_LEVEL_CH		= "告警等级";
	/** 设施编码 */
	public static final String	FACILITES_CODE		= "facilitiesCode";
	public static final String	FACILITES_CODE_CH	= "设施序列号";
	/** 设施id */
	public static final String	FACILITES_ID		= "facilitiesId";
	/** 位置key */
	public static final String	ADDRESS_KEY			= "addressKey";
	/** 设备id */
	public static final String	DEVICE_ID			= "deviceId";
	/** 组织id */
	public static final String	ORG_ID				= "organizationId";
	/** 组织名 */
	public static final String	ORG_NAME			= "organizationName";
	public static final String	ORG_NAME_CH			= "所属组织";
	/** 告警时间 */
	public static final String	ALARM_TIME			= "alarmTime";
	public static final String	ALARM_TIME_CH		= "告警时间";
	/** 上报时间 */
	public static final String	REPORT_TIME			= "reportTime";
	/** 设施编号或序列号 */
	public static final String	FACILITY_CODE_OR_ID	= "facilityCodeOrId";
	/** 人员id */
	public static final String	USER_ID				= "userId";
	/** 设施类型 */
	public static final String	FACILITIES_TYPE		= "facilityTypeAlias";
	public static final String	FACILITIES_TYPE_CH	= "设施类型";
	/**设施地址*/
	public static final String	FACILITIES_ADDRESS		= "address";
	public static final String	FACILITIES_ADDRESS_CH	= "设施地址";
	/** 告警流水号 */
	public static final String	SERIAL_NUM			= "serialNum";
	public static final String	SERIAL_NUM_CH		= "告警流水号";
	/** 告警规则 */
	public static final String	ALARM_DESC			= "alarmDesc";
	public static final String	ALARM_DESC_CH		= "触发规则";
	/** 告警信息导出 */
	public static final String	EXCEL_NAME			= "告警信息统计";
	/** 总条数 */
	public static final String	TOTAL_COUNT			= "totalCount";
	/**
	 * ------------------------------kafka 订阅相关---------------------------------
	 */
	/** 用户手机号 */
	public static final String	MOBILE				= "mobile";
	/** 设备类型ID */
	public static final String	DEVICE_TYPE_ID		= "deviceTypeId";
	/** 上报时间 */
	public static final String	TIMESTAMP			= "timestamp";
	/** 上报状态属性节点 */
	public static final String	STATE				= "state";
	/** 上报属性节点 */
	public static final String	REPORTED			= "reported";
	/** kafka订阅地址 */
	public static final String	THINGTOPIC			= "ThingTopic";

	/** ---------参数封装------------ */
	public static final String	DEVICEID			= "_deviceId";
	public static final String	REPORTTIME			= "_reportTime";
	public static final String	FACILITYID			= "_facilityId";
	public static final String	FACILITYTYPEID		= "_facilityTypeId";
	public static final String	ACCESSSECRET		= "_accessSecret";
	public static final String	FACILITYTYPENAME	= "_facilityTypeName";
	public static final String	FACILITIESCODE		= "_facilitiesCode";
	public static final String	MOBILE_				= "_mobile";
	public static final String	ADDRESS				= "_address";
	public static final String	ALARMDESC			= "_alarmDesc";
	public static final String	RULE				= "_rule";
	public static final String	ALARMLEVEL			= "_alarmLevel";
	public static final String	ISMATCHORDER		= "_isMatchOrder";
	public static final String	ALARMTIME			= "_alarmTime";
	public static final String	RULENAME			= "_ruleName";
	public static final String	ORGANIZATIONID		= "_organizationId";
	public static final String	ENGINE_CHECK		= "_engine_check";

}
