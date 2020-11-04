
package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.dto.CountAlarmOrderDto;
import com.run.locman.api.entity.AlarmCountDetails;
import com.run.locman.api.entity.AlarmOrder;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年11月1日
 */
@SuppressWarnings("rawtypes")
public interface AlarmOrderQueryRepository extends BaseQueryRepository<AlarmOrder, String> {
	/**
	 * 
	 * @Description:通过接入方密钥查询告警工单信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> getAlarmOrderByAccessSercret(Map<String, Object> map);



	/**
	 * 
	 * @Description:通过告警工单id告警工单信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> getAlarmOrderById(Map<String, Object> map);



	/**
	 * 
	 * @Description:查询下拉框状态
	 * @return
	 */
	List<Map<String, Object>> getSelectState(@Param("type") Integer type);



	/**
	 * 
	 * @Description:通过工单id查询工单信息
	 * 
	 * @param orderId
	 * @return
	 */
	Map<String, Object> getAlarmOrderInfoById(@Param("orderId") String orderId);



	/**
	 *
	 * @Description:通过故障工单id查询告警工单信息（告警转故障的工单）
	 *
	 * @param faultOrderId
	 * @return
	 */
	Map<String, Object> getAlarmOrderInfoByFaultOrderId(@Param("faultOrderId") String faultOrderId);



	/**
	 * @Description:获取核实的告警工单数
	 * @param ids
	 * @return
	 */
	Integer getalarmOrderCount(List<String> ids);



	/**
	 * notClaimAlarmOrder
	 * 
	 * @Description: 通过接入方查询未被接受的工单(告警)
	 * @param notClaimOrderQueryMap
	 * @return
	 */

	List<Map<String, Object>> notClaimAlarmOrder(Map notClaimOrderQueryMap);



	/**
	 * 
	 * @Description:通过接入方查询未被接受的告警工单总数
	 * @param
	 * @return
	 */
	int getNotClaimAlarmOrderTotal(Map notClaimOrderQueryMap);



	/**
	 * 
	 * @Description: 告警统计
	 * @param orderQueryMap
	 * @return
	 */
	List<Map> alarmOrderCountInfo(Map<String, Object> orderQueryMap);



	/**
	 * 
	 * @Description:告警统计详情根据区域搜索
	 * @param alarmCountDetails
	 * @return
	 */
	List<Map> alarmOrderDetailsInfo(AlarmCountDetails alarmCountDetails);



	/**
	 * 
	 * @Description: 统计所有告警工单
	 * @param
	 * @return
	 */

	List<CountAlarmOrderDto> countAllAlarmOrder(Map<String, Object> map);



	/**
	 * 
	 * @Description: 获取接入方所有有设施的组织ID
	 * @param
	 * @return
	 */
	List<String> getAllOrgIds(String accessSecret);



	/**
	 * 
	 * @Description: 查询工单及其对应的所有告警信息
	 * @param
	 * @return
	 */
	List<Map<String, Object>> getAlarmOrderAndAllAlarmInfo(@Param("orderId") String orderId);



	/**
	 * 
	 * @Description: 根据告警信息id查询告警工单信息
	 * @param alarmId
	 * @return
	 */
	Map<String, Object> getAlarmOrderInfoByAlarmId(String alarmId);



	/**
	 * 
	 * @Description: 根据告警工单id查询告警工单信息
	 * @param
	 * @return
	 */
	Map<String, Object> getChangedAlarmOrderInfoByOrderId(String alarmOrderId);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */
	List<Map<String, Object>> countAlarmOrderByOrg(Map<String, Object> map);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	Map<String, Object> getAlarmOrderAndFacInfo(String alarmOrderId);



	/**
	 * 
	 * @Description:
	 * @param alarmOrderId
	 * @return
	 */
	List<Map<String, Object>> getAlarmIdByAlarmOrderId(String alarmOrderId);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */
	List<Map<String, Object>> getHiddenTroubleType();




}
