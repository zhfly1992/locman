/*
 * File name: DistributionPowersQueryService.java
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

package com.run.locman.api.query.service;

import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.DistributionPowers;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年9月14日
 */

public interface DistributionPowersQueryService {

	/**
	 * 
	 * @Description: 分页查询分权分域配置
	 * @param accessSecret
	 *            接入方秘钥
	 * @param pageNum
	 *            页码
	 * @param pageSize
	 *            页大小
	 * @param searchParam
	 *            查询参数
	 * @return
	 */
	public RpcResponse<PageInfo<Map<String, Object>>> getDistributionPowersListPage(String accessSecret, int pageNum,
			int pageSize, Map<String, String> searchParam);



	/**
	 * 
	 * @Description: 根据主键ID查询分权分域配置
	 * @param id
	 *            主键ID
	 * @return
	 */
	public RpcResponse<DistributionPowers> getDistributionPowersById(String id);



	/**
	 * 
	 * @Description: 根据设施类型ID查询分权分域配置(当前时间可用)
	 * @param facilityTypeId
	 *            设施类型ID
	 * @return
	 */
	public RpcResponse<DistributionPowers> getDistributionPowersByFacilityTypeId(String facilityTypeId);



	/**
	 * 
	 * @Description: 根据设施类型ID查询分权分域配置（未过期）
	 * @param facilityTypeId
	 *            设施类型ID
	 * @return
	 */
	public RpcResponse<DistributionPowers> getByFacilityTypeId(String facilityTypeId);



	/**
	 * 
	 * @Description:校验当前设施类型当前组织或当前组织岗位是否配有分权分域，用于控制地图设备命令操作判断之一
	 * @param param
	 * @return RpcResponse<Boolean>
	 */
	public RpcResponse<Boolean> getPowersByParam(JSONObject param);



	/**
	 * 
	 * @Description:地图下发命令时查询分权分域所配超时未关时间配置,以便启动定时器
	 * @param josnParam
	 * @return RpcResponse<Map<String, Object>>
	 */
	public RpcResponse<Map<String, Object>> getPowersTime(JSONObject josnParam);

}
