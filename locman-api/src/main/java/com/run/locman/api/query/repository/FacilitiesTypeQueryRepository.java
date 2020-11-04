package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.entity.FacilitiesType;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年08月09日
 */
public interface FacilitiesTypeQueryRepository extends BaseQueryRepository<FacilitiesType, String> {

	/**
	 * 
	 * @Description: 分页查询公司下的所有设施类型
	 * @param params
	 *            查询参数
	 * @return
	 */
	public List<FacilitiesType> queryFacilitiesTypeListPage(Map<String, String> params);



	/**
	 * 
	 * @Description: 查询公司下的所有设施类型(不分页)
	 * @param params
	 *            查询参数
	 * @return
	 */
	public List<FacilitiesType> findAllType(String accessSecret);



	/**
	 * 
	 * @Description:校验该接入方下该设施类型名称是否重复
	 * @param facilitiesTypeName
	 * @param accessSecret
	 * @param facilityTypeId 
	 * @return Integer
	 */
	public Integer checkFacilitiesTypeName(@Param("facilitiesTypeName") String facilitiesTypeName,
			@Param("accessSecret") String accessSecret, @Param("facilityTypeId") String facilityTypeId);



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public List<Map<String, String>> findAllFacilitiesTypeAndNum(@Param("accessSecret") String accessSecret);



	/**
	  * 
	  * @param string 
	 * @Description:
	  * @param 
	  * @return
	  */
	
	public List<Map<String, Object>> findAllFacTypeAndDeviceTypeNum(@Param("accessSecret") String accessSecret,@Param("boundState") String boundState);

}
