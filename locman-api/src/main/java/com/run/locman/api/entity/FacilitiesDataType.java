package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 设施数据类型实体
 * @author: wangsheng
 * @version: 1.0, 2017年8月8日
 */
public class FacilitiesDataType implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String				id;
	private String				facilitiesTypeId;
	private String				dataType;
	private String				name;
	private String				sign;
	private String				initialValue;
	private String				isNotMandatory;
	private String				state;
	private String				remarks;



	public FacilitiesDataType() {
	}



	public FacilitiesDataType(String id, String facilitiesTypeId, String dataType, String name, String sign,
			String initialValue, String isNotMandatory, String state, String remarks) {
		super();
		this.id = id;
		this.facilitiesTypeId = facilitiesTypeId;
		this.dataType = dataType;
		this.name = name;
		this.sign = sign;
		this.initialValue = initialValue;
		this.isNotMandatory = isNotMandatory;
		this.state = state;
		this.remarks = remarks;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getFacilitiesTypeId() {
		return facilitiesTypeId;
	}



	public void setFacilitiesTypeId(String facilitiesTypeId) {
		this.facilitiesTypeId = facilitiesTypeId;
	}



	public String getDataType() {
		return dataType;
	}



	public void setDataType(String dataType) {
		this.dataType = dataType;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getSign() {
		return sign;
	}



	public void setSign(String sign) {
		this.sign = sign;
	}



	public String getInitialValue() {
		return initialValue;
	}



	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}



	public String getIsNotMandatory() {
		return isNotMandatory;
	}



	public void setIsNotMandatory(String isNotMandatory) {
		this.isNotMandatory = isNotMandatory;
	}



	public String getState() {
		return state;
	}



	public void setState(String state) {
		this.state = state;
	}



	public String getRemarks() {
		return remarks;
	}



	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FacilitiesDataType [id=" + id + ", facilitiesTypeId=" + facilitiesTypeId + ", dataType=" + dataType
				+ ", name=" + name + ", sign=" + sign + ", initialValue=" + initialValue + ", isNotMandatory="
				+ isNotMandatory + ", state=" + state + ", remarks=" + remarks + "]";
	}

}