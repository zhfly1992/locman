/*
 * File name: FacInfoQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年4月19日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;

import com.run.locman.api.base.repository.BasePagingRepository;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.model.FacilitiesModel;

/**
 * @Description: 一般工单与设施信息查询
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月19日
 */

public interface FacInfoOrderQueryRepository extends BasePagingRepository<Facilities> {

	/**
	 * 
	 * @Description:通过工单id查询设施id集合
	 * @param simpleOrderId
	 * @return
	 * @throws Exception
	 */
	List<String> findFacIdsByOrerId(FacilitiesModel facilitiesModel) throws Exception;

}
