/*
 * File name: SimpleTimerPush.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2019年1月2日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import entity.JiguangEntity;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2019年1月2日
 */

public class SimpleTimerPush implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	/** 工单id */
	private String				orderId;
	/** 工单流水号 */
	private String				serialNumber;
	/** 列表项 1:我的流程 ,2:代办, 3:已办 */
	private String				selectNo = "1";
	/** 多久之后提醒(秒)*/
	private int					remindTime;
	/** 工单类型 */
	private String				orderType = "generalProcess";

	/** 要发送设备的注册id集合 */
	private List<String>		aliasIds;
	/** 通知内容标题 */
	private String				notificationTitle = "作业工单";
	/** 消息内容标题 */
	private String				msgTitle = "作业工单";
	/** 消息内容 */
	private String				msgContent;



	public JiguangEntity getJiguangEntity() {
		
		if (StringUtils.isBlank(msgContent)) {
			msgContent = String.format("您好！工单流水号为：%s即将结束，请及时处理，如需延期工单请点击!", serialNumber);
		}
		JiguangEntity jiguangEntity = new JiguangEntity();
		jiguangEntity.setAliasIds(aliasIds);
		jiguangEntity.setNotificationTitle(notificationTitle);
		jiguangEntity.setMsgTitle(msgTitle);
		jiguangEntity.setMsgContent(msgContent);
		JSONObject json = new JSONObject();
		json.put("processType", orderType);
		json.put("selectNo", selectNo);
		json.put("orderId", orderId);
		jiguangEntity.setExtrasparam(json.toJSONString());
		return jiguangEntity;

	}



	/**
	 * 
	 */
	public SimpleTimerPush() {
		super();

	}



	/**
	 * @return the orderType
	 */
	public String getOrderType() {
		return orderType;
	}



	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}



	/**
	 * @return the aliasIds
	 */
	public List<String> getAliasIds() {
		return aliasIds;
	}



	/**
	 * @param aliasIds
	 *            the aliasIds to set
	 */
	public void setAliasIds(List<String> aliasIds) {
		this.aliasIds = aliasIds;
	}



	/**
	 * @return the notificationTitle
	 */
	public String getNotificationTitle() {
		return notificationTitle;
	}



	/**
	 * @param notificationTitle
	 *            the notificationTitle to set
	 */
	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}



	/**
	 * @return the msgTitle
	 */
	public String getMsgTitle() {
		return msgTitle;
	}



	/**
	 * @param msgTitle
	 *            the msgTitle to set
	 */
	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}



	/**
	 * @return the msgContent
	 */
	public String getMsgContent() {
		return msgContent;
	}



	/**
	 * @param msgContent
	 *            the msgContent to set
	 */
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}



	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}



	/**
	 * @param orderId
	 *            the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}



	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}



	/**
	 * @param serialNumber
	 *            the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}



	/**
	 * @return the selectNo
	 */
	public String getSelectNo() {
		return selectNo;
	}



	/**
	 * 
	 * @param 列表项
	 *            1:我的流程 ,2:代办, 3:已办
	 */
	public void setSelectNo(String selectNo) {
		this.selectNo = selectNo;
	}



	/**
	 * @return the remindTime
	 */
	public int getRemindTime() {
		return remindTime;
	}



	/**
	 * @param remindTime
	 *            the remindTime to set
	 */
	public void setRemindTime(int remindTime) {
		this.remindTime = remindTime;
	}

}
