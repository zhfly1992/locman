package com.run.locman.api.crud.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.entity.Facilities;

/**
 * 
 * @Description:设施crud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface FacilitiesCrudRepository extends BaseCrudRepository<Facilities, String> {

	/**
	 * 
	 * 
	 * @Description:工具 批量绑定设施和设备的关系
	 * @param list
	 * @return
	 */
	public int addFacilitiesAndDeviceId(List<Map<String, Object>> list);



	/**
	 * 
	 * @Description:根据设备id查询设施详情
	 * @param deviceId
	 * @return
	 */
	public Facilities getFacByDeviceId(String deviceId);



	/**
	 * 
	 * @Description: 批量插入设施信息
	 * @param sheet
	 * @return
	 */
	public int batchInsertFacilities(List<Facilities> sheet);



	/**
	 * 
	 * @Description:批量保存设施与设备信息
	 * @param sheet
	 * @return
	 */
	public int batchInsertFacAndDevice(List<Facilities> sheet);



	/**
	 * 
	 * @Description:批量修改设施的屏蔽状态
	 * @param organIds
	 * @param facIds
	 * @return
	 */
	public int updateFacilitiesDefenseState(@Param("facIds") List<String> facIds,
			@Param("organizationId") List<String> organizationId, @Param("denfenseState") String denfenseState,
			@Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @Description:获取需要修改的告警工单列表
	 * @param facIds
	 * @param organizationId
	 * @param accessSecret
	 * @return
	 */
	public List<String> getAlarmOrderId(@Param("facIds") List<String> facIds,
			@Param("organizationId") List<String> organizationId, @Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @Description:修改告警工单状态
	 * @param alarmIdList
	 * @return
	 */
	public int updateAlarmOrderState(@Param("alarmOrderIdList") List<String> alarmIdList);



	/**
	 * 
	 * @Description:修改告警信息
	 * @param facIds
	 * @param organizationId
	 * @param accessSecret
	 * @return
	 */
	public int updateAlarmInfoState(@Param("facIds") List<String> facIds,
			@Param("organizationId") List<String> organizationId, @Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @param hiddenTroubleDesc 
	 * @Description: 修改设施为待整治状态
	 * @param
	 * @return
	 */

	public int updateFacDefenseStateRenovation(@Param("facilityId") String facilityId,
			@Param("presentPic") String presentPic, @Param("defenseState") String defenseState,
			@Param("accessSecret") String accessSecret, @Param("hiddenTroubleDesc")String hiddenTroubleDesc);
	
	
	
	public int updateAlarmOrderPresentPic(@Param("alarmOrderId") String alarmOrderId,
			@Param("alarmOrderPresentPic") String alarmOrderPresentPic, @Param("userId") String userId);
	
	String  findIdByCodeAndAcc(Map<String,Object> map);
	/**
	 * 
	* @Description:根据设施Id查询地图需要缓存的信息
	* @param facId
	* @return
	 */
	Map<String,Object> findInfoToCache(Map<String,Object> map);
	/**
	 * 
	* @Description:批量设施Id查询地图需要缓存的信息
	* @param facIds
	* @param accessSecret
	* @return
	 */
	List<Map<String,Object>> findBatchInfoToCache(@Param("facIds") List<String> facIds,@Param("accessSecret") String accessSecret);
}
