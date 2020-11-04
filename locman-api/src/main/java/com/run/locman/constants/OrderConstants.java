/*
 * File name: OrderConstants.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年11月21日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.constants;

/**
 * @Description:工单常量
 * @author: 张贺
 * @version: 1.0, 2018年11月21日
 */

public class OrderConstants {
	/** 绑定状态 */
	public static final String	BINDING		= "binding";

	/** 已绑定 */
	public static final String	BOUND		= "bound";
	/** 未绑定 */
	public static final String	UNBOUND		= "unBound";

	/** 模块类型 */
	public static final String	MODEL_TYPE	= "modelType";
	/** 待办*/
	public static final String  NOT_DONE    = "notDone";
	/**已办*/
	public static final String  HAVE_DONE   = "haveDone";
	
	/**一般*/
	public static final String  SIMPLE  = "simple";
	/**故障*/
	public static final String  FAULT  = "fault";
	/**告警*/
	public static final String  ALARM  = "alarm";
	
	/**历史节点*/
	public static final String  HIS_EXCU_LIST = "hisExcuList";
	
	public static final String  ORDER_TYPE    = "orderType";
}
