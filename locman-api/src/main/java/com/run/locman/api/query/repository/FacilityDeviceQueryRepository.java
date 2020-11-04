package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;


import com.run.locman.api.entity.FacilityDevice;

/**
 * 
 * @Description: 设施与设备关系Repository
 * @author: guolei
 * @version: 1.0, 2017年9月15日
 */

public interface FacilityDeviceQueryRepository extends BaseQueryRepository<FacilityDevice, String> {

	/**
	 * 
	 * @Description:查询设施绑定情况
	 * @param facility
	 * @return
	 */
	public List<String> queryFacilityBindingState(String facilityId);

	/**
	 * 
	 * @Description:查询设备绑定情况
	 * @param facility
	 * @return
	 */
	public String queryDeviceBindingState(String deviceId);

	/**
	 * 
	 * @Description:根据id查询设施
	 * @param facility
	 * @return
	 */
	public Map<String, Object> queryFacilityById(String facilityId);

	/**
	 * 
	 * @Description:查询设施绑定列表（分页）
	 * @param facility
	 * @return
	 */
	public List<Map<String, Object>> queryFacilityBindListByPage(Map<String, Object> params);

	/**
	 * 
	 * @Description:查询所有已绑定设备的id
	 * @return
	 */
	public List<String> queryAllBoundDeviceId();

	/**
	 * 
	 * @Description:查询所有已绑定设备信息
	 * @return
	 */
	public List<String> queryAllDeviceInfo(Map<String, String> params);

	/**
	 * 根据接入方秘钥查询所有已绑定的设施信息
	 * 
	 * @Description:
	 * @param accessSecret
	 *            接入方秘钥
	 * @return
	 */
	public List<Map<String, Object>> findAllBindData(String accessSecret);

	/**
	 * 
	 * @Description: 通过设施id数组查询这些设施下的所有设备
	 * @param facIdsMap
	 * @return
	 * 
	 */
	public List<String> findDeviceByFacIds(Map<String, Object> facIdsMap);
	
	public List<Map<String,Object>> findOnlineQuery();

}
