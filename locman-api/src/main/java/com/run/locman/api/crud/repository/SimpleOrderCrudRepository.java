/*
 * File name: SimpleOrderCrudRepository.java
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

package com.run.locman.api.crud.repository;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.dto.SimpleOrderDto;
import com.run.locman.api.entity.SimpleOrderProcess;

/**
 * @Description:
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public interface SimpleOrderCrudRepository extends BaseCrudRepository<SimpleOrderProcess, String> {

	String findProcessEndTimeById(@Param("orderId") String orderId);
	SimpleOrderDto findProcessInfoById(@Param("orderId") String orderId);
}
