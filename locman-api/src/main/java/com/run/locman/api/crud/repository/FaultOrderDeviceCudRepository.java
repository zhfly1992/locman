package com.run.locman.api.crud.repository;

import com.run.locman.api.entity.FaultOrderDevice;

import java.util.Map;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
public interface FaultOrderDeviceCudRepository extends BaseCrudRepository<FaultOrderDevice, String> {

    /**
     * @Description: 添加故障工單和设备绑定关系
     * @param faultOrderDevice
     * @return
     */
	int addBindDevices(FaultOrderDevice faultOrderDevice);

    /**
     * @Description: 删除故障工單和设备绑定关系
     * @param map
     * @return
     */
	int delBindDevices(Map<String, Object>map);
}
