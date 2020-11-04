/*
* File name: FacilitiesModel.java								
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
* 1.0			Administrator		2018年4月19日
* ...			...			...
*
***************************************************/

package com.run.locman.api.model;

import java.io.Serializable;
import java.util.List;

import com.run.locman.api.base.model.PageModel;

/**
 * @Description:设施model实体
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月19日
 */

public class FacilitiesModel extends PageModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1727561162684008117L;

	/**
	 * 设施id
	 */
	private String id;

	/**
	 * 设施序列号
	 */
	private String facilitiesCode;

	/**
	 * 接入方密钥
	 */
	private String accessSecret;

	/**
	 * 设施id集合(已经勾选的设施id)
	 */
	private List<String> facIds;

	/**
	 * 设施状态
	 */
	private String manageState;

	/**
	 * 设施类型id
	 */
	private String facilitiesTypeId;

	/**
	 * 是否已选择 unBound bound
	 */
	private String binding;

	/**
	 * 设施地址
	 */
	private String address;

	/**
	 * 一般工单id
	 */
	private String simpleOrderId;

	/**
	 * 设施id
	 */
	private String facilitiesId;

	/**
	 * 工单与设施id
	 */
	private String simplerOrFacId;

	/**
	 * 关键字查询
	 */
	private String searchKey;

	/**
	 * @return the searchKey
	 */
	public String getSearchKey() {
		return searchKey;
	}

	/**
	 * @param searchKey
	 *            the searchKey to set
	 */
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	/**
	 * @return the simpleOrderId
	 */
	public String getSimpleOrderId() {
		return simpleOrderId;
	}

	/**
	 * @return the simplerOrFacId
	 */
	public String getSimplerOrFacId() {
		return simplerOrFacId;
	}

	/**
	 * @param simplerOrFacId
	 *            the simplerOrFacId to set
	 */
	public void setSimplerOrFacId(String simplerOrFacId) {
		this.simplerOrFacId = simplerOrFacId;
	}

	/**
	 * @param simpleOrderId
	 *            the simpleOrderId to set
	 */
	public void setSimpleOrderId(String simpleOrderId) {
		this.simpleOrderId = simpleOrderId;
	}

	/**
	 * @return the facilitiesId
	 */
	public String getFacilitiesId() {
		return facilitiesId;
	}

	/**
	 * @param facilitiesId
	 *            the facilitiesId to set
	 */
	public void setFacilitiesId(String facilitiesId) {
		this.facilitiesId = facilitiesId;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the facilitiesCode
	 */
	public String getFacilitiesCode() {
		return facilitiesCode;
	}

	/**
	 * @param facilitiesCode
	 *            the facilitiesCode to set
	 */
	public void setFacilitiesCode(String facilitiesCode) {
		this.facilitiesCode = facilitiesCode;
	}

	/**
	 * @return the accessSecret
	 */
	public String getAccessSecret() {
		return accessSecret;
	}

	/**
	 * @param accessSecret
	 *            the accessSecret to set
	 */
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	/**
	 * @return the facIds
	 */
	public List<String> getFacIds() {
		return facIds;
	}

	/**
	 * @param facIds
	 *            the facIds to set
	 */
	public void setFacIds(List<String> facIds) {
		this.facIds = facIds;
	}

	/**
	 * @return the manageState
	 */
	public String getManageState() {
		return manageState;
	}

	/**
	 * @param manageState
	 *            the manageState to set
	 */
	public void setManageState(String manageState) {
		this.manageState = manageState;
	}

	/**
	 * @return the facilitiesTypeId
	 */
	public String getFacilitiesTypeId() {
		return facilitiesTypeId;
	}

	/**
	 * @param facilitiesTypeId
	 *            the facilitiesTypeId to set
	 */
	public void setFacilitiesTypeId(String facilitiesTypeId) {
		this.facilitiesTypeId = facilitiesTypeId;
	}

	/**
	 * @return the binding
	 */
	public String getBinding() {
		return binding;
	}

	/**
	 * @param binding
	 *            the binding to set
	 */
	public void setBinding(String binding) {
		this.binding = binding;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FacilitiesModel [id=" + id + ", facilitiesCode=" + facilitiesCode + ", accessSecret=" + accessSecret
				+ ", facIds=" + facIds + ", manageState=" + manageState + ", facilitiesTypeId=" + facilitiesTypeId
				+ ", binding=" + binding + ", address=" + address + ", simpleOrderId=" + simpleOrderId
				+ ", facilitiesId=" + facilitiesId + ", simplerOrFacId=" + simplerOrFacId + ", searchKey=" + searchKey
				+ ", pageSize=" + pageSize + ", pageNum=" + pageNum + "]";
	}

}
