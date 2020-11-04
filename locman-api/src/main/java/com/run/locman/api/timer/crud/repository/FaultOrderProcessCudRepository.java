package com.run.locman.api.timer.crud.repository;

import com.run.locman.api.crud.repository.BaseCrudRepository;
import com.run.locman.api.entity.FaultOrderProcess;

import java.util.Map;

/**
 * @Description:
 * @author: 郭飞龙
 * @version: 1.0, 2018年7月5日
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
	  * @Description:查询获取故障工单流水号
	  * @param accessSecret 密钥
	  * @return 流水号
	  */
	String querySerialNumber(String accessSecret) ;
	
	/**
	  * 
	  * @Description:查询故障工单的故障描述
	  * @param id 故障工单id
	  * @return
	  */
	String queryMark(String id) ;
	
}
