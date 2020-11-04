/*
 * File name: BalanceSwitchPowersQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月7日 ... ... ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.entity.BalanceSwitchPowers;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月7日
 */

public interface BalanceSwitchPowersQueryRepository {
	/**
	 * 
	 * @Description:分页查询平衡告警开关权限配置
	 * @param findParam
	 * @return List<BalanceSwitchPowers>
	 */
	public List<BalanceSwitchPowers> getBalanceSwitchPowersList(JSONObject findParam);



	/**
	 * 
	 * @Description:根据id查询平衡告警开关权限配置
	 * @param id
	 * @return BalanceSwitchPowers
	 */
	public BalanceSwitchPowers getBalanceSwitchPowersById(String id);



	/**
	 * 
	 * @Description:校验该设施类型在有效期内是否配有启用的平衡告警权限
	 * @param facilityTypeId
	 * @return int
	 */
	public List<BalanceSwitchPowers> checkByFacilityTypeId(String facilityTypeId);



	/**
	 * 
	 * @Description:校验当前组织及下级组织或当前组织岗位及下级组织岗位在当当前时间是否配有平衡告警开关权限。
	 * @param checkParam
	 *            {"facilityTypeId":"","organizationId":["",""],"postId":"","accessSecret":""}
	 * @return checkBalanceSwitchPowers
	 */
	public List<BalanceSwitchPowers> checkBalanceSwitchPowers(JSONObject checkParam);
}
