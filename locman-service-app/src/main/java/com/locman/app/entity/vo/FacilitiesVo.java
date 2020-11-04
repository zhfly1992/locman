package com.locman.app.entity.vo;

public class FacilitiesVo {

	private String	facilitiesId;		// 设施id
	private String	ManageState;		// 设施状态
	private String	address;			// 设施地址
	private String	facilitiesCode;		// 设施编号
	private String	latitude;			// 纬度
	private String	longitude;			// 经度
	private String	facilityTypeIco;	// 设施图标
	private String	facilitiesTypeName;	// 设施类型名称
	private Integer	alarmWorstLevel;	// 告警等级



	public String getFacilitiesId() {
		return facilitiesId;
	}



	public void setFacilitiesId(String facilitiesId) {
		this.facilitiesId = facilitiesId;
	}



	public String getManageState() {
		return ManageState;
	}



	public void setManageState(String manageState) {
		ManageState = manageState;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getFacilitiesCode() {
		return facilitiesCode;
	}



	public void setFacilitiesCode(String facilitiesCode) {
		this.facilitiesCode = facilitiesCode;
	}



	public String getLatitude() {
		return latitude;
	}



	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}



	public String getLongitude() {
		return longitude;
	}



	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}



	public String getFacilityTypeIco() {
		return facilityTypeIco;
	}



	public void setFacilityTypeIco(String facilityTypeIco) {
		this.facilityTypeIco = facilityTypeIco;
	}



	public String getFacilitiesTypeName() {
		return facilitiesTypeName;
	}



	public void setFacilitiesTypeName(String facilitiesTypeName) {
		this.facilitiesTypeName = facilitiesTypeName;
	}



	public Integer getAlarmWorstLevel() {
		return alarmWorstLevel;
	}



	public void setAlarmWorstLevel(Integer alarmWorstLevel) {
		this.alarmWorstLevel = alarmWorstLevel;
	}

}
