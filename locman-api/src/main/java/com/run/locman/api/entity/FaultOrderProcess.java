package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月05日
 */
public class FaultOrderProcess implements Serializable {

    private static final long	serialVersionUID	=1L;

    private String				id;
    private String				orderName;
    private String				serialNumber;
    private String				createTime;
    private String				createBy;
    private Integer				faultType;
    private String				mark;
    private String				accessSecret;
    private String				phone;
    private Integer				deviceCount;
    private String				manager;
    private String				processState;
    private String				processId;
    private String				userId;
    private String				updateTime;
    private String				orderImg;
    private String				factoryId;
    private String				faultProcessType;

    public FaultOrderProcess() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }


    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getFaultType() {
        return faultType;
    }

    public void setFaultType(Integer faultType) {
        this.faultType = faultType;
    }

    public Integer getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(Integer deviceCount) {
        this.deviceCount = deviceCount;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getProcessState() {
        return processState;
    }

    public void setProcessState(String processState) {
        this.processState = processState;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOrderImg() {
        return orderImg;
    }

    public void setOrderImg(String orderImg) {
        this.orderImg = orderImg;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getFaultProcessType() {
        return faultProcessType;
    }

    public void setFaultProcessType(String faultProcessType) {
        this.faultProcessType = faultProcessType;
    }

    @Override
    public String toString() {
        return "FaultOrderProcess{" +
                "id='" + id + '\'' +
                ", orderName='" + orderName + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", createTime='" + createTime + '\'' +
                ", createBy='" + createBy + '\'' +
                ", faultType=" + faultType +
                ", mark='" + mark + '\'' +
                ", accessSecret='" + accessSecret + '\'' +
                ", phone='" + phone + '\'' +
                ", deviceCount=" + deviceCount +
                ", manager='" + manager + '\'' +
                ", processState='" + processState + '\'' +
                ", processId='" + processId + '\'' +
                ", userId='" + userId + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", orderImg='" + orderImg + '\'' +
                ", factoryId='" + factoryId + '\'' +
                ", faultProcessType='" + faultProcessType + '\'' +
                '}';
    }
}

