package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月05日
 */
public class FaultOrderDevice implements Serializable{

    private static final long	serialVersionUID	=1L;

    private String id;
    private String deviceId;
    private String faultOrderId;

    public FaultOrderDevice() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFaultOrderId() {
        return faultOrderId;
    }

    public void setFaultOrderId(String faultOrderId) {
        this.faultOrderId = faultOrderId;
    }

    @Override
    public String toString() {
        return "FaultOrderDevice{" +
                "id='" + id + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", faultOrderId='" + faultOrderId + '\'' +
                '}';
    }
}
