package com.run.locman.api.query.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.FacilitiesType;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年08月09日
 */
public interface FacilitiesTypeQueryService {

	/**
	 * @Description: 查询所有设施类型(不分页)
	 * @return 设施类型集合
	 */
	RpcResponse<List<FacilitiesType>> findAllFacilities(String accessSecret);



	/**
	 * 
	 * @Description: 根据设施类型ID查询设施相关信息
	 * @param id
	 *            设施类型ID
	 * @return
	 */
	public RpcResponse<FacilitiesType> queryFacilitiesById(String id);



	/**
	 * 
	 * @Description: 分页查询所有设施类型数据
	 * @param accessSecret
	 *            公司ID
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            页大小
	 * @param param
	 *            查询参数 {"searchKey":""}
	 * @return
	 */
	public RpcResponse<PageInfo<FacilitiesType>> getFacilitiesPage(String accessSecret, int pageNo, int pageSize,
			Map<String, String> param);



	/**
	 * 
	 * @Description:校验该接入方下该设施类型名称是否重复
	 * @param facilitiesTypeName
	 * @param accessSecret
	 * @return RpcResponse<Boolean>
	 */
	public RpcResponse<Boolean> checkFacilitiesTypeName(String facilitiesTypeName, String accessSecret,String facilityTypeId);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public RpcResponse<List<Map<String, String>>> findAllFacilitiesTypeAndNum(String accessSecret);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	RpcResponse<List<Map<String, Object>>> findAllFacTypeAndDeviceTypeNum(String accessSecret);

}
