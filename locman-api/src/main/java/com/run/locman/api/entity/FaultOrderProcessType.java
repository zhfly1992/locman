package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月05日
 */
public class FaultOrderProcessType implements Serializable{

    private static final long	serialVersionUID	=1L;

    private String id;
    private String name;
    private String accessSecret;

    public FaultOrderProcessType() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    @Override
    public String toString() {
        return "FaultOrderProcessType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", accessSecret='" + accessSecret + '\'' +
                '}';
    }
}
