/*
* File name: AppTagDto.java								
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
* 1.0			guofeilong		2018年10月19日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

import java.io.Serializable;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年10月19日
*/

public class AppTagDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 应用id */
	private String				appId;
	
	/** 应用key */
	private String				appKey;
	
	/** 设备id(新iot参数) */
	private String				subThingId;
	
	/** 设备名称(新iot参数) */
	private String				subThingName;
	
	/** 网关id(新iot参数) */
	private String				gatewayId;
	
	/** 网关名称(新iot参数) */
	private String				gatewayName;
	
	/** 产品id(新iot参数) */
	private String				productId;
	
	/** Factory_AppTag表id */
	private String				id;
	
	/**
	 * 接入方密钥
	 */
	public static final String	ACCESSSECRET				= "accessSecret";
	
	/**
	 * 新iot中appTag固定后缀
	 */
	public static final String	DATACHANGE				= ".dataChange";
	
	/**
	 * appTag字符串
	 */
	public static final String	APPTAGS				= "appTags";
	
	/**
	 * 
	 */
	public AppTagDto() {
		super();
		
	}

	/**
	 * @param appId
	 * @param appKey
	 * @param subThingId
	 * @param subThingName
	 * @param gatewayId
	 */
	public AppTagDto(String appId, String appKey, String subThingId, String subThingName, String gatewayId, String gatewayName, String productId, String id) {
		super();
		this.appId = appId;
		this.appKey = appKey;
		this.subThingId = subThingId;
		this.subThingName = subThingName;
		this.gatewayId = gatewayId;
		this.gatewayName = gatewayName;
		this.productId = productId;
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the gatewayName
	 */
	public String getGatewayName() {
		return gatewayName;
	}

	/**
	 * @param gatewayName the gatewayName to set
	 */
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @return the subThingId
	 */
	public String getSubThingId() {
		return subThingId;
	}

	/**
	 * @param subThingId the subThingId to set
	 */
	public void setSubThingId(String subThingId) {
		this.subThingId = subThingId;
	}

	/**
	 * @return the subThingName
	 */
	public String getSubThingName() {
		return subThingName;
	}

	/**
	 * @param subThingName the subThingName to set
	 */
	public void setSubThingName(String subThingName) {
		this.subThingName = subThingName;
	}

	/**
	 * @return the gatewayId
	 */
	public String getGatewayId() {
		return gatewayId;
	}

	/**
	 * @param gatewayId the gatewayId to set
	 */
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the appKey
	 */
	public String getAppKey() {
		return appKey;
	}

	/**
	 * @param appKey the appKey to set
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	/**
	 * @return the appTag
	 */
	public String getAppTag() {
		return new StringBuffer().append(appId).append(".").append(appKey).append(DATACHANGE).toString();
	}

	/**
	 * @param appTag the appTag to set
	 */
	public void setAppTag(String appId,String appKey) {
		this.appId = appId;
		this.appKey = appKey;
	}
	
	
}
