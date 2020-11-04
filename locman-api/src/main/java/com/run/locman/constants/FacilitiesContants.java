package com.run.locman.constants;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年08月08日
 */
public class FacilitiesContants {
	// 参数常量
	/** 设施管理状态 */
	public static final String	FACILITIES_MANAGESTATE		= "manageState";
	/** 设施类型ID */
	public static final String	FACILITIES_TYPE_ID			= "facilitiesTypeId";
	/** 设施序列号 */
	public static final String	FACILITIES_CODE				= "facilitiesCode";
	/** 设施序列号 */
	public static final String	FACILITIES_SEARCHKEY		= "searchKey";
	/** 组织id */
	public static final String	ORGANIZATION_ID				= "organizationId";
	/** 组织名 */
	public static final String	ORGANIZATION_NAME			= "organizationName";
	/** 设施类型ID */
	public static final String	ADDRESS						= "address";
	/** 设施类型ID */
	public static final String	COMPLETE_ADDRESS			= "completeAddress";
	/** 告警等级 */
	public static final String	ALARM_WORST_LEVEL			= "alarmWorstLevel";
	/** 区域 */
	public static final String	AREA_ID						= "areaId";
	/** 文件 */
	public static final String	FILE_FILE					= "file";
	/** 人员id */
	public static final String	USER_ID						= "userId";
	/** 文件网关 */
	public static final String	FILE_GATEWAY				= "gatewayFile";
	/** 基础设施类型 */
	public static final String	FACILITY_TYPE_BASE_ID		= "facilityTypeBaseId";
	/** 设施类型创建者id */
	public static final String	CREATION_USER_ID			= "creationUserId";
	/** 设施id */
	public static final String	FACILITY_ID					= "facilityId";
	/** 设施屏蔽状态*/
	public static final String  DEFENSE_STATE               = "defenseState";
	// 应用常量
	/** 启用 */
	public static final String	ENABLE						= "enable";
	/** 停用 */
	public static final String	DISABLE						= "disable";
	/** 删除 */
	public static final String	DELETE						= "delete";
	/** 设施id */
	public static final String	FACILITES_ID				= "id";
	/** 远程mong请求使用的id */
	public static final String	ID							= "_id";
	/** 设施code */
	public static final String	FACILITES_CODE				= "code";
	/** 分页大小 */
	public static final String	PAGE_SIZE					= "pageSize";
	/** 分页的当前页 */
	public static final String	PAGE_NO						= "pageNo";
	/** 模糊查询字段 */
	public static final String	SEARCH_KEY					= "serchKey";
	/** 组织信息 */
	public static final String	ORG_INFO					= "orgInfo";
	/** 接入方编码 **/
	public static final String	USC_ACCESS_SECRET			= "accessSecret";
	/** 设施绑定状态 */
	public static final String	FAC_BINGSTATUS				= "bingStatus";
	/** 设施额外信息 */
	public static final String	EXTEND						= "extend";

	public static final String	FACILITY_TYPE_ID			= "facilityTypeId";

	/** 常量参数 */
	public static final String	FAIL_LIST					= "failList";
	public static final String	FACILITIES_LIST				= "facilitiesList";

	public static final String	CHECK_XLX					= "^.+\\.(?i)(xlsx)$";
	public static final String	CHECK_LONGITUDE				= "^[\\-\\+]?(0?\\d{1,2}\\.\\d{1,5}|1[0-7]?\\d{1}\\.\\d{1,20}|180\\.0{1,10})$";
	public static final String	CHECK_LATITUDE				= "^[\\-\\+]?([0-8]?\\d{1}\\.\\d{1,20}|90\\.0{1,10})$";
	public static final String	CHECK_CODE					= "^[0-9a-zA-Z]{6,16}";

	/**
	 * --------------------------------------设施导出key----------------------------------------
	 */
	public static final String	FAC_FACILITIESCODE			= "facilitiesCode";
	public static final String	FAC_FACILITIESCODE_PAR		= "设施序列号";

