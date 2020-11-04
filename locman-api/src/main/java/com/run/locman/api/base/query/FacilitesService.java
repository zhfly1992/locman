package com.run.locman.api.base.query;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;

/**
 * 
 * @Description:设施service
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface FacilitesService {
	/**
	 * 
	 * @Description:通过分页查询设施信息
	 * @param pageObj
	 * @return
	 */
	public RpcResponse<PageInfo<Map<String, Object>>> getFacilitesInfoByPage(JSONObject pageObj);



	/**
	 * 
	 * @Description:通过设施id查询设施信息
	 * @param id
	 * @param token
	 * @return
	 */
	public RpcResponse<Map<String, Object>> getFacilitesInfoByFacId(String id, String token);



	/**
	 * 
	 * @Description:通过设施code查询设施信息
	 * @param code
	 * @param token
	 * @return
	 */
	public RpcResponse<Map<String, Object>> getFacilitesInfoByfacCode(String code, String token);



	/**
	 * 验证设施序列号唯一性
	 * 
	 * @Description:
	 * @param facilitiesCode
	 * @param accessSecret
	 * @return
	 */
	public RpcResponse<Boolean> checkFacilitiesCode(String facilitiesCode, String accessSecret, String id);



	/**
	 * @Description:获取包含扩展值的信息
	 * @param id
	 * @param accessSecret
	 * @param token
	 * @return
	 */

	RpcResponse<Map<String, Object>> getFacilitesInfo(String id, String accessSecret, String token);



	/**
	 * 
	 * @Description:查询告警设施数量
	 * @param
	 * @return
	 */
	public RpcResponse<Integer> countAlarmFacilities(JSONObject pageObj);



	/**
	 * 
	 * @Description: 根据接入方密钥和设施序列号查询设施信息
	 * @param facilitiesCode
	 *            设施序列号
	 * @param accessSecret
	 *            接入方密钥
	 * @return
	 */
	public RpcResponse<Map<String, Object>> findFacilityInfoByCodeAndAcc(String facilitiesCode, String accessSecret);



	/**
	  * 
	  * @Description: 查询待整治设施列表
	  * @param 
	  * @return
	  */
	
	RpcResponse<PageInfo<Map<String, Object>>> findFacilityRenovatedByPage(JSONObject pageObj);



	/**
	 * 
	 * @Description:根据组织id查询该组织下的设施是否全被屏蔽
	 * @param organizationId
	 * @return true全被屏蔽，false未被全屏蔽
	 */
	//public RpcResponse<Boolean> queryFacDefenseStateByOrganizationId(String organizationId,String accessSecret,String token);

}
