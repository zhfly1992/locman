package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.run.locman.api.entity.AlarmInfo;

/**
 * 
 * @Description:告警信息crud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface AlarmInfoCrudRepository extends BaseCrudRepository<AlarmInfo, String> {

	/**
	 * 查询当前最大的告警流水号
	 * 
	 * @Description:
	 * @param accessSecret
	 * @return
	 */
	long getSerialNumBySecrete(String accessSecret);



	/**
	 * 
	 * @Description:修改一般告警信息的忽略状态
	 * @param map
	 * @return int
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	int updateTheDel(Map map) throws Exception;



	/**
	 *
	 * @Description:通过组合条件修改一般告警信息的状态（除开忽略状态）
	 * @param map
	 * @return int
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	int updateTheDelByCondition(Map map) throws Exception;



	/**
	 * 
	 * @Description: 处理告警
	 * @param map
	 * @return
	 */
	int disposeAlarmInfo(Map<String, Object> map);



	AlarmInfo getAlarmInfoById(String alarmId);



	/**
	 * 
	 * @Description:告警完工审核拒绝时，将告警信息的状态改为0(已生成工单)
	 * @param list
	 * @return
	 */
	int updateTheDelByAlarmIds(List<String> list);



	/**
	 * 
	 * @Description: 根据告警信息id查询告警工单信息
	 * @param alarmId
	 * @return
	 */
	Map<String, Object> getAlarmOrderInfoByAlarmId(String alarmId);



	/**
	 * 
	 * @Description:成华区需求，将相应时间后的告警信息状态置为已处理
	 * @param deviceId
	 * @param time
	 * @return
	 */
	int dealAlarmInfoState(@Param("deviceId") String deviceId);



	/**
	 * 
	 * @Description:成华区需求,获取告警信息相关的告警工单号
	 * @param deviceId
	 * @param time
	 * @return
	 */
	String getAlarmOrderId(@Param("deviceId") String deviceId);



	/**
	 * 
	 * @Description:成华区需求，将相应时间后的告警工单状态置为已处理
	 * @param deviceId
	 * @param time
	 * @return
	 */
	int dealAlarmOrder(@Param("alarmOrderId") String alarmOrderId);



	/**
	 * 
	 * @Description:成华区需求，将告警对应的短信记录状态置为2(取消发送)
	 * @param deviceId
	 * @param time
	 * @return
	 */
	int updateSmsRecordState(@Param("serialNum") List<String> serialNum);



	/**
	 * 
	 * @Description:成华区需求，获取需要取消发送短信的告警流水号
	 * @param deviceId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<String> getAlarmSerialNumForCancelSend(@Param("deviceId") String deviceId);



	/**
	 * 
	 * @Description:获取该工单关联的所有告警id
	 * @param alarmOrderId
	 * @return
	 */
	List<String> getAlarmIdByAlarmOrderId(@Param("alarmOrderId") String alarmOrderId);
	
	/**
	 * 
	* @Description:根据告警工单id查询设施组织ID
	* @param alarmOrderId
	* @return
	 */
	Map<String,Object> getFacInfoByAlarmOrderId(@Param("alarmOrderId") String alarmOrderId);



	/**
	  * 
	  * @Description:根据设备id和接入方密钥获取该设备所有未完成处理的告警信息类型
	  * @param 
	  * @return
	  */
	
	List<String> getDistinctAlarmDescByDeviceId(@Param("deviceId") String deviceId, @Param("accessSecret") String accessSecret);



	/**
	  * 
	  * @Description: 根据设施序列号查询设施是否在启用的重保名单之中
	  * @param 
	  * @return
	  */
	
	Map<String,Object> getPersonInCharge(@Param("facilitiesCode") String facilitiesCode, @Param("accessSecret") String accessSecret);
}