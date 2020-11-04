/*
 * File name: DeviceDeletionConstants.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年6月15日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.constants;

/**
 * @Description: 设备超时未上报相关字段
 * @author: guofeilong
 * @version: 1.0, 2018年6月15日
 */

public class DeviceDeletionConstants {
	/**
	 * 接入方密钥
	 */
	public static final String	ACCESS_SECRET			= "accessSecret";
	/**
	 * 故障工单类型(如:外力破坏,系统使用误操作,其他)
	 */
	public static final String	FAULT_TYPE				= "faultType";
	/**
	 * 申报人
	 */
	public static final String	MANAGER					= "manager";
	/**
	 * 统计设备数
	 */
	public static final String	DEVICE_COUNT			= "deviceCount";
	/**
	 * 厂家人员联系电话
	 */
	public static final String	PHONE					= "phone";
	/**
	 * 申报人id
	 */
	public static final String	USER_ID					= "userId";
	/**
	 * 厂家id
	 */
	public static final String	FACTORY_ID				= "factoryId";
	/**
	 * 组织id
	 */
	public static final String	ORGANIZE_ID				= "organizeId";
	/**
	 * 设备id集合
	 */
	public static final String	DEVICE_IDS_ADD			= "deviceIdsAdd";
	/**
	 * 故障工单id
	 */
	public static final String	FAULT_ORDER_ID			= "id";
	/**
	 * 故障描述
	 */
	public static final String	MARK					= "mark";
	/**
	 * 故障工单名
	 */
	public static final String	ORDER_NAME				= "orderName";
	/**
	 * 故障流程类型(故障工单,告警转故障)
	 */
	public static final String	FAULT_PROCESS_TYPE		= "faultProcessType";
	/**
	 * 故障图片
	 */
	public static final String	ORDER_IMG				= "orderImg";
	/**
	 * 工作流 - 用户节点信息
	 */
	public static final String	ACTIVITY_USEROBJ		= "actvity_userObj";
	/**
	 * 流程id
	 */
	public static final String	PROCESS_ID				= "processId";
	/**
	 * 故障工单流水号
	 */
	public static final String	SERIAL_NUMBER			= "serialNumber";
	/**
	 * 故障工单流水号(工作流所需字段)
	 */
	public static final String	ACTIVITY_SERIAL_NUMBER	= "orderNumber";
	/**
	 * 配置的超时时间
	 */
	public static final String	CONFIG_TIME				= "configTime";
	/**
	 * 设备类型名称
	 */
	public static final String	DEVICE_TYPE_NAME		= "deviceTypeName";
	
	/**
	 * faultProcessType:1表示告警转故障的流程类型 2表示故障流程类型,3表示待整治设施转故障
	 */
	public static final String	CODE_THREE		= "3";
	/**
	 * faultProcessType:1表示告警转故障的流程类型 2表示故障流程类型
	 */
	public static final String	CODE_TWO		= "2";
	/**
	 * faultProcessType:1表示告警转故障的流程类型 2表示故障流程类型
	 */
	public static final String	CODE_ONE		= "1";
	/**
	 * 告警工单状态
	 */
	public static final String	STATE		= "state";
	/**
	 * 工单已经审核过
	 */
	public static final String	FALSE					= 	"false";
	
	/**
	 * 工单操作类型
	 */
	public static final String	OPERATIONTYPE			= 	"operationType";
	/**
	 * 工单操作类型参数
	 */
	public static final String	WITHDRAW					= 	"撤回";
	/**
	 * 工单操作类型参数
	 */
	public static final String	REFUSE					= 	"拒绝";
	
	/**
	 * 工单操作类型参数
	 */
	public static final String	PASS					= 	"通过";
	
	/**
	 * 工单操作类型参数
	 */
	public static final String	COMPLETE				= 	"完成";
	
	/**
	 * 工单操作类型参数
	 */
	public static final String	RETURN_TO_FACTORY		= 	"返厂";
	
	/**
	 * 理由
	 */
	public static final String	DETAIL					= 	"detail";
	
}
