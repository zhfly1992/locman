package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月11日
 */
public class DefinedAlarmRuleDevice implements Serializable{

    private static final long	serialVersionUID	= 1L;

    private String				id;
    private String				deviceId;
    private String				alarmRuleId;

    public DefinedAlarmRuleDevice() {
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

    public String getAlarmRuleId() {
        return alarmRuleId;
    }

    public void setAlarmRuleId(String alarmRuleId) {
        this.alarmRuleId = alarmRuleId;
    }

    @Override
    public String toString() {
        return "DefinedAlarmRuleDevice{" +
                "id='" + id + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", alarmRuleId='" + alarmRuleId + '\'' +
                '}';
    }
}