	public static final String	FAC_COMPLETEADDRESS			= "completeAddress";
	public static final String	FAC_COMPLETEADDRESS_PAR		= "所属区域";

	public static final String	FAC_FACILITYTYPENAME		= "facilityTypeName";
	public static final String	FAC_FACILITYTYPENAME_PAR	= "基础设施类型";

	public static final String	FAC_FACILITYTYPEALIAS		= "facilityTypeAlias";
	public static final String	FAC_FACILITYTYPEALIAS_PAR	= "设施类型名称";

	public static final String	FAC_ORGANIZATIONNAME		= "organizationName";
	public static final String	FAC_ORGANIZATIONNAME_PAR	= "所属组织";

	public static final String	FAC_MANAGESTATE				= "manageState";
	public static final String	FAC_MANAGESTATE_PAR			= "管理状态";

	public static final String	FAC_LONGITUDE				= "longitude";
	public static final String	FAC_LONGITUDE_PAR			= "经度";

	public static final String	FAC_LATITUDE				= "latitude";
	public static final String	FAC_LATITUDE_PAR			= "纬度";

	public static final String	FAC_ADDRESS					= "address";
	public static final String	FAC_ADDRESS_PAR				= "地址";

	public static final String	FAC_EXPROT_NAME				= "设施列表";

	public static final String	FACILITESTYPE				= "facilitiesType";
	public static final String	FACILITESTYPE_PAR			= "设施类型";

	public static final String	DEVICECODE					= "deviceCode";
	public static final String	DEVICECODE_PAR				= "预装载设备编号";

	public static final String	GATEWAY_EXPROT_NAME			= "网关设备绑定列表";

	public static final String	GATEWAY_DEVICE_ID			= "deviceId";
	public static final String	GATEWAY_DEVICE_ID_PAR		= "设备Id";

	public static final String	GATEWAY_DEVICE_CODE			= "deviceCode";
	public static final String	GATEWAY_DEVICE_CODE_PAR		= "设备编号";

	public static final String	BASICS_FACILITIES_TYPE		= "basicsFacType";
	public static final String	BASICS_FACILITIES_TYPE_PAR	= "基础设施类型名称";

	public static final String	FAC_BINGOUNTD				= "bingStatus";
	public static final String	FAC_BINGOUNTD_PAR			= "绑定状态";

	public static final String	ERROR_FACILITIES_EXCEL		= "设施错误信息表";

	public static final long	FIFTEEN_M					= 15728640;

	/* 成华区项目特殊用 */

	/** 污水井英文,用于传参 */
	public static final String	DRAIN_WELL					= "drainWell";

	/** 污水井中文，用于字符串判断 */
	public static final String	CH_DRAIN_WELL				= "污水";
	/** 雨水井英文,用于传参 */
	public static final String	RAIN_WELL					= "rainWell";

	/** 雨水井中文,用于字符串判断 */
	public static final String	CH_RAIN_WELL				= "雨水";
	/** 雨水篦子英文,用于传参 */
	public static final String	RAIN_GRATE					= "rainGrate";

	/** 雨水篦子中文,用于字符串判断 */
	public static final String	CH_RAIN_GRATE				= "雨水篦子";
	/** 其他设施类型 ,用于传参*/
	public static final String	OTHER						= "other";
	
	/** 设施绑定状态*/
	public static final String  BOUND_STATE                 =  "boundState";
	
	/**设施未绑定*/
	public static final String  UNBOUND                     = "unBound";
	/**设施已绑定*/
	public static final String  BOUND                       = "bound";
	
	/** 一般告警,用于分类，向前端传参*/
	public static final String  NORMAL_ALARM               =  "normalAlarm";
	/** 紧急告警,用于分类，向前端传参*/
	public static final String  URGENT_ALARM               = "urgentAlarm";

}
