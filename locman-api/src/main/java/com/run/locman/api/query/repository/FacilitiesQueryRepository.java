package com.run.locman.api.query.repository;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.Facilities;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年08月08日
 */
public interface FacilitiesQueryRepository extends BaseQueryRepository<Facilities, String> {

	/**
	 * @Description: GIS地图展示设施数据
	 * @return 设施集合
	 */
	List<Map<String, Object>> queryMapFacilities(@Param("accessSecret") String accessSecret,
			@Param("facilitiesTypeId") String facilitiesTypeId, @Param("searchKey") String searchKey,
			@Param("organizationId") List<String> organizationId, @Param("completeAddress") String completeAddress,
			@Param("alarmWorstLevel") String alarmWorstLevel, @Param("defenseState") String defenseState);


	/**
	 * 
	* @Description:带条件查询设施id，list
	* @param accessSecret
	* @param facilitiesTypeId
	* @param searchKey
	* @param organizationId
	* @param completeAddress
	* @param alarmWorstLevel
	* @param defenseState
	* @return
	 */
	List<String> queryMapFacilitiesId(@Param("accessSecret") String accessSecret,
			@Param("facilitiesTypeId") String facilitiesTypeId, @Param("searchKey") String searchKey,
			@Param("organizationId") List<String> organizationId, @Param("completeAddress") String completeAddress,
			@Param("alarmWorstLevel") String alarmWorstLevel, @Param("defenseState") String defenseState);
	/**
	 *
	 * @Description:通过设施id查询设施信息
	 * @param
	 * @return
	 */
	public List<Map<String, Object>> findFacByPage(Map<String, Object> map);



	/**
	 *
	 * @Description:通过设施id查询设施信息
	 * @param id
	 * @return
	 */
	public Map<String, Object> findByFacId(String id);



	/**
	 *
	 * @Description:通过设施code查询设施信息
	 * @param code
	 * @return
	 */
	public Map<String, Object> findByFacCode(String code);



	public int checkFacilitiesCode(Map<String, Object> queryMap);



	/**
	 *
	 * APP GIS地图数据展示
	 * 
	 * @author liaodan
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> queryMapFacilitiesToApp(Map<String, Object> map);



	/**
	 * 根据设施id查询设施最严重的告警等级
	 * 
	 * @param queryMap
	 *            设施查询参数
	 * @return 告警等级
	 */
	String queryFacilityWorstAlarm(Map<String, Object> queryMap);



	/**
	 * 根据设施类型及关键词查询设施信息 <method description>
	 *
	 * @param map
	 *            设施查询参数
	 * @return 设施集合
	 */
	public List<Map<String, Object>> getFacilityListBySelectKeyAndTypeName(Map<String, Object> map);



	/**
	 * 根据区域id查询设施所属区域 <method description>
	 *
	 * @param id
	 * @return
	 */
	public List<String> getAreaByIds(String id);



	/**
	 * 根据区域id查询设施的状态
	 *
	 * @param id
	 * @return
	 */

	public List<String> getFacilityMangerStateById(List<String> idlist);



	/**
	 * 
	 * @Description:查询该组织id及子组织该设施序列号该接入方的设施(APP用)
	 * @param jsonParam
	 * @return List<Facilities>
	 */
	public List<Facilities> getFacilityByParam(JSONObject jsonParam);



	/**
	 * 
	 * 
	 * @Description:工具 查询该接入方下该设施类型设施id集合
	 * @param jsonParam
	 * @return
	 */
	public List<Map<String, Object>> getFacilityIdList(JSONObject jsonParam);



	/**
	 * 
	 * 
	 * @Description:根据appTag查询设备id集合
	 * @param jsonParam
	 * @return
	 */
	public List<Map<String, Object>> getDeviceIdList(JSONObject jsonParam);



	/**
	 * 
	 * @Description:通过设施名称以及基础设施名称查询
	 * @param facilitiesNameMap
	 * @return
	 */
	public String findFacilitiesTypeId(Map<String, Object> facilitiesNameMap);



	/**
	 * 
	 * @Description:查询设备id是否存在
	 * @param deviceId
	 * @return
	 */
	public int checkDeviceId(Map<String, String> deviceMap);



	/**
	 * 
	 * @Description:校验设备是否已绑定设施
	 * @param deviceId
	 * @return
	 */
	public int checkDeviceOrFac(String deviceId);



	/**
	 * 
	 * @Description:查询告警的设施数量
	 * @param
	 * @return
	 */
	public int countAlarmFacilities(JSONObject jsonParam);



	/**
	 * 
	 * @Description: 查询设施绑定的设备数据( 设备id 设备名称 设备类型id 设备类型名称)
	 * @param facilityId 设施id
	 * @param accessSecret 接入方密钥
	 * @return 
	 */
	
	List<Map<String, Object>> getBoundDeviceInfo(@Param("facilityId")String facilityId, @Param("accessSecret")String accessSecret);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<Map<String, Object>> findFacitiesByIds(List<String> idList);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<Map<String, Object>> getBoundDevicesInfoByfacIds(@Param("ids")List<String> idList, @Param("accessSecret")String accessSecret);
	
	/**
	 * 
	* @Description: 成华区地图展示需求，统计一个街道上的污水井，雨水井（排除雨水箅子），雨水箅子数量，具体看service中的介绍
	* @param organizationId
	* @param accessSecret
	* @return
	 */
	List<Map<String, Object>> countFacByTypeAndOrg(@Param("organizationId")String organizationId, @Param("accessSecret")String accessSecret);
	
	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	List<Map<String, Object>> countFacNumByStreet(@Param("orgIds")List<String> orgIds, @Param("accessSecret")String accessSecret);



	/**
	  * 
	  * @Description: 根据接入方密钥和设施序列号查询设施信息
	  * @param 
	  * @return
	  */
	
	Map<String, Object> findFacilityInfoByCodeAndAcc(@Param("facilitiesCode")String facilitiesCode, @Param("accessSecret")String accessSecret);
	
	
	List<Map<String, Object>> findFacilityLngLat(@Param("accessSecret")String accessSecret);

	/**
	 *
	 * @Description:通过设施id查询设施信息
	 * @param
	 * @return
	 */
	public List<Map<String, Object>> findFacilityRenovatedByPage(Map<String, Object> map);



	/**
	  * 
	  * @Description: 根据设备id查询设备接入方密钥及绑定的设施id
	  * @param 
	  * @return
	  */
	
	JSONObject findFacInfoByDeviceId(String deviceId);
	/**
	 * 
	* @Description:根据组织id查询有绑定设备的设施Id
	* @param map
	* @return
	 */
	List<String> findFacIdByOrgId(Map<String,Object> map);


}
