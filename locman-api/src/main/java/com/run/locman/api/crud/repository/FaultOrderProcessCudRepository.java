package com.run.locman.api.crud.repository;

import com.run.locman.api.entity.FaultOrderProcess;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月05日
 */
public interface FaultOrderProcessCudRepository extends BaseCrudRepository<FaultOrderProcess, String> {

	/**
	 *
	 * @Description:新增故障工单
	 * @param params
	 * @return
	 * @throws Exception
	 */
	int addFaultOrder(Map<String, Object> params) throws Exception;



	/**
	 *
	 * @Description:修改故障工单
	 * @param params
	 * @return
	 * @throws Exception
	 */
	int updateFaultOrder(Map<String, Object> params) throws Exception;



	/**
	 * 
	 * @Description:查询告警规则名称
	 * @param faultOrderId
	 * @return
	 * @throws Exception
	 */
	List<String> findAlarmDescByFaultOrderId(String faultOrderId) throws Exception;



	/**
	 * 
	 * @Description:通过故障工单id查询设备id
	 * @param id
	 * @return
	 */
	List<String> findDeviceIdByFaultOrderId(String faultOrderId);

}
