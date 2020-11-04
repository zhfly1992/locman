package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.FacilitiesDataType;

/**
* @Description:	设施数据类型查询接口
* @author: wangsheng
* @version: 1.0, 2017年8月8日
*/
public interface FacilitiesDataTypeQueryRpcService {
	/**
	 * @Description:查询全部设施数据类型(不分页)
	 * @param map
	 * @return
	 * @throws Exception
     */
	RpcResponse<List<FacilitiesDataType>> getAllFacilitiesDataType(Map<String,String> map) throws Exception;

	/**
	 * @Description:查询全部设施数据类型
	 * @param pageNo
	 * @param pageSize
	 * @param map
	 * @return
     * @throws Exception
     */
	RpcResponse<PageInfo<FacilitiesDataType>> getFacilitiesDataTypeList(Integer pageNo, Integer pageSize,Map<String,String> map) throws Exception;


	/**
	 * @Description: 查询设施扩展详情
	 * @param id: 设施扩展ID
	 * @return
	 * @throws Exception
     */
	RpcResponse<FacilitiesDataType> getById(String id) throws Exception;
	
	
	/**
	 * 验证设施扩展名称或标识名是否重复
	 * @Description:
	 * @param map
	 * @return
	 */
	RpcResponse<Integer> validFacilitiesDataTypeName(Map<String,String> map);

}
