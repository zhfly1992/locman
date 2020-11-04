/*
 * File name: FocusSecurityCrudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 钟滨远 2020年4月26日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.entity.FocusSecurity;
import com.run.locman.api.entity.FocusSecurityAndFac;

/**
 * @Description:
 * @author: 钟滨远
 * @version: 1.0, 2020年4月26日
 */

public interface FocusSecurityCrudRepository {

	/**
	 * 
	 * @Description:插入保障表
	 * @param focusSecurity
	 * @return
	 */

	Integer addFocusSecurity(FocusSecurity focusSecurity);



	/**
	 * 
	 * @Description:插入中间表
	 * @return
	 */

	Integer addFocusSecurityAndFac(List<FocusSecurityAndFac> list);



	/**
	 * 
	 * @Description:根据组织ID查询绑定的设备ID和设施ID
	 * @param map
	 * @return
	 */

	List<Map<String, Object>> findFacIdByOrgIds(Map<String, Object> map);



	/**
	 * 
	 * @Description:启用停用
	 * @param map
	 * @return
	 */

	Integer enabledFocusSecurity(Map<String, Object> map);
	
	/**
	 * 
	* @Description:根据重保ID查询设施List
	* @param securityId
	* @return
	 */
	List<String> findFacIdListBySecurityId(String securityId);



	List<Map<String, Object>> findAllOrgId();
	
	/**
	 * 
	* @Description:根据设施id获取设备信息
	* @return
	 */
	List<Map<String, Object>> getDeviceInfoByFacId(@Param("facId")String facId);
	
	/**
	 * 
	* @Description:更改IotReceivingStatus
	* @param facId
	* @param status
	* @return
	 */
	int updateIotReceivingStatus(@Param("facId") String facId,@Param("commandStatus") String commandStatus,@Param("securityId") String securityId);
	
	/**
	 * 
	* @Description:重保时间结束，更改工单状态
	* @return
	 */
	int querySecurityFacilitiesOrders();
	/**
	 * 
	* @Description:查询当前时间点需要结束重保的设施Id,重保Id
	* @return
	 */
	
	List<Map<String,Object>>  querySecurityFacIdList();
	
	int updateEndStatus(@Param("facId") String facId,@Param("status") String status,@Param("securityId") String securityId);

}
