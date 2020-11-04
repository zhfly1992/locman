package com.run.locman.api.crud.repository;

import com.run.locman.api.entity.Factory;

/**
 * 
 * @Description:厂家crud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface FactoryCrudRepository extends BaseCrudRepository<Factory, String> {

	public Integer deleteFactoryById(String factoryId);
	
	/**
	 * 
	* @Description:彭州使用新增厂家
	* @param fac
	* @return
	 */
	public Integer insertNewFactory(Factory fac);
}