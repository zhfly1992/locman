/*
 * File name: SimpleOrderConstants.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月6日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.constants;

import java.io.Serializable;

/**
 * @Description:
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public class SimpleOrderConstants implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/** 增加修改类型 */
	public static final String	TYPE				= "type";
	/** 工单绑定条数 */
	public static final String	DEVICECOUNT			= "deviceCount";
	/** 工单类型 */
	public static final String	ORDERTYPE			= "orderType";
	/** 工单名称 */
	public static final String	ORDERNAME			= "orderName";
	/** 施工单位 */
	public static final String	CONSTRUCTBY			= "constructBy";
	/** 发起人 */
	public static final String	CREATEBY			= "createBy";
	/** 联系人 */
	public static final String	MANAGER				= "manager";
	/** 联系人方式 */
	public static final String	PHONE				= "phone";
	/** 工单预约起始时间 */
	public static final String	PROCESSSTARTTIME	= "processStartTime";
	/** 工单预约结束时间 */
	public static final String	PROCESSENDTIME		= "processEndTime";
	/** 施工说明 */
	public static final String	MARK				= "mark";
	/** 上传图片 */
	public static final String	ORDERIMG			= "orderImg";
	/** 用户id */
	public static final String	USERID				= "userId";
	/** 接入方秘钥 */
	public static final String	ACCESSSECRET		= "accessSecret";
	/** 工单id */
	public static final String	ID					= "id";
	/** 流程id */
	public static final String	PROCESSID			= "processId";
	/** 流程状态 */
	public static final String	PROCESSSTATE		= "processState";
	/** 工单绑定的id集合 */
	public static final String	DEVICEIDSADD		= "deviceIdsAdd";
	/** 工单解除绑定的id集合 */
	public static final String	DEVICEIDSDEL		= "deviceIdsDel";
	/** 一般工单流程id */
	public static final String	SIMPLEORDERID		= "simpleOrderId";
	/** 设备id */
	public static final String	DEVICEID			= "deviceId";
	/** 设施类型 */
	public static final String	FACILITIESTYPEID	= "facilitiesTypeId";
	/** 搜索条件 */
	public static final String	SELECTKEY			= "selectKey";
	/** 操作类型 */
	public static final String	OPERATIONTYPE		= "operationType";
	/** 处理理由 */
	public static final String	DETAIL				= "detail";
	/** 组织id */
	public static final String	ORGANIZEID			= "organizeId";
	/** 工作流 - 用户节点信息 */
	public static final String	ACTIVITY_USEROBJ	= "actvity_userObj";
	/** 设施id集合 */
	public static final String	FACILITIES_LIST		= "facilitiesList";
	/** 工单流水号 */
	public static final String	ORDER_NUM			= "orderNumber";
	/** hours延时 时间 */
	public static final String	HOURS				= "hours";
	/** 工单状态 app用 (具有过期状态) */
	public static final String	PROCESS_STATE_APP	= "processStateApp";
	/** 是否延时标识 */
	public static final String	AFFIRM				= "affirm";
	/** 工单详细信息key */
	public static final String	HISEXCULIST			= "hisExcuList";
	/** 工单任务节点本地变量key */
	public static final String	ORDER_VARIABLE		= "variable";
	/** 工单流水号 */
	public static final String	SERIALNUMBER		= "serialNumber";
	/** 工单任务节点是否是终审 */
	public static final String	FINAL_AUDIT			= "finalAudit";
	/** 提前提醒时间 */
	public static final String	REMIND_TIME			= "remindTime";
	/** 提前提醒规则 */
	public static final String	REMIND_RULE			= "remindRule";
	
	
	/** 添加 */
	public static final String	ADD					= "add";
	/** 修改 */
	public static final String	UPDATE					= "update";
	
	/** 状态 */
	public static final String	STATUS				= "status";
	
	/**
	 * 工单操作类型参数
	 */
	public static final String	REFUSE				= 	"拒绝";
	
	/**
	 * 工单操作类型参数
	 */
	public static final String	PASS				= 	"通过";
	/**
	 * 工单操作类型参数
	 */
	public static final String	WITHDRAW			= 	"撤回";
	/**
	 * 工单操作类型参数
	 */
	public static final String	COMPLETE			= 	"完成";
	/**
	 * 是最终审核,或是延时操作
	 */
	public static final String	TRUE				= 	"true";
	
	/**
	 * 工单已经审核过,不能再审核
	 */
	public static final String	FALSE				= 	"false";
	
	/**
	 * 工单状态7代表是的已过期
	 */
	public static final String	STATE_SEVEN			= 	"7";
	/**
	 * 工单状态
	 */
	public static final String	STATE				= 	"state";
	
}
