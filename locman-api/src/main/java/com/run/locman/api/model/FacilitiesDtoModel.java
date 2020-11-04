/*
 * File name: FacilitiesDtoModel.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年6月27日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.model;

import java.io.Serializable;
import java.util.List;

import com.run.locman.api.base.model.PageModel;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年6月27日
 */

public class FacilitiesDtoModel extends PageModel implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6271069236263203245L;

	/**
	 * 设施id
	 */
	private String				id;

	/**
	 * 设施序列号
	 */
	private String				facilitiesCode;

	/**
	 * 接入方密钥
	 */
	private String				accessSecret;

	/**
	 * 设施id集合(已经勾选的设施id)
	 */
	private List<String>		facIds;

	/**
	 * 设施状态
	 */
	private String				manageState;

	/**
	 * 设施类型id
	 */
	private String				facilitiesTypeId;

	/**
	 * 是否已选择 unBound bound
	 */
	private String				binding;

	/**
	 * 设施地址
	 */
	private String				address;

	/**
	 * 设施id
	 */
	private String				facilitiesId;

	/**
	 * 配置id
	 */
	private String				configId;

	/**
	 * 关键字查询
	 */
	private String				searchKey;

	/**
	 * 
	 */
	public FacilitiesDtoModel() {
		super();
		
	}

	/**
	 * @param id
	 * @param facilitiesCode
	 * @param accessSecret
	 * @param facIds
	 * @param manageState
	 * @param facilitiesTypeId
	 * @param binding
	 * @param address
	 * @param facilitiesId
	 * @param configId
	 * @param searchKey
	 */
	public FacilitiesDtoModel(String id, String facilitiesCode, String accessSecret, List<String> facIds,
			String manageState, String facilitiesTypeId, String binding, String address, String facilitiesId,
			String configId, String searchKey) {
		super();
		this.id = id;
		this.facilitiesCode = facilitiesCode;
		this.accessSecret = accessSecret;
		this.facIds = facIds;
		this.manageState = manageState;
		this.facilitiesTypeId = facilitiesTypeId;
		this.binding = binding;
		this.address = address;
		this.facilitiesId = facilitiesId;
		this.configId = configId;
		this.searchKey = searchKey;
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
	 * @return the facilitiesCode
	 */
	public String getFacilitiesCode() {
		return facilitiesCode;
	}

	/**
	 * @param facilitiesCode the facilitiesCode to set
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
	 * @param accessSecret the accessSecret to set
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
	 * @param facIds the facIds to set
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
	 * @param manageState the manageState to set
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
	 * @param facilitiesTypeId the facilitiesTypeId to set
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
	 * @param binding the binding to set
	 */
	public void setBinding(String binding) {
		this.binding = binding;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the facilitiesId
	 */
	public String getFacilitiesId() {
		return facilitiesId;
	}

	/**
	 * @param facilitiesId the facilitiesId to set
	 */
	public void setFacilitiesId(String facilitiesId) {
		this.facilitiesId = facilitiesId;
	}

	/**
	 * @return the configId
	 */
	public String getConfigId() {
		return configId;
	}

	/**
	 * @param configId the configId to set
	 */
	public void setConfigId(String configId) {
		this.configId = configId;
	}

	/**
	 * @return the searchKey
	 */
	public String getSearchKey() {
		return searchKey;
	}

	/**
	 * @param searchKey the searchKey to set
	 */
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
