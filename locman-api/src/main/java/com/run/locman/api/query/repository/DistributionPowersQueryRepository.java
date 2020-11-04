/*
 * File name: DistributionPowersQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年9月14日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.DistributionPowers;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */

public interface DistributionPowersQueryRepository extends BaseQueryRepository<DistributionPowers, String> {

	/**
	 * 
	 * @Description: 分页查询分权分域配置
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryDistributionPowersListPage(Map<String, String> params);



	/**
	 * @Description:根据设施类型id查询分权分域配置（当前时间可用）
	 * @param facilityTypeId
	 *            设施类型id
	 * @return
	 */

	public DistributionPowers getDistributionPowersByFacilityTypeId(String facilityTypeId);



	public List<DistributionPowers> findByOrganizationIdAndPostId(Map<String, Object> map);



	/**
	 * @Description:根据设施类型id查询分权分域配置（未过期）
	 * @param facilityTypeId
	 *            设施类型id
	 * @return
	 */

	public DistributionPowers getByFacilityTypeId(String facilityTypeId);



	/**
	 * 
	 * @Description:校验当前设施类型当前组织或当前组织岗位是否配有分权分域，用于控制地图设备命令操作判断之一
	 * @param param
	 * @return RpcResponse<Boolean>
	 */
	public List<Map<String, Object>> getPowersByParam(Map<String, Object> param);



	/**
	 * 
	 * @Description:地图下发命令时查询分权分域所配超时未关时间配置,以便启动定时器
	 * @param deviceId
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getPowersTime(JSONObject jsonParam);

}
