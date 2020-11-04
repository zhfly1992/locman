package com.run.locman.constants;

/**
 * 
 * @Description:告警工单常量类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public class AlarmOrderConstants {
	/** 告警工单分页码 */
	public static final String	PAGE_NO								= "pageNo";
	/** 告警工单页大小 */
	public static final String	PAGE_SIZE							= "pageSize";
	/** 告警工单id */
	public static final String	ORDER_ID							= "orderId";
	/** 告警工单正常处理类型 */
	public static final String	NORMAL_ORDER_TYPE					= "orderType";
	/** 告警工单正常处理类型 */
	public static final String	NORMAL_ORDER_TYPE_NAME				= "orderTypeName";
	/** 告警正常完成工单备注 */
	public static final String	NORMAL_REMARK						= "remark";
	/** 告警正常工单图片地址 */
	public static final String	NORMAL_ORDER_PIC					= "orderPic";
	/** 用户请求人 **/
	public static final String	APPLY_USERID						= "apply_userId";
	/** 用户id **/
	public static final String	USERID								= "userId";
	/** 告警工单无法修复处理类型 */
	public static final String	POWERLESS_ORDER_TYPE				= "powerless_orderType";
	/** 告警工单无法修复备注 */
	public static final String	POWERLESS_REMARK					= "powerless_remark";
	/** 告警工单无法修复图片地址 */
	public static final String	POWERLESS_ORDER_PIC					= "powerless_orderImg";
	/** 无法处理申请人 */
	public static final String	POWERLESS_USERID					= "powerless_userId";
	/** 接入方编码 **/
	public static final String	USC_ACCESS_SECRET					= "accessSecret";
	/** 用户id当前审批的人 **/
	public static final String	APPROVE_USERID						= "approve_userId";
	/** 用户name审批指定操作的人 **/
	public static final String	USER_NAME							= "name";
	/** 用户phone审批指定操作的人 **/
	public static final String	USER_PHONE							= "phone";
	/** 拒绝原因 */
	public static final String	REJECT_REMARK						= "reject_remark";
	/** 拒绝人id */
	public static final String	REJECT_USERID						= "reject_userId";
	/** 规则引擎id **/
	public static final String	PROCESS_ID							= "processId";
	/** 转故障id */
	public static final String	FAULT_ORDER_ID						= "faultOrderId";
	/** 到场图片 */
	public static final String	PRESENT_PIC							= "presentPic";
	/** 开始时间 */
	public static final String	START_TIME							= "startTime";
	/** 结束时间 */
	public static final String	END_TIME							= "endTime";
	/** 处理结果 */
	public static final String	MESSAGE								= "message";

	/// ** 规则引擎用户数据变量 **/
	// public static final String PROCESS_VARIABLES = "variables";
	/** 规则引擎用户数据变量 **/
	public static final String	PROCESS_TYPE						= "type";
	/** 规则引擎用户数据变量 **/
	public static final String	PROCESS_NODE_ID						= "nodeId";
	/** 一般流程在数据使用的时候需要剔除的工作节点,表示不用返回当前节点的数据,更改工作流bpm图节点id后就需要更改此处 **/
	public static final String	PROCCESS_NODE_REMOVEN_SIGN_SIM		= "usertask4";
	/** 告警流程在数据使用的时候需要剔除的工作节点,表示不用返回当前节点的数据,更改工作流bpm图节点id后就需要更改此处 **/
	public static final String	PROCCESS_NODE_REMOVEN_SIGN_ALA		= "usertask4";
	/** 新建故障工单流程流程在数据使用的时候需要剔除的工作节点,表示不用返回当前节点的数据,更改工作流bpm图节点id后就需要更改此处 **/
	public static final String	PROCCESS_NODE_REMOVEN_SIGN_NEW_FAU	= "usertask1";
	/** 告警转故障故障工单流程流程在数据使用的时候需要剔除的工作节点,表示不用返回当前节点的数据,更改工作流bpm图节点id后就需要更改此处 **/
	public static final String	PROCCESS_NODE_REMOVEN_SIGN_ALA_FAU	= "usertask3";
	/** 规则引擎用户数据变量 **/
	public static final String	PROCESS_TYPE_ALARMFAILURE			= "alarmProcess";
	/** 规则引擎用户数据变量 **/
	public static final String	PROCESS_STATE						= "processState";
	/** 规则引擎用户数据变量 **/
	public static final String	STATE								= "status";
	/** id **/
	public static final String	ID									= "id";
	/** 设备id **/
	public static final String	DEVICE_ID							= "deviceId";
	/** 规则 **/
	public static final String	DEVICE_ALARM_RULE					= "rule";

	public static final String	COMMANDFLAG							= "commandFlag";

	public static final String	ALARM_ID							= "alarmId";
	/** 接收人名称 */
	public static final String	RECEIVE_USER_NAME					= "receiveUserName";
	/** 完工审批同意人id */
	public static final String	APPROVE_COMPLETE_USERID				= "approveCompleteUserId";
	/** 完工审批拒绝人id */                                             
	public static final String	REJECT_COMPLETE_USERID				= "rejectCompleteUserId";
}
