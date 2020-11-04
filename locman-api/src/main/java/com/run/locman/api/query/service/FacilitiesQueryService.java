package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.Facilities;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年08月08日
 */
public interface FacilitiesQueryService {
	/**
	 * @Description: GIS地图展示设施数据
	 * @param facilitiesTypeId
	 *            设施类型ID
	 * @param searchKey
	 *            设施序列号/设施编号
	 * @param organizationId
	 *            组织ID
	 * @param address
	 *            设施地址
	 * @param token
	 *            token验证
	 * @return 设施集合
	 */
	RpcResponse<Map<String, Object>> queryMapFacilities(JSONObject paramsObject);
	
	
	/**
	 * 
	* @Description:刷新关键字时间
	* @return
	 */
	RpcResponse<String> updateKeyTime(String accessSecret);


	/**
	 * 
	 * <method description> APP GIS地图展示设施数据
	 * 
	 * @author liaodan
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param scopeValue
	 * @param accessSecret
	 *            接入方秘钥
	 * @param facilitiesTypeId
	 *            设施类型ID
	 * @param facilitiesCode
	 *            设施序列号
	 * @param organizationId
	 *            组织ID
	 * @param address
	 *            设施地址
	 * @return 设施集合
	 */
	RpcResponse<List<Map<String, Object>>> queryMapFacilitiesToApp(String longitude, String latitude,
			Integer scopeValue, String accessSecret, String facilitiesTypeId, String facilitiesCode,
			String organizationId, String address);



	/**
	 * 按唯一编号查找
	 * 
	 * @param id
	 * @return
	 */
	RpcResponse<Facilities> findById(String id);



	/**
	 * 查询设施最严重的告警等级
	 * 
	 * @param facilitiesId
	 * @return
	 */
	RpcResponse<String> queryFacilityWorstAlarm(String facilitiesId);



	/**
	 * 
	 * @Description:根据设施类型及关键词查询设施信息
	 * @param paramInfo
	 * @return RpcResponse<List<Map>>
	 * @throws Exception
	 * @author 廖丹
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getFacilityListBySelectKeyAndTypeName(JSONObject paramInfo)
			throws Exception;



	/**
	 * 
	 * 根据区域id查询设施所属区域
	 * 
	 * @param id
	 * @return
	 * @author 廖丹
	 * @throws Exception
	 */
	RpcResponse<List<String>> getAreaById(String id) throws Exception;



	/**
	 * 
	 * 根据id查询设施状态
	 * 
	 * @param list<id>
	 *            id数组
	 * @return
	 * @author 张贺
	 * @throws Exception
	 */
	RpcResponse<Boolean> getFacilityMangerStateById(List<String> idlist);



	/**
	 * 
	 * @Description:查询该组织id及子组织该设施序列号该接入方的设施(APP用)
	 * @param jsonParam
	 *            {"facilitiesCode":"","organizationId":"","accessSecret":""}
	 * @return RpcResponse<List<Facilities>>
	 */
	RpcResponse<List<Facilities>> getFacilityByParam(JSONObject jsonParam);



	/**
	 * 
	 * @Description:解析sheet返回list 两个list 一个解析失败的list，一个是插入mysql的list
	 * @param sheet
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> analysisInsertSheetFile(List<Facilities> sheet);



	/**
	 * 
	 * 
	 * @Description:工具 查询该接入方下该设施类型设施id集合
	 * @param jsonParam
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getFacilityIdList(JSONObject jsonParam);



	/**
	 * 
	 * 
	 * @Description:根据appTag查询设备id集合
	 * @param jsonParam
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getDeviceIdList(JSONObject jsonParam);



	/**
	 * 
	 * @Description: 查询设施绑定的设备数据( 设备id 设备名称 设备类型id 设备类型名称)
	 * @param facilityId
	 *            设施id
	 * @param accessSecret
	 *            接入方密钥
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getBoundDeviceInfo(String facilityId, String accessSecret);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	RpcResponse<List<Map<String, Object>>> findFacitiesByIds(List<String> idList);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	RpcResponse<List<Map<String, Object>>> getBoundDevicesInfoByfacIds(List<String> idList, String accessSecret);



	/**
	 * 
	 * @Description: 成华区地图展示需求，统计一个街道上的污水井，雨水井（排除雨水箅子），雨水箅子数量
	 *               其中污水井，雨水井是设施类型，在设施的extend字段中，若含有"checkWellFuncType":"进水篦"，则视为雨水箅子
	 *               organizationId为组织id,对应的是街道，具体逻辑问相关人员
	 * @param accessSecret
	 * @param organizationId
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> countFacByTypeAndOrg(String accessSecret, String organizationId);



	/**
	  * 
	  * @Description: 按街道办统计各设施类型数量
	  * @param 
	  * @return
	  */
	
	RpcResponse<List<Map<String, Object>>> countFacNumByStreet(String accessSecret, String token);



	/**
	  * 
	  * @Description: 地图设施经纬度查询
	  * @param 
	  * @return
	  */
	
	RpcResponse<List<Map<String, Object>>> findFacilityLngLat(String accessSecret);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<JSONObject> findFacInfoByDeviceId(String deviceId);



}
