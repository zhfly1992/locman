package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.run.locman.api.entity.FacilitiesDataType;

/**
 * @Description: 设施数据类型查询接口
 * @author: wangsheng
 * @version: 1.0, 2017年8月8日
 */
public interface FacilitiesDataTypeQueryRpcRepository {

	/**
	 * @Description:查询全部设施数据类型(不分页)
	 * @param map
	 * @return
     */
	List<FacilitiesDataType> findAllFacilitiesDataType(Map<String,String> map);

	/**
	 * @Description:根据facilitiesTypeId查询FacilitiesDataType集合
	 * @param map
	 * @return
     */
	List<FacilitiesDataType> findFacilitiesDataTypeList(Map<String,String> map);

	/**
	 * 根据设施扩展id查询设施扩展详情
	 * @param id 设施扩展详情id
	 * @return
     */
	FacilitiesDataType findById(String id);
	
	/**
	 * 验证设施扩展名称或标识名是否重复
	 * @Description:
	 * @param map
	 * @return
	 */
	int validFacilitiesDataTypeName(Map<String,String> map);
}
