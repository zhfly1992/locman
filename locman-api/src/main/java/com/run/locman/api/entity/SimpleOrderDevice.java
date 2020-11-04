package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月05日
 */
public class SimpleOrderDevice implements Serializable {

    private static final long	serialVersionUID	=1L;

    private String id;
    private String simpleOrderId;
    private String deviceId;

    public SimpleOrderDevice() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSimpleOrderId() {
        return simpleOrderId;
    }

    public void setSimpleOrderId(String simpleOrderId) {
        this.simpleOrderId = simpleOrderId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "SimpleOrderDevice{" +
                "id='" + id + '\'' +
                ", simpleOrderId='" + simpleOrderId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
