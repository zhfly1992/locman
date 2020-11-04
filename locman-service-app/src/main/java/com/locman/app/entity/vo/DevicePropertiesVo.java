package com.locman.app.entity.vo;

import com.run.locman.api.entity.DeviceProperties;

@SuppressWarnings("serial")
public class DevicePropertiesVo extends DeviceProperties {

	private String	value;			// 英文值

	private String	description;	// 汉化后中文描述



	public DevicePropertiesVo(DeviceProperties devPro) {
		this.setAppIcon(devPro.getAppIcon());
		this.setCreationTime(devPro.getCreationTime());
		this.setDataType(devPro.getDataType());
		this.setDataValue(devPro.getDataValue());
		this.setDevicePropertiesName(devPro.getDevicePropertiesName());
		this.setDevicePropertiesSign(devPro.getDevicePropertiesSign());
		this.setId(devPro.getId());
		this.setOrder(devPro.getOrder());
		this.setReadWrite(devPro.getReadWrite());
		this.setRemark(devPro.getRemark());
		this.setTemplateId(devPro.getTemplateId());
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}

}
