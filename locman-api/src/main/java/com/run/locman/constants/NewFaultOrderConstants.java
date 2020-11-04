/*
* File name: NewFaultOrderOperateConstants.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			Administrator		2019年12月7日
* ...			...			...
*
***************************************************/

package com.run.locman.constants;

/**
* @Description:	
* @author: Administrator
* @version: 1.0, 2019年12月7日
*/

public class NewFaultOrderConstants {
	
	    /*操作动作*/
		/** 接受工单**/
		public static final String Receive = "receive";
		/** 完成处理 **/
		public static final String CompleteDeal = "completeDeal";
		/** 更改处理人 **/
		public static final String ChangeUser = "changeUser";
		/** 自动检测通过 **/
		public static final String AutoDectPass = "autoDectPass";
		/** 自动检测未通过 **/
		public static final String AutoDectReject = "autoDectReject";
		/** 同意更改处理人 **/
		public static final String ChangePass = "changePass";
		/** 拒绝更改处理人 **/
		public static final String ChangeReject = "changeReject";
		
		/*工单状态*/
		/**待处理*/
		public static final String NotDeal = "1";
		/**处理中*/
		public static final String Dealing = "2";
		/**换人审核中*/
		public static final String ChangeUserCheck = "3";
		/**自动检测中*/
		public static final String AutoDectCheck = "4";
		/**已完成*/
		public static final String Complete = "5";
		
		
}
