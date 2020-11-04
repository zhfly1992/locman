/*
* File name: AttributeInfo.java								
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
* 1.0			guofeilong		2019年8月30日
* ...			...			...
*
***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年8月30日
*/

public class AttributeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 属性别名(中文)
	 */
	private String				attributeAlias;
	
	/**
	 * 属性名称
	 */
	private String				attributeName;
	
	/**
	 * 上报值
	 */
	private String				attributeReportedValue;
	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}
	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	/**
	 * 
	 */
	public AttributeInfo() {
		super();
		
	}
	/**
	 * @return the attributeAlias
	 */
	public String getAttributeAlias() {
		return attributeAlias;
	}
	/**
	 * @param attributeAlias the attributeAlias to set
	 */
	public void setAttributeAlias(String attributeAlias) {
		this.attributeAlias = attributeAlias;
	}
	/**
	 * @return the attributeReportedValue
	 */
	public String getAttributeReportedValue() {
		return attributeReportedValue;
	}
	/**
	 * @param attributeReportedValue the attributeReportedValue to set
	 */
	public void setAttributeReportedValue(String attributeReportedValue) {
		this.attributeReportedValue = attributeReportedValue;
	}
	
}
