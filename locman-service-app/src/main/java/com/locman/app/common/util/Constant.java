package com.locman.app.common.util;

import java.util.Map;

import com.google.common.collect.Maps;

public class Constant {

	public static final String GETACCESSSECRET(String userId) {
		try {
			/** 根据userid获取用户接入方秘钥 */
			if (RedisUtil.exists(APP + userId)) {
				String object = (String) RedisUtil.get(APP + userId);
				return object;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static final String	MOBILE	= "mobile";
	public static final String	EMAIL	= "email";

	public static final String	APP		= "APP";

	/** 对象状态 有效 */
	public static final String	VALID	= "valid";		// 有效
	/** 对象状态 无效 */
	public static final String	INVALID	= "invalid";	// 无效

	/** 一般流程处理动作 */
	public static final class SIMOPERATIONTYPE {
		public static final Map<String, String> TYPEMAP = Maps.newHashMap();
		static {
			TYPEMAP.put("0", "撤回");
			TYPEMAP.put("1", "通过");
			TYPEMAP.put("2", "拒绝");
			TYPEMAP.put("3", "完成");
			TYPEMAP.put("4", "返厂");
		}
	}

	/** 告警工单流程处理动作 */
	public static final class ALAOPERATIONTYPE {
		/** 正常完成 */
		public static final String	COMPLATE	= "0";
		/** 无法修复 */
		public static final String	POWERLESS	= "1";
		/** 审核通过 */
		public static final String	APPROVE		= "2";
		/** 审核拒绝 */
		public static final String	REJECT		= "3";
		/** 接受工单和图片上传 */
		public static final String	ACCEPT		= "4";
	}

	/** rest请求locman根目录 */
	public static final String	LOCMAN_PORT			= ":8002/locman/";
	/** rest请求用户中心根目录 */
	public static final String	INTERGATEWAY_PORT	= ":8002/interGateway/";

	/** 流程类型描述 */
	public static final class PROCESSTYPENAME {
		/** 故障工单类型描述 */
		public static final String	FAULT_TYPE_NAME		= "faultOrderType";
		/** 一般流程工单类型 */
		public static final String	SIMPLE_TYPE_NAME	= "simpleType";

	}

	/** 流程选项卡 */
	public static final class OPTIONS_TABS {
		/** 我的流程 */
		public static final String	MY_PROCESS		= "1";
		/** 待办流程 */
		public static final String	BACKLOG_PROCESS	= "2";
		/** 已办流程 */
		public static final String	RUN_PROCESS		= "3";
		/** 待接收工单 */
		public static final String	NOT_RECEIVED	= "4";
	}

	/** 流程操作按钮 */
	public static final class BUTTUN {
		/** 一般流程审批，告警工单完成和无法修复 */
		public static final String	BT_ONE		= "1";
		/** 一般流程开锁和完成，告警工单转故障审批 */
		public static final String	BT_TWO		= "2";
		/** 一般流程撤回，告警工单接受工单 */
		public static final String	BT_THREE	= "3";
		/** 一般流程审批人联系方式 , 告警工单完成审批 通过或拒绝*/
		public static final String	BT_FOUR		= "4";
		/** 一般流程申请人作废已过期工单 */
		public static final String	BT_FIVE		= "5";
		/** 一般流程申请人延时审核时联系审核人和开锁 */
		public static final String	BT_SIX		= "6";
		/** 故障工单撤回 */
		public static final String	BT_ROLLBACK	= "revoke";
		/** 故障工单完成和返厂 */
		public static final String	BT_COMPLETE	= "complete";
		/** 故障工单审批 */
		public static final String	BT_APPROVE	= "approve";
		/** 故障工单确认 */
		public static final String	BT_CONFIRM	= "confirm";
	}

	/** 告警工单相关参数 */
	public static final class ALARM {
		/** 告警工单工单状态为其他的id值 */
		public static final String ALARM_ORDETYPE_ID = "89f75b8bda7011e78d43d4ae52b55aae";
	}

	/** 工单类型 */
	public static final class ORDERTYPE {
		/** 开锁工单 */
		public static final String	UNLOCK	= "unlock";
		/** 告警工单 */
		public static final String	ALARM	= "alarm";
	}

	/** 视频上传最大值 */
	public static final long FIFTEEN_M = 15728640;

}
