package com.run.locman.api.crud.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.Facilities;

/**
 * 
 * @Description:设施crud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface FacilitiesCrudService {

	/**
	 * 
	 * @Description:新增设施
	 * @param facilities
	 * @return
	 */
	RpcResponse<Facilities> addFacilities(Facilities facilities);



	/**
	 * 
	 * @Description:修改设施
	 * @param facilities
	 * @return
	 */
	RpcResponse<Facilities> updateFacilities(Facilities facilities);



	/**
	 * 工具 批量绑定设施和设备的关系
	 * 
	 * @Description:
	 * @param list
	 * @return
	 */
	RpcResponse<String> addFacilitiesAndDeviceId(List<Map<String, Object>> list,String accessSecret);



	/**
	 * 
	 * @Description: 批量插入设施信息
	 * @param sheet
	 * @return
	 * @return
	 */
	RpcResponse<Integer> batchInsertFacilities(List<Facilities> sheet);



	/**
	 * 
	 * @Description:批量保存设施与设备信息
	 * @param sheet
	 * @return
	 */
	RpcResponse<Integer> batchInsertFacAndDevice(List<Facilities> sheet);



	/**
	 * 
	 * @Description:批量修改设施的denfenseState
	 * @param facIds
	 * @return
	 */
	RpcResponse<Integer> updateFacilitiesDenfenseState(String organizationId,List<String> facIds,String token,String denfenseState,String accessSecret);



	/**
	  * 
	  * @Description:修改设施的denfenseState为待整治状态审批中
	  * @param 
	  * @return
	  */
	
	RpcResponse<Integer> examineRenovationFacility(JSONObject json);

}
