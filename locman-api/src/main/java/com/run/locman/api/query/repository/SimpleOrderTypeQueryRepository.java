/*
 * File name: SimpleOrderTypeQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月6日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import com.run.locman.api.entity.SimpleOrderProcessType;

/**
 * @Description:
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public interface SimpleOrderTypeQueryRepository extends BaseQueryRepository<SimpleOrderProcessType, String> {

	List<SimpleOrderProcessType> findOrderTypeBySecret() throws Exception;



	@SuppressWarnings("rawtypes")
	List<Map> findOrderState() throws Exception;
}
