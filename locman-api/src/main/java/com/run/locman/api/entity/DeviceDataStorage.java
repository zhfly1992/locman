/*
 * File name: DeviceDataStorage.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月23日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 工程版设备信息存储表(属性沿用工程版)
 * @author: guofeilong
 * @version: 1.0, 2018年11月23日
 */

public class DeviceDataStorage implements Serializable {

	private static final long	serialVersionUID	= 2151547733101210144L;

	/**
	 * 主键id
	 */
	private String				id;
	/**
	 * 工程版中设备ID
	 */
	private String				deviceId;
	/**
	 * 设备硬件编号
	 */
	private String				deviceNumber;
	/**
	 * 设备蓝牙名称
	 */
	private String				bluetooth;
	/**
	 * 设备安装地址
	 */
	private String				deviceAddress;
	/**
	 * 设备安装经度
	 */
	private String				longitude;
	/**
	 * 设备安装纬度
	 */
	private String				latitude;
	/**
	 * 设备上报地址
	 */
	private String				ipPort;
	/**
	 * 序列号（井盖序列号、井盖铭牌号）
	 */
	private String				serialNumber;
	/**
	 * 设施设备状态（survey：勘测、project：安装）
	 */
	private String				status;
	/**
	 * 扩展属性，JSON字符串
	 */
	private String				properties;
	/**
	 * 设备/设施类型ID
	 */
	private String				deviceTypeId;
	/**
	 * 同步状态
	 */
	private String				synchronizationState;
	/**
	 * 区域id
	 */
	private String				areaId;
	/**
	 * 同步失败信息
	 */
	private String				errorInfo;
	/**
	 * 解析为显示扩展属性
	 */
	private String				showExtend;

	/**
	 * 解析为扩展属性
	 */
	private String				extend;

	/**
	 * 修改人
	 */
	private String				updateBy;

	/**
	 * 同步人
	 */
	private String				synchBy;



	/**
	 * @return the updateBy
	 */
	public String getUpdateBy() {
		return updateBy;
	}



	/**
	 * @param updateBy
	 *            the updateBy to set
	 */
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}



	/**
	 * @return the synchBy
	 */
	public String getSynchBy() {
		return synchBy;
	}



	/**
	 * @param synchBy
	 *            the synchBy to set
	 */
	public void setSynchBy(String synchBy) {
		this.synchBy = synchBy;
	}



	/**
	 * @return the showExtend
	 */
	public String getShowExtend() {
		return showExtend;
	}



	/**
	 * @param showExtend
	 *            the showExtend to set
	 */
	public void setShowExtend(String showExtend) {
		this.showExtend = showExtend;
	}



	/**
	 * @return the extend
	 */
	public String getExtend() {
		return extend;
	}



	/**
	 * @param extend
	 *            the extend to set
	 */
	public void setExtend(String extend) {
		this.extend = extend;
	}



	/**
	 * @return the errorInfo
	 */
	public String getErrorInfo() {
		return errorInfo;
	}



	/**
	 * @param errorInfo
	 *            the errorInfo to set
	 */
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}



	/**
	 * 
	 */
	public DeviceDataStorage() {
		super();

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
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}



	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



	/**
	 * @return the deviceNumber
	 */
	public String getDeviceNumber() {
		return deviceNumber;
	}



	/**
	 * @param deviceNumber
	 *            the deviceNumber to set
	 */
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}



	/**
	 * @return the bluetooth
	 */
	public String getBluetooth() {
		return bluetooth;
	}



	/**
	 * @param bluetooth
	 *            the bluetooth to set
	 */
	public void setBluetooth(String bluetooth) {
		this.bluetooth = bluetooth;
	}



	/**
	 * @return the deviceAddress
	 */
	public String getDeviceAddress() {
		return deviceAddress;
	}



	/**
	 * @param deviceAddress
	 *            the deviceAddress to set
	 */
	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}



	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}



	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}



	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}



	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}



	/**
	 * @return the ipPort
	 */
	public String getIpPort() {
		return ipPort;
	}



	/**
	 * @param ipPort
	 *            the ipPort to set
	 */
	public void setIpPort(String ipPort) {
		this.ipPort = ipPort;
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}



	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}



	/**
	 * @return the properties
	 */
	public String getProperties() {
		return properties;
	}



	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(String properties) {
		this.properties = properties;
	}



	/**
	 * @return the deviceTypeId
	 */
	public String getDeviceTypeId() {
		return deviceTypeId;
	}



	/**
	 * @param deviceTypeId
	 *            the deviceTypeId to set
	 */
	public void setDeviceTypeId(String deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}



	/**
	 * @return the synchronizationState
	 */
	public String getSynchronizationState() {
		return synchronizationState;
	}



	/**
	 * @param synchronizationState
	 *            the synchronizationState to set
	 */
	public void setSynchronizationState(String synchronizationState) {
		this.synchronizationState = synchronizationState;
	}



	/**
	 * @return the areaId
	 */
	public String getAreaId() {
		return areaId;
	}



	/**
	 * @param areaId
	 *            the areaId to set
	 */
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceDataStorage [id=" + id + ", deviceId=" + deviceId + ", deviceNumber=" + deviceNumber
				+ ", bluetooth=" + bluetooth + ", deviceAddress=" + deviceAddress + ", longitude=" + longitude
				+ ", latitude=" + latitude + ", ipPort=" + ipPort + ", serialNumber=" + serialNumber + ", status="
				+ status + ", properties=" + properties + ", deviceTypeId=" + deviceTypeId + ", synchronizationState="
				+ synchronizationState + ", areaId=" + areaId + ", errorInfo=" + errorInfo + ", showExtend="
				+ showExtend + ", extend=" + extend + ", updateBy=" + updateBy + ", synchBy=" + synchBy + "]";
	}



	public void allParamNull2Empty() {
		if (id == null) {
			setId("");
		}
		if (deviceId == null) {
			setDeviceId("");
		}
		if (deviceNumber == null) {
			setDeviceNumber("");
		}
		if (bluetooth == null) {
			setBluetooth("");
		}
		if (deviceAddress == null) {
			setDeviceAddress("");
		}
		if (longitude == null) {
			setLongitude("");
		}
		if (latitude == null) {
			setLatitude("");
		}
		if (ipPort == null) {
			setIpPort("");
		}
		if (serialNumber == null) {
			setSerialNumber("");
		}
		if (status == null) {
			setStatus("");
		}
		if (properties == null) {
			setProperties("");
		}
		if (deviceTypeId == null) {
			setDeviceTypeId("");
		}
		if (synchronizationState == null) {
			setSynchronizationState("");
		}
		if (areaId == null) {
			setAreaId("");
		}
		if (errorInfo == null) {
			setErrorInfo("");
		}
		if (showExtend == null) {
			setShowExtend("");
		}
		if (extend == null) {
			setExtend("");
		}
		if (updateBy == null) {
			setUpdateBy("");
		}
		if (synchBy == null) {
			setSynchBy("");
		}

	}

}
