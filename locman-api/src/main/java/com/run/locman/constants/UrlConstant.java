
package com.run.locman.constants;

/**
 * 
 * @Description:url常量类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public class UrlConstant {
	/** 设施管理根目录  */
	public static final String	FACILITES								= "/facilities";
	/** 通过设施id查询 */
	public static final String	FACILITES_BY_FAC_ID						= "/facilitiesQueryByFacId";
	/** 通过设施code查询 */
	public static final String	FACILITES_BY_FAC_CODE					= "/facilitiesQueryFacCode";
	/** 通过接入方密钥和设施code查询 */
	public static final String	FACILITY_BY_CODE_ACC					= "/{accessSecret}/{facilitiesCode}";
	/** 通过设施分页查询 */
	public static final String	FACILITES_BY_PAGE						= "/facilitiesByPage";
	/** 地图查询展示 */
	public static final String	FACILITIES_GIS_MAP						= "/facilitiesmap";
	/** 设施根路径 */
	public static final String	FACILITY								= "/facility";
	/** 设施根路径 */
	public static final String	FIND_BY_FACDATATYPE_ID					= "/facilitiesDataTypeQueryById";
	/** 分页查询所有设施数据类型（不带分页） */
	public static final String	GETALLFACILITIESDATATYPE				= "/getAllFacilitiesDataType";
	/** 校验设施扩展类型标识名,或显示名是否重复 */
	public static final String	VALID_FACILITIES_DATA_TYPE_NAME			= "/validFacilitiesDataTypeName";

	public static final String	GET_FACILITIES_INFO						= "/getFacilitiesInfo";
	/** 查询所有设施数据类型（带分页） */
	public static final String	FACILITIESDATATYPE_LIST					= "/getFacilitiesDataTypeList";
	/** 设施类型基础路径 */
	public static final String	FACILITIES_TYPE_BASE_PATH				= "/facilitiesType";
	/** 查询所有设施类型集合 */
	public static final String	FACILITIES_TYPE_LIST					= "/facilitiesTypeList";
	/** 查询接入方设施类型及对应数量 */
	public static final String	FACILITIES_TYPE_ALL_BY_ACCESS			= "/all/{accessSecret}";
	/** 检查设施编号是否重复 **/
	public static final String	FACILITIES_CHECK_FACILITIES_CODE		= "checkFacilitiesCode";
	/** 检查设施类型名称是否重复 **/
	public static final String	CHECK_FACILITIES_NAME					= "/checkFacilitiesName";
	/** 添加设施 **/
	public static final String	FACILITIES_ADD							= "addFacilities";
	/** 修改设施 **/
	public static final String	FACILITIES_UPDATE						= "updateFacilities";
	/** 导出设施信息 */
	public static final String	EXPORT_FACILITES_INFO					= "/exportFacilitesInfo";
	/** 同步设施信息 */
	public static final String	SYNCH_FACILITES_INFO					= "/synch";
	/** 导入设施信息 */
	public static final String	IMPORT_FACILITES_INFO					= "/importFacilitesInfo";
	/** 导出设施信息 */
	public static final String	EXPORT_FACILITES_TEMPLATE				= "/exportFacilitesTemplate";
	/** 导出设施信息 */
	public static final String	EXPORT_GATEWAY_TEMPLATE					= "/exportGatewayTemplate";
	/** 查询告警设施数 */
	public static final String	COUNT_ALARM_FACILITIES					= "/countAlarmFacilities";
	/** 根据组织ID和类型统计设施数量（用于成华区） */
	public static final String	COUNT_FACILITES_BY_TYPE_AND_ORGAN		= "/countFacitiesByTypeAndOrgan";
	/** 统计各街道办各设施类型数量（用于成华区） */
	public static final String	COUNT_FACNUM_BY_STREET					= "/facNumByTypeAndStreet/{accessSecret}";
	/** 查询成华区所有设施经纬度（用于成华区） */
	public static final String	FIND_FACILITY_LNG_LAT					= "/facilityLngLat/{accessSecret}";
	/** 查询成华区所有设施经纬度（用于成华区,无key） */
	public static final String	FACILITY_LNG_LAT_LIST					= "/facilityLngLatList/{accessSecret}";
	
	/** 查询接入方设施类型设备类型对应的数量(成华区雨水篦单独为设施类型) */
	public static final String	FIND_ALL_FACTYPE_AND_DEVICETYPE_NUM		= "/facilityTypeDeviceType/num/{accessSecret}";
	/** 修改设施的屏蔽状态*/
	public static  final String UPDATE_FACILITIES_DENFENSE_STATE        = "/updateFacilitiesDenfenseState";
	/** 审核待整治设施*/
	public static  final String EXAMINE_RENOVATION_FACILITY        		= "/examineRenovationFacility";
	/** 待整治设施转故障工单*/
	public static  final String RENOVATION_FACILITY_2_FAULTORDER       	= "/RenovationFacility/faultOrder";
	
	/** 分页查询待整治设施 */
	public static final String	FIND_FACILITY_RENOVATED_BY_PAGE			= "/renovatedFacilityByPage";
	

	/** ---------------------------设备类型属性配置----------------------- */
	/** 设备类型属性配置跟路径 */
	public static final String	DEVICETYPE_PROPRT_CONFIG				= "deviceTypePropertyConfig";
	/** 模糊查询设备类型属性配置列表 */
	public static final String	QUERY_DEVICETYPE_PROPRT_CONFIG_LIST		= "queryDeviceTypePropertyConfigList";
	/** 模糊查询设备属性模板 */
	public static final String	QUERY_DEVICE_PROPERTY_TEMPLATE_LIST		= "queryDevicePropertyTemplateList";
	/** 解绑设备属性模板与设备类型关系 */
	public static final String	UNBIND_DEVICETYPE_PROPRTY_AND_TEMP		= "unbindDeviceTypePropertyAndTemplate";
	/** 绑定设备属性模板与设备类型关系 */
	public static final String	BIND_DEVICETYPE_PROPRTY_AND_TEMP		= "bindDeviceTypePropertyAndTemplate";
	/** 编辑设备类型 */
	public static final String	EDIT_DEVICETYPE							= "editDeviceType";

	/** ---------------------------设备类型----------------------- */
	/** 设备类型根路径 */
	public static final String	DEVICETYPE								= "/deviceType";

	/** 查询当前接入方设备类型及对应设备数量 */
	public static final String	DEVICETYPE_ALL_BY_ACCESS				= "/all/{accessSecret}";

	/** ---------------------------人员类型----------------------- */
	/** 人员类型管理根目录 */
	public static final String	STAFFTYPE								= "/staffType";
	/** 根据接入方秘钥查询所有人员类型 */
	public static final String	FIND_STAFFTYPE_BY_ACCESSSCRET			= "/findAllStaffType";
	/** 根据人员类型ID查询详情 */
	public static final String	FIND_STAFFTYPE_BY_ID					= "/findById/{id}";

	/** ---------------------------设施与设备关系绑定----------------------- */
	/** 设施与设备关系绑定根目录 */
	public static final String	BINDING									= "/binding";
	/** 根据设施id查询设施绑定信息 */
	public static final String	QUERY_FACILITY_BY_ID					= "queryFacilityById/{facilityId}";
	/** 根据设备id查询设备绑定信息 */
	public static final String	QUERY_DEVICE_BY_ID						= "queryDeviceById/{deviceId}";
	/** 查询设施绑定列表 */
	public static final String	QUERY_FACILITIES_BY_PAGE				= "queryFacilitiesByPage";
	/** 分页查询设备列表 */
	public static final String	QUERY_DEVICES_BY_PAGE					= "queryDevicesByPage";
	/** 绑定设备与设施 */
	public static final String	BIND_FACILITY_WITH_DEVICES				= "bindFacilityWithDevices";
	/** 解绑设备与设施 */
	public static final String	UNBIND_FACILITY_WITH_DEVICES			= "unbindFacilityWithDevices";
	/** 查询设备类型集合 */
	public static final String	QUERY_DEVICE_TYPE_LIST					= "queryDeviceTypeList/{accessSecret}";
	/** 查询设施下绑定的设备信息 */
	public static final String	FAC_BIND_DEVICE_INFO					= "/ByFacId";
	public static final String	FIND_BY_ACCESSSCRET						= "/findByAccessSecret/{accessSecret}";
	/** 地图设备远程命令，校验该设备是否在当前时间存在处理中的工单，分权分域，和配置了属性 */
	public static final String	DEVICE_CHECK							= "/deviceCheck";
	/** 导出设备列表 */
	public static final String	EXPORT_DEVICES							= "/exportDevices";
	
	/**信息概览使用，告警规则查询设备*/
	public static final String  QUERY_DEVICES_BY_RULE					="/queryDevicesByRule";
	/** 导出设备列表 */
	public static final String	EXPORT_DEVICES_BY_RUlE					= "/exportDevicesByRule";

	/** ---------------------------分权分域------------------------- */
	/** 分权分域管理根路径 */
	public static final String	DISTRIBUTIONPOWERS						= "/distributionPowers";
	/** 根据接入方秘钥分页查询分权分域配置 */
	public static final String	GET_PAGE_BY_ACCESSSCRET					= "/getPageByAccessSecret";
	/** 根据主键ID查询分权分域配置 */
	public static final String	GET_BY_ID								= "/getById/{id}";

	/** 新增分权分域配置 */
	public static final String	SAVE_DISTRIBUTIONPOWERS					= "/saveDistributionPowers";
	/** 修改分权分域配置 */
	public static final String	EDIT_DISTRIBUTIONPOWERS					= "/editDistributionPowers";
	/** 删除分权分域配置 */
	public static final String	DELETE_DISTRIBUTIONPOWERS				= "/deleteDistributionPowers/{id}";
	/** 改变管理状态 */
	public static final String	CHANGE_STATE							= "/changeState";

	/** ---------------------------厂家管理------------------------- */
	public static final String	FACTORY_BASE_PATH						= "factory";
	/** 新增厂家 **/
	public static final String	FACTORY_ADD								= "add/{accessSecret}";
	/** 新增厂家(适应新iot) **/
	public static final String	NEW_FACTORY								= "new/{accessSecret}";
	/** 修改厂家 **/
	public static final String	FACTORY_UPDATE							= "update/{id}";
	/** 修改厂家(适应新iot) **/
	public static final String	FACTORY_NEW_UPDATE						= "newUpdate/{id}";
	/** appTag监听 add **/
	public static final String	ADD_APPTAGQUEUE							= "addAppTagQueue";
	/** appTag监听 update **/
	public static final String	UPDATE_APPTAGQUEUE						= "updateAppTagQueue";
	/** 根据appTag同步平台设备数据 **/
	public static final String	SYNCHRO_DEVICE							= "synchro";
	/** 根据appTag同步iot平台(新)设备数据 **/
	public static final String	SYNCHRO_DATA							= "synchroData";
	/** 启停用厂家 **/
	public static final String	FACTORY_UPDATE_STATE					= "updateState/{id}";
	/** 分页查询厂家 **/
	public static final String	FACTORY_LIST							= "list/{accessSecret}";
	/** 分页查询厂家(适应新iot) **/
	public static final String	NEW_FACTORY_LIST						= "newList/{accessSecret}";
	/** 验证厂家apptag唯一性 **/
	public static final String	FACTORY_CHECK_APP_TAG					= "checkAppTag";
	/** 厂家修改apptag唯一性验证 **/
	public static final String	APPTAG_UPDATE_CHECK						= "appTagUpdateCheck";
	/** apptag唯一性验证适用于新iot厂家 **/
	public static final String	CHECK_APPTAG_EXIST						= "checkAppTagExist";
	/** 查询厂家详情 **/
	public static final String	FACTORY_FIND_BY_ID						= "findById/{id}";
	/** 查询厂家详情(适应新iot) **/
	public static final String	FIND_FACTORY_BY_ID						= "/{id}";

	/** 厂家名字查询（用于下拉框) **/
	public static final String	QUERY_FACTORY_NAME_LIST					= "queryFactoryNameList/{accessSecret}";

	/** 厂家加载下拉框 */
	public static final String	QUERY_FACTORY_BOX						= "/queryfactory/box/{accessSecret}";

	/** ---------------------------设备属性模板管理------------------------- */
	public static final String	DEVICE_BASE_PATH						= "device";
	/** 添加设备属性模版 **/
	public static final String	DEVICE_PROPERTIES_TEMPLATE_ADD			= "propertiesTemplate/add/{accessSecret}";
	/** 启停用设备属性模版 **/
	public static final String	DEVICE_PROPERTIES_TEMPLATE_UPDATESTATE	= "propertiesTemplate/updateState/{id}";
	/** 分页查询设备属性模版 **/
	public static final String	DEVICE_PROPERTIES_TEMPLATE_LIST			= "propertiesTemplate/list/{accessSecret}";
	/** 设备属性模版名称校验 **/
	public static final String	TEMPLATE_NAME_EXIST						= "propertiesTemplate/templateNameExist";

	/** ---------------------------设备属性管理------------------------- */
	/** 新增设备属性 **/
	public static final String	DEVICE_PROPERTIES_ADD					= "properties/add/{templateId}";
	/** 修改设备属性 **/
	public static final String	DEVICE_PROPERTIES_UPDATE				= "properties/update/{templateId}/{id}";
	/** 删除设备属性 **/
	public static final String	DEVICE_PROPERTIES_DELETE				= "properties/delete/{templateId}/{id}";
	/** 分页查询设备属性 **/
	public static final String	DEVICE_PROPERTIES_LIST					= "properties/list/{templateId}";
	/** 设备属性模板属性配置标识名显示名重名校验 **/
	public static final String	NAME_OR_SIGN_EXIST						= "properties/nameOrSignExist";

	/** ----------规则引擎管理------------ */
	/** 规则根目录 */
	public static final String	DROOLS									= "drools";
	/** 查询所有规则 */
	public static final String	GET_ALLDROOLS_BYPAGE					= "getAllDroolByPage";
	/** 根据规则引擎id查询规则 */
	public static final String	GET_DROOLINFO_BYID						= "getDroolInfoById";
	/** 从新加载规则 */
	public static final String	RELOAD_DROOLS							= "reloadDrools";
	/** 添加规则引擎 */
	public static final String	ADD_DROOLS								= "addDrools";
	/** 修改规则引擎 */
	public static final String	UPDATE_DROOLS							= "updateDrools";
	/** 启用或者警用规则引擎 */
	public static final String	SWATE_DROOLS_STATE						= "swateDroolsState";

	/** ----------告警规则管理------------ */
	/** 告警规则根目录 */
	public static final String	ALARM_RULE								= "/alarmRule";
	/** 告警规则增加 */
	public static final String	ALARM_RULE_ADD							= "/add";
	/** 告警规则更新 */
	public static final String	ALARM_RULE_UPDATE						= "/update";
	/** 修改告警信息忽略状态 */
	public static final String	UPDATE_THE_DEL							= "/updateTheDel";
	/** 告警规则删除 */
	public static final String	ALARM_RULE_DELETE						= "/del/{id}/{userId}";
	/** 告警规则-根据设备类型id查询状态属性集合 */
	public static final String	FIND_DATAPOINT_LIST_BY_DEVICETYPEID		= "/findDataPointByDeviceTypeId";
	/** 告警规则停用启用 */
	public static final String	ALARM_RULE_STATE						= "/state";
	/** 告警规则发布 */
	public static final String	ALARM_RULE_PULISH						= "/publish";
	/** 告警规则查询列表 */
	public static final String	ALARM_RULE_QUERY						= "/get/page";
	/** 告警规则根据g告警规则id查询规则信息 */
	public static final String	ALARM_RULE_FIND_BY_ID					= "/findById/{id}";

	/** ----------告警信息管理------------ */
	/** 告警信息根目录 */
	public static final String	STATE_INFO								= "/stateInfo";
	/** 告警信息分页查询 */
	public static final String	STATE_INFO_PAGE							= "/page";
	/** 告警列表分页查询 */
	public static final String	GETALARMINFOLIST						= "/getAlarmInfoList";
	/** 通过设施信息查询告警信息 */
	public static final String	STATE_INFO_BY_FAC_ID					= "/ByFacId";
	/** 统计告警信息 */
	public static final String	STATISTICS_ALARM_INFO					= "/statisticsAlarmInfo";
	/** 导出告警信息 */
	public static final String	EXPORT_ALARM_INFO						= "/exportAlarmInfo";
	/** 10条最新不自动生成工单的告警信息 */
	public static final String	ALARM_INFO_ROLL							= "/alarmInfoRoll";

	public static final String	FIND_ALARM_DEVICE_DATA					= "/findAlarmDeviceData/{alarmInfoId}";

	/** ------------设备状态---- */
	/** 设备状态 */
	public static final String	DEVICE_REAL_STATE						= "/deviceRealState";
	/** 获取状态路径 */
	public static final String	GET_REAL_STATE							= "/getRealState/{accessSecret}/{deviceId}";
	/** mongdb根据设备id分页查询设备历史上报数据状态 */
	public static final String	GET_HISTORY_STATE						= "/getHistoryState";
	/** 导出设备历史上报数据状态 */
	public static final String	EXPORT_HISTORY_STATE					= "/exportHistoryState";
	/** 统计中心统计设备实时电压、信号、锁状态等 */
	public static final String	GET_COUNT_DEVICE_REAL_STATE				= "/getCountDeviceRealState";
	/** 导出统计中心统计设备实时电压、信号、锁状态等 */
	public static final String	EXPORT_COUNT_DEVICE_REAL_STATE			= "/exportCountDeviceRealState";
	/**统计近5天设备的timing和trigger次数*/
	public static final String	COUNT_DEVICE_TIMING_TRIGGER				= "/countDeviceTimingTrigger";

	/** ----------故障工单管理------------ */
	/** 故障工单根目录 */
	public static final String	ORDER_FAULT								= "/order/faultOrder";
	/** 添加或修改故障工单 */
	public static final String	ADD_OR_UPDATE_FAULT_ORDER				= "/addOrUpdateFaultOrder";
	/** 添加或修改故障工单 */
	public static final String	UPDATE_FAULT_ORDER_STATE				= "/updateFaultOrderState";
	/** 获取故障类型 */
	public static final String	GET_FAULT_ORDER_TYPE					= "/getFaultOrderTypeList";
	/** 获取故障工单列表 */
	public static final String	GET_FAULT_ORDER_LIST					= "/getFaultOrderList";
	/** 获取故障工单列表 */
	public static final String	GET_FAULT_ORDER_LIST_NEW				= "/getFaultOrderListNew";
	/** 获取待办/流程列表故障工单 */
	public static final String	QUERY_AGENDA_OR_PROCESS					= "/queryAgendaOrProcessFaultOrderList";

	/** 获取待办/流程列表故障工单 */
	public static final String	QUERY_AGENDA_OR_PROCESS_NEW				= "/queryAgendaOrProcessFaultOrderListNew";

	/** 获取设施设备列表 */
	public static final String	QUERY_DEV_FAC_FOR_FAULT_ORDER			= "/queryDevicesForFaultOrder";
	/** 根据工单id查询工单详情 */
	public static final String	QUERY_FAULT_ORDER_INFO_BY_ID			= "/queryFaultOrderInfoById";
	/** 获取故障流程状态 */
	public static final String	GET_FAULT_ORDER_STATE					= "/getFaultOrderStateList";
	/** 统计故障工单信息 */
	public static final String	COUNT_FAULT_ORDERINFO_BY_AS				= "/countFaultOrderInfoByAS";
	/** 导出故障工单信息 */
	public static final String	FAULT_ORDER_EXCEL						= "/faultOrderExcel";
	/** 故障工单信息柱状图 */
	public static final String	FAULT_ORDER_HISTOGRAM					= "/faultOrderHistogram";
	/** 故障工单按组织和类型统计 */
	public static final String	COUNT_FAULT_ORDER_BY_ORG_AND_TYPE		= "/countFaultOrderByOrgAndType";
	/**故障工单列表*/
	public static final String	FAULT_ORDER_LIST						= "/faultOrderList";
	
	public static final String	FAULT_ORDER_STATE_COUNT					= "/faultOrderStateCount";

	/** ----------一般流程工单------------ */
	/** 根路径 */
	public static final String	SMIPLE_ORDER							= "/order/simpleOrder";
	/** 创建或修改一般流程工单 */
	public static final String	ADD_OR_UPDATE_SIMPLE_ORDER				= "/addOrUpdateSimpleOrder";
	/** 获取设施工单类型 */
	public static final String	GETORDERTYPELIST						= "/getOrderTypeList";
	/** 获取设施设备列表 */
	public static final String	GETFACILITIESLIST						= "/getFacilitiesList";
	/** 根据工单id获取信息 */
	public static final String	GETSIMPLEORDERBYID						= "/getSimpleOrderById";
	/** 获取一般流程工单列表 */
	public static final String	GETSIMPLEORDERLIST						= "/getSimpleOrderList";
	/** 获取一般工单流程状态集合 */
	public static final String	GETSIMPLEORDERSTATELIST					= "/getSimpleOrderStateList";
	/** 工单流程撤回、通过、拒绝、完成 */
	public static final String	UPDATESIMPLEORDERSTATE					= "/updateSimpleOrderState";
	/** 查询给定时间点该设备是否存在处理中的一般流程工单 */
	public static final String	WHETHEREXISTORDER						= "/whetherExistOrder";
	/** 我的代办获取工单列表 */
	public static final String	GETSIMPLEORDERAGENCYLIST				= "/getSimpleOrderAgencyList";
	/** 获取一般流程工单节点信息 */
	public static final String	GETORDERNODEDETAILS						= "/getOrderNodeDetails";
	/** 查询工单设施信息 */
	public static final String	FINDFACINFO								= "/findFacInfo";
	/** 延时审核（只有通过审核的工单并且是已过期的） */
	public static final String	DELAYED_SIMPLEORDER						= "/delayedSimpleOrder";
	/** 作废工单(工单必须是过期状态且只能由发起人作废) */
	public static final String	INVALIDATE_SIMPLEORDER					= "/invalidateSimpleOrder";
	/** ----------告警工单------------ */
	/** 告警工单根目录 */
	public static final String	ALARM_ORDER								= "/alarmOrder";
	/** 分页查询告警工单信息 */
	public static final String	ALARM_ORDER_GET_ALL_BY_PAGE				= "/getAllByPage";
	/** 分页查询告警工单待办信息 */
	public static final String	ALARM_ORDER_GET_ALL_TODO_BY_PAGE		= "/getAllTodoByPage";
	/** 分页查询告警工单待办信息 */
	public static final String	ALARM_ORDER_GET_HAVE_DEAL_BY_PAGE		= "/getAllHaveDealByPage";
	/** 添加同用告警规则时获取设施设备列表 */
	public static final String	FACILITIES_DEVICE_LIST					= "/getFacilitiesDeviceList";
	/** 下拉框状态 */
	public static final String	ALARM_ORDER_GET_STATE					= "/getState";
	/** 获取拒绝原因 */
	public static final String	ALARM_ORDER_GET_REJECT_CAUSE			= "/getRejectCause/{orderId}";
	/** 获取正常完成信息 */
	public static final String	ALARM_ORDER_GET_COMPLETEINFO			= "/getComplete/{orderId}";
	/** 获取无法修复信息 */
	public static final String	ALARM_ORDER_GET_POWERLESS_INFO			= "/getPowerlessInfo/{orderId}";
	/** 查看工单处理信息 */
	public static final String	ALARM_ORDER_PROCESS_INFO				= "/processInfo/{orderId}";
	/** 获取工单类型 */
	public static final String	ALARM_ORDER_GET_ORDER_TYPE				= "/getOrderType";
	/** 获取工单类型 */
	public static final String	ALARM_ORDER_GET_QUESTION_TYPE			= "/getQuestionType";
	/** 工单完成处理 */
	public static final String	ALARM_ORDER_COMPLETE					= "/complete";
	/** 生产告警工单 */
	public static final String	ALARM_ORDER_SAVE						= "/saveAlarmOrder";
	/** 接受告警工单 */
	public static final String	ALARM_ORDER_ACCEPT						= "/acceptAlarmOrder";
	/** 工单完转故障 */
	public static final String	ALARM_ORDER_POWERLESS					= "/powerless";
	/** 工单审批 */
	public static final String	ALARM_ORDER_APPROVE						= "/approve";
	/** 工单拒绝 */
	public static final String	ALARM_ORDER_REJECT						= "/reject";
	/** 查询未接受的告警工单 */
	public static final String	ALARM_NOT_CLAIM							= "/notClaimAlarmOrder";
	/** 查询未接受的告警工单总数(区分接入方,可选区分组织) */
	public static final String	ALARM_NOT_CLAIM_TOTAL					= "/getNotClaimAlarmOrderTotal";
	/** 统计告警工单数量 */
	public static final String	ALARM_ORDER_COUNT						= "/alarmOrderCount";
	/** 统计告警信息详情工单列表 */
	public static final String	ALARM_DETAILS_COUNT						= "/alarmDetailsCount";
	/** 导出告警工单信息列表 */
	public static final String	ALARM_EXCEL_ALL							= "/alarmOrderExcel";
	/** 告警统计信息导出 */
	public static final String	ALARM_EXCEL_COUNT						= "/alarmExcelCount";
	/** 统计所有告警工单列表 */
	public static final String	COUNT_ALL_ALARMORDER					= "/countAllAlarmOrder";
	/** 导出所有告警工单Excel */
	public static final String	ALARM_ORDER_EXCEL						= "/allAlarmOrderExcel";
	/** 告警工单完成审批通过 */
	public static final String	ALARM_ORDER_APPROVAL_BEFORE_END			= "/alarmOrderApprovalBeforeEnd";
	/** 告警工单完成审批拒绝 */
	public static final String	ALARM_ORDER_REJECT_BEFORE_END			= "/alarmOrderRejectBeforeEnd";
	/** 隐患类型查询  app用*/
	public static final String  GET_HIDDEN_TROUBLE_TYPE                 = "/getHiddenTroubleType";

	/** 添加到场图片和完成处理图片 */

	public static final String	ADD_PRESENT_PIC							= "/addPresentPic";
	/** app端图片上传对接 */
	public static final String	ADD_APP_PIC								= "/addAppPic";
	/** 按组织统计工单信息 */
	public static final String	COUNT_ALARM_ORDER_BY_ORG				= "/countAlarmOrderBy";
	/** 按组织统计工单信息导出 */
	public static final String	EXPROT_ALARM_ORDER_COUNT				= "/exprotAlarmOrderCount";

	/** ----------统计------------ */
	/** 工单统计 */
	public static final String	STATISTICS								= "/statistics";
	/** 工单统计 */
	public static final String	STATISTICS_ORDER						= "/order/{accessSecret}/{userId}";
	/** 工单统计-我的流程 */
	public static final String	PROCESS_MY								= "/orderMy/{accessSecret}/{userId}";
	/** 工单统计-我的代办流程 */
	public static final String	PROCESS_MY_TODO							= "/orderTodo/{accessSecret}/{userId}";
	/** 工单统计-我的已办流程 */
	public static final String	PROCESS_MY_HTODO						= "/orderHTodo/{accessSecret}/{userId}";
	/** 工单总条数 */
	public static final String	PROCESS_TOTAL							= "/order/totalCount";

	/** --------------------------- 工单流程配置 ----------------------- */
	/** 工单流程根路径 */
	public static final String	ORDER_PROCESS_CONFIG					= "/orderProcessConfig";
	/** 根据工单流程类型模糊查询工单流程列表 */
	public static final String	QUERY_ORDERPROCESS_LIST					= "/queryOrderProcessList";
	/** 管理工单流程状态(启用/停用) */
	public static final String	UPDATE_STATE							= "/updateState";
	/** 查询工单流程类型 */
	public static final String	QUERY_ORDERPROCESS_TYPE					= "/queryOrderProcessType";
	/** 解析流程模版xml文件 */
	public static final String	PARSE_ORDERPROCESS_TMPLATE				= "/parseOrderProcessTemplate";
	/** 上传保存流程模版xml文件 */
	public static final String	SAVE_ORDERPROCESS_TMPLATE				= "/saveOrderProcessTemplate/{accessSecret}";
	/** 新增工单流程 */
	public static final String	ADD_ORDERPROCESS						= "/addOrderProcess";
	/** 编辑工单流程 */
	public static final String	UPDATE_ORDERPROCESS						= "/updateOrderProcess";
	/** 根据工单流程id查看工单流程详情 */
	public static final String	QUERY_ORDERPROCESS_BY_ID				= "/queryOrderProcessById/{id}";
	/** 判断用户是否绑定在流程图上 */
	public static final String	EXIST_USER_INPRO						= "/existUserInProcess/{userId}";

	/** --------------------------- 设备信息转换 ----------------------- */
	/** 设备信息转换根路径 */
	public static final String	DEVICE_INFO_CONVERT						= "/deviceInfoConvert";
	/** 字典翻译保存 */
	public static final String	CONVERT_INFO_SAVE						= "/convertInfoSave";
	/** 字典翻译修改 */
	public static final String	CONVERT_INFO_UPDATE						= "/convertInfoUpdate";
	/** 字典翻译删除 */
	public static final String	CONVERT_INFO_DELETE						= "/convertInfoDelete";
	/** 分页模糊查询 */
	public static final String	CONVERT_INFO_QUERYALL					= "/convertInfoQueryAll";
	/** 根据id查询 */
	public static final String	CONVERT_INFO_QUERYBYID					= "/convertInfoById";
	/** 判断是否存在 */
	public static final String	CONVERT_INFO_EXIST						= "/existConvertInfo";
	/** 根据id查询设施状态 */
	public static final String	QUERY_FACILITY_MANAGESTATE_BY_IDS		= "/queryfacilitymanagestatebyids";

	/** --------------------------- 基础数据同步 ----------------------- */
	/** 同步基础数据根路径 */
	public static final String	BASIC_DATA								= "/basicData";
	/** 同步基础设施数据 */
	public static final String	BASIC_FACILITIES						= "/basicFacilities";
	/** 同步基础通用告警规则 */
	public static final String	BASIC_ALARMRULE							= "/basicAlarmRule";
	/** 同步基础数据状态查询 */
	public static final String	SYNCHRONOUS_STATE						= "/synchronousState/{accessSecret}";
	/** 同步基础数据(设备属性模板)路径 */
	public static final String	BASIC_DEVICETYPE_TEMPLATE				= "/basicDeviceTypeTemplate";
	/** 同步基础数据特殊值转换 */
	public static final String	BASIC_DEVICEINFO_CONVERT				= "/basicdeviceinfoconvert";

	/** --------------------------- 工程版设施数据同步 ----------------------- */
	/** 工程版设施数据同步根路径 */
	public static final String	DEVICE_DATA_STORAGE						= "/deviceDataStorage";
	/** 添加工程版设施数据 */
	public static final String	ADD_DEVICE_DATA_STORAGE					= "/add";
	/** 工程版设施数据删除 */
	public static final String	DELETE_DEVICE_DATA_STORAGE				= "/delete/{deviceDataId}";
	/** 修改工程版设施数据 */
	public static final String	UPDATE_DEVICE_DATA_STORAGE				= "/updateDataStorage";
	/** 分页查询工程版设施列表 */
	public static final String	DEVICE_DATA_STORAGE_LIST				= "/list";
	/** 根据id查询工程版设施数据 */
	public static final String	DEVICE_DATA_STORAGE_BY_ID				= "/getById/{id}";
	/** 同步工程版传入设施数据到locman */
	public static final String	SYNCHRONIZE_TO_FACILITIES				= "/add2Facilities";
	/** 查询工程版数据的所有区域信息 */
	public static final String	GET_ALL_AREA							= "/areaInfo";
	/** 校验是设备编号否存在(于同步数据暂存表) */
	public static final String	CHECK_DEVICE_NUMBER_EXIST				= "/checkDeviceNumber";
	/** 校验是序列号否存在(于同步数据暂存表) */
	public static final String	CHECK_SERIAL_NUMBER_EXIST				= "/checkSerialNumber";

	/** --------------------------- 平衡告警开关权限配置 ----------------------- */
	/** 平衡告警权限配置根路径 */
	public static final String	BALANCE									= "/balance";
	/** 平衡告警权限配置 保存 */
	public static final String	BALANCE_SWITCH_SAVE						= "/balanceSwitchSave";
	/** 平衡告警权限配置 修改 */
	public static final String	BALANCE_SWITCH_UPDATE					= "/balanceSwitchUpdate";
	/** 平衡告警权限配置 删除 */
	public static final String	BALANCE_SWITCH_DEL						= "/balanceSwitchDel";
	/** 平衡告警权限配置 停用，启用 */
	public static final String	BALANCE_SWITCH_STATE_CHANGE				= "/balanceSwitchStateChange";
	/** 分页查询平衡告警权限配置 */
	public static final String	GET_BALANCESWITCHPOWERS_LIST			= "/getBalanceSwitchPowersList";
	/** 根据id查询平衡告警权限配置 */
	public static final String	GET_BALANCESWITCHPOWERS_BY_ID			= "/getBalanceSwitchPowersById";

	/** --------------------------- 信息概述 ----------------------- */
	/** 信息概述根路径 */
	public static final String	INFO_SUMMARY							= "/infoSummary";
	/** 设备统计路径 */
	public static final String	DEVICE_COUNT							= "/deviceCount/{accessSecret}";
	/** 设施统计路径 */
	public static final String	FACILITY_TOTAL							= "/facilityCount/{accessSecret}";
	/** 未处理告警和故障工单统计路径 */
	public static final String	UNPROCESSED_ORDER_COUNT					= "/unprocessedOrderCount/{accessSecret}";
	/** 待处理流程数量路径 */
	public static final String	ORDER_TO_DO_COUNT						= "/orderToDoCount/{accessSecret}/{userId}";
	/** 超指定天数天未上报设备数量路径 */
	public static final String	DEVICE_NOT_REPORT						= "/deviceCountNotReportInSetDay/{accessSecret}";
	/** 正常设备数量路径 */
	public static final String	NORMAL_DEVICE_COUNT						= "/normalDeviceCount/{accessSecret}";
	/** 用户数量路径 */
	public static final String	USER_NUMBER								= "/userNumber/{accessSecret}";
	/** 接入方信息路径 */
	public static final String	ACCESS_INFORMATION						= "/accessInformation/{accessSecret}";
	/** 接入方使用率路径 */
	public static final String	USAGE_RATE								= "/usageRate/{accessSecret}";
	/** 统计每天系统中告警数量（当前日期为准的30天） */
	public static final String	DAILY_ALARM_COUNT						= "/dailyAlarmCount/{accessSecret}";
	/** 统计每天系统中故障数量（当前日期为准的30天） */
	public static final String	DAILY_FAULT_COUNT						= "/dailyFaultCount/{accessSecret}";
	/** 统计每天系统中告警数量（当前日期为准的30天） */
	public static final String	COUNT_ALARM_NUM_BY_DATE					= "/alarmNumByDate/{accessSecret}";
	/** 统计系统中指定时间段每天的告警工单或者故障工单处理和未处理数量 **/
	public static final String	COUNT_ORDER_STATE_NUM_BY_DATE			= "/countOrderStateNumByDate";
	/** 统计街道办和街道数量，仅限成华区使用 */
	public static final String	COUNT_STREET_OFFICE_AND_STREET_NUM		= "/countStreetOfficeAndStreetNum";

	/** 设施告警数量和无告警数量统计 */
	public static final String	COUNT_FAC_ALARM_NUM					= "/countFacAlarmNum/{accessSecret}";
	
	/**7天上报设备数量统计(屏蔽版本号)*/
	public static final String COUNT_DEVICE_REPORT_NUM				="/countDeviceReportNum";
	/**7天内每天上报设备数量按告警等级分类(屏蔽版本号)*/
	public static final String COUNT_DAILY_DEVICE_NUM_BY_ALARM_LEVEL  ="/countDailyDeviceNumByALarmLevel/{accessSecret}";
	/**按告警等级和工单状态统计告警工单数量*/
	public static final String COUNT_ALARM_ORDER_NUM_BY_STATE_AND_ALARM_LEVEL = "/countAlarmOrderNumByStateAndAlarmLevel/{accessSecret}";
	/**按街道统计告警设施*/
	public static final String COUNT_ALARM_FAC_BYORG			 = "/countAlarmFacByOrg/{accessSecret}";
	/**组合条件搜索设备时间段告警趋势*/
	public static final String COUNT_ALARM_BYID_ANDTIME  		 ="/countAlarmByIdAndTime";
	/**成华区，统计触发告警规则设备数量*/
	public static final String COUNT_ALARM_DEV_BYRULE			 ="/countAlarmDevByRule/{accessSecret}";
	/**成华区，统计开井数量前20*/
	public static final String COUNT_TRIGGER_TOP			 ="/countTriggerTop";
	/**成华区，根据设备id查看井盖开启次数详情*/
	public static final String DETAILS_BY_DEVICEID			 ="/detailsByDeviceId";
	/**成华区，导出井盖开启详情*/
	public static final String EXPORT_OPEN_DETAILS			 ="/exportOpenDetails";
	
	/** --------------------------- 行政区域 ----------------------- */
	/** 行政区域查询根路径 */
	public static final String	AREA									= "/area";
	/** 根据行政区域码查询区域 */
	public static final String	GETAREABYCODE							= "/getAreaByCode";
	/** 根据接入方密钥查询地图显示信息 */
	public static final String  GET_ACCESS_PARTY_INFO_FOR_MAP			= "/accessParty/info/{accessSecret}";
	/** 根据接入方密钥查询地图显示信息 */
	public static final String  GET_INFO_BY_URL_STR						= "/accessParty/homePageInfo/{urlStr}";
	/**地图区域查询接口*/
	public static final String	GET_CROSSEDMAP_BY_ACC					= "/accessParty/crossedMap/{accessSecret}";

	/** --------------------------- 超时未上报----------------------- */
	/** 超时未上报根路径 */
	public static final String	TIMEOUT									= "/timeoutReport";
	/** 添加超时未上报设置 */
	public static final String	ADD_TIMEOUT_REPORT_CONFIG				= "/addTimeoutReportConfig";
	/** 删除超时未上报设置 */
	public static final String	DELETE_TIMEOUT_REPORT_CONFIG			= "/deleteTimeoutReportConfig";
	/** 修改超时未上报设置 */
	public static final String	UPDATE_TIMEOUT_REPORT_CONFIG			= "/updateTimeoutReportConfig";
	/** 接入方密钥查询超时未上报设置列表 */
	public static final String	QUERY_TIMEOUT_REPORT_CONFIG_LIST		= "/TimeoutReportConfigList/{accessSecret}";
	/** 接入方密钥查询超时未上报设置列表 */
	public static final String	QUERY_TIMEOUT_REPORT_CONFIG_BYID		= "/TimeoutReportConfig/{id}";
	/** 查询已选择/未选择的设施信息 */
	public static final String	QUERY_FACINFO							= "/queryFacInfo";

	/** --------------------------- 开锁记录----------------------- */
	/** 开锁记录跟路径 */
	public static final String	OPEN_RECORD								= "/openRecord";
	/** 查询开锁记录 */
	public static final String	QUERY_OPEN_RECORD						= "/queryOpenRecord";
	/** 开锁记录导出 */
	public static final String	OPEN_RECORD_EXCEL						= "/excelOpenRecord";

	/** ----------------------- 开关锁记录--------------------------- */
	/** 开关锁根路径 */
	public static final String	SWITCHLOCK_ROOT							= "/switchLockRoot";
	/** 开关锁分页查询 */
	public static final String	SWITCHLOCK_PAGE							= "/switchLockPage";
	/** 开关锁导出 */
	public static final String	SWITCHLOCK_EXPROT						= "/switchLockExprot";
	/**成华区，设施开关记录列表*/
	public static final String 	LIST_MANHOLE_COVER_SWITCH  				="/listManholeCoverSwitch";
	/**成华区，根据设备Id查询开关井详细信息*/
	public static final String  GET_MANHOLE_COVER_SWITCH_INFO			="/getManholeCoverSwitchInfo";

	/** ------------------------ 设备状态导出-------------------------- */
	public static final String	DEVICE_STATE_URL						= "/deviceStateUrl";
	/** 导出设备状态 */
	public static final String	DEVICE_STATE_EXPORT						= "/deviceStateExport/{accessSecret}";

	/** ------------------------ 启动activemq客户端-------------------------- */
	/** 根 */
	public static final String	ACTIVEMQ_APPTAG							= "/activeMqAppTag";
	/** 启动时调用 */
	public static final String	ACTIVEMQ_APPTAG_START					= "/startAppTagAMQP";
	/** 修改时调用 */
	public static final String	ACTIVEMQ_APPTAG_UPDATE					= "/updateAppTagAMQP";

	/** ------------------------ 一次性工具类常量-------------------------- */
	/** 添加设备首次上线时间 */
	public static final String	DEVICEFIRST								= "/deviceFirst";
	/** mysql添加设备最新上报时间 */
	public static final String	DEVICE_LAST_REPORT_TIME					= "/deviceLastReportTime";
	/** mysql添加设备在线离线状态 */
	public static final String	DEVICE_ON_LINE_STATE					= "/deviceOnLineState";
	/** mysql添加设备硬件编码 */
	public static final String	DEVICE_HARDWAREID						= "/deviceHardwareId";

	/**
	 * ------------------------
	 * 平台数据mysql、mongoDB数据直接查询--------------------------
	 */
	/** 根路径 */
	public static final String	DATAQUERY								= "/dataQuery";
	/** mysql查询 */
	public static final String	MYSQLQUERY								= "/mysqlQuery";
	/** mongoDb查询 */
	public static final String	MONGODBQUERY							= "/mongoDbQuery";
	
	
	/** ------------------------ 电信iot推送数据接收根路径-------------------------- */
	public static final String  WINGSIOTPUSH                            =  "/wingsIotPush";
	
	
	/**指令响应消息*/
	public static final String  COMMANDRESPONSE                         =  "/commandResponse";
	
	
	
	/** --------------------------- 重保配置相关路径 ----------------------- */
	/** 根路径 */
	public static final String	FOCUSSECURITY							= "/focusSecurity";
	/** 分页查询重保配置*/
	public static final String	GET_FOCUSSECURITY_INFO_PAGE				= "/getFocusSecurityInfoPage";
	/** 新增重保配置*/
	public static final String	ADD_FOCUSSECURITY						= "/addFocusSecurity";
	/** 启用、停用重保配置*/
	public static final String	ENABLED_FOCUSSECURITY					= "/enabledFocusSecurity";
	/** 下发命令*/
	public static final String	OPERATE_LOCK							= "/operateLock";
	/** 查询重保配置相关设施命令接收情况*/
	public static final String	FACILITES_COMMAND_RECEIVE_STATES		= "/commandReceiveStates";
	
	
	/** ------------------------ 供数据转换中心调用接口------------------------- */
	
	public static final String  DATACONVERSION                          =  "/dataConversion";
	
	/**设备上报数据*/
	public static final String  DEVICEREPORTED                          =  "/deviceReported";
	
	/**数据转换中心推送设备接收接口*/
	public static final String PUSHDEVICE								= "/deviceInfoSyn";
	

	
	/**------------------------ 验证码根路径------------------------ */
	public static final String	VERIFICATION									= "/verification";

	/** 获得图片 */
	public static final String	GETPIC											= "/getPic";

	/** 验证 */
	public static final String	CHECKCAPCODE									= "/checkcapcode";
	
	
	
	
	
}
