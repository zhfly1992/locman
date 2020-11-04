package com.run.locman.api.crud.repository;

import com.run.locman.api.entity.DefinedAlarmRuleDevice;

import java.util.Map;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月11日
 */
public interface DefinedAlarmRuleDeviceCudRepository {

    /**
     * @Description: 添加规则引擎和设备绑定关系
     * @param definedAlarmRuleDevice 规则和设备中间表
     * @return
     */
    int addBindDevices(DefinedAlarmRuleDevice definedAlarmRuleDevice);

    /**
     * @Description: 删除规则引擎和设备绑定关系
     * @param map
     * @return
     */
    int delBindDevices(Map<String, Object> map);
}
