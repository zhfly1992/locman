package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @author
 */
public class DeviceProperties implements Serializable {
	private String				id;

	/**
	 * 设备属性名
	 */
	private String				devicePropertiesName;

	/**
	 * 设备属性标识
	 */
	private String				devicePropertiesSign;

	/**
	 * 数据类型
	 */
	private String				dataType;

	/**
	 * 数据值
	 */
	private String				dataValue;

	/**
	 * 读写类型
	 */
	private Integer				readWrite;

	/**
	 * 图标
	 */
	private String				icon;

	/**
	 * APP图标
	 */
	private String				appIcon;

	/**
	 * 备注
	 */
	private String				remark;

	/**
	 * 创建时间
	 */
	private String				creationTime;

	/**
	 * 模版id
	 */
	private String				templateId;

	/**
	 * 排序序号
	 */
	private Integer				order;

	/**
	 * 排序序号
	 */
	private String				orderNo;

	private static final long	serialVersionUID	= 1L;



	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}



	/**
	 * @param orderNo
	 *            the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getDevicePropertiesName() {
		return devicePropertiesName;
	}



	public void setDevicePropertiesName(String devicePropertiesName) {
		this.devicePropertiesName = devicePropertiesName;
	}



	public String getDevicePropertiesSign() {
		return devicePropertiesSign;
	}



	public void setDevicePropertiesSign(String devicePropertiesSign) {
		this.devicePropertiesSign = devicePropertiesSign;
	}



	public String getDataType() {
		return dataType;
	}



	public void setDataType(String dataType) {
		this.dataType = dataType;
	}



	public String getDataValue() {
		return dataValue;
	}



	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}



	public Integer getReadWrite() {
		return readWrite;
	}



	public void setReadWrite(Integer readWrite) {
		this.readWrite = readWrite;
	}



	public String getIcon() {
		return icon;
	}



	public void setIcon(String icon) {
		this.icon = icon;
	}



	public String getRemark() {
		return remark;
	}



	public void setRemark(String remark) {
		this.remark = remark;
	}



	public String getCreationTime() {
		return creationTime;
	}



	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}



	public String getTemplateId() {
		return templateId;
	}



	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}



	public Integer getOrder() {
		return order;
	}



	public void setOrder(Integer order) {
		this.order = order;
	}



	public String getAppIcon() {
		return appIcon;
	}



	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceProperties [id=" + id + ", devicePropertiesName=" + devicePropertiesName
				+ ", devicePropertiesSign=" + devicePropertiesSign + ", dataType=" + dataType + ", dataValue="
				+ dataValue + ", readWrite=" + readWrite + ", icon=" + icon + ", appIcon=" + appIcon + ", remark="
				+ remark + ", creationTime=" + creationTime + ", templateId=" + templateId + ", order=" + order
				+ ", orderNo=" + orderNo + "]";
	}
	
	
}