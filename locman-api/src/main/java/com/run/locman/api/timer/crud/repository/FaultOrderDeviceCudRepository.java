package com.run.locman.api.timer.crud.repository;

import com.run.locman.api.crud.repository.BaseCrudRepository;
import com.run.locman.api.dto.FaultOrderDetetionDto;
import com.run.locman.api.entity.FaultOrderDevice;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: 郭飞龙
 * @version: 1.0, 2018年7月5日
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
	
	/**
	  * 
	  * @Description:根据设备id查询超时未上报故障工单信息
	  * @param 
	  * @return
	  */
	List<FaultOrderDetetionDto> getOrderInfo(List<String> deviceIds);
}
