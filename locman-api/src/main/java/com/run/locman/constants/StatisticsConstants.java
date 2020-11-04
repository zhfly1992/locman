package com.run.locman.constants;

/**
 * 
 * @Description:统计实体类
 * @author: lkc
 * @version: 1.0, 2017年12月7日
 */
public class StatisticsConstants {
	/** 用户id */
	public static final String	USERID								= "userId";
	/** 接入方编码 **/
	public static final String	USC_ACCESS_SECRET					= "accessSecret";
	/** 流程类型 **/
	public static final String	PROCCESS_TYPE						= "type";
	/** 一般流程在数据使用的时候需要剔除的工作节点,表示不用返回当前节点的数据,更改工作流bpm图节点id后就需要更改此处 **/
	public static final String	PROCCESS_NODE_REMOVEN_SIGN_SIM		= "usertask4";
	/** 告警流程在数据使用的时候需要剔除的工作节点,表示不用返回当前节点的数据,更改工作流bpm图节点id后就需要更改此处 **/
	public static final String	PROCCESS_NODE_REMOVEN_SIGN_ALA		= "usertask4";
	/** 新建故障工单流程流程在数据使用的时候需要剔除的工作节点,表示不用返回当前节点的数据,更改工作流bpm图节点id后就需要更改此处 **/
	public static final String	PROCCESS_NODE_REMOVEN_SIGN_NEW_FAU	= "usertask1";
	/** 告警转故障故障工单流程流程在数据使用的时候需要剔除的工作节点,表示不用返回当前节点的数据,更改工作流bpm图节点id后就需要更改此处 **/
	public static final String	PROCCESS_NODE_REMOVEN_SIGN_ALA_FAU	= "usertask3";
}
